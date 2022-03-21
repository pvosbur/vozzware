package com.vozzware.db;

/**
 * This exception will be thrown by the VwSqlMgr when a synchSave or syncDelete method is invoked and the 
 * timestamp column changes in between the original retrieve operation and the save or delete operation.
 * 
 * @author petervosburghjr
 *
 */
public class VwTimestampOutOfSyncException extends Exception
{
  public VwTimestampOutOfSyncException( String strMessaage )
  { super( strMessaage ); }
}
