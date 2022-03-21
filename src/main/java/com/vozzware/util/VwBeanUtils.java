/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE vw PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: ItBeanUitls.java

Create Date: Dec 4, 2000
============================================================================================
 */
package com.vozzware.util;

import com.vozzware.db.VwDVOBase;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class to handle reflection and dynamic invocation of properties and methods.
 * 
 * @author P. VosBurgh
 * 
 */
public class VwBeanUtils
{
  private static Map<Class<?>, Map<String, PropertyDescriptor>> s_mapBeanProps = Collections
      .synchronizedMap( new HashMap<Class<?>, Map<String, PropertyDescriptor>>() );

  /**
   * Test to see if a property exists for a bean
   * 
   * @param objBean
   *          The bean instance to test
   * @param strPropName
   *          The name of the property to test for
   * 
   * @return ture if the property exists on the bean, false otherwise
   * @throws Exception
   */
  public static boolean hasProperty( Object objBean, String strPropName ) throws Exception
  {
    Class<?> clsBean = objBean.getClass();
    return hasProperty( clsBean, strPropName );

  } // end hasProperty()


  /**
   * Copies common properties from fromBean to toBean, It is assumed that the data types are the same
   *
   * @param objFromBean The object to copy from
   * @param objToBean The object to copy to
   *
   * @throws Exception If the property value from the frombean cannot be coerced to the set property in the tobean
   */
  public static void copy( Object objFromBean, Object objToBean ) throws Exception
  {
    copy( objFromBean, objToBean, false );
  }

  /**
   * Copies common properties from fromBean to toBean, It is assumed that the data types are the same
   *
   * @param objFromBean The object to copy from
   * @param objToBean The object to copy to
   * @param fIgnoreNoSetter if true, dont throw exception when the toBean does not have a matching setter method
   *
   * @throws Exception If the property value from the frombean cannot be coerced to the set property in the tobean
   */
  public static void copy( Object objFromBean, Object objToBean, boolean fIgnoreNoSetter ) throws Exception
  {
    List<PropertyDescriptor> listFromProps =  getReadProperties( objFromBean.getClass() );

    List<PropertyDescriptor> listWritesProps = getWriteProperties( objToBean.getClass()  );

    for ( PropertyDescriptor reader: listFromProps )
    {

      String strPropName = reader.getName();
      if ( hasProperty( objToBean, strPropName ))
      {
        Object objFromVal = getValue( objFromBean, strPropName );

        if ( objFromVal == null )
        {
          continue;
        }

        setValue( objToBean, strPropName, objFromVal, fIgnoreNoSetter );
      }

    }

  }


  /**
   * Test to see if a property exists for a bean
   * 
   * @param clsBean
   *          The bean class to test
   * @param strPropName
   *          The name of the property to test for
   * 
   * @return true if the property exists on the bean, false otherwise
   * @throws Exception
   */
  public static boolean hasProperty( Class<?> clsBean, String strPropName ) throws Exception
  {
    Map<String, PropertyDescriptor> mapProps = getProps( clsBean );

    PropertyDescriptor pd = mapProps.get( strPropName.toLowerCase() );

    return (pd != null);

  } // end hasProperty()


  /**
   * Gets the value of the bean's property
   * 
   * @param objBean
   * @param strPropName
   * @return
   * @throws Exception
   * @deprecated Use {@link #getValue(Object,String)} instead
   */
  public static Object getBeanProperty( Object objBean, String strPropName ) throws Exception
  {
    return getValue( objBean, strPropName );
  } // end getBeanProperty()

  public static Object getValue( Object objBean, String strPropName ) throws Exception
  {
    return getValue( objBean, null, strPropName );
  }

  /**
   * Gets the value of the bean's property
   * 
   * @param objBean
   * @param strPropName
   * @return
   * @throws Exception
   */
  public static Object getValue( Object objBean, Object objCtx, String strPropName ) throws Exception
  {
    Object objData = null;
    Method mthdGetter = null;

    if (objBean == null)
      return null;

    // test for nested property specifier

    while ( true )
    {
      if (strPropName == null || strPropName.length() == 0)
        break;

      char ch = '.';
      if (strPropName.indexOf( ']' ) > 0)
        ch = ']';

      int nPos = strPropName.indexOf( ch );

      if (ch == ']')
        nPos = strPropName.indexOf( '.', nPos + 1 );

      Class<?> clsBean = objBean.getClass();
      Map<String, PropertyDescriptor> mapProps = getProps( clsBean );
      String strCurProp = null;

      if (nPos > 0)
      {
        strCurProp = strPropName.substring( 0, nPos );
        strPropName = strPropName.substring( nPos + 1 );
      }
      else
        strCurProp = strPropName;

      int nArrayPos = strCurProp.indexOf( '[' );
      if (nArrayPos > 0)
      {
        objBean = getArrayObject( objBean, objCtx, strCurProp, nArrayPos, false );
        if (objBean == null)
          return null;

        objData = objBean;

        if (nPos < 0)
          return objBean;

        continue;
      }

      PropertyDescriptor pd = (PropertyDescriptor)mapProps.get( strCurProp.toLowerCase() );

      if (pd == null)
        throw new Exception( "The bean class '" + clsBean.getName() + "' does not contain the property '" + strPropName
            + "'" );

      mthdGetter = pd.getReadMethod();

      if (mthdGetter == null)
        throw new Exception( "The bean class '" + clsBean.getName() + "' does not contain the getter property '"
            + strPropName + "'" );

      objData = mthdGetter.invoke( objBean, null );

      if (objData == null)
        return null;

      if (nPos < 0)
        break;

      objBean = objData; // This is a nested property specifier
    } // end while

    return objData;

  } // end getBeanProperty()

  private static Object getArrayObject( Object objBean, Object objCtx, String strCurProp, int nStartBracketPos,
      boolean fCreateNullObject ) throws Exception
  {
    // First find the index value
    int nEndBracketPos = strCurProp.indexOf( ']' );

    String strIndexId = null;

    if (nEndBracketPos > 0)
      strIndexId = strCurProp.substring( nStartBracketPos + 1, nEndBracketPos );
    else
      strIndexId = strCurProp.substring( nStartBracketPos + 1 );
    // get index identifier

    int ndx = -1;
    if (VwExString.isIntegral( strIndexId, false ))
      ndx = Integer.parseInt( strIndexId );
    else
    {
      Object objIndexVal = null;

      if (objCtx != null)
      {
        Class<?> clsValueHelper = Class.forName( "com.vozzware.jsp.taglib.VwValueHelper" );
        Method mthdGetValue = clsValueHelper.getMethod( "getValue", new Class[] { Object.class, String.class } );
        objIndexVal = mthdGetValue.invoke( null, new Object[] { objCtx, strIndexId } );
      }

      if (objIndexVal == null)
        throw new Exception( VwResourceMgr.getString( "vw.noResolveIndexIdentiier" ) );

      ndx = Integer.parseInt( objIndexVal.toString() );

    }

    if (ndx < 0)
      throw new Exception( VwResourceMgr.getString( "vw.invalidIndexIdentiier" ) );

    // we have a good index, now gethe the object that requires the index
    // resolution. Must be an array or a List object

    int nNextPropPos = strCurProp.indexOf( '.' );

    if (nNextPropPos > 0)
      strCurProp = strCurProp.substring( 0, nNextPropPos );

    // now strip off the index string

    strCurProp = strCurProp.substring( 0, nStartBracketPos );

    PropertyDescriptor pd = getPropDescriptor( objBean.getClass(), strCurProp );
    if (pd == null)
      throw new Exception( VwResourceMgr.getString( "vw.invalidPropertyName" ) + objBean.getClass() + "." + strCurProp );

    Method mthdRead = pd.getReadMethod();

    if (mthdRead == null)
      throw new Exception( VwResourceMgr.getString( "vw.noReadMethodName" ) + objBean.getClass() + "." + strCurProp );

    Object objResult = mthdRead.invoke( objBean, (Object[])null );

    if (objResult == null)
      return null;

    if (objResult.getClass().isArray())
      return Array.get( objResult, ndx );
    else
      if (objResult instanceof List)
        return ((List)objResult).get( ndx );

    throw new Exception( VwResourceMgr.getString( "vw.invalidIndexObject" + objResult.getClass() ) );
  }

  /**
   * Sets the bean's property
   * 
   * @param objBean
   * @param strPropName
   * @param objData
   * @deprecated Use {@link #setValue(Object,String,Object)} instead
   * @throws Exception
   */
  public static void setBeanProperty( Object objBean, String strPropName, Object objData ) throws Exception
  {
    setValue( objBean, null, strPropName, objData, false );
  }

  public static void setValue( Object objBean, String strPropName, Object objData ) throws Exception
  {
    setValue( objBean, null, strPropName, objData, false );
  }


  public static void setValue( Object objBean, String strPropName, Object objData, boolean fIgnoreNoSetter ) throws Exception
   {
     setValue( objBean, null, strPropName, objData, fIgnoreNoSetter );
   }

  /**
   * Sets the bean's property
   * 
   * @param objBean
   * @param strPropName
   * @param objData
   * @throws Exception
   */
  public static void setValue( Object objBean, Object objCtx, String strPropName, Object objData, boolean fIgnoreNoSetter ) throws Exception
  {

    int nPos = strPropName.lastIndexOf( '.' );

    if (nPos > 0)
    {
      String strBeanPath = strPropName.substring( 0, nPos );
      strPropName = strPropName.substring( ++nPos );
      objBean = getValue( objBean, objCtx, strBeanPath );

      if (objBean == null)
        throw new Exception( "Cannot find bean object from the path '" + strBeanPath + "'" );

    }

    Class<?> clsBean = objBean.getClass();
    Map<String, PropertyDescriptor> mapProps = getProps( clsBean );

    PropertyDescriptor prop = (PropertyDescriptor)mapProps.get( strPropName.toLowerCase() );
    if (prop == null)
      return;

    Method mthdSetter = prop.getWriteMethod();

    if (mthdSetter == null)
    {
      if ( fIgnoreNoSetter )
        return;

      throw new Exception( "The bean class '" + clsBean.getName() + "' does not contain the setter property '"
          + strPropName + "'" );
    }


    Class<?> clsParamType = mthdSetter.getParameterTypes()[0];

    Object[] aParams = null;

    if (objData == null)
    {
      if (clsParamType == Boolean.TYPE)
        aParams = new Boolean[] { new Boolean( false ) };
      else
        if (clsParamType == Byte.TYPE)
          aParams = new Byte[] { new Byte( (byte)0 ) };
        else
          if (clsParamType == Character.TYPE)
            aParams = new Character[] { new Character( (char)0 ) };
          else
            if (clsParamType == Short.TYPE)
              aParams = new Short[] { new Short( (short)0 ) };
            else
              if (clsParamType == Integer.TYPE)
                aParams = new Integer[] { new Integer( 0 ) };
              else
                if (clsParamType == Long.TYPE)
                  aParams = new Long[] { new Long( 0 ) };
                else
                  if (clsParamType == Float.TYPE)
                    aParams = new Float[] { new Float( 0.0 ) };
                  else
                    if (clsParamType == Double.TYPE)
                      aParams = new Double[] { new Double( 0.0 ) };
                    else
                      aParams = new Object[] { null };

    }
    else
      if (clsParamType == String.class)
        aParams = new String[] { objData.toString() };
      else
        if (clsParamType == Boolean.class || clsParamType == Boolean.TYPE)
        {
          boolean fState = false;
          String strData = objData.toString();
          if (strData.equalsIgnoreCase( "true" ) || strData.equalsIgnoreCase( "t" ) || strData.equalsIgnoreCase( "yes" )
              || strData.equalsIgnoreCase( "y" ) || strData.equals( "1" ))
            fState = true;

          aParams = new Boolean[] { new Boolean( fState ) };
        }
        else
          if (clsParamType == Byte.class || clsParamType == Byte.TYPE)
          {
            if (objData instanceof Byte)
              aParams = new Byte[] { (Byte)objData };
            else
              aParams = new Byte[] { new Byte( objData.toString() ) };
          }
          else
            if (clsParamType == Short.class || clsParamType == Short.TYPE)
            {
              if (objData instanceof Short)
                aParams = new Short[] { (Short)objData };
              else
                aParams = new Short[] { new Short( objData.toString() ) };

            }
            else
              if (clsParamType == Integer.class || clsParamType == Integer.TYPE)
              {
                if (objData instanceof Integer)
                  aParams = new Integer[] { (Integer)objData };
                else
                  aParams = new Integer[] { new Integer( objData.toString() ) };

              }
              else
                if (clsParamType == Long.class || clsParamType == Long.TYPE)
                {
                  if (objData instanceof Long)
                    aParams = new Long[] { (Long)objData };
                  else
                    aParams = new Long[] { new Long( objData.toString() ) };

                }
                else
                  if (clsParamType == Float.class || clsParamType == Float.TYPE)
                  {
                    if (objData instanceof Float)
                      aParams = new Float[] { (Float)objData };
                    else
                      aParams = new Float[] { new Float( objData.toString() ) };
                  }
                  else
                    if (clsParamType == Double.class || clsParamType == Double.TYPE)
                    {
                      if (objData instanceof Double)
                        aParams = new Double[] { (Double)objData };
                      else
                        aParams = new Double[] { new Double( objData.toString() ) };

                    }
                    else
                      if (clsParamType == java.math.BigInteger.class)
                      {
                        if (objData instanceof java.math.BigInteger)
                          aParams = new java.math.BigInteger[] { (java.math.BigInteger)objData };
                        else
                          aParams = new java.math.BigInteger[] { new java.math.BigInteger( objData.toString() ) };
                      }
                      else
                        if (clsParamType == java.math.BigDecimal.class)
                        {
                          if (objData instanceof java.math.BigDecimal)
                            aParams = new java.math.BigDecimal[] { (java.math.BigDecimal)objData };
                          else
                            aParams = new java.math.BigDecimal[] { new java.math.BigDecimal( objData.toString() ) };
                        }
                        else
                          if (clsParamType == Date.class)
                          {
                            if (objData instanceof Date)
                              aParams = new Date[] { (Date)objData };
                            else
                              if (objData instanceof Timestamp)
                                aParams = new Date[] { new Date( ((Timestamp)objData).getTime() ) };
                              else
                                if (objData instanceof VwDate)
                                  aParams = new Date[] { ((VwDate)objData).toDate() };

                          }
                          else
                            if (clsParamType == Timestamp.class)
                            {
                              if (objData instanceof Timestamp)
                                aParams = new Timestamp[] { (Timestamp)objData };
                              else
                                if (objData instanceof Date)
                                  aParams = new Timestamp[] { new Timestamp( ((Date)objData).getTime() ) };
                                else
                                  if (objData instanceof VwDate)
                                    aParams = new Timestamp[] { ((VwDate)objData).toTimestamp() };

                            }
                            else
                              if (clsParamType == VwDate.class)
                              {
                                if (objData instanceof String)
                                {
                                  String strFormat = System.getProperty( "VwDateFormat" );
                                  if (strFormat == null)
                                    strFormat = VwDate.USADATE;

                                  aParams = new VwDate[] { new VwDate( (String)objData, strFormat ) };

                                }
                                else
                                  if (objData instanceof VwDate)
                                    aParams = new VwDate[] { (VwDate)objData };
                                  else
                                    if (objData instanceof Timestamp)
                                      aParams = new VwDate[] { new VwDate( (Timestamp)objData ) };
                                    else
                                      if (objData instanceof Date)
                                        aParams = new VwDate[] { new VwDate( (Date)objData ) };

                              }
                              else
                                if (Enum.class.isAssignableFrom( clsParamType ))
                                {
                                  aParams = new Enum[] { Enum.valueOf( (Class<Enum>)clsParamType, (String)objData ) };
                                }
                                else
                                  aParams = new Object[] { objData };

    // Invoke the method

    try
    {
      mthdSetter.invoke( objBean, aParams );
    }
    catch ( Exception e )
    {

      String strErr = "Error invoking method '" + mthdSetter.getName() + "' on class " + clsBean.getName()
          + " with param data " + objData + "\n Failure Reason: " + e.toString();

      throw new Exception( strErr );

    }

  } // end setBeanProperty()

  /**
   * Return a list of getter property names. <br>
   * Any getter that was marked with the NoIntrospect annotation will not be
   * returned
   *
   * @param clsType
   *          The bean class to introspect
   * @return a List of PrtopertyDescriptors that have read methods (getters)
   * @throws Exception
   */
  public static List<PropertyDescriptor> getReadProperties( Class<?> clsType ) throws Exception
  {
    PropertyDescriptor[] aProps = Introspector.getBeanInfo( clsType ).getPropertyDescriptors();
    List<PropertyDescriptor> listProps = new ArrayList<PropertyDescriptor>();

    for ( int x = 0; x < aProps.length; x++ )
    {
      if (aProps[x].getName().equalsIgnoreCase( "class" ))
        continue;

      Method rm = aProps[x].getReadMethod();

      if (rm == null)
        continue;

      if (rm.getAnnotation( NoIntrospect.class ) != null)
        continue;

      listProps.add( aProps[x] );
    }

    return listProps;

  } // end

  /**
   * Return a list of setter property names
   *
   * @param clsType
   *          The bean class to introspect
   * @return a List of PropertyDescriptors (that have write methods)
   * @throws Exception
   */
  public static List<PropertyDescriptor> getWriteProperties( Class<?> clsType ) throws Exception
  {
    PropertyDescriptor[] aProps = Introspector.getBeanInfo( clsType ).getPropertyDescriptors();
    List<PropertyDescriptor> listProps = new ArrayList<PropertyDescriptor>();

    for ( int x = 0; x < aProps.length; x++ )
    {
      if (aProps[x].getName().equalsIgnoreCase( "class" ))
        continue;

      if (aProps[x].getWriteMethod() != null)
        listProps.add( aProps[x] );
    }

    return listProps;

  } // end getWriteProperties()

  /**
   * Returns a List of Read methods (from getters) for the bean class specified. <br>
   * Any getter that was marked with the NoIntrospect annotation will not be
   * returned
   *
   * @param clsBean
   *          The Class object of the bean to get the read methods from
   * @return a List of Method objects. One for each getXxxx property defined on
   *         the bean class
   *
   * @throws Exception
   *           if any introspection error occurs
   */
  public static List<Method> getReadMethods( Class<?> clsBean ) throws Exception
  {
    PropertyDescriptor[] aProps = Introspector.getBeanInfo( clsBean ).getPropertyDescriptors();

    List<Method> listReadMethods = new ArrayList<Method>();

    for ( int x = 0; x < aProps.length; x++ )
    {
      if (aProps[x].getName().equals( "class" ))
        continue;

      Method m = aProps[x].getReadMethod();

      if (m == null)
        continue;

      if (m.getAnnotation( NoIntrospect.class ) != null)
        continue;

      listReadMethods.add( m );

    }

    return listReadMethods;

  } // end getReadMethods()


  /**
   * Returns a Map of Read methods (from getters) for the bean class specified <br>
   * The map key is the method name in lower case, and the map value is the read
   * Method instance
   *
   * @param clsBean
   *          The Class object of the bean to get the read methods from
   * @return a List of Method objects. One for each getXxxx property defined on
   *         the bean class
   *
   * @throws Exception
   *           if any introspection error occurs
   */
  public static Map<String, Method> getReadMethodMap( Class<?> clsBean ) throws Exception
  {
    PropertyDescriptor[] aProps = Introspector.getBeanInfo( clsBean ).getPropertyDescriptors();

    Map<String, Method> mapReadMethods = new HashMap<String, Method>();

    for ( int x = 0; x < aProps.length; x++ )
    {
      if (aProps[x].getName().equals( "class" ))
        continue;

      Method m = aProps[x].getReadMethod();

      if (m == null)
        continue;

      if (m.getAnnotation( NoIntrospect.class ) != null)
        continue;

      mapReadMethods.put( m.getName().toLowerCase(), m );

    } // end for()

    return mapReadMethods;

  } // end getReadMethodMap()

  /**
   * Returns a List of Write methods (from setters) for the bean class specified
   *
   * @param clsBean
   *          The Class object of the bean to get the write methods from
   * @return a List of Method objects. One for each setXxxx property defined on
   *         the bean class
   *
   * @throws Exception
   *           if any introspection error occurs
   */
  public static List<Method> getWriteMethods( Class<?> clsBean ) throws Exception
  {
    PropertyDescriptor[] aProps = Introspector.getBeanInfo( clsBean ).getPropertyDescriptors();

    List<Method> listReadMethods = new ArrayList<Method>();

    for ( int x = 0; x < aProps.length; x++ )
    {
      if (aProps[x].getName().equals( "class" ))
        continue;

      Method m = aProps[x].getWriteMethod();

      if (m == null)
        continue;

      listReadMethods.add( m );

    }

    return listReadMethods;

  } // end getWriteMethods()

  /**
   * Returns a Map of write methods (from setters) for the bean class specified <br>
   * The map key is the method name in lower case, and the map value is the
   * write Method instance
   *
   * @param clsBean
   *          The Class object of the bean to get the read methods from
   * @return a List of Method objects. One for each setXxxx property defined on
   *         the bean class
   *
   * @throws Exception
   *           if any introspection error occurs
   */
  public static Map<String, Method> getWriteMethodMap( Class clsBean ) throws Exception
  {
    PropertyDescriptor[] aProps = Introspector.getBeanInfo( clsBean ).getPropertyDescriptors();

    Map<String, Method> mapWriteMethods = new HashMap<String, Method>();

    for ( int x = 0; x < aProps.length; x++ )
    {
      if (aProps[x].getName().equals( "class" ))
        continue;

      Method m = aProps[x].getWriteMethod();

      if (m == null)
        continue;

      mapWriteMethods.put( m.getName().toLowerCase(), m );

    } // end for()

    return mapWriteMethods;

  } // end getWriteMethodMap()

  /**
   * Gets the PropertyDescriptor for the property and bean specified
   *
   * @param clsBean
   *          The bean Class object
   * @param strPropName
   *          The name of the bean property
   * @return The PropertyDescriptor of the property exists, else null is
   *         returned
   * @throws Exception
   */
  public static PropertyDescriptor getPropDescriptor( Class<?> clsBean, String strPropName ) throws Exception
  {
    Map<String, PropertyDescriptor> mapProps = getProps( clsBean );

    if (mapProps == null)
      return null;

    int nPos = strPropName.indexOf( '.' );

    if (nPos > 0)
    {
      while ( nPos >= 0 )
      {
        String strClassName = strPropName.substring( 0, nPos );
        strPropName = strPropName.substring( ++nPos );
        PropertyDescriptor pd = mapProps.get( strClassName.toLowerCase() );
        if (pd == null)
          throw new Exception( "No property exists for : " + clsBean.getSimpleName() + "." + strClassName );

        Class<?> clsProp = pd.getReadMethod().getReturnType();
        mapProps = getProps( clsProp );

        if (mapProps == null)
          throw new Exception( "No property exists for : " + clsBean.getSimpleName() + "." + strClassName );

        nPos = strPropName.indexOf( '.' );
      }

      return mapProps.get( strPropName.toLowerCase() );

    }

    return mapProps.get( strPropName.toLowerCase() );

  } // get getPropDescriptor()

  /**
   *
   * @param strFullyQualifiedPackage  The fully qualified package path of the object to create i.e. com.mycompany.util.MyObject
   * @param aclsConstcuctorArgs  if non zero arg constructor, then array of Class constrctor param types else this is null
   * @param aParams   if non zero arg constructor, then array of object values used to instantiate the object else this is null
   * @return  The created instance of the object
   * @throws Exception
   */
  public static Object createObjectInstance( String strFullyQualifiedPackage, Class[] aclsConstcuctorArgs, Object[] aParams )  throws Exception
  {
    Class clsObj = Class.forName( strFullyQualifiedPackage );

    Constructor objConstructor = null;
    Object objInstance = null;

    if ( aclsConstcuctorArgs != null )
    {
       Constructor constructorBean = clsObj.getDeclaredConstructor( aclsConstcuctorArgs );

      constructorBean.setAccessible( true );

      objInstance = constructorBean.newInstance( aParams );

    }
    else
    {
      objConstructor = clsObj.getDeclaredConstructor();
      
      objInstance = objConstructor.newInstance();
    }

    return objInstance;

  } // end getInstance()

  /**
   * Dumps the values of a bean in the format propname=value
   *
   * @param objBean
   *          The bean instance to dump the values for
   * @return String of propname=value pairs for each getXxx method defined for
   *         the bean
   *
   */
  public static String dumpBeanValues( Object objBean )
  {
    return dumpBeanValues( objBean, null );
  }

  /**
   * Dumps the values of a bean in the format propname=value
   *
   * @param objBean
   *          The bean instance to dump the values for
   * @param aClsIgnore
   *          an Array of super classes to ignore when dumping bean values
   * @return String of propname=value pairs for each getXxx method defined for
   *         the bean
   *
   */
  public static String dumpBeanValues( Object objBean, Class[] aClsIgnore )
  {
    StringBuffer sb = new StringBuffer();
    try
    {
      List<Method> listGetters = VwBeanUtils.getReadMethods( objBean.getClass() );

      for ( Method mthdGetter : listGetters )
      {
        boolean fIgnoreClass = false;
        Class clsMethod = mthdGetter.getDeclaringClass();

        if (aClsIgnore != null)
        {
          for ( int x = 0; x < aClsIgnore.length; x++ )
          {
            if (clsMethod == aClsIgnore[x])
            {
              fIgnoreClass = true;
              break;
            }
          }

        }

        if (fIgnoreClass)
          continue;

        if (mthdGetter.getParameterTypes().length > 0)
          continue;

        String strName = mthdGetter.getName();
        int nLen = 3;

        if (strName.startsWith( "is" ))
          nLen = 2;

        sb.append( "\n" ).append( strName.substring( nLen ) ).append( "=" );

        Object objValue = mthdGetter.invoke( objBean, null );

        if (objValue == null)
          sb.append( "null" );
        else
          sb.append( objValue.toString() );

      }

    }
    catch ( Exception ex )
    {
      sb.append( ex.toString() );
    }

    return sb.toString();

  } // end dumpBeanValues()

  /**
   * Return a Map of PropertyDescriptor objects for the class specified
   *
   * @param clsBean
   *          The class to get the ProperyDescriptor's for
   * @return
   * @throws Exception
   */
  private static Map<String, PropertyDescriptor> getProps( Class<?> clsBean ) throws Exception
  {

    Map<String, PropertyDescriptor> mapProps = null;

    synchronized ( s_mapBeanProps )
    {
      mapProps = s_mapBeanProps.get( clsBean );
      if (mapProps == null)
      {
        mapProps = new HashMap<String, PropertyDescriptor>();
        s_mapBeanProps.put( clsBean, mapProps );

        PropertyDescriptor[] aProps = Introspector.getBeanInfo( clsBean ).getPropertyDescriptors();

        for ( int x = 0; x < aProps.length; x++ )
        {
          String strName = aProps[x].getName().toLowerCase();

          if (strName.equalsIgnoreCase( "class" ))
            continue;

          mapProps.put( strName, aProps[x] );
        }

      } // end if

    } // end synchronized

    return mapProps;

  } // end getProps()

  /**
   * Determines if the class name is a simple type. I.E. String, Byte, Integer,
   * int, short ...
   *
   * @param objInstance
   *          The ovject instance to test
   *
   * @return true if the class type is a simple type, false otherwise
   */
  public static boolean isSimpleType( Object objInstance )
  { return isSimpleType( objInstance.getClass() ); }


  /**
   * Determines if the class name is a simple type. I.E. String, Byte, Integer,
   * int, short ...
   *
   * @param classType
   *          The class type of the method parameter
   *
   * @return true if the class type is a simple type, false otherwise
   */
  public static boolean isSimpleType( Class<?> classType )
  {
    if (classType == null)
      return true;

    if (classType == String.class || classType == Boolean.TYPE || classType == Byte.TYPE
        || classType == Character.TYPE || classType == Short.TYPE
        || classType == Integer.TYPE || classType == Long.TYPE || classType == Float.TYPE
        || classType == Double.TYPE || classType == java.math.BigInteger.class
        || classType == java.math.BigDecimal.class || classType == Boolean.class
        || classType == Byte.class || classType == Character.class
        || classType == Short.class || classType == Integer.class
        || classType == Long.class || classType == Float.class || classType == VwDate.class
        || classType == Date.class || classType == java.sql.Date.class
        || classType == Timestamp.class || classType == Calendar.class || classType == Double.class)
      return true;

    return false;

  } // end isSimpleType()

  /**
   * Determines if the class name is a Date type. I.E. Date, Timestamp, Calendar...
   *
   * @param objInstance
   *          The ovject instance to test
   *
   * @return true if the class type is a simple type, false otherwise
   */
  public static boolean isDateType( Object objInstance )
  { return isDateType( objInstance.getClass() ); }

  /**
   * Determines if the class name is a Date type. I.E. Date, Timestamp, Calendar...
   *
   * @param classType
   *          The class type of the method parameter
   *
   * @return true if the class type is a simple type, false otherwise
   */
  public static boolean isDateType( Class<?> classType )
  {
    if (classType == null)
      return false;

    if ( classType == VwDate.class|| classType == Date.class || classType == java.sql.Date.class
        || classType == Timestamp.class || classType == Calendar.class )
      return true;

    return false;

  } // end isSimpleType()

  /**
   * Try to convert string value to the class type specified
   *
   * @param clsConvertToType
   *          The class type to convert to
   * @param strValue
   *          The string value to convert
   *
   * @return an Object of the converted class type if successful else null is
   *         returned
   */
  public static Object convertFromString( Class<?> clsConvertToType, String strValue )
  {
    if (clsConvertToType == String.class)
      return strValue;
    else
      if (clsConvertToType == Boolean.TYPE || clsConvertToType == Boolean.class)
      {
        boolean fFlag = false;

        if (strValue.equalsIgnoreCase( "true" ))
          fFlag = true;
        else
          if (strValue.toLowerCase().startsWith( "y" ))
            fFlag = true;

        return new Boolean( fFlag );

      }
      else
        if (clsConvertToType == Byte.TYPE || clsConvertToType == Byte.class)
          return new Byte( strValue );
        else
          if (clsConvertToType == Character.TYPE || clsConvertToType == Character.class)
            return new Character( strValue.charAt( 0 ) );
          else
            if (clsConvertToType == Short.TYPE || clsConvertToType == Short.class)
              return new Short( strValue );
            else
              if (clsConvertToType == Integer.TYPE || clsConvertToType == Integer.class)
                return new Integer( strValue );
              else
                if (clsConvertToType == Long.TYPE || clsConvertToType == Long.class)
                  return new Long( strValue );
                else
                  if (clsConvertToType == Float.TYPE || clsConvertToType == Float.class)
                    return new Float( strValue );
                  else
                    if (clsConvertToType == Double.TYPE || clsConvertToType == Double.class)
                      return new Double( strValue );
                    else
                      if (clsConvertToType == java.math.BigInteger.class)
                        return new java.math.BigInteger( strValue );
                      else
                        if (clsConvertToType == java.math.BigDecimal.class)
                          return new java.math.BigDecimal( strValue );

    return null; // Can't convert

  } // end

  /**
   * Determines if the class name is a Collection type. I.E. List, Map, Vector
   * Hashtable
   *
   * @param objInstance
   *          The object instance to test
   *
   * @return true if the class type is a Collection type or array, false
   *         otherwise
   */
  public static boolean isCollectionType( Object objInstance )
  { return isCollectionType( objInstance.getClass() ); }

  /**
   * Determines if the class name is a Collection type. I.E. List, Map, Vector
   * Hashtable
   *
   * @param classType
   *          The class type to test
   *
   * @return true if the class type is a Collection type or array, false
   *         otherwise
   */
  public static boolean isCollectionType( Class<?> classType )
  {
    if (java.util.Collection.class.isAssignableFrom( classType )
        || java.util.Iterator.class.isAssignableFrom( classType )
        || java.util.Enumeration.class.isAssignableFrom( classType )
        || Map.class.isAssignableFrom( classType ) || classType.isArray())
      return true;

    return false;

  } // end isCollectionType()

  /**
   * Tests the property's return type for a generic type
   * 
   * @param clsObj
   *          The class of the object containing the property to test
   * @param strPropName
   *          The name of the property to test
   * 
   * @return true if the return type of the property is a generic type, false
   *         otherwise
   * @throws Exception
   *           if the property name does not exist
   */
  public static boolean isGenericReturnType( Class<?> clsObj, String strPropName ) throws Exception
  {
    Map<String, PropertyDescriptor> mapProps = s_mapBeanProps.get( clsObj );
    if (mapProps == null)
      mapProps = getProps( clsObj );

    PropertyDescriptor pd = mapProps.get( strPropName.toLowerCase() );
    if (pd == null)
      throw new Exception( "Property '" + strPropName + "' does not exist" );

    return isGenericReturnType( pd );

  } // end

  /**
   * 
   * Tests the PropertyDescriptor's read method retuun type for generic type
   * 
   * @param pd
   *          The property descriptor to test
   * 
   * @return true if the return type of the property is a generic type
   * @throws Exception
   *           if the property descriptor does not have a read method
   */
  public static boolean isGenericReturnType( PropertyDescriptor pd ) throws Exception
  {
    Method mthdReturn = pd.getReadMethod();

    if (mthdReturn == null)
      throw new Exception( "Property '" + pd.getName() + "' does not have a read method(getter)" );

    return isGenericReturnType( mthdReturn );
  }

  /**
   * Tests the Methods return type for a generic type
   * 
   * @param mthdReturn
   *          The method to test
   * @return true if the method's return type is a generic type, false otherwise
   * @throws Exception
   */
  public static boolean isGenericReturnType( Method mthdReturn ) throws Exception
  {
    return isGenericType( mthdReturn.getGenericReturnType() );

  } // end isGenericReturnType(

  /**
   * Tests the property's return type for a generic type
   * 
   * @param clsObj
   *          The class of the object containing the property to test
   * @param strPropName
   *          The name of the property to test
   * @param nParamNbr
   *          The parameter number to test
   * 
   * @return true if the return type of the property is a generic type, false
   *         otherwise
   * @throws Exception
   *           if the property name does not exist
   */
  public static boolean isGenericParameterType( Class<?> clsObj, String strPropName, int nParamNbr ) throws Exception
  {
    Map<String, PropertyDescriptor> mapProps = s_mapBeanProps.get( clsObj );
    if (mapProps == null)
      mapProps = getProps( clsObj );

    PropertyDescriptor pd = mapProps.get( strPropName.toLowerCase() );
    if (pd == null)
      throw new Exception( "Property " + strPropName + " does not exist" );

    return isGenericParameterType( pd, nParamNbr );

  } // end

  /**
   * 
   * Tests the PropertyDescriptor's read method return type for generic type
   * 
   * @param pd
   *          The property descriptor to test
   * @param nParamNbr
   *          The parameter number to test
   * 
   * @return true if the return type of the property is a generic type
   * @throws Exception
   *           if the property descriptor does not have a read method
   */
  public static boolean isGenericParameterType( PropertyDescriptor pd, int nParamNbr ) throws Exception
  {
    Method mthdWrite = pd.getWriteMethod();

    if (mthdWrite == null)
      throw new Exception( "Property '" + pd.getName() + "' does not have a write method(setter)" );

    return isGenericParameterType( mthdWrite, nParamNbr );
  }

  /**
   * Tests the Methods return type for a generic type
   * 
   * @param mthdWrite
   *          The method to test
   * @return true if the method's return type is a generic type, false otherwise
   * @throws Exception
   */
  public static boolean isGenericParameterType( Method mthdWrite, int nParamNbr ) throws Exception
  {
    Type[] aTypes = mthdWrite.getGenericParameterTypes();
    if (nParamNbr > aTypes.length)
      throw new Exception( "Parameter number is out of bounds for the setter method '" + mthdWrite.getName() + "'" );

    return isGenericType( aTypes[nParamNbr] );

  } // end isGenericReturnType(

  /**
   * Test the Type object for a generic type
   * 
   * @param type
   *          The type to test
   * 
   * @return true if the type is a generic type, false otherwise
   */
  public static boolean isGenericType( Type type )
  {
    String strType = type.toString();
    int nPos = strType.indexOf( '<' );
    return (nPos > 0 && strType.charAt( ++nPos ) != '?');

  }

  /**
   * Gets the fully qualified class name the properties generic return type
   * 
   * @param clsObj
   *          The class of the object containing the property to test
   * 
   * @param strPropName
   *          The name of the property get get the generic type
   * 
   * @return the fully qualified class name the property's generic return type
   *         or null if the type is not generic
   * @throws Exception
   *           if the property name does not exist
   */
  public static String getGenericReturnType( Class<?> clsObj, String strPropName ) throws Exception
  {
    Map<String, PropertyDescriptor> mapProps = s_mapBeanProps.get( clsObj );
    if (mapProps == null)
      mapProps = getProps( clsObj );

    PropertyDescriptor pd = mapProps.get( strPropName.toLowerCase() );
    if (pd == null)
      throw new Exception( "Property '" + strPropName + "' does not exist" );

    return getGenericReturnType( pd );

  } // end

  /**
   * 
   * Gets the generic type for PropertyDescriptor's read method's return type
   * 
   * @param pd
   *          The property descriptor to test
   * 
   * @return the generic type for PropertyDescriptor's read method's return type
   *         or null if the type is not a generic type
   * @throws Exception
   *           if the property descriptor does not have a read method
   */
  public static String getGenericReturnType( PropertyDescriptor pd ) throws Exception
  {
    Method mthdReturn = pd.getReadMethod();

    if (mthdReturn == null)
      throw new Exception( "Property '" + pd.getName() + "' does not have a read method(getter)" );

    return getGenericReturnType( mthdReturn );
  }

  /**
   * Returns a String containing the fully qualified class name of the generic
   * type
   * 
   * @param mthdReturn
   *          The return method to test
   * 
   * @return a String containing the fully qualified class name of the generic
   *         type or null if the type is not generic
   */
  public static String getGenericReturnType( Method mthdReturn )
  {
    Type type = mthdReturn.getGenericReturnType();
    if (type == null)
      return null;

    return getGenericType( type );

  } // end getGenericReturnType()

  /**
   * Gets the fully qualified class name the properties generic return type
   * 
   * @param clsObj
   *          The class of the object containing the property to test
   * 
   * @param strPropName
   *          The name of the property get get the generic type
   * @param nParamNbr
   *          The parameter nbr to get the generic type for
   * 
   * @return the fully qualified class name the property's generic return type
   *         or null if the type is not generic
   * @throws Exception
   *           if the property name does not exist
   */
  public static String getGenericParameterType( Class<?> clsObj, String strPropName, int nParamNbr ) throws Exception
  {
    Map<String, PropertyDescriptor> mapProps = s_mapBeanProps.get( clsObj );
    if (mapProps == null)
      mapProps = getProps( clsObj );

    PropertyDescriptor pd = mapProps.get( strPropName.toLowerCase() );
    if (pd == null)
      throw new Exception( "Property '" + strPropName + "' does not exist" );

    return getGenericParameterType( pd, nParamNbr );

  } // end

  /**
   * 
   * Gets the generic type for PropertyDescriptor's read method's return type
   * 
   * @param pd The property descriptor to test
   * @param nParamNbr The  parameter nbr to get the generic type for
   * 
   * @return the generic type for PropertyDescriptor's read method's return type
   *         or null if the type is not a generic type
   * 
   * @throws Exception
   *           if the property descriptor does not have a write/setter method
   */
  public static String getGenericParameterType( PropertyDescriptor pd, int nParamNbr ) throws Exception
  {
    Method mthdWrite = pd.getWriteMethod();

    if (mthdWrite == null)
      throw new Exception( "Property '" + pd.getName() + "' does not have a write method(setter)" );

    return getGenericParameterType( mthdWrite, nParamNbr );
  }

  /**
   * Returns a String containing the fully qualified class name of the generic
   * type
   * 
   * @param mthdWrite
   *          The write method to get
   * @param nParamNbr The
   *          parameter nbr to the get the generic type for
   * 
   * @return a String containing the fully qualified class name of the generic
   *         type or null if the type is not generic
   * @exception if the parameter nbr is out of bounds
   */
  public static String getGenericParameterType( Method mthdWrite, int nParamNbr ) throws Exception
  {
    Type[] aTypes = mthdWrite.getGenericParameterTypes();
    if (nParamNbr > aTypes.length)
      throw new Exception( "Parameter number is out of bounds for the setter method '" + mthdWrite.getName() + "'" );

    return getGenericType( aTypes[nParamNbr] );

  } // end getGenericReturnType()

  /**
   * Gets the fully qualified class name of the generic type
   * 
   * @param type
   *          The type to extract the class name form
   * @return a String of the fully qualified class name of the generic type or
   *         null if not generic
   */
  public static String getGenericType( Type type )
  {
    String strType = type.toString();
    int nPos = strType.lastIndexOf( '<' );
    if (nPos < 0)
      return null;

    if (nPos > 0 && strType.charAt( ++nPos ) == '?')
      return null; // treat wildcard generics as no generic as class type cannot
                   // be determined

    int nEndPos = strType.lastIndexOf( '>' );

    return strType.substring( nPos, nEndPos );

  } // end getGenericType()

  /**
   * Gets the fully qualified class name of the generic type
   * 
   * @param type
   *          The type to extract the class name form
   * @return a String of the fully qualified class name of the generic type or
   *         null if not generic
   */
  public static Class<?> getGenericTypeAsClass( Type type ) throws Exception
  {
    return Class.forName( getGenericType( type ) );
  }

  /**
   * Returns the super class of clsBase
   *
   * @param clsBase The class to tp get the super class from
   * @return
   */
  public static Class getSuperClass( Class clsBase )
  {
    if ( clsBase == null )
    {
      return null;
    }

    return clsBase.getSuperclass();

  }

  /**
   * Gets a list of all super classes starting with clsStart and endind with clsStop (if specified)
   *
   * @param clsStart Required the base class to get the super classes
   * @param clsStop Optional the super class to stop at - if null all super classes are returned
   * @return
   */
  public static List<Class<?>> getAllSuperClasses( Class clsStart, Class clsStop )
  {
    List<Class<?>>listSuperClasses = new ArrayList<>(  );

    while( true )
    {
      Class clsSuper = clsStart.getSuperclass();

      if ( clsSuper == null )
      {
        break;
      }

      if ( clsStop != null && clsSuper.equals( clsStop ))
      {
        break;
      }

      listSuperClasses.add( clsSuper );

      clsStart = clsSuper;

    }
    if ( listSuperClasses.size() == 0 )
    {

      listSuperClasses = null;
    }
    return listSuperClasses;

  }

  public static Class<?>[] obj2Class( Object[]aobj )
  {

    if ( aobj == null )
      return null;

    Class<?>[] acls = new Class[ aobj.length ];
    for ( int x = 0; x < acls.length; x++)
    {
      if ( aobj[ x  ] instanceof Class )
       acls[ x ] = (Class)aobj[ x ];
      else
       acls[ x ] = aobj[ x ].getClass();
    }

    return acls;
    
    
  }


  /**
   * Returns true if the object has the method with the parameters specified
   * @param objInstance
   * @param strMethodName
   * @param aobjParams
   * @return
   */
  public static boolean hasMethod( Object objInstance, String strMethodName, Object[] aobjParams )
  {


    try
    {
      Class<?>[]aclsParamTypes = obj2Class( aobjParams );

      Method method = objInstance.getClass().getMethod( strMethodName, aclsParamTypes );

      return true;

    }
    catch ( NoSuchMethodException e )
    {
     return false;      // Methos doesnt exist
    }

  }
  /**
   * Invoke a method that does not return an object
   * @param objInstance The class object instance
   * @param strMethodName the method name to invoke
   * @param aobjParams the method's parameters
   * @throws Exception if the method could not be found or a runtime invocation error occurs
   */
  public static void execVoidMethod( Object objInstance, String strMethodName, Object[] aobjParams ) throws Exception
  {
    Class<?>[]aclsParamTypes = obj2Class( aobjParams );
    
    Method method = objInstance.getClass().getMethod( strMethodName, aclsParamTypes );
    method.invoke( objInstance, aobjParams );
    
  }
  
  
 /** Invoke a method that does not return an object
  * @param objInstance The class object instance
  * @param strMethodName the method name to invoke
  * @param aobjParams the method's parameters
  * @return The object returned by the method
  * @throws Exception if the method could not be found or a runtime invocation error occurs
   */
  public static Object execMethod( Object objInstance, String strMethodName, Object[] aobjParams ) throws Exception
  {
    Class<?>[]aclsParamTypes = null;
    
    if ( aobjParams != null ) 
      aclsParamTypes = obj2Class( aobjParams );
    
    Method method = objInstance.getClass().getMethod( strMethodName, aclsParamTypes );
    return method.invoke( objInstance, aobjParams );
    
  }
  
  public static void main( String[] args )
  {
    try
    {
      List<PropertyDescriptor> listProps = VwBeanUtils.getReadProperties( VwDVOBase.class );
      for ( PropertyDescriptor pd : listProps )
        System.out.println( pd.getName() );

    }
    catch ( Exception e )
    {
      e.printStackTrace();
    }
  }

} // end class VwBeanUtils{}

// *** End of VwBeanUtils.java ***

