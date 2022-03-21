/*
============================================================================================

                       V o z z W o r k s   C o d e   G e n e r a t o r                      

                               2009 by V o z z W a r e   L L C                              

    Source File Name: VwExtendsDescriptor.java

    Author:           

    Date Generated:   05-27-2018

    Time Generated:   07:57:47

============================================================================================
*/

package com.vozzware.db.util;

import com.vozzware.db.VwDVOBase;
import com.vozzware.util.VwBeanUtils;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;


public class VwExtendsDescriptor extends VwDVOBase implements Serializable, Cloneable
{

  private String                 m_strSuperClass;                
  private String                 m_strSuperClassMappingId;       
  private List<String>           m_listSuperPrimeKeyProperties;  
  private List<String>           m_listPrimeKeyProperties;       

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
   * Sets the superClassMappingId property
   */
  public void setSuperClassMappingId( String strSuperClassMappingId )
  { 
    
    testDirty( "superClassMappingId", strSuperClassMappingId );
    m_strSuperClassMappingId = strSuperClassMappingId;
  }

  /**
   * Gets superClassMappingId property
   * 
   * @return  The superClassMappingId property
   */
  public String getSuperClassMappingId()
  { return m_strSuperClassMappingId; }

  /**
   * Sets the superPrimeKeyProperties property
   */
  public void setSuperPrimeKeyProperties( List<String> listSuperPrimeKeyProperties )
  { 
    
    testDirty( "superPrimeKeyProperties", listSuperPrimeKeyProperties );
    m_listSuperPrimeKeyProperties = listSuperPrimeKeyProperties;
  }

  /**
   * Gets superPrimeKeyProperties property
   * 
   * @return  The superPrimeKeyProperties property
   */
  public List<String> getSuperPrimeKeyProperties()
  { return m_listSuperPrimeKeyProperties; }

  /**
   * Sets the primeKeyProperties property
   */
  public void setPrimeKeyProperties( List<String> listPrimeKeyProperties )
  { 
    
    testDirty( "primeKeyProperties", listPrimeKeyProperties );
    m_listPrimeKeyProperties = listPrimeKeyProperties;
  }

  /**
   * Gets primeKeyProperties property
   * 
   * @return  The primeKeyProperties property
   */
  public List<String> getPrimeKeyProperties()
  { return m_listPrimeKeyProperties; }

  /**
   * Clones this object
   *
   */
  public Object clone()
  {
    VwExtendsDescriptor classClone = new VwExtendsDescriptor();
    
    classClone.m_strSuperClass = m_strSuperClass;
    classClone.m_strSuperClassMappingId = m_strSuperClassMappingId;

    if ( m_listSuperPrimeKeyProperties  != null )
      classClone.m_listSuperPrimeKeyProperties = (List<String>)cloneList( m_listSuperPrimeKeyProperties );

    if ( m_listPrimeKeyProperties  != null )
      classClone.m_listPrimeKeyProperties = (List<String>)cloneList( m_listPrimeKeyProperties );

    return classClone;
  }



  /**
   *Clones a list and all its elements
   *
   * @param list The list to clone
   *
   * @return The cloned List object
   *
   */
  private List cloneList( List list )
  {

    try
    {
      List listClone = (List)list.getClass().newInstance();

      for ( Object objListContent : list )
      {
        if ( objListContent instanceof Cloneable )
        {
          Method mthdClone = objListContent.getClass().getMethod( "clone", (Class[])null );
          Object objClone = mthdClone.invoke( objListContent, (Object[])null );
          listClone.add( objClone );
        } // end if
      } // end for()

      return listClone;
    }
    catch( Exception ex )
    {
      throw new RuntimeException( ex.toString() );
    }
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

    VwExtendsDescriptor objToTest = (VwExtendsDescriptor)objTest;

    if ( ! doObjectEqualsTest( m_strSuperClass, objToTest.m_strSuperClass ) )
      return false; 

    if ( ! doObjectEqualsTest( m_strSuperClassMappingId, objToTest.m_strSuperClassMappingId ) )
      return false; 

    if ( ! doListElementTest( m_listSuperPrimeKeyProperties, objToTest.m_listSuperPrimeKeyProperties ) )
      return false;

    if ( ! doListElementTest( m_listPrimeKeyProperties, objToTest.m_listPrimeKeyProperties ) )
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

  /**
   * Do equals test on each object in the list
   *
   * @param list1 the base list
   * @param list2 the list to compare to the base list
   *
   * @return true if the lists are equal, false otherwise
   *
   */
  private boolean doListElementTest( List list1, List list2 )
  {

    if ( list1 != null )
    {
      if ( list2 == null )
        return false;
      else
      {
        if ( list1.size() != list2.size() )
          return false;   // sizes are different, not equal

        Iterator iObj2 = list2.iterator();

        for ( Object obj1 : list1 )
        {
          Object obj2 = iObj2.next();
          if ( !obj1.equals( obj2 ) )
            return false;

        } // end for

        return true;      // all elements are equal
      } // end else

    } // end if

    if ( list2 == null )
      return true;      // both lists are null so therefore the are equal

    return false;

  } // end doListElementTest()

} // *** End of class VwExtendsDescriptor{}

// *** End Of VwExtendsDescriptor.java