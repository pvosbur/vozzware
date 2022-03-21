/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwTextParser.java

============================================================================================
*/


package com.vozzware.util;

import java.util.ResourceBundle;


/**
 * This class is a simple parser for Strings. The getWord() method returns substring token
 * groupings delimited by whitespace and additional user defined delimiters. Each call to
 * getWord returns the next word in the string until the end of the string is encountered in
 * which getWord returns null. The string can be traversed in either direction. For example
 * the String "Value=2" using the following setup would return 3 words. Value, = , and 2
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
public class VwTextParser
{

  private int           m_nCursor;              // The current cursor value
  private int           m_nDirInd;              // Direction indicator 1 = forward -1 = backwards
  private int           m_nBuffLen;             // The string buffer length of m_sbBuffer

  private char[]        m_achQuotedString = new char[]{'"','\''};

  private StringBuffer  m_sbBuffer;             // Buffer used in word fetches
  private String        m_strDelim = null;      // Optional delimiter list in addition to whitespace

  private String        m_strLineComment;       // Single line beginning comment char sequence
  private String        m_strStartBlockComment; // Multiline block comment char sequence
  private String        m_strEndBlockComment;   // multiline ending sequence

  private String        m_strCommentChars = ""; // Starting characters of any comment types defined

  private boolean       m_fRetWhitespace = false;     // Return whitespace if true

  private boolean       m_fRetQuotes;         // Return the quote characters in quoted string if true
  private boolean       m_fBreakOnNewLineChar = false;
  private boolean       m_fIgnoreQuotes = false;

  /**
   * Constant for a quoted string token type
   */
  public static final int QSTRING = 1;        // Quoted string token type
  /**
   * Constant for white space token type
   */
  public static final int WSPACE = 2;         // White space token type

  /**
   * Constant for a single line comment token type
   */
  public static final int LCOMMENT = 3;       // Single line comment

  /**
   * Constant for a block comment token type
   */
  public static final int BCOMMENT = 4;       // Block  comment

  /**
   * Constant for JAVADOC comment structure
   */
  public static final int JAVADOC = 5;        // Java Doc comment

  /**
   * Constant for a simple word token type
   */
  public static final int WORD = 6;           // a word

  /**
   * Constant for a delimiter token type
   */
  public static final int DELIM = 7;          // a delimiter


  /**
   * Constant end of file/stream
   */
  public static final int EOF = 8;          // end of file/stream

  private ResourceBundle m_msgs = ResourceBundle.getBundle( "com.vozzware.util.vwutil" );

  /**
   * Constructor that initializes the cursor to zero and a forward moving direction
   *
   * @param inSrc The buffer used to fetch words
   *
   * @exception Exception if the String passed is null
   */
  public VwTextParser( VwInputSource inSrc ) throws Exception
  {
     setInput( inSrc );

  } // end VwStringCursor()


  /**
   * Sets/resets the input to parse
   * @param inSrc
   * @throws Exception
   */
  public void setInput( VwInputSource inSrc ) throws Exception
  {
    if ( inSrc == null )
      throw new Exception( m_msgs.getString( "VwUtil.NullBuffer" ) );

    m_sbBuffer = inSrc.readAll();
    m_nBuffLen = m_sbBuffer.length();

    m_nCursor = 0;
    m_nDirInd = 1;

  } // end setInput;

  /**
   * Constructor that initializes the cursor and direction
   *
   * @param inpSrc The InputSource to parse
   * @param nInitVal The inital cursor value
   * @param nDirInd The initial direction 1 = forwards, -1 = backwards
   *
   * @exception Exception if strBuffer is null,
   *            StringIndexOutOfBoundsException if nInitVal < 0 or > strBuffer length
   *
   */
  public VwTextParser( VwInputSource inpSrc, int nInitVal, int nDirInd ) throws Exception,
                                                                   StringIndexOutOfBoundsException
  {
    if ( inpSrc == null )
      throw new Exception( m_msgs.getString( "VwUtil.NullBuffer" ) );

    m_sbBuffer = inpSrc.readAll();

    if ( nInitVal < 0 || nInitVal > m_sbBuffer.length() )
      throw new StringIndexOutOfBoundsException();

    m_nBuffLen = m_sbBuffer.length();

    m_nCursor = nInitVal;

    if ( nDirInd >= 0 )
      m_nDirInd = 1;
    else
      m_nDirInd = -1;

  } // end VwTextParser()


  public void setIgnoreQuotes( boolean fIgnoreQuotes )
  { m_fIgnoreQuotes = fIgnoreQuotes; }


  public void setTreatSingleQuoteAsData( boolean fSingleQuoteAsData )
  {
    // remove single quote from quote test

    if ( fSingleQuoteAsData )
      m_achQuotedString = new char[]{'"'};
    else
      m_achQuotedString = new char[]{'"','\''};


  }

  public void setBreakonNewLinfCharacter( boolean fBreakOnNewLineChar )
  { m_fBreakOnNewLineChar = fBreakOnNewLineChar; }
  
  /**
   * Define block comment start and end sequences
   * @param strStart Starting block comment sequence. NOTE! if the '/*' is defined the javadoc version of '/**'
   * is also recognized. i.e. '/*'
   *
   * @param strEnd The block comment end sequence  i.e. "*\\"
   * @throws RuntimeException if either parameter is null
   */
  public void setBlockComment( String strStart, String strEnd )
  {
    if ( strStart == null)
      throw new RuntimeException( "Starting block comment sequence cannot be null");

    m_strStartBlockComment = strStart;

    if ( strEnd == null)
      throw new RuntimeException( "Ending block comment sequence cannot be null");

    m_strEndBlockComment = strEnd;

    char chFirst = m_strStartBlockComment.charAt(  0 );
    char chLast = m_strEndBlockComment.charAt(m_strEndBlockComment.length() - 1 );

    if ( !VwExString.isin( chFirst, m_strCommentChars) )
      m_strCommentChars += chFirst;

    if ( !VwExString.isin( chLast, m_strCommentChars) )
      m_strCommentChars += chLast;

  } // end setBlockComments()

  /**
   * Set the character that marks the start end end of a quoted string. The default is is " character
   * @param achQuotedString The quotes string character
   */
  public void setQuotedStringChar( char[] achQuotedString )
  { m_achQuotedString = achQuotedString; }
  
  
  /**
   * Gets the the character that marks the start end end of a quoted string. The default is is " character
   * @return
   */
  public char[] getQuotedStringChar()
  { return m_achQuotedString; }
  
  /**
   * Set a recognized single  single line comment.<br>
   * A single line comment is always terminated by the new line character '\n'
   *
   * @param strLineComment The single line comment character sequence i.e. // or -- or rem ...
   */
  public void setSingleLineComment( String strLineComment )
  {
    if ( strLineComment == null)
      throw new RuntimeException( "Line comment sequence cannot be null");

    m_strLineComment = strLineComment;

    char chFirst = m_strLineComment.charAt(  0 );

    if ( !VwExString.isin( chFirst, m_strCommentChars) )
      m_strCommentChars += m_strLineComment.substring( 0, 1 );

    m_strCommentChars += "\n";

  } // end setSingleLineComments()


  /**
   * Determines if white space will be returned as a token type. The default is false
   *
   * @param fRetWhitespace If true, white space will be returned else it will be skipped
   */
  public void setReturnWhitespace( boolean fRetWhitespace )
  { m_fRetWhitespace = fRetWhitespace; }


  /**
   * Determines if the quote characters in a quoted string will be returned with the string.
   * The default is false, quote characters will not be returned with the quoted string
   *
   * @param fRetQuotes if true, quote characters will be returned with the quoted string
   */
  public void setIncludeQuotes( boolean fRetQuotes )
  { m_fRetQuotes = fRetQuotes; }


  /**
   * Returns the string class this cursor is parsing
   *
   */
  public String getBuffer()
  { return m_sbBuffer.toString(); }



  /**
   * Returns the length of the string this cursor is for
   *
   * @return the length of the string this cursor is for
   */
  public final int getLength()
  { return m_sbBuffer.length(); }


  /**
   * Sets the cursor position to a new value
   *
   * @param nNewCursorVal The new cursor position within the buffer
   *
   * @exception StringIndexOutOfBoundsException if nNewCursorVal < 0 or > strBuffer length
   */
  public final void setCursor( int nNewCursorVal )
  {

    if ( nNewCursorVal < 0 || nNewCursorVal > m_sbBuffer.length() )
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

    if ( m_nDirInd < 0 )
      m_nCursor = m_sbBuffer.length() - 1;

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
   * Finds the first/next occurrence of the token starting at the current cursor position and
   * directon indicator
   * 
   * @param strToken The token to search for
   * @return The position in the string where the token starts
   */
  public int findToken( String strToken )
  {
    StringBuffer sbToken = new StringBuffer();
    
    while( getToken( sbToken ) != EOF)
    {
      if ( sbToken.toString().equalsIgnoreCase( strToken ))
      { 
        if ( m_nDirInd < 0 )
          return getCursor() + 1;
        else
          return getCursor() - strToken.length();
      }
    }
    
    return -1;      // Not found
    
  } // end findToken()
  
  
  /**
   * Gets the next token and it's value for the String buffer passed or EOF  if the end of the buffer
   * has been reached.
   *
   * @param sbToken A StringBuffer which will contain the string representation of the token
   * 
   * @return The token type found see the static constant token types for this class 
   */
  public final int getToken( StringBuffer sbToken )
  {
    sbToken.setLength( 0 );

    if ( m_nCursor < 0 || m_nCursor >= m_nBuffLen )
     return EOF;                 // End of Buffer

    // *** Begin the search
    char ch = ' ';

    boolean fIsDelim = false;
    boolean fIsWhitespace = false;
    int nQuoteCount = 0;

    int nStartPos = m_nCursor;

    // *** first suck up any characters that are white space or delimiters, or comments to get us to the
    // *** start of the next word
    for ( ; (m_nCursor >=0 && m_nCursor < m_nBuffLen); m_nCursor += m_nDirInd )
    {
      ch = m_sbBuffer.charAt( m_nCursor );

      // Make sure delimiter is not also a starting comment character
      // Are we looking for comment sequences?

      int nCommentType = 0;

      if ( VwExString.isin( ch, m_strCommentChars )   && (nQuoteCount == 0) && (nCommentType = getCommentType( sbToken ) ) > 0 )
      {

        if ( m_nDirInd < 0  )
        {
          if ( nCommentType == LCOMMENT )
            return LCOMMENT;
          else
            return getBlockComment( sbToken );

        }
        else
        {
          // Test for block comments
          if ( nCommentType == LCOMMENT )
            return getLineComment( sbToken );
          else
            return getBlockComment( sbToken );
        }

      } // end if ( VwExString.isin( ch, m_strCommentChars ) )

      // Found a delimiter, bail out

      if ( m_strDelim != null && VwExString.isin( ch, m_strDelim ) )
      {
        fIsDelim = true;
        break;
      }

      // Otherwise if this character is whitespace keep searching for non delim/whitespace char
      if ( VwExString.isin( ch, " \t\r\n" ) )
      {
        if ( m_fBreakOnNewLineChar && ch == '\n' )
          return 0;
        
        if ( m_strLineComment != null && (nQuoteCount == 0)  && m_nDirInd < 0 )
        {
          if ( nStartPos == (m_nBuffLen - 1) || ch == '\n' )
          {
            if ( isLineComment( sbToken ) )
              return LCOMMENT;
          }
        }

        fIsWhitespace = true;
        continue;
      }

      break;                    // At the start of the next word

    } // end for

    // If white space and requested, return white space
    if ( m_fRetWhitespace && fIsWhitespace )
    {
      if ( m_nDirInd < 0 )
        sbToken.append( m_sbBuffer.substring( m_nCursor + 1, nStartPos + 1 ) );
      else
        sbToken.append( m_sbBuffer.substring( nStartPos, m_nCursor ) );

       return WSPACE;
    }

    if ( m_nCursor < 0 || m_nCursor >= m_nBuffLen )
      return EOF;               // Only white space found, and we hit the end of the string

    // Check to see if bailout was caused by a delimiter
    if ( fIsDelim )
    {

      if ( m_nDirInd < 0 )      // Moving backwards?
      {
        --m_nCursor;
        sbToken.append(  m_sbBuffer.substring( m_nCursor + 1, m_nCursor + 2 ) );
        return DELIM;
      }
      else
      {
        ++m_nCursor;
        sbToken.append( m_sbBuffer.substring( m_nCursor - 1, m_nCursor ) );
        return DELIM;
      }

    } // end if

    nStartPos = m_nCursor;

    // *** This search terminates at the next white space or delimiter character or comment

    for ( ; (m_nCursor >=0 && m_nCursor < m_nBuffLen ); m_nCursor += m_nDirInd )
    {
      ch = m_sbBuffer.charAt( m_nCursor );

      if ( m_strDelim != null && VwExString.isin( ch, m_strDelim ) && (nQuoteCount == 0) )
        break;

      // Quotes are terminated with new line characters
      if ( VwExString.isin( ch, "\r\n" ) && (nQuoteCount == 2 ) )
        break;

      if ( ! m_fRetWhitespace)
      {
        if ( VwExString.isin( ch, " \t\r\n" ) && (nQuoteCount == 0) )
          break;
      }

      // Treat a comment char as a delimiter
      if ( VwExString.isin( ch, m_strCommentChars ) && (nQuoteCount == 0) && getCommentType( sbToken ) > 0  )
        break;

      // Do quoted string test

      if ( !m_fIgnoreQuotes && isQuotedString( ch ) )
      {
        ++nQuoteCount;
        if ( nQuoteCount == 2 )   // This is the end quote so get out
          break;

        if ( nStartPos != m_nCursor )
          break;  // Treat as a delimiter, possibly this is due to unmatched quotes
        // else we are starting a quoted string
        nStartPos = m_nCursor;   // Update start pos

        continue;
      }

    } // end for


    if ( m_nDirInd < 0 )
    {
     if ( nQuoteCount == 2 )
     {
       int nTemp = 0;

       if ( !m_fRetQuotes )
       {
         nTemp = m_nCursor + 1;
       }
       else
       {
         nTemp = m_nCursor;
         if ( nQuoteCount == 2 )
           ++nStartPos;
       }

       m_nCursor--;            // Discard quote
       sbToken.append( m_sbBuffer.substring( nTemp, nStartPos ) );
       return  QSTRING;
     }
     else
     {
       sbToken.append( m_sbBuffer.substring( m_nCursor + 1, nStartPos + 1 ) );
       return  WORD;
     }
    }

    if ( nQuoteCount > 0  )
    {

     if ( !m_fRetQuotes )
       ++nStartPos;
     else
     {
       if ( nQuoteCount == 2)
         ++m_nCursor;
     }

     sbToken.append( m_sbBuffer.substring( nStartPos, m_nCursor ) );
     if ( !m_fRetQuotes )
       ++m_nCursor;

     if ( nQuoteCount == 1 )
     {
       if ( sbToken.charAt( 0 ) == '\'' || sbToken.charAt( 0 ) == '"' )
         return  QSTRING;
       else
         return WORD;
     }
     return QSTRING;

    }

    sbToken.append( m_sbBuffer.substring( nStartPos, m_nCursor ) );
    return  WORD;

  } // end getToken()

  private boolean isQuotedString( char ch )
  {
    for ( int x = 0; x < m_achQuotedString.length; x++ )
    {
      if ( ch == m_achQuotedString[ x ])
        return true;
      
    }
    
    return false;
  }


  /**
   * Determine if the character sequence from the starting cursor pos is a comment start
   * @return The comment type constant if its a comment or zero if not
   */
  private int getCommentType( StringBuffer sbVal )
  {
    int nCommentType = 0;

    if ( m_nDirInd < 0 )
    {
      if ( m_strStartBlockComment != null  )
      {
        int nBlockLen = m_strEndBlockComment.length();

        if ( m_nCursor - nBlockLen >= 0  )
        {
          if ( m_sbBuffer.substring( m_nCursor - nBlockLen + 1,  m_nCursor + 1 ).equals( m_strEndBlockComment ) )
            return BCOMMENT;
        }

      } // end if ( m_strStartBlockComment != null )

      if ( m_strLineComment != null  )
      {
        if ( m_sbBuffer.charAt( m_nCursor ) == '\n' || m_nCursor == (m_nBuffLen -1) )
        {
          if ( isLineComment( sbVal ) )
            return LCOMMENT;
        }
      }

      return 0;

    } // end if ( m_nDirInd < 0 )

    if ( m_strStartBlockComment != null  )
    {
      int nBlockLen = m_strStartBlockComment.length();

      if ( m_nCursor + nBlockLen < m_sbBuffer.length() )
      {
        if ( m_sbBuffer.substring( m_nCursor, m_nCursor + nBlockLen).equals( m_strStartBlockComment ) )
        {
          if ( m_strStartBlockComment.equals( "/**"))
            nCommentType = JAVADOC;
          else
            nCommentType = BCOMMENT;

          return nCommentType;
        }
      }

    } // end if ( m_strStartBlockComment != null )

    if ( m_strLineComment != null  )
    {
      int nBlockLen = m_strLineComment.length();

      if ( m_nCursor + nBlockLen < m_sbBuffer.length() )
      {
        if ( m_sbBuffer.substring( m_nCursor, m_nCursor + nBlockLen).equals( m_strLineComment ) )
          return LCOMMENT;
      }

    } // end if ( m_strStartBlockComment != null )


    return 0; // Not a comment sequence

  } // end getCommentType()


  /**
   * Test to see if we have a line comment moving in a backwords direction.
   * @param sbVal The buufer to set if we have a line comment
   * @return true, if there is a line comment, false otherwise
   */
  private boolean isLineComment( StringBuffer sbVal )
  {
    int nStartPos = m_nCursor - 1;
    boolean fQuotedString = false;

    for ( ; nStartPos > 0; --nStartPos )
    {
      char ch = m_sbBuffer.charAt( nStartPos );

      if ( ch == '\'' || ch == '"' )
       fQuotedString = !fQuotedString;

      if ( ch == '\n' || ch == '\n' )
        return false;

      if ( VwExString.isin( ch, m_strLineComment ) && !fQuotedString )
      {
        int nNextPos = nStartPos - 1;

        if ( nNextPos > 0 )
        {
          if ( m_sbBuffer.charAt( nNextPos ) == ch ) // we have a // or a -- sequence
          {
            sbVal.append( m_sbBuffer.substring( nNextPos, m_nCursor ) );
            m_nCursor = nNextPos - 1;  // Move cursor to next pos in buff
            return true; // Got a line comment
          }
        }
      }

    } // end for


    return false;

  } // end isLineComment()


  /**
   * Gets the bock comment. NOTE! Block comments are terminated in the following ways:
   * <br>1. End of buffer is reached
   * <br>2. The ending block comment character sequence was found
   * <br>3. The strating sequence of another block comment. Block comments cannot be nessted.
   *
   * @param sbVal The StringBuffer to hold the block comment
   * @return
   */
  private int getBlockComment( StringBuffer sbVal )
  {

    int nStartPos = m_nCursor;
    m_nCursor += m_nDirInd;

    int nCommentType = BCOMMENT;
    int nCommentLen = 0;
    int nCommentLenNested = 0;
    char chSearchEnd = 0;
    char chNested = 0;
    int nBuffLen = m_sbBuffer.length();

    if ( m_nDirInd < 0 )
    {
      nCommentLen = m_strStartBlockComment.length();
      chSearchEnd = m_strStartBlockComment.charAt( 0 );

      nCommentLenNested = m_strEndBlockComment.length();
      chNested = m_strEndBlockComment.charAt( nCommentLenNested - 1 );

    }
    else
    {
      nCommentLen = m_strEndBlockComment.length();
      chSearchEnd = m_strEndBlockComment.charAt( nCommentLen - 1 );
      nCommentLenNested = m_strStartBlockComment.length();
      chNested = m_strStartBlockComment.charAt( 0 );
    }


    for ( ; (m_nCursor >=0 && m_nCursor < m_nBuffLen ); m_nCursor += m_nDirInd )
    {
      char ch = m_sbBuffer.charAt( m_nCursor );
      int nLen = 0;
      String strComment = null;

      if ( ch == chSearchEnd  || ch == chNested )
      {

        if ( m_nDirInd >= 0 )
        {
          if ( ch == chSearchEnd )
          {
            nLen = nCommentLen;
            strComment = m_strEndBlockComment;
            if ( m_nCursor - nLen >= 0  )
            {
              if ( m_sbBuffer.substring( m_nCursor - nLen + 1, m_nCursor + 1  ).equals( strComment ))
                break;
            }
          }
          // Nest comment found here
          nLen = nCommentLenNested;
          strComment = m_strStartBlockComment;
          if ( m_nCursor + nLen < nBuffLen )
          {
            if ( m_sbBuffer.substring( m_nCursor, m_nCursor + nLen  ).equals( strComment ))
            {
              --m_nCursor;
              break;
            }
          }
        } // end if (  m_nDirInd >= 0 )
        else
        {

          if ( ch == chSearchEnd )
          {
            nLen = nCommentLen;
            strComment = m_strStartBlockComment;
            if ( m_nCursor + nLen < nBuffLen )
            {
              if ( m_sbBuffer.substring( m_nCursor, m_nCursor + nLen  ).equals( strComment ))
                break;
            }

          }
          else
          {
            nLen = nCommentLenNested;
            strComment = m_strEndBlockComment;
            if ( m_nCursor - nLen >= 0  )
            {
              if ( m_sbBuffer.substring( m_nCursor - nLen + 1, m_nCursor + 1  ).equals( strComment ) )
              {
                ++m_nCursor;
                break;
              }
            }

          }

        }
      }  // end if
    } // end for

    if ( m_nDirInd < 0 )
    {
      if ( m_nCursor < 0  )
        m_nCursor = 1;

      sbVal.append( m_sbBuffer.substring( m_nCursor, nStartPos + 1 ) );
      --m_nCursor;
    }
    else
    {
      if ( m_nCursor >= m_nBuffLen )
        m_nCursor = m_nBuffLen -1;

      sbVal.append( m_sbBuffer.substring( nStartPos, ++m_nCursor ) );

    } // end else

    if ( sbVal.substring( 0,3 ).equals( "/**") )
      nCommentType = JAVADOC;

    return nCommentType;

  } // end get Block Comment

  /**
   * Gather all characters that make up the single line comment
   * @param sbVal
   */
  private int getLineComment( StringBuffer sbVal )
  {

    int nStartPos = m_nCursor;

    for ( ; (m_nCursor >=0 && m_nCursor < m_nBuffLen ); m_nCursor += m_nDirInd )
    {
      char ch = m_sbBuffer.charAt( m_nCursor );

      if ( ch == '\r' || ch == '\n' )
        break;

    } // end for

    if ( m_nCursor >= m_nBuffLen )
      m_nCursor = m_nBuffLen;

    sbVal.append( m_sbBuffer.substring( nStartPos, m_nCursor ) );

    return LCOMMENT;

  } // end getLineComment()


  // *** For internal testing only

  public static void main( String[] args )
  {

    try
    {
      VwTextParser tp = new VwTextParser( new VwInputSource( "// 'Test'<!-- A Java doc comment--> <!--http-equiv = \"some shit\"\n/*Yes*/ <!--Content=\n'more shit-->" ) );
      //VwTextParser tp = new VwTextParser( new VwInputSource( "<Top attr=\"Y\"/><!--Comment-->" ) );
      tp.setDelimiters( "<>" );
      tp.setDirection( -1 );
      tp.setBlockComment( "<!--", "-->");
      //tp.setSingleLineComment( "--" );

      //tp.setReturnWhitspace( true );
      tp.setIncludeQuotes( true );
      String strWord = null;
      StringBuffer sbWord = new StringBuffer();
      int nTokType = -1;

      while ( (nTokType = tp.getToken( sbWord )) != VwTextParser.EOF )
      {

        strWord = sbWord.toString();

        System.out.print( strWord );
        switch( nTokType )
        {
          case QSTRING:

               System.out.println( " is quoted String" );
               break;

          case WORD:

               System.out.println( " is simple Word" );
               break;

          case LCOMMENT:

               System.out.println( " is a single line comment" );
               break;

          case BCOMMENT:

               System.out.println( " is a block comment" );
               break;

          case JAVADOC:

               System.out.println( " is a JavaDoc comment" );
               break;

          case DELIM:

               System.out.println( " is a delimiter" );
               break;

          case WSPACE:

               System.out.println( " is a white space" );
               break;
        }
      }

    }
    catch( Exception e )
    {
      e.printStackTrace();
    }

  } // end main()

} // end class VwTextParser{}

// *** End of VwTextParser.java ***

