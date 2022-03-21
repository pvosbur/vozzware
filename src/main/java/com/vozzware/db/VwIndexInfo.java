/*
 ============================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

  THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
  PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
  CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

  Source Name:   VwIndexInfo.java


 ============================================================================
*/


package com.vozzware.db;                         // Our package


/**
 * This class contains the description of a foreign key
 */
public class VwIndexInfo
{

  String    m_strSchema;           // Table schema for the tbale this is a primary key in
  String    m_strCatalog;          // Table catalog of the table with the primary key (may be null)
  String    m_strTableName;        // Name of the Table this foreign is a primary key of
  String    m_strColName;          // The index column name
  String    m_strIndexName;        // The name of the index
  int       m_nColPos;             // Ordinal position of the column
  boolean   m_fUnique;             // If true, index is unique
  boolean   m_fAscending;          // If true, index is ascending else its descending
 

  /**
   * Gets the name of the Catalog the column is in
   *
   * @return The catalog name for the column or null if not supported by the datasource
   */
  public final String getCatalog()
  { return m_strCatalog; }


  /**
   * Sets the name of the Catalog the column is in
   *
   * @param strCatalog The catalog name for the column or null if not supported by the datasource
   */
  public final void setCatalog( String strCatalog )
  { m_strCatalog = strCatalog; }


  /**
   * Gets the name of the Schema the column belongs to
   *
   * @return The schema name for the column or null if not supported by the data source
   */
  public final String getSchemaName()
  { return m_strSchema; }


  /**
   * Sets the name of the Schema the column belongs to
   *
   * @param strSchema The schema name for the column or null if not supported by the data source
   */
  public final void setSchemaName( String strSchema )
  { m_strSchema = strSchema; }


  /**
   * Gets the name of the Table the column is in
   *
   * @return The table name for the column
   */
  public final String getTableName()
  { return m_strTableName; }


  /**
   * Sets the name of the Table the column is in
   *
   * @param strTableName The table name for the column
   */
  public final void setTableName( String strTableName )
  { m_strTableName = strTableName; }


  /**
   * Gets the column name
   *
   * @return The column name
   */
  public final String getColumnName()
  { return m_strColName; }


  /**
   * Sets the column name
   *
   * @param strColName The column name
   */
  public final void  setColumnName( String strColName )
  { m_strColName = strColName; }

 
  public String getIndexName()
  {
    return m_strIndexName;
  }
  public void setIndexName( String strIndexName )
  {
    m_strIndexName = strIndexName;
  }
} // end class VwIndexInfo{}


// *** End of VwIndexInfo.java ***

