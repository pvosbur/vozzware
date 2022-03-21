/*
 ===========================================================================================

               V o z z W o r k s  F r a m e W o r k  L i b r a r i e s

                            Copyright(c) 2001 by

            I n t e r n e t   T e c h n o l o g i e s   C o m p a n y

                                 All Rights Reserved


 Source Name: VwCheckBox.java


 ============================================================================================
*/

package com.vozzware.ui;

import javax.swing.JCheckBox;
import java.awt.Component;

/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwCheckbox.java

Create Date: Apr 11, 2003
============================================================================================
*/
/**
 * The VwCheckBox bean extends the Java Swing JCheckBox class giving extended
 * capabilities for working with the VwSmartPanel and the Opera Server
 * products.  This button class allows the user to associate a service to run
 * when this button is clicked.  The VwSmartPanel will get the name of the
 * service and the required service parameter names when the button is clicked.
 *  The VwSmartPanel will then execute the service and load the form with the
 * results if the service is a query, or display the status if it is an insert,
 * update, or delete service.
 *
 * @version 2.0
 */
public class VwCheckBox extends JCheckBox
{
  private String       m_strServiceName = "";        // Name of associated Vw service
  private String       m_strServiceParamNames = "";  // Comma delimited list required service params

  private boolean m_fLocked = false;                // true if button is currently locked.
  private boolean m_fEnabled = true;                // true if button should be enabled when unlocked.

  private String          m_strCompName;            // Component name used by httpServices


  private boolean         m_fHasInitActions = false;

  private boolean         m_fHasActions = false;

  private String[]        m_astrCompListenerNames;


  /**
   * Constructs the VwCheckBox instance
   */
  public VwCheckBox()
  {
    super();
    m_fEnabled = isEnabled();
  }

  public VwCheckBox( String strLabel )
  {
    super();
    m_fEnabled = isEnabled();
    setText( strLabel );
    
  }

  /**
   * Sets the name of the service that will be executed when this button is clicked
   *
   * @param strServiceName - The Vw service name to execute
   */
  public final void setServiceName( String strServiceName )
  { m_strServiceName = strServiceName; }


  /**
    * Lock the button.
    * Preserve the current state, then disable the button.
    */
  synchronized public void lock()
  {
    // Only lock the button if it is currently unlocked.
    // Locking an already-locked button may disable it.
    if ( !m_fLocked )
    {
      m_fEnabled = isEnabled();         // get current state
      super.setEnabled( false );        // disable the button
      m_fLocked = true;                 // remember that it's locked
    }
  }


  /**
    * Unlock the button.
    * If the button should be enabled, enable it now.
    */
  public void unLock()
  {
    // Only unlock the button if it is currently locked.
    // Unlocking an already-unlocked button may disable it.
    if ( m_fLocked )
    {
      m_fLocked = false;            // remember that it's unlocked
      if ( m_fEnabled )             // enable button if it should be on
        super.setEnabled( true );
    }
  }

  /**
   * Override from parent class to track disable. The Smart Panel always disables buttons
   * on a form before executing a service and reanables them after.  If a button was previously
   * disabled, it should not be re-enabled until the original disabler has re-enabled the button.
   */
  synchronized public void setEnabled( boolean fEnable )
  {
    if ( !fEnable )
    {
      m_fEnabled = false;
      super.setEnabled( false );

      return;

    } // end if

    // This is an enable request. If the disable count > 0 don't enable the button

    m_fEnabled = true;
    if ( m_fLocked )
      return;

    super.setEnabled( true );   // enable if count is 0

  } // end setEnabled()


  /**
   * Gets the name of the service that will be executed when this button is clicked
   *
   * @return - A string with the Vw service name to execute
   */
  public final String getServiceName()
  { return m_strServiceName; }


  /**
   * Sets the name of the service parameter names that must be loaded from the
   * VwEditFields prior to executing the service.  The parameter names must be
   * comma delimited.  E.g., Name1,Name2 ...
   *
   * @param strServiceParamNames - A comma delimited of required paramters for the service.
   * The parameter names must be the same as the VwTextField names that hold the data.
   */
  public final void setServiceParamNames( String strServiceParamNames )
  { m_strServiceParamNames = strServiceParamNames; }


  /**
   * Gets the defined service parameter names
   *
   * @return - A string with the service parameter names
   */
  public final String getServiceParamNames()
  { return m_strServiceParamNames; }




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

} // end class VwCheckBox{}


// *** End of VwCheckBox.java ***

