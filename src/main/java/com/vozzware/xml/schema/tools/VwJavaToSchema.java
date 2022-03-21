/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwJavaToSchema.java

Create Date: Apr 11, 2006
============================================================================================
*/
package com.vozzware.xml.schema.tools;

import com.vozzware.util.VwBeanUtils;
import com.vozzware.util.VwDate;
import com.vozzware.util.VwFileUtil;
import com.vozzware.xml.namespace.Namespace;

import javax.xml.schema.All;
import javax.xml.schema.ComplexContent;
import javax.xml.schema.ComplexType;
import javax.xml.schema.Element;
import javax.xml.schema.Extension;
import javax.xml.schema.Schema;
import javax.xml.schema.util.SchemaFactory;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * This utility class converts Java classes to XML Schema representation.
 * <br>The utility will follow all Java referenced through return types
 * <br>unless a Collection object is returned. The schema will element will contain question mark characters '?'
 * <br>for the data type. This will need to be changed by the developer. If an actual object instance is passed that has
 * <br>objects in the collection, Then the utility can determine the type by introspecting the first object in the collection.
 *
 */
public class VwJavaToSchema
{
  private static Map<String,String>     s_mapPrimTypes;           // Schema primitive typs to Java maps map
  private Map<Class<?>, String>         m_mapGennedClasses = new HashMap<Class<?>, String>();
  
  private List<VwClassDef>             m_listClasses = new ArrayList<VwClassDef>();

  private String                        m_strTargetNameSpace;
  private String                        m_strTNSPrefix;
  
  private boolean                       m_fAttrNormalForm;
  
  static
  {
    s_mapPrimTypes = new HashMap<String,String>();
    buildPrimTypesMap();
  }

  class VwClassDef
  {
    VwClassDef( Object obj, boolean fGenObjectAsType )
    {
      m_obj = obj;
      m_fGenObjectAsType = fGenObjectAsType;
      m_cls = m_obj.getClass();
    }

    VwClassDef( Class clsObj, boolean fGenObjectAsType )
    {
      m_obj = null;
      m_fGenObjectAsType = fGenObjectAsType;
      m_cls = clsObj;
    }

    Class     m_cls;    // Class type of M-ob
    Object    m_obj;    // Object instance to be introspected
    boolean   m_fGenObjectAsType;
  }

  /**
   * Constructor
   * 
   * @param strTargetNameSpace The target namespace for this schema document
   * @param strTNSPrefix The target namespace prefix defualts to tns if null
   * 
   */
  public VwJavaToSchema( String strTargetNameSpace, String strTNSPrefix, boolean fAttrNormalForm )
  {
    m_mapGennedClasses.put( Iterator.class, null );
    m_mapGennedClasses.put( Enumeration.class, null );
    m_mapGennedClasses.put( VwDate.class, null );
    m_fAttrNormalForm = fAttrNormalForm;
    
    m_strTargetNameSpace = strTargetNameSpace;
    
    if ( strTNSPrefix == null )
      m_strTNSPrefix = "tns";
    else  
     m_strTNSPrefix = strTNSPrefix;
    
  } // end VwJavaToSchema


  /**
   * Add an object class  to the schema definition
   *
   * @param obj Object instance which will be introspected for is properties
   * and attributes (object must extend VwXmlBeanAdapter for attribute support).
   *
   * @param fGenObjectAsType If true generate a schema complexType. If false,
   * generate class as an inline anonymous type.
   */
  public void addClass( Object obj, boolean fGenObjectAsType )
  { m_listClasses.add( new VwClassDef( obj, fGenObjectAsType ) ); }

  /**
   * 
   * @param clsJava The Java Class to build an XML schema document from
   * @param fGenObjectAsType If true generate a schema complexType. If false,
   * generate class as an inline anonymous type.
   */
  public void addClass( Class<?> clsJava, boolean fGenObjectAsType )
  { m_listClasses.add( new VwClassDef( clsJava, fGenObjectAsType ) ); }

  /**
   * Process the named schema acordcing to the options
   */
  public Schema process() throws Exception
  {

    Schema schema = SchemaFactory.getInstance().newSchema();
    schema.setTargetNamespace( m_strTargetNameSpace );
    schema.addNamespace( new Namespace( "xsd", "http://www.w3.org/2001/XMLSchema" ));
    schema.addNamespace( new Namespace( m_strTNSPrefix, m_strTargetNameSpace ));
        
    if ( m_listClasses.size() == 0 )
      throw new Exception( "No java classes were specified to create schema for");
    

    for(  VwClassDef classDef : m_listClasses )
    {
      introspect( classDef, schema );

    } // end while()


    return schema;


  } // end process()


  private void introspect( VwClassDef classDef, Schema schema ) throws Exception
  {
    Class clsObj = null;
    
    if ( classDef.m_obj != null )
      clsObj = classDef.m_obj.getClass();
    else
      clsObj = classDef.m_cls;
    
    if ( m_mapGennedClasses.containsKey( clsObj ))
      return;
    
    if ( VwBeanUtils.isSimpleType( clsObj ))
      return;
    
    m_mapGennedClasses.put( clsObj, null );
    
    PropertyDescriptor[] aProps = Introspector.getBeanInfo( classDef.m_cls ).getPropertyDescriptors();

    if ( classDef.m_fGenObjectAsType )
      buildComplexType( classDef, aProps, schema );

  } // end introspect()


  /**
   * Builds a schema complexType for this class object
   *
   * @param strName The name of the complex type
   * @param
   */
  private void buildComplexType( VwClassDef classDef, PropertyDescriptor[] aProps,
                                 Schema schema  ) throws Exception
  {
    Class clsObj = classDef.m_cls;

    String strTypeName = getClassName( clsObj.getName() );

    ComplexType cplxType = schema.createComplexType();
    cplxType.setName( strTypeName );

    schema.addComplexType( cplxType );
    All allProps = schema.createAll();

    cplxType.setAll( allProps );

    for ( int x = 0; x < aProps.length; x++ )
    {
      String strName = aProps[ x ].getName();
      if ( strName.equals( "class" ) )
        continue;

      Element element = schema.createElement();
      allProps.addElement( element );

      element.setName( strName );

      Method m = aProps[ x ].getReadMethod();

      if ( m == null )
        continue;
      
      Object objRet = null;

      if ( objRet == Class.class )
        continue;

      if ( classDef.m_obj != null )
        objRet = m.invoke( classDef.m_obj, null );

      Class clsRet = m.getReturnType();

      if ( clsRet == Class.class )
        continue;

      if ( VwBeanUtils.isSimpleType( clsRet ) )
      {
        String strType = (String)s_mapPrimTypes.get( getClassName( clsRet.getName() ) );

        if ( strType == null )
          strType = "xsd:string";

        element.setType( strType );

      }
      else
      if ( VwBeanUtils.isCollectionType( clsRet ) )
      {
        if ( clsRet.isArray() )
        {
          objRet = null;
          strName = clsRet.getName();
          strName = strName.substring( 2 );
          strName = strName.substring( 0, strName.length() - 1 );
          
          clsRet = Class.forName( strName );
           
        }
        else
        if ( classDef.m_obj != null )
        {
         
          clsRet = null;
          objRet = VwBeanUtils.getValue( classDef.m_obj, aProps[ x ].getName() );
          
          if ( objRet instanceof Collection )
          {
            
            Iterator iObj = ((Collection)objRet).iterator();
            if ( iObj.hasNext() )
              objRet = iObj.next();
          }
          else
            objRet = null;
          
        }
        
        VwClassDef newDef = null;

        if ( objRet != null || clsRet != null )
        {
          Class clsType = null;
          
          if ( objRet != null )
          {
            newDef = new VwClassDef( objRet, true );
            clsType = objRet.getClass();
            
          }
          else
          {
            newDef = new VwClassDef( clsRet, true );
            clsType = clsRet;
            
          }
          
          String strType = null;
          
          if ( VwBeanUtils.isSimpleType( clsType ))
            strType = (String)s_mapPrimTypes.get( getClassName( clsType.getName()));
          else
            strType = m_strTNSPrefix + ":" +  getClassName( clsType.getName() );
          
          element.setType( strType );
              
          introspect( newDef, schema );
        }
        else
          element.setType( m_strTNSPrefix + ":???????" );
        element.setMaxOccurs( "unbounded");
        
      }
      else
      {
        element.setType( m_strTNSPrefix + ":" + getClassName( clsRet.getName() ) );

        VwClassDef newDef = null;

        if ( objRet != null )
          newDef = new VwClassDef( objRet, true );
        else
          newDef = new VwClassDef( clsRet, true );

        introspect( newDef, schema );

      }

    } // end for


  } // end buildComplexType()


  /**
   * Build the primitive type conversion map
   */
  private static void buildPrimTypesMap()
  {
    s_mapPrimTypes.put( "String", "xsd:string" );
    s_mapPrimTypes.put( "boolean", "xsd:boolean");
    s_mapPrimTypes.put( "Boolean", "xsd:boolean");
    s_mapPrimTypes.put( "byte", "xsd:byte" );
    s_mapPrimTypes.put( "Byte", "xsd:byte" );
    s_mapPrimTypes.put( "short", "xsd:short" );
    s_mapPrimTypes.put( "Short", "xsd:short" );
    s_mapPrimTypes.put( "int", "xsd:integer" );
    s_mapPrimTypes.put( "Integer", "xsd:integer" );
    s_mapPrimTypes.put( "long", "xsd:long" );
    s_mapPrimTypes.put( "Long", "xsd:long" );
    s_mapPrimTypes.put( "float", "xsd:float" );
    s_mapPrimTypes.put( "Float", "xsd:float" );
    s_mapPrimTypes.put( "double", "xsd:double" );
    s_mapPrimTypes.put( "Double", "xsd:double" );
    s_mapPrimTypes.put( "double", "xsd:decimal" );
    s_mapPrimTypes.put( "VwDate", "xsd:date" );
    s_mapPrimTypes.put( "Object", "xsd:object" );

  } // end buildPrimTypesMap()


  /**
   * Return the Schema type for the Java type
   */
  public static String getSchemaType( String strJavaType )
  {  return (String)s_mapPrimTypes.get( strJavaType ); }



  /**
   * Extract the class name from a fully qualified package and class
   *
   * @param strClass The class name
   *
   * @return Just the class name
   *
   */
  private static String getClassName( String strClass )
  {
    int nPos = strClass.lastIndexOf( '.' );

    if ( nPos > 0 )
      return strClass.substring( nPos + 1 );

    return strClass;  // No package was specified

  } // end getClassName()


  /**
   * Returns the name of the extension if this schema type is an extension
   */
  private String getBaseClassName( ComplexType type )
  {

    if ( ! (type.getContent() instanceof ComplexContent) )
      return null;

    ComplexContent content = (ComplexContent)type.getContent();

    if ( content.getContent() instanceof Extension )
      return ((Extension)content.getContent()).getBase();

    return null;

  } // end getBaseClassName();


  /**
   * Program entry point ( for command line use )
   */
  public static void main( String[] astrArgs )
  {

    String strTargetNameSpace = null;
    String strTNSPrefix = "tns";
    String strJavaClassName = null;
    String strJavaPackageName = null;
    String strSchemaFileName = null;
    boolean fAttrNormalForm = false;
    
    for ( int x = 0; x < astrArgs.length; x++ )
    {
      if ( astrArgs[ x ].equalsIgnoreCase( "-t" ) )
      {
        strTargetNameSpace = astrArgs[ ++x ];
      }
      else
      if ( astrArgs[ x ].equalsIgnoreCase( "-tp" ) )
      {
        strTNSPrefix = astrArgs[ ++x ];
      }
      else
      if ( astrArgs[ x ].equalsIgnoreCase( "-c" ) )
      {
        strJavaClassName = astrArgs[ ++x ];
      }
      else
      if ( astrArgs[ x ].equalsIgnoreCase( "-f" ) )
      {
        strSchemaFileName = astrArgs[ ++x ];
      }
      else
      {
        System.out.println( "\nCommand Arg: " +  astrArgs[ x ] + " is invalid");
        return;
      }
    } // end for()

    if ( strTargetNameSpace == null || strJavaClassName == null || strSchemaFileName == null )
    {
      showFormat();
      System.exit( -1 );

    }

    try
    {
      VwJavaToSchema stj = new VwJavaToSchema( strTargetNameSpace, strTNSPrefix, fAttrNormalForm );
      
      Class<?> clsJava = Class.forName( strJavaClassName );
      
      stj.addClass( clsJava, true );
      
      Schema schema = stj.process();

      String strSchemaContent = schema.toString();
      
      VwFileUtil.writeFile( strSchemaFileName, strSchemaContent );
      return;

    }
    catch( Exception e )
    {
      e.printStackTrace();
    }
  } // end main()



  /**
   * Display the command line fromat
   */
  private static void showFormat()
  {
    System.out.println( "VwJavaToSchema -c Java class -t Target Namespace\n [-p Target Namespace prefix defaults to tns]\n" +
                        "-f File path to place schema");

  } // end showFormat()


} // end class VwJavaToSchema{}

// *** End of VwJavaToSchema.java ***
