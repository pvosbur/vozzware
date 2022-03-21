/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwStringCursor.java

============================================================================================
*/


package com.vozzware.util;

import java.util.ResourceBundle;

/**
 * This class is a simple parser for Strings. The getWord() method returns substring token
 * groupings delimited by whitespace and additional user defined delimiters. Each call to
 * getWord returns the next word in the string until the end of the string is encounterd in
 * which getWord return null. The string can be traversed in either direction. For example
 * the String "Value=2" using the folowing setup would return 3 words. Value, = , and 2
 * VwStringCursor cur = new VwStringCursor( "Value=2" );
 * cur.setDelimiters( "=" );
 * String strWord = null;
 * while( (strWord = cur.getWord() ) != Null )
 * {
 *   ....
 *  }
 *
 *
 */
public class VwStringCursor
{

  private int           m_nCursor;            // The current cursor value
  private int           m_nDirInd;            // Direction indicator 1 = forward -1 = backwards

  private int           m_nBuffLen;           // The string buffer length of m_strBuffer
  private String        m_strBuffer;          // Buffer used in word fetches
  private String        m_strDelim = null;    // Optional delimter list in addition to whitespace

  private boolean       m_fQuotedString;      // If true last word returned was in a quoted string
  private boolean       m_fIsComment;         // If true last word returned was a comment

  private ResourceBundle m_msgs = ResourceBundle.getBundle( "com.vozzware.util.vwutil" );

  /**
   * Constructor that initializes the cursor to zero and a forward moving direction
   *
   * @param strBuffer The buffer used to fetch words
   *
   * @exception Exception if the String passed is null
   */
  public VwStringCursor( String strBuffer ) throws Exception
  {
    if ( strBuffer == null )
      throw new Exception( m_msgs.getString( "VwUtil.NullBuffer" ) );

    m_strBuffer = strBuffer;
    m_nBuffLen = m_strBuffer.length();

    m_nCursor = 0;
    m_nDirInd = 1;

  } // end VwStringCursor()


  /**
   * Constructor that initializes the cursor and direction
   *
   * @param strBuffer The buffer used to fetch words
   * @param nInitVal The inital cursor value
   * @param nDirInd The initial direction 1 = forwards, -1 = backwards
   *
   * @exception Exception if strBuffer is null,
   *            StringIndexOutOfBoundsException if nInitVal < 0 or > strBuffer length
   *
   */
  public VwStringCursor( String strBuffer, int nInitVal, int nDirInd ) throws Exception,
                                                                   StringIndexOutOfBoundsException
  {
    if ( strBuffer == null )
      throw new Exception( m_msgs.getString( "VwUtil.NullBuffer" ) );

    if ( nInitVal < 0 || nInitVal > strBuffer.length() )
      throw new StringIndexOutOfBoundsException();

    m_strBuffer = strBuffer;
    m_nBuffLen = m_strBuffer.length();

    m_nCursor = nInitVal;

    if ( nDirInd >= 0 )
      m_nDirInd = 1;
    else
      m_nDirInd = -1;

  } // end VwStringCursor()

  /**
   * Returns the string class this cursor is parsing
   *
   */
  public String getBuffer()
  { return m_strBuffer; }


  /**
   * Returns true if last word returned was inside a quoted string
   *
   * @return true if last word returned was inside a quoted string
   */
  public final boolean isQuotedString()
  { return m_fQuotedString; }

  /**
   * Returns the length of the string this cursor is for
   *
   * @return the length of the string this cursor is for
   */
  public final int getLength()
  { return m_strBuffer.length(); }


  /**
   * Sets the cursor position to a new value
   *
   * @param nNewCursorVal
   *
   * @exception StringIndexOutOfBoundsException if nNewCursorVal < 0 or > strBuffer length
   */
  public final void setCursor( int nNewCursorVal )
  {

    if ( nNewCursorVal < 0 || nNewCursorVal > m_strBuffer.length() )
      throw new StringIndexOutOfBoundsException();

    m_nCursor = nNewCursorVal;

  } // end setCursor()


  /**
   * Gets the current cursor position
   *
   @return the current cursor position -1 means EOF
   */
  public final int getCursor()
  { return m_nCursor; }


  /**
   * Sets the current cursor direction indicator 1 = forward -1 = backwards
   *
   * @param nDirInd The direction indicator
   */
  public final void setDirection( int nDirInd )
  {
    if ( nDirInd >= 0 )
      m_nDirInd = 1;
    else
      m_nDirInd = -1;

  } // end setDirection()


  /**
   * Gets the current cursor direction indicator 1 = forward -1 = backwards
   *
   * @return   The direction indicator
   */
  public final int getDirection()
  { return m_nDirInd; }


  /**
   * Sets a delimiter list of characters in addition to the default whitespace characters
   *
   * @param strDelim The string of additional delimiters
   */
  public final void setDelimiters( String strDelim )
  { m_strDelim = strDelim; }


  /**
   * Gets the current delimiter list or null if not set
   *
   * @return the current delimiter list or null if not set
   */
  public final String getDelimiters()
  { return m_strDelim; }


  /**
   * Gets the next word for the String buffer passed or null if the end of the buffer
   * has been reached.
   */
  public final String getWord()
  {
    m_fQuotedString = false;
    m_fIsComment = false;

    if ( m_nCursor < 0 || m_nCursor >= m_nBuffLen )
     return null;                 // End of Buffer

    // *** Begin the search

    // *** first suck up any characters that are white space or delimiters to get us to the
    // *** start of the next word

    char ch = ' ';

    boolean fIsDelim = false;

    for ( ; (m_nCursor >=0 && m_nCursor < m_nBuffLen); m_nCursor += m_nDirInd )
    {
      ch = m_strBuffer.charAt( m_nCursor );

      // Found a delimiter return the delimiter

      if ( m_strDelim != null && VwExString.isin( ch, m_strDelim ) )
      {
        fIsDelim = true;
        break;
      }

      // Otherwise if this character is whitespace keep searching for non delim/whitespace char
      if ( VwExString.isin( ch, " \t\r\n" ) )
        continue;

      break;                    // At the start of the next word

    } // end for


    if ( m_nCursor < 0 || m_nCursor >= m_nBuffLen )
      return null;               // Only white pace found, and we hit the end of the string

    // Check to see if bailout was caused by a delimiter
    if ( fIsDelim )
    {

      if ( m_nDirInd < 0 )                // Moving backwards?
      {
        --m_nCursor;
        return m_strBuffer.substring( m_nCursor + 1, m_nCursor + 2 );
      }
      else
      {
        ++m_nCursor;
        return m_strBuffer.substring( m_nCursor - 1, m_nCursor );
      }

    } // end if

    int nStartPos = m_nCursor;

    // *** This search terminates at the next white space or delimiter character

    for ( ; (m_nCursor >=0 && m_nCursor < m_nBuffLen ); m_nCursor += m_nDirInd )
    {
      ch = m_strBuffer.charAt( m_nCursor );

      if ( m_strDelim != null && VwExString.isin( ch, m_strDelim ) && !m_fQuotedString )
        break;

      if ( VwExString.isin( ch, " \t\r\n" ) && !m_fQuotedString )
        break;

      // Do comment test

      if ( VwExString.isin( ch, "\n/-" ) )

      // Do quoted string test

      if ( ch == '"' )
      {
        if ( m_fQuotedString )   // This is the end quote so get out
          break;

        // else we are starting a quoted string
        m_fQuotedString = true;
        nStartPos = m_nCursor + m_nDirInd;   // Update start pos to point pas the double quote

        continue;
      }

     } // end for

     if ( m_nDirInd < 0 )
     {
       if ( m_fQuotedString )
       {
         int nTemp = m_nCursor + 1; // Don't include the quote character
         --m_nCursor;               // Discard quote

         return  m_strBuffer.substring( nTemp, nStartPos + 1 );
       }
       else
         return  m_strBuffer.substring( m_nCursor + 1, nStartPos + 1);
     }

     if ( m_fQuotedString )
       return  m_strBuffer.substring( nStartPos, m_nCursor++ );

     return  m_strBuffer.substring( nStartPos, m_nCursor );

  } // end getWord()

  public static void main( String[] args )
  {

    try
    {
      VwStringCursor sc = new VwStringCursor( "http-equiv=\" some shit\" Content=\"more shit\"" );
      sc.setDelimiters( "=" );
      String strWord = null;
      String strPiece = null;

      while ( true )
      {

        String strKeyWord = sc.getWord();

        if ( strKeyWord == null )
          break;

        sc.getWord(); // suck up delimiter

        String strVal = sc.getWord();

        if ( sc.isQuotedString() )
          System.out.println( "quoted String" );


      }

    }
    catch( Exception e )
    {}
  }
} // end class VwStringCursor{}

// *** End of VwStringCursor.java ***
