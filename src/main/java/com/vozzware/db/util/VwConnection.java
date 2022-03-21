/*
============================================================================================

                       V o z z W o r k s   C o d e   G e n e r a t o r                      

                               2009 by V o z z W a r e   L L C                              

    Source File Name: VwConnection.java

    Author:           

    Date Generated:   07-13-2019

    Time Generated:   10:51:56

============================================================================================
*/

package com.vozzware.db.util;

import com.vozzware.db.VwDVOBase;
import com.vozzware.util.VwBeanUtils;

import java.io.Serializable;


public class VwConnection extends VwDVOBase implements Serializable, Cloneable
{

  private String                 m_strDriverId;                  
  private String                 m_strDriverUrl;                 
  private String                 m_strUid;                       
  private String                 m_strPwd;                       

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
   * Sets the driverId property
   */
  public void setDriverId( String strDriverId )
  { 
    
    testDirty( "driverId", strDriverId );
    m_strDriverId = strDriverId;
  }

  /**
   * Gets driverId property
   * 
   * @return  The driverId property
   */
  public String getDriverId()
  { return m_strDriverId; }

  /**
   * Sets the driverUrl property
   */
  public void setDriverUrl( String strDriverUrl )
  { 
    
    testDirty( "driverUrl", strDriverUrl );
    m_strDriverUrl = strDriverUrl;
  }

  /**
   * Gets driverUrl property
   * 
   * @return  The driverUrl property
   */
  public String getDriverUrl()
  { return m_strDriverUrl; }

  /**
   * Sets the uid property
   */
  public void setUid( String strUid )
  { 
    
    testDirty( "uid", strUid );
    m_strUid = strUid;
  }

  /**
   * Gets uid property
   * 
   * @return  The uid property
   */
  public String getUid()
  { return m_strUid; }

  /**
   * Sets the pwd property
   */
  public void setPwd( String strPwd )
  { 
    
    testDirty( "pwd", strPwd );
    m_strPwd = strPwd;
  }

  /**
   * Gets pwd property
   * 
   * @return  The pwd property
   */
  public String getPwd()
  { return m_strPwd; }

  /**
   * Clones this object
   *
   */
  public Object clone()
  {
    VwConnection classClone = new VwConnection();
    
    classClone.m_strDriverId = m_strDriverId;
    classClone.m_strDriverUrl = m_strDriverUrl;
    classClone.m_strUid = m_strUid;
    classClone.m_strPwd = m_strPwd;

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

    VwConnection objToTest = (VwConnection)objTest;

    if ( ! doObjectEqualsTest( m_strDriverId, objToTest.m_strDriverId ) )
      return false; 

    if ( ! doObjectEqualsTest( m_strDriverUrl, objToTest.m_strDriverUrl ) )
      return false; 

    if ( ! doObjectEqualsTest( m_strUid, objToTest.m_strUid ) )
      return false; 

    if ( ! doObjectEqualsTest( m_strPwd, objToTest.m_strPwd ) )
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
} // *** End of class VwConnection{}

// *** End Of VwConnection.java