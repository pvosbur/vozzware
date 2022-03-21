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
import com.vozzware.util.VwExString;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.DefaultHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class converts an XML document to an VwDataObject. The data object can preserve
 * the hieracrchy and parentage of an XML document by invoking the makeDataObjectsForParentTags()
 * <br>method or it can treat the xml document as a  flattened name/value Map with only the
 * <br>tags that contain data.
 */
public class VwXmlToDataObj extends DefaultHandler
{
  private static final int MAXSIZE = 20;

  private String[]      m_astrParentage;              // Tag parentage stack
  private int           m_nPos;                       // Parentage stack position

  private VwDataObject m_dataObj;                    // Data object of tag values

  private List           m_listParentDataObjects;     // List of tags that will have their own data objects
  private List           m_listAttr;                  // Temp storage for attributes
  private List           m_listData;                  // List of tag data values by their parent stack pos
  private Map            m_mapTagObjects = new HashMap();
  
  private boolean       m_fMaintainParentage = false; // Maintain tag parentage flag
  private boolean       m_fAllowDups = false;         // Duplicate value flag

  private boolean       m_fPreserveCase = false;       // If true, preserves case else converts to lower

  private boolean       m_fPreserveDataOrder = false;  // Preserve order of elements in data object if true

  private boolean       m_fMakeDataObjectsForParents = false; // If true each parent tag will
                                                       // have a data object of its children
  private boolean       m_fIgnoreNameSpace = false;    // If true, don't use QNames as keys

  private boolean       m_fHoldTagsForDocLife = false;
  
  private String        m_strXml = null;               // Holds xml document

  private String        m_strRoot = null;              // Name of the root tag in the xml document

  private String        m_strCurElementName = "";

  private String        m_strTagName;

  private VwDelimString  m_dlmsXmlNs = null;

  /**
   * Default Constructor Case is ignored and tag order is not preserved
   */
  public VwXmlToDataObj()
  {
    init();

  } // end VwXmlToDataObj()


  /**
   * Constructor Allows control of case and tag order in the VwDataObject(s) returned.
   *
   * @param fPreserveCase If true, store the tag keys in the data object with the same case
   * as the xml tags else treat all tags as lowercase.
   *
   * @param fPreserveDataOrder If true preserve the order in the VwDataObject of the xml
   * tags in the document.
   */
  public VwXmlToDataObj( boolean fPreserveCase, boolean fPreserveDataOrder )
  {
    m_fPreserveCase = fPreserveCase;
    m_fPreserveDataOrder = fPreserveDataOrder;

    init();

  } // end VwXmlToDataObj()


  private void init()
  {
    m_strRoot = null;

    m_astrParentage = new String[ 50 ];               // Allow for 50 nested tags
    m_nPos = -1;

    m_listParentDataObjects = new ArrayList( MAXSIZE );
    m_listAttr = new ArrayList( MAXSIZE );
    m_listData = new ArrayList( MAXSIZE );

    // Alloacate initial null entries for placeholders
    for ( int x = 0; x < MAXSIZE; x++ )
    {
      m_listParentDataObjects.add( null );
      m_listAttr.add( null );
      m_listData.add( null );
    }

    m_dataObj = new VwDataObject( m_fPreserveCase, m_fPreserveDataOrder );


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
  public VwDataObject parse( String strXml, boolean fValidate ) throws Exception, VwXmlDupValueException
  {

    reset();

    XMLReader saxp = (XMLReader)Class.forName("org.apache.xerces.parsers.SAXParser").newInstance();

    saxp.setContentHandler( this );
    saxp.setErrorHandler( this );

    saxp.setFeature( "http://xml.org/sax/features/validation", fValidate);

    InputSource ins = new InputSource( new  StringReader( strXml ) );

    saxp.parse( ins );

    return m_dataObj;

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
  public VwDataObject parse( File fileXml, boolean fValidate ) throws Exception, VwXmlDupValueException
  {
    reset();

    XMLReader saxp = (XMLReader)Class.forName("org.apache.xerces.parsers.SAXParser").newInstance();

    saxp.setContentHandler( this );
    saxp.setErrorHandler( this );
    saxp.setFeature( "http://xml.org/sax/features/validation", fValidate);

    InputSource ins = new InputSource( new FileInputStream( fileXml ) );

    saxp.parse( ins );

    return m_dataObj;

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
  public VwDataObject parse( URL urlXml, boolean fValidate ) throws Exception, VwXmlDupValueException
  {
    reset();

    XMLReader saxp = (XMLReader)Class.forName("org.apache.xerces.parsers.SAXParser").newInstance();

    saxp.setContentHandler( this );
    saxp.setErrorHandler( this );
    saxp.setFeature( "http://xml.org/sax/features/validation", fValidate);

    InputSource ins = new InputSource( urlXml.openStream() );

    saxp.parse( ins );

    return m_dataObj;

  } // end parse()

  /**
   * Parses the xml document referenced by a File object.
   *
   * @param inSource The InputSource representing the xml stream
   *
   * @param fValidate If true, use a validating parser (assumes a DTD definition with this document)
   * <br>otherwise use the non validating parser.
   *  At minimum the document is required to be well formed.
   *
   * @exception Exception if any parse errors occur
   */
  public VwDataObject parse( InputSource inSource, boolean fValidate ) throws Exception, VwXmlDupValueException
  {
    reset();

    XMLReader saxp = (XMLReader)Class.forName("org.apache.xerces.parsers.SAXParser").newInstance();

    saxp.setContentHandler( this );
    saxp.setErrorHandler( this );
    saxp.setFeature( "http://xml.org/sax/features/validation", fValidate);

    saxp.parse( inSource );

    return m_dataObj;

  } // end parse()


  /**
   * Gets the VwDataObject following completion of the parsed message
   */
  public VwDataObject getParsedMsg()
  { return m_dataObj; }


  /**
   * Reset for multiple parse
   */
  private void reset()
  {
    m_nPos = -1;
    m_strRoot = null;
    m_dataObj.clear();

  }


  /**
   * Returns the name of the root tag
   */
  public String getRootTagName()
  { return m_strRoot; }

  /**
   * Toggles the maintain tag parentage flag. NOTE If the makeDataObjectsForParentTags option is
   * specified<br>then this option is unconditionaly set to false
   *
   * @param fMaintainTagParentage If true (the default setting), then the tag parentage is maintained
   * in a dot notation when storing the name/value pairs in the Map.<br>
   * Ex. &lt;person&gt;<br>
   *       &lt;name&gt;Joe&lt;/name&gt;<br>
   *     &lt;/person&gt;<br>
   * The data "Joe" would be stored in the map as key value "person.name".<br>
   * If false the same data would be stored
   * under the key "name". <br>Also the parentage flag is false the the Map object always returns
   * a Vector object to allow for duplicate key instances to be resolved. I.E. multiple person
   * tags in the example above.
   *
   */
  public void setMaintainParentage( boolean fMaintainTagParentage )
  {
    if ( m_fMakeDataObjectsForParents )
      m_fMaintainParentage = false;     // Unconditionally false if this option is on

    m_fMaintainParentage = fMaintainTagParentage;

  } // end setMaintainParentage()

  
  /**
   * If true tag dataobjects persist for life of document. This is useful for situation for introspecting an
   * instance document to get all possible child objects configurations
   * @param fHoldTags
   */
  public void holdTagObjectsForDocLife( boolean fHoldTags )
  { m_fHoldTagsForDocLife = fHoldTags; }
  

  /**
   * Sets the option to make a data object for each xml tag that is a parent.
   * Multiple instances of the parent tag
   * map will be placed in a VwDataObjList collection.
   */
  public void makeDataObjectsForParentTags()
  {
    m_fMaintainParentage = false;     // Unconditionally false when this option is on
    m_fMakeDataObjectsForParents = true;
    m_fAllowDups = true;

  } // end makeMapsForParentTags()

  /**
   * Preserves the xml tag case for data object keys if true , else tag names
   * are converted to lowercase (the default).
   *
   * @param fCase true to preserve case, false to convert tags to lower case before they are stored
   * in the map.
   */
  public void setPreserveCase( boolean fCase )
  { m_fPreserveCase = fCase; }


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
   * Sets the allows dup values flag. If the flag is false (the default) an VwXmlDupValueException
   * is thrown if a duplicate tag exists. If dup values are allowed, then an VwElementList of objects are stored in the
   * data object. Normally, the data object returns a String object for a tag key. If there are
   * duplicte tag names, then each object for the tag is stored in an VwElementList collection.
   * The following code segment illustrates the use of the data object when allowing dup tags
   * is set:<pre><br>
   * Object obj = dataObj.getObject( key );<br>
   * if ( obj instanceof String )<br>
   * { // Do string assign }<br>
   * else<br>
   * {<br>
   *   Iterator iItems = ((VwElementList)obj).iterator();<br>
   *   while( iTems.hasNext() )<br>
   *   {<br>
   *     // Process List<br>
   *   }<br>
   * </pre>
   *
   *
   * @param fAllowDups Set to true to allow dups, false otherwise
   */
  public void setAllowDupValues( boolean fAllowDups )
  { m_fAllowDups = fAllowDups; }


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

    if ( !m_fPreserveCase )
      m_strTagName = m_strTagName.toLowerCase();

    if ( m_strRoot == null )
    {
      m_strRoot = m_strTagName;

      if ( m_fMakeDataObjectsForParents )
      {
        VwDataObject dobjTop = new VwDataObject( m_fPreserveCase, m_fPreserveDataOrder );
        dobjTop.setRootElementName( m_strTagName );
        m_mapTagObjects.put( m_strTagName.toLowerCase(),  dobjTop ); // Create initial root map for this option
      }
    } // end if

    m_astrParentage[ ++m_nPos ] = m_strTagName;     // Put new tag name on the parent stack

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

    m_listAttr.set( m_nPos, listAttr );

  } // end startElement()


  /**
   * Returns a key based on the parentage array
   *
   * @return a dot refereneced key based on the parentage array
   */
  private String getParentageKey()
  {
    String strKey = "";

    for ( int x = 1; x <= m_nPos; x++ )
    {
      if ( x > 1 )
        strKey += ".";

      strKey += m_astrParentage[ x ];

    }

    return strKey;

  } // end getParentageKey()


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

    if ( m_strTagName.equals( m_strCurElementName ) )
    {
       StringBuffer sbData = (StringBuffer)m_listData.get( m_nPos );
       sbData.append( strVal );
       return;
    }

    StringBuffer sbData = new StringBuffer( strVal.length() );

    sbData.append( strVal );

    // Store the data value for this tag at the cuurent slot (m_nPos)
    m_listData.set( m_nPos, sbData );

    String strElementName = null;

    if ( m_fMaintainParentage )
      strElementName = getParentageKey();
    else
      strElementName = m_astrParentage[ m_nPos ];

    m_strCurElementName = strElementName;

  } // end characters


  /**
   * Process the closing tag.
   */
  public void endElement( String strUri, String strName, String strQName ) throws SAXException
  {

    m_strCurElementName = "";

    if ( !m_fMakeDataObjectsForParents )
    {

      String strElementName = null;

      if ( m_fMaintainParentage )
        strElementName = getParentageKey();
      else
        strElementName = m_astrParentage[ m_nPos ];

      try
      {
        Attributes listAttr = (Attributes)m_listAttr.get( m_nPos );
        m_listAttr.set( m_nPos, null );
        StringBuffer sbData = (StringBuffer)m_listData.get( m_nPos );
        m_listData.set( m_nPos, null );

        setupDataObject( m_dataObj, strElementName, sbData, listAttr );
      }
      catch( Exception e )
      {
        throw new SAXException( e );
      }

      --m_nPos;


      return;

    } // end if endElement( )

    // If this is an attribute only tag get the the attributes before adjusting parentage
    Attributes listAttrChild = (Attributes)m_listAttr.get( m_nPos );
    m_listAttr.set( m_nPos, null );

    // See if this child is a parent dataobject of other children
    VwDataObject dobjChild = (VwDataObject)m_mapTagObjects.get(  strName.toLowerCase()  );
    
    if ( !m_fHoldTagsForDocLife )
      m_mapTagObjects.remove( strName.toLowerCase() );
    
    m_listParentDataObjects.set( m_nPos, null );

    StringBuffer sbChildDataVal = (StringBuffer)m_listData.get( m_nPos );
    m_listData.set( m_nPos, null );

    // Position us to this ending tag's parent
    --m_nPos;

    String strChild = (String)(m_fIgnoreNameSpace ? strName : strQName);

    if ( !m_fPreserveCase )
      strChild = strChild.toLowerCase();

    // If this is the root tag, we're all done
    if ( strChild.equals( m_strRoot )  )
    {
      if ( listAttrChild != null )
      {
        try
        {
          setupAttributes( dobjChild, listAttrChild, strChild );
        }
        catch( Exception e )
        {
          throw new SAXException( e );
        }

      } // end if listAttrChild != null )
      m_dataObj = dobjChild;

      if ( sbChildDataVal != null )
        m_dataObj.put( strChild, sbChildDataVal.toString() );

      return;
    }

    // Current parent is top of stack

    String strParent = m_astrParentage[ m_nPos ];

    // Get data object for this parent tag and remove entry from master list
    VwDataObject dobjParent = (VwDataObject)m_mapTagObjects.get(  strParent.toLowerCase()  );

    if ( dobjParent == null ) // Parent dataobject has not been created yet, so create it now
    {
      dobjParent = new VwDataObject( m_fPreserveCase, m_fPreserveDataOrder );
      m_mapTagObjects.put( strParent.toLowerCase(), dobjParent );   // Put data object parent in master list
      Attributes listAttr = (Attributes)m_listAttr.get( m_nPos );

      if ( listAttr != null )
      {
        try
        {
          setupAttributes( dobjParent, listAttr, strParent );
        }
        catch( Exception e )
        {
          throw new SAXException( e );
        }

      } // end if

    } // end if ( dobjParent == null )

    // Put tag and data in the data object if this a primitive child (No children itself)
    if ( dobjChild == null )
      setupDataObject( dobjParent, strChild, sbChildDataVal, listAttrChild );
    else
    // If the child object is a dataobject then add it to the parent here
    {

      Object obj = dobjParent.getObject( strChild );

      if ( sbChildDataVal != null )
        setupDataObject( dobjChild, strChild, sbChildDataVal, null );

      // See what object is stored for this parent. If there already exists a data object, we have
      // another instance of this tag, so we create a VwDataObjList for dup parent tags.
      // If the object is an VwDataObjList then we add the new data object to the List

      if ( obj == null )         // First entry, just add the data object
          dobjParent.put( strChild, dobjChild );
       else
      {
        if ( obj instanceof VwElement )
        {
          VwElementList list = new VwElementList();
          list.add( obj );
          VwElement element = new VwElement( strChild, null );
          element.setChildObject( dobjChild );
          list.add( element );
          dobjParent.put( strChild, list );
        }
        else
        if ( obj instanceof VwElementList )
        {
          VwElement element = new VwElement( strChild, null );
          element.setChildObject( dobjChild );
          ((List)obj).add( element );

        }
        else
        if ( obj instanceof VwDataObjList )    // List already present, add new map to the list
          ((VwDataObjList)obj).add( dobjChild );
        else
        {
          VwDataObjList list = new VwDataObjList();     // We nedd a List to hold the dups
          list.add( obj );                  // Add original map to the new List
          list.add( dobjChild );            // Add the new map to the new List
          dobjParent.put( strChild, list );
        }

      } // end else

    } // end if ( dobjChile != null )

  } // end endElement{}


  /**
   * Add any attributes for a given tga if they exists
   *
   * @param dataObj The data object containing the tag with attributes
   * @param strKey The tag name
   */
  private void setupAttributes( VwDataObject dataObj, Attributes listAttr, String strKey ) throws Exception
  {
    if ( dataObj == null )
      return;

    // If tag has attribute list, add it to the data object
    if ( listAttr != null )
      setupDataObject( dataObj, strKey, null, listAttr );

  } // end setupAttributes()


  /**
   * Find the data object for the current parent or create an empty data object
   * if nothing yet exists.
   *
   * @return An VwDataObject to hold child entries
   */
  private VwDataObject getDataObject()
  {

    // See if all parent tags will have their own maps
    if ( m_fMakeDataObjectsForParents )
    {

      String strParent = null;

      if ( m_nPos > 0 )
        strParent = m_astrParentage[ m_nPos - 1 ];
      else
        strParent = m_astrParentage[ m_nPos ];

      VwDataObject dataObjParent = (VwDataObject)m_listParentDataObjects.get( m_nPos );

      if ( dataObjParent == null )
      {
        dataObjParent = new VwDataObject( m_fPreserveCase, m_fPreserveDataOrder );
        m_listParentDataObjects.set( m_nPos, dataObjParent );
      }

      return dataObjParent;

    }
    else
      return m_dataObj;

  } // end getValuesMap()


  /**
   * Setups a data object with the proper object based on
   */
  private void setupDataObject( VwDataObject dataObjParent, String strKey, StringBuffer sbData, Attributes listAttr ) throws SAXException
  {

    Object objVal = dataObjParent.getObject( strKey );

    String strData = null;

    if ( sbData != null )
      strData = VwExString.expandMacro( sbData.toString() );

    VwElement xmle = new VwElement( strKey, strData, listAttr );

    if ( objVal != null )
    {
      // See if this parent tag data that is comming in following an attribute
      //
      if ( objVal instanceof VwElement && listAttr == null && ((VwElement)objVal).getObject() == null )
      {
        ((VwElement)objVal).setValue( strData );
        return;
      }


      if ( !m_fAllowDups )
        throw new SAXException( new VwXmlDupValueException( "'" + strKey +
                                                             "' tag already encountered and allow "
                                                             + "duplicates flag is set to false" ) );
      // There could be a dataobject or dataobjlist already in here by the same name.
      // if this is the case the dataobject needes to be placed in an VwElement

      if ( objVal instanceof VwDataObject )
      {
        VwElementList eleList = new VwElementList();
        VwElement element = new VwElement( strKey, null );
        element.setChildObject( (VwDataObject)objVal );
        eleList.add( element );
        eleList.add( xmle );
        dataObjParent.put( strKey, eleList );
      }
      else
      if ( objVal instanceof VwDataObjList )
      {
        VwElementList eleList = new VwElementList();
        VwElement element = new VwElement( strKey, null );
        element.setChildObject( (VwDataObjList)objVal );
        eleList.add( element );
        eleList.add( xmle );
        dataObjParent.put( strKey, eleList );

      }
      else
      if ( objVal instanceof VwElement )
      {
        VwElementList list = new VwElementList();
        list.add( objVal );
        list.add( xmle );
        dataObjParent.put( strKey, list );
      }
      else
        ((VwElementList)objVal).add( xmle );
    }
    else
      dataObjParent.put( xmle );


  } // end setupDataObject()


  // *** For Testing only ***
  public static void main( String[] args )
  {
    try
    {
      String strXml = null;
      String strVal = null;

      strXml = "<Doc>"
             + "  <Data><![CDATA[6 > 10\r\n& 10 < \r\n12]]></Data>"
             + "</Doc>";


      VwXmlToDataObj xxx = new VwXmlToDataObj( true, true );
      xxx.makeDataObjectsForParentTags();
      xxx.setAllowDupValues( true );
      VwDataObject dobj = xxx.parse( new File( "\\itc\\estimate.xml" ) , false );

      System.out.println( dobj.getString( "description" ) );
      strXml = dobj.toXml( "Doc", null, true, 0 );
      System.out.println( strXml );

      VwDataObjToXml xmlWriter = new VwDataObjToXml();

    /*
    strXml = "<person id=\"1\" employee=\"yes\" >\n"
                  + "  <Vitals process=\"No\">"
                  + "    <name employed=\"Yes\">Joe</name>\n"
                  + "    <age>20</age>"
                  + "    <interests>"
                  + "      <main>Golf</main>"
                  + "      <other>Tennis</other>"
                  + "    </interests>"
                  + "    <Phone>"
                  + "      <type>Home</type>"
                  + "      <area>203</area>"
                  + "      <pre>658</pre>"
                  + "      <last>7195</last>"
                  + "    </Phone>"
                  + "    <Phone>"
                  + "      <type>Work</type>"
                  + "      <area>777</area>"
                  + "      <pre>888</pre>"
                  + "      <last>999</last>"
                  + "    </Phone>"
                  + "    <Phone>"
                  + "      <type>Cell</type>"
                  + "      <area>860</area>"
                  + "      <pre>460</pre>"
                  + "      <last>6258</last>"
                  + "    </Phone>"
                  + "  </Vitals>"
                  + "  <address>\n"
                  + "    <street>123 Lane</street>\n"
                  + "    <city>Hartford</city>\n"
                  + "  </address>\n"
                  + "  <test>Just Testing</test>"
                  + "</person>";

    */

    // /*
    strXml = "<GetEmployeesResponse>"
                  + "  <ServiceAttrOnly flags=\"1\"/>"
                  + "  <ResultSet id=\"1\">"
                  + "    <name married=\"No\">Joe</name>"
                  + "    <age>22</age>"
                  + "  </ResultSet>"
                  + "  <ResultSet id=\"2\">"
                  + "    <name married=\"Yes\">Jane</name>"
                  + "    <age>28</age>"
                  + "  </ResultSet>"
                  + "  <ResultSet id=\"3\">"
                  + "    <name married=\"Yes\">Alice</name>"
                  + "    <age>30</age>"
                  + "  </ResultSet>"
                  + "</GetEmployeesResponse>" ;

    // */

    Attributes listAttr = null;

    VwXmlToDataObj mc = new VwXmlToDataObj( false, true );

     mc.setAllowDupValues( true );
     mc.setMaintainParentage( true );
     //mc.makeDataObjectsForParentTags();
     //mc.setPreserveCase( true );
     //VwDataObject dObjPerson = mc.parse( new File( "\\cirqit-source\\ariba_cxml.xml" ), false );
     VwDataObject dObjPerson = mc.parse( strXml, false );
     VwElementList list = null;


      /*
      strXml = VwDataObjToXml.toXml( "GetEmployeesResponse", dObjPerson, null, true, 0 );
      System.out.println( strXml );
      */


      list = (VwElementList)dObjPerson.getObject( "resultset" );

      Attributes la = list.getAttributeList( 1 );

      strVal = la.getQName( 0 );
      strVal = la.getValue( 0 );
      strVal = dObjPerson.getString( "ServiceAttrOnly" );
      la = dObjPerson.getAttributeList( "ServiceAttrOnly" );
      strVal = la.getQName( 0 );
      strVal = la.getValue( 0 );

      //if ( dObjPerson.exists( "resultset.name" ) )
      {
        list = (VwElementList)dObjPerson.getObject( "resultset.name" );
        strVal = list.getValue( 0 );
        strVal = list.getValue( 1 );
        strVal = list.getValue( 2 );
        la = list.getAttributeList( 0 );
        strVal = la.getQName( 0 );
        strVal =  la.getValue( 0 );

      }

      list = (VwElementList)dObjPerson.getObject( "resultset.age" );
        strVal = list.getValue( 0 );
        strVal = list.getValue( 1 );
      // */

      /*
      VwDataObjList list = (VwDataObjList)dObjPerson.getObject( "ResultSet" );
      for ( int x = 0; x < list.size(); x++ )
      {
        VwDataObject mapName = list.getDataObj( x );
        strVal = mapName.getAttribute( "ResultSet", "id" );
        strVal = mapName.getString( "name" );
        strVal = mapName.getString( "age" );
     }

      */

      /*
      VwElementList l = (VwElementList)dObjPerson.get( "name" );
      VwElement ele = (VwElement)l.get( 0 );

      strVal = ((VwElement)l.get( 0 )).getValue();

      //strVal = ((VwElement)).getValue();
      //strVal = ((VwElement)dObjPerson.get( "person.vitals.interests.main" )).getValue();

      Object obj1 = dObjPerson.get( "type" );

      if ( obj1 instanceof VwElementList )
      {

        strVal = ((VwElementList)obj1).getValue( 0 );
        strVal = ((VwElementList)obj1).getValue( 2 );
      }

      l = (VwElementList)dObjPerson.get( "vitals" );
      ele = (VwElement)l.get( 0 );

      strVal = ((VwAttribute)ele.getAttributes().get( 0 )).getValue();

     */

      strVal = dObjPerson.getString( "test" );

      VwDataObject mapVitals = (VwDataObject)dObjPerson.getObject( "vitals" );

      listAttr = mapVitals.getAttributeList( "vitals" );
      strVal = listAttr.getValue( 0 );

      strVal = mapVitals.getString( "name" );

      listAttr = mapVitals.getAttributeList( "name" );
      strVal = listAttr.getValue( 0 );

      strVal = mapVitals.getString( "age" );

      VwDataObject mapInterests = (VwDataObject)mapVitals.getObject( "interests" );

      strVal = mapInterests.getString( "main" );
      strVal = mapInterests.getString( "other" );

      Object obj = mapVitals.getObject( "phone" );

      VwDataObject mapPhone = null;

      if ( obj instanceof VwDataObject )
        mapPhone = (VwDataObject)obj;
      else
      {
        VwDataObjList dlist = (VwDataObjList)obj;
        for ( int x = 0; x < dlist.size(); x++ )
        {
          mapPhone = dlist.getDataObj( x );

          strVal = mapPhone.getString( "area" );
          strVal = mapPhone.getString( "pre" );
          strVal = mapPhone.getString( "last" );

        }

      }

      VwDataObject mapAddress = (VwDataObject)dObjPerson.getObject( "address" );
      strVal = mapAddress.getString( "city" );
      strVal = mapAddress.getString( "street" );

      listAttr = dObjPerson.getAttributeList( "person" );

      strVal = listAttr.getValue( 0 );
      strVal = listAttr.getValue( 1 );

      strXml = xmlWriter.toXml( "Person", dObjPerson, null, true, 0 );
      System.out.println( strXml );

      int i = 1;

    }
    catch( Exception e )
    {

      e.printStackTrace();
    }

  } // end main()

  public void warning( SAXParseException p0 ) throws SAXException
  {
    System.out.println( p0.toString() );
  }


  public void error( SAXParseException p0 ) throws SAXException
  {
    System.out.println( p0.toString() );
  }


  public void processingInstruction( String target,
                                     String data ) throws SAXException
  {

    System.out.println( "Target = " + target );
    System.out.println( "Data = " + data );

  }

} // end class VwXmlToDataObj {}

// End of VwXmlToDataObj.java ***
