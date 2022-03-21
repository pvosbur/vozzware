/*
 ============================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

  THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
  PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
  CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

  Source Name:   VwForeignKeyInfo.java


 ============================================================================
*/


package com.vozzware.db;                         // Our package


/**
 * This class contains the description of a foreign key
 */
public class VwForeignKeyInfo
{


  String m_strDatabase;         // Database the column is in (may be null)
  String m_strPkSchema;         // Table schema for the tbale this is a primary key in
  String m_strPkCatalog;          // Table catalog of the table with the primary key (may be null)
  String m_strPkTableName;      // Name of the Table this foreign is a primary key of
  String m_strPkColName;        // The Primary key name this foreign key name references
  
  String m_strFkCatalog;        // Table catalog of the foreign key
  String m_strFkSchema;         // Table schema for the foreign key 
  String m_strFkTableName;      // The table this foreign key is in
  String m_strFkColName;        // The foreign key column name

  short  m_sUpdateRule;         // Rule on updates
  short  m_sDeleteRule;         // Rule on deletes
  short  m_sDeferRule;          // Rule on commits
  /**
   * Gets the name of the Database the column is in
   *
   * @return The database name for the column or null if not supported by the datasource
   */
  public final String getDatabase()
  { return m_strDatabase; }


  /**
   * Sets the name of the Database the column is in
   *
   * @param strDatabase The database name for the column
   */
  public final void setDatabase( String strDatabase )
  { m_strDatabase = new String( strDatabase ); }

  /**
   * Gets the name of the Catalog the column is in
   *
   * @return The catalog name for the column or null if not supported by the datasource
   */
  public final String getPkCatalog()
  { return m_strPkCatalog; }


  /**
   * Sets the name of the Catalog the column is in
   *
   * @param strCatalog The catalog name for the column or null if not supported by the datasource
   */
  public final void setPkCatalog( String strCatalog )
  { m_strPkCatalog = strCatalog; }


  /**
   * Gets the name of the Schema the column belongs to
   *
   * @return The schema name for the column or null if not supported by the data source
   */
  public final String getPkSchemaName()
  { return m_strPkSchema; }


  /**
   * Sets the name of the Schema the column belongs to
   *
   * @param strSchema The schema name for the column or null if not supported by the data source
   */
  public final void setPkSchemaName( String strSchema )
  { m_strPkSchema = strSchema; }


  /**
   * Gets the name of the Table the column is in
   *
   * @return The table name for the column
   */
  public final String getPkTableName()
  { return m_strPkTableName; }


  /**
   * Sets the name of the Table the column is in
   *
   * @param strTableName The table name for the column
   */
  public final void setPkTableName( String strTableName )
  { m_strPkTableName = strTableName; }


  /**
   * Gets the column name
   *
   * @return The column name
   */
  public final String getPkColName()
  { return m_strPkColName; }


  /**
   * Sets the column name
   *
   * @param strPkColName The primary key column name
   */
  public final void  setPkColName( String strPkColName )
  { m_strPkColName = strPkColName; }


  public short getUpdateRule()
  { return m_sUpdateRule; }

  public void setUpdateRule( short sUpdateRule )
  { m_sUpdateRule = sUpdateRule;  }

  public short getDeleteRule()
  { return m_sDeleteRule; }

  public void setDeleteRule( short sDeleteRule )
  { m_sDeleteRule = sDeleteRule;  }

  public short getDeferRule()
  { return m_sDeferRule; }

  public void setDeferRule( short sDeferRule )
  { m_sDeferRule = sDeferRule; }

  
  public String getFkCatalog()
  { return m_strFkCatalog; }


  public void setFkCatalog( String fkCatalog )
  { m_strFkCatalog = fkCatalog; }


  public String getFkSchema()
  { return m_strFkSchema; }


  public void setFkSchema( String fkSchema )
  { m_strFkSchema = fkSchema; }


  public String getFkTableName()
  { return m_strFkTableName; }


  public void setFkTableName( String fkTableName )
  { m_strFkTableName = fkTableName; }


  public String getFkColName()
  { return m_strFkColName; }
  
  public void setFkColName( String strFkColName )
  { m_strFkColName = strFkColName; }
  
  public String toString()
  { return m_strFkColName + "==>" + m_strPkTableName + ":" + m_strPkColName; }
  
} // end class VwForeignKeyInfo{}


// *** End of VwForeignKeyInfo.java ***

