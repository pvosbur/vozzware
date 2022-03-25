/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwTextPane.java

Create Date: Apr 11, 2003
============================================================================================
*/

package com.vozzware.ui;

import javax.swing.Action;
import javax.swing.JPopupMenu;
import javax.swing.JTextPane;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.Document;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;



/**
 * This class extends the java JTextPane class to add data validation and data aware
 * capabilites through the VwDataDictionary, Vw Servers, and VwEdit classes.
 * The VwTextPane can limit the number and type of characters entered using the
 * dictionary definitons.  If a character is entered that conflicts with a defined
 * edit mask, the character will be ignored and an invalid character event will be
 * sent to interested listeners.  The data in the VwTextPane is formatted acording
 * to the edit mask defined when the text field looses focus.
 */
public class VwTextPane extends JTextPane implements ActionListener
{

  private ResourceBundle  m_msgs;                        // String message bundle

  private String          m_strCompName;                 // Component name used by httpServices


  private boolean         m_fHasInitActions = false;

  private boolean         m_fHasActions = false;

  private String[]        m_astrCompListenerNames;

  private JPopupMenu      m_popupMenu = new JPopupMenu();

  private boolean         m_fEnablePopupMenu = false;
  private boolean         m_fActionsDisabled = false;

  private List            m_listCaretActions =  new LinkedList();

  private Document        m_doc;

  private Component       m_frameParent;
  /**
   * Default constructor - Used editors default Document m_btModel
   */
  public VwTextPane()
  { this( null ); }


  /**
   * Constructor - Uses the Document m_btModel specified
   */
  public VwTextPane( Document doc )
  {
    super();

    m_msgs = ResourceBundle.getBundle( "resources.properties.vwutil" );

    if ( doc == null )
      m_doc = getDocument();                  // Get Default
    else
      m_doc = doc;

    this.setDocument( m_doc );
    installListeners();


  } // end VwTextPane

  
  public boolean getScrollableTracksViewportWidth()
  {
    return false;     // Force Horizontal scroll bar when needed
  }
  /**
   * Adds a caret action where the action will be disabled when the text pane is empty
   * and enabled when the text pane has content
   *
   * @param action The Action to be enabled/disabled based on the text pane size
   */
  public void addCaretAction( Action action )
  { m_listCaretActions.add( action ); }

  /**
   * Removes the caret action form the list
   *
   * @param action The Action to remove
   */
  public void removeCaretAction( Action action )
  { m_listCaretActions.remove( action ); }


  /**
   * Return an Iterator to the list of registered caret actions
   * @return
   */
  public Iterator getCaretActions()
  { return m_listCaretActions.iterator(); }


  /**
   * Install the editor listeners
   */
  private void installListeners()
  {

   
    this.addCaretListener( new CaretListener()
    {
      public void caretUpdate( CaretEvent ce )
      {
        if ( m_listCaretActions.size() == 0 )
          return;

        if ( m_doc.getLength() == 0 )
        {
          m_fActionsDisabled = true;
          for ( Iterator iActions = m_listCaretActions.iterator(); iActions.hasNext(); )
          {
            Action action = (Action)iActions.next();
            action.setEnabled( false );
          }

          return;
        }

        if ( m_doc.getLength() > 0 && m_fActionsDisabled )
        {
          m_fActionsDisabled = false;
          for ( Iterator iActions = m_listCaretActions.iterator(); iActions.hasNext(); )
          {
            Action action = (Action)iActions.next();
            action.setEnabled( true );
          }
        }
      }
    });

    // Mouse Listener for standard popup menus
    this.addMouseListener( new MouseAdapter()
    {

      public void mouseReleased( MouseEvent e )
      {

        if ( e.isPopupTrigger() )
        {
          if ( !m_fEnablePopupMenu )
            return;

          if ( getSelectedText() == null )  // Only do if there is no text selected
            handlePopup( e.getX(), e.getY() );
        }
      }
    });


  }


  public void setPopupMenuEnabled( boolean fEnabled )
  { m_fEnablePopupMenu = fEnabled; }


  /**
   * Handles popup menu selection
   */
  private void handlePopup( int x, int y )
  {
    m_popupMenu.show( this, x, y );
  }


  /**
   * Handles popup menu actions
   */
  public void actionPerformed( ActionEvent ae )
  {
    if ( ae.getActionCommand().equalsIgnoreCase( "clear" ) )
      super.setText( "" );

  }


  /**
   * Sets the data name for the VwDataObject associated with this component
   *
   * @param strName - The data key name associated with this component
   */
  public final void setComponentName( String strName ) throws Exception
  {
    m_strCompName = strName;
    Component comp = getParent();

    if ( comp instanceof VwSmartPanel )
      ((VwSmartPanel)comp).bind( this, strName );

  } // end setComponentName()


  /**
   * Gets the service name for this component
   *
   * @return The service name for this component
   */
  public final String getComponentName()
  { return m_strCompName; }


  /**
   * Sets the name of the properties file defining the list of initialization
   * actions to take.
   *
   * @param strPropFile The name of the properties file that holds the init actions
   */
  public void setInitActions( boolean fHasInitActions )
  { m_fHasInitActions = fHasInitActions; }


  /**
   * Gets the name name of the properties file defining the initialization actions
   *
   * @return - A string with the name of the properties file
   * defining the initialization actions
   */
  public boolean getInitActions()
  { return m_fHasInitActions; }


  /**
   * Event handler when a registered component changes it's content
   *
   * @param dca The event object containing the component info that sent this event
   */
  public void dataChanged( VwDataChangedEvent dca )
  {
  } // end dataChanged( VwDataChangedEvent dca )

  /**
   * Sets the set of parameter values corrresponding to the parameter names
   *
   * @param strParamValues - A comma delimited string of parameter values
   */
  public  void setLoadOnInitParamValues( String strParamValues )
  { }


  /**
   * Gets the set of parameter values corrresponding to the parameter names
   *
   * @return - A comma delimited string of parameter values
   */
  public String getLoadOnInitParamValues()
  { return null; }


  /**
   * Sets the actions to take place that are define in the component properties file
   *
   * @param fHasActions True if component has data change actions defined in the
   * properties file false otherwise.
   */
  public void setDataChangeActions( boolean fHasActions )
  { m_fHasActions = fHasActions; }


  /**
   * Gets the state of the actions file
   *
   * @return - A string with the name of the properties file
   * defining the initialization actions
   */
  public boolean getDataChangeActions()
  { return m_fHasActions; }


  /**
   * Initialize the component by executing the defined actions
   *
   */
  public void init()
  {

  } // end init()


  /**
   * Registers an array of component names that are interested the VwDataChanged
   * events for this component.
   *
   * @param astrCompListenerNames an array of VwServiceable component names to
   * register as data change  event listeners
   */
  public void setDataChangedListeners( String[] astrCompListenerNames )
  {
    m_astrCompListenerNames = astrCompListenerNames;

  } // end setDataChangedListeners()


  /**
   * Returns a String array of VwServiceable component names that are registered dataChange
   * event listeners of this control
   *
   * @return An array of registered VwServiceable component names or null if nothing was registered.
  */
  public String[] getDataChangedListeners()
  {
    return m_astrCompListenerNames;

  } // end getDataChangedListeners()



  /**
   * Registers the VwServiceable control as listener of data change events for this control
   *
   * @param serviceSupport The VwServiceable control to receive the VwDataChanged event
   * any time data changes in the context of this control. This could be a selection change
   * in a list or combo box, a radio or checkbox state change, TextField data change ectc ...
   */
  public void addDataChangedListener( VwServiceable serviceSupport )
  {

  } // end addDataChangedListener()


  /**
   * Removes the VwServiceable control as a dataChanged event listener.
   *
   * @param serviceSupport The VwServiceable control to remove from the listener list
  */
  public void removeDataChangedListener( VwServiceable serviceSupport )
  {

  } // end removeDataChangedListener()


} // end class VwTextPane{}

// *** End of VwTextPane.java ***
