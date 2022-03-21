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
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;

import java.io.File;
import java.io.FileWriter;

/**
 * This class formats an XML document
 */
public class VwXmlWriter
{
  private StringBuffer    m_sbXml = new StringBuffer( 2048 );         // Holds the xml document

  private StringBuffer    m_sbEscape = new StringBuffer( 512 );       // For escaping characters

  private StringBuffer    m_sbTag = new StringBuffer( 256 );          // For formatting tags

  private StringBuffer    m_sbIndent;                                 // For Indentation

  private boolean         m_fFormatted = false;  // If true adds cr/lf and indentation to the xml document

  private int             m_nLevel = 0;          // Parentage level for indentation

  private String          m_strIndent = "";      // For well formatted xml documents


  /**
   * Default Constructor for specifying no formatting
   */
  public VwXmlWriter()
  { ; }

  /**
   * Constructor for specifying formatting option and the starting level of parentage (Indentation)
   *
   * @param nIndentLevel The starting indentation level. -1 is the defualt to start
   * with no indentation. Each level number equates to two spaces.
   */
  public VwXmlWriter( boolean fFormatted, int nIndentLevel )
  {
    m_fFormatted = fFormatted;

    if ( m_fFormatted )
    {
      setFormattedOutput( true );
      setLevel( nIndentLevel );
    }

  } // end VwXmlWriter()

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
	      m_sbIndent.append( ' ' );
    } // end if
    
  }// end setFormattedOutput()

  /**
   * Sets the initial indentation level to start at for well formatted documents.<br> If this object
   * was created with fWellFormatted set to false, then this method will have no affect on the
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
  { m_sbXml.setLength( 0 ); }


  /**
   * Returns the formatted xml string
   */
  public String getXml()
  { return m_sbXml.toString(); }


  /**
   * Writes the xml to the File object specified
   * 
   * @param fileToWrite The file to write the xml contents to
   * @throws Exception if any disk write exceptions occur
   */
  public void write( File fileToWrite ) throws Exception
  {
    FileWriter fw = new FileWriter( fileToWrite );
    fw.write( getXml() );
    fw.close();
    
  } // end write()
  /**
   * Returns true if the formmatting flag is on
   */
  public boolean isFormatted()
  { return m_fFormatted; }

  /**
   * Adds a parent xml tag with no attributes
   *
   * @param strElementName The xml element tag
   *
   */
  public void addParent( String strElementName )
  { addParent( strElementName, null ); }


  /**
   * Adds a parent xml tag with optional attributes
   *
   * @param strElementName The xml element tag
   * @param listAttr  A Listof any element attributes
   *
   */
  public void addParent( String strElementName, Attributes listAttr )
  {
    String strXml = formatOpenElementTag( strElementName, listAttr );

    if ( m_fFormatted )
    {
      if ( m_sbXml.length() > 0 )
        m_sbXml.append( "\r\n" );

      m_sbXml.append( m_strIndent );
      m_sbXml.append( strXml );

      ++m_nLevel;

      m_strIndent = getIndentation();

    } // end if ( m_fFormatted )
    else
      m_sbXml.append( strXml );

  } // end addParent


  /**
   * Adds a new xml element with no attributes
   *
   * @param strElementName The xml element tag
   * @param strData Any Associated data with the tag. NOTE if strData is null the element
   * @param mapAttributes  A map of any element attributes
   */
  public void addChild( String strElementName, String strData )
  { addChild( strElementName, strData, null, false ); }


  /**
   * Adds a new xml element with an optional attribute list.
   *
   * @param strElementName The xml element tag
   * @param strData Any Associated data with the tag. NOTE if strData is null the element
   * @param mapAttributes  A map of any element attributes
   */
  public void addChild( String strElementName, String strData,
                        Attributes listAttributes )
  { addChild( strElementName, strData, listAttributes, false ); }


  /**
   * Adds a new xml element with an optional attribute list.
   *
   * @param strElementName The xml element tag
   * @param strData Any Associated data with the tag. NOTE if strData is null the element
   * @param mapAttributes  A map of any element attributes
   * @param fSupressClosingTag Don't add closing tag if true - this is for mixed content objects
   */
  public void addChild( String strElementName, String strData,
                        Attributes listAttributes, boolean fSupressClosingTag )
  {

    String strXmlTag = null;

    if ( strData != null )
    {
      // test for any xml characters in the data that need to be escaped

      if ( VwExString.findAny( strData, "<&\n", 0 ) >= 0 )
        strData = "<![CDATA[" + strData + "]]>";

      strXmlTag = formatOpenElementTag( strElementName, listAttributes )
                  + strData;

      if ( ! fSupressClosingTag )
       strXmlTag += formatCloseElementTag( strElementName );
    }
    else
      strXmlTag = formatAttributeOnlyTag( strElementName, listAttributes );

    if ( m_fFormatted )
    {

      if ( m_sbXml.length() > 0 )
        m_sbXml.append( "\r\n" );

      m_sbXml.append( m_strIndent );
      m_sbXml.append( strXmlTag );

      if ( fSupressClosingTag )
        ++m_nLevel;
    }
    else
      m_sbXml.append( strXmlTag );

  } // end addChild


  public void addElement( Element element )
  {
    String strName = element.getTagName();
    NamedNodeMap attrs = element.getAttributes();
    boolean fHasChildren = element.hasChildNodes();
    String strIndentation = "";
      
    if ( m_fFormatted )
    {
      if ( m_sbXml.length() > 0 )
        m_sbXml.append( "\r\n" );

      strIndentation = getIndentation();
    }
    
    m_sbXml.append( strIndentation ).append( "<" ).append( strName );
      
    if ( attrs.getLength() > 0 )
    {
      int nLen = attrs.getLength();
      
      for ( int x = 0; x < nLen; x++ )
      {
        m_sbXml.append( " " );
        
        m_sbXml.append( attrs.item( x ).getNodeName() ).append( "=\"" ).append( attrs.item( x ).getNodeValue() ).append( "\"");
        
      }
    }
      
    if ( !fHasChildren )
    {
      m_sbXml.append( "/>");
      return;
    }
      
    m_sbXml.append( ">" );
    NodeList nl = element.getChildNodes();
    
    int nlLen = nl.getLength();
    
    short sNodeType = 0;
    for ( int x = 0; x < nlLen; x++ )
    {
      
      Node objNode = nl.item( x );
      sNodeType = objNode.getNodeType();
      
      if ( sNodeType == Node.TEXT_NODE )
         m_sbXml.append( objNode.getNodeValue() );
      else
      if ( sNodeType == Node.ELEMENT_NODE ) 
      {
        ++m_nLevel;

        addElement ( (Element)objNode );
        --m_nLevel;
        
      } // end if
    }
    
    if ( m_fFormatted && sNodeType != Node.TEXT_NODE )
      m_sbXml.append( "\r\n").append( getIndentation() );
    
    m_sbXml.append( "</" ).append( strName ).append( ">");

      
  } // end addElement()
  
  /**
   * Adds an existing xml string at the current location
   *
   * @param strXml The xml string to add to the one being built in this object. The
   * string is added at the current location.
   */
  public void addXml( String strXml )
  {
    if ( m_fFormatted )
    {
      if ( m_sbXml.length() > 0 )
        m_sbXml.append( "\r\n" );

      m_sbXml.append(  strXml );

    }
    else
      m_sbXml.append( strXml );

  } // end add()


  /**
   * Adds a closing parent xml tag
   *
   * @param strElementName The element name of the closing tag
   *
   */
  public void closeParent( String strElementName )
  {
    
    if ( strElementName == null )
    {
      m_sbXml = m_sbXml.deleteCharAt( m_sbXml.length() -1 );
      m_sbXml.append( "/>" );
      
    }
    if ( m_fFormatted )
    {
      --m_nLevel;
      
      m_strIndent = getIndentation();

      if ( strElementName == null )
        return;
      
      m_sbXml.append( "\r\n" );
      m_sbXml.append( m_strIndent );
      m_sbXml.append( formatCloseElementTag( strElementName ) );

    }
    else
    if ( strElementName != null )
      m_sbXml.append( formatCloseElementTag( strElementName ) );


  } // end addCloseTag()


  /**
   * Formats an open element tag with attributtes if specified
   *
   * @param strElementName The name of the xml tag
   * @param listAttributes The attribute List of VwAttribute objects ( may be null )
   *
   * @return a String containing the formateed xml tag
   *
   */
  private String formatOpenElementTag( String strElementName, Attributes listAttributes )
  {

    if ( strElementName == null )
      return "";

    m_sbTag.setLength( 0 );
    m_sbTag.append( "<" );
    m_sbTag.append( strElementName );

    if ( listAttributes != null )
    {
      for ( int x = 0; x < listAttributes.getLength(); x++ )
      {
        m_sbTag.append( " " );
        m_sbTag.append( listAttributes.getQName( x ));
        m_sbTag.append( "=\"" );
        m_sbTag.append( escapeAttrData( listAttributes.getValue( x ) ) );
        m_sbTag.append( "\"" );

      } // end for()

    } // end if listAttributes != null )

    // Add in closing bracket

    m_sbTag.append( ">" );

    return m_sbTag.toString();

  } // end formatOpenElementTag()


  /**
   * Escape out any '&' and '<' characters found in the attribute value
   * @param strValue The attribute value
   * @return
   */
  private String escapeAttrData( String strValue )
  {
    if ( VwExString.findAny( strValue, "<&", 0 ) < 0 )
      return strValue;
    
    StringBuffer sb = new StringBuffer();
    int nLen = strValue.length();
    
    for ( int x = 0; x < nLen; x++)
    {
      char ch = strValue.charAt( x );
      
      if ( ch == '&')
      {
        sb.append( "&amp;" );
        continue;
      }
      else
      if ( ch == '<')
      {
        sb.append( "&lt;" );
        continue;
      }
      else
      sb.append( ch );
      
    }
    
    return sb.toString();
  }

  /**
   * Formats an xml tag that only has attributes
   *
   * @param strElementName The name of the xml tag
   * @param listAttributes The attribute List of VwAttribute objects ( may be null )
   *
   * @return a String containing the formateed xml tag
   *
   */
  private String formatAttributeOnlyTag( String strElementName, Attributes listAttributes )
  {

    if ( strElementName == null )
      return "";

    m_sbTag.setLength( 0 );

    m_sbTag.append( "<" );
    m_sbTag.append( strElementName );

    if ( listAttributes != null )
    {
      for ( int x = 0; x < listAttributes.getLength(); x++ )
      {
        m_sbTag.append( " " );
        m_sbTag.append( listAttributes.getQName( x ) );
        m_sbTag.append( "=\"" );
        m_sbTag.append( escapeAttrData( listAttributes.getValue( x ) ) );
        m_sbTag.append( "\"" );

      } // end for()

    } // end if listAttributes != null )

    // Add in closing bracket

    m_sbTag.append( "/>" );

    return m_sbTag.toString();

  } // end formatOpenElementTag()

  /**
   * Formats a close element tag
   *
   * @param strElementName The name of the xml tag
   *
   * @return a String containing the formateed xml tag
   *
   */
  private String formatCloseElementTag( String strElementName )
  {
    if ( strElementName == null )
      return "";
    
    return "</" + strElementName + ">";
  }


  /**
   * Formats a String with spaces according to the m_nLevel value
   */
  public String getIndentation()
  {
    if ( m_nLevel < 0 )
      return "";

    int nSpaces = m_nLevel * 2;

    int nsbLen = m_sbIndent.length();

    if ( nSpaces > nsbLen )
    {
      int nNeed = nSpaces - nsbLen;

      for ( int x = 0; x < nNeed; x++ )
        m_sbIndent.append( ' ' );
    }

    return m_sbIndent.substring( 0, nSpaces );

  } // end getIndentation



} // end class VwXmlWriter{}


// *** End of VwXmlWriter.java ***
