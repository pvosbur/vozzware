/*
============================================================================================

                       V o z z W o r k s   C o d e   G e n e r a t o r                      

                               2009 by V o z z W a r e   L L C                              

    Source File Name: VwConstraint.java

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


public class VwConstraint extends VwDVOBase implements Serializable, Cloneable
{

  private String                 m_strId;                        
  private String                 m_strSqlRef;                    
  private String                 m_strWhere;                     
  private List<VwMappingTableConstraint>  m_listMappingConstraints;       

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
   * Sets the sqlRef property
   */
  public void setSqlRef( String strSqlRef )
  { 
    
    testDirty( "sqlRef", strSqlRef );
    m_strSqlRef = strSqlRef;
  }

  /**
   * Gets sqlRef property
   * 
   * @return  The sqlRef property
   */
  public String getSqlRef()
  { return m_strSqlRef; }

  /**
   * Sets the where property
   */
  public void setWhere( String strWhere )
  { 
    
    testDirty( "where", strWhere );
    m_strWhere = strWhere;
  }

  /**
   * Gets where property
   * 
   * @return  The where property
   */
  public String getWhere()
  { return m_strWhere; }

  /**
   * Sets the mappingConstraints property
   */
  public void setMappingConstraints( List<VwMappingTableConstraint> listMappingConstraints )
  { 
    
    testDirty( "mappingConstraints", listMappingConstraints );
    m_listMappingConstraints = listMappingConstraints;
  }

  /**
   * Gets mappingConstraints property
   * 
   * @return  The mappingConstraints property
   */
  public List<VwMappingTableConstraint> getMappingConstraints()
  { return m_listMappingConstraints; }

  /**
   * Clones this object
   *
   */
  public Object clone()
  {
    VwConstraint classClone = new VwConstraint();
    
    classClone.m_strId = m_strId;
    classClone.m_strSqlRef = m_strSqlRef;
    classClone.m_strWhere = m_strWhere;

    if ( m_listMappingConstraints  != null )
      classClone.m_listMappingConstraints = (List<VwMappingTableConstraint>)cloneList( m_listMappingConstraints );

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

    VwConstraint objToTest = (VwConstraint)objTest;

    if ( ! doObjectEqualsTest( m_strId, objToTest.m_strId ) )
      return false; 

    if ( ! doObjectEqualsTest( m_strSqlRef, objToTest.m_strSqlRef ) )
      return false; 

    if ( ! doObjectEqualsTest( m_strWhere, objToTest.m_strWhere ) )
      return false; 

    if ( ! doListElementTest( m_listMappingConstraints, objToTest.m_listMappingConstraints ) )
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

} // *** End of class VwConstraint{}

// *** End Of VwConstraint.java