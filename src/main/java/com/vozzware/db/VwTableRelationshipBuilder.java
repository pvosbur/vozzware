package com.vozzware.db;


import com.vozzware.util.VwDelimString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class reverse engineers table entries in a database schema using referential integrity
 * to discover a complete set of related tables given a starting table known as the top level base table.
 */
public class VwTableRelationshipBuilder
{
  private   VwDatabase     m_db;
  
  private   Map<String,VwTableRelationship>  m_mapProcessedTables = new HashMap<String,VwTableRelationship>();
  private   Map<String,VwTableRelationship>  m_mapTr = new HashMap<String,VwTableRelationship>();
  private   Map<String,String>                m_mapIncludeTables;
  private   Map<String,String>                m_mapExcludeTables;
  private   List<String>                      m_listParentTables;
  
  private   String                            m_strSchema;
  private   String                            m_strCatalog;
  
  private   String          m_strTopLevelTableName;
  
  private   boolean         m_fBaseTableRelOnly = false;
  private   int             m_nRelLevel = 2;
  
  private boolean           m_fOneWayRelationships = true;
  

  class VwTableSpec
  {
    String                  m_strTableName;
    String                  m_strCatalog;
    String                  m_strSchema;
    VwTableRelationship    m_tr;
    
    VwTableSpec( String strTableName, String strCatalog, String strSchema, VwTableRelationship tr )
    {
      m_strTableName = strTableName;
      m_strCatalog = strCatalog;
      m_strSchema = strSchema;
      m_tr = tr;
      
    }
  }
  /**
   * Constructor
   * @param db The database connection instance
   * 
   * @param strCatalog The catalog - may be null
   * @param strSchema The scheama 
   * @param strBaseTableName The base or top level table name to start the object graph
   */
  public VwTableRelationshipBuilder( VwDatabase db, String strCatalog, String strSchema, String strBaseTableName )
  {
    m_db = db;
    m_strCatalog = strCatalog;
    m_strSchema = strSchema;
    m_strTopLevelTableName = strBaseTableName;
    
  } // end VwTableRelationshipBuilder()

  
  /**
   * Sets the list of tables to include
   * @param strIncludeTableList a comma separated list if tables to include
   */
  public void setIncludeTables( String strIncludeTableList )
  { 
    if ( strIncludeTableList == null )
    {
      m_mapIncludeTables = null;
      return;
      
    }
    
    VwDelimString dlms = new VwDelimString( strIncludeTableList );
    m_mapIncludeTables = dlms.toMap( true );
    
  }

  /**
   * Sets a list of subordinate tables to the base parent tables that are themselves parent table
   * @param strParentTableList
   */
  public void setParentTables( String strParentTableList )
  { 
    if ( strParentTableList == null )
    {
      m_listParentTables = null;
      return;
      
    }
    
    VwDelimString dlms = new VwDelimString( strParentTableList );
    m_listParentTables = dlms.toStringList();
    
    
  }
  
  /**
   * Sets the relationship level to drill down on.
   * @param nRelLevel The relationship dependency level level.
   * <br>A value of 1 does foreign key relationships only.
   * <br>A value of 2 includes any table that has a foreign key to the current table being intrspected
   * <br>The default value is 2
   */
  public void setRelationshipLevel( int nRelLevel )
  { m_nRelLevel = nRelLevel; }
  
  
  public void setDefaults()
  {
    m_nRelLevel = 2;
    m_mapExcludeTables = null;
    m_mapIncludeTables = null;
    m_listParentTables = null;
    m_fBaseTableRelOnly = false;
    
  } // end setDefaults()
  /**
   * If this flag is set, only tables that are directly related to the top level table are included. The included tables
   * <br>are based on the relationship level set
   * 
   * @param fBaseTableRelOnly if true show only direct related tables to the base  table
   */
  public void setBaseTableRelationshipsOnly( boolean fBaseTableRelOnly )
  { m_fBaseTableRelOnly = fBaseTableRelOnly; }
  
  /**
   * Sets a list of tables to be excluded from the final result
   * @param strExcludeTableList a comma separated list of tables to exclude
   */
  public void setExcludeTables( String strExcludeTableList )
  { 
    if ( strExcludeTableList == null )
    {
      m_mapExcludeTables = null;
      return;
    }
    
    VwDelimString dlms = new VwDelimString( strExcludeTableList );
    m_mapExcludeTables = dlms.toMap( true );
    
  }
  
  
  /**
   * Build the table relationship result set
   * @return
   * @throws Exception
   */
  public VwTableRelationship getRelatedTables() throws Exception
  {
    m_mapProcessedTables.clear();
    m_mapTr.clear();
    
    VwTableRelationship tr = new VwTableRelationship();
    
    tr.setColumns( m_db.getColumns( m_strCatalog, m_strSchema, m_strTopLevelTableName) );
    
    if ( tr.getColumns().size() == 0 )
      return null;
    
    tr.setName( m_strTopLevelTableName );
    tr.setCatalog( m_strCatalog );
    tr.setSchema( m_strSchema );
    
    buildRelatedTableMap( null, tr,  m_strCatalog, m_strSchema, m_strTopLevelTableName );   
    
    if ( m_mapIncludeTables != null )
      doTableIncludes( tr.getRelationships(), m_mapIncludeTables );
    else
    if ( m_mapExcludeTables != null )
       doTableExcludes( tr.getRelationships(), m_mapExcludeTables );
    
    return tr;
    
  } // end getRelatedTables() 
  
  /**
   * Builds the map of related tables. This is a recursive method
   * 
   * @param strIgnoreTable
   * @param tr
   * @param strCatalog
   * @param strSchema
   * @param strTableName
   * 
   * @throws Exception
   */
  private void  buildRelatedTableMap( String strIgnoreTable, VwTableRelationship tr, String strCatalog, String strSchema, String strTableName ) throws Exception
  {
    if ( m_mapProcessedTables.containsKey( strTableName.toLowerCase()) )
      return;

    m_mapProcessedTables.put(strTableName.toLowerCase(), tr );
    
    List <VwForeignKeyInfo>listForeignKeys = null;
    
    
    listForeignKeys = m_db.getForeignKeys( strCatalog, strSchema, strTableName );
    
      
    tr.setForeignKeys( listForeignKeys );
    
    tr.setPrimeKeys( m_db.getPrimaryKeys( strCatalog, strSchema, strTableName ) );
    
    // Exit if this is not the toplevel table and only toplevel direct relationships are wanted
    if ( !strTableName.equalsIgnoreCase( m_strTopLevelTableName ) && m_fBaseTableRelOnly )
      return;
 
    List<VwTableSpec> listFkTablesToProcess = new ArrayList<VwTableSpec>();
    List<VwTableSpec> listLinkedTablesToProcess = new ArrayList<VwTableSpec>();
    
    
     // Get tables for the the foreign key refs we have in this table
    for ( VwForeignKeyInfo fki : listForeignKeys )
    {
      String strFkTable = fki.getPkTableName();
      String strFkSchema = fki.getPkSchemaName();
      
      if ( strSchema == null ) // if base schema is null don't include schema name
        strFkSchema = null;
      else
      if ( strFkSchema == null )
        strFkSchema = strSchema;
      
      if ( strIgnoreTable != null && strIgnoreTable.equalsIgnoreCase( strFkTable ) )
        continue;
      
      if ( tr.getName().equalsIgnoreCase( strFkTable ))
          continue;

      VwTableRelationship trRelated = null;

      if ( m_fOneWayRelationships && isAlreadyRelated( strFkTable, strTableName  ) )
        continue;
      
      if ( m_mapProcessedTables.containsKey( strFkTable.toLowerCase()) )
      {
        trRelated = (VwTableRelationship)m_mapProcessedTables.get( strFkTable.toLowerCase() );
        
        if ( trRelated.getRelationType() != VwTableRelationship.OBJECT )
        {
          trRelated = new VwTableRelationship( trRelated );
          trRelated.setRelationType( VwTableRelationship.OBJECT );
        }
        tr.getRelationships().put( strFkTable , trRelated );
        continue;
      }

      trRelated = (VwTableRelationship)m_mapTr.get( strFkTable.toLowerCase() );
      
      if ( trRelated == null )
      {
        trRelated = new VwTableRelationship(); 
        trRelated.setRelationType( VwTableRelationship.OBJECT );
        m_mapTr.put( strFkTable.toLowerCase(), trRelated );
      }
      
      if ( trRelated.getRelationType() != VwTableRelationship.OBJECT )
      {
        trRelated = new VwTableRelationship( trRelated );
        trRelated.setRelationType( VwTableRelationship.OBJECT );
      }
      
      trRelated.setName( strFkTable );
      trRelated.setCatalog( strCatalog );
      trRelated.setSchema( strSchema );
      trRelated.setColumns( m_db.getColumns( strCatalog, strFkSchema, strFkTable ) );
      trRelated.setForeignKeys( m_db.getForeignKeys( strCatalog, strFkSchema, strFkTable ));
      tr.getRelationships().put( strFkTable , trRelated );
      
      listFkTablesToProcess.add( new VwTableSpec( strFkTable, strCatalog, strFkSchema, trRelated ) );
      
     
    } // end for

    try
    {
      // Exit if this is not the toplevel table and only toplevel direct relationships are wanted
      if ( !strTableName.equalsIgnoreCase( m_strTopLevelTableName ) && m_fBaseTableRelOnly )
        return;
      
      if ( !strTableName.equalsIgnoreCase( m_strTopLevelTableName ) && !isParentTable( strTableName )  )
        return;
      
      if ( m_nRelLevel == 1 && !m_fBaseTableRelOnly )
        return;
      
      // This gets all tables that have a foreign keys to this table
      String[] astrTables = m_db.getLinkedTables( strCatalog, strSchema, strTableName );
  
      
      for ( int x = 0; x < astrTables.length; x++ )
      {
        String strLinkedTable = astrTables[ x ];
        
        int nPos = strLinkedTable.indexOf( '.');
        String strLinkSchema = strLinkedTable.substring( 0, nPos );
        strLinkedTable = strLinkedTable.substring( ++nPos );

        // if this table is a parent table, and the linked tabel is also a parent table and it is
        // in front of me in the parentTables list then skip this entry.
        if ( isParentTable( strTableName ) && isParentTable( strLinkedTable ))
        {
          if ( skipLinkedTableEntry( strTableName, strLinkedTable ))
            continue;
          
        }
        if ( strSchema == null )
          strLinkSchema = null;
        
        if ( strLinkSchema != null && strLinkSchema.length() == 0 )
          strLinkSchema = strSchema;
        
        if ( strLinkedTable.equalsIgnoreCase( m_strTopLevelTableName ) )
          continue;
        
        if ( strLinkedTable.equalsIgnoreCase( strTableName ) )
          continue;
  
        if ( strIgnoreTable != null && strIgnoreTable.equalsIgnoreCase( strLinkedTable ) )
          continue;
        
        // get linked table's foreign keys
        listForeignKeys = m_db.getForeignKeys( strCatalog, strLinkSchema, strLinkedTable );
        
        VwForeignKeyInfo fki = findDependencyKey( listForeignKeys, strTableName );
        if ( fki != null )
        {
          VwTableRelationship trRelated = null;
          
          if ( tr.getName().equalsIgnoreCase( strLinkedTable ))
            continue;
          
          if ( m_mapProcessedTables.containsKey( strLinkedTable.toLowerCase()) || m_mapTr.containsKey( strLinkedTable.toLowerCase() ))
          {
            trRelated = (VwTableRelationship)m_mapProcessedTables.get( strLinkedTable.toLowerCase() );
            if ( trRelated == null )
              trRelated = (VwTableRelationship)m_mapTr.get(  strLinkedTable.toLowerCase()  );
            
            trRelated = new VwTableRelationship( trRelated );
            trRelated = makeTableRelationship( tr, strCatalog, strLinkSchema, strLinkedTable, fki );
            tr.getRelationships().put( strLinkedTable, trRelated );
            continue;
          }
          
          if ( strLinkSchema == null || strLinkSchema.length() == 0 )
          {
            if ( strSchema != null )
              strLinkSchema = strSchema;
            else
              strLinkSchema = null;
            
          }
          trRelated = makeTableRelationship( tr, strCatalog, strLinkSchema, strLinkedTable, fki );
          tr.getRelationships().put( strLinkedTable, trRelated );
  
   
          if ( m_nRelLevel < 2 )
            continue;
          
          if ( m_fOneWayRelationships && isAlreadyRelated( strLinkedTable, strTableName ) )
            continue;
          
           
          listLinkedTablesToProcess.add( new VwTableSpec( strLinkedTable, strCatalog, strLinkSchema, trRelated ) );
           
         }
        
      }  // end for()
 
    } // end try
    finally
    {
       
     // go and build foreign key tables
      for ( VwTableSpec ts : listFkTablesToProcess )
      {
         buildRelatedTableMap( strTableName, ts.m_tr, ts.m_strCatalog, ts.m_strSchema, ts.m_strTableName );
      }
      
      
      // go and build linked tables
      for ( VwTableSpec ts : listLinkedTablesToProcess )
      {
        buildRelatedTableMap( strTableName, ts.m_tr, ts.m_strCatalog, ts.m_strSchema, ts.m_strTableName );
      }
    
    }
  } // end buildRelatedTableMap()
  
  
  
  private boolean skipLinkedTableEntry( String strTableName, String strLinkedTable )
  {
    if ( m_listParentTables == null )
      return false;
    
    int nPosTable = m_listParentTables.indexOf( strTableName.toLowerCase() );
    int nPosLinkedTable = m_listParentTables.indexOf( strLinkedTable.toLowerCase() );
    return nPosLinkedTable < nPosTable;
  }


  /**
   * See if the linked table has already included the current table we are processing
   * @param strLinkedTable The linked table
   * @param strTable the table currently being processed
   * @return
   */
  private boolean isAlreadyRelated( String strLinkedTable, String strTable )
  {
    VwTableRelationship tr = (VwTableRelationship)m_mapProcessedTables.get( strLinkedTable.toLowerCase() );
    
    if ( tr == null )
      return false;
    
    Map<String,VwTableRelationship> mapRelatedTables = tr.getRelationships();
    
    if ( mapRelatedTables == null )
      return false;
    
    for ( String strName : mapRelatedTables.keySet() )
    {
       
      if ( strName.equalsIgnoreCase( strTable ))
        return true;        // table already inluded by 
      
    }
    
    return false;
  }


  /**
   * Test to see if this table is designated as parent table
   * @param strTable
   * @return
   */
  private boolean isParentTable( String strTable )
  {
    if ( m_listParentTables == null )
      return true;
    
    if ( m_listParentTables != null && m_listParentTables.contains( strTable.toLowerCase() ) )
        return true;
    
    return false;
    
  } // end isParentTable()

  /**
   * @param strCatalog
   * @param strSchema
   * @param strTable
   * @param fki
   * @return
   */
  private VwTableRelationship makeTableRelationship( VwTableRelationship trRelated, String strCatalog,
                                                      String strSchema, String strTable, VwForeignKeyInfo fki ) throws Exception
  {
    
    VwTableRelationship tr = (VwTableRelationship)m_mapTr.get( strTable.toLowerCase() );
    
    if ( tr == null )
    {
      tr = new VwTableRelationship(); 
      m_mapTr.put( strTable.toLowerCase(), tr );
    }
    
    tr.setColumns( m_db.getColumns( strCatalog, strSchema, strTable) );
    tr.setForeignKeys( m_db.getForeignKeys( strCatalog, strSchema, strTable));
    tr.setName( strTable );
    tr.setCatalog( strCatalog );
    tr.setSchema( strSchema );
    
    List<VwColInfo> listPrimaryKeys = m_db.getPrimaryKeys( strCatalog, strSchema, strTable );
    
    boolean fInPrimeKeyList = false;
    
    for ( VwColInfo pki : listPrimaryKeys )
    {
      if ( pki.getColumnName().equalsIgnoreCase( fki.getPkColName() ))
      {
        fInPrimeKeyList = true;
        break;
      }
      
    }
    
    if ( fInPrimeKeyList )
    {
      if ( listPrimaryKeys.size() > 1 )     // this represents a one to may relationship becuse this is part of a composite primary key
        tr.setRelationType( VwTableRelationship.COLLECTION );
      else
        tr.setRelationType( VwTableRelationship.OBJECT ); // this is a one to one
        
    }
    else
      tr.setRelationType( VwTableRelationship.COLLECTION );
      
    return tr;
    
  } // end makeTableRelationship()

  /**
   * find the foreign key object in the linked table that matches the parent tables name
   * @param listForeignKeys The list of foreign keys in the linked table name
   * @param strParentTableName The name of the parent table to find the foreign key
   * @return the VwForeignKeyInfo object
   */
  private VwForeignKeyInfo findDependencyKey( List<VwForeignKeyInfo> listForeignKeys, String strParentTableName  )
  {
    
    for ( VwForeignKeyInfo fki : listForeignKeys )
    {
      
      if ( fki.getPkTableName().equalsIgnoreCase( strParentTableName ))
         return fki;
    }
    
    return null;
    
  } // end buildDependencies()
  
  /**
   * 
   * @param mapTables
   * @param mapExludeTables
   */
  private void doTableExcludes( Map<String,VwTableRelationship> mapTables, Map<String,String> mapExludeTables )
  {
    Map<String,VwTableRelationship> mapCloneTables = new HashMap<String,VwTableRelationship>( mapTables );
    
    for ( String strTableName : mapCloneTables.keySet() )
    {
      VwTableRelationship trRelated = (VwTableRelationship)mapTables.get( strTableName );
      
      if ( mapExludeTables.containsKey( strTableName.toLowerCase() ))
        mapTables.remove( strTableName );
      
      if ( trRelated != null && trRelated.getRelationships() != null )
        doTableExcludes( trRelated.getRelationships(), mapExludeTables );
      
    }
    
  } // end doTableExcludes()


  /**
   * 
   * @param mapTables
   * @param mapIncludeTables
   */
  private void doTableIncludes( Map<String,VwTableRelationship>mapTables, Map<String,String> mapIncludeTables )
  {
    Map<String,VwTableRelationship> mapCloneTables = new HashMap<String,VwTableRelationship>( mapTables );
    
    for ( String strTableName : mapCloneTables.keySet() )
    {
      VwTableRelationship trRelated = (VwTableRelationship)mapTables.get( strTableName );
      
      if ( !(mapIncludeTables.containsKey( strTableName.toLowerCase() )))
        mapTables.remove( strTableName );
      
      if ( trRelated != null && trRelated.getRelationships() != null )
        doTableIncludes( trRelated.getRelationships(), mapIncludeTables );
      
    }
    
  } // end doTableIncludes()

  
} // end class VwTableRelationshipBuilder{}

// *** End of VwTableRelationshipBuilder()
