/*
============================================================================================

                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwBeanToXml.java

Create Date: Apr 11, 2006
============================================================================================
*/
package com.vozzware.xml;
import com.vozzware.db.VwDVOBase;
import com.vozzware.util.VwBeanUtils;
import com.vozzware.util.VwDate;
import com.vozzware.util.VwDelimString;
import com.vozzware.wsdl.util.VwWSDLReaderImpl;
import com.vozzware.xml.dtd.VwDtdAttributeDecl;
import com.vozzware.xml.dtd.VwDtdElementDecl;
import com.vozzware.xml.dtd.VwDtdParser;
import com.vozzware.xml.namespace.Namespace;
import com.vozzware.xml.namespace.QName;
import com.vozzware.xml.schema.VwAttrQName;
import com.vozzware.xml.schema.util.VwSchemaReaderImpl;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

import javax.wsdl.Definition;
import javax.wsdl.Types;
import javax.xml.schema.Attribute;
import javax.xml.schema.ComplexContent;
import javax.xml.schema.ComplexType;
import javax.xml.schema.Element;
import javax.xml.schema.ModelGroup;
import javax.xml.schema.Schema;
import javax.xml.schema.util.XmlFeatures;
import javax.xml.schema.util.XmlSerializer;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * This class converts a bean or a list of beans (of the same class type) to an xml document
 */
public class VwBeanToXml implements XmlSerializer
{

  private String              m_strTrue = "Y";    // Defualt value for true boolean properties
  private String              m_strFalse = "N";   // Defualt value for false boolean properties

  private Class               m_clsTopLevel;

  private Map                 m_mapPropAliases = new HashMap(); // Map of property aliases
  private Map<String,Object>  m_mapObjAlias = new HashMap<String,Object>();      // Map of tag to object name alias
  private Map<String,Boolean> m_mapChildren = new HashMap<String,Boolean>();      // Map of tag to object name alias


  private static Map<Class, PropertyDescriptor[]>  s_mapProps = Collections.synchronizedMap( new HashMap<Class,PropertyDescriptor[]>() );       // Map of cached property descriptors
  private static Map<Class,PropertyDescriptor[]>  s_mapClassProps = Collections.synchronizedMap( new HashMap<Class,PropertyDescriptor[]>() );
  private static Map<Class,String>  s_mapContentHandled = Collections.synchronizedMap( new HashMap<Class,String>() );
  private Map<String,String> m_mapElementTypes = Collections.synchronizedMap( new HashMap<String,String>() );

  private static Map          s_mapTopLevelTypes = Collections.synchronizedMap( new HashMap() );
  private static Map<URL,Object> s_mapSchemas =          Collections.synchronizedMap( new HashMap<URL,Object>() );
  private static Map          s_mapSchemaObjAliases = Collections.synchronizedMap( new HashMap() );
  private static Map          s_mapMixedModeObjects = Collections.synchronizedMap( new HashMap() );
  private static Map          s_mapAttrsBySchema = Collections.synchronizedMap( new HashMap() );
  private static Map 		      s_mapSchemasByClass = Collections.synchronizedMap( new HashMap() );
  private static Map<Package,List<URL>>  s_mapSchemasByPackage = Collections.synchronizedMap( new HashMap<Package,List<URL>>() );
  private        List         m_listSchemasToProcess = new LinkedList();
  private static List<Class>  s_listSuperclassFilters; // List of SuperClass types to exclude from PropertyDescriptor lists
  private static Map          s_mapComplexTypes = Collections.synchronizedMap( new HashMap() );

  private String              m_strDefaultForNulls;   // Default tag data for props that return nulll

  private String              m_strCommentHeaderText; // User defined document comment header text

  private String              m_strTopLevelClassName; // Top level bean class xml is being genned from

  private String              m_strCurPropName;

  private VwXmlWriter        m_xmlWriter;                 // xml formatter class

  private URL                 m_urlSchema;           // Current schema to use if specified

  private boolean             m_fUseAttributeModel = false;

  private boolean             m_fUseNamespaces = false;

  private boolean             m_fGenEmptyTagsForNulls = false;

  private boolean             m_fLowerCaseFirstCarClassNames = true;

  private VwPropertyListener m_propListener;
  private String              m_strXMLDecl;


  /**
   * This class makes an enumeration look like an iterator
   */
  class VwEnumerator implements Iterator
  {
    Enumeration m_enum;

    VwEnumerator ( Enumeration en )
    { m_enum = en; }

    public boolean hasNext()
    { return m_enum.hasMoreElements(); };

    public Object next()
    { return m_enum.nextElement(); }

    public void remove()
    {}

  } // end class Enumerator


  /**
   * This class uses the Iterator interface to access an array
   */
  class VwArrayIterator implements Iterator
  {
    Object[] m_aObj;         // Object array to iterate through
    int    m_ndx = 0;        // Array index

    VwArrayIterator( Object[] aObj )
    { m_aObj = aObj; }

    public boolean hasNext()
    { return m_ndx < m_aObj.length; };

    public Object next()
    { return m_aObj[ m_ndx++ ]; }

    public void remove()
    {}

  } // end class VwArrayIterator


  /**
   * Default Constructor
   */
  public VwBeanToXml()
  { this( null, "", false, 0 ); }

  /**
   * Constructor
   *
   * @param strXMLDecl The xml declaration string that typicially defines the document as an xml
   * document, provides the version number and the encoding type.<br>
   * Ex. &lt;?xml version="1.0" encoding="ISO-8859-1"?&gt;
   *
   * @param strDefaultForNulls The default placholder string when properties of a bean return
   * null. If this parameter is null, then the property will be omitted from the xml document.
   *
   * @param fFormatted if true, the xml document will insert CR/LF and indentation characters
   * for tag parentage.
   *
   * @param nIndentLevel The indentation level to start the formatting in. Each level nbr results
   * in an indentation of 2 spaces. This paramter only has affect if fFormatted is true. 0 should
   * be specified for the standard indentation.
   */
  public VwBeanToXml( String strXMLDecl, String strDefaultForNulls,
                       boolean fFormatted, int nIndentLevel )
  {
    addSuperClassPropertyFilter( VwDVOBase.class );

    try
    {
      this.setContentMethods( VwDate.class, "getTime", true );
      this.setContentMethods( Date.class, "getTime", true );
    }
    catch( Exception ex )
    {
      ex.printStackTrace();
    }

    m_strXMLDecl = strXMLDecl;

    m_strDefaultForNulls = strDefaultForNulls;

    m_xmlWriter = new VwXmlWriter( fFormatted, nIndentLevel );
    if ( strXMLDecl != null )
      m_xmlWriter.addXml( strXMLDecl );


  } // end VwBeanToXml()


  /**
   * Sets the formatted output feature which is off by default. Formatted output adds CRLF pairs as well as indentaion
   * of child elements.
   *
   * @param fFormatOutput true to turn on formatting, false to turn off
   *
   * @param nStartingIndentLevel The starting indentaion level. each level nbr adds two spaces of indentaion
   */
  public void setFormattedOutput( boolean fFormatOutput, int nStartingIndentLevel )
  {
    m_xmlWriter.setFormattedOutput( fFormatOutput );
    m_xmlWriter.setLevel( nStartingIndentLevel );

  }

  /**
   * Adds a super class type that will be excluded from the property descriptors during introspection. This will
   * prevent those super class properties from being serialized in the xml.
   *
   * @param classToFilter The superclass to filter
   */
  public static void addSuperClassPropertyFilter( Class classToFilter )
  {
    if ( s_listSuperclassFilters == null )
      s_listSuperclassFilters = new ArrayList<Class>();

    if ( s_listSuperclassFilters.indexOf( classToFilter ) < 0 )
        s_listSuperclassFilters.add( classToFilter );
  }

  /**
   * Removes a superclass filter
   * @see addSuperClassPropertyFilter
   * @param classToRemove The class to remove from the list
   */
  public static void removeSuperClassPropertyFilter( Class classToRemove )
  {
    if ( s_listSuperclassFilters != null )
      s_listSuperclassFilters.remove( classToRemove );

  }


  /**
   * The default value to use for null menthod values. The default behaviour is to not output xml for properties
   * that return null.
   *
   * @param strDefaultForNulls The value to output for properties that return null during serialization
   */
  public void setDefaultForNulls( String strDefaultForNulls )
  { m_strDefaultForNulls = strDefaultForNulls; }


  /**
   * If true, will generate empty tag names for peoprties return null values else
   * no xml tags will be generated
   *
   * @param fGenEmptyTagsForNulls
   */
  public void setGenEmptyTagsForNulls( boolean fGenEmptyTagsForNulls )
  { m_fGenEmptyTagsForNulls = fGenEmptyTagsForNulls; }

  /**
   * @see setContentMethods below
   */
  public void setContentMethods( Class cls, String strPropList ) throws Exception
  { setContentMethods( cls, strPropList, false ); }

  public void setDocumentCommentHeader( String strCommentHeaderText )
  { m_strCommentHeaderText = strCommentHeaderText; }


  /**
   * This method allows you to override the properties returned through the Introspector with the list specified
   * <br>in the comma separated property list. if The fIncludeInXml is true, the the property name is also included in
   * <br>the xml output otherwise just the content serialized and the property name is omitted.
   *
   * @param clsObject The class of the object to serialize
   * @param strMethodNames a comma separated list of method names to include in the output
   * @param fIncludeInXml if true include the property name in the output else include just the properties content
   * @throws Exception
   */
  public void setContentMethods( Class cls, String strPropList, boolean fIncludeInXml ) throws Exception
  {
    VwDelimString dlmsProps = new VwDelimString( ",", strPropList );
    List<PropertyDescriptor> listProps = new ArrayList<PropertyDescriptor>();
    PropertyDescriptor[] aProps = null;
    Map<String,String> mapProps = new HashMap<String,String>();

    if ( s_mapContentHandled.containsKey( cls ))
      return;

    s_mapContentHandled.put(  cls, null );

    // Gets properties for this class as well it's super class

    if ( fIncludeInXml )
      aProps = s_mapProps.get( cls );
    else
      aProps = s_mapClassProps.get( cls );

    if ( aProps != null )
    {
      for ( int x = 0; x < aProps.length; x++ )
      {
        if ( aProps[ x ] == null )
          continue;

        if ( mapProps.containsKey( aProps[ x ].getName().toLowerCase() ) )
          continue;

        listProps.add( aProps[ x ] );
        mapProps.put( aProps[ x ].getName().toLowerCase(), null );
      }
    }

    for ( Iterator iProps = dlmsProps.iterator(); iProps.hasNext(); )
    {
      String strProp = (String)iProps.next();
      Method m = cls.getMethod( strProp, (Class[])null );

      if ( strProp.startsWith( "get" ) )
        strProp = strProp.substring( 3 );;

      strProp = Character.toLowerCase( strProp.charAt( 0 ) ) + strProp.substring( 1 );

      if ( mapProps.containsKey( strProp.toLowerCase() ))
        continue;
      mapProps.put( strProp.toLowerCase(), null );

      listProps.add(  new PropertyDescriptor( strProp, m, null ) );

    } // end for

    aProps = new PropertyDescriptor[ listProps.size() ];

    listProps.toArray( aProps );

    if ( fIncludeInXml )
      s_mapProps.put( cls, aProps );
    else
      s_mapClassProps.put( cls, aProps );

  } // end setClassProps()

  private void resolveContentMethodSuperProps( Map<Class,PropertyDescriptor[]> mapProps) throws Exception
  {

    // walk the parent chain to get all superclass content method properties
    for ( Class clsContent : mapProps.keySet() )
    {
      Map<String,String>mapDescriptors = new HashMap<String, String>();
      List<PropertyDescriptor> listProps = new ArrayList<PropertyDescriptor>();
      Class clsSuper = clsContent;

      if ( s_mapContentHandled.get( clsContent ) != null )
        continue;

      while( clsSuper != Object.class )
      {
        PropertyDescriptor[] aProps = mapProps.get( clsSuper );
        if ( aProps != null )
        {

          for ( int x = 0; x < aProps.length; x++ )
          {
            if ( aProps[ x ] == null )
              continue;

            String strPropName =  aProps[x ].getName();

            if ( mapDescriptors.containsKey( strPropName ))
              continue;

            mapDescriptors.put( strPropName, null );

            listProps.add( aProps[ x ] );
          }
        }
        clsSuper = clsSuper.getSuperclass();
      }

      PropertyDescriptor[]  aProps = new PropertyDescriptor[ listProps.size() ];
      listProps.toArray( aProps );
      s_mapContentHandled.put( clsContent, "1" );

      // Reorder props by scheam or dtd if defined
      aProps = orderProps( clsContent, aProps );
      mapProps.put( clsContent, aProps );

    }
  }
  /**
   * Sets a behavioural attribute for the deSerializer
   * @param strURIFeature The uri of the feature to set
   * @param fEnable if true enable the feature, else disable the feature
   *
   * @throws Exception If the feature requested is not valid
   */
  public void setFeature( String strURIFeature, boolean fEnable ) throws Exception
  {
    if ( strURIFeature.equals( XmlFeatures.ATTRIBUTE_MODEL ) )
        m_fUseAttributeModel = fEnable;
    else
    if ( strURIFeature.equals( XmlFeatures.USE_NAMESPACES ) )
      m_fUseNamespaces = fEnable;

  } // end setFeature()

  /**
   * Adss an object to tag alias
   * @param clsObject The class of the object
   * @param strTagAlias The name of the xml tag that is generated for this class name. (i.e., schema )
   *
   */
  public void setObjectElementName( Class clsObject, String strTagAlias )
  { doObjAlias(  stripPackage( clsObject ), strTagAlias );  }



  /**
   * Handle the oblect alias. If an object has more than one alias, create a List of names
   * @param strObject The Object
   * @param strAlias The ibject alias
   */
  private void doObjAlias( String strObject, String strAlias )
  {
    Object objAlias = m_mapObjAlias.get( strObject );
    if ( objAlias != null )
    {
      if ( ! (objAlias instanceof List) )
      {
        List<String> listAlias = new ArrayList<String>();
        listAlias.add( (String)objAlias ); // add original entry
        listAlias.add( strAlias );         // add new entry
        m_mapObjAlias.put( strObject, listAlias );
      }
      else
      ((List<String>)objAlias).add( strAlias );

    }
    else
      m_mapObjAlias.put( strObject, strAlias );


  }

  /**
   * If true, output an objects properties as XML attributes as opposed to child tags
   * @param fTreatPropsAsAttrs
   */
  public void setTreatPropsAsAttributes( boolean fTreatPropsAsAttrs )
  { m_fUseAttributeModel = fTreatPropsAsAttrs; }


  /**
   * If true, the properties getQName and getNamespaces are look for on each bean instance and if present
   * are used to format the tag with namespace support and xmlns formats in the tag for each namespace defined
   * @param fUseNamespaces
   */
  public void setUseNamespaces( boolean fUseNamespaces )
  { m_fUseNamespaces = fUseNamespaces; }


  /**
   * Convert a bean to an xml document
   *
   * @deprecated use serialize
   * @param strDocRoot The document root tag or null to exclude
   * @param objBean A bean to convert to xml
   */
  public String toXml( String strDocRoot, Object objBean ) throws Exception
  { return serialize( strDocRoot, objBean ); }

  /**
   * Convert a bean to an xml document
   *
   * @param strDocRoot The document root tag or null to exclude
   * @param objBean A bean to convert to xml
   */
  public String serialize( String strDocRoot, Object objBean ) throws Exception
  {
    List listBeans = new ArrayList( 1 );
    listBeans.add( objBean );
    return serialize( strDocRoot, listBeans );

  }

  /**
   * Convert a list of beans to an xml document
   *
   * @deprecated use serialize
   *
   * @param strDocRoot The document root tag or null to exclude
   * @param listBeans A lsit of beans to convert to this document
   */
  public String toXml( String strDocRoot, List listBeans ) throws Exception
  { return serialize( strDocRoot, listBeans ); }

  /**
   * Serialize the object specified to an XML document and writes the document to the file specified
   * @param strDocRoot The root element of the document or null to use the default
   * @param objBean The object to serialize
   * @param fileXML The File object the xml document will be written to
   * @throws Exception if any io errors occur
   */
  public void serialize( String strDocRoot, Object objBean, File fileXML  ) throws Exception
  {
    FileWriter fw = new FileWriter( fileXML, false );
    String strXML = serialize( strDocRoot, objBean );
    fw.write( strXML );
    fw.close();

  }

  /**
   * Serialize a List of beans to an XML document and write the document to the file specified
   * @param strDocRoot The root element of the document or null to use the default
   * @param listBeans The list of toplevel beans to serialize
   * @param fileXML The File object the xml document will be written to
   * @throws Exception if any io errors occur
   */
  public void serialize( String strDocRoot, List listBeans, File fileXML ) throws Exception
  {
    FileWriter fw = new FileWriter( fileXML, false );

    fw.write( serialize( strDocRoot, listBeans ) );
    fw.close();

  }

  /**
   * Convert a list of beans to an xml document
   *
   * @param strDocRoot The document root tag or null to exclude
   * @param listBeans A lsit of beans to convert to this document
   */
  public String serialize( String strDocRoot, List listBeans ) throws Exception
  {

    m_strTopLevelClassName = null;

    // Get property descriptor for the first in the list since the others are the same

    m_clsTopLevel = listBeans.get( 0 ).getClass();

    String strClassName = getClassName( m_clsTopLevel );

    m_strTopLevelClassName = strClassName;

    processSchemas();

    String strAlias = getObjAlias( strClassName );

    if ( strAlias != null )
      strClassName = strAlias;
    else
    {
      if ( m_fLowerCaseFirstCarClassNames)
        strClassName = strClassName.substring( 0, 1 ).toLowerCase() + strClassName.substring( 1 );

    }
    if ( m_fUseAttributeModel )
      buildElementAttrNames();

    boolean fSkipClassName = false;

    if ( m_strTopLevelClassName != null  )
      fSkipClassName = true;
    else
      m_strTopLevelClassName = strClassName;

    String strTemp =  getObjAlias( m_strTopLevelClassName );
    if (strTemp != null )
      m_strTopLevelClassName = strTemp;

    m_xmlWriter.clear();

    if ( m_strXMLDecl != null )
      m_xmlWriter.addXml( m_strXMLDecl );

    if ( m_strCommentHeaderText != null )
    {
      if ( !m_strCommentHeaderText.trim().startsWith( "<!--" ))
        m_xmlWriter.addXml( "<!--\n" );

      m_xmlWriter.addXml( m_strCommentHeaderText );

      if ( !m_strCommentHeaderText.trim().startsWith( "<!--" ))
        m_xmlWriter.addXml( "\n-->" );

    }


    // Add in doc root tag if specified

    if ( strDocRoot != null )
      m_xmlWriter.addParent( strDocRoot, null );


    // If property descriptor array not in cache, get it and put it in cache
    PropertyDescriptor[] aProps = (PropertyDescriptor[])s_mapProps.get( m_clsTopLevel );

    if ( aProps == null )
    {
      aProps = getProps( m_clsTopLevel );
      s_mapProps.put( m_clsTopLevel, aProps );

    }

    resolveContentMethodSuperProps( s_mapProps );
    resolveContentMethodSuperProps( s_mapClassProps );

    Iterator iBeans = listBeans.iterator();

    while ( iBeans.hasNext() )
    {
      Object objBean = iBeans.next();

      QName qnClassName = getQName( objBean );
      if ( qnClassName != null )
        m_strTopLevelClassName = qnClassName.toElementName();

      if ( fSkipClassName )
        m_xmlWriter.addParent( m_strTopLevelClassName, getObjAttrs( objBean, m_strTopLevelClassName ) );

      beanToXml( objBean, aProps, getObjAttrs( objBean, m_strTopLevelClassName ), fSkipClassName );

    } // end while()

    if ( fSkipClassName )
      m_xmlWriter.closeParent( m_strTopLevelClassName );

    if ( strDocRoot != null )
      m_xmlWriter.closeParent( strDocRoot );

    return m_xmlWriter.getXml();

  } // end toXml()


  /**
   * Sets the xml tag data string that will be generated for boolean properties that return a value of true
   *
   * @param strTrueValue The value that will be generated for true boolean properties
   */
  public void setTrueBooleanTransform( String strTrueValue )
  { m_strTrue = strTrueValue; }


  /**
   * Gets current string transform value for a true boolean state
   *
   */
  public String getTrueBooleanTransform()
  { return m_strTrue; }


  /**
   * Sets the xml tag data string that will be generated for boolean properties that return a value of false
   *
   * @param strFalseValue The value that will be generated for false boolean properties
   */
  public void setFalseBooleanTransform( String strFalseValue )
  { m_strFalse = strFalseValue; }


  /**
   * Gets current string transform value for a false boolean state
   *
   */
  public String getFalseBooleanTransform()
  { return m_strFalse; }

  /**
   * Orders properties by an XML Schema or DTD definition (if one exists)
   * else just returns the original properties
   * @param clsBean The class of the properties being ordered
   * @param aProps The oriigianl list of property descriptors
   * @return
   * @throws Exception
   */
  private PropertyDescriptor[] orderProps( Class clsBean, PropertyDescriptor[] aProps ) throws Exception
  {
    List<URL> listUrls = s_mapSchemasByPackage.get( clsBean.getPackage() );


    if ( listUrls == null )
      return aProps;

    PropertyDescriptor[] aOrderedProps = null;

    for ( URL urlSchema : listUrls  )
    {

      Object objSchema = s_mapSchemas.get( urlSchema );

      // Process all registered schemas until we get a hot on the class
      if ( objSchema instanceof Schema )
        aOrderedProps =  orderBySchema( clsBean, aProps, (Schema)objSchema );
      else
        aOrderedProps = orderByDtd( clsBean, aProps, (VwDtdParser)objSchema );

      if ( aOrderedProps != null )
      {
        s_mapSchemasByClass.put( clsBean, objSchema );
        return aOrderedProps;
      }

    } // end for

    return aProps;  // Not found

  }

  /**
   * Returns the array of PropertyDescriptors for the class specified. If a dtd or xml
   * schema is specified, the array will be ordered according to the schema spec.
   */
  private PropertyDescriptor[] getProps( Class clsBean ) throws Exception
  {
    Class clsStopAt = null;

    if ( s_listSuperclassFilters != null )
    {
      for ( Class classToStop : s_listSuperclassFilters )
      {
        if ( classToStop.isAssignableFrom( clsBean ))
        {
          clsStopAt = classToStop; // ok found a superclass stop point for introspector
          break;
        }
      }
    }

    PropertyDescriptor[] aProps = null;

    if ( clsStopAt != null )
      aProps = Introspector.getBeanInfo( clsBean, clsStopAt ).getPropertyDescriptors();
    else
      aProps = Introspector.getBeanInfo( clsBean  ).getPropertyDescriptors();

    return orderProps( clsBean, aProps );

  } // end getProps()

  /**
   * Return a Attributes if there are attributes for an Object name tag
   */
  private Attributes getObjAttrs( Object objBean,  String strElementName )  throws Exception
  {

    AttributesImpl attrs = new AttributesImpl();
    Method mthdNS = null;
    Method mthdUserAttrs = null;
    try
    {
      mthdNS = objBean.getClass().getMethod( "getNamespaces", null );
    }
    catch( Exception ex )
    {
      // don't care
    }

    try
    {
      mthdUserAttrs = objBean.getClass().getMethod( "getUserAttributes", null );

      List listUserAttrs = (List)mthdUserAttrs.invoke( objBean, null );

      for ( Iterator iUserAttrs = listUserAttrs.iterator(); iUserAttrs.hasNext(); )
      {
        Attribute attr = (Attribute)iUserAttrs.next();
        attrs.addAttribute( "", attr.getName(), attr.getName(), "CDATA", attr.getType()  );

      }

    }
    catch( Exception ex )
    {
      // don't care
    }

    if ( m_fUseAttributeModel )
    {
      List listNamespaces = null;


      if ( mthdNS != null )
        listNamespaces = (List)mthdNS.invoke( objBean, null );

      PropertyDescriptor[] aProps = (PropertyDescriptor[])s_mapProps.get( objBean.getClass() );

      // Namespaces get added back as attributes
      if ( listNamespaces != null )
      {
        for ( Iterator iNS = listNamespaces.iterator(); iNS.hasNext(); )
        {
          Namespace ns = (Namespace)iNS.next();

          String strNSPre = "xmlns";

          if ( ns.getPrefix().length() > 0 )
            strNSPre += ":" + ns.getPrefix();

          attrs.addAttribute( "", strNSPre, strNSPre, "CDATA", ns.getURI() );

        }

      } // end if listNamespaces != null

      Class<?>clsBean = objBean.getClass();

      Object objSchema = s_mapSchemasByClass.get(  clsBean );

      if ( objSchema == null )
      {
        objSchema = findSchema( clsBean );
        if ( objSchema == null )
          return null;

      }

      Map mapAttrs = (Map)s_mapAttrsBySchema.get( objSchema );

      List listAttrNames = null;

      if ( strElementName != null)
      {
        int nPos = strElementName.indexOf( ':' );
        strElementName = strElementName.substring( ++nPos );

        listAttrNames = (List)mapAttrs.get( strElementName.toLowerCase() );
      }

      if ( listAttrNames == null )
        return null;

      for ( Iterator iAttrNames = listAttrNames.iterator(); iAttrNames.hasNext(); )
      {
        String strLocalName = (String)iAttrNames.next();
        String strQName = strLocalName;

        int nPos = strLocalName.indexOf( ':' );

         if ( nPos > 0 )
         {
           strQName = strLocalName;
           strLocalName = strQName.substring( ++nPos );

         }

        Method mthdAttr = getMethod( strLocalName, aProps );

        if ( mthdAttr!= null )
        {

          if ( objBean instanceof VwAttrQName && nPos < 0  )
            strQName = ((VwAttrQName)objBean).getAttrQname( strLocalName );

          Object objVal = mthdAttr.invoke( objBean, null );

          if ( objVal != null )
            attrs.addAttribute( "", strLocalName, strQName, "CDATA", objVal.toString()  );

        } // end if ( mthdAttr!= null )

      } // end for()

      return attrs;

    } // end if ( m_fTreatPropsAsAttrs )


    if ( strElementName == null )
      strElementName = getClassName( objBean );

    try
    {
      Method methodAttr = objBean.getClass().getMethod( "getAttributes",
                                                        new Class[]{ java.lang.String.class } );

      if ( methodAttr != null )
        return (Attributes)methodAttr.invoke( objBean, new Object[]{ strElementName } );

    }
    catch( Exception ex )
    {
      // fall thru and return null
    }

    return null;

  } // end getObjAttrs()



  private Object findSchema( Class<?> clsBean )
  {
    String strSchemaType = clsBean.getSimpleName();

    for ( Iterator iSchemas = s_mapSchemas.values().iterator(); iSchemas.hasNext(); )
    {
      Object objSchema = iSchemas.next();

      if ( objSchema instanceof Schema )
      {
        Schema schema = (Schema)objSchema;
        if ( schema.getComplexObject( strSchemaType ) != null || schema.getComplexType( strSchemaType ) != null )
        {
          s_mapSchemasByClass.put( clsBean, schema );
          return schema;
        }
      }
    }

    return null; // Not Found
  }

  /**
   *
   * @param strName
   * @param aProps
   * @return
   */
  private Method getMethod( String strName, PropertyDescriptor[] aProps )
  {
    int nLen = aProps.length;

    for ( int x = 0; x < nLen && aProps[ x ] != null; x++)
    {
      if ( aProps[ x ].getName().equals( strName ) )
        return aProps[ x ].getReadMethod();

    } // end for()


    return null;

  } // end getMethod()


  /**
   * Sets a Map of bean property aliases to use. The map key specifies the name of the bean
   * property (without the set or get ). The map value is the xml tag that will be generated
   * for that property.
   *
   * @param mapPropAliases A Map of bean property aliases
   */
  public void setBeanPropertyAliases( Map mapPropAliases )
  { m_mapPropAliases = mapPropAliases; }



  /**
   * Build a map in cache of attribute names from schema definitions associated with
   * a top level class
   */
  private void buildElementAttrNames()
  {

    for ( Iterator iSchemas = s_mapSchemas.values().iterator(); iSchemas.hasNext(); )
    {

      Object objSchema = iSchemas.next();
      Map mapAttrsByElement = (Map)s_mapAttrsBySchema.get( objSchema );

      if ( mapAttrsByElement == null )
      {
        mapAttrsByElement = new HashMap();
        s_mapAttrsBySchema.put( objSchema, mapAttrsByElement );

      }

      if ( objSchema instanceof Schema )
        buildMapFromSchema( mapAttrsByElement, (Schema)objSchema );
      else
        buildMapFromDtd( mapAttrsByElement, (VwDtdParser)objSchema );

    } // end for()

  } // end buildElementAttrNames()

  /**
   *
   * @param mapAttrsByElement
   * @param dtdParser
   */
  private void buildMapFromDtd( Map mapAttrsByElement, VwDtdParser dtdParser )
  {

    Map mapAttrs = dtdParser.getAttributes();

    for ( Iterator iAllAttrs = mapAttrs.keySet().iterator(); iAllAttrs.hasNext(); )
    {
      String strElementName = (String)iAllAttrs.next();
      List listAttrs = (List)mapAttrs.get( strElementName );
      List listAttrNames = new LinkedList();

      for ( Iterator iAttrs = listAttrs.iterator(); iAttrs.hasNext(); )
      {
        VwDtdAttributeDecl attrDecl = (VwDtdAttributeDecl)iAttrs.next();
        String strLocalName = attrDecl.getAttrName();
        listAttrNames.add( strLocalName );

      } // end for

      mapAttrsByElement.put( strElementName.toLowerCase(), listAttrNames );

    } // end for()

  }// end buildMapFromDtd()


  /**
   * Builds an attribute name map (by element name) for an XML  schema definition
   *
   * @param mapAttrsByElement The map by element name to build
   * @param schema The Toplevel Schema instance
   */
  private void buildMapFromSchema( Map mapAttrsByElement, Schema schema )
  {


    List listContent = schema.getComplexTypes();
    listContent.addAll( schema.getElements() );
    Map<String,String> mapTempDupes = new HashMap<String, String>(); // temp to avoid derived complextype dup attribute names


    for ( Iterator iContent = listContent.iterator(); iContent.hasNext(); )
      buildAlias( schema, iContent.next() );

    for ( Iterator iContent = listContent.iterator(); iContent.hasNext(); )
    {
      Object objContent = iContent.next();
      String strElementName = null;
      List listAttributes = null;
      List<String> listAttrNames = new ArrayList<String>();
      mapTempDupes.clear();

      if ( objContent instanceof ComplexType )
      {
         strElementName = ((ComplexType)objContent).getName();
        listAttributes = ((ComplexType)objContent).getAttributes();
      }
      else
      {
        strElementName = ((Element)objContent).getName();
        listAttributes = ((Element)objContent).getAttributes( schema );
      }

      if ( listAttributes == null )
        continue;

      for ( Iterator iAttrs = listAttributes.iterator(); iAttrs.hasNext(); )
      {
        Attribute attr = (Attribute)iAttrs.next();
        String strLocalName = attr.getName();
        if ( mapTempDupes.containsKey( strLocalName ))
          continue;

        mapTempDupes.put( strLocalName, null );

        listAttrNames.add( strLocalName );

      } // end for

      if ( m_mapObjAlias.containsKey( strElementName ))
      {
        Object objAlias = m_mapObjAlias.get( strElementName );

        if ( objAlias instanceof String )
          mapAttrsByElement.put( ((String)objAlias).toLowerCase(), listAttrNames );
        else
        {
          for ( String strAliasName : (List<String>)objAlias)
            mapAttrsByElement.put( strAliasName.toLowerCase(), listAttrNames );

        }
      }
      else
      if ( strElementName != null )
        mapAttrsByElement.put( strElementName.toLowerCase(), listAttrNames );

    } // end for()

  } // end buildMapFromSchema()


  /**
   * Build the object alias map which outputs the element name defined in the xml schema as opposed to the name of the
   * java class
   * @param schema The XML schema instance
   * @param objContent The schema content object (we look for complexTypes and element schema objects )
   */
  private void buildAlias( Schema schema, Object objContent )
  {
    if ( objContent instanceof ComplexType )
    {
      ModelGroup model = ((ComplexType)objContent).getModelGroup();
      String strTypeName = ((ComplexType)objContent).getName();

      if ( strTypeName != null )
        strTypeName = stripNamespace( strTypeName );


      if ( model != null )
      {
        for ( Iterator iElements = model.getContent().iterator(); iElements.hasNext(); )
        {
          Object objElement = iElements.next();

          if ( objElement instanceof Element )
          {
            String strType = ((Element)objElement).getType();
            if ( strType == null )
              strType = ((Element)objElement).getRef();

            if ( strType == null )
              continue;

            strType = stripNamespace( strType );

            String strName = ((Element)objElement).getName();
            if ( strName == null )
              strName = ((Element)objElement).getRef();

            m_mapElementTypes.put( strName, strType );

            if ( strName != null )
              doObjAlias( strType, strName );

          } // end for()

        } // end if (m_btModel != null)
      } // end if ( objContent instanceof ComplexType

    }
    else
    if ( objContent instanceof Element )
    {
      String strTypeName = ((Element)objContent).getType();

      if ( strTypeName != null )
      {
        strTypeName = stripNamespace( strTypeName );

        if ( schema.getComplexType( strTypeName ) != null )
        {
          if ( !m_mapObjAlias.containsKey( strTypeName ))
            doObjAlias( strTypeName, ((Element)objContent).getName() );

        }
      }
    }


  }

  /**
   * Recursive method that builds an xml document from introspecting the given object.
   * Properties that return non primitive type recursivly call this method so that all\
   * objects tied to the parent bean get converted into the xml document.
   *
   * @param objBean The bean object whose property values will be used to build the vector
   * @param aProps Array of property descriptors for the objBean
   * @param listAttr a list of attributes or null
   * @param fSkipClassName If true do not generate a tag for the class name, only the classes properties
   *
   */
  private void beanToXml( Object objBean, PropertyDescriptor[] aProps,
                          Attributes listAttr, boolean fSkipClassName )
    throws Exception
  {

    String strClassName = getClassName( objBean );
    m_mapChildren.put( strClassName.toLowerCase(), new Boolean( false ) );

    Class clsBean = objBean.getClass();

    if ( !fSkipClassName )
    {
      String strObjAlias = getObjAlias( strClassName );

      if ( strObjAlias != null )
        strClassName = strObjAlias;
      else
      if ( m_fLowerCaseFirstCarClassNames )
        strClassName = strClassName.substring( 0,1  ).toLowerCase() + strClassName.substring( 1 );

      Attributes listAtt = null;

      if ( listAttr != null )
        listAtt = listAttr;
      else
        listAtt = getObjAttrs( objBean, strClassName );

      QName qnClassName = getQName( objBean );
      if ( qnClassName != null && m_fUseNamespaces )
        strClassName = qnClassName.toElementName();

       Method mthdIsParent = null;

      try
      {
        mthdIsParent = objBean.getClass().getMethod( "isParent", null );
      }
      catch( Exception ex )
      {

      }

      boolean fIsParent = true;
      if ( mthdIsParent != null )
        fIsParent = ((Boolean)mthdIsParent.invoke( objBean, null )).booleanValue();

      Method m = (Method)s_mapMixedModeObjects.get( strClassName.toLowerCase() );

      if ( m != null )
        m_xmlWriter.addChild( strClassName, (String)m.invoke( objBean, null ), listAtt, true );
      else
      if ( mthdIsParent != null && !fIsParent )
      {
        m_xmlWriter.addChild( strClassName, null, listAtt, false );
        return;
      }
      else
        m_xmlWriter.addParent( strClassName, listAtt );

    } // end if

    String strPropName = null;

    Method methodAttr = null;

    try
    {
      methodAttr = objBean.getClass().getMethod( "getAttributes",
                                                  new Class[] {java.lang.String.class} );

    }
    catch( Exception e )
    {

    }

    boolean fIgnorePropName = false;

    if ( m_fUseAttributeModel )
    {
      aProps = s_mapClassProps.get( clsBean );

      if ( aProps == null )  // walk up the superclass until we get to Object or get a property hit
      {
        Class clsSuper = clsBean.getSuperclass();

        while( clsSuper != Object.class )
        {
          aProps = s_mapClassProps.get( clsSuper );

          if ( aProps != null )
            break;

          clsSuper = clsSuper.getSuperclass();

        }
      }
      if ( aProps == null )
      {
        if ( !fSkipClassName )
          m_xmlWriter.closeParent( null );

        return;
      }

      fIgnorePropName = true;
    }
    else
    {
      if ( s_mapClassProps.containsKey( clsBean ) )
      {
        fIgnorePropName = true;
        aProps = s_mapClassProps.get( clsBean );
      }

    }

    // Dump this object's properties
    for ( int x = 0; x < aProps.length; x++ )
    {
      Attributes listPropAttr = null;

      if ( aProps[ x ] == null )
        continue;

      strPropName = aProps[ x ].getName();


      String strTemp = (String)m_mapPropAliases.get( strPropName );

      if ( strTemp != null )
        strPropName = strTemp;

      // Ignore the auto generated getClass property
      if ( strPropName.equalsIgnoreCase( "class" ) )
        continue;

      m_strCurPropName = strPropName;

      Object objAttr = null;

      if ( methodAttr != null )
        objAttr = methodAttr.invoke( objBean, new Object[]{ strPropName.toLowerCase() } );

      if ( objAttr instanceof Attributes )
        listPropAttr = (Attributes)objAttr;

      // Get read method for property
      Method m = aProps[ x ].getReadMethod();

      // if no get method specified, skip
      if ( m == null )
        continue;

      String strText = "";

      Object objVal = m.invoke( objBean, null );

      // Determine object type
      if ( objVal == null )
      {
        if ( m_strDefaultForNulls != null && !fIgnorePropName )
          m_xmlWriter.addChild( strPropName, m_strDefaultForNulls, listPropAttr );
        else
        if ( m_fGenEmptyTagsForNulls )
          m_xmlWriter.addChild( strPropName, null, null );
        else
        if ( listPropAttr != null && !fIgnorePropName)
          m_xmlWriter.addChild( strPropName, null, listPropAttr );
        else
        if ( objAttr instanceof java.util.List )
        {
          // These Attribute only tags in this case
          for ( Iterator iAttrs = ((List)objAttr).iterator(); iAttrs.hasNext(); )
          {
            Attributes attrs = (Attributes)iAttrs.next();
            m_xmlWriter.addChild( strPropName, m_strDefaultForNulls, attrs );

          } // end for

        } // end if

        continue;

      } // end if ( objVal == null )

      m_mapChildren.put( strClassName.toLowerCase(), new Boolean( true ) );

      if ( objVal instanceof String )
      {
        objVal =( (String)objVal).trim();

        if ( ((String)objVal).length() == 0 )
        {

          if ( m_strDefaultForNulls != null && !fIgnorePropName )
            m_xmlWriter.addChild( strPropName, m_strDefaultForNulls, listPropAttr );

          continue;
        }

      }

      if ( objVal instanceof VwDataObject )
      {
        handleDataObject( (VwDataObject)objVal, strClassName );
        continue;

      }

      boolean fIsPrimArray = isPrimArray( objVal );
      boolean fIsCollection = false;
      if ( !fIsPrimArray )
        fIsCollection = VwBeanUtils.isCollectionType( objVal.getClass() );

      // we normally don't include method names in the XML for attribute normal unless they
      // are primitive arrays or Collections of primitive data types
      if ( fIsCollection && fIgnorePropName )
      {
        Type gtType = m.getGenericReturnType();

        if ( VwBeanUtils.isGenericType( gtType ) )
        {
          Class clsType = VwBeanUtils.getGenericTypeAsClass(  gtType );
          if ( VwBeanUtils.isSimpleType( clsType ) )
            fIgnorePropName = false;  // override ignore here because this is a primitive collection
        }
        else
        {
          // not a generic type so we have to look at the first element
          if ( objVal instanceof List )
          {
            List list = (List)objVal;
            if ( list.size() > 0 )
            {
              if ( VwBeanUtils.isSimpleType( list.get( 0 ).getClass()  ) )
                fIgnorePropName = false;  // override ignore here because this is a primitive collection

            }

          } // end if
        } // end else
      }
      else
      if ( fIsPrimArray )
        fIgnorePropName = false;

      if ( fIgnorePropName )
        strPropName = null;

      // See what kind of return object we have
      if ( VwBeanUtils.isSimpleType( objVal.getClass() ) )
        handleSimpleType( objVal, strPropName, listPropAttr );
      else
      if ( fIsPrimArray )
        handlePrimArray( objVal, strPropName, objAttr, listPropAttr );
      else
      if ( fIsCollection )   // Collection types dump their list
        handleCollectionType( objBean, objVal, strPropName, objAttr, listPropAttr, strClassName );
      else
      if ( objVal instanceof org.w3c.dom.Element )
        m_xmlWriter.addElement( (org.w3c.dom.Element)objVal  );
      else
        handleUserType( objBean, objVal, strPropName, objAttr, listPropAttr );

    } // end for()

    if ( !fSkipClassName )
    {
      Boolean boolHasChildren = (Boolean)m_mapChildren.get( strClassName.toLowerCase() );

      if ( boolHasChildren != null && boolHasChildren.booleanValue())
        m_xmlWriter.closeParent( strClassName );
      else
        m_xmlWriter.closeParent( null );


    }

  } // end  beanToXml()



  private QName getQName( Object objBean )
  {
    Method mthdQName = null;

    try
    {
      mthdQName = objBean.getClass().getMethod( "getQName", null );
      if ( mthdQName != null )
      {
        QName qnClassName = ((QName)mthdQName.invoke( objBean, null ));
        return qnClassName;

      }
    }
    catch( Exception ex )
    {

    }

     return null;

  }

  /**
   * This resolves object alias types that have multple properties of the same object type
   * @param strClassName The class name of the alias to resolve
   * @return
   */
  private String getObjAlias( String strClassName )
  {
    Object objAlias = m_mapObjAlias.get( strClassName );
    if ( objAlias == null )
      return null;

    if ( objAlias instanceof String)
      return (String)objAlias;    // Only one found
    else
    {
      for ( String strAlias : (List<String>)objAlias )
      {
        if ( strAlias.equalsIgnoreCase( m_strCurPropName ))
          return strAlias;       // return the one that matches the current property

      }
    }

    return null;

  } // end getObjALias

  /**
   *
   * @param objUser
   * @param strPropName
   * @param objAttr
   * @param listPropAttr
   */
  private void handleUserType( Object objBean, Object objUser, String strPropName,
                               Object objAttr, Attributes listPropAttr )  throws Exception
  {

    boolean fSkipChildClassName = false;

    String strObjName = getObjName( objUser.getClass() );
    String strAlias = null;

    // Check to see if an alias override was specified
    if ( strPropName != null )
    {
      strAlias = getObjAlias( strObjName );
      if ( strAlias != null )
      {

        if ( (strAlias.equalsIgnoreCase( strPropName ) ) )
          fSkipChildClassName = true;
        //else
          strPropName = strAlias;

      } // end if

    } // end if

    if ( strAlias == null && isSchemaType( objBean, strPropName, objUser ))
      fSkipChildClassName = true;
    else
    if ( strPropName != null && strPropName.equalsIgnoreCase( strObjName ))
    {
      strPropName = strObjName;
      fSkipChildClassName = true;
    }


    Class clsChildObj = objUser.getClass();


    if ( strPropName != null  ||  (fSkipChildClassName && strPropName != null))
      m_xmlWriter.addParent( strPropName, getObjAttrs( objUser, strPropName ) );

    // See if property descriptors arfe in cache first
    PropertyDescriptor[] aChildProps = (PropertyDescriptor[])s_mapProps.get( clsChildObj );

    if ( aChildProps == null )
    {
      aChildProps = getProps( clsChildObj );
      // Get property descriptors and put'em in cache
      s_mapProps.put( clsChildObj, aChildProps );
    }

    beanToXml( objUser, aChildProps, getObjAttrs( objUser, strObjName ), fSkipChildClassName );

    if ( strPropName != null  ||  (fSkipChildClassName && strPropName != null) )
      m_xmlWriter.closeParent( strPropName );

  } // end handleUserType()

  /**
   * @param objBean
   * @param strPropName
   * @param objUser
   * @return
   */
  private boolean isAnonymouseCollection( Object objBean  )
  {
    String strBean = getObjName( objBean.getClass() );

    Object objType = s_mapComplexTypes.get( strBean );


    if ( objType instanceof ComplexType )
    {

      ModelGroup mgroup = ((ComplexType)objType).getModelGroup();
      if ( mgroup == null )
        return false;

      if ( mgroup.getMaxOccurs() != null )
        return true;
    }

    return false;

  }

  /**
   * @param objBean
   * @param strPropName
   * @param objUser
   * @return
   */
  private boolean isSchemaType( Object objBean, String strPropName, Object objUserType )
  {
    String strBean = getObjName( objBean.getClass() );

    Object objType = s_mapComplexTypes.get( strBean );

    String strUserType = getObjName( objUserType.getClass() );

    if ( objType instanceof ComplexType )
    {
      if ( strPropName == null )
        return false;

      ModelGroup mgroup = ((ComplexType)objType).getModelGroup();
      if ( mgroup == null )
        return false;


      Element eleProp = mgroup.findElement( strPropName );

      if ( eleProp == null )
      {
        if ( ((ComplexType)objType).isComplexContent() )
        {
          ComplexContent content = ((ComplexType)objType).getComplexContent();
          String strBase = null;

          if ( content.isExtension() )
            strBase = content.getExtension().getBase();

          else
          if ( content.isRestriction() )
            strBase = content.getRestriction().getBase();

          ComplexType tbase = (ComplexType)s_mapComplexTypes.get( stripNamespace(strBase ) );
          ModelGroup group = tbase.getModelGroup();

          eleProp = group.findElement( strPropName );
        }

      }

      if ( eleProp != null )
      {
        String strTypeName = eleProp.getType();

        if ( strTypeName != null )
        {
          int nPos = strTypeName.indexOf( ':' );
          if ( nPos > 0 )
            strTypeName = strTypeName.substring( ++nPos );

          // special case test for int and integer
          if ( strUserType.toLowerCase().startsWith( "int" )&& strTypeName.toLowerCase().startsWith( "int" ))
            return true;

          return strTypeName.equalsIgnoreCase( strUserType );
        }
      }
    }

    return false;
  }

  /**
   * @param class1
   * @return
   */
  private String getObjName( Class cls )
  {
    String strName = cls.getName();

    int nPos = strName.lastIndexOf( '.' );

    if ( nPos >= 0 )
      strName = strName.substring( ++nPos );

    return strName;
  }

  /**
   *
   * @param objVal
   * @param strPropName
   * @param objAttr
   * @param listPropAttr
   * @throws Exception
   */
  private void handleCollectionType( Object objBean, Object objVal, String strPropName, Object objAttr,
                                     Attributes listPropAttr,  String strClassName )  throws Exception
  {

    Iterator iObj = null;
    boolean fNeedOpenPropTag = (strPropName != null)? true:false;
    boolean fSkipClassName = false;

    PropertyDescriptor[] aChildProps = null;

    Iterator iAttrs = null;

    // Find the right iterator
    if ( objAttr instanceof List )
      iAttrs = ((List)objAttr).iterator();

    if ( objVal instanceof Collection )
      iObj = ((Collection)objVal).iterator();
    else
    if ( objVal instanceof Enumeration )
      iObj = new VwEnumerator( (Enumeration)objVal );
    else
    if ( objVal.getClass().isArray() )
      iObj = new VwArrayIterator( (Object[])objVal );
    else
    if ( objVal instanceof Map )
      iObj = ((Map)objVal).keySet().iterator();
    else
      iObj = (Iterator)objVal;

    if ( objVal instanceof Map )
      doMapCollection( (Map)objVal, iObj, strPropName );
    else
    {

      int nObjCount = 0;

       // Now iterate through the collection
      while ( iObj.hasNext() )
      {
        Object objColection = iObj.next();
        ++nObjCount;

        boolean fISchemaType = isSchemaType( objBean, strPropName, objColection );

        if ( objColection instanceof VwDataObject )
        {
          handleDataObject( (VwDataObject)objColection, strClassName );
          continue;

        }

        if ( VwBeanUtils.isSimpleType( objColection.getClass() ) )
        {
          if ( iAttrs != null )
            listPropAttr = (Attributes)iAttrs.next();

          // If this is a schema type then the property element name is used in the iteration for each occurrence
          if ( fISchemaType )
          {
            fNeedOpenPropTag = false;
            m_xmlWriter.addChild( strPropName, objColection.toString() );

          }
          else
          {
            fNeedOpenPropTag = true;
            m_xmlWriter.addParent( strPropName, listPropAttr  );
            handlePrimitiveCollection( iObj, objColection );
          }
          continue;
        }

        Class clsCollectionObj = objColection.getClass();

        String strCollectionClassName = getClassName( clsCollectionObj );

        String strAlias = null;

        strAlias = getObjAlias( strCollectionClassName );


        if ( strAlias != null )
        {
          strCollectionClassName = strAlias;
          if ( strPropName != null && strPropName.equalsIgnoreCase( strAlias ) )
          {
            fNeedOpenPropTag = false;
            fSkipClassName = true;

          }
        }
        else
        if ( strPropName != null && (isSchemaType( objBean, strPropName, objColection ) || (strPropName.equalsIgnoreCase( strCollectionClassName))))
        {
           fSkipClassName = true;
           fNeedOpenPropTag = false;

        }
        // See if property descriptors are in cache first
        aChildProps = (PropertyDescriptor[])s_mapProps.get( clsCollectionObj );

        if ( aChildProps == null )
        {
          // Get property descriptors and put'em in cache
          aChildProps = getProps( clsCollectionObj );
          s_mapProps.put( clsCollectionObj, aChildProps );
        }

        // if this is an anonymouse collection i.e a schema sequence or choice with a maxOccurs then we omit the list name
        // and only dump the object naes in the list
        if ( isAnonymouseCollection( objBean ) )
            fNeedOpenPropTag = false;

        if ( nObjCount == 1 && fNeedOpenPropTag )
          m_xmlWriter.addParent( strPropName, listPropAttr );

        if ( fSkipClassName )
          m_xmlWriter.addParent( strPropName, getObjAttrs( objColection, strPropName ) );

        beanToXml( objColection, aChildProps, getObjAttrs( objColection, strPropName ), fSkipClassName );

        if ( fSkipClassName )
          m_xmlWriter.closeParent( strPropName );

      } // end while()

      if ( nObjCount == 0 )
        return;

    } // end else


    if ( fNeedOpenPropTag )
      m_xmlWriter.closeParent( strPropName );

   }  // end handleCollectionType()

  private void handlePrimitiveCollection( Iterator iObj, Object objInitialVal )
  {
    Object objVal = objInitialVal;
    while( true )
    {

      Class clsType = objVal.getClass();
      String strTag = null;
      if ( clsType == String.class )
      {
        strTag = "string";
      }
      else
      if ( clsType == boolean.class || clsType == Boolean.class )
      {
        strTag = "boolean";
      }
      else
      if ( clsType == byte.class || clsType == Byte.class)
      {
        strTag = "byte";
      }
      else
      if ( clsType == Character.class || clsType == char.class)
      {
        strTag = "char";
      }
      else
      if ( clsType == short.class || clsType == Short.class)
      {
        strTag = "short";
      }
      else
      if ( clsType == int.class || clsType == Integer.class)
      {
        strTag = "int";
      }
      else
      if ( clsType == long.class || clsType == Long.class)
      {
        strTag = "long";
      }
      else
      if ( clsType == float.class || clsType == Float.class )
      {
        strTag = "float";
      }
      else
      if ( clsType == double.class || clsType == Double.class)
      {
        strTag = "double";
      }

      m_xmlWriter.addChild( strTag, objVal.toString() );

      if ( iObj.hasNext() )
        objVal = iObj.next();
      else
        return;
    } // end for()
  }

  /**
   * Handle a primitive array
   * @param objVal
   * @param strPropName
   * @param objAttr
   * @param listPropAttr
   */
  private void handlePrimArray( Object objVal, String strPropName, Object objAttr, Attributes listPropAttr )
  {

    Class clsType = objVal.getClass();
    int nLen = Array.getLength( objVal );
    String strTag = null;
    String strVal = null;

    List listAttrs = null;

    if ( objAttr instanceof List )
      listAttrs = (List)objAttr;

    m_xmlWriter.addParent( strPropName, null );

    for ( int y = 0; y < nLen; y++ )
    {

      if ( listAttrs != null )
        listPropAttr = (Attributes)listAttrs.get( y );

      if ( clsType == String[].class )
      {
        strVal = Array.get( objVal, y ).toString();
        strTag = "string";
      }

      if ( clsType == boolean[].class )
      {
        strVal = String.valueOf( Array.getBoolean( objVal, y ) );
        strTag = "boolean";
      }
      else
      if ( clsType == byte[].class )
      {
        strVal = String.valueOf( Array.getByte( objVal, y ) );
        strTag = "byte";
      }
      else
      if ( clsType == char[].class )
      {
        strVal = String.valueOf( Array.getChar( objVal, y ) );
        strTag = "char";
      }
      else
      if ( clsType == short[].class )
      {
        strVal = String.valueOf( Array.getShort( objVal, y ) );
        strTag = "short";
      }
      else
      if ( clsType == int[].class )
      {
        strVal = String.valueOf( Array.getInt( objVal, y ) );
        strTag = "int";
      }
      else
      if ( clsType == long[].class )
      {
        strVal = String.valueOf( Array.getLong( objVal, y ) );
        strTag = "long";
      }
      else
      if ( clsType == float[].class )
      {
        strVal = String.valueOf( Array.getFloat( objVal, y ) );
        strTag = "float";
      }
      else
      if ( clsType == double[].class )
      {
        strVal = String.valueOf( Array.getDouble( objVal, y ) );
        strTag = "double";
      }

      m_xmlWriter.addChild( strTag, strVal, listPropAttr );

    } // end for()

    m_xmlWriter.closeParent( strPropName );

  } // end handlePrimArray()


  /**
   * Handle a primitive (simple type) property
   * @param objVal  The property value
   * @param strPropName  the property name
   * @param listPropAttr the property attributes
   */
  private void handleSimpleType( Object objVal, String strPropName, Attributes listPropAttr )
  {
    String   strText = objVal.toString();      // Simple types dump property name and value

    if ( objVal instanceof Boolean )
    {
      if ( strText.equals( "true" ) )
        strText = m_strTrue;
      else
        strText = m_strFalse;
    }

    m_xmlWriter.addChild( strPropName, strText, listPropAttr );

  } // end handleSimpleType()


  /**
   * Insert generated XML from dataobject into the cuurrent xml stream
   *
   * @param dobj The VwDataOBject instance to generate XML from
   */
  private void handleDataObject( VwDataObject dobj, String strClassName ) throws Exception
  {
    int nLevel = m_xmlWriter.getLevel();

    String strXML = dobj.toXml( null, null, m_xmlWriter.isFormatted(), nLevel, strClassName );

    m_xmlWriter.addXml( strXML.trim() );

  } // end handleDataObject()

  /**
   * Geneates Xml for a map
   *
   * @param mapCollection The Map collection
   * @param iKeyIterator The key itereator
   */
  private void doMapCollection( Map mapCollection, Iterator iKeyIterator, String strPropName )
    throws Exception
  {

    m_xmlWriter.addParent( strPropName );

    // Get the object for each key

    int nCount = 0;

    while ( iKeyIterator.hasNext() )
    {
      Object objKey = iKeyIterator.next();

      AttributesImpl listAttr = null;

      if ( ++nCount == 1 )
      {
        listAttr = new AttributesImpl();

        listAttr.addAttribute( "", "id", "id", "ID", objKey.toString() );
        listAttr.addAttribute( "", "type", "type", "CDATA", objKey.getClass().getName() );
      }
      else
      {
        listAttr = new AttributesImpl();

        listAttr.addAttribute( "", "id", "id", "ID", objKey.toString() );

      }

      Object objData = mapCollection.get( objKey );
      if ( VwBeanUtils.isSimpleType( objData.getClass() ) )
      {
        String strSimpleName = (String)m_mapPropAliases.get( strPropName );
        if ( strSimpleName == null )
          strSimpleName = "value";

        m_xmlWriter.addChild( strSimpleName, objData.toString(), listAttr );
      }
      else
      {

        Class clsChildObj = objData.getClass();

        // See if property descriptors arfe in cache first
        PropertyDescriptor[] aChildProps = (PropertyDescriptor[])s_mapProps.get( clsChildObj );

        if ( aChildProps == null )
        {
          // Get property descriptors and put'em in cache
          aChildProps = getProps( clsChildObj );
          s_mapProps.put( clsChildObj, aChildProps );
        }

        beanToXml( objData, aChildProps, listAttr, false );

      }

    } // end while()

 } // end doMapCollection()


  /**
   * Extracts the class name from an object instance, stripping off any package specified
   *
   * @param objBean The object to extract the class name for
   *
   * @return The class name for the object minus the package specification
   */
  private static String getClassName( Object objBean )
  {

    Class clsBean = objBean.getClass();

    String strClassName = clsBean.getName();

    // Strip of package name from class name
    int nPos = strClassName.lastIndexOf( '.' );

    if ( nPos >= 0 )
      strClassName = strClassName.substring( nPos + 1 );

    return strClassName;

  } // end getClassName()



  /**
   * Extracts the class name from a Class instance
   *
   * @param clsBean The class to extract the class name for
   *
   * @return The class name for the object minus the package specification
   */
  private static String getClassName( Class clsBean )
  {

    String strClassName = clsBean.getName();

    // Strip of package name from class name
    int nPos = strClassName.lastIndexOf( '.' );

    if ( nPos >= 0 )
      strClassName = strClassName.substring( nPos + 1 );

    return strClassName;

  } // end getClassName()

  /**
   * Determins if an array object is an array of primitive types
   */
  public static boolean isPrimArray( Object objArray )
  { return isPrimArray( objArray.getClass() ); }


  public static boolean isPrimArray( Class cls )
  {

    if ( cls == String[].class )
     return true;
    else
    if ( cls == boolean[].class )
      return true;
    else
    if ( cls == byte[].class )
      return true;
    else
    if ( cls == char[].class )
      return true;
    else
    if ( cls == short[].class )
      return true;
    else
    if ( cls == int[].class )
      return true;
    else
    if ( cls == long[].class )
      return true;
    else
    if ( cls == float[].class )
      return true;
    else
    if ( cls == double[].class )
      return true;

    return false;

  } // end isPrimArray

  /**
   * Adds a schema/dtd associated to a Java package
   *
   * @param javaPackage The Package class returned the getPackage medthod on any Class object
   * @param urlSchema The Schema's URL
   * @throws Exception
   */
  public void addSchema( URL urlSchema, Package pkgSchema ) throws Exception
  {
    Object objUrls = s_mapSchemasByPackage.get( pkgSchema );

    if ( objUrls == null )
    {
      List listUrls = new LinkedList();
      listUrls.add( urlSchema );
      s_mapSchemasByPackage.put( pkgSchema, listUrls );
    }
    else
    {
      boolean fInList = false;
      for ( Iterator iUrlSchemas = ((List)objUrls).iterator(); iUrlSchemas.hasNext(); )
      {
        URL urlInList = (URL)iUrlSchemas.next();

        if ( urlInList.equals( urlSchema ))
        {
          fInList = true;
          break;  // ALready in list
        }

      }

      // Add it to list
      if ( !fInList )
       ((List)objUrls).add( urlSchema );

    }

    m_listSchemasToProcess.add( urlSchema );

    return;


  } // end processSchema()

  /**
   * Process an XML schema
   *
   * @param schema The XML Schema instance
   */
  private void processXMLSchema( Schema schema ) throws Exception
  {

    // Look at the schema components to toplevel elements or complex type definitions
    for ( Iterator iComp = schema.getContent().iterator(); iComp.hasNext(); )
    {
      Object objComp = iComp.next();

      if ( objComp instanceof Element  )
      {
        Element element = (Element)objComp;

        String strType = element.getType();
        String strName = element.getName();

        if ( strName == null )
          strName = element.getRef();

        if ( strType != null )
        {
	        int ndx = strType.indexOf( ':' );
	        if ( ndx > 0 )
	          strType = strType.substring( ++ndx );
        }
        // See if this type matches our toplevel class name
        if ( strType != null && strType.equalsIgnoreCase( m_strTopLevelClassName ) )
        {
          //Save Toplevel class translations in static map for repeatitive lookup
          doObjAlias( m_strTopLevelClassName, strName );
          m_strTopLevelClassName = strName;

        }

        // See if this element is an anonymous complexType
        if ( element.isComplexType() )
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
   * Process a DTD schema
   *
   * @param mapElements
   */
  private void processDTDSchema( Map mapElements ) throws Exception
  {

    // Look at the schema components and update the object alias map so that xml tags
    // are generated in the same case that they were defined in the dtd
    for ( Iterator iElements = mapElements.values().iterator(); iElements.hasNext(); )
    {

      VwDtdElementDecl eleDecl = (VwDtdElementDecl)iElements.next();

      if ( eleDecl.getContentType() == VwDtdElementDecl.PARENT ||
           eleDecl.getContentType() == VwDtdElementDecl.MIXED )
      {
        String strName = eleDecl.getName();
        doObjAlias( strName, strName );

      }

    } // end for

  } // end processDTDSchema()

  /**
   * Process the child tags to resolve type alias and collections definitions
   */
  private void processComplexType( Schema schema, String strName, ComplexType type ) throws Exception
  {

    if ( type.getName() != null )
      s_mapComplexTypes.put( strName, type );

    ModelGroup group = type.getModelGroup();

    if ( group == null )
      return;

    processElementGroup( group, schema );


  } // end processComplexType()


  /**
   *
   * @param group
   * @param schema
   */
  private void processElementGroup( ModelGroup group, Schema schema ) throws Exception
  {
    Iterator iGroup = group.getContent().iterator();

    while ( iGroup.hasNext() )
    {

      Object objComp = iGroup.next();

      if ( objComp instanceof ModelGroup )
        processElementGroup( (ModelGroup)objComp, schema );
      else
      if ( objComp instanceof Element )
      {

        Element element = (Element)objComp;

        String strEleName = element.getName();
        String strEleType = element.getType();

        if ( strEleName == null )
          strEleName = element.getRef();

        if ( strEleType != null )
        {
          // See if this element is an anonymous complexType
          if ( element.isComplexType() )
            processComplexType( schema, strEleName, element.getComplexType() );

        }
        else
        {
          ComplexType complexType = ((Element)objComp).getComplexType();

          if ( complexType != null )
          {
            processComplexType( schema, strEleName, complexType );

          }

        } // end else

      } // end if ( objComp instanceof VwSchemaElement )

    } // end while()

  } // end processElementGroup

  /**
   * Based on the schema definitions, revise the order of the bean properties
   * to match the schema spec.
   */
   private PropertyDescriptor[] orderBySchema( Class clsBean, PropertyDescriptor[] aProps,
                                               Schema schema ) throws Exception
   {

     PropertyDescriptor[] aOrderedProps = new PropertyDescriptor[ aProps.length ];

     String strClassName = clsBean.getSimpleName();

     String strAlias = getObjAlias( strClassName );
     if ( strAlias != null )
       strClassName = strAlias;


     Object objComp = schema.getComponent( strClassName );

     if ( objComp == null )
       objComp = schema.getComplexObject( strClassName );

     if ( objComp == null )
       return null;

     String strType = null;

     if ( objComp instanceof Element )
     {
       strType = ((Element)objComp).getType();

       if (strType != null )
       {
         strType = stripNamespace( strType );

         Object objType = schema.getComplexObject( strType );

         if ( objType  != null )
           objComp = objType;
       }
     }

     int nGot = 0;

     if ( m_fUseAttributeModel)
     {
       nGot = processSchemaAttributes( schema, objComp, aProps, aOrderedProps, nGot );
       if ( objComp instanceof ComplexType )
       {
         if ( ((ComplexType)objComp).hasChildElements() )
           nGot = processGroup( schema, objComp, aProps, aOrderedProps, nGot );
       }
     }
     else
       nGot = processGroup( schema, objComp, aProps, aOrderedProps, 0 );

     if ( nGot == 0 )
        return null;

     return aOrderedProps;

   } // end orderBySchema()

  /**
   * Try to locate a schema  for the class name specified
   * @param listSchemas a List of Schema objects to search
   * @param strClassName The name of the class (comp[lexType to search for
   * @return
   */
  private Schema findSchemaComponent( List<Schema> listSchemas, String strClassName )
  {
    for ( Schema schema : listSchemas )
    {
      Object objComp = schema.getComponent( strClassName );

      if ( objComp == null )
        objComp = schema.getComplexObject( strClassName );

      if ( objComp != null )
        return schema;


    }

    return null;
  }

  /**
   *
   * String off namespace from name if exists
   * @param strType
   * @return
   */
  private String stripNamespace( String strType )
  {
    int nPos = strType.indexOf( ':' );

    if ( nPos < 0 )
      return strType;

    return strType.substring( ++nPos );

  } // end stripNamespace()


  private void processSchemas() throws Exception
  {
    if ( m_listSchemasToProcess.size() == 0 )
      return;

    for ( Iterator iSchemaURLs = m_listSchemasToProcess.iterator(); iSchemaURLs.hasNext(); )
    {
      URL urlSchema = (URL)iSchemaURLs.next();


      String strName = urlSchema.toString();

      if ( strName.endsWith( "xsd" ) )
      {
        Schema schema = (Schema)s_mapSchemas.get( urlSchema );

        if ( schema == null )
        {
          VwSchemaReaderImpl schemaReader = new VwSchemaReaderImpl();
          schema = schemaReader.readSchema( urlSchema );
          schema.mergeIncludes();
          s_mapSchemas.put( urlSchema, schema );
        }

        processXMLSchema( schema );


      }
      else
      if ( strName.endsWith( "wsdl" ) ) // we support wsdl urls as well. The wsdl must contain a schema in the types sectiopn
      {
        Schema schema = (Schema)s_mapSchemas.get( urlSchema );

        if ( schema == null )
        {
          VwWSDLReaderImpl reader = new VwWSDLReaderImpl();
          Definition def = reader.readWsdl( urlSchema );
          Types types = def.getTypes();
          if ( types == null )
            throw new Exception( "The WSDL document : " + strName + " does not conatins a types section with an XML Scheam defined" );

          schema = types.getSchema();

          if ( schema == null )
            throw new Exception( "The WSDL document : " + strName + " does not conatins a types section with an XML Scheam defined" );

          schema.mergeIncludes();
          s_mapSchemas.put( urlSchema, schema );
        }

        processXMLSchema( schema );


      }
      else
      if ( strName.endsWith( "dtd" ) )
      {
        VwDtdParser parser = (VwDtdParser)s_mapSchemas.get( urlSchema );

        if ( parser == null )
        {
          parser = new VwDtdParser( urlSchema, null );
          parser.process();
          s_mapSchemas.put( urlSchema, parser );

        }

        processDTDSchema( parser.getElements() );

      }
      else
        throw new Exception( m_urlSchema.getPath() +
                             " is an unrecognized schema type, looking for dtd or xsd extensions" );


    }

  }


  private int processSchemaAttributes( Schema schema, Object objComp, PropertyDescriptor[] aProps, PropertyDescriptor[] aOrderedProps, int i )
  {

    List listAttrs = null;

    if ( objComp instanceof Element )
      listAttrs = ((Element)objComp).getAttributes( schema );
    else
    if ( objComp instanceof ComplexType )
      listAttrs = ((ComplexType)objComp).getAttributes();

    Map<String,String>mapDups = new HashMap<String, String>();

    int ndx = 0;

    if ( listAttrs != null )
    {
      for ( Iterator iAttrs = listAttrs.iterator(); iAttrs.hasNext(); )
      {
        Attribute attr = (Attribute)iAttrs.next();

        String strName = attr.getName();

        if ( mapDups.containsKey( strName ))
          continue;

        mapDups.put( strName, null );

        for ( int x = 0; x < aProps.length; x++ )
        {
          if ( aProps[ x ].getName().equalsIgnoreCase( strName ) )
          {
            aOrderedProps[ ndx ] = aProps[ x ];
            aOrderedProps[ ndx ].setName( strName ); // Use the case specified in the schema
            ++ndx;
            break;
          }

        } // end for()

      }
    }

    return ndx;

  }

  /**
    *
    * @param objComp
    * @param aOrderedProps
    */
   private int processGroup( Schema schema,  Object objComp, PropertyDescriptor[] aProps,
                              PropertyDescriptor[] aOrderedProps, int ndx )
   {
     ModelGroup group = null;

     if ( objComp instanceof Element )
     {
       group = ((Element)objComp).getModelGroup( schema );
       if ( group == null )
         return 0;
     }
     else
     if ( objComp instanceof ComplexType )
     {
       if ( ((ComplexType)objComp).isComplexContent() )
       {
         ComplexContent content = ((ComplexType)objComp).getComplexContent();
         String strBase = null;

         if ( content.isExtension() )
           strBase = content.getExtension().getBase();

         else
         if ( content.isRestriction() )
           strBase = content.getRestriction().getBase();

         ComplexType tbase = schema.getComplexType( strBase );
         group = tbase.getModelGroup();
         if ( group != null )
           ndx = processModelGroup( schema, group, aOrderedProps, aProps, ndx );


       }

       group = ((ComplexType)objComp).getModelGroup();
     }
     else
     if ( objComp instanceof ModelGroup )
       group = (ModelGroup)objComp;

     if ( group == null )
       return 0;

     ndx = processModelGroup( schema, group, aOrderedProps, aProps, ndx );


     return ndx;

   } // end processGroup()


  private int processModelGroup( Schema schema, ModelGroup group, PropertyDescriptor[] aOrderedProps, PropertyDescriptor[] aProps,  int ndx  )
  {
    for ( Iterator iElements = group.getContent().iterator(); iElements.hasNext(); )
    {
      Object objElement = iElements.next();

      if ( objElement instanceof Element )
      {
        String strName = ((Element)objElement).getName();

        if ( strName == null )
          strName = ((Element)objElement).getRef();

        strName = stripNamespace(strName );
        for ( int x = 0; x < aProps.length; x++ )
        {
          if ( aProps[ x ].getName().equalsIgnoreCase( strName ) )
          {
            aOrderedProps[ ndx ] = aProps[ x ];
            aOrderedProps[ ndx ].setName( strName ); // Use the case specified in the schema
            ++ndx;
            break;
          }

        } // end for()

      } // end if
      else
      if ( objElement instanceof ModelGroup )
        ndx = processGroup( schema, objElement, aProps, aOrderedProps, ndx );


    } // end for ( Iterator ... )

    return ndx;


  }

  /**
   * Based on the schema definitions, revise the order of the bean properties
   * to match the schema spec.
   */
   private PropertyDescriptor[] orderByDtd( Class clsBean, PropertyDescriptor[] aProps,
                                            VwDtdParser parser ) throws Exception
   {

     PropertyDescriptor[] aOrderedProps = new PropertyDescriptor[ aProps.length ];

     String strClassName = clsBean.getName();

     int nPos = strClassName.lastIndexOf( '.' );

     if ( nPos >= 0 )
       strClassName = strClassName.substring( nPos + 1 );

     String strAlias = getObjAlias( strClassName );
     if ( strAlias != null )
       strClassName = strAlias;

     // Get elements Map defined in this DTD
     Map mapElements = parser.getElements();

     VwDtdElementDecl eleDecl = (VwDtdElementDecl)mapElements.get( strClassName.toLowerCase() );

     if ( eleDecl == null )
       return null;

     if ( eleDecl.getContentType() == VwDtdElementDecl.ANY )
       return aProps;

     if ( eleDecl.getContentType() == VwDtdElementDecl.MIXED )
     {
       for ( int x = 0; x < aProps.length; x++ )
       {
         if ( aProps[ x ].getName().equalsIgnoreCase( strClassName ) )
         {
           s_mapMixedModeObjects.put( strClassName.toLowerCase(), aProps[ x ].getReadMethod() );
           break;

         } // end if

       } // end for
     } // end if

     if ( m_fUseAttributeModel)
     {
       Map mapAttrs = parser.getAttributes();

       List listAttrs = (List)mapAttrs.get( strClassName );

       if ( listAttrs != null)
         orderByProps( listAttrs, aProps, aOrderedProps );


     }
     else
     {
       ModelGroup group = eleDecl.getGroup();
       handleDtdGroup( group, aProps, aOrderedProps );

     }

     if ( aOrderedProps[ 0 ] == null )
       return null;

     return aOrderedProps;


   } // end orderByDtd()


  /**
   * Reorder jaava properties as defined by an xml schema/dtd
   * @param listAttrs
   * @param aProps
   * @param aOrderedProps
   */
  private void orderByProps( List listAttrs, PropertyDescriptor[] aProps, PropertyDescriptor[] aOrderedProps )
  {
    int x = 0;

    for ( Iterator iAttrs = listAttrs.iterator(); iAttrs.hasNext(); )
    {
      VwDtdAttributeDecl attr = (VwDtdAttributeDecl)iAttrs.next();

      String strName = attr.getAttrName();

      for ( int y = 0; y < aProps.length; y++)
      {
        if ( aProps[ y ].getName().equals( strName ) )
        {
          aOrderedProps[ x ] = aProps[ y ];
          aOrderedProps[ x ].setName( strName ); // Use the case specified in the schema
          ++x;
           break;

        }
      }
    }
  }


  /**
   * Propcess a DTD group level element
   * @param group
   * @param aProps
   * @param aOrderedProps
   * @return
   */
  private int handleDtdGroup( ModelGroup group, PropertyDescriptor[] aProps,
                               PropertyDescriptor[] aOrderedProps )
   {

     int ndx = 0;


     for ( ndx = 0; ndx < aOrderedProps.length && aOrderedProps[ ndx ] != null; ++ndx );

     for ( Iterator iElements = group.getContent().iterator(); iElements.hasNext(); )
     {
       Object objElement = iElements.next();

       if ( objElement instanceof Element )
       {
         String strName = ((Element)objElement).getName();

         if ( strName == null )
           strName = ((Element)objElement).getRef();

         strName = stripNamespace( strName );

         for ( int x = 0; x < aProps.length; x++ )
         {
           if ( aProps[ x ].getName().equalsIgnoreCase( strName ) )
           {
             aOrderedProps[ ndx ] = aProps[ x ];
             aOrderedProps[ ndx ].setName( strName ); // Use the case specified in the schema
             ++ndx;
             break;
           }

         } // end for()

       } // end if
       else
       if ( objElement instanceof ModelGroup )
         ndx = handleDtdGroup( (ModelGroup)objElement, aProps, aOrderedProps );


     } // end for ( Iterator ... )

     return ndx;

   } // end handleDtdGroup()


  /**
   * Strips name of package and returns the the name of the class
   * @param clsObject
   * @return
   */
  private String stripPackage( Class clsObject )
  {
    String strName = clsObject.getName();
    int nPos = strName.lastIndexOf( '.' );

    if ( nPos >= 0 )
      return strName.substring( ++nPos );

    return strName;

  } // end stripPackage

} // end class VwBeanToXml{}


// *** End of VwBeanToXml
