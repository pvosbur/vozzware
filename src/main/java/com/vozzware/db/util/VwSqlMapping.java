/*
============================================================================================

                       V o z z W o r k s   C o d e   G e n e r a t o r                      

                               2009 by V o z z W a r e   L L C                              

    Source File Name: VwSqlMapping.java

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


public class VwSqlMapping extends VwDVOBase implements Serializable, Cloneable
{

  private String                 m_strId;                        
  private String                 m_strClassName;                 
  private String                 m_strInheritClassName;          
  private String                 m_strImplementsClassName;       
  private List<VwPrimaryKeyGeneration>  m_listPrimaryKeyGeneration;     
  private List<VwKeyDescriptor>  m_listForeignKey;               
  private VwExtendsDescriptor    m_extendsClass;                 
  private List<VwKeyDescriptor>  m_listPrimaryKeySupplier;       
  private String                 m_strInsert;                    
  private VwSqlStatement         m_updateBy;                     
  private VwSqlStatement         m_deleteBy;                     
  private VwSqlStatement         m_findBy;                       
  private VwSqlStatement         m_exists;                       
  private VwSqlStatement         m_timestampCheck;               
  private VwSqlStatement         m_query;                        
  private VwSqlStatement         m_proc;                         

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
   * Sets the inheritClassName property
   */
  public void setInheritClassName( String strInheritClassName )
  { 
    
    testDirty( "inheritClassName", strInheritClassName );
    m_strInheritClassName = strInheritClassName;
  }

  /**
   * Gets inheritClassName property
   * 
   * @return  The inheritClassName property
   */
  public String getInheritClassName()
  { return m_strInheritClassName; }

  /**
   * Sets the implementsClassName property
   */
  public void setImplementsClassName( String strImplementsClassName )
  { 
    
    testDirty( "implementsClassName", strImplementsClassName );
    m_strImplementsClassName = strImplementsClassName;
  }

  /**
   * Gets implementsClassName property
   * 
   * @return  The implementsClassName property
   */
  public String getImplementsClassName()
  { return m_strImplementsClassName; }

  /**
   * Sets the primaryKeyGeneration property
   */
  public void setPrimaryKeyGeneration( List<VwPrimaryKeyGeneration> listPrimaryKeyGeneration )
  { 
    
    testDirty( "primaryKeyGeneration", listPrimaryKeyGeneration );
    m_listPrimaryKeyGeneration = listPrimaryKeyGeneration;
  }

  /**
   * Gets primaryKeyGeneration property
   * 
   * @return  The primaryKeyGeneration property
   */
  public List<VwPrimaryKeyGeneration> getPrimaryKeyGeneration()
  { return m_listPrimaryKeyGeneration; }

  /**
   * Sets the foreignKey property
   */
  public void setForeignKey( List<VwKeyDescriptor> listForeignKey )
  { 
    
    testDirty( "foreignKey", listForeignKey );
    m_listForeignKey = listForeignKey;
  }

  /**
   * Gets foreignKey property
   * 
   * @return  The foreignKey property
   */
  public List<VwKeyDescriptor> getForeignKey()
  { return m_listForeignKey; }

  /**
   * Sets the extendsClass property
   */
  public void setExtendsClass( VwExtendsDescriptor extendsClass )
  { 
    
    testDirty( "extendsClass", extendsClass );
    m_extendsClass = extendsClass;
  }

  /**
   * Gets extendsClass property
   * 
   * @return  The extendsClass property
   */
  public VwExtendsDescriptor getExtendsClass()
  { return m_extendsClass; }

  /**
   * Sets the primaryKeySupplier property
   */
  public void setPrimaryKeySupplier( List<VwKeyDescriptor> listPrimaryKeySupplier )
  { 
    
    testDirty( "primaryKeySupplier", listPrimaryKeySupplier );
    m_listPrimaryKeySupplier = listPrimaryKeySupplier;
  }

  /**
   * Gets primaryKeySupplier property
   * 
   * @return  The primaryKeySupplier property
   */
  public List<VwKeyDescriptor> getPrimaryKeySupplier()
  { return m_listPrimaryKeySupplier; }

  /**
   * Sets the insert property
   */
  public void setInsert( String strInsert )
  { 
    
    testDirty( "insert", strInsert );
    m_strInsert = strInsert;
  }

  /**
   * Gets insert property
   * 
   * @return  The insert property
   */
  public String getInsert()
  { return m_strInsert; }

  /**
   * Sets the updateBy property
   */
  public void setUpdateBy( VwSqlStatement updateBy )
  { 
    
    testDirty( "updateBy", updateBy );
    m_updateBy = updateBy;
  }

  /**
   * Gets updateBy property
   * 
   * @return  The updateBy property
   */
  public VwSqlStatement getUpdateBy()
  { return m_updateBy; }

  /**
   * Sets the deleteBy property
   */
  public void setDeleteBy( VwSqlStatement deleteBy )
  { 
    
    testDirty( "deleteBy", deleteBy );
    m_deleteBy = deleteBy;
  }

  /**
   * Gets deleteBy property
   * 
   * @return  The deleteBy property
   */
  public VwSqlStatement getDeleteBy()
  { return m_deleteBy; }

  /**
   * Sets the findBy property
   */
  public void setFindBy( VwSqlStatement findBy )
  { 
    
    testDirty( "findBy", findBy );
    m_findBy = findBy;
  }

  /**
   * Gets findBy property
   * 
   * @return  The findBy property
   */
  public VwSqlStatement getFindBy()
  { return m_findBy; }

  /**
   * Sets the exists property
   */
  public void setExists( VwSqlStatement exists )
  { 
    
    testDirty( "exists", exists );
    m_exists = exists;
  }

  /**
   * Gets exists property
   * 
   * @return  The exists property
   */
  public VwSqlStatement getExists()
  { return m_exists; }

  /**
   * Sets the timestampCheck property
   */
  public void setTimestampCheck( VwSqlStatement timestampCheck )
  { 
    
    testDirty( "timestampCheck", timestampCheck );
    m_timestampCheck = timestampCheck;
  }

  /**
   * Gets timestampCheck property
   * 
   * @return  The timestampCheck property
   */
  public VwSqlStatement getTimestampCheck()
  { return m_timestampCheck; }

  /**
   * Sets the query property
   */
  public void setQuery( VwSqlStatement query )
  { 
    
    testDirty( "query", query );
    m_query = query;
  }

  /**
   * Gets query property
   * 
   * @return  The query property
   */
  public VwSqlStatement getQuery()
  { return m_query; }

  /**
   * Sets the proc property
   */
  public void setProc( VwSqlStatement proc )
  { 
    
    testDirty( "proc", proc );
    m_proc = proc;
  }

  /**
   * Gets proc property
   * 
   * @return  The proc property
   */
  public VwSqlStatement getProc()
  { return m_proc; }

  /**
   * Clones this object
   *
   */
  public Object clone()
  {
    VwSqlMapping classClone = new VwSqlMapping();
    
    classClone.m_strId = m_strId;
    classClone.m_strClassName = m_strClassName;
    classClone.m_strInheritClassName = m_strInheritClassName;
    classClone.m_strImplementsClassName = m_strImplementsClassName;

    if ( m_listPrimaryKeyGeneration  != null )
      classClone.m_listPrimaryKeyGeneration = (List<VwPrimaryKeyGeneration>)cloneList( m_listPrimaryKeyGeneration );

    if ( m_listForeignKey  != null )
      classClone.m_listForeignKey = (List<VwKeyDescriptor>)cloneList( m_listForeignKey );

    if ( m_extendsClass  != null )
      classClone.m_extendsClass = (VwExtendsDescriptor)m_extendsClass.clone();

    if ( m_listPrimaryKeySupplier  != null )
      classClone.m_listPrimaryKeySupplier = (List<VwKeyDescriptor>)cloneList( m_listPrimaryKeySupplier );
    classClone.m_strInsert = m_strInsert;

    if ( m_updateBy  != null )
      classClone.m_updateBy = (VwSqlStatement)m_updateBy.clone();

    if ( m_deleteBy  != null )
      classClone.m_deleteBy = (VwSqlStatement)m_deleteBy.clone();

    if ( m_findBy  != null )
      classClone.m_findBy = (VwSqlStatement)m_findBy.clone();

    if ( m_exists  != null )
      classClone.m_exists = (VwSqlStatement)m_exists.clone();

    if ( m_timestampCheck  != null )
      classClone.m_timestampCheck = (VwSqlStatement)m_timestampCheck.clone();

    if ( m_query  != null )
      classClone.m_query = (VwSqlStatement)m_query.clone();

    if ( m_proc  != null )
      classClone.m_proc = (VwSqlStatement)m_proc.clone();

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

    VwSqlMapping objToTest = (VwSqlMapping)objTest;

    if ( ! doObjectEqualsTest( m_strId, objToTest.m_strId ) )
      return false; 

    if ( ! doObjectEqualsTest( m_strClassName, objToTest.m_strClassName ) )
      return false; 

    if ( ! doObjectEqualsTest( m_strInheritClassName, objToTest.m_strInheritClassName ) )
      return false; 

    if ( ! doObjectEqualsTest( m_strImplementsClassName, objToTest.m_strImplementsClassName ) )
      return false; 

    if ( ! doListElementTest( m_listPrimaryKeyGeneration, objToTest.m_listPrimaryKeyGeneration ) )
      return false;

    if ( ! doListElementTest( m_listForeignKey, objToTest.m_listForeignKey ) )
      return false;

    if ( ! doObjectEqualsTest( m_extendsClass, objToTest.m_extendsClass ) )
      return false; 

    if ( ! doListElementTest( m_listPrimaryKeySupplier, objToTest.m_listPrimaryKeySupplier ) )
      return false;

    if ( ! doObjectEqualsTest( m_strInsert, objToTest.m_strInsert ) )
      return false; 

    if ( ! doObjectEqualsTest( m_updateBy, objToTest.m_updateBy ) )
      return false; 

    if ( ! doObjectEqualsTest( m_deleteBy, objToTest.m_deleteBy ) )
      return false; 

    if ( ! doObjectEqualsTest( m_findBy, objToTest.m_findBy ) )
      return false; 

    if ( ! doObjectEqualsTest( m_exists, objToTest.m_exists ) )
      return false; 

    if ( ! doObjectEqualsTest( m_timestampCheck, objToTest.m_timestampCheck ) )
      return false; 

    if ( ! doObjectEqualsTest( m_query, objToTest.m_query ) )
      return false; 

    if ( ! doObjectEqualsTest( m_proc, objToTest.m_proc ) )
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

} // *** End of class VwSqlMapping{}

// *** End Of VwSqlMapping.java