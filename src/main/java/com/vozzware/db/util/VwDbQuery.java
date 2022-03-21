/*
============================================================================================

                       V o z z W o r k s   C o d e   G e n e r a t o r                      

                               2009 by V o z z W a r e   L L C                              

    Source File Name: VwDbQuery.java

    Author:           

    Date Generated:   07-13-2019

    Time Generated:   10:51:56

============================================================================================
*/

package com.vozzware.db.util;

import com.vozzware.util.VwBeanUtils;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;


public class VwDbQuery extends VwDbObjCommon implements Serializable, Cloneable
{

  private String                 m_strSql;                       
  private List<VwFinder>         m_listFinder;                   

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
   * Sets the finder property
   */
  public void setFinder( List<VwFinder> listFinder )
  { 
    
    testDirty( "finder", listFinder );
    m_listFinder = listFinder;
  }

  /**
   * Gets finder property
   * 
   * @return  The finder property
   */
  public List<VwFinder> getFinder()
  { return m_listFinder; }

  /**
   * Clones this object
   *
   */
  public Object clone()
  {
    VwDbQuery classClone = new VwDbQuery();
    
    classClone.m_strSql = m_strSql;

    if ( m_listFinder  != null )
      classClone.m_listFinder = (List<VwFinder>)cloneList( m_listFinder );

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

    VwDbQuery objToTest = (VwDbQuery)objTest;

    if ( ! doObjectEqualsTest( m_strSql, objToTest.m_strSql ) )
      return false; 

    if ( ! doListElementTest( m_listFinder, objToTest.m_listFinder ) )
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

} // *** End of class VwDbQuery{}

// *** End Of VwDbQuery.java