/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwTextArea.java

Create Date: Apr 11, 2003
============================================================================================
*/

package com.vozzware.ui;

import com.vozzware.util.VwEdit;
import com.vozzware.util.VwInvalidMaskException;
import com.vozzware.xml.VwDataDictionary;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextArea;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ResourceBundle;
import java.util.Vector;



/**
 * This class extends the java JTextArea class to add data validation and data aware
 * capabilites through the VwDataDictionary, Vw Servers, and VwEdit classes.
 * The VwTextArea can limit the number and type of characters entered using the
 * dictionary definitons.  If a character is entered that conflicts with a defined
 * edit mask, the character will be ignored and an invalid character event will be
 * sent to interested listeners.  The data in the VwTextArea is formatted acording
 * to the edit mask defined when the text field looses focus.
 */
public class VwTextArea extends JTextArea implements ActionListener
{
  private Vector          m_vecListeners;                // Vector for registered listeners

  private Color           m_clrErrBackGround;            // Error backgroung color
  private Color           m_clrErrText;                  // Error text color

  private Color           m_clrBackGround;               // Normal background color
  private Color           m_clrText;                     // Normal text color

  private boolean         m_fMarkTextOnFocus = true;     // Hilite text on entry into field

  private boolean         m_fWantSuper = false;

  private ResourceBundle  m_msgs;                        // String message bundle

  private String          m_strCompName;                 // Component name used by httpServices


  private boolean         m_fHasInitActions = false;

  private boolean         m_fHasActions = false;

  private String[]        m_astrCompListenerNames;


  private JPopupMenu      m_popupMenu = new JPopupMenu();

  VwEdit                 m_itcEdit;

  private boolean         m_fEnablePopupMenu = false;
  /**
   * Constructs an VwTextArea object
   */
  public VwTextArea()
  {
    super();

    m_msgs = ResourceBundle.getBundle( "resources.properties.vwutil" );

    m_vecListeners = new Vector( 1 );

    m_clrErrBackGround = Color.red;                        // Error backgroung color
    m_clrErrText = Color.white;                            // Error text color

    m_clrBackGround = getBackground();                     // Normal background color
    m_clrText = getForeground();                           // Normal text color

    JMenuItem menuItem = new JMenuItem( "Clear" );
    menuItem.addActionListener( this );
    m_popupMenu.add(  menuItem );

    try
    {
      m_itcEdit = new VwEdit( "*" );

    }
    catch( Exception e )
    { ; }

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
  } // end VwTextArea


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
   * Sets the VwDataDictionary to use for editing and validation for visual text formatting
   *
   * @param itcDictionaryObj - The VwDataDictionary object with the data dictionary contstraints
   *
   * @exception throws Exception if the data dictionary keys expected are not present
   */
  public final void setDataDictionary( VwDataDictionary itcDictionaryObj ) throws Exception
  { m_itcEdit.setDataDictionary( itcDictionaryObj ) ; }


  /**
   * Sets the Required Entry property value (data length must be > 0)
   *
   * @param fRequired - If True, data is required for this TextField; if False,
   * data is not required.
   */
  public final void setRequiredEntry( boolean fRequiredEntry )
  { m_itcEdit.setRequiredEntry( fRequiredEntry ); }


  /**
   * Gets the current setting of the Required Entry property
   *
   * @return True if data is required for this TextField; False if it is not
   */
  public final boolean getRequiredEntry()
  { return m_itcEdit.getRequiredEntry(); }


  /**
   * Sets the maximum number of characters allowed in this TextField
   *
   * @param nMaxChars - The maximum number of characters allowed
   */
  public final void setMaxCharsAllowed( int nMaxChars )
  { m_itcEdit.setMaxCharsAllowed( nMaxChars ); }


  /**
   * Gets the current setting for the maximum input characters allowed
   *
   * @return An int with the current setting for the maximum characters allowed
   */
  public final int getMaxCharsAllowed()
  { return m_itcEdit.getMaxCharsAllowed(); }


  /**
   * Sets the minimum number of characters required for this TextField
   *
   * @param nMinChars - The minimum number of characters required
   */
  public final void setMinCharsRequired( int nMinChars )
  { m_itcEdit.setMinCharsRequired( nMinChars ); }


  /**
   * Gets the current setting for the minimum input characters required
   *
   * @return An int with the current setting for the minimum characters required
   */
  public final int getMinCharsRequired()
  { return m_itcEdit.getMinCharsRequired(); }


  /**
   * Sets the edit mask for formatting and data validation of the TextField
   *
   * @param strEditMask - The edit mask
   *
   * @exception throws Exception, VwInvalidMaskException if the edit mask is
   * illegal.  See class VwEdit for valid mask characters and formats.
   */
  public final void setEditMask( String strEditMask ) throws Exception, VwInvalidMaskException
  { m_itcEdit.setEditMask( strEditMask ); }


  /**
   * Gets the currently defined edit mask
   *
   * @return A string with the edit mask, or null if no edit mask is defined
   */
  public final String getEditMask()
  {
    if ( m_itcEdit == null )
      return null;

    return m_itcEdit.getEditMask();

  } // end getEditMask()


  /**
   * Sets the Values or Range constraints for the data
   *
   * @param strValueRanges - The delimited string of Values and Range constraints.
   * See class VwEdit for the Values and Ranges string format.
   *
   * @exception throws Exception if the Values or Ranges conflict with the defined
   * edit mask.
   */
  public final void setValuesRanges( String strValuesRanges ) throws Exception
  {
    m_itcEdit.setValuesRanges( strValuesRanges );

  } // end setValuesRanges()


  /**
   * Gets the current Values or Range constraints property
   *
   * @return A string with the values and range constraints, or null if no
   * Values or Range constraints are defined.
   */
  public final String getValuesRanges()
  {
    return m_itcEdit.getValuesRanges().toString();

  } // end getValuesRanges()




  /**
   * Sets the normal colors for the EditField to the default background and
   * foreground colors.
   *
   */
  public void setNormalColors()
  {
    setBackground( m_clrBackGround );
    setForeground( m_clrText );
    validate();
  } // end setNormalColors()


  /**
   * Sets the error colors for the EditField to the error background and
   * foreground colors.
   *
   */
  public void setErrorColors()
  {
    setBackground( m_clrErrBackGround );
    setForeground( m_clrErrText );
    validate();
  } // end setNormalColors()



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
   * Sets an array of VwEdit classes used to format data items displayed in a compomnent.
   * Each data element will have its own VwEdit class that contains the format mask of the
   * data to be displayed. This property is most usefull in multi-column list and combo boxes.
   *
   * @param aEditors  An array of VwEdits classes ( one for each data element displayed )
   */
  //public  void setDisplayFormats( VwEdit[] aEditors )
  //{ ; }



  /**
   * Gets the display format array
   *
   * @return - An array of VwEdit classes that edit/format a data item in the component
   */
  //public VwEdit[] getDisplayFormats()
  //{ return null; }


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


} // end class VwTextArea{}


// *** End of VwTextArea.java ***
