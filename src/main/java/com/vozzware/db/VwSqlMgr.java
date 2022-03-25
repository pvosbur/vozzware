package com.vozzware.db;


import com.vozzware.db.util.VwExtendsDescriptor;
import com.vozzware.db.util.VwKeyDescriptor;
import com.vozzware.db.util.VwPrimaryKeyGeneration;
import com.vozzware.util.VwBeanUtils;
import com.vozzware.util.VwDate;
import com.vozzware.util.VwDelimString;
import com.vozzware.util.VwExString;
import com.vozzware.util.VwLogger;
import com.vozzware.xml.VwDataObject;
import com.vozzware.xml.VwServiceFlags;
import com.vozzware.xml.VwServiceable;
import com.vozzware.xml.VwXmlWriter;

import java.beans.PropertyDescriptor;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.net.URL;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.UUID;

/**
 * This class handles all SQL execution requests to the database session the instance is associated with.
 * <br>This class is the high level abstraction of the various types of JDBC statements. This class handles
 * <br>all the data bindings for dynamic parameters from Java beans or VwDataObjects. The class offers object relational mapping
 * <br>support through its high level methods such as findBy, saveBy, deleteBy as well as the lower level exec method which
 * <br>can execute any SQL statement the database vendor supports. Results can be renerderd as XML documents using the toXml()
 * <br>method, Java beans or the specialized VwDataObject(a smart Map).
 */
public class VwSqlMgr
{
  // fetch direction constants
  private static final int  	NEXT = 1;		                 // get next row indicator
  private static final int  	PREV = 2;                    // get previous row indicator
  private static final int  	ABS = 3;                     // get absolute row indicator

  private static Map<Long,String>s_mapQueriesByThreadId = Collections.synchronizedMap( new HashMap<Long, String>( ) );

  public static final String PRIME_KY = "primaryKey";
  
  private VwDatabase       	m_db;                        // A database connection

  private VwSqlParser      	m_sqlParser;                 // Handles parsing of sql statements

  private static VwSqlMappingDictionary s_sqlMappingDictionary;  // DAO document dictionary
  
  private ResultSet         	m_rs;                        // Result set for executed select statements

  private CallableStatement 	m_cs;

  private PreparedStatement 	m_ps;
  
  private ResultSetMetaData 	m_rsMeta;                    // Result set meta data

  private int               	m_nRowCount = 0;             // Rows affected by an insert, update delete

  private int               	m_nGetCount;                 // Nbr of times getNext is called

  private static ResourceBundle m_dbMsgs = null;          // Resource bundle for all database messages

  private VwSqlData        	m_sqlData;                   // parsed SQL statement to process


  private boolean           	m_fReuseDataObj = false;     // Reuse in data object on output

  private boolean           	m_fCloseOnExecute = true;    // For stored procs only, this will close
                                                           // the callable statement after execution if true
  private boolean           	m_fParamRebind = true;       // The no rebind stored proc param flag

  private boolean           	m_fNotFoundAsNull = false;   // Treat not found data obj keys as null

  private boolean               m_fIgnoreParser = false;
  
  private VwDriverTranslationMsgs m_xlateMsgs;            // Active driver error msg xlation class

  private short             	m_fFlags = 0;                // Retained from an input dataobjec on an exec

  private VwServiceFlags   	m_serviceFlags = new VwServiceFlags();


  private String               m_strFinderId = null;
  
  private boolean           	m_fPreserveDataObjOrder;  // If true, preserve order in data object

  private int               	m_nResultSetType = ResultSet.TYPE_FORWARD_ONLY;
  private int               	m_nResultSetConcur = ResultSet.CONCUR_READ_ONLY;
  
  private Map                  m_mapDynamicWhereListeners = new HashMap();

  private static int[]      	s_anSqlTypes = {Types.ARRAY, Types.BIGINT, Types.BINARY, Types.BIT,
	                                            Types.BLOB, Types.BOOLEAN, Types.CHAR, Types.CLOB,
	                                            Types.DATALINK, Types.DATE, Types.DECIMAL, Types.DISTINCT,
	                                            Types.DOUBLE, Types.FLOAT, Types.INTEGER, Types.JAVA_OBJECT,
	                                            Types.LONGVARBINARY, Types.LONGVARCHAR, Types.NULL,
	                                            Types.NUMERIC, Types.OTHER, Types.REAL, Types.REF,
	                                            Types.SMALLINT, Types.STRUCT, Types.TIME, Types.TIMESTAMP,
	                                            Types.TINYINT, Types.VARBINARY, Types.VARCHAR };

  private static String[]   	s_astrJavaTypes = { null, "BigInteger", "byte[]", "byte",
                                              "byte[]", "boolean", "char", "String",
                                              null, "Date", "double", null,
                                              "double", "float", "int", "Object",
                                              "byte[]", "String", "null",
                                              "double", "Object", "double", "Object",
                                              "int", "Object", "Time", "Timestamp",
                                              "short", "byte[]", "String" };
 
  private static Map         	    s_mapSqlDataCache = Collections.synchronizedMap( new  HashMap() );
  private static Map         	    s_mapUserSeqSqlCache = Collections.synchronizedMap( new  HashMap() );
  private static Map<String,Map<String,List<VwSqlData>>>  s_mapSqlMappings = Collections.synchronizedMap( new  HashMap<String,Map<String,List<VwSqlData>>>() );

  private List               	  m_listSqlData = new LinkedList();    // SqlData object created for this instance
  
  private boolean				         m_fIngoreMtehodNotFound;
  private boolean               m_fStatementCaching;
  
  
  private VwLogger m_logger;
  private String                m_strSourceDateFormat = VwDate.USADATE;
  
  private static Object         s_objSemifore = new Object();
  
  class TimestampWrapper
  {
    TimestampWrapper( Object objTimestamp , String tsPropName )
    {
      m_objTimestamp = objTimestamp;
      m_tsPropName = tsPropName;
    }
    
    Object  m_objTimestamp;     // The object representing a timestamp value
    String  m_tsPropName;       // The DVO bean property holding the timestamp value
    
  }

  public static void loadSqlMappings( URL urlSqlMappingDoc ) throws Exception
  {
    s_sqlMappingDictionary = new VwSqlMappingDictionary( urlSqlMappingDoc );
  }

  /**
   * Constructor
   *
   * @param db - A valid database connection
   */
  public VwSqlMgr( VwDatabase db, URL urlSqlMappingDoc ) throws Exception
  {
    this( db, false );
    
  } // end VwSqlMgr()
  

  /**
   * Constructor
   *
   * @param db - A valid database connection
   */
  public VwSqlMgr( VwDatabase db )
  {
    this( db, false );

  } // end VwSqlMgr()


  /**
   * Constructor
   *
   * @param db - A valid database connection
   * @param fPreserveDataObjOrder If true, the data objects created for each result, retain
   * the order in which the items are put in the container.
   */
  public VwSqlMgr( VwDatabase db, boolean fPreserveDataObjOrder )
  {
    m_db = db;

    m_fStatementCaching = db.getDbMgr().isStatementCachingEnabled();
    
    m_fPreserveDataObjOrder = fPreserveDataObjOrder;

    if ( m_dbMsgs == null )
      m_dbMsgs = ResourceBundle.getBundle( "resources.properties.vwdb" );


    m_xlateMsgs = db.getDbMgr().getDriverTranslationMsgs();

    m_sqlData = null;

    m_sqlParser = null;
    m_rs = null;

  } // end VwSqlMgr()


  /**
   * Releases all database resources
   *
   */
  public final void close() throws Exception
  {
    m_sqlData = null;
    m_rsMeta = null;

    if ( m_fStatementCaching )
      return;
    
    Exception exLast = null;
    
    if ( m_cs != null )
    {
      try
      {
        m_cs.close();
      }
      catch( Exception ex )
      {
         exLast = ex;
      }
      
      m_cs = null;
      
    }
    
    if ( m_ps != null )
    {
      try
      {
        m_ps.close();
      }
      catch( Exception ex )
      {
        exLast = ex;
      }
      
      m_ps = null;
    }
    
    if ( m_rs != null )
    {
      try
      {
        m_rs.close();
        
      }
      catch( Exception ex )
      {
        exLast = ex;
        
      }
      m_rs = null;
    }
    
    if ( exLast != null )
      throw exLast;
    


  } // end close()

  
  public void addDynamicWhereListener( String strFinderId, VwDynamicWhereCallBack cbListener )
  { m_mapDynamicWhereListeners.put( strFinderId,  cbListener ); }
  
  public void setFinderId( String strFinderId )
  { m_strFinderId = strFinderId; }
  
  /**
   * Sets the logger to use form dummping out all sql statements and any bound parameter values.
   * NOTE! this only logs if the DEBUG level is set so as not to hinder performance
   * @param logger
   */
  public void setLogger( VwLogger logger )
  { m_logger = logger; }
  
  /**
   * If true do not parse SQL before executing.
   * @param fIgnoreParser
   */
  public void setIgnoreSQLParser( boolean fIgnoreParser )
  { m_fIgnoreParser = fIgnoreParser; }
  
  
  /**
   * Maps seql types to Java types
   * 
   * @param nSqlType The sql type from the Types class
   * @return
   */
  public static String sqlTypeToJava( int nSqlType )
  {
    for ( int x = 0; x < s_anSqlTypes.length; x++ )
    {
      if ( nSqlType == s_anSqlTypes[ x ] )
        return s_astrJavaTypes[ x ];
    }


    return null;

  } // end sqlTypeToJava()


  /**
   * Sets the ResultSet type to one of the ResultSet allowable constants<br>
   * ResultSet.TYPE_FORWARD_ONLY, ResultSet.TYPE_SCROLL_INSENSITIVE, or ResultSet.TYPE_SCROLL_SENSITIVE
   * The default is ResultSet.TYPE_FORWARD_ONLY
   *
   * @param nResultSetType One of the following constants: ResultSet.TYPE_FORWARD_ONLY, ResultSet.TYPE_SCROLL_INSENSITIVE, or ResultSet.TYPE_SCROLL_SENSITIVE
   */
  public void setResultSetType( int nResultSetType )
  { m_nResultSetType = nResultSetType; }
  
  
  /**
   * Returns the current ResultSet type setting. The default is ResultSet.TYPE_FORWARD_ONLY
   * @return
   */
  public int getResultSetType()
  { return m_nResultSetType; }
  
  
  /**
   * Sets the ResultSet concurrency The default setting is ResultSet.CONCUR_READ_ONLY
   * 
   * @param nResultSetConcur must be one the the following constants: ResultSet.CONCUR_READ_ONLY or ResultSet.CONCUR_UPDATABLE 
   */
  public void setResultSetConcurrency( int nResultSetConcur )
  { m_nResultSetConcur = nResultSetConcur; }
  
  /**
   * Returns the ResultSet current Concurrency setting. The default is ResultSet.CONCUR_READ_ONLY
   * @return
   */
  public int getResultSetConcurrency()
  { return m_nResultSetConcur; }
  
  
  /**
   * For stored procedure calls only, this method will set the CloseOnExecute flag. If this
   * flag is set to false, the callable statement will not be closed following the execution
   * of the stored procedure. The default setting for this flag is true.
   *
   * @param fCloseOnExecute True to close the statement after execution; False to leave
   * a callable statement open after execution.
   */
  public void setCloseOnExecute( boolean fCloseOnExecute )
  { m_fCloseOnExecute = fCloseOnExecute; }


  /**
   * Sets the stored procedure input paramater rebind flag. If the flag is true (the default)
   * all input paramaters in the dataobject are rebound before the stored procedure is executed
   * otherwise the stored procedure is executed without rebinding any input parameters. This
   * is useful for Oracle stored procedures that return arrays and require multiple calls
   * to get the complete result set.
   *
   * @param fParamRebind True to rebind the parameters before the next execution; False to maintain
   * the current parameter bindings.
   */
  public void setParamRebindFlag( boolean fParamRebind )
  { m_fParamRebind  = fParamRebind; }


  public void setAutoCommit( boolean fAutoCommit ) throws Exception
  { m_db.setAutoCommitMode( fAutoCommit ); }
  
  
  public void commit() throws Exception
  { m_db.commit(); }
  
  public void rollback() throws Exception
  { m_db.rollback(); }
  
  public VwDatabase getDatabase()
  { return m_db; }
  
  
  public VwSqlMappingDictionary getSqlDictionary()
  { return s_sqlMappingDictionary; }
  
  
  public ResultSetMetaData getMetaData()
  { return m_rsMeta; }
  
  public ResultSet getResultSet()
  { return m_rs; }
  
  /**
   * Sets the flag that instructs the sql binding process what to do when a key value in
   * the VwDatsObject does not exists. When binding parameter data in any sql statement
   * or stored procedure call, the default behaviour is to throw an VwNotFoundException
   * if the data in the VwDataObject does not exist. If this method is called with a value
   * of true, not found data will set that parameter to null.
   *
   * @param fSet if true parameters are set to null if the parameter name key is not found
   * in the VwDataObject else an VwNotFoundException is thrown.
   */
  public void setNotFoundKeysToNull( boolean fSet )
  { m_fNotFoundAsNull = fSet; }


  /**
   * Returns the state of the NotFoundKeysToNull property. See description in the
   * setNotFoundKeysToNull property
   */
  public boolean getNotFoundKeysToNull()
  { return m_fNotFoundAsNull; }

  
  /**
   * Delete the row(s) represented by the constraintId the object referes to
   * 
   * @param objToDelete The object that contains the primary key value to delete
   * @param strConstraintId The constraintId to use for the base delete clause
   * @throws Exception
   */
  public void deleteBy( Object objToDelete, String strConstraintId ) throws Exception
  { deleteBy( objToDelete.getClass().getName(), objToDelete, strConstraintId ); }
  

  /**
   * Delete the row(s) represented by the constraintId the object referes to
   * 
   * @param clsMappingId The class that represents the sqlMapping Id as specified in the DAO XML Document
   * @param objToDelete The object that contains the primary key value to delete
   * @param strConstraintId The constraintId to use for the base delete clause
   * 
   * @throws Exception if any database errors occur or if no mapping document was specified
   */
  public void deleteBy( Class<?> clsMappingId, Object objToDelete, String strConstraintId ) throws Exception
  { deleteBy( clsMappingId.getName(), objToDelete, strConstraintId ); }
  

  /**
   * Delete the row(s) represented by the constraintId the object referes to
   * 
   * @param strMappingId The sqlMapping Id as specified in the DAO XML Document
   * @param objToDelete The object that contains the primary key value to delete
   * @param strConstraintId The constraintId to use for the base delete clause
   * 
   * @throws Exception if any database errors occur or if no mapping document was specified
   */
  public void deleteBy( String strMappingId, Object objToDelete, String strConstraintId ) throws Exception
  {
    if ( s_sqlMappingDictionary == null )
    {
      throw new Exception( m_dbMsgs.getString( "Vw.SqlMgr.NoDict"));
    }

    strMappingId = findClassSqlMapping( Class.forName( strMappingId ) ).getName();
    
    String strDeleteSQL = s_sqlMappingDictionary.getDelete( strMappingId, strConstraintId );
    
    if ( strDeleteSQL == null )
    {
      String strErrMsg = m_dbMsgs.getString( "Vw.SqlMgr.NoDeleteSQL");
      throw new Exception( VwExString.replace( strErrMsg, "%1", objToDelete.getClass().getName() ) );
    
    }

    VwExtendsDescriptor extendsDescriptor = s_sqlMappingDictionary.getExtendsDescriptor( strMappingId );

    // If there is a super class that also needs tobe delete, set the autocommit flag off
    if ( extendsDescriptor != null )
    {
      m_db.setAutoCommitMode( false );

    }

    exec( strDeleteSQL, objToDelete );


    if ( extendsDescriptor != null )
    {
      deleteBy(  extendsDescriptor.getSuperClass(), objToDelete, strConstraintId );
    }

  } // end save

  /**
   * Deletes the row identified by the primary key property in the DVO.
   * @param objToDelete The object representing the row to delete
   * @throws Exception if any database error occur
   */
  public void delete( Object objToDelete) throws Exception
  { delete( objToDelete, null, false, false ); }

  /**
   * Delete the row represented by the primary key the object refers to
   * @param objToDelete The object that contains the primary key value to delete
   * @param strMappingId A mapping id if different from the class instance name (may be null)
   * @throws Exception if any database errors occur or if no mapping document was specified
   */
  public void delete( Object objToDelete, String strMappingId ) throws Exception
  { delete( objToDelete, strMappingId, false, false ); }

  
  /**
   * Deletes the row identified by the primary key property in the DVO.
   * <br>NOTE! Using this method requires a timestampCheck entry
   * <br>in the sql mapping document(.xsm). The VwObjectSqlMapper will generate these entries if the
   * timestampColName is specified in the input mapping spec document. The specified timestamp column
   * <br>is read for the primary key of the object and it's value is compared to the timestamp property
   * <br>in the DVO. If they are different, an VwTimestampOutOfSyncException is thrown
   * @param objToDelete The object representing the row to delete
   * @throws VwTimestampOutOfSyncException
   * @throws Exception if any database error occur
   */
  public void syncDelete( Object objToDelete) throws VwTimestampOutOfSyncException, Exception
  { delete( objToDelete, null, true, false ); }
 

  /**
   * Delete the row represented by the primary key the object referes to
   * @param objToDelete The object that contains the primary key value to delete
   * @param strMappingId A mapping id if different from the class instance name (may be null)
   * @param fDoTimestampCheck performs a timestamp comparison test. See the description in the syncDelete method 
   * <br>for complete details
   * @throws Exception if any database errors occur or if no mapping document was specified
   */
  public void delete( Object objToDelete, String strMappingId, boolean fDoTimestampCheck, boolean fBaseClassOnly ) throws VwTimestampOutOfSyncException, Exception
  {
    if ( s_sqlMappingDictionary == null )
    {
      throw new Exception( m_dbMsgs.getString( "Vw.SqlMgr.NoDict"));
    }


    if ( objToDelete instanceof Collection )
    {
      for ( Iterator<?> iObjects = ((Collection<?>)objToDelete).iterator(); iObjects.hasNext(); )
      {
        Object objDelete = iObjects.next();

        delete( objDelete, strMappingId, fDoTimestampCheck, fBaseClassOnly );

      } // end for()

      return;


    }

    if ( fDoTimestampCheck )
    {
      doTimestampCheck( objToDelete, strMappingId );
    }
    
    String strDeleteSQL = null;

    String strSqlMappingId = null;

    if ( strMappingId != null )
    {
      strSqlMappingId = strMappingId;
    }
    else
    {
      strSqlMappingId = findClassSqlMapping( objToDelete.getClass() ).getName();
    }

    VwExtendsDescriptor extendsDescriptor = null;

    if ( !fBaseClassOnly )
    {
      extendsDescriptor = s_sqlMappingDictionary.getExtendsDescriptor( strSqlMappingId );
    }

    // If there is a super class that also needs tobe delete, set the autocommit flag off
    if ( extendsDescriptor != null )
    {
      m_db.setAutoCommitMode( false );

    }

    strDeleteSQL = s_sqlMappingDictionary.getDelete( strSqlMappingId );

    if ( strDeleteSQL == null )
    {
      String strErrMsg = m_dbMsgs.getString( "Vw.SqlMgr.NoDeleteSQL");
      throw new Exception( VwExString.replace( strErrMsg, "%1", objToDelete.getClass().getName() ) );
    
    }
    
    exec( strDeleteSQL, objToDelete );

    if ( extendsDescriptor != null )
    {
      delete( objToDelete,  extendsDescriptor.getSuperClass() );
    }

  } // end delete()

  /**
   * Deletes all objects reference in an object graph.
   * <br>NOTE. This method may fail if cascading deletes are not setup in the schema definition for these table objects
   * @param objToDelete The object to delete
   */
  public void syncDeleteAll( Object objToDelete ) throws VwTimestampOutOfSyncException, Exception
  { deleteAll( objToDelete, true ); }

  /**
   * Deletes all objects reference in an object graph.
   * <br>NOTE. This method may fail if cascading deletes are not setup in the schema definition for these table objects
   * @param objToDelete The object to delete
   */
  public void deleteAll( Object objToDelete ) throws Exception
  { deleteAll( objToDelete, false ); }
 
  /**
   * Deletes all objects reference in an object graph.
   * <br>NOTE. This method may fail if cascading deletes are not setup in the schema definition for these table objects
   * @param objToDelete The object to delete
   * @param fDoTimeStampCheck performs a timestamp comparison test. See the description in the syncDelete method for complete details
   */
  public void deleteAll( Object objToDelete, boolean fDoTimeStampCheck ) throws VwTimestampOutOfSyncException, Exception
  {

    if ( objToDelete instanceof Collection )
    {
      for ( Iterator<?> iObjects = ((Collection<?>)objToDelete).iterator(); iObjects.hasNext(); )
      {
        Object objDelete = iObjects.next();

        deleteAll( objDelete, fDoTimeStampCheck  );

      } // end for()

      return;


    }


    List<PropertyDescriptor> listProps = VwBeanUtils.getReadProperties( objToDelete.getClass() );
    
    for ( PropertyDescriptor pd : listProps )
    {
      Method mthdRead = pd.getReadMethod();
      
      if ( VwBeanUtils.isSimpleType( mthdRead.getReturnType() ))  
      {
        continue;
      }
      
     if ( VwBeanUtils.isCollectionType( mthdRead.getReturnType() ))
     {
       doCollectionDelete( objToDelete, mthdRead, fDoTimeStampCheck );
     }
     else
     {
        Object objReturn = mthdRead.invoke( objToDelete, null );
        if ( objReturn != null )
        {
          deleteAll( objReturn );
        }
     }
       
    }

    delete( objToDelete );

  }

  /**
   * Delete a collectionog objects
   *
   * @param objToDelete
   * @param mthdCollection
   * @param fDoTimestampCheck
   * @throws Exception
   */
  private void doCollectionDelete( Object objToDelete, Method mthdCollection, boolean fDoTimestampCheck ) throws Exception
  {

   Object objCollection = mthdCollection.invoke( objToDelete, (Object[])null );
    
    if ( objCollection == null )
    {
      return;
    }
    
    if ( objCollection instanceof Collection )
    {
      for ( Iterator<?> iElements = ((Collection)objCollection).iterator(); iElements.hasNext(); )
      {
        Object objElement = iElements.next();
        if ( fDoTimestampCheck )
        {
          syncDeleteAll( objElement );
        }
        else
        {
          deleteAll( objElement );
        }
      }
      
    }
    else
    if ( objCollection.getClass().isArray() )
    {
      int nElements = Array.getLength( objCollection );
      
      for ( int x = 0; x < nElements; x++ )
      {
        Object objElement = Array.get( objCollection, x );
        
        if ( fDoTimestampCheck )
        {
          syncDeleteAll( objElement );
        }
        else
        {
          deleteAll( objElement );
        }
      }
     
    }
    
  }


  /**
   * Test to see if row in table exists for the primary key
   * @param objToTest The object containing the primary key value to test
   * @return
   * @throws Exception
   */
  public boolean exists( Object objToTest ) throws Exception
  {
    if ( s_sqlMappingDictionary == null )
    {
      throw new Exception( m_dbMsgs.getString( "Vw.SqlMgr.NoDict"));
    }
    
    String strSql = s_sqlMappingDictionary.getExists( findClassSqlMapping( objToTest.getClass() ) );
    
    exec( strSql, objToTest );
    
    return ( getNext() != null );

  }

  public void syncSave( Object objToSave ) throws Exception
  { save( objToSave, true, true, null ); }

  public void syncSave( Object objToSave, String strMappingId  ) throws Exception
  { save( objToSave, true, true, strMappingId ); }

  /**
   * Save or delete the object, but assume that primary key value might not exist in the database so do exists test first
   * and insert the row if primary key does not exist, else update the row.
   * <br>If the object extends VwDVOBase and is marked for deletion, it will be deleted. If it in in a list of objects
   * <br>it will be removed from the list. If the object to be deleted is a reference to a parent object, the reference
   * <br>to the deleted object in the object graph will be set to null.<strong>NOTE!</strong> If the object to be deleted
   * <br>has dependent rows and referential integrity is defined for the table set, then cascading deletes must be defined
   * <br>for tables declaring foreign key constraints to the object being deleted.
   * 
   * 
   * @param objToSave The object to save
   *
   * @throws Exception
   */
  public void save( Object objToSave ) throws Exception
  { save( objToSave, true ); }

  public void save( Object objToSave, String strMappingId  ) throws Exception
  { save( objToSave, true, false, strMappingId ); }

  public void save( Object objToSave, boolean fRemoveDeletedObjects ) throws Exception
  { save( objToSave, fRemoveDeletedObjects, false, null ); }
  
  
  /**
   * Save the object, but assume that primary key value might not exists in the database so do exists test first
   * and insert the row if primary key does not exist, else update the row
   * 
   * @param objToSave The object to save
   * @param fRemoveDeletedObjects if true remove deleted objects from list and or null out referenced object else
   * <br>delete the object marked for delete but don't remove the object in the graph.
   * @param fDoTimestampCheck performs a timestamp comparison test. See the description in the syncDelete method 
   * @param strMappingId The mapping id to use if different from the class name of the objToSave
   * 
   * @throws Exception
   */
  public void save( Object objToSave, boolean fRemoveDeletedObjects, boolean fDoTimestampCheck, String strMappingId ) throws VwTimestampOutOfSyncException, Exception
  {
    if ( s_sqlMappingDictionary == null )
    {
      throw new Exception( m_dbMsgs.getString( "Vw.SqlMgr.NoDict"));
    }
    
    if ( objToSave instanceof Collection )
    {
      for ( Iterator<?> iObjects = ((Collection<?>)objToSave).iterator(); iObjects.hasNext(); )
      {
        Object objSave = iObjects.next();
        
        if ( objSave instanceof VwDVOBase )
        {
          if ( ((VwDVOBase)objSave).isMarkedForDelete() )
          {
            delete( objSave, strMappingId);
            if ( fRemoveDeletedObjects )
            {
              iObjects.remove();
            }
            
            continue;
            
          } // end if

        } // end if

        saveObject( objSave, fRemoveDeletedObjects, fDoTimestampCheck, strMappingId );
        
      } // end for()
        
    }
    else
    if ( objToSave.getClass().isArray() )
    {
      int nLen = Array.getLength( objToSave );
      
      for ( int x = 0; x < nLen; x++ )
      {
        Object objSave = Array.get(  objToSave, x );
   
        if ( objSave instanceof VwDVOBase )
        {
          if ( ((VwDVOBase)objSave).isMarkedForDelete() )
          {
            delete( objSave, strMappingId );
            if ( fRemoveDeletedObjects )
            {
              Array.set( objToSave, x, null );
            }
            
            continue;
            
          } // end if
          
        } // end if
        
        saveObject( objSave, fRemoveDeletedObjects, fDoTimestampCheck, strMappingId );
        
      }
        
    }
    else
    {
      saveObject( objToSave, fRemoveDeletedObjects, fDoTimestampCheck, strMappingId );
    }
    
  } // end save


  /**
   * 
   * @param objToSave
   * @param fRemoveDeletedObjects
   * @param fDoTimestampCheck performs a timestamp comparison test. See the description in the syncDelete method 
   * @param strMappingId
   * @return
   * @throws Exception
   */
  private boolean saveObject( Object objToSave, boolean fRemoveDeletedObjects, boolean fDoTimestampCheck, String strMappingId  )  throws VwTimestampOutOfSyncException, Exception
  {

    Class clsObjToSave = findClassSqlMapping( objToSave.getClass() );

    boolean fDoExistsTest = false;
    boolean fNeedInsert = false;
    
    if ( objToSave instanceof VwDVOBase )
    {
      if ( ((VwDVOBase)objToSave).isMarkedForDelete() )
      {
        delete( objToSave, strMappingId );
        return true;
      }
    }


    String strId = null;

    if ( strMappingId != null )
    {
      strId = strMappingId;
    }
    else
    {
      strId = clsObjToSave.getName();
      strMappingId = strId;

    }

    // See if this class has a super class and if so save that first as we will need to propagte the primary key value down to this object
    VwExtendsDescriptor vwExtendsDesc = s_sqlMappingDictionary.getExtendsDescriptor( strId );

    if ( vwExtendsDesc != null )
    {
      // If there is a super class that also needs to be saved, set the autocommit flag off
      m_db.setAutoCommitMode( false );

      // Save super class to database
      saveObject( objToSave, fRemoveDeletedObjects, fDoTimestampCheck, vwExtendsDesc.getSuperClass() );

      // Copy primaty key(s) values to sub table
      int nPropKeyNbr = -1;
      for ( String strSuperPrimeKeyProps : vwExtendsDesc.getSuperPrimeKeyProperties() )
      {
        // Propagate key to this class
        VwBeanUtils.setValue( objToSave, vwExtendsDesc.getPrimeKeyProperties().get( ++nPropKeyNbr ), VwBeanUtils.getValue( objToSave, strSuperPrimeKeyProps) );
      }



    }

    List<VwPrimaryKeyGeneration> listPrimeGeneration = s_sqlMappingDictionary.getPrimaryKeyGeneration( strId );
    List<VwKeyDescriptor> listPrimeKeySuppliers = s_sqlMappingDictionary.getPrimaryKeySupplier( strId );
    List<VwKeyDescriptor> listForeignKeys = s_sqlMappingDictionary.getForeignKeys( strId );

    // First we must save any objects that represent foreign key links
    if ( listForeignKeys != null )
    {
      for ( Iterator<VwKeyDescriptor> iKeys = listForeignKeys.iterator(); iKeys.hasNext(); )
      {
        VwKeyDescriptor foreignKey = iKeys.next();

        String strForeignKeyBeanProp = foreignKey.getBeanProperty();

        Object objForeign = VwBeanUtils.getValue( objToSave, strForeignKeyBeanProp );

        if ( objForeign == null )
        {
          continue;
        }

        boolean fDeleted = saveObject( objForeign, fRemoveDeletedObjects, fDoTimestampCheck, strMappingId );

        if ( fDeleted )
        {
          if ( fRemoveDeletedObjects )
          {
            VwBeanUtils.setValue( objToSave, strForeignKeyBeanProp, null );
          }

          continue;
        }
        // get the primary key value fromm the object just saved and  set this foreign key vale to it

        Object objKeyForeign = VwBeanUtils.getValue( objForeign, foreignKey.getPrimeKeyProperty() );

        if ( objKeyForeign != null )
        {
          VwBeanUtils.setValue( objToSave, foreignKey.getForeignKeyProperty(), objKeyForeign );
        }

      }
    }

    if ( listPrimeGeneration != null )
    {
      for ( VwPrimaryKeyGeneration primeKey : listPrimeGeneration )
      {
        String strPrimeKeyProp = primeKey.getBeanProperty();
        Object objPrimeKeyVal = VwBeanUtils.getValue( objToSave, strPrimeKeyProp );

        if ( objPrimeKeyVal == null )
        {
          fNeedInsert = true;

          String strKeyGenPolicy = primeKey.getKeyGenerationPolicy();

          if ( strKeyGenPolicy == null )
          {
            String strErrMsg = m_dbMsgs.getString( "Vw.SqlMgr.MissingKeyGenPolicy" );
            throw new Exception( VwExString.replace( strErrMsg, "%1", strId ) );

          }

          if ( strKeyGenPolicy.equalsIgnoreCase( "table_seq" ) )
          {
            setPrimaryKeyFromUserTable( objToSave, primeKey );
          }
          else
          if ( strKeyGenPolicy.equalsIgnoreCase( "oracle_seq" ) )
          {
            setPrimaryKeyFromOracleSeq( objToSave, primeKey );
          }
          else
          if ( strKeyGenPolicy.equalsIgnoreCase( "postgres_seq" ) )
          {
            setPrimaryKeyFromPostgresSeq( objToSave, primeKey );
          }
          else
          if ( strKeyGenPolicy.equalsIgnoreCase( "uuid" ) )
          {
            setPrimaryKeyFromUUID( objToSave, primeKey );
          }
          else
          {
            String strErrMsg = m_dbMsgs.getString( "Vw.SqlMgr.InvalidKeyGenPolicy" );
            throw new Exception( VwExString.replace( strErrMsg, "%1", strKeyGenPolicy ) );

          }
        } // end if

      } // end for()

    } // end if
    else
    {
      fDoExistsTest = true;
    }

    // If option set
    if ( fDoExistsTest )
    {
      if ( !exists( objToSave ) )
      {
        fNeedInsert = true;
      }
    }

    // Now save this object
    if ( fNeedInsert )
    {
      doInsert( objToSave, strMappingId );
    }
    else
    {
      doUpdate( objToSave, strMappingId, fDoTimestampCheck );
    }

    if ( listPrimeKeySuppliers != null )
    {
      for ( Iterator iKeySup = listPrimeKeySuppliers.iterator(); iKeySup.hasNext(); )
      {
        VwKeyDescriptor keySupplier = (VwKeyDescriptor) iKeySup.next();
        String strBeanProp = keySupplier.getBeanProperty();

        Object objVal = VwBeanUtils.getValue( objToSave, strBeanProp );
        if ( objVal == null )
        {
          continue;
        }

        if ( objVal instanceof Collection )
        {
          Collection<?> list = (Collection<?>) objVal;

          for ( Iterator<?> iObj = list.iterator(); iObj.hasNext(); )
          {
            Object objToSupply = iObj.next();
            setPrimeKey( objToSave, objToSupply, keySupplier );
            boolean fDeleted = saveObject( objToSupply, fRemoveDeletedObjects, fDoTimestampCheck, null );
            if ( fDeleted )
            {
              if ( fRemoveDeletedObjects )
              {
                iObj.remove();
              }

              continue;
            }

          }
        }
        else
        {
          setPrimeKey( objToSave, objVal, keySupplier );
          saveObject( objVal, fRemoveDeletedObjects, fDoTimestampCheck, strMappingId );

        }


      } // end for()
    }

   strMappingId = null;
      
   return false;
   
  } // end saveObject()

  /**
   * Find the sql mapping for an object class. If the object is derived from a generated table mapping it will be one of tts super classes
   *
   * @param clsToSearch The object class to search
   * @return
   * @throws Exception
   */
  private Class findClassSqlMapping( Class<?> clsToSearch  )  throws Exception
  {
    Class clsSearch = clsToSearch;

    // Find the mapping id of the class if it is derrived from a database table class  i.e. like a PBO
     while( true )
     {
       if ( s_sqlMappingDictionary.sqlMappingExists( clsSearch ) )
       {
         break;
       }

       clsSearch = clsToSearch.getSuperclass();

       if ( clsSearch == null )
       {
         throw new Exception( "Can't find a sql mapping for object class: " + clsToSearch.getName() +". It must be a database generated class or derrived from one." );
       }

     }

     return clsSearch;

  }


  /**
   * Set the primary key(s) properties to the recipient object
   * @param objPrimeKeySupplier The object representing the primary key supplier
   * @param objRecipient The recipient getting the primary key(s) value
   * @param keySupplier The primary key supplier value object
   * @throws Exception
   */
  private void setPrimeKey( Object objPrimeKeySupplier, Object objRecipient, VwKeyDescriptor keySupplier ) throws Exception
  {
    VwDelimString dlmsPrimeKey = new VwDelimString( keySupplier.getPrimeKeyProperty() );
    VwDelimString dlmsForeignKey = new VwDelimString( keySupplier.getForeignKeyProperty() );
    
    while( dlmsPrimeKey.hasMoreElements() )
    {
      String strPrimeKeyProp = dlmsPrimeKey.getNext();
      String strForeignKeyProp = dlmsForeignKey.getNext();
      
      Object objPrimeVal = VwBeanUtils.getValue( objPrimeKeySupplier, strPrimeKeyProp );
      VwBeanUtils.setValue( objRecipient, strForeignKeyProp, objPrimeVal );
      
    }
    
    
  }

  /**
   * Set the primary key(s) properties to the recipient object
   *
   * @param objRecipient The recipient getting the primary key(s) value
   * @param keySupplier The primary key supplier value object
   * @throws Exception
   */
  private void setNullKey( Object objRecipient, VwKeyDescriptor keySupplier ) throws Exception
  {
    VwDelimString dlmsForeignKey = new VwDelimString( keySupplier.getForeignKeyProperty() );
    
    while( dlmsForeignKey.hasMoreElements() )
    {
      String strForeignKeyProp = dlmsForeignKey.getNext();
      VwBeanUtils.setValue( objRecipient, strForeignKeyProp, null );
      
    }
    
    
  }


  /**
   * Attempt to update the object
   * @param objToSave the object to update
   * @param fDoTimestampCheck if true check if timestamp options are set and throw an exception if
   *        the time stamp on the current object is out of date with the latest in the database
   * @throws Exception
   */
  public void doUpdate( Object objToSave, boolean fDoTimestampCheck ) throws Exception
  { doUpdate( objToSave, objToSave.getClass().getName(), fDoTimestampCheck); }


  /**
   * Attempt to update the object
   * @param objToSave the object to update
   * @throws Exception
   */
  public void doUpdate( Object objToSave ) throws Exception
  { doUpdate( objToSave, objToSave.getClass().getName(), false ); }

  /**
   * Attempt to update the object
   * @param objToSave the object to update
   * @param strMappingId A mapping id if different from the class instance name (may be null)
   * @param fDoTimestampCheck if true check if timestamp options are set and throw an exception if
   *        the time stamp on the current object is out of date with the latest in the database
   * @throws Exception
   */
  public void doUpdate( Object objToSave, String strMappingId, boolean fDoTimestampCheck ) throws Exception
  {
    boolean fIsDirty = true;
    
    if ( objToSave instanceof VwDVOBase && !((VwDVOBase)objToSave).isDirty() )
    {
      fIsDirty = false;
    }
    
    if ( fIsDirty )
    {
      String strUpdateSQL = null;
      if ( strMappingId != null )
      {
        strUpdateSQL = s_sqlMappingDictionary.getUpdate( strMappingId );
      }
      else
      {
        strUpdateSQL = s_sqlMappingDictionary.getUpdate( objToSave.getClass() );
      }
      
      if ( strUpdateSQL == null )
      {
        String strErrMsg = m_dbMsgs.getString( "Vw.SqlMgr.NoUpateSQL");
        throw new Exception( VwExString.replace( strErrMsg, "%1", objToSave.getClass().getName() ) );
      }
      
      if ( fDoTimestampCheck )
      {
        doTimestampCheck( objToSave, strMappingId );
      }
      
      exec( strUpdateSQL, objToSave );
      
      if ( fDoTimestampCheck ) // update dvo's timestamp property if timestampCheck is on
      {
        updateTimestamp( objToSave, strMappingId  );
      }
      
      
      
    }
    
  }

  private void updateTimestamp( Object objToSave, String strMappingId  ) throws Exception
  {
    TimestampWrapper tsWrap  =  getTimestamp( objToSave, strMappingId );
    VwBeanUtils.setValue( objToSave, tsWrap.m_tsPropName, tsWrap.m_objTimestamp ); 
    
  }


  /**
   * Perform a timestamp check on the row to saved or deleted. This reads the timestamp column as specified to the 
   * <br>object sql mapper. If the timestamp on the row identified by the primary key in the bean to save, differs from
   * <br>the timestamp based column just read, throw the VwTimestampOutOfSyncException
   * 
   * @param objToTest
   * @param strMappingId
   * @throws Exception
   */
  private void doTimestampCheck( Object objToTest, String strMappingId ) throws VwTimestampOutOfSyncException,
                                                                                Exception
  {
    TimestampWrapper tsWrap  =  getTimestamp( objToTest, strMappingId );
    
    Object objBeanTimestamp = VwBeanUtils.getValue( objToTest, tsWrap.m_tsPropName );
    
    if ( objBeanTimestamp instanceof VwDate )
    {
      VwDate dtOrigTs = (VwDate)objBeanTimestamp;
      VwDate dtRowTs = (VwDate)tsWrap.m_objTimestamp;
      
      if ( dtOrigTs.compareDateTime( dtRowTs ) != 0 )
      {
        throw new VwTimestampOutOfSyncException( "" );
      }
      
    }
    else
    if ( objBeanTimestamp instanceof Long )
    {
      if ( !((Long)objBeanTimestamp).equals( (Long)tsWrap.m_objTimestamp ) )
        throw new VwTimestampOutOfSyncException( "" );
    }
    else
    if ( objBeanTimestamp instanceof Timestamp )
    {
      if ( !((Timestamp)objBeanTimestamp).equals( (Timestamp)tsWrap.m_objTimestamp ) )
      {
        throw new VwTimestampOutOfSyncException( "" );
      }
    }
    
  }

  /**
   * Get current timestamp value for the row identified by the beans primary key
   * @param objToTest The bean holding the primary key
   * @param strMappingId The mapping id that contains the timestamp fetch sql
   * @return
   * @throws Exception
   */
  private TimestampWrapper  getTimestamp( Object objToTest, String strMappingId ) throws Exception
  {
    String strTimestampSQL = null;
    if ( strMappingId != null )
    {
      strTimestampSQL = s_sqlMappingDictionary.getTsCheck( strMappingId );
    }
    else
    {
      strTimestampSQL = s_sqlMappingDictionary.getTsCheck( objToTest.getClass() );
    }
    
    if ( strTimestampSQL == null )
    {
      String strErrMsg = m_dbMsgs.getString( "Vw.SqlMgr.NoTimestampSQL");
      throw new Exception( VwExString.replace( strErrMsg, "%1", objToTest.getClass().getName() ) );
    }
    
    exec(  strTimestampSQL, objToTest );
    VwDataObject dobjResult = getNext();
    if ( dobjResult == null )
    {
      throw new Exception( "Did not expect not found condition for timestamp check on obejct '" + objToTest.getClass().getName() );
    }
    
    String strKey = (String)dobjResult.keys().next();
    Object objTimestamp = dobjResult.get( strKey );
    
    return new TimestampWrapper( objTimestamp, strKey );
    
  }

  /**
    * Attempt to insert the object
    * @param objToSave The object to insert

    * @throws Exception if dup key or database errors occur
    */
   public void doInsert( Object objToSave ) throws Exception
   { doInsert( objToSave, objToSave.getClass().getName() ); }

  /**
   * Attempt to insert the object
   * @param objToSave The object to insert
   * @param strMappingId A mapping id if different from the class instance name (may be null)

   * @throws Exception if dup key or database errors occur
   */
  public void doInsert( Object objToSave, String strMappingId ) throws Exception
  {
    String strInsertSQL = null;
    if ( strMappingId != null )
    {
      strInsertSQL = s_sqlMappingDictionary.getInsert( strMappingId );
    }
    else
    {
      strInsertSQL = s_sqlMappingDictionary.getInsert( objToSave.getClass() );
    }

    if ( strInsertSQL == null )
    {
      String strErrMsg = m_dbMsgs.getString( "Vw.SqlMgr.NoInsertSQL");
      throw new Exception( VwExString.replace( strErrMsg, "%1", objToSave.getClass().getName() ) );
    }
    
    exec( strInsertSQL, objToSave );
    
  } // end doInsert()


  /**
   * Sets the primary key from the next oracle sequence as defined in the VwPrimaryKeyGeneration
   * @param objToSave The object that will have its primary key property set
   * @param primeKey The primary key generation metadata that defines the oracle sequence to use
   * @throws Exception
   */
  public void setPrimaryKeyFromOracleSeq( Object objToSave, VwPrimaryKeyGeneration primeKey ) throws Exception
  {
    Class clsObj = objToSave.getClass();
    String strOraSeqSQL = (String)s_mapUserSeqSqlCache.get( clsObj );
    String strOraSeqName = primeKey.getSequenceName();
    
    if ( strOraSeqSQL == null )
    {
      StringBuffer sb = new StringBuffer();
      sb.append( "select " ).append( strOraSeqName ).append( ".NEXTVAL from DUAL" );
      strOraSeqSQL = sb.toString();
      
      s_mapUserSeqSqlCache.put( clsObj, strOraSeqSQL );
    }
    
    VwSqlMgr sqlMgrOra = new VwSqlMgr( m_db );
    sqlMgrOra.setLogger( m_logger );
    try
    {
      sqlMgrOra.exec( strOraSeqSQL, null );
      VwDataObject dobjSeq = sqlMgrOra.getNext();
      long lSeq = dobjSeq.getLong( "nextval" );
  
      String strKeyProp = primeKey.getBeanProperty();
      
      VwBeanUtils.setValue( objToSave, strKeyProp, new Long( lSeq ) );
      
      
    }
    finally
    {
      sqlMgrOra.close();
    }
    
  }

  /**
    * Sets the primary key from the next oracle sequence as defined in the VwPrimaryKeyGeneration
    * @param objToSave The object that will have its primary key property set
    * @param primeKey The primary key generation metadata that defines the oracle sequence to use
    * @throws Exception
    */
   public void setPrimaryKeyFromPostgresSeq( Object objToSave, VwPrimaryKeyGeneration primeKey ) throws Exception
   {
     Class clsObj = objToSave.getClass();
     String strPostgresSeqSQL = (String)s_mapUserSeqSqlCache.get( clsObj );
     String strPostgresSeqName = primeKey.getSequenceName();

     if ( strPostgresSeqSQL == null )
     {
       StringBuffer sb = new StringBuffer();
       sb.append( "select nextval('" ).append( strPostgresSeqName ).append( "')" );
       strPostgresSeqSQL = sb.toString();

       s_mapUserSeqSqlCache.put( clsObj, strPostgresSeqSQL );
     }

     VwSqlMgr sqlMgrPostGres = new VwSqlMgr( m_db );
     sqlMgrPostGres.setLogger( m_logger );
     try
     {
       sqlMgrPostGres.exec( strPostgresSeqSQL, null );
       VwDataObject dobjSeq = sqlMgrPostGres.getNext();
       long lSeq = dobjSeq.getLong( "nextval" );

       String strKeyProp = primeKey.getBeanProperty();

       VwBeanUtils.setValue( objToSave, strKeyProp, new Long( lSeq ) );


     }
     finally
     {
       sqlMgrPostGres.close();
     }

   }

  /**
   * Sets the primary key from a UUID
   *
   * @param objToSave The object that will have its primary key property set
   * @param primeKey The primary key generation metadata that defines the primary key properties
   * @throws Exception
   */
  public void setPrimaryKeyFromUUID( Object objToSave, VwPrimaryKeyGeneration primeKey ) throws Exception
  {
     VwBeanUtils.setValue( objToSave, primeKey.getBeanProperty(), UUID.randomUUID() );
  }

  /**
   * Sets the primary key from the next managed table sequence as defined in the VwPrimaryKeyGeneration
   * @param objToSave The object that will have its primary key property set
   * @param primeKey The primary key generation metadata that defines the the table and column holding the sequence value
   * @throws Exception
   */
  public void setPrimaryKeyFromUserTable( Object objToSave, VwPrimaryKeyGeneration primeKey ) throws Exception
  {
    Class clsObj = objToSave.getClass();
    
    ArrayList listSql = (ArrayList)s_mapUserSeqSqlCache.get( clsObj );
    String strSeqTableName = primeKey.getSequenceTableName();
    
    if ( strSeqTableName == null )
      throw new Exception( "The sequenceTableName attribute of the sqlMappingSpec element must be defined");
    
    String strSeqColName = primeKey.getSequenceColName();
    
    if ( strSeqColName == null || strSeqColName.startsWith( "XX"))
      throw new Exception( "The sequenceColName attribute of the sqlMappingSpec element must be defined");
    
    if ( listSql == null )
    {
      listSql = new ArrayList( 2 );
      s_mapUserSeqSqlCache.put( clsObj, listSql );
      
      StringBuffer sb = new StringBuffer();
      
	    
	    sb.append( "select ").append( strSeqColName ).append( " from ").append( strSeqTableName );
	    // this clause faile on sqlserver -- need to look inthis furthor -- ;
      sb.append( " for update ");
	    listSql.add( 0, sb.toString() );
	    sb.setLength( 0 );
	    
	    sb.append( "update ").append( strSeqTableName ).append( " set ").append( strSeqColName );
	    sb.append( " = ?");
	    listSql.add( 1, sb.toString() );
    
    }
    
    
    synchronized( s_objSemifore )
    {
      VwDatabase dbSequence = null;
      VwSqlMgr sqlMgrSeq = null;
      
      try
      {
        dbSequence = m_db.getDbMgr().login();   // get our own connection so we can commit the sequence action
        dbSequence.setAutoCommitMode( true );
        sqlMgrSeq = new VwSqlMgr( dbSequence );
        sqlMgrSeq.setLogger( m_logger );
        sqlMgrSeq.m_fStatementCaching = true;
        
	      sqlMgrSeq.exec( (String)listSql.get( 0 ), null );
	      VwDataObject dobjSeq = sqlMgrSeq.getNext();
	    
	      long lSeq = 0;
	    
  	    if ( dobjSeq == null )
  	    {
  	      String strErrMsg = m_dbMsgs.getString( "Vw.SqlMgr.MissingSeqRow");
  	      throw new Exception( VwExString.replace( strErrMsg, "%1", strSeqTableName ) );
  	    }
  	    else
  	      lSeq = dobjSeq.getLong( strSeqColName );
  	
  	    String strKeyProp = primeKey.getBeanProperty();
  	    
  	    VwBeanUtils.setValue( objToSave, strKeyProp, new Long( lSeq ) );
  	    
  	    // Update sequence
  	    
  	    dobjSeq.put(strSeqColName, ++lSeq );
  	    sqlMgrSeq.exec( (String)listSql.get( 1 ), dobjSeq );
  
        
      }
      finally
      {
        if ( sqlMgrSeq != null )
          sqlMgrSeq.close();
        
        if ( dbSequence != null )
          dbSequence.close();
        
      }
        
	    
    }
    
  }

  /**
   * Does query based on the primary key finder id 
   * <br>When the class type of the objParams object is the same as the mapping id return object
   * <br>Then use this method instead of one that requires the mapping id
   * 
   *
   * @param objParams any object contatining the bean properties used to resolve where clause parameters
   * @return An object of type clsIdD or null if no rows were found
   *  
   * @throws Exception if any database errors were encountered
   */
  public Object findByPrimaryKey( Object objParams ) throws Exception
  { return findBy( objParams.getClass(), PRIME_KY, objParams );  }
  

  /**
   * Does query based on the primary key finder id 
   * @param clsID The Object class for the object type to be returned
   *
   * @param objParams any object containing the bean properties used to resolve where clause parameters
   * @return An object of type clsIdD or null if no rows were found
   *  
   * @throws Exception if any database errors were encountered
   */
  public Object findByPrimaryKey( Class clsID, Object objParams ) throws Exception
  { return findBy( clsID, PRIME_KY, objParams );  }
  

  /**
   * Retrieves one object of the type specicied by the clsId param. This mthod should only be used
   * when only one object is expected from the query
   *
   * @param clsID The Object class for the object type to be returned
   * @param strFindById The finder id to use for the lookup
   *
   * @param objParams any object contatining the bean properties used to resolve where clause parameters
   * @return an objectsof type clsId or null if no rows were found
   *
   * @throws Exception if any database errors were encountered
   */
  public Object findBy( Class clsID, String strFindById, Object objParams ) throws Exception
  {
    return findBy( clsID, strFindById, objParams, null );
  }

  /**
   * Retrieves one object of the type specicied by the clsId param. This mthod should only be used
   * when only one object is expected from the query
   * 
   * @param clsID The Object class for the object type to be returned
   * @param strFindById The finder id to use for the lookup
   * 
   * @param objParams any object contatining the bean properties used to resolve where clause parameters
   * @return an objectsof type clsId or null if no rows were found
   *  
   * @throws Exception if any database errors were encountered
   */
  public Object findBy( Class clsID, String strFindById, Object objParams, Object objBean ) throws Exception
  {
    if ( s_sqlMappingDictionary == null )
    {
      throw new Exception( m_dbMsgs.getString( "Vw.SqlMgr.NoDict"));
    }

    String strId = findClassSqlMapping( clsID ).getName();

    String strSql = s_sqlMappingDictionary.getQuery( strId, strFindById );

    if ( VwExString.count( strSql, ';' ) > 0   )
    {
      List listSqlData = getSqlDataList( strId, strFindById, strSql );
      
      objBean = VwOrmBuilder.buildObject( objParams, m_db, listSqlData, m_logger, objBean );
      if ( objBean instanceof List )
      {
        throw new Exception( "Expected only one object of type '" + clsID.getName() + "' for param values " + objParams.toString() + " to be returned but got a count of " + ((List)objBean).size() );
      }
      
      return objBean;
      
    } // end if
    
    exec( strSql, objParams );
    
    objBean = getNext( clsID, objBean );
   
    return objBean;
    
  } // end findBy()


  /**
   * Retrieves one object of the type specicied by the clsId param. This mthod should only be used
   * when only one object is expected from the query
   * 
   * @param strID The id entry in the sql mapping document of the sql to retrieve
   * @param clsID The Object class for the object type to be returned
   * @param strFindById The finder id to use for the lookup
   * 
   * @param objParams any object contatining the bean properties used to resolve where clause parameters
   * @return an objectsof type clsId or null if no rows were found
   *  
   * @throws Exception if any database errors were encountered
   */
  public Object findBy( String strID, Class clsID, String strFindById, Object objParams ) throws Exception
  {
    if ( s_sqlMappingDictionary == null )
      throw new Exception( m_dbMsgs.getString( "Vw.SqlMgr.NoDict"));

    Object objBean = null;

    String strSql = s_sqlMappingDictionary.getQuery( strID, strFindById );
    
    if ( VwExString.count( strSql, ';' ) > 0   )
    {
      List listSqlData = getSqlDataList( clsID.getName(), strFindById, strSql );
      
      Object objResult = VwOrmBuilder.buildObject( objParams, m_db, listSqlData, m_logger, null );
      if ( objResult instanceof List )
        throw new Exception( "Expected only one object of type '" + clsID.getName() + "' to be returned but got a count of " + ((List)objResult).size() );
      
      return objResult;
      
    } // end if
    
    exec( strSql, objParams );
    
    Object objResult = getNext( clsID, objBean );
   
    return objResult;
    
  } // end findBy()


  /**
   * Just get the row for the super class object's sub class
   *
   * @param objBean
   * @return
   * @throws Exception
   */
  public void getChildObject( Object objBean ) throws Exception
  {
    if ( s_sqlMappingDictionary == null )
    {
      throw new Exception( m_dbMsgs.getString( "Vw.SqlMgr.NoDict"));
    }

    Class clsID = findClassSqlMapping( objBean.getClass() );
    String strId = clsID.getName();

    String strFindById = "primaryKey";

    String strSql = s_sqlMappingDictionary.getQuery( strId, strFindById );

    if ( VwExString.count( strSql, ';' ) > 0   )
    {
      List listSqlData = getSqlDataList( strId, strFindById, strSql );

      objBean = VwOrmBuilder.buildObject( objBean, m_db, listSqlData, m_logger, objBean );
      if ( objBean instanceof List )
      {
        throw new Exception( "Expected only one object of type '" + objBean.getClass().getName() + "' for param values " + objBean.toString() + " to be returned but got a count of " + ((List)objBean).size() );
      }


    } // end if

    exec( strSql, objBean );

    getNext( clsID, objBean );


  } // end findBy()

  /**
   * 
   * @param strID  The mapping id
   * @param strFindById  The findBy id as specified in the finder xml tag
   * @param strSql The sql for the mapping
   * @return
   * @throws Exception
   */
  private List<VwSqlData> getSqlDataList( String strID, String strFindById, String strSql ) throws Exception
  {
    Map<String,List<VwSqlData>> mapSqlData = s_mapSqlMappings.get( strID );
    List<VwSqlData> listSqlData = null;
    
    if ( mapSqlData == null )
    {
      mapSqlData = new HashMap<String,List<VwSqlData>>();
      s_mapSqlMappings.put( strID, mapSqlData );
    }
    
    listSqlData = mapSqlData.get( strFindById );
    
    if ( listSqlData == null )
    {
      listSqlData = VwOrmBuilder.buildMappings( m_db, strSql );
      mapSqlData.put( strFindById, listSqlData );
      
    }
    
    return listSqlData;
    
 }

  


  /**
   * Retrieves one object of the type specicied by the clsId param. This mthod should only be used
   * when only one object is expected from the query
   * 
   * <br>When the class type of the objParams object is the same as the mapping id return object
   * <br>Then use this method instead of one that requires the mapping id
   * 
   * @param strFindById The finder id to use for the lookup
   * 
   * @param objParams any object containing the bean properties used to resolve where clause parameters
   * @return an objectsof type clsId or null if no rows were found
   *  
   * @throws Exception if any database errors were encountered
   */
  public Object findBy( String strFindById, Object objParams ) throws Exception
  { return findBy( objParams.getClass(), strFindById, objParams ); }
  
  /**
   * Uses the finderId to retrieve one or more objects and place them in a List.
   * 
   * @param clsID The Object class for the object type to be returned
   * @param strFindById The finder id to use for the lookup
   * 
   * @param objParams any object contatining the bean properties used to resolve where clause parameters
   * @return a list of one or more objects of type clsId or null if no rows were found
   *  
   * @throws Exception if any database errors were encountered
   */
  public List<?> findAllBy( Class<?> clsID, String strFindById, Object objParams ) throws Exception
  { return findAllBy( clsID.getName(), clsID, strFindById, objParams ); }


  /**
   * Uses the finderId to retrieve one or more objects and place them in a List.
   * 
   * @param clsID The Object class for the object type to be returned
   * @param strFindById The finder id to use for the lookup
   * 
   * @param objParams any object containing the bean properties used to resolve where clause parameters
   * @return a list of one or more objects of type clsId or null if no rows were found
   *  
   * @throws Exception if any database errors were encountered
   */
  public List<?> findAllBy( String strID, Class<?> clsID, String strFindById, Object objParams ) throws Exception
  {
    if ( s_sqlMappingDictionary == null )
    {
      throw new Exception( m_dbMsgs.getString( "Vw.SqlMgr.NoDict"));
    }

    Object objBean = null;

    String strMappingId = strID;

    String strSql = s_sqlMappingDictionary.getQuery( strMappingId, strFindById );

    if ( VwExString.count( strSql, ';' ) > 0  )
    {
      List<VwSqlData> listSqlData = getSqlDataList( strMappingId, strFindById, strSql );
      
      List listResult = null;
      
      objBean = VwOrmBuilder.buildObject( objParams, m_db, listSqlData, m_logger, objBean );
      if ( objBean == null )
      {
        return null;
      }
      
      if (! (objBean instanceof List) )
      {
        listResult = new ArrayList( 1 );
        listResult.add( objBean );
      }
      else
      {
        listResult = (List)objBean;
      }

      return listResult;
      
    } // end if
    
    exec( strSql, objParams );
    
    Object objResult = null;
    
    List listResult = new ArrayList();
    
    while( (objResult = getNext( clsID, objBean ) ) != null )
    {
      listResult.add( objResult );
    }
    
    if ( listResult.size() == 0 )
    {
      return null;
    }
    
   
    return listResult;
    
  } // end findAllBy()
    
  /**
   *
   *
   * Uses the "base" sql in the mapping entry (which has no constraints) to retrieve one or more objects and place them in a List.
   *
   * @param clsId Object class for the object type to be returned
   * @param objParams any object contatining the bean properties used to resolve where clause parameters
   * @return a list of one or more objects of type clsId or null if no rows were found
   *  
   * @throws Exception if any database errors were encountered
   */
  public List<?> findAll( Class<?> clsId, Object objParams ) throws Exception
  { return findAllBy( clsId, "all", objParams ); }

  /**
   * Get all rows for a mapping id
   * @param clsId The mapping id represented by the object class
   * @return
   * @throws Exception
   */
  public List<?> findAll( Class<?> clsId ) throws Exception
  { return findAllBy( clsId, "all", null ); }

  /**
   * Find all rows for a mapping id
   * @param strMappingId The mapping id
   * @param clsRetType The class type of the returned objects
   * @return
   * @throws Exception
   */
  public List<?> findAll( String strMappingId, Class<?>clsRetType ) throws Exception
  { return findAllBy( strMappingId, clsRetType, "all", null ); }

 /**
   * Uses the "base" sql in the mapping entry (which has no constraints) to retrieve one or more objects and place them in a List.
   * <br>When the class type of the objParams object is the same as the mapping id return object
   * <br>Then use this method instead of one that requires the mapping id
   * 
   * @param objParams any object contatining the bean properties used to resolve where clause parameters
   * @return a list of one or more objects of type clsId or null if no rows were found
   *  
   * @throws Exception if any database errors were encountered
   */
  public List<?> findAll( Object objParams ) throws Exception
  { return findAllBy( objParams.getClass(), "all", objParams ); }
  
  /**
   * Uses the finderId to retrieve one or more objects and place them in a List.
   * <br>When the class type of the objParams object is the same as the mapping id return object
   * <br>Then use this method instead of one that requires the mapping id
   * 
   * @param strFindById The finder id to use for the lookup
   * 
   * @param objParams any object contatining the bean properties used to resolve where clause parameters
   * @return a list of one or more objects of type clsId or null if no rows were found
   *  
   * @throws Exception if any database errors were encountered
   */
  public List<?> findAllBy( String strFindById, Object objParams ) throws Exception
  { return findAllBy( objParams.getClass(), strFindById, objParams ); }

  
  
  /**
   * Executes the SQL using the object passed to supply the input data
   * @param strSQL The SQL string to execute
   * @param objData Either an VwDataObject or a Java bean (POJO) that supplies the input
   * <br>parameters specified in the SQL string
   * 
   * @throws VwDataSourceException
   * @throws SQLException
   * @throws Exception
   */
  public final void exec( String strSQL, Object objData  )throws VwDataSourceException,
                                                                  SQLException, Exception

  { exec( strSQL, objData, false ); }
  

  /**
   * Excecutes the SQL statement
   *
   * @param strSQL - The SQL statement or stored procedure to execute
   * @param objData - If passed use that instance for storing results else create a new one.
   * for each getNext() call. NOTE ! The VwDataObject is required if the sql statement contains
   * input paramters that need values bound at runtime prior to execution of the
   * SQL Statement or stored procedure.
   *
   * @param fReuseDataObj - If True, re-use the data object passed for input to be used for
   * result data from the successfull exceution of the SQL statement.
   *
   * @exception Exception for any database errors
   */
  public final void exec( String strSQL, Object objData, boolean fReuseDataObj  ) throws Exception

  {

    close();


    m_sqlData = (VwSqlData)s_mapSqlDataCache.get( strSQL );

    if ( m_sqlData == null )
    {

      if ( !m_fIgnoreParser )
      {
        m_sqlParser = new VwSqlParser( m_db, strSQL );
        m_sqlData = m_sqlParser.getParsedSqlData();
      }
      else
      {
        m_sqlData = new VwSqlData();
        m_sqlData.m_strSQL = strSQL.trim();

        if ( m_sqlData.m_strSQL.length() > "select".length() )
        {
          if ( m_sqlData.m_strSQL.substring( 0, 6 ).equalsIgnoreCase( "select" ))
          {
            m_sqlData.m_nStmtType = VwSqlParser.SELECT;
          }

        }
      }

      m_sqlData.m_fReuseDataObj = fReuseDataObj;
      s_mapSqlDataCache.put( strSQL, m_sqlData );


    }

    exec( m_sqlData, objData );
    

  } // end exec()



  /**
   * Excecutes an already prepared SQL statememnt. This method is used to rebind dynamic
   * parameter data to an already prepared SQL statement from a previos exec call that
   * pased the Sql statement to be processed. This form of exec is typicially used when
   * inserting or updating rows in a table or repeatedly calling the same stored procedure
   * with different parameter data.
   *
   * @param objData - If passed use that instance for storing results else create a new one.
   * for each getNext() call. NOTE ! The VwDataObject is required if the sql statement contains
   * input paramters that need values bound at runtime prior to execution of the
   * SQL Statement or stored procedure.
   *
   * @param fReuseDataObj - If True, re-use the data object passed for input to be used for
   * result data from the successfull exceution of the SQL statement.
   *
   * @exception Exception for any errors
   */
  public final void exec( Object objData, boolean fReuseDataObj  ) throws Exception
  {

    if ( m_sqlData == null )
      throw new Exception( m_dbMsgs.getString( "Vw.Db.InvalidState" ) );

    m_sqlData.m_fReuseDataObj = fReuseDataObj;

    exec( m_sqlData, objData );

  } // end exec()


  /**
   * Excecutes the SQL statement. This version of exec is used when a statement has been
   * previously parsed with the VwSqlParser class.
   *
   * @param sqlData - The SQL data class that contains a parsed SQL statement
   * from a previos call to the VwSqlParser class
   * @param objData - If passed use that instance for storing results else create a new one.
   * for each getNext() call. NOTE ! The VwDataObject is required if the sql statement contains
   * input paramters that need values bound at runtime prior to texecution of the
   * SQL Statement or stored procedure.
   *
   * @exception Exception for any database errors
   */
  public final void exec( VwSqlData sqlData, Object objData  ) throws Exception
  {


    m_sqlData = sqlData;

    m_nGetCount = 0;

    long lStartQueryTime = 0;

    if ( objData != null && objData instanceof VwServiceable)
    {
      // Update service flags from incomming data object
      m_serviceFlags.set( (VwServiceable)objData );

    } // end if


    if ( m_sqlData.m_strSQL.startsWith( "{" ) )
    {
      doStoredProc( objData );
      if ( objData instanceof VwDVOBase )
      {
        ((VwDVOBase)objData).setDirty( false );
      }
      
      return;
    }


    try
    {
      if ( m_rs != null )
      {
        m_rs.close();

      }

      Connection con = m_db.getConnection();
      VwDate dt = null;
      
      String strSql = m_sqlData.m_strSQL;
      
      if ( m_sqlData.m_fIsDynamicWhere )
      {
        VwDynamicWhereCallBack dhcb= (VwDynamicWhereCallBack)m_mapDynamicWhereListeners.get( m_strFinderId );
        String strWhere = dhcb.resolve( m_strFinderId );
        
        strSql = VwExString.replace( strSql, "itccallback", strWhere );
        
      }
      
      // see if it's in cache
      m_ps = (PreparedStatement)m_sqlData.getStatement( m_db );
        
      if ( m_ps == null )
      {
         m_ps = con.prepareStatement( strSql, m_nResultSetType, m_nResultSetConcur );
          
         if ( m_fStatementCaching )
         {
           m_sqlData.cacheStatement( m_db, m_ps );
         }
      }
      else
      {
        m_ps.clearParameters();
      }
      

      if ( objData == null &&  m_sqlData.m_listParams != null )
      {
        if ( m_logger != null && m_logger.isDebugEnabled() && m_logger.getDebugVerboseLevel() >= 3 )
        {
          m_logger.debug( this.getClass(), "Trying to bind with null object for expected parameters" );
        }

      }

      if ( m_logger != null && m_logger.isDebugEnabled() )
      {
        m_logger.debug( this.getClass(), "About to execute statement\n" + m_sqlData.m_strSQL + "\n Using Object " + objData );
      }


      // Bind input parameters if data object specified
      if ( objData != null )
      {
        if ( m_sqlData.m_listParams != null )
        {

          if ( m_logger != null && m_logger.isDebugEnabled() && m_logger.getDebugVerboseLevel() >= 3 )
          {
            m_logger.debug( this.getClass(), "\nStatement has : " + m_sqlData.m_listParams.size() + " to bind:" );

          }

          for ( int x = 0; x < m_sqlData.m_listParams.size(); x++ )
          {

            if ( m_logger != null && m_logger.isDebugEnabled() && m_logger.getDebugVerboseLevel() >= 3 )
            {
              m_logger.debug( this.getClass(), "In for loop for x: " + x );

            }

            String strParamName = (String)m_sqlData.m_listParams.get( x );

            if ( m_logger != null && m_logger.isDebugEnabled() && m_logger.getDebugVerboseLevel() >= 3 )
             {
               m_logger.debug( this.getClass(), "Got param name: " + strParamName + " for param: " + x );

             }

            Object objValue = null;

            if ( objData instanceof VwDataObject )
            {
              objValue = ((VwDataObject)objData).get( strParamName );
            }
            else
            if ( objData instanceof Map )
            {
              objValue = ((Map)objData).get( strParamName );
            }
            else
            if ( objData != null && VwBeanUtils.isSimpleType( objData.getClass()) )
            {
              objValue = objData;
            }
            else
            {
              objValue = VwBeanUtils.getValue( objData, strParamName );
            }
             
            if ( objValue instanceof VwDate )
            {
              objValue = ((VwDate)objValue).toTimestamp();
            }
            else
            if ( objValue instanceof Calendar )
            {
              dt = new VwDate( (Calendar)objValue );
              objValue = dt.toTimestamp();
            }
            else
            if ( objValue instanceof java.util.Date )
            {
              dt = new VwDate( (java.util.Date)objValue );
              objValue = dt.toTimestamp();
            }
            

            if ( objValue != null )
            {
              if ( m_logger != null && m_logger.isDebugEnabled() && m_logger.getDebugVerboseLevel() >= 3 )
              {
                m_logger.debug( this.getClass(), "\nBinding Param " + (x + 1) + " with " + objValue );
              }

              if ( objData instanceof VwDVOBase )
              {
                ((VwDVOBase)objData).clearDirty( strParamName );
              }

              m_ps.setObject( x + 1, objValue );
            }
            else
            {
              if ( m_db.getDatabaseType() == VwDatabase.ORACLE )
              {
                if ( m_logger != null && m_logger.isDebugEnabled() && m_logger.getDebugVerboseLevel() >= 3 )
                {
                  m_logger.debug( this.getClass(), "\nSetting param " + ( x + 1 ) + " to -1" );
                }

                m_ps.setNull( x + 1, -1 );
              }
              else  
              if ( m_db.getDatabaseType() == VwDatabase.POSTGRESQL )
              {
                if ( m_logger != null && m_logger.isDebugEnabled() && m_logger.getDebugVerboseLevel() >= 3 )
                {
                  m_logger.debug( this.getClass(), "\nSetting Param " + ( x + 1 ) + " to Types.OTHER" );
                }

                m_ps.setNull( x + 1, Types.OTHER );
              }
              else
              {
                if ( m_logger != null && m_logger.isDebugEnabled() && m_logger.getDebugVerboseLevel() >= 3 )
                {
                  m_logger.debug( this.getClass(), "\nSetting Param " + ( x + 1 ) + " to -1" );
                }

                m_ps.setNull( x + 1, -1 );
              }

            }

          } // end for()

        } // end if

      }  // end if

      if ( m_logger != null && m_logger.isDebugEnabled() && m_logger.getDebugVerboseLevel() >= 2  )
      {
        lStartQueryTime = System.currentTimeMillis();

        ParameterMetaData pmd =  m_ps.getParameterMetaData();

        s_mapQueriesByThreadId.put( Thread.currentThread().getId(), m_sqlData.m_strSQL );

        m_logger.debug( this.getClass(), "About to execute " + m_sqlData.m_strSQL + " with " + pmd.getParameterCount() + " parameters");

      }


      if ( m_ps.execute() )
      {
        if ( m_logger != null && m_logger.isDebugEnabled() && m_logger.getDebugVerboseLevel() >= 2 )
        {
          m_logger.debug( this.getClass(), "Query " + s_mapQueriesByThreadId.get( Thread.currentThread().getId() ) + " took: " + (System.currentTimeMillis() - lStartQueryTime) + " milliseconds" );
          s_mapQueriesByThreadId.remove( Thread.currentThread().getId() );
        }

        m_rs = m_ps.getResultSet();
        m_rsMeta = m_rs.getMetaData();

      }

      else
      {
        if ( m_logger != null && m_logger.isDebugEnabled() && m_logger.getDebugVerboseLevel() >= 2 )
        {
          m_logger.debug( this.getClass(), "Query " + s_mapQueriesByThreadId.get( Thread.currentThread().getId() ) + " took: " + (System.currentTimeMillis() - lStartQueryTime) + " milliseconds" );
          s_mapQueriesByThreadId.remove( Thread.currentThread().getId() );
        }

        m_nRowCount = m_ps.getUpdateCount();
      }

      m_nGetCount = 0;

      /* todo this should probably be deleted
      if ( sqlData.m_nStmtType == VwSqlParser.INSERT || sqlData.m_nStmtType == VwSqlParser.UPDATE )
      {
        if ( objData instanceof VwDVOBase )
        {
          ( (VwDVOBase) objData ).setDirty( false );
        }

      }
      */

    } // end try
    catch( Exception ex )
    {

      if ( m_logger != null )
      {
        m_logger.error( VwSqlMgr.class, ex.toString(), ex );
      }

      if ( ex instanceof  SQLException )
      {


        SQLException sqleNext = (SQLException) ex;

        while ( true )
        {
          sqleNext = sqleNext.getNextException();
          if ( sqleNext == null )
          {
            break;
          }

          if ( m_logger != null )
          {
            m_logger.error( VwSqlMgr.class, sqleNext.toString(), sqleNext );
          }

        }
      }

      if ( m_rs != null )
      {
        m_rs.close();
        m_rs = null;
      }

      if ( ex instanceof SQLException )
      {
        m_db.getDbMgr().handleException( (SQLException)ex );
      }

      throw ex;
    } // end catch

  } // end exec()


  /**
   * Cancel the current runnong query
   * @throws Exception
   */
  public void cancel() throws Exception
  {
    if ( m_ps != null )
    {
      m_ps.cancel();
    }
  }


  /**
   * Returns the number of rows affected by an insert, update, or delete statement
   *
   * @return An integer with the number of rows affected by the SQL statement
   */
  public final int getRowsAffected()
  { return m_nRowCount; }


  /**
   * Get the VwSqlData object ( parsed meta data for a sql statement ). This will be valid
   * after successfull completeion of the exec method.
   *
   * @return the VwSqlData object or null.
   */
  public VwSqlData getSqlData()
  { return m_sqlData; }


  /*
   * Determines if the SQL statement was a select statement returning a result set
   *
   * @return True if the SQL statement was a select statement returning a result set;
   * otherwise, False is returned.
   */
  public final boolean isResSet()
  { return (m_rs != null || m_cs != null); }


  /**
   * Returns the result as an xml document.
   *
   * @param strParentTag The parent tag to place the resultset under (Optional).
   * @param strDefaultForNull Default data string to use for null data
   * @param nMaxRows The max rows to return in this call
   * @param fPreserveParentOnNoData if true and there is no resultset, return an xml string<br>
   * with the strParentTag preserved otherwise return null
    */
  public String toXml( String strParentTag, String strDefaultForNull,
                       int nMaxRows, boolean fPreserveParentOnNoData ) throws Exception
  { return toXml( strParentTag, strDefaultForNull, nMaxRows, false, 0, fPreserveParentOnNoData ); }


  /**
   * Returns the result as an xml document.
   *
   * @param strParentTag The parent tag to place the resultset under (Optional).
   * @param strDefaultForNull Default data string to use for null data
   * @param nMaxRows The max rows to return in this call
    */
  public String toXml( String strParentTag, String strDefaultForNull,
                       int nMaxRows ) throws Exception
  { return toXml( strParentTag, strDefaultForNull, nMaxRows, false, 0, false ); }


  /**
   * Returns the result as an xml document.
   *
   * @param strParentTag The parent tag to place the resultset under (Optional).
   * @param strDefaultForNull Default data string to use for null data
   * @param nMaxRows The max rows to return in this call
   * @param fFormatted If true format trhe xml with CR/LF characters and indentation based on tag parentage
   * @param nIndentLevel The indetation level. Each level number equates to two spaces.
    */
  public String toXml( String strParentTag, String strDefaultForNull,
                       int nMaxRows, boolean fFormatted, int nIndentLevel ) throws Exception
  { return toXml( strParentTag, strDefaultForNull, nMaxRows, fFormatted, nIndentLevel, false ); }


  /**
   * Returns the result as an xml document.
   *
   * @param strParentTag The parent tag to place the resultset under (Optional).
   * @param strDefaultForNull Default data string to use for null data
   * @param nMaxRows The max rows to return in this call
   * @param fFormatted If true format trhe xml with CR/LF characters and indentation based on tag parentage
   * @param nIndentLevel The indetation level. Each level number equates to two spaces.
   * @param fPreserveParentOnNoData if true and there is no resultset, return an xml string<br>
   * with the strParentTag preserved otherwise return null
   */
  public String toXml( String strParentTag, String strDefaultForNull,
                       int nMaxRows, boolean fFormatted, int nIndentLevel, boolean fPreserveParentOnNoData )
    throws VwDbServerNotAvailException,
           VwDbInvalidSessionException,
           VwDbInvalidUidPwdException,
           VwDbDupKeyException,
           SQLException,
           Exception
  {

    VwXmlWriter xmlf = new VwXmlWriter( fFormatted, nIndentLevel );

    int nRowCtr = 0;

    boolean fHadException = false;

    String strData = null;

    try
    {
      while ( true )
      {

        if ( m_rs == null && m_cs == null )
          throw new SQLException( m_dbMsgs.getString( "VwDb.NotSelectStatement" ), "S1000", 1000 );

        if ( m_sqlData.m_listResults == null )
          throw new Exception( "Missing a result set list" );

        try
        {
          // *** If m_cs is not null then we're a stored procedure

          if ( m_cs != null )
          {
            ++nRowCtr;

            if ( ++m_nGetCount > 1 )
            {
              if ( m_fCloseOnExecute )
              {
                m_cs.close();
                m_cs = null;
              }
              break;
            }

          } // end if ( m_cs != null )
          else
          if ( m_rs != null )
          {
            if ( !m_rs.next() )
            {

              m_rs.close();
              m_rs = null;
              m_rsMeta = null;
              break;
            }

            ++nRowCtr;

            // Get nbr of columns in the result set
            int nColCount = m_rsMeta.getColumnCount();

            if ( m_sqlData.m_asResultTypes == null )
            {
              m_sqlData.m_asResultTypes = new short[ nColCount ];
              for ( int x = 0; x < nColCount; x ++ )
                m_sqlData.m_asResultTypes[ x ] = (short)m_rsMeta.getColumnType( x + 1 );
            }

          } //end if ( m_rs != null )

        } // end try
        catch( SQLException sqle )
        {
          fHadException = true;
          m_db.getDbMgr().handleException( sqle );
          return null;
        }
        catch( Exception e )
        {
          fHadException = true;
          throw e;
        }

       if ( strParentTag != null )
         xmlf.addParent( strParentTag, null ); // Each row is under an xml Record tag

        // Get the resultset
       for ( int x = 0; x < m_sqlData.m_listResults.size(); x++ )
       {

          int nParamNbr = x + 1;

          if ( m_cs != null )
            nParamNbr = m_sqlData.m_asResultParamNbr[ x ];

          switch( m_sqlData.m_asResultTypes[ x ] )
          {

            case Types.TIMESTAMP:
            case Types.DATE:
            case Types.TIME:

                 Object objDate = null;

                 if ( m_cs != null )
                   objDate = m_cs.getObject( nParamNbr );
                 else
                   objDate = m_rs.getObject( nParamNbr );


                 if ( objDate != null )
                 {
                   
                   VwDate dt = null;
                   
                   if ( objDate instanceof Timestamp)
                    dt = new VwDate( (Timestamp)objDate );
                   else
                   if ( objDate instanceof Date)
                     dt = new VwDate( (Date)objDate );
                   else
                     throw new Exception( "Can't convert date object " + objDate.getClass().getName() + " to VwDate ");

                   xmlf.addChild( (String)m_sqlData.m_listResults.get( x ), dt.format( m_strSourceDateFormat ), null );
                 }
                 else
                 {
                   if ( strDefaultForNull == null )
                     break;

                   xmlf.addChild( (String)m_sqlData.m_listResults.get( x ), strDefaultForNull, null );

                 }

                 break;

            default:

                if ( m_cs != null )
                  strData = m_cs.getString( nParamNbr );
               else
                  strData = m_rs.getString( nParamNbr );

               if ( strData != null )
                 xmlf.addChild( (String)m_sqlData.m_listResults.get( x ), strData, null );
               else
               {
                 if ( strDefaultForNull == null )
                   break;

                 xmlf.addChild( (String)m_sqlData.m_listResults.get( x ), strDefaultForNull, null );
               }

               break;

          } // end switch()

        } // end for()

       if ( strParentTag != null )
         xmlf.closeParent( strParentTag ); // Each row is under an xml Record tag

        // See if row blocks were defined
        if ( nMaxRows > 0 && nRowCtr == nMaxRows )
          break;


      }  // end while()

    } // end try
    finally
    {
      if ( m_cs != null && fHadException )
      {
        m_cs.close();
        m_cs = null;
      }
      else
      if ( m_rs != null && fHadException )
      {
        m_rs.close();
        m_rs = null;
        m_rsMeta = null;
      }

    } // end finally

    // If resultset is empty and the don't preserve parent tag is false, return null
    if ( nRowCtr == 0 && !fPreserveParentOnNoData )
      return null;

    return xmlf.getXml();

  } // end toXml()


  /**
   * Return a comma delimited string of sql data types as defined in Types.java for a result
   * set returned by a select or stored procedure call statement
   * @return
   */
  public VwDelimString getResultSetTypes() throws Exception
  {

    VwDelimString dlms = new VwDelimString();

    if ( m_rsMeta == null )
      return null;

    for ( int x = 1; x <= m_rsMeta.getColumnCount(); x++ )
      dlms.add( String.valueOf( m_rsMeta.getColumnType( x ) ) );

    return dlms;

  } // end getResultSetTypes()

  /**
   * Retursn the size of the result set
   * @return
   * @throws Exception
   */
  public int getResultSetSize() throws Exception
  {
    if ( m_rs == null )
      throw new Exception( "No ResultSet exists");

    return m_rs.getFetchSize();

  } // end getResultSetSize()


  /**
   * Updates the object in the current result set for the row nbr specified.. The following rules apply.<br>
   *   1. Prior to executing the select statement you must call the setResultSetType method with either <br>
   *     ResultSet.TYPE_SCROLL_INSENSITIVE, or ResultSet.TYPE_SCROLL_SENSITIVE as well as the setResultSetConcurrency method<br>
   *     with the value ResultSet.CONCUR_UPDATABLE<br>
   *
   *   2.There must be an active result set open
   *
   *
   * @param nRowNbr The row nbr in the result set to update
   *
   * @throws Exception
   */
  public void deleteResultSetRow( int nRowNbr ) throws Exception
  {
    if ( m_rs == null  )
      throw new SQLException( m_dbMsgs.getString( "VwDb.NotSelectStatement" ), "S1000", 1000 );

    m_rs.absolute( nRowNbr );
    m_rs.deleteRow();

  } // end insertResultSetRow()



  /**
   * Updates the object in the current result set for the row nbr specified.. The following rules apply.<br>
   *   1. Prior to executing the select statement you must call the setResultSetType method with either <br>
   *     ResultSet.TYPE_SCROLL_INSENSITIVE, or ResultSet.TYPE_SCROLL_SENSITIVE as well as the setResultSetConcurrency method<br>
   *     with the value ResultSet.CONCUR_UPDATABLE<br>
   *
   *   2.There must be an active result set open
   *
   *
   * @param nRowNbr The row nbr in the result set to update
   * @param objToInsert The data for that row to update
   *
   * @throws Exception
   */
  public void updateResultSetRow( int nRowNbr, Object objToInsert ) throws Exception
  {
    if ( m_rs == null  )
      throw new SQLException( m_dbMsgs.getString( "VwDb.NotSelectStatement" ), "S1000", 1000 );

    m_rs.absolute( nRowNbr );
    updateResultSetColumns( objToInsert );
    m_rs.updateRow();

  } // end insertResultSetRow()



  /**
   * Inserts the object into the current result set. The following rules apply.<br>
   *   1. Prior to executing the select statement you must call the setResultSetType method with either <br>
   *     ResultSet.TYPE_SCROLL_INSENSITIVE, or ResultSet.TYPE_SCROLL_SENSITIVE as well as the setResultSetConcurrency method<br>
   *     with the value ResultSet.CONCUR_UPDATABLE<br>
   *
   *   2.There must be an active result set open
   *
   *
   *
   * @param objToInsert
   * @throws Exception
   */
  public void insertResultSetRow( Object objToInsert ) throws Exception
  {
    if ( m_rs == null  )
      throw new SQLException( m_dbMsgs.getString( "VwDb.NotSelectStatement" ), "S1000", 1000 );

    m_rs.moveToInsertRow();
    int nRowNbr = m_rs.getRow();

    updateResultSetColumns( objToInsert );
    m_rs.insertRow();

  } // end insertResultSetRow()


  /**
   * Update the result row columns form data in the input object
   *
   * @param objInput The input data object with the values to updatre the columns
   * @throws Exception
   */
  private void updateResultSetColumns( Object objInput ) throws Exception
  {
    // Bind input parameters if data object specified
    if ( m_sqlData.m_listResults != null )
    {
      int nColCount = m_rsMeta.getColumnCount();

      if ( m_sqlData.m_asResultTypes == null )
      {
        m_sqlData.m_asResultTypes = new short[ nColCount ];
        for ( int x = 0; x < nColCount; x ++ )
          m_sqlData.m_asResultTypes[ x ] = (short)m_rsMeta.getColumnType( x + 1 );
      }


      for ( int x = 0; x < m_sqlData.m_listResults.size(); x++ )
      {
        String strParamName = (String)m_sqlData.m_listResults.get( x );
        Object objValue = null;

        if ( objInput instanceof VwDataObject )
          objValue = ((VwDataObject)objInput).get( strParamName );
        else
          objValue = VwBeanUtils.getValue( objInput, strParamName );

        if ( objValue != null && objValue.toString().length() == 0 )
          objValue = null;

        switch ( m_sqlData.m_asResultTypes[ x ] )
        {
          case Types.TIMESTAMP:
          case Types.DATE:

               VwDate dt = null;

               if ( objValue != null )
               {
                 if ( objValue instanceof String )
                  dt = new VwDate( (String)objValue, m_strSourceDateFormat );
                 else
                 if ( objValue instanceof VwDate )
                   dt = (VwDate)objValue;

                 if ( dt != null )
                  objValue = dt.toTimestamp();

               } // end if

           default:

               if ( objValue == null )
                 m_rs.updateNull( x + 1 );
               else
               {
                 //System.out.println( "Setting obj param " + objValue.toString() );
                 m_rs.updateObject( x + 1, objValue );
               }
               break;
        }

      } // end for()

    } // end if

  } // end updateResultSetColumns()

  /**
   * Gets the next row in the result set or fetches the output parammaters of a stored procedure call
   *
   * @return The object instance of the Class type specified if successful or null if EOF
   *
   * @throws VwDbServerNotAvailException
   * @throws VwDbInvalidSessionException
   * @throws VwDbInvalidUidPwdException
   * @throws VwDbDupKeyException
   * @throws SQLException
   * @throws Exception
   */
  public final VwDataObject getNext()
    throws  VwDbServerNotAvailException,
					  VwDbInvalidSessionException,
					  VwDbInvalidUidPwdException,
					  VwDbDupKeyException,
					  SQLException,
					  Exception
  {  return get( NEXT, 0 ); }


  /**
   * Gest the previous row in the result set. This call is not valid for stored procedure calls
   *
   * NOTE! this method is only valid when the setResultSetType method has beem=n called with the values<br>
   * ResultSet.TYPE_SCROLL_INSENSITIVE, or ResultSet.TYPE_SCROLL_SENSITIVE
   *
   * @return The object instance of the Class type specified if successful or null if EOF
   *
   * @throws VwDbServerNotAvailException
   * @throws VwDbInvalidSessionException
   * @throws VwDbInvalidUidPwdException
   * @throws VwDbDupKeyException
   * @throws SQLException
   * @throws Exception
   */
  public final VwDataObject  getPrevious()
    throws  VwDbServerNotAvailException,
					  VwDbInvalidSessionException,
					  VwDbInvalidUidPwdException,
					  VwDbDupKeyException,
					  SQLException,
					  Exception
  { return get( PREV, 0 ); }


  /**
   * Gets the absolute row in the result set or fetches the output parammaters of a stored procedure call
   *
   * @param nRowNbr The absolute row nbr in the result set to fetch. <br>
   * NOTE! this method is only valid when the setResultSetType method has beem=n called with the values<br>
   * ResultSet.TYPE_SCROLL_INSENSITIVE, or ResultSet.TYPE_SCROLL_SENSITIVE
   *
   * @return The object instance of the Class type specified if successful
   *
   * @throws VwDbServerNotAvailException
   * @throws VwDbInvalidSessionException
   * @throws VwDbInvalidUidPwdException
   * @throws VwDbDupKeyException
   * @throws SQLException
   * @throws Exception
   */
  public final VwDataObject  getAbsolute( int nRowNbr )
    throws  VwDbServerNotAvailException,
					  VwDbInvalidSessionException,
					  VwDbInvalidUidPwdException,
					  VwDbDupKeyException,
					  SQLException,
					  Exception
  { return get( ABS, nRowNbr ); }

  /**
   * Returns the next Result Set row in an VwDataObject
   *
   * @param nDirectionInd The fecth direction. One of the static constants defined
   *
   * @param nRowNbr This is used if the fetch type is ABS (Absolute row fetch) and is the row nbr in the resultset to fetch
   * @return An VwDataObject containing the next row in the Result Set, or null if at the end
   * of the Result Set.
   *
   * @exception Exception for any database errors
   */
  private final VwDataObject get( int nDirectionInd, int nRowNbr ) throws Exception
  {
    if ( m_rs == null && m_cs == null )
    {
      throw new SQLException( m_dbMsgs.getString( "VwDb.NotSelectStatement" ), "S1000", 1000 );
    }


    VwDataObject  dataObj = null;

    if ( m_sqlData.m_fReuseDataObj )
    {
      if ( !(m_sqlData.m_objData instanceof VwDataObject) )
      {
        throw new Exception( "Resue VwDataObject was specified in exec call but an object of type " +
                               m_sqlData.m_objData.getClass().getName() + " was used" );
      }

      dataObj = (VwDataObject)m_sqlData.m_objData;

    }
    else
    {
      if ( !(m_sqlData.m_objData instanceof VwDataObject) )
        m_sqlData.m_objData = null;

      if ( m_sqlData.m_objData == null )
      {
        dataObj = new VwDataObject();
      }
      else
      {
        dataObj = (VwDataObject)m_sqlData.m_objData.getClass().newInstance();
      }

      dataObj.setMaintainDataOrder();

      if ( m_serviceFlags.get() > 0  )
      {
        dataObj.setAttribute( VwServiceFlags.ITCSVCFLAGS,VwServiceFlags.ITCSVCFLAGS,
                              m_serviceFlags.toString() );
      }
    }


    // *** If m_cs is not null then we're a stored procedure

    if ( m_cs != null )
    {

      if ( nDirectionInd != NEXT )
      {
        throw new Exception( "Stored Procdeures can only be used with getNext()");
      }

      if ( ++m_nGetCount > 1 )
      {
        if ( m_fCloseOnExecute )
        {
          m_cs.close();
          m_cs = null;
        }

        return null;
      }

    } // end if ( m_cs != null )
    else
    if ( m_rs != null )
    {
      synchronized( m_sqlData )
      {
        if ( m_sqlData.m_listResults == null )
        {
          ResultSetMetaData rsm = m_rs.getMetaData();
          int nColCount = rsm.getColumnCount();
          m_sqlData.m_listResults = new ArrayList();


          for ( int x = 0; x < nColCount; x++ )
          {
            m_sqlData.m_listResults.add( rsm.getColumnLabel( x + 1 ) );
          }
        }

      }

      try
      {
        boolean fGotRow = false;

        switch( nDirectionInd )
        {
          case NEXT:

            	 fGotRow = m_rs.next();
               break;

          case PREV:

            	 fGotRow = m_rs.previous();
            	 break;

          case ABS:

            	 fGotRow = m_rs.absolute( nRowNbr );
         	     break;


        } // end switch()

        if ( !fGotRow )
        {
          if ( m_nResultSetType == ResultSet.TYPE_FORWARD_ONLY)
          {
            m_rs.close();
            m_rs = null;
            m_rsMeta = null;
          }

          return null;
        }

      }
      catch( SQLException sqle )
      {
        m_db.getDbMgr().handleException( sqle );
        return null;
      }

      int nColCount = m_rsMeta.getColumnCount();

      if ( m_sqlData.m_asResultTypes == null )
      {
        m_sqlData.m_asResultTypes = new short[ nColCount ];
        for ( int x = 0; x < nColCount; x ++ )
        {
          m_sqlData.m_asResultTypes[ x ] = (short)m_rsMeta.getColumnType( x + 1 );
        }
      }
    } // end else

    // Get result col data from fetch and put it in the dataobject
    Object resultObj = null;


    for ( int x = 0; x < m_sqlData.m_listResults.size(); x++ )
    {
        int nParamNbr = x + 1;

        if ( m_cs != null )
        {
          nParamNbr = m_sqlData.m_asResultParamNbr[ x ];
        }

        switch( m_sqlData.m_asResultTypes[ x ] )
        {

          case Types.TIMESTAMP:
          case Types.DATE:
          case Types.TIME:

               Object objDate = null;

               if ( m_cs != null )
               {
                 objDate = m_cs.getObject( nParamNbr );
               }
               else
               {
                 objDate = m_rs.getObject( nParamNbr );
               }


	           VwDate dt = null;
	           if ( objDate != null )
	           {

	             if ( objDate instanceof Timestamp)
               {
                 dt = new VwDate( (Timestamp)objDate );
               }
	             else
	             if ( objDate instanceof Date)
               {
                 dt = new VwDate( (Date)objDate );
               }
	             else
               if ( objDate.getClass().getName().equals( "oracle.sql.TIMESTAMP" ) )
               {
                 Method mthdTimeStamp = objDate.getClass().getMethod( "timestampValue", null );
                 
                 Timestamp tm = (Timestamp)mthdTimeStamp.invoke( objDate, null );
                 dt = new VwDate( tm );
                 
               }
               else    
               {
                 throw new Exception( "Can't convert date object " + objDate.getClass().getName() + " to VwDate ");
               }
	            
	           }
		           
               dataObj.put( (String)m_sqlData.m_listResults.get( x ), dt );

               break;


          default:

              if ( m_cs != null )
              {
                resultObj = m_cs.getObject( nParamNbr );
              }
              else
              {
                resultObj = m_rs.getObject( nParamNbr );
              }

              dataObj.put( (String)m_sqlData.m_listResults.get( x ), resultObj );
              
              break;

        } // end switch()

      } // end for()

      return dataObj;


  } // end getNext()


  /**
   * Gets the next row in the result set or fetches the output parammaters of a stored procedure call
   * 
   * @param clsResultBean The class of the data value object bean to return
   * 
   * @return The object instance of the Class type specified if successful or null if EOF
   * 
   * @throws VwDbServerNotAvailException
   * @throws VwDbInvalidSessionException
   * @throws VwDbInvalidUidPwdException
   * @throws VwDbDupKeyException
   * @throws SQLException
   * @throws Exception
   */
  public final Object getNext( Class clsResultBean, Object objBean )
    throws  VwDbServerNotAvailException,
					  VwDbInvalidSessionException,
					  VwDbInvalidUidPwdException,
					  VwDbDupKeyException,
					  SQLException,
					  Exception
  {  return get( NEXT, 0, clsResultBean, objBean ); }

  
  /**
   * Gest the previous row in the result set. This call is not valid for stored procedure calls
   * 
   * @param clsResultBean The class of the data value object bean to return<br>
   * NOTE! this method is only valid when the setResultSetType method has beem=n called with the values<br>
   * ResultSet.TYPE_SCROLL_INSENSITIVE, or ResultSet.TYPE_SCROLL_SENSITIVE
   * 
   * @return The object instance of the Class type specified if successful or null if EOF
   * 
   * @throws VwDbServerNotAvailException
   * @throws VwDbInvalidSessionException
   * @throws VwDbInvalidUidPwdException
   * @throws VwDbDupKeyException
   * @throws SQLException
   * @throws Exception
   */
  public final Object getPrevious( Class clsResultBean, Object objBean )
    throws  VwDbServerNotAvailException,
					  VwDbInvalidSessionException,
					  VwDbInvalidUidPwdException,
					  VwDbDupKeyException,
					  SQLException,
					  Exception
  { return get( PREV, 0, clsResultBean, objBean ); }
  
  
  /**
   * Gets the absolute row in the result set or fetches the output parammaters of a stored procedure call
   * 
   * @param clsResultBean The class of the data value object bean to return
   * @param nRowNbr The absolute row nbr in the result set to fetch. <br>
   * NOTE! this method is only valid when the setResultSetType method has beem=n called with the values<br>
   * ResultSet.TYPE_SCROLL_INSENSITIVE, or ResultSet.TYPE_SCROLL_SENSITIVE
   * 
   * @return The object instance of the Class type specified if successful
   * 
   * @throws VwDbServerNotAvailException
   * @throws VwDbInvalidSessionException
   * @throws VwDbInvalidUidPwdException
   * @throws VwDbDupKeyException
   * @throws SQLException
   * @throws Exception
   */
  public final Object getAbsolute( Class clsResultBean, int nRowNbr )  
    throws  VwDbServerNotAvailException,
					  VwDbInvalidSessionException,
					  VwDbInvalidUidPwdException,
					  VwDbDupKeyException,
					  SQLException,
					  Exception
  {
    return get( ABS, nRowNbr, clsResultBean, null );
  }
  
  /**
   * Gets the next,previous or absolute row as defined by the direction indicator.
   * 
   * @param nDirectionInd The fecth direction. One of the static constants defined
   * @param nRowNbr This is used if the fetch type is ABS (Absolute row fetch) and is the row nbr in the resultset to fetch
   * @param clsResultBean The Class type of the result object
   *
   * @return An instance of the Class specified for the result in the result set
   *
   * @exception  VwDbServerNotAvailException if the database server machine is down or not responding
   * VwDbInvalidSessionException If a cached login is no longer valid,
   * VwDbInvalidUidPwdException if the userid/password is invalid,
   * VwDBDupKeyException if a dup key constraint is violated
   * SQLException for all other database errors
   * Exception for all other errors
   */
  private final Object get( int nDirectionInd, int nRowNbr, Class clsResultBean, Object objBean )
           throws VwDbServerNotAvailException,
           VwDbInvalidSessionException,
           VwDbInvalidUidPwdException,
           VwDbDupKeyException,
           SQLException,
           Exception
  {
    if ( m_rs == null && m_cs == null )
      throw new SQLException( m_dbMsgs.getString( "VwDb.NotSelectStatement" ), "S1000", 1000 );


    Object objResultBean = null;

    // If an object is passed used that else create a new one
    if ( objBean != null )
    {
      objResultBean = objBean;
    }
    else
    {
      objResultBean = clsResultBean.newInstance();

    }


    // *** If m_cs is not null then we're a stored procedure

    if ( m_cs != null )
    {
      if ( m_sqlData.m_listResults == null )
        throw new Exception( "Missing a result set list" );
      
      if ( nDirectionInd != NEXT )
        throw new Exception( "Stored Procdeures can only be used with getNext()");
      
      if ( ++m_nGetCount > 1 )
      {
        if ( m_fCloseOnExecute )
        {
          m_cs.close();
          m_cs = null;
        }

        return null;
      }

    } // end if ( m_cs != null )
    else
    if ( m_rs != null )
    {
      
      synchronized( m_sqlData )
      {
         if ( m_sqlData.m_listResults == null )
         {
           ResultSetMetaData rsm = m_rs.getMetaData();
           int nColCount = rsm.getColumnCount();
           m_sqlData.m_listResults = new ArrayList();


           for ( int x = 0; x < nColCount; x++ )
           {
             m_sqlData.m_listResults.add( rsm.getColumnLabel( x + 1 ) );
           }
         }

      }
      
      try
      {
        boolean fGotRow = false;
        
        switch( nDirectionInd )
        {
          case NEXT:
               
            	 fGotRow = m_rs.next();
               break;
               
          case PREV:
            
            	 fGotRow = m_rs.previous();
            	 break;
            
          case ABS:
            
            	 fGotRow = m_rs.absolute( nRowNbr );
         	     break;
         
            
        } // end switch()
        
        if ( !fGotRow )
        {
          if ( m_nResultSetType == ResultSet.TYPE_FORWARD_ONLY)
          {
            m_rs.close();
            m_rs = null;
            m_rsMeta = null;
          }
          
          return null;
        }

      }
      catch( SQLException sqle )
      {
        m_db.getDbMgr().handleException( sqle );
        return null;
      }

      int nColCount = m_rsMeta.getColumnCount();

      if ( m_sqlData.m_asResultTypes == null )
      {
        m_sqlData.m_asResultTypes = new short[ nColCount ];
        for ( int x = 0; x < nColCount; x ++ )
          m_sqlData.m_asResultTypes[ x ] = (short)m_rsMeta.getColumnType( x + 1 );
      }
    } // end else

    // Get result col data from fetch and put it in the dataobject
    Object resultObj = null;


    for ( int x = 0; x < m_sqlData.m_listResults.size(); x++ )
    {
      int nParamNbr = x + 1;

      if ( m_cs != null )
        nParamNbr = m_sqlData.m_asResultParamNbr[ x ];

      String strResultName = (String)m_sqlData.m_listResults.get( x );
      

      if ( m_cs != null )
        resultObj = m_cs.getObject( nParamNbr );
      else
        resultObj = m_rs.getObject( nParamNbr );
      
 
      if ( resultObj instanceof Blob )
      {
        Blob blob = (Blob)resultObj;
        InputStream ins = blob.getBinaryStream();
        int nAvail = ins.available();
        byte[] ab = new byte[nAvail];
        ins.read( ab );
        resultObj = ab;        
         
      }
      VwBeanUtils.setValue( objResultBean, strResultName, resultObj );

      } // end for()

    // Turn off dirty flag (turned on when setXXX methods are called) for bean just loaded if instanceof VwDVOBase
    if ( objResultBean instanceof VwDVOBase )
      ((VwDVOBase)objResultBean).setDirty( false );


    return objResultBean;


  } // end get()

  private byte[] getInputStream( InputStream inps ) throws Exception
  {
    int nMax = 65535;
    int nGot = 0;

    byte[] aBytes = new byte[65535];
    nGot = inps.read( aBytes );

    while( nGot >= nMax )
    {
      nGot =  inps.read( aBytes );
    }

    return aBytes;

  } // End of getInputStream()


  private String getVarcharInputStream( InputStream inps ) throws Exception
  {
    int nMax = 32767;
    int nGot = 0;

    String strResult;
    byte[] aBytes = new byte[nMax];
    nGot = inps.read( aBytes );

    strResult = new String( aBytes, 0, nGot );

    while( nGot >= nMax )
    {
      nGot =  inps.read( aBytes );
      if ( nGot <= 0 )
        break;

      strResult += new  String( aBytes, 0, nGot );
    }

    return strResult;

  } // End of getVarcharInputStream()


  /**
   * Executes a stored procedure
   *
   * @param objData The data used to bing the parameters
   * @exception VwDataSourceException see method exec( VwSqlData, VwDataObject ) for details
   * SQLException for database errors not translated by a VwDriverTranslationMsgs object
   * Exception for all other errors
   */
  private final void doStoredProc( Object objData )
    throws Exception,
           SQLException,
           VwDataSourceException
  {
    try
    {
      // *** Prepare the call statement

      m_cs = (CallableStatement)m_sqlData.getStatement( m_db );
      
      
      Connection con = m_db.getConnection();

      if ( m_cs == null )
      {
        m_cs = con.prepareCall( m_sqlData.m_strSQL, m_nResultSetType, m_nResultSetConcur );
        
        if ( m_fStatementCaching )
        {
          m_sqlData.cacheStatement( m_db, m_cs );
        }
      }
      
      // *** If flag is false just execute stored procedure without rebinding input params

      if ( !m_fParamRebind )
      {
        m_cs.execute();
        return;
      }

      // *** If we have a parameter list then set the parameter data before executing the query

      if ( m_sqlData.m_listParams != null )
      {

        // *** Set the param data

        for ( int x = 0; x < m_sqlData.m_listParams.size(); x++ )
        {
          int nParamNbr = m_sqlData.m_asParamNbr[ x ];

          Object objValue = null;
          String strParamName = (String)m_sqlData.m_listParams.get( x );
          
          if ( objData instanceof VwDataObject )
          {
            objValue = ((VwDataObject)objData).get( strParamName );
          }
          else
          if ( objData instanceof Map )
          {
            objValue = ((Map)objData).get( strParamName );
          }
          else
          if ( m_sqlData.m_listParams.size() == 1 && VwBeanUtils.isSimpleType( objData.getClass()) )
          {
            objValue = objData;
          }
          else
          {
            objValue = VwBeanUtils.getValue( m_sqlData.m_objData, strParamName );
          }
          
          switch( m_sqlData.m_asParamTypes[ x ] )
          {
            case Types.TIMESTAMP:
            case Types.DATE:

                 VwDate dt = null;

                 if ( objValue != null )
                 {
                   if ( objValue instanceof String )
                     dt = new VwDate( (String)objValue, m_strSourceDateFormat );
                   else
                   if ( objValue instanceof VwDate )
                     dt = (VwDate)objValue;

                   if ( dt != null )
                    objValue = dt.toTimestamp();

                 } // end if


            default:

                 if ( objValue == null )
                   m_cs.setNull( nParamNbr, m_sqlData.m_asParamTypes[ x ] );
                 else
                   m_cs.setObject( nParamNbr, objValue );

                 break;

          } // end switch()

        } // end for()

      } // end if m_sqlData.m_astrParamList != null )


    if ( m_sqlData.m_listResults != null )
    {

      for ( int x = 0; x < m_sqlData.m_listResults.size(); x++ )
      {
        int nParamNbr = m_sqlData.m_asResultParamNbr[ x ];

        m_cs.registerOutParameter( nParamNbr, m_sqlData.m_asResultTypes[ x ] );


      } // end for()

    } // end if

    
    // *** Execute the stored procedure

    m_cs.execute();
    m_nRowCount = m_cs.getUpdateCount();

    } // end try
    catch( SQLException sqle )
    {

      m_db.getDbMgr().handleException( sqle );

    } // end catch

  } // end doStoreProc()


  /**
   * Setup a date storage string based on the JdbcDriver used
   */
  private String getFormatBasedOnDriver()
  {
    String strDriver = m_db.getDbMgr().getDriverClassName();

    if ( strDriver.startsWith( "oracle" ) )
      return VwDate.ORADATE;

    return VwDate.USADATE_TIME;         // The default

  } // end getFormatBasedOnDriver()

} // end class VwSqlMgr


//*** End of VwSqlMgr.java ***

