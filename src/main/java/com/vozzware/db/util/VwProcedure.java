/*
============================================================================================

                       V o z z W o r k s   C o d e   G e n e r a t o r                      

                               2009 by V o z z W a r e   L L C                              

    Source File Name: VwProcedure.java

    Author:           

    Date Generated:   07-13-2019

    Time Generated:   10:51:56

============================================================================================
*/

package com.vozzware.db.util;

import com.vozzware.util.VwBeanUtils;

import java.io.Serializable;


public class VwProcedure extends VwDbObjCommon implements Serializable, Cloneable
{

  private String                 m_strName;                      
  private String                 m_strSql;                       
  private String                 m_strReturnsResultset;          

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
   * Sets the sql property
   */
  public void setSql( String strSql )
  { 
    
    testDirty( "sql", strSql );
    m_strSql = strSql;
  }

  /**
   * Gets sql property
   * 
   * @return  The sql property
   */
  public String getSql()
  { return m_strSql; }

  /**
   * Sets the returnsResultset property
   */
  public void setReturnsResultset( String strReturnsResultset )
  { 
    
    testDirty( "returnsResultset", strReturnsResultset );
    m_strReturnsResultset = strReturnsResultset;
  }

  /**
   * Gets returnsResultset property
   * 
   * @return  The returnsResultset property
   */
  public String getReturnsResultset()
  { return m_strReturnsResultset; }

  /**
   * Clones this object
   *
   */
  public Object clone()
  {
    VwProcedure classClone = new VwProcedure();
    
    classClone.m_strName = m_strName;
    classClone.m_strSql = m_strSql;
    classClone.m_strReturnsResultset = m_strReturnsResultset;

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

    VwProcedure objToTest = (VwProcedure)objTest;

    if ( ! doObjectEqualsTest( m_strName, objToTest.m_strName ) )
      return false; 

    if ( ! doObjectEqualsTest( m_strSql, objToTest.m_strSql ) )
      return false; 

    if ( ! doObjectEqualsTest( m_strReturnsResultset, objToTest.m_strReturnsResultset ) )
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
} // *** End of class VwProcedure{}

// *** End Of VwProcedure.java