/*
 ===========================================================================================

               V o z z W o r k s  F r a m e W o r k  L i b r a r i e s

                         Copyright(c) 2000 by

            I n t e r n e t   T e c h n o l o g i e s   C o m p a n y

                                 All Rights Reserved


 Source Name: VwComboBox.java


 ============================================================================================
*/

package com.vozzware.ui;

import com.vozzware.util.VwDelimString;
import com.vozzware.xml.VwDataObject;

import javax.swing.JComboBox;
import java.awt.Component;
import java.util.ResourceBundle;
import java.util.Vector;

/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwComboBox.java

Create Date: Apr 11, 2003
============================================================================================
*/
/**
 * The VwComboBox bean extends the JComboBox class giving extended
 * capabilities for working with the VwSmartPanel class and the Vw Server
 * products.  This class allows the user to associate a Service to run that will
 * load the AWT Choice control from the Vw middleware.  Like the VwButton
 * class, the VwComboBox class supports the definition of a Service to be run when
 * a selection is made from the Choice control, providing Master/Detail capabilities.
 *
 * @version 2.0
 */
public class VwComboBox extends JComboBox 
{
  private String            m_strLoadService = "";        // Vw service to execute to load


  // this control with data
   
  private VwDelimString    m_dlmsLoadServiceParamNames;  // Comma delimited list required service params

  private String            m_strSelService = "";         // Vw service to execute when an
                                                          // an item has been selected
  private String            m_strSelServiceParamNames = "";   // Data items associated with the service
                                                          // when a choice selection is made

  private String[]          m_astrDisplayNames = null;    // Array of the data names in the returned
                                                          // VwDataobject used to display a row
  private short[]           m_asDisplayWidths = null;     // Corresponding widths
  private short[]           m_asDecPos = null;            // Corresponding decima;; positions for
                                                          // For decimal numeric data
  private char[]            m_achDelimiters = null;       // Corresponding delimters

  private VwDelimString    m_dlmsSelServiceParamNames;   // Comma delimited list required service params

  private VwDelimString    m_dlmsParamValues;            // Required service param values

  private int               m_nColSpacing = 5;

  private int               m_nRowCursor = 0;             // Current fetch row

  private ResourceBundle    m_msgs;                       // language independant msg strings

  private Vector            m_vecDataObjects = null;      // Vector of data objects that corresponds
                                                          // to the index of the Choice control
  private boolean           m_fSaveDataObjs = false;      // If set save each data object received

  private boolean           m_fLoadOnInit;                // Set to true if this shuld be loaded
                                                          // before panel is shown
  private boolean           m_fHasInitActions = false;
  private boolean           m_fHasActions = false;

  private String            m_strCompName;                // Symbolic name for this component

  private String[]          m_astrCompListenerNames;      //  Array of dataChanged listener
                                                          // component names

  /**
   * Constructs the VwComboBox instance
   */
  public VwComboBox()
  {
     super();

     try
     {
       m_msgs = ResourceBundle.getBundle( "resources.properties.vwutil" );
     }
     catch( Exception e ){}

     m_vecDataObjects = new Vector();

  } // end VwComboBox();


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


  /**
   * Override of super class method
   */
  public int getSelectedIndex()
  { return super.getSelectedIndex(); }



  /**
   * Sets the value of the Save Data Object property.  If set to True, an VwDataObject
   * is saved from the Service return for each row in the Choice control. If a load service
   * is specified this flag is automaticially set to true and cannot be turned off.
   *
   * @param fSet - True to store the data object with the corresponding row in
   * the Choice control.
   */
  public final void setSaveDataObjectOnLoad( boolean fSet )
  {
    if ( m_strLoadService.length() > 0 )
      return;          // This flag cannot be reset if a load service is specified

    m_fSaveDataObjs = fSet;
  }


  /**
   * Adds a string display item and a corresponding VwDataobject to the list control
   *
   * @param strDisplayItem The string to display in the list control
   * @param dataObj The corresponding VwDataObject to store with the string
   */
  public void add( String strDisplayItem, VwDataObject dataObj )
  {
    super.addItem( strDisplayItem );
    if ( !m_fSaveDataObjs )
      m_fSaveDataObjs = true;

    m_vecDataObjects.addElement( dataObj );

  } //end addItem()



  /**
   * Returns an VwDataObject for the specified index of the Choice list
   *
   * @param nRowNbr - The row number to get the data from
   *
   * @return - An VwDataObject with the data elements for the specified row
   *
   * @exception Throws Exception if the row index is out of bounds
   */
  public VwDataObject getRowData( int nRowNbr ) throws Exception
  {
    if ( nRowNbr >= getItemCount() || nRowNbr < 0 )
      throw new Exception( "Invalid Row Nbr" );

     if ( m_fSaveDataObjs )
       return (VwDataObject)m_vecDataObjects.elementAt( nRowNbr );

    VwDataObject dataObj = new VwDataObject();
    String strRow = (String)getItemAt( nRowNbr );

    dataObj.add( "ITEM", strRow );

    m_nRowCursor = nRowNbr;

    return dataObj;

  } // end getRowData()


  /**
   * Returns an VwDataObject for the item at the first index in the Choice list
   *
   * @return - An VwDataObject with the data elements for the first row
   *
   * @exception Throws Exception if the Result Set is empty
   */
  public final VwDataObject getFirstRowData()
  { try
    { m_nRowCursor = 0; return getRowData( 0 ); }
    catch( Exception e )
    { return null; }
  }

  /**
   * Returns an VwDataObjct for the item at the next index in the Choice list
   *
   * @return - An VwDataObject with the data elements for the next row, or null
   * if there is no more data.
   *
   * @exception Throws Exception if the Choice control is empty
   */
  public final VwDataObject getNextRowData()
  {
    try
    { return getRowData( ++m_nRowCursor ); }
    catch( Exception e )
    { return null; }
  }

} // end class VwComboBox


// *** End of VwComboBox.java ***

