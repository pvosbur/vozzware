/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwResourceMgr.java

============================================================================================
*/

package com.vozzware.util;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * This class manages resource bundles. It caches bundles by their fully qualified name and can be used to
 * retrive property values
 *
 */
public class VwResourceMgr
{
  private static Map<String,Map<String,String>> s_mapPropsByNameLocale = Collections.synchronizedMap( new HashMap<String,Map<String,String>>() );
  private static Map<Locale,Map<String,String>> s_mapPropsByLocale = Collections.synchronizedMap( new HashMap<Locale,Map<String,String>>() );
  private static Map<String,String>             s_mapLoadedResources = Collections.synchronizedMap( new HashMap<String,String>() );
  
  private static Locale s_localeDefault = Locale.getDefault();
  
  
  /**
   * Sets the default locale to use when just the get key value methods are callled with no locale specified
   * @param localeDefault The default locale to use
   */
  public static void setDefaultLocale( Locale localeDefault )
  { s_localeDefault = localeDefault; }
  
  
  /**
   * gets the default locale 
   * @return
   */
  public static Locale getDefaultLocale()
  { return s_localeDefault; }

  /**
   * Removes a resource bundle or properties file cache. This allows reloading of a resource
   * 
   * @param strResourceName The name of a resource bundle or properties file. It must be the same name as was called
   * <br> in the loadBundle or loadProperties method.
   */
  public static void removeFromCache( String strResourceName )
  { removeFromCache( strResourceName, null ); }
  
  
  
  /**
   * Removes a resource bundle or properties file cache. This allows reloading of a resource
   * 
   * @param strResourceName The name of a resource bundle or properties file. It must be the same name as was called
   * <br> in the loadBundle or loadProperties method.
   * @param locale The associated locale or null for default
   */
  public static void removeFromCache( String strResourceName, Locale locale )
  {
    if ( locale == null )
      locale = Locale.getDefault();
    
    s_mapLoadedResources.remove( strResourceName + locale.toString() );
    
  } // end removeFromCache()
  
  
  /**
   * Loads the requested resource bundle
   * 
   * @param strBundleName The bundle to load
   * @param session The HttpSession holding the locale of the bundle to load (using session key itcLocale).
   * <br>This may be null and defaults to the English language locale. If no locale exists in the session, the englist locale is stored.
   * @param fMergeKeys If true, all bundles for a locale will be merged in to one properties file
   * @throws Exception If the locale cannot be found
   */
  public static synchronized void loadBundle( String strBundleName, HttpSession session, boolean fMergeKeys ) throws Exception
  {
    Locale locale = (Locale)session.getAttribute( "itcLocale");
    
    if ( locale == null  ) // use english as default
    {
      locale = Locale.getDefault();
      
      session.setAttribute( "itcLocale", locale );
    }
    
    loadBundle(strBundleName, locale, fMergeKeys );
    
  }

  /**
   * 
   * @param strPropertyName
   * @param session
   * @param fMergeKeys
   * @throws Exception
   */
  public static synchronized void loadProperties( String strPropertyName, HttpSession session, boolean fMergeKeys ) throws Exception
  {
    Locale locale = (Locale)session.getAttribute( "itcLocale");
    
    if ( locale == null  ) // use english as default
    {
      locale = Locale.getDefault();
      session.setAttribute( "itcLocale", locale );
    }
    
    loadProperties( strPropertyName, locale, fMergeKeys );
    
  } // end loadProperties

  
  
  /**
   * Loads a ResourceBundle into the specified Map object using the default locale
   * 
   * @param strBundleName The base name of the resource bundle to load
   * @param map The map instance to the resource bundle into
   * 
   * @throws Exception
   */
  public static void loadBundleToMap( String strBundleName, Map<String,String>map ) throws Exception 
  {
    Map<String,String> mapTemp = loadBundleToMap( strBundleName, (Locale)null );
    map.putAll( mapTemp );
    
  }
  
  /**
   * Loads a ResourceBundle into the specified Map object
   * 
   * @param strBundleName The base name of the resource bundle to load
   * @param locale The locale of the resource bundle, if null the default locale is used
   * @param map The map instance to the resource bundle into
   * 
   * @throws Exception
   */
  public static void loadBundleToMap( String strBundleName, Locale locale, Map<String,String>map ) throws Exception 
  {
    Map<String,String> mapTemp = loadBundleToMap( strBundleName, locale );
    map.putAll( mapTemp );
    
  }
  
  /**
   * Loads a resource bundle into a map object using the default locale
   * 
   * @param strBundleName The bundle base name to load
   * 
   * @return A Map of the budle keys and values
   * @throws Exception
   */
  public static Map<String,String>loadBundleToMap( String strBundleName ) throws Exception 
  { return loadBundleToMap( strBundleName, (Locale)null ); }
  
  
  /**
   * Loads a resource bundle into a map object
   * 
   * @param strBundleName The bundle base name to load
   * @param locale The Locale of the bundle to load, if null the default locale is used
   * 
   * @return A Map of the budle keys and values
   * @throws Exception
   */
  public static Map<String,String>loadBundleToMap( String strBundleName, Locale locale ) throws Exception 
  {
    if ( locale == null )
    {
      locale = Locale.getDefault();
    }
    
    ResourceBundle rb = ResourceBundle.getBundle( strBundleName, locale );
    
    Map<String,String>mapBundle = new HashMap<String, String>();
    
    Enumeration<String> enKeys = rb.getKeys();
    
    while( enKeys.hasMoreElements() )
    {
      String strKey = enKeys.nextElement();
      mapBundle.put( strKey, VwExString.expandMacro( rb.getString( strKey ) ) );
    }
    
    return mapBundle;
    
  } // end loadBundleToMap
  

  /**
   * Loads a properties file return a Properties object
   * 
   * @param strPropertiesName The properties file to load
    * 
   * @returnA Properties object if the properties file could be loaded
   * @throws Exception
   */
  public static Properties getProperties( String strPropertiesName ) throws Exception 
  {

    if ( !strPropertiesName.endsWith( ".properties" ) )
    {
      strPropertiesName += ".properties";

    }
    String strPropsToLoad = VwExString.expandMacro( strPropertiesName );
    
    Properties props = new Properties();
    File fileProps = new File( strPropsToLoad );
    URL urlProps = null;
    
    if ( !fileProps.exists() )
    {
      urlProps = VwResourceStoreFactory.getInstance().getStore().getPropertiesAsURL( strPropsToLoad );
      if ( urlProps == null )
      {
        throw new Exception( "Properties file: '" + strPropsToLoad +  "' cannot be found");
      }
      
    }
    else
    {
      urlProps = fileProps.toURL();
    }
    

    
    props.load( urlProps.openStream() );
    
    Enumeration en = props.keys();
    
    // cycle through and expand macros
    while ( en.hasMoreElements() )
    {
      String strKey = (String)en.nextElement();
      String strVal = VwExString.expandMacro( props.getProperty( strKey ) );
      props.put( strKey, strVal );
      
    }
    
    return props;
    
    
  } // end getProperties()
 
  /**
   * Loads the requested resource bundle
   * 
   * @param strBundleName The bundle to load
   * @param fMergeKeys If true, all bundles for a locale will be merged in to one properties file
   * @throws Exception If the locale cannot be found
   */
  public static synchronized void loadBundle( String strBundleName, boolean fMergeKeys ) throws Exception
  { loadBundle( strBundleName, (Locale)null, fMergeKeys); }
  
  
  /**
   * Loads the requested resource bundle
   * 
   * @param strBundleName The bundle to load
   * @param locale The locale of the bundle to load. This may be null and defaults to the English language locale
   * @param fMergeKeys If true, all bundles for a locale will be merged in to one properties file
   * @throws Exception If the locale cannot be found
   */
  public static synchronized void loadBundle( String strBundleName, Locale locale, boolean fMergeKeys ) throws Exception
  {
    ResourceBundle rb = null;
    Map<String,String> mapProperties = null;
    
    if ( locale == null )
    {
      locale = Locale.getDefault();
    }
    
    if ( s_mapLoadedResources.containsKey( strBundleName + locale.toString() ))
    {
      return;
    }
    
    s_mapLoadedResources.put( strBundleName + locale.toString(), null );

    // if dotted path to properites was specified, use standaed bundle loading else use resource store    
    if ( strBundleName.indexOf( '.' ) > 0 ) 
    {
      rb = ResourceBundle.getBundle( strBundleName, locale );
    }
    else
    {
      rb = VwResourceStoreFactory.getInstance().getStore().getPropertiesAsBundle( strBundleName, locale );
    }
    
    if ( rb == null )
    {
      rb = ResourceBundle.getBundle(  strBundleName, locale );
    }
    
    if ( rb == null )
    {
      throw new Exception( "resource bundle '" + strBundleName + "' could not be located");
    }
    
    if ( fMergeKeys )
    {
      mapProperties = (Map)s_mapPropsByLocale.get( locale );
      
      if ( mapProperties == null )
      {
        mapProperties = Collections.synchronizedMap( new HashMap());
        s_mapPropsByLocale.put( locale, mapProperties );
      }
      
    }
    else
    {
      String strSuffix = "_" + locale.toString();
      String strName = strBundleName + strSuffix;
      mapProperties = s_mapPropsByNameLocale.get( strName );
      
      if ( mapProperties == null )
      {
        mapProperties = Collections.synchronizedMap( new HashMap<String,String>() );
        s_mapPropsByNameLocale.put( strName, mapProperties );
        
      }
 
    } // end else
    
    // Put keys in map from resource bundle
    Enumeration enumKeys = rb.getKeys();
    
    while( enumKeys.hasMoreElements() )
    {
      String strKey = (String)enumKeys.nextElement();
      String strValue = rb.getString( strKey );
      
      mapProperties.put( strKey, strValue );
    } // end while()
    
  } // end load

  /**
   * Loads a pro[erties file given the url to a properties file
   *
   * @param urlFileProps The url to the file properties to load
   * @throws Exception
   */
  public static synchronized void loadProperties( URL urlFileProps, boolean bMergeKeys, Locale locale ) throws Exception
  {
    if ( locale == null )
    {
      locale = Locale.getDefault();
    }

    loadPropsFromUrl( urlFileProps, bMergeKeys, locale );

  } // end loadProperties


  /**
   * Loads requested properties file
   * @param strPropertiesFile
   * @param fMergeKeys
   * @throws Exception
  */
  public static synchronized void loadProperties( String strPropertiesFile, boolean fMergeKeys ) throws Exception
  { loadProperties( strPropertiesFile, (Locale)null, fMergeKeys );  }

  /**
   * Loads the requested properties
   * 
   * @param strPropertiesFile The bundle to load
   * @param locale The locale of the properties file to load. This may be null and defaults to the English language locale
   * @param fMergeKeys If true, all bundles for a locale will be merged in to one properties file
   * @throws Exception If the locale cannot be found
   */
  public static synchronized void loadProperties( String strPropertiesFile,  Locale locale, boolean fMergeKeys ) throws Exception
  {
    if ( locale == null )
    {
      locale = Locale.getDefault();
    }

    strPropertiesFile = VwExString.expandMacro( strPropertiesFile  );
    
    if ( s_mapLoadedResources.containsKey( strPropertiesFile + locale.toString() ))
    {
      return;
    }
    
    s_mapLoadedResources.put( strPropertiesFile + locale.toString(), null );
    
 
    String strExtension = null;
    String strPropNameBase = null;

    int nPos = strPropertiesFile.lastIndexOf( '.' );
     
    if ( !strPropertiesFile.endsWith( ".properties" ) )
    {
      strPropNameBase = strPropertiesFile;
      strPropertiesFile += ".properties";
      strExtension = "properties";
    }
    else
    {
      strExtension = strPropertiesFile.substring( nPos + 1 );
      strPropNameBase = strPropertiesFile.substring( 0, nPos );
    }
    
    if ( locale.equals( Locale.getDefault() ))
    {
      try
      {
        loadProps( locale, strPropertiesFile, fMergeKeys );
        return;
      }
      catch( Exception ex )
      {
        ; // keep trying
      }
    }
    
    
    
    StringBuffer sbPropLocale = new StringBuffer( strPropNameBase );
    sbPropLocale.append( "_" ).append( locale.toString() ).append( "." ).append( strExtension );
    
    try
    {
      loadProps( locale, sbPropLocale.toString(), fMergeKeys );
      return;
    }
    catch( Exception ex )
    {
      ; // keep trying
    }
    
    // Ok still not found add in the country code to the file name
    
    sbPropLocale = new StringBuffer( strPropNameBase );
    sbPropLocale.append( "_" ).append( locale.toString() ).append( "_" ).append( locale.getCountry() );
    sbPropLocale.append( "." ).append( strExtension );  
    
    loadProps( locale, sbPropLocale.toString(), fMergeKeys );
    
  } // end load
  


  public static Map<String,String>loadPropFileToMap( String strPropertiesFile ) throws Exception
  {
    Map<String,String> mapProperties = new HashMap<>();
    Properties props = new Properties();

    File fileProps = new File( strPropertiesFile );

    URL urlProps = null;

    if ( !fileProps.exists() )
    {
        throw new Exception( "Properties file: '" + strPropertiesFile + " ' cannot be found");

    }
    else
    {
      urlProps = fileProps.toURI().toURL();
    }

    props.load( urlProps.openStream() );

    for ( Iterator iKeys = props.keySet().iterator(); iKeys.hasNext(); )
    {
      String strKey = (String)iKeys.next();

      mapProperties.put( strKey, props.getProperty( strKey ) );

    }

    return mapProperties;

  } // end loadPropFileToMap()


  /**
   * @param locale
   * @param strPropertiesFile
   * @param bMergeKeys
   */
  private static void loadProps( Locale locale, String strPropertiesFile, boolean bMergeKeys ) throws Exception
  {
    File fileProps = new File( strPropertiesFile );
    URL urlProps = null;
    
    if ( !fileProps.exists() )
    {
      urlProps = VwResourceStoreFactory.getInstance().getStore().getPropertiesAsURL( strPropertiesFile );
      if ( urlProps == null )
      {
        throw new Exception( "Properties file: '" + strPropertiesFile + " for Locale '" + locale.toString() + "' cannot be found");
      }
      
    }
    else
    {
      urlProps = fileProps.toURI().toURL();
    }
    
    loadPropsFromUrl( urlProps, bMergeKeys, locale );

    
  } // end load props()


  /**
   * Loads a properties file from a URL
   *
   * @param urlProps The url to the properties file to load
   * @param bMergeKeys  if true merge keys in with an existing properties map
   * @param locale  The locale
   *
   * @throws Exception
   */
  private static void loadPropsFromUrl( URL urlProps, boolean bMergeKeys, Locale locale ) throws Exception
  {
    Map<String,String> mapProperties = null;
    Properties props = new Properties();


    props.load( urlProps.openStream() );

    if ( bMergeKeys )
    {
      mapProperties = (Map<String,String>)s_mapPropsByLocale.get( locale );

      if ( mapProperties == null )
      {
        mapProperties = Collections.synchronizedMap( new HashMap<String,String>());
        s_mapPropsByLocale.put( locale, mapProperties );
      }

    }
    else
    {
      String strFilePath = urlProps.getFile();
      int nPos = strFilePath.lastIndexOf( "/" ) + 1;

      String strPropFileName = strFilePath.substring( nPos );
      
      mapProperties = (Map)s_mapPropsByNameLocale.get( strPropFileName  );

      if ( mapProperties == null )
      {
        mapProperties = Collections.synchronizedMap( new HashMap());
        s_mapPropsByNameLocale.put( strPropFileName + "_" + locale.toString(), mapProperties );
      }
      else
      {
        return;
      }

    } // end else

    // Load the properties map

    for ( Iterator iKeys = props.keySet().iterator(); iKeys.hasNext(); )
    {
      String strKey = (String)iKeys.next();

      mapProperties.put( strKey, VwExString.expandMacro( props.getProperty( strKey ) ));

    }

  } // end


  /**
   * Extracts a Properties set from the master list of properties by locale.
   *
   * @param strPartialKey If specified, only properties that start with the partial key are included else all are included
   *
   * @param locale The locale to get the properties from. if null, the default locale is used
   *
   * @return a Properties object with the specified properties or null if no properties are found
   */
  public static Properties extractProperties( String strPartialKey, Locale locale )
  {

    if ( locale == null )
    {
      locale = Locale.getDefault();
    }

    Map<String,String>mapProps = s_mapPropsByLocale.get( locale );

    if ( mapProps == null )
    {
      return null;
    }


    Properties props = new Properties(  );


    for ( String strKey : mapProps.keySet() )
    {

      if ( strPartialKey == null )
      {
        props.put( strKey, mapProps.get( strKey ) );
        continue;

      }
      if ( strKey.startsWith( strPartialKey ))
      {
        props.put( strKey, mapProps.get( strKey ) );
      }
    }

    if ( props.size() == 0 )
    {
      return null;
    }

    return props;


  }


  /**
   * Returns the properties map for the locale stored in the current HttpSession. If the session is null,
   * <br>then ENGLISH locale is used. Note! the merge keys option is required when loading bundles in order to use this method
   * @param session The HttpSession object containingg the Locale entry (using the key itcLocale) to get the map for
   * @return The Map of properties for the locale stored in the HttpSession object or null if no map exists.
   */
  public static Map<String,String> getPropertyMap( HttpSession session )
  {
    Locale locale = null;
    
    if ( session == null )
    {
      locale = Locale.getDefault();
    }
    else
    {
      locale = (Locale)session.getAttribute( "itcLocale" );
    }
    
    return getPropertyMap( locale );
    
    
  } // end getPropertyMap
  
  /**
   * Returns the properties map for the locale stored specified. If the loacle is null
   * <br>then ENGLISH locale is used. Note! the merge keys option is required when loading bundles in order to use this method
   * @return The Map of properties for the locale stored in the HttpSession object or null if no map exists.
   */
  public static Map<String,String> getPropertyMap( Locale locale )
  {
    
    if ( locale == null )
    {
      locale = Locale.getDefault();
    }
    
    return s_mapPropsByLocale.get(  locale  );
    
  }
  
  /**
   * Gets the value for the string key requested based on the locale stored in a HttpSession instance with the assumed key of 'itcLocale'.
   * <br>If no locale is found, English is used.
   * <br>If the keys were not merged then the static getString that requires the name of the bundle must be used
   * 
   * @param strKey The key to retrieve the value for
   * @return
   */
  public static String getString( String strKey, HttpSession session )
  {
    String strValue = null;
    
    Locale locale = (Locale)session.getAttribute( "itcLocale" );
    
    if ( locale == null )
    {
      locale = Locale.getDefault();
    }
    
    Map mapProps = (Map)s_mapPropsByLocale.get(  locale  );
    
    if ( mapProps != null )
    {
      strValue = (String)mapProps.get( strKey );
    }
      
    if ( strValue == null )
    {
      strValue = strKey;
    }
    
    
    return strValue;
    
  } // end getString()

  /**
   * Convert a string value to boolean. The foloowing string values are supported to return true:
   * <br> true, 1, y, yes. Any other value return false
   * @param strKey The property key, must represent
   * @return
   */
  public static boolean getBoolean( String strKey )
  { 
    String strVal = getString( strKey );
    return convertToBoolean( strVal );
    
  } // end 

  /**
   * Convert a string value to boolean. The foloowing string values are supported to return true:
   * <br> true, 1, y, yes. Any other value return false
   * @param strKey The property key, must represent
   * @return
   */
  public static boolean getBoolean( String strKey, boolean fDefault )
  { 
    String strVal = getString( strKey );
    
    if ( strVal.equals( strKey ))
    {
      return fDefault;
    }
    
    return convertToBoolean( strVal );
    
  } // end 
  
  /**
   * Convert a string value to boolean. The foloowing string values are supported to return true:
   * <br> true, 1, y, yes. Any other value return false
   * @param strKey The property key, must represent
   * @return
   */
  public static boolean getBoolean( String strKey, Locale locale )
  { 
    String strVal = getString( strKey, locale, null );
    return convertToBoolean( strVal );
    
  } // end 

  
  /**
   * Convert a string value to boolean. The foloowing string values are supported to return true:
   * <br> true, 1, y, yes. Any other value return false
   * @param strKey The property key, must represent
   * @return
   */
  public static boolean getBoolean( String strKey, Locale locale, boolean fDefault )
  { 
    String strVal = getString( strKey, locale, null );
    
    if ( strVal.equals( strKey ))
    {
      return fDefault;
    }
    
    return convertToBoolean( strVal );
    
  } // end 

  
  /**
   *Convert a string value to boolean. The foloowing string values are supported to return true:
   * <br> true, 1, y, yes. Any other value return false  * 
   * @param strVal The value to convert
   * @return
   */
  private static boolean convertToBoolean( String strVal )
  {
    if ( strVal == null )
    {
      return false;
    }
    
    if ( strVal.equalsIgnoreCase( "true" ) || strVal.equals( "1" ) || strVal.equalsIgnoreCase( "y" ) || strVal.equalsIgnoreCase( "yes" ))
    {
      return true;
    }
    
    return false;
    
  }

  
  /**
   * Converts the property value to an int value
   * @param strKey The property key 
   * @return
   */
  public static int getInt( String strKey )
  {
    String strVal = getString( strKey );
    if ( strVal == null )
    {
      return 0;
    }
    
    return Integer.parseInt( strVal );
    
  }

  /**
   * Converts the property value to an int value
   * @param strKey The property key 
   * @return
   */
  public static int getInt( String strKey, int nDefValue )
  {
    String strVal = getString( strKey );
    if ( strVal.equals( strKey ) )
    {
      return nDefValue;
    }
    
    return Integer.parseInt( strVal );
    
  }
  
  
  /**
   * Converts the property value to an int value
   * 
   * @param strKey The property key
   * @param locale the locale of the bundle to use
   * @return the property converted to an int
   */
  public static int getInt( String strKey, Locale locale )
  {
    String strVal = getString( strKey, locale, null );
    if ( strVal == null )
    {
      return 0;
    }
    
    return Integer.parseInt( strVal );
    
  }

  /**
   * Converts the property value to an int value
   * 
   * @param strKey The property key
   * @param locale the locale of the bundle to use
   * @return the property converted to an int
   */
  public static int getInt( String strKey, Locale locale, int nDefault )
  {
    String strVal = getString( strKey, locale, null );
    if ( strVal == null )
    {
      return nDefault;
    }
    
    return Integer.parseInt( strVal );
    
  }
  

  /**
   * Converts the property value to a double value
   * @param strKey The property key 
   * @return
   */
  public static double getDouble( String strKey, double dblDefault )
  {
    String strVal = getString( strKey );
    if ( strVal == null )
    {
      return dblDefault;
    }
    
    return Double.parseDouble( strVal );
    
  }

  /**
   * Converts the property value to a double value
   * @param strKey The property key 
   * @return
   */
  public static double getDouble( String strKey )
  {
    String strVal = getString( strKey );
    if ( strVal == null )
    {
      return 0;
    }
    
    return Double.parseDouble( strVal );
    
  }

  /**
   * Converts the property value to a double value
   * @param strKey The property key 
   * @return 0 if the key was not found
   */
  public static double getDouble( String strKey, Locale locale )
  {
    String strVal = getString( strKey, locale, null );
    if ( strVal == null )
    {
      return 0;
    }
    
    return Double.parseDouble( strVal );
    
  }
 
  
  /**
   * Converts the property value to a double value
   * @param strKey The property key 
   * @return 0 if the key was not found
   */
  public static double getDouble( String strKey, Locale locale, double dblDefault)
  {
    String strVal = getString( strKey, locale, null );
    if ( strVal == null )
    {
      return dblDefault;
    }
    
    return Double.parseDouble( strVal );
    
  }
 
  /**
   * Get a property value for a bundle in the locale specified
   * @param strKey The property key
   * @param locale The locale of the bundle
   * @return
   */
  public static String getString( String strKey, Locale locale )
  { return getString( strKey, locale, null ); }
  
   
  /**
   * Get a property value for a bundle in the locale specified
   * @param strKey The property key
   * @param locale The locale of the bundle
   * @return
   */
  public static String getString( String strKey, Locale locale, String strDefault )
  {
    
    if ( locale == null )
    {
      locale = Locale.getDefault();
    }
    
    String strValue = null;
    int nPos = strKey.indexOf( ':' );
    
    Map<String,String> mapProps = null;
    
    if ( nPos >= 0 )
    {
      String strBundle = strKey.substring( 0, nPos );
      strKey = strKey.substring( ++nPos );
      mapProps = getBundleProps( strBundle, locale);
    }
    else
    {
      mapProps = s_mapPropsByLocale.get(  locale  );
    }
    
    if ( mapProps != null )
    {
      strValue = (String)mapProps.get( strKey );
    }
      
    if ( strValue == null )
    {
      strValue = strDefault;
    }
    
    strValue = VwExString.expandMacro( strValue );
    
    return strValue;
    

  }
  
  public static Map<String,String> getBundleProps( Locale locale )
  {
    if ( locale == null )
    {
      locale = Locale.getDefault();
    }

    return s_mapPropsByLocale.get( locale );
  }


  public static Map<String,String> getBundleProps( String strBundle, Locale locale )
  {
    if ( locale == null )
    {
      locale = Locale.getDefault();
    }
    
    Map<String,String> mapProps = s_mapPropsByNameLocale.get( strBundle + "_" + locale.toString() );

    try
    {
      if ( mapProps == null )
      {
        loadBundle( strBundle, locale, false );
      }
    }
    
    catch( Exception ex )
    {
      return null; // can't get bundle
      
    }

    mapProps = (Map)s_mapPropsByNameLocale.get( strBundle + "_" + locale.toString() );    
    
    return mapProps;
  }



  /**
   * Gets the value for the string key requested. 
   * <br>If the mkeys were not merged then key value is expected to contain the form bundleName.keyname
   * 
   * @param strKey The key to retrieve the value for
   * @return
   */
  public static String getString( String strKey )
  { return getString( strKey, s_localeDefault, strKey );  } 

  
  /**
   * if the key is not defined, the the default value is returned
   * @param strKey Thesource key to retrieve
   * @param strDefault The default value if the key cannot be found
   * @return
   * @throws Exception
   */
  public static String getString( String strKey,  String strDefault )
  { return getString( strKey, s_localeDefault, strDefault ); }


  /**
   * Return the property value with a serach and replace on strXlateKey and strXlateValue
   * @param strKey The property key to retrieve the value
   * @param strXlateKey The substring to replace in the message
   * @param strXlateValue the replacement string
   * @return The property value with the strXlate key replaced by the strXlateValue
   */
  public static String getString( String strKey, String strXlateKey, String strXlateValue ) throws Exception
  {
    String strValue = getString( strKey, s_localeDefault, null );

    return VwExString.replace( strValue, strXlateKey, strXlateValue );

  } // end getString

  
  public static String getString( String strKey, String strXlateKey, String[] astrXlateValues ) throws Exception
  {
    String strValue = getString( strKey, s_localeDefault, null );

    return VwExString.replace( strValue, "%", astrXlateValues );

  } // end getString


  /**
   * The the property string and do macro substitution using the ${maroName} pattern
   * @param strKey The property key
   * @param objValues can be either a Map or a bean
   * @return
   * @throws Exception
   */
  public static String getMacroString( String strKey, Object objValues ) throws Exception
  {
    String strValue = getString( strKey, s_localeDefault );

    return VwExString.replace( strValue, objValues );

  } // end getString


  /**
   * The the property string and do macro substitution using the ${maroName} pattern
   * @param strKey The property key
   * @param objValues can be either a Map or a bean
   * @return
   * @throws Exception
   */
  public static String getMacroString( String strKey, Object objValues, Locale locale ) throws Exception
  {
    String strValue = getString( strKey, locale, (String)null );

    return VwExString.replace( strValue, objValues );

  } // end getString


  /**
   * This uses the java.text.MessageFormat class to translate and array of values that correspond
   * to {0}, {1} ... etc strings in the property value pattern
   * @param strKey The property key to retrieve the value
   * @param astrReplaceValues array of String replacement values for the value pattern
   * @param fKeyIsValue 
   * @return the proprty value translated
   */
  public static String getString( String strKey, String[] astrReplaceValues, boolean fKeyIsValue ) throws Exception
  {

    String strValue = null;

    if ( fKeyIsValue )
    {
      strValue = strKey;
    }
    else
    {
      strValue = getString( strKey, s_localeDefault, null );
    }

    return MessageFormat.format( strValue, astrReplaceValues );

  } // end getString()
  
} // end class VwResourceMgr{}

// *** End of VwResourceMgr.java ***

