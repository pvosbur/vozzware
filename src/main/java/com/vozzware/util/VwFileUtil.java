/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwFileUtil.java

============================================================================================
*/


package com.vozzware.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author P. Vosburgh
 *
 */
public class VwFileUtil
{
  
  /**
   * Reads the file specified by the path into a String
   * 
   * @param strPath The fully qualidief path (including the file name) to read
   * @return A String with the contents of the file
   * 
   * @throws Exception if any io errors occur
   */
  public static String readFile( String strPath ) throws Exception 
  {
    strPath = VwExString.expandMacro( strPath );

    File fileToRead = new File( strPath );
    return readFile( fileToRead.toURL() );
    
  }// end readFile()

  
  /**
   * Reads the file specified by the path into a String
   * 
   * @param fileToRead  The file to read
   * @return A String with the contents of the file
   * 
   * @throws Exception if any io errors occur
   */
  public static String readFile( File fileToRead ) throws Exception 
  {  return readFile( fileToRead.toURL() );  }

  /**
   * Reads a file to a ByteArrayOutputStream
   * @param fileToRead  The file to read
   * @return
   * @throws Exception
   */
  public static ByteArrayOutputStream readFileToStream( File fileToRead ) throws Exception
  {  return readFileToStream( fileToRead.toURI().toURL(), (int)fileToRead.length() );  }

  /**
   * Write contents to the Output Stream
   * @param ins The input stream to read
   * @param outs The output stream to write
   * @throws Exception
   */
  public static void writeToOutputStream( InputStream ins, OutputStream outs ) throws Exception
  {
    int nChuck = 1024 * 128;
    byte[] ab = new byte[ nChuck ];

    while( true )
    {
      int nRead = ins.read( ab );

      if ( nRead <= 0 )
      {
        break;
      }

      outs.write( ab, 0, nRead );
    }

    ab = null;

    outs.close();

  } // end writeToOutputStream()


  /**
   * Reads the file specified by the path into a String
   * 
   * @param urlFile The URL to the file to read
   * @return A String with the contents of the file
   * 
   * @throws Exception if any io errors occur
   */
  public static String readFile( URL urlFile ) throws Exception
  {
    InputStream ins = null;
    
    try
    {
      ins = urlFile.openStream();

      return readFile( ins );
    }
    finally
    {
      if ( ins != null )
      {
        ins.close();
      }
      
    }
    
  } // end readFile()


  /**
   * Reads the file from an InputStream
   *
   * @param inps The InputStraem  to the file to read
   * @return A String with the contents of the file
   *
   * @throws Exception if any io errors occur
   */
  public static String readFile( InputStream inps ) throws Exception
  {
    int nLen = 64 * 1024;

    byte[] abData = new byte[ nLen ];

    int nGot = 0;

    StringBuffer sb = new StringBuffer( nLen );

    while( true )
    {
      nGot = inps.read( abData );
      if ( nGot <= 0 )
      {
        break;
      }

      sb.append( new String( abData,0, nGot ) );

    }

    if ( nGot > 0 )
    {
      sb.append( new String( abData, 0, nGot ) );
    }

    return sb.toString();

  }

  /**
   * Reads the file to a ByteArrayOutputStream
   *
   * @param urlFile The url of the file to read
   * @return
   * @throws Exception
   */
  public static ByteArrayOutputStream readFileToStream( URL urlFile, int nFileLen ) throws Exception
  {
    InputStream ins = null;

    ByteArrayOutputStream baouts = new ByteArrayOutputStream( nFileLen );

    try
    {
      ins = urlFile.openStream();

      int nLen = 64 * 1024;

      byte[] abData = new byte[ nLen ];

      int nGot = 0;

      StringBuffer sb = new StringBuffer( nLen );

      while( true )
      {
        nGot = ins.read( abData );
        if ( nGot <= 0 )
        {
          break;
        }

        baouts.write( abData, 0, nGot );

      }

      if ( nGot > 0 )
      {
        baouts.write( abData, 0, nGot );
      }

      return baouts;

    }
    finally
    {
      if ( ins != null )
      {
        ins.close();
      }

    }

  } // end readFile()

  
  /**
   * Creates a file and writes its contents
   * @param strFilePath The file path and name
   * @param strFileContent The content of the file
   * @throws Exception if any file io errors occur
   */
  public static void writeFile( String strFilePath, String strFileContent ) throws Exception
  {
    File fileToWrite = new File( strFilePath );
    writeFile( fileToWrite, strFileContent );
    
  }

  /**
   * Creates a file and writes its contents
   * @param strFilePath The file path and name
   * @param abFileContent The content of the file
   * @throws Exception if any file io errors occur
   */
  public static void writeFile( String strFilePath, byte[] abFileContent ) throws Exception
  {
    File fileToWrite = new File( strFilePath );
    writeFile( fileToWrite, abFileContent );


  }

  /**
   * Creates a file and writes its contents
   * @param fileFilePath The file object that will be written/created
   * @param strFileContent The content of the file
   * @throws Exception if any file io errors occur
   */
  public static void writeFile( File fileFilePath, String strFileContent ) throws Exception 
  {
    FileWriter fw = new FileWriter( fileFilePath );
    fw.write( strFileContent );
    fw.close();

  }

  /**
   * Creates a file and writes its contents
   * @param fileFilePath The file object that will be written/created
   * @param abFileContent The content of the file
   * @throws Exception if any file io errors occur
   */
  public static void writeFile( File fileFilePath, byte[] abFileContent ) throws Exception
  {
    FileOutputStream fos = new FileOutputStream( fileFilePath );
    fos.write( abFileContent );
    fos.close();

  }


  /**
   * Creates a file and writes its contents
   * @param fileFilePath The file object that will be written/created
   * @param insContent The input stream to read the file content from
   * @throws Exception if any file io errors occur
   */
  public static void writeFile( File fileFilePath, InputStream insContent ) throws Exception
  {

    int nChuck = 1024 * 128;
    byte[] ab = new byte[ nChuck ];

        // Write the file
    FileOutputStream fos = new FileOutputStream( fileFilePath );

    while( true )
    {
      int nRead = insContent.read( ab );

      if ( nRead <= 0 )
      {
        break;
      }

      fos.write( ab, 0, nRead );
    }

    ab = null;

    fos.close();

  }

  /**
   * Serializes a Java object
   * @param objToSerialize The java object to serialize
   * @param filePath The file path to serialize to
   * @throws Exception
   */
  public static void serialize( Object objToSerialize, File filePath ) throws Exception
  {
    FileOutputStream fos = new FileOutputStream( filePath );
    serialize( objToSerialize, fos );

  }


  /**
   * Serializes the Java object to the Output stream specified
   * @param objToSerialize The object to serialize
   * @param outs The OutputStream to serialize to
   * @throws Exception
   */
  public static void serialize( Object objToSerialize, OutputStream outs ) throws Exception
  {
		ObjectOutputStream out = null;
		out = new ObjectOutputStream( outs );
		out.writeObject(objToSerialize);
		out.close();
  }


  /**
   * Serializes a Java object
   * @param filePath The file path to the file to deSerialize
   * @throws Exception
   */
  public static Object deSerialize( File filePath ) throws Exception
  {
    FileInputStream ins = new FileInputStream( filePath );
    return deSerialize( ins );

  }


  /**
   * De-serializes a Java object
   * @param ins input stream containg the serialized bytes
   * @return
   * @throws Exception
   */
  public static Object deSerialize( InputStream ins ) throws Exception
  {
		ObjectInputStream oin = null;
		oin = new ObjectInputStream( ins );
		Object objDeSerialized = oin.readObject();
		oin.close();

    return objDeSerialized;

  }

  /**
   * Make directory(s) form a path string
   * @param strPath The directory path
   * @return
   * @throws Exception
   */
  public static boolean makeDirs( String strPath ) throws Exception
  {
    File fileDirs = new File( strPath );
    return fileDirs.mkdirs();
    
  } // end makeDirs()


  /**
   * 
   * @param fileIn
   * @param fileOut
   * @param strMacroStart
   * @param strMacroEnd
   * @param mapMacroValues
   * @throws Exception
   */
  public static void macroExpand( File fileIn, File fileOut, String strMacroStart, String strMacroEnd, Map mapMacroValues ) throws Exception
  {
    if ( !fileIn.exists() )
    {
      throw new Exception( "The File: '" + fileIn.getAbsolutePath() + "' does not exist");
    }
    
    String strFileContents = VwFileUtil.readFile( fileIn );
    
    strFileContents = VwExString.replace( strFileContents, strMacroStart, strMacroEnd, mapMacroValues );
    boolean fIsTemp = false;
    
    if ( fileOut == null )
    {
      fIsTemp = true;
      
      String strPath = fileIn.getAbsolutePath() + ".tmp";
      
      fileOut = new File( strPath );
    }
    
    FileWriter fw = new FileWriter( fileOut );
    
    fw.write( strFileContents );
    
    fw.close();
    
    if ( fIsTemp )
    {
      fileIn.delete();
      fileOut.renameTo( fileIn );
      
    }
    
  } // end macroExpand()


  /**
   * Copies a File 
   * @param fileToCopy The file top copy
   * @param strNewFileName The name of the new file copied
   * @param fileDestinationDir The destination directory or null if the destination directory is the same is the file we are copying
   * @param fCreateDestinationPath if true create the destination directory if it does not exist
   * @throws Exception
   */
  public static void copy( File fileToCopy, String strNewFileName, File fileDestinationDir, boolean fCreateDestinationPath ) throws Exception
  {
    if ( !fileToCopy.exists() )
    {
      throw new Exception( "The file:'" + fileToCopy.getAbsolutePath() + "' does not exist, Cannot copy");
    }
    
    if ( fileDestinationDir == null )
    {
      // if the file destination directory is null, assume copy is in same directory
      String strPath = fileToCopy.getAbsolutePath();
      String strDirectory = VwExString.getDirPath( strPath );
      fileDestinationDir = new File( strDirectory );
    }
    
    copy( fileToCopy.toURI().toURL(), strNewFileName, fileDestinationDir, fCreateDestinationPath );
    
  }

  /**
   * Copies a File preserving the original file name to a different location
   * @param fileToCopy The file top copy
   * @param fileDestinationDir The destination directory
   * @param fCreateDestinationPath if true create the destination directory if it does not exist
   * @throws Exception
   */
  public static void copy( File fileToCopy, File fileDestinationDir, boolean fCreateDestinationPath ) throws Exception
  {
    if ( !fileToCopy.exists() )
    {
      throw new Exception( "The file:'" + fileToCopy.getAbsolutePath() + "' does not exist, Cannot copy");
    }
    
    copy( fileToCopy.toURI().toURL(), null, fileDestinationDir, fCreateDestinationPath );
    
  }

  
  /**
   * Copies the specified file (filetoCopy) to the location as specified by the destination path (strPathLocation)
   *  
   * @param urlFileToCopy  The File to copy
   * @param fileDestinationDir The File object representing the destination directory
   * @param fCreateDestinationPath if true, create the destination directory if it does not exist, else throw Exception if it does not
   * @throws Exception
   */
  public static void copy( URL urlFileToCopy,  File fileDestinationDir, boolean fCreateDestinationPath ) throws Exception
  { copy(  urlFileToCopy, null, fileDestinationDir, fCreateDestinationPath ); }
  

  /**
   * Copies the specified file (filetoCopy) to the location as specified by the destination path (strPathLocation)
   *  
   * @param urlFileToCopy  The File to copy
   * @param strNewFileName The name of the new file copied
   * @param fileDestinationDir The File object representing the destination directory
   * @param fCreateDestinationPath if true, create the destination directory if it does not exist, else throw Exception if it does not
   * @throws Exception
   */
  public static void copy( URL urlFileToCopy, String strNewFileName, File fileDestinationDir, boolean fCreateDestinationPath ) throws Exception
  {
    
    if ( !fileDestinationDir.exists() )
    {
      if ( !fCreateDestinationPath )
      {
        throw new Exception( "The destination directory:'" + fileDestinationDir.getAbsolutePath() + "' does not exist");
      }
      
      fileDestinationDir.mkdirs();
      
      if ( !  fileDestinationDir.exists())
      {
        throw new Exception( "Could not create destination directory:'" + fileDestinationDir.getAbsolutePath() + "'");
      }
    }
    
    if ( strNewFileName == null )
    {
      strNewFileName = VwExString.getFileName( urlFileToCopy.getFile(), true );
    }
    
    // Copy File
    ByteArrayOutputStream baouts = readFileToStream( new File( urlFileToCopy.toURI() ) );
    
    strNewFileName = VwExString.getFileName( strNewFileName, true );
    File fileToWrite = new File( fileDestinationDir.getAbsolutePath() + File.separator + strNewFileName );
    
    FileOutputStream fw = new FileOutputStream( fileToWrite );
    fw.write( baouts.toByteArray(), 0, baouts.size() );
    fw.close();
    
  } // end copy
  
  /**
   * Moves the specified file (fileToMove) to the location as specified by the destination path (strPathLocation)
   *  
   * @param fileToMove  The File to copy
   * @param fileDestinationDir The File object representing the destination directory
   * @param fCreateDestinationPath if true, create the destinaltion directory if it does not exist, else throw Exception if it does not
   * @throws Exception
   */
  public static void move( File fileToMove, File fileDestinationDir, boolean fCreateDestinationPath ) throws Exception
  {
    copy( fileToMove, fileDestinationDir, fCreateDestinationPath );
    fileToMove.delete();
    
    if ( fileToMove.exists() )
    {
      throw new Exception( "Could not delete file:'" + fileToMove.getAbsolutePath() + "'");
    }
    
  } // end move

  /**
   * Concatenates a List of files to the fileDestination
   *
   * @param fileDestination The final file concatenated by the list of files
   * @param listFilesToConcat  List of files to concatenate
   * @param fDeleteFilePart  if true delete each file part as it is concatenated to the destination file
   *
   * @throws Exception
   */
  public static void concat( File fileDestination, List<File> listFilesToConcat, boolean fDeleteFilePart ) throws Exception
  {

    // If we only have one in the list then just rename the piece to the fle destination
    if ( listFilesToConcat.size() == 1 )
    {
      listFilesToConcat.get( 0 ).renameTo( fileDestination );
      return;

    }

    FileOutputStream fos = new FileOutputStream( fileDestination );
    FileInputStream fis = null;

    byte[] abFileChunk = new byte[ 64 * 1024 ]; // 64 k read chunks

    for ( File fileToConcat : listFilesToConcat )
    {
      fis = new FileInputStream( fileToConcat );

      // Add contents of chunk to the final destination file
      while( true )
      {
        int nGot = fis.read( abFileChunk );

        if ( nGot <= 0 )
        {
          fis.close(); // all done this this file close input stream

          if ( fDeleteFilePart )
          {
            fileToConcat.delete(); // deletes file just concatenated

          }

          break;
        }

        fos.write( abFileChunk, 0 , nGot );

      } // end while


    }  // end for()

    // All files written to master

    fos.close();

  }

  /**
   * Creats a Zip file for the file list specified
   *
   * @param fileOutput The the resulting zip file
   * @param listFilesToZip The list if files to zip
   * @return
   * @throws Exception
   */
  public static void zipFileList( File fileOutput, List<File>listFilesToZip ) throws Exception
  {
    FileOutputStream outs = new FileOutputStream( fileOutput );

    zipFileList( outs,  listFilesToZip );
  }


  /**
   * Creats a Zip file for the file list specified
   *
   * @param outs The zipfile output stream to write to
   * @param listFilesToZip The list if files to zip
   * @return
   * @throws Exception
   */
  public static void zipFileList( OutputStream outs, List<File>listFilesToZip ) throws Exception
  {
    ZipOutputStream zout = new ZipOutputStream( outs );

    for ( File fileToZip : listFilesToZip )
    {
      ZipEntry zFileToZip = new ZipEntry( fileToZip.getName() );
      zFileToZip.setSize( fileToZip.length() );

      ByteArrayOutputStream bos = VwFileUtil.readFileToStream( fileToZip );

      zout.putNextEntry( zFileToZip );
      byte[] abFile = bos.toByteArray();

      zout.write( abFile, 0, abFile.length );
    }

     zout.close();
  }

} // end class VwFileUtil{}

// *** End of VwFileUtil.java ***

