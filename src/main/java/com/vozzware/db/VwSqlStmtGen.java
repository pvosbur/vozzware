/*
 ============================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

  THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
  PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
  CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

  Source Name:  VwSqlStmtGen.java


 ============================================================================
*/

package com.vozzware.db;

import com.vozzware.util.VwDelimString;
import com.vozzware.util.VwExString;
import com.vozzware.util.VwFormat;
import com.vozzware.util.VwInputSource;
import com.vozzware.util.VwTextParser;
import com.vozzware.xml.VwDataObject;

import java.sql.DatabaseMetaData;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * This class contains static methods to generate vendor indepemdent SQL for<br>
 * select, insert, update, delete and stored procedure call statements.<br>
 * Each method required a List of VwColInfo objects for each database column to be
 * referenced in the statement.
 */
public class VwSqlStmtGen
{
  private static final int LINELEN = 50;
  public  static final String[]  s_astrTableAliases = { "a","b","c","d","e","f","g","h","i",
                                                        "j","k","l","m","n","o","p", "q", "r",
                                                        "s", "t", "u", "v", "v", "x", "y", "z" };
  


  /**
   * Generates a select statement using the column names in the List of VwColInfo objects
   *
   * @param listColInfo A List of VwColInfoObjects
   * @param strColPrefix an option column presfix to use (if not null)
   * @param strWhere An Optional SQL "WHERE" clause ( if not null )
   *
   * @return a String containg the SQL select statement
   */
  public static String genSelect( List<VwColInfo> listColInfo, String strColPrefix, String strWhere ) throws Exception
  {
    String strSelect = "select ";

    VwDelimString dlmsColList = new VwDelimString();
    String strColName = null;
    String strTableName = null;

    for ( VwColInfo ci : listColInfo )
    {
      if ( strTableName == null )
      {
        strTableName = ci.getTableName();
      }

      if ( strColPrefix != null )
      {
        strColName = strColPrefix + "." + ci.getColumnName();
      }
      else
      {
        strColName = ci.getColumnName();
      }

      if ( ci.getColumnAliasName() != null )
      {
        strColName += " " + ci.getColumnAliasName();
      }

      dlmsColList.add( strColName );

    } // end for

    String strColList = null;
    String strRemain = dlmsColList.toString();

    // If the column list is larger than 50 characters then add line feeds at around
    // each 50 characater mark at the comma boundary

    while ( strRemain.length() > LINELEN )
    {
      int ndx = LINELEN;

      // Find the first break point at a comma boundry

      while( ndx < strRemain.length() )
      {
        if ( strRemain.charAt( ndx ) == ',' )
        {
          ++ndx;
          break;
        }

        ++ndx;

      } // end while()

      if ( strColList == null )
      {
        strColList = strRemain.substring( 0, ndx );
      }
      else
      {
        strColList += "\n       " + strRemain.substring( 0, ndx );
      }

      strRemain = strRemain.substring( ndx );

    }

    if ( strColList == null )
    {
      strColList = strRemain;
    }
    else
    if ( strRemain.length() > 0 )
    {
      strColList += "\n       " + strRemain;
    }

    strSelect += strColList;

    strSelect += "\n  from " + strTableName;

    if ( strWhere != null )
    {
      strSelect += "\n  where " + strWhere;
    }

    return strSelect;

 }  // end genSelect()


  /**
   * Generates a select statement that joins the tables specified in the mapTableOpts
   *
   * @param listColInfo A List of VwColInfoObjects
   * @param mapTableOpts a Map of VwTableOption objects that define how the column names will be prefixed
   * @param strWhere An Optional SQL "WHERE" clause ( if not null )
   *
   * @return a String containg the SQL select statement
   */
  public static String genSelect( List<VwColInfo> listColInfo, Map mapTableOpts, String strWhere ) throws Exception
  {
    StringBuffer sbSelect = new StringBuffer( "select " );

    VwDelimString dlmsColList = new VwDelimString();
    String strColName = null;

    for ( VwColInfo ci : listColInfo )
    {
      VwTableOptions tblOpts = (VwTableOptions)mapTableOpts.get( ci.getTableName() );

      String strColPrefix = null;

      if ( tblOpts.getPrefixWithTableName() == true )
      {
        strColPrefix = ci.getTableName();
      }
      else
      {
        strColPrefix = tblOpts.getTableAlias();
      }

      if ( strColPrefix != null )
      {
        strColName = strColPrefix + "." + ci.getColumnName();
      }
      else
      {
        strColName = ci.getColumnName();
      }

      if ( ci.getColumnAliasName() != null )
      {
        strColName += " " + ci.getColumnAliasName();
      }

      dlmsColList.add( strColName );

    } // end for

    StringBuffer sbColList = new StringBuffer();
    String strRemain = dlmsColList.toString();

    // If the column list is larger than 50 characters then add line feeds at around
    // each 50 characater mark at the comma boundary

    while ( strRemain.length() > LINELEN )
    {
      int ndx = LINELEN;

      // Find the first break point at a comma boundry

      while( ndx < strRemain.length() )
      {
        if ( strRemain.charAt( ndx ) == ',' )
        {
          ++ndx;
          break;
        }

        ++ndx;

      } // end while()

      if ( sbColList.length() == 0 )
      {
        sbColList.append( strRemain.substring( 0, ndx ) );
      }
      else
      {
        sbColList.append( "\n       " ).append( strRemain.substring( 0, ndx ) );
      }

      strRemain = strRemain.substring( ndx );

    }

    if ( sbColList.length() == 0 )
    {
      sbColList.append( strRemain );
    }
    else
    if ( strRemain.length() > 0 )
    {
      sbColList.append( "\n       " ).append( strRemain );
    }

    sbSelect.append( sbColList );

    StringBuffer  sbTableList = new StringBuffer();

    for ( String strTable : (Set<String>)mapTableOpts.keySet()  )
    {
      VwTableOptions tblOpts = (VwTableOptions)mapTableOpts.get( strTable );
      String strSchema = tblOpts.getSchema();
      
      if ( strSchema != null )
      {
        strTable = strSchema + "." + strTable;
      }
      
      if ( sbTableList.length() == 0 )
      {
        sbTableList.append( strTable );
      }
      else
      {
        sbTableList.append( "," ).append( strTable );
      }

      if ( tblOpts.getTableAlias() != null )
      {
        sbTableList.append( " " ).append( tblOpts.getTableAlias() );
      }

    } // end for()

    sbSelect.append( "\n  from " ).append( sbTableList );

    if ( strWhere != null )
    {
      sbSelect.append( "\n  where " ).append( strWhere );
    }

    return sbSelect.toString();

  }  // end genSelect()


  /**
   * Generate a multi table join select select
   *
   * @param dobjColumns
   * @param listTableRel
   * @param strWhere
   * @return
   */
  public static String genJoinedSelect( VwDataObject dobjColumns, List<VwTableRelationship> listTableRel,  String strWhere )
  {
    StringBuffer sbSelect = new StringBuffer( 2048 );
    sbSelect.append( "select ");

    StringBuffer sbBuff = new StringBuffer( 2048 );
    StringBuffer sbJoins = new StringBuffer( 2048 );
    StringBuffer sbColWhere = new StringBuffer( 2048 );
    StringBuffer sbHaving = new StringBuffer( 1024 );
    StringBuffer sbSort = new StringBuffer( 1024 );
    List<String> listTables = new ArrayList<String>();
    Map<String,String> mapInnerJoins = new HashMap<String,String>();
    Map<String,String> mapJoins = new HashMap<String,String>();

    Map mapAggregate = new HashMap();

    Map<String,String> mapTableAlias = new HashMap<String,String>();

    int nColCount = 0;

    String strBaseTableName = listTableRel.get( 0 ).getName();
    mapTableAlias.put( strBaseTableName, "a" );
    mapInnerJoins.put( strBaseTableName, null );

    
    // This is done first so that table aliases are allocated in the order needed by the table relationships
    buildTableJoins( strBaseTableName, listTableRel, sbJoins, mapTableAlias, mapInnerJoins, mapJoins, new TableNbr() );

    // Build the select column list for all tables in the join
    for ( Iterator iCols = dobjColumns.keys(); iCols.hasNext(); )
    {
      String strKey = (String)iCols.next();

      VwColDescriptor colDesc = (VwColDescriptor)dobjColumns.get( strKey );

      String strTableName = colDesc.getTableName();
      
      if ( listTables.indexOf( strTableName ) < 0 )
      {
        listTables.add( strTableName );
      }
      
      String strTableAlias =  mapTableAlias.get(  strTableName );

      String strColTableName = colDesc.getTableName();
      
      
      // See if we have a table alias defined
      String strColName = (String)mapTableAlias.get( strColTableName );
      
      if ( strColName == null )
      {
        strColName = colDesc.getColName();
      }
      else
      {
        strColName += "." + colDesc.getColName();
      }

      String strColAlias = colDesc.getColAlias();

      if ( colDesc.isIncluded() )
      {
        if ( ++nColCount > 1 )
        {
          sbBuff.append( ", " );
        }

        if ( colDesc.getAggregate() != null )
        {
          sbBuff.append( colDesc.getAggregate() );
          sbBuff.append( "(");
          sbBuff.append( strColName );

          sbBuff.append( ")");

          // If no afggreate alias was defined, create one using the alias and column name
          if ( strColAlias != null )
          {
            sbBuff.append( " " + strColAlias );
          }
          else
          {
            sbBuff.append( " " + colDesc.getAggregate() + "_" + colDesc.getColName() );
          }

          mapAggregate.put( strKey, null );

          if ( colDesc.getHaving() != null )
          {
            if ( sbHaving.length() == 0 )
            {
              sbHaving.append( "\n     having " );
            }
            else
            {
              sbHaving.append( "\n     and " );
            }

            sbHaving.append( colDesc.getAggregate() );
            sbHaving.append( "(");
            sbHaving.append( strColName );
            sbHaving.append( ") ");
            sbHaving.append( colDesc.getHaving() );

          } // end if ( colDesc.getHaving() ...

        } // end if colDesc.getAggregate() != nul )
        else
        {
          sbBuff.append( strColName );
          if ( strColAlias != null )
          {
            sbBuff.append( " " + strColAlias );
          }

        } // end else

      } // end if

      if ( colDesc.getSort() != null )
      {
        if ( sbSort.length() == 0 )
        {
          sbSort.append( "\n     order by " );
        }
        else
        {
          sbSort.append( ", " );
        }

        sbSort.append( strColName );
        sbSort.append( " " + colDesc.getSort() );
      }
      // Check for column condition
      if ( colDesc.getWhere() != null )
      {
        if ( sbColWhere.length() > 0 )
        {
          sbColWhere.append( "\n     and " );
        }

        String strValue = colDesc.getWhere();

        strValue = checkValue( colDesc );
        
        sbColWhere.append( strColName + " " + strValue );

      } // end if

      if ( sbBuff.length() > LINELEN )
      {
        if ( sbSelect.length() > 0 )
        {
          sbSelect.append( "\n       " );
        }

        sbSelect.append( sbBuff );
        sbBuff.setLength( 0 );

      }

    } // end for ( Iterator iCols...)

    if ( nColCount == 0 )
    {
      return "";
    }

    // Append any remaining columns
    if ( sbBuff.length() > 0 )
    {
      sbSelect.append( "\n       " );
      sbSelect.append( sbBuff );
    }

    // Build from clause from table list
    sbSelect.append( "\n       from " );
    VwTableRelationship tr = findTr( strBaseTableName, listTableRel );
    
    sbSelect.append( tr.getSchema() ).append( "." ).append( strBaseTableName );
 
    String strTableAlias = mapTableAlias.get( strBaseTableName );

    if ( strTableAlias != null )
    {
      sbSelect.append( ' ' );
      sbSelect.append( strTableAlias );

    } // end if
    else
    {
      strTableAlias = strBaseTableName;
    }
    


    // if sbBuff length > 0 then it contains the where clause for the table joins
    if ( sbJoins.length() > 0 )
    {
      sbSelect.append( sbJoins );
    }

    if ( sbColWhere.length() > 0 )
    {
      sbSelect.append( "\n     where ( " );
      sbSelect.append( sbColWhere );
      sbSelect.append( " )");

    } // end if ( sbColWhere...)


    if ( mapAggregate.size() > 0 )
    {
      StringBuffer sbGroupBy = new StringBuffer();
      int nCount = 0;

      // Build the select column list for all tables in the join
      for ( Iterator iCols = dobjColumns.keys(); iCols.hasNext(); )
      {
        String strKey = (String)iCols.next();

        if ( !mapAggregate.containsKey( strKey ) )
        {

          if ( ++nCount > 1 )
          {
            sbGroupBy.append( ", " );
          }
          else
          {
            sbGroupBy.append( "\n     group by ");
          }

          VwColDescriptor colDesc = (VwColDescriptor)dobjColumns.get( strKey );
          sbGroupBy.append( mapTableAlias.get( colDesc.getTableName() ) ).append( '.' ).append( colDesc.getColName() );

        }

      } // end for

      sbSelect.append( sbGroupBy );
    }

    if ( sbHaving.length() > 0 )
    {
      sbSelect.append( sbHaving );
    }

    if ( strWhere != null )
    {
      if ( sbBuff.length() > 0 || sbColWhere.length() > 0 )
      {
        sbSelect.append( "\n     and ( " + strWhere + " )" );
      }
      else
      {
        sbSelect.append( "\n     where ( " + strWhere + " )" );
      }

    }

    if ( sbSort.length() > 0 )
    {
      sbSelect.append( sbSort );
    }

    return sbSelect.toString();

  } // end genJoinedSelect()

  private static String checkValue( VwColDescriptor colDesc )
  {
    String strWhere = colDesc.getWhere();
    
    switch( colDesc.getSQLType() )
    {
      case Types.CHAR:
      case Types.CLOB:
      case Types.VARCHAR:
      case Types.LONGVARCHAR:
      case Types.DATE:
      case Types.TIME:
      case Types.TIMESTAMP:
        
           return dataTypeFixup( strWhere );
      default:
        
           return strWhere;
      
    } // end case
  }


  private static String dataTypeFixup( String strWhere )
  {
    StringBuffer sbWhere = new StringBuffer();
    StringBuffer sbToken = new StringBuffer();
    
    try
    {
      VwTextParser tp = new VwTextParser( new VwInputSource( strWhere ) );
      tp.setDelimiters( "<>= \n\t" );
      boolean fNextTokIsVal = false;
      
      while( tp.getToken( sbToken ) != VwTextParser.EOF )
      {
        String strToken = sbToken.toString();
        
        if ( VwExString.isWhiteSpace( strToken ))
        {
          sbWhere.append( strToken );
        }
        else
        if ( fNextTokIsVal )
        {
          fNextTokIsVal = false;
          sbWhere.append( fixValue( strToken ) );
        }
        else
        if ( strToken.equalsIgnoreCase( "between" ) || strToken.equalsIgnoreCase( "and" ) || strToken.equals( "=" ))
        {
          fNextTokIsVal = true;          
          sbWhere.append( strToken );
          
        }
        else
        if ( VwExString.findAny( strToken, "<>!", 0 ) >= 0  )
        {
          sbWhere.append( strToken );
          tp.getToken( sbToken );
          strToken = sbToken.toString();
          if ( VwExString.findAny( strToken, "<>!=", 0 ) >= 0  )
          {
            sbWhere.append( strToken );
            fNextTokIsVal = true;
            
          }
        }
      }
      
    }
    catch( Exception ex )
    {
      ex.printStackTrace();
    }
    
    return sbWhere.toString();
  }


  private static String fixValue( String strValue )
  {
    
    if ( strValue.startsWith( ":" )) // this is a dynamic input marker so leave alone
    {
      return strValue;
    }
    
    StringBuffer sb = new StringBuffer();
    
    if ( !strValue.startsWith( "'" ))
    {
      sb.append( "'" );
    }
    
    sb.append( strValue );
    
    if ( !strValue.endsWith( "'" ))
    {
      sb.append( "'" );
    }
    
    return sb.toString();
    
  } // end fixValue


  /**
   * Build table join clauses
   * @param strBaseTable
   * @param listTableRel
   * @param sbBuff
   * @param mapTableAlias
   * @param mapInnerJoins
   * @param mapJoins
   * @param tableNbr
   */
  private static void buildTableJoins( String strBaseTable,  List<VwTableRelationship> listTableRel,
                                       StringBuffer sbBuff, Map<String,String> mapTableAlias,
                                       Map<String,String> mapInnerJoins, Map<String,String> mapJoins, TableNbr tableNbr )
  {

    VwTableRelationship trBase = findTr( strBaseTable, listTableRel );
    
    for ( int x = 0; x < listTableRel.size(); x++ )
    {
      String strRelTable = listTableRel.get( x ).getName();

      VwTableRelationship trRel = findTr( strRelTable, listTableRel );

      // see if we already did this join
      if ( mapJoins.containsKey( strBaseTable + "_" + strRelTable ) || mapJoins.containsKey( strRelTable + "_" + strBaseTable ))
      {
        continue;
      }

      // Skip if this is the base table
      if ( strRelTable.equals( strBaseTable ))
      {
        continue;
      }

      if ( setJoin(  mapInnerJoins, mapJoins, listTableRel, trBase, trRel, sbBuff, mapTableAlias, tableNbr ) )
      {
        // Mark that we did this join
        mapJoins.put( strBaseTable + "_" + strRelTable, null );
        mapJoins.put( strRelTable + "_" + strBaseTable, null );
        
        // now go find and realted tables for this join
        buildTableJoins( strRelTable, listTableRel, sbBuff, mapTableAlias, mapInnerJoins, mapJoins, tableNbr );
        
      }


    } // end for ( terator iJoinTables...)
    

  } // end buildTargetRelationship()


 
  /**
   * Fins the VwTableRealtionship object for a table name
   * @param strTableName Ther name of the table to search for
   * @param listTableRel The list to search in
   * @return
   */
  private static VwTableRelationship findTr( String strTableName, List<VwTableRelationship> listTableRel )
  {
    for ( VwTableRelationship tr : listTableRel )
    {
      if ( tr.getName().equalsIgnoreCase( strTableName ))
      {
        return tr;
      }
    }
    
    return null;
  }


  /**
   * Build the inner join statements
   * @param mapInnerJoins
   * @param trParent
   * @param trRelated
   * @param sbBuff
   * @param mapTableAlias
   * @return
   */
  private static boolean setJoin( Map<String,String> mapInnerJoins, Map<String,String> mapJoins,
                                  List<VwTableRelationship>listTableRel, VwTableRelationship trParent,
                                  VwTableRelationship trRelated, StringBuffer sbBuff,
                                  Map<String,String> mapTableAlias, TableNbr tableNbr )
  {

    List<VwForeignKeyInfo> listParentForigenKeys = trParent.getForeignKeys();
    String strParentTable = trParent.getName();
    String strRelatedTable = trRelated.getName();
    
    
    boolean fFoundRelationship = false;
    int nKeyCount = 0;
    
    if ( mapInnerJoins.containsKey( strParentTable ) && mapInnerJoins.containsKey( strRelatedTable))
    {
      return false;
    }

    
    StringBuffer sbJoin = new StringBuffer();
    
    // First test to see if releationship is foreign key from parent to realted table
    for ( VwForeignKeyInfo fki : listParentForigenKeys )
    {

      if ( fki.getPkTableName().equals( strRelatedTable ) )
      {
        fFoundRelationship = true;
        sbJoin.append( "\n       " );
 
        String strParentAlias = getTabLeAlias( tableNbr, mapTableAlias, strParentTable );
        String strRelatedAlias = getTabLeAlias( tableNbr, mapTableAlias, strRelatedTable );

        if ( ++nKeyCount > 1 )
        {
          sbJoin.append( "           and " );
        }
        else
        {
          sbJoin.append( "inner join " );

          if ( mapInnerJoins.containsKey( strRelatedTable ))
          {
            sbJoin.append( trParent.getSchema() ).append( "." ).append( strParentTable ).append( ' ' ).append( strParentAlias ).append( " on " );
            mapInnerJoins.put( strParentTable, null );
          }
          else
          {
            sbJoin.append( trRelated.getSchema() ).append( "." ).append( strRelatedTable ).append( ' ' ).append( strRelatedAlias ).append( " on " );
            mapInnerJoins.put( strRelatedTable, null );
          }
        }
        
        
        sbJoin.append( strParentAlias + "." + fki.getFkColName() );
        sbJoin.append( " = " );
        sbJoin.append( strRelatedAlias + "." + fki.getPkColName() );

        
      } // end if
      
    } // end for()
    
    
    if ( fFoundRelationship )
    {
      sbBuff.append( sbJoin ); // didn't need a dependency resolved
      return true;
    }
    
    List listRealtedForigenKeys = trRelated.getForeignKeys();
    
    fFoundRelationship = false;
    
    // If we get here, then realted table has foreign key relationship to parent table
    for ( Iterator iRelatedFks = listRealtedForigenKeys.iterator(); iRelatedFks.hasNext(); )
    {
      VwForeignKeyInfo fki = (VwForeignKeyInfo)iRelatedFks.next();
      if ( fki.getPkTableName().equals( strParentTable ) )
      {
        fFoundRelationship = true;
        
        String strParentAlias = getTabLeAlias( tableNbr, mapTableAlias, strParentTable );
        String strRelatedAlias = getTabLeAlias( tableNbr, mapTableAlias, strRelatedTable );

        sbJoin.append( "\n       " );
        if ( ++nKeyCount > 1 )
        {
          sbJoin.append( "           and " );
        }
        else
        {
          sbJoin.append( "inner join " );
          if ( mapInnerJoins.containsKey( strParentTable ))
          {
            sbJoin.append( trRelated.getSchema() ).append( "." ).append( strRelatedTable ).append( ' ' ).append( strRelatedAlias ).append( " on " );
            mapInnerJoins.put( strRelatedTable, null );
          }
          else
          {
            sbJoin.append( trParent.getSchema() ).append( "." ).append( strParentTable ).append( ' ' ).append( strParentAlias ).append( " on " );
            mapInnerJoins.put( strParentTable, null );
          }
        }
        
        sbJoin.append( strParentAlias + "." + fki.getPkColName() );
        sbJoin.append( " = " );
        sbJoin.append(  strRelatedAlias + "." + fki.getFkColName() );

        
      } // end if
      
    } // end for()
    
    if ( fFoundRelationship )
    {
      sbBuff.append( sbJoin ); // didn't need a dependency resolved
      return true;
    }
    
    return fFoundRelationship;
    
  } // end setJoin()


  /**
   * Get next alias name from the alias character array
   * @param tableNbr Object to hold the next table array index
   * @param mapTableAlias map of table names and their aliases
   * @param strTableName The table name to get the alias for
   * @return
   */
  private static String getTabLeAlias( TableNbr tableNbr, Map<String,String> mapTableAlias, String strTableName )
  {
    String strTableAlias = null;
    
    strTableAlias = mapTableAlias.get( strTableName );
    if ( strTableAlias == null )
    {
      strTableAlias = s_astrTableAliases[ ++tableNbr.m_nTableNbr ].toLowerCase();
      mapTableAlias.put( strTableName, strTableAlias );
    }
    
    return strTableAlias;
    
  } // end getTabLeAlias()


  /**
   * Generates a select statement using the column names in the List of VwColInfo objects
   *
   * @param listColDescriptor A List of VwColDescriptor objects
   *
   * @return a String containg the SQL select statement
   */
  public static String genInsert( List listColDescriptor ) throws Exception
  {
    String strInsert = "insert into ";

    VwDelimString dlmsColList = new VwDelimString();
    VwDelimString dlmsValues = new VwDelimString();

    String strColName = null;
    String strTableName = null;

    for ( Iterator iCols = listColDescriptor.iterator(); iCols.hasNext(); )
    {
      VwColDescriptor colDesc = (VwColDescriptor)iCols.next();

      if ( strTableName == null )
      {
        strTableName = colDesc.getTableName();
      }

      strColName = colDesc.getColName();

      dlmsColList.add( strColName );

      if ( colDesc.getColValue() != null )
      {
        dlmsValues.add( colDesc.getColValue() );
      }
      else
      if ( colDesc.getColAlias() != null )
      {
        dlmsValues.add( ":" + colDesc.getColAlias() );
      }
      else
      {
        dlmsValues.add( "?" );
      }

    } // end for

    String strColList = null;
    String strRemain = dlmsColList.toString();

    // If the column list is larger than 50 characters then add line feeds at around
    // each 50 characater mark at the comma boundary

    while ( strRemain.length() > LINELEN )
    {
      int ndx = LINELEN;

      // Find the first break point at a comma boundry

      while( ndx < strRemain.length() )
      {
        if ( strRemain.charAt( ndx ) == ',' )
        {
          ++ndx;
          break;
        }

        ++ndx;

      } // end while()

      if ( strColList == null )
      {
        strColList = "( " + strRemain.substring( 0, ndx );
      }
      else
      {
        strColList += "\n" + VwFormat.left( " ", strTableName.length() + 15, ' ' )
          + strRemain.substring( 0, ndx );
      }

      strRemain = strRemain.substring( ndx );

    }

    if ( strColList == null )
    {
      strColList = "( " + strRemain;
    }
    else
    if ( strRemain.length() > 0 )
    {
      strColList += "\n" + VwFormat.left( " ", strTableName.length() + 15, ' ' )
        + strRemain;
    }

    strColList += " )";

    strInsert += strTableName + " " + strColList + "\n  values( " + dlmsValues.toString()
              + " )";


    return strInsert;

  }  // end genInsert()


  /**
   * Generates an update statement using the column names in the List of VwColInfo objects
   *
   * @param listColDescriptor A List of VwColDescriptor objects
   *
   * @return a String containing the SQL select statement
   */
  public static String genUpdate( List<VwColDescriptor> listColDescriptor, String strWhere ) throws Exception
  {
    StringBuffer sbUpdate =       new StringBuffer( "update " );
    StringBuffer sbSet =          new StringBuffer();
    StringBuffer sbColumnWhere =  new StringBuffer(); // Condition specified on the column
    

    String strColName = null;
    String strTableName = null;

    int nColCount = 0;

    for ( VwColDescriptor colDesc : listColDescriptor)
    {

      if ( strTableName == null )
      {
        strTableName = colDesc.getTableName();
      }

      strColName = colDesc.getColName();

      String strPlaceHolder = null;

      ++nColCount;

      if ( colDesc.getColValue() != null )
      {
        strPlaceHolder = colDesc.getColValue();
      }
      else
      if ( colDesc.getColAlias() != null )
      {
        strPlaceHolder = ":" +  colDesc.getColAlias();
      }
      else
      {
        strPlaceHolder = "?";
      }

      if ( sbSet.length() == 0 )
      {
        sbSet.append( "set  " ).append(  strColName ).append( " = " ).append( strPlaceHolder );
      }
      else
      {
        sbSet.append( ",\n       " ).append( strColName ).append( " = ").append( strPlaceHolder );
      }


      if ( colDesc.getWhere() != null )
      {
        if ( sbColumnWhere.length() == 0 )
        {
          if ( colDesc.getWhere().toLowerCase().trim().startsWith(  "where" ))
          {
            sbColumnWhere.append( " ( " );
          }
          else  
          {
            sbColumnWhere.append( "\n  where ( " );
          }
        }
        else
        {
          sbColumnWhere.append( "\n  and " );
        }

        sbColumnWhere.append( strColName ).append( colDesc.getWhere() );

      } // end if

    } // end for

    if ( nColCount == 0 )
    {
      return "";
    }

    sbUpdate.append( strTableName ).append( "\n  " ).append( sbSet );

    if ( sbColumnWhere.length() > 0  )
    {
      sbColumnWhere.append( " )" );

      sbUpdate.append( sbColumnWhere );
    }

    if ( strWhere != null )
    {
      int nPos = strWhere.toLowerCase().indexOf( "where" );
      
      if ( nPos >= 0 )
        nPos += 5;
      else
        ++nPos;
      
      if ( sbColumnWhere.length() > 0  )
      {
        sbUpdate.append( "\n  and " ).append( strWhere.substring( nPos ) );
      }
      else
      {
        sbUpdate.append( "\n  where " ).append( strWhere.substring( nPos )  );
      }

    }

    return sbUpdate.toString();

  }  // end genUpdate()


  /**
   * Generates a delete statement
   *
   * @param strTableName The name of the delete tbale
   * @param strWhere An Optional SQL "WHERE" clause ( if not null )
   *
   * @return a String containg the SQL delete statement
   */
  public static String genDelete( String strTableName, List listColDesc, String strWhere ) throws Exception
  {
    String strDelete = "delete from " + strTableName;
    String strColCond = null;

    if ( listColDesc != null )
    {
      for ( Iterator iColDesc = listColDesc.iterator(); iColDesc.hasNext(); )
      {
        VwColDescriptor colDesc = (VwColDescriptor)iColDesc.next();

        if ( colDesc.getWhere() != null )
        {
          String strColName = colDesc.getColAlias();
          if ( strColName == null )
          {
            strColName = colDesc.getColName();
          }

          if ( strColCond == null )
          {
            strColCond = "\n  where ( " + strColName + colDesc.getWhere();
          }
          else
          {
            strColCond += "\n  and " + strColName + colDesc.getWhere();
          }
        }

      } // end for


      if ( strColCond != null )
        strColCond += " )";

    }


    if ( strColCond != null )
    {
      if ( listColDesc != null )
        strDelete += strColCond;
    }

    if ( strWhere != null )
    {
      if ( strColCond != null )
        strDelete += "\n   and " + strWhere;
      else
        strDelete += "\n   where " + strWhere;

    }
    return strDelete;


  } // end genDelete()


  /**
   * Generates a stored procedure call statement using the column names in the List of VwColInfo objects
   *
   * @param listColDescriptor A List of VwColDescriptor objects
   *
   * @return a String containg the SQL select statement
   */
  public static String genProc( List listColDescriptor ) throws Exception
  {
    String strProc = "{ call ";

    VwDelimString dlmsValues = new VwDelimString();

    String strProcName = null;

    for ( Iterator iCols = listColDescriptor.iterator(); iCols.hasNext(); )
    {
      VwColDescriptor colDesc = (VwColDescriptor)iCols.next();

      if ( strProcName == null )
      {
        strProcName = colDesc.getTableName();
      }

      if ( colDesc.getColValue() != null )
      {
        dlmsValues.add( colDesc.getColValue() );
      }
      else
      if ( colDesc.getColAlias() != null )
      {
        dlmsValues.add( ":" + colDesc.getColAlias() );
      }
      else
      {
        dlmsValues.add( "?" );
      }

    } // end for


    strProc += strProcName + "( " + dlmsValues.toString() + " ) }";

    return strProc;

  }  // end genProc()



  /**
   * Generates DDL create table from a List of VwColInfo classes for a database vendor type
   */
  public static String genTableDDL( String strTableName, String strSchema, List<VwColInfo> listColInfo, boolean fUseUnderScores )
  {

    if ( listColInfo.size() == 0 )
      return "";

    StringBuffer sb = new StringBuffer( 2048 );

    sb.append( "create table " );

     if ( strSchema != null )
     {
       if ( fUseUnderScores )
         strSchema = formatName( strSchema );
       
      sb.append( strSchema + "." );
     }
    if ( fUseUnderScores )
      strTableName =  formatName( strTableName );
    sb.append( strTableName );

    sb.append( "\n(\n  " );

    int nCount = 0;
    for ( VwColInfo ci : listColInfo )
    {

      if ( ++nCount > 1 )
       sb.append( ",\n  " );

 
      sb.append( VwExString.rpad( ci.getColumnName(), ' ', 35 ) );
      sb.append( ci.getSQLTypeName() );

      switch( ci.getSQLType() )
      {
        case Types.CHAR:
        case Types.VARCHAR:

             sb.append( "( "  + ci.getColSize() + " )" );
             break;

      } // end switch()


      if ( ci.getNullable() == DatabaseMetaData.columnNoNulls )
        sb.append( "     NOT NULL" );

    } // end for()

    sb.append( "\n)" );

    return sb.toString();

  } // end genTableDDL()


 
  
  private static String formatName( String strName )
  {
    StringBuffer sbName = new StringBuffer();
    
    for ( int x = 0; x < strName.length(); x++ )
    {
      char ch = strName.charAt( x );
      
      if ( Character.isUpperCase( ch ))
      {
        if ( x > 0  )
          sbName.append( '_' );
        
        sbName.append( Character.toLowerCase( ch ) );
      }
      else
        sbName.append( Character.toLowerCase( ch ) );
      
    }
    
    return sbName.toString();
  }


  public static String genSelectSet( VwTableRelationship trTop, Map<String,String>mapTabl2Class, String strPkgName, boolean fUsePrimaryKey,
                                     String strTopTableJoin, int nNewLineIndent )
  { return genSelectSet( trTop, mapTabl2Class, strPkgName, fUsePrimaryKey, strTopTableJoin, nNewLineIndent, null ); }
  

  public static String genSelectSet( VwTableRelationship trTop, Map<String,String>mapTabl2Class, String strPkgName, boolean fUsePrimaryKey,
                                     String strTopTableJoin, int nNewLineIndent, String[] astrOmitColumns )
  {
    StringBuffer sbSelect = new StringBuffer();

    StringBuffer sbIndent = new StringBuffer();
    
    for ( int x = 0; x < nNewLineIndent; x++ )
    {
      sbIndent.append( ' ' );
    }
    
    String strIndent = sbIndent.toString();
    
    String strTableName = null;
    if ( trTop.getSchema() == null )
    {
      strTableName = trTop.getName();
    }
    else
    {
      strTableName = trTop.getSchema() + "." + trTop.getName();
    }
    
    if ( trTop.getSelectOverride() != null )
    {
      sbSelect.append( trTop.getSelectOverride() );
    }
    else
    {
      sbSelect.append( "select " );
      
      String strColPrefix = null;
      
      if ( strTopTableJoin != null )
      {
        strColPrefix = "a";
      }
      
      buildColList( trTop, sbSelect, strIndent,  strColPrefix, astrOmitColumns );
      
      sbSelect.append( "\n").append( strIndent).append( "from " ).append( strTableName );
      
      if ( strTopTableJoin != null )
      {
        sbSelect.append( " " ).append( strTopTableJoin );
      }
      
      if ( fUsePrimaryKey )
      {
        sbSelect.append( "\n" ).append( strIndent ).append( "where " );
        
        List<VwColInfo> listPrimKeys = trTop.getPrimeKeys();
        
        int nCount = 0;
        
        for ( VwColInfo ciKey :  listPrimKeys )
        {
          if ( nCount++ > 0 )
          {
            sbSelect.append( " and " );
          }
          
          sbSelect.append( ciKey.getColumnName() ).append( " = :" ).append( VwExString.makeJavaName( ciKey.getColumnName(), true) );
          
        }
      }

      sbSelect.append( " into " ).append( getClassName( mapTabl2Class, strPkgName, trTop.getName() ) );
      sbSelect.append( ";" );
      
    } // end if()
    
    Map<String,String> mapGeneratedSelects = new HashMap<String,String>();
   
    buildRelatedSelects( trTop.getName(), mapGeneratedSelects, mapTabl2Class, trTop, strPkgName, sbSelect, strIndent, astrOmitColumns );
    
    return sbSelect.toString();
    
    
  } // end genSelectSet()


  /**
   * Generate a fully qualified class name form a table or a class name already mapped in the table2Class map.
   * @param mapTabl2Class A map of table to class names - may be empty
   * @param strPkgName  The package of of the class
   * @param strTableName  The tablename
   * @return
   */
  private static String getClassName( Map<String,String> mapTabl2Class, String strPkgName, String strTableName  )
  {
    String strClassName =  mapTabl2Class.get( strTableName.toLowerCase() );

    if ( strClassName == null )
      strClassName = strPkgName + "." + VwExString.makeJavaName( strTableName, false );

    return strClassName;

  }


  /**
   * Builds a select statemet for a related table to the base table
   * @param tr
   * @param sbSelect
   */
  private static void buildRelatedSelects( String strBase, Map<String,String> mapGeneratedSelects,
                                           Map<String,String> mapTabl2Class,VwTableRelationship tr,
                                           String strPkgName, StringBuffer sbSelect, String strIndent, String[] astrOmitColumns )
  {
    
    if ( strBase == null )
    {
      strBase = tr.getName();
    }
    
    
    Map<String,VwTableRelationship> mapRelationships = tr.getRelationships();
    
    if ( mapGeneratedSelects.containsKey( tr.getName().toLowerCase() ))
    {
      return;
    }
    
    mapGeneratedSelects.put( tr.getName().toLowerCase(), null );
    
    for( VwTableRelationship trRelated : mapRelationships.values() )
    {
      
      if ( trRelated.getPrimeKeys() == null )
        continue;
      
     
      sbSelect.append( "\n\n" ).append( strIndent ).append( "select " );
      buildColList( trRelated, sbSelect, strIndent, null, astrOmitColumns );

      String strTableName = null;
      if ( trRelated.getSchema() == null )
      {
        strTableName = trRelated.getName();
      }
      else
      {
        {
          strTableName = trRelated.getSchema()  + "."  + trRelated.getName();
        }
      }
        
      sbSelect.append( "\n" ).append( strIndent ).append( "from " );
      sbSelect.append( strTableName ).append( "\n" ).append( strIndent ).append(  "where " );

      if ( trRelated.getMappingTableConstraint() != null )
      {
        doMappingConstraint( tr, trRelated, mapTabl2Class, strPkgName, sbSelect, strIndent );
      }
      else
      if ( trRelated.getRelationType() == VwTableRelationship.OBJECT )
      {
        doOneToOneRelationship( tr, trRelated, mapTabl2Class, strPkgName, sbSelect, strIndent );
      }
      else
      {
        doOneToManyRelationship( tr, trRelated, mapTabl2Class,  strPkgName, sbSelect, strIndent );
      }
      
      sbSelect.append( ";" );
        
    }

    // Keep following related tables
    for( Iterator iValues = mapRelationships.values().iterator(); iValues.hasNext(); )
    {
      VwTableRelationship trRelated = (VwTableRelationship)iValues.next();
      
      buildRelatedSelects( strBase, mapGeneratedSelects,  mapTabl2Class, trRelated, strPkgName, sbSelect, strIndent, astrOmitColumns );
    }
  }

  private static void doMappingConstraint( VwTableRelationship tr, VwTableRelationship trRelated,
                                                 Map<String,String> mapTabl2Class, String strPkgName, StringBuffer sbSelect, String strIndent  )
  {
    String strWhere = trRelated.getMappingTableConstraint().getWhere();
    String strTableName = VwExString.makeJavaName( trRelated.getName(), false );
    String strLinkTable = tr.getName();

    if ( strWhere.toLowerCase().startsWith( "where" ))
    {
      int nPos = strWhere.indexOf( " " );
      strWhere = strWhere.substring( ++nPos );
    }

    sbSelect.append( "\n  " ).append( strIndent ).append( strWhere );

    sbSelect.append( "\n" ).append( strIndent).append( "into " ).append( strPkgName ).append( '.' ).append( strTableName );
    sbSelect.append( "\n" ).append( strIndent ).append( "for " ).append( getClassName( mapTabl2Class, strPkgName, strLinkTable ) );

    sbSelect.append( '.' ).append( Character.toLowerCase( strTableName.charAt( 0 )));

    if ( strTableName.length() > 1 )
    {
      sbSelect.append( strTableName.substring( 1 ) );
    }

  }

  /**
   * @param tr
   * @param trRelated
   * @param strPkgName
   * @param sbSelect
   */
  private static void doOneToManyRelationship( VwTableRelationship tr, VwTableRelationship trRelated,
                                               Map<String,String> mapTabl2Class, String strPkgName, StringBuffer sbSelect, String strIndent )
  {
    List listForeignKeys = trRelated.getForeignKeys();
    String strLinkTable = tr.getName();
    String strTableName = VwExString.makeJavaName( trRelated.getName(), false );
    
    int nCount = 0;
    
    for ( Iterator ifKeys = listForeignKeys.iterator(); ifKeys.hasNext(); )
    {
      VwForeignKeyInfo fki = (VwForeignKeyInfo)ifKeys.next();

      if ( fki.getPkTableName().equalsIgnoreCase( strLinkTable ) )
      {
        String strFkName = fki.getFkColName();
        
        if ( nCount++ > 0 )
          sbSelect.append( " and " );
        
        sbSelect.append( strFkName ).append( " = :" ).append( VwExString.makeJavaName( fki.getPkColName(), true ) );
      }
      
    } // end for()
    
    sbSelect.append( "\n" ).append( strIndent).append(" into " ).append( strPkgName ).append( '.' ).append( strTableName );
    sbSelect.append( "\n" ).append( strIndent ).append( "for " ).append( getClassName( mapTabl2Class, strPkgName, strLinkTable ) );
    
    sbSelect.append( '.' ).append( Character.toLowerCase( strTableName.charAt( 0 )));
    
    if ( strTableName.length() > 1 )
    {
      sbSelect.append( strTableName.substring( 1 ) );
    }

    
  }


  /**
   * Builds a where clause for a one one releactionship between the base table and the related table
   *
   * @param tr
   * @param trRelated
   */
  private static void doOneToOneRelationship( VwTableRelationship tr,
                      VwTableRelationship trRelated, Map<String,String> mapTabl2Class, String strPkgName,
                      StringBuffer sbSelect, String strIndent  )
  {
    List listPrimeKeys = tr.getPrimeKeys();
    
    if ( listPrimeKeys == null )
    {
      return;
    }
    
    boolean fFound = false;
    int nCount = 0;

    String strTableName = VwExString.makeJavaName( trRelated.getName(), false );
    
    for ( Iterator ipKeys = listPrimeKeys.iterator(); ipKeys.hasNext(); )
    {
      VwColInfo ciPrime = (VwColInfo)ipKeys.next();
      String strPkName = ciPrime.getColumnName();
      List listForeignKeys = trRelated.getForeignKeys();
             
      for ( Iterator ifKeys = listForeignKeys.iterator(); ifKeys.hasNext(); )
      {
        VwForeignKeyInfo fki = (VwForeignKeyInfo)ifKeys.next();
        String strFkName = fki.getPkColName();
        
        if ( strFkName.equals( strPkName ))
        {
          if ( ++nCount > 1 )
          {
            sbSelect.append( " and " );
          }
          
          fFound = true;
          sbSelect.append( strPkName ).append( " = :" ).append( VwExString.makeJavaName( fki.getFkColName(), true ) );
          break;
        }
        
      } // end inner for()
      
    } // end for
  
    if ( fFound )
    {
      //sbSelect.append( " into " ).append( strPkgName ).append( '.' ).append( strTableName );
      sbSelect.append( "\n" ).append( strIndent).append(" into " ).append( getClassName( mapTabl2Class, strPkgName, trRelated.getName() ) );
      sbSelect.append( "\n" ).append( strIndent ).append( "for " ).append( getClassName( mapTabl2Class, strPkgName, tr.getName() ) );
      
      sbSelect.append( '.' ).append( Character.toLowerCase( strTableName.charAt( 0 )));
      
      if ( strTableName.length() > 1 )
      {
        sbSelect.append( strTableName.substring( 1 ) );
      }
      return;
    }
    
   listPrimeKeys = trRelated.getPrimeKeys();
   if ( listPrimeKeys == null )
   {
     return;
   }
   
   List listForeignKeys = tr.getForeignKeys();
   
   if ( listForeignKeys == null )
   {
     return;
   }
   
   nCount = 0;
   
   for ( Iterator ipKeys = listPrimeKeys.iterator(); ipKeys.hasNext(); )
   {
     VwColInfo ciPrime = (VwColInfo)ipKeys.next();
     String strPkName = ciPrime.getColumnName();
      
     for ( Iterator ifKeys = listForeignKeys.iterator(); ifKeys.hasNext(); )
     {
       VwForeignKeyInfo fki = (VwForeignKeyInfo)ifKeys.next();
       String strFkName = fki.getPkColName();
       
       if ( strFkName.equals( strPkName ))
       {
         if ( ++nCount > 1 )
         {
           sbSelect.append( " and " );
         }
         
         sbSelect.append( strPkName ).append( " = :" ).append( VwExString.makeJavaName( fki.getFkColName(), true ) );
         break;
       }
       
     } // end inner for()
     
   } // end for

    sbSelect.append( "\n" ).append( strIndent).append(" into " ).append( strPkgName ).append( '.' ).append( strTableName );
   sbSelect.append( "\n" ).append( strIndent ).append( "for " ).append( getClassName( mapTabl2Class, strPkgName, tr.getName() ) );
   
   sbSelect.append( '.' ).append( Character.toLowerCase( strTableName.charAt( 0 )));
   
   if ( strTableName.length() > 1 )
   {
     sbSelect.append( strTableName.substring( 1 ) );
   }
     
  } // end doOneToOneRelationship()


  private static void buildColList( VwTableRelationship tr, StringBuffer sb, String strIndent, String strColPrefix, String[] astrOmitColumns  )
  {

    
    List listColInfo = tr.getSubsetSqlColumns();
    
    if ( listColInfo == null )
      listColInfo = tr.getColumns();        // Get complete list if subset list not defined
    
    int nCount = 0;
    StringBuffer sbLine = new StringBuffer();
    
    for ( Iterator iCols = listColInfo.iterator(); iCols.hasNext(); )
    {
      VwColInfo ci = (VwColInfo)iCols.next();
      
      if ( astrOmitColumns != null && omitColumn( astrOmitColumns, ci.getColumnName()) )
        continue;
      if ( nCount++ > 0 )
        sbLine.append( ", " );
      
      if ( strColPrefix != null )
        sbLine.append( strColPrefix ).append( "." );
      
      sbLine.append( ci.getColumnName() ).append( " " ).append( ci.getColumnAliasName() );
      
      if ( sbLine.length() > 60 )
      {
        sb.append( "\n").append( strIndent).append( sbLine );
        sbLine.setLength( 0 );
        
      }
      
      
    } // end for()

    if ( sbLine.length() > 0 )
      sb.append( "\n").append( strIndent).append( sbLine );
    
  }


  /**
   * Test array for omit column hit
   * @param astrOmitColumns
   * @param strColumnName
   * @return
   */
  private static boolean omitColumn( String[] astrOmitColumns, String strColumnName )
  {
    for ( int x = 0; x < astrOmitColumns.length; x++ )
    {
      if ( astrOmitColumns[ x ].equalsIgnoreCase( strColumnName ))
        return true;
    }
    
    return false;
  }

} // end class VwSqlStmtGen{}

class TableNbr
{
  int m_nTableNbr;

  public TableNbr() { /* compiled code */ }
}

// *** End of VwSqlStmtGen.java ***

