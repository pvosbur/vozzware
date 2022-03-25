 /*
 ============================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

  THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
  PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
  CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

  Source Name:   VwDatabase.java


 ============================================================================
*/


package com.vozzware.db;                         // Our package

 import com.vozzware.util.VwDelimString;
 import com.vozzware.util.VwExString;

 import java.sql.Connection;
 import java.sql.DatabaseMetaData;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Types;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.ResourceBundle;

/**
 * This class provides a higher level interface to the JDBC Connection class. It also combines
 * the catalog information methods in the DatabaseMetaClass to provide a much simpler interface
 * for retrieving information about tables, views, columns in tables ...
 */
public class VwDatabase
{
  // Class to define a Temp data object for the getPrimaryKeys
  class TempData
  {
    String m_strName;         // Name of the primary key column
    int    m_nSeqNbr;         // Seq nbrin the key

    TempData( String strName, int nSeq )
    { m_strName = strName; m_nSeqNbr = nSeq; }
  }

  private Connection        m_con;          // Connection instance passed from VwDbMgr

  private DatabaseMetaData  m_dbm;          // Meta class for catalog info

  private ResourceBundle    m_dbMsgs;       // Resource bundle for all database messages

  private VwDbMgr          m_dbMgr;        // Db Mgr that created this instance

  private VwSQLTypeConverterDriver m_SQLTypeCvtr;  // Driver class to convert datatypes

  private VwDataSourceException m_dsNotConnected; // No database connection

  private int               m_nDatabaseType = UNKNOWN;
  
  private boolean           m_fTreatCatalogsAsSchemas = false;
  
  public final static String[] s_astrDriverClass = { "" };
  
  
  
  /**
   * Constant for Table object type
   */
  public static final int TABLE = 1;

  /**
   * Constant for View object type
   */
  public static final int VIEW = 2;

  /**
   * Constant for System Table object type
   */
  public static final int SYSTEM_TABLE = 3;

  /**
   * Constant for Global temporory object type
   */
  public static final int GLOBAL_TEMPORARY = 4;

  /**
   * Constant for Local Temporary object type
   */
  public static final int LOCAL_TEMPORARY = 5;

  /**
   * Constant for Alias object type
   */
  public static final int ALIAS = 6;

  /**
   * Constant for Synonym object type
   */
  public static final int SYNONYM = 7;


  // Database venfor constants
  
  /**
   * Constant for Oracle Database type
   */
  public static final int ORACLE = 0;

  /**
   * Constant for UDB (IBM) Database type
   */
  public static final int UDB = 1;

  /**
   * Constant for MYSQL Database type
   */
  public static final int MYSQL = 2;
  
  /**
   * Constant for Microsoft SQLServer Database type
   */
  public static final int MSSQLSERVER = 3;

  /**
   * Constant for Sybase SQLServer Database type
   */
  public static final int SQLSERVER = 4;

  /**
   * Constant for DERBY Database type
   */
  public static final int DERBY = 5;


  /**
   * Constant for DERBY Database type
   */
  public static final int POSTGRESQL = 6;


  /**
   * Constant for UNKNOWN Database type
   */
  public static final int UNKNOWN = -1;
  
  
  public VwDatabase( Connection con ) throws SQLException
  { this( null, con, null, null ); }
  
  /**
   * Constructor
   *
   * @param dbMgr - An VwDbMgr with the driver information
   * @param con - The established connection instance from VwDbMgr
   * @param dbMsgs - ResourceBundle of error messages
   * @param sqlTypeConverter - A type converter with the type conversion
   * information, if required; otherwise null.
   *
   * @exception throws SQLException if any database errors occur
   */
  public VwDatabase( VwDbMgr dbMgr, Connection con, ResourceBundle dbMsgs, VwSQLTypeConverterDriver sqlTypeConverter ) throws SQLException
  {
    m_dbMgr = dbMgr;

    m_con = con;

    m_dbm = con.getMetaData();

    if ( dbMsgs == null )
      m_dbMsgs = ResourceBundle.getBundle( "resources.properties.vwdb" );
    else
      m_dbMsgs = dbMsgs;
      

    m_SQLTypeCvtr = sqlTypeConverter;

    
    String strDbName = m_dbm.getDatabaseProductName();
    
    if ( strDbName.toLowerCase().indexOf( "mysql" ) >= 0 )
      m_fTreatCatalogsAsSchemas = true;
    
    
  } // end VwDatabase()

  
  public boolean treatCatalogsAsSchemas()
  { return m_fTreatCatalogsAsSchemas; }
  
  
  public int getDatabseType()
  { return m_nDatabaseType; }
  
  /**
   * Returns the state of the auto commit mode flag
   *
   * @return True if the connection is in auto commit mode; False if not
   *
   * @exception throws SQLException if any database errors occur
   */
  public boolean getAutoCommitMode() throws SQLException
  {
    try
    {
      return m_con.getAutoCommit();
    }
    catch( SQLException sqle )
    {
      if ( m_dbMgr != null )
        m_dbMgr.handleException( sqle );

    }

    return false;

  } //end  getAutoCommitMode()


  /**
   * Turns on/off the auto commit mode of this database session
   *
   * @param fAutoCommit If True, turn on the autocommit flag after each sql
   * statement, if False, put the database session in manual commit mode.
   *
   * @exception throws SQLException if any database errors occur
   */
  public void setAutoCommitMode( boolean fAutoCommit ) throws SQLException
  {
    try
    {
      m_con.setAutoCommit( fAutoCommit );
    }
    catch( SQLException sqle )
    {
      if ( m_dbMgr != null )
        m_dbMgr.handleException( sqle );
    }

  } // end setAutoCommitMode()


  /**
   * Commit a database transaction
   *
   * @exception throws SQLException if any database errors occur
   */
  public void commit() throws SQLException
  {
    try
    {
      if ( m_con != null )
        m_con.commit();
    }
    catch( SQLException sqle )
    {
      if ( m_dbMgr != null )
        m_dbMgr.handleException( sqle );
    }

  } // end commit()



  /**
   * Rollback a database transaction
   *
   * @exception throws SQLException if any database errors occur
   */
  public void rollback() throws SQLException
   {
    try
    {
      if ( m_con != null )
        m_con.rollback();
    }
    catch( SQLException sqle )
    {
      if ( m_dbMgr != null )
        m_dbMgr.handleException( sqle );
    }

  } // end rollback()


  /**
   * Get the Connection instance
   *
   * @return The Connection instance for a valid connection or null if not connected
   *
   * @exception VwDataSourceException if the connection is null
   */
  public final Connection getConnection() throws VwDataSourceException
  {
    if ( m_con == null )
      throw m_dsNotConnected;       // Rethrow the exception

    return m_con;

  } // end getConnection()


  /**
   * Get the VwDbMgr instance that is associated with this database connection
   *
   * @return The VwDbMgr instance for the current object
   */
  public final VwDbMgr getDbMgr()
  { return m_dbMgr; }


  /**
   * Close the database connection if the connection is still open.  
   *
   * @exception throws SQLException if any database errors occur when closing
   * the connection.
   */
  public final void close() throws Exception
  {
    if ( m_dbMgr != null ) 
    {
      m_dbMgr.close( this );
    }
    else
    {
      closeConn();
    }
    
  } // end close()


  /**
   * Gets any catalog databases from this datasource or null
   *
   * @return A String array of catalog names, or null if not supported by the driver
   *
   * @exception throws SQLException if any database errors occur
   */
  public final String[] getCatalogs() throws SQLException
  {
    ResultSet cat = m_dbm.getCatalogs();

    ArrayList<String> listCat = new ArrayList<String>();
     while( cat.next() )
      listCat.add( cat.getString( 1 ) );

    cat.close();

    if ( listCat.size() == 0 )
      return null;
    
    String[] astrCatalogs = new String[ listCat.size() ];
    
    listCat.toArray( astrCatalogs );
    
    return astrCatalogs;

  } // end getCatalogs()


  /**
   * Get the schema names defined for a database
   *
   * @return A Sting array of schema names, or null if no schemas have been defined
   *
   * @exception throws SQLException if any database errors occur
   */
  public final String[] getSchemas() throws SQLException
  {

    if ( m_fTreatCatalogsAsSchemas )
      return getCatalogs();
    
    ResultSet cat = m_dbm.getSchemas();

    ArrayList<String> listSchemas = new ArrayList<String>();
    

    while( cat.next() )
      listSchemas.add( cat.getString( 1 ) );

    cat.close();

    if ( listSchemas.size() == 0 )
      return null;
    
    String[] astrSchemas = new String[ listSchemas.size() ];

    listSchemas.toArray( astrSchemas );
    
    return astrSchemas;

  } // end getSchemas()


  
  /**
   * Get an array of tables names for a catalog and schema
   *
   * @param strCatalog - A Catalog name for object dsearch or null for all catalogs
   * @param strSchema - A Schema name for object search or null for all schemas
   * @param strPattern - A wildcard pattern search string, or null for all tables
   *
   * @return A String array of table names, or null if no tables have been defined
   *
   * @exception throws SQLException if any database errors occur
   */
  public final String[] getTables( String strCatalog, String strSchema, String strPattern ) throws SQLException
  { return getObjects( strCatalog, strSchema, TABLE, strPattern ); }

  /**
   * Get an array of tables names for a catalog and schema in the form schemaName.tableName
   *
   * @param strCatalog - A Catalog name for object dsearch or null for all catalogs
   * @param strSchema - A Schema name for object search or null for all schemas
   * @param strPattern - A wildcard pattern search string, or null for all tables
   *
   * @return A String array of table names, or null if no tables have been defined
   *
   * @exception throws SQLException if any database errors occur
   */
  public final String[] getTables( String strCatalog, String strSchema, String strPattern, boolean fIncludeSchemaName ) throws SQLException
  { return getObjects( strCatalog, strSchema, TABLE, strPattern, fIncludeSchemaName ); }

  /**
   * Get an array of fully qualified tables names for a catalog and schema
   *
   * @param strCatalog - A Catalog name for object dsearch or null for all catalogs
   * @param strSchema - A Schema name for object search, or null for all schemas
   * @param strPattern - A wildcard pattern search string, or null for all tables
   *
   * @return A VwDelimString array of table names (catalog,schema,table)
   *
   * @exception throws SQLException if any database errors occur
   */
  public final VwDelimString[] getTablesVerbose( String strCatalog, String strSchema, String strPattern ) throws SQLException
  { return getObjectsVerbose( strCatalog, strSchema, TABLE, strPattern ); }

  
  public List<VwIndexInfo> getTableIndexes( String strCatalog, String strSchema, String strTable ) throws SQLException
  {
    List<VwIndexInfo> listTableIndexes = new ArrayList<VwIndexInfo>();
    
    String m_strCatalog;
    String m_strSchema;
    String m_strTable;

    m_strCatalog = ( strCatalog == null ) ? null : strCatalog.toUpperCase();
    m_strSchema = ( strSchema == null ) ? null : strSchema.toUpperCase();
    m_strTable = ( strTable == null ) ? null : strTable.toUpperCase();

    // Create a temp vector of primary key column names

    ResultSet rs = null;
    
    try
    {
      if ( m_fTreatCatalogsAsSchemas)
        rs = m_dbm.getIndexInfo( m_strSchema, null,  m_strTable, false, false  );
      else
        rs = m_dbm.getIndexInfo( m_strCatalog, m_strSchema, m_strTable, false, false  );

      while( rs.next() )
      {
        short sType = rs.getShort( 7 );
        
        if ( sType == DatabaseMetaData.tableIndexStatistic)
          continue;
        
        VwIndexInfo ii = new VwIndexInfo();
        ii.m_strCatalog = rs.getString( 1 );
        ii.m_strSchema = rs.getString( 2 );
        ii.m_strTableName = rs.getString( 3 );
        ii.m_fUnique = !rs.getBoolean( 4 );
        ii.m_strIndexName = rs.getString( 6 );
        ii.m_nColPos = rs.getInt( 8 );
        ii.m_strColName = rs.getString( 9 );
        String strAsc = rs.getString( 10 );
        
        if ( strAsc == null || strAsc.startsWith( "A"))
          ii.m_fAscending = true;
        else
          ii.m_fAscending = false;
        
        listTableIndexes.add( ii );
        
      }

    }
    finally
    {
      if ( rs != null )
        rs.close();
    }

    
    return listTableIndexes;
    
  } // end getTableIndexesString()

  /**
   * Get an array of view names for a catalog and schema
   *
   * @param strCatalog - A Catalog name for object dsearch or null for all catalogs
   * @param strSchema - A Schema name for object search, or null for all schemas
   * @param strPattern - A wildcard pattern search string, or null for all views
   *
   * @return A String array of view names or null if no views have been defined
   *
   * @exception throws SQLException if any database errors occur
   */
  public final String[] getViews( String strCatalog, String strSchema, String strPattern ) throws SQLException
  { return getObjects( strCatalog, strSchema, VIEW, strPattern ); }



  /**
   * Get an array of system table names for a catalog and schema
   *
   * @param strCatalog - A Catalog name for object search, or null for all catalogs
   * @param strSchema - A Schema name for object search, or null for all schemas
   * @param strPattern - A wildcard pattern search string, or null for all system tables
   *
   * @return A String array of system table names or null if no system tables are accessable
   *
   * @exception throws SQLException if any database errors occur
   */
  public final String[] getSystemTables( String strCatalog, String strSchema, String strPattern ) throws SQLException
  { return getObjects( strCatalog, strSchema, SYSTEM_TABLE, strPattern ); }


  /**
   * Get an array of global temporary table names for a catalog and schema
   *
   * @param strCatalog - A Catalog name for object search, or null for all catalogs
   * @param strSchema - A Schema name for object search, or null for all schemas
   * @param strPattern - A wildcard pattern search string, or null for all global
   * temporary tables
   *
   * @return A String array of global temporary table names, or null if no such table
   * names exist.
   *
   * @exception throws SQLException if any database errors occur
   */
  public final String[] getGlobalTempTables( String strCatalog, String strSchema, String strPattern ) throws SQLException
  { return getObjects( strCatalog, strSchema, GLOBAL_TEMPORARY, strPattern ); }


  /**
   * Get an array of local temporary table names for a catalog and schema
   *
   * @param strCatalog - A Catalog name for object search, or null for all catalogs
   * @param strSchema - A Schema name for object search, or null for all schemas
   * @param strPattern - A wildcard pattern search, or null for all local temporary tables
   *
   * @return A String array of local temporary table names, or null if no such table
   * names exist.
   *
   * @exception throws SQLException if any database errors occur
   */
  public final String[] getLocalTempTables( String strCatalog, String strSchema, String strPattern ) throws SQLException
  { return getObjects( strCatalog, strSchema, LOCAL_TEMPORARY, strPattern ); }


  /**
   * Get an array of alias names for a catalog and schema
   *
   * @param strCatalog - A Catalog name for object search, or null for all catalogs
   * @param strSchema - A Schema name for object search, or null for all schemas
   * @param strPattern - A wildcard pattern search string, or null for all system tables
   *
   * @return A String array of alias names, or null if no alias names exist
   *
   * @exception throws SQLException if any database errors occur
   */
  public final String[] getAlias( String strCatalog, String strSchema, String strPattern ) throws SQLException
  { return getObjects( strCatalog, strSchema, ALIAS, strPattern ); }


  /**
   * Get an array of synonyms for a catalog and schema
   *
   * @param strCatalog - A Catalog name for object search, or null for all catalogs
   * @param strSchema - A Schema name for object search, or null for all schemas
   * @param strPattern - A wildcard pattern search string, or null for all synonyms
   *
   * @return A String array of synonyms, or null if no synonyms exist
   *
   * @exception throws SQLException if any database errors occur
   */
  public final String[] getSynonyms( String strCatalog, String strSchema, String strPattern ) throws SQLException
  { return getObjects( strCatalog, strSchema, SYNONYM, strPattern ); }

  public final String[] getObjects( String strCatalog, String strSchema, int nObjType, String strObjPattern ) throws SQLException
  { return getObjects( strCatalog,  strSchema, nObjType, strObjPattern, false ); }

  /**
   * Get the database object names defined for the database tables, views, synonyms,
   * aliases, etc.
   *
   * @param strCatalog - A Catalog name for object search, or null for all catalogs
   * @param strSchema - A Schema name for object search, or null for all schemas
   * @param nObjType - The object type to search for (TABLE, VIEW, etc.)
   * @param strObjPattern - A wildcard pattern search string, or null for all objects
   * of the given type.
   
   * @return A String array of object names, or null if no such objects have been defined
   *
   * @exception throws SQLException if any database errors occur
   */
  public final String[] getObjects( String strCatalog, String strSchema, int nObjType, String strObjPattern, boolean fIncludeSchemaName ) throws SQLException
  {
    String strObjType = xlateTypes( nObjType );

    boolean fNeedUpperCase = m_dbm.storesUpperCaseIdentifiers();

    if ( strCatalog != null && fNeedUpperCase )
      strCatalog = strCatalog.toUpperCase();
    
    if ( strSchema != null && fNeedUpperCase )
      strSchema = strSchema.toUpperCase();

    if ( strObjPattern != null && fNeedUpperCase )
      strObjPattern = strObjPattern.toUpperCase();
    
    if ( strObjType == null )
      throw new SQLException( m_dbMsgs.getString( "VwDb.InvalidObjectType" ) );

    ResultSet cat = null;
     
    if ( m_fTreatCatalogsAsSchemas)
      cat = m_dbm.getTables( strSchema, null,  strObjPattern, new String[]{strObjType} );
    else
      cat = m_dbm.getTables( strCatalog, strSchema, strObjPattern, new String[]{strObjType} );

    ArrayList<String> listTables = new ArrayList<String>();
    
    //cat = m_dbm.getTables( strCatalog, strSchema, strObjPattern, new String[]{strObjType} );
    StringBuffer sbTableName = new StringBuffer();
    String strCatName = null;
    while( cat.next() )
    {
      sbTableName.setLength( 0 );
      
      if ( fIncludeSchemaName )
      {
        strCatName = cat.getString( 2 );
        if ( strCatName != null )
          sbTableName.append( strCatName ).append( '.');
      }
      
      sbTableName.append( cat.getString( 3 ) );

      String strTableName = sbTableName.toString();
      
      // Some JDBC drivers (known Oracle 10g return objects that are not really tables here
      if ( VwExString.findAny( strTableName, "/=\\|", 0 ) >= 0 )
        continue;
      
      listTables.add( strTableName );
    } 
    
    cat.close();

    if ( listTables.size() == 0 )
      return null;
    
    String[] astrObjects = new String[ listTables.size() ];
    
    listTables.toArray( astrObjects );
    return astrObjects;

  } // end getObjects()


  /**
   * Get the database object names defined for the database tables, views, synonyms,
   * aliases, etc. in Verbose form (all information from the resultset).
   *
   * @param strCatalog - A Catalog name for object search, or null for all catalogs
   * @param strSchema - A Schema name for object search, or null for all schemas
   * @param nObjType - The object type to serach for (TABLE, VIEW, etc.)
   * @param strObjPattern - A wildcard pattern search string, or null for all objects
   * of the given type.
   *
   * @return A VwDelmimString array of detailed object names, or null if no such objects
   * have been defined.
   *
   * @exception throws SQLException if any database errors occur
   */
  public final VwDelimString[] getObjectsVerbose( String strCatalog, String strSchema, int nObjType, String strObjPattern ) throws SQLException
  {
    String strObjType = xlateTypes( nObjType );

    if ( strObjType == null )
      throw new SQLException( m_dbMsgs.getString( "VwDb.InvalidObjectType" ) );

    ResultSet cat = null;
    
    if ( m_fTreatCatalogsAsSchemas )
      cat = m_dbm.getTables( strSchema, null, strObjPattern, new String[]{strObjType} );
    else
      cat = m_dbm.getTables( strCatalog, strSchema, strObjPattern, new String[]{strObjType} );
      
    // *** First find out how many

    int nbr = 0;
    while( cat.next() )
      ++nbr;

    cat.close();

    if ( nbr == 0 )
      return null;

    if ( m_fTreatCatalogsAsSchemas )
      cat = m_dbm.getTables( strSchema, null,  strObjPattern, new String[]{strObjType} );
    else
      cat = m_dbm.getTables( strCatalog, strSchema, strObjPattern, new String[]{strObjType} );
      
    VwDelimString[] astrObjects = new VwDelimString[ nbr ];

    for ( int i = 0; i < nbr; i++ )
      astrObjects[i] = new VwDelimString();

    nbr = 0;

    while( cat.next() )
    {
      for ( int i =0; i < cat.getMetaData().getColumnCount(); i++ )
      {

        switch( cat.getMetaData().getColumnType( i + 1 ) )
        {

          case Types.BINARY:
          case Types.VARBINARY:
          case Types.LONGVARBINARY:
          case Types.NULL:

            break;

          case Types.DATE:
          case Types.TIME:
          case Types.TIMESTAMP:
          case Types.BIT:

          case Types.TINYINT:
          case Types.SMALLINT:
          case Types.INTEGER:
          case Types.BIGINT:

          case Types.FLOAT:
          case Types.REAL:
          case Types.DOUBLE:

          case Types.NUMERIC:
          case Types.DECIMAL:

          case Types.CHAR:
          case Types.VARCHAR:
          case Types.LONGVARCHAR:

            String strTemp = cat.getString( i + 1 );

            if ( strTemp == null )
              strTemp = new String("" );

            astrObjects[ nbr ].add( strTemp );

        }

      }

      nbr++;
    }

    cat.close();

    return astrObjects;

  } // end getObjects()


  /**
   * Enumerates the columns in a table
   *
   * @param strCatalog - The catalog name or null if N/A
   * @param strSchema - The Schema name or null for any schema
   * @param strTable - The table name to enumerate the columns
   *
   * @return An Enumeration that returns VwColInfo objects with the column information
   *
   * @exception throws SQLException if any database errors occur
   */
  public final List<VwColInfo> getColumns( final String strCatalog, final String strSchema,
                                       final String strTable ) throws SQLException
  { return getColumns( strCatalog, strSchema, strTable, null ); }

  
  /**
   * Return a single VwColInfo object for the column specified else null if col not found
   * @param strCatalog
   * @param strSchema
   * @param strTable
   * @param strColName
   * @return
   * @throws SQLException
   */
  public final VwColInfo getColumn( final String strCatalog, final String strSchema,
      															 final String strTable, final String strColName ) throws SQLException
  {
     List list = getColumns( strCatalog, strSchema, strTable, strColName );
     
     if ( list.size() == 1 )
       return (VwColInfo)list.get( 0 );
     
     return null;
     
  } // endGetColumn()

  /**
   * Gets a List of columns in a table
   *
   * @param p_strCatalog - The catalog name or null if N/A
   * @param p_strSchema - The Schema name or null for any schema
   * @param p_strTable - The table name to enumerate the columns
   * @param p_strColPattern - The wildcard pattern search string, or null for all columns
   * in the table.

   * @return An Enumeration that returns VwColInfo objects with the column information
   *
   * @exception throws SQLException if any database errors occur
   */
  public final List<VwColInfo> getColumns( final String p_strCatalog, final String p_strSchema,
                                final String p_strTable, final String p_strColPattern ) throws SQLException
  {
    List<VwColInfo> list = new ArrayList<VwColInfo>();   // Listr of column info objects

    String strCatalog;
    String strSchema;
    String strTable;
    String strColPattern;
    
    boolean fNeedUpperCase = m_dbm.storesUpperCaseIdentifiers();
    
    if ( fNeedUpperCase )
    {
      strCatalog = ( p_strCatalog == null ) ? null : p_strCatalog.toUpperCase();
      strSchema = ( p_strSchema == null ) ? null : p_strSchema.toUpperCase();
      strTable = ( p_strTable == null ) ? null : p_strTable.toUpperCase();
      strColPattern = ( p_strColPattern == null ) ? null : p_strColPattern.toUpperCase();
    }
    else
    {
      strCatalog = p_strCatalog;
      strSchema = p_strSchema;
      strTable = p_strTable;
      strColPattern = p_strColPattern;
      
    }
    
    synchronized( m_dbm )
    {
      ResultSet rs = null;
      
      if ( m_fTreatCatalogsAsSchemas )
        rs = m_dbm.getColumns( strSchema, null, strTable, strColPattern );
      else
        rs = m_dbm.getColumns( strCatalog, strSchema, strTable, strColPattern );
      
      while( rs.next() )
      {
        VwColInfo ci = new VwColInfo();
  
        ci.m_strCatalog = rs.getString( 1 );
        ci.m_strSchema = rs.getString( 2 );
        ci.m_strTableName = strTable;
        ci.m_strColName = rs.getString( 4 );
        ci.m_sSQLType = rs.getShort( 5 );
        ci.m_strSQLType = rs.getString( 6 );
        ci.m_nColSize = rs.getInt( 7 );
        ci.m_nDecDigits = rs.getInt( 9 );
        ci.m_nRadix = rs.getInt( 10 );
        ci.m_nNullable = rs.getInt( 11 );
        ci.m_strRemarks = rs.getString( 12 );
  
        if ( rs.getMetaData().getColumnCount() >= 13 )
           ci.m_strDefValue = rs.getString( 13 );
  
        try
        {
          if ( m_SQLTypeCvtr != null )
            m_SQLTypeCvtr.convertFromNativeSQLType( ci );
        }
        catch( Exception e )
        {
        }

        list.add( ci );

      } // end while()

      rs.close();

    } // end synchronized()
    
    return list;

  } // end getColumns()


  /**
   * Lists the primary keys in a table.
   *
   * @param p_strCatalog - The catalog name or null if N/A
   * @param p_strSchema - The Schema name or null for any schema
   * @param p_strTable - The table name to enumerate the primary Keys
   *
   * @return An List that returns VwColInfo objects with the primary key information
   *
   * @exception throws SQLException if any database errors occur
   */
  public final List<VwColInfo> getPrimaryKeys( final String p_strCatalog, final String p_strSchema,
                                    final String p_strTable ) throws SQLException
  {
    List<VwColInfo> listKeys = new ArrayList<VwColInfo>();   // Vector of column info objects
    List<TempData> listTemp = new ArrayList<TempData>();
    
    String strCatalog;
    String strSchema;
    String strTable;
    
    boolean fNeedUpperCase = m_dbm.storesUpperCaseIdentifiers();

    if ( fNeedUpperCase )
    {
      strCatalog = ( p_strCatalog == null ) ? null : p_strCatalog.toUpperCase();
      strSchema = ( p_strSchema == null ) ? null : p_strSchema.toUpperCase();
      strTable = ( p_strTable == null ) ? null : p_strTable.toUpperCase();
    }
    else
    {
      strCatalog = p_strCatalog;
      strSchema =  p_strSchema;
      strTable =  p_strTable;
      
    }
    // Create a temp vector of primary key column names

    ResultSet rs = null;
    
    try
    {
      if ( m_fTreatCatalogsAsSchemas )
        rs = m_dbm.getPrimaryKeys( strSchema, null, strTable  );
      else
        rs = m_dbm.getPrimaryKeys( strCatalog, strSchema, strTable  );

      while( rs.next() )
      {
        listTemp.add( new TempData( rs.getString( 4 ), rs.getInt( 5 ) ) );
      }

    }
    finally
    {
      if ( rs != null )
        rs.close();
    }

    // *** Sort the vector by the primary key sequence nbr

    ArrayList<String> sortList = new ArrayList<String>( listTemp.size() );

    for ( int nSeq = 1; nSeq <= listTemp.size(); nSeq++ )
    {
      for ( int x = 0; x < listTemp.size(); x++ )
      {
         TempData t = listTemp.get( x );
         if ( t.m_nSeqNbr == nSeq )
         {
           sortList.add( t.m_strName );
           break;

         }

      } // end for

    } // end for

    int x = -1;

    while( ++x < sortList.size() )
    {

       List<VwColInfo> l = getColumns( strCatalog, strSchema, strTable, sortList.get( x ) );
       listKeys.add( l.get( 0 ) );

    } // end while()

    return listKeys;

  } // end getPrimaryKeys()


  /**
   * Lists the Foreign keys in a table.
   *
   * @param p_strCatalog - The catalog name or null if N/A
   * @param p_strSchema - The Schema name or null for any schema
   * @param p_strTable - The table name to enumerate the foreign Keys
   *
   * @return An List that returns VwForeignKeyInfo objects for each table it is a primary key in
   *
   * @exception throws SQLException if any database errors occur
   */
  public final List<VwForeignKeyInfo> getForeignKeys( final String p_strCatalog, final String p_strSchema,
                                                       final String p_strTable ) throws SQLException
  {
    List<VwForeignKeyInfo> listKeys = new ArrayList<VwForeignKeyInfo>();   // Vector of column info objects

    String strCatalog;
    String strSchema;
    String strTable;

    boolean fNeedUpperCase = m_dbm.storesUpperCaseIdentifiers();
    
    if ( fNeedUpperCase )
    {
      strCatalog = ( p_strCatalog == null ) ? null : p_strCatalog.toUpperCase();
      strSchema = ( p_strSchema == null ) ? null : p_strSchema.toUpperCase();
      strTable = ( p_strTable == null ) ? null : p_strTable.toUpperCase();
    }
    else
    {
      strCatalog = p_strCatalog;
      strSchema = p_strSchema;
      strTable = p_strTable;
      
    }
    // Create a temp vector of primary key column names

    ResultSet rs = null;
    
    if ( m_fTreatCatalogsAsSchemas)
      rs = m_dbm.getImportedKeys( strSchema, null,  strTable  );
    else      
      rs = m_dbm.getImportedKeys( strCatalog, strSchema, strTable  );

    while( rs.next() )
    {
      VwForeignKeyInfo fk = new VwForeignKeyInfo();

      fk.setPkCatalog( rs.getString( 1 ) );
      fk.setPkSchemaName( rs.getString( 2 ) );
      fk.setPkTableName( rs.getString( 3 ) );
      fk.setPkColName( rs.getString( 4 ) );
      fk.setFkCatalog( rs.getString( 5 ) );
      fk.setFkSchema( rs.getString( 6 ) );
      fk.setFkTableName( rs.getString( 7 ) );
      fk.setFkColName( rs.getString( 8 ) );
      fk.setUpdateRule( rs.getShort( 10 ));
      fk.setDeleteRule( rs.getShort( 11 ));
      fk.setDeferRule( rs.getShort( 14 ));

      listKeys.add( fk );
    }

    rs.close();

    return listKeys;

  } // end getForeignKeys()


  /**
   * Gets the stored procedure names of any catalog stored procedures
   *
   * @param strCatalog - The catalog name or null if N/A
   * @param strSchema - The Schema name or null for any schema
   * @param strProcedurePattern - A wildcard pattern search string, or null for all
   * stored procedure objects.
   *
   * @return A string array of proceure names, or null if no procedure names are found
   *
   * @exception throws SQLException if any database errors occur
   */
  public final String[] getProcedureNames( String strCatalog, String strSchema,
                                           String strProcedurePattern ) throws SQLException
  {
    boolean fNeedUpperCase = m_dbm.storesUpperCaseIdentifiers();
    
    if ( fNeedUpperCase )
    {
      strCatalog = ( strCatalog == null ) ? null : strCatalog.toUpperCase();
      strSchema = ( strSchema == null ) ? null : strSchema.toUpperCase();
      strProcedurePattern = ( strProcedurePattern == null ) ? null : strProcedurePattern.toUpperCase();
    }

    ResultSet cat = null;
    
    if ( m_fTreatCatalogsAsSchemas )
      cat = m_dbm.getProcedures( strSchema, null, strProcedurePattern );
    else 
     cat =  m_dbm.getProcedures( strCatalog, strSchema, strProcedurePattern );

    // *** First find out how many

    int nbr = 0;
    while( cat.next() )
      ++nbr;

    cat.close();

    if ( nbr == 0 )
      return null;

    String[] m_astrObjects = new String[ nbr ];

    if ( m_fTreatCatalogsAsSchemas )
      cat = m_dbm.getProcedures( strSchema, null, strProcedurePattern );
    else
      cat = m_dbm.getProcedures( strCatalog, strSchema, strProcedurePattern );
      
    nbr = 0;

    while( cat.next() )
      m_astrObjects[ nbr++ ] = cat.getString( 3 );

    cat.close();

    return m_astrObjects;

  } // end getProcedureNames()



  /**
   * Enumerates the columns in a stored procedure
   *
   * @param strCatalog - The catalog name or null if N/A
   * @param strSchema - The Schema name or null for any schema
   * @param strProc - The procedure name to enumerate the columns
   * @param strColPattern- The wildcard pattern search string, or null for all columns
   * of the given stored procedure.
   *
   * @return An Enumeration instance that returns VwColInfo objects with the column information
   *
   * @exception throws SQLException if any database errors occur
   */
  public final List<VwColInfo> getProcedureColumns( final String strCatalog, final String strSchema,
                                         final String strProc, final String strColPattern ) throws SQLException
  {

    List<VwColInfo> list = new ArrayList<VwColInfo>();   // Listr of column info objects

    String strInCatalog = strCatalog;
    String strInSchema = strSchema;
    String strInProc = strProc;
    String strInColPattern = strColPattern;

    boolean fNeedUpperCase = m_dbm.storesUpperCaseIdentifiers();
    
    if ( fNeedUpperCase )
    {
      strInCatalog = ( strCatalog == null ) ? null : strCatalog.toUpperCase();
      strInSchema = ( strSchema == null ) ? null : strSchema.toUpperCase();
      strInProc = ( strSchema == null ) ? null : strProc.toUpperCase();
      strInColPattern = ( strInColPattern == null ) ? null : strInColPattern.toUpperCase();
    }
    
    ResultSet rs = null;
    
    if ( m_fTreatCatalogsAsSchemas )
       m_dbm.getProcedureColumns( strInSchema, null, strInProc,  strInColPattern );
    else
      m_dbm.getProcedureColumns( strInCatalog, strInSchema, strInProc,  strInColPattern );

    while( rs.next() )
    {
      VwColInfo ci = new VwColInfo();

      ci.m_strCatalog = rs.getString( 1 );
      ci.m_strSchema = rs.getString( 2 );
      ci.m_strTableName = rs.getString( 3 );
      ci.m_strColName = rs.getString( 4 );
      ci.m_sColParamType = rs.getShort( 5 );
      ci.m_sSQLType = rs.getShort( 6 );
      ci.m_strSQLType = rs.getString( 7 );
      ci.m_nColSize = rs.getInt( 8 );
      ci.m_nDecDigits = rs.getInt( 10 );
      ci.m_nRadix = rs.getInt( 11 );
      ci.m_nNullable = rs.getInt( 12 );
      ci.m_strRemarks = rs.getString( 13 );

      try
      {
        if ( m_SQLTypeCvtr != null )
          m_SQLTypeCvtr.convertFromNativeSQLType( ci );
      }
      catch( Exception e )
      {
      }

      list.add( ci );

    } // end while()

    rs.close();

    return list;

  } // end getProcedureColumns()

  
  /**
   * Get tables that have a foreign key relationship to the table name specified
   * @param strCatalog
   * @param strSchema
   * @param strBaseTableName
   * @return
   * @throws Exception
   */
  public String[] getLinkedTables( String strCatalog, String strSchema, String strBaseTableName ) throws Exception
  {
    Map<String,String> mapTables = new HashMap<String,String>();
    
    boolean fNeedUpperCase = m_dbm.storesUpperCaseIdentifiers();
    
    if ( fNeedUpperCase )
    {
      strCatalog = ( strCatalog == null ) ? null : strCatalog.toUpperCase();
      strSchema = ( strSchema == null ) ? null : strSchema.toUpperCase();
      strBaseTableName = ( strBaseTableName == null ) ? null : strBaseTableName.toUpperCase();
    }
    
    ResultSet rs = null;
    if ( m_fTreatCatalogsAsSchemas )
      rs =  m_dbm.getExportedKeys( strSchema, null, strBaseTableName );
    else
      rs = m_dbm.getExportedKeys( strCatalog, strSchema, strBaseTableName );
    
    while( rs.next() )
    {
      String strLinkedSchema = rs.getString( 6 );
      String strTable = rs.getString( 7 );
      if ( strLinkedSchema == null )
        strLinkedSchema = "";
      
      mapTables.put( strLinkedSchema + "." + strTable, null );
    }

    rs.close();
    
    String[] astrTables = new String[ mapTables.size() ];
    int ndx = -1;
    
    for ( Iterator iKeys = mapTables.keySet().iterator(); iKeys.hasNext(); )
      astrTables[ ++ndx ] = (String)iKeys.next();
    
    
    return astrTables;
    
  }


  /**
   * Returns the metadata object for this session
   *
   * @return A metadata object with the metadata for this session
   */
  public DatabaseMetaData getMetaData()
  { return m_dbm; }


  /**
   * Returns the vendor's product name for the driver
   *
   * @return The vendor's product name for the driver
   */
  public String getDriverName() throws SQLException
  { return m_dbm.getDriverName(); }


  /**
   * Returns the vendor's version number for the driver
   *
   * @return The vendor's version number for the driver
   */
  public String getDriverVersionNbr() throws SQLException 
  { return m_dbm.getDriverVersion(); }


  /**
   * Returns the vendor's product name of the database
   *
   * @return The vendor's product name for the driver
   */
  public String getDatabaseName() throws SQLException
  { return m_dbm.getDatabaseProductName(); }

  /**
   * Returns one of the VwDatabase static constants of the database type
   * @return an int representing the database type
   */
  public int getDatabaseType()
  { return m_dbMgr.getDatabaseType(); }
  
  /**
   * Returns The vendor's version number for the database
   *
   * @return The vendor's version number for the driver
   */
  public String getDatabaseVersion() throws SQLException
  { return m_dbm.getDatabaseProductVersion(); }


  /**
   * Translates the public numeric type constants to the corresponding string type
   *
   * @param nType - The numeric constant defined in this class
   *
   * @return The corresponding string type, or null if the type is invalid
   */
  private String xlateTypes( int nType )
  {
    switch( nType )
    {
      case TABLE:

           return "TABLE";

      case VIEW:

           return "VIEW";

      case SYSTEM_TABLE:

           return "SYSTEM TABLE";

      case GLOBAL_TEMPORARY:

           return "GLOBAL TEMPORARY";

      case LOCAL_TEMPORARY:

           return "LOCAL TEMPORARY";

      case ALIAS:

           return "ALIAS";

      case SYNONYM:

           return "SYNONYM";

    } // end switch()

    return null;        // Invalid type

  } // end xlateTypes()


  protected void closeConn() throws Exception
  {
    if ( m_con != null )
    {
      try
      {
        m_con.close();
        m_con = null;
        if ( m_dbMgr != null )
          m_dbMgr.removeDB( this );
        
      }
      catch( SQLException sqle )
      {
        if ( m_dbMgr != null )
          m_dbMgr.handleException( sqle );
      }

    } // end if
    
  }

} // end class VwDatabase{}

// *** End of VwDatabase.java ***

