/*
============================================================================================

                       V o z z W o r k s   C o d e   G e n e r a t o r                      

                               2009 by V o z z W a r e   L L C                              

    Source File Name: VwMappingTableConstraint.java

    Author:           

    Date Generated:   07-13-2019

    Time Generated:   10:51:56

============================================================================================
*/

package com.vozzware.db.util;

import com.vozzware.db.VwDVOBase;
import com.vozzware.util.VwBeanUtils;

import java.io.Serializable;


public class VwMappingTableConstraint extends VwDVOBase implements Serializable, Cloneable
{

  private String                 m_strTableName;                 
  private String                 m_strWhere;                     
  private String                 m_strIncludeCols;               
  private String                 m_strExcludeCols;               

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
   * Sets the tableName property
   */
  public void setTableName( String strTableName )
  { 
    
    testDirty( "tableName", strTableName );
    m_strTableName = strTableName;
  }

  /**
   * Gets tableName property
   * 
   * @return  The tableName property
   */
  public String getTableName()
  { return m_strTableName; }

  /**
   * Sets the where property
   */
  public void setWhere( String strWhere )
  { 
    
    testDirty( "where", strWhere );
    m_strWhere = strWhere;
  }

  /**
   * Gets where property
   * 
   * @return  The where property
   */
  public String getWhere()
  { return m_strWhere; }

  /**
   * Sets the includeCols property
   */
  public void setIncludeCols( String strIncludeCols )
  { 
    
    testDirty( "includeCols", strIncludeCols );
    m_strIncludeCols = strIncludeCols;
  }

  /**
   * Gets includeCols property
   * 
   * @return  The includeCols property
   */
  public String getIncludeCols()
  { return m_strIncludeCols; }

  /**
   * Sets the excludeCols property
   */
  public void setExcludeCols( String strExcludeCols )
  { 
    
    testDirty( "excludeCols", strExcludeCols );
    m_strExcludeCols = strExcludeCols;
  }

  /**
   * Gets excludeCols property
   * 
   * @return  The excludeCols property
   */
  public String getExcludeCols()
  { return m_strExcludeCols; }

  /**
   * Clones this object
   *
   */
  public Object clone()
  {
    VwMappingTableConstraint classClone = new VwMappingTableConstraint();
    
    classClone.m_strTableName = m_strTableName;
    classClone.m_strWhere = m_strWhere;
    classClone.m_strIncludeCols = m_strIncludeCols;
    classClone.m_strExcludeCols = m_strExcludeCols;

    return classClone;
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

    VwMappingTableConstraint objToTest = (VwMappingTableConstraint)objTest;

    if ( ! doObjectEqualsTest( m_strTableName, objToTest.m_strTableName ) )
      return false; 

    if ( ! doObjectEqualsTest( m_strWhere, objToTest.m_strWhere ) )
      return false; 

    if ( ! doObjectEqualsTest( m_strIncludeCols, objToTest.m_strIncludeCols ) )
      return false; 

    if ( ! doObjectEqualsTest( m_strExcludeCols, objToTest.m_strExcludeCols ) )
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
} // *** End of class VwMappingTableConstraint{}

// *** End Of VwMappingTableConstraint.java