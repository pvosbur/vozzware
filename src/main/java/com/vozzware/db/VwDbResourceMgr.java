package com.vozzware.db;

import com.vozzware.util.VwLogger;
import com.vozzware.xml.VwDataObject;

import java.net.URL;
import java.util.List;

public interface VwDbResourceMgr
{
  VwDbMgr getDbMgr();

  VwDatabase getDatabase() throws Exception; // end getDatabase()

  VwDatabase getDatabase( String strUserId, String strPassword ) throws Exception;

  VwSqlMgr getSqlMgr() throws Exception; // end getSqlMgr()

  VwSqlMgr getSqlMgr( String strUserId, String strPassword ) throws Exception; // end getSqlMgr()

  VwSqlMgr getSqlMgr( VwDatabase db ) throws Exception; // end getSqlMgr()

  VwSqlMgr getSqlMgr( VwDatabase db, URL urlSqlMappingDoc ) throws Exception; // end getSqlMgr()

  void beginTransaction() throws Exception;

  void commit() throws Exception;

  void rollback() throws Exception;

  void cancelQuery() throws Exception;

  VwLogger getLogger();


  /**
    * Close VwDatabase (connection) and VwSqlMgr resources
    * @param db The VwDatabase instance to clise or put back in the pool (may be null)
    *
    * @param sqlMgr an array of VwSqlMgrs (statement) to close
    * @throws Exception
    */
   void closeResources( VwDatabase db, VwSqlMgr sqlMgr ) throws Exception;

   /**
    * Close VwDatabase (connection) and VwSqlMgr resources
    * @param db The VwDatabase instance to clise or put back in the pool (may be null)
    * @param sqlMgr The VwSqlMgr (statement) to close
    * @param exPrior The original exception thrown from the previous sql operation.

    * @throws Exception
    */
   void closeResources( VwDatabase db, VwSqlMgr sqlMgr, Exception exPrior ) throws Exception;


   /**
    * Close VwDatabase (connection) and VwSqlMgr resources
    * @param db The VwDatabase instance to clise or put back in the pool (may be null)
    *
    * @param asqlMgr an array of VwSqlMgrs (statement) to close
    * @throws Exception
    */
   void closeResources( VwDatabase db, VwSqlMgr[] asqlMgr ) throws Exception;

   /**
    * Close VwDatabase (connection) and VwSqlMgr resources
    * @param db The VwDatabase instance to clise or put back in the pool (may be null)
    *
    * @param asqlMgr an array of VwSqlMgrs (statement) to close
    * @throws Exception
    */
   void closeResources( VwDatabase db, VwSqlMgr[] asqlMgr, Exception exPrior ) throws Exception;

   /**
    * Close VwDatabase (connection) and VwSqlMgr resources
    * @param sqlMgr The VwSqlMgr to close. It's associated VwDatabase connection is also
    * <br>closed or put back in the connection pool.
    * @param exPrior The original exception thrown from the previous sql operation.
    *
    * @throws Exception
    */
   public void closeResources( VwSqlMgr sqlMgr, Exception exPrior ) throws Exception;

   /**
    * Close VwDatabase (connection) and VwSqlMgr resources
    * @param sqlMgr The VwSqlMgr to close. It's associated VwDatabase connection is also
    * <br>closed or put back in the connection pool.
    *
    * @throws Exception
    */
   void closeResources( VwSqlMgr sqlMgr ) throws Exception;

  /**
   * Executes a queru that is assumed to return one row
   *
   * @param strSql  The sql statement to execute
   * @param objParams paramater object to sql
   * @return
   * @throws Exception
   */
   VwDataObject execWithResult( String strSql, Object objParams ) throws Exception;

  /**
   * Executes a sql statement query that contains one column and returns a scaler object ie.e String,Long,Float ...
   * @param strSql The sql statement to execute
   * @param objParams any params to satisfy a where clause
   * @return
   * @throws Exception
   */
   Object execWithScalerResult( String strSql, Object objParams ) throws Exception;


  /**
   * Executes a sql statement query that contains one column and returns a list of objects ie.e String,Long,Float ...
   * @param strSql The sql statement to execute
   * @param objParams any params to satisfy a where clause
   * @return
   * @throws Exception
   */
   List<?> execWithScalerListResult( String strSql, Object objParams ) throws Exception;

  /**
   * Executes query and returns a List of VwDataObjects (one for each row and contains the column data as specified in the query)
   *
   * @param strSql The sql statement to execute
   * @param objParams any params to satisfy a where clause
    * @return
   * @throws Exception
   */
   List<VwDataObject> execWithListResult( String strSql, Object objParams ) throws Exception;
  /**
   * Gets the child object of its superclass. This assumes the super class portion that has the primary
   * key value has already be retrieved
   *
   * @param objBean The Child object (sub class) with its parent alreay initialized
   * @throws Exception
   */
  public void getChildObject( Object objBean ) throws Exception;

}
