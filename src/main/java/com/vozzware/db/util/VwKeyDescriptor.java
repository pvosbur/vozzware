/*
============================================================================================

                       V o z z W o r k s   C o d e   G e n e r a t o r                      

                               2009 by V o z z W a r e   L L C                              

    Source File Name: VwKeyDescriptor.java

    Author:           

    Date Generated:   05-27-2018

    Time Generated:   07:57:47

============================================================================================
*/

package com.vozzware.db.util;

import com.vozzware.db.VwDVOBase;
import com.vozzware.util.VwBeanUtils;

import java.io.Serializable;


public class VwKeyDescriptor extends VwDVOBase implements Serializable, Cloneable
{

  private String                 m_strBeanProperty;              
  private String                 m_strPrimeKeyProperty;          
  private String                 m_strForeignKeyProperty;        

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
   * Sets the beanProperty property
   */
  public void setBeanProperty( String strBeanProperty )
  { 
    
    testDirty( "beanProperty", strBeanProperty );
    m_strBeanProperty = strBeanProperty;
  }

  /**
   * Gets beanProperty property
   * 
   * @return  The beanProperty property
   */
  public String getBeanProperty()
  { return m_strBeanProperty; }

  /**
   * Sets the primeKeyProperty property
   */
  public void setPrimeKeyProperty( String strPrimeKeyProperty )
  { 
    
    testDirty( "primeKeyProperty", strPrimeKeyProperty );
    m_strPrimeKeyProperty = strPrimeKeyProperty;
  }

  /**
   * Gets primeKeyProperty property
   * 
   * @return  The primeKeyProperty property
   */
  public String getPrimeKeyProperty()
  { return m_strPrimeKeyProperty; }

  /**
   * Sets the foreignKeyProperty property
   */
  public void setForeignKeyProperty( String strForeignKeyProperty )
  { 
    
    testDirty( "foreignKeyProperty", strForeignKeyProperty );
    m_strForeignKeyProperty = strForeignKeyProperty;
  }

  /**
   * Gets foreignKeyProperty property
   * 
   * @return  The foreignKeyProperty property
   */
  public String getForeignKeyProperty()
  { return m_strForeignKeyProperty; }

  /**
   * Clones this object
   *
   */
  public Object clone()
  {
    VwKeyDescriptor classClone = new VwKeyDescriptor();
    
    classClone.m_strBeanProperty = m_strBeanProperty;
    classClone.m_strPrimeKeyProperty = m_strPrimeKeyProperty;
    classClone.m_strForeignKeyProperty = m_strForeignKeyProperty;

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

    VwKeyDescriptor objToTest = (VwKeyDescriptor)objTest;

    if ( ! doObjectEqualsTest( m_strBeanProperty, objToTest.m_strBeanProperty ) )
      return false; 

    if ( ! doObjectEqualsTest( m_strPrimeKeyProperty, objToTest.m_strPrimeKeyProperty ) )
      return false; 

    if ( ! doObjectEqualsTest( m_strForeignKeyProperty, objToTest.m_strForeignKeyProperty ) )
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
} // *** End of class VwKeyDescriptor{}

// *** End Of VwKeyDescriptor.java