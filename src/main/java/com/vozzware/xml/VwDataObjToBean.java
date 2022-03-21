/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwDataObjToBean.java

Create Date: Apr 11, 2006
============================================================================================
*/

package com.vozzware.xml;

import org.xml.sax.Attributes;

import java.beans.Introspector;
import java.beans.MethodDescriptor;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

/**
 * This class converts an VwDataObject container to a java bean whose property names
 * match the data object keys.
 */
public class VwDataObjToBean
{

  private static final int SIMPLE = 1;
  private static final int OBJECT = 2;
  private static final int COLLECTION = 3;

  private class MethodInfo
  {
    Method      m_methodSetter;               // The method to invoke for this tag

    Class       m_clsParamType;               // The parameter class type that the method takes
    String      m_strParamClassName;          // Class name if OBJECT type
    Class       m_clsCollectionType;          // If param is a collection, the class type the collection holds

    Method      m_methodCollection;           // The add, or put method for the collection type

    boolean     m_fIsPut;                     // If true, if collection type is map/hashtable based

    int         m_nType;                      // Type Java mapping for tag


    MethodInfo( Method methodSetter, Class clsParamType, int nType )
    {
      m_methodSetter = methodSetter;
      m_clsParamType = clsParamType;
      m_nType = nType;

      if ( m_nType == OBJECT || m_nType == COLLECTION )
        m_strParamClassName = getObjName( clsParamType );

    }

  } // end class MethodInfo{}

  private boolean   m_fIgnoreNullData = false;   // Don't generate entries

  private boolean   m_fGenNullAttrForNulls = false; // Gen xsi:null atrribute for null date

  private boolean   m_fPreserveCase = false;

  private static Map  m_mapObjects = Collections.synchronizedMap( new HashMap() );       // A map of object method maps
  private HashMap     m_mapCurObjMethods = null;            // A map of tags to methods for current object
  private HashMap     m_mapMethodAlias = new HashMap();     // A map of xml tags and their corresponding method names

  private String      m_strTopLevelClassName;               // Class name for the top level class
  private Class       m_clsTopLevelClass;                   // Class object of the top level class

  private String      m_strCurTagName;                      // The current xml tag being parsed

  private String      m_strCurObjName = "";                 // Name of the current object

  private Object      m_curObj = null;                      // Current object instance
  private Object      m_objCollection = null;               // Collection object if property takes a collection

  private Stack       m_objStack;                           // Stack of current object instances
  private Stack       m_stackMethods;                       // Stack of method maps
  private Stack       m_stackCollections;                   // Stack of collection methods
  private Stack       m_stackObjCollections;                // Stack of collection objects


  /**
   * Constructor - All bean introspection is done in the constructor so that this object can be
   * kept in memory to parse like xml documents repeatedly with out having to re-inspect
   * the bean.
   *
   * @param clsTopLevelBean The Top level bean for this document type
   * @param fValidate if true use a validating parser ( expects a DTD ) else don't validate.<BR>
   * NOTE! If this param is false, the xml document is still checked to insure that
   * it is well formed.
   *
   * @exception Exception if any introspection errors occur
   */
  public VwDataObjToBean( Class clsTopLevelBean ) throws Exception
  {
    introspect( clsTopLevelBean );

    // Save class name and Class object for the top level bean
    m_strTopLevelClassName  = getObjName( clsTopLevelBean );

    m_clsTopLevelClass = clsTopLevelBean;

    // Get the map of methods for the top level class

    m_mapCurObjMethods = (HashMap)m_mapObjects.get( m_strTopLevelClassName );

    /*
    m_objStack = new Stack();

    m_stackMethods = new Stack();
    m_stackCollections = new Stack();
    m_stackObjCollections = new Stack();

    */
  } // end VwXmlToBean()


  /**
   * Generate a bean from the contents of an VwDataObject container.
   *
   * @param dataObj The VwDataObject to convert to the bean specified in the constructor
   *
   */
  public Object toBean( VwDataObject dataObj ) throws Exception
  {

    Object objBean = m_clsTopLevelClass.newInstance();

    processDataObject( m_strTopLevelClassName, dataObj, objBean );

    return objBean;

  } // end toXml


  /**
   * Generate a bean from the contents of an VwDataObject container.
   *
   * @param dataObj The VwDataObject to convert to the bean specified in the constructor
   *
   */
  public Object toBean( VwDataObject dataObj, Object objBean ) throws Exception
  {

    processDataObject( m_strTopLevelClassName, dataObj, objBean );

    return objBean;

  } // end toXml
  
  /**
   * Process the contents of the data object
   */
  private void processDataObject( String strDataObjName, VwDataObject dataObj,
                                  Object objBean ) throws Exception
  {
    m_curObj = objBean;

    // Iterate the map keys to build the xml document

    if ( strDataObjName != null )
    {

      Attributes listAttr = null;

      // See if there is an attribute element for this map name

      if ( dataObj.exists( strDataObjName ) )
      {
        VwElement element = (VwElement)dataObj.getObject( strDataObjName );

        String strData = element.getValue();
        listAttr = element.getAttributes();

        if ( strData != null )
        {

        }

      } // end if ( dataObj.exists( strDataObjName )


    } // end if (strDataObjName != null )


    Iterator iKeys = dataObj.keys();

    while ( iKeys.hasNext() )
    {

      String strKey = (String)iKeys.next();

      Object objData = dataObj.getObject( strKey );

      if ( objData == null )
        continue;

      if ( objData instanceof VwElement )
        processElement( (VwElement)objData, objBean );

    } // end while()


  } // end processMap()


  /**
   * Process a single VwElement
   *
   * @param strDataObjName Name of data object tag this element is contained in
   * @param element The element to process
   *
   */
  private void processElement( VwElement element, Object objBean ) throws Exception
  {

    MethodInfo mi = (MethodInfo)m_mapCurObjMethods.get( element.getName().toLowerCase() );

    if ( mi != null )
    {
      String strData = element.getValue();
      if ( strData == null )
        return;

      invokeSetterMethod( mi, strData );

    }

  } // end processElement()


  /**
   * A recursive method to introspect a class and any other class it references. This method
   * builds a map of Method objects for each class encountered. It looks for class methods or
   * properties that start with set or add, all other methods are ignored
   *
   * @param classToIntrospect The class to introspect
   */
  private void introspect( Class classToIntrospect ) throws Exception
  {

    String strClassName = classToIntrospect.getName();

    String strClassFullName = strClassName;   // Preserve with package name for alias lookup

    strClassName = getObjName( classToIntrospect );

    HashMap mapClassMethods = (HashMap)m_mapObjects.get( strClassName );

    if ( mapClassMethods != null )
      return;             // Already introspected

    mapClassMethods = new HashMap();

    // Each class will store a map of its' methods
    m_mapObjects.put( strClassName, mapClassMethods );

    MethodDescriptor[] aMethods =
      Introspector.getBeanInfo( classToIntrospect ).getMethodDescriptors();

    for ( int x = 0; x < aMethods.length; x++ )
    {
      String strMethodName = aMethods[ x ].getName();

      if ( strMethodName.startsWith( "add" ) || strMethodName.startsWith( "set" ) )
      {

        // Remove the the prefix string "add" or "set" so it maps to the xml tag
        if ( strMethodName.length() > 3 )
          strMethodName = strMethodName.substring( 3 );
        else
          continue;

        if ( !m_fPreserveCase )
          strMethodName = strMethodName.toLowerCase();

        if ( mapClassMethods.get( strMethodName ) != null )
          continue;     // This is a collection method previously defined

        Method method =  aMethods[ x ].getMethod();

        Class[] aParamTypes = method.getParameterTypes();

        if ( aParamTypes.length > 1 )  // Only look a methods/accessors with one parameter
          continue;

        // See if ther'es an alias for this method

        String strAlias = (String)m_mapMethodAlias.get( strClassFullName + strMethodName );

        if ( strAlias == null )
          strAlias = strMethodName;

        if ( isSimpleType( aParamTypes[ 0 ] ) )
          mapClassMethods.put( strAlias, new MethodInfo( method, aParamTypes[ 0 ], SIMPLE )  );
        else
        if ( !isCollectionType( aParamTypes[ 0 ] ) )
        {
          mapClassMethods.put( strAlias, new MethodInfo( method, aParamTypes[ 0 ], OBJECT )  );

          String strParamClassName = getObjName( aParamTypes[ 0 ] );

          // Only introspect if we have a new class
          if ( m_mapObjects.get( strParamClassName ) == null )
            introspect( aParamTypes[ 0 ] );
        }

      } // end if

    } // end for()

  } // end introspect


  /**
   * Returns just the name of the object without the package
   *
   * @param obj The object to get the name for
   */
  private String getObjName( Object obj )
  {
    String strName = obj.getClass().getName();

    int nPos = strName.lastIndexOf( '.' );    // This check is for inner classes

    if ( nPos >= 0 )
      strName = strName.substring( nPos + 1 );

    if ( !m_fPreserveCase )
      strName = strName.toLowerCase();

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

    if ( !m_fPreserveCase )
      return strName.toLowerCase();

    return strName;

  } // end getObjName()


  /**
   * Invoke the setter or add method on the current object with the data in strVal
   *
   * @param mi The info data about the class param types for the xml tag
   */
  private void invokeSetterMethod( MethodInfo mi, String strData ) throws Exception
  {
    Object[] aParams = null;

    if ( mi.m_clsParamType == java.lang.String.class  )
      aParams = new String[] { strData };
    else
    if ( mi.m_clsParamType == java.lang.Boolean.class ||
         mi.m_clsParamType == java.lang.Boolean.TYPE )
    {
      boolean fState = false;

      if ( strData.equalsIgnoreCase( "y" ) || strData.equalsIgnoreCase( "yes" ) ||
           strData.equalsIgnoreCase( "true" ) || strData.equals( "1" ) )
        fState = true;

      aParams = new Boolean[] { new Boolean( fState ) };
    }
    else
    if ( mi.m_clsParamType == java.lang.Byte.class || mi.m_clsParamType == java.lang.Byte.TYPE )
      aParams = new Byte[] { new Byte( strData ) };
    else
    if ( mi.m_clsParamType == java.lang.Short.class || mi.m_clsParamType == java.lang.Short.TYPE )
      aParams = new Short[] { new Short( strData ) };
    else
    if ( mi.m_clsParamType == java.lang.Integer.class || mi.m_clsParamType == java.lang.Integer.TYPE )
      aParams =  new Integer[] { new Integer( strData ) };
    else
    if ( mi.m_clsParamType == java.lang.Long.class || mi.m_clsParamType == java.lang.Long.TYPE )
      aParams = new Long[] { new Long( strData ) };
    else
    if ( mi.m_clsParamType == java.lang.Float.class || mi.m_clsParamType == java.lang.Float.TYPE )
      aParams = new Float[] { new Float( strData ) };
    else
    if ( mi.m_clsParamType == java.lang.Double.class || mi.m_clsParamType == java.lang.Double.TYPE )
      aParams = new Double[] { new Double( strData ) };
    else
    if ( mi.m_clsParamType == java.math.BigInteger.class )
      aParams = new java.math.BigInteger[] { new java.math.BigInteger( strData ) };
    else
    if ( mi.m_clsParamType == java.math.BigDecimal.class  )
      aParams = new java.math.BigDecimal[] { new java.math.BigDecimal( strData ) };

    // Invoke the method

    try
    {
      mi.m_methodSetter.invoke( m_curObj, aParams )  ;
    }
    catch( Exception e )
    {

      String strErr = "Error invoking method '" + mi.m_methodSetter.getName()
                    + "' on class " + m_curObj.getClass().getName()
                    + " with param data " + strData
                    + "\n Failure Reason: " + e.toString();

      throw new Exception( strErr );

    }
  } // end invokeSetterMethod()

  /**
   * Deteremins if the class name is a simple type. I.E. String, Byte, Integer, int, short ...
   *
   * @param classType The class type of the method parameter
   *
   * @return true if the class type is a simple type, false otherwise
   */
  static boolean isSimpleType( Class classType )
  {
    if ( classType == java.lang.String.class  ||
         classType == java.lang.Boolean.TYPE  ||
         classType == java.lang.Byte.TYPE   ||
         classType == java.lang.Short.TYPE   ||
         classType == java.lang.Integer.TYPE   ||
         classType == java.lang.Long.TYPE   ||
         classType == java.lang.Float.TYPE   ||
         classType == java.lang.Double.TYPE   ||
         classType == java.math.BigInteger.class   ||
         classType == java.math.BigDecimal.class   ||
         classType == java.lang.Boolean.class  ||
         classType == java.lang.Byte.class   ||
         classType == java.lang.Short.class   ||
         classType == java.lang.Integer.class   ||
         classType == java.lang.Long.class   ||
         classType == java.lang.Float.class   ||
         classType == java.lang.Double.class   )
       return true;

    return false;


  } // end isSimpleType()


  /**
   * Deteremins if the class name is a Collection type. I.E. List, Map, Vector Hashtable
   *
   * @param classType The class type to test
   *
   * @return true if the class type is a Collection type or array, false otherwise
   */
  static boolean isCollectionType( Class classType )
  {
    if ( java.util.Collection.class.isAssignableFrom( classType ) ||
         java.util.Iterator.class.isAssignableFrom( classType ) ||
         java.util.Enumeration.class.isAssignableFrom( classType ) ||
         java.util.Map.class.isAssignableFrom( classType ) ||
         classType.isArray() )
      return true;

    return false;

  } // end isCollectionType()


} // end class  VwDataObjToBean{}

// *** End of  VwDataObjToBean.java ***

