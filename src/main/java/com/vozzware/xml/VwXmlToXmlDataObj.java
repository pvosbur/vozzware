/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwXmlToDataObj.java

Create Date: Apr 11, 2006
============================================================================================
*/
package com.vozzware.xml;

import com.vozzware.util.VwDelimString;
import com.vozzware.util.VwStack;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.DefaultHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.StringReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * This class converts an XML document to an VwXmlDataObj. The data object can preserve
 * the hieracrchy and parentage of an XML document by invoking the makeDataObjectsForParentTags()
 * <br>method or it can treat the xml document as a  flattened name/value Map with only the
 * <br>tags that contain data.
 */
public class VwXmlToXmlDataObj extends DefaultHandler
{

  private VwStack<XmlTag> m_stackTags = new VwStack<XmlTag>();

  private Map<String,VwXmlDataObj> m_mapXmlObjects = new HashMap<String, VwXmlDataObj>();

  private Map            m_mapTagObjects = new HashMap();
  

  private boolean       m_fIgnoreNameSpace = false;    // If true, don't use QNames as keys

  private String        m_strRoot = null;              // Name of the root tag in the xml document

  private String        m_strCurElementName = "";

  private String        m_strTagName;

  private VwDelimString m_dlmsXmlNs = null;

  private VwXmlDataObj  m_xmlObjRoot;
  private VwXmlDataObj  m_xmlObjCurParent = null;



  private XmlTag        m_tagCur = null;

  class XmlTag
  {
    String m_strName;
    String m_strQName;
    Attributes m_attrs;

    StringBuffer  m_sbData;

    XmlTag( String strQName, String strName )
    {
      m_strName = strName;
      m_strQName = strQName;
    }

    String getName()
    { return m_strName; }


    void setAttributes( AttributesImpl listAttr )
    {
      m_attrs = listAttr;
    }

    Attributes getAttributes()
    { return m_attrs; }
  }

  /**
   * Default Constructor Case is ignored and tag order is not preserved
   */
  public VwXmlToXmlDataObj()
  {
    init();

  } // end VwXmlToDataObj()


 
  private void init()
  {
    m_strRoot = null;


  }


  /**
   * Parse the xml content into a VwXmlDataObj hierarchy
   * @param inpSrc   the input sourse needed by parser
   * @param fValidate  validation flaf
   * @return
   * @throws Exception
   */
  private VwXmlDataObj parse ( InputSource inpSrc, boolean fValidate ) throws Exception
  {

    reset();

    XMLReader saxp = (XMLReader)Class.forName("org.apache.xerces.parsers.SAXParser").newInstance();

    saxp.setContentHandler( this );
    saxp.setErrorHandler( this );

    saxp.setFeature( "http://xml.org/sax/features/validation", fValidate);

    saxp.parse( inpSrc );

    return m_xmlObjRoot;

  }


  /**
   * Parses the xml document contained in a String object.
   *
   * @param strXml The String object containing the Xml document to parse
   * @param fValidate If true, use a validating parser (assumes a DTD definition with this document)
   * <br>otherwise use the non validating parser.
   *  At minimum the document is required to be well formed.
   *
   * @exception Exception if any parse errors occur,
   * VwDupValueException if a dup value is encountered, and the allows dup values flag is false
   */
  public VwXmlDataObj parse( String strXml, boolean fValidate ) throws Exception, VwXmlDupValueException
  {

    InputSource ins = new InputSource( new  StringReader( strXml ) );
    return parse( ins, fValidate );


  } // end parse()


  /**
   * Parses the xml document referenced by a File object.
   *
   * @param fileXml The File object referencing the xml document to parse
   * @param fValidate If true, use a validating parser (assumes a DTD definition with this document)
   * <br>otherwise use the non validating parser.
   *  At minimum the document is required to be well formed.
   *
   * @exception Exception if any parse errors occur
   */
  public VwXmlDataObj parse( File fileXml, boolean fValidate ) throws Exception
  {
    InputSource ins = new InputSource( new FileInputStream( fileXml ) );

    return parse( ins, fValidate );


  } // end parse()


  /**
   * Parses the xml document referenced by a File object.
   *
   * @param urlXml The URL object referencing the xml document to parse
   * @param fValidate If true, use a validating parser (assumes a DTD definition with this document)
   * <br>otherwise use the non validating parser.
   *  At minimum the document is required to be well formed.
   *
   * @exception Exception if any parse errors occur
   */
  public VwXmlDataObj parse( URL urlXml, boolean fValidate ) throws Exception
  {
    InputSource ins = new InputSource( urlXml.openStream() );

    return parse( ins, fValidate );

  } // end parse()




  /**
   * Reset for multiple parse
   */
  private void reset()
  {
    m_strRoot = null;

    if ( m_xmlObjRoot != null )
      m_xmlObjRoot.clear();

  }


  /**
   * Returns the name of the root tag
   */
  public String getRootTagName()
  { return m_strRoot; }


  /**
   * Sets the QNameOld key stroage. If set to true, then Quailfied names are not used as keys
   * in the data object. NCNAMES or used for attribute keys and element keys. The default
   * is false.
   *
   * @pararm fIgnoreNameSpace Sets the state
   */
  public void setIgnoreNameSpace( boolean fIgnoreNameSpace )
  { m_fIgnoreNameSpace = fIgnoreNameSpace; }


  /**
   * Record namespace mappings and treat them as attributes so that they can be reproduced
   */
  public void startPrefixMapping( String strPrefix, String strUri )
  {
    if ( m_dlmsXmlNs == null )
      m_dlmsXmlNs = new VwDelimString();

    m_dlmsXmlNs.add( "xmlns:" + strPrefix + "=" + strUri );

  }

  /**
   * Notification of a new XML tag. If attributes are specified, they are stored in the m_listAttr
   *
   * @param strName The name of the XML tag
   * @param attrList Any attributes that are part of the tag
   */
  public void startElement( String strUri, String strName, String strQName, Attributes attrList ) throws SAXException
  {

    if ( m_fIgnoreNameSpace )
      m_strTagName = strName;
    else
      m_strTagName = strQName;

    XmlTag tagNew = new XmlTag( strQName, strName );

    if ( m_tagCur == null )
    {
      m_xmlObjRoot = new VwXmlDataObj( strQName, strName );
      m_strRoot = m_strTagName;
      m_xmlObjCurParent = m_xmlObjRoot;
      m_mapXmlObjects.put( strQName, m_xmlObjRoot );
    }

    m_tagCur = tagNew;

    XmlTag tagParent = m_stackTags.peek();

    if ( tagParent == null )
      tagParent = tagNew;         // This is the root element

    m_stackTags.push( tagNew );


    m_xmlObjCurParent = m_mapXmlObjects.get( tagParent.m_strQName );

    if ( m_xmlObjCurParent == null )
    {
      m_xmlObjCurParent = new VwXmlDataObj( tagParent.m_strQName, tagParent.m_strName );
      m_mapXmlObjects.put( tagParent.m_strQName, m_xmlObjCurParent );

      XmlTag tagGrandParent = m_stackTags.peek( 2 );
      if ( tagGrandParent != null )
      {
        VwXmlDataObj xmlObjGrandParent =  m_mapXmlObjects.get( tagGrandParent.m_strQName );
        m_xmlObjCurParent.setParent( xmlObjGrandParent );
        xmlObjGrandParent.addChild( m_xmlObjCurParent );
      }

    }


    AttributesImpl listAttr = null;


    if ( attrList.getLength() > 0 )
    {
      if ( listAttr == null )
        listAttr = new AttributesImpl( attrList );
      else
      {
        for ( int x = 0; x < attrList.getLength(); x++ )
        {
          listAttr.addAttribute( attrList.getURI( x ), attrList.getLocalName( x ),
                                 attrList.getQName( x ), attrList.getType( x ),
                                 attrList.getValue( x ) );
        } // end for()

      } // end else

    } // end if

    if ( m_dlmsXmlNs != null )
    {
      if ( listAttr == null )
        listAttr = new AttributesImpl();

      String strNs = null;

      while ( (strNs = m_dlmsXmlNs.getNext() ) != null )
      {

        int nPos = strNs.indexOf( '=' );

        String strAttrName = strNs.substring( 0, nPos );

        listAttr.addAttribute( "", strAttrName, strAttrName, "CDATA", strNs.substring( ++nPos ) );
      }

      m_dlmsXmlNs = null;

    }

    tagNew.setAttributes( listAttr );


  } // end startElement()



  /**
   * Contains any text associated with a tag
   *
   * @param ach character array
   * @param nStart Starting position in array string starts
   * @param nLength Length of the text
   */
  public void characters( char[] ach, int nStart, int nLength )
                          throws SAXException
  {
    String strVal =  new String( ach, nStart, nLength );
    if ( strVal.trim().length() == 0 )
      return;            // Nothing but white space so quit here


     if ( m_tagCur.m_sbData == null )
       m_tagCur.m_sbData = new StringBuffer( );

     m_tagCur.m_sbData.append( strVal );

  } // end characters


  /**
   * Process the closing tag.
   */
  public void endElement( String strUri, String strName, String strQName ) throws SAXException
  {

    XmlTag tagComplete = m_stackTags.pop();

    Attributes tagAttrs = tagComplete.getAttributes();

    // See if this is a parent object
    VwXmlDataObj xmlObjParent = m_mapXmlObjects.get( tagComplete.m_strQName );

    if ( xmlObjParent != null )
    {
      if ( tagAttrs != null )
        xmlObjParent.setAttributes( tagAttrs );

      // Remove my instance from the current parent list as this tag is complete
      m_mapXmlObjects.remove( tagComplete.m_strQName );
    }
    else
    {
      // this is a child  element so add this to the current parent object
      String strData = null;
      if ( tagComplete.m_sbData != null )
        strData =  tagComplete.m_sbData.toString();

      VwXmlElement element = new VwXmlElement( tagComplete.m_strQName, tagComplete.m_strName,
                                                strData, tagAttrs );
      m_xmlObjCurParent.add( element );

    }


  } // end endElement{}


} // end class VwXmlToDataObj {}

// End of VwXmlToDataObj.java ***
