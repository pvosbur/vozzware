/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwXmlFileConfig.java

Create Date: Apr 11, 2006
============================================================================================
*/
package com.vozzware.xml;

import com.vozzware.util.VwDelimString;
import com.vozzware.util.VwExString;
import com.vozzware.util.VwResourceStoreFactory;
import org.xml.sax.InputSource;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * This is a helper class that parses an xml file into an VwDataObject.
 * The path of the xml file is determined by the following rules:
 * 1. The property ITCDOCS is first looked at to determine the location of the xml file
 * 2. If the ITCDOCS property is not defined the current directory of the running application
 * is used.
 */
public class VwXmlFileConfig
{

  /**
   * Gets the xml file parsed into an VwDataObject. Parent tags are represented by their
   * own VwDataObjects. Duplicate tags (if allowed) are represented by either VwDataObjList
   * objects (if they are themselves parent tags ) or an VwElelmentList objects if they
   * are data tags.
   *
   * @param strXmlFileName The name of the xml file to parse (without the path )
   * @param fAllowDups if true allow duplicate tags else throw an exception
   */
  public static VwDataObject get( String strXmlFileName, boolean fAllowDups ) throws Exception
  {

    int nPos = strXmlFileName.lastIndexOf( File.separator );
    File fileConfig = null;
    String strDocPath = null;
    URL urlDoc = null;
    
    // If we already have a path, then use that, else try to find the path from ITCDOCS loc
    if ( nPos >= 0 )
      fileConfig = new File( strXmlFileName );
    
    if ( fileConfig == null )
    {
      
      urlDoc = VwResourceStoreFactory.getInstance().getStore().getDocument( strXmlFileName );
      
      if ( urlDoc == null ) // see if the ITCDOCS (old way was specified)
      {
        
        strDocPath = System.getProperty( "ITCDOCS" );

        // If doc path property is not specified, assume current directory as last effort
        if ( strDocPath == null )
        {
          // try to get it from meta-inf
        
          urlDoc = Thread.currentThread().getContextClassLoader().getResource( "META-INF/" + strXmlFileName );
        
          if ( urlDoc == null )
          {
            File fileCurDir = new File( "." );
            strDocPath = fileCurDir.getAbsolutePath();
          }
        }

        if ( urlDoc == null )
        {
          VwDelimString dlmsDocPath = new VwDelimString( ";", strDocPath );
  
          String[] astrPaths = dlmsDocPath.toStringArray();
  
         // Try to find path from ITCDOCS location(s)
         for ( int x = 0; x < astrPaths.length; x++ )
         {
           String strPath = astrPaths[ x ];
  
           if ( !strPath.endsWith( File.separator )  )
             strPath += File.separator;
  
            strPath += strXmlFileName;
  
            fileConfig = new File( strPath );
  
            if ( fileConfig.exists() )
              break;         // Look at next path
  
            fileConfig = null;
  
          } // end for()
  
        } // end if ( fileConfig == null )
        
      } // end if ( urlDoc == null )  

    }
    
    if ( fileConfig != null || urlDoc != null )
    {
      if ( fileConfig != null )
        urlDoc = fileConfig.toURL();
    
      VwXmlToDataObj xtd = new  VwXmlToDataObj();
      xtd.makeDataObjectsForParentTags();
      xtd.setAllowDupValues( fAllowDups );
  
      // Parse the drivers file
      return xtd.parse( new InputSource( urlDoc.openStream() ), false );

    }
    
    // Not Found
    String strErrMsg = ResourceBundle.getBundle( "resources.properties.xmlmsgs" ).getString( "Vw.Xml.MissingDocs" );
    strErrMsg = VwExString.replace( strErrMsg, "%1", strXmlFileName );
    throw new Exception( strErrMsg );

  } // end get()

} // end class VwXmlFileConfig{}

// *** End of VwXmlFileConfig.java ***
