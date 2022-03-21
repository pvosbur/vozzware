/*
===========================================================================================

 
                             Copyright(c) 2000 - 2005 by

                      V o z z W a r e   L L C (Vw)

                             All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.


Source Name: VwDos2Unix.java

============================================================================================
*/
package com.vozzware.util;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

/**
 * This class strips out carriage return characters and writes the results to stdout
 * @author P. VosBurgh
 *
 */
public class VwDos2Unix
{

  /**
   * @param args
   */
  public static void main( String[] args )
  {
    if ( args.length == 0 )
    {
      showArgs();
      System.exit( 1 );
    }
    
    String strFileName = args[ 0 ];
    
    int nPos = strFileName.lastIndexOf( "\\");
    if ( nPos < 0 )
      nPos = strFileName.lastIndexOf( "/");
    
    String strPath = "";
    String strName = null;
    
    if ( nPos >= 0 )
    {
      strPath = strFileName.substring( 0, ++nPos );
      strName = strFileName.substring( nPos );
    }
    else
      strName = strFileName;
    
    
    File fileOrig = new File( strFileName );
      
    if ( !fileOrig.exists() )
    {
      System.out.println("File : '" + strFileName + "' does not exist");
      System.exit( 1 );
        
    }
      
    File fileTemp = new File( strPath + "temp__" + strName );
    if ( fileTemp.exists() )
      fileTemp.delete();
    
    try
    {
      FileReader fr = new FileReader(  fileOrig );
      char[] ach = new char[ 16384 ];
      FileWriter fw = new FileWriter( fileTemp );
      
      while ( true )
      {
        int nGot = fr.read( ach );
        String strLine = new String( ach, 0, nGot );
        strLine = VwExString.replace( strLine, "\r", (String)null );
        fw.write( strLine );
        
        if ( nGot < ach.length )
          break;
        
      }
      
      fw.close();
      
      fr.close();
      
      fileOrig.delete();
      fileTemp.renameTo( fileOrig );
      
          
    }
    catch( Exception ex )
    {
      ex.printStackTrace();
        
    }
      
  }
  
    private static void showArgs()
    {
      System.out.println("Format: VwDos2Unix <input File name>");
      
    }
}

