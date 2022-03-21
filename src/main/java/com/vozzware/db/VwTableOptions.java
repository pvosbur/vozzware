/*
 ============================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

  THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
  PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
  CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

  Source Name:  VwTableOptions.java


 ============================================================================
*/

package com.vozzware.db;

/**
 * This class is used by the VwSqlStmtGen class for generating select statements that join tables
 */
public class VwTableOptions
{
  private boolean   m_fPrefixWithTableName;
  private String    m_strAlias;
  private boolean   m_fAutoTableAliases = true;
  private String    m_strSchema;
  

  /**
   * If true, each column will have a table name prefix
   *
   * @param fState the boolean state
   */
  public void setPrefixWithTableName( boolean fState )
  { m_fPrefixWithTableName = fState; }

  /**
   * Gets the state of the PrefixWithTableName
   */
  public boolean getPrefixWithTableName()
  { return m_fPrefixWithTableName; }


  /**
   * Sets the table alias name that columns of this table should be prefixed with
   *
   * @param strAlias The table alias to prefix column names with
   */
  public void setTableAlias( String strAlias )
  { m_strAlias = strAlias; }


  /**
   * Gets the table alias name
   *
   */
  public String getTableAlias()
  { return m_strAlias; }


  /**
   * If true auto aliases names will be generated
   * @param fAutoTableAliases true to urn on auto aliases
   */
  public void setAutoTableAliases( boolean fAutoTableAliases )
  { m_fAutoTableAliases = fAutoTableAliases; }


  /**
   * Returns true if auto table aliases are on
   * @return
   */
  public boolean hasAutoTableAliases()
  { return m_fAutoTableAliases; }

  
  public void setSchema( String strSchema )
  { m_strSchema = strSchema; }
  
  public String getSchema()
  { return m_strSchema; }
  
} // end class VwTableOptions{}

// *** End of VwTableOptions.java ***

