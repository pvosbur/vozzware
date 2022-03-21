package com.vozzware.db;


import com.vozzware.db.util.VwMappingTableConstraint;
import com.vozzware.util.VwExString;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author P. VosBurgh
 *
 */
public class VwTableRelationship extends VwCatalogEntry
{
  public final static int OBJECT = 0;
  
  public final static int COLLECTION = 1;
  
  private int   	m_nRelationType;

  private VwMappingTableConstraint m_tableWhereConstraint;

  private Map<String,VwTableRelationship>    m_mapRelationships = new HashMap<String,VwTableRelationship>();    // A map of VwTableRelationship object this table has with other tables
  
  private List<String>                        m_listSubsetSqlColumns;               // If not null, the subset of the table columns to create the select list from
  
  private String                              m_strSelectOverride;                  // Select statement override if not null
  
  /**
   * Default constructor
   *
   */
  public VwTableRelationship()
  { ; }
  
  
  /**
   * Copy Constructor
   * @param trCopy The original to copy
   */
  public VwTableRelationship( VwTableRelationship trCopy )
  {
    
    m_mapRelationships = trCopy.m_mapRelationships;
    
    m_listSubsetSqlColumns = trCopy.m_listSubsetSqlColumns;               // If not null, the subset of the table columns to create the select list from
    
    m_strSelectOverride = trCopy.m_strSelectOverride;                  // Select statement override if not null
    
    m_listColumns = trCopy.m_listColumns;
    
    m_listForeignKeys = trCopy.m_listForeignKeys;
    
    m_listPrimeKeys = trCopy.m_listPrimeKeys;
    
    m_strAlias = trCopy.m_strAlias;
    
    m_strCatalog = trCopy.m_strCatalog;
    
    m_strName = trCopy.m_strName;
    
    m_strSchema = trCopy.m_strSchema;
    
  } // end VwTableRelationship()
  
  /**
   * Complete list of columns as defined for the table/view This list is used to gen the DVO's from
   * 
   * @param listColumns a List of VwColInfo objects, one for each column defined fir the table/view
   */
  public void setColumns( List<VwColInfo> listColumns )
  { setColumns( listColumns, true ); }

  /**
   * Complete list of columns as defined for the table/view This list is used to gen the DVO's from
   * 
   * @param listColumns a List of VwColInfo objects, one for each column defined fir the table/view
   */
  public void setColumns( List<VwColInfo> listColumns, boolean fCreateAlias )
  {
    for ( VwColInfo ci : listColumns )
    {
       
      if ( fCreateAlias )
        ci.setColumnAliasName( "\"" + VwExString.makeJavaName( ci.getColumnName(), true ) + "\"");
    }
    
    super.setColumns( listColumns );
    
  }

  /**
   * This list is used to create a subset select list if not null
   * @param listSubsetSqlColumns a subset column list from the total table columns
   */
  public void setSubsetSQLColumns( List listSubsetSqlColumns )
  { m_listSubsetSqlColumns = listSubsetSqlColumns; }
    
  
  /**
   * Retuen the subset column list
   * @return
   */
  public List getSubsetSqlColumns()
  { return m_listSubsetSqlColumns; }
  
  public void setSelectOverride( String strSelectOverride )
  { m_strSelectOverride = strSelectOverride; }
  
  public String getSelectOverride()
  { return m_strSelectOverride; }
  
  /**
   * @return Returns the mapRelationShips.
   */
  public Map<String,VwTableRelationship> getRelationships()
  { return m_mapRelationships; }
  
  /**
   * @param mapRelationships The mapRelationShips to set.
   */
  public void setRelationShips( Map<String,VwTableRelationship> mapRelationships )
  { m_mapRelationships = mapRelationships; }
  

  public VwTableRelationship getTableRelationship( String strTableName )
  {
    return m_mapRelationships.get( strTableName );
  }
  
  /**
   * @return Returns the nRelationType.
   */
  public int getRelationType()
  { return m_nRelationType; }
  
  
  /**
   * @param nRelationType The nRelationType to set.
   */
  public void setRelationType( int nRelationType )
  { m_nRelationType = nRelationType; }
  
  public String toString()
  { return getName(); }


  public void setMappingTableConstraint( VwMappingTableConstraint tableConstraint )
  {
    m_tableWhereConstraint = tableConstraint;
  }

  public VwMappingTableConstraint getMappingTableConstraint()
  {
    return m_tableWhereConstraint;
  }

} // end class VwTableRelationship{}

// *** End of VwTableRelationship.java

                                            ;