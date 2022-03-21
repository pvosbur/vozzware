package com.vozzware.db;

/*
============================================================================================

    Source File Name: Transaction.java

    Author:           petervosburgh
    
    Date Generated:   12/1/17

    Time Generated:   7:29 AM

============================================================================================
*/

import java.util.List;

/**
 * This class defines a Database table definition which has all the meta information about the table
 * and the list of column, primary, foreign keys
 */
public class VwTableDef
{
  private VwDatabase m_db;
  private String m_strCatalog;
  private String m_strSchema;
  private String m_strTableName;
  private String m_strTableAlias;

  private List<VwColInfo>m_listTableCols;
  private List<VwColInfo>m_listPrimaryKeys;
  private List<VwForeignKeyInfo>m_listForeignKeys;


  public VwTableDef( VwDatabase db, String strCatalog, String strSchema, String strTableName ) throws Exception
  {
    m_db = db;

    m_strCatalog = strCatalog;
    m_strSchema = strSchema;
    m_strTableName = strTableName;

    m_listTableCols = db.getColumns( strCatalog, strSchema, strTableName );

    if ( m_listTableCols == null || m_listTableCols.size() == 0 )
    {
      throw new Exception( "Table: " + strTableName + " does not exist" );
      
    }
    m_listPrimaryKeys = db.getPrimaryKeys( strCatalog, strSchema, strTableName );
    m_listForeignKeys = db.getForeignKeys( strCatalog, strSchema, strTableName );
  }

  /**
   * Returns the table name with the schema prefix if schema specified
   * @return
   */
  public String getTableName()
  {
    String strTableName = "";

    if ( m_strSchema != null )
    {
      strTableName = m_strSchema + ".";
    }

    strTableName += m_strTableName;

    return strTableName;
  }

  /**
   * Gets the list of table columns
   * @return
   */
  public List<VwColInfo>getColumns()
  {
    return m_listTableCols;
  }

  /**
   * gets list of the primary keys
   * @return
   */
  public List<VwColInfo>getPrimaryKeys()
  {
    return m_listPrimaryKeys;
  }

  /**
   * Gets list of any foreign keys
   * @return
   */
  public List<VwForeignKeyInfo>getForeignKeys()
   {
     return m_listForeignKeys;
   }

  /**
   * Gets the table alias
   * @return
   */
  public String getTableAlias()
  {
    return m_strTableAlias;
  }

  /**
   * Dets the table alias
   * @param strTableAlias
   */
  public void setTableAlias( String strTableAlias )
  {
    m_strTableAlias = strTableAlias;
  }
} // end VwTableDef{}
