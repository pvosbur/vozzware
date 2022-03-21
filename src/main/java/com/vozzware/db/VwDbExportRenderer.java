/*
 ============================================================================================
 
 Copyright(c) 2000 - 2008 by

 i  T e c h n o l o g i e s   C o r p. (Vw)

 All Rights Reserved

 THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
 PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
 CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

 Source Name: VwDbExportRenderer.java

 Create Date: Nov 9, 2007
 ============================================================================================
 */
package com.vozzware.db;

import java.util.List;
import java.util.Map;

/**
 * This class provides the export rendering contract for exporting database objects to some external form.
 * 
 * @author petervosburghjr
 *
 */
public interface VwDbExportRenderer
{
  
  /**
   * This is the first method invoked to signify the start of an export session
   * 
   * @param db The database object with all the meta data about the specific database 
   * 
   * @throws Exception 
   */
  public void beginExport( VwDatabase db ) throws Exception;
  
  
  /**
   * This is the last method called to signify the end of the export session
   * @param db
   * @throws Exception
   */
  public void endExport( VwDatabase db ) throws Exception;
  /**
   * This method is invoked for each table that will be exported. It is called once at the start of each table export.
   * 
   * @param strCatalogName The catalog the table resides in or null if N/A
   * @param strSchemaName The schema the table resides in
   * @param strTableName The table name to be exported
   * @param listPrimaryKeys A list of VwColInfo objects for the primary key specification or null if no primary keys defined
   * @param listForeignKeys A list of VwForeignKeyInfo objects for any foreign keys defined or null if N/A
   * 
   * @throws Exception If any persistence errors occur
   */
  public void beginTable( String strCatalogName, String strSchemaName, String strTableName,
                          List<VwColInfo> listPrimaryKeys, List<VwForeignKeyInfo> listForeignKeys ) throws Exception;
  
  /**
   * This method is invoked for each row in the table to be exported
   * 
   * @param listColInfo The List of database column specifications
   * @param mapData The data map where the column name is the key to the column data. Columns that
   * <br>are null return null from the map get operation.
   * 
   * @throws Exception If any persistence errors occur
   */
  public void tableRow( List<VwColInfo> listColInfo, Map<String,?>mapData ) throws Exception;
  
  /**
   * This method is invoked after the last tableRow to indicate the end of table data
   * 
   * @param strCatalogName The catalog the table resides in or null if N/A
   * @param strSchemaName The schema the table resides in
   * @param strTableName The table name to be exported
   * 
   * @throws Exception If any persistence errors occur
   */
  public void endTable( String strCatalogName, String strSchemaName, String strTableName ) throws Exception;
  
} // end interface VwDbExportRenderer{}

// *** End of VwDbExportRenderer.java ***

