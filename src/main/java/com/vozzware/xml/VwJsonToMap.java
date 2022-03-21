package com.vozzware.xml;

import com.vozzware.util.VwInputSource;
import com.vozzware.util.VwStack;
import com.vozzware.util.VwTextParser;
import org.xml.sax.InputSource;

import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

/*
============================================================================================

    Source File Name: Transaction.java

    Author:           petervosburgh
    
    Date Generated:   7/11/17

    Time Generated:   11:59 AM

============================================================================================
*/
public class VwJsonToMap
{
  private int BUFF_SIZE = 1024;

  private static enum eJsonState { INITIAL, DATA_READY, OBJ_NAME, ELEMENT_NAME, PROP_VALUE, ARRAY_START };
  private static enum eElementTypes { PRIMITIVE, PRIMITIVE_ARRAY, OBJECT_ARRAY, OBJECT, INVALID_JSON_TOKEN };

  private eJsonState m_eJsonstate = eJsonState.ELEMENT_NAME;

  public VwJsonToMap() throws Exception
  {
    super();

    //setDateFormat( "EEE MMM dd yyyy HH:mm:ss zzz" );
  }

  public Map<String,Object> deSerialize( String strJsonObject ) throws Exception
  {

    return parse( new InputSource( new StringReader( strJsonObject ) ) );

  }


  public Map<String,Object> parse( InputSource inpSrc ) throws Exception
  {
    Reader rdr = inpSrc.getCharacterStream();
    StringBuffer sb = new StringBuffer();
    char[] achChars = new char[ BUFF_SIZE ];

    Map<String,Object> mapCurrent = null;
    Map<String,Object> mapPrev = null;

    while( true )
    {
      int nGot = rdr.read( achChars, 0, BUFF_SIZE );
      sb.append( achChars, 0, nGot );

      if ( nGot < BUFF_SIZE )
        break;
    }

    VwTextParser tp = new VwTextParser( new VwInputSource( sb.toString() ) );
    tp.setDelimiters( "{}:,[]" );
    tp.setIgnoreQuotes( false );
    tp.setTreatSingleQuoteAsData( true );

    tp.setReturnWhitespace( true );

    tp.getToken( sb );
    int nObjLevel = 0;

    char chFirst = sb.charAt( 0 );

    if ( chFirst != '{' && chFirst != '[' )
      throw new Exception( "Invalid JSON String, must start with either '{' or '[' character");

    tp.setCursor( 0 ); //reset cursor to start of string

    VwStack<String> stack = new VwStack<String>();
    VwStack<Map<String,Object>> stackMaps = new VwStack();

    String strElementName = "";
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
      if ( strToken.equals( "{" ))  // This indicates and object
      {

        if ( mapCurrent != null )
        {
          ++nObjLevel;
          stack.push( strElementName );
          stackMaps.push( mapCurrent );
          mapPrev = mapCurrent;
        }

        mapCurrent = new HashMap<>( );

        // Only put on stack if its the initial map
        if ( nObjLevel == 0 )
        {
          stackMaps.push( mapCurrent );
        }
        else
        {
          mapPrev.put( strElementName, mapCurrent );
        }
        m_eJsonstate = eJsonState.ELEMENT_NAME;
      }
      else
      if ( strToken.equals( "}" ))   // end object definition
      {
        strElementName = stack.pop();
        mapCurrent = stackMaps.pop();
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
      {
        m_eJsonstate = eJsonState.ELEMENT_NAME;
      }
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
          {
            continue;
          }

          if ( eType == eElementTypes.PRIMITIVE_ARRAY )
          {
            //todo handlePrimitiveArray( strElementName, tp, listAttr );
            continue;
          }

          // ** This is for primitive data types only
          //todo m_saxHandler.startElement( "", strElementName, strElementName, listAttr );
          m_eJsonstate = eJsonState.PROP_VALUE;
        }
        else
        {
          char[] achData = strName.toCharArray();
          mapCurrent.put( strElementName, strName );

          m_eJsonstate = eJsonState.ELEMENT_NAME;

        } // end else

      } // end if

    } // end while

    return mapCurrent;

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
        throw new Exception( "Unexpected EOF in JSON string" );

      if ( sb.toString().equals( ":" ))
      {

        // Get the next token

        if ( tp.getToken( sb ) ==  VwTextParser.EOF )
          throw new Exception( "Unexpected EOF in JSON string" );

        if ( sb.length() == 0 )
          return eElementTypes.PRIMITIVE;

        char ch = sb.charAt( 0 );

        if ( ch  == '[' )
        {
          // For arrays, we need to determine if they are primitive or objects

          if ( tp.getToken( sb ) ==  VwTextParser.EOF )
            throw new Exception( "Unexpected EOF in JSON string" );

          if ( sb.charAt( 0 ) == '{')
          {
            return eElementTypes.OBJECT_ARRAY;
          }
          else
          {
            return eElementTypes.PRIMITIVE_ARRAY;
          }
        }
        else
        if ( ch  == '{' )
        {
          return eElementTypes.OBJECT;
        }
        else
        {
          return eElementTypes.PRIMITIVE;
        }
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
