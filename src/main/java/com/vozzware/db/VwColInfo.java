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


package com.vozzware.db;                         // Our package

import java.sql.DatabaseMetaData;
import java.sql.ResultSetMetaData;

/**
 * This class contains all database information (attributes) about a column in a table
 */
public class VwColInfo extends Object
{


  String m_strDatabase;         // Database the column is in (may be null)
  String m_strSchema;           // Table schema (may be null)
  String m_strCatalog;          // Table catalog (may be null)
  String m_strTableName;        // Name of the Table the column is in
  String m_strColName;          // Column name
  String m_strColAliasName;     // Column alias name (User defined)
  short  m_sSQLType;            // SQL type from java.sql.Types
  short  m_sColParamType;       // For stored procedures only: the IN, OUT, INOUT param type
  String m_strSQLType;          // Data source dependent type name
  int    m_nColSize;            // Column size. For char or date types this is the maximum number of characters,
                                // for numeric or decimal types this is the precision.

  int    m_nDecDigits;          // The number of fractional digits
  int    m_nRadix;              // The radix (typically either 10 or 2)
  int    m_nNullable;           // Is NULL allowed?
  String m_strRemarks;          // Comment describing column (may be null)
  String m_strDefValue;         // Default value for column (may be null )

  public static final short UNKNOWN = (short)DatabaseMetaData.procedureColumnUnknown;
  public static final short IN = (short)DatabaseMetaData.procedureColumnIn;
  public static final short OUT = (short)DatabaseMetaData.procedureColumnOut;
  public static final short INOUT = (short)DatabaseMetaData.procedureColumnInOut;
  public static final short RETURN = (short)DatabaseMetaData.procedureColumnReturn;
  public static final short RESULT= (short)DatabaseMetaData.procedureColumnResult;


  /**
   * Default Constructor
   */
  public VwColInfo()
  { ; }

  
  /**
   * Initialize from a ResultSetMetaData instance
   * 
   * @param rsm The ResultSetMetaData instance to initialize this object
   * @param nColNBr The column number to retrieve the data
   */
  public VwColInfo( ResultSetMetaData rsm, int nColNbr  ) throws Exception
  {
    m_strDatabase = null;
    m_strSchema = rsm.getSchemaName( nColNbr );
    m_strCatalog = rsm.getCatalogName( nColNbr );
    m_strTableName = rsm.getTableName( nColNbr );
    m_strColName = rsm.getColumnName( nColNbr );
    m_strColAliasName = null;
    m_sSQLType = (short)rsm.getColumnType( nColNbr );
    m_sColParamType = 0;
    m_strSQLType = rsm.getColumnTypeName( nColNbr );
    m_nColSize = rsm.getPrecision( nColNbr );
    m_nDecDigits = rsm.getScale( nColNbr );
    m_nRadix = 0;
    m_nNullable = rsm.isNullable( nColNbr );
    m_strRemarks = null;
    m_strDefValue = null;

  }
  
  
  /**
   * Gets the name of the Database the column is in
   *
   * @return The database name for the column or null if not supported by the data source
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
   * Gets the column alisa name (if defined )
   *
   * @return The column alias name
   */
  public final String getColumnAliasName()
  { return m_strColAliasName; }


  /**
   * Sets an alias name for the column
   *
   * @param strColAliasName The column name
   */
  public final void  setColumnAliasName( String strColAliasName )
  { m_strColAliasName = strColAliasName; }


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

  /**
   * Gets the SQL TYPE of column based upon the java.sql.Types constants
   *
   * @return The SQL type of the column (one of the java.sql.Types constants)
   */
  public final short getSQLType()
  { return m_sSQLType; }



  /**
   * Sets the SQL TYPE of column based upon the java.sql.Types constants
   * NOTE: Should only be called by an VwSQLTypeConverterDriver interface object.
   *
   * @param iSQLType The SQL Type for the column (one of the java.sql.Types constants)
   */

  public final void setSQLType( int iSQLType )
  { m_sSQLType = (short)iSQLType; }


  /**
   * Gets the Stored procedure column param type for stored procedue columns
   *
   * @return The SQL Param Type for the column (IN, OUT, INOUT)
   */
  public final short getProcParamType()
  { return m_sColParamType; }


  /**
   * Sets the Stored procedure column param type for stored procedue columns
   *
   * @param sColParamType The SQL Param Type for the column (IN, OUT, INOUT)
   */
  public final void setProcParamType( short sColParamType )
  { m_sColParamType = sColParamType; }


  /**
   * Gets the SQL TYPE name (datasource dependent)
   *
   * @return The SQL Type name (the string representation of one of the type
   * constants from java.sql.Types)
   */
  public final String getSQLTypeName()
  { return m_strSQLType; }


  /**
   * Sets the SQL TYPE name (datasource dependent)
   * NOTE: Should only be called by an VwSQLTypeConverterDriver interface object.
   *
   * @param strSQLTypeName The SQL Type name for the column (the string
   * representation of one of the type constants from java.sql.Types).
   */
  public final void setSQLTypeName( String strSQLTypeName )
  { m_strSQLType = strSQLTypeName; }


  /**
   * Gets the max column size for char data or the precision for numeric columns
   *
   * @return The column size
   */
  public final int getColSize()
  { return m_nColSize; }

  /**
   * Sets the column size
   *
   * @param nColSize The column size
   */
  public void setColSize( int nColSize )
  { m_nColSize = nColSize; }


  /**
   * Gets the nbr of decimal digits for a real number type column
   *
   * @return The nbr of decimal digits for the column
   */
  public final int getNbrDecimalDigits()
  { return m_nDecDigits; }


  /**
   * Sets the nbr of decimal digits for a real number type column
   *
   * @param nDecDigits The nbr of decimal digits for the column
   */
  public final void setNbrDecimalDigits( int nDecDigits )
  { m_nDecDigits = nDecDigits; }


  /**
   * Gets the radix for the column (usually 10 or 2)
   *
   * @return The radix for the column
   */
  public final int getRadix()
  { return m_nRadix; }


  /**
   * Sets the radix for the column (usually 10 or 2)
   *
   * @param nRadix The radix for the column
   */
  public final void setRadix( int nRadix )
  { m_nRadix = nRadix; }

  /**
   * Gets the nullable state of a column
   *
   * @return One of the following:
   * �	columnNoNulls - might not allow NULL values
   * �	columnNullable - definitely allows NULL values
   * �	columnNullableUnknown - nullability unknown
   */
  public final int getNullable()
  { return m_nNullable; }


  /**
   * Sets the nullable state of a column
   *
   * @param nNullable One of the following:
   * �	columnNoNulls - might not allow NULL values
   * �	columnNullable - definitely allows NULL values
   * �	columnNullableUnknown - nullability unknown
   */
  public final void setNullable( int nNullable )
  { m_nNullable = nNullable; }


  /**
   * Gets the column comments
   *
   * @return The column comments if supported by the datasource else null is returned
   */
  public final String getRemarks()
  { return m_strRemarks; }


  /**
   * Sets the column comments
   *
   * @param strRemarks The column comments if supported by the datasource else null is returned
   */
  public final void setRemarks( String strRemarks )
  { m_strRemarks = strRemarks; }


  /**
   * Gets column default value
   *
   * @return The column defualt value or null if there is no default value
   */
  public final String getDefValue()
  { return m_strDefValue; }

  /**
   * Sets column default value
   *
   * @param strDefValue The column defualt value or null if there is no default value
   */
  public final void setDefValue( String strDefValue )
  { m_strDefValue = strDefValue; }


  public String toString()
  { return m_strTableName + ":" + m_strColName + "==>" + m_strSQLType; }
  
  /**
   * Gets the name for a given SQL TYPE constant from java.sql.Types
   *
   * @return The SQL Type name (the string representation of one of the type
   * constants from java.sql.Types).
   */
  public static final String getTypeName( int iType )
  {
    switch( iType )
    {
      case java.sql.Types.BIT :
        return "BIT";

      case java.sql.Types.TINYINT :
        return "TINYINT";

      case java.sql.Types.SMALLINT :
        return "SMALLINT";

      case java.sql.Types.INTEGER :
        return "INTEGER";

      case java.sql.Types.BIGINT :
        return "BIGINT";

      case java.sql.Types.FLOAT :
        return "FLOAT";

      case java.sql.Types.REAL :
        return "REAL";

      case java.sql.Types.DOUBLE :
        return "DOUBLE";

      case java.sql.Types.NUMERIC :
        return "NUMERIC";

      case java.sql.Types.DECIMAL :
        return "DECIMAL";

      case java.sql.Types.CHAR :
        return "CHAR";

      case java.sql.Types.VARCHAR :
        return  "VARCHAR";

      case java.sql.Types.LONGVARCHAR :
        return "LONGVARCHAR";

      case java.sql.Types.DATE :
        return "DATE";

      case java.sql.Types.TIME :
        return "TIME";

      case java.sql.Types.TIMESTAMP :
        return "TIMESTAMP";

      case java.sql.Types.BINARY :
        return "BINARY";

      case java.sql.Types.VARBINARY :
        return "VARBINARY";

      case java.sql.Types.LONGVARBINARY :
        return "LONGVARBINARY";

      case java.sql.Types.NULL :
        return "NULL";

      default:
        return "UNKNOWN";

   } // end switch()

 } // end getTypeName( int iType )

  

} // end class VwColInfo{}


// *** End of VwColInfo.java ***

