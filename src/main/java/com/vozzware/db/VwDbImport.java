/*
 ============================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

  THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
  PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
  CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

  Source Name:   VwDbImport.java


 ============================================================================
*/

package com.vozzware.db;                         // Our package

import com.vozzware.util.VwDelimString;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.ResourceBundle;
import java.util.TooManyListenersException;
import java.util.Vector;

/**
 * This class is used to import data stored in the flat file previously created by the
 * VwDbExport class. The format of the flat file is as follows:
 *
 * pos 0 - 7    ITCDBIXO The eyecatcher
 * pos 8 - 15   File offset to the start of the schema/table directory
 * pos 16 - n   Database row records (These are de-serialized VwDataObjects)
 * pos n - n    The schema table directory. The starting position indicated in the
 * 8 - 15 position is a long data type indicating the file offset.
 */
public class VwDbImport
{

  private VwDbImpExpList     m_impList;        // List schemas and tables to import

  private VwDatabase         m_db;             // Database instance we're exporting from

  private RandomAccessFile    m_ioStream;       // Io stream for writing export file

  private ResourceBundle      m_msgs;           // Error Message bundle

  private VwDbImpExpListener m_ixListener;     // Listener of export events, only one allowed

  private Hashtable           m_htDirectory;    // Hash table of Table name vectors by schema

  
  /**
   * Flag that allows an imported row to be treated as an update idf the key alreay exists
   */
  public static final int UPD_ON_DUPS = 0x00001;

  /**
   * Class Constructor
   *
   * @param VwDatabase The database instance of a logged in user.
   * @param strExpFileName The name of the export file to create
   *
   * @exception Exception if the export file exists and the fOverWrite flag is false
   */
  public VwDbImport( VwDatabase db, String strExpFileName )  throws Exception
  {
    m_msgs = ResourceBundle.getBundle( "resources.properties.vwdb" );

    m_htDirectory = new Hashtable();

    m_db = db;


    // *** Open export file for writing

    m_ioStream = new RandomAccessFile( strExpFileName, "rw" );

    // *** Get the directory lstings

    loadDirectory();

  } // end Constructor  VwDbImport()


  /**
   * Adss an event listener for this export session. Only one listener allowed
   *
   * @param VwDbInpExpListener The listener object
   *
   * @exception TooManyListenersException if more thatn one listener registers
   */
  public void addVwDbInpExpListener( VwDbImpExpListener ixListener )
    throws TooManyListenersException
  {
    if ( m_ixListener != null )
      throw new TooManyListenersException();

    m_ixListener = ixListener;

  } // end addVwDbInpExpListener()


  /**
   * Gets a list of the database schema names found in the export file
   *
   * @return a String array of schema names or null if no schema names are avaliable
   */
  public String[] getSchemaNames()
  {
    if ( m_htDirectory == null )
      return null;

    Enumeration eSchemas = m_htDirectory.keys();

    VwDelimString dlmsSchema = new VwDelimString();

    while( eSchemas.hasMoreElements() )
      dlmsSchema.add( (String)eSchemas.nextElement() );

    return dlmsSchema.toStringArray();

  } // end getSchemaNames()


  /**
   * Gets a list of the exported tables in a given schema
   *
   * @return a String array of table names or null if the schema does not exist
   */
  public String[] getTableNames( String strSchema )
  {
    if ( m_htDirectory == null )
      return null;

    Vector vecTables = (Vector)m_htDirectory.get( strSchema.toUpperCase() );

    if ( vecTables == null )
      return null;

    String[] astrTables = new String[ vecTables.size() ];

    for ( int x = 0; x < astrTables.length; x++ )
    {
      VwDbExpDesc expDesc = (VwDbExpDesc)vecTables.elementAt( x );

      int nPos = expDesc.m_strName.indexOf( ':' );

      astrTables[ x ] = new String( expDesc.m_strName.substring( nPos + 1 ) );

    } // end for()

    return astrTables;

  } // end getTableNames()
  

  /**
   * Starts the inport process.
   *
   * @exception Exception when any errors occur
   */
  public void importTables( VwDbImpExpList impList ) throws Exception
  {
    m_impList = impList;

  } // end import()


  /**
   * Bulds the the table info vectors and schema Hashtable from the directory listing section
   * of the export file
   *
   * @exception IOExeption if any flat file read erros occur
   */
  private void loadDirectory() throws IOException
  {

    // *** First read eye catcher

    byte[] abBytes = new byte[ 8 ];

    int nGot =  m_ioStream.read( abBytes );

    if ( nGot != 8 )
      throw new IOException( m_msgs.getString( "Vw.Db.InvalidExpFile" ) );

    String strEyeCatcher = new String( abBytes );

    if ( !strEyeCatcher.equals( "ITCDBIXO" ) )
      throw new IOException( m_msgs.getString( "Vw.Db.InvalidExpFile" ) );

    // *** Next segment is the number of directory entries
      
    int nNbrDirEntries = m_ioStream.readInt();

    // *** Next segment is the file offset pointer to the directory

    long lDirOffset = m_ioStream.readLong();

    // *** Seek to start of directory

    m_ioStream.seek( lDirOffset );

    String strPrevSchema = "";

    Vector vecTableDefs = null;

    for ( int x = 0; x < nNbrDirEntries; x++ )
    {
      // *** Each directory entry is prefixed with an integer holding the length
      // *** schema:tableName

      int nDirLen = m_ioStream.readInt();

      abBytes = null;
      abBytes = new byte[ nDirLen ];

      nGot = m_ioStream.read( abBytes );

      if ( nGot != abBytes.length )
        throw new IOException( m_msgs.getString( "Vw.Db.ExpDirReadError" ) );

      // Entry is in schema:tableName format

      String strName = new String( abBytes );

      VwDelimString dlms = new VwDelimString( ":", strName );

      VwDbExpDesc expDesc= new VwDbExpDesc();

      expDesc.m_strName = strName;
      expDesc.m_lRowCount = m_ioStream.readLong();
      expDesc.m_lOffset = m_ioStream.readLong();

      String strSchema = dlms.getNext();

      if ( !strSchema.equals( strPrevSchema ) )
      {
        if ( strPrevSchema.length() > 0 )
          m_htDirectory.put( strPrevSchema.toUpperCase(), vecTableDefs );

        strPrevSchema = strSchema;

        vecTableDefs = new Vector();
      }

      vecTableDefs.addElement( expDesc );

    } // end for()

    // *** Add the current schema in process as we are at end of file
    
    m_htDirectory.put( strPrevSchema.toUpperCase(), vecTableDefs );

  } // end loadDirectory

} // end class VwDbImport{}

// *** End of VwDbImport.java ***

