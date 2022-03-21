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
import com.vozzware.wsdl.util.VwWSDLReaderImpl;
import com.vozzware.xml.dtd.VwDtdAttributeDecl;
import com.vozzware.xml.dtd.VwDtdElementDecl;
import com.vozzware.xml.dtd.VwDtdParser;
import com.vozzware.xml.namespace.QName;
import com.vozzware.xml.schema.util.VwSchemaReaderImpl;

import javax.wsdl.Definition;
import javax.wsdl.Types;
import javax.xml.schema.Attribute;
import javax.xml.schema.ComplexContent;
import javax.xml.schema.ComplexType;
import javax.xml.schema.Element;
import javax.xml.schema.ModelGroup;
import javax.xml.schema.Schema;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.net.URL;
import java.text.SimpleDateFormat;
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
import java.util.TimeZone;


/**
 * This class converts a bean or a list of beans (of the same class type) to an json document
 */
public class VwBeanToJson
{
  private Class               m_clsTopLevel;
  private Class               m_clsCollectionOverride;

  private URL                 m_urlSchema;           // Current schema to use if specified

  private Map                 m_mapPropAliases = new HashMap();       // Map of property aliases
  private Map<String,Object>  m_mapObjAlias = new HashMap<String,Object>();      // Map of tag to object name alias
  private Map<String,Boolean> m_mapChildren = new HashMap<String,Boolean>();      // Map of tag to object name alias

  
  private static Map<Class,PropertyDescriptor[]>  s_mapProps = Collections.synchronizedMap( new HashMap<Class,PropertyDescriptor[]>() );       // Map of cached property descriptors
  private static Map<Class,PropertyDescriptor[]>  s_mapClassProps = Collections.synchronizedMap( new HashMap<Class,PropertyDescriptor[]>() );

  private static Map          s_mapTopLevelTypes = Collections.synchronizedMap( new HashMap() );
  private static Map<URL,Object> s_mapSchemas =          Collections.synchronizedMap( new HashMap<URL,Object>() );
  private static Map          s_mapSchemaObjAliases = Collections.synchronizedMap( new HashMap() );
  private static Map          s_mapMixedModeObjects = Collections.synchronizedMap( new HashMap() );
  private static Map 		      s_mapSchemasByClass = Collections.synchronizedMap( new HashMap() );
  private static Map<Package,List<URL>>  s_mapSchemasByPackage = Collections.synchronizedMap( new HashMap<Package,List<URL>>() );
  private        List         m_listSchemasToProcess = new LinkedList();
  private static List<Class>  s_listSuperclassFilters; // List of SuperClass types to exclude from PropertyDescriptor lists
  private static Map          s_mapComplexTypes = Collections.synchronizedMap( new HashMap() );
  
  private String              m_strDefaultForNulls;   // Default tag data for props that return nulll

  private String              m_strTopLevelClassName; // Top level bean class json is being genned from

  private String              m_strCurPropName;

  private String              m_strTrue = "true";
  private String              m_strFalse = "false";

  private boolean             m_fCapsOnFirstCharacter;

  private VwJsonWriter m_jsonWriter;                 // json formatter class
  private boolean m_fLowerCaseFirstCarClassNames = true;

  private VwDataObject m_mapElementTypes;
  private boolean m_fUseAttributeModel = false;
  private boolean m_fForceArray = false;

  private String m_strDateFormat = "EEE MMM dd yyyy HH:mm:ss zzz";

  private Map<String,String>m_mapIgnoreProps = null;

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
  public VwBeanToJson()
  { this( null, null, false, 0 ); }

  /**
   * Constructor
   *
   *
   * @param strDefaultForNulls The default placholder string when properties of a bean return
   * null. If this parameter is null, then the property will be omitted from the json document.
   *
   * @param fFormatted if true, the json document will insert CR/LF and indentation characters
   * for tag parentage.
   *
   * @param nIndentLevel The indentation level to start the formatting in. Each level nbr results
   * in an indentation of 2 spaces. This paramter only has affect if fFormatted is true. 0 should
   * be specified for the standard indentation.
   */
  public VwBeanToJson( String strXMLDecl, String strDefaultForNulls,
                       boolean fFormatted, int nIndentLevel )
  {
    addSuperClassPropertyFilter( VwDVOBase.class );
    
    try
    {
      // NOT Sure if we need these this.setContentMethods( VwDate.class, "getTime", true );
      //this.setContentMethods( Date.class, "getTime", true );
    }
    catch( Exception ex )
    {
      ex.printStackTrace();
    }
    
    
    m_strDefaultForNulls = strDefaultForNulls;

    m_jsonWriter = new VwJsonWriter( fFormatted, nIndentLevel );


  } // end VwBeanToXml()

  public void setCollectionClassOverride( Class clsCollectionOverRide )
  {
    m_clsCollectionOverride = clsCollectionOverRide;
  }

  public void setDateFormat( String strDateFormat )
  {
    m_strDateFormat = strDateFormat;

  }

  public String getDateFormat()
  { return m_strDateFormat; }


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
    m_jsonWriter.setFormattedOutput( fFormatOutput );
    m_jsonWriter.setLevel( nStartingIndentLevel );
    
  }

  /**
   * Capitalize first character of property names if this option is on. The default is lower case for Java
   * @param fCapsOnFirstCharacter If true captialize first character in property names
   */
  public void setCapsOnFirstCharacter( boolean fCapsOnFirstCharacter )
  {
    m_fCapsOnFirstCharacter = fCapsOnFirstCharacter;
    m_jsonWriter.setCapsOnForstCharacter( m_fCapsOnFirstCharacter );

  }

  public void setIgnoreProps( Map<String,String>mapIgnoreProps )
  {
    m_mapIgnoreProps = mapIgnoreProps;

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
    {
      s_listSuperclassFilters = new ArrayList<Class>();
    }
    
    if ( s_listSuperclassFilters.indexOf( classToFilter ) < 0 )
    {
      s_listSuperclassFilters.add( classToFilter );
    }
  }
  
  /**
   * Removes a superclass filter
   * 
   * @param classToRemove The class to remove from the list
   */
  public static void removeSuperClassPropertyFilter( Class classToRemove )
  {
    if ( s_listSuperclassFilters != null )
    {
      s_listSuperclassFilters.remove( classToRemove );
    }
    
  }
  
  
  /**
   * The default value to use for null method values. The default behaviour is to not output json for properties
   * that return null.
   * 
   * @param strDefaultForNulls The value to output for properties that return null during serialization
   */
  public void setDefaultForNulls( String strDefaultForNulls )
  { m_strDefaultForNulls = strDefaultForNulls; }
  
  
  
  
  /**
   * Adds an object to tag alias
   * 
   * @param clsObject The class of the object
   * @param strTagAlias The name of the json property tag that is generated for this class name. 
   *
   */
  public void setObjectElementName( Class clsObject, String strTagAlias )
  { doObjAlias(  stripPackage( clsObject ), strTagAlias );  }

  /**
   * Forces the Json output string to be an array regardless of the listbeans size
   * @param fForceArray True to trun off force, false (the default) to go by the size of the bean list passed
   */
  public void setForceArray( boolean fForceArray )
  { m_fForceArray = fForceArray; }


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
      {
        ((List<String>)objAlias).add( strAlias );
      }
      
    }
    else
    {
      m_mapObjAlias.put( strObject, strAlias );
    }
      
    
  }


  
  /**
   * Convert a bean to an Json string
   *
   * @param objBean A bean to convert to xml
   */
  public String serialize( Object objBean ) throws Exception
  {

    if ( objBean == null )
    {
      return null;
    }

    if ( VwBeanUtils.isCollectionType( objBean  ) )
    {
      return serialize( (List)objBean );
    }

    List listBeans = new ArrayList( 1 );
    listBeans.add( objBean );
    return serialize( listBeans );

  }

  
  /**
   * Convert a list of beans to a Json string
   *
   * @param listBeans A lsit of beans to convert to this document
   */
  public String serialize( List listBeans ) throws Exception
  {

    m_strTopLevelClassName = null;

    if ( listBeans.size() == 0 )
    {
      return "[]"; // empty array
    }

    // Get property descriptor for the first in the list since the others are the same

    if ( m_clsCollectionOverride != null )
    {
      m_clsTopLevel = m_clsCollectionOverride;
    }
    else
    {
      m_clsTopLevel = listBeans.get( 0 ).getClass();
    }

    String strClassName = getClassName( m_clsTopLevel );

    m_strTopLevelClassName = strClassName;
    
    processSchemas();
    
    String strAlias = getObjAlias( strClassName );

    if ( strAlias != null )
    {
      strClassName = strAlias;
    }
    else
    {
      if ( m_fLowerCaseFirstCarClassNames)
      {
        strClassName = strClassName.substring( 0, 1 ).toLowerCase() + strClassName.substring( 1 );
      }
      
    }


    if ( m_strTopLevelClassName == null  )
    {
      m_strTopLevelClassName = strClassName;
    }

    String strTemp =  getObjAlias( m_strTopLevelClassName );

    if (strTemp != null )
    {
      m_strTopLevelClassName = strTemp;
    }

    m_jsonWriter.clear();


    // If property descriptor array not in cache, get it and put it in cache
    PropertyDescriptor[] aProps = (PropertyDescriptor[])s_mapProps.get( m_clsTopLevel );

    if ( aProps == null )
    {
      aProps = getProps( m_clsTopLevel );
      s_mapProps.put( m_clsTopLevel, aProps );

    }


    Iterator iBeans = listBeans.iterator();


    boolean fPrimCollection = VwBeanUtils.isSimpleType ( listBeans.get( 0 ).getClass() );

    // See if this is a primitive collection type

    if ( fPrimCollection )
    {
      return doPrimCollection( listBeans );
    }

    if ( listBeans.size() > 1 || m_fForceArray )
    {
      m_jsonWriter.beginArray();
    }


    while ( iBeans.hasNext() )
    {
      Object objBean = iBeans.next();

      if ( m_clsCollectionOverride != null )
      {
        Class clsBean = objBean.getClass();
        aProps = getProps( clsBean );

        if ( !s_mapProps.containsKey( clsBean ) )
        {
          s_mapProps.put( clsBean, aProps );
        }

      }

      QName qnClassName = getQName( objBean );
      if ( qnClassName != null )
      {
        m_strTopLevelClassName = qnClassName.toElementName();
      }
      
      m_jsonWriter.beginObject( null );

      beanToXml( objBean, aProps, true );

      m_jsonWriter.endObject();

    } // end while()

    if ( listBeans.size() > 1 || m_fForceArray )
    {
      m_jsonWriter.endArray();
    }

    return m_jsonWriter.toString();

  } // end toXml()


  /**
   * Make a Json primitive array
   * @param listValues
   * @return
   */
  private String doPrimCollection( List<?>listValues )
  {
    Object[] aValues = new Object[ listValues.size() ];

    listValues.toArray( aValues );

    boolean fNeedQuotes = false;

    Class clsEntry = listValues.get( 0 ).getClass();

    if (  clsEntry == String.class || clsEntry == Date.class )
    {
      fNeedQuotes = true;

    }

    m_jsonWriter.makePrimitiveArray(  aValues, fNeedQuotes );

    return m_jsonWriter.toString();


  }

  /**
   * Sets the json tag data string that will be generated for boolean properties that return a value of true
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
   * Sets the json tag data string that will be generated for boolean properties that return a value of false
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
   * Orders properties by an xml Schema or DTD definition (if one exists)
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
    {
      return aProps;
    }
     
    PropertyDescriptor[] aOrderedProps = null;
    
    for ( URL urlSchema : listUrls  )
    {
     
      Object objSchema = s_mapSchemas.get( urlSchema );

      // Process all registered schemas until we get a hot on the class
      if ( objSchema instanceof Schema )
      {
        aOrderedProps =  orderBySchema( clsBean, aProps, (Schema)objSchema );
      }
      else
      {
        aOrderedProps = orderByDtd( clsBean, aProps, (VwDtdParser)objSchema );
      }
      
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
    {
      aProps = Introspector.getBeanInfo( clsBean, clsStopAt ).getPropertyDescriptors();
    }
    else      
    {
      aProps = Introspector.getBeanInfo( clsBean  ).getPropertyDescriptors();
    }
    
    return orderProps( clsBean, aProps );

  } // end getProps()


  /**
   * Try to find a schema based on class name
   * @param clsBean
   * @return
   */
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
      {
        return aProps[ x ].getReadMethod();
      }

    } // end for()


    return null;

  } // end getMethod()


  /**
   * Sets a Map of bean property aliases to use. The map key specifies the name of the bean
   * property (without the set or get ). The map value is the json tag that will be generated
   * for that property.
   *
   * @param mapPropAliases A Map of bean property aliases
   */
  public void setBeanPropertyAliases( Map mapPropAliases )
  { m_mapPropAliases = mapPropAliases; }




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
   * Builds an attribute name map (by element name) for an json  schema definition
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
    {
      buildAlias( schema, iContent.next() );
    }
    
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
      {
        continue;
      }

      for ( Iterator iAttrs = listAttributes.iterator(); iAttrs.hasNext(); )
      {
        Attribute attr = (Attribute)iAttrs.next();
        String strLocalName = attr.getName();
        if ( mapTempDupes.containsKey( strLocalName ))
        {
          continue;
        }
        
        mapTempDupes.put( strLocalName, null );
        
        listAttrNames.add( strLocalName );

      } // end for

      if ( m_mapObjAlias.containsKey( strElementName ))
      {
        Object objAlias = m_mapObjAlias.get( strElementName );
        
        if ( objAlias instanceof String )
        {
          mapAttrsByElement.put( ((String)objAlias).toLowerCase(), listAttrNames );
        }
        else
        {
          for ( String strAliasName : (List<String>)objAlias)
          {
            mapAttrsByElement.put( strAliasName.toLowerCase(), listAttrNames );
          }
            
        }
      }
      else
      if ( strElementName != null )
      {
        mapAttrsByElement.put( strElementName.toLowerCase(), listAttrNames );
      }
      
    } // end for()
      
  } // end buildMapFromSchema()


  /**
   * Build the object alias map which outputs the element name defined in the json schema as opposed to the name of the
   * java class
   * @param schema The json schema instance
   * @param objContent The schema content object (we look for complexTypes and element schema objects )
   */
  private void buildAlias( Schema schema, Object objContent )
  {
    if ( objContent instanceof ComplexType )
    {
      ModelGroup model = ((ComplexType)objContent).getModelGroup();
      String strTypeName = ((ComplexType)objContent).getName();
      
      if ( strTypeName != null )
      {
        strTypeName = stripNamespace( strTypeName );
      }
      
      
      if ( model != null )
      {
        for ( Iterator iElements = model.getContent().iterator(); iElements.hasNext(); )
        {
          Object objElement = iElements.next();
          
          if ( objElement instanceof Element )
          {
            String strType = ((Element)objElement).getType();
            if ( strType == null )
            {
              strType = ((Element)objElement).getRef();
            }
            
            if ( strType == null )
            {
              continue;
            }
            
            strType = stripNamespace( strType );

            String strName = ((Element)objElement).getName();
            if ( strName == null )
            {
              strName = ((Element)objElement).getRef();
            }

            m_mapElementTypes.put( strName, strType );
            
            if ( strName != null )
            {
              doObjAlias( strType, strName );
            }
             
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
          {
            doObjAlias( strTypeName, ((Element)objContent).getName() );
          }
          
        }
      }
    }

     
  }

  /**
   * Recursive method that builds an json string from introspecting the given object.
   * Properties that return non primitive type recursively call this method so that all\
   * objects tied to the parent bean get converted into the json document.
   *
   * @param objBean The bean object whose property values will be used to build the vector
   * @param aProps Array of property descriptors for the objBean
   * @param fSkipClassName If true do not generate a tag for the class name, only the classes properties
   *
   */
  private void beanToXml( Object objBean, PropertyDescriptor[] aProps, boolean fSkipClassName )
    throws Exception
  {

    String strClassName = getClassName( objBean );
    m_mapChildren.put( strClassName.toLowerCase(), new Boolean( false ) );
    
    Class clsBean = objBean.getClass();

    if ( !fSkipClassName )
    {
      String strObjAlias = getObjAlias( strClassName );

      if ( strObjAlias != null )
      {
        strClassName = strObjAlias;
      }
      else
      if ( m_fLowerCaseFirstCarClassNames )
      {
        strClassName = strClassName.substring( 0,1  ).toLowerCase() + strClassName.substring( 1 );
      }
      

      QName qnClassName = getQName( objBean );
      if ( qnClassName != null  )
      {
        strClassName = qnClassName.toElementName();
      }
    
      m_jsonWriter.beginObject( strClassName );

    } // end if

    String strPropName = null;

    boolean fIgnorePropName = false;

    if ( s_mapClassProps.containsKey( clsBean ) )
    {
      fIgnorePropName = true;
      aProps = s_mapClassProps.get( clsBean );
    }
        

    // Dump this object's properties
    for ( int x = 0; x < aProps.length; x++ )
    {

      if ( aProps[ x ] == null )
      {
        continue;
      }

      strPropName = aProps[ x ].getName();

      if ( m_mapIgnoreProps != null && m_mapIgnoreProps.containsKey( strPropName  ) )
      {
        continue;
      }


      String strTemp = (String)m_mapPropAliases.get( strPropName );

      if ( strTemp != null )
      {
        strPropName = strTemp;
      }

      // Ignore the auto generated getClass property
      if ( strPropName.equalsIgnoreCase( "class" ) )
      {
        continue;
      }

      m_strCurPropName = strPropName;
      
      // Get read method for property
      Method m = aProps[ x ].getReadMethod();

      // if no get method specified, skip
      if ( m == null )
      {
        continue;
      }

      Object objVal = m.invoke( objBean, null );

      // Determine object type
      if ( objVal == null )
      {
        if ( m_strDefaultForNulls != null && !fIgnorePropName )
        {
          m_jsonWriter.addProperty( strPropName, m_strDefaultForNulls, true );
        }

        //no default wase specified, then dont add property
        continue;

      } // end if ( objVal == null )
      
      m_mapChildren.put( strClassName.toLowerCase(), new Boolean( true ) );

      if ( objVal.getClass().isEnum() )
      {
        objVal = objVal.toString();

      }

      if ( objVal instanceof String )
      {
        objVal =( (String)objVal).trim();

        if ( ((String)objVal).length() == 0 )
        {

          if ( m_strDefaultForNulls != null && !fIgnorePropName )
          {
            m_jsonWriter.addProperty( strPropName, m_strDefaultForNulls, true );
          }

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
      {
        fIsCollection = VwBeanUtils.isCollectionType( objVal.getClass() );
      }
      
      if ( fIsPrimArray )
      {
        fIgnorePropName = false;
      }
      
      if ( fIgnorePropName )
      {
        strPropName = null;
      }
      
      // See what kind of return object we have
      if ( VwBeanUtils.isSimpleType( objVal.getClass() ) )
      {
        handleSimpleType( objVal, strPropName );
      }
      else
      if ( fIsPrimArray )
      {
        handlePrimArray( objVal, strPropName );
      }
      else
      if ( fIsCollection )   // Collection types dump their list
      {
        handleCollectionType( objBean, objVal, strPropName, strClassName );
      }
      else
      {
        handleUserType( objBean, objVal, strPropName );
      }

    } // end for()

    if ( !fSkipClassName )
    {
      m_jsonWriter.endObject();

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
    {
      return null;
    }
    
    if ( objAlias instanceof String)
    {
      return (String)objAlias;    // Only one found
    }
    else
    {
      for ( String strAlias : (List<String>)objAlias )
      {
        if ( strAlias.equalsIgnoreCase( m_strCurPropName ))
        {
          return strAlias;       // return the one that matches the current property
        }
        
      }
    }
    
    return null;
    
  } // end getObjALias

  /**
   *
   * @param objBean The bean instance
   * @param objUser
   * @param strPropName the property name that represents this object
   */
  private void handleUserType( Object objBean, Object objUser, String strPropName )  throws Exception
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
        {
          fSkipChildClassName = true;
        }
        strPropName = strAlias;

      } // end if

    } // end if


    Class clsChildObj = objUser.getClass();

    // See if property descriptors arfe in cache first
    PropertyDescriptor[] aChildProps = (PropertyDescriptor[])s_mapProps.get( clsChildObj );

    if ( aChildProps == null )
    {
      aChildProps = getProps( clsChildObj );
      // Get property descriptors and put'em in cache
      s_mapProps.put( clsChildObj, aChildProps );
    }

    m_jsonWriter.beginObject( strPropName );

    beanToXml( objUser, aChildProps, true );

    m_jsonWriter.endObject();

  } // end handleUserType()

  /**
   * @param objBean
   * @return
   */
  private boolean isAnonymousCollection( Object objBean )
  {
    String strBean = getObjName( objBean.getClass() );
    
    Object objType = s_mapComplexTypes.get( strBean );

    if ( objType instanceof ComplexType )
    {
      
      ModelGroup mgroup = ((ComplexType)objType).getModelGroup();
      if ( mgroup == null )
      {
        return false;
      }
      
      if ( mgroup.getMaxOccurs() != null )
      {
        return true;
      }
    }
    
    return false;
    
  }
  
  /**
   * Determins if the property is a schema type
   * @param objBean
   * @param strPropName
   * @param objUserType
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
      {
        return false;
      }
      
      ModelGroup mgroup = ((ComplexType)objType).getModelGroup();
      if ( mgroup == null )
      {
        return false;
      }
      
      
      Element eleProp = mgroup.findElement( strPropName );
      
      if ( eleProp == null )
      {
        if ( ((ComplexType)objType).isComplexContent() )
        {
          ComplexContent content = ((ComplexType)objType).getComplexContent();
          String strBase = null;
          
          if ( content.isExtension() )
          {
            strBase = content.getExtension().getBase();
          }
          else
          if ( content.isRestriction() )
          {
            strBase = content.getRestriction().getBase();
          }
          
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
          {
            strTypeName = strTypeName.substring( ++nPos );
          }
          
          // special case test for int and integer
          if ( strUserType.toLowerCase().startsWith( "int" )&& strTypeName.toLowerCase().startsWith( "int" ))
          {
            return true;
          }
          
          return strTypeName.equalsIgnoreCase( strUserType );
        }
      }
    }
    
    return false;
  }


  /**
   * @param cls
   * @return
   */
  private String getObjName( Class cls )
  {
    String strName = cls.getName();
    
    int nPos = strName.lastIndexOf( '.' );
    
    if ( nPos >= 0 )
    {
      strName = strName.substring( ++nPos );
    }
    
    return strName;
  }

  /**
   *
   * @param objBean
   * @param objVal
   * @param strPropName
   * @throws Exception
   */
  private void handleCollectionType( Object objBean, Object objVal, String strPropName,
                                     String strClassName )  throws Exception
  {

    Iterator iObj = null;

    PropertyDescriptor[] aChildProps = null;
    

    if ( objVal instanceof Collection )
    {
      iObj = ((Collection)objVal).iterator();
    }
    else
    if ( objVal instanceof Enumeration )
    {
      iObj = new VwEnumerator( (Enumeration)objVal );
    }
    else
    if ( objVal.getClass().isArray() )
    {
      iObj = new VwArrayIterator( (Object[])objVal );
    }
    else
    if ( objVal instanceof Map )
    {
      iObj = ((Map)objVal).keySet().iterator();
    }
    else
    {
      iObj = (Iterator)objVal;
    }

    if ( objVal instanceof Map )
    {
      doMapCollection( (Map)objVal, iObj, strPropName );
    }
    else
    {

      int nObjCount = 0;

       // Now iterate through the collection
      while ( iObj.hasNext() )
      {
        Object objColectionValue = iObj.next();
        ++nObjCount;
 
        boolean fISchemaType = isSchemaType( objBean, strPropName, objColectionValue );

        if ( objColectionValue instanceof VwDataObject )
        {
          handleDataObject( (VwDataObject)objColectionValue, strClassName );
          continue;

        }

        if ( VwBeanUtils.isSimpleType( objColectionValue.getClass() ) )
        {

          handlePrimitiveCollection( strPropName, (Collection)objVal );
          return;
        }
        else
        if ( nObjCount == 1 )
          m_jsonWriter.beginArray( strPropName );

        Class clsCollectionObj = objColectionValue.getClass();

        String strCollectionClassName = getClassName( clsCollectionObj );


        // See if property descriptors are in cache first
        aChildProps = (PropertyDescriptor[])s_mapProps.get( clsCollectionObj );

        if ( aChildProps == null )
        {
          // Get property descriptors and put'em in cache
          aChildProps = getProps( clsCollectionObj );
          s_mapProps.put( clsCollectionObj, aChildProps );
        }

        m_jsonWriter.beginObject();

        beanToXml( objColectionValue, aChildProps,  true );

        m_jsonWriter.endObject();
        
      } // end while()


      if ( nObjCount == 0 )
      {
        m_jsonWriter.beginArray( strPropName );

      }

      m_jsonWriter.endArray();

      return;
      
    } // end else

    

   }  // end handleCollectionType()

  private void handlePrimitiveCollection( String strPropName, Collection collection )
  {

    int nLen = collection.size();
    Object[] aValues = new Object[ nLen ];
    String strTag = null;

    int x = -1;

    for( Object objVal : collection )
    {
      aValues[ ++x ] = objVal;

      if ( strTag == null )
      {
        Class clsType = objVal.getClass();
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

      }

    } // end for()


    m_jsonWriter.addArray( strPropName, aValues, strTag.equals( "string" ) );

  }

  /**
   * Handle a primitive array
   * @param objVal
   * @param strPropName
    */
  private void handlePrimArray( Object objVal, String strPropName )
  {

    m_jsonWriter.addArray( strPropName, objVal,  objVal.getClass().equals( String[].class ) );

  } // end handlePrimArray()


  /**
   * Handle a primitive (simple type) property
   * @param objVal  The property value
   * @param strPropName  the property name
   */
  private void handleSimpleType( Object objVal, String strPropName )
  {
    String   strText = null;

    if ( VwBeanUtils.isDateType( objVal ) )
    {
      SimpleDateFormat format = new SimpleDateFormat( m_strDateFormat );

      if ( m_strDateFormat.endsWith( "Z'" ))
      {
        format = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'" );
        TimeZone tz = TimeZone.getTimeZone( "UTC" );
        format.setTimeZone( tz );

      }

      strText = format.format( objVal );

    }
    else
    {
     strText = objVal.toString();      // Simple types dump property name and value
    }


    if ( objVal instanceof Boolean )
    {
      if ( strText.equals( "true" ) )
      {
        strText = m_strTrue;
      }
      else
      {
        strText = m_strFalse;
      }
    }

    m_jsonWriter.addProperty( strPropName, strText, (objVal instanceof String || VwBeanUtils.isDateType( objVal ) ) );

  } // end handleSimpleType()


  /**
   * Insert generated json from dataobject into the cuurrent json stream
   *
   * @param dobj The VwDataOBject instance to generate json from
   */
  private void handleDataObject( VwDataObject dobj, String strClassName ) throws Exception
  {
    int nLevel = m_jsonWriter.getLevel();

    throw new Exception( "Dataobject type not implemented");

    //String strXML = dobj.toXml( null, null, m_jsonWriter.isFormatted(), nLevel, strClassName );

    //m_jsonWriter.addXml( strXML.trim() );

  } // end handleDataObject()

  /**
   * Geneates json for a map
   *
   * @param mapCollection The Map collection
   * @param iKeyIterator The key itereator
   */
  private void doMapCollection( Map mapCollection, Iterator iKeyIterator, String strPropName )
    throws Exception
  {

    // Get the object for each key

    List<Object>listSimpleTypes = new ArrayList<Object>(  );

    Boolean fIsStringType = null;

    while ( iKeyIterator.hasNext() )
    {
      Object objKey = iKeyIterator.next();

      Object objData = mapCollection.get( objKey );
      if ( VwBeanUtils.isSimpleType( objData.getClass() ) )
      {
        if ( fIsStringType == null  )
        {
          fIsStringType =  objData.getClass() == String.class;
        }

        listSimpleTypes.add( objData );
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

        beanToXml( objData, aChildProps,  false );

      }

    } // end while()

    if ( listSimpleTypes.size() > 0 )
    {
      Object[] aValues = listSimpleTypes.toArray();

      m_jsonWriter.addArray( strPropName, aValues, fIsStringType );
    }

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
    {
      strClassName = strClassName.substring( nPos + 1 );
    }

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
    {
      strClassName = strClassName.substring( nPos + 1 );
    }

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
    {
      return true;
    }
    else
    if ( cls == boolean[].class )
    {
      return true;
    }
    else
    if ( cls == byte[].class )
    {
      return true;
    }
    else
    if ( cls == char[].class )
    {
      return true;
    }
    else
    if ( cls == short[].class )
    {
      return true;
    }
    else
    if ( cls == int[].class )
    {
      return true;
    }
    else
    if ( cls == long[].class )
    {
      return true;
    }
    else
    if ( cls == float[].class )
    {
      return true;
    }
    else
    if ( cls == double[].class )
    {
      return true;
    }

    return false;

  } // end isPrimArray



  /**
   * Adds a schema/dtd associated to a Java package
   *
   * @param urlSchema The Schema's URL
   * @param pkgSchema The Package class returned the getPackage method on any Class object
   *
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
      for ( Iterator iUrlSchemas = ( (List) objUrls ).iterator(); iUrlSchemas.hasNext(); )
      {
        URL urlInList = (URL) iUrlSchemas.next();

        if ( urlInList.equals( urlSchema ) )
        {
          fInList = true;
          break;  // ALready in list
        }

      }

      // Add it to list
      if ( !fInList )
      {
        ((List)objUrls).add( urlSchema );
      }
      
    }
    
    m_listSchemasToProcess.add( urlSchema );
    
    return;


  } // end processSchema()

  /**
   * Process an json schema
   *
   * @param schema The json Schema instance
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
        {
          strName = element.getRef();
        }
        
        if ( strType != null )
        {
	        int ndx = strType.indexOf( ':' );
	        if ( ndx > 0 )
          {
            strType = strType.substring( ++ndx );
          }
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
        {
          processComplexType( schema, strName, element.getComplexType() );
        }
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

    // Look at the schema components and update the object alias map so that json tags
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
    {
      s_mapComplexTypes.put( strName, type );
    }
    
    ModelGroup group = type.getModelGroup();

    if ( group == null )
    {
      return;
    }

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
      {
        processElementGroup( (ModelGroup)objComp, schema );
      }
      else
      if ( objComp instanceof Element )
      {

        Element element = (Element)objComp;

        String strEleName = element.getName();
        String strEleType = element.getType();

        if ( strEleName == null )
        {
          strEleName = element.getRef();
        }

        if ( strEleType != null )
        {
          // See if this element is an anonymous complexType
          if ( element.isComplexType() )
          {
            processComplexType( schema, strEleName, element.getComplexType() );
          }
          
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
     {
       strClassName = strAlias;
     }
     
     
     Object objComp = schema.getComponent( strClassName );
     
     if ( objComp == null )
     {
       objComp = schema.getComplexObject( strClassName );
     }
       
     if ( objComp == null )
     {
       return null;
     }
     
     String strType = null;
       
     if ( objComp instanceof Element )
     {
       strType = ((Element)objComp).getType();
       
       if (strType != null )
       {
         strType = stripNamespace( strType );
         
         Object objType = schema.getComplexObject( strType );
         
         if ( objType  != null )
         {
           objComp = objType;
         }
       }
     }
       
     int nGot = 0;

     if ( m_fUseAttributeModel)
     {
       nGot = processSchemaAttributes( schema, objComp, aProps, aOrderedProps, nGot );
       if ( objComp instanceof ComplexType )
       {
         if ( ((ComplexType)objComp).hasChildElements() )
         {
           nGot = processGroup( schema, objComp, aProps, aOrderedProps, nGot );
         }
       }
     }
     else
     {
       nGot = processGroup( schema, objComp, aProps, aOrderedProps, 0 );
     }

     if ( nGot == 0 )
     {
       return null;
     }
     
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
      {
        objComp = schema.getComplexObject( strClassName );
      }
      
      if ( objComp != null )
      {
        return schema;
      }
      

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
    {
      return strType;
    }
    
    return strType.substring( ++nPos );
    
  } // end stripNamespace()

  
  private void processSchemas() throws Exception
  {
    if ( m_listSchemasToProcess.size() == 0 )
    {
      return;
    }
    
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
          {
            throw new Exception( "The WSDL document : " + strName + " does not conatins a types section with an json Scheam defined" );
          }
          
          schema = types.getSchema();
          
          if ( schema == null )
          {
            throw new Exception( "The WSDL document : " + strName + " does not conatins a types section with an json Scheam defined" );
          }
          
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
    {
      listAttrs = ((Element)objComp).getAttributes( schema );
    }
    else
    if ( objComp instanceof ComplexType )
    {
      listAttrs = ((ComplexType)objComp).getAttributes();
    }

    Map<String,String>mapDups = new HashMap<String, String>();
    
    int ndx = 0;

    if ( listAttrs != null )
    {
      for ( Iterator iAttrs = listAttrs.iterator(); iAttrs.hasNext(); )
      {
        Attribute attr = (Attribute)iAttrs.next();

        String strName = attr.getName();

        if ( mapDups.containsKey( strName ))
        {
          continue;
        }
        
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
       {
         return 0;
       }
     }
     else
     if ( objComp instanceof ComplexType )
     {
       if ( ((ComplexType)objComp).isComplexContent() )
       {
         ComplexContent content = ((ComplexType)objComp).getComplexContent();
         String strBase = null;
         
         if ( content.isExtension() )
         {
           strBase = content.getExtension().getBase();
         }
         
         else
         if ( content.isRestriction() )
         {
           strBase = content.getRestriction().getBase();
         }
         
         ComplexType tbase = schema.getComplexType( strBase );
         group = tbase.getModelGroup();
         if ( group != null )
         {
           ndx = processModelGroup( schema, group, aOrderedProps, aProps, ndx );
         }
         
         
       }
       
       group = ((ComplexType)objComp).getModelGroup();
     }
     else
     if ( objComp instanceof ModelGroup )
     {
       group = (ModelGroup)objComp;
     }

     if ( group == null )
     {
       return 0;
     }

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
        {
          strName = ((Element)objElement).getRef();
        }

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
      {
        ndx = processGroup( schema, objElement, aProps, aOrderedProps, ndx );
      }


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
     {
       strClassName = strClassName.substring( nPos + 1 );
     }

     String strAlias = getObjAlias( strClassName );
     if ( strAlias != null )
     {
       strClassName = strAlias;
     }

     // Get elements Map defined in this DTD
     Map mapElements = parser.getElements();

     VwDtdElementDecl eleDecl = (VwDtdElementDecl)mapElements.get( strClassName.toLowerCase() );

     if ( eleDecl == null )
       return null;

     if ( eleDecl.getContentType() == VwDtdElementDecl.ANY )
     {
       return aProps;
     }

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
       {
         orderByProps( listAttrs, aProps, aOrderedProps );
       }


     }
     else
     {
       ModelGroup group = eleDecl.getGroup();
       handleDtdGroup( group, aProps, aOrderedProps );

     }

     if ( aOrderedProps[ 0 ] == null )
     {
       return null;
     }
     
     return aOrderedProps;


   } // end orderByDtd()


  /**
   * Reorder jaava properties as defined by an json schema/dtd
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
         {
           strName = ((Element)objElement).getRef();
         }

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
       {
         ndx = handleDtdGroup( (ModelGroup)objElement, aProps, aOrderedProps );
       }


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
    {
      return strName.substring( ++nPos );
    }
    
    return strName;
    
  } // end stripPackage
  
} // end class VwBeanToXml{}


// *** End of VwBeanToXml
