/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwDocFinder.java

============================================================================================
*/

package com.vozzware.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Iterator;

/**
 * This class locates a document by getting a path list from a user define property and
 * searching the path entries until the document is found
 * @author P. VosBurgh
 *
 */
public class VwDocFinder
{

  /**
   * Finds a document using the specified path list property and returns a URL to the document
   * 
   * @param strPropName The name of the proprty defining the path list 
   * @param strDocName The name of the document to find
   * @return A URL to the document found
   * @throws Exception if the property is not found
   * @throws FileNotFoundException if the document cannot be found
   */
  public static URL findURL( String strPropName, String strDocName )
  { return findURLClassPath( strPropName, strDocName ); }
  
  /**
   * Finds a document using the default ITCDOCS path list property and returns a URL to the document
   * 
   * @param strDocName The name of the document to find
   * @return A URL to the document found
   * @throws Exception if the property is not found
   * @throws FileNotFoundException if the document cannot be found
   */
  public static URL findURL( String strDocName )
  { return findURLClassPath( "VWDOCS", strDocName ); }

  /**
   * Finds a document using the default ITCDOCS path list property and returns a File object to the document
   * 
   * @param strDocName The name of the document to find
   * @return A File object to the found document
   * @throws Exception if the property is not found
   * @throws FileNotFoundException if the document cannot be found
   */
  public static File find( String strDocName ) throws Exception
  { return find( "ITCDOCS", strDocName ); }
  
  /**
   * Finds a document using the specified path list property and returns a File object to the document
   * 
   * @param strPropName The name of the proprty defining the path list 
   * @param strDocName The name of the document to find
   * @return A File object to the found document
   * @throws Exception if the property is not found
   * @throws FileNotFoundException if the document cannot be found
   */
  public static File find( String strPropName, String strDocName ) throws Exception
  {
    
    if ( strPropName == null )
      return findInClassPath( strDocName );
    
    String strPaths = System.getProperty( strPropName );
    if ( strPaths == null )
      return findInClassPath( strDocName );
    
    VwDelimString dlmsPaths = new VwDelimString( File.pathSeparator, strPaths );
    
    for ( Iterator<String> iPaths = dlmsPaths.iterator(); iPaths.hasNext(); )
    {
      String strPath = URLDecoder.decode( iPaths.next(), "UTF-8" );

      File fileDoc = new File( strPath + File.separatorChar + strDocName );
      if ( fileDoc.exists() )
        return fileDoc;
      
    }

    File fileDoc = findInClassPath( strDocName );

    if ( fileDoc != null )
      return fileDoc;
    
    throw new FileNotFoundException( strDocName );
    
  } // end find()

  private static File findInClassPath( String strDocName )
  {
    ClassLoader ldr = Thread.currentThread().getContextClassLoader();
    
    URL urlFile = ldr.getResource( strDocName );
    
    if ( urlFile != null )
    {
      try
      {
        return new File( URLDecoder.decode( urlFile.getPath(), "UTF-8" ) );
      }
      catch( Exception ex )
      {
        return new File( urlFile.getPath() );
      }
    }
    urlFile = ClassLoader.getSystemClassLoader().getResource( strDocName );
    if ( urlFile != null )
    {
      try
      {
        return new File( URLDecoder.decode( urlFile.getPath(), "UTF-8" ) );
      }
      catch( Exception ex )
      {
        return new File( urlFile.getPath() );
      }

    }
    return null;
  }

  private static URL findURLClassPath( String strPropName, String strDocName ) //throws Exception
  {
    ClassLoader ldr = Thread.currentThread().getContextClassLoader();
    
    URL urlFile = ldr.getResource( strDocName );
    
    if ( urlFile == null )
      urlFile = ClassLoader.getSystemClassLoader().getResource( strDocName );
    
    if ( urlFile != null )
    {
      try
      {
        String strAbsolute = URLDecoder.decode( urlFile.toString(), "UTF-8" );
        return new URL( strAbsolute );
        
      }
      catch( Exception ex )
      {
        return urlFile;
      }
    }
    if ( strPropName == null )
      return null;
    
    String strPaths = System.getProperty( strPropName );
    if ( strPaths == null )
      return null;
    
    VwDelimString dlmsPaths = new VwDelimString( File.pathSeparator, strPaths );
    
    for ( Iterator<String>iPaths = dlmsPaths.iterator(); iPaths.hasNext(); )
    {
      try
      {
        String strPath = URLDecoder.decode( iPaths.next(), "UTF-8" );
        File fileDoc = new File( strPath + File.separatorChar + strDocName );
        if ( fileDoc.exists() )
          return fileDoc.toURL();
      }
      catch( Exception ex )
      {
        ex.printStackTrace();
        return null;
      }
    }
    
    return null;
  }
  
} // end class VwDocFinder{}

// *** End of VwDocFinder.java ***

