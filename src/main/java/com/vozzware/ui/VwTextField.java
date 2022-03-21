/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwTextField.java

Create Date: Apr 11, 2003
============================================================================================
*/

package com.vozzware.ui;

import com.vozzware.util.VwEdit;
import com.vozzware.util.VwExString;
import com.vozzware.util.VwInvalidMaskException;
import com.vozzware.xml.VwDataDictionary;

import javax.swing.JTextField;
import java.awt.Color;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ResourceBundle;
import java.util.Vector;



/**
 * This class extends the java JTextField class to add data validation and data aware
 * capabilities through the VwDataDictionary, Vw Servers, and VwEdit classes.
 * The VwTextField can limit the number and type of characters entered using the
 * dictionary definitions.  If a character is entered that conflicts with a defined
 * edit mask, the character will be ignored and an invalid character event will be
 * sent to interested listeners.  The data in the VwTextField is formatted according
 * to the edit mask defined when the text field looses focus.
 */
public class VwTextField extends JTextField
{
  private VwEdit         m_itcEdit = null;              // Edit class

  private Vector          m_vecListeners;                // Vector for registered listeners

  private Color           m_clrErrBackGround;            // Error backgroung color
  private Color           m_clrErrText;                  // Error text color

  private Color           m_clrBackGround;               // Normal background color
  private Color           m_clrText;                     // Normal text color

  private boolean         m_fKeyError = false;           // Invalid keystroke error flag
  private boolean         m_fValidationError = false;    // Field in error flag
  private boolean         m_fFormatError = false;
  private boolean         m_fMarkTextOnFocus = true;     // Hilite text on entry into field

  private boolean         m_fWantSuper = false;

  private ResourceBundle  m_msgs;                        // String message bundle

  private String          m_strCompName;                 // Component name used by httpServices


  private boolean         m_fHasInitActions = false;

  private boolean         m_fHasActions = false;

  private String[]        m_astrCompListenerNames;

  /**
   * Constructs an VwTextField object
   */
  public VwTextField()
  {
    super();

    try
    {
      m_itcEdit = new VwEdit( "*" );                      // Allow anything as default
    }
    catch( Exception e ){}                                 // We know this mask is valid

    m_msgs = ResourceBundle.getBundle( "com.vozzware.util.vwutil" );

    m_vecListeners = new Vector( 1 );

    m_clrErrBackGround = Color.red;                        // Error backgroung color
    m_clrErrText = Color.white;                            // Error text color

    m_clrBackGround = getBackground();                     // Normal background color
    m_clrText = getForeground();                           // Normal text color

    // *** and inline class to handle key events so we can stop user input when the
    // *** max allowed charcters has been reached

    addKeyListener( new KeyAdapter(){
     public void keyTyped( KeyEvent ke )
     {
       char ch = ke.getKeyChar();
       int nch = ch;
       if ( ke.isActionKey() ||  ( nch ==  KeyEvent.VK_DELETE ) || ke.getKeyCode() == KeyEvent.VK_DELETE ||
            ke.getKeyCode() == KeyEvent.VK_BACK_SPACE || ch == '\b' )
        return;            // Always allow delete and backspace keys

       int nMaxChars = m_itcEdit.getMaxCharsAllowed();
       if (  nMaxChars > 0 )
       {
         m_fWantSuper = true;
         if ( getText().length() == nMaxChars )
         {
           if ( getSelectedText() == null )
           {
             // *** If there is no marked text thAt would cause an overwrite than don't allow
             // *** the character
  
             Toolkit.getDefaultToolkit().beep();
             ke.consume();
           }

         } // end if

       } // end if( nMaxChars > 0 )

       if ( ch != (char)0 )
       {
         if ( !m_itcEdit.isValid( ch ) )
         {
           m_fKeyError = true;
           ke.consume();
           fireInvalidCharEvent( m_itcEdit.getAllowableCharacters() );
         }

         if ( m_itcEdit.isNumeric() )
         {
           m_fWantSuper = true;
           String strData = VwTextField.this.getText();

           // *** Only one plus, minus or decimal point allowed or
           // *** if its a plus or minus sign  it has to be the first character
           if ( ch == '+' || ch == '-' || ch == '.' )
           {
             if ( VwExString.isin( ch, strData) ||
               (  strData.length() > 0 && ( ch == '+' || ch == '-' ) ) )
             {
               m_fKeyError = true;
               ke.consume();
               fireInvalidCharEvent( m_itcEdit.getAllowableCharacters() );
             }

           } // end if

         } // end if ( m_itcEdit.isNumeric() )

         else
         {
           if ( m_fKeyError )
           {
             m_fKeyError = false;
             fireclearValidationErrorEvent();
           }

         } // end else

       } // end if ( ch != (char)0 )
     }
     } );

    // *** and inline class to handle key events so we can stop user input when the
    // *** max allowed charcters have been reached

    addFocusListener( new FocusAdapter()
    {
      public void focusLost( FocusEvent fe )
      {
        try
        {
          // *** This will force a format of the data acording to the edit mask

          setText( getText() );

        } catch( Exception e ){}

        fireLostFocusEvent();

      } // end focusLost

      public void focusGained( FocusEvent fe )
      {
        // For numeric fields put numeric data back to it's prue unformated form

        VwTextField.this.focusGained();
      }

    } ); // end class FocusAdapter

  } // end VwTextField


  /**
   * Adds a VwTextField Listener
   *
   * @param itcTextFiledListener - The listener to add
   */
  public synchronized void addTextFieldListener( VwTextFieldListener itcTextFiledListener )
  {
    m_vecListeners.addElement( itcTextFiledListener );

  } // end addTextFieldListener()


  private void focusGained()
  {
    m_fWantSuper = true;

    if ( m_itcEdit.isNumeric() )
      super.setText( VwExString.numericStrip( super.getText() ) );

    m_fWantSuper = true;
    if ( m_fMarkTextOnFocus )
      select( 0, super.getText().length() );

  } // end focusGained()


  /**
   * Override of the setText() method to format the data according to the edit mask
   *
   * @param strText - The text to place in this text field
   */
  public final void setText( String strText )
  {
    if ( m_fWantSuper )
    {
      m_fWantSuper = false;         // Flag used internally to by pass formatting of data
      super.setText( strText );
    }

    try
    {
      super.setText( m_itcEdit.format( strText ) );
      m_fFormatError = false;
    }
    catch( Exception e )
    {
      m_fFormatError = true;
    }

  } // end setText()


  /**
   * Override of getText() to strip out format characters for numeric data
   *
   * @return A string with the data in the TextField; in the case of numeric data,
   * return string has the format characters removed.
   */
  public final String getText()
  {
    if ( m_fWantSuper )
    {
      m_fWantSuper = false;        // Flag used internally to by pass formatting of data
      return super.getText();
    }

    if ( m_itcEdit.isNumeric() )
      return VwExString.numericStrip( super.getText() );

    return super.getText();

  } // end get text()

  /**
   * Clears the field
   *
   */
  public void clear()
  { super.setText( "" ); }


  /**
   * Returns True if there is an existing format or validation constraint error
   *
   */
  public final boolean hasErrors()
  {
    if ( m_fValidationError || m_fFormatError )
      return true;

    return false;

  } // end hasErrors()


  /**
   * Validates and formats the data in the edit field based upon the edit and
   * constraint attributes define for this TextField.
   *
   * @exception throws Exception if the data is invalid
   */
  public void validateData() throws Exception
  {
    try
    {
      // try to get a successfull format first if we have a current format error

      if ( m_fFormatError )
      {
        super.setText( m_itcEdit.format( getText() ) );
        m_fFormatError = false;
      }

      m_itcEdit.validate( getText() );

      if ( m_fValidationError )
      {
        m_fValidationError = false;

        // Return field back to it's defualt normal color
        setNormalColors();

      } // end if

    } // end try

    catch( Exception e )
    {
      m_fValidationError = true;

      // *** Put field in its error colors
      setErrorColors();

      // *** Re-Throw the exception

      throw new Exception( e.toString() );
    }

  } // end validate()


  /**
   * Sets the data key associated with this TextField
   *
   * @param strName - The data key (Name ID) for this TextField
   */
  public void setNameID( String strName ) throws Exception
  {
    super.setName( strName );

    Component comp = getParent();

    if ( comp instanceof VwSmartPanel )
      ((VwSmartPanel)comp).bind( this, strName );

  } // end setNameID()


  /**
   * Gets the data key associated with this TextField
   *
   * @return The data key (Name ID) for the TextField
   */
  public final String getNameID()
  { return super.getName(); }


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
   * Sends a lostFocus event to all registered listeners
   *
   * @param strReason - A string with the failing reason
   */
  public synchronized void fireLostFocusEvent()
  {
    VwTextFieldEvent tfe = new VwTextFieldEvent( this, "" );

    int nLen = m_vecListeners.size();

    for ( int x = 0; x < nLen; x++ )
    {
      VwTextFieldListener tfl = (VwTextFieldListener)m_vecListeners.elementAt( x );
      tfl.loosingFocus( tfe );

    } // end for()

  } // end of fireLostFocusEvent()


  /**
   * Sends an invalidCharacter event to all registered listeners
   *
   * @param strReason - A string with the failing reason
   */
  public synchronized void fireInvalidCharEvent( String strReason )
  {
    VwTextFieldEvent tfe = new VwTextFieldEvent( this, strReason );

    int nLen = m_vecListeners.size();

    for ( int x = 0; x < nLen; x++ )
    {
      VwTextFieldListener tfl = (VwTextFieldListener)m_vecListeners.elementAt( x );
      tfl.invalidCharacter( tfe );

    } // end for()

  } // end of fireInvalidCharEvent()


  /**
   * Sends a clearValidationError event to all registered listeners
   *
   * @param strReason - A string with the failing reason
   */
  public synchronized void fireclearValidationErrorEvent()
  {
    VwTextFieldEvent tfe = new VwTextFieldEvent( this, "" );

    int nLen = m_vecListeners.size();

    for ( int x = 0; x < nLen; x++ )
    {
      VwTextFieldListener tfl = (VwTextFieldListener)m_vecListeners.elementAt( x );
      tfl.clearValidationError( tfe );

    } // end for()

  } // end of fireclearValidationErrorEvent()


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


} // end class VwTextField{}

// *** End of VwTextField.java ***
