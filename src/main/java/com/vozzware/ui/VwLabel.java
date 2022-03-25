/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwLabel.java

Create Date: Apr 11, 2003
============================================================================================
*/
package com.vozzware.ui;

import com.vozzware.util.VwDelimString;
import com.vozzware.util.VwEdit;

import javax.swing.JLabel;
import java.awt.Component;
import java.util.ResourceBundle;

/**
 *
 * @version 2.0
 */
public class VwLabel extends JLabel
{
  private String            m_strLoadService = "";        // Vw service to execute to load
                                                         // this control with data

  private char[]            m_achDelimiters = null;       // Corresponding delimters

  private VwDelimString    m_dlmsSelServiceParamNames;   // Comma delimited list required service params

  private VwDelimString    m_dlmsParamValues;            // Required service param values

  private int               m_nColSpacing = 5;

  private int               m_nRowCursor = 0;             // Current fetch row

  private ResourceBundle    m_msgs;                       // language independant msg strings

  private String            m_strCompName;                // Symbolic name for this component

  private String[]          m_astrCompListenerNames;      //  Array of dataChanged listener
                                                          // component names

  private String            m_strPropFile;

  private boolean           m_fHasInitActions = false;
  private boolean           m_fHasActions = false;

  /**
   * Constructs the VwLabel instance
   */
  public VwLabel()
  {
     super();

     try
     {
       m_msgs = ResourceBundle.getBundle( "resources.properties.vwutil" );
     }
     catch( Exception e ){}

  } // end VwLabel();




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
   * Gets the data name for this component
   *
   * @return The data name for this component
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
  public  void setDisplayFormats( VwEdit[] aEditors )
  { ; }



  /**
   * Gets the display format array
   *
   * @return - An array of VwEdit classes that edit/format a data item in the component
   */
  public VwEdit[] getDisplayFormats()
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




} // end class VwLabel


// *** End of VwLabel.java ***

