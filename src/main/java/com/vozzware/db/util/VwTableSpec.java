/*
============================================================================================

                       V o z z W o r k s   C o d e   G e n e r a t o r                      

                               2009 by V o z z W a r e   L L C                              

    Source File Name: VwTableSpec.java

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


public class VwTableSpec extends VwDbObjCommon implements Serializable, Cloneable
{

  private String                 m_strName;                      
  private String                 m_strIncludeCols;               
  private String                 m_strExcludeCols;               
  private String                 m_strPrimaryKeyCols;            
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
   * Sets the includeCols property
   */
  public void setIncludeCols( String strIncludeCols )
  { 
    
    testDirty( "includeCols", strIncludeCols );
    m_strIncludeCols = strIncludeCols;
  }

  /**
   * Gets includeCols property
   * 
   * @return  The includeCols property
   */
  public String getIncludeCols()
  { return m_strIncludeCols; }

  /**
   * Sets the excludeCols property
   */
  public void setExcludeCols( String strExcludeCols )
  { 
    
    testDirty( "excludeCols", strExcludeCols );
    m_strExcludeCols = strExcludeCols;
  }

  /**
   * Gets excludeCols property
   * 
   * @return  The excludeCols property
   */
  public String getExcludeCols()
  { return m_strExcludeCols; }

  /**
   * Sets the primaryKeyCols property
   */
  public void setPrimaryKeyCols( String strPrimaryKeyCols )
  { 
    
    testDirty( "primaryKeyCols", strPrimaryKeyCols );
    m_strPrimaryKeyCols = strPrimaryKeyCols;
  }

  /**
   * Gets primaryKeyCols property
   * 
   * @return  The primaryKeyCols property
   */
  public String getPrimaryKeyCols()
  { return m_strPrimaryKeyCols; }

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
    VwTableSpec classClone = new VwTableSpec();
    
    classClone.m_strName = m_strName;
    classClone.m_strIncludeCols = m_strIncludeCols;
    classClone.m_strExcludeCols = m_strExcludeCols;
    classClone.m_strPrimaryKeyCols = m_strPrimaryKeyCols;

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

    VwTableSpec objToTest = (VwTableSpec)objTest;

    if ( ! doObjectEqualsTest( m_strName, objToTest.m_strName ) )
      return false; 

    if ( ! doObjectEqualsTest( m_strIncludeCols, objToTest.m_strIncludeCols ) )
      return false; 

    if ( ! doObjectEqualsTest( m_strExcludeCols, objToTest.m_strExcludeCols ) )
      return false; 

    if ( ! doObjectEqualsTest( m_strPrimaryKeyCols, objToTest.m_strPrimaryKeyCols ) )
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

} // *** End of class VwTableSpec{}

// *** End Of VwTableSpec.java