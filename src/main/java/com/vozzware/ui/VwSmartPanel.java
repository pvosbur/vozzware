/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwSmartPanel.java

Create Date: Apr 11, 2003
============================================================================================
*/

package com.vozzware.ui;            // The package this class belongs to

import com.vozzware.util.VwDelimString;
import com.vozzware.xml.VwDataObject;
import com.vozzware.xml.VwElement;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JToggleButton;
import javax.swing.text.JTextComponent;
import java.awt.Container;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Vector;


/**
 * This class implements a smart panel that communicates with the VwDataObject
 * and VwValidateObject.  Load, edit, and unload data to a middle tier with objects
 * of this class.
 *
 * @author Internet Technologies Corp.
 * @version 1.0
*/
public class VwSmartPanel extends JPanel implements VwServiceable,
                                                     VwTextFieldListener
{
  // *** Inner class to describe JComponents that make up this panel

  class VwServiceableComp
  {
    VwServiceable m_comp;                   // comp.implementing the VwServiceable interface
    String            m_strID;                  // Character Bind Id
    boolean           m_fBound;                 // Bound flag

    VwServiceableComp( VwServiceable comp )
    { m_comp = comp; m_fBound = false; }

  } // end inner class VwServiceableComp

 
  private Map               m_mapServiceSupport;          // Vector to ref. the JComponents

  private JTextComponent    m_errorFieldInFocus = null;

  private VwDataObject     m_dataObj;                    // data object to be initialized

  private boolean           m_fUseNumID;                  // If true use numeric ID

  private JFrame            m_parent = null;              // Parent frmae of this smart panel

  private ResourceBundle    m_msgs = null;                // Message Strings

  private boolean           m_fInvalidForm = false;       // Form validation flag

  private boolean           m_fComingFromMsgBox = false;  // Flag to prevent recurise lost focus loop

  private boolean           m_fValidateOnLostFocus = false; // Validation of lost focus flag

  private boolean           m_fCancelled = false;           // Set to true if a cancel button
                                                            // closed a Dialog

  private Vector            m_vecServices;                  // Vector of defined serverices this
                                                            // panel will execute

  private String            m_strCheckedText = "Y";         // Text put in dataobj if a button is checked

  private String            m_strUnCheckedText = "N";       // Text put in dataobj if a button is unchecked

  private String            m_strPropFile = null;           // Name of the properties containing
                                                            // the action list
  private String            m_strCompName;                  // The symbolic component bind name

  private boolean           m_fHasInitActions = false;
  private boolean           m_fHasActions = false;

  private class ServiceInfo
  {
    String         m_strServiceName;                   // Name of the service
    VwDelimString m_dlmsParamDataNames;               // Any Required parameter names
    int            m_nServiceType;                     // Type catagory of service I.E
                                                       // Insert, Update Query ...
  } // end inner class ServiceInfo

  private String[]          m_astrCompListenerNames;      //  Array of dataChanged listener
                                                          // component names

 /**
   * Constructs the panel class
  */
  public VwSmartPanel()
  {
    super( null );

    try
    {
      m_msgs = ResourceBundle.getBundle( "resources.properties.vwutil" );

    } catch( Exception e ){}

    m_mapServiceSupport = new HashMap();

    m_vecServices = new Vector();

  } // end VwSmartPanel()



  /**
   * Override of Container.add() method so JComponents can be added
   */
  public JComponent add( JComponent comp )
  {
    super.add( comp );
    addServiceSupportComp( comp );

    return comp;

  } // end add()


  /**
   * Override of Container.add() method so JComponents can be added
   */
  public JComponent add( String name, JComponent comp )
  {
    super.add( name, comp );
    addServiceSupportComp( comp );

    return comp;

  } // end add()


  /**
   * Override of Container.add() method so JComponents can be added
   */
  public JComponent add( JComponent comp, int nPos)
  {
    super.add( comp, nPos );
    addServiceSupportComp( comp );

    return comp;

  } // end add()


  /**
   * Override of Container.add() method so JComponents can be added
   */
  public void add( JComponent comp, Object obj )
  {
    super.add( comp, obj );

    addServiceSupportComp( comp );

  } // end add()


  /**
   * Override of Container.add() method so JComponents can be added
   */
  public void add( JComponent comp, Object obj, int ndx )
  {
    super.add( comp, obj, ndx );
    addServiceSupportComp( comp );

   } // end add()


  /**
   * Adds the JComponent to the JComponent list
   *
   * @param comp - The JComponent to add
   */
  private void addServiceSupportComp( JComponent comp )
  {

    if ( ! (comp instanceof VwServiceable ) )
      return;

    VwServiceableComp temp = new VwServiceableComp( (VwServiceable)comp );

    temp.m_strID = ((VwServiceable)comp).getComponentName();

    if ( comp instanceof VwTextField )
     ((VwTextField)comp).addTextFieldListener( this );

    if ( temp.m_strID == null )
      return;

    if ( temp.m_strID.length() == 0 )
      return;

    temp.m_fBound = true;

    m_mapServiceSupport.put( temp.m_strID, temp );

    return;


  } // end addServiceSupportComp()


  /**
   * Binds a JComponent to a character ID for communction with the VwDataObject
   * and the VwValidateObject.
   *
   * @param comp - The JComponent instance to be bound
   * @param strID - The character ID used for the JComponent binding
   *
   * @exception throws Exception if the JComponent is not in the JComponent list,
   * or the ID is a duplicate.
   */
  public final void bind( JComponent comp, String strID ) throws Exception
  {
    for ( Iterator iComp = m_mapServiceSupport.values().iterator(); iComp.hasNext(); )
    {
      VwServiceableComp temp = (VwServiceableComp)iComp.next();
      if ( temp.m_comp == comp )
      {
        temp.m_fBound = true;
        temp.m_strID = strID;
        return;
      }
      else
      {
        if ( temp.m_strID != null && temp.m_strID.equalsIgnoreCase( strID ) )
          throw new Exception( "ID " + strID + " Is a Duplicate" );

      } // end else

    } // end for()


  } // end bind()


  /**
   * Determines if this was a dialog that was closed from a Cancel button
   *
   * @return True if a Cancel button click closed the dialog; otherwise, False
   * is returned.
   */
  public final boolean wasCancelled()
  { return m_fCancelled; }


  /**
   * Sets the symbolic name used by httpServices or other components to get and set data
   * values. I.E if this component is a VwTextField that represents a person's
   * first name and you have a data object with a key of FirstName then the component
   * name should be set to "FirstName" to enable automatic interaction with the
   * VwDataObject.
   *
   * @param strName The user assigned symbolic name for this component.
   */
  public void setComponentName( String strName ) throws Exception
  { m_strCompName = strName; }


  /**
   * Gets the symbolic name for this component.
   *
   * @return The symbolic name for this component.
   */
  public String getComponentName()
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
   * Sets the name of the properties file defining the list of initialization
   * actions to take.
   *
   * @param strPropFile The name of the properties file that holds the init actions
   */
  public void setPropertyFile( String strPropFile )
  { m_strPropFile = strPropFile; }


  /**
   * Gets the name name of the properties file defining the initialization actions
   *
   * @return - A string with the name of the properties file
   * defining the initialization actions
   */
  public String getPropertyFile()
  { return m_strPropFile; }

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
   * Validates the VwTextField data on loss of focus if this property is set;
   * otherwise, performs all validation when the unLoad() method is called.
   *
   * @param fValidateOnLostFocus - The value of the Validate On Lost Focus property
   */
  public final void setValidateOnLostFocus( boolean fValidateOnLostFocus )
  { m_fValidateOnLostFocus = fValidateOnLostFocus; }


  /**
   * Gets the current setting for the Validate On Loss Focus property
   *
   * @return True if the VwTextField data will be validated on loss of focus;
   * otherwise, all validation is done when the unLoad() method is called.
   */
  public final boolean getValidateOnLostFocus()
  { return m_fValidateOnLostFocus; }



  /**
   * Sets the default data object for the form
   *
   * @param dataObj - The VwDataObject instance for this form
   */
  public final void setDataObject( VwDataObject dataObj )
  { m_dataObj = dataObj; }


  /**
   * Sets the value the smart panel will use for a CheckButton that is in a checked state.
   * The default setting is a 'Y'
   *
   * @param strCheckStateValue - The new value to use for a checked state button
   */
  public void setDataObjBtnCheckText( String strCheckStateValue )
  { m_strCheckedText = strCheckStateValue; }


  /**
   * Gets the value the smart panel will use for a CheckButton that is in a checked state.
   *
   */
  public String getDataObjBtnCheckText()
  { return m_strCheckedText; }


  /**
   * Sets the value the smart panel will use for a CheckButton that is in an unchecked state.
   * The default setting is a 'N'
   *
   * @param strUnCheckStateValue - The new value to use for an unchecked state button
   */
  public void setDataObjBtnUnCheckText( String strUnCheckStateValue )
  { m_strUnCheckedText = strUnCheckStateValue; }


  /**
   * Gets the value the smart panel will use for a CheckButton that is in an unchecked state.
   *
   */
  public String getDataObjBtnUnCheckText()
  { return m_strUnCheckedText; }

  /**
   * Loads the Smart Panel using the given VwDataObject
   *
   * @param dataObject - The data object with the data to load into the form
   *
   */
  public final void load( VwDataObject dataObj ) throws Exception
  {
    m_dataObj = dataObj;
    load();

  } // end load


  /**
   * Loads the form data from the current data object
   *
   * @exception throws Exception if no VwDataObject instance is set
   */
  public final void load() throws Exception
  {
    if ( m_dataObj == null )
      throw new Exception( "Missing VwDataObject Instance - use setVwDataObject()" );

    for ( Iterator iComp =  m_mapServiceSupport.values().iterator(); iComp.hasNext(); )
    {
      VwServiceableComp temp = (VwServiceableComp)iComp.next();
      String strData = null;

      if ( temp.m_fBound )
      {
        try
        {
          strData = m_dataObj.getString( temp.m_strID );
        }
        catch( Exception e )
        {
          continue;
        }

        if ( strData == null )
          strData = "";

        if ( temp.m_comp instanceof JTextComponent )
          ((JTextComponent)temp.m_comp).setText( strData );
        else
        if ( temp.m_comp instanceof JLabel )
          ((JLabel)temp.m_comp).setText( strData );
        else
        if ( temp.m_comp instanceof JCheckBox )
        {
          if ( strData.equals( "Y" ) || strData.equals( "1" ) || strData.equals( "true" ) )
            ((JCheckBox)temp.m_comp).setSelected( true );
          else
            ((JCheckBox)temp.m_comp).setSelected( false );
        }
        else
        if ( temp.m_comp instanceof JToggleButton )
        {
          if ( strData.equals( "Y" ) || strData.equals( "1" ) || strData.equals( "true" ) )
            ((JToggleButton)temp.m_comp).setSelected( true );
          else
            ((JToggleButton)temp.m_comp).setSelected( false );
        }
        else
        if ( temp.m_comp instanceof JComboBox )
        {
          ((JComboBox)temp.m_comp).setSelectedItem( strData );
        }

        else
        if ( temp.m_comp instanceof JList )
        {
          ((JList)temp.m_comp).setSelectedValue( strData, true );
        }
      } // end if

    } // end for()

  } // end load()


  /**
   * Unloads the form data into the data object using only the bound parameter names specified
   *
   * @param dlmsParamNames - A delimited string of parameter or data object bound names
   *
   * @exception throws Exception if no VwDataObject instance is set
   */
  public final void unLoad( VwDelimString dlmsParamNames ) throws Exception
  {
    if ( m_dataObj == null )
    {
      displayMsg( m_msgs.getString( "VwUtil.Error" ), m_msgs.getString( "VwUtil.MissingDataObj" ) );
      throw new Exception( m_msgs.getString( "VwUtil.MissingDataObj" ) );
    }

    if ( dlmsParamNames == null )
    {
      displayMsg( m_msgs.getString( "VwUtil.Error" ), m_msgs.getString( "VwUtil.MissingParamNames" ) );
      throw new Exception( m_msgs.getString( "VwUtil.MissingParamNames" ) );
    }

    String strName;
    dlmsParamNames.reset();

    while( (strName = dlmsParamNames.getNext() ) != null )
    {
      VwServiceableComp temp = (VwServiceableComp)m_mapServiceSupport.get( strName );

      if ( temp.m_comp instanceof VwTextField )
      {
        validateTextField( (VwTextField)temp.m_comp );

        if ( ((VwTextField)temp.m_comp).hasErrors() )
          throw new Exception( m_msgs.getString( "VwUtil.FieldValidationErrors" ) );
      }

      if ( temp.m_fBound )
        xferToDataObj( temp );

    } // end while

  } // end unLoad()


  /**
   * Unloads the form data into the data object using names from the bound controls
   *
   * @exception throws Exception if no VwDataObject instance is set
   */
  public final void unLoad() throws Exception
  {
    if ( m_dataObj == null )
      throw new Exception( m_msgs.getString( "VwUtil.MissingDataObj" ) );


    for ( Iterator iComp =  m_mapServiceSupport.values().iterator(); iComp.hasNext(); )
    {
      VwServiceableComp temp = (VwServiceableComp)iComp.next();

      if ( temp.m_comp instanceof VwTextField )
      {
        validateTextField( (VwTextField)temp.m_comp );

      }

      if ( temp.m_fBound )
        xferToDataObj( temp );

    } // end for()

  } // end unlload()


  /**
   * Transfers data from the given control on the panel to the current VwDataObject
   *
   * @param VwServiceableComp - The Vozzware LLC JComponent instance
   */
  private void xferToDataObj( VwServiceableComp temp ) throws Exception
  {
    String strData = null;

    if ( temp.m_comp instanceof JTextComponent )
    {
      strData = ((JTextComponent)temp.m_comp).getText();
    }
    else
    if ( temp.m_comp instanceof JCheckBox )
    {
      if ( ((JCheckBox)temp.m_comp).isSelected() )
        strData = m_strCheckedText;
      else
        strData = m_strUnCheckedText;

    } // end if
    else
    if ( temp.m_comp instanceof JRadioButton )
    {
      if ( ((JRadioButton)temp.m_comp).isSelected() )
       strData = ((JRadioButton)temp.m_comp).getText();
    }
    else
    if ( temp.m_comp instanceof JList )
    {
      strData = (String)((JList)temp.m_comp).getSelectedValue();
    }
    else
    if ( temp.m_comp instanceof JComboBox )
    {
      strData = (String)((JComboBox)temp.m_comp).getSelectedItem();
    }
    else
      return;

    if ( strData == null )
      strData = "";

    try
    {
      m_dataObj.put( new VwElement( temp.m_strID, strData ) );

    }
    catch( Exception nf )
    {
      // Add it to object if it dosent exist

      m_dataObj.put( new VwElement( temp.m_strID, strData ) );

    } // end catch()

  } // end xferToDataObj()


  /**
   * Disables all push buttons
   *
   */
  public final void disableButtons()
  { setButtonState( false ); }


  /**
   * Enables all push buttons
   *
   */
  public final void enableButtons()
  { setButtonState( true ); }


  /**
   * Enables/Disables all push buttons, depending upon the value of fEnable
   *
   * @param fEnable - If True, enable all push buttons; otherwise, disable all
   * push buttons.
   */
  public final void setButtonState( boolean fEnable )
  {
    for ( Iterator iComp =  m_mapServiceSupport.values().iterator(); iComp.hasNext(); )
    {
      VwServiceableComp temp = (VwServiceableComp)iComp.next();

      if ( temp.m_comp instanceof VwButton )
      {
        if ( fEnable )
         ((VwButton)temp.m_comp).unLock();
        else
        ((VwButton)temp.m_comp).lock();

      }
    } // end for()

  } // end setButtonState()


  /**
   * Sets all text fields to blank, and deselects any check boxes
   *
   */
  public final void clearAllFields()
  {
    for ( Iterator iComp =  m_mapServiceSupport.values().iterator(); iComp.hasNext(); )
    {
      VwServiceableComp temp = (VwServiceableComp)iComp.next();

      if ( temp.m_comp instanceof JToggleButton )
        ((JToggleButton)temp.m_comp).setSelected( false );
      else
      if ( temp.m_comp instanceof VwTextField )
      {
        ((VwTextField)temp.m_comp).clear();
        ((VwTextField)temp.m_comp).setNormalColors();
      }
      else
      if ( temp.m_comp instanceof JTextComponent )
      {
        ((JTextComponent)temp.m_comp).setText( "" );
      }
      else
      if ( temp.m_comp instanceof JTextComponent )
        ((JTextComponent)temp.m_comp).setText( "" );

    } // end for()

  } // end clearAllFields()



  /**
   * Called when data in an VwTextField is losing focus and requires validation
   *
   * @param textEvent - The VwTextField event with the failing reason
   */
  public void loosingFocus( VwTextFieldEvent textEvent )
  {
    if ( !m_fValidateOnLostFocus )
      return;                    // Donn't vaidate on lost focus

    try
    {
      validateTextField( (JTextComponent)textEvent.getSource() );
    }
    catch( Exception e ){}

  } // end loosingFocus()


  /**
   * Validates the data of the given VwTextField
   *
   * @param VwJTextField - The VwTextField to validate
   *
   * @exception throws Exception if the validation fails
   */
  private void validateTextField( JTextComponent textComp ) throws Exception
  {
    if ( m_fComingFromMsgBox )
      return;

      try
      {
        if ( textComp instanceof VwTextField )
          ((VwTextField)textComp).validateData();
        else
          ((VwPasswordField)textComp).validateData();

        m_errorFieldInFocus = null;         // Passed edits
      }
      catch( Exception e )
      {

        String strMsg = "This field failed validation checks for the following reason:\n"
                      + e.getMessage();

        m_errorFieldInFocus = textComp;

        // *** Mark form as in valid

        m_fInvalidForm = true;

        // Set focus back to edit field in error
        displayMsg( "Field Validation Error", strMsg );

        textComp.requestFocus();

        throw new VwFieldValidationException( strMsg );

      } // end catch()

  } // end validateTextField()


  /**
   * Sent when an invalid character is typed into an VwTextField.  All characters
   * typed into an VwTextField are validated against the edit mask (if one is defined).
   *
   * @param textEvent - The VwTextField event with the failing reason
   */
  public void invalidCharacter( VwTextFieldEvent textEvent )
  {
    m_fInvalidForm = true;

  } // end invalidCharacter()


  /**
   * Sent when a previous invalidCharacter() or validationFailed() event was sent
   * to inform interested objects that they can cleanup or clear any status messages
   * that may be displayed.
   *
   * @param textEvent - The VwTextField event identifying the cleared field
   */
  public void clearValidationError( VwTextFieldEvent textEvent )
  {
     m_fInvalidForm = false;

  } // end clearValidationError()





  public void dataChanged( VwDataChangedEvent dca )
  {

    VwServiceable ss = dca.getSource();
   /*
    String strServiceName = ss.getDataChangedServiceName();

    VwDelimString dlmsParamNames = new VwDelimString( ",", btn.getServiceParamNames() );

    m_dataObj = null;

    m_dataObj = new VwDataObject( strServiceName );

    setCursor( Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR ) );

    try
    {
      try
      {
        if ( dlmsParamNames.toString().equals( "*" ) )
          unLoad();                   // Unload form with all bound fields
        else
          unLoad( dlmsParamNames );   // else unload form with just the names requested
      }
      catch( Exception e )
      {
        return;
      }

      disableButtons();

      if ( m_serviceListner != null )
        m_serviceListner.preServiceExec( new VwServiceEvent( m_dataObj, comp ) );

      m_VwClientServiceMgr.execService( m_dataObj );

      VwDataObject dataObj = m_VwClientServiceMgr.getServiceData();

      if ( m_serviceListner != null )
      {
        while( true )
        {
          boolean fContinue = m_serviceListner.postServiceExec( new VwServiceEvent( dataObj, comp ) );

          if ( dataObj == null )
            break;

          setDataObject( dataObj );          // Set the form with the loaded data
          load();

          if ( !fContinue )
            break;

          dataObj = m_VwClientServiceMgr.getServiceData();

        } // end while

        return;      // All Done

      } // end if ( m_serviceListner != null )

      // *** Default processing here if no service listner is defined

      if ( dataObj == null )
      {
        displayMsg( m_msgs.getString( "VwUtil.Informational" ),
                    m_msgs.getString( "VwUtil.NotFound" ) );

        return;

      } // end if

      try
      {
        int nRowsEffected = dataObj.getInt( "STATUS" );
        if ( m_serviceListner == null )
          displayMsg( m_msgs.getString( "VwUtil.Informational" ),
                      "There were " + String.valueOf( nRowsEffected )
                      + " Effected" );

        return;

      } // end inner try

      catch( Exception e )
      {
        // We get here on a not found get request and if there is is STATUS key then a row
        // was returned

        while( dataObj != null )
        {
          setDataObject( dataObj );
          load();
          dataObj = m_VwClientServiceMgr.getServiceData();

        } // end while()

      } // end catch()

    } // end outer try

    catch( Exception ex )
    {

      displayMsg( m_msgs.getString( "VwUtil.Error" ), ex.getMessage() );
      // *** For this exception from unload we don't do anything because a message box
      // *** has already been displayed.  Just fall through here for cleanup.

      if ( m_errorFieldInFocus != null )
        m_errorFieldInFocus.requestFocus();

    } // end catch()
    finally
    {
      setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ) );
      enableButtons();
    }

    */
  } //end dataChanged()


  /**
   * Displays a modal message box
   *
   * @param strCaption - The message box titlebar
   * @param strMsg - The message to be displayed
   */
  private void displayMsg( String strCaption, String strMsg )
  {
    if ( m_parent == null )
      m_parent = findJFrame();

    JOptionPane.showMessageDialog( m_parent, strMsg, strCaption, JOptionPane.ERROR_MESSAGE );

  } // end displayMsg()


  /**
   * Finds the parent JFrame instance of this panel
   *
   * @return The parent JFrame instance, or null if a parent cannot be found
   */
  private JFrame findJFrame()
  {
    Container compWindow = this;

    // *** Walk parent chain until we find a JFrame instance

    while( compWindow != null )
    {
      if ( compWindow instanceof JFrame )
        return (JFrame)compWindow;

      compWindow = compWindow.getParent();

    } // end while()

    return null;                          // Not found

  } // end findJFrame()


  /**
   * Gets the ServiceInfo object for the Service name requested
   *
   * @param strServiceName - the name of the Service to fetch
   *
   * @return A ServiceInfo object if found; otherwise, null if not found
   */
  private ServiceInfo getServiceInfo( String strServiceName )
  {
    for ( int x = 0; x < m_vecServices.size(); x++ )
    {
      if ( ((ServiceInfo)m_vecServices.elementAt( x )).m_strServiceName.equals( strServiceName ) )
        return (ServiceInfo)m_vecServices.elementAt( x );

    } // end for()

    return null;

  } // end getServiceInfo()


} // end class VwSmartPanel


// *** End of VwSmartPanel.java ***

