/*
============================================================================================

                       V o z z W o r k s   C o d e   G e n e r a t o r                      

                               2009 by V o z z W a r e   L L C                              

    Source File Name: VwConnectionPool.java

    Author:           

    Date Generated:   10-16-2020

    Time Generated:   09:55:17

============================================================================================
*/

package com.vozzware.db.util;

import com.vozzware.db.VwDVOBase;
import com.vozzware.util.VwBeanUtils;

import java.io.Serializable;


public class VwConnectionPool extends VwDVOBase implements Serializable, Cloneable
{

  private String                 m_strId;                        
  private String                 m_strMin;                       
  private String                 m_strMax;                       
  private String m_strCred;

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
   * Sets the min property
   */
  public void setMin( String strMin )
  { 
    
    testDirty( "min", strMin );
    m_strMin = strMin;
  }

  /**
   * Gets min property
   * 
   * @return  The min property
   */
  public String getMin()
  { return m_strMin; }

  /**
   * Sets the max property
   */
  public void setMax( String strMax )
  { 
    
    testDirty( "max", strMax );
    m_strMax = strMax;
  }

  /**
   * Gets max property
   * 
   * @return  The max property
   */
  public String getMax()
  { return m_strMax; }



  /**
   * Sets the credential property
   */
  public void setCred( String strCred )
  { 
    
    testDirty( "cred", strCred );
    m_strCred = strCred;
  }

  /**
   * Gets credential property
   * 
   * @return  The pwd property
   */
  public String getCred()
  { return m_strCred; }

  /**
   * Clones this object
   *
   */
  public Object clone()
  {
    VwConnectionPool classClone = new VwConnectionPool();
    
    classClone.m_strId = m_strId;
    classClone.m_strMin = m_strMin;
    classClone.m_strMax = m_strMax;
    classClone.m_strCred = m_strCred;

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

    VwConnectionPool objToTest = (VwConnectionPool)objTest;

    if ( ! doObjectEqualsTest( m_strId, objToTest.m_strId ) )
      return false; 

    if ( ! doObjectEqualsTest( m_strMin, objToTest.m_strMin ) )
      return false; 

    if ( ! doObjectEqualsTest( m_strMax, objToTest.m_strMax ) )
      return false; 


    if ( ! doObjectEqualsTest( m_strCred, objToTest.m_strCred ) )
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
} // *** End of class VwConnectionPool{}

// *** End Of VwConnectionPool.java