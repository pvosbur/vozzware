/*
 ============================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

  THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
  PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
  CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

  Source Name:   VwColDescriptor.java


 ============================================================================
*/
package com.vozzware.db;

public class VwColDescriptor
{

  private String    m_strColName;     // the name of the column
  private String    m_strColAlias;    // the column alias
  private String    m_strTableName;   // The table name for this column
  private String    m_strTableAlias;  // The table alias or correlation name
  private String    m_strWhere;       // select where clause constraint for column
  private String    m_strAggregate;   // Aggregate funtion
  private String    m_strHaving;      // Having restriction clause
  private String    m_strColValue;    // Column value used for inserts and updates
  private boolean   m_fIncluded;      // Include this column in the select list
  private String    m_strSort;        // null, ascending, descending
  private short     m_sSQLType;       // SQL data type from java.sql.Types

  public String getColName()
  {
    return m_strColName;
  }

  public void setColName( String strName )
  {
    m_strColName = strName;
  }

  public String getColAlias()
  {
    return m_strColAlias;
  }

  public void setColAlias( String strColAlias )
  {
    if ( !strColAlias.startsWith( "\"" ))
      m_strColAlias = "\"" + strColAlias + "\"";
    else
     m_strColAlias = strColAlias;
  }

  public String getTableName()
  {
    return m_strTableName;
  }

  public void setTableName( String strTableName )
  {
    m_strTableName = strTableName;
  }

  public String getTableAlias()
  {
    return m_strTableAlias;
  }

  public void setTableAlias( String strTableAlias )
  {
    m_strTableAlias = strTableAlias;
  }

  public boolean isIncluded()
  {
    return m_fIncluded;
  }

  public void setInclude( boolean fIncluded )
  {
    m_fIncluded = fIncluded;
  }

  public String getWhere()
  {
    return m_strWhere;
  }

  public void setWhere( String strWhere )
  {
    m_strWhere = strWhere;
  }

  public String getAggregate()
  {
    return m_strAggregate;
  }

  public void setAggregate( String strAggregate )
  {
    m_strAggregate = strAggregate;
  }

  public String getHaving()
  {
    return m_strHaving;
  }

  public void setHaving( String strHaving )
  {
    m_strHaving = strHaving;
  }


  public String getColValue()
  {
    return m_strColValue;
  }

  public void setColValue( String strColValue )
  {
    m_strColValue = strColValue;
  }

  public String getSort()
  {
    return m_strSort;
  }

  public void setSortDesc(String strSort )
  {
    m_strSort = strSort;
  }

  public short getSQLType()
  {
    return m_sSQLType;
  }

  public void setSQLType( short sSQLType )
  {
    m_sSQLType = sSQLType;
  }
} // end class VwColDescriptor{}

// *** End of VwColDescriptor.java ***

