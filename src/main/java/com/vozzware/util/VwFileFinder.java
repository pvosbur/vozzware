/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwFileFinder.java

============================================================================================
*/

package com.vozzware.util;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Class to find files in directories or archives
 * @author P. VosBurgh
 *
 */
public class VwFileFinder
{

  private boolean       m_fIncludeSubDirs;
  private boolean       m_fIncludeArchives;
  private boolean       m_fSearchArchivesOnly;

  private int           m_nFilesSearched = 0;
  private int           m_nDotCount = 0;
  private long          m_lDirsSearched = 1;
  private long          m_lFilesSearched = 0;
  private int           m_nArchivesSearched = 0;

  private PrintWriter   m_printWriter; // Displaying results

  private VwFileFilter m_filter;

  private String        m_strSearchFiles;

  private File          m_fileStart;  // Staring directory

  private String        m_strArchivePath;

  private List<String>  m_listFoundFiles = new ArrayList<String>();

  /**
   * Constructor
   * @param strPath The starting path to begin the search
   * @param strSearchFiles The files or wildcards to search for
   * @param strArchivePath The path within an archive to include for matches (if not null)
   * @param fIncludeSubDirs if true, include sub directories
   * @param fIncludeArchives if true, search inside archives
   * @param outStream Output stream for displaying search results
   * @throws Exception
   */
  public VwFileFinder( String strPath, String strSearchFiles, String strArchivePath,
                       boolean fIncludeSubDirs, boolean fIncludeArchives, boolean fSearchArchivesOnly,
                       OutputStream outStream ) throws Exception
  {

    m_printWriter = new PrintWriter( outStream );

    if ( strPath.endsWith( File.separator) )
      strPath = strPath.substring( 0, strPath.length() - 1 );

    m_fileStart = new File( strPath );

    if ( !m_fileStart.exists() )
    {
      m_printWriter.println(  "Directory: " + strPath + " does not exist" );
      System.exit( 1 );

    }

    m_strArchivePath = strArchivePath;
    m_fIncludeSubDirs = fIncludeSubDirs;
    m_fIncludeArchives = fIncludeArchives;
    m_fSearchArchivesOnly = fSearchArchivesOnly;
    m_strSearchFiles = strSearchFiles;
    m_filter = new VwFileFilter( strSearchFiles, true, false );

  } // end VwSqlStatement()


  /**
   * Invloke the finder
   */
  public void find() throws Exception
  {
    long lMiisecsStartTime = System.currentTimeMillis();

    m_printWriter.println( "Searching for the following File(s) : " + m_strSearchFiles );
    m_printWriter.println( "Starting at directory: " + m_fileStart.getAbsolutePath() );

    if ( m_fSearchArchivesOnly )
      m_printWriter.println( "Only .jar, .Zip, .war and .ear archives will be searched");
    else
    {
      if ( m_fIncludeSubDirs )
        m_printWriter.println( "Sub directories will be searched ");

      if ( m_fIncludeArchives )
        m_printWriter.print( "and archives .zip, .jar, .war, .ear will be searched\n" );

    }

    m_printWriter.println( "\nStarting search now, Please Wait...\n"  );
    m_printWriter.flush();

    File[] aFiles = m_fileStart.listFiles();

    if ( aFiles.length == 0 )
    {
      m_printWriter.println( "No matching entries found");
      System.exit( 1 );
    }

    // Perform the search
    search( m_fileStart, aFiles );

    long lMilliSecsTotTime = System.currentTimeMillis() - lMiisecsStartTime;

    m_printWriter.println( "\n" + m_lFilesSearched + " file(s) were searched in " + m_lDirsSearched + " directories");
    m_printWriter.println( "" + m_nArchivesSearched + " archives were searched" );

    m_printWriter.println( "Search took " + VwExString.milliSecsToTime( true, lMilliSecsTotTime ) + " to complete");

    if ( m_listFoundFiles.size() == 0 )
    {
      m_printWriter.println( "Did not find any of the file(s) requested" );

    }
    else
    {
      m_printWriter.println( "Found " + m_listFoundFiles.size() +  " file(s), printing results:\n" );

      for ( String strFoundEntry : m_listFoundFiles )
        m_printWriter.println( strFoundEntry );

    }

    m_printWriter.flush();
    System.exit( ( 0 ) );


  } // end Find()


  /**
   * Search for the request files
   * @param aFiles
   */
  private void search( File fileDir, File[] aFiles ) throws Exception
  {

    if ( aFiles == null )
      return;


    for ( int x = 0; x < aFiles.length; x++ )
    {

      // Show that we are still alive
      if ( ++m_nFilesSearched > 10000 )
      {
        if ( ++m_nDotCount > 120 )
        {
          m_printWriter.print( "\n");
          m_nDotCount = 0;
        }

        m_nFilesSearched = 0;
        m_printWriter.print( ".");
        m_printWriter.flush();
      }

      File aFile = aFiles[ x ];


      if ( aFile.isDirectory() && m_fIncludeSubDirs )
      {
        ++m_lDirsSearched;
         search( aFile, aFile.listFiles() );
      }
      else
      {
        ++m_lFilesSearched;
        String strName = aFile.getName().toLowerCase();
        boolean fIsArchive = false;

        if ( m_fIncludeArchives || m_fSearchArchivesOnly )
          fIsArchive = isArchiveFile( strName );

        if ( m_fSearchArchivesOnly && !fIsArchive )
            continue;

        if ( fIsArchive )
        {
          try
          {
            lookInArchive( fileDir, aFile );
          }
          catch( Exception ex )
          {
            m_printWriter.println( "Error accessing archive file : " + aFile.getAbsolutePath() + "\nException " );
            ex.printStackTrace( m_printWriter );
          }
        }
        else
        if ( m_filter.accept( fileDir, strName ) )
        {
          m_listFoundFiles.add( aFile.getAbsolutePath() );
        }
      }
    } // end for
  }


  /**
   * Check to see if file name ends with .zip, .jar, .war or .ear
   * @param strName The name of the file
   * @return
   */
  private boolean isArchiveFile( String strName )
  {

    int nPos = strName.lastIndexOf( '.' );

    if ( nPos < 0 )
      return false;  // not an archive extension

    String strExt = strName.substring( ++nPos  ).toLowerCase();

    if ( strExt.equals( "jar" ) || strExt.equals( "zip" ) || strExt.equals( "war" ) || strExt.equals( "ear" ) )
      return true;

    return false;

  }


  /**
   * Process archive
   * @param fileDir
   * @param aFile
   * @throws Exception
   */
  private void lookInArchive( File fileDir, File aFile ) throws Exception
  {
    ++m_nArchivesSearched;

    JarFile jarFile = new JarFile( aFile );
    Enumeration enumEntries = jarFile.entries();

    while( enumEntries.hasMoreElements() )
    {
      JarEntry je = (JarEntry)enumEntries.nextElement();
      String strPath = je.getName();
      String strName = null;

      int nPos = strPath.lastIndexOf( '/');
      if ( nPos >= 0 )
      {
        strName = strPath.substring( nPos + 1);
        strPath = strPath.substring( 0, nPos );
      }
      else
        strName = strPath;



      if ( strName.length() > 0 && m_filter.accept( aFile, strName ) )
      {
        if ( m_strArchivePath != null )
        {
          if ( !m_strArchivePath.equalsIgnoreCase( strPath ) )
            continue;

        }

        String strFound = "Directory: " + fileDir.getAbsolutePath() + " ==> Archive: " + aFile.getName() + " File: " + je.getName();
        m_listFoundFiles.add( strFound );

      }
    }
  }


  /**
   * Program entry point for command line use
   * @param args
   */
  public static void main( String[] args )
  {


    // Get search criteria

    String strPath = null;
    String strSearch = null;
    boolean fIncludeSubDirs = false;
    boolean fIncludeArchives = false;
    boolean fSearchArchivesOnly = false;

    for ( int x = 0; x < args.length; x++ )
    {
      String strArg = args[ x ].toLowerCase();

      if ( strArg.equals( "-p" ) )
        strPath = args[ ++x ];
      else
      if ( strArg.equals( "-f" ) )
        strSearch = args[ ++x ];
      else
      if ( strArg.equals( "r" )  || strArg.equals( "-r" ))
        fIncludeSubDirs = true;
      else
      if ( strArg.equals( "ia" )  || strArg.equals( "-ia" ))
        fIncludeArchives = true;
      else
      if ( strArg.equals( "ao" )  || strArg.equals( "-ao" ))
        fSearchArchivesOnly = true;
      else
      {
        System.out.println( "Invalid argument: " + strArg );
        showFormat();
        System.exit( 1 );
      }

    } // end for()


    if ( strPath == null )
    {
      System.out.println( "Missing starting path to search" );
      showFormat();
      System.exit( 1 );
    }

    if ( strSearch == null )
    {
      System.out.println( "Missing file search criteria" );
      showFormat();
      System.exit( 1 );
    }

    try
    {

      VwFileFinder finder = new VwFileFinder( strPath, strSearch, null, fIncludeSubDirs,
                                              fIncludeArchives, fSearchArchivesOnly, System.out );

      finder.find();
    }
    catch( Exception ex )
    {
      ex.printStackTrace();
      System.exit( 1 );
    }
  } // end main()


  /**
   * Show required format
   */
  private static void showFormat()
  {
    System.out.println( "Format: VwFileFinder -p Starting directory path\n" +
                        "                  -f comma separated list files or wildcards to search for\n" +
                        "                  -r Include sub directories\n" +
                        "                  -ia Search inside standard archives zip,jar,war and ear files" +
                        "                  -ao Only Search inside standard archives zip,jar,war and ear files" );
  }
} // end class VwFileFinder{}

// *** End of VwFileFinder.java ***

