/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwEdit.java

Create Date: Apr 11, 2003
============================================================================================
*/

package com.vozzware.util;

import com.vozzware.xml.VwDataDictionary;

import java.util.ResourceBundle;

/**
 * <br>This class formats and validates data.  The format is based on an edit mask that is
 * <br>specified in the constructor of the class.  The setEditMask() method may also be used
 * <br>to change the edit mask.  There are many additional properties that can be used to
 * <br>filter data, including values and ranges, minimum and maximum lengths.  This class
 * <br>also accepts an VwDataDictionary object that sets and overrides the data properties.
 * <br>
 * <br>Once an edit mask is defined, the isValid() method can be used to test any character
 * <br>for validity based upon the edit mask.  This class defines many prebuilt standard masks
 * <br>for common data types such as Dates, Social Security, Phone numbers, file names, addresses,
 * <br>etc.
 * <br>
 * <br>All edit masks are created as a string of characters, consisting of reserved format
 * <br>characters and delimiters defined as follows:
 * <br>
 * <br>X - Alpha-numeric character a-Z digits 0-9, data is automatically converted to UPPER CASE
 * <br>x - Alpha-numeric character a-Z digits 0-9 data is automatically converted to lower case
 * <br>c - Alpha-numeric character a-Z digits 0-9, case is preserved as is
 * <br>
 * <br>A - Alpha only character a-Z data is automatically converted to UPPER CASE
 * <br>a - Alpha only character a-Z data is automatically converted to lower case
 * <br>l - Alpha only character a-Z case is preserved as is
 * <br>
 * <br>9 - digits 0 - 9, numeric masks that are not currency or mathematical values
   <br>    (e.g., Social Security and telephone numbers)
 * <br>#   digits 0 - 9, numeric masks for data used in mathematical operations
 * <br>
 * <br>'*' (asterisk) The asterisk allows any character in any case
 * <br>
 * <br>In addition to the above reserved format symbols, it may be necessary to add special
 * <br>characters like spaces and other special symbols.  This is accomplished with the \t escape
 * <br>symbols.  The following example adds the space and comma to the alpha-numeric character set
 * <br>String strNameMask = "\t ,\tc".  The escape filter must always be specified first,
 * <br>followed my the format mask.  In the example, the space and comma characters are defined in
 * <br>between the \t pairs.  The 'c' symbol is for the alphanumeric character set, which does not
 * <br>include spaces or commas.
 * <br>
 * <br>If only one format symbol is specified in the mask, the format symbol applies for the
 * <br>maximum allowed length of the data.  If more than one format symbol is repeated, the mask
 * <br>is the number of format symbols (replication length).  E.g., a format of "c" applies
 * <br>to the total length of the data, but a format of "ccccc" allows only 5 characters.
 * <br>A Social Security mask is defined as follows: "999-99-9999".  Notice that 9's, not #'s,
 * <br>are used as Social Security numbers are not numeric (used in mathematical operations).
 * <br>A salary mask is defined as follows: "###,###.##".  The "#" denotes a numeric mask, and
 * <br>has additional constraints.  The numeric masks may only have the comma delimiters, one
 * <br>decimal point, an appropriate currency symbol (like a dollar or English pound symbol),
 * <br>and a '+' a '-' or the '!'(suppress leading spaces) sign.  If the data allows negative numbers, the minus sign must be included
 * <br>at the beginning of the mask, i.e., "-###.#####".  Numeric numbers are always left justified
 * <br>based on the length of the mask.  The default fill character is a space but may be overridden by placing
 * <br>a different character as the first character in the mask. i.e."*-###,###.##" uses an asterisk to left
 * <br>fill unused digits. The '!' as the first character suppress leading spaces if the fill character is space.
 * <br>Truncation exceptions are thrown if the length of the data ,would cause high order truncation.
 * <br>
 * <br>The following symbols are reserved as format delimiters:
 * <br>@  - at sign
 * <br>.  - period
 * <br>-  - minus sign
 * <br>+  - plus sign
 * <br>!  - Exclamation or not sign
 * <br>(  - left paren
 * <br>)  - right paren
 * <br>[  - left bracket
 * <br>]  - right bracket
 *
 */
public class VwEdit
{
  private String          m_strMask;                   // The edit mask used for this instance
  

  private VwDate         m_itcDate = null;            // Used for date masks

  private int             m_nMaxChars = 0;             // Max characters allowed, 0 = unlimited
  private int             m_nMinChars = 0;             // Minimum characters required

  private String          m_strMaskList = "XxAa9cl*";  // The format mask characters
  private String          m_strDelimList= "@.+-()[]";  // The special reserved delimiters

  private String          m_strFilter = "";            // Additional optional character filter

  private String          m_strAllowableChars;         // The allowable characters according to mask

  private VwDelimString  m_dlmsValues = null;         // Value/ranges

  private String          m_strSeparators = ",";       // Default value/range separators
  
  private boolean         m_fIsDate = false;           // Returns true if mask is a date mask
  private boolean         m_fIsNumeric = false;        // Returns true if mask is numeric
  private boolean         m_fIsDecimal = false;        // Returns true if mask has decimals
  private boolean         m_fRequiredEntry = false;    // If true, data length cannot be 0
  private boolean         m_fMaskOverride = false;     // If true max overrides max input length
  private boolean         m_fInValidation = false;

  private ResourceBundle  m_msgBundle;                 // Resource bundle for all database messages

  private char            m_chValueSep = ',';          // Value separator
  private char            m_chRangeSep = '-';          // Range separator
  private char            m_chFillCharacter = ' ';     // Fill character for padding
  private boolean         m_fCaseCompare = true;       // Used in value range tests

  
  // *** Common edit mask constants

  /**
   * Edit mask constant for the social security number
   */
  public final static String SSN = "999-99-9999";

  /**
   * Edit mask constant for a 5 digit zip code
   */
  public final static String ZIP5 = "99999";

  /**
   * Edit mask constant for a 9 digit zip code
   */
  public final static String ZIP9 = "99999-9999";

  /**
   * Edit mask constant for the phone number without area code
   */
  public final static String PHONENBR = "999-9999";

  /**
   * Edit mask constant for the phone number
   */
  public final static String PHONEAREA = "(999) 999-9999";


  /**
   * Edit mask constant for a persons complete name ( allows spaces and commas, periods and alphanumerics
   */
  public final static String PERSON_NAME_FULL = "\t ,.\tc";

  /**
   * Edit mask constant for a persons first or last name component only no spaces
   */
  public final static String PERSON_NAME = "c";


  /**
   * Edit mask constant for an address
   */
  public final static String ADDRESS = "\t ,.&#\tc";

  /**
   * Edit mask constant for a filename
   */
  public final static String FILENAME = "\t ._\tc";


  /**
   * Edit mask constant for a file path
   */
  public final static String FILEPATH = "\t \\/.$_\tc";

  /**
   * Edit mask constant for money using a fixed position dollar sign and space filled right justified fill character
   */
  public final static String MONEY = "-###,###,###,###,###.##";

  /**
   * Edit mask constant for money with added dollar sign
   */
  public final static String MONEY$ = "$-###,###,###,###,###,###.##";

  /**
   * Edit mask constant for money with using floating dollar sign
   */
  public final static String MONEY$$ = "$$-###,###,###,###,###,###.##";

  /**
   * Edit mask constant for money with a padded '*' commonly used when printing checks
   */
  public final static String MONEY_CHECK = "*$-###,###,###,###,###,###.##";

  
  private final static String s_astrPreDefNames[] = { "SSN", "ZIP5", "ZIP9", "PHONENBR", "PHONEAREA",
    "PERSON_NAME_FULL", "PERSON_NAME", "ADDRESS",
    "FILENAME", "FILEPATH", "MONEY", "MONEY$",
    "MONEY$$", "MONEY_CHECK" };

  private final static String s_astrPreDefValues[] = { SSN, ZIP5, ZIP9, PHONENBR, PHONEAREA,
                                                       PERSON_NAME_FULL, PERSON_NAME, ADDRESS,
                                                       FILENAME, FILEPATH, MONEY, MONEY$,
                                                       MONEY$$, MONEY_CHECK };

  /**
   * Constructs the edit object
   *
   * @paramm_strMask - The edit mask used for validation and formatting
   *
   * @exception throws Exception, VwInvalidMaskException
   */
  public VwEdit()
  {
    m_msgBundle = ResourceBundle.getBundle( "resources.properties.vwutil" );

    m_strAllowableChars = null;  // The default setting

    try
    {
      setEditMask( "*" );

      determineAllowableCharacters();
    }
    catch( Exception e )
    { ; }

  } // end VwEdit()


  /**
   * Constructs the edit object
   *
   * @paramm_strMask - The edit mask used for validation and formatting
   *
   * @exception throws Exception, VwInvalidMaskException
   */
  public VwEdit( String strMask ) throws Exception,
                                          VwInvalidMaskException
  {
    m_msgBundle = ResourceBundle.getBundle( "resources.properties.vwutil" );

    m_strAllowableChars = null;  // The default setting

    setEditMask( strMask );

    determineAllowableCharacters();

  } // end VwEdit()


  /**
   * Returns an array of predefined edit mask names
   * @return
   */
  public String[] getPredefinedMaskNames()
  { return s_astrPreDefNames; }
  
  /**
   * Returns an array predefined edit mask values
   * @return
   */
  public String[] getPredefinedMaskValues()
  { return s_astrPreDefValues; }
  
  /**
   * Returns a predefined edit mask value for the index specified
   * @param ndx The predefined mask value to get
   * @return
   */
  public String getPredefinedMaskValue( int ndx )
  { return s_astrPreDefValues[ ndx ]; }
  
  /**
   * Sets the edit mask for formatting and data validation
   *
   * @param strMask - The edit mask to be set
   *
   * @exception throws VwInvalidMaskException if the mask has invalid format characters
   */
  public final void setEditMask( String strMask ) throws VwInvalidMaskException
  {
    // *** Treat an empty string like an asterisk
    if ( strMask == null )
    {
      m_strMask = "*";
      return;
    }

    if ( strMask.length() == 0 )
    {
      m_strMask = "*";
      return;
    }

    m_strMask = splitMask( strMask );

    int nPos = m_strMask.indexOf( '%' );
    if ( nPos >= 0 )
    {
      if ( nPos < ( strMask.length() - 1 ) )
      {
        char ch = strMask.charAt( nPos + 1 );

        if ( VwExString.isin( ch, "AaBbdHImMSyY" ) )
        {
          m_fIsDate = true;
          m_strMask = strMask;
          m_itcDate = new VwDate( m_strMask );
          m_nMaxChars = 0;

          return;
        }

      } // end if

    } // end if ( nPos >= 0 )

    getMaxCharsAllowedFromMask();

    if ( m_strMask.indexOf( '#' ) >= 0 )
    {
      if ( !isValidNumericMask( m_strMask ) )
      {
        String str = m_msgBundle.getString( "VwEdit.InvalidNumericMask" );
        throw new VwInvalidMaskException( str );
      }

    }

    determineAllowableCharacters();
  } // end setEditMask()


  /**
   * Gets the edit mask defined for this instance
   *
   * @return a String containing the edit mask define for this instance
   */
  public final String getEditMask()
  { return m_strMask; }


  /**
   * Sets the data dictionary object with the data dictionary values
   *
   * @param itcDictionaryObj - The VwDataDictionary object with the data dictionary contstraints
   *
   * @exception throws Exception if the expected data dictionary keys are not present
   */
  public final void setDataDictionary( VwDataDictionary itcDictionaryObj ) throws Exception
  {
    setEditMask( itcDictionaryObj.getString( VwDataDictionary.EDIT_MASK ) );
    setValuesRanges( itcDictionaryObj.getString( VwDataDictionary.VALUES_RANGES ) );
    setMaxCharsAllowed( itcDictionaryObj.getInt( VwDataDictionary.MAX_INPUT ) );
    setMinCharsRequired( itcDictionaryObj.getInt( VwDataDictionary.MIN_INPUT ) );

    String strVal = itcDictionaryObj.getString( VwDataDictionary.VALUE_SEP );
    if ( strVal.length() == 1 )
      setValueSeparator( strVal.charAt( 0 ) );
    else
      setValueSeparator( ',' );     // Comma is the default

    strVal = itcDictionaryObj.getString( VwDataDictionary.RANGE_SEP );
    if ( strVal.length() == 1 )
      setRangeSeparator( strVal.charAt( 0 ) );
    else
      setRangeSeparator( '-' );     // Hyphen is the default

    if ( itcDictionaryObj.getInt( VwDataDictionary.ENTRY_REQUIRED ) > 0 )
      setRequiredEntry( true );
    else
      setRequiredEntry( false );

  } // end setDataDictionary()


  /**
   * Sets the maximum number of input characters for the text field associated with the edit object
   *
   * NOTE: Upon construction, this value is 0, which permits an unlimited number of characters
   *
   * @param nMaxChars - The maximum number of characters allowed (0 = unlimited)
   */
  public final void setMaxCharsAllowed( int nMaxChars )
  { if ( !m_fMaskOverride ) m_nMaxChars = nMaxChars; }


  /**
   * Gets the current property setting for the maximum number of input characters allowed
   *
   * @return An int containing the maximum input characters allowed
   */
  public final int getMaxCharsAllowed()
  { return m_nMaxChars; }


  /**
   * Sets the required data flag (data length must be > 0)
   *
   * @param fRequired - True if data is required; False if it is not
   */
  public final void setRequiredEntry( boolean fRequiredEntry )
  { m_fRequiredEntry = fRequiredEntry; }


  /**
   * Gets the required data flag property setting
   *
   * @return True if data is required; False if it is not
   */
  public final boolean getRequiredEntry()
  { return m_fRequiredEntry; }

  /**
   * Sets the minimum number of characters required for the text field associated with the edit object
   *
   * @param nMinChars - The minimum number of characters required
   */
  public final void setMinCharsRequired( int nMinChars )
  { m_nMinChars = nMinChars; }


  /**
   * Gets the current property se3tting for the minimum number of input characters required
   *
   * @return An integer containing the minimum characters required
   */
  public final int getMinCharsRequired()
  { return m_nMinChars; }


  /**
   * Returns the allowable characters based upon the edit mask supplied
   *
   * @return A string containing the allowable characters
   */
  public final String getAllowableCharacters()
  { return m_strAllowableChars; }


  /**
   * Sets the values and ranges allowed.  The default value separator is the comma, and
   * the default range separator is the hyphen.
   *
   * @param strValuesRanges - The values and ranges string
   *
   * @exception - throws Exception if the value range data types conflict with
   * the edit mask defined
   */
  public final void setValuesRanges( String strValuesRanges ) throws Exception
  {
    if ( m_dlmsValues == null )
      m_dlmsValues = new VwDelimString( new String( new char[]{ m_chValueSep } ), strValuesRanges );
    else
      m_dlmsValues.setContents( strValuesRanges );

    // *** Make sure value ranges data types agree with edit mask specified

    validateValuesToMask();

  } // end setValuesRanges()


  /**
   * Sets the values and ranges allowed.  The default value separator is the comma, and
   * the default range separator is the hyphen.
   *
   * @param dlmsValuesRanges - An VwDelimString with the values and ranges
   *
   * @exception throws Exception if the values or range data types conflict with
   * the edit mask defined.
   */
  public final void setValuesRanges( VwDelimString dlmsValuesRanges ) throws Exception
  {
    dlmsValuesRanges.setDelimList( new String( new char[]{m_chValueSep} ) );

    if ( m_dlmsValues == null )
      m_dlmsValues = new VwDelimString( dlmsValuesRanges );
    else
      m_dlmsValues = dlmsValuesRanges;

    // *** Make sure value ranges data types agree with edit mask specified

    validateValuesToMask();

  } // end setValuesRanges()


  /**
   * Gets the current values and ranges of the edit object
   *
   * @return VwDelimString containing the current values/ranges (may be null)
   */
  public final VwDelimString getValuesRanges()
  { return m_dlmsValues; }


  /**
   * Sets the character used to separate the value list
   *
   * @param ch - The value separator character
   */
  public final void setValueSeparator( char ch )
  { m_chValueSep = ch; }


  /**
   * Returns the current value separator
   *
   * @return A char containing the value separator
   */
  public final char getValueSeparator()
  { return m_chValueSep; }


  /**
   * Sets the character used to separate the range (low - high) values
   *
   * @param ch - The range separator character
   */
  public final void setRangeSeparator( char ch )
  { m_chRangeSep = ch; }

  /**
   * Returns the current range separator
   *
   * @return A char containing the range separator
   */
  public final char getRangeSeparator()
  { return m_chRangeSep; }


  /**
   * Turns On/Off the value/range case sensitivity
   *
   * @param fOn - If True, value/range tests are case sensitive; if False, case is ignored
   */
  public final void setCaseSensitivity( boolean fOn )
  { m_fCaseCompare = fOn; }


  /**
   * Gets the current case sensitivity property
   *
   * @return True if value/range tests are case sensitive; False if case is ignored
   */
  public final boolean getCaseSensitivity()
  { return m_fCaseCompare; }


  /**
   * Gets additional allowable filter characters in addition to what the mask allows
   *
   * @return A string containing the allowable characters
   */
  public final String getFilter()
  { return m_strFilter; }


  /**
   * Returns true if the edit mask is a date mask
   *
   * @return True if the mask is a date mask; otherwise False is returned
   */
  public final boolean isDate()
  { return m_fIsDate; }


  /**
   * Determines if the edit mask is numeric
   *
   * @return True if the mask is numeric; otherwise False is returned
   */
  public final boolean isNumeric()
  { return m_fIsNumeric; }


  /**
   * Determines if the edit mask has decimal places
   *
   * @return True if the mask has decimal places; otherwise False is returned
   */
  public final boolean hasDecimals()
  { return m_fIsDecimal; }


  /**
   * Tests to see if a character is valid according to the given edit mask or character filter
   *
   * @param ch - The character to test
   *
   * @return True if the character is valid; otherwise False is returned
   */
  public final boolean isValid( char ch )
  {
    if ( m_strMask.startsWith( "*" ) )
      return true;

    if ( m_fIsNumeric )
    {
      String strAllowList = "+-";

      if ( m_fIsDecimal )
        strAllowList += ".";

      // *** if character is not a dgit it can only be a +- or decimal point

      if ( ! Character.isDigit( ch ) )
      {

        // Make sure non dgit character is a plus, miuns or decimal point

        if ( !VwExString.isin( ch, strAllowList ) )
          return false;
      }

      // *** If we get here it's a digit or allowable character so we're ok

      return true;
    } // end if( m_IsNumeric )

    // *** Character format and date tests ***

    if ( Character.isDigit( ch ) )
    {
      if ( VwExString.findAny( m_strMask, "xXCc#9mMdyYhHsS", 0 ) >= 0 )
        return true;

      return false;
    }


    if ( Character.isLetter( ch ) )
    {
      if ( VwExString.findAny( m_strMask, "xXaAcC", 0 ) >= 0  )
        return true;

      return false;
    }

    if ( m_strMask.indexOf( ch ) >= 0 )
      return true;

    if ( m_strFilter.indexOf( ch ) >= 0 )
      return true;


    return false;             // Not legal

  } // end isValid()


  /**
   * Validates the data against all the defined constraint properties, values, and ranges
   *
   * @param strData - The data to validate
   *
   * @exception throws Exception if any of the validation constraints fail
   */
  public final void validate( String strData ) throws Exception
  {
    if ( strData == null )
      strData = "";


    // *** See if data entry is mandatory

    if ( m_fRequiredEntry && strData.length() == 0 )
    {
      if ( m_nMinChars > 0 ) // Format msg with the min chars need
      {
        String strMsg = m_msgBundle.getString( "VwUtil.MinCharsNotMet" );
        int nPos = strMsg.indexOf( ',' );
        throw new Exception( strMsg.substring( 0, nPos ) + " " +
                             String.valueOf( m_nMinChars ) +  " " +
                             strMsg.substring( nPos + 1 ) );
      }
      else                   // Format a generic data required message
        throw new Exception( m_msgBundle.getString( "VwUtil.DataEntryRequired" ) );

    }

    // *** If data is entered check for minimum length

    if ( strData.length() > 0 )
    {
      if ( m_nMinChars > 0 && ( strData.length() < m_nMinChars ) )
      {
        String strMsg = m_msgBundle.getString( "VwUtil.MinCharsNotMet" );
        int nPos = strMsg.indexOf( ',' );
        throw new Exception( strMsg.substring( 0, nPos ) + " " +
                             String.valueOf( m_nMinChars ) +  " " +
                             strMsg.substring( nPos + 1 ) );
      } // end if


      if ( m_nMaxChars > 0 && strData.length() > m_nMaxChars )
      {
        String strMsg = m_msgBundle.getString( "VwUtil.MaxCharsAllowed" );
        int nPos = strMsg.indexOf( ',' );
        throw new Exception( strMsg.substring( 0, nPos ) + " " +
                             String.valueOf( m_nMaxChars ) +  " " +
                             strMsg.substring( nPos + 1 ) );
      } // end if

    } // end if (strData.length() > 0 )

    m_fInValidation = true;
    format( strData );
    m_fInValidation = false;

    // *** Test any values or ranges defined

    testValuesRanges( strData );

  } // end validate()


  /**
   * Formats the short value into a string according to the edit mask
   *
   * @param sNumber - The short value to format
   *
   * @return - A string containing the data formatted according to the edit mask
   *
   * @exception throws Exception if the input data violates the allowable characters in the
   * edit mask or filter.
   */
  public final String format( short sNumber ) throws Exception
  { return format( String.valueOf( sNumber ) ); }


  /**
   * Formats the integer value into a string according to the edit mask
   *
   * @param nNumber - The integer value to format
   *
   * @return - A string containing the data formatted according to the edit mask
   *
   * @exception throws Exception if the input data violates the allowable characters in the
   * edit mask or filter.
   */
  public final String format( int nNumber ) throws Exception
  { return format( String.valueOf( nNumber ) ); }



  /**
   * Formats the long value into a string according to the edit mask
   *
   * @param lNumber - The long value to format
   *
   * @return - A string containing the data formatted according to the edit mask
   *
   * @exception throws Exception if the input data violates the allowable characters in the
   * edit mask or filter.
   */
  public final String format( long lNumber ) throws Exception
  { return format( String.valueOf( lNumber ) ); }

  /**
   * Formats the float value into a string according to the edit mask
   *
   * @param fltNumber - The float value to format
   *
   * @return - A string containing the data formatted according to the edit mask
   *
   * @exception throws Exception if the input data violates the allowable characters in the
   * edit mask or filter.
   */
  public final String format( float fltNumber ) throws Exception
  { return format( String.valueOf( fltNumber ) ); }


  /**
   * Formats the double value into a string according to the edit mask
   *
   * @param dblNumber - The double value to format
   *
   * @return - A string containing the data formatted according to the edit mask
   *
   * @exception throws Exception if the input data violates the allowable characters in the
   * edit mask or filter.
   */
  public final String format( double dblNumber ) throws Exception
  { return format( String.valueOf( dblNumber ) ); }


  /**
   * Formats the string input data according to the edit mask
   *
   * @param strData - The data to format
   *
   * @return - A string containing the data formatted according to the edit mask
   *
   * @exception throws Exception if the input data violates the allowable characters in the
   * edit mask or filter.
   */
  public final String format( String strData ) throws Exception
  {
    if ( strData == null )
      throw new Exception( m_msgBundle.getString( "VwUtil.NullData" ) );

    if ( !m_fInValidation )
      validate( strData );

    // *** if data length is 0 theres nothing more to do here

    if ( strData.length() == 0 )
      return strData;              // Nothing to edit

    if ( isDate() )
    {
      m_itcDate.setDate( strData, m_strMask );
      if ( !m_itcDate.isValid() )
        throw new Exception( m_itcDate.getErrDesc() );

      return m_itcDate.format();

    } // end if ( isDate() )

    if ( m_strMask.indexOf( '#' ) >= 0 )
      return doNbrFormat( strData );

    return doCharacterFormat( strData );

  } // end format()


  /**
   * Formats an input string with numeric data according to the edit mask.  Formatting is
   * done from right to left.
   *
   * @param strData - A string with the numeric data to format
   *
   * @return - A string containing the data formatted according to the edit mask
   *
   * @exception throws VwIllegalCharException, VwNumericTruncationException, or
   * VwIllegalValueException if the input data violates the allowable characters in the
   * edit mask or filter.
   */
  private String doNbrFormat( String strData ) throws VwIllegalCharException,
                                                      VwNumericTruncationException,
                                                      VwIllegalValueException,
                                                      Exception
  {
    
    boolean fIsNegative = ( strData.indexOf( '-' ) >= 0 );

    strData =  VwExString.strip( strData, " $,+-" );

    String strDecimal = null;

    boolean fFloating$Sign = false;
    boolean fSuppressLeadingSpaces = false;
    
    // ***  if there is decimal data then the decimal portion is formatted left to right
    // *** zero padded

    int nDollarPos = m_strMask.indexOf( '$' );
    if ( nDollarPos >= 0 )
    {
      if ( m_strMask.charAt( nDollarPos + 1 ) == '$')
        fFloating$Sign = true;
      
       
    }
    
     
    int nMaskPos = m_strMask.indexOf( '.' );
    int nStart = strData.indexOf( '.' );

    if ( nMaskPos >= 0 )              // We have a decimal point
    {
      ++nMaskPos;                     // Bypass decimal point

      StringBuffer sb = new StringBuffer( 10 );

      if ( nStart < 0 )           // Mask had decimal places but actual data did not so
      {                           // append a zero for each decimal position defined in the mask
        // *** how many places were defined in the mask

        int nNbrDecPos = m_strMask.length() - nMaskPos;

        for ( int x = 0; x < nNbrDecPos; x++ )
          sb.append( '0' );  // append zeroes

      } // end if

      else                      // Data had decimal positions
      {
        ++nStart;
        int nDataLen = strData.length();
        int nMaskLen = m_strMask.length();

        while( nMaskPos < nMaskLen )
        {
          if ( nStart < nDataLen )
          {
            if ( m_strMask.charAt( nMaskPos ) == '#' )
            {
              if ( !Character.isDigit( strData.charAt( nStart ) ) )
                throw new VwIllegalCharException( m_msgBundle.getString( "VwEdit.NotDigit" ) );
              else
                sb.append( strData.charAt( nStart ) );
            }
            else
            {
              if ( m_strMask.charAt( nMaskPos ) == '$' && fFloating$Sign && m_strMask.charAt( nMaskPos + 1 ) != '$' )
                sb.append( m_strMask.charAt( nMaskPos ) );
            }
          }
          else
          {
            if ( m_strMask.charAt( nMaskPos ) == '#' )
              sb.append( '0' );            // Format string longer than data so append zeroes
            else
              sb.append( m_strMask.charAt( nMaskPos ) );  // Use the decimal data
          }

          ++nMaskPos;                        // Look at next characters
          ++nStart;

        } // end while

      } // end else

      strDecimal = sb.toString();

      nMaskPos = m_strMask.indexOf( '.' ) - 1;

    } // end if

    else
      nMaskPos = m_strMask.length() - 1;   // No decimal point in mask so start at last mask character

    // *** Next move right to left to format the base number

    int nDataPos = ( nStart < 0 )? strData.length() - 1 : strData.indexOf( '.' ) - 1;

    int nMaskLen = m_strMask.length();

    if ( m_strMask.charAt( 0 ) == m_chFillCharacter || m_strMask.charAt( 0 ) == '!')
      --nMaskLen;

    StringBuffer sbNumber = new StringBuffer( nMaskLen );

    for ( int x = 0; x < nMaskLen; x++ )
      sbNumber.append( '~'  );

    int nNbrPos = sbNumber.length() - 1;

    while( nMaskPos >= 0 )
    {
      // if data is less than the mask, we are all done

      if ( nDataPos < 0 )
        break;

      if ( nMaskPos == 0 && m_strMask.charAt( 0 ) == m_chFillCharacter )
        break;

      if ( m_strMask.charAt( nMaskPos ) == '#' )
      {
        if ( !Character.isDigit( strData.charAt( nDataPos ) ) )
          throw new VwIllegalCharException( m_msgBundle.getString( "VwEdit.NotDigit" ) );
        else
          sbNumber.setCharAt( nNbrPos, strData.charAt( nDataPos-- ) );

      } // end if

      else   // Delimiter character
      {
        if ( m_strMask.charAt( nMaskPos ) == '$' )
        {
          if ( fFloating$Sign &&  m_strMask.charAt( nMaskPos +1 ) != '$') 
            sbNumber.setCharAt( nNbrPos, m_strMask.charAt( nMaskPos ) );
        }
        else
          sbNumber.setCharAt( nNbrPos, m_strMask.charAt( nMaskPos ) );
      }
      --nMaskPos;
      --nNbrPos;

    } // end while()

    if ( nDataPos >= 0 )
      throw new
        VwNumericTruncationException( m_msgBundle.getString("VwEdit.SignificantTruncation" ));

    boolean fNeedPlus = false;
    boolean fNeedMinus = false;

    while( nMaskPos >= 0 )
    {
      char ch = m_strMask.charAt( nMaskPos );

      if ( nMaskPos == 0 && ch == m_chFillCharacter )
        break;        // All Done

      switch( ch )
      {
        case '+':

             if ( !fIsNegative )
               fNeedPlus = true;
             break;

        case '-':

             if ( fIsNegative )
               fNeedMinus = true;

             break;

        case '#':
        case ',':

             // *** Pad with fill character for unused position unless floating dollar sign is requested

             if ( !fFloating$Sign )
               sbNumber.setCharAt( nNbrPos--, m_chFillCharacter );
             break;

        case '$':
          
             if ( fFloating$Sign && sbNumber.charAt( nNbrPos + 1 ) == '$' )
               --nNbrPos;
             else
               sbNumber.setCharAt( nNbrPos--,ch );
             
             break;
               
        case '!':
          
             if ( nMaskPos > 0 )
               throw new Exception(  "Invalid mask, the '~' character must be the first character in the mask");
             
             fSuppressLeadingSpaces = true;
             break;
             
        default:

             
             sbNumber.setCharAt( nNbrPos--, ch );
             break;

      } // end switch()

      --nMaskPos;

    } // end while()

    if ( fNeedPlus || fNeedMinus )
    {
      int x;

      for ( x = 0; (sbNumber.charAt( x ) == '~' || sbNumber.charAt( x ) == ' '
                    || sbNumber.charAt( x ) == '$'  ); x++ );
      if ( fNeedPlus )
        sbNumber.setCharAt( x - 1, '+' );
      else
        sbNumber.setCharAt( x - 1, '-' );
    }


    if ( strDecimal != null )
      sbNumber.append( '.' ).append( strDecimal );

    String strResult = VwExString.strip( sbNumber.toString(), "~" );
    
    if ( fSuppressLeadingSpaces )
      strResult = strResult.trim();
    
    return strResult;

  } // end doNbrFormat()


  /**
   * Formats an input string with character data according to the edit mask.  Formatting is
   * done from right to left and padded with blanks.
   *
   * @param strData - A string with the data to format
   *
   * @return - A string containing the data formatted according to the edit mask
   *
   * @exception throws Exception if the input data violates the allowable characters in the
   * edit mask or filter.
   */
  private final String doCharacterFormat( String strData ) throws VwIllegalCharException,
                                                                  VwInvalidMaskException,
                                                                  VwIllegalValueException
  {
    int nMaskPos = -1;
    int nDataPos = 0;


    if ( m_strMask.length() == 1 && m_strMask.charAt( 0 ) == '*' )
      return strData;       // There is no format '*' allows any character and a single
                            // format charcater applies to the exact length of the input data


    StringBuffer sb = new StringBuffer();


    // *** We now have two genral format possibllites. if the format mask is only one character
    // *** we test the validity in the data only. If it is more than one character then a
    // *** combination mask and data scan is done with padding or truncation done if necessary

    if ( m_strMask.length() == 1 )
    {
      char chMask = m_strMask.charAt( 0 );

      while( nDataPos < strData.length() )
      {
        sb.append( testAndXlateChar( chMask, strData.charAt( nDataPos ) ) );
        ++nDataPos;

      } // end while

    } // end if

    else
    {

      while( ( ++nMaskPos < m_strMask.length() && nDataPos < strData.length() ))
      {
        char chMask = m_strMask.charAt( nMaskPos );
        char chData = strData.charAt( nDataPos );

        if ( m_strDelimList.indexOf( chMask ) >= 0 )
        {
          if ( chMask == chData )
          {
            sb.append( chMask );
            ++nDataPos;
          }
          else
            sb.append( chMask );
        }
        else
        if ( m_strMaskList.indexOf( chMask ) < 0 )
          sb.append( chMask );      // user defined format symbol just add it to the output
        else
        {
          sb.append( testAndXlateChar( chMask, chData ) );
          ++nDataPos;
        }

      } // end while()

      if ( nMaskPos < m_strMask.length() || nDataPos < strData.length() )
        throw new VwIllegalCharException( m_msgBundle.getString("VwEdit.DataMisMatch" ) );

      while( nMaskPos < m_strMask.length() )
      {
        sb.append( ' ' );
        ++nMaskPos;
      }

    } // end else

    return sb.toString();

  } //end doCharacterFormat()


  /**
   * Validates the given character according to the mask format character, and does a case
   * translation for the character, if necessary.
   *
   * @param chMask - The format mask character
   * @param chData - The data character to be validated and formatted
   *
   * return - The character formatted if the validation succeeds
   *
   * @exception throws VwIllegalCharException if the charater violates the edit mask specification
   */
  private char testAndXlateChar( char chMask, char chData ) throws VwIllegalCharException,
                                                                   VwInvalidMaskException
  {
    // *** If the character is in the apecial allowable filter list then it passes the test
    // *** so we just return back that same character

    if ( m_strFilter.indexOf( chData ) >= 0 )
      return chData;

    switch( chMask )
    {
      case '*':

           return chData;        // '*' = anything allowed

      case 'x':                  // Alpha numeric and convert to lower case

           if ( !Character.isLetterOrDigit( chData ) )
             throw new
                VwIllegalCharException( m_msgBundle.getString("VwEdit.NotAlphaNum" ) );

           // Convert character to lower case

           return Character.toLowerCase( chData );

      case 'X':                  // Alpha numeric and convert to UPPER case

            if ( !Character.isLetterOrDigit( chData ) )
              throw new
                  VwIllegalCharException( m_msgBundle.getString("VwEdit.NotAlphaNum" ) );

            // Convert character to lower case

            return Character.toUpperCase( chData );

      case 'c':                  // Alpha numeric any case

           if ( !Character.isLetterOrDigit( chData ) )
             throw new
                VwIllegalCharException( m_msgBundle.getString("VwEdit.NotAlphaNum" ) );

           // Convert character to lower case

           return chData;

      case 'l':                  // Alpha any case

            if ( !Character.isLetter( chData ) )
              throw new
                  VwIllegalCharException( m_msgBundle.getString("VwEdit.NotAlpha" ) );

            return chData;

       case 'a':                // Alpha only and convert to lower case

            if ( !Character.isLetter( chData ) )
              throw new
                VwIllegalCharException( m_msgBundle.getString("VwEdit.NotAlpha" ) );

            // Convert character to lower case

            return Character.toLowerCase( chData );

      case 'A':                // Alpha only and convert to UPPER case

           if ( !Character.isLetter( chData ) )
            throw new
              VwIllegalCharException( m_msgBundle.getString("VwEdit.NotAlpha" ) );

           // Convert character to lower case

           return  Character.toUpperCase( chData );

      case '9':                // Digit 0 - 9 only

           if ( !Character.isDigit( chData ) )
             throw new
               VwIllegalCharException( m_msgBundle.getString("VwEdit.NotDigit" ) );

           return chData;

      default:

           // *** This is a delimiter character

           return chMask;

    } // end switch()

  } // end testAndXlateChar()


  /**
   * Tests the given numeric format mask for validity.  The rules are:
   *   1. It may start with a fill character a space or zero or asterisk
   *      are the allowed fill characters or the $,+,- characters
   *   I.e The following format "0###" for the number 12 produces 012 as the result
   *
   *   2. It may contain only one decimal point
   *   3. It may contain commas after the start of a # character, but no other character
   *      other than one decimal point
   *
   * @param strMask - The edit mask to test
   *
   * @return - True if the numeric edit mask is valid; otherwise False is returned
   */
  private boolean isValidNumericMask( String strMask )
  {
    m_chFillCharacter = ' ';
    
    int nPos = strMask.indexOf( '#' );

    String strPrefix = "";

    if ( nPos > 0 )
      strPrefix = strMask.substring( 0, nPos );

    boolean fGotDollar = false;
    boolean fGotPlus = false;
    boolean fGotMinus = false;
    boolean fGotDecimal = false;
    boolean fGotFillChar = false;

    for ( int x = 0; x < strPrefix.length(); x++ )
    {
      switch( strPrefix.charAt( x ) )
      {
        case '$':

             if ( fGotDollar )
             {
               if ( x > 1 )
                 return false;      // no more that two allowed - two $$ means floating dollar sign
             }
             
             if ( x > 1  && !fGotFillChar )
               return false;      // Dollar sign can only be the first character

             fGotDollar = true;

             break;

        case '+':

             if ( fGotPlus )      // Only one allowed
               return false;

             fGotPlus = true;

             break;

        case '-':

             if ( fGotMinus )     // Only one allowed
               return false;

             fGotMinus = true;

             break;

        case ' ':
        case '0':
        case '*':

             if ( x > 0 )        // Fill character must be the first character
               return false;

             m_chFillCharacter = strPrefix.charAt( x );
             fGotFillChar = true;
             break;

        case '!':
        
             if ( x > 0 )
               return false;
             
             break;
             
        default:

             return false;        // Invalid character

      } // end swich()

    } // end for()

    // *** Prefix passed, now test rest of mask

    for ( int x = nPos; x < strMask.length(); x++ )
    {

      switch( strMask.charAt( x ) )
      {

        case '#':
             break;               // OK

        case ',':

             if ( strMask.charAt( x - 1 ) == ',' )
               return false;      // commas can only be three appart

             if ( ( x - 2 ) >= 0 )
             {
               if ( strMask.charAt( x - 2 ) == ',' )
                 return false;    // commas can only be three appart

             }
             break;

         case '.':

              if ( fGotDecimal )
                return false;   // Only one allowed

              fGotDecimal = true;

              break;

        default:

              return false;      // No othe characters allowed
      } //end switch()

    } // end for()

    m_fIsNumeric = true;         // This is a numeric mask

    if ( fGotDecimal )
      m_fIsDecimal = true;       // This mask also has decimal places

    return true;                 // Numeric edit mask is valid

  } // end isValidNumericMask()


  /**
   * Splits out the filter characters, delimited by the backslash, from the given edit mask.
   * The filter characters of the current edit object are initialized with the filter
   * characters split from the given edit mask.
   *
   * @param strMask - An edit mask which includes filter characters
   *
   * @return - A string containing the edit mask with the filter characters removed.
   * The filter characters of the current edit object are initialized with the filter
   * characters split from the strMask parameter.
   */
  private String splitMask( String strMask ) throws VwInvalidMaskException
  {
    int nPosBack1 = strMask.indexOf( '\t' );
    int nPosBack2 = 0;

    if ( nPosBack1 == 0 )
      nPosBack2 = strMask.indexOf( '\t', 1 );

    if ( nPosBack1 > 0 )
      throw new VwInvalidMaskException( m_msgBundle.getString( "VwEdit.InvalidMask" ) );

    if ( nPosBack1 == 0 && nPosBack2 < 0 )
      throw new VwInvalidMaskException( m_msgBundle.getString( "VwEdit.InvalidMask" ) );

    if ( nPosBack1 == 0 )
    {
      m_strFilter = strMask.substring( 1, nPosBack2 );
      strMask = strMask.substring( nPosBack2 + 1 );
    }

    return strMask;

  } // end splitMask()


  /**
   * Sets the fill character to be used on numeric masks 
   * @param chFilCharacter
   */
  public void setFillCharacter( char chFilCharacter )
  { m_chFillCharacter = chFilCharacter; }
  
  
  /**
   * Determines the characters allowed by the edit mask
   */
  private void determineAllowableCharacters()
  {
    // *** Determine allowable characters

    if ( m_fIsDate )
    {
      m_strAllowableChars= m_msgBundle.getString( "VwEdit.DateAllowed" );
      return;
    }

    // *** Numeric mask that is is for mathimatical operations

    if ( m_strMask.indexOf( '#' ) >= 0 )
    {
      m_strAllowableChars = m_msgBundle.getString( "VwEdit.DigitsAllowed" ) + ",+-";

      if (m_strMask.indexOf( '.' ) >= 0 )
        m_strAllowableChars += " " + m_msgBundle.getString( "VwEdit.And" )
                            + " " + m_msgBundle.getString( "VwEdit.DecimalPoint" );
      return;

    }

    // *** Numeric mask that is not used for mathimatical operations

    if (m_strMask.indexOf( '9' ) >= 0 )
    {
      m_strAllowableChars = m_msgBundle.getString( "VwEdit.DigitsAllowed" );

      String strDelimSet = getDelimiterSet();
      if ( strDelimSet != null )
        m_strAllowableChars += ", " + m_msgBundle.getString( "VwEdit.And" )
                            +  " " + m_msgBundle.getString( "VwEdit.Following" ) + strDelimSet;

      if ( m_strFilter.length() > 0 )
      {
        if ( strDelimSet == null )
        {

          m_strAllowableChars += ", " + m_msgBundle.getString( "VwEdit.And" )
                              + " " +  m_msgBundle.getString( "VwEdit.Following" );
        }

        m_strAllowableChars += m_strFilter;

      } // end if

      return;

    } // end if

    if ( VwExString.findAny( m_strMask, "Xxc", 0 ) >= 0 )
    {
      m_strAllowableChars = m_msgBundle.getString( "VwEdit.CharsAllowed" )
                          + ", " + m_msgBundle.getString( "VwEdit.DigitsAllowed" );
    }
    else
    if ( VwExString.findAny( m_strMask, "Aal", 0 ) >= 0 )
    {
      m_strAllowableChars= m_msgBundle.getString( "VwEdit.CharsAllowed" );
    }

    String strDelimSet = getDelimiterSet();

    if ( strDelimSet != null )
      m_strAllowableChars += ", " + m_msgBundle.getString( "VwEdit.VwEdit.And" )
                          + " " + m_msgBundle.getString( "VwEdit.Following" )
                          + strDelimSet;

    if ( m_strFilter.length() > 0 )
    {

      if ( strDelimSet == null )
      {
        m_strAllowableChars += ", " + m_msgBundle.getString( "VwEdit.And" )
                            + " " +  m_msgBundle.getString( "VwEdit.Following" );
      }

      m_strAllowableChars+= m_strFilter;
    }

  } // end determineAllowableCharacters()


  /**
   * Returns the delimiter set used in the current edit mask
   *
   * @return A string containing the delimiter set; null if no delimiters are found
   */
  private String getDelimiterSet()
  {
    int nLen = m_strMask.length();
    String strDelim = "";

    for ( int x = 0; x < nLen; x++ )
    {
      if ( VwExString.isin( m_strMask.charAt( x ), m_strDelimList ) )
        strDelim += m_strMask.charAt( x );
    }
    if ( strDelim.length() > 0 )
      return strDelim;

    return null;           // No delimiters found

  } // end getDelimiterSet()


  /**
   * Tests the data against the current values and ranges to insure the value and range
   * constraints are met.
   *
   * @param strData - A string with the data to test
   *
   * @exception throws VwIllegalValueException if the data violates the value or range constraints
   */
  private void testValuesRanges( String strData ) throws VwIllegalValueException
  {
    String strValue;             // Single value from list in delimited string

    if ( m_dlmsValues == null )
      return;

    if ( m_dlmsValues.toString().length() == 0 )
      return;                    // Nothing to test

    m_dlmsValues.reset();

    if ( isDate() )
    {
      VwDate itcTestDate = new VwDate( strData, m_strMask );

      VwDate itcDate = new VwDate();
      VwDate itcDateLo = new VwDate();
      VwDate itcDateHi = new VwDate();

      while( (strValue = m_dlmsValues.getNext()) != null )
      {
        // *** Test to see if this piece is a range

        int nPos = strValue.indexOf( m_chRangeSep );

        if ( nPos > 0 )
        {
          itcDateLo.setDate( strValue.substring( 0, nPos ), m_strMask );
          itcDateHi.setDate( strValue.substring( nPos + 1 ), m_strMask );

          if ( itcTestDate.gtEq( itcDateLo ) &&  itcTestDate.ltEq( itcDateHi ) )
            return;              // A constraint is satisfied and we're all done
        }
        else
        {
          itcDate.setDate( strValue, m_strMask );
          if ( itcTestDate.equals( itcDate ) )
            return;              // A constraint is satisfied and we're all done
        }

      } // end while()

      // *** If we get here the data did not meet the constraints so throw the exception

      throw new VwIllegalValueException( m_msgBundle.getString( "VwEdit.IllegalValue" )
                                        + " " + m_dlmsValues.toString() );

    } // end if ( isDate )

    if ( m_fIsNumeric )
    {
      double dblData = Double.valueOf( VwExString.strip( strData, "$%, " ) ).doubleValue();
      double dblVal;

      while( (strValue = m_dlmsValues.getNext()) != null )
      {
        // *** Test to see if this piece is a range

        int nPos = strValue.indexOf( m_chRangeSep );

        if ( nPos > 0 )
        {
          // *** Extract low and hi range values

          double dblLow = Double.valueOf( strValue.substring( 0, nPos ) ).doubleValue();
          double dblHi = Double.valueOf( strValue.substring( nPos + 1 ) ).doubleValue();

          if ( dblData >= dblLow && dblData <= dblHi )
            return;       // A constraint is satisfied and we're all done
         }
         else
         {
           dblVal = Double.valueOf( strValue ).doubleValue();
           if ( dblData == dblVal )
             return;      // A constraint is satisfied and we're all done
         }

      } // end while

    } // end if VwExString.isNumeric( strData ) )
    else
    {
      // *** Non numeric data test

      while( (strValue = m_dlmsValues.getNext()) != null )
      {

        // *** Test to see if this piece is a range

        int nPos = strValue.indexOf( m_chRangeSep );

        if ( nPos > 0 )
        {
          String strLow = strValue.substring( 0, nPos );
          String strHi =  strValue.substring( nPos + 1 );

          int nLow = strData.compareTo( strLow );
          int nHi = strData.compareTo( strHi );

          if ( nLow >= 0 && nHi <= 0 )
             return;      // A constraint is satisfied and we're all done
        }
        else
        {
          if ( m_fCaseCompare )
          {
            if ( strData.equals( strValue ) )
              return;     // A constraint is satisfied and we're all done
          }
          else
          {
            if ( strData.equalsIgnoreCase( strValue ) )
              return;     // A constraint is satisfied and we're all done
          } // end else

        } // end else

      } // end while

    } // end else

    // *** If we get here the data did not meet the constraints so throw the exception

    throw new VwIllegalValueException( m_msgBundle.getString( "VwEdit.IllegalValue" )
                                        + " " + m_dlmsValues.toString() );

  } // end testValuesRanges()


  /**
   * Tests the values and ranges data types against the edit mask to insure type compatability
   *
   * @exception throws Exception if the values or range data types conflict with the edit mask
   */
  private void validateValuesToMask() throws Exception
  {
    String strValue = null;

    if ( m_dlmsValues == null )
      return;

    while( ( strValue = m_dlmsValues.getNext() ) != null )
    {
      String strLo = null;
      String strHi = null;

      int npos = strValue.indexOf( m_chRangeSep );
      if ( npos >= 0 )
      {
        strLo = strValue.substring( 0, npos );
        strHi = strValue.substring( npos + 1);

      } // end if

      if ( isDate() )
      {
        if ( strLo != null )
        {
          VwDate dt = new VwDate( strLo, m_strMask );
          if ( !dt.isValid() )
            throw new Exception( m_msgBundle.getString( "VwEdit.InvalidValueDate" ) );

          dt.setDate( strHi, m_strMask );
          if ( !dt.isValid() )
            throw new Exception( m_msgBundle.getString( "VwEdit.InvalidValueDate" ) );
        }
        else
        {
          VwDate dt = new VwDate( strValue, m_strMask );
          if ( !dt.isValid() )
            throw new Exception( m_msgBundle.getString( "VwEdit.InvalidValueDate" ) );
        } // end else

      } // end if ( isDate )
      else
      if ( isNumeric() )
      {
        if ( strLo != null )
        {
          if ( !VwExString.isNumeric( strLo ) )
            throw new Exception( m_msgBundle.getString( "VwEdit.InvalidNumber" ) );

          if ( !VwExString.isNumeric( strHi ) )
            throw new Exception( m_msgBundle.getString( "VwEdit.InvalidNumber" ) );
        }
        else
        {
          if ( !VwExString.isNumeric( strValue ) )
            throw new Exception( m_msgBundle.getString( "VwEdit.InvalidNumber" ) );
        }
     } // end if ( isNumeric() )

    } // end while()

    m_dlmsValues.reset();

  } // end validateValuesToMask()


  /**
   * Determins the maximum allowed characters based on the edit mask. This overrides
   * the setMaxCharsAllowed method if the mask does not allow unlimited characters.
   * The internal property m_nMaxCahrs is adjusted
   *
   */
  private void getMaxCharsAllowedFromMask()
  {

    if ( m_strMask.indexOf( '#' ) >= 0 )
    {
      // This test is for the numeric format masks
      m_fMaskOverride = true; // Mask determins max length
      m_nMaxChars = 0;        // Override of any previous seting
      int nLen = m_strMask.length();
      for( int x = 0; x < nLen; x++ )
      {

        switch ( m_strMask.charAt( x ) )
        {

          case '+':
          case '-':
          case '.':
          case '#':

               ++m_nMaxChars;

               break;

        } // end switch()

      } //end for()

      return;

     } //end if ( strMask.indexOf( '#' ) >= 0 )

     // *** Test here is for character based masks. If o

     if ( m_strMask.length() > 1 )
     {
       m_fMaskOverride = true; // Mask determins max length

       m_nMaxChars = m_strMask.length();
     }
  } // end getMaxCharsAllowedFromMask()
} // end class VwEdit{}

// *** End if VwEdit.java ***
