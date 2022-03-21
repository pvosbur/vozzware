/*
 ============================================================================================
 
 Copyright(c) 2000 - 2008 by

 i  T e c h n o l o g i e s   C o r p. (Vw)

 All Rights Reserved

 THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
 PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
 CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

 Source Name: VwLineCounter.java

 Create Date: Mar 30, 2008
 ============================================================================================
 */
package com.vozzware.util;

import java.io.File;

public class VwCharCounter
{
  private VwFileFilter m_fileFilter;
  private File[] m_afileSourceDirectories;
  
  public enum Options {TOTAL, DIR, FILE };
  
  private Options m_eOptions;
  
  private char    m_chCharToCount;
  private long    m_lTotFiles;
  private long    m_lTotChars;
  
  
  public VwCharCounter( File[] afileSourceDirectories, String strFileExtFilter, Options eOptions, char chCharToCount )
  {
    m_afileSourceDirectories = afileSourceDirectories;
    m_fileFilter = new VwFileFilter( strFileExtFilter, true, true );
 
    m_eOptions = eOptions;
    m_chCharToCount = chCharToCount;
  }
  
  public void count() throws Exception
  {
    for ( int x = 0; x < m_afileSourceDirectories.length; x++ )
    {
      File fileDir = m_afileSourceDirectories[ x ];
      countFilesAndChars( fileDir );
      
    }
    
    String strCharType = "lines ";
    
    System.out.println( "Total File(s) : " + m_lTotFiles + " Total " + strCharType + " found: " + m_lTotChars );
  } // end count
  
  private void countFilesAndChars( File fileDir ) throws Exception
  {
    File[] aFiles = fileDir.listFiles( m_fileFilter  );
    for ( int x = 0; x < aFiles.length; x++ )
    {
      if ( aFiles[ x ].isDirectory() )
      {
        countFilesAndChars( aFiles[ x ] );
        continue;
      }
      
      ++m_lTotFiles;
      
      File fileToRead = aFiles[ x ];
      
      String strContents = VwFileUtil.readFile( fileToRead );
      long lCharCount = VwExString.count( strContents, m_chCharToCount );
      if ( m_eOptions == m_eOptions.FILE )
        System.out.println( "File : " + fileToRead.getName() + " count : " + lCharCount );
      
      m_lTotChars += lCharCount;
      
    } // end for
    
  } 

  /**
   * @param args
   */
  public static void main( String[] astrArgs )
  {
    String strFileExtFilter = null;
    String strDirsToSearch = null;
    Options eOption = null;
    
    char chCharToCount = '\n';    // default is the new line character
    
    if ( astrArgs.length < 1 )
    {
      showFormat();
      System.exit( -1 );
    }
    
    // arg 0 is always the starting directory(s) to search
    strDirsToSearch = astrArgs[ 0 ];
    
    for ( int x = 1; x < astrArgs.length; x++ )
    {
      String strArg = astrArgs[ x ];
      if ( strArg.startsWith( "-" ))
        strArg = strArg.substring( 1 );
      
      switch( strArg.charAt( 0 ) )
      {
         case 't':
           
              if ( !testOption( eOption ))
                System.exit( 1 );
              
              eOption = Options.TOTAL;
              break;
              
         case 'd':
              if ( !testOption( eOption ))
                System.exit( 1 );
           
              eOption = Options.DIR;
              break;
              
         case 'f':
              if ( !testOption( eOption ))
               System.exit( 1 );
            
              eOption = Options.FILE;
              break;

         case 'e':
           
              strFileExtFilter = astrArgs[ ++x ];
              break;

      } // end switch()
        
    } // end for()
    
   
    if ( eOption == null )
      eOption = Options.TOTAL;
    
    VwDelimString dlmsSrc = new VwDelimString( strDirsToSearch );
    String[] astrDirsToSearch = dlmsSrc.toStringArray();
    File[] aFilesToSearch = new File[ astrDirsToSearch.length ];
    for ( int x = 0; x < aFilesToSearch.length; x++ )
      aFilesToSearch[ x ] = new File( astrDirsToSearch[ x ] );
        
    VwCharCounter charCtr = new VwCharCounter( aFilesToSearch, strFileExtFilter, eOption, chCharToCount );
    
    try
    {
      charCtr.count(); 
      
    }
    catch( Exception ex )
    {
      System.out.println( ex.toString() );
    }
  }

  private static boolean testOption( Options option )
  {
    if ( option == null )
      return true;
    
    System.err.println( "Duplicate option specified, Option " + option.name() + " already specified");
    
    return false;
  }
  private static void showFormat()
  {
    System.out.println( "VwCharCounter <Directory Search List> -e File extension filter list" );
    
  }

}
