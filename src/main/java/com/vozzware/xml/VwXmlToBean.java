/*
============================================================================================

                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwXmlToBean.java

Create Date: Apr 11, 2006
============================================================================================
*/
package com.vozzware.xml;

import com.vozzware.db.VwDVOBase;
import com.vozzware.util.VwBeanUtils;
import com.vozzware.util.VwDate;
import com.vozzware.util.VwDelimString;
import com.vozzware.util.VwDocFinder;
import com.vozzware.util.VwExString;
import com.vozzware.util.VwResourceStoreFactory;
import com.vozzware.xml.dtd.VwDtdElementDecl;
import com.vozzware.xml.dtd.VwDtdParser;
import com.vozzware.xml.namespace.Namespace;
import com.vozzware.xml.namespace.QName;
import com.vozzware.xml.schema.AnyTypeContent;
import com.vozzware.xml.schema.VwAttributeImpl;
import com.vozzware.xml.schema.VwModelGroupImpl;
import com.vozzware.xml.schema.util.VwSchemaReaderImpl;
import org.w3c.dom.Document;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.schema.ComplexType;
import javax.xml.schema.Element;
import javax.xml.schema.ModelGroup;
import javax.xml.schema.Schema;
import javax.xml.schema.util.UnknownElementHandler;
import javax.xml.schema.util.XmlCloseElementEvent;
import javax.xml.schema.util.XmlCloseElementListener;
import javax.xml.schema.util.XmlDeSerializer;
import javax.xml.schema.util.XmlOpenElementEvent;
import javax.xml.schema.util.XmlOpenElementListener;
import java.beans.Introspector;
import java.beans.MethodDescriptor;
import java.io.File;
import java.io.FileReader;
import java.io.StringReader;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Stack;
import java.util.TimeZone;


/**
 * This class transforms an XML document into a Java bean representation. The transformer
 * also supports properties that take objects and collections as parameter types.<br> The defualt XML
 * structure expected by this class is as follows:<br>
 * <pre>
 * EX. &lt;BankCustomer&gt;
 *       &lt;Name&gt;John Doe&lt;/Name&gt;
 *       &lt;Age&gt;30&lt;/Age&gt;
 *       &lt;Address&gt;
 *         &lt;Stree&gt;40 Roswell Rd&lt;/Street&gt;
 *         &lt;State&gt;CT&lt;/State&gt;
 *       &lt;/Address&gt;
 *       &lt;Accounts&gt;
 *         &lt;Account&gt;
 *           &lt;AcctNo&gt;12345&lt;/AcctNo&gt;
 *           &lt;Balance&gt;1000.45&lt;/Balance&gt;
 *         &lt;/Account&gt;
 *         &lt;Account&gt;
 *           &lt;AcctNo&gt;99987&lt;/AcctNo&gt;
 *           &lt;Balance&gt;6543.22&lt;/Balanc&gt;
 *         &lt;/Account&gt;
 *       &lt;/Accounts&gt;
 *      &lt;/BankCustomer&gt;
 * </pre>
 *
 * The XML documnet above has three object Types: BankCustomer, Address and Account. In this example
 * <br>we can a collection of Account objects that will be placed in a List collection.
 * <br>Xml tags that have text data are assumed to properties in the bean they are mapped to.
 * <br>I.E. the &lt;Name&gt; tag within the &lt;Person&gt; tag would map to a setColName property
 * <br>defined in the Person object. If a bean property takes an object i.e. Address then the name
 * <br>of the property becomes a parent tag in the xml document ( unless the property name and
 * <br>the object name are both the same, which is the case in this example) followed by
 * <br>the object name also as a parent tag. Defining collections is done in the same way,
 * <br>but expect the object's to repeat as does the Account object in the above example. Both
 * <br>Map and List based collections are supported. If the bean property specifies List
 * <br>collection type, then a ArrayList is created by the transformer. If the bean property
 * <br>specifies Map collection type, then a HashMap is created by the transformer.
 * <br>Collections can contain simple types like a collection of Integer or String objects.
 * <br>The following Xml snippet shows a collection of strings from some object that
 * <br>had a propety named setFavoriteColors( List listColors )
 * <br><pre>
 *   &lt;FavoriteColors&gt;
 *     &lt;String&gt;Red&lt;/String&gt;
 *     &lt;String&gt;Green&lt;/String&gt;
 *     &lt;String&gt;Blue&lt;/String&gt;
 *   &lt;/FavoriteColors&gt;
 *
 *   or a collection of Integer objects:
 *
 *   &lt;Numbers&gt;
 *     &lt;Integer&gt;100&lt;/Integer&gt;
 *     &lt;Integer&gt;200n&lt;/Integerg&gt;
 *     &lt;Integer&gt;300&lt;/Integer&gt;
 *   &lt;/Numbers&gt;
 *   </pre>
 *   <br>Note that the tag &lt;String&gt; and &lt;value&gt; can be used interchangably.
 *   <br>Use the java class type for the other primitives.
 *   <br>
 *   <h2>Handling tag attributes</h2>
 *   If the xml document has tag attributes then you must define the following property
 *   for each object that has tags with attributes:
 *   <br> public void setAttributes( String strPropName, Attributes listAttr )
 *   <br> { m_mapAttr.put( strPropName, listAttr ); }
 *   <br>The implementation code is up to you but a HashMap is the easiest way to store
 *   <br>attributes. The strPropName (map key) is always converted to lower case. You might
 *   <br>want to define a retrieval property as follows:
 *   <br> public Attributes getAttributes( String strPropName )
 *   <br> { return (Attributes)m_mapAttr.get( strPropName ); }
 *   <br>Attributes can also be set at the object level in which case you use the object name
 *   <br>in lower case to retrieve the attributes.
 *
 *
 *   <br>Please refer to the XmlTester sample prgram for a complete example of this utility.
 */
public class VwXmlToBean extends DefaultHandler implements XmlDeSerializer
{
  private static final int SIMPLE = 1;        // Simple data type
  private static final int OBJECT = 2;        // Tag is a parent that represents a single object
  private static final int COLLECTION = 3;    // Tag is parent that represents a collection of children
  private static final int MIXED = 7;         // Could be a String or an object
  private static final int EMPTY = 8;         // Tag has attributes only
  /**
   * Inner class to hold tag info odata
   */
  private class TagMethodInfo
  {
    String      m_strParamClassName = "";     // Class name if OBJECT type
    String      m_strTagName = "";            // The tag name that represents this collection
    Method      m_methodSetter;               // The method to invoke for this tag
    Method      m_methodCollection;           // The add, or put method for the collection type

    Class       m_clsParamType;               // The parameter class type that the method takes
    Class       m_clsCollectionType;          // The collection type -  ArrayList, HashMap ...
    Class       m_clsMapKeyType;              // If Map collection the class type of the key
    Class       m_clsArray;                   // Array class type if collection is an array

    Constructor m_constructSimpleType;        // Constructor for a simple type

    boolean     m_fIsPut;                     // If true, if collection type is map/hashtable based

    boolean     m_fIsSimpleType;              // If true, tag is a simple (primitive) data type

    boolean     m_fAnyType;                   // If true this is a collection of objects that can
                                              // contain any type of content

    int         m_nType;                      // Type Java mapping for tag


    TagMethodInfo( String strTagName, Method methodSetter, Class clsParamType, int nType, boolean fObjAlias )
    {

      if ( clsParamType != null && clsParamType.isInterface() )
        clsParamType = null;

      m_strTagName = strTagName;
      m_methodSetter = methodSetter;
      m_clsParamType = clsParamType;
      m_nType = nType;

      if ( m_nType == SIMPLE )
        m_fIsSimpleType = true;

      if ( (m_nType == OBJECT || m_nType == COLLECTION) && clsParamType != null )
      {
        if ( !m_fIsSimpleType )
        {
          if ( fObjAlias )
            m_strParamClassName = m_strTagName;
          else
            m_strParamClassName = getObjName( clsParamType );
        }
      }

    }

  } // end class TagMethodInfo{}

  private static final int ANY = 9;           // Tag can have any content

  private static Object s_objSemi = new Object();

  private VwXmlToDataObj   m_anyContentType;               // Used to collect Map representation of am ANY tag

  private URL               m_urlSchema = null;

  private Schema            m_schema = null;

  private List  m_listTopLevelObjects;                // List of Top Level Objects

  private static Map<String,String>  s_mapPrimTypes;           // Scheam primitive typs to Java maps map
  private static Map  s_mapPackages = Collections.synchronizedMap( new HashMap() );  // package list for collections of user def objects
  protected static Map  s_mapObjects = Collections.synchronizedMap( new HashMap() );         // A map of object method maps
  private static Map  s_mapSchemas = Collections.synchronizedMap( new HashMap() );         // A map of object method maps
  private static Map  s_mapTagHandlersBySchema = Collections.synchronizedMap( new HashMap() );         // A map of object method maps
  private static Map  s_mapTopLevelTypes = Collections.synchronizedMap( new HashMap() );
  private static Map  s_mapMethodAlias = Collections.synchronizedMap(new HashMap() );     // A map of xml tags and their corresponding method names
  private static Map  s_mapMethodSetterAlias = Collections.synchronizedMap(new HashMap() );     // A map of xml tags and their corresponding method names
  private static Map  s_mapTagHandlers = Collections.synchronizedMap( new HashMap() );     // A map of xml tags and their corresponding object alias names

  protected Map   m_mapCurObjMethods = null;            // A map of tags to methods for current object
  private Map     m_mapAttr = new HashMap();            // A map of attributes for a tag
  private Map     m_mapTagData = new HashMap();         // A map of tag data by tag name

  private Map     m_mapCloseTagListeners = new HashMap();   // Custom tag listeners
  private Map     m_mapOpenTagListeners = new HashMap();   // Custom tag listeners

  private Map     m_mapObjCollections = new HashMap();
  private Map     m_mapTagURIs = new HashMap();

  private UnknownElementHandler m_unknownElementHandler;
  private String      m_strQNUnknownGrandParent;

  private org.w3c.dom.Element m_eleUnknownParent = null;
  private org.w3c.dom.Element m_eleUnknownCur = null;
  private Document		m_docUnknownElement = null;

  private Object      m_curObj = null;                      // Current object instance
  private Object      m_objSetter = null;                   // Object that will be a setter values
  protected Object    m_objTopLevelInstance = null;         // Toplevel instance to use if not null
  private Object      m_objCollection = null;               // Collection object if property takes a collection

  private Stack       m_objStack;                           // Stack of current object instances
  private Stack       m_stackObjNames;                      // Corresponding object names
  private Stack       m_stackMethods;                       // Stack of method maps
  private Stack       m_stackCollectionsTmi;                // Stack of collection methods
  private Stack       m_stackObjCollections;                // Stack of collection objects
  private Stack       m_stackParentage;                     // tag parentage stack

  private TagMethodInfo m_tagInfo = null;                   // Current tag
  private TagMethodInfo m_collectionTagInfo = null;         // Set only for Collections

  protected String      m_strTopLevelClassName;               // Class name for the top level class
  protected Class       m_clsTopLevelClass;                   // Class object of the top level class

  private String      m_strCurTagName;                      // The current xml tag being parsed

  private String      m_strCurObjName = "";                 // Name of the current object

  protected String      m_strTopLevelElementName;									// The element that represents the toplevel bean handler

  private String      m_strDateFormat = VwDate.USADATE_TIME;

  private XMLReader   m_saxp = null;                        // Sax parser

  private ResourceBundle m_msgs = ResourceBundle.getBundle( "resources.properties.xmlmsgs" , Locale.ENGLISH );

  private String[]   m_astrBoolValues = { "y", "yes", "true", "1" };

  private VwDelimString  m_dlmsXmlNs = null;
  private boolean         m_fExpandMacros = true;
  private boolean         m_fUseAttributeModel = false;         // if true, treat attributes as class properties
  private boolean         m_fValidate = false;
  private boolean         m_fSetClearDirtyFlag = true;          // Clear dirty flag if true


  static
  {
    synchronized ( s_objSemi )
    {
      s_mapPrimTypes = Collections.synchronizedMap( new HashMap<String, String>() );  // package list for collections of user def objects
      buildPrimTypesMap();
    }
  }

  /**
   * Default constructor
   * @throws Exception
   */
  public VwXmlToBean() throws Exception
  { doConstructorSetup( null, false ); }



  public VwXmlToBean( Class clsTopLevelBean, boolean fValidate ) throws Exception
  { doConstructorSetup( clsTopLevelBean, fValidate  ); }

  /**
   * Constructor - Introspects the bean and caches the the Method descriptiors for the
   * life of the JVM.
   *
   * @param clsTopLevelBean The Top level bean for this document type
   * @param fValidate if true use a validating parser ( expects a DTD ) else don't validate.<BR>
   * NOTE! If this param is false, the xml document is still checked to insure that
   * it is well formed.
   *
   * @exception Exception if any introspection errors occur
   */
  public VwXmlToBean( Class clsTopLevelBean, boolean fValidate, URL urlSchema ) throws Exception
  {
    m_urlSchema = urlSchema;

    doConstructorSetup( clsTopLevelBean, fValidate );
  }


  /**
   * Constructor - Introspects the bean and caches the the Method descriptiors for the
   * life of the JVM.
   *
   * @param clsTopLevelBean The Top level bean for this document type
   * @param fValidate if true use a validating parser ( expects a DTD ) else don't validate.<BR>
   * NOTE! If this param is false, the xml document is still checked to insure that
   * it is well formed.
   * @param schema an XML Schema instance
   * @exception Exception if any introspection errors occur
   */
  public VwXmlToBean( Class clsTopLevelBean, boolean fValidate, Schema schema ) throws Exception
  {
    m_schema = schema;
    if ( s_mapObjects != null )
      s_mapObjects.clear();

    doConstructorSetup( clsTopLevelBean, fValidate );
  }

  private void doConstructorSetup( Class clsTopLevelBean, boolean fValidate )  throws Exception
  {
    m_fValidate = fValidate;

    // Save class name and Class object for the top level bean

    if ( clsTopLevelBean != null )
    {
	    m_strTopLevelClassName  = getObjName( clsTopLevelBean );

	    m_clsTopLevelClass = clsTopLevelBean;
	    addPackageClass( m_clsTopLevelClass );

	    // Get the map of methods for the top level class

	    m_mapCurObjMethods = (HashMap)s_mapObjects.get( m_strTopLevelClassName.toLowerCase( ) );

    }

    m_listTopLevelObjects = new ArrayList();


    m_objStack = new Stack();
    m_stackObjNames = new Stack();
    m_stackMethods = new Stack();
    m_stackCollectionsTmi = new Stack();
    m_stackObjCollections = new Stack();
    m_stackParentage = new Stack();


  } // end VwXmlToBean1()


  /**
   * Resets the bean List for subseqent parses
   */
  public void reset() throws Exception
  {
    m_objTopLevelInstance = null;
    m_listTopLevelObjects = new ArrayList();

    init();

  }


  public void setUnknownElementHanlder( UnknownElementHandler unknownElementHandler )
  { m_unknownElementHandler = unknownElementHandler; }


  /**
   * DVOS that extend VwDVOBase dirty flag will be cleared unless property is set to false
   *
   * @param fSetClearDirtyFlag
   */
  public void setClearDirtyFlag( boolean fSetClearDirtyFlag )
  { m_fSetClearDirtyFlag = fSetClearDirtyFlag ; }


  /**
   * Get the state of the cleardirty flag
   * @return
   */
  public boolean isClearDirtyFlag()
  { return m_fSetClearDirtyFlag; }


  /**
   * Parses an xml document as a file into the toplevel class defined in the constructor
   *
   * @param fileXML The XML document as a file to parse
   *
   * @return List of one or top levele beans as defined in the xml document
   *
   * @exception Exception if any parse errors occur
   */
  public List parse( File fileXML ) throws Exception
  {

    reset();

    // Get the map of methods for the top level class

    //m_mapCurObjMethods = (HashMap)s_mapObjects.get( m_strTopLevelClassName );

    InputSource ins = new InputSource( new FileReader( fileXML ) );

    m_saxp.parse( ins );

    return m_listTopLevelObjects;


  }// end parse()


  /**
   * Parses an xml document as a file into the toplevel class defined in the constructor
   *
   * @param inSource The InputSource representing the xml stream
   *
   * @return List of one or top levele beans as defined in the xml document
   *
   * @exception Exception if any parse errors occur
   */
  public List parse( InputSource inSource ) throws Exception
  {

    reset();

    // Get the map of methods for the top level class

    m_mapCurObjMethods = (HashMap)s_mapObjects.get( m_strTopLevelClassName.toLowerCase() );

    m_saxp.parse( inSource );

    return m_listTopLevelObjects;


  }// end parse()


  /**
   * Parses an xml document referenced by the fileXML object into the top level bean
   * instance referenced by the second parameter
   *
   * @param fileXML The XML document as a file to parse
   * @param objTopLevelInstance The Topl level bean instance to parse the xml contents into
   *
   * @exception Exception if any parse errors occur
   */
  public void parse( File fileXML, Object objTopLevelInstance ) throws Exception
  {
    reset();

    m_objTopLevelInstance = objTopLevelInstance;

    // Get the map of methods for the top level class

    m_mapCurObjMethods = (HashMap)s_mapObjects.get( getObjName( objTopLevelInstance ).toLowerCase() );

    InputSource ins = new InputSource( new FileReader( fileXML ) );

    m_saxp.parse( ins );


  }// end parse()


  /**
   * Parses an xml document contained in a string into List java bean(s)
   *
   * @param strXml The xml document contained in a String object
   *
   * @return List of one or top levele beans as defined in the xml document
   *
   * @exception Exception if any parse errors occur
   */
  public List parse( String strXml ) throws Exception
  {
    reset();

    // Get the map of methods for the top level class

    m_mapCurObjMethods = (HashMap)s_mapObjects.get( m_strTopLevelClassName.toLowerCase() );


    InputSource ins = new InputSource( new StringReader( strXml ) );

    m_saxp.parse( ins );

    return m_listTopLevelObjects;

  }// end parse()


  /**
   * Parses an xml document contained in a string into object instance specified in the second parameter
   *
   * @param strXml The xml string to parse into the eban
   * @param objTopLevelInstance The Topl level bean instance to parse the xml contents into
   *
   * @exception Exception if any parse errors occur
   */
  public void parse( String strXml, Object objTopLevelInstance ) throws Exception
  {
    reset();

    m_objTopLevelInstance = objTopLevelInstance;

    // Get the map of methods for the top level class

    m_mapCurObjMethods = (HashMap)s_mapObjects.get( getObjName( objTopLevelInstance ).toLowerCase() );

    InputSource ins = new InputSource( new StringReader( strXml ) );

    m_saxp.parse( ins );


  }// end parse()


  /**
   * Parses an xml document contained in a string into object instance specified in the second parameter
   *
   * @param inSource The InputSource representing the xml stream
   * @param objTopLevelInstance The Topl level bean instance to parse the xml contents into
   *
   * @exception Exception if any parse errors occur
   */
  public void parse( InputSource inSource, Object objTopLevelInstance ) throws Exception
  {
    reset();

    m_objTopLevelInstance = objTopLevelInstance;

    // Get the map of methods for the top level class

    m_mapCurObjMethods = (HashMap)s_mapObjects.get( getObjName( objTopLevelInstance ).toLowerCase() );


    m_saxp.parse( inSource );


  }// end parse()


  /**
   * Sets the XML element name thats associated with the top level bean name if the element name is different
   * than the class name of the toplevel bean
   *
   * @param strTopLevelElementName The element name that will represent the top levelbean name
   */
  public void setTopLevelElementName( String strTopLevelElementName )
  { m_strTopLevelElementName = strTopLevelElementName; }

  /**
   * Initialize any schema setup
   */
  private void init() throws Exception
  {
    if ( m_saxp == null )
    {
      m_saxp = (XMLReader)Class.forName("org.apache.xerces.parsers.SAXParser").newInstance();

      m_saxp.setContentHandler( this );

      m_saxp.setFeature( "http://xml.org/sax/features/validation", m_fValidate );
    }

    m_mapCurObjMethods = (HashMap)s_mapObjects.get( m_strTopLevelClassName.toLowerCase() );

    if ( m_strTopLevelElementName != null )
      m_strTopLevelClassName = m_strTopLevelElementName;


    if ( m_mapCurObjMethods == null || !m_mapCurObjMethods.containsKey( m_strTopLevelClassName.toLowerCase() ) )
    {
      introspect( m_clsTopLevelClass );

      m_mapCurObjMethods = (HashMap)s_mapObjects.get( m_strTopLevelClassName.toLowerCase() );
    }

    Object objSchemaKey = null;

    if ( m_urlSchema != null || m_schema != null )
    {
      objSchemaKey = (m_urlSchema != null)? m_urlSchema : m_schema;
      s_mapTagHandlers = (HashMap)s_mapTagHandlersBySchema.get( objSchemaKey );

      if ( s_mapTagHandlers == null )
      {
        s_mapTagHandlers = new HashMap();
        s_mapTagHandlersBySchema.put( objSchemaKey , s_mapTagHandlers );

        if ( m_urlSchema != null )
        {
          if ( !s_mapSchemas.containsKey( m_urlSchema.getPath() ) )
          {
            processSchema( m_urlSchema );
          }
        }
        else
          processXMLSchema( m_schema );
      }
      else
      {
        s_mapTagHandlers = (HashMap)s_mapTagHandlersBySchema.get( objSchemaKey );
        String strTopLevelAlias = (String)s_mapTopLevelTypes.get( m_strTopLevelClassName );

        if ( strTopLevelAlias != null )
          m_strTopLevelClassName = strTopLevelAlias;

      }
    }

  } // end init

  /**
   * De-serialize an XML document to the Java object specidied in the top level class parameter
   *
   * @param inps The input source of the XML document
   * @param clsTopLevel The top level class type of the object to be deserialized in to
    *
   * @return an object of the top level class type containing the deSerialized XML document
   */
  public Object deSerialize( InputSource inps, Class clsTopLevel ) throws Exception
  {
    m_strTopLevelClassName  = getObjName( clsTopLevel );

    m_clsTopLevelClass = clsTopLevel;
    addPackageClass( m_clsTopLevelClass );

    Object objTopLevel = clsTopLevel.newInstance();
    reset();

    parse( inps, objTopLevel );

    return objTopLevel;

  } // end deSerialize()


  /**
   * De-serialize an XML document to the Java object specified in the top level class parameter
   *
   * @param inps The input source of the XML document
   * @param clsTopLevel The top level class type of the object to be deserialized in to
   * @param urlSchema An XML schema/DTD to use for deSerialization help (May be null )
   *
   * @return an object of the top level class type containing the deSerialized XML document
   */
  public Object deSerialize( InputSource inps, Class clsTopLevel, URL urlSchema ) throws Exception
  {
    m_urlSchema = urlSchema;
    return deSerialize( inps, clsTopLevel );
  }

  /**
   * De-serialize an XML document to the Java object specified in the top level class parameter
   *
   * @param inps The input source of the XML document
   * @param clsTopLevel The top level class type of the object to be de-serialized in to
   * @param urlSchema An XML schema/DTD to use for deSerialization help (May be null )
   *
   * @return an object of the top level class type containing the deSerialized XML document
   */
  public Object deSerialize( InputSource inps, Class clsTopLevel, Schema schema ) throws Exception
  {
    m_schema = schema;
    return deSerialize( inps, clsTopLevel );
  }

  /**
   * If true (the default) any tag or attribute data value that has the form ${propertyname} will automaticially
   * be resolved ( if avaliable ) else the original string data will be returned
   * @param fExpandMacros
   */
  public void setExpandMacros( boolean fExpandMacros )
  { m_fExpandMacros = fExpandMacros; }


  /**
   * Sets a behavioural attribut for the deSerializer
   * @param strURIFeature The uri of the feature to set
   * @param fEnable if true enable the feature, else disable the feature
   *
   * @throws Exception If the feature requested is not valid
   */
  public void setFeature( String strURIFeature, boolean fEnable ) throws Exception
  {
    if ( strURIFeature.equals( XmlDeSerializer.VALIDATE ) )
      m_saxp.setFeature( "http://xml.org/sax/features/validation", fEnable );
    else
    if ( strURIFeature.equals( XmlDeSerializer.ATTRIBUTE_MODEL ) )
      m_fUseAttributeModel = fEnable;
    else
    if ( strURIFeature.equals( XmlDeSerializer.EXPAND_MACROS ) )
      m_fExpandMacros = fEnable;
  }


  /**
   * Sets the date format string for date objects
   *
   */
  public void setDateFormat( String strDateFormat )
  { m_strDateFormat = strDateFormat; }



  /**
   * Sets a Java class type to handel deserialazation
   *
   * @param strElementName The xml element name
   * @param clsHandler
   */
  public void setElementHandler( String strXmlTagName, Class clsAlias )
  { s_mapTagHandlers.put( strXmlTagName.toLowerCase(), clsAlias ); }



  /**
   * Assocaite a Class name that is different from the xml tag name
   * @param strXmlElementName
   * @param strURI The URI associated with this xml element
   * @param clsHandler The class to handle this element
   */
  public void setElementHandler( String strXmlTagName, String strURI, Class clsAlias )
  { s_mapTagHandlers.put( (strURI + strXmlTagName).toLowerCase(), clsAlias ); }

  /**
   * Gets the tag handler class for the xml tag specified. (May be null). This is used as
   * an override. The default behaviour is the name of the class and xml tag are the same.
   *
   * @param strXmlTagName The name of the xml tag to receive the class handler for
   *
   * @return
   */
  public Class getTagHandler( String strXmlTagName )
  { return (Class)s_mapTagHandlers.get( strXmlTagName.toLowerCase( ) ); }


  /**
   * Gets the tag handler class for the xml tag specified. (May be null). This is used as
   * an override. The default behaviour is the name of the class and xml tag are the same.
   *
   * @param strXmlTagName The name of the xml tag to receive the class handler for
   * @param strURI The namespace uri associated with this tag
   *
   * @return
   */
  public Class getTagHandler( String strXmlTagName, String strURI )
  { return (Class)s_mapTagHandlers.get( (strURI + strXmlTagName).toLowerCase( ) ); }



  /**
   * Registers a listener for an XML close element event
   *
   * @param strElementName The name of the XML element to listen for
   * @param strURI The associated URI for this element (may be null if N/A)
   * @param iOpenTagListener The implementing listener class
   *
   * @throws Exception
   */
  public void setCloseElementListener( String strElementName, String strURI, XmlCloseElementListener iCloseElementListener ) throws Exception
  {

    String strRegTag = strElementName;

    if ( strURI != null )
      strRegTag = strURI + strRegTag;


    m_mapCloseTagListeners.put( strRegTag, iCloseElementListener );


  } // end addCloseTagListener()


  /**
   * Registers an open element listener. Only one listener is allowed per element name.
   *
   * @param strElementName The name of the XML element to listen for
   * @param strURI The associated URI for this element (may be null if N/A)
   * @param iOpenTagListener The implementing listener class
   *
   * @exception Exception if the element to register already has been registered
   *
   */
  public void setOpenElementListener( String strElementName, String strURI, XmlOpenElementListener iOpenElementListener ) throws Exception
  {

    String strRegTag = strElementName;

    if ( strURI != null )
      strRegTag = strURI + strRegTag;

    m_mapOpenTagListeners.put( strRegTag, iOpenElementListener );

  } // end addOpenTagListener()


  /**
   * Removes a registered IVwOpenTagListener object.
   *
   * @param strTagName The name of the tag to remove listener for
   *
   */
  public void removeOpenElementListener( String strTagName )
  { m_mapOpenTagListeners.remove( strTagName ); }


  /**
   * Removes a registered IVwCloseTagListener object.
   *
   * @param strTagName The name of the tag to remove listener for
   *
   */
  public void removeCloseElementListener( String strTagName )
  { m_mapCloseTagListeners.remove( strTagName ); }


  /**
   * Fires the custom tag listener for open tag event
   */
  public XmlOpenElementEvent fireOpenTagEvent( String strKey, String strLocalName, String strQName, String strUri, Attributes attrs  )
  {
    XmlOpenElementListener openListener =
     (XmlOpenElementListener)m_mapOpenTagListeners.get( strKey );

    XmlOpenElementEvent te = new XmlOpenElementEvent( strLocalName, strQName, strUri, attrs, (Stack)m_stackParentage.clone() );
    openListener.xmlTagOpen( te );

    return te;

  } // end fireCloseTagEvent()


  /**
   * Fires the custom tag listener event listener
   */
  public XmlCloseElementEvent fireCloseTagEvent( String strURIKey, String strLocalName, String strQName, String strURI, Object objBean, String strData  )
  {
    String strKey = null;

    if ( strURIKey != null )
      strKey = strURIKey;
    else
      strKey = strLocalName;

    XmlCloseElementListener closeListener =
     (XmlCloseElementListener)m_mapCloseTagListeners.get( strKey );

    XmlCloseElementEvent te = new XmlCloseElementEvent( strLocalName, strQName, strURI, objBean, strData, null, (Stack)m_stackParentage.clone() );
    closeListener.xmlTagClosed( te );

    return te;

  } // end fireCloseTagEvent()


  /**
   * Sets a tag object alias.
   *
   * @param strTagName   The name of the xml tag that is an alias for an object
   * @param strClassName The name of the class the tag is in
   * @param strObjName       The name of the class this tag is an alias for
   */
  /*
  public void setObjAlias( String strTagName, String strClassName, String strObjName ) throws Exception
  {

    Map mapCurObjMethods = (HashMap)s_mapObjects.get( strClassName.toLowerCase() );

    if ( mapCurObjMethods == null )
    {
      Class clsType = determineType( strClassName, "" );

      if ( clsType != null )
        introspect( clsType );
      else
        return;

    }

    mapCurObjMethods = (HashMap)s_mapObjects.get( strClassName.toLowerCase() );

      //throw new Exception( "Could not find definition for object '" + strObjName + "'" );

    TagMethodInfo tmi = (TagMethodInfo)mapCurObjMethods.get( strTagName.toLowerCase() );

    if ( tmi == null )
      return;

      //throw new Exception( "Could not find method definition for tag '" + strTagName + "'" );

    tmi.m_strParamClassName = strTagName;

    m_mapTagHandlers.put( strTagName, strObjName );

  } // end setObjALias()
  */

  /**
   * Update collection info
   *
   * @param strTagName   The name of the xml tag that is an alias for an object
   * @param strClassName The name of the class the tag is in
   */
  private void updateCollection( String strTagName, String strClassName, String strType, boolean fUseType ) throws Exception
  {
    String strOrigClassName = strClassName;


    Map mapCurObjMethods = (HashMap)s_mapObjects.get( strClassName.toLowerCase() );

    if ( mapCurObjMethods == null )
    {
      if ( strType.equalsIgnoreCase( "string") )
        return;

      Class cls = determineType( strOrigClassName, "" );

      if ( cls == null )
        throw new Exception( "Missing a package name for class: " + strClassName + ", use the addPackageList method");

      if ( ! ( cls == String.class ) )
        introspect( cls );

      mapCurObjMethods = (HashMap)s_mapObjects.get( strClassName.toLowerCase() );


      if ( mapCurObjMethods == null )
        return;

    }

    TagMethodInfo tmi = (TagMethodInfo)mapCurObjMethods.get( strTagName.toLowerCase() );

    if ( tmi == null )
    {
      tmi = (TagMethodInfo)mapCurObjMethods.get( strType.toLowerCase() );
      if ( tmi == null )
        return;

      mapCurObjMethods.put( strTagName.toLowerCase(), tmi );

    }

    if ( fUseType )
      tmi.m_strParamClassName = strType;
    else
      tmi.m_strParamClassName = strTagName;

    if ( tmi.m_clsParamType == null )
    {
      tmi.m_clsParamType = determineType( strType, "" );

      tmi.m_fIsSimpleType = VwBeanUtils.isSimpleType( tmi.m_clsParamType );

    } // end if

  } // end updateCollection



  /**
   * Update TagMethodInfoEntry
   *
   * @param strTagName   The name of the xml tag that is an alias for an object
   * @param strClassName The name of the class the tag is in
   */
  private void updateTmi( String strTagName, String strClassName, int nType ) throws Exception
  {
    String strOrigClassName = strClassName;


    Map mapCurObjMethods = (HashMap)s_mapObjects.get( strClassName.toLowerCase() );

    if ( mapCurObjMethods == null )
    {
      Class cls = determineType( strOrigClassName, "" );

      if ( ! ( cls == String.class ) )
        introspect( cls );

      mapCurObjMethods = (HashMap)s_mapObjects.get( strClassName.toLowerCase() );


      if ( mapCurObjMethods == null )
        return;

      //throw new Exception( "Could not find definition for object '" + strClassName + "'" );
    }

    TagMethodInfo tmi = (TagMethodInfo)mapCurObjMethods.get( strTagName );

    if ( tmi == null )
      return;

    if ( tmi.m_nType != VwXmlToBean.COLLECTION )
      tmi.m_nType = nType;
    else
      tmi.m_fAnyType = true;

  } // end updateTmi()

  /**
   * Sets the allowable values found in tag data that will qualify a true boolean property.
   * Any other value would set the property to false. The String is a comma separated list
   * of values. The default setting is "y,yes,true,1" (case insensitive)
   *
   * @param strTrueValueList The comma separated list of values that will specify a true boolean property
   */
  public void setTrueBooleanTransform( String strTrueValueList )
  {
    VwDelimString dlms = new VwDelimString( ",", strTrueValueList );
    m_astrBoolValues = dlms.toStringArray();

  } // end setTrueBooleanTransform



  /**
   * Gets the toplevel bean list or null if the single instance version of parse methos was used
   */
  public List getBeanList()
  { return m_listTopLevelObjects; }


  /**
   * A recursive method to introspect a class and any other class it references. This method
   * builds a map of Method objects for each class encountered. It looks for class methods or
   * properties that start with set or add, all other methods are ignored
   *
   * @param classToIntrospect The class to introspect
   */
  protected void introspect( Class classToIntrospect ) throws Exception
  {

    String strClassName = classToIntrospect.getName();

    String strClassFullName = strClassName;   // Preserve with package name for alias lookup

    strClassName = getObjName( classToIntrospect );

    Class classAlias = (Class)s_mapTagHandlers.get( strClassName.toLowerCase() );

    if ( classAlias != null )
    {
      classToIntrospect = classAlias;
      strClassName = getObjName( classToIntrospect );

    }

    HashMap mapClassMethods = (HashMap)s_mapObjects.get( strClassName.toLowerCase( ) );

    if ( mapClassMethods != null )
    {
      return;     // Already introspected
    }

    mapClassMethods = new HashMap();

    // Each class will store a map of its' methods
    s_mapObjects.put( strClassName.toLowerCase(), mapClassMethods );


    MethodDescriptor[] aMethods = Introspector.getBeanInfo( classToIntrospect ).getMethodDescriptors();

    for ( int x = 0; x < aMethods.length; x++ )
    {
      String strMethodName = aMethods[ x ].getName();

      if ( strMethodName.startsWith( "add" ) || strMethodName.startsWith( "set" ) )
      {
        String strOrigMethodName = strMethodName;

        // Remove the the prefix string "add" or "set" so it maps to the xml tag
        if ( strMethodName.length() > 3 )
        {
          strMethodName = strMethodName.substring( 3 );
        }
        else
        {
          continue;
        }


        if ( mapClassMethods.get( strMethodName ) != null )
        {
          continue;     // This is a collection method previously defined
        }

        Method method =  aMethods[ x ].getMethod();

        Class[] aParamTypes = method.getParameterTypes();
        if ( aParamTypes.length == 0 )
        {
          continue;
        }

        if ( aParamTypes.length > 1 )  // Only look a methods/accessors with one parameter
        {
          if ( strOrigMethodName.equals( "setAttributes" ) &&
               aParamTypes[ 0 ] == String.class &&
               aParamTypes[ 1 ] == Attributes.class )
            mapClassMethods.put( "attributes", method );

          continue;
        }
        // See if ther'es an alias for this method

        String strAlias = (String)s_mapMethodAlias.get( strClassFullName + strMethodName );

        if ( strAlias == null )
          strAlias = strMethodName;


        Class clsParam = aParamTypes[ 0 ];
        boolean fHasTagHandler = false;

        if ( s_mapTagHandlers.containsKey( getObjName( clsParam ).toLowerCase() ))
        {
          fHasTagHandler = true;
          clsParam = (Class)s_mapTagHandlers.get( strAlias.toLowerCase( ) );
        }


        if ( VwBeanUtils.isSimpleType( clsParam ) )
        {
          mapClassMethods.put( strMethodName.toLowerCase(), new TagMethodInfo( strAlias, method, clsParam, SIMPLE, fHasTagHandler )  );
        }
        else
        if ( !VwBeanUtils.isCollectionType( clsParam ) )
        {
          mapClassMethods.put( strMethodName.toLowerCase(), new TagMethodInfo( strAlias, method, clsParam, OBJECT, fHasTagHandler )  );

          String strParamClassName = getObjName( clsParam );

          // Only introspect if we have a new class
          if ( !s_mapObjects.containsKey( strParamClassName.toLowerCase() ) )
          {
            introspect( clsParam );
          }
        }
        else
        {
          describeCollectionMethod( strOrigMethodName, classToIntrospect, clsParam,
                                    null, fHasTagHandler );

        }

      } // end if

    } // end for()

  } // end introspect


  /**
   * Adds the packages that will be used for dynamicially creating instances of user classes
   * for use in collections.
   *  <br>This list is static and once added will remain in effect for all
   * instances of the class for the life of the jvm.
   * <br>NOTE! This method must be used if a bean
   * has properties that take collections of non primitive base object types.
   *
   * @param cls A java class object that represents a package for resovling user types
   */
  public static void addPackageClass( Class cls )
  {

    String strName = cls.getName();
    int nPos = strName.lastIndexOf( '.' );
    if ( nPos >= 0 )
      strName = strName.substring( 0, nPos );

    if ( s_mapPackages.containsKey( strName ) )
      return;

    s_mapPackages.put( strName, null );

  } // end  setPackageList()

  public static void addPackageClass( Class[] aCls )
  {
    for ( int x= 0; x < aCls.length; x++ )
      addPackageClass( aCls[ x ] );
  }

  /**
   * Clears the contents of the package list used for collection class resolutions
   */
  public static void clearPackageList()
  { s_mapPackages.clear(); }


  /**
   * Forces the use of a difeerent setter method to be invoked for the name of the element specified. This is
   * usefull for using a common setter method on a super class type
   *
   * @param strLocalName The local name of the xml element
   * @param strURI The uri namespace of the element (may be null)
   * @param strSetterAliasName The setter method name to use
   */
  public void addObjectSetterAlias( String strLocalName, String strURI, String strSetterAliasName )
  {
    if ( strURI != null )
      s_mapMethodSetterAlias.put( (strURI + strLocalName).toLowerCase(), strSetterAliasName );
    else
      s_mapMethodSetterAlias.put( strLocalName.toLowerCase(), strSetterAliasName );

  } // end addObjectSetterAlias()

  /**
   * Describes a method that takes a collection type class as it's parameter. The parser needs to
   * know the class type of the object that will be stored in the collection
   *
   * @param strCollectionMethodName The name of the method that takes a collection as its parameter
   * @param clsCollectionMethod The Class of the object containing the method
   * @param clsCollection The Clss of the collection type. I.E. java.util.ArrayList.class
   * @param clsCollectionContains The Class of the object going into the collection<BR>
   * I.E. java.lang.Integer.class or somepackage.SomeComplexObject.class
   *
   */
  public void describeCollectionMethod( String strCollectionMethodName,
                                        Class clsCollectionMethod,
                                        Class clsCollection,
                                        Class clsCollectionContains, boolean fObjAlias ) throws Exception
  {

    if ( !strCollectionMethodName.startsWith( "set" ) &&
         !strCollectionMethodName.startsWith( "add" ) )
    {
      throw new Exception( ResourceBundle.getBundle( "resources.properties.xmlmsgs" ).getString( "IllegalMethodName" ) );
    }

    Method[] methods = clsCollectionMethod.getMethods();

    Method method = null;

    for ( int x = 0; x < methods.length; x++ )
    {
      if ( methods[ x ].getName().equalsIgnoreCase( strCollectionMethodName ) )
      {
        method = methods[ x ];
        break;
      }

    }

    if ( method == null )
    {
      ResourceBundle rb = ResourceBundle.getBundle( "resources.properties.xmlmsgs" );
      String strMsg = rb.getString( "MethodNotFound" );

      strMsg = VwExString.replace( strMsg, "%1", strCollectionMethodName )
             + " " + clsCollectionMethod.getName();

      throw new Exception( strMsg );
    }

    boolean isGenericType = VwBeanUtils.isGenericParameterType( method, 0  );

    if ( isGenericType )
    {
      clsCollectionContains = Class.forName( VwBeanUtils.getGenericParameterType( method, 0  ) );
    }

    strCollectionMethodName = strCollectionMethodName.substring( 3 );

    Class clsArray = null;
    if ( clsCollection.isArray() )
    {
      String strName = clsCollection.getName();

      if ( strName.length() == 2 )
      {
        clsArray = determineType( strName, "" );
        if ( clsArray == null )
        {
          throw new Exception( "Array class '" + strName + "' could not be found" );
        }

      }
      else
      {
        strName = strName.substring( 2 );
        strName = strName.substring( 0, strName.length() - 1 );
        clsArray = Class.forName( strName );
      }

      clsCollection = ArrayList.class;
      clsCollectionContains = clsArray;

    }
    else
    if ( clsCollection == Map.class )
    {
      clsCollection = HashMap.class;
    }
    else
    if ( clsCollection == List.class )
    {
      clsCollection = ArrayList.class;
    }

    TagMethodInfo tmi = new TagMethodInfo( strCollectionMethodName, method, clsCollectionContains, COLLECTION, fObjAlias );
    tmi.m_clsCollectionType = clsCollection;
    tmi.m_clsParamType = clsCollectionContains;
    tmi.m_clsArray = clsArray;
    tmi.m_fIsSimpleType = true;     // The defualt for now

    if ( java.util.Dictionary.class.isAssignableFrom( clsCollection ) ||
         Map.class.isAssignableFrom( clsCollection ) ||
         java.util.AbstractMap.class.isAssignableFrom( clsCollection ) )
    {
      tmi.m_fIsPut = true;
      tmi.m_methodCollection = clsCollection.getMethod( "put", new Class[] { Object.class, Object.class } );

    }
    else
    if ( List.class.isAssignableFrom( clsCollection ) ||
         java.util.AbstractList.class.isAssignableFrom( clsCollection ) )
    {
      tmi.m_fIsPut = false;
      tmi.m_methodCollection = clsCollection.getMethod( "add", new Class[] { Object.class } );

    }

    // See if ther'es an alias for this method

    String strAlias = (String)s_mapMethodAlias.get( clsCollectionMethod.getName()
                                                    + strCollectionMethodName );

    if ( strAlias == null )
    {
      strAlias = strCollectionMethodName;
    }

    String strClassName = getObjName( clsCollectionMethod );

    HashMap mapClassMethods = (HashMap)s_mapObjects.get( strClassName.toLowerCase() );

    if ( mapClassMethods == null )
    {
      mapClassMethods = new HashMap();
      s_mapObjects.put( strClassName, mapClassMethods );

    }

    mapClassMethods.put( strAlias.toLowerCase(), tmi );

    if ( clsCollectionContains == null )
    {
      return;
    }

    boolean fSimpleType = VwBeanUtils.isSimpleType( clsCollectionContains );

    if ( fSimpleType )
    {
      tmi.m_fIsSimpleType = true;
      tmi.m_strParamClassName = strAlias;

    }
    else
    {
      tmi.m_fIsSimpleType = false;

    }
    // If this a collection of objects, then we need to introspect the object
    if ( !fSimpleType &&
         !VwBeanUtils.isCollectionType( clsCollectionContains ) )
    {
      introspect( clsCollectionContains );
    }

  } // end describeCollectionMethod()


  /**
   * See if we can determine the class type from the tag name.
   * @param strName
   * @return
   * @throws Exception
   */
  private Class determineType( String strName, String strURI  ) throws Exception
  {
    Class clsType = null;

    String strKey = null;

    if ( strURI.length() > 0 )
      strKey = strURI + strName;
    else
      strKey = strName;

    clsType = (Class)s_mapTagHandlers.get( strKey.toLowerCase() );

    if ( clsType != null )
      return clsType;

    String strClassName = strName;

    strClassName = Character.toUpperCase( strClassName.charAt( 0 ) ) +
                   strClassName.substring( 1 );


    // Append the tag name to the package list to see if this is a user defined class
    for ( Iterator iList = s_mapPackages.keySet().iterator(); iList.hasNext(); )
    {
      String strPckg = (String)iList.next();
      try
      {
        clsType = Class.forName(  strPckg + "." + strClassName, true, Thread.currentThread().getContextClassLoader() );
        return clsType;

      }
      catch( ClassNotFoundException ex )
      {

      }
    } // end for


    if ( strName.equalsIgnoreCase( "value" ) || strName.equalsIgnoreCase( "string" ) )
      clsType = String.class;
    else
    if ( strName.startsWith( "int" ) )
      clsType = Integer.class;
    else
    if ( strName.equalsIgnoreCase( "[I" ) )
      clsType = Integer.TYPE;
    else
    if ( strName.equalsIgnoreCase( "byte" )  )
      clsType = Byte.class;
    else
    if ( strName.equalsIgnoreCase( "[B" ) )
      clsType = Byte.TYPE;
    else
    if ( strName.equalsIgnoreCase( "char" )  )
      clsType = Character.class;
    else
    if ( strName.equalsIgnoreCase( "[C" ) )
      clsType = Character.TYPE;
    else
    if ( strName.equalsIgnoreCase( "short" ) )
      clsType = Short.class;
    else
    if (strName.equalsIgnoreCase( "[S" ) )
      clsType = Short.TYPE;
    else
    if ( strName.equalsIgnoreCase( "long" ) )
      clsType = Long.class;
    else
    if ( strName.equalsIgnoreCase( "[J" ) )
      clsType = Long.TYPE;
    else
    if ( strName.equalsIgnoreCase( "float" ) )
      clsType = Float.class;
    else
    if ( strName.equalsIgnoreCase( "[F" ) )
      clsType = Float.TYPE;
    else
    if ( strName.equalsIgnoreCase( "double" ) )
      clsType = Double.class;
    else
    if ( strName.equalsIgnoreCase( "[D" ) )
      clsType = Double.TYPE;
    else
    if ( strName.equalsIgnoreCase( "bigdecimal" ) )
      clsType = java.math.BigDecimal.class;
    else
    if ( strName.equalsIgnoreCase( "boolean" ) )
      clsType = Boolean.class;
    else
    if ( strName.equalsIgnoreCase( "[Z" ) )
      clsType = Boolean.TYPE;

    return clsType;

  } // end determineType

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
   * Notification of a new XML tag
   *
   * @param strName The name of the XML tag
   * @param attrList Any attributes that are part of the tag
   */
  public void startElement( String strUri, String strName, String strQName, Attributes attrList ) throws SAXException
  {
    String strTagParent = null;
    String strQTagParent = null;

    if ( strName.equalsIgnoreCase( "include" ))
      handleIncludeOption( strUri, attrList );

    m_mapTagURIs.put( strQName, strUri );


    if ( m_stackParentage.size() > 0 )
    {
      strQTagParent = (String)m_stackParentage.peek();
      int nQPos = strQTagParent.indexOf( ':' );
      if ( nQPos >= 0 )
        strTagParent = strQTagParent.substring( ++nQPos );
      else
        strTagParent = strQTagParent;

    } // end if

    m_stackParentage.push( strQName );

    // See if we're processing an ANY content type for this tag
    if ( m_anyContentType != null )
    {
      m_anyContentType.startElement( strUri, strName, strQName, attrList );
      return;
    }

    m_strCurTagName = strName;

    AttributesImpl listAttr = null;


    if ( attrList.getLength() > 0 )
    {
      if ( listAttr == null )
        listAttr = new AttributesImpl( attrList );
      else
      {
        for ( int x = 0; x < attrList.getLength(); x++ )
        {
          String strAttrValue = attrList.getValue( x );

          if ( m_fExpandMacros )
            strAttrValue = VwExString.expandMacro( strAttrValue );

          listAttr.addAttribute( attrList.getURI( x ), attrList.getLocalName( x ),
                                 attrList.getQName( x ), attrList.getType( x ),
                                 strAttrValue );
        } // end for()

      } // end else


    } // end if

    List listNameSpaces = null;


    // test for namespace declarations
    if ( m_dlmsXmlNs != null )
    {

      TagMethodInfo tmiNameSpace = (TagMethodInfo) m_mapCurObjMethods.get( "namespace" );

      // see if object supports namespaces, if not treat namespace as attribute
      if ( tmiNameSpace != null )
      {
        listNameSpaces = new ArrayList();
      }
      else
      if ( listAttr == null )
        listAttr = new AttributesImpl();

      String strNs = null;

      while ( (strNs = m_dlmsXmlNs.getNext() ) != null )
      {

        int nPos = strNs.indexOf( '=' );

        String strAttrName = strNs.substring( 0, nPos );

        if ( tmiNameSpace != null )
        {
          String strPrefix = strAttrName.substring( 6 );
          listNameSpaces.add( new Namespace( strPrefix, strNs.substring( ++nPos ) ) );
        }
        else
          listAttr.addAttribute( "", strAttrName, strAttrName, "CDATA", strNs.substring( ++nPos ) );
      }

      m_dlmsXmlNs = null;

    }

    if ( strName.equalsIgnoreCase( m_strTopLevelClassName ) && m_stackParentage.size() == 1 )
    {
      // Create instance of the top level class bean

      try
      {
        if ( m_objTopLevelInstance != null )
          m_curObj = m_objTopLevelInstance;
        else
          m_curObj = m_clsTopLevelClass.newInstance();


        m_strCurObjName = m_strTopLevelClassName;

        if ( m_mapCurObjMethods == null )
        {
          introspect( m_clsTopLevelClass );
          m_mapCurObjMethods = (HashMap)s_mapObjects.get( getObjName( m_curObj ).toLowerCase() );

        }
        if ( strUri.length() > 0 )
          setQName( strUri, strQName );

        loadAttributes( listAttr, listNameSpaces );

        return;
      }
      catch( Exception e )
      {
        throw new SAXException( e );
      }

    }

    String strKey = null;

    if ( strUri.length() > 0 )
      strKey = strUri + strName;
    else
      strKey = strName;

    Class clsHandler = (Class)s_mapTagHandlers.get(  strKey.toLowerCase() );
    TagMethodInfo tmi = null;
    String strTagAlias = null;

    if ( m_mapOpenTagListeners.containsKey( strKey ) )
    {
      XmlOpenElementEvent openEvent = fireOpenTagEvent( strKey, strName, strQName, strUri, attrList  );
      clsHandler = openEvent.getTagHandlerClass();

      strTagAlias = openEvent.getTagAlias();
      if ( strTagAlias != null )
        tmi = (TagMethodInfo) m_mapCurObjMethods.get( strTagAlias.toLowerCase() );
      else
        tmi = (TagMethodInfo) m_mapCurObjMethods.get( strName.toLowerCase() );
    }


    if ( tmi == null )
      tmi = (TagMethodInfo) m_mapCurObjMethods.get( strName.toLowerCase() );

    if ( tmi == null )
    {
      strTagAlias = (String)s_mapMethodSetterAlias.get( strName.toLowerCase() );

      if ( strTagAlias == null )
        strTagAlias = (String)s_mapMethodSetterAlias.get( strKey.toLowerCase() );

      if ( strTagAlias != null)
        tmi = (TagMethodInfo)m_mapCurObjMethods.get( strTagAlias.toLowerCase() );

    }

    // if class hander was specified for event, update tagMethodInfo
    if (  clsHandler != null  )
    {
      if ( tmi == null )
      {
        tmi = new TagMethodInfo( strName, null, clsHandler, OBJECT, false );
      }
      else
        tmi.m_clsParamType = clsHandler;

      if ( strTagAlias != null )
      {
        tmi.m_strParamClassName = strTagAlias;
        strName = strTagAlias;
      }
      else
        tmi.m_strParamClassName = m_strCurTagName;

      tmi.m_fIsSimpleType = VwBeanUtils.isSimpleType( tmi.m_clsParamType );

    }


    if ( tmi == null && m_objCollection != null)
    {

      if ( m_objCollection != null &&  m_collectionTagInfo.m_strTagName.equalsIgnoreCase( strTagParent ) )
      {
        try
        {
          if ( m_collectionTagInfo.m_clsParamType == null )
          {
              m_collectionTagInfo.m_clsParamType = determineType( m_strCurTagName, strUri );

              if ( m_collectionTagInfo.m_clsParamType == null )
                return;


              m_collectionTagInfo.m_strParamClassName = getObjName( m_collectionTagInfo.m_clsParamType );
            }

            m_collectionTagInfo.m_fIsSimpleType = VwBeanUtils.isSimpleType( m_collectionTagInfo.m_clsParamType );

            if ( m_collectionTagInfo.m_fIsSimpleType )
              return;

            if ( VwBeanUtils.isSimpleType( determineType( m_strCurTagName, strUri ) ) )
              return;

            m_tagInfo = m_collectionTagInfo;

            // If we have and existing collection save tag info on the stack
            if ( m_collectionTagInfo != null )
              m_stackCollectionsTmi.push( m_collectionTagInfo );

            // If we have and existing collection save the collection instance on the stack
            if ( m_objCollection != null )
              m_stackObjCollections.push( m_objCollection );

            setupObject( listAttr, listNameSpaces );
            if ( strUri.length() > 0 )
              setQName( strUri, strQName );

            return;

        }
        catch( Exception ex )
        {
          throw new SAXException( ex );

        }

      } // end if


      // Reset m_tagInfo if it's not null and it's not a collection
      if ( m_tagInfo != null )
      {
        // If this method takes an object as its parameter create the object.
        if ( m_tagInfo.m_nType == OBJECT &&
             m_tagInfo.m_strParamClassName.equalsIgnoreCase( strName ) )
        {
          try
          {
            setupObject( listAttr, listNameSpaces );
            if ( strUri.length() > 0 )
              setQName( strUri, strQName );

            if ( m_anyContentType != null )
              m_anyContentType.startElement( strUri, strName, strQName, attrList );
          }
          catch( Exception ex )
          {
            throw new SAXException( ex.toString() );
          }
         }
       }

      return;

    } // end if( tmi == null )

    if ( tmi != null )
      m_tagInfo = tmi;

    if ( m_tagInfo == null )
    {
      try
      {
	      if ( m_unknownElementHandler != null )
	        doUnknownElementHandler( strQTagParent, strQName, strUri, listAttr );
      }
      catch( Exception ex )
      {
        throw new SAXException( ex.toString() );

      }
      return;

    }
    try
    {
      if ( (m_tagInfo.m_nType == OBJECT || m_tagInfo.m_nType == MIXED ||
            m_tagInfo.m_nType == ANY)&&
           strName.equalsIgnoreCase( m_tagInfo.m_strParamClassName ) )
      {

        /*
        if ( m_objCollection != null &&  m_collectionTagInfo.m_strTagName.equalsIgnoreCase( strTagParent ) )
        {
          // If we have and existing collection save tag info on the stack
          if ( m_collectionTagInfo != null )
            m_stackCollectionsTmi.push( m_collectionTagInfo );

          // If we have and existing collection save the collection instance on the stack
          if ( m_objCollection != null )
            m_stackObjCollections.push( m_objCollection );

          m_collectionTagInfo.m_fIsSimpleType = false;
          if ( m_collectionTagInfo.m_clsParamType == null )
          {
            m_collectionTagInfo.m_clsParamType = determineType( m_strCurTagName, strUri );
            m_collectionTagInfo.m_strParamClassName = getObjName( m_collectionTagInfo.m_clsParamType );

          }
        }
        */
        setupObject( listAttr, listNameSpaces );
        if ( strUri.length() > 0 )
          setQName( strUri, strQName );

        if ( m_anyContentType != null )
          m_anyContentType.startElement( strUri, strName, strQName, attrList );

        return;

      }
      else
      if ( m_tagInfo.m_nType == COLLECTION )
      {
        // If we have and existing collection save tag info on the stack
        if ( m_collectionTagInfo != null )
          m_stackCollectionsTmi.push( m_collectionTagInfo );

        m_collectionTagInfo = m_tagInfo;

        // If we have and existing collection save the collection instance on the stack
        if ( m_objCollection != null )
          m_stackObjCollections.push( m_objCollection );


        // See if there is an existing set collections fro this object instance
        Map mapObjCollections = (Map)m_mapObjCollections.get( m_curObj );

        // See if there is an open collection for this tag
        if ( mapObjCollections != null )
          m_objCollection = mapObjCollections.get( strName );

        // Create it if nothing exists
        if ( mapObjCollections == null || m_objCollection == null )
        {
          // Create the Collection object
          m_objCollection = m_tagInfo.m_clsCollectionType.newInstance();

          if ( mapObjCollections == null )
          {
            mapObjCollections = new HashMap();
            m_mapObjCollections.put( m_curObj, mapObjCollections );

          }

          mapObjCollections.put( strName, m_objCollection );

        } // end if ( m_objCollection == null )


        // If null, we don't know the collection data type, try to resolve it
        if ( m_tagInfo.m_clsParamType == null )
        {
          m_tagInfo.m_clsParamType = determineType( m_strCurTagName, strUri );
          if ( m_tagInfo.m_clsParamType != null )
          {
            m_tagInfo.m_strParamClassName = m_strCurTagName;
            m_tagInfo.m_fIsSimpleType = VwBeanUtils.isSimpleType(  m_tagInfo.m_clsParamType );
          }
        }

        // if the name of the collection property and the the collection object type are the
        // same then create the object here
        if ( strName.equalsIgnoreCase( m_tagInfo.m_strParamClassName ) && !m_tagInfo.m_fIsSimpleType )
          setupObject( listAttr, listNameSpaces );

        if ( m_anyContentType != null )
          m_anyContentType.startElement( strUri, strName, strQName, attrList );

        if ( strUri.length() > 0 )
          setQName( strUri, strQName );

        return;

      }


    } // end try
    catch( Exception e )
    {
       throw new SAXException( e );
    }


    if ( tmi == null )
    {
      try
      {
	      if ( m_unknownElementHandler != null )
	        doUnknownElementHandler( strQTagParent, strQName, strUri,  listAttr );
      }
      catch( Exception ex )
      {
        throw new SAXException( ex.toString() );

      }
    }
    //else
    //{
      if ( attrList.getLength() > 0 )
        handleAttributes( listAttr, listNameSpaces );

    //}
  } // end startElement()


  /**
   * Includes the references document inline with the current instance
   * @param strUri The uri of the include element
   * @param attrList The attribute list for the incude element
   * @throws Exception
   */
  private void handleIncludeOption( String strUri, Attributes attrList ) throws SAXException
  {
    try
    {
      String strFileToInclude = attrList.getValue( "file" );

      /// see if using XInclude spec
      if ( strFileToInclude == null )
        strFileToInclude = attrList.getValue( "href" );

      // now see if this is an XML Schema include
      if ( strFileToInclude == null )
      {
        if ( attrList.getValue( "schemaLocation" ) != null )
          return; // Let the scheam include tag process this request

        throw new SAXException( "Invalid <include> option, expecting either the file= attribute");
      }
      // first see if the resource store can find this
      URL urlDoc = null;

      if ( strFileToInclude.startsWith( "http:" ) || strFileToInclude.startsWith( "file:" ) )
          urlDoc = new URL( strFileToInclude );
      else
      {

        if ( VwExString.findAny( strFileToInclude, "/\\", 0 ) < 0 ) // no path separaters defined so assume the VwResourceSore can find this
          urlDoc = VwResourceStoreFactory.getInstance().getStore().getDocument( strFileToInclude );

        if ( urlDoc == null ) // see if it's in a different classpath location
          urlDoc = VwDocFinder.findURL( strFileToInclude );

        if ( urlDoc == null ) // assume an absolute directory path
        {
          File file = new File( strFileToInclude );
          if ( file.exists() )
            urlDoc = file.toURL();
        }

      }

      if ( urlDoc == null )
        throw new SAXException( "Cannot file the file '" + strFileToInclude + "' specified in the <include> tag");

      InputSource inSrc = new InputSource( urlDoc.openStream() );


      parseInclude( inSrc );

    }
    catch( Exception ex )
    {
      throw new SAXException( ex.toString() );
    }

  }


  private void parseInclude( InputSource inSrc  ) throws Exception
  {

    XMLReader saxp = (XMLReader)Class.forName("org.apache.xerces.parsers.SAXParser").newInstance();

    saxp.setContentHandler( this );

    saxp.setFeature( "http://xml.org/sax/features/validation", m_fValidate );

    saxp.parse( inSrc );

  }

  /**
   * @param strQName
   * @param listAttr
   */
  private void doUnknownElementHandler( String strQTagParent, String strQName, String strUri, AttributesImpl listAttr ) throws Exception
  {
    if ( m_docUnknownElement == null )
      m_docUnknownElement = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();

    org.w3c.dom.Element element = m_docUnknownElement.createElementNS( strUri, strQName );

    if ( m_eleUnknownParent == null )
    {
      m_eleUnknownParent = element;
      m_eleUnknownCur = element;
      m_strQNUnknownGrandParent = strQTagParent;
    }
    else
      m_eleUnknownCur.appendChild( element );

    int nAttrLen = listAttr.getLength();
    if ( nAttrLen  > 0 )
    {
       for ( int x = 0; x < nAttrLen; x++ )
         element.setAttributeNS( listAttr.getURI( x ), listAttr.getQName( x ), listAttr.getValue( x ) );

    }
    m_eleUnknownCur = element;


  } // end doUnknownElementHandler()


  /**
   * Inokes the setQName menthod if it is defined for this object
   * @param strUri
   * @param strQName
   * @throws Exception
   */
  private void setQName( String strUri, String strQName ) throws Exception
  {
    TagMethodInfo tmi = (TagMethodInfo) m_mapCurObjMethods.get( "qname" );
    if ( tmi == null )
      return;

    String strPrefix = null;

    int nPos = strQName.indexOf( ':' );
    if ( nPos >= 0 )
    {
      strPrefix = strQName.substring( 0, nPos);
      strQName = strQName.substring( ++nPos );

    }
    QName qn = new QName( strPrefix , strUri, strQName  );

    tmi.m_methodSetter.invoke( m_curObj, new Object[]{ qn } );

  } // end setQName()


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

    // See if we're processing an ANY content type for this tag
    if ( m_anyContentType != null )
    {
      m_anyContentType.characters( ach, nStart, nLength );
      return;
    }

    if ( m_curObj == null || m_tagInfo == null )
      return;

    String strVal =  new String( ach, nStart, nLength );

    StringBuffer sbTagData = (StringBuffer)m_mapTagData.get( m_strCurTagName );

    if ( sbTagData == null )
    {
      sbTagData = new StringBuffer( strVal );
      m_mapTagData.put( m_strCurTagName, sbTagData );
    }
    else
      sbTagData.append( strVal );


  } // end characters


  /**
   * Process the closing tag. Decrement the parentage index.
   */
  public void endElement( String strUri, String strName, String strQName ) throws SAXException
  {

    try
    {
      m_stackParentage.pop();

      if ( m_curObj == null )
      {
        return;                                       // No active current object, so just exit
      }

      strUri = (String)m_mapTagURIs.get( strQName );

      if ( m_eleUnknownCur != null )
      {
        if ( m_eleUnknownCur.getTagName().equals( strQName ))
        {
          String strTagData = getTagData( strName );

          if ( strTagData != null )
          {
            m_eleUnknownCur.appendChild( m_docUnknownElement.createTextNode( strTagData ));
            m_mapTagData.remove( strName );
          }

          if ( m_eleUnknownCur == m_eleUnknownParent )
          {
            m_unknownElementHandler.unknownElement( m_curObj, m_strQNUnknownGrandParent, m_eleUnknownParent );
            m_eleUnknownParent = m_eleUnknownCur = null;
          }
          else
            m_eleUnknownCur = (org.w3c.dom.Element)m_eleUnknownCur.getParentNode();

        }

        return;
      }

      String strKey = null;
      boolean fHasURI = false;

      if ( strUri.length() > 0 )
      {
        strKey = strUri + strName;
        fHasURI = true;
      }
      else
      {
        strKey = strName;
      }

      String strTagAlias = null;

      if ( m_mapCloseTagListeners.containsKey( strKey )  )
      {
        XmlCloseElementEvent te = fireCloseTagEvent( strKey, strName, strQName, strUri, m_curObj, getTagData( m_strCurTagName ) );
        strTagAlias = te.getTagAlias();

      }
      else
      if ( fHasURI && m_mapCloseTagListeners.containsKey( strName ) )
      {
        XmlCloseElementEvent te = fireCloseTagEvent( null, strName, strQName, strUri, m_curObj, getTagData( m_strCurTagName ) );
        strTagAlias = te.getTagAlias();

      }

      if ( strTagAlias == null )
      {
        strTagAlias = (String)s_mapMethodSetterAlias.get( strKey.toLowerCase() );
      }

      if ( strTagAlias == null && fHasURI )
      {
        strTagAlias = (String)s_mapMethodSetterAlias.get( strName.toLowerCase() );
      }

      if ( strTagAlias != null )
      {
        strName = strTagAlias;
      }

      // If this is the closing tag for the top level bean, then put the object in the list unless
      // the top level instance was specified
      if ( strName.equalsIgnoreCase( m_strTopLevelClassName ) && m_stackParentage.size() == 0 )
      {

        try
        {
          if ( m_mapObjCollections.containsKey( m_curObj ) )
          {
            doCollectionCleanup();
          }
        }
        catch( Exception ex )
        {
          throw new SAXException( ex.toString() );
        }

        if ( m_objTopLevelInstance == null )
        {
          m_listTopLevelObjects.add( m_curObj );
        }

        if ( m_fSetClearDirtyFlag )
        {
          if ( m_curObj instanceof VwDVOBase )
          {
            ((VwDVOBase)m_curObj).setDirty( false ); // clear dirty flag from setters getting called during load
          }
        }

        m_curObj = null;

      }
      else
      if ( strName.equalsIgnoreCase( m_strCurObjName ) )  // Tag represents the completion of an object
      {

        if ( m_fSetClearDirtyFlag )
        {
          if ( m_curObj instanceof VwDVOBase )
          {
            ((VwDVOBase)m_curObj).setDirty( false ); // clear dirty flag from setters getting called during load
          }
        }

        if ( m_curObj instanceof AnyTypeContent  )
        {
          m_anyContentType.endElement( strUri, strName, strQName );
          ((AnyTypeContent)m_curObj).setContent( m_anyContentType.getParsedMsg() );
          m_anyContentType = null;
        }

        TagMethodInfo tmiObj = (TagMethodInfo)m_mapCurObjMethods.get( strName.toLowerCase() );

        try
        {
          if ( tmiObj != null  )
          {
            invokeSetterMethod( tmiObj, getTagData( m_strCurTagName ) );
          }

          // Call the Set methods for all open collections on this object
          if ( m_mapObjCollections.containsKey( m_curObj ) )
          {
            doCollectionCleanup();
          }
        }
        catch( Exception ex )
        {
          throw new SAXException( ex.toString() );
        }

        if ( m_collectionTagInfo != null )
        {
          // Add object to collection
          if ( strName.equalsIgnoreCase( m_collectionTagInfo.m_strParamClassName ) ||
               ( m_collectionTagInfo.m_clsParamType == null &&
               m_collectionTagInfo.m_strTagName.equalsIgnoreCase( (String)m_stackParentage.peek() ) ) )
          {

            Object[] aParams = null;

            // This is an Object collection. Add the current object to the collection
            // and pop the next object off the stack

            if ( m_collectionTagInfo.m_fIsPut ) // This is a Map collection type
            {
              Object objMapKey = getMapKey( m_curObj );

              aParams = new Object[]{ objMapKey, m_curObj };

            }
            else
            {
              aParams = new Object[]{ m_curObj };
            }

            try
            {
              // Add to the collection
              m_collectionTagInfo.m_methodCollection.invoke( m_objCollection, aParams );
              m_curObj = m_objStack.pop();      // Restore prior current object
              m_strCurObjName = (String)m_stackObjNames.pop();

              // Restore previous objects method map
              m_mapCurObjMethods = (HashMap)m_stackMethods.pop();

              try
              {
                m_objCollection = m_stackObjCollections.pop();
                m_collectionTagInfo = (TagMethodInfo)m_stackCollectionsTmi.pop();
                m_tagInfo = m_collectionTagInfo;
              }
              catch( Exception ex )
              {
                m_objCollection = null;
                m_collectionTagInfo = null;
                m_tagInfo = null;

              }
              return;


            }
            catch( Exception e )
            {

              String strInvokeError = getInvocationError( m_collectionTagInfo.m_methodCollection,
                                                          m_objCollection, aParams );

              throw new SAXException( strInvokeError );
            }

          } // end  if ( m_collectionTagInfo != null )

        } // end ( strName.equals( m_collectionTagInfo.m_strParamClassName )

        // Restore previous objects method map
        m_mapCurObjMethods = (HashMap)m_stackMethods.pop();

        if ( s_mapMethodSetterAlias.containsKey( strKey.toLowerCase() ))
        {
          strName = (String)s_mapMethodSetterAlias.get( strKey.toLowerCase() );
        }

        TagMethodInfo tmi = (TagMethodInfo)m_mapCurObjMethods.get( strName.toLowerCase() );


        // If the property and the object parameter are the same, same then invoke the set method
        // to set the object
        if ( tmi != null )
        {

          Object[] aParams = { m_curObj };  // Setup current object as the parameter to this method

          m_curObj = m_objStack.pop();      // Restore object that belongs to this method

          m_strCurObjName = (String)m_stackObjNames.pop();

          // The current object represents the paramter of the method to be set

          try
          {
            tmi.m_methodSetter.invoke( m_curObj, aParams );
          }
          catch( Exception e )
          {
             String strInvokeError = getInvocationError( tmi.m_methodSetter,
                                                         m_curObj, aParams );

             throw new SAXException( strInvokeError );
          }

        }
        else
        {
          m_objSetter = m_curObj; // this most likely will be used as the setter to the parent tag

          // Restore the object to this objects parent
          m_curObj = m_objStack.pop();      // Restore object that belongs to this method

          m_strCurObjName = (String)m_stackObjNames.pop();
        }
      } // end if ( strName.equals( m_strCurObjName ) )
      else
      {
        if ( m_anyContentType != null )
        {
          m_anyContentType.endElement( strUri, strName, strQName );
          return;
        }

        TagMethodInfo tmi = (TagMethodInfo)m_mapCurObjMethods.get( strName.toLowerCase() );

        if ( tmi == null && m_objCollection != null )
        {
          String strData = getTagData( m_strCurTagName );

          if ( strData == null )
            return;

          if ( m_collectionTagInfo.m_fIsSimpleType || m_collectionTagInfo.m_clsParamType == null )
          {
            // In this case, this is a collection of simple types where the tag name
            // and the property name are the same
            try
            {
               addToSimpleCollection( m_collectionTagInfo );
            }
            catch( Exception ex )
            {
              throw new SAXException( ex );

            }
          }

        }
        else
        if ( tmi != null )
        {
          if ( tmi.m_nType == COLLECTION )
          {
            try
            {
              if ( m_collectionTagInfo.m_fIsSimpleType || m_collectionTagInfo.m_clsParamType == null )
              {
                // In this case, this is a collection of simple types where the tag name
                // and the property name are the same

                addToSimpleCollection( tmi );
              }

              m_objCollection = m_stackObjCollections.pop();
              m_collectionTagInfo = (TagMethodInfo)m_stackCollectionsTmi.pop();
              m_tagInfo = m_collectionTagInfo;
            }
            catch( Exception ex )
            {
              m_objCollection = null;
              m_collectionTagInfo = null;
              m_tagInfo = null;


            }

          } // end if ( tmi.m_nType == COLLECTION )
          else
          if ( tmi.m_nType == OBJECT )
          {

            if ( m_objSetter != null )
            {
              if ( tmi.m_clsParamType.equals( m_objSetter.getClass() ))
              {
                Object[] aParams = { m_objSetter };  // Setup current object as the parameter to this method
                try
                {
                  tmi.m_methodSetter.invoke( m_curObj, aParams );
                }
                catch( Exception e )
                {
                   String strInvokeError = getInvocationError( tmi.m_methodSetter,
                                                               m_curObj, aParams );

                   throw new SAXException( strInvokeError );
                }

                m_objSetter = null;
                return;
              }

            } // end if

            if ( !m_strCurObjName.equalsIgnoreCase( tmi.m_strParamClassName ) )
            {
              return;
            }

            Object[] aParams = { m_curObj };  // Setup current object as the parameter to this method

            m_curObj = m_objStack.pop();      // Restore object that belongs to this method

            m_strCurObjName = (String)m_stackObjNames.pop();

            // The current object represents the paramter of the method to be set

            try
            {
              tmi.m_methodSetter.invoke( m_curObj, aParams );
            }
            catch( Exception e )
            {
               String strInvokeError = getInvocationError( tmi.m_methodSetter,
                                                           m_curObj, aParams );

               throw new SAXException( strInvokeError );
            }

          } // end if
          else
          {

            // This is a property or a method , set its data
            // Only invoke simple param types here
            if ( m_tagInfo.m_nType == SIMPLE )
            {
              try
              {
                invokeSetterMethod( m_tagInfo, getTagData( m_strCurTagName ) );
              }
              catch( Exception e )
              {
                throw new SAXException( e );
              }

            } // end if

          } // end else

        } // end if ( tmi != null )

      } // end else

    } // end try
    finally
    {
      m_mapTagData.remove( m_strCurTagName );
    }

  } // end endElement{}


  /**
   * Get Tag data from StringBuffer
   * @param strTagName The tag name key
   * @return
   */
  private String getTagData( String strTagName )
  {
    StringBuffer sbTagData = (StringBuffer)m_mapTagData.get( strTagName );
    String strTagData = null;

    if ( sbTagData == null )
      return null;

    strTagData = sbTagData.toString().trim();

    if ( strTagData.length() == 0 )
      return null;

    if ( m_fExpandMacros )
      return VwExString.expandMacro( strTagData );

    return strTagData;

  }

  /**
   * Move the attribute values to the current object set properties
   * @param attrs Attributes to be treated as properties
   */
  private void movePropsToObject( Attributes attrs, List listNameSpaces ) throws Exception
  {
    int nLen = 0;

    if ( attrs != null)
      nLen = attrs.getLength();

    TagMethodInfo tmiUserAttr = (TagMethodInfo)m_mapCurObjMethods.get( "userattribute" );

    for ( int x = 0; x < nLen; x++ )
    {
      String strName = attrs.getLocalName( x );

      TagMethodInfo tmi = (TagMethodInfo)m_mapCurObjMethods.get( strName.toLowerCase() );

      if ( tmi == null )
      {

        if ( tmiUserAttr == null )
          continue;

        VwAttributeImpl attrImpl = new VwAttributeImpl();
        attrImpl.setName( strName );
        attrImpl.setType( attrs.getValue( x ) );

        tmiUserAttr.m_methodSetter.invoke( m_curObj, new Object[]{ attrImpl } );
        continue;

      }

      String strVal = attrs.getValue( x );

      if ( m_fExpandMacros )
        strVal = VwExString.expandMacro( strVal );

      invokeSetterMethod( tmi, strVal );

    }

    if ( m_curObj instanceof VwDVOBase )
      ((VwDVOBase)m_curObj).setDirty( false );  // reset dirty flag from loading object

    if ( listNameSpaces != null )
    {
      TagMethodInfo tmiNS= (TagMethodInfo)m_mapCurObjMethods.get( "namespace" );

      for ( Iterator iNS = listNameSpaces.iterator(); iNS.hasNext(); )
      {
        Namespace ns = (Namespace)iNS.next();
        tmiNS.m_methodSetter.invoke( m_curObj, new Object[]{ ns } );
      }

    }
  }


  /**
   * Convert primitive objects to corresponding primitive types. i.e an Integer cobject is
   * converted to an int etc ...
   */
  private void doPrimitiveArray( Object objArray, Class clsType, List listItems )
  {

    int x = 0;
    for ( Iterator iObj = listItems.iterator(); iObj.hasNext(); )
    {
      Object objVal = iObj.next();
      if ( clsType == String.class )
        Array.set( objArray, x++, objVal );
      else
      if ( clsType == Boolean.TYPE )
        Array.setBoolean( objArray, x++, ((Boolean)objVal).booleanValue() );
      else
      if ( clsType == Byte.TYPE )
        Array.setByte( objArray, x++, ((Byte)objVal).byteValue() );
      else
      if ( clsType == Character.TYPE )
        Array.setChar( objArray, x++, ((Character)objVal).charValue() );
      else
      if ( clsType == Short.TYPE )
        Array.setShort( objArray, x++, ((Short)objVal).shortValue() );
      else
      if ( clsType == Integer.TYPE )
        Array.setInt( objArray, x++, ((Integer)objVal).intValue() );
      else
      if ( clsType == Long.TYPE )
        Array.setLong( objArray, x++, ((Long)objVal).longValue() );
      else
      if ( clsType == Float.TYPE )
        Array.setFloat( objArray, x++, ((Float)objVal).floatValue() );
      else
      if ( clsType == Double.TYPE )
        Array.setDouble( objArray, x++, ((Double)objVal).doubleValue() );
      else
        Array.set( objArray, x++, objVal );

    }
  }


  /**
   *  Invoke the setAttributes method for this object to load associated attributes
   *  with this tag
   */
  private void handleAttributes( AttributesImpl attrs, List listNameSpaces ) throws SAXException
  {

    Method method = (Method)m_mapCurObjMethods.get( "attributes" );

    if ( method != null )
    {

      if ( listNameSpaces != null )
      {
        if ( attrs == null )
          attrs = new AttributesImpl();

        for ( Iterator iNamespaces = listNameSpaces.iterator(); iNamespaces.hasNext(); )
        {
          Namespace ns = (Namespace)iNamespaces.next();
          String strNs = "xmlns";
          if ( ns.getPrefix().length() > 0  )
            strNs += ":" + ns.getPrefix();

          attrs.addAttribute( ns.getURI(), strNs, strNs, "CDATA", ns.getURI() );
        }
      }


      if ( attrs != null )
      {
        Object[] aobjParams = { m_strCurTagName, attrs };

        try
        {
          method.invoke( m_curObj, aobjParams );
        }
        catch( Exception  ex )
        {
          throw new SAXException( ex.toString() );
        }

      }
    } // end if

  } // end handleAttributes()


  /**
   * Creates a Map key object from the tag's attibute ( "id" and "type" )
   *
   * @param strName The name of the Map value tag
   */
  private Object getMapKey( Object objKey ) throws SAXException
  {

    // Expecting attribute id to identify the keyand possibly a key type

    // Get the map key (the id attribute)
    Attributes listAttr = (Attributes)m_mapAttr.get( objKey );

    if ( listAttr == null )
      throw new SAXException( getMissingMapAttrError( objKey.toString() ) );

    String strMapKey = listAttr.getValue( "id" );
    String strKeyType = null;

    if ( strMapKey == null )
      throw new SAXException( getMissingMapAttrError( objKey.toString() ) );


    // Get the map key type if first time (String is the default )
    if ( m_collectionTagInfo.m_clsMapKeyType == null )
    {

      strKeyType = listAttr.getValue( "type" );

      if ( strKeyType == null )
        strKeyType = "String";

      // Load the map key type class
      try
      {
        m_collectionTagInfo.m_clsMapKeyType = Class.forName( strKeyType );
      }
      catch( Exception e )
      {
        throw new SAXException( m_msgs.getString( "Vw.Xml.NoKeyClassFound" ) + " " + strKeyType );

      }

      // For simple type the constructor must take a single String as its param
      try
      {
        m_collectionTagInfo.m_constructSimpleType =
          m_collectionTagInfo.m_clsMapKeyType.getConstructor( new Class[]{ String.class } );

      }
      catch( Exception e )
      {
        // This will never happen becuase all the primiutive classes have constrcutors
        // take take a sString
        throw new SAXException( e.toString() );
      }

    } // end if

    Object objMapKey = null;

    try
    {
      // Create the Map key object
      objMapKey = m_collectionTagInfo.m_constructSimpleType.newInstance( new Object[]{ strMapKey } );
    }
    catch( Exception e )
    {
      // This should never happen
      throw new SAXException( e.toString() );

    }

    return objMapKey;

  } // end getMapKey()


  /**
   * Setup state for new object
   */
  private void setupObject( AttributesImpl attrs, List listNamespaces ) throws Exception
  {
    // Save current object's method map before we overlay it with this object
    m_stackMethods.push( m_mapCurObjMethods );

    // Put it on the stack
    m_objStack.push( m_curObj );
    m_stackObjNames.push( m_strCurObjName );

    // Create instance of the object defined in the m_clsParamType
    m_curObj = m_tagInfo.m_clsParamType.newInstance();

    if ( m_tagInfo.m_fIsPut && attrs.getLength() > 0 )
      m_mapAttr.put( m_curObj, attrs );

    // Get the method map for the new object

    m_mapCurObjMethods = (HashMap)s_mapObjects.get( getObjName( m_curObj ).toLowerCase() );

    if ( m_mapCurObjMethods == null )
    {
      introspect( m_tagInfo.m_clsParamType );
      m_mapCurObjMethods = (HashMap)s_mapObjects.get( getObjName( m_curObj ).toLowerCase() );

    }

    m_strCurObjName = m_tagInfo.m_strParamClassName;


    if ( m_curObj instanceof AnyTypeContent  )
    {
      m_anyContentType = new VwXmlToDataObj( true, true );
      m_anyContentType.makeDataObjectsForParentTags();
    }

    loadAttributes( attrs, listNamespaces );


  } // end setupObject()

  private void loadAttributes( AttributesImpl attrs, List listNamespaces ) throws SAXException
  {
    // Setup attributes

    if ( attrs != null || listNamespaces != null )
    {
      if ( m_fUseAttributeModel  )
      {
        try
        {
          movePropsToObject( attrs, listNamespaces );
        }
        catch( Exception ex )
        {
          throw new SAXException( ex.toString() );
        }
      }
      else
        handleAttributes( attrs, listNamespaces );

    } // end if

  } // end laodAttributes()


  /**
   * Invoke the setter or add method on the current object with the data in strVal
   *
   * @param tmi The info data about the class param types for the xml tag
   */
  private void invokeSetterMethod( TagMethodInfo tmi, String strTagData ) throws Exception
  {
    Object[] aParams = null;
    if ( strTagData == null )
      return;


    if ( tmi.m_clsParamType == String.class  )
    {
      aParams = new String[] { strTagData };
    }
    else
    if ( tmi.m_clsParamType == Boolean.class ||
         tmi.m_clsParamType == Boolean.TYPE )
    {
      boolean fState = false;

      for ( int x = 0; x < m_astrBoolValues.length; x++ )
      {
        if ( strTagData.equalsIgnoreCase( m_astrBoolValues[ x ]  ) )
        {
          fState = true;
          break;
        }
      } // end for()

      aParams = new Boolean[] { new Boolean( fState ) };
    }
    else
    if ( tmi.m_clsParamType == Byte.class || tmi.m_clsParamType == Byte.TYPE )
    {
      aParams = new Byte[] { new Byte( strTagData ) };
    }
    else
    if ( tmi.m_clsParamType == Short.class || tmi.m_clsParamType == Short.TYPE )
    {
      aParams = new Short[] { new Short( strTagData ) };
    }
    else
    if ( tmi.m_clsParamType == Integer.class || tmi.m_clsParamType == Integer.TYPE )
    {
      aParams =  new Integer[] { new Integer( strTagData ) };
    }
    else
    if ( tmi.m_clsParamType == Long.class || tmi.m_clsParamType == Long.TYPE )
    {
      aParams = new Long[] { new Long( strTagData ) };
    }
    else
    if ( tmi.m_clsParamType == Float.class || tmi.m_clsParamType == Float.TYPE )
    {
      aParams = new Float[] { new Float( strTagData ) };
    }
    else
    if ( tmi.m_clsParamType == Double.class || tmi.m_clsParamType == Double.TYPE )
    {
      aParams = new Double[] { new Double( strTagData ) };
    }
    else
    if ( tmi.m_clsParamType == java.math.BigInteger.class )
    {
      aParams = new java.math.BigInteger[] { new java.math.BigInteger( strTagData ) };
    }
    else
    if ( tmi.m_clsParamType == java.math.BigDecimal.class  )
      aParams = new java.math.BigDecimal[] { new java.math.BigDecimal( strTagData ) };
    else
    if ( tmi.m_clsParamType == Date.class  )
    {
      SimpleDateFormat df = new SimpleDateFormat( m_strDateFormat  );

      if ( m_strDateFormat.endsWith( "Z'" ))
      {
        TimeZone tz = TimeZone.getTimeZone( "UTC" );
        df.setTimeZone(tz );

      }

      aParams = new Date[] { df.parse( strTagData ) };
    }
    else
     if ( tmi.m_clsParamType == VwDate.class  )
         aParams = new VwDate[] { new VwDate( strTagData, m_strDateFormat ) };

    // Invoke the method

    try
    {
      tmi.m_methodSetter.invoke( m_curObj, aParams )  ;
    }
    catch( Exception e )
    {

      String strErr = "Error invoking method '" + tmi.m_methodSetter.getName()
                    + "' on class " + m_curObj.getClass().getName()
                    + " with param data " + strTagData
                    + "\n Failure Reason: " + e.toString();

      throw new Exception( strErr );

    }
  } // end invokeSetterMethod()


  /**
   * Adds a simple data type I.E. String, Integer, Byte .. to the current collectiion object
   *
   * @param tmi The TagMethodInfo description class
   *
   */
  private void addToSimpleCollection( TagMethodInfo tmi ) throws Exception
  {
    Object obj = null;

    String strTagData = getTagData( m_strCurTagName );

    if ( strTagData == null )
      return;

    if ( tmi.m_clsParamType == null ) // Default to String if still not known at this time
    {
      tmi.m_clsParamType = String.class;
      tmi.m_fIsSimpleType = true;
      tmi.m_strParamClassName = m_strCurTagName;
    }

    if ( tmi.m_clsParamType == String.class   )
      obj = new String( strTagData );
    else
    if ( tmi.m_clsParamType == Boolean.class || tmi.m_clsParamType == Boolean.TYPE )
    {
      boolean fState = false;

      if ( strTagData.equalsIgnoreCase( "y" ) || strTagData.equalsIgnoreCase( "yes" ) ||
           strTagData.equalsIgnoreCase( "true" ) || strTagData.equals( "1" ) )
        fState = true;

      obj = new Boolean( fState );
    }
    else
    if ( tmi.m_clsParamType == Byte.class || tmi.m_clsParamType == Byte.TYPE )
      obj = new Byte( strTagData );
    else
    if ( tmi.m_clsParamType == Character.class || tmi.m_clsParamType == Character.TYPE )
      obj = new Character( strTagData.charAt( 0 ) );
    else
    if ( tmi.m_clsParamType == Short.class || tmi.m_clsParamType == Short.TYPE  )
      obj = new Short( strTagData );
    else
    if ( tmi.m_clsParamType == Integer.class || tmi.m_clsParamType == Integer.TYPE )
      obj = new Integer( strTagData );
    else
    if ( tmi.m_clsParamType == Long.class || tmi.m_clsParamType == Long.TYPE )
      obj =  new Long( strTagData );
    else
    if ( tmi.m_clsParamType == Float.class || tmi.m_clsParamType == Float.TYPE )
      obj =  new Float( strTagData );
    else
    if ( tmi.m_clsParamType == Double.class  || tmi.m_clsParamType == Double.TYPE )
      obj = new Double( strTagData );
    else
    if ( tmi.m_clsParamType == java.math.BigInteger.class )
      obj = new java.math.BigInteger( strTagData );
    else
    if ( tmi.m_clsParamType == java.math.BigDecimal.class  )
      obj = new java.math.BigDecimal( strTagData );

    // Invoke the method

    Object[] aParams = null;

    if ( tmi.m_fIsPut )
      aParams = new Object[] { getMapKey( m_strCurTagName ), obj };
    else
      aParams = new Object[] { obj };

    tmi.m_methodCollection.invoke( m_objCollection, aParams )  ;

  } // end addToSimpleCollection()


  /**
   * Call set methods for all open collections on the current object
   */
  private void doCollectionCleanup() throws Exception
  {
    Object[] aParams = new Object[ 1 ];

    Map mapObjCollections  = (Map)m_mapObjCollections.get( m_curObj );

    for ( Iterator iKeys = mapObjCollections.keySet().iterator(); iKeys.hasNext();  )
    {

      String strPropName = (String)iKeys.next();
      Object objCollection = mapObjCollections.get( strPropName );
      TagMethodInfo tmi = (TagMethodInfo)m_mapCurObjMethods.get( strPropName.toLowerCase() );

      if ( tmi == null )
        continue;

      if ( tmi.m_clsArray != null )
      {
         Object objArray = Array.newInstance( tmi.m_clsArray,
                                              ((ArrayList)objCollection).size() );

         if ( tmi.m_fIsSimpleType )
           doPrimitiveArray( objArray, tmi.m_clsArray, (ArrayList)objCollection );
         else
           ((ArrayList)objCollection).toArray( (Object[])objArray );

         aParams[ 0 ] =  objArray;
      }
      else
        aParams[ 0 ] = objCollection;

      tmi.m_methodSetter.invoke( m_curObj, aParams );


    } //end for

    m_mapObjCollections.remove( m_curObj );

  } // end doCollectionCleanup()



  /**
   * Returns just the name of the object without the package
   *
   * @param obj The object to get the name for
   */
  protected String getObjName( Object obj )
  {
    String strName = obj.getClass().getName();

    int nPos = strName.lastIndexOf( '.' );    // This check is for inner classes

    if ( nPos >= 0 )
      strName = strName.substring( nPos + 1 );


    return strName;

  } // end getObjName()


  /**
   * Returns just the name of the object without the package
   *
   * @param cls The the Class of the object to get the name for
   */
  private String getObjName( Class cls )
  {
    String strName = cls.getName();

    int nPos = strName.lastIndexOf( '.' );    // This check is for inner classes

    if ( nPos >= 0 )
      strName = strName.substring( nPos + 1 );


    return strName;

  } // end getObjName()


  /**
   * Format an invocation error with method, object and parameter details
   *
   * @param method The Method class to invoke
   * @param objBean The object that has the method
   * @param aParams The method parameters
   */
  private String getInvocationError( Method method, Object objBean, Object[] aParams )
  {

    String strErrMsg = m_msgs.getString( "Vw.Xml.InvocationError" );
    strErrMsg = VwExString.replace( strErrMsg, "%1", method.getName() );
    strErrMsg = VwExString.replace( strErrMsg, "%2", objBean.getClass().getName() );
    strErrMsg = VwExString.replace( strErrMsg, "%3", aParams[ 0 ].toString() );

    return strErrMsg;

  } // end getInvocationError()


  /**
   * Format an missing "id" attribute error for needed Map collection key
   *
   * @param strTag The tag name
   */
  private String getMissingMapAttrError( String strTag )
  {

    String strErrMsg = m_msgs.getString( "Vw.Xml.MissingMapAttr" );
    strErrMsg = VwExString.replace( strErrMsg, "%1", strTag );

    return strErrMsg;

  } // end getMissingMapAttrError()


  /**
   * Setup alias tags or update collection properties if an xml schema or dtd is defined
   *
   * @param urlSchema The location of the dtd or xml schema to process
   */
  private void processSchema( URL urlSchema ) throws Exception
  {
    String strName = urlSchema.getPath();
    s_mapSchemas.put( strName, null );

    if ( strName.endsWith( "xsd" ) )
      processXMLSchema( urlSchema );
    else
    if ( strName.endsWith( "dtd" ) )
      processDTDSchema( urlSchema );
    else
      throw new Exception( urlSchema.getPath() +
                           " is an unrecognized schema type, looking for dtd or xsd extensions" );
    return;


  } // end processSchema()

  /**
   * Process an XML schema
   *
   * @param urlSchema The location of the xml schema to process
   */
  private void processXMLSchema( URL urlSchema ) throws Exception
  {
    VwSchemaReaderImpl reader = new VwSchemaReaderImpl();

    Schema schema = reader.readSchema( urlSchema );
    processXMLSchema( schema );
  }


  /**
   * Process an XML schema
   *
   * @param schema The schema to process
   */
  private void processXMLSchema( Schema schema) throws Exception
  {
    VwSchemaReaderImpl reader = new VwSchemaReaderImpl();

    schema.mergeIncludes();

    // Look at the schema components to toplevel elements or complex type definitions
    for ( Iterator iComp = schema.getContent().iterator(); iComp.hasNext(); )
    {
      Object objComp = iComp.next();

      if ( objComp instanceof Element  )
      {
        Element element = (Element)objComp;

        String strType = element.getType();
        String strName = element.getName();

        int ndx = -1;

        if ( strType != null )
          ndx = strType.indexOf( ':');

        // See if this type matches our toplevel class name
        if ( strType != null && strType.substring( ++ndx ).equalsIgnoreCase( m_strTopLevelClassName ) )
        {
          //Save Toplevel class translations in static map for repeatitive lookup
          s_mapTopLevelTypes.put( m_strTopLevelClassName, strName );
          m_strTopLevelClassName = strName;

        }

        // See if this element is an anonymous complexType
        if ( element.getComplexType() != null )
          processComplexType( schema, strName, element.getComplexType() );
      }
      else
      if ( objComp instanceof ComplexType  )
      {

        String strName = ((ComplexType)objComp).getName();
        processComplexType( schema, strName, (ComplexType)objComp );

      }

    } // end for

  } // end processXMLSchema()


  /**
   * See if this is a parent tag with only one child (which is also a parent )
   * and if so return the decl for the child
   *
   * @param type The parent to test
   */
  public static Element getChildParent( ComplexType type )
  {

    ModelGroup group = type.getModelGroup();

    if ( group != null && group.getContent().size() == 1 )
    {

      // Get the element, and if it is a parent element return the elementDecl class
      Element element = ((VwModelGroupImpl)group).findFirstElement();

      if ( element.isComplexType() )
      {

        ComplexType ctype = (ComplexType)element.getComplexType();
        if ( ctype != null && ctype.hasChildElements() )
          return element;
      }

    } // end if

    // Either multiple children or child tag is not a parent
    return null;

  } // end getChildParent()



  /**
   * Process a DTD schema
   *
   * @param urlSchema The location of the dtd schema to process
   */
  private void processDTDSchema( URL urlSchema ) throws Exception
  {
    VwDtdParser parser = new VwDtdParser( urlSchema, null );

    parser.process();

    Map mapElements = parser.getElements();

    // Look at the schema components to toplevel elements or complex type definitions
    for ( Iterator iDecl = mapElements.values().iterator(); iDecl.hasNext(); )
    {
      VwDtdElementDecl eleDecl = (VwDtdElementDecl)iDecl.next();


      ModelGroup group = eleDecl.getGroup();

      if ( group != null )
      {
        for ( Iterator iElements = group.getContent().iterator(); iElements.hasNext(); )
        {
          Object objComp = iElements.next();

          if ( objComp instanceof Element  )
          {
            Element element = (Element)objComp;

            VwDtdElementDecl childDecl =
               (VwDtdElementDecl)mapElements.get( element.getName() );

            int nContentType = childDecl.getContentType();
            if (  nContentType == VwDtdElementDecl.MIXED ||
                 nContentType == VwDtdElementDecl.ANY )
            {
              String strMixedName = childDecl.getName();
              updateTmi( strMixedName, eleDecl.getName(), nContentType );
            }

            String strName = element.getName();

            // See if this element is a collection
            String strOccurs =  element.getMaxOccurs();
            String strEleType = element.getType();

            if ( strEleType == null )
              strEleType = strName;

            if ( strOccurs != null && !strOccurs.equals( "1" ) )
            {
              String strJavaType = getJavaType( strEleType );

              if ( strJavaType != null )
               strEleType = strJavaType;

              updateCollection( strName, eleDecl.getName(), strEleType, false  );
            }


          }
          else
          if ( objComp instanceof ComplexType  )
          {
            String strName = ((ComplexType)objComp).getName();

          } // end if

        } // end for

      } // end if

    } // end for

  } // end processDTDSchema()


  /**
   * Process the child tags to resolve type alias and collections definitions
   */
  private void processComplexType( Schema schema, String strName, ComplexType type ) throws Exception
  {

    ModelGroup group = type.getModelGroup();


    if ( type.getUserAttribute( "setterAlias" ) != null )
    {
      String strSetterAlias = type.getUserAttribute( "setterAlias" ).getType();
      addObjectSetterAlias( strName, null, strSetterAlias );
      setElementHandler( strName, determineType( strName, "" ) );
    }

    if ( group == null )
      return;

    processElementGroup( schema, group, strName, group.getMaxOccurs() );


  } // end processComplexType()


  /**
   * Process an element group
   * @param schema
   * @param group
   * @param strName
   * @param strMaxOccurs
   * @throws Exception
   */
  private void processElementGroup( Schema schema, ModelGroup group, String strName, String strMaxOccurs )
   throws Exception
  {
    Iterator iGroup = group.getContent().iterator();

    while ( iGroup.hasNext() )
    {

      Object objComp = iGroup.next();

      if ( objComp instanceof ModelGroup )
      {
        if ( strMaxOccurs == null )
          strMaxOccurs = ((ModelGroup)objComp).getMaxOccurs();

        processElementGroup( schema, (ModelGroup)objComp, strName, strMaxOccurs );
      }
      else
      if ( objComp instanceof Element )
      {

        Element element = (Element)objComp;

        String strEleName = element.getName();
        String strEleType = element.getType();


        // if name is null, see if its a reference
        if ( strEleName == null )
          strEleName = element.getRef();

        if ( strEleName == null )
          throw new Exception( "Either the element name or ref attribute must be specified for " + strName );

        int nPos = strEleName.indexOf( ':' );

        if ( nPos > 0 )
          strEleName = strEleName.substring( ++nPos );

        if ( strEleType != null )
        {
          Object objType = schema.getComponent( strEleType );

          if (  objType instanceof ComplexType )
          {

            int ndx = strEleType.indexOf( ':');

            if ( ndx > 0 )
              strEleType = strEleType.substring( ++ndx );

            strEleType = Character.toUpperCase( strEleType.charAt( 0 ) ) +
                         strEleType.substring( 1 );
            Class clsType = determineType( strEleType, "" );

            this.setElementHandler( strEleName, clsType );

          } // end if

        } // end

        // See if this element is a collection
        String strOccurs =  ((Element)objComp).getMaxOccurs();

        if ( strOccurs != null && !strOccurs.equals( "1" ) ||
            ( strMaxOccurs != null && !strMaxOccurs.equals( "1 ") ) )
        {
          String strJavaType = null;

          if ( ((Element)objComp).isComplexType() )
            strEleType = strEleName;

          if ( strEleType != null )
            strJavaType = getJavaType( strEleType );

          if ( strJavaType != null )
            strEleType = strJavaType;

          updateCollection( strEleName, strName, strEleType, false  );
        } // end if


        ComplexType complexType = ((Element)objComp).getComplexType();

        if ( complexType != null )
        {
          processComplexType( schema, strEleName, complexType );

        } // end if


      } // end if ( objComp instanceof VwSchemaElement )

    } // end while()


  } // end processElementGroup

  /**
   * Build the primitive type conversion map
   */
  private static void buildPrimTypesMap()
  {
    s_mapPrimTypes.put( "string", "String" );
    s_mapPrimTypes.put( "boolean", "boolean" );
    s_mapPrimTypes.put( "Boolean", "Boolean" );
    s_mapPrimTypes.put( "byte", "byte" );
    s_mapPrimTypes.put( "Byte", "Byte" );
    s_mapPrimTypes.put( "short", "short" );
    s_mapPrimTypes.put( "Short", "Short" );
    s_mapPrimTypes.put( "int", "int" );
    s_mapPrimTypes.put( "integer", "int" );
    s_mapPrimTypes.put( "Integer", "Integer" );
    s_mapPrimTypes.put( "long", "long" );
    s_mapPrimTypes.put( "Long", "Long" );
    s_mapPrimTypes.put( "unsignedByte", "byte" );
    s_mapPrimTypes.put( "unsignedSshort", "short" );
    s_mapPrimTypes.put( "unsignedInt", "int" );
    s_mapPrimTypes.put( "unsignedLong", "long" );
    s_mapPrimTypes.put( "float", "float" );
    s_mapPrimTypes.put( "Float", "Float" );
    s_mapPrimTypes.put( "double", "double" );
    s_mapPrimTypes.put( "Double", "Double" );
    s_mapPrimTypes.put( "decimal", "double" );
    s_mapPrimTypes.put( "date", "String" );
    s_mapPrimTypes.put( "time", "String" );
    s_mapPrimTypes.put( "ID", "String" );
    s_mapPrimTypes.put( "IDREF", "String" );
    s_mapPrimTypes.put( "QNAME", "String" );
    s_mapPrimTypes.put( "ENTITY", "String" );
    s_mapPrimTypes.put( "positiveInteger", "int" );
    s_mapPrimTypes.put( "nonPositiveInteger", "int" );
    s_mapPrimTypes.put( "nonNegativeInteger", "int" );
    s_mapPrimTypes.put( "negativeInteger", "int" );
    s_mapPrimTypes.put( "object", "Object" );

  } // end buildPrimTypesMap()


  /**
   * Return the Java type for the schema type
   */
  public static String getJavaType( String strSchemaType )
  {
    // Remove namespace qualifier
    int nPos = strSchemaType.indexOf( ':' );

    if ( nPos >= 0 )
      strSchemaType = strSchemaType.substring( nPos + 1 );

    return s_mapPrimTypes.get( strSchemaType );

  } // end getSchemaType()


} // end class VwXmlToBean{}


// *** End of VwXmlToBean.java ***
