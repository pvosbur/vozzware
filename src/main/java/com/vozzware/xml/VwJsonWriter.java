/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwXmlWriter.java

Create Date: Apr 11, 2006
============================================================================================
*/

package com.vozzware.xml;

import com.vozzware.util.VwExString;

import java.io.OutputStream;
import java.lang.reflect.Array;

/**
 * This class Formats Json from objects or maps
 */
public class VwJsonWriter
{
  private StringBuffer m_sbJson = new StringBuffer( 2048 );         // Holds the xml document

  private StringBuffer    m_sbIndent;                                 // For Indentation

  private boolean         m_fFormatted = false;  // If true adds cr/lf and indentation to the xml document

  private int             m_nLevel = 0;          // Parentage level for indentation

  private String          m_strIndent = "";      // For well formatted xml documents

  private boolean         m_fCapsOnFirstCgaracter;

  /**
   * Default Constructor for specifying no formatting
   */
  public VwJsonWriter()
  {
    ;
  }

  /**
   * Constructor for specifying formatting option and the starting level of parentage (Indentation)
   *
   * @param nIndentLevel The starting indentation level. -1 is the defualt to start
   * with no indentation. Each level number equates to two spaces.
   */
  public VwJsonWriter( boolean fFormatted, int nIndentLevel )
  {
    this();

    m_fFormatted = fFormatted;

    if ( m_fFormatted )
    {
      setFormattedOutput( true );
      setLevel( nIndentLevel );
    }


  } // end VwXmlWriter()

  /**
   * Id set then capitslize first character in property names
   * @param fCapsOnFirstCgaracter
   */
  public void setCapsOnForstCharacter( boolean fCapsOnFirstCgaracter )
  {
    m_fCapsOnFirstCgaracter = fCapsOnFirstCgaracter;
  }

  /**
   * If true, format the output
   * @param fFormatOutput true to format the output, false to disable formatting
   */
  public void setFormattedOutput( boolean fFormatOutput )
  { 
    m_fFormatted = fFormatOutput; 
    
    if ( m_sbIndent == null && fFormatOutput )
    {
	    m_sbIndent = new StringBuffer( 128 );
	
	    for ( int x = 0; x < 128; x++ )
      {
        m_sbIndent.append( ' ' );
      }

    } // end if
    
  }// end setFormattedOutput()

  /**
   * Sets the initial indentation level to start at for well formatted Json.<br>
   * This option can be useful for debugging purposes.  If this object
   * was created with fWellFormatted set to false,<br></br> then this method will have no affect on the
   * output.<br> The default setting is -1.
   */
  public void setLevel( int nLevel )
  {
    m_nLevel = nLevel;
    m_strIndent = getIndentation();

  } // end setLevel()


  /**
   * Gets the current level of indentation
   */
  public int getLevel()
  { return m_nLevel; }


  /**
   * Restes the internal xml string to an empty string so this object can be reused to
   * reformat a new xml document.
   */
  public void clear()
  { m_sbJson.setLength( 0 ); }


  /**
   * Returns the formatted xml string
   */
  public String getJson()
  { return m_sbJson.toString(); }


  /**
   * Writes the Json object to the specified output stream
   * 
   * @param outs The output stream to write the Json object to
   * @throws Exception if any write exceptions occur
   */
  public void write( OutputStream outs ) throws Exception
  {
    String strJson = getJson();
    outs.write( strJson.getBytes() );

  } // end write()


  /**
   * Returns true if the formatting flag is on
   */
  public boolean isFormatted()
  { return m_fFormatted; }


  /**
   * Adds a '{' to  jason string
   *
   *
   */
  public void beginObject()
  { beginObject( null ); }


  /**
   * Adds a { jason begin object
   *
   * @param strObjectName The json object name if not null will add the objJect: to the json string
   *
   */
  public void beginObject( String strObjectName )
  {

    if ( m_fFormatted )
    {
      if ( m_sbJson.length() > 0 )
      {
        m_sbJson.append( "\r\n" );
      }

      m_sbJson.append( m_strIndent );
      ++m_nLevel;

      m_strIndent = getIndentation();

    } // end if ( m_fFormatted )

    checkAddComma();
    if ( strObjectName != null )
    {
      m_sbJson.append( "\"" ).append( strObjectName ).append( "\":" );
    }

    m_sbJson.append( "{" );


  } // end beginObject


  /**
   * Ends Json object definition
   */
  public void endObject()
  {
    if ( m_fFormatted )
    {
      if ( m_sbJson.length() > 0 )
        m_sbJson.append( "\r\n" );

      m_sbJson.append( m_strIndent );
      --m_nLevel;

      m_strIndent = getIndentation();

    } // end if ( m_fFormatted )

    m_sbJson.append( "}" );

  }


  /**
   * Makes an un named primitive array
   * @param aValues Array of valyes
   * @param fNeedValueQuotes
   */
  public void makePrimitiveArray( Object aValues, boolean fNeedValueQuotes )
  {
    m_sbJson.append( "[");
    int nArrLen = Array.getLength( aValues );

    for ( int x = 0; x < nArrLen; x++  )
    {

      if ( x > 0 )
      {
        m_sbJson.append( "," );
      }

      if ( fNeedValueQuotes )
      {
        m_sbJson.append( "\"" );
      }


      m_sbJson.append( getArrayValue( aValues, x ) );

      if ( fNeedValueQuotes )
      {
        m_sbJson.append( "\"" );
      }

    }

    m_sbJson.append( "]" );

  }


  /**
   * Adds an array of primitive values ie.e Strings, numbers, booleans
   *
   * @param strArrayName The property name of the array
   * @param aValues The array of primitive values
   */
  public void addArray( String strArrayName, Object aValues, boolean fNeedValueQuotes )
  {

    if ( m_fFormatted )
    {
      if ( m_sbJson.length() > 0 )
      {
        m_sbJson.append( "\r\n" );
      }

      m_sbJson.append( m_strIndent );
      ++m_nLevel;

      m_strIndent = getIndentation();

    } // end if ( m_fFormatted )

    if ( m_sbJson.charAt( m_sbJson.length()  - 1  ) != '{' )
    {
      m_sbJson.append( "," );
    }

    if ( m_fCapsOnFirstCgaracter )
    {
      strArrayName =capsOnFirstChar( strArrayName );

    }

    m_sbJson.append( "\"" ).append( strArrayName ).append( "\":" ).append( "[" );

    int nArrLen = Array.getLength( aValues );

    for ( int x = 0; x < nArrLen; x++  )
    {

      if ( x > 0 )
      {
        m_sbJson.append( "," );
      }

      if ( fNeedValueQuotes )
      {
        m_sbJson.append( "\"" );
      }


      m_sbJson.append( getArrayValue( aValues, x ) );

      if ( fNeedValueQuotes )
      {
        m_sbJson.append( "\"" );
      }

    }

    m_sbJson.append( "]" );

  } // end beginObject


  /**
   * Get the value oan an array element
   *
   *
   * @param array  The array object
   * @param ndx The index of the array to the the value for
   * @return
   */
  private String getArrayValue( Object array, int ndx )
  {
    Object objVal = null;

    objVal = Array.get( array, ndx );

    if ( objVal == null )
    {
      return "";
    }

    return objVal.toString();

  }


  /**
   * Begin a JSON array definition i.e. the '[' character
   */
  public void beginArray()
  { beginArray( null ); }


  /**
   *  Begin a JSON array def with a name   i.e. "arrayName":[
   * @param strArrayName
   */
  public void beginArray( String strArrayName )
  {

    if ( m_fFormatted )
    {
      if ( m_sbJson.length() > 0 )
      {
        m_sbJson.append( "\r\n" );
      }

      m_sbJson.append( m_strIndent );
      --m_nLevel;

      m_strIndent = getIndentation();

    } // end if ( m_fFormatted )

    checkAddComma();

    if ( strArrayName != null )
    {
      if ( m_fCapsOnFirstCgaracter )
      {
        strArrayName = capsOnFirstChar( strArrayName );
      }

      m_sbJson.append( "\"" ).append( strArrayName ).append( "\":" );
    }

    m_sbJson.append( "[" );
  }
  /**
   * Ends Json array definition
   */
  public void endArray()
  {
    if ( m_fFormatted )
    {
      if ( m_sbJson.length() > 0 )
      {
        m_sbJson.append( "\r\n" );
      }

      m_sbJson.append( m_strIndent );
      --m_nLevel;

      m_strIndent = getIndentation();

    } // end if ( m_fFormatted )

    m_sbJson.append( "]" );

  }

  public void addComma()
  {
    m_sbJson.append( "," );
  }

  /**
   * Adds a new property
   *
   * @param strPropName  The property name
   * @param strPropValue The property value
   * @param fNeedValueQuotes  if true, surround the property value in quotes
   */
  public void addProperty( String strPropName, String strPropValue, boolean fNeedValueQuotes )
  {

    checkAddComma();

    if ( m_fFormatted )
    {

      if ( m_sbJson.length() > 0 )
      {
        m_sbJson.append( "\r\n" );
      }

      m_sbJson.append( m_strIndent );

    }

    if ( m_fCapsOnFirstCgaracter )
    {
      strPropName = capsOnFirstChar( strPropName );

    }
    m_sbJson.append( "\"" ).append( strPropName ).append( "\":" );


    // *** Prop value will be null if the property is the start of an array or new object
    if ( strPropValue != null )
    {
      if ( fNeedValueQuotes )
      {
        m_sbJson.append( "\"" );
      }

      m_sbJson.append( doEscapeCheck( strPropValue ) );

      if ( fNeedValueQuotes )
      {
        m_sbJson.append( "\"" );
      }
    }

  } // end addChild

  /**
   * Capitalize first character of string
   * @param strName The property name to cap first char on
   * @return
   */
  private String capsOnFirstChar( String strName )
  {

    return Character.toUpperCase( strName.charAt( 0 ) ) + strName.substring( 1 ) ;
  }


  /**
   * Check for any single or double quote characters in the data and escape them out
   * @param strPropValue
   * @return
   */
  private String doEscapeCheck( String strPropValue )
  {
    String strVal = strPropValue;

    // Escape any standalone backslash '\' chars
    if ( strVal.indexOf( "\\" ) >= 0 )
    {
      strVal = VwExString.replace( strVal, "\\", "\\\\");
    }

    //Escape any double quote chars '"'
    if ( strVal.indexOf( "\"" ) >= 0 )
    {
      strVal = VwExString.replace( strVal, "\"", "\\\"" );
    }


    return strVal;
  }


  /**
   * Formats a String with spaces according to the m_nLevel value
   */
  public String getIndentation()
  {
    if ( m_nLevel < 0 )
    {
      return "";
    }

    int nSpaces = m_nLevel * 2;

    int nsbLen = m_sbIndent.length();

    if ( nSpaces > nsbLen )
    {
      int nNeed = nSpaces - nsbLen;

      for ( int x = 0; x < nNeed; x++ )
      {
        m_sbIndent.append( ' ' );
      }
    }

    return m_sbIndent.substring( 0, nSpaces );

  } // end getIndentation


  public String toString()
  { return getJson(); }


  /**
   * Check to see if comma is needed and add it
   */
  private void checkAddComma()
  {
    if ( m_sbJson.length() == 0 )
    {
      return;
    }

    char chLast =  m_sbJson.charAt( m_sbJson.length()  - 1  );

    if ( chLast != '{' && chLast != '[' )
    {
      m_sbJson.append( "," );
    }

  }

} // end class VwJsonWriter{}


// *** End of VwJsonlWriter.java ***
