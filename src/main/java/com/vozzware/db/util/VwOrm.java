/*
============================================================================================

                       V o z z W o r k s   C o d e   G e n e r a t o r                      

                               2009 by V o z z W a r e   L L C                              

    Source File Name: VwOrm.java

    Author:           

    Date Generated:   07-13-2019

    Time Generated:   10:51:56

============================================================================================
*/

package com.vozzware.db.util;

import com.vozzware.util.VwBeanUtils;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;


public class VwOrm extends VwDbObjCommon implements Serializable, Cloneable
{

  private String                 m_strBaseTable;                 
  private String                 m_strBaseRelationshipsOnly;     
  private String                 m_strBaseTableJoin;             
  private String                 m_strRelationshipLevel;         
  private String                 m_strIncludeTables;             
  private String                 m_strExcludeTables;             
  private String                 m_strParentTables;              
  private String                 m_strOmitColumns;               
  private List<VwMappingTableConstraint>  m_listMappingTableConstraint;   
  private List<VwFinder>         m_listFinder;                   
  private List<VwTableSpec>      m_listTable;                    

  /**
   * Renders bean instance property values to a String
   * 
   * @return  A String containing the bean property values
   */
  public String toString()
  {
    return VwBeanUtils.dumpBeanValues( this );
  } // End of toString()



  // *** The following members set or get data from the class members *** 

  /**
   * Sets the baseTable property
   */
  public void setBaseTable( String strBaseTable )
  { 
    
    testDirty( "baseTable", strBaseTable );
    m_strBaseTable = strBaseTable;
  }

  /**
   * Gets baseTable property
   * 
   * @return  The baseTable property
   */
  public String getBaseTable()
  { return m_strBaseTable; }

  /**
   * Sets the baseRelationshipsOnly property
   */
  public void setBaseRelationshipsOnly( String strBaseRelationshipsOnly )
  { 
    
    testDirty( "baseRelationshipsOnly", strBaseRelationshipsOnly );
    m_strBaseRelationshipsOnly = strBaseRelationshipsOnly;
  }

  /**
   * Gets baseRelationshipsOnly property
   * 
   * @return  The baseRelationshipsOnly property
   */
  public String getBaseRelationshipsOnly()
  { return m_strBaseRelationshipsOnly; }

  /**
   * Sets the baseTableJoin property
   */
  public void setBaseTableJoin( String strBaseTableJoin )
  { 
    
    testDirty( "baseTableJoin", strBaseTableJoin );
    m_strBaseTableJoin = strBaseTableJoin;
  }

  /**
   * Gets baseTableJoin property
   * 
   * @return  The baseTableJoin property
   */
  public String getBaseTableJoin()
  { return m_strBaseTableJoin; }

  /**
   * Sets the relationshipLevel property
   */
  public void setRelationshipLevel( String strRelationshipLevel )
  { 
    
    testDirty( "relationshipLevel", strRelationshipLevel );
    m_strRelationshipLevel = strRelationshipLevel;
  }

  /**
   * Gets relationshipLevel property
   * 
   * @return  The relationshipLevel property
   */
  public String getRelationshipLevel()
  { return m_strRelationshipLevel; }

  /**
   * Sets the includeTables property
   */
  public void setIncludeTables( String strIncludeTables )
  { 
    
    testDirty( "includeTables", strIncludeTables );
    m_strIncludeTables = strIncludeTables;
  }

  /**
   * Gets includeTables property
   * 
   * @return  The includeTables property
   */
  public String getIncludeTables()
  { return m_strIncludeTables; }

  /**
   * Sets the excludeTables property
   */
  public void setExcludeTables( String strExcludeTables )
  { 
    
    testDirty( "excludeTables", strExcludeTables );
    m_strExcludeTables = strExcludeTables;
  }

  /**
   * Gets excludeTables property
   * 
   * @return  The excludeTables property
   */
  public String getExcludeTables()
  { return m_strExcludeTables; }

  /**
   * Sets the parentTables property
   */
  public void setParentTables( String strParentTables )
  { 
    
    testDirty( "parentTables", strParentTables );
    m_strParentTables = strParentTables;
  }

  /**
   * Gets parentTables property
   * 
   * @return  The parentTables property
   */
  public String getParentTables()
  { return m_strParentTables; }

  /**
   * Sets the omitColumns property
   */
  public void setOmitColumns( String strOmitColumns )
  { 
    
    testDirty( "omitColumns", strOmitColumns );
    m_strOmitColumns = strOmitColumns;
  }

  /**
   * Gets omitColumns property
   * 
   * @return  The omitColumns property
   */
  public String getOmitColumns()
  { return m_strOmitColumns; }

  /**
   * Sets the mappingTableConstraint property
   */
  public void setMappingTableConstraint( List<VwMappingTableConstraint> listMappingTableConstraint )
  { 
    
    testDirty( "mappingTableConstraint", listMappingTableConstraint );
    m_listMappingTableConstraint = listMappingTableConstraint;
  }

  /**
   * Gets mappingTableConstraint property
   * 
   * @return  The mappingTableConstraint property
   */
  public List<VwMappingTableConstraint> getMappingTableConstraint()
  { return m_listMappingTableConstraint; }

  /**
   * Sets the finder property
   */
  public void setFinder( List<VwFinder> listFinder )
  { 
    
    testDirty( "finder", listFinder );
    m_listFinder = listFinder;
  }

  /**
   * Gets finder property
   * 
   * @return  The finder property
   */
  public List<VwFinder> getFinder()
  { return m_listFinder; }

  /**
   * Sets the table property
   */
  public void setTable( List<VwTableSpec> listTable )
  { 
    
    testDirty( "table", listTable );
    m_listTable = listTable;
  }

  /**
   * Gets table property
   * 
   * @return  The table property
   */
  public List<VwTableSpec> getTable()
  { return m_listTable; }

  /**
   * Clones this object
   *
   */
  public Object clone()
  {
    VwOrm classClone = new VwOrm();
    
    classClone.m_strBaseTable = m_strBaseTable;
    classClone.m_strBaseRelationshipsOnly = m_strBaseRelationshipsOnly;
    classClone.m_strBaseTableJoin = m_strBaseTableJoin;
    classClone.m_strRelationshipLevel = m_strRelationshipLevel;
    classClone.m_strIncludeTables = m_strIncludeTables;
    classClone.m_strExcludeTables = m_strExcludeTables;
    classClone.m_strParentTables = m_strParentTables;
    classClone.m_strOmitColumns = m_strOmitColumns;

    if ( m_listMappingTableConstraint  != null )
      classClone.m_listMappingTableConstraint = (List<VwMappingTableConstraint>)cloneList( m_listMappingTableConstraint );

    if ( m_listFinder  != null )
      classClone.m_listFinder = (List<VwFinder>)cloneList( m_listFinder );

    if ( m_listTable  != null )
      classClone.m_listTable = (List<VwTableSpec>)cloneList( m_listTable );

    return classClone;
  }



  /**
   *Clones a list and all its elements
   *
   * @param list The list to clone
   *
   * @return The cloned List object
   *
   */
  private List cloneList( List list )
  {

    try
    {
      List listClone = (List)list.getClass().newInstance();

      for ( Object objListContent : list )
      {
        if ( objListContent instanceof Cloneable )
        {
          Method mthdClone = objListContent.getClass().getMethod( "clone", (Class[])null );
          Object objClone = mthdClone.invoke( objListContent, (Object[])null );
          listClone.add( objClone );
        } // end if
      } // end for()

      return listClone;
    }
    catch( Exception ex )
    {
      throw new RuntimeException( ex.toString() );
    }
  }

  /**
   * Performs deep equal test on this object
   *
   * @param objTest The object to compare this object to
   *
   * @return if the two objects are equal, false otherwise
   *
   */
  public boolean equals( Object objTest )
  {

    if ( objTest == null )
      return false;

    if ( this.getClass() != objTest.getClass() )
      return false;

    VwOrm objToTest = (VwOrm)objTest;

    if ( ! doObjectEqualsTest( m_strBaseTable, objToTest.m_strBaseTable ) )
      return false; 

    if ( ! doObjectEqualsTest( m_strBaseRelationshipsOnly, objToTest.m_strBaseRelationshipsOnly ) )
      return false; 

    if ( ! doObjectEqualsTest( m_strBaseTableJoin, objToTest.m_strBaseTableJoin ) )
      return false; 

    if ( ! doObjectEqualsTest( m_strRelationshipLevel, objToTest.m_strRelationshipLevel ) )
      return false; 

    if ( ! doObjectEqualsTest( m_strIncludeTables, objToTest.m_strIncludeTables ) )
      return false; 

    if ( ! doObjectEqualsTest( m_strExcludeTables, objToTest.m_strExcludeTables ) )
      return false; 

    if ( ! doObjectEqualsTest( m_strParentTables, objToTest.m_strParentTables ) )
      return false; 

    if ( ! doObjectEqualsTest( m_strOmitColumns, objToTest.m_strOmitColumns ) )
      return false; 

    if ( ! doListElementTest( m_listMappingTableConstraint, objToTest.m_listMappingTableConstraint ) )
      return false;

    if ( ! doListElementTest( m_listFinder, objToTest.m_listFinder ) )
      return false;

    if ( ! doListElementTest( m_listTable, objToTest.m_listTable ) )
      return false;

    return true;
  }



  /**
   * Perform an equals test on an Object
   *
   * @param obj1 first object
   * @param obj2 second object
   *
   * @return true if objects are equal, false otherwise
   *
   */
  private boolean doObjectEqualsTest( Object obj1, Object obj2 )
  {
    if ( obj1 != null )
    {
      if ( obj2 == null )
        return false;
      return obj1.equals( obj2 );
    }
    else
    if ( obj2 != null )
      return false;

    return true;

  }

  /**
   * Do equals test on each object in the list
   *
   * @param list1 the base list
   * @param list2 the list to compare to the base list
   *
   * @return true if the lists are equal, false otherwise
   *
   */
  private boolean doListElementTest( List list1, List list2 )
  {

    if ( list1 != null )
    {
      if ( list2 == null )
        return false;
      else
      {
        if ( list1.size() != list2.size() )
          return false;   // sizes are different, not equal

        Iterator iObj2 = list2.iterator();

        for ( Object obj1 : list1 )
        {
          Object obj2 = iObj2.next();
          if ( !obj1.equals( obj2 ) )
            return false;

        } // end for

        return true;      // all elements are equal
      } // end else

    } // end if

    if ( list2 == null )
      return true;      // both lists are null so therefore the are equal

    return false;

  } // end doListElementTest()

} // *** End of class VwOrm{}

// *** End Of VwOrm.java