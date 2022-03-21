/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwDtdParser.java

Create Date: Apr 11, 2006
============================================================================================
*/
package com.vozzware.xml.dtd;


import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.DefaultHandler;

import java.io.File;
import java.io.StringReader;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

//import javax.xml.parsers.SAXParser;


/**
 * This utility class converts certain requested schema types into java classes that hold
 * a parsed xml document instance as well as java classes that build xml documents from
 * defined data sources.
 *
 */
public class VwDtdParser extends DefaultHandler implements DeclHandler, LexicalHandler
{
  private Map m_mapPrimTypes = new HashMap ();    // Scheam primitive typs to Java maps map

  private URL m_urlDtdFile;                       // The name of the dtd file to parse

  private File m_fileOptions = null;              // XML file of additional options if not null

  private XMLReader m_parser;                     // SAX2 parser

  private Map m_mapElements = new HashMap ();     // Map of VwDtdElementDecl objects

  private Map m_mapAttributes = new HashMap ();

  private Map m_mapInternalEntities = new HashMap ();

  private Map m_mapExternalEntities = new HashMap ();

  private boolean m_fDoLexical = false;           // Include lexical callbacks

  private boolean m_fMaintainObjectList = false;  // Mantain list of objects as they appear

  private List m_objOrderList = new LinkedList ();


  /**
   * Constructor
   */
  public VwDtdParser( URL urlDtdFile, File fileOptions )
  {
    m_urlDtdFile = urlDtdFile;
    m_fileOptions = fileOptions;

  } // end VwDtdParser


  /**
   * Turns On/Off lexical processing -- used mostly for gathering comments
   *
   * @param fDoLexical
   */
  public void setLexicalProcessing( boolean fDoLexical )
  {
    m_fDoLexical = fDoLexical;
  }


  /**
   * Turns On/Off maintaing object list in the oreder they are parsed
   *
   * @param fMaintainObjectList
   */
  public void setMaintainObjectList( boolean fMaintainObjectList )
  {
    m_fMaintainObjectList = fMaintainObjectList;
  }


  /**
   * Returns the the object list of parsed entities in order if teh maintainObjectList property
   * was set to true
   *
   * @return The object list
   */
  public List getObjectList()
  {
    return m_objOrderList;
  }


  /**
   * Process the named schema acordcing to the options
   */
  public void process() throws Exception
  {
    //m_parser = new SAXParser ();
    m_parser = (XMLReader)Class.forName("org.apache.xerces.parsers.SAXParser").newInstance();

    m_parser.setProperty ( "http://xml.org/sax/properties/declaration-handler", this );

    if ( m_fDoLexical )
    {
      m_parser.setProperty ( "http://xml.org/sax/properties/lexical-handler", this );
      m_parser.setContentHandler ( this );
    }

    // Create and empty document that references the DTD that we are parsing
    String strDtd = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><!DOCTYPE DtdDocument SYSTEM \"" +
                    m_urlDtdFile.toString() + "\"><DtdDocument/>";
    // Create input source for parsing
    InputSource ins = new InputSource ( new StringReader ( strDtd ) );
    m_parser.parse ( ins );

  } // end process()


  /**
   * Return the Element map - a map of VwDtdElementDecl objects
   */
  public Map getElements()
  {
    return m_mapElements;
  }


  /**
   * Return the Attribute map - This is a map which is contains a list of VwDtdAttributeDecl
   * objects by element name (the map key)
   */
  public Map getAttributes()
  {
    return m_mapAttributes;
  }


  /**
   * Return a map of internal entities (key is entity name)
   */
  public Map getInternalEntities()
  {
    return m_mapInternalEntities;
  }


  /**
   * Return a map of external entities (key is entity name)
   */
  public Map getExternalEntities()
  {
    return m_mapExternalEntities;
  }

  /**
   * AttributeDecl Store list of attributes (as VwDtdAttributeDecl objects ) by element name
   *
   * @param strElementName
   * @param strAttrName
   * @param strType
   * @param strDefValue
   * @param strValue
   * @exception SAXException
   */
  public void attributeDecl( String strElementName, String strAttrName, String strType,
                             String strDefValue, String strValue ) throws SAXException
  {
    VwDtdAttributeDecl attrDecl = new VwDtdAttributeDecl ( strElementName, strAttrName, strType,
            strDefValue, strValue );

    int nPos =  strElementName.indexOf( ':');
    if ( nPos >= 0 )
      strElementName = strElementName.substring( ++nPos  );

    List listAttr = (List) m_mapAttributes.get ( strElementName );

    if ( listAttr == null )
    {
      listAttr = new LinkedList ();
      m_mapAttributes.put ( strElementName, listAttr );
    }

    listAttr.add ( attrDecl );

    if ( m_fMaintainObjectList )
      m_objOrderList.add ( attrDecl );


  } // end AttributeDecl

  /**
   * ElementDecl Store elements (as VwDtdElementDecl objects)
   *
   * @param strName
   * @param strModel
   * @exception SAXException
   */
  public void elementDecl( String strName, String strModel ) throws SAXException
  {
    try
    {
      int nPos =  strName.indexOf( ':');
      if ( nPos >= 0 )
        strName = strName.substring( ++nPos  );

      VwDtdElementDecl eleDecl = new VwDtdElementDecl ( strName, strModel );
      m_mapElements.put ( strName.toLowerCase (), eleDecl );
      if ( m_fMaintainObjectList )
        m_objOrderList.add ( eleDecl );

    }
    catch ( Exception ex )
    {
      throw new SAXException ( ex.toString () );
    }
  }

  /**
   * Internal Entity Decl
   *
   * @param strName
   * @param strValue
   * @exception SAXException
   */
  public void internalEntityDecl( String strName, String strValue ) throws SAXException
  {

    VwDtdInternalEntity entity = new VwDtdInternalEntity( strName, strValue );
    m_mapInternalEntities.put ( strName, entity );

    if ( m_fMaintainObjectList )
      m_objOrderList.add ( entity  );

  }


  /**
   * External Entity Decl
   *
   * @param strName
   * @param strPublicId
   * @param strSystemId
   * @exception SAXException
   */
  public void externalEntityDecl( String strName, String strPublicId,
                                  String strSystemId ) throws SAXException
  {
    String strValue = null;
    int nType;

    if ( strPublicId != null )
    {
      nType = VwDtdExternalEntity.PUBLIC;
      strValue = strPublicId;
    }
    else
    {
      nType = VwDtdExternalEntity.SYSTEM;
      strValue = strSystemId;
    }

    VwDtdExternalEntity entity = new VwDtdExternalEntity( strName, strValue, nType );

    m_mapExternalEntities.put ( strName, entity );

    if ( m_fMaintainObjectList )
      m_objOrderList.add ( entity );
  }


  // *** Lexical Callbacks ***

  /**
   * Report the start of DTD declarations, if any.
   *
   * <p>This method is intended to report the beginning of the
   * DOCTYPE declaration; if the document has no DOCTYPE declaration,
   * this method will not be invoked.</p>
   *
   * <p>All declarations reported through
   * {@link org.xml.sax.DTDHandler DTDHandler} or
   * {@link org.xml.sax.ext.DeclHandler DeclHandler} events must appear
   * between the startDTD and {@link #endDTD endDTD} events.
   * Declarations are assumed to belong to the internal DTD subset
   * unless they appear between {@link #startEntity startEntity}
   * and {@link #endEntity endEntity} events.  Comments and
   * processing instructions from the DTD should also be reported
   * between the startDTD and endDTD events, in their original
   * order of (logical) occurrence; they are not required to
   * appear in their correct locations relative to DTDHandler
   * or DeclHandler events, however.</p>
   *
   * <p>Note that the start/endDTD events will appear within
   * the start/endDocument events from ContentHandler and
   * before the first
   * {@link org.xml.sax.ContentHandler#startElement startElement}
   * event.</p>
   *
   * @param name The document type name.
   * @param publicId The declared public identifier for the
   *        external DTD subset, or null if none was declared.
   * @param systemId The declared system identifier for the
   *        external DTD subset, or null if none was declared.
   * @exception SAXException The application may raise an
   *            exception.
   * @see #endDTD
   * @see #startEntity
   */
  public void startDTD( String name, String publicId,
                        String systemId ) throws SAXException
  {

  }


  /**
   * Report the end of DTD declarations.
   *
   * <p>This method is intended to report the end of the
   * DOCTYPE declaration; if the document has no DOCTYPE declaration,
   * this method will not be invoked.</p>
   *
   * @exception SAXException The application may raise an exception.
   * @see #startDTD
   */
  public void endDTD() throws SAXException
  {

  }


  /**
   * Report the beginning of some internal and external XML entities.
   *
   * <p>The reporting of parameter entities (including
   * the external DTD subset) is optional, and SAX2 drivers that
   * support LexicalHandler may not support it; you can use the
   * <code
   * >http://xml.org/sax/features/lexical-handler/parameter-entities</code>
   * feature to query or control the reporting of parameter entities.</p>
   *
   * <p>General entities are reported with their regular names,
   * parameter entities have '%' prepended to their names, and
   * the external DTD subset has the pseudo-entity name "[dtd]".</p>
   *
   * <p>When a SAX2 driver is providing these events, all other
   * events must be properly nested within start/end entity
   * events.  There is no additional requirement that events from
   * {@link org.xml.sax.ext.DeclHandler DeclHandler} or
   * {@link org.xml.sax.DTDHandler DTDHandler} be properly ordered.</p>
   *
   * <p>Note that skipped entities will be reported through the
   * {@link org.xml.sax.ContentHandler#skippedEntity skippedEntity}
   * event, which is part of the ContentHandler interface.</p>
   *
   * <p>Because of the streaming event m_btModel that SAX uses, some
   * entity boundaries cannot be reported under any
   * circumstances:</p>
   *
   * <ul>
   * <li>general entities within attribute values</li>
   * <li>parameter entities within declarations</li>
   * </ul>
   *
   * <p>These will be silently expanded, with no indication of where
   * the original entity boundaries were.</p>
   *
   * <p>Note also that the boundaries of character references (which
   * are not really entities anyway) are not reported.</p>
   *
   * <p>All start/endEntity events must be properly nested.
   *
   * @param name The name of the entity.  If it is a parameter
   *        entity, the name will begin with '%', and if it is the
   *        external DTD subset, it will be "[dtd]".
   * @exception SAXException The application may raise an exception.
   * @see #endEntity
   * @see org.xml.sax.ext.DeclHandler#internalEntityDecl
   * @see org.xml.sax.ext.DeclHandler#externalEntityDecl
   */
  public void startEntity( String name ) throws SAXException
  {
    ;
  }


  /**
   * Report the end of an entity.
   *
   * @param name The name of the entity that is ending.
   * @exception SAXException The application may raise an exception.
   * @see #startEntity
   */
  public void endEntity( String name ) throws SAXException
  {
    ;
  }


  /**
   * Report the start of a CDATA section.
   *
   * <p>The contents of the CDATA section will be reported through
   * the regular {@link org.xml.sax.ContentHandler#characters
   * characters} event; this event is intended only to report
   * the boundary.</p>
   *
   * @exception SAXException The application may raise an exception.
   * @see #endCDATA
   */
  public void startCDATA() throws SAXException
  {

  }


  /**
   * Report the end of a CDATA section.
   *
   * @exception SAXException The application may raise an exception.
   * @see #startCDATA
   */
  public void endCDATA() throws SAXException
  {

  }

  public void ignorableWhitespace( char ch[], int start, int length ) throws SAXException
  {
    String strWhiteSpace = new String ( ch, start, length );
    return;
  }

  /**
   * Report an XML comment anywhere in the document.
   *
   * <p>This callback will be used for comments inside or outside the
   * document element, including comments in the external DTD
   * subset (if read).  Comments in the DTD must be properly
   * nested inside start/endDTD and start/endEntity events (if
   * used).</p>
   *
   * @param ch An array holding the characters in the comment.
   * @param start The starting position in the array.
   * @param length The number of characters to use from the array.
   * @exception SAXException The application may raise an exception.
   */
  public void comment( char ch[], int start, int length ) throws SAXException
  {

    m_objOrderList.add ( new VwXmlComment ( new String ( ch, start, length ) ) );
  }


  public void characters( char ch[], int start, int length ) throws SAXException
  {

    String strChars = new String ( ch, start, length );
    return;

  }

  /**
   * For testing only
   */
  public static void main( String[] args )
  {
    try
    {
      VwDtdParser parser = new VwDtdParser( new File ( "\\VwDev\\test.dtd" ).toURL(), null );

      parser.setLexicalProcessing ( true );
      parser.setMaintainObjectList ( true );
      parser.process ();

      Map mapElements = parser.getElements();
      
      List listObj = parser.getObjectList ();

      return;

    }
    catch ( Exception ex )
    {
      ex.printStackTrace ();
    }


  }

} // end class VwDtdParser{}


// *** End of VwDtdParser.java ***
