/*
============================================================================================

                       V o z z W o r k s   C o d e   G e n e r a t o r                      

                               2009 by V o z z W a r e   L L C                              

    Source File Name: VwConnectionProperty.java

    Author:           

    Date Generated:   10-16-2020

    Time Generated:   09:55:17

============================================================================================
*/

package com.vozzware.db.util;

import com.vozzware.db.VwDVOBase;
import com.vozzware.util.VwBeanUtils;

import java.io.Serializable;


public class VwConnectionProperty extends VwDVOBase implements Serializable, Cloneable
{

  private String                 m_strName;                      
  private String                 m_strValue;                     

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
   * Sets the value property
   */
  public void setValue( String strValue )
  { 
    
    testDirty( "value", strValue );
    m_strValue = strValue;
  }

  /**
   * Gets value property
   * 
   * @return  The value property
   */
  public String getValue()
  { return m_strValue; }

  /**
   * Clones this object
   *
   */
  public Object clone()
  {
    VwConnectionProperty classClone = new VwConnectionProperty();
    
    classClone.m_strName = m_strName;
    classClone.m_strValue = m_strValue;

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

    VwConnectionProperty objToTest = (VwConnectionProperty)objTest;

    if ( ! doObjectEqualsTest( m_strName, objToTest.m_strName ) )
      return false; 

    if ( ! doObjectEqualsTest( m_strValue, objToTest.m_strValue ) )
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
} // *** End of class VwConnectionProperty{}

// *** End Of VwConnectionProperty.java