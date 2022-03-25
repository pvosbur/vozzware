/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwListBox.java

Create Date: Apr 11, 2003
============================================================================================
*/

package com.vozzware.ui;

import com.vozzware.util.VwDelimString;
import com.vozzware.xml.VwDataObjList;
import com.vozzware.xml.VwDataObject;

import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.Component;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.Vector;

/**
 * The VwListBox bean extends the The JScrollPane class and includes the JList class
 * internally. The ui component JList does not provide scrolling and must be added
 * to the JScrollPane class which is why this class extends the JScrollPane class.
 * All of the public JList methods can be invoked from this class by first calling
 * the getList() method which returns the JList reference. The basic methods for adding
 * and retrieving listbox items are define in this class.
 *
 * This class adds capabilities for working with the VwSmartPanel class and the Vw Server
 * products.  This class allows the user to associate a service to run that will
 * load the ListBox control from the Vw middleware.  Like the VwButton
 * class, the VwListBox class supports definition of a service to be run when a
 * selection is made from the ListBox control, providing Master/Detail capabilities.
 *
 * @version 2.0
 */
public class VwListBox extends JList implements ListSelectionListener
{
  private String           m_strLoadService = "";        // Vw service to execute to load
                                                         // this control with data
  private String           m_strDataChangeService = ""; // Service to exec on datachange event
  private VwDelimString   m_dlmsLoadServiceParamNames;  // Comma delimited list required service params

  private String[]         m_astrDisplayNames = null;    // Array of the data names in the returned
                                                         // VwDataobject used to display a row
  private short[]          m_asDisplayWidths = null;     // Corresponding widths
  private short[]          m_asDecPos = null;            // Corresponding decimal positions for
                                                         // decimal numeric data
  private char[]           m_achDelimiters = null;       // Corresponding delimiters

  private VwDelimString   m_dlmsSelServiceParamNames;   // Comma delimited list required service params

  private VwDelimString   m_dlmsParamValues;            // Required service param values

  private int              m_nColSpacing = 5;

  private int              m_nRowCursor = 0;             // Current fetch row

  private ResourceBundle   m_msgs;                       // lang. independant msg strings

  private Vector           m_vecDataObjects = null;      // Vector of data objects that corresponds
                                                         // to the index of the List control

                                                         // before panel is shown
  private Vector           m_vecListObjects = new Vector();

  private String[]         m_astrCompListenerNames;      //  Array of dataChanged listener

  private boolean          m_fHasActions;

  private String           m_strCompName;                // The component's name

  /**
   * Constructs an VwListBox instance
   */
  public VwListBox()
  {
     super();
     // Create The actual JListbox and add it to the scroll pane
     addListSelectionListener( this );

     try
     {
       m_msgs = ResourceBundle.getBundle( "resources.properties.vwutil" );
     }
     catch( Exception e )
     {System.out.println( "In Bundle excep, Reason: " + e.toString());}


  } // end VwListBox();

  /**
   * Initialize the component by executing the defined actions
   *
   */
  public void init()
  {

  } // end init()


  /**
   * Sets the service name associated with an VwDataObject data element
   *
   * @param strName - The data key name associated with this component
   */
  public final void setComponentName( String strName ) throws Exception
  {
    m_strCompName = strName;

    Component comp = getParent();

    if ( comp instanceof VwSmartPanel )
      ((VwSmartPanel)comp).bind( this, strName );

  } // end setNameID()


  /**
   * Gets the data name associated with this component
   *
   * @return A string with the data name
   */
  public final String getComponentName()
  { return m_strCompName; }

  /**
   * Gets the name name of the properties file defining the initialization actions
   *
   * @return - A string with the name of the properties file
   * defining the initialization actions
   */
  //public boolean getInitActions()
  //{ return m_fHasInitActions; }


  /**
   * Sets the listbox selection mode
   *
   * @paran nSelMode The listbox selection mode as defined in the JList doc
   */
  public void setSelectionMode( int nSelMode )
  { setSelectionMode( nSelMode ); }



  /**
   * Selects the index value as the current selected item in the listbox
   *
   * @param nSel - The selection index
   */
  public void select( int nSel )
  { setSelectedIndex( nSel ); }

  /**
   * Sets the name of the service that will be executed when the listbox is clicked
   *
   * @param strServiceName - The Vw service name to execute
   */
  public final void setLoadOnInitService( String strServiceName )
  { m_strLoadService = strServiceName; }

  /**
   * Gets the name of the service that will be executed when the listbox is clicked
   *
   * @return - A string with the Vw service name to execute
   */
  public final String getLoadOnInitService()
  { return m_strLoadService; }

  /**
   * Sets the set of parameter values corrresponding to the parameter names
   *
   * @param strParamValues - A comma delimited string of parameter values
   */
  public final void setLoadOnInitParamValues( String strParamValues )
  { m_dlmsParamValues = new VwDelimString( ",", strParamValues ); }


  /**
   * Gets the set of parameter values corrresponding to the parameter names
   *
   * @return - A comma delimited string of parameter values
   */
  public final String getLoadOnInitParamValues()
  { return m_dlmsParamValues.toString(); }


  /**
   * Override of super class method
   */
  public int getSelectedIndex()
  { return getSelectedIndex(); }


  /**
   * Sets the Result Set data items that are to be displayed in the listbox if more than
   * one data item is returned.  If no display item names are specified, then all data items
   * that are returned in the VwDataObject are displayed.  Each display item defined must be
   * specified in the following format (name~width~delimiter)(name~width~delimiter) ...  Note
   * that the tilde character (~) is used as the delimiter within each display name attribute
   * set, and each attribute set is defined within an open and closed paren.  For example, to
   * display the name in the format "LastName,FirstName age" (Jones,Peter    29), the following
   * is issued.  The corresponding VwDataObject names are used lname, fname, and age:
   * (lname~0~,)(fname~40~ )(age~0~ ).  The zero width specified in the lname will immediately
   * place the delimiter following the actual length of the last name.  The 40 following the
   * fname will format the "fname,lname" pair to a combined 40 positions.  This allows for a
   * columnized view.  A fixed pitch Courier font is automatically applied when this property
   * is set to allow for a report style look.  Each Display name set must contain all three
   * attributes within the parens or an Exception will be thrown.
   *
   * @exception throws Exception if the format specified above is not followed
   *
   * @param strParamValues - A comma delimited string of parameter values
   */
  public final void setColumnDisplayItems( String strDisplayItems ) throws Exception
  {
    VwDelimString dlmsSpec = new VwDelimString( "(", strDisplayItems );

    int nCount = dlmsSpec.count();                // Count nbr of items specs

    // *** Create arrays to hold the specs

    m_astrDisplayNames = new String[ nCount ];
    m_asDisplayWidths = new short[ nCount ];
    m_asDecPos = new short[ nCount ];
    m_achDelimiters =  new char[ nCount ];

    String strSpec;         // An individual column spec

    int ndx = -1;

    while( (strSpec = dlmsSpec.getNext() ) != null )
    {
      // Delim string to parae the pieces within each spec

      VwDelimString dlmsPieces = new VwDelimString( "~", strSpec );

      if ( dlmsPieces.count() != 3 )
      {
        m_astrDisplayNames = null;
        m_asDisplayWidths = null;
        m_achDelimiters =  null;
        m_asDecPos = null;
        throw new Exception( m_msgs.getString( "VwUtil.InvalidColumSpec" ) );
      }

     ++ndx;

     // Load arrays with the spec info

     m_astrDisplayNames[ ndx ] = dlmsPieces.getNext();
     String strWidths = dlmsPieces.getNext();
     int nPos = strWidths.indexOf( '.' );

     if ( nPos >= 0 )
     {
       m_asDisplayWidths[ ndx ] = Short.parseShort( strWidths.substring( 0, nPos ) );
       m_asDecPos[ ndx ] = Short.parseShort( strWidths.substring( nPos + 1 ) );
     }
     else
       m_asDisplayWidths[ ndx ] = Short.parseShort( strWidths );

     String strDelim = dlmsPieces.getNext();

     if ( !strDelim.endsWith( ")" ) )
     {
       m_astrDisplayNames = null;
       m_asDisplayWidths = null;
       m_achDelimiters =  null;
       m_asDecPos = null;

       throw new Exception( m_msgs.getString( "VwUtil.InvalidColumSpec" ) );
     }
     // Add in the delimiter minus the ")" right paren char

     m_achDelimiters[ ndx ] = strDelim.charAt( 0 );


     dlmsPieces = null;

    } // end while()

    dlmsSpec = null;

  } // end setColumnDisplayItems()


  /**
   * Gets the column display item list
   *
   * @return - A comma delimited string of column display item names, or null none
   * are defined.
   */
  public final String getColumnDisplayItems()
  {
    if ( m_astrDisplayNames == null )     // return empty string if nothing defined
      return "";

    // *** Re-construct delim string from arrays

    String strDisplaySpec = "";
    for ( int x = 0; x < m_astrDisplayNames.length; x++ )
    {
      strDisplaySpec += "(" + m_astrDisplayNames[ x ] + "~"
                     +  String.valueOf( m_asDisplayWidths[ x ] ) + "~"
                     + m_achDelimiters[ x ] + ")";
    } // end for()

    return strDisplaySpec;

  } // end getColumnDisplayItems()


  /**
   * Sets the name of the Service that will be executed on a dataChangedEvent
   *
   * @param strDataChangeService - The name of the Vw Service to execute
   */
  public final void setDataChangedServiceName( String strDataChangeService )
  { m_strDataChangeService = strDataChangeService;  }


  /**
   * Gets the name of the Service that will be executed on a dataChangedEvent
   *
   * @return - The name of the Vw Service to execute dataChangedEvent
   */
  public final String getDataChangedServiceName()
  { return m_strDataChangeService; }


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
   * Adds a string to the listbox
   *
   * @param strItem - The string item to add
   */
  public void add( String strItem )
  {
    m_vecListObjects.addElement( strItem );
    setListData( m_vecListObjects );
    repaint();
    validate();

  } // end addItem()


  /**
   * Adds an object to the listbox
   *
   * @param objItem - The object item to add
   */
  public void add( Object objItem )
  {
    m_vecListObjects.addElement( objItem );
    setListData( m_vecListObjects );
    repaint();
    validate();

  } // end addItem()


  /**
   * Gets the number of items in the listbox
   *
   * @return - The nbr of items in the listbox
   */
  public int getItemCount()
  { return m_vecListObjects.size(); }


  /**
   * Gets the string item in the listbox at the specidied index
   *
   * @return the string at index specified if the object stored in the listbox is a string
   */
  public String getItem( int ndx )
  { return (String)m_vecListObjects.elementAt( ndx ); }



  /**
   * Loads this listbox from an VwDataObjList
   *
   * @param dobjList The VwDataObjectList to provide the contents of the list box
   * @param strDisplayKeys A comma separated list of one or more data object keys with wich to display the contents
   */
  public void setData( VwDataObjList dobjList, String strDisplayKeys )
  {
    VwDelimString dlms = new VwDelimString( ",", strDisplayKeys );
    String[] astrDisplayKeys = dlms.toStringArray();

    for ( Iterator idobj = dobjList.iterator(); idobj.hasNext(); )
    {
      String strDisplayItem = "";
      VwDataObject dobj = (VwDataObject)idobj.next();


      for ( int x = 0; x < astrDisplayKeys.length; x++ )
        strDisplayItem += dobj.getString( astrDisplayKeys[ x ] ) + " ";

      add( strDisplayItem );

    } // end for

  } // end setData

  /**
   * Adds a string display item and a corresponding VwDataobject to the list control
   *
   * @param strDisplayItem The string to display in the list control
   * @param dataObj The corresponding VwDataObject to store with the string
   */
  public void add( String strDisplayItem, VwDataObject dataObj )
  {

    add( strDisplayItem );

    m_vecDataObjects.addElement( dataObj );

  } //end addItem()




  /**
   * Returns an VwDataObject for the row number requested
   *
   * @param nRowNbr - The row number to get the data from
   *
   * @return - An VwDataObject with the data elements for the listbox item
   *
   * @exception - throws Exception if the row index is out of bounds
   */
  public VwDataObject getRowData( int nRowNbr ) throws Exception
  {
    if ( nRowNbr >= getItemCount() || nRowNbr < 0 )
      throw new Exception( "Invalid Row Nbr" );

     return (VwDataObject)m_vecDataObjects.elementAt( nRowNbr );


  } // end getRowData()


  /**
   * Returns an VwDataObjct for the first row in the listbox
   *
   * @return - An VwDataObject with the data elements for the first row in the listbox
   *
   * @exception - throws Exception if the listbox is empty
   */
  public final VwDataObject getFirstRowData()
  {
    try
    { m_nRowCursor = 0; return getRowData( 0 ); }
    catch( Exception e )
    { return null; }

  } // getFirstRowData()


  /**
   * Returns an VwDataObjct for the next row in the listbox
   *
   * @return - An VwDataObject with the data elements for the next row in the
   * listbox, or null if there is no more data.
   *
   * @exception - throws Exception if the listbox is empty
   */
  public final VwDataObject getNextRowData()
  {
    try
    { return getRowData( ++m_nRowCursor ); }
    catch( Exception e )
    { return null; }

  } // end getNextRowData()


  /**
   * Adds a lst of other VwServiceable controls that are interested the VwDataChanged
   * events for this control.
   *
   * @param serviceSupport The VwServiceable control to receive the VwDataChanged event
   * any time data changes in the context of this control. This could be a selection change
   * in a list or combo box, a radio or checkbox state change, TextField data change ectc ...
   */
  public void addDataChangedListener( VwServiceable serviceSupport )
  {

  } // end addVwDataChangedListener()



  /**
   * Removes the VwServiceable control as a dataChanged event listener.
   *
   * @param serviceSupport The VwServiceable control to remove from the listener list
  */
  public void removeDataChangedListener( VwServiceable serviceSupport )
  {

  } // end addVwDataChangedListener()

  /**
   * This method is called when data has changed in an VwServiceable implemented control.
   * Any listeners registered with that control get this event. The object that processes
   * this event will be responsible for updating its content for its context.
   *
   * @param dataChangedEvent The VwDataChangedEvent containing the VwDataObject(s)
   * that represent the changed data.
   */
  public void dataChanged( VwDataChangedEvent dataChangedEvent )
  {

  } // end itcDataChanged()


  /**
   * Implementor for the VwChoice item state change events
   *
   * @param itemEvent - The ItemEvent instance
   */
  public final void valueChanged( ListSelectionEvent itemEvent )
  {

    /* To be determined
    Object obj = itemEvent.getSource();
    if ( obj instanceof VwJListInterface )
      handleListSelectionChange((VwJListInterface)obj );
    else
      return;                         // Not one of ours

   */

  }

} // enc class VwListBox{}


// *** End of VwListBox.java ***

