/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwIniFile.java

============================================================================================
*/

package com.vozzware.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

/**
 * The <strong>VwIniFile</strong> class is used to encapsulate parameters used in
 * applications.  It simulates most of the functionality of <strong>.INI</strong>
 * file processing found in other environments by allowing parameters to be specified
 * in an ASCII file, using <strong>name/value</strong> pairs. It also supports multiple
 * keyed sets of parameters, typically referred to as <strong><em>application keys</em></strong>.
 * <p>
 * This class is <strong>not</strong> designed to be instantiated.  All methods are
 * declared static.  A user may call the <strong>loadParameters</strong> method to initialize
 * the class. Afterwards, calls can be made to the <strong><em>getValue()</em></strong> methods.
 * <p>
 * Although the standard Java Properties class supports functionality similar to this
 * class, the application key functionality is not part of the Java class.  Therefore,
 * Properties are utilized in this class, to contain the individual name/value pairs of
 * each application key set.
 * <p>
 * A default application key is pre-defined by this class, allowing the same
 * style input file the Properties class accepts to be used here.
 * <p>
 * The System.getProperties() method is used during the static initialization
 * of this class to preload the Java run time environment variables, including
 * any <strong>-D</strong> parameters passed in.  If security restrictions are in
 * place (e.g., for applets), this is obviously not done.  If access is allowed, the
 * static initialization also looks for two parameters which can be passed in
 * using the <strong>-D</strong> option of the Java run time environment.  These
 * parameters are defined as:
 * <p>
 * <blockquote><pre>
 * INIFILE - the default .INI file to preload.
 * INISECTION - the default application key to use.  By default, the global
 * section of an .INI file is used as the default application key.
 * </pre></blockquote>
 * <p>
 * Below is a sample file that can be input to this class.  Note that lines
 * beginning with ;'s are considered comments, as well as blank lines. All
 * other lines must have one of the two following formats:
 * <br>
 * <blockquote><pre>
 * [applicationKey]<br>
 *  or<br>
 * name=value<br>
 * </pre></blockquote>
 * <br>
 * <blockquote><pre>
 * ; This is a sample input file usable by the IniFile class.<br>
 *
 * ; these first few keys are global, outside any application
 * ; specific group of values.<br>
 *
 * myKey1=a string value
 * myKey2=111
 * myKey3=these keys are in the default application key set<br>
 *
 * ; this next set is an application specific group of key/values.<br>
 *
 * [production]
 * host=PRODA
 * timeout=5
 * maxusers=1000<br>
 *
 * [qualitycontrol]
 * host=QA
 * timeout=30
 * maxusers=2<br>
 *
 * ; end of file, note that all blank lines above are considered comments.
 * </pre></blockquote>
 */

public  class VwIniFile
{
  private char m_chCommentChar = '#';

  public VwIniFile( File fileIniFile ) throws Exception
  {

    this( getStream( fileIniFile ) );
  }

  /**
   * Constructor.
   */
  public VwIniFile( InputStream insIniFile ) throws Exception
  {
     loadIni( insIniFile );
  }

  private static InputStream getStream( File fileIni ) throws Exception
  {
    URL urlFile = new URL( "file://" + fileIni.getAbsolutePath() );
    return urlFile.openStream();

  }


  /**
   * A vector of Strings used to define the application keys encountered
   * in an INI file.
   */
  private Vector m_appKeys = new Vector( 50 );

  /**
   * A vector of Properties used to define the key/value pairs for any
   * particular application key set, including the default global set.
   */
  private Vector m_properties = new Vector( 50 );

  /**
   * The current default Properties object.  This is used when methods that
   * only require a key are used. Can be changed by calling setDefaultAppKey().
   */
  private Properties m_currentKeyProperties = null;

  /**
   * The current default application key. This key is what is used when
   * methods that only require a key are used. Can be changed by calling
   * setDefaultAppKey().
   *
    */
   private String m_currentAppKey = null;

  /**
   * The default application key. This key is used by setDefaultAppKey().
   * This value can only be set by passing it in on the Java run time command
   * line. If System.getProperties has an entry named "INISECTION", the value
   * assigned is used as the default application key. If this entry is not
   * found or is invalid, the global section is used instead as the default
   * application key.
   *
   */
  private String m_defaultAppKey = "default";

  /**
   * A list of strings that are compared to in the get?Boolean methods to
   * indicate a true condition.
   */
  private String[] m_trueStrings = new String[] { "true",
                                                         "on",
                                                         "yes",
                                                         "1" };

  /**
   * A list of strings that are compared to in the get?Boolean methods to
   * indicate a false condition.
   */
   private String[] m_falseStrings = new String[] { "false",
                                                           "off",
                                                           "no",
                                                           "0" };

  /**
   * When this class is loaded, this static initialization establishes a
   * stable state of existence. Since the entire class is static member data
   * and methods, we don't allow instantiation. This logic gets our state
   * stable so anything can be called anytime. Preloads the default .INI
   * file defined with a -DINIFILE=filename and sets the default application
   * key defined with a -DINISECTION=section.
   */
   private void loadIni( InputStream ins ) throws Exception
   {
     // *** we maintain a default app key that's empty (the global key)

     m_currentAppKey = new String( "" );
     m_appKeys.addElement( m_currentAppKey );

     // *** add the Properties associated with the default app key
     // *** we do this by 1st attempting to use System.getProperties()
     // *** if not allowed, we simply start w/an empty Properties object.

     try
     {
       m_currentKeyProperties = System.getProperties();
       m_properties.addElement( m_currentKeyProperties );

       // *** if we got here, we've successfully accessed the Properties
       // *** object from the System class, now go look for some defaults

       loadParameters( ins );

       // *** check for the default application key contained in the
       // *** property entry named INISECTION

     }
     catch ( SecurityException e )
     {
       m_currentKeyProperties = new Properties();
       m_properties.addElement( m_currentKeyProperties );
     }

   }


  /**
   * Examine the string passed in for values which can be interpreted as
   * being either true or false. A sensible set of values is defined as a
   * private string array, which we go through comparing against.
   *
   * @param string The string to compare against.
   *
   * @return True if the value decoded was found in the m_trueStrings array,
   * False when it was found in the m_falseStrings array or wasn't found at all.
   */
   private boolean decodeBoolean( String string )
   {
      if ( string == null ||
           string.length() == 0 )     // no string?
          return false;               // can't be true

      int index = 0;
      while ( index < m_trueStrings.length )
      {
          if ( string.equalsIgnoreCase( m_trueStrings[ index++ ] ))
              return true;
      }
      index = 0;
      while ( index < m_falseStrings.length )
      {
          if ( string.equalsIgnoreCase( m_falseStrings[ index++ ] ))
              return false;
      }
      return false;                   // none of the above, must be an error
   }

  /**
   * Extract a delimited string from the input stream.
   * @param strFile The byte array containing the input file
   * @param inPtr The current index into bInStream
   * @param sb A StringBuffer that is used to return the string
   * @param chDelim The character (byte) that delimits the string
   *
   * @return The index value of the next character to process.
   *
   * @exception  Exception for unhandled or <EM>should never</EM> happen
   * type exceptions, including StringIndexOutOfBoundsException. These are
   * only generated when the input file has invalid syntax (missing = on a
   * name/value pair, missing trailing ] on an application key definition, etc.).
   */
   private int extractString( String  strFile, int inPtr, StringBuffer sb, char chDelim )
   {
     int nBufferEnd = strFile.length();

     while ( inPtr < nBufferEnd && strFile.charAt( inPtr ) != chDelim )
     {
       if ( strFile.charAt( inPtr ) == m_chCommentChar )
       {
         return skipComments( strFile, ++inPtr );
       }

       if ( chDelim == '\n' && strFile.charAt( inPtr ) ==  '\r' )
       {
         break;              // don't do \r's when doing \n's
       }

       sb.append( (char) strFile.charAt( inPtr ) );
       inPtr++;

     }

     return inPtr + 1;               // adjust for ending delimiter
  }

  /**
   * Enumerates the Application Keys ( Those defined inside brackets I.E [MyAppKey] )
   *
   * @return - The application keys in the ini file in a string array, or null if none
   * are defined.
   */
   public String[] enumAppKeys()
   {
     if ( m_appKeys.size() == 0 )
     {
       return null;
     }

     // There is a default empty app key we don't want

     String[] astrKeys = new String[ m_appKeys.size() - 1 ];

     for ( int x = 1; x <= astrKeys.length; x++ )
     {
       astrKeys[ x - 1 ] = (String)m_appKeys.elementAt( x );
     }

     return astrKeys;

  } // end enumAppKeys()

  /**
   * Enumerates the entries in a specific AppKey
   *
   * @param strSectionKey The AppKey to enumerate
   *
   * @return a String array containing all entries in an Appkey, or null if
   * no entries are defined or the AppKey is invalid
   */
   public String[] enumAppKeyEntires( String strSectionKey )
   {
     String[] astrAppEntries = null;

     int ndx = m_appKeys.indexOf( strSectionKey );
     if ( ndx >= 0 )
     {
       Properties thisAppKey = ( Properties )m_properties.elementAt( ndx );
       Enumeration e = thisAppKey.propertyNames();

       VwDelimString dlmsEntries = new VwDelimString();

       while( e.hasMoreElements() )
       {
         dlmsEntries.add( (String)e.nextElement() );
       }

       astrAppEntries = dlmsEntries.toStringArray();

     } // end if

     return astrAppEntries;

   } // end enumAppKeyEntires()


  /**
   * Retrieve a key value as a string. This method can be used as a
   * convenient mechanism to get string values. It is up to the caller to
   * determine if the string was successfully found or not, perhaps using
   * the isKey() method(s) or checking the return value for null.
   *
   * @param strKey The name of the entry containing the string value.
   *
   * @return The string value or null if the key isn't found.
   */
  public String getValue( String strKey )
  {
    return m_currentKeyProperties.getProperty( strKey );
  }

  /**
   * Retrieve a key value as a string. This method can be used as a
   * convenient mechanism to get string values. It is up to the caller to
   * determine if the string was successfully found or not, perhaps using
   * the isKey() method(s) or checking the return value for null.
   *
   * @param strSectionKey The name of the application section the key is found in.
   * @param strKey The name of the entry containing the string value.
   * @return The string value or null if the key isn't found.
   */
  public String getValue( String strSectionKey, String strKey )
  {
    int ndx = m_appKeys.indexOf( strSectionKey );

    if ( ndx >= 0 )
    {
      Properties thisProperty = ( Properties )m_properties.elementAt( ndx );
      if ( thisProperty.containsKey( strKey ) )
      {
        return thisProperty.getProperty( strKey );
      }
    }

    return null;
  }

  /**
   * Retrieve a key value as a boolean. This method can be used as a
   * convenient mechanism to get boolean values. It is up to the caller to
   * determine if the key was successfully found or not, perhaps using the
   * isKey() method(s). The following string values are decoded to true/false
   * meanings:
   * <blockquote><pre>
   * <li>true false</li>
   * <li>on off</li>
   * <li>yes no</li>
   * <li>1 0</li>
   * </pre></blockquote>
   *
   * @param strKey The name of the entry containing the char value.
   *
   * @return The char value found or null if the key isn't found.
   */
  public boolean getValueAsBoolean( String strKey )
  {
    if ( m_currentKeyProperties.containsKey( strKey ) )
    {
      return decodeBoolean( m_currentKeyProperties.getProperty( strKey ) );
    }

    return false;                   // missing/invalid value is false
  }

  /**
   * Retrieve a key value as a boolean. This method can be used as a
   * convenient mechanism to get boolean values. It is up to the caller to
   * determine if the key was successfully found or not, perhaps using the
   * isKey() method(s). The following string values are decoded to true/false
   * meanings:
   * <blockquote><pre>
   * <li>true false</li>
   * <li>on off</li>
   * <li>yes no</li>
   * <li>1 0</li>
   * </pre></blockquote>
   *
   * @param strSectionKey The name of the application section the key is found in.
   * @param strKey The name of the entry containing the char value.
   *
   * @return The char value found or null if the key isn't found.
   */
  public boolean getValueAsBoolean( String strSectionKey, String strKey )
  {
    return decodeBoolean( getValue( strSectionKey, strKey ) );
  }

  /**
   * Retrieve a key value as a byte. This method can be used as a
   * convenient mechanism to get byte values. It is up to the caller to
   * determine if the key was successfully found or not, perhaps using the
   * isKey() method(s).<p>
   * <strong>Note: </strong>The <strong>decode</strong> method of the Byte
   * class is used to interpret the value, that is, a leading # or 0x denotes
   * a hex value, a leading 0 denotes octal, all other values are interpreted
   * as a decimal number, converting each format to a byte value.
   *
   * @param strKey The name of the entry containing the byte value.
   *
   * @return The byte value found or null if the key isn't found.
   * @see Byte
   */
  public byte getValueAsByte( String strKey )
  {
    if ( m_currentKeyProperties.containsKey( strKey ) )
    {
      String strVal = m_currentKeyProperties.getProperty( strKey );
      try
      {
        return Byte.decode( strVal ).byteValue();
      }
      catch ( NumberFormatException e )
      {
          ;                       // we'll return 0 below
      }
    }

    return 0;
  }

  /**
   * Retrieve a key value as a byte. This method can be used as a
   * convenient mechanism to get byte values. It is up to the caller to
   * determine if the key was successfully found or not, perhaps using the
   * isKey() method(s).<p>
   * <strong>Note: </strong>The <strong>decode</strong> method of the Byte
   * class is used to interpret the value, that is, a leading # or 0x denotes
   * a hex value, a leading 0 denotes octal, all other values are interpreted
   * as a decimal number, converting each format to a byte value.
   *
   * @param strSectionKey The name of the application section the key is found in.
   * @param strKey The name of the entry containing the byte value.
   *
   * @return The byte value found or null if the key isn't found.
   * @see Byte
   */
  public byte getValueAsByte( String strSectionKey, String strKey )
  {
    String strVal = getValue( strSectionKey, strKey );// first get the string value

    if ( strVal != null )                   // as long as it was found...
    {
      try
      {
        return Byte.decode( strVal ).byteValue();
      }
      catch ( NumberFormatException e )
      {
          ;                       // we'll return 0 below
      }
    }

    return 0;
  }

  /**
   * Retrieve a key value as a char. This method can be used as a
   * convenient mechanism to get char values. It is up to the caller to
   * determine if the key was successfully found or not, perhaps using the
   * isKey() method(s).
   *
   * @param strKey The name of the entry containing the char value.
   *
   * @return The char value found or null if the key isn't found.
   */
  public char getValueAsChar( String strKey )
  {
    if ( m_currentKeyProperties.containsKey( strKey ) )
    {
      String strVal = m_currentKeyProperties.getProperty( strKey );
      if ( strVal.length() > 0 )
      {
        return strVal.charAt( 0 );
      }
    }

    return 0;
  }

  /**
   * Retrieve a key value as a char. This method can be used as a
   * convenient mechanism to get char values. It is up to the caller to
   * determine if the key was successfully found or not, perhaps using the
   * isKey() method(s).
   *
   * @param strSectionKey The name of the application section the key is found in.
   * @param strKey The name of the entry containing the char value.
   *
   * @return The char value found or null if the key isn't found.
   */
  public char getValueAsChar( String strSectionKey, String strKey )
  {
    String strVal = getValue( strSectionKey, strKey );// first get the string value
    if ( strVal != null )                   // as long as it was found...
    {
      if ( strVal.length() > 0 )
      {
        return strVal.charAt( 0 );
      }
    }

    return 0;
  }

  /**
   * Retrieve a key value as a double. This method can be used as a
   * convenient mechanism to get double values. It is up to the caller to
   * determine if the key was successfully found or not, perhaps using the
   * isKey() method(s).
   *
   * @param strKey The name of the entry containing the double value.
   *
   * @return The char value found or null if the key isn't found.
   */
  public  double getValueAsDouble( String strKey )
  {
    if ( m_currentKeyProperties.containsKey( strKey ) )
    {
      String strVal = m_currentKeyProperties.getProperty( strKey );
      try
      {
          return Double.valueOf( strVal ).doubleValue();
      }
      catch ( NumberFormatException e )
      {
          ;                       // we'll return 0 below
      }
    }

    return 0.0;
  }

  /**
   * Retrieve a key value as a double. This method can be used as a
   * convenient mechanism to get double values. It is up to the caller to
   * determine if the key was successfully found or not, perhaps using the
   * isKey() method(s).
   *
   * @param strSectionKey The name of the application section the key is found in.
   * @param strKey The name of the entry containing the double value.
   *
   * @return The char value found or null if the key isn't found.
   */
  public double getValueAsDouble( String strSectionKey, String strKey )
  {
    String strVal = getValue( strSectionKey, strKey );// first get the string value

    if ( strVal != null )                   // as long as it was found...
    {
      try
      {
          return Double.valueOf( strVal ).doubleValue();
      }
      catch ( NumberFormatException e )
      {
          ;                       // we'll return 0 below
      }
    }

    return 0.0;
  }

  /**
   * Retrieve a key value as a float. This method can be used as a
   * convenient mechanism to get float values. It is up to the caller to
   * determine if the key was successfully found or not, perhaps using the
   * isKey() method(s).
   *
   * @param strKey The name of the entry containing the float value.
   *
   * @return The char value found or null if the key isn't found.
   */
  public float getValueAsFloat( String strKey )
  {
    if ( m_currentKeyProperties.containsKey( strKey ) )
    {
      String strVal = m_currentKeyProperties.getProperty( strKey );
      try
      {
        return Float.valueOf( strVal ).floatValue();
      }
      catch ( NumberFormatException e )
      {
          ;                       // we'll return 0 below
      }
    }

    return 0;
  }

  /**
   * Retrieve a key value as a float. This method can be used as a
   * convenient mechanism to get float values. It is up to the caller to
   * determine if the key was successfully found or not, perhaps using the
   * isKey() method(s).
   *
   * @param strSectionKey The name of the application section the key is found in.
   * @param strKey The name of the entry containing the float value.
   *
   * @return The char value found or null if the key isn't found.
   */
  public float getValueAsFloat( String strSectionKey, String strKey )
  {
    String strVal = getValue( strSectionKey, strKey );// first get the string value
    if ( strVal != null )                   // as long as it was found...
    {
      try
      {
          return Float.valueOf( strVal ).floatValue();
      }
      catch ( NumberFormatException e )
      {
          ;                       // we'll return 0 below
      }

    }

    return 0;
  }

  /**
   * Retrieve a key value as an int(eger). This method can be used as a
   * convenient mechanism to get integer values. It is up to the caller to
   * determine if the key was successfully found or not, perhaps using the
   * isKey() method(s).<p>
   * <strong>Note: </strong>The <strong>decode</strong> method of the Integer
   * class is used to interpret the value, that is, a leading # or 0x denotes
   * a hex value, a leading 0 denotes octal, all other values are interpreted
   * as a decimal number, converting each format to an int(eger) value.
   *
   * @param strKey The name of the entry containing the integer value.
   *
   * @return The char value found or null if the key isn't found.
   * @see Integer
   */
  public int getValueAsInteger( String strKey )
  {
    if ( m_currentKeyProperties.containsKey( strKey ) )
    {
      String strVal = m_currentKeyProperties.getProperty( strKey );
      try
      {
        return Integer.decode( strVal ).intValue();
      }
      catch ( NumberFormatException e )
      {
          ;                       // we'll return 0 below
      }
    }

    return 0;
  }

  /**
   * Retrieve a key value as an int(eger). This method can be used as a
   * convenient mechanism to get integer values. It is up to the caller to
   * determine if the key was successfully found or not, perhaps using the
   * isKey() method(s).<p>
   * <strong>Note: </strong>The <strong>decode</strong> method of the Integer
   * class is used to interpret the value, that is, a leading # or 0x denotes
   * a hex value, a leading 0 denotes octal, all other values are interpreted
   * as a decimal number, converting each format to an int(eger) value.
   *
   * @param strSectionKey The name of the application section the key is found in.
   * @param strKey The name of the entry containing the integer value.
   *
   * @return The char value found or null if the key isn't found.
   * @see Integer
   */
  public int getValueAsInteger( String strSectionKey, String strKey )
  {
      String strVal = getValue( strSectionKey, strKey );// first get the string value
      if ( strVal != null )                   // as long as it was found...
      {
        try
        {
            return Integer.decode( strVal ).intValue();
        }
        catch ( NumberFormatException e )
        {
            e.printStackTrace();    // we'll return 0 below
        }

      }

      return 0;
  }

  /**
   * Retrieve a key value as a long. This method can be used as a
   * convenient mechanism to get long values. It is up to the caller to
   * determine if the key was successfully found or not, perhaps using the
   * isKey() method(s).<p>
   * <strong>Note: </strong>The <strong>decode</strong> method of the Long
   * class is used to interpret the value, that is, a leading # or 0x denotes
   * a hex value, a leading 0 denotes octal, all other values are interpreted
   * as a decimal number, converting each format to a long value.
   *
   * @param strKey The name of the entry containing the long value.
   *
   * @return The char value found or null if the key isn't found.
   */
  public long getValueAsLong( String strKey )
  {
    if ( m_currentKeyProperties.containsKey( strKey ) )
    {
      String strVal = m_currentKeyProperties.getProperty( strKey );
      try
      {
        return Long.parseLong( strVal );
      }
      catch ( NumberFormatException e )
      {
        ;                       // we'll return 0 below
      }
    }

    return 0l;
  }

  /**
   * Retrieve a key value as a long. This method can be used as a
   * convenient mechanism to get long values. It is up to the caller to
   * determine if the key was successfully found or not, perhaps using the
   * isKey() method(s).<p>
   * <strong>Note: </strong>The <strong>decode</strong> method of the Long
   * class is used to interpret the value, that is, a leading # or 0x denotes
   * a hex value, a leading 0 denotes octal, all other values are interpreted
   * as a decimal number, converting each format to a long value.
   *
   * @param strSectionKey The name of the application section the key is found in.
   * @param strKey The name of the entry containing the long value.
   *
   * @return The char value found or null if the key isn't found.
   */
  public long getValueAsLong( String strSectionKey, String strKey )
  {
    String strVal = getValue( strSectionKey, strKey );// first get the string value
    if ( strVal != null )                   // as long as it was found...
    {
      try
      {
          return Long.parseLong( strVal );
      }
      catch ( NumberFormatException e )
      {
          ;                       // we'll return 0 below
      }

    }

    return 0l;
  }

  /**
   * Retrieve a key value as a short. This method can be used as a
   * convenient mechanism to get short values. It is up to the caller to
   * determine if the key was successfully found or not, perhaps using the
   * isKey() method(s).<p>
   * <strong>Note: </strong>The <strong>decode</strong> method of the Short
   * class is used to interpret the value, that is, a leading # or 0x denotes
   * a hex value, a leading 0 denotes octal, all other values are interpreted
   * as a decimal number, converting each format to a short value.
   *
   * @param strKey The name of the entry containing the short value.
   *
   * @return The char value found or null if the key isn't found.
   * @see Short
   */
  public short getValueAsShort( String strKey )
  {
    if ( m_currentKeyProperties.containsKey( strKey ) )
    {
      String strVal = m_currentKeyProperties.getProperty( strKey );
      try
      {
        return Short.decode( strVal ).shortValue();
      }
      catch ( NumberFormatException e )
      {
          ;                       // we'll return 0 below
      }
    }

    return 0;
  }

  /**
   * Retrieve a key value as a short. This method can be used as a
   * convenient mechanism to get short values. It is up to the caller to
   * determine if the key was successfully found or not, perhaps using the
   * isKey() method(s).<p>
   * <strong>Note: </strong>The <strong>decode</strong> method of the Short
   * class is used to interpret the value, that is, a leading # or 0x denotes
   * a hex value, a leading 0 denotes octal, all other values are interpreted
   * as a decimal number, converting each format to a short value.
   *
   * @param strSectionKey The name of the application section the key is found in.
   * @param strKey The name of the entry containing the short value.
   *
   * @return the char value found or null if the key isn't found.
   * @see Short
   */
  public short getValueAsShort( String strSectionKey, String strKey )
  {
    String strVal = getValue( strSectionKey, strKey );// first get the string value
    if ( strVal != null )                   // as long as it was found...
    {
      try
      {
          return Short.decode( strVal ).shortValue();
      }
      catch ( NumberFormatException e )
      {
          ;                       // we'll return 0 below
      }

    }

    return 0;
  }

  /**
   * Test if the specified key exists in the default application key space.
   *
   * @param strKey The name of the entry containing the short value.
   *
   * @return true if the key exists, false if not.
   */
  public boolean isKey( String strKey )
  {
    if ( m_currentKeyProperties.containsKey( strKey ) )
    {
      return true;
    }

    return false;
  }

  /**
   * Test if the specified key exists in a specific application key space.
   *
   * @param strSectionKey The application key to lookup.
   * @param strKey The data value key within appKey to lookup.
   *
   * @return True if the key exists, False if not.
   */
  public boolean isKey( String strSectionKey, String strKey )
  {
    int ndx = m_appKeys.indexOf( strSectionKey );
    if ( ndx >= 0 )
    {
      Properties thisProperty = ( Properties )m_properties.elementAt( ndx );
      if ( thisProperty.containsKey( strKey ) )
      {
        return true;
      }

    }

    return false;
  }

  /**
   * Loads the input file given, reading the name/value pairs. This method is
   * typically called once by an application, during it's initialization logic.
   * Afterwards, calls are made to the getValue? methods to obtain the values
   * read in by this method.<br>
   *
   * Subsequent calls to this method are allowed, with the caveat that a
   * duplicate name/value pair in the same application key will overlay the
   * older value.
   *
   * @param insIniFile The input stream of the ini file to loaded
   *
   * @exception  FileNotFoundException if the input file cannot be processed.
   * @exception  Exception for unhandled or <EM>should never</EM> happen
   * type exceptions, including StringIndexOutOfBoundsException. These are
   * only generated when the input file has invalid syntax (missing = on a
   * name/value pair, missing trailing ] on an application key definition, etc.).
   */
  public void loadParameters( InputStream  insIniFile ) throws Exception
  {
    setDefaultAppKey();             // make sure the global set is our default

    // *** creating a File object requires the path/name to be separated...

    String  strFile = null;
    try
    {
      // *** Create file object to get the file size

      // *** Read file name into byte array for parser

      strFile = VwFileUtil.readFile( insIniFile );

      if ( strFile == null  )
      {
        return;
      }

      int inPtr = 0;              // setup index to buffer

      // *** now process the buffer, creating app keys and values for
      // *** the contents of the file

      while ( inPtr < strFile.length()  )
      {
        if ( strFile.charAt( inPtr ) == '\n' ||  strFile.charAt( inPtr ) == (byte) '\r' )
        {
          inPtr = inPtr + 1;
          continue;           // empty line?
        }

        if ( strFile.charAt( inPtr ) == m_chCommentChar )
        {
          inPtr = skipComments( strFile, inPtr );
          continue;
        }

        if ( strFile.charAt( inPtr ) == '[' )
        {
          inPtr = processAppKey( strFile, ++inPtr );
          continue;
        }

        // *** no special condition, must be a name/value pair
        // *** note that if we haven't processed any [appkey] type
        // *** entries yet, entries found will become part of the
        // *** "default" key (the global set).

        inPtr = processKeyValue( strFile, inPtr );

      } // end while
    }
    finally
    {
      insIniFile.close();
      insIniFile = null;
      setDefaultAppKey();         // leave the default key selected

    }
  }

  /**
   * Process a new application key, detected by encountering a [ in the
   * input stream. The application key is all text within the closing []'s.
   *
   * @param strFile The byte array containing the input file
   * @param inPtr The current index into bInStream
   *
   * @return The index value of next character to process.
   *
   * @exception Exception for unhandled or <EM>should never</EM> happen
   * type exceptions, including StringIndexOutOfBoundsException. These are
   * only generated when the input file has invalid syntax (missing = on a
   * name/value pair, missing trailing ] on an application key definition, etc.).
   */
  private int processAppKey( String strFile, int inPtr )  throws Exception
  {
    StringBuffer sbAppKey = new StringBuffer();

    inPtr = extractString( strFile, inPtr, sbAppKey, ']' );

    // see if we already have this app key
    int index = m_appKeys.indexOf( sbAppKey );

    if ( index >= 0 )
    {                               // make this our current Properties set
      m_currentKeyProperties = ( Properties )m_properties.elementAt( index );
      m_currentAppKey   = ( String )m_appKeys.elementAt( 0 );
    }
    else
    {                               // we need another Properties/key added
      m_currentKeyProperties = new Properties();
      m_properties.addElement( m_currentKeyProperties );
      m_currentAppKey = new String( sbAppKey );
      m_appKeys.addElement( m_currentAppKey );
    }

    return inPtr;                   // new index value to process against
  }

  /**
   * Process comments in the input stream, simply by ignoring everything
   * till the end-of-line.
   *
   * @param strIniFile The byte array containing the input file
   * @param inPtr The current index into bInStream
   *
   * @return The index value of next character to process.
   *
   * @exception Exception for unhandled or <EM>should never</EM> happen
   * type exceptions, including StringIndexOutOfBoundsException. These are
   * only generated when the input file has invalid syntax (missing = on a
   * name/value pair, missing trailing ] on an application key definition, etc.).
   */
  private int processKeyValue( String  strIniFile, int inPtr )
          throws Exception
  {
    StringBuffer key = new StringBuffer();

    inPtr = extractString( strIniFile, inPtr, key, '=' );

    StringBuffer value = new StringBuffer();

    inPtr = extractString( strIniFile, inPtr, value, '\n' );

    String sKey   = new String( key );
    sKey = sKey.trim();

    if ( sKey.length() == 0 )
    {
      return inPtr;
    }

    String sValue = new String( value );
    m_currentKeyProperties.put( sKey, sValue.trim() );

    return inPtr;
  }

  /**
   * Set the current default application key to the global section of the INI
   * file or the section named by the System.getProperties entry named
   * INISECTION.
   */
  public void setDefaultAppKey()
  {
    if ( !setDefaultAppKey( m_defaultAppKey ) ) // can't set this value?
    {                                           // use the defaults then
      m_currentAppKey   = ( String )m_appKeys.elementAt( 0 );
      m_currentKeyProperties = ( Properties )m_properties.elementAt( 0 );
    }
  }

  /**
   * Set the current default application key to the global section of the INI
   * file processed. That is, the section which has no application key defined.
   *
   * @param appKey The application key to use as the current default.
   *
   * @return True if the key was set, False if the key is not found.
   */
  public boolean setDefaultAppKey( String appKey )
  {
    if ( appKey != null )
    {
      int ndx = m_appKeys.indexOf( appKey );
      if ( ndx >= 0 )
      {
        m_currentAppKey   = ( String )m_appKeys.elementAt( ndx );
        m_currentKeyProperties = ( Properties )m_properties.elementAt( ndx );
        return true;            // we succeeded
      }
    }

    return false;
  }

  /**
   * Process comments in the input stream, simply by ignoring everything
   * till the end-of-line.
   *
   * @param  strFile The byte array containing the input file
   * @param inPtr The current index into bInStream
   *
   * @return The index value of the next character to process.
   */
  private int skipComments( String strFile, int inPtr )
  {
    int nBufferEnd = strFile.length();

    while ( inPtr < nBufferEnd && strFile.charAt( inPtr ) != '\n' )
    {
      inPtr++;
    }

    return inPtr;
  }

} // end class VwIniFile{}

// *** End of VwIniFile.java ***

