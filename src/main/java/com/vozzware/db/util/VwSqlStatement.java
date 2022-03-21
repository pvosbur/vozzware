/*
============================================================================================

                       V o z z W o r k s   C o d e   G e n e r a t o r                      

                               2009 by V o z z W a r e   L L C                              

    Source File Name: VwSqlStatement.java

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


public class VwSqlStatement extends VwDVOBase implements Serializable, Cloneable
{

  private List<VwConstraint>     m_listConstraint;               
  private List<VwSql>            m_listSql;                      

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
   * Sets the constraint property
   */
  public void setConstraint( List<VwConstraint> listConstraint )
  { 
    
    testDirty( "constraint", listConstraint );
    m_listConstraint = listConstraint;
  }

  /**
   * Gets constraint property
   * 
   * @return  The constraint property
   */
  public List<VwConstraint> getConstraint()
  { return m_listConstraint; }

  /**
   * Sets the sql property
   */
  public void setSql( List<VwSql> listSql )
  { 
    
    testDirty( "sql", listSql );
    m_listSql = listSql;
  }

  /**
   * Gets sql property
   * 
   * @return  The sql property
   */
  public List<VwSql> getSql()
  { return m_listSql; }

  /**
   * Clones this object
   *
   */
  public Object clone()
  {
    VwSqlStatement classClone = new VwSqlStatement();
    

    if ( m_listConstraint  != null )
      classClone.m_listConstraint = (List<VwConstraint>)cloneList( m_listConstraint );

    if ( m_listSql  != null )
      classClone.m_listSql = (List<VwSql>)cloneList( m_listSql );

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

    VwSqlStatement objToTest = (VwSqlStatement)objTest;

    if ( ! doListElementTest( m_listConstraint, objToTest.m_listConstraint ) )
      return false;

    if ( ! doListElementTest( m_listSql, objToTest.m_listSql ) )
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

} // *** End of class VwSqlStatement{}

// *** End Of VwSqlStatement.java