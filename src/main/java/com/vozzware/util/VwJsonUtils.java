/*
 *
 * ============================================================================================
 *
 *                                A r m o r e d  I n f o   W e b
 *
 *                                     Copyright(c) 2012 By
 *
 *                                       Armored Info LLC
 *
 *                             A L L   R I G H T S   R E S E R V E D
 *
 *  ============================================================================================
 * /
 */

package com.vozzware.util;

import com.vozzware.db.VwDVOBase;
import com.vozzware.xml.VwBeanToJson;
import com.vozzware.xml.VwJsonToBean;
import com.vozzware.xml.VwJsonToMap;
import com.vozzware.xml.VwMapToJson;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.util.Map;

/*
============================================================================================


    Author:           petervosburgh
    
    Date Generated:   6/22/12

    Time Generated:   12:33 PM

============================================================================================
*/
public class VwJsonUtils
{

  /**
   * Converts a java bean object to a Json string
   *
   * @param objBean  The java object to convert
   *
   * @return The JSON string
   * @throws Exception
   */
  public static String toJson( Object objBean ) throws Exception
  { return toJson( objBean, false, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", null, null ); }


  /**
   * Converts a java bean object to a Json string
   *
   * @param objBean  The java object to convert
   *
   * @return The JSON string
   * @throws Exception
   */
  public static String toJson( Object objBean,  Map<String,String>mapIgnoreProps ) throws Exception
  { return toJson( objBean, false, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", mapIgnoreProps, null ); }

  /**
   * Converts a java bean object to a Json string
   *
   * @param objBean  The java object to convert
   * @param fForceArray if true force the json string to be an array
   *
   * @return The JSON string
   * @throws Exception
   */
  public static String toJson( Object objBean, boolean fForceArray ) throws Exception
  {
    return toJson( objBean, fForceArray, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", null, null );
  }

  public static String toJson( Object objBean, boolean fForceArray, Class clsCollectionOverride ) throws Exception
  {
    return toJson( objBean, fForceArray, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", null, clsCollectionOverride );
  }

  /**
   * Converts a java bean object to a Json string
   *
   * @param objBean  The java object to convert
   * @param fForceArray if true force the json string to be an array
   *
   * @return The JSON string
   * @throws Exception
   */
  public static String toJson( Object objBean, boolean fForceArray, Map<String,String>mapIgnoreProps ) throws Exception
  {
    return toJson( objBean, fForceArray, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", mapIgnoreProps, null );
  }


  /**
   * Converts a java bean object to a Json string
   *
   * @param objBean  The java object to convert
   * @param fForceArray if true force the json string to be an array
   *
   * @return The JSON string
   * @throws Exception
   */
  public static String toJson( Object objBean, boolean fForceArray, String strDateFormat  ) throws Exception
  {
    return toJson( objBean, fForceArray, strDateFormat, null, null );

  }

  /**
   * Converts a java bean object to a Json string
   *
   * @param objBean  The java object to convert
   * @param fForceArray if true force the json string to be an array
   *
   * @return The JSON string
   * @throws Exception
   */
  public synchronized  static String toJson( Object objBean, boolean fForceArray, String strDateFormat, Map<String,String>mapIgnoreProps, Class clsOverRide  ) throws Exception
  {

    VwBeanToJson btj = new VwBeanToJson();
    btj.setDateFormat( strDateFormat );

    btj.setForceArray( fForceArray );
    btj.setIgnoreProps( mapIgnoreProps );

    btj.setCollectionClassOverride( clsOverRide );

    return  btj.serialize( objBean );
  }


  /**
   * Converts a Map of name values to a JSON String
   *
   * @param mapParams Map to seriealize into a JSON string
   * @return
   * @throws Exception
   */
  public synchronized  static String toJson( Map mapParams ) throws Exception
  {
    VwMapToJson mtj = new VwMapToJson(  );
    return mtj.serialize( mapParams );

  }

  /**
   * Converts a JSON string to a Map
   * @param strJson The JSON string to deserialize to a Map
   * @return
   * @throws Exception
   */
  public synchronized  static Map fromJson( String strJson ) throws Exception
  {
    VwJsonToMap vtm = new VwJsonToMap();
    return vtm.deSerialize( strJson );

  }

  /**
   * Converts a JSON string to a java object
   * @param strJson The JSON string to convert
   * @param clsObj The Class of the java object to be created
   *
   * @return
   * @throws Exception
   */
  public static Object fromJson( String strJson, Class clsObj ) throws Exception
  {
    return fromJson( strJson, clsObj, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", null );

  }


  public static Object fromJson( String strJson, Class clsObj, Class[]aPackageClass ) throws Exception
  {
    return fromJson( strJson, clsObj, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", aPackageClass );

  }

  /**
   * Converts a JSON string to a java object
   * @param strJson The JSON string to convert
   * @param clsObj The Class of the java object to be created
   * @param strDateFormat the date format string for json to java date conversion
   *
   * @return
   * @throws Exception
   */
  public synchronized static Object fromJson( String strJson, Class clsObj, String strDateFormat, Class[]aPackageClass ) throws Exception
  {

    if ( aPackageClass != null )
    {
      VwJsonToBean.addPackageClass( aPackageClass );
    }

    VwJsonToBean jtb = new VwJsonToBean();

    jtb.setDateFormat( strDateFormat );

    Object obj = jtb.deSerialize( new InputSource( new StringReader( strJson )), clsObj );

    if ( obj instanceof VwDVOBase )
    {
      ((VwDVOBase)obj).setDirty( true );
    }

    return obj;


  }

  /**
   * Converts a JSON string to a java object
   * @param strJson The JSON string to convert
   * @param clsObj The Class of the java object to be created
   * @param mapElementHandlers a map of property names  and ther Class handlers
   * @return
   * @throws Exception
   */
  public synchronized static Object fromJson( String strJson, Class clsObj, Map<String,Class> mapElementHandlers ) throws Exception
  {


    VwJsonToBean jtb = new VwJsonToBean();

    for ( String strElement : mapElementHandlers.keySet() )
    {
      jtb.setElementHandler( strElement, mapElementHandlers.get( strElement ) );
    }


    jtb.setDateFormat( "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'" );

    Object obj = jtb.deSerialize( new InputSource( new StringReader( strJson )), clsObj );

    if ( obj instanceof VwDVOBase )
    {
      ((VwDVOBase)obj).setDirty( true );
    }

    return obj;


  }


  /**
    * Converts a JSON string to a java object
    * @param strJson The JSON string to convert
    * @param clsObj The Class of the java object to be created holding the list
    * @param clsListObj the class of the objects in the list
    *
    * @return
    * @throws Exception
    */
   public static Object fromJsonList( String strJson, Class clsObj, Class clsListObj ) throws Exception
   {
     return fromJsonList( strJson, clsObj, clsListObj, "EEE MMM dd yyyy HH:mm:ss zzz" );

   }


  /**
   * Converts a JSON string to a java object
   * @param strJson The JSON string to convert
   * @param clsObj The Class of the java object to be created holding the list
   * @param clsListObj the class of the objects in the list
   * @param strDateFormat the date format string for json to java date conversion
   *
   * @return
   * @throws Exception
   */
  public synchronized  static Object fromJsonList( String strJson, Class clsObj, Class clsListObj, String strDateFormat ) throws Exception
  {
    VwJsonToBean jtb = new VwJsonToBean();
    jtb.setDateFormat( strDateFormat );

    VwJsonToBean.addPackageClass( clsListObj );

    Object obj = jtb.deSerialize( new InputSource( new StringReader( strJson )), clsObj );

    if ( obj instanceof VwDVOBase )
    {
      ((VwDVOBase)obj).setDirty( true );
    }

    return obj;


  }

}
