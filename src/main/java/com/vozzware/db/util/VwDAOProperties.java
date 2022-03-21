/*
============================================================================================

                       V o z z W o r k s   C o d e   G e n e r a t o r                      

                               2009 by V o z z W a r e   L L C                              

    Source File Name: VwDAOProperties.java

    Author:           

    Date Generated:   07-13-2019

    Time Generated:   10:51:56

============================================================================================
*/

package com.vozzware.db.util;

import com.vozzware.db.VwDVOBase;
import com.vozzware.util.VwBeanUtils;

import java.io.Serializable;


public class VwDAOProperties extends VwDVOBase implements Serializable, Cloneable
{

  private String                 m_strBasePath;                  
  private String                 m_strPackage;                   
  private String                 m_strClassName;                 
  private String                 m_strExtensionClass;            
  private String                 m_strUseSinglton;               
  private String                 m_strTypePrefix;                
  private String                 m_strTypeSuffix;                

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
   * Sets the className property
   */
  public void setClassName( String strClassName )
  { 
    
    testDirty( "className", strClassName );
    m_strClassName = strClassName;
  }

  /**
   * Gets className property
   * 
   * @return  The className property
   */
  public String getClassName()
  { return m_strClassName; }

  /**
   * Sets the extensionClass property
   */
  public void setExtensionClass( String strExtensionClass )
  { 
    
    testDirty( "extensionClass", strExtensionClass );
    m_strExtensionClass = strExtensionClass;
  }

  /**
   * Gets extensionClass property
   * 
   * @return  The extensionClass property
   */
  public String getExtensionClass()
  { return m_strExtensionClass; }

  /**
   * Sets the useSinglton property
   */
  public void setUseSinglton( String strUseSinglton )
  { 
    
    testDirty( "useSinglton", strUseSinglton );
    m_strUseSinglton = strUseSinglton;
  }

  /**
   * Gets useSinglton property
   * 
   * @return  The useSinglton property
   */
  public String getUseSinglton()
  { return m_strUseSinglton; }

  /**
   * Sets the typePrefix property
   */
  public void setTypePrefix( String strTypePrefix )
  { 
    
    testDirty( "typePrefix", strTypePrefix );
    m_strTypePrefix = strTypePrefix;
  }

  /**
   * Gets typePrefix property
   * 
   * @return  The typePrefix property
   */
  public String getTypePrefix()
  { return m_strTypePrefix; }

  /**
   * Sets the typeSuffix property
   */
  public void setTypeSuffix( String strTypeSuffix )
  { 
    
    testDirty( "typeSuffix", strTypeSuffix );
    m_strTypeSuffix = strTypeSuffix;
  }

  /**
   * Gets typeSuffix property
   * 
   * @return  The typeSuffix property
   */
  public String getTypeSuffix()
  { return m_strTypeSuffix; }

  /**
   * Clones this object
   *
   */
  public Object clone()
  {
    VwDAOProperties classClone = new VwDAOProperties();
    
    classClone.m_strBasePath = m_strBasePath;
    classClone.m_strPackage = m_strPackage;
    classClone.m_strClassName = m_strClassName;
    classClone.m_strExtensionClass = m_strExtensionClass;
    classClone.m_strUseSinglton = m_strUseSinglton;
    classClone.m_strTypePrefix = m_strTypePrefix;
    classClone.m_strTypeSuffix = m_strTypeSuffix;

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

    VwDAOProperties objToTest = (VwDAOProperties)objTest;

    if ( ! doObjectEqualsTest( m_strBasePath, objToTest.m_strBasePath ) )
      return false; 

    if ( ! doObjectEqualsTest( m_strPackage, objToTest.m_strPackage ) )
      return false; 

    if ( ! doObjectEqualsTest( m_strClassName, objToTest.m_strClassName ) )
      return false; 

    if ( ! doObjectEqualsTest( m_strExtensionClass, objToTest.m_strExtensionClass ) )
      return false; 

    if ( ! doObjectEqualsTest( m_strUseSinglton, objToTest.m_strUseSinglton ) )
      return false; 

    if ( ! doObjectEqualsTest( m_strTypePrefix, objToTest.m_strTypePrefix ) )
      return false; 

    if ( ! doObjectEqualsTest( m_strTypeSuffix, objToTest.m_strTypeSuffix ) )
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
} // *** End of class VwDAOProperties{}

// *** End Of VwDAOProperties.java