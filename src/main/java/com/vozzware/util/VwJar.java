/*
  ===========================================================================================


                 V o z z  W o r k s  F r a m e W o r k  L i b r a r i e s

                              Copyright(c) 2000 - 2011 by

                                 V o z z w a r e   L L C

                              All Rights Reserved


  Source Name:  VwJar.java


  ============================================================================================
*/

package com.vozzware.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.ResourceBundle;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

/**
 * @(#) VwJar.java
 * This class is a sophisticated jar/zip/war builder
 *
 */
public class VwJar
{

  private  List<File>  m_listFiles;
  private  String m_strArchiveFileName;
  private  String     m_strManifestFileName;    // Name of the archive file
  private  String[]   m_astrBaseDir;            // An array of base (parent) directories to start from
  private  String[]   m_astrPkgNames;           // An array of package names to jar
  private  String[]   m_astrFilterList;          // List of file types to include
  private  String     m_strJarPathStart;        // -B option start all jar entries with this path if not null
  private  int        m_nCompressionLevel;      // The compression level to use when archiving

  private  boolean    m_bRecurseDir = false;    // Recurse sub directories
  private  boolean    m_bCreate = false;
  private  boolean    m_bIgnoreCaseForFilters = false;
  private  boolean    m_bShowContents = false;
  private  boolean    m_bXtract = false;
  private  boolean    m_bUpdate = false;
  private  boolean    m_bVerbose = false;
  private  boolean    m_bRecursePkg = false;

  private  byte[]     m_abtBuff = null;

  private  JarOutputStream m_jos;                 // Jar output stream

  private  ResourceBundle  m_msgs = ResourceBundle.getBundle( "resources.properties.vwutil" );


  /**
   * Default constructor for command line version
   */
  public VwJar()
  { super(); }

  /**
   * Constructs JarOutputStream form File path
   * @param fileJar The file path to create an empty jar from
   */
  public VwJar( File fileJar, Manifest man ) throws Exception
  {
    if ( man != null )
    {
      m_jos = new JarOutputStream( new FileOutputStream( fileJar), man );
    }
    else
    {
      m_jos = new JarOutputStream( new FileOutputStream( fileJar) );
    }


  } // end VwJar()


  /**
   * Execute the jar request
   *
   * @param astrArgs The command line args
   */
  public void exec( String[] astrArgs ) throws Exception
  {

    if ( !parseArgs( astrArgs ) )
    {
      return;
    }

    if ( m_bVerbose )
    {
      System.out.println( "Executing VwJar for arguments: " + VwExString.toString( astrArgs ));
    }

    if ( m_bCreate || m_bUpdate )
    {


      FileOutputStream fo;
      if ( m_listFiles != null )
      {
        fo = new FileOutputStream( m_listFiles.get( 0 ), m_bUpdate );
      }
      else
      {
        fo = new FileOutputStream(m_astrBaseDir[0] + File.separator + m_strArchiveFileName );

      }

      m_jos = new JarOutputStream( fo );

      m_jos.setLevel( m_nCompressionLevel );

      makeArchive();
      m_jos.close();

    }
    else
    {
      // We're reading an archive file

      if ( m_bShowContents )
      {
        displayJarFileContents();
      }
      else
      if ( m_bXtract )
      {
        extractArchive();
      }

    } // end else

  } // end exec



  /**
   *  Make an archive file based on the options specified
   */
  private void makeArchive() throws Exception
  {

    for ( int x = 0; x < m_astrBaseDir.length; x++ )
    {
      String strBaseDir = m_astrBaseDir[ x ];

      // Convert file separators to the proper file system

      if ( File.separatorChar == '/' )
      {
        strBaseDir = strBaseDir.replace( '\\', '/' );
      }
      else
      {
        strBaseDir = strBaseDir.replace( '/', '\\' );
      }


      // Make sure base dir ends with the separator
      if ( !strBaseDir.endsWith( File.separator ) )
      {
        strBaseDir += File.separator;
      }

      if ( m_astrPkgNames != null )
      {
        doPkgList( strBaseDir );
      }
      else
      {
        doFileList( strBaseDir );
      }

    }

  } // end makeArchive


  /**
   * Make archive file from a list of package names
   */
  private void doPkgList( String strBaseDir ) throws Exception
  {
    String strFileFilterList = null;

    for ( int x = 0; x < m_astrPkgNames.length; x++ )
    {

      String strPackage = m_astrPkgNames[ x ];

      if ( strPackage.equals( "*" ) )
      {
        strFileFilterList = "*.class,*.gif,*.xml,*.prop*";
        doRecursePkg( strBaseDir, null );
        return;
      }

      int nPos = strPackage.indexOf( ':' );

      // If no filter list specified, then use the package default list
      if ( strFileFilterList == null )
      {
        strFileFilterList = "*.class,*.gif,*.xml,*.prop*";
      }

      if ( m_bVerbose )
      {
        String strMsg = m_msgs.getString( "VwUtil.AddPkg" );
        strMsg = VwExString.replace( strMsg, "$1", strPackage );
        strMsg = VwExString.replace( strMsg, "$2", m_listFiles.get( 0 ).getAbsolutePath() );
        System.out.println( "\n" + strMsg + ":\n" );

      }

      String strDir = strPackage.replace( '.', File.separatorChar );

      // Make complete directory
      File file = new File( strBaseDir + strDir );

      File[] aFiles = file.listFiles( new VwFileFilter( strFileFilterList ) );

      if ( aFiles == null )
      {
        throw new Exception( "Cannot find directory or file for : " + strBaseDir + strDir );

      }

      addJarFiles( strBaseDir, aFiles );

    } // end for ( x.. )


  } // end doPkgList()


  /**
   * Make archive file from a list of file types
   */
  private void doFileList( String strBaseDir ) throws Exception
  {

    // Make complete directory
    File file = new File( strBaseDir );
    String strFilterList = null;

    if ( m_astrFilterList == null )
    {
      strFilterList = "*.*";
    }
    else
    {
      strFilterList = m_astrFilterList[ 0 ];
    }

    if ( strFilterList.equals( "." ) || strFilterList.equals( "*" ) )
    {
      strFilterList = "*.*";
    }

    VwFileFilter ff = new VwFileFilter( strFilterList, true, m_bRecurseDir );
    File[] aFiles = file.listFiles( ff );

    System.out.println("Getting file list for :" + file.getAbsolutePath() + " and got " + aFiles.length + " files");
    if ( aFiles == null )
    {
      throw new Exception( "Cannot find directory or file for : " + strBaseDir );

    }

    addJarFiles( strBaseDir, aFiles );

  } // end doFileList()


  /**
   * Handles the recursive package option
   */
  private void doRecursePkg( String strBaseDir, String strPath ) throws Exception
  {

    String strActualDir = strBaseDir;

    if ( strPath != null )
    {
      strActualDir = strPath;
    }

    // Make complete directory
    File file = new File( strActualDir );

    File[] aFiles = file.listFiles( new VwFileFilter( m_astrFilterList[ 0 ], false, true ) );

    if ( aFiles == null )
    {
      throw new Exception( "Cannot find directory or file for : " + strBaseDir );

    }

    addJarFiles( strBaseDir, aFiles );

  } // end doRecursePkg()

 /**
  * Adds a JArEntry and contents to the jar file
  *
  * @param jarEntry The JarEntry
  * @param abData The data contents of the entry
  * @throws Exception
  */
  public void addEntry( JarEntry jarEntry, byte[] abData ) throws Exception
  {
    if ( m_jos == null )
    {
      throw new Exception( m_msgs.getString( "VwJar.NullJOS") );
    }

    m_jos.putNextEntry( jarEntry );
    m_jos.write( abData );

  } // end addEntry()


  /**
   * Close the JAROutputStream
   * @throws Exception
   */
  public void close()  throws Exception
  {
    if ( m_jos == null )
    {
      throw new Exception( m_msgs.getString( "VwJar.NullJOS") );
    }

    m_jos.close();

  } // close the stream


  /**
   * Adds a entry to the jar file
   *
   * @param strBaseDir The base directory i.e the classpath
   * @param aFiles An array of files to add
   */
  private void addJarFiles( String strBaseDir, File[] aFiles ) throws Exception
  {
    byte[] abtIn = new byte[ 65535 ];  // Buffer to read in the file

    for ( int x = 0; x < aFiles.length; x++ )
    {
      File file = aFiles[ x ];

      if ( file.isDirectory() )
      {
        if ( m_bRecursePkg )
        {
          doRecursePkg( strBaseDir, file.getCanonicalPath() );
        }
        else
        {
          doFileList( file.getCanonicalPath() );
        }
        continue;
      }

      FileInputStream  fi = new FileInputStream( file );

      int nAvail = fi.available();

      if ( nAvail > abtIn.length )
      {
        abtIn = null;
        abtIn = new byte[ nAvail ];
      }

      fi.read( abtIn, 0, nAvail );

      String strFile = file.getPath();

      if ( m_bRecurseDir || m_bRecursePkg )
      {

        int nOffset = m_astrBaseDir[ 0 ].charAt( 1 ) == ':' ? 2 : 0;

        if ( nOffset == 0 && strFile.charAt( 1 ) == ':' )
        {
          strFile = strFile.substring( m_astrBaseDir[ 0 ].length()+ 2  );
        }
        else
        {
          strFile = strFile.substring( m_astrBaseDir[ 0 ].length() );
        }
      }
      else
      {
        strFile = strFile.substring( strBaseDir.length() );
      }

     // Strip off base path from actual path
     if ( m_strJarPathStart != null )
     {
       strFile = VwExString.remove( strFile, m_strJarPathStart );
     }

      if ( strFile.startsWith( File.separator ) )
      {
        strFile = strFile.substring( 1 );
      }

      if ( File.separatorChar != '/' )
      {
        strFile = VwExString.replace( strFile, "\\", "/" );
      }

      JarEntry ze = new JarEntry( strFile );
      ze.setTime( file.lastModified() );
      ze.setSize( nAvail );

      m_jos.putNextEntry( ze );
      m_jos.write( abtIn, 0, nAvail );

      if ( m_bVerbose )
      {
        String strMsg = m_msgs.getString( "VwUtil.AddFile" );
        strMsg = VwExString.replace( strMsg, "$1", file.getName() );

        if ( m_listFiles != null )
        {
          strMsg = VwExString.replace( strMsg, "$2", m_listFiles.get( 0).getAbsolutePath() );
        }
        else
        {
          strMsg = VwExString.replace( strMsg, "$2", m_strArchiveFileName );

        }
        System.out.println( strMsg  );

      }

    } // end for()


  } // end addJarFiles()


  /**
   * Display the contents of the jar file(s) to standard out
   */
  private void displayJarFileContents() throws Exception
  {

    VwNameWildcardMatcher matcher = null;

    if ( m_astrFilterList != null )
    {
      if ( m_bVerbose )
      {
        System.out.println( "Appling filter(s): " + VwExString.toString( m_astrFilterList ));
      }

      matcher = new VwNameWildcardMatcher( m_astrFilterList );
    }

    if ( m_listFiles == null )
    {
      if ( m_strArchiveFileName == null )
      {
        throw new Exception( "argument list is invalid for displaying jar file contents as not files were named");
      }

      m_listFiles = new ArrayList<File>();
      m_listFiles.add( new File( m_strArchiveFileName ));
    }
    int nOutputCount = 0;

    for ( File file : m_listFiles )
    {
      String strDisplayJarFileName = null;

      String strJarName = file.getAbsolutePath();

      if ( m_bVerbose )
      {
        System.out.println( "processing jar file: " + strJarName );
      }
      JarFile jf = new JarFile( strJarName );

      Enumeration e = jf.entries();


      while ( e.hasMoreElements() )
      {
        JarEntry je = (JarEntry) e.nextElement();

        String strLine = null;

        String strName = je.getName();

        if ( m_bVerbose )
        {
          System.out.println( "processing jar entry: " + strName );
        }
        
        String strDir = "";

        VwDate date = new VwDate( new Date( je.getTime() ) );

        int nPos = strName.lastIndexOf( "/" );

        if ( nPos >= 0 )
        {
          strDir = strName.substring( 0, nPos + 1 );
          strName = strName.substring( nPos + 1 );
        }

        if ( matcher != null )
        {
          int nPos1 = strName.lastIndexOf( "." );

          if ( nPos1 > 0 )
          {
            if ( !matcher.hasMatch( strName.substring( 0, nPos1 ) ) )
            {
              continue;
            }
          }
          else
          {
            continue;
          }
        }

        if ( strDisplayJarFileName == null )
        {
          strDisplayJarFileName = strJarName;
          System.out.println( "\nDisplaying entries for jar file: " + strJarName + "\n" );
        }

        ++nOutputCount;

        strLine = "  ";
        strLine += VwFormat.left( strName, 50, ' ' );
        strLine += VwFormat.left( date.format( "%m/%d/%y %H:%M:%S" ), 20, ' ' );
        strLine += VwFormat.right( String.valueOf( je.getSize() ), 10, ' ' );
        strLine += "    " + strDir;

        System.out.println( strLine );

      } // end while

      jf.close();

    } // end ( for ( File..

    if ( nOutputCount == 0 )
    {
      if ( m_astrFilterList.length > 1 )
      {
        String strFilters = "";

        for ( int x = 0; x < m_astrFilterList.length; x++ )
        {
          if ( strFilters.length() > 0 )
          {
            strFilters +=", ";
          }

          strFilters += m_astrFilterList[ x ];

        }
        
        System.out.println( "Nothing found for filters: " + strFilters );

      }
      else
      {
        System.out.println( "Nothing found for filter: " + m_astrFilterList[ 0 ]);
      }
    }

  } // end displayArchive()



  /**
   * Extract the contentsof the archive file
   */
  private void extractArchive() throws Exception
  {

    JarFile jf = new JarFile(m_listFiles.get( 0 ).getAbsolutePath() );

    Enumeration e = jf.entries();

    String strPath = null;

    m_abtBuff = new byte[ 8096 ];

    if ( m_astrBaseDir != null )
    {
      strPath = m_astrBaseDir[ 0 ] + File.separator;
    }
    else
    {
      File curDir = new File( "." );
      strPath = curDir.getCanonicalPath() +  File.separator;
    }

    strPath = strPath.replace( '/', File.separatorChar );

    VwFileFilter fileFilter = null;

    if ( m_astrFilterList != null )
    {
      fileFilter = new VwFileFilter( m_astrFilterList[ 0 ] );
    }

    while ( e.hasMoreElements() )
    {
      JarEntry je = (JarEntry)e.nextElement();

      String strLine = null;

      String strName = je.getName();
      String strDir = "";

      int nPos = strName.lastIndexOf( "/" );

      if ( nPos >= 0 )
      {
        strDir = strName.substring( 0, nPos + 1 );
        strName = strName.substring( nPos + 1 );
      }

      String strFullPath = strPath + strDir;

      if ( m_astrPkgNames != null )
      {
        for ( int x = 0; x < m_astrPkgNames.length; x++ )
        {
          String strPkgName = m_astrPkgNames[ x ].replace( '.', '/' ) + "/";

          if ( strPkgName.equals( strDir ) )
          {
            writeFile( jf, je, strFullPath, strName );
          } // end if

        } // end for()

      } // end if
      else
      if ( m_astrFilterList != null )
      {

        File curDir = new File( strFullPath );
        if ( fileFilter.accept( curDir, strName ) )
        {
          writeFile( jf, je, strFullPath, strName );
        }

      }
      else
      {
        writeFile( jf, je, strFullPath, strName );
      }

    } // end while

    jf.close();

    m_abtBuff = null;

  } // end extractArchive()


  /**
   * Write out the file from the jar inputstream and create directory(s) if needed
   */
  private void writeFile( JarFile jf, JarEntry je, String strFullPath, String strName ) throws Exception
  {
    strFullPath = strFullPath.replace( '/', File.separatorChar );

    File fileDir = new File( strFullPath );

    if ( !fileDir.exists() )
    {
      fileDir.mkdirs();
    }

    InputStream is = jf.getInputStream( je );
    File newFile = new File( strFullPath + strName );
    newFile.setLastModified( je.getTime() );

    FileOutputStream fo = new FileOutputStream( newFile );

    int nSize = (int)je.getSize();

    if ( nSize > m_abtBuff.length )
    {
      m_abtBuff = null;
      m_abtBuff = new byte[ nSize ];
    }

    int nTot = 0;
    int nNeed = nSize;

    while( true )
    {
      int nRead = is.read( m_abtBuff, nTot, nNeed  );

      nTot += nRead;

      if ( nTot == nSize )
      {
        break;
      }

      nNeed -= nRead;

    }

    fo.write( m_abtBuff, 0, nSize );

    fo.close();
    newFile.setLastModified( je.getTime() );

    is.close();

    if ( m_bVerbose )
    {
      System.out.println( "Writing File: " + newFile.getAbsoluteFile() );
    }

  } // end writeFile()


  /**
   * Process the inital command line flags
   *
   * @param strFlags a string of char settings
   */
  private boolean processInitialFlags( String strFlags )
  {
    for ( int x = 0; x < strFlags.length(); x++ )
    {
      switch( strFlags.charAt( x ) )
      {
        case '-':

             continue;

        case 'v':

             m_bVerbose = true;
             break;

        case 'c':

             m_bCreate = true;

             if ( m_bUpdate || m_bXtract || m_bShowContents )
             {
               System.out.println( "Create option cannot be mixed with t,u or x options\n");
               return false;
             }

             break;

        case 'i':

             m_bIgnoreCaseForFilters = true;
             break;

        case 't':

             m_bShowContents = true;
             if ( m_bUpdate || m_bXtract || m_bCreate )
             {
               System.out.println( "List (t) option cannot be mixed with c,u or x options\n");
               return false;
             }
             break;

        case 'x':

             m_bXtract = true;
             if ( m_bShowContents || m_bUpdate || m_bCreate )
             {
               System.out.println( "Extract (x) option cannot be mixed with c,t or u options\n");
               return false;
             }
             break;

        case 'u':

             m_bUpdate = true;
             if ( m_bShowContents || m_bXtract || m_bCreate )
             {
               System.out.println( "Update (u) option cannot be mixed with c,t or x options\n");
               return false;
             }

             break;


        default:

            return false;     // Invalid option

      } // end swvwh()

    } // end for()

    return true;

  } // end processInitialFlags()


  /**
   * Process File List. The file list may take the following forms:
   *   1.Full path and name of ile ex. /myproject/lib/log4j.jar
   *   2. all jar files in a directory path. ex. /myproject/lib/*
   *   3. multiple single jars separated by commas ex. /myproject/lib/log4j.jar,/myproject/lib/junit.jar
   *   4. multiple jar paths separated by commas ex.   /myproject/lib/*,/distribution/*
   *
   *
   * @param strFileListSpec The file name
   */
  private boolean processFileList( String strFileListSpec )
  {
    String[] astrFileListSpecs = strFileListSpec.split( "," );
    m_listFiles = new ArrayList<>();

    for ( int x = 0; x < astrFileListSpecs.length; x++ )
    {
      String strListSpec = astrFileListSpecs[ x ];

      if ( strListSpec.endsWith( "*" ) )
      {
        File[] aFiles = new File( strListSpec.substring( 0, strListSpec.lastIndexOf( "*" ) - 1 ) ).listFiles( new VwFileFilter( "*.jar" ) );

        for ( int y = 0; y < aFiles.length; y++ )
        {
          m_listFiles.add( aFiles[ y ]);
        }

      }
      else
      {
        m_listFiles.add( new File( strListSpec ));

      }
    }

    return true;

  } // end processInitialFlags()

  /**
   * One or more filter specs delimited by a comma
   * @param strFilterSpec
   * @return
   */
  private boolean processFilter( String strFilterSpec )
  {
    m_astrFilterList = strFilterSpec.split( ",");
    return true;
  }


  /**
   * Process final command line options
   *
   * @param astrOptions The array of command line args
   * @param ndx The starting index where the option parameters start
   */
  private boolean processOptions( String[] astrOptions ) throws Exception
  {
    VwDelimString dlms = null;
    boolean bPackage = false;

    for ( int x = 1; x < astrOptions.length; x++ )
    {
      String strOp = astrOptions[ x ];

      switch( strOp )
      {
        case "-l":

          m_nCompressionLevel = Integer.parseInt( astrOptions[ ++x ] );
          break;

        case "-R":

          m_bRecurseDir = true;
          break;

        case "-f":

             m_strArchiveFileName = astrOptions[ ++x ];
             break;


        case "-jl":

          processFileList( astrOptions[ ++x ] );

          break;

        case "-C":

          m_astrBaseDir = new String[] { astrOptions[ ++x ] };
          if ( m_astrBaseDir[0].equals( "."))
          {
            m_astrBaseDir[0] = new File( ".").getAbsolutePath();

            // remove the .
            m_astrBaseDir[0] =  m_astrBaseDir[0].substring( 0, m_astrBaseDir[0].length() - 1);

          }

          break;

        case  "-B":

          m_bRecurseDir = true;
          m_strJarPathStart = astrOptions[ ++x ];
          m_astrBaseDir  = new String[] {m_strJarPathStart };

          break;

        case "-CPR":

          m_astrBaseDir = new String[] { astrOptions[ ++x ] };
          m_bRecursePkg = true;
          m_astrPkgNames  = new String[] { "*" };

          break;

        case "-P":

          dlms = new VwDelimString( ";", astrOptions[ ++x ] );
          m_astrPkgNames = dlms.toStringArray();

          break;


        case "-mf":

          processFilter( astrOptions[ ++x ] );
          break;

      } // end switch()

     /* todo may still be neededd
       File curDir = new File( "." );

       String strPath = curDir.getCanonicalPath();

       // Strip of drive letter if dos base OS
       int nPos = strPath.indexOf( ':' );

       if ( nPos > 0 )
       {
         strPath = strPath.substring( nPos + 1 );
       }

       m_astrBaseDir = new String[] { strPath };

       dlms = new VwDelimString( ",", astrOptions[ x++ ] );

       for ( ; x < astrOptions.length; x++ )
       {
          dlms.add( astrOptions[ x ] );
       }

       m_strFilterList = dlms.toString();

       return true;
      */

    } // end for()

    return true;

  } // end processOptions()


  /**
   * Process manifest file
   *
   * @param strFlags a string of char settings
   */
  private boolean processManifestName( String strFileName )
  {
    m_strManifestFileName = strFileName;
    return true;

  } // end processInitialFlags()



  /**
   * Parses the commandline args
   *
   * @param astrArgs The command line args
   */
  private boolean parseArgs( String[] astrArgs ) throws Exception
  {
    if ( astrArgs.length == 0 )
    {
      showUsage();
      return false;

    } // end if

    if ( !processInitialFlags( astrArgs[ 0 ] ) )
    {
      return false;
    }

    m_nCompressionLevel = 0;

   return processOptions( astrArgs );

  } // end parseArgs()


  /**
   * Show the command line usage of VwHar
   */
  private void showUsage()
  {

    String strUsage = "Usage: Vwjar {ctxu}[vlM] [-j jar-file] [-jl jar file list] [-m manifest-file] [-mf filter to match]  [-C dir] files ...\n"
                    + "Options:\n"
                    + "-c  create new archive\n"
                    + "-f  The name of the archive file\n"
                    + "-t  list table of contents for archive\n"
                    + "-i  Ignore case when using filters\n"
                    + "-x  extract named (or all) files from archive\n"
                    + "-u  update existing archive\n"
                    + "-v  generate verbose output on standard output\n"
                    + "-m  include manifest information from specified manifest file\n"
                    + "-l  set compression level to use, zero (none) is the default\n"
                    + "-M  do not create a manifest file for the entries\n"
                    + "-C  change to the specified directory and include the following file(s), wilcards are allowed\n"
                    + "-CPR  change to the specified directory and make package entries from any sub directories\n"
                    + "-P  list of semi-colan delimited package names\n"
                    + "-B  Place entries in archive starting at the specified path i.e. -B /foo/bar";

    System.out.println( strUsage );

  } // end showUsage



  /**
   * Program entry
   *
   * @param astrArgs The command line args
   *
   */
  public static void main( String[] astrArgs )
  {

    try
    {
      VwJar jar = new VwJar();
      jar.exec( astrArgs );

    }
    catch( Exception e )
    {
      e.printStackTrace();
    }

  } //end main()


} // end class VwJar{}


// *** End of VwJar.java ***

