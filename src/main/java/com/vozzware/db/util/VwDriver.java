/*
============================================================================================

                       V o z z W o r k s   C o d e   G e n e r a t o r                      

                               2009 by V o z z W a r e   L L C                              

    Source File Name: VwDriver.java

    Author:           

    Date Generated:   10-16-2020

    Time Generated:   09:55:17

============================================================================================
*/

package com.vozzware.db.util;

import com.vozzware.db.VwDVOBase;
import com.vozzware.util.VwBeanUtils;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;


public class VwDriver extends VwDVOBase implements Serializable, Cloneable
{

  private String                 m_strId;                        
  private String                 m_strDriverClass;               
  private String                 m_strMsgXlateClass;             
  private String                 m_strDesc;                      
  private String                 m_strArchive;                   
  private String                 m_strConnectionTestTable;       
  private List<VwConnectionPool> m_listConnectionPool;           
  private List<VwUrl>            m_listUrl;                      

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
   * Sets the driverClass property
   */
  public void setDriverClass( String strDriverClass )
  { 
    
    testDirty( "driverClass", strDriverClass );
    m_strDriverClass = strDriverClass;
  }

  /**
   * Gets driverClass property
   * 
   * @return  The driverClass property
   */
  public String getDriverClass()
  { return m_strDriverClass; }

  /**
   * Sets the msgXlateClass property
   */
  public void setMsgXlateClass( String strMsgXlateClass )
  { 
    
    testDirty( "msgXlateClass", strMsgXlateClass );
    m_strMsgXlateClass = strMsgXlateClass;
  }

  /**
   * Gets msgXlateClass property
   * 
   * @return  The msgXlateClass property
   */
  public String getMsgXlateClass()
  { return m_strMsgXlateClass; }

  /**
   * Sets the desc property
   */
  public void setDesc( String strDesc )
  { 
    
    testDirty( "desc", strDesc );
    m_strDesc = strDesc;
  }

  /**
   * Gets desc property
   * 
   * @return  The desc property
   */
  public String getDesc()
  { return m_strDesc; }

  /**
   * Sets the archive property
   */
  public void setArchive( String strArchive )
  { 
    
    testDirty( "archive", strArchive );
    m_strArchive = strArchive;
  }

  /**
   * Gets archive property
   * 
   * @return  The archive property
   */
  public String getArchive()
  { return m_strArchive; }

  /**
   * Sets the connectionTestTable property
   */
  public void setConnectionTestTable( String strConnectionTestTable )
  { 
    
    testDirty( "connectionTestTable", strConnectionTestTable );
    m_strConnectionTestTable = strConnectionTestTable;
  }

  /**
   * Gets connectionTestTable property
   * 
   * @return  The connectionTestTable property
   */
  public String getConnectionTestTable()
  { return m_strConnectionTestTable; }

  /**
   * Sets the connectionPool property
   */
  public void setConnectionPool( List<VwConnectionPool> listConnectionPool )
  { 
    
    testDirty( "connectionPool", listConnectionPool );
    m_listConnectionPool = listConnectionPool;
  }

  /**
   * Gets connectionPool property
   * 
   * @return  The connectionPool property
   */
  public List<VwConnectionPool> getConnectionPool()
  { return m_listConnectionPool; }

  /**
   * Sets the url property
   */
  public void setUrl( List<VwUrl> listUrl )
  { 
    
    testDirty( "url", listUrl );
    m_listUrl = listUrl;
  }

  /**
   * Gets url property
   * 
   * @return  The url property
   */
  public List<VwUrl> getUrl()
  { return m_listUrl; }

  /**
   * Clones this object
   *
   */
  public Object clone()
  {
    VwDriver classClone = new VwDriver();
    
    classClone.m_strId = m_strId;
    classClone.m_strDriverClass = m_strDriverClass;
    classClone.m_strMsgXlateClass = m_strMsgXlateClass;
    classClone.m_strDesc = m_strDesc;
    classClone.m_strArchive = m_strArchive;
    classClone.m_strConnectionTestTable = m_strConnectionTestTable;

    if ( m_listConnectionPool  != null )
      classClone.m_listConnectionPool = (List<VwConnectionPool>)cloneList( m_listConnectionPool );

    if ( m_listUrl  != null )
      classClone.m_listUrl = (List<VwUrl>)cloneList( m_listUrl );

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

    VwDriver objToTest = (VwDriver)objTest;

    if ( ! doObjectEqualsTest( m_strId, objToTest.m_strId ) )
      return false; 

    if ( ! doObjectEqualsTest( m_strDriverClass, objToTest.m_strDriverClass ) )
      return false; 

    if ( ! doObjectEqualsTest( m_strMsgXlateClass, objToTest.m_strMsgXlateClass ) )
      return false; 

    if ( ! doObjectEqualsTest( m_strDesc, objToTest.m_strDesc ) )
      return false; 

    if ( ! doObjectEqualsTest( m_strArchive, objToTest.m_strArchive ) )
      return false; 

    if ( ! doObjectEqualsTest( m_strConnectionTestTable, objToTest.m_strConnectionTestTable ) )
      return false; 

    if ( ! doListElementTest( m_listConnectionPool, objToTest.m_listConnectionPool ) )
      return false;

    if ( ! doListElementTest( m_listUrl, objToTest.m_listUrl ) )
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

} // *** End of class VwDriver{}

// *** End Of VwDriver.java