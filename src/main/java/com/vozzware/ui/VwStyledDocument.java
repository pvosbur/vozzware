/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwStyledDocument.java

Create Date: Apr 11, 2003
============================================================================================
*/
package com.vozzware.ui;

import com.vozzware.util.VwExString;
import com.vozzware.util.VwInputSource;
import com.vozzware.util.VwTextParser;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;

/**
 * Class that implements syntax hiliting
 */
public class VwStyledDocument extends DefaultStyledDocument
{
  private VwStyledWordDescriptor[] m_aWordDesc = null;

  private VwTextParser             m_tp = null; // Text parser

  private String                    m_strDelimiters = " \r\n\t";
  private String                    m_strWordDelimiters;
  private AttributeSet              m_defAttrSet;

  private String                    m_strStartBlockComment = null;
  private String                    m_strEndBlockComment = null;
  private String                    m_strLineComment = null;
  private String                    m_strAnyComment = null;

  private static final int SKIP = 99;
  
  class CommentPositions
  {
    String  m_strWord;
    int     m_nStartBlock;
    int     m_nEndBlock;
    int     m_nStartLine;

  } // end CommentPositions{}

  private CommentPositions        m_cp = new CommentPositions();

  private boolean m_fDirty;

  
  /**
   * Create with one inital set set of styles words
   *
   * @param wordDesc The styled word set
   */
  public VwStyledDocument( VwStyledWordDescriptor wordDesc, VwStyledWordDescriptor wordDefault, String strDelimiters )
  {
    m_aWordDesc = new VwStyledWordDescriptor[]{ wordDesc };
    m_defAttrSet = wordDefault.getAttrSet( null );

    if ( strDelimiters != null )
      m_strDelimiters += strDelimiters;

    m_strWordDelimiters = m_strDelimiters + "'\"";

   } // end VwStyledDocument()


  /**
   * Default constructor
   */
  public VwStyledDocument()
  {
    m_aWordDesc = new VwStyledWordDescriptor[ 0 ];
    m_strWordDelimiters = m_strDelimiters + "'\"";
  } // end VwStyledDocument()


  /**
   * Sets an additional set of delimietrs for the lecical grammer of the syntax
   * @param strDelimiters A comma separated list of character delimiters
   */
  public void setDelimeters( String strDelimiters )
  {
    m_strDelimiters += strDelimiters;
    m_strWordDelimiters = m_strDelimiters + "'\"";

  }

  public boolean isDirty()
  { return m_fDirty; }
  
  public void setDirty( boolean fDirty )
  { m_fDirty = fDirty; }
  
  
  /**
   * Sets the default attribute set to use when a word is encounterd with no associated
   * attribute set
   *
   * @param wordDesc The default word descriptor
   */
  public void setDefaultAttrSet( VwStyledWordDescriptor wordDesc )
  { m_defAttrSet = wordDesc.getAttrSet( null ); }

  
  /**
   * Return the defualt attribute set
   * @return
   */
  public AttributeSet getDefaultAttrSet()
  { return m_defAttrSet; }
  
  
  /**
   * Adss a styled word set to the list of word set descriptors
   * @param wordDesc the styled word set to add
   */
  public void addStyledWordSet( VwStyledWordDescriptor wordDesc )
  {
    if ( m_aWordDesc == null )
      m_aWordDesc = new VwStyledWordDescriptor[]{ wordDesc };
    else
    {
      VwStyledWordDescriptor[] aTemp = new VwStyledWordDescriptor[ m_aWordDesc.length + 1 ];
      System.arraycopy( m_aWordDesc, 0, aTemp, 0, m_aWordDesc.length );
      aTemp[ m_aWordDesc.length ] = wordDesc;
      m_aWordDesc = null;
      m_aWordDesc = aTemp;
      aTemp = null;

    }

  } // end addStyledWordSet()

  /**
   * Remove all of the contents of this document
   */
  public void removeAll()
  {
    m_fDirty = true;
    
    int nLen = getLength();

    try
    {
      if ( nLen > 0 )
        super.remove( 0, nLen );
    }
    catch( Exception ex )
    { ; } // this exception will never happen on this call )
  }

  /**
   * Check for syntax hiliting on character removal
   * @param nOffset Starting offset in the document
   * @param nLen Nbr of characters being deleted
   * @throws BadLocationException
   */
  public void remove( int nOffset, int nLen ) throws BadLocationException
  { remove( nOffset, nLen, false ); }
  

  /**
   * Check for syntax hiliting on character removal
   * @param nOffset Starting offset in the document
   * @param nLen Nbr of characters being deleted
   * @throws BadLocationException
   */
  public void remove( int nOffset, int nLen, boolean fIsUndoRedo ) throws BadLocationException
  {
    m_fDirty = true;
    String strRemoveText = getText( nOffset, nLen );
    
    if ( VwExString.isWhiteSpace( strRemoveText ))
    {
      if ( !fIsUndoRedo )
        super.remove( nOffset, nLen );
      return;
    }
    
    if ( m_strLineComment != null && strRemoveText.indexOf( m_strLineComment ) >= 0 )
    {
      if ( !fIsUndoRedo )
        super.remove( nOffset, nLen );
      
      doDocScan( nOffset, findNewLine( nOffset, 1 ) );
      return;
      
    }
    
    // If the character we are removing, is a comment character or string
    // quote character, then the document needs to be re-scaned

    if ( nLen == 1 )
    {
      String str = getText( nOffset, 1 );

      getCommentPos( nOffset );

      if ( m_strStartBlockComment != null && m_cp.m_strWord.equals( m_strStartBlockComment ) )
      {
        if ( m_cp.m_nStartLine < 0  )
        {
          if ( !fIsUndoRedo )
            super.remove( nOffset, nLen );
          doCommentScan( m_cp.m_nStartBlock );
          return;
        }

      }

      if ( m_strEndBlockComment != null && m_cp.m_strWord.equals( m_strEndBlockComment ) )
      {
        if ( m_cp.m_nStartLine < 0  )
        {
          if ( !fIsUndoRedo )
            super.remove( nOffset, nLen );
          doCommentScan( m_cp.m_nStartBlock );
          return;
        }
      }

      if ( m_strLineComment != null && m_cp.m_strWord.equals( m_strLineComment ) )
      {
        if ( m_cp.m_nStartBlock < 0  )
        {
          if ( !fIsUndoRedo )
            super.remove( nOffset, nLen );
          doCommentScan( m_cp.m_nStartLine  );
          return;
        }
      }

      if ( !fIsUndoRedo )
        super.remove( nOffset, nLen );

      char ch = str.charAt( 0 );

      if ( ch == '\'' || ch == '"' )
      {

        if ( m_cp.m_nStartBlock >= 0 && nOffset >  m_cp.m_nStartBlock )
        {
          if ( m_cp.m_nEndBlock < 0 || m_cp.m_nEndBlock >= 0 && nOffset < m_cp.m_nEndBlock )
          {
            doCharScan( nOffset, getText( nOffset, 1 ));
            return;
          }
        }

        if ( m_cp.m_nStartLine >= 0 && nOffset >  m_cp.m_nStartLine )
        {
          doCharScan( nOffset, getText( nOffset, 1 ));
          return;

        }

        if ( m_strStartBlockComment != null )
        {
          if ( insideQuote( nOffset ) )
            doCommentScan( getQuotePos( nOffset ) );
          else
            doCommentScan( nOffset );
        }
        else
        {
          if ( insideQuote( nOffset ) )
            doDocScan( getQuotePos( nOffset ), findNewLine( nOffset, 1 ) );
          else
            doDocScan( findNewLine( nOffset, -1 ),  findNewLine( nOffset, +1 ));
        }

        return;

      }

      if ( nOffset < 0 )
        nOffset = 0;

      int nWordStart = findStartOfWord( nOffset - 1);
      
      nLen = (findEndOfWord( nOffset) ) - nWordStart;
      
      if ( nLen > 0 )
        doCharScan( nWordStart, getText( nWordStart, nLen ));
      
      return;

    } // end if nLen == 1 )

    if ( !fIsUndoRedo )
      super.remove( nOffset, nLen );
    
    if ( m_strStartBlockComment != null )
    {
      if ( strRemoveText.indexOf( m_strStartBlockComment ) < 0 && strRemoveText.indexOf( m_strEndBlockComment ) < 0 )
      {
        doDocScan( nOffset, findNewLine( nOffset, 1 ));
        return;
        
        
      }
    }
    
    doCommentScan( nOffset );

  } // end remove


  /**
   * Override of the insertString. We perform our synatx hilting here
   * @param nOffset
   * @param strText
   * @param attrSet
   * @throws BadLocationException
   */
  public void insertString( int nOffset, String strText, AttributeSet attrSet ) throws BadLocationException
  { insertString( nOffset, strText, attrSet, false ); }

  /**
   * Override of the insertString. We perform our synatx hilting here
   * @param nOffset
   * @param strText
   * @param attrSet
   * @throws BadLocationException
   */
  public void insertString( int nOffset, String strText, AttributeSet attrSet, boolean fIsUndoRedo ) throws BadLocationException
  {
    m_fDirty = true;
    
    if ( strText.length() == 1 )
    {
      getCommentPos( nOffset );

      // Before inserting typed character, see if we're breaking up a comment sequence

      // We test for the case here where there used to be a comment and the character just
      // inserted cancels the comment sequence. we need to re-scan
      // See if start block comment sequence was broken

      if ( m_strStartBlockComment != null && m_cp.m_strWord.equals( m_strStartBlockComment ) )
      {
        if ( m_cp.m_nStartLine < 0 )
        {
          if( !fIsUndoRedo ) // skip actual insert if this is from an undo/redo operation as text was already inserted by undo/redo manager
            super.insertString( nOffset, strText, attrSet );
          
          doCommentScan( m_cp.m_nStartBlock );
          return;
        }

      }

      if ( m_strEndBlockComment != null && m_cp.m_strWord.equals( m_strEndBlockComment ) )
      {
        if ( m_cp.m_nStartLine < 0  )
        {
          if( !fIsUndoRedo ) // skip actual insert if this is from an undo/redo operation as text was already inserted by undo/redo manager
            super.insertString( nOffset, strText, attrSet );
          
          // If the character we're inserting is part of the end block comment then scan from the end
          // of the comment
          if ( m_strEndBlockComment.indexOf( strText ) >= 0 )
            doCommentScan( m_cp.m_nEndBlock + m_strEndBlockComment.length() );
          else
            doCommentScan( m_cp.m_nStartBlock );
          return;
        }
      }

      if ( m_strLineComment != null && m_cp.m_strWord.equals( m_strLineComment ) )
      {
        
        if ( m_cp.m_nStartBlock < 0  )
        {
          if( !fIsUndoRedo ) // skip actual insert if this is from an undo/redo operation as text was already inserted by undo/redo manager
             super.insertString( nOffset, strText, attrSet );
          
          doCommentScan( m_cp.m_nStartLine );
          return;
        }
      }

      if( !fIsUndoRedo ) // skip actual insert if this is from an undo/redo operation as text was already inserted by undo/redo manager
        super.insertString( nOffset, strText, attrSet );
      
      doCharScan( nOffset, strText );

      return;

    }

    // Multi insert string
    if ( VwExString.isWhiteSpace( strText ))
    {
      if( !fIsUndoRedo ) // skip actual insert if this is from an undo/redo operation as text was already inserted by undo/redo manager
        super.insertString( nOffset, VwExString.remove( strText, "\r"), m_defAttrSet );
    }
    else
    {
      if( !fIsUndoRedo ) // skip actual insert if this is from an undo/redo operation as text was already inserted by undo/redo manager
        super.insertString( nOffset, VwExString.remove( strText, "\r"), attrSet );
      doDocScan( nOffset, -1 );
    }
  }

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
    m_strWordDelimiters += m_strStartBlockComment + m_strEndBlockComment;

    if ( m_strAnyComment == null )
      m_strAnyComment = m_strStartBlockComment;
    else
    {
      for ( int x = 0; x < m_strStartBlockComment.length(); x++ )
      {
        if ( VwExString.isin( m_strStartBlockComment.charAt( x ), m_strAnyComment ) )
          continue;
        else
          m_strAnyComment += m_strStartBlockComment.charAt( x );
      }
    }

    for ( int x = 0; x < m_strStartBlockComment.length(); x++  )
    {
      if ( VwExString.isin( m_strEndBlockComment.charAt( x ), m_strAnyComment ) )
        continue;
      else
        m_strAnyComment += m_strEndBlockComment.charAt( x );

    }
  } // end setBlockComments()

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

    m_strWordDelimiters += m_strLineComment;

    if ( m_strAnyComment == null )
      m_strAnyComment = m_strLineComment;
    else
    {
      for ( int x = 0; x < m_strLineComment.length(); x++ )
      {
        if ( VwExString.isin( m_strLineComment.charAt( x ), m_strAnyComment ) )
          continue;
        else
          m_strAnyComment += m_strLineComment.charAt( x );
      }
    }


  } // end setSingleLineComments()




  /**
   * Find the first encountered new line character based on the direction indicator
   * @param nOffset The starting position in the document
   * @param nDirInd The direction indicator < 0 is backwards, > 0 is forwards
   * @return
   */
  private int findNewLine( int nOffset, int nDirInd ) throws BadLocationException
  {

    int nInc = (nDirInd < 0)? -1: 1;
    int nDocLen = getLength();

    while ( nOffset >= 0 && nOffset < nDocLen )
    {
      if ( getText( nOffset, 1).equals( "\n") )
        return nOffset;

      nOffset += nInc;
    }

    if ( nOffset < 0 )
      nOffset = 0;

    if ( nOffset >= nDocLen )
      nOffset = nDocLen;

    return nOffset;

  }

  
  /**
   * Find the first encountered new line character based on the direction indicator
   * @param nOffset The starting position in the document
   * @param nDirInd The direction indicator < 0 is backwards, > 0 is forwards
   * @return
   */
  private int findStartOfWord( int nOffset ) throws BadLocationException
  {

    int nNewOffset = nOffset;
    
    while ( nNewOffset >= 0  )
    {
      if ( VwExString.isin( getText( nNewOffset, 1).charAt( 0 ), m_strWordDelimiters ) )
        return ++nNewOffset;

      --nNewOffset;
    }

    if ( nNewOffset < 0 )
      nNewOffset = 0;


    return nNewOffset;

  }
  
  /**
   * Find the first encountered new line character based on the direction indicator
   * @param nOffset The starting position in the document
   * @param nDirInd The direction indicator < 0 is backwards, > 0 is forwards
   * @return
   */
  private int findEndOfWord( int nOffset ) throws BadLocationException
  {

    int nNewOffset = nOffset;
    int nLen = this.getLength();
    
    while ( nNewOffset < nLen  )
    {
      char ch = getText( nNewOffset, 1).charAt( 0 );
      
      if ( VwExString.isin( ch, m_strWordDelimiters ) )
        return --nNewOffset;

      ++nNewOffset;
    }

    if ( nNewOffset < 0 )
      nNewOffset = 0;


    return nNewOffset;

  }

  /**
   * Return the position of the next block comment - (either the begin or ending comment character sequence
   * moving in a backwards position from the start position or
   * -1 if no block comment found
   *
   * @param nOffset The starting offset
   * @param strCommentChar
   * @param strComment The start or ending block comment seq. i.e /* or *&amp\
   * @return
   * @throws BadLocationException
   */
  private int getBlockComment( int nOffset, String strCommentChar, String strComment )  throws BadLocationException
  {

    if ( strComment == null )
      return -1;

    int nPos = VwExString.findAny( strComment, strCommentChar, 0 );
    int ctr = 0;
    int nDocLen = getLength();

    if ( nPos < 0 )
      return -1;          // Not found

    String strTest = "";
    int nStartIndex = nOffset;

    for ( int x = 0; x < nPos; x++ )
    {
      --nStartIndex;
      ++ctr;

      if ( nOffset - ctr >= 0 )
        strTest += getText( nOffset - ctr, 1 );
    }

    for ( int x = nPos; x < strComment.length(); x++ )
    {
      if ( nOffset  < nDocLen )
        strTest += getText( nOffset++, 1 );

    }

    if ( strTest.equals( "**") )   // Possible Javadoc
    {
      if ( nStartIndex - 1 >= 0 )
        strTest = getText( nStartIndex - 1, 1 ) + strTest;
    }

    if ( strTest.startsWith(  strComment ) )
      return nOffset - strTest.length();

     return -1;

  } // end getBlockComment()


  /**
   * Getst the starting position of the line comment
   * @param nOffset
   * @return
   * @throws BadLocationException
   */
  private int getLineCommentStart( int nOffset )  throws BadLocationException
  {
    int nStart = nOffset;
    int nDocLen = getLength();

     while( nStart >= 0 )
     {
       char ch = getText( nStart, 1 ).charAt( 0 );

       if ( VwExString.isin( ch, m_strLineComment ) )
       {
         if ( nStart + m_strLineComment.length() >= nDocLen )
           return -1;

         if ( getText( nStart, m_strLineComment.length() ).equals( m_strLineComment ) )
         {
           if ( nStart - 1 >= 0 )
           {
             String strCh = getText( nStart - 1, 1 );
             if ( m_strEndBlockComment != null && m_strEndBlockComment.startsWith(  strCh ) )
             {
               --nStart;
               continue;
             }
           }
           return nStart;
         }
       }
       else
         return -1;

       --nStart;

    } // end while


    return -1;   // NotFound

  } // end getLineComment()


  /**
   * Do a single character scan
   *
   * @param nOffset Starting offset in the document
   * @param strText Text to hilite
   * @throws BadLocationException
   */
  private void doCharScan( int nOffset, String strText )  throws BadLocationException
  {
    // try to find a complete word by first moving back in the document, then forward until
    // white space, user defined delimiter or document end is found.

    int nDocLen = this.getLength();
    int nStartPos = nOffset;
    int nEndPos = nOffset + 1;

    if ( strText.length() == 0 )
      return;
    
    char chInsert = strText.charAt( 0 );

    if ( VwExString.isin( chInsert, " \n\t" ) )
    {
      if ( m_defAttrSet != null )
      {
        super.setCharacterAttributes( nOffset, 1, m_defAttrSet, true );
      }

      if ( nOffset - 1 > 0 )
      {
        chInsert = getText( nOffset - 1, 1 ).charAt( 0 );

        if ( VwExString.isin( chInsert, " \n\t" ) )
          return;

      }

      if ( nOffset + 1 < nDocLen )
      {
        chInsert = getText( nOffset + 1, 1 ).charAt( 0 );

        if ( VwExString.isin( chInsert, " \n\t" ) )
          return;
        else
        {
          if ( nOffset > 0 )
          {
            doCharScan( nOffset -1 , getText( nOffset - 1, 1 ) );
            doCharScan( nOffset + 1 , getText( nOffset + 1, 1 ) );
            return;
          }
        }

      } // end if

    } // end if


    String strWord = null;
    int nTokType = 0;

    switch ( nTokType = getCharType( chInsert, nOffset ) )
    {
       case VwTextParser.QSTRING:

            strWord = "'";
            break;

       case VwTextParser.LCOMMENT:

            strWord = "//";
            break;

      case VwTextParser.BCOMMENT:

           strWord = "/*";
           break;

      case VwTextParser.JAVADOC:

           strWord = "/**";
           break;

      case VwTextParser.WORD:
           break;

    } // end switch()

    if ( nTokType == SKIP )
      return;

    if ( VwExString.isin( chInsert, m_strWordDelimiters ) )
       ;
    else
    if ( nTokType == VwTextParser.WORD )
    {
      // Find start of word
      for ( ; nStartPos >= 0; nStartPos-- )
      {
        char ch = getText( nStartPos, 1 ).charAt(  0 );

        if ( VwExString.isin( ch, m_strWordDelimiters ) )
        {
          ++nStartPos;
          break;
        }

      } // end for

      // Find end of word

      for ( ; nEndPos < nDocLen; nEndPos++ )
      {
        char ch = getText( nEndPos, 1 ).charAt(  0 );

        if ( VwExString.isin( ch, m_strWordDelimiters ) )
          break;
      }

    } // end if (


    // Make sure we don't go out of bounds
    if ( nStartPos < 0 )
      nStartPos = 0;

    if ( nEndPos >= nDocLen )
      nEndPos = nDocLen;

    int nLen = nEndPos - nStartPos;

    if ( nLen <= 0 )
    {
      strWord = strText;
      nLen = 1;
    }

    if ( strWord == null )
      strWord = getText( nStartPos, nLen );

    AttributeSet attrSet = null;

    for ( int x = 0; x < m_aWordDesc.length; x++ )
    {
      attrSet = m_aWordDesc[ x ].getAttrSet(  strWord );

      if ( attrSet != null )
        break;

    }

    if ( attrSet != null )
    {
      super.setCharacterAttributes( nStartPos, nLen, attrSet, true );
    }
    else
    if ( m_defAttrSet != null)
    {
      super.setCharacterAttributes( nStartPos, nLen, m_defAttrSet, true );

    }
  } // end doCharScan()

  /**
   * Scan only for comment
   * @param nStartPos
   * @throws BadLocationException
   */
  private void doCommentScan( int nStartPos ) throws BadLocationException
  {
    String strText = getText( nStartPos, getLength() - nStartPos );
    try
    {
      if ( m_tp == null )
      {
        m_tp = new VwTextParser( new VwInputSource( strText ) );
        m_tp.setIncludeQuotes( true );
        m_tp.setDelimiters( m_strDelimiters.trim() );
        m_tp.setReturnWhitespace( true );
        
        if ( m_strStartBlockComment != null )
          m_tp.setBlockComment( m_strStartBlockComment, m_strEndBlockComment);

        if ( m_strLineComment != null )
          m_tp.setSingleLineComment( m_strLineComment );
      }
        
      else
        m_tp.setInput( new VwInputSource( strText ) );
    }
    catch( Exception ex )
    {
       ex.printStackTrace();
       return;
    }

    StringBuffer sbTokVal = new StringBuffer();

    while( true )
    {
      int nTokType = m_tp.getToken( sbTokVal );

      if ( nTokType == VwTextParser.EOF )
        return;

      int nCurPos = m_tp.getCursor() - sbTokVal.length() + nStartPos;

      setAttribute( nTokType, nCurPos, sbTokVal.toString() );

      switch( nTokType )
      {
        case VwTextParser.BCOMMENT:
        case VwTextParser.JAVADOC:

             return;

      }

    } // end while

  } // end endCommentScan

  /**
   * Scan document for syntax hiliting
   *
   * @param nStartPos The starting position in the document
   * @param nEndPos The ending position or -1 for absolute end of document
   */
  public void doDocScan( int nStartPos, int nEndPos ) throws BadLocationException
  {
    int nLen = 0;

    if ( nEndPos < 0 )
      nLen = getLength() - nStartPos;
    else
      nLen = nEndPos - nStartPos;

    String strText = getText( nStartPos, nLen );

    try
    {
      if ( m_tp == null )
      {

        m_tp = new VwTextParser( new VwInputSource( strText ) );
        m_tp.setIncludeQuotes( true );
        m_tp.setDelimiters( m_strDelimiters.trim() );
        m_tp.setReturnWhitespace( true );
        
        if ( m_strStartBlockComment != null )
          m_tp.setBlockComment( m_strStartBlockComment, m_strEndBlockComment);

        if ( m_strLineComment != null )
          m_tp.setSingleLineComment( m_strLineComment );

      }
      else
        m_tp.setInput( new VwInputSource( strText ) );
    }
    catch( Exception ex )
    {
       ex.printStackTrace();
       return;
    }

    int nCurPos = nStartPos;

    int nTokType = 0;
    StringBuffer sbTokVal = new StringBuffer();

    while ( (nTokType = m_tp.getToken( sbTokVal ) ) != VwTextParser.EOF )
    {
      nCurPos = m_tp.getCursor() - sbTokVal.length() + nStartPos;

      setAttribute( nTokType, nCurPos, sbTokVal.toString() );

    } // end while()

  } // end doDocScan()


  /**
   * Sets the color attribute based on token type and value
   * @param nTokType The type of token found
   * @param strWord The value of the token
   */
  private void setAttribute( int nTokType, int nCurPos, String strWord )
  {
    AttributeSet attrSet = null;
    int nAttrLen = strWord.length();

    switch( nTokType )
    {
      case VwTextParser.WORD:

           break;

      case VwTextParser.WSPACE:

           if ( m_defAttrSet != null )
             super.setCharacterAttributes( nCurPos, nAttrLen, m_defAttrSet, true );
           return;
           
      case VwTextParser.QSTRING:

           strWord = "'";
           break;

      case VwTextParser.LCOMMENT:

           strWord = "//";
           break;

      case VwTextParser.BCOMMENT:

           strWord = "/*";
           break;

      case VwTextParser.JAVADOC:

           strWord = "/**";
           break;


    } // end switch()

    for ( int x = 0; x < m_aWordDesc.length; x++ )
    {
      attrSet = m_aWordDesc[ x ].getAttrSet(  strWord );
      if ( attrSet != null )
        break;

    } // end for()

    if ( attrSet != null )
      super.setCharacterAttributes( nCurPos, nAttrLen, attrSet, true );
    else
    if ( m_defAttrSet != null )
    // Apply the default attributes
      super.setCharacterAttributes( nCurPos, nAttrLen, m_defAttrSet, true );

  } // end setAttribute()


  /**
   * Test to see if character is a delimiter
   * @param ch The character to test
   * @return
   */
  private int getCharType( char ch, int nPos ) throws BadLocationException
  {
    int nBlockType =  findBlock( ch, nPos );

    if ( nBlockType > 0 )
      return nBlockType;

    // Not in comment or quoted string
    return VwTextParser.WORD;

  } // end getCharType()



  /**
   * Determins if the current character is the start/end of or inside a comment or quote sequence sequence
   * @param ch The character just inserted into the buffer
   * @param nStartPos The position in the buffer of the character
   * @return One of the comment types if in a comment or zero if not
   * @throws BadLocationException
   */
  private int findBlock( char ch, int nStartPos ) throws BadLocationException
  {
    int nType = -1;

    getCommentPos( nStartPos );

    if ( m_strStartBlockComment != null && m_cp.m_strWord.equals( m_strStartBlockComment ) )
    {
      if (  m_cp.m_nStartLine < 0  )
      {
        doCommentScan( m_cp.m_nStartBlock );
        return SKIP;
      }

    }

    if ( m_strEndBlockComment != null && m_cp.m_strWord.equals( m_strEndBlockComment ) )
    {
      if ( m_cp.m_nStartLine < 0  )
      {
        doCommentScan( m_cp.m_nEndBlock + m_strEndBlockComment.length() );

        if ( isJavaDoc( m_cp.m_nStartBlock ))
          return VwTextParser.JAVADOC;
        else
          return VwTextParser.BCOMMENT;


      }
    }

    if ( m_strLineComment != null && m_cp.m_strWord.equals( m_strLineComment ) )
    {
      if ( m_cp.m_nStartBlock < 0  )
      {
        doCommentScan( m_cp.m_nStartLine );
        return SKIP;
      }
    }

    // Are we inside a line comment

    if ( m_cp.m_nStartLine >= 0 )
      return VwTextParser.LCOMMENT;
    else
    // Are we insside block comment
    if ( m_cp.m_nStartBlock >= 0 )
    {
      if ( m_cp.m_nEndBlock < 0 || (m_cp.m_nEndBlock >= 0 && nStartPos < m_cp.m_nEndBlock ) )
      {
        if ( isJavaDoc( m_cp.m_nStartBlock ) )
          return VwTextParser.JAVADOC;
        else
          return VwTextParser.BCOMMENT;

      }

    }

    if ( ch == '\'' || ch == '"' )
    {
      int nPosBegLine = -1;
      if ( insideQuote( nStartPos ))
        nPosBegLine = getQuotePos( nStartPos );
      else
        nPosBegLine = nStartPos;

      int nPosEndLine = findNewLine(nStartPos, 1 );

      doDocScan( nPosBegLine, nPosEndLine );
       return SKIP;
    }

    // Test to see if we're inside quotes
    if ( insideQuote( nStartPos ) )
      return VwTextParser.QSTRING;

    return nType;


  } // findBlock()


  /**
   * Get positions of the current comment positions
   * @param nStartPos The starting position in the document
   * @throws BadLocationException
   */
  private void getCommentPos( int nStartPos ) throws BadLocationException
  {
    int nLen = nStartPos;

    if ( m_strStartBlockComment != null )
      nLen += m_strStartBlockComment.length();
    else
    if ( m_strLineComment != null )
      nLen += m_strLineComment.length();

    if ( nLen > getLength() )
      nLen = getLength();

    String strBlock = getText( 0, nLen );

    int nLastPos = 0;
    int nStartNdx = 0;
    int nLastEndPos = 0;
    int nLastStartPos = -1;

    m_cp.m_nStartBlock = -1;
    m_cp.m_nEndBlock = -1;
    m_cp.m_nStartLine = -1;
    m_cp.m_strWord = null;

    if ( m_strStartBlockComment != null )
    {

      while ( true )
      {
        nLastPos = strBlock.indexOf( m_strStartBlockComment, nStartNdx );

        if ( nLastPos < 0 )
          break;

        nLastStartPos = nLastPos;

        nStartNdx = nLastPos + m_strStartBlockComment.length();

        if ( isJavaDoc( nLastPos ))
          ++nStartNdx;

        nLastEndPos = strBlock.indexOf( m_strEndBlockComment, nStartNdx );

        if ( nLastEndPos >= 0 )
        {
          nStartNdx = nLastEndPos + m_strEndBlockComment.length();
          m_cp.m_nEndBlock = nLastEndPos;

        }
      } // end while()

    }

    m_cp.m_nStartBlock = nLastStartPos;

    if ( m_cp.m_nStartBlock > m_cp.m_nEndBlock )
      m_cp.m_nEndBlock = -1;

    m_cp.m_nStartLine = getLineCommentPos( nStartPos );

    if ( m_cp.m_nStartBlock >= 0 )
    {
      if ( m_cp.m_nStartLine >= 0 && m_cp.m_nStartLine > m_cp.m_nStartBlock )
      {
        if ( m_cp.m_nEndBlock < 0 || ( m_cp.m_nEndBlock > 0 && m_cp.m_nStartLine < m_cp.m_nEndBlock ) )
          m_cp.m_nStartLine = -1;  // This line comment is inside a block comment, so turn it off

      }

    } // end if

    // Guard against false line comment (i.e., *// sequence )
    if ( m_cp.m_nStartLine >= 0 )
    {
      if ( m_cp.m_nEndBlock > 0  && nStartPos == m_cp.m_nEndBlock + m_strEndBlockComment.length() )
        m_cp.m_nStartLine = -1;   // False line comment
      else
      if ( m_cp.m_nEndBlock > 0  && m_cp.m_nStartLine == m_cp.m_nEndBlock + m_strEndBlockComment.length() - 1)
      {
        int nTestPos = m_cp.m_nEndBlock + m_strEndBlockComment.length();

        if ( nTestPos < nLen )
        {
          if ( getText( nTestPos, m_strLineComment.length() ).equals( m_strLineComment ) )
           m_cp.m_nStartLine = nTestPos;
        }
      }
    }

    // Test to see if block comments are inside a line comment
    if ( m_cp.m_nStartLine >= 0 && m_cp.m_nStartBlock >= 0 && m_cp.m_nStartLine < m_cp.m_nStartBlock ||
         m_cp.m_nStartLine >= 0 && m_cp.m_nEndBlock > 0 && m_cp.m_nStartLine > m_cp.m_nEndBlock  )
      m_cp.m_nStartBlock = m_cp.m_nEndBlock = -1;


    if ( m_cp.m_nStartBlock >= 0 )
    {
      nLen =  m_strStartBlockComment.length();
      boolean fIsJavaDoc = isJavaDoc( m_cp.m_nStartBlock );

      if ( fIsJavaDoc )
        ++nLen;

      if ( nStartPos >= m_cp.m_nStartBlock && nStartPos < m_cp.m_nStartBlock + nLen )
      {
        m_cp.m_strWord = m_strStartBlockComment;
        return;
      }

      if ( m_cp.m_nEndBlock >= 0 )
      {
        if ( nStartPos >= m_cp.m_nEndBlock && nStartPos < m_cp.m_nEndBlock + m_strEndBlockComment.length() )
        {
          m_cp.m_strWord = m_strEndBlockComment;
          return;
        }

      }

      if ( nStartPos > m_cp.m_nStartBlock && (m_cp.m_nEndBlock > 0 && nStartPos < m_cp.m_nEndBlock) || m_cp.m_nEndBlock < 0 )
        m_cp.m_strWord = "";    // We're in a comment, don't care

    }


    // If still null, not in a comment so get the word
    if ( m_cp.m_strWord == null )
    {
      if ( m_cp.m_nStartLine >= 0 && nStartPos >= m_cp.m_nStartLine && nStartPos < m_cp.m_nStartLine + m_strLineComment.length() )
      {
        m_cp.m_strWord = m_strLineComment;
        return;
      }
    }

    m_cp.m_strWord = "";

  } // end getCommentPos()

  /**
   * Tests to see if block comment is a JavaDoc
   * @param nStartPos
   * @return
   * @throws BadLocationException
   */
  private boolean isJavaDoc( int nStartPos ) throws BadLocationException
  {

    // This test gurards against the sequence /**/ which would falsely see the first 3 characters as javadoc
    if ( m_cp.m_nEndBlock > 0 && nStartPos + 3 >= m_cp.m_nEndBlock && nStartPos + 3 < m_strEndBlockComment.length() )
      return false;

    if ( nStartPos + 3 < getLength() )
    {
      if ( getText( nStartPos, 3 ).equals( "/**") )
        return true;
    }

    return false;

  } // end isJavaDoc()




  /**
   * Determins if we're inside a line comment givevn the current position in the document
   * @param nPos The starting position in the document to start the search
   * @return The position of the line comment or -1 if none found on the current line
   * @throws BadLocationException
   */
  private int getLineCommentPos( int nPos  ) throws BadLocationException
  {
    if ( m_strLineComment == null )
      return -1;

    int nStart = findNewLine( nPos, -1 );

    if ( nStart <= 0 )
      nStart = 0;
    else
      ++nStart;   // Bump past previous new line to start search


    int nLen = getLength();
    int nCommentLen = m_strLineComment.length();

    while ( nStart + nCommentLen - 1 < nLen )
    {
      String strCh = getText( nStart, 1 );
      if ( strCh.equals( "\"") || strCh.equals( "'"))
        return -1;

      if ( strCh.equals( "\n") )
        return -1;

      if ( m_strLineComment.startsWith( strCh ) )
      {
        if ( nStart + nCommentLen - 1 < nLen  )
        {
          String strTest = getText( nStart, nCommentLen );
          if ( strTest.equals( m_strLineComment ) )
            return nStart;

        }
      }

      ++nStart;
    }

    return -1;     // No Line Comment

  } // end getLineCommentPos

  /**
   * Determins if character is inside a quoted string
   * @param nStartPos The starting position to search
   * @return
   */
  private boolean insideQuote( int nStartPos ) throws BadLocationException
  {

    char ch = 0;
    int ndx = nStartPos;
    int nQuoteCount = 0;

    while( --ndx >= 0  )
    {
      ch = getText( ndx, 1 ).charAt( 0 );

      if ( ch == '\n' )
        break;

      if ( ch == '"' || ch == '\'')
        ++nQuoteCount;
    }

    // If we have an off number of quote characters, we're inside a quote
    if ( nQuoteCount % 2 > 0 )
      return true;

    return false;

  } // end insideQuote

  /**
   * Moving backwards, get the position of the next quote
   * @param nStart
   * @return
   * @throws BadLocationException
   */
  int getQuotePos( int nStart ) throws BadLocationException
  {
    while( --nStart >= 0  )
    {
      char ch = getText( nStart, 1 ).charAt( 0 );

      if ( ch == '\n' )
        break;

      if ( ch == '"' || ch == '\'')
        return nStart;
    }

    return -1;

  } // end getQuotePos()


} // end class VwStyledDocument{}

// *** End of VwStyledDocument.java ***

