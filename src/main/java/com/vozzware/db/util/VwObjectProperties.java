/*
============================================================================================

                       V o z z W o r k s   C o d e   G e n e r a t o r                      

                               2009 by V o z z W a r e   L L C                              

    Source File Name: VwObjectProperties.java

    Author:           

    Date Generated:   07-13-2019

    Time Generated:   10:51:56

============================================================================================
*/

package com.vozzware.db.util;

import com.vozzware.db.VwDVOBase;
import com.vozzware.util.VwBeanUtils;

import java.io.Serializable;


public class VwObjectProperties extends VwDVOBase implements Serializable, Cloneable
{

  private String                 m_strBasePath;                  
  private String                 m_strPackage;                   
  private String                 m_strUseJavaObjects = "true";   
  private String                 m_strUseDirtyObjectDetection = "true"; 
  private String                 m_strClassPath;                 
  private String                 m_strSuperClass;                
  private String                 m_strTreatChar1AsBoolean;       

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
   * Sets the basePath property
   */
  public void setBasePath( String strBasePath )
  { 
    
    testDirty( "basePath", strBasePath );
    m_strBasePath = strBasePath;
  }

  /**
   * Gets basePath property
   * 
   * @return  The basePath property
   */
  public String getBasePath()
  { return m_strBasePath; }

  /**
   * Sets the package property
   */
  public void setPackage( String strPackage )
  { 
    
    testDirty( "package", strPackage );
    m_strPackage = strPackage;
  }

  /**
   * Gets package property
   * 
   * @return  The package property
   */
  public String getPackage()
  { return m_strPackage; }

  /**
   * Sets the useJavaObjects property
   */
  public void setUseJavaObjects( String strUseJavaObjects )
  { 
    
    testDirty( "useJavaObjects", strUseJavaObjects );
    m_strUseJavaObjects = strUseJavaObjects;
  }

  /**
   * Gets useJavaObjects property
   * 
   * @return  The useJavaObjects property
   */
  public String getUseJavaObjects()
  { return m_strUseJavaObjects; }

  /**
   * Sets the useDirtyObjectDetection property
   */
  public void setUseDirtyObjectDetection( String strUseDirtyObjectDetection )
  { 
    
    testDirty( "useDirtyObjectDetection", strUseDirtyObjectDetection );
    m_strUseDirtyObjectDetection = strUseDirtyObjectDetection;
  }

  /**
   * Gets useDirtyObjectDetection property
   * 
   * @return  The useDirtyObjectDetection property
   */
  public String getUseDirtyObjectDetection()
  { return m_strUseDirtyObjectDetection; }

  /**
   * Sets the classPath property
   */
  public void setClassPath( String strClassPath )
  { 
    
    testDirty( "classPath", strClassPath );
    m_strClassPath = strClassPath;
  }

  /**
   * Gets classPath property
   * 
   * @return  The classPath property
   */
  public String getClassPath()
  { return m_strClassPath; }

  /**
   * Sets the superClass property
   */
  public void setSuperClass( String strSuperClass )
  { 
    
    testDirty( "superClass", strSuperClass );
    m_strSuperClass = strSuperClass;
  }

  /**
   * Gets superClass property
   * 
   * @return  The superClass property
   */
  public String getSuperClass()
  { return m_strSuperClass; }

  /**
   * Sets the treatChar1AsBoolean property
   */
  public void setTreatChar1AsBoolean( String strTreatChar1AsBoolean )
  { 
    
    testDirty( "treatChar1AsBoolean", strTreatChar1AsBoolean );
    m_strTreatChar1AsBoolean = strTreatChar1AsBoolean;
  }

  /**
   * Gets treatChar1AsBoolean property
   * 
   * @return  The treatChar1AsBoolean property
   */
  public String getTreatChar1AsBoolean()
  { return m_strTreatChar1AsBoolean; }

  /**
   * Clones this object
   *
   */
  public Object clone()
  {
    VwObjectProperties classClone = new VwObjectProperties();
    
    classClone.m_strBasePath = m_strBasePath;
    classClone.m_strPackage = m_strPackage;
    classClone.m_strUseJavaObjects = m_strUseJavaObjects;
    classClone.m_strUseDirtyObjectDetection = m_strUseDirtyObjectDetection;
    classClone.m_strClassPath = m_strClassPath;
    classClone.m_strSuperClass = m_strSuperClass;
    classClone.m_strTreatChar1AsBoolean = m_strTreatChar1AsBoolean;

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

    VwObjectProperties objToTest = (VwObjectProperties)objTest;

    if ( ! doObjectEqualsTest( m_strBasePath, objToTest.m_strBasePath ) )
      return false; 

    if ( ! doObjectEqualsTest( m_strPackage, objToTest.m_strPackage ) )
      return false; 

    if ( ! doObjectEqualsTest( m_strUseJavaObjects, objToTest.m_strUseJavaObjects ) )
      return false; 

    if ( ! doObjectEqualsTest( m_strUseDirtyObjectDetection, objToTest.m_strUseDirtyObjectDetection ) )
      return false; 

    if ( ! doObjectEqualsTest( m_strClassPath, objToTest.m_strClassPath ) )
      return false; 

    if ( ! doObjectEqualsTest( m_strSuperClass, objToTest.m_strSuperClass ) )
      return false; 

    if ( ! doObjectEqualsTest( m_strTreatChar1AsBoolean, objToTest.m_strTreatChar1AsBoolean ) )
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
} // *** End of class VwObjectProperties{}

// *** End Of VwObjectProperties.java