/*
===========================================================================================

 
                               Copyright(c) 2000 - 2006 by

                      V o z z W a r e   L L C (Vw)

                                 All Rights Reserved

Source Name: VwDbResourceMgr.java

Create Date: Sep 19, 2006

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.


============================================================================================
*/
package com.vozzware.db;

import com.vozzware.util.VwLogger;
import com.vozzware.util.VwStack;
import com.vozzware.util.VwStackTraceWriter;
import com.vozzware.xml.VwDataObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class can be used as a helper super class for DAO implementations.
 * It manages the VwDbMgr,VwDatabase connections and cleans up resources.
 * Optionally, it can use an VwLogger for complete tracing of resources.
 * 
 * @author P. VosBurgh
 *
 */
public class VwDbResourceMgrImpl implements VwDbResourceMgr
{
  private   VwDbMgr   m_dbMgr;
  protected VwLogger m_loggerSql;
  protected VwLogger  m_loggerDbMgr;
  private   URL       m_urlSqlMappingDoc;

  private static Object s_tranSemi = new Object();

  protected Map<Long, VwStack<VwSqlMgr>> m_mapTransactionsByThread = Collections.synchronizedMap( new HashMap<Long, VwStack<VwSqlMgr>>( ) );

  public void setLogger( VwLogger logger )
  {
    m_loggerSql = logger;
  }

  /**
   * Constructor
   * 
   * @param strDriverId The name of the driver id as defined in the DatasourceDrivers.xml document
   * @param strDriverURLId The connection url to use as defined in the DatasourceDrivers.xml document
   * @param urlSqlMappingDoc The url to the Sql Mapping doc (.xsm )
   * 
   * @throws Exception if any Database errors occur
   */
  public VwDbResourceMgrImpl( String strDriverId, String strDriverURLId, URL urlSqlMappingDoc ) throws Exception
  { this( strDriverId, strDriverURLId, urlSqlMappingDoc, null, null, null ); }

  
  /**
   * Constructor
   * 
   * @param strDriverId The name of the driver id as defined in the DatasourceDrivers.xml document
   * @param strDriverURLId The connection url to use as defined in the DatasourceDrivers.xml document
   * @param urlSqlMappingDoc The url to the Sql Mapping doc (.xsm )
   * @param strPassword A password override from that defined in a <connectPool> xml entry
   * 
   * @throws Exception if any Database errors occur
   */
  public VwDbResourceMgrImpl( String strDriverId, String strDriverURLId, URL urlSqlMappingDoc, String strPassword ) throws Exception
  { this( strDriverId, strDriverURLId, urlSqlMappingDoc, null, null, strPassword ); }


  /**
   * Constructor
   * 
   * @param strDriverId The name of the driver id as defined in the DatasourceDrivers.xml document
   * @param strDriverURLId The connection url to use as defined in the DatasourceDrivers.xml document
   * @param urlSqlMappingDoc The url to the Sql Mapping doc (.xsm )
   * @param loggerSql The VwLogger instance if logging is desired
   * @param loggerDbMgr The VwLogger instance fir VwDbMgr
   *
   * @throws Exception if any Database errors occur
   */
  public VwDbResourceMgrImpl( String strDriverId, String strDriverURLId, URL urlSqlMappingDoc, VwLogger loggerSql, VwLogger loggerDbMgr  ) throws Exception
  { this( strDriverId, strDriverURLId, urlSqlMappingDoc, loggerSql, loggerDbMgr, null ); }

  /**
   * Constructor
   * 
   * @param strDriverId The name of the driver id as defined in the DatasourceDrivers.xml document
   * @param strDriverURLId The connection url to use as defined in the DatasourceDrivers.xml document
   * @param urlSqlMappingDoc The url to the Sql Mapping doc (.xsm )
   * @param loggerSql The VwLogger instance if logging is desired
   * @param strPassword A password override from that defined in a <connectPool> xml entry
   * 
   * @throws Exception if any Database errors occur
   */
  public VwDbResourceMgrImpl( String strDriverId, String strDriverURLId, URL urlSqlMappingDoc, VwLogger loggerSql, VwLogger loggerDbMgr, String strPassword ) throws Exception
  {
    m_loggerSql = loggerSql;
    m_loggerDbMgr = loggerDbMgr;

    m_urlSqlMappingDoc = urlSqlMappingDoc;

    if ( m_loggerSql != null && m_loggerSql.isDebugEnabled() )
    {
      m_loggerSql.debug( VwDbResourceMgrImpl.class, "Creating VwDbMgr instance using the driver id '" + strDriverId + "' and url '" + strDriverURLId );
    }

    try
    {
      m_dbMgr = new VwDbMgr( strDriverId, strDriverURLId, strPassword, m_loggerDbMgr );

      if ( m_loggerSql != null && m_loggerSql.isDebugEnabled() )
      {
        m_loggerSql.debug( VwDbResourceMgrImpl.class, "VwDbMgr created" );
      }

      if ( m_urlSqlMappingDoc != null )
      {
        VwSqlMgr.loadSqlMappings( m_urlSqlMappingDoc );
      }
    }
    catch( Exception ex  )
    {
      m_loggerSql.error( this.getClass(), ex.toString(), ex );
      throw ex;

    }
  } // end VwDbResourceMgrImpl()
  
  
  /**
   * Returns The VwDbMgr instance
   * @return
   */
  public VwDbMgr getDbMgr()
  { return m_dbMgr; }
  
  /**
   * Returns an VwDatabase instance using a pooled connection
   * @return an VwDatabase instance using a pooled connection
   * 
   * @throws Exception if any database errors occur
   */
  public VwDatabase getDatabase() throws Exception
  { 
    VwDatabase db = null;


    try
    {
      db =  m_dbMgr.login();

    }
    catch( Exception ex )
    {
      m_loggerSql.fatal( VwDbResourceMgrImpl.class, "Failed getting VwDatabase instance from connection pool", ex  );
      throw ex;
    }
    
    return db;
    
    
  } // end getDatabase()
  
  
  /**
   * Returns an VwDatabase instance by logging into the database with the user id and password specified
   * 
   * @param strUserId The database user id for login
   * @param strPassword The database password for login
   * 
   * @return an VwDatabase instance
   * 
   * @throws Exception if any database errors occur
   */
  public VwDatabase getDatabase( String strUserId, String strPassword ) throws Exception
  {

    VwDatabase db = null;

    try
    {
      db =  m_dbMgr.login( strUserId, strPassword );

    }
    catch( Exception ex )
    {
      m_loggerSql.fatal( VwDbResourceMgrImpl.class, "Failed getting VwDatabase instance login attempt", ex  );
    }
    
    return db;
    
  }

  /**
   * Retrieves an VwSqlMgr. This gets the associated VwDatabase instance from a connection pool 
   * 
   * @return An VwSqlMgr
   * @throws Exception if a Database connection cannot be retrieved
   */
  public VwSqlMgr getSqlMgr() throws Exception
  {

    VwDatabase db = getDatabase();
    VwSqlMgr sqlMgr = new VwSqlMgr( db, m_urlSqlMappingDoc );
    sqlMgr.setLogger( m_loggerSql );
    
    return sqlMgr;
    
  } // end getSqlMgr()

  
  /**
   * Retrieves an VwSqlMgr. This gets the associated VwDatabase instance (single connection) by
   * logging in with the specified user id and password
   * 
   * @return An VwSqlMgr
   * @throws Exception if a Database connection cannot be retrieved
   */
  public VwSqlMgr getSqlMgr( String strUserId, String strPassword ) throws Exception
  {
    VwDatabase db = getDatabase( strUserId, strPassword );
    return new VwSqlMgr( db );
    
  } // end getSqlMgr()
  
  /**
   * Gets an VwSqlMgr instance for the VwDatbase object passed
   * The VwSqlMgr will be initialized with the DAO document URL passed in the constructor as
   * well as the VwLogger (if specified)
   * 
   * @param db The VwDatabase object to be used to create the VwSqlMgr instance
   * @return
   * @throws Exception
   */
  public VwSqlMgr getSqlMgr( VwDatabase db ) throws Exception
  {

    VwSqlMgr sqlm = new VwSqlMgr( db, m_urlSqlMappingDoc );
    sqlm.setLogger( m_loggerSql );
    
    return sqlm;
    
  } // end getSqlMgr()

  
  /**
   * Gets an VwSqlMgr instance for the VwDatbase object passed and url to a different Sql Mapping document that
   * is not the default (as specified in the constructor
   * 
   * The VwSqlMgr will be initialized with the DAO document URL passed in the constructor as
   * well as the VwLogger (if specified)
   * 
   * @param db The VwDatabase object to be used to create the VwSqlMgr instance
   * @param urlSqlMappingDoc The url to the Sql Mapping doc (.xsm )
   * 
   * @return
   * @throws Exception
   */
  public VwSqlMgr getSqlMgr( VwDatabase db, URL urlSqlMappingDoc ) throws Exception
  {

    VwSqlMgr sqlm = new VwSqlMgr( db, urlSqlMappingDoc  );
    sqlm.setLogger( m_loggerSql );
    
    return sqlm;
    
  } // end getSqlMgr()


  /**
   * Begin a transaction
   * @throws Exception
   */
   public void beginTransaction() throws Exception
   {
     synchronized ( s_tranSemi )
     {
       long lThreadId = Thread.currentThread().getId();

       VwStack<VwSqlMgr> transActionStack = m_mapTransactionsByThread.get( lThreadId );

       if ( m_loggerSql != null && m_loggerSql.isDebugEnabled() )
       {
         m_loggerSql.debug( this.getClass(), "beginTransaction TRAN ID: " + lThreadId );
       }

       // if this is the first for this thread then create the transaction stack
       if ( transActionStack == null )
       {
         transActionStack = new VwStack<VwSqlMgr>();
         m_mapTransactionsByThread.put( Thread.currentThread().getId(), transActionStack );

       }
       else
       {
         m_loggerSql.debug( this.getClass(), "Beginning nested transaction for TRAN ID: " + lThreadId );

       }

       VwSqlMgr sqlMgrTrans = transActionStack.peek();

       if ( sqlMgrTrans == null )  // This is the parent of the transaction so we need to create a new sql mgr
       {

         m_loggerSql.debug( this.getClass(), "SETTING AUTO COMMIT TO FALSE FOR  TRAN ID: " + lThreadId );
         sqlMgrTrans = getSqlMgr();   // This sql mgr will be used for all calls until commit
         sqlMgrTrans.setAutoCommit( false ); // make sure we're in manual mode

       }

       transActionStack.push( sqlMgrTrans );
       ;

     }
   }


  /**
   * Commits the transaction
   * @throws Exception
   */
  public void  commit() throws Exception
  {

    synchronized ( s_tranSemi )
    {

      long lThreadId = Thread.currentThread().getId();

      if ( m_loggerSql != null && m_loggerSql.isDebugEnabled() )
      {
        m_loggerSql.debug( this.getClass(), "commit  TRAN ID: " + lThreadId );
      }

      VwStack<VwSqlMgr> transActionStack = m_mapTransactionsByThread.get( lThreadId );

      if ( transActionStack == null )
      {
        throw new Exception( "Invalid transaction state, call beginTransaction first" );
      }

      VwSqlMgr sqlMgrTrans = transActionStack.pop();

      if ( transActionStack.size() > 0 )
      {
        m_loggerSql.debug( this.getClass(), "Commit of nested transaction, Skipping actual commit: " + lThreadId );
        return; // We are not the parent transaction so don't commit here
      }

      try
      {
        m_loggerSql.debug( this.getClass(), "COMMITING TRANSACTION FOR  TRAN ID: " + lThreadId );
        sqlMgrTrans.commit();   // Commit transaction
        sqlMgrTrans.setAutoCommit( true );

      }
      catch ( Exception ex )
      {
        m_loggerSql.error( "Commit transaction failed for tran ID: " + lThreadId, ex );

      }
      finally
      {
        try
        {
          if ( transActionStack.size() > 0 )
          {
            return; // We are not the parent transaction so don't commit here
          }

          m_mapTransactionsByThread.remove( lThreadId );
          m_loggerSql.debug( this.getClass(), "Closing resources for committed transaction: " + lThreadId );
          closeResources( sqlMgrTrans );
        }
        catch ( Exception ex1 )
        {
          m_loggerSql.error( "close resources failed for transaction", ex1 );

        }

        sqlMgrTrans = null;
      }

    }
  }

  /**
   * Rolls back the  transaction
   * @throws Exception
   */
  public void rollback() throws Exception
  {

    long lThreadId = Thread.currentThread().getId();

    if ( m_loggerSql != null && m_loggerSql.isDebugEnabled() )
    {
      m_loggerSql.debug( this.getClass(), "rollback  TRAN ID: " + lThreadId );
    }

    VwStack<VwSqlMgr> transActionStack = m_mapTransactionsByThread.get( lThreadId );

    if ( transActionStack == null )
    {
      throw new Exception( "Invalid transaction state, call beginTransaction first" );
    }

    VwSqlMgr sqlMgrTrans = transActionStack.pop();

    if ( transActionStack.size() > 0 )
    {
      m_loggerSql.debug( this.getClass(), "Rollback of nested transaction, skipping actual rollback: " + lThreadId );
      return; // We are not the parent transaction so don't commit here
    }

    try
    {
      sqlMgrTrans.rollback();   // rollback  transaction

    }
    catch( Exception ex )
    {

      m_loggerSql.error( "Rollback transaction failed for TRAN ID: " + lThreadId, ex  );

    }
    finally
    {
      try
      {
        if ( transActionStack.size() > 0 )
        {
          return; // We are not the parent transaction so don't close resources here
        }

        m_mapTransactionsByThread.remove( Thread.currentThread().getId() );

        m_loggerSql.debug( this.getClass(), "Closing Resources Rollbacj transaction: " + lThreadId );
        closeResources( sqlMgrTrans );
      }
      catch( Exception ex1 )
      {
        m_loggerSql.error( "close resources failed for transaction", ex1  );

      }

      sqlMgrTrans = null;
    }


  }

  /**
   * Cancels the current running query. This requires that the query being cancelled s inside a transation block
   */
  public void cancelQuery() throws Exception
  {

    long lThreadId = Thread.currentThread().getId();

    if ( m_loggerSql != null && m_loggerSql.isDebugEnabled() )
    {
      m_loggerSql.debug( this.getClass(), "Canceling Query for  TRAN ID: " + lThreadId );
    }

    VwStack<VwSqlMgr> transActionStack = m_mapTransactionsByThread.get( lThreadId );

    if ( transActionStack == null )
    {
      throw new Exception( "Invalid transaction state, Must be in a transaction to cancel this query" );
    }

    VwSqlMgr sqlMgrTrans = transActionStack.pop();
    try
    {
      sqlMgrTrans.cancel();   // rollback  transaction

    }
    catch( Exception ex )
    {

      m_loggerSql.error( "Cancel Query failed for TRAN ID: " + lThreadId, ex  );

    }
    finally
    {
      try
      {

        m_mapTransactionsByThread.remove( Thread.currentThread().getId() );

        closeResources( sqlMgrTrans );
      }
      catch( Exception ex1 )
      {
        m_loggerSql.error( "close resources failed for canceling transaction", ex1  );

      }

      sqlMgrTrans = null;
    }


  }

  /**
   * Returns either the VwSqlMgr associated with a transaction if a transaction block exists or returns
   * a new VwSqlMgr instance
   * @return
   * @throws Exception
   */
  public VwSqlMgr getTransactionSqlMgr() throws Exception
  {
    VwStack<VwSqlMgr> transActionStack = m_mapTransactionsByThread.get( Thread.currentThread().getId() );
    
    // If no transaction exists then return a new standalone VwSqlMgr
    if ( transActionStack == null )
    {
      return null;
    }
    
    // return the sqlMgr at top of stack
    return transActionStack.peek();
    
    
  }
  
  
  /**
   * Returns the VwLogger(if specified) for this DAO
   * @return
   */
  public VwLogger getLogger()
  { return m_loggerSql; }
  
  
  /**
   * Rollback the transaction
   * @param sqlMgr The VwSqlMgr used in the transaction
   * @throws Exception
   */
  public void rollback( VwSqlMgr sqlMgr ) throws Exception
  { rollback( sqlMgr, null ); }
  

  /**
   * 
   * @param sqlMgr
   * @param exPrior
   * @throws Exception
   */
  public void rollback( VwSqlMgr sqlMgr, Exception exPrior ) throws Exception
  {

    if ( m_loggerSql != null && m_loggerSql.isDebugEnabled() )
    {
      m_loggerSql.info( this.getClass(), "Attempting to rollback transaction" );
    }
    
    StringBuffer sbStackTrace = new StringBuffer();
    
    try
    {
      sqlMgr.rollback();
    }
    catch( Exception ex )
    {
      if ( exPrior != null )
      {
        sbStackTrace.append( getStackTrace( exPrior ) );
      }

      sbStackTrace.append( getStackTrace( ex ) );
    }

    if ( m_loggerSql != null && sbStackTrace.length() > 0 )
    {
      m_loggerSql.error( VwDbResourceMgrImpl.class, sbStackTrace.toString() );
    }
    else
    {
      if ( m_loggerSql != null && m_loggerSql.isDebugEnabled() )
      {
        m_loggerSql.debug( VwDbResourceMgrImpl.class, "Rollback successful" );
      }
    }

    if ( sbStackTrace.length() > 0 )
    {
      throw new Exception( sbStackTrace.toString() );
    }
    
  }
  
  /**
   * Close VwDatabase (connection) and VwSqlMgr resources
   * @param db The VwDatabase instance to clise or put back in the pool (may be null)
   * 
   * @param sqlMgr an array of VwSqlMgrs (statement) to close
   * @throws Exception
   */
  public void closeResources( VwDatabase db, VwSqlMgr sqlMgr ) throws Exception
  { closeResources( db, sqlMgr, null ); }
  
  /**
   * Close VwDatabase (connection) and VwSqlMgr resources
   * @param db The VwDatabase instance to clise or put back in the pool (may be null)
   * @param sqlMgr The VwSqlMgr (statement) to close
   * @param exPrior The original exception thrown from the previous sql operation. 

   * @throws Exception
   */
  public void closeResources( VwDatabase db, VwSqlMgr sqlMgr, Exception exPrior ) throws Exception
  {
    if ( m_loggerSql != null && m_loggerSql.isDebugEnabled() && m_loggerSql.getDebugVerboseLevel() >= 2 )
    {
      StringBuffer sbMsg = new StringBuffer("Attempting to close ");
      if ( db != null )
      {
        sbMsg.append( " VwDatabase resource" );
      }
      
      if ( sqlMgr != null )
      {
        if ( db != null )
        {
          sbMsg.append( " and " );
        }
        
        sbMsg.append( "VwSqlMgr resource" );
      }
      
      m_loggerSql.debug( VwDbResourceMgrImpl.class, sbMsg.toString() );
    }
    
    StringBuffer sbStackTrace = new StringBuffer();
    
    
    try
    {
      if ( sqlMgr != null )
      {
        sqlMgr.close();
      }
    }
    catch( Exception ex )
    {
      if ( exPrior != null )
      {
        sbStackTrace.append( getStackTrace( exPrior ) );
      }
      
      sbStackTrace.append( getStackTrace( ex ) );
      
    }
    
    
    try
    {
      if ( db != null )
      {
        m_dbMgr.close( db );
      }
    }
    catch( Exception ex )
    {
      if ( exPrior != null )
      {
        sbStackTrace.append( getStackTrace( exPrior ) );
      }

      sbStackTrace.append( getStackTrace( ex ) );
      
    }

    if ( m_loggerSql != null && sbStackTrace.length() > 0 )
    {
      m_loggerSql.error( VwDbResourceMgrImpl.class, sbStackTrace.toString() );
    }
    else
    if ( m_loggerSql != null && m_loggerSql.isDebugEnabled() && m_loggerSql.getDebugVerboseLevel() >= 2 )
    {
        m_loggerSql.debug( VwDbResourceMgrImpl.class, "Resources successfully closed" );
    }

    if ( sbStackTrace.length() > 0 )
    {
      throw new Exception( sbStackTrace.toString() );
    }
    else
    if ( exPrior != null )
    {
      throw new Exception( getStackTrace( exPrior ) );
    }

  } // end closeResources()
 

  /**
   * Close VwDatabase (connection) and VwSqlMgr resources
   * @param db The VwDatabase instance to clise or put back in the pool (may be null)
   * 
   * @param asqlMgr an array of VwSqlMgrs (statement) to close
   * @throws Exception
   */
  public void closeResources( VwDatabase db, VwSqlMgr[] asqlMgr) throws Exception
  { closeResources( db, asqlMgr, null ); }
  
  /**
   * Close VwDatabase (connection) and VwSqlMgr resources
   * @param db The VwDatabase instance to clise or put back in the pool (may be null)
   * 
   * @param asqlMgr an array of VwSqlMgrs (statement) to close
   * @throws Exception
   */
  public void closeResources( VwDatabase db, VwSqlMgr[] asqlMgr, Exception exPrior ) throws Exception
  {
    if ( m_loggerSql != null && m_loggerSql.isDebugEnabled() && m_loggerSql.getDebugVerboseLevel() >= 2 )
    {
      StringBuffer sbMsg = new StringBuffer("Attempting to close ");
      if ( db != null )
      {
        sbMsg.append( " VwDatabase resource" );
      }
      
      if ( asqlMgr != null )
      {
        if ( db != null )
        {
          sbMsg.append( " and " );
        }
        
        sbMsg.append( "VwSqlMgr resources" );
      }
      
      m_loggerSql.debug( VwDbResourceMgrImpl.class, sbMsg.toString() );
      
    } // end if
    
    StringBuffer sbStackTrace = new StringBuffer();

    if ( exPrior != null )
    {
      sbStackTrace.append( getStackTrace( exPrior ) );
    }
    

     
    try
    {
      for ( int x = 0; x < asqlMgr.length; x++ )
      {
        if ( asqlMgr[ x ] != null )
        {
          asqlMgr[ x ].close();
        }
      }
      
    }
    catch( Exception ex )
    {
      sbStackTrace.append( getStackTrace( exPrior ) );
    }
    

    try
    {
      if ( db != null )
      {
        m_dbMgr.close( db );
      }
    }
    catch( Exception ex )
    {
      sbStackTrace.append( getStackTrace( ex ) );
      
    }

    if ( m_loggerSql != null && sbStackTrace.length() > 0 )
    {
      m_loggerSql.error( VwDbResourceMgrImpl.class, sbStackTrace.toString() );
    }
    else
    {
      if ( m_loggerSql != null && m_loggerSql.isDebugEnabled() && m_loggerSql.getDebugVerboseLevel() >= 2 )
      {
        m_loggerSql.debug( VwDbResourceMgrImpl.class, "Resources successfully closed" );
      }
    }

    if ( sbStackTrace.length() > 0 )
    {
      throw new Exception( sbStackTrace.toString() );
    }
   
  } // end closeResources()

  /**
   * Close VwDatabase (connection) and VwSqlMgr resources
   * @param sqlMgr The VwSqlMgr to close. It's associated VwDatabase connection is also
   * <br>closed or put back in the connection pool.
   * @param exPrior The original exception thrown from the previous sql operation. 
   * 
   * @throws Exception
   */
  public void closeResources( VwSqlMgr sqlMgr, Exception exPrior ) throws Exception
  {
    VwDatabase db = sqlMgr.getDatabase();
    closeResources( db, sqlMgr, exPrior );
  }

  /**
   * Close VwDatabase (connection) and VwSqlMgr resources
   * @param sqlMgr The VwSqlMgr to close. It's associated VwDatabase connection is also
   * <br>closed or put back in the connection pool.
   * 
   * @throws Exception
   */
  public void closeResources( VwSqlMgr sqlMgr ) throws Exception
  {
    VwDatabase db = sqlMgr.getDatabase();
    closeResources( db, sqlMgr, null );
    
    
  } // end closeResourcess()
  
  /**
   * Closes the complete Connection / Connection pool releasing all JDBC resources.
   * @throws Exception if the Database is inaccessible. 
   */
  public void close() throws Exception
  {
    if ( m_dbMgr != null )
    {
      m_dbMgr.close();
    }
    
  } // end close()


  /**
   * Executes a query that is assumed to return one row
   *
   * @param strSql  The sql statement to execute
   * @param objParams paramater object to sql
   * @return
   * @throws Exception
   */
  public VwDataObject execWithResult( String strSql, Object objParams ) throws Exception
  {
    VwSqlMgr sqlm = null;
    try
    {
      sqlm = getSqlMgr();
      sqlm.exec( strSql, objParams );

      return sqlm.getNext();

    }
    finally
    {
      closeResources( sqlm );
    }

  }

  /**
   * Executes a sql statement query that contains one column and returns a sclater object ie.e String,Long,Float ...
   * @param strSql The sql statement to execute
   * @param objParams any params to satisfy a where clause
   * @return
   * @throws Exception
   */
  public Object execWithScalerResult( String strSql, Object objParams ) throws Exception
  {
    VwDataObject dobjResult = execWithResult( strSql, objParams );

    if ( dobjResult == null )
    {
      return null;
    }

    return dobjResult.get( 0 );


  }


  /**
   * Executes a sql statement query that contains one column and returns a list of objects ie.e String,Long,Float ...
   * @param strSql The sql statement to execute
   * @param objParams any params to satisfy a where clause
   * @return
   * @throws Exception
   */
  public List<?> execWithScalerListResult( String strSql, Object objParams ) throws Exception
  {
    List<Object>listResult = new ArrayList<>(  );

    VwSqlMgr sqlm = null;
    try
    {
      sqlm = getSqlMgr();
      sqlm.exec( strSql, objParams );

      VwDataObject dobjResult = null;

      while ( (dobjResult =  sqlm.getNext() ) != null )
      {
        listResult.add( dobjResult.get( 0 ) );

      }

    }
    finally
    {
      closeResources( sqlm );
    }

    if ( listResult.size() == 0 )
    {
      listResult = null;

    }

    return listResult;
  }

  public List<VwDataObject> execWithListResult( String strSql, Object objParams ) throws Exception
   {
     List<VwDataObject>listResult = new ArrayList<>(  );

     VwSqlMgr sqlm = null;
     try
     {
       sqlm = getSqlMgr();
       sqlm.exec( strSql, objParams );

       VwDataObject dobjResult = null;

       while ( (dobjResult =  sqlm.getNext() ) != null )
       {
         listResult.add( dobjResult );

       }

     }
     finally
     {
       closeResources( sqlm );
     }

     if ( listResult.size() == 0 )
     {
       listResult = null;

     }

     return listResult;
   }

  /**
   * Gets the child object of its superclass. This assumes the super class portion that has the primary
   * key value has already be retrieved
   *
   * @param objBean The Child object (sub class) with its parent alreay initialized
   * @throws Exception
   */
  public void getChildObject( Object objBean ) throws Exception
  {
    VwSqlMgr sqlm = null;
    try
    {
      sqlm = getSqlMgr();
      sqlm.getChildObject( objBean );

    }
    finally
    {
      closeResources( sqlm );
    }

  }

  /**
   * Get the stack trace for the exception 
   * @param ex The Exception instance to get the stack trace for
   * @return
   */
  public String getStackTrace( Exception ex )
  {
    VwStackTraceWriter stw = new VwStackTraceWriter();
    ex.printStackTrace( stw );
    return stw.toString();
  }


} // end class VwDbResourceMgrImpl{}

// *** End of VwDbResourceMgrImpl.java ***

