/*
============================================================================================

                       V o z z W o r k s   C o d e   G e n e r a t o r                      

                               2009 by V o z z W a r e   L L C                              

    Source File Name: VwPrimaryKeyGeneration.java

    Author:           

    Date Generated:   05-27-2018

    Time Generated:   07:57:47

============================================================================================
*/

package com.vozzware.db.util;

import com.vozzware.db.VwDVOBase;
import com.vozzware.util.VwBeanUtils;

import java.io.Serializable;


public class VwPrimaryKeyGeneration extends VwDVOBase implements Serializable, Cloneable
{

  private String                 m_strBeanProperty;              
  private String                 m_strKeyGenerationPolicy;       
  private String                 m_strSequenceDataSourceName;    
  private String                 m_strSequenceTableName;         
  private String                 m_strSequenceName;              
  private String                 m_strSequenceColName;           

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
   * Sets the keyGenerationPolicy property
   */
  public void setKeyGenerationPolicy( String strKeyGenerationPolicy )
  { 
    
    testDirty( "keyGenerationPolicy", strKeyGenerationPolicy );
    m_strKeyGenerationPolicy = strKeyGenerationPolicy;
  }

  /**
   * Gets keyGenerationPolicy property
   * 
   * @return  The keyGenerationPolicy property
   */
  public String getKeyGenerationPolicy()
  { return m_strKeyGenerationPolicy; }

  /**
   * Sets the sequenceDataSourceName property
   */
  public void setSequenceDataSourceName( String strSequenceDataSourceName )
  { 
    
    testDirty( "sequenceDataSourceName", strSequenceDataSourceName );
    m_strSequenceDataSourceName = strSequenceDataSourceName;
  }

  /**
   * Gets sequenceDataSourceName property
   * 
   * @return  The sequenceDataSourceName property
   */
  public String getSequenceDataSourceName()
  { return m_strSequenceDataSourceName; }

  /**
   * Sets the sequenceTableName property
   */
  public void setSequenceTableName( String strSequenceTableName )
  { 
    
    testDirty( "sequenceTableName", strSequenceTableName );
    m_strSequenceTableName = strSequenceTableName;
  }

  /**
   * Gets sequenceTableName property
   * 
   * @return  The sequenceTableName property
   */
  public String getSequenceTableName()
  { return m_strSequenceTableName; }

  /**
   * Sets the sequenceName property
   */
  public void setSequenceName( String strSequenceName )
  { 
    
    testDirty( "sequenceName", strSequenceName );
    m_strSequenceName = strSequenceName;
  }

  /**
   * Gets sequenceName property
   * 
   * @return  The sequenceName property
   */
  public String getSequenceName()
  { return m_strSequenceName; }

  /**
   * Sets the sequenceColName property
   */
  public void setSequenceColName( String strSequenceColName )
  { 
    
    testDirty( "sequenceColName", strSequenceColName );
    m_strSequenceColName = strSequenceColName;
  }

  /**
   * Gets sequenceColName property
   * 
   * @return  The sequenceColName property
   */
  public String getSequenceColName()
  { return m_strSequenceColName; }

  /**
   * Clones this object
   *
   */
  public Object clone()
  {
    VwPrimaryKeyGeneration classClone = new VwPrimaryKeyGeneration();
    
    classClone.m_strBeanProperty = m_strBeanProperty;
    classClone.m_strKeyGenerationPolicy = m_strKeyGenerationPolicy;
    classClone.m_strSequenceDataSourceName = m_strSequenceDataSourceName;
    classClone.m_strSequenceTableName = m_strSequenceTableName;
    classClone.m_strSequenceName = m_strSequenceName;
    classClone.m_strSequenceColName = m_strSequenceColName;

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

    VwPrimaryKeyGeneration objToTest = (VwPrimaryKeyGeneration)objTest;

    if ( ! doObjectEqualsTest( m_strBeanProperty, objToTest.m_strBeanProperty ) )
      return false; 

    if ( ! doObjectEqualsTest( m_strKeyGenerationPolicy, objToTest.m_strKeyGenerationPolicy ) )
      return false; 

    if ( ! doObjectEqualsTest( m_strSequenceDataSourceName, objToTest.m_strSequenceDataSourceName ) )
      return false; 

    if ( ! doObjectEqualsTest( m_strSequenceTableName, objToTest.m_strSequenceTableName ) )
      return false; 

    if ( ! doObjectEqualsTest( m_strSequenceName, objToTest.m_strSequenceName ) )
      return false; 

    if ( ! doObjectEqualsTest( m_strSequenceColName, objToTest.m_strSequenceColName ) )
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
} // *** End of class VwPrimaryKeyGeneration{}

// *** End Of VwPrimaryKeyGeneration.java