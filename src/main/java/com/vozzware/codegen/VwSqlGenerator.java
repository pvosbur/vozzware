package com.vozzware.codegen;

/*
============================================================================================

    Source File Name: VwSqlGenerator.java

    Author:           petervosburgh
    
    Date Generated:   12/1/17

    Time Generated:   12:47 PM

============================================================================================
*/

import com.vozzware.db.VwColInfo;
import com.vozzware.db.VwDatabase;
import com.vozzware.db.VwTableDef;
import com.vozzware.util.VwDelimString;
import com.vozzware.util.VwExString;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class defines methods to generate select, insert, update and delete sql statements
 */
public class VwSqlGenerator
{
  public  static final String[]  s_astrTableAliases = { "a","b","c","d","e","f","g","h","i",
                                                        "j","k","l","m","n","o","p", "q", "r",
                                                        "s", "t", "u", "v", "v", "x", "y", "z" };

  private VwDatabase m_db;

  private int m_nLineIndentSpaces =  2;
  private int m_nMaxLineLength = 80;

  private String m_strIndentSpaces;

  /**
   * Constructor
   * @param db The VwDatabase object for retrieving all table meta data
   */
  public VwSqlGenerator( VwDatabase db )
  {
    m_db = db;
    m_strIndentSpaces = VwExString.lpad( "", ' ', m_nLineIndentSpaces );
  }

  /**
   * Gets the new line space indent value
   * @return
   */
  public int getLineIndentSpaces()
  {
    return m_nLineIndentSpaces;

  }

  /**
   * Sets the new line indent space value
   * @param nLineIndentSpaces
   */
  public void setLineIndentSpaces( int nLineIndentSpaces )
  {
    m_nLineIndentSpaces = nLineIndentSpaces;

    m_strIndentSpaces = VwExString.lpad( "", ' ', m_nLineIndentSpaces );

  }

  /**
   * Returns the max line length value
   * @return
   */
  public int getMaxLineLength()
  {
    return m_nMaxLineLength;
  }

  /**
   * Sets the max line length value
   * @param nMaxLineLength
   */
  public void setMaxLineLength( int nMaxLineLength )
  {
    m_nMaxLineLength = nMaxLineLength;
  }

  /**
    * Generates a select statement for a single table
    *
    * @param tableDef The VwTableDef for the table
    * @return
    */
  public StringBuffer genSelect( VwTableDef tableDef ) throws Exception
  {
    return genSelect( tableDef, null, null, null );
  }

  /**
   * Generates a select statement for a single table
   *
   * @param tableDef The VwTableDef for the table
   * @param strSelectInto The fully qualified java class name of the object to receive the query contents
   * @return
   */
  public StringBuffer genSelect( VwTableDef tableDef, String strSelectInto ) throws Exception
  {
    return genSelect( tableDef, null, null, strSelectInto );
  }

  /**
   * Generates a select statement for a single table
   *
   * @param tableDef The VwTableDef for the table
   * @param dlmsOmitColumns  a comma delimited list of columns to exclude from the query (may be null)
   * @param dlmsIncludeColumns a comma delimited list of columns to include from the query (may be null)
   * @param strSelectInto The fully qualified java class name of the object to receive the query contents
   *
   * @exception Exception if both include and exclude delimited string column list are both specified
   * @return
   */
  public StringBuffer genSelect( VwTableDef tableDef, VwDelimString dlmsOmitColumns, VwDelimString dlmsIncludeColumns, String strSelectInto ) throws Exception
  {

    if ( dlmsOmitColumns != null && dlmsIncludeColumns != null )
    {
      throw new Exception( "genSelect cannot specify both dlmsIncludeColumns and dlmsOmitColumns" );
    }

    StringBuffer sbSelect = new StringBuffer( "select " );

    genSelectColumns( sbSelect, tableDef, tableDef.getTableAlias(), null, dlmsOmitColumns, dlmsIncludeColumns );

    testLineLength( sbSelect );

    sbSelect.append( "from " ).append( tableDef.getTableName() );

    if ( tableDef.getTableAlias() != null )
    {
      sbSelect.append( " ").append( tableDef.getTableAlias() );
    }

    if ( strSelectInto != null )
    {
      appendNewLineSpaces( sbSelect );
      sbSelect.append( "into " ).append( strSelectInto );
    }

    return sbSelect;

  }

  /**
   * Generates a joind select statement from the list of tabledefs
   *
   * @param listTablesToJoin List of VwTableDefs for the joined select statement. The table aliases start with "a" and use the
   *                         next lowercase letter in the alphabet for ech table in the join
   * @return
   * @throws Exception
   */
  public StringBuffer genJoinedSelect( List<VwTableDef> listTablesToJoin  ) throws Exception
  {
    return genJoinedSelect( listTablesToJoin, null, null, null );
  }

  /**
   * Generates a joind select statement from the list of tabledefs
   *
   * @param listTablesToJoin List of VwTableDefs for the joined select statement. The table aliases start with "a" and use the
   *                         next lowercase letter in the alphabet for ech table in the join
   *
   * @param strSelectInto a fully qualified java class name of the java object to receive the query result
   * @return
   * @throws Exception
   */
  public StringBuffer genJoinedSelect( List<VwTableDef> listTablesToJoin, String strSelectInto  ) throws Exception
  {
    return genJoinedSelect( listTablesToJoin, null, null, strSelectInto );
  }

  /**
   * Generates a joind select statement from the list of tabledefs
   *
   * @param listTablesToJoin List of VwTableDefs for the joined select statement. The table aliases start with "a" and use the
   *                         next lowercase letter in the alphabet for ech table in the join
   * @param dlmsOmitColumns A comma delimited string of table columns to exclude from the update statment. If specified
   *                        then dlmsIncludeColumns must be null.
   *
   * @param dlmsIncludeColumns A comma delimited string of table columns to incude in the update statment, all other columns
   *                           will be ignored. If specified then dlmsOmitColumns must be null.
   * @param strSelectInto a fully qualified java class name of the java object to receive the query result
   * @return
   * @exception Exception if both dlmsOmitColumns and dlmsIncludeColumns are specified
   */
  public StringBuffer genJoinedSelect( List<VwTableDef> listTablesToJoin, VwDelimString dlmsOmitColumns, VwDelimString dlmsIncludeColumns, String strSelectInto  ) throws Exception
  {

    if ( dlmsOmitColumns != null && dlmsIncludeColumns != null )
    {
      throw new Exception( "genJoinedSelect cannot specify both dlmsIncludeColumns and dlmsOmitColumns" );
    }

    StringBuffer sbSelect = new StringBuffer( "select " );

    Map<String,VwColInfo> mapPrimeKeysGenned = new HashMap<>(  );
    VwTableDef tdBase = listTablesToJoin.get( 0 );

    // Remove base table from the list
    listTablesToJoin.remove( 0 );

    int nAliasIndex = 0;

    genSelectColumns( sbSelect, tdBase, s_astrTableAliases[ 0 ], mapPrimeKeysGenned, dlmsOmitColumns, dlmsIncludeColumns );

    for ( VwTableDef tableDef : listTablesToJoin )
    {
      genSelectColumns( sbSelect, tableDef, s_astrTableAliases[ ++nAliasIndex ], mapPrimeKeysGenned, dlmsOmitColumns, dlmsIncludeColumns );
    }

    appendNewLineSpaces( sbSelect );

    sbSelect.append( "from " ).append( tdBase.getTableName() ).append( " a ");
    nAliasIndex = 0;

    for ( VwTableDef tableDef : listTablesToJoin )
    {
      genTableJoins( sbSelect, tdBase, tableDef, s_astrTableAliases[ ++nAliasIndex ] );
    }

    if ( strSelectInto != null )
    {
      appendNewLineSpaces( sbSelect );
      sbSelect.append( "into " ).append( strSelectInto );
    }

    return sbSelect;

  }


  /**
   * Generates an update statement for a single table
   *
   * @param tableDef The VwTableDef for the table
   *
   * @return
   */
  public StringBuffer genUpdate( VwTableDef tableDef )  throws Exception
  {
    return genUpdate( tableDef, null, null );
  }

  /**
   * Generates an update statement for a single table
   *
   * @param tableDef The VwTableDef for the table
   *
   * @param dlmsOmitColumns A comma delimited string of table columns to exclude from the update statment. If specified
   *                        then dlmsIncludeColumns must be null.
   *
   * @param dlmsIncludeColumns A comma delimited string of table columns to incude in the update statment, all other columns
   *                           will be ignored. If specified then dlmsOmitColumns must be null.
   * @exception Exception if both dlmsOmitColumns and dlmsIncludeColumns are specified
   * @return
   */
  public StringBuffer genUpdate( VwTableDef tableDef, VwDelimString dlmsOmitColumns, VwDelimString dlmsIncludeColumns ) throws Exception
  {

    if ( dlmsOmitColumns != null && dlmsIncludeColumns != null )
    {
      throw new Exception( "genUpdate cannot specify both dlmsIncludeColumns and dlmsOmitColumns" );
    }

    StringBuffer sbUpdate = new StringBuffer( "update " ).append( tableDef.getTableName() );

    if ( tableDef.getTableAlias() != null )
    {
      sbUpdate.append ( " ").append( tableDef.getTableAlias() );

    }

    sbUpdate.append( "\n").append( m_strIndentSpaces ).append( "set ");

    genUpdateColumns( sbUpdate, tableDef, dlmsOmitColumns, dlmsIncludeColumns );

    return sbUpdate;

  }

  /**
   * Add the table columns to the update's set list
   * @param sbUpdate
   * @param vwTableDef
   * @param dlmsOmitColumns
   */
  private void genUpdateColumns( StringBuffer sbUpdate, VwTableDef vwTableDef, VwDelimString dlmsOmitColumns, VwDelimString dlmsIncludeColumns )
  {
    List<VwColInfo>listColumns = vwTableDef.getColumns();
    List<VwColInfo>listPrimaryKeys = vwTableDef.getPrimaryKeys();

    int nUpdateCount = 0;
    for (VwColInfo ci : listColumns )
    {
      String strColName = ci.getColumnName();

      if ( dlmsOmitColumns != null && dlmsOmitColumns.isIn( strColName ) )
      {
        continue;
      }

      if ( dlmsIncludeColumns != null && !dlmsIncludeColumns.isIn( strColName ) )
      {
        continue;
      }

      if ( isPrimaryKey( ci, listPrimaryKeys ))
      {
        continue;
      }

      testLineLength( sbUpdate );

      if ( ++nUpdateCount > 1 )
      {
        sbUpdate.append( ", " );

      }

      sbUpdate.append( strColName ).append( " = :").append( VwExString.makeJavaName( strColName, true ));

    }  // end ( for...)

  }

  /**
   * Generates an insert statememnt for the tabledDef
   *
   * @param tableDef The VwTableDef object for the table to generate the insert statement from
   *
   * @exception Exception if both dlmsOmitColumns and dlmsIncludeColumns are specified
   * @return
   */
   public StringBuffer genInsert( VwTableDef tableDef ) throws Exception
   {
     return genInsert( tableDef, null, null, null );
   }

  /**
   * Generates an insert statememnt for the tabledDef
   *
   * @param tableDef The VwTableDef object for the table to generate the insert statement from
   *
   * @param strPrimaryKeyPolicy If Using the Vozzworks orm package, the primary key policy for generated primary key
   *                            (i.e., sequences, internal, uuid ...) may be null
   * @exception Exception if both dlmsOmitColumns and dlmsIncludeColumns are specified
   * @return
   */
  public StringBuffer genInsert( VwTableDef tableDef, String strPrimaryKeyPolicy ) throws Exception
  {
    return genInsert( tableDef, null, null, strPrimaryKeyPolicy );
  }

  /**
   * Generates an insert statememnt for the tabledDef
   *
   * @param tableDef The VwTableDef object for the table to generate the insert statement from
   * @param dlmsOmitColumns A comma delimited string of table columns to exclude from the update statment. If specified
   *                        then dlmsIncludeColumns must be null.
   *
   * @param dlmsIncludeColumns A comma delimited string of table columns to incude in the update statment, all other columns
   *                           will be ignored. If specified then dlmsOmitColumns must be null.
   * @exception Exception if both dlmsOmitColumns and dlmsIncludeColumns are specified
   * @return
   */
  public StringBuffer genInsert( VwTableDef tableDef, VwDelimString dlmsOmitColumns, VwDelimString dlmsIncludeColumns, String strPrimaryKeyPolice ) throws Exception
  {

    if ( dlmsOmitColumns != null && dlmsIncludeColumns != null )
    {
      throw new Exception( "genInsert cannot specify both dlmsIncludeColumns and dlmsOmitColumns" );
    }

    StringBuffer sbInsert = new StringBuffer( "insert into " ).append( tableDef.getTableName() );

    sbInsert.append( "\n");

    genInsertColumns( sbInsert, tableDef, tableDef.getTableAlias(),  dlmsOmitColumns, dlmsIncludeColumns, strPrimaryKeyPolice );

    return sbInsert;

  }

  /**
   * Generate a select list for all the columns in all of the tables defined in each VwTableDef
   * @param sbInsert
   * @param vwTableDef
   * @param strTableAlias
   */
  private void genInsertColumns( StringBuffer sbInsert, VwTableDef vwTableDef, String strTableAlias,
                                 VwDelimString dlmsOmitColumns, VwDelimString dlmsIncludeColumns, String strPrimayKeyPolicy ) throws Exception
  {
    List<VwColInfo>listColumns = vwTableDef.getColumns();

    List<VwColInfo>listPrimaryKeys = vwTableDef.getPrimaryKeys();
    StringBuffer sbInsertValuePlaceHolders = new StringBuffer();

    sbInsert.append( m_strIndentSpaces ).append( "( ");

    int nInsertColCount = 0;

    for ( VwColInfo ci : listColumns )
    {
      String strColName = ci.getColumnName();

      if ( dlmsOmitColumns != null && dlmsOmitColumns.isIn( strColName ) )
      {
        continue;
      }

      if ( dlmsIncludeColumns != null && !dlmsIncludeColumns.isIn( strColName ) )
      {
        continue;
      }

      // Dont create an insert column form a primary key that is auto generated by the RDBMS
      if ( isPrimaryKey( ci, listPrimaryKeys ))
      {
        if (strPrimayKeyPolicy != null && strPrimayKeyPolicy.equals( "internal" ) )
        {
          continue;
        }

      }

      if ( sbInsertValuePlaceHolders.length() > 0 )
      {
        sbInsertValuePlaceHolders.append( ", " );
      }

      sbInsertValuePlaceHolders.append( ":").append( VwExString.makeJavaName( ci.getColumnName(), true ));

      testLineLength( sbInsertValuePlaceHolders );
      testLineLength( sbInsert );

      if ( ++nInsertColCount > 1 )
      {
        sbInsert.append( ", ");

      }
      
      sbInsert.append( strColName );

      if ( ci.getColumnAliasName() != null )
      {
        sbInsert.append( " " ).append( ci.getColumnAliasName() );
      }
      
    }  // end ( for...)

    sbInsert.append( " )\r\n").append( m_strIndentSpaces ).append( "values ( ").append( sbInsertValuePlaceHolders ).append( " )");

  }


  /**
   * Adss a new line character in the buffer if a line exceeds the max line length
   * @param sbTest
   */
  private void testLineLength( StringBuffer sbTest )
  {
    if ( sbTest.length() - sbTest.lastIndexOf( "\n" ) >= m_nMaxLineLength )
    {
      sbTest.append( "\r\n" ).append( m_strIndentSpaces );
      
    }
  }

  /**
   * Generates a primary key where clause
   *
   * @param tableDef The table for the table the where clause is generated
   * @param strTableAlias The table alias (may be null)
   *
   * @return A StringBuffer with te where clause
   * @throws Exception if no primary key is defined
   */
  public StringBuffer genPrimaryKeyWhereClause( VwTableDef tableDef, String strTableAlias )  throws Exception
  {
    List<VwColInfo>listPrimaryKeys = tableDef.getPrimaryKeys();

    if ( listPrimaryKeys == null || listPrimaryKeys.size() == 0 )
    {
      throw new Exception( "Table: '" + tableDef.getTableName() + "' does not define a primary key. Cannot generate a where clause");

    }

    StringBuffer sbWhere = new StringBuffer( "\r\n").append( m_strIndentSpaces ).append( "  where " );

    int nKeyCount = 0;

    for ( VwColInfo ci : listPrimaryKeys )
    {

      if ( nKeyCount++ > 0 )
      {
        sbWhere.append( " and " );
      }

      if ( strTableAlias != null )
      {
        sbWhere.append( strTableAlias ).append( "." );

      }

      sbWhere.append( ci.getColumnName() ).append( " = :" ).append( VwExString.makeJavaName( ci.getColumnName(), true ) );

    } // enf for()

    return sbWhere;

  }

  /**
   * Generate an exists select statement
   *
   * @param tableDef  The table def for the exists statement
   * @param strTableAlias The table alias (my be null)
   * @return
   */
  public StringBuffer genExists( VwTableDef tableDef, String strTableAlias )
  {
    StringBuffer sbExists = new StringBuffer( "select 1 from " ).append( tableDef.getTableName() );

    if ( strTableAlias != null )
    {
      sbExists.append( " " ).append( strTableAlias );
    }

    return sbExists;

  }


  /**
   *
   * @param sbSelect
   * @param tdBase
   * @param tableToJoin
   * @param strTableAlias
   */
  private void genTableJoins( StringBuffer sbSelect, VwTableDef tdBase, VwTableDef tableToJoin, String strTableAlias )
  {
    List<VwColInfo>listTableBasePrimaryKeys = tdBase.getPrimaryKeys();
    List<VwColInfo>listTableJoinPrimaryKeys = tableToJoin.getPrimaryKeys();

    String strTableName = tableToJoin.getTableName();

    appendNewLineSpaces( sbSelect );
    sbSelect.append( "inner join ").append( strTableName ).append( " ").append( strTableAlias ).append( " on ");

    int nPkListNbr = -1;

    for( VwColInfo ciPkBase : listTableBasePrimaryKeys )
    {
      ++nPkListNbr;

      if ( nPkListNbr > 0 )
      {
        sbSelect.append( " and ");
      }

      sbSelect.append( "a.").append( ciPkBase.getColumnName() ).append( " = ").append( strTableAlias );
      sbSelect.append( ".").append (listTableJoinPrimaryKeys.get( nPkListNbr ).getColumnName() );
    }

   }

  private String getTableJoinPkColName( String strColumnName, List<VwColInfo> listTableJoinPrimaryKeys )
  {
    for( VwColInfo pki : listTableJoinPrimaryKeys )
    {
      if ( strColumnName.equals( pki.getColumnName() ) )
      {
        return pki.getColumnName() ;
      }
    }

    return null;
  }

  /**
   * Generate a select list for all the columns in all of the tables defined in each VwTableDef
   * @param sbSelect
   * @param vwTableDef
   * @param strTableAlias
   * @param mapPrimeKeysGenned
   */
  private void genSelectColumns( StringBuffer sbSelect, VwTableDef vwTableDef, String strTableAlias, Map<String,VwColInfo> mapPrimeKeysGenned, VwDelimString dlmsOmitColumns, VwDelimString dlmsIncludeColumns   )
  {
    List<VwColInfo>listColumns = vwTableDef.getColumns();
    List<VwColInfo>listPrimaryKeys = vwTableDef.getPrimaryKeys();

    for (VwColInfo ci : listColumns )
    {
      String strColName = ci.getColumnName();

      if ( dlmsOmitColumns != null && dlmsOmitColumns.isIn( strColName ) )
      {
        continue;
      }

      if ( dlmsIncludeColumns != null && !dlmsIncludeColumns.isIn( strColName ) )
      {
        continue;
      }

      if ( isPrimaryKey( ci, listPrimaryKeys ))
      {
        if ( mapPrimeKeysGenned != null && mapPrimeKeysGenned.containsKey( strColName ) )
        {
          continue;
        }

        if ( mapPrimeKeysGenned != null )
        {
          mapPrimeKeysGenned.put( ci.getColumnName(), ci );
        }
      }

      testLineLength( sbSelect );

      if ( sbSelect.length() > " select ".length() )
      {
        sbSelect.append( ", " );

      }

      if ( strTableAlias != null )
      {
        sbSelect.append( strTableAlias ).append( '.' );
      }

      sbSelect.append( strColName ).append( " \"").append( VwExString.makeJavaName( strColName, true )).append( "\" ");

    }  // end ( for...)

  }

  /**
   * Checks if the column is a primary key
   *
   * @param colInfo The column infeo object
   * @param listPrimaryKeys List of primary keys for this table
   *
   * @return true if column is a primary key, false otherwise
   */
  private boolean isPrimaryKey( VwColInfo colInfo, List<VwColInfo> listPrimaryKeys )
  {
    for( VwColInfo ciPrimeKey : listPrimaryKeys )
    {
      if( colInfo.getColumnName().equals( ciPrimeKey.getColumnName()  ))
      {
        return true;
      }
    }

    return false;

  }

  /**
   * Appends a new line character and adds the indetnt spaces
   *
   * @param sb The string buffer to append to
   */
  private void appendNewLineSpaces( StringBuffer sb )
  {
    sb.append( "\n").append( m_strIndentSpaces );

  }
} // end VwSqlGenerator{}
