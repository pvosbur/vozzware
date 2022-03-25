/*
===========================================================================================

 
                               Copyright(c) 2000 - 2006 by

                      V o z z W a r e   L L C (Vw)

                                 All Rights Reserved

Source Name: VwResourceStore.java

Create Date: May 3, 2006

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.


============================================================================================
*/
package com.vozzware.util;

import com.vozzware.ui.VwIcon;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;


public class VwResourceStore
{
  private Map<String, Object> s_mapResources = Collections.synchronizedMap( new HashMap<String,Object>() );
  
  private String m_strImagePath = "resources/images/";
  private String m_strMenuPath = "resources/menues/";
  private String m_strDocPath = "resources/docs/";
  private String m_strPropPath = "resources/properties/";
  
  /**
   * Constructor
   * @param rb The resources bundle with location properties that override the default locations may be null
   * <br>If the resources.properties is not defined then the following default locations are in effect
   * <br><strong>images</strong> are in resources/images
   * <br><strong>documents (.xml,.xsd,.wsdl etc...</strong> are in resources/docs
   * <br><strong>property files</strong> are in resources/properties
   */
  protected VwResourceStore( ResourceBundle rb  )
  {
    
    if ( rb != null ) // if resource bundle defined get paths from bundle keys
    {
      m_strImagePath = rb.getString( "path.images" );
      m_strMenuPath = rb.getString( "path.menues" );
      m_strDocPath = rb.getString( "path.docs" );
      m_strPropPath = rb.getString( "path.props" );
    }

    
    if ( ! m_strImagePath.endsWith( "/") )
      m_strImagePath += "/";   
    
    if ( ! m_strMenuPath.endsWith( "/") )
      m_strMenuPath += "/";
    
    if ( ! m_strDocPath.endsWith( "/") )
      m_strDocPath += "/" ;   

    if ( ! m_strPropPath.endsWith( "/") )
      m_strPropPath += "/" ;   

    if ( m_strImagePath.equals( "/") )
      m_strImagePath = "";   
    
    if ( m_strMenuPath.equals( "/") )
      m_strMenuPath = "";
    
    if ( m_strDocPath.equals( "/") )
      m_strDocPath = "" ;   

    if ( m_strPropPath.equals( "/") )
      m_strPropPath = "" ;   

  }
  
  
  /**
   * Puts the named icon in the resource cache
   * @param strImageName
   * @param icon
   */
  public void cacheIcon( String strImageName, VwIcon icon )
  { s_mapResources.put( strImageName, icon );  }
  
  
  /**
   * Creates an icon from any supported image file that the swing ImageIcon handles
   * @param strImageName The name of the image file without any path prefix
   * 
   * @return An VwIcon (swing ImageIcon) if successful, null otherwise
   */
  public VwIcon getIcon( String strImageName )
  {
    VwIcon icon = (VwIcon)s_mapResources.get( strImageName );
    
    if ( icon != null )
      return icon;

    URL urlIcon = null;
    
    try
    {
      urlIcon = VwDocFinder.findURL( null, m_strImagePath + strImageName );
    }
    catch( Exception ex )
    {
      return null;
    }

    if ( urlIcon == null )
      return null;

    icon = new VwIcon( urlIcon );
    s_mapResources.put( strImageName, icon );

    return icon;

  } // end getIcon()


  /**
   * Retrieves a URL to the image file location
   *
   * @param strImageName The name of the image file to retrieve without any path prefix
   * @return a URL to the image file if successful, null otherwise
   */
  public URL getIconURL( String strImageName )
  { return getDocument( strImageName, m_strImagePath, "_url" ); }

  /**
   * Returns a URL to the requested document file
   * @param strDocName The name of the document without any path prefix
   * @return a URL to the requested document file if successful, null otherwise
   */
  public URL getDocument( String strDocName )
  { return getDocument( strDocName, m_strDocPath, null  ); }


  /**
   * Returns a URL to the requested document file
   * @param strDocName The name of the document without any path prefix
   * @param strDocSuffix suffix to use for uniqueness (optional)
   * @return a URL to the requested document file if successful, null otherwise
   */
  private URL getDocument( String strDocName, String strDocPath, String strDocSuffix )
  {
    strDocName = VwExString.expandMacro( strDocName );

    if ( strDocSuffix == null )
    {
      strDocSuffix = "";
    }

    URL urlDoc = (URL)s_mapResources.get( strDocName + strDocSuffix );
    if ( urlDoc != null )
    {
      return urlDoc;
    }

    int nPos = strDocName.indexOf( '/' );
    if ( nPos < 0 )
    {
      nPos = strDocName.indexOf( '\\' );
    }
    try
    {

      if ( nPos >= 0 )
      {
        urlDoc = findAbsolute( strDocName );
        if ( urlDoc == null )
        {
          urlDoc =  VwDocFinder.findURL( null, strDocName );
        }

      }
      else
        urlDoc =  VwDocFinder.findURL( null, strDocPath + strDocName );

      if ( urlDoc == null )
        return null;

      s_mapResources.put(strDocName + strDocSuffix, urlDoc );

    }
    catch( Exception ex )
    {
      ;
    }

    return urlDoc;

  } // end getDocument()


  private URL findAbsolute( String strDocName ) throws Exception
  {
    // Try to find document in the absolute path

    File fileDoc = new File( strDocName );
    if ( !fileDoc.exists() )
      return null;

    return fileDoc.toURL();

  }


  /**
   * Returns a URL to the requested properties file
   * @param strPropName The name of the properties file without any path prefix
   *
   * @return a URL to the requested properties file if successful, null otherwise
   */
  public URL getPropertiesAsURL( String strPropName )
  { return getDocument( strPropName, m_strPropPath, "_url" ); }


  /**
   * Returns a ResourceBumdle for the requested properties file
   * @param strBundleName The name of the resource bundle file without any path prefix
   * @param session The HttpSession to find the active Locale in. This assumes The Locale is store
   * in the session with the 'itcLocale' key
   *
   * @return a ResourceBundle to the requested properties file if successful, null otherwise
   */
  public ResourceBundle getPropertiesAsBundle( String strBundleName, HttpSession session )
  {
    Locale locale = (Locale)session.getAttribute( "itcLocale" );
    return getPropertiesAsBundle( strBundleName, locale );

  }


  /**
   * Returns a ResourceBumdle for the requested properties file
   * @param strBundleName The name of the resource bundle file without any path prefix
   *
   * @return a ResourceBundle to the requested properties file if successful, null otherwise
   */
  public ResourceBundle getPropertiesAsBundle( String strBundleName )
  {
    return getPropertiesAsBundle( strBundleName, (Locale)null );

  }

  /**
   * Returns a ResourceBumdle for the requested properties file
   * @param strBundleName The name of the resource bundle file without any path prefix
   * @param locale The Locale to use for retrieving the right bundle. If nutll the JVM's default Locale is used
   *
   * @return a ResourceBundle to the requested properties file if successful, null otherwise
   */
  public ResourceBundle getPropertiesAsBundle( String strBundleName, Locale locale )
  {
    if ( locale == null )
      locale = Locale.getDefault();

    ResourceBundle rb = (ResourceBundle)s_mapResources.get( strBundleName + "_" + locale.toString() );
    if ( rb != null )
      return rb;

    try
    {

      String strRb = m_strPropPath + strBundleName;
      strRb = VwExString.replace( strRb, '/', '.' );
      
      rb = ResourceBundle.getBundle( strRb, locale );
      
      s_mapResources.put( strBundleName + "_" + locale.toString(), rb );
      
    }
    catch( Exception ex )
    {
      return null;
    }
    
    return rb;
    
  } // end getProperties()


  public String getImagePath()
  {  return m_strImagePath; }


  public void setImagePath( String imagePath )
  {  m_strImagePath = imagePath; }


  public String getMenuPath()
  { return m_strMenuPath; }


  public void setMenuPath( String menuPath )
  { m_strMenuPath = menuPath; }


  public String getDocPath()
  { return m_strDocPath; }


  public void setDocPath( String docPath )
  {  m_strDocPath = docPath;  }


  public String getPropPath()
  { return m_strPropPath; }


  public void setPropPath( String propPath )
  {  m_strPropPath = propPath;  }
  
  
} // end VwResourceStore
