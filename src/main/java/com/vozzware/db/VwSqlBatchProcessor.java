/*
 ============================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

  THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
  PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
  CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

  Source Name:  VwSqlBatchProcessor.java


 ============================================================================
 */

package com.vozzware.db; // This package

import com.vozzware.util.VwDelimString;
import com.vozzware.util.VwExString;
import com.vozzware.util.VwInputSource;
import com.vozzware.util.VwLogger;
import com.vozzware.util.VwTextParser;
import org.apache.logging.log4j.core.Appender;

import java.io.File;
import java.io.FileReader;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

/**
 * This class reads SQL files that contain batch SQL statements to execute. Each
 * SQL statement must be terminated with a semi-colon. Each SQL statementment is
 * parsed and executed.
 */
public class VwSqlBatchProcessor
{
  private VwDatabase m_db; // A valid database connection

  private FileReader m_sqlBatch; // Input stream to read the sql batch file

  private boolean m_fErrors = false; // Set to true whn any errors detected

  private boolean m_fQuitOnError = true; // If true terminate on first error

  private boolean m_fIgnoreDropErrors = true; // Ignore drop statement errors

  private VwDelimString m_dlmsSql; // Delimited string of SQL batch commands.

  // Each sql statement is delimited by the semi-colon

  private File m_fileLogPath; // Log file path and name

  private static Map s_mapDDLCommands = new HashMap();

  private VwLogger m_logger = VwLogger.getInstance();
  
  private OutputStream  m_outs = null;
  
  private String    m_strSqlBatchFileName;
  
  static
  {
    buildMap();
  }

  /**
   * Constructor
   * 
   * @param dataBase -
   *          A valid VwDatabase instance connected and logged in to a
   *          datasource
    * @param fileErrLogFile -
   *          The name of Error log file to write any error descriptions to
   * 
   * @exception throws
   *              Exception if the input SQL file cannot be opened, or the
   *              output log file cannot be opened.
   */
  public VwSqlBatchProcessor(VwDatabase dataBase, File fileSqlFile, File fileErrLogFile) throws Exception
  {
    m_db = dataBase;

    m_fileLogPath = fileErrLogFile;

    m_strSqlBatchFileName = fileSqlFile.getAbsolutePath();
    
    m_sqlBatch = new FileReader(fileSqlFile);

    char achFile[] = new char[(int) fileSqlFile.length()];

    // *** Read entire contents into the array

    m_sqlBatch.read(achFile);

    // Make a delimited string of the file for parsing

    m_dlmsSql = new VwDelimString(";", VwExString.extractComments( new String(achFile) ));

    achFile = null;

  } // end VwSqlBatchProcessor()

  
  public void addAppender( Appender appender )
  { m_logger.addAppender( appender ); }
  
  private static void buildMap()
  {
    s_mapDDLCommands.put( "create", "created");    
    s_mapDDLCommands.put( "drop", "dropped");    
    s_mapDDLCommands.put( "alter", "altered");    
    s_mapDDLCommands.put( "insert", "inserted");    
    s_mapDDLCommands.put( "delete", "deleted");    
    s_mapDDLCommands.put( "update", "updated");    
  }

  /**
   * Constructor
   * 
   * @param dataBase -
   *          A valid VwDatabase instance connected and logged in to a
   *          datasource
   * @param strSql -
   *          a String containing the batch SQL statements
   * @param fileErrLogFile -
   *          The name of Error log file to write any error descriptions to
   * 
   * @exception throws
   *              Exception if the input SQL file cannot be opened, or the
   *              output log file cannot be opened.
   */
  public VwSqlBatchProcessor(VwDatabase dataBase, String strSql, File fileErrLogFile) throws Exception
  {
    m_db = dataBase;

    m_fileLogPath = fileErrLogFile;

    strSql = VwExString.extractComments( strSql );
    
    m_dlmsSql = new VwDelimString(";", strSql);

    
  } // end VwSqlBatchProcessor()

  /**
   * Process the batch SQL statements
   * 
   * @return True if no errors encountered; otherwise False is returned
   * 
   * @exception Exception if any I/O errors occur
   */
  public final boolean processBatch() throws Exception
  {
    String strSQL; // Holds a single sql statement

    Connection con = m_db.getConnection();
    con.setAutoCommit(false);

    Statement stmt = con.createStatement();
    if ( m_strSqlBatchFileName != null )
      m_logger.info( null, "Procesing SQL script file: '" + m_strSqlBatchFileName + "'");
    else
      m_logger.info( null, "Procesing SQL Script" );
    
    while ((strSQL = m_dlmsSql.getNext()) != null)
    {
      strSQL = strSQL.trim();

      if (strSQL.length() == 0)
        break;
      
      // *** Replace tildas back to semicolons

      if (strSQL.indexOf('~') > 0)
        strSQL = strSQL.replace('~', ';');

      try
      {
        boolean fRet = stmt.execute(strSQL);
        int nRows = stmt.getUpdateCount();
        
        if ( m_outs != null )
          logIt( m_outs, strSQL, nRows );
        else
          logIt( m_logger, strSQL, nRows );
        
      } // end try

      catch (SQLException sqle)
      {

        // Extract verb from sql statement and see if its a drop statement

        StringBuffer sbMsg = new StringBuffer( sqle.toString() );
        while( (sqle = sqle.getNextException() ) != null )
          sbMsg.append( "\n").append( sqle.toString() );
        
        int nPos = strSQL.indexOf(' ');
        if (nPos > 0)
        {
          String strVerb = strSQL.substring(0, nPos);
          if (strVerb.equalsIgnoreCase("drop"))
          {
            if (!m_fIgnoreDropErrors)
            {
              m_fErrors = true;
              writeLogFile(strSQL, sbMsg.toString());

              if (m_fQuitOnError)
                return false;

            } // end if
            else
              continue; // Ignore drop error

          } // end if strVerb.equalsIgnoreCase( "drop" )

        } // end if (nPos)

        m_fErrors = true;

        writeLogFile(strSQL, sbMsg.toString());

        if (m_fQuitOnError)
          break;

      } // end catch

    } // end while

    if ( m_fErrors )
    {
      if ( m_strSqlBatchFileName != null )
        m_logger.info( null, "Procesing of SQL script file: '" + m_strSqlBatchFileName + "' has terminated with errors");
      else
        m_logger.info( null, "Procesing SQL Script has terminated with errors" );
      
      return m_fErrors;
      
    }
    
    con.commit();
    
    if ( m_strSqlBatchFileName != null )
      m_logger.info( null, "Procesing of SQL script file: '" + m_strSqlBatchFileName + "' has completed successfully");
    else
      m_logger.info( null, "Procesing SQL Script has completed successfully" );
    
    return true; // Everything is successful

  } // end processBatch()

  
  /**
   * Formats SQL output based on object and verb and uses a configured VwLogger to log the result
   * @param logger
   * @param strSQL The SQL string to interpret
   * @param nRows The number of rows affected
   * 
   * @throws Exception
   */
  public static void logIt( VwLogger logger, String strSQL, int nRows ) throws Exception
  {
    logger.info( null, formatMsg( strSQL, nRows ));
    
  }

  /**
   * 
   * Formats SQL output based on object and verb and writes the formatted output to the OutputStream specified
   * @param outs The output stream to log the string to
   * @param strSQL
   * @param nRows
   * @throws Exception
   */
  public static void logIt( OutputStream outs, String strSQL, int nRows ) throws Exception 
  {
    outs.write( ("\n" + formatMsg( strSQL, nRows )).getBytes() );
    
  }
  
  private static String formatMsg( String strSQL, int nRows ) throws Exception
  {
    StringBuffer sb = new StringBuffer();
    
    VwTextParser parser = new VwTextParser( new VwInputSource(  strSQL ) );
    parser.getToken( sb );
    String strVerb = sb.toString();
    parser.getToken( sb );
    String strObjectType = sb.toString();
    parser.getToken( sb );
    String strObjectName = sb.toString();
    
    String strConvertedVerb = (String) s_mapDDLCommands.get( strVerb.toLowerCase() );

    if ( strConvertedVerb != null && (strConvertedVerb.equals( "inserted") || strConvertedVerb.equals( "deleted") || strConvertedVerb.equals( "updated")        ) )
      return "" + nRows + " rows(s) " +  strConvertedVerb;
    else  
      return strObjectType + " " + strObjectName + " " + (String)((strConvertedVerb != null)?strConvertedVerb:strVerb);
    
  }
  /**
   * Writes an entry to the error log file
   * 
   * @param strError -
   *          The error text to write
   * 
   * @exception  Exception if any I/O errors occur
   */
  private void writeLogFile( String strSQL, String strError ) throws Exception
  {
    String strErrMsg = "The following script statement failed:\n" + strSQL + "\nfor this reason: ";
    if ( m_outs != null )
      m_outs.write( ("\n" + strErrMsg + strError).getBytes() );
    else
     m_logger.error( null,  strErrMsg + strError );

  } // end writeLogFile()

  
  /**
   * Command line support
   * @param args
   */
  public static void main( String[] args )
  {
    String strScriptName = null;
    String strDataSource = null;
    String strDataSourceUrl = null;
    String strUid = null;
    String strPwd = null;
    String strLogFile = null;
    
    if ( args.length == 0 )
    {
      showFormat();
      System.exit( -1 );
    }
    
    for ( int x = 0; x < args.length; x++ )
    {
      String strArg = args[ x ];
      
      if ( strArg.equals( "-s" ) )
        strScriptName = args[ ++x ];
      else
      if ( strArg.equals( "-d" ) )
        strDataSource = args[ ++x ];
      else
      if ( strArg.equals( "-i" ) )
        strDataSourceUrl = args[ ++x ];
      else
      if ( strArg.equals( "-u" ) )
        strUid = args[ ++x ];
      else
      if ( strArg.equals( "-p" ) )
        strPwd = args[ ++x ];
      else
      if ( strArg.equals( "-l" ) )
        strLogFile = args[ ++x ];
      else
      {
        showFormat();
        System.exit( -1 );
      }
      
    } // end for()
    
    if ( strScriptName == null ||
         strDataSource == null ||
         strDataSourceUrl == null ||
         strUid == null ||
         strPwd == null )
    {
      showFormat();
      System.exit( -1 );
      
    }
    
    try
    {
      VwDbMgr dbMgr = new VwDbMgr( strDataSource, strDataSourceUrl, VwLogger.getInstance() );
      VwDatabase db = dbMgr.login( strUid, strPwd );
      File fileSql = new File( strScriptName );
      File fileLog = null;
      
      if ( strLogFile != null )
        fileLog = new File( strLogFile );
      
      
      VwSqlBatchProcessor sbp = new VwSqlBatchProcessor( db, fileSql, fileLog );
      sbp.processBatch();
      
    }
    catch( Exception ex )
    {
      ex.printStackTrace();
    }
    
  }

  private static void showFormat()
  {
    System.out.println("VwSqlBatchProcessor -s scriptfile - d data source name -i datsource url id\n" +
                        "-u user id -p password [-l log file]");
    
  }
} // end class VwSqlBatchProcessor()

// *** End of VwSqlBatchProcessor.java

