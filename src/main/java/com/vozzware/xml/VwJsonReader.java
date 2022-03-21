package com.vozzware.xml;

import com.vozzware.util.VwInputSource;
import com.vozzware.util.VwStack;
import com.vozzware.util.VwTextParser;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.DefaultHandler;

import java.io.Reader;

public class VwJsonReader
{
  private DefaultHandler m_saxHandler;
  private int BUFF_SIZE = 1024;
  private String          m_strTopLevelClassName;

  private static enum eJsonState { INITIAL, DATA_READY, OBJ_NAME, ELEMENT_NAME, PROP_VALUE, ARRAY_START };
  private static enum eElementTypes { PRIMITIVE, PRIMITIVE_ARRAY, OBJECT_ARRAY, OBJECT, INVALID_JSON_TOKEN };

  private eJsonState m_eJsonstate = eJsonState.ELEMENT_NAME;

  public VwJsonReader( String strTopLevelClassName )
  {
    m_strTopLevelClassName = strTopLevelClassName;
  }
  public void setContentHandler( DefaultHandler saxHandler )
  { m_saxHandler = saxHandler; }
  
  public void parse( InputSource inpSrc ) throws Exception
  {
    Reader rdr = inpSrc.getCharacterStream();
    StringBuffer sb = new StringBuffer();
    char[] achChars = new char[ BUFF_SIZE ];
    AttributesImpl listAttr = new AttributesImpl( );

    int nGot = rdr.read( achChars, 0, BUFF_SIZE );

    while( nGot >= 0 )
    {
      sb.append( achChars, 0, nGot );
      
      nGot = rdr.read( achChars, 0, BUFF_SIZE );
    }


    if ( achChars.length == 0 )
    {
      throw new Exception( "Did not get any character input for Class Name: " + m_strTopLevelClassName );
      
    }

    VwTextParser tp = new VwTextParser( new VwInputSource( sb.toString() ) );
    tp.setDelimiters( "{}:,[]" );
    tp.setIgnoreQuotes( false );
    tp.setTreatSingleQuoteAsData( true );

    tp.setReturnWhitespace( true );
    m_saxHandler.startDocument();

    tp.getToken( sb );
    int nObjLevel = 0;

    char chFirst = sb.charAt( 0 );

    if ( chFirst != '{' && chFirst != '[' )
      throw new Exception( "Invalid JSON String, must start with either '{' or '[' character");
   
    tp.setCursor( 0 ); //reset cursor to start of string

    VwStack<String> stack = new VwStack<String>();
    stack.push( m_strTopLevelClassName );
    String strElementName = m_strTopLevelClassName;
    String strName = null;
    
    VwStack<String> stackCollections = new VwStack<String>();
    String strCollectionName = null;

    while( tp.getToken( sb ) != VwTextParser.EOF )
    {
      String strToken = sb.toString();
      strName = null;
      
      if ( strToken.equals( ":" ))
      {

        if ( m_eJsonstate != eJsonState.ELEMENT_NAME && m_eJsonstate != eJsonState.PROP_VALUE )
          throw new Exception( "Found token ':' but the parser was not in the expected ELEMENT NAME, ELEMENT VALUE State");

        m_eJsonstate = eJsonState.PROP_VALUE;
        continue;

      }

      if ( strToken.equals( "[" ))
      {
        if ( strCollectionName != null )
          stackCollections.push( strCollectionName );
        else
        {
          strCollectionName = strElementName;

          stackCollections.push( strCollectionName );
        }

        continue;
      }
      else
      if ( strToken.equals( "{" ))  // This indicates and object inside of an array
      {
        ++nObjLevel;
        stack.push( strElementName );

       /*  PBV 06/30/2013 Think this is a bug neew more testing
        if ( strCollectionName != null && !strCollectionName.equals( strElementName ))
        {
          m_saxHandler.startElement( "", strCollectionName, strCollectionName, listAttr );

        }
        else
       */
        m_saxHandler.startElement( "", strElementName, strElementName, listAttr );

        m_eJsonstate = eJsonState.ELEMENT_NAME;
      }
      else
      if ( strToken.equals( "}" ))   // end object definition
      {
        strElementName = stack.pop();
        m_saxHandler.endElement(  "", strElementName, strElementName );
        --nObjLevel;
      }
      else
      if ( strToken.equals( "]" ))   // end array
      {
        strCollectionName = stackCollections.pop( );
        m_eJsonstate = eJsonState.ELEMENT_NAME;
      }
      else
      if ( strToken.equals( "," ))
        m_eJsonstate = eJsonState.ELEMENT_NAME;
      else
      {
        strName = strToken; // remove double quotes surrounding JSON identifiers
        if ( strName.length() >0  )
        {
          char ch = strName.charAt( 0 );
          if ( ch == '"'  || ch == '\'')
          {
            strName = strName.substring( 1 );
            strName = strName.substring( 0, strName.length() -1  );

          }
        }
      }


      if ( strName != null  )
      {

        if ( strName.equals( "null"))
          strName = "";

        if ( m_eJsonstate == eJsonState.ELEMENT_NAME )
        {
          strElementName = strName;
          eElementTypes eType = getElementType( tp, strName );

          if ( eType == eElementTypes.OBJECT_ARRAY || eType == eElementTypes.OBJECT )
             continue;

          if ( eType == eElementTypes.PRIMITIVE_ARRAY )
          {
            handlePrimitiveArray( strElementName, tp, listAttr );
            continue;
          }

          // ** This is for primitive data types only
          m_saxHandler.startElement( "", strElementName, strElementName, listAttr );
          m_eJsonstate = eJsonState.PROP_VALUE;
        }
        else
        {
          char[] achData = strName.toCharArray();
          m_saxHandler.characters( achData, 0, achData.length );
          m_saxHandler.endElement(  "", strElementName, strElementName );

          m_eJsonstate = eJsonState.ELEMENT_NAME;

        } // end else
        
      } // end if 
      
    } // end while
    
    m_saxHandler.endDocument();

  } // end parse()


  /**
   * Handle a primitive array
   * @param strElementName
   * @param tp
   */
  private void handlePrimitiveArray( String strElementName, VwTextParser tp, AttributesImpl listAttr ) throws Exception
  {
    int nCursor = tp.getCursor() + 2;  // bump cursor past the array character to point to the first element
    tp.setCursor(  nCursor );

    StringBuffer sb = new StringBuffer();

    m_saxHandler.startElement( "", strElementName, strElementName, listAttr ); // add the array name

    while( tp.getToken( sb ) != VwTextParser.EOF )
    {
      String strToken = sb.toString();
      String strName = null;

      if ( strToken.equals( ":" ))
      {

        if ( m_eJsonstate != eJsonState.ELEMENT_NAME && m_eJsonstate != eJsonState.PROP_VALUE )
          throw new Exception( "Found token ':' but the parser was not in the expected ELEMENT NAME, ELEMENT VALUE State");

        m_eJsonstate = eJsonState.PROP_VALUE;
        continue;

      }

      if ( strToken.equals( "]" ))
      {
        m_saxHandler.endElement(  "", strElementName, strElementName );
        return;

      }
      if ( strToken.equals( "," ))
      {
        m_eJsonstate = eJsonState.PROP_VALUE;
        continue;

      }
      else
      {
        strName = strToken; // remove double quotes surrounding JSON identifiers
        char ch = strName.charAt( 0 );
        if ( ch == '"'  || ch == '\'')
        {
          strName = strName.substring( 1 );
          strName = strName.substring( 0, strName.length() -1  );

        }

        m_saxHandler.startElement( "", "value", "value", listAttr ); // add the array name
        char[] achData = strName.toCharArray();
        m_saxHandler.characters( achData, 0, achData.length );
        m_saxHandler.endElement(  "", "value", "value" );


      }
    }

  }


  private eElementTypes getElementType( VwTextParser tp, String strElementName ) throws Exception
  {
    if ( m_eJsonstate != eJsonState.ELEMENT_NAME  )
      throw new Exception( "The parser state must be in ELEMENT_NAME state in order to determine its data type");

    int nCursor = tp.getCursor();  // save current position

    try
    {
      StringBuffer sb = new StringBuffer(  );

      // Get next token

      if ( tp.getToken( sb ) ==  VwTextParser.EOF )
        throw new Exception ( "Unexpected EOF in JSON string" );

      if ( sb.toString().equals( ":" ))
      {

        // Get the next token

        if ( tp.getToken( sb ) ==  VwTextParser.EOF )
          throw new Exception ( "Unexpected EOF in JSON string" );

        if ( sb.length() == 0 )
          return eElementTypes.PRIMITIVE;

        char ch = sb.charAt( 0 );

        if ( ch  == '[' )
        {
          // For arrays, we need to determine if they are primitive or objects

          if ( tp.getToken( sb ) ==  VwTextParser.EOF )
            throw new Exception ( "Unexpected EOF in JSON string" );

          if ( sb.charAt( 0 ) == '{')
            return eElementTypes.OBJECT_ARRAY;
          else
           return eElementTypes.PRIMITIVE_ARRAY;
        }
        else
        if ( ch  == '{' )
          return eElementTypes.OBJECT;
        else
          return eElementTypes.PRIMITIVE;
      }

      throw new Exception( "Invalid token found following " + strElementName );

    }
    finally
    {
      // Restore cursor
      tp.setCursor( nCursor );
    }
  }

}

