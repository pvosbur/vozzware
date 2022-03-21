/*
============================================================================================

                       V o z z W o r k s   C o d e   G e n e r a t o r                      

                               2009 by V o z z W a r e   L L C                              

    Source File Name: VwSql.java

    Author:           

    Date Generated:   05-27-2018

    Time Generated:   07:57:47

============================================================================================
*/

package com.vozzware.db.util;

import com.vozzware.db.VwDVOBase;
import com.vozzware.util.VwBeanUtils;

import java.io.Serializable;


public class VwSql extends VwDVOBase implements Serializable, Cloneable
{

  private String                 m_strId;                        
  private String                 m_strBody;                      

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
   * Sets the id property
   */
  public void setId( String strId )
  { 
    
    testDirty( "id", strId );
    m_strId = strId;
  }

  /**
   * Gets id property
   * 
   * @return  The id property
   */
  public String getId()
  { return m_strId; }

  /**
   * Sets the body property
   */
  public void setBody( String strBody )
  { 
    
    testDirty( "body", strBody );
    m_strBody = strBody;
  }

  /**
   * Gets body property
   * 
   * @return  The body property
   */
  public String getBody()
  { return m_strBody; }

  /**
   * Clones this object
   *
   */
  public Object clone()
  {
    VwSql classClone = new VwSql();
    
    classClone.m_strId = m_strId;
    classClone.m_strBody = m_strBody;

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

    VwSql objToTest = (VwSql)objTest;

    if ( ! doObjectEqualsTest( m_strId, objToTest.m_strId ) )
      return false; 

    if ( ! doObjectEqualsTest( m_strBody, objToTest.m_strBody ) )
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
} // *** End of class VwSql{}

// *** End Of VwSql.java