/*
 ============================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

  THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
  PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
  CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

  Source Name:   VwColInfo.java


 ============================================================================
*/
package com.vozzware.db;

import java.util.List;

public class VwCatalogEntry
{
  /**
   * Constant for TABLE catalog type
   */
  public                      static final int TABLE = 1;

  /**
   * Constant for stored procedure catalog type
   */
  public                      static final int PROC = 2;

  String                      m_strCatalog;       // Catalog may be null;
  String                      m_strSchema;        // Schema owner
  String                      m_strName;          // catalog entry  name (table or proc name)
  String                      m_strAlias;         // Alias for table/proc name

  List<VwColInfo>            m_listColumns;      // List of VwColInfo objects (one for each column in the table
  List<VwColInfo>            m_listPrimeKeys;    // List of column(s) that are the primary key(s) to this table
  List<VwForeignKeyInfo>     m_listForeignKeys;  // List of foreign keys contained in a table
  int                         m_nType;            // One of the catalog type constants

  /**
   * Get the ctatlog object type. will be either TABLE or PROC constant
   * @return
   */
  public int getType()
  { return m_nType; }


  /**
   * Sets the catalog entry type either ( TABLE or PROC constant )
   * @param nType
   */
  public void setType( int nType )
  { m_nType = nType; }


  /**
   * Return the name of the catalog (if available)
   * @return
   */
  public String getCatalog()
  {
    return m_strCatalog;
  }

  /**
   * Sets the catalog name
   * @param strCatalog  The catalog name
   */
  public void setCatalog( String strCatalog )
  {
    m_strCatalog = strCatalog;
  }


  /**
   * Gets the schema name this catalog object is owned bt
   * @return
   */
  public String getSchema()
  {
    return m_strSchema;
  }


  /**
   * Sets the scheam owner name for this catalog entry
   * @param strSchema The schema name
   */
  public void setSchema( String strSchema )
  {
    m_strSchema = strSchema;
  }


  /**
   * gets the catalog name ( table or proc name )
   * @return
   */
  public String getName()
  {
    return m_strName;
  }


  /**
   * Sets the catalog table or proc name
   * @param strName  The name of the table or proc
   */
  public void setName( String strName )
  {
    m_strName = strName;
  }

  /**
   * Get the alias name for a table (may be null)
   * @return
   */
  public String getAlias()
  {
    return m_strAlias;
  }


  /**
   * Sets the alias name for a tbale
   *
   * @param strAlias The alias name for the table
   */
  public void setAlias( String strAlias )
  {
    m_strAlias = strAlias;
  }

  /**
   * Gets a List of VwColInfo objects, one for each column in the table or proc
   * @return
   */
  public List<VwColInfo> getColumns()
  {
    return m_listColumns;
  }

  /**
   * Sets the column list for this table or proc
   * @param listColumns
   */
  public void setColumns( List<VwColInfo> listColumns )
  {
    m_listColumns = listColumns;
  }


  /**
   * Returns a List of VwColInfo objects one for each part of the primary key or
   * just a single VwColInfo entry if the key is not a composite key
   * @return
   */
  public List<VwColInfo> getPrimeKeys()
  {
    return m_listPrimeKeys;
  }

  /**
   * Sets the list ofr primay key(s)
   * @param listPrimeKeys
   */
  public void setPrimeKeys( List<VwColInfo>  listPrimeKeys )
  {
    m_listPrimeKeys = listPrimeKeys;
  }


  /**
   * Returns a List of VwColInfo objects one for each foreign key
   * @return
   */
  public List<VwForeignKeyInfo> getForeignKeys()
  {
    return m_listForeignKeys;
  }

  /**
   * Sets the list ofr primay key(s)
   * @param listPrimeKeys
   */
  public void setForeignKeys( List<VwForeignKeyInfo>  listForeignKeys )
  {
    m_listForeignKeys = listForeignKeys;
  }

  public String toString()
  {
    return m_strSchema + "." + m_strName;
  }
} // end class VwCatalogEntry{}

// *** End of VwCatalogEntry.java ***

