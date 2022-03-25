/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwDelimString.java

============================================================================================
*/


package com.vozzware.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

/**
 * Provides a delimited string with one or more tokens as delimiters.
 * If no delimiters are specified, a comma delimited string is created
 * (default constructor).
 *
 * This class adds additional functionality to the java String class
 * to retrieve substrings based upon token delimiters.  E.g., "boats,cars,
 * planes,bikes" is a comma delimited string.  Multiple tokens may be specified
 * as delimiters.  The getDelimString() method returns a substring when one of
 * the delimiter tokens is found.
 *
 * @version 1.0
 *
 * @since 1.1
 */

public class VwDelimString extends Object
{

  private char              m_chConcatDelim = ',';   // The comma is default delimiter for concatenation

  private String            m_strDelimiterList;      // String of delimiters

  private StringBuffer      m_sbContents = new StringBuffer();      // String with its token delimiters

  private StringTokenizer   m_st;                     // Tokenizer for this instance

  private boolean           m_fChanged;

  private boolean           m_fTrimWhiteSpace = true;

  class DlmsIterator implements Iterator<String>
  {

    DlmsIterator()
    {
      init();
    }

    public boolean hasNext()
    {
      return m_st.hasMoreElements();
    }

    public String next()
    {
      return getNext();
    }

    public void remove()
    {
      return;
    }

  } // end class DlmsIterator{}

  /**
   * Common initialization
   */
  private void init()
  {
    m_st = null;
    m_st = new StringTokenizer( m_sbContents.toString(), m_strDelimiterList );
    m_fChanged = false;
  }


  /**
   *  Constructs a delimited string using the comma as the default delimiter
   */
  public VwDelimString()
  {
    m_strDelimiterList = ",";
    init();
  }


  /**
   *  Constructs a delimited string using the delimiter list and initialization string
   *  supplied in the constructor arguments.
   *
   *  Usage: new VwDelimString( ", \t", "cars,boats,trains" )
   *  Where: ", \t" is the token delimiter list used to parse the string
   *         "cars,boats,trains" is the initial contents of the string

   * @param strDelimiterList The set of delimiter tokens to use
   * @param strInitString  The initial string contents
   *
   */
  public VwDelimString( String strDelimiterList, String strInitString )
  {
    m_strDelimiterList = strDelimiterList;
    m_sbContents = new StringBuffer( strInitString );

    init();

    m_chConcatDelim = m_strDelimiterList.charAt( 0 );

  } // end VwDelimString()



  /**
   * Constructor
   * Constructs the delimited string object from a comma delimited string
   * @param strCommaDelimitedString The comma delimited string
   */
  public VwDelimString( String strCommaDelimitedString )
  {
    this( ",", strCommaDelimitedString );
  }

  /**
   *  Constructs a delimited string from an array of String objects using the
   *  delimiter supplied in the constructor argument..
   *
   *  Usage: new VwDelimString( "," astrStrings  )
   *  Where: "," is the comma token delimiter used to build the delimited string
   *         astrString is String array which will be converted to a comma
   *         delimited string

   * @param strDelimiter The delimiter token to use
   * @param astrStrings  The String array to convert
   *
   */
  public VwDelimString( String strDelimiter, String[] astrStrings )
  {

    m_strDelimiterList = strDelimiter;

    init();

    m_chConcatDelim = m_strDelimiterList.charAt( 0 );

    if ( astrStrings == null )
    {
      return;       // Prevenmt us from blowing up
    }

    for ( int x = 0; x < astrStrings.length; x++ )
    {
      add( astrStrings[ x ] );
    }

  } // end VwDelimString()


  /**
    *  Constructs a delimited string from a List of String objects using the
    *  delimiter supplied in the constructor argument..
    *
    *  Usage: new VwDelimString( "," listStrings  )
    *  Where: "," is the comma token delimiter used to build the delimited string
    *         listStrings is List<String> which will be converted to a comma
    *         delimited string

    * @param strDelimiter The delimiter token to use
    * @param listStrings  The List of Strings
    *
    */
   public VwDelimString( String strDelimiter, List<String> listStrings )
   {

     m_strDelimiterList = strDelimiter;
     init();

     m_chConcatDelim = m_strDelimiterList.charAt( 0 );

     if ( listStrings == null || listStrings.size() == 0  )
     {
       return;       // Prevenmt us from blowing up
     }

     for ( String strPiece : listStrings )
     {
       add( strPiece );
     }

   } // end VwDelimString()


  /**
   *  Constructs a delimited string from an array of shorts using the
   *  delimiter supplied in the constructor argument..
   *
   *  Usage: new VwDelimString( "," asShorts  )
   *  Where: "," is the comma token delimiter used to build the delimited string
   *         anShorts is an short array which will be converted to a comma
   *         delimited string
   *
   * @param strDelimiter The delimiter token to use
   * @param asShorts  The integer array to convert
   *
   */
  public VwDelimString( String strDelimiter, short[] asShorts )
  {

    m_strDelimiterList = strDelimiter;
    init();

    m_chConcatDelim = m_strDelimiterList.charAt( 0 );

    if ( asShorts == null )
    {
      return;       // Prevenmt us from blowing up
    }

    for ( int x = 0; x < asShorts.length; x++ )
    {
      add( String.valueOf( asShorts[ x ] ) );
    }

  } // end VwDelimString()




  /**
   *  Constructs a delimited string from an array of integers using the
   *  delimiter supplied in the constructor argument..
   *
   *  Usage: new VwDelimString( "," anIntegers  )
   *  Where: "," is the comma token delimiter used to build the delimited string
   *         anIntegers is an integer array which will be converted to a comma
   *         delimited string
   *
   * @param strDelimiter The delimiter token to use
   * @param anIntegers - The integer array to convert
   *
   */
  public VwDelimString( String strDelimiter, int[] anIntegers )
  {

    m_strDelimiterList = strDelimiter;
    init();

    m_chConcatDelim = m_strDelimiterList.charAt( 0 );

    if ( anIntegers == null )
    {
      return;       // Prevenmt us from blowing up
    }

    for ( int x = 0; x < anIntegers.length; x++ )
    {
      add( String.valueOf( anIntegers[ x ] ) );
    }

  } // end VwDelimString()


  /**
   *  Constructs a delimited string from an array of longs using the
   *  delimiter supplied in the constructor argument..
   *
   *  Usage: new VwDelimString( "," alLongs )
   *  Where: "," is the comma token delimiter used to build the delimited string
   *         anIntegers is an integer array which will be converted to a comma
   *         delimited string
   *
   * @param strDelimiter The delimiter token to use
   * @param alLongs - The long array to convert
   *
   */
  public VwDelimString( String strDelimiter, long[] alLongs )
  {

    m_strDelimiterList = strDelimiter;
    init();

    m_chConcatDelim = m_strDelimiterList.charAt( 0 );

    if ( alLongs == null )
    {
      return;       // Prevenmt us from blowing up
    }

    for ( int x = 0; x < alLongs.length; x++ )
    {
      add( String.valueOf( alLongs[ x ] ) );
    }

  } // end VwDelimString()



  /**
   *  Constructs a delimited string from an array of doubles using the
   *  delimiter supplied in the constructor argument..
   *
   *  Usage: new VwDelimString( "," anDoubles  )
   *  Where: "," is the comma token delimiter used to build the delimited string
   *         anDoubles is an double array which will be converted to a comma
   *         delimited string
   *
   * @param strDelimiter The delimiter token to use
   * @param anDoubles  The integer array to convert
   *
   */
  public VwDelimString( String strDelimiter, double[] anDoubles )
  {

    m_strDelimiterList = strDelimiter;
    init();

    m_chConcatDelim = m_strDelimiterList.charAt( 0 );

    if ( anDoubles == null )
    {
      return;       // Prevenmt us from blowing up
    }

    for ( int x = 0; x < anDoubles.length; x++ )
    {
      add( String.valueOf( anDoubles[ x ] ) );
    }

  } // end VwDelimString()




  /**
   *  Constructs a delimited string from another delimited string instance
   *
   * @param delimString - The VwDelimString instance to make a copy of
   *
   */
  public VwDelimString( VwDelimString delimString )
  { m_strDelimiterList = new String( delimString.m_strDelimiterList );
    m_sbContents = new StringBuffer( delimString.m_sbContents );
    m_chConcatDelim = m_strDelimiterList.charAt( 0 );

    init();

  } // end VwDelimString()

  /**
   * Deteremins the string is in the delimited string list
   *
   * @param strStringTest The string to test
   *
   * @return true if the the string to test is in the delimited string list
   */
  public boolean isIn( String strStringTest )
  {
    return m_sbContents.indexOf( strStringTest ) >= 0;
  }

  /**
   * Returns the trim whitespace of each string piece - this is the default
   * @return
   */
  public boolean isTrimWhitePpace()
  { return m_fTrimWhiteSpace; }


  /**
   * Sets the trim whitespace flag. The defualt is to trim any white space when each piece is retrieved
   * @param fTrimWhiteSpace
   */
  public void setTrimWhiteSpace( boolean fTrimWhiteSpace )
  { m_fTrimWhiteSpace = fTrimWhiteSpace; }

  /**
   * Returns the current content string (i.e., the string to be parsed) of the
   * delimited string.
   */
  public String toString()
  { return m_sbContents.toString(); }


  /**
   * Converts the delimited string to a string array of the substring tokens
   *
   * @return - An array of strings with the substring tokens of the delimited string
   *
   */
  public final String[] toStringArray()
  {
    reset();

    String[] astr = new String[ count() ];

    for ( int x = 0; x < astr.length; x++ )
      astr[ x ] = getNext().trim();

    reset();

    return astr;

  } // end toStringArray()


  /**
   * Converts the delimited string to a string array of the substring tokens
   *
   * @return - An array of Objects  with the substring tokens of the delimited string
   *
   */
  public final Object[] toObjectArray()
  {
    reset();

    Object[] aobj = new Object[ count() ];

    for ( int x = 0; x < aobj.length; x++ )
      aobj[ x ] = getNext().trim();

    reset();

    return aobj;

  } // end toStringArray()


  /**
   * Return delimited string as a List of String objects
   * @return
   */
  public final List<String> toStringList()
  {
    reset();

    int nSize = count();

    ArrayList<String> list = new ArrayList<String>( nSize );
    for ( int x = 0; x < nSize; x++ )
      list.add( getNext() );

    return list;

  } // end toStringList()

  /**
   * Returns a Map where each string is put in the map as both the key and the value
   * 
   * @return
   */
  public final Map<String,String> toMap()
  {  return toMap( false, null  );  }

  /**
   * Returns a Map where each string is put in the map as both the key and the value
   * 
   * @return
   */
  public final Map<String,String> toMap( String strKeyValueDelim )
  {  return toMap( false, strKeyValueDelim  );  }

  /**
   * Returns a Map where each string is put in the map as both the key and the value
   * 
   * @return
   */
  public final Map<String,String> toMap( boolean fUseLowerCase )
  {  return toMap( fUseLowerCase, null  );  }

  /**
   * Returns a Map where each string is put in the map as both the key and the value
   * @param fUseLowerCase if true put map key as lower case
   * 
   * @return a Map where each delimited sting value is put as the key and value
   */
  public final Map<String,String> toMap( boolean fUseLowerCase, String strKeyValueDelim )
  {
    Map<String,String> map = new HashMap<String,String>();
    
    String strItem = null;
    
    while( (strItem = getNext()) != null )
    {
      String strKey = null;
      String strVal = null;
      
      if ( strKeyValueDelim != null )
      {
        VwDelimString dlm = new VwDelimString( strKeyValueDelim, strItem );
        String[] astrTokens = dlm.toStringArray();
        strKey = astrTokens[ 0 ];
        strVal = astrTokens[ 1 ];
        
      }
      else
        strKey = strVal = strItem;
      
      if ( fUseLowerCase )
        strKey = strKey.toLowerCase();
      
      map.put( strKey, strVal );
    }
    
    return map;
    
  } // end to map
  
  
  /**
   * Converts the delimited string to a array of the substring tokens as shorts
   *
   * @return - A array of shorts representing the numeric values of the delimited string tokens
   *
   * @exception throws Exception if any of the substring tokens cannot be converted to a short
   *
   */
  public final short[] toShortArray()
  {
    reset();

    short[] ashorts = new short[ count() ];

    for ( int x = 0; x < ashorts.length; x++ )
      ashorts[ x ] = Short.parseShort( getNext().trim() );

    reset();

    return ashorts;

  } // end toShortArray()


  /**
   * Converts the delimited string to an array of the substring tokens as ints
   *
   * @return - An array of ints representing the numeric values of the delimited string tokens
   *
   * @exception throws Exception if any of the substring tokens cannot be converted to an integer
   *
   */
  public final int[] toIntArray()
  {
    reset();

    int[] aInts = new int[ count() ];

    for ( int x = 0; x < aInts.length; x++ )
      aInts[ x ] = Integer.parseInt( getNext().trim() );

    reset();

    return aInts;

  } // end toIntArray()


  /**
   * Converts the delimited string to an array of the substring tokens as longs
   *
   * @return - An array of longs representing the numeric values of the delimited string tokens
   *
   * @exception throws Exception if any of the substring tokens cannot be converted to an long
   *
   */
  public final long[] toLongArray()
  {
    reset();

    long[] aLongs = new long[ count() ];

    for ( int x = 0; x < aLongs.length; x++ )
      aLongs[ x ] = Long.parseLong( getNext().trim() );

    reset();

    return aLongs;

  } // end toLongArray()


  /**
   * Converts the delimited string to an array of the substring tokens as doubles
   *
   * @return - A array of doubles representing the numeric values of the delimited string tokens
   *
   * @exception throws Exception if any of the substring tokens cannot be converted to a double
   *
   */
  public final double[] toDoubleArray()
  {
    reset();

    double[] aDoubles = new double[ count() ];

    for ( int x = 0; x < aDoubles.length; x++ )
      aDoubles[ x ] = Double.valueOf( getNext().trim() ).doubleValue();

    reset();

    return aDoubles;

  } // end toDoubleArray()


  /**
   * Initialiazes the content string (i.e., the string to be parsed) of the
   * delimited string with the given string.
   *
   * @param strContents - A string with the new content to be parsed
   *
   */
  public void setContents( String strContents )
  { 
    m_sbContents = new StringBuffer( strContents );
    init();
  }


  /**
   * Assigns the values of another VwDelimString to the current delimited string
   *
   * @param delimString - The delimited string object to be assigned to the current object
   */
  public void setDelimString( VwDelimString delimString )
  { m_strDelimiterList = delimString.m_strDelimiterList;
    m_sbContents = delimString.m_sbContents;
    m_chConcatDelim = m_strDelimiterList.charAt( 0 );
    init();
  }


  /**
   * Initializes the token delimiter list with the given list
   *
   * @param strnewDelimList - A string with the new token list
   */
  public void setDelimList( String strNewDelimList )
  {
    m_strDelimiterList = strNewDelimList;
    m_chConcatDelim = m_strDelimiterList.charAt( 0 );
    init();
  }

  /**
  * Counts the number of substring tokens contained in the delimited string
  *
  * @return An integer with the number of substring tokens in the delimited string
  */

  public int count()
  {
    if ( m_fChanged )
      init();

    return m_st.countTokens();

  } // end count()

  /**
   * Returns true if there are more elements to be returned in the delimited string
   */
  public boolean hasMoreElements()
  {
    if ( m_fChanged )
    {
      init();
    }

    return m_st.hasMoreTokens();

  } // end hasMoreElements()


  /**
   * Gets the next substring from the delimited string, or null if there are no more substrings to parse
   *
   * @return - A string with the next substring of the delimited string, or null if all the substrings
   * have been parsed.
   */
  public String getNext()
  {
    if ( m_fChanged )
    {
      init();
    }

    String strPiece = null;

    try
    {
      strPiece = m_st.nextToken();
    }
    catch( Exception e )
    {
      return null;
    }

    return strPiece.trim();

  } // end getNext()


  /**
   * Resets the content string pointer to the beginning of the string, allowing
   * the string to be re-parsed using getNext().
   */
  public void reset()
  { init(); }


  /**
   * Replaces all occurences of a given substring in the delimited string with a new substring
   *
   * @param strOrig - The substring to search for
   * @param strNew - The new substring used to replace the original substring
   *
   * @return A new string with all replacements made, or a copy of the original string
   * if strOrig is not found.
   */

  public VwDelimString replace( String strOrig,  String strNew )
  {
    String strContents = VwExString.replace( m_sbContents.toString(), strOrig, strNew );
    return new VwDelimString( m_strDelimiterList, strContents  );
  }


  /**
   * Forces the given delimiter to be used for concatenation, if more
   * than one delimiter was previously specified.  The default is the first
   * delimiter in the delimiter list.  If not in the delimiter list, the given
   * delimiter is added.
   *
   * @param chDelim - The new delimiter used for concatenation
   */
  public void useThisDelimiter( char chDelim )
  {
    m_chConcatDelim = chDelim;
    if ( m_strDelimiterList.indexOf( chDelim ) < 0 )
     m_strDelimiterList += chDelim;
    init();
  }


  /**
   * Adds the given substring to the delimited string, using the default delimiter
   * or the delimiter specified by useThisDelimiter().  This method allows a delimited
   * string to be assembled by successive add() calls.
   *
   * @param strSub - The substring to concatenate to the delimited string
   */
  public void add( String strSub )
  {
    m_fChanged = true;      // String has changed

    if ( m_sbContents.length() == 0 )
      m_sbContents.append(  strSub );
    else
    {
      if ( strSub.startsWith( "\n" ))       // don't add delimiter if starting a new line
        m_sbContents.append( strSub );
      else
        m_sbContents.append( m_chConcatDelim ).append( strSub );
    }
   }  // end add()


  /**
   * Adds the given string to the front of the delimited string, followed by
   * the default delimiter or the delimiter specified by useThisDelimiter(),
   * so that the prefix is preserved as a substring token.
   *
   * @param strPrefix - The string to add at the front of the delimited string
   */
  public void addAtFront( String strPrefix )
  {
    m_fChanged = true;      // String has changed

    if ( m_sbContents.length() == 0 )
      m_sbContents.append( strPrefix );
    else
    {
      m_sbContents.insert( 0, strPrefix + m_chConcatDelim );
    }

  } // end addAtFront()


  /**
   *
   * Removes a token from the delimited string. Ex. If the delimited string was "cars,boats,trains"
   * then removeToken( "boats" ) returns a delimited string of "cars,trains".
   *
   * @param strToken The string token you want removed.
   *
   * @return A new VwDelimString with the token removed.
   *
   * @exception Exception if the token to remove does not exist
   */
  public VwDelimString removeToken( String strToken ) throws Exception
  {
    reset();
    VwDelimString temp = new VwDelimString( m_strDelimiterList, "" );

    boolean fFound = false;

    String strPiece = null;

    while( (strPiece = getNext() ) != null )
    {
      if ( strPiece.equals( strToken ) )
      {
        fFound = true;
        continue;           // This is the one we want removed
      }

      temp.add( strPiece );       // rebild delim string with the other tokens

    } // end while

    if ( !fFound )
    {
      ResourceBundle rb = ResourceBundle.getBundle("resources.properties.vwutil");
      throw new Exception( strToken + " " + rb.getString( "VwUtil.TokenNotFound" ) );
    }

    return temp;

  } // end removeToken()

  /**
   * Removes all occurrences of the given substring from the delimited string,
   * starting at the specified position.
   *
   * @param strRemove - The substring to be removed
   * @param nStartPos - The position in the delimited string from which to start
   * removing the substring.
   *
   * @return - A new VwDelimString with substring removed as specified
   */
  public VwDelimString remove( String strRemove, int nStartPos )
  {
   String strContents = VwExString.remove( m_sbContents.toString(), strRemove, nStartPos );
   return new VwDelimString( m_strDelimiterList, strContents  );
  }


  /**
   * Removes all occurrences of the given substring from the delimited string
   *
   * @param strRemove - The substring to be removed
   *
   * @return - A new VwDelimString with all occurrences of the substring removed
   */
  public VwDelimString remove( String strRemove )
  { return remove( strRemove, 0 ); }


  /**
   * Provides an Iterator to retrieve the delimited string pieces
   */
  public Iterator<String> iterator()
  { return new DlmsIterator(); }


} // end class VwDelimString


// *** End of VwDelimString.java

