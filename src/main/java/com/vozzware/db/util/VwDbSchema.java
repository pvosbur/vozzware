/*
============================================================================================

                       V o z z W o r k s   C o d e   G e n e r a t o r                      

                               2009 by V o z z W a r e   L L C                              

    Source File Name: VwDbSchema.java

    Author:           

    Date Generated:   07-13-2019

    Time Generated:   10:51:56

============================================================================================
*/

package com.vozzware.db.util;

import com.vozzware.util.VwBeanUtils;

import java.io.Serializable;


public class VwDbSchema extends VwDbObjCommon implements Serializable, Cloneable
{

  private String                 m_strName;                      
  private String                 m_strIncludeTables;             
  private String                 m_strExcludeTables;             

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
   * Sets the name property
   */
  public void setName( String strName )
  { 
    
    testDirty( "name", strName );
    m_strName = strName;
  }

  /**
   * Gets name property
   * 
   * @return  The name property
   */
  public String getName()
  { return m_strName; }

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
   * Clones this object
   *
   */
  public Object clone()
  {
    VwDbSchema classClone = new VwDbSchema();
    
    classClone.m_strName = m_strName;
    classClone.m_strIncludeTables = m_strIncludeTables;
    classClone.m_strExcludeTables = m_strExcludeTables;

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

    VwDbSchema objToTest = (VwDbSchema)objTest;

    if ( ! doObjectEqualsTest( m_strName, objToTest.m_strName ) )
      return false; 

    if ( ! doObjectEqualsTest( m_strIncludeTables, objToTest.m_strIncludeTables ) )
      return false; 

    if ( ! doObjectEqualsTest( m_strExcludeTables, objToTest.m_strExcludeTables ) )
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
} // *** End of class VwDbSchema{}

// *** End Of VwDbSchema.java