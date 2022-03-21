/*
===========================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

  THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
  PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
  CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwDVOBase.java

Create Date: Mar 28, 2005
============================================================================================
*/
package com.vozzware.db;

import com.vozzware.util.NoIntrospect;
import com.vozzware.util.VwBeanUtils;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author P. VosBurgh
 *
 * This is the super class for DVO objects that want to have dirty detection support.
 * <br>This class implements the concept of 'Smart setters' which will mark the object dirty if
 * <br>any any set method alters the the value from its previous value. The super class also maintains
 * <br>a list of properties that are dirty. The isDirty property applies only to the specific object that calls it.
 * <br>The isGraphDirty follows all object references in the graph and returns true if any object in the graph is dirty.
 * 
 */
public class VwDVOBase implements Serializable 
{
  private Map<String,String>	m_mapDirtyFields = new HashMap<String,String>();
  private List<VwDVOBase>    m_listMarkedObjects;
  
  private boolean             m_fMarkForDelete = false;
  
  /**
   * Sets the mark for delete flag. This is only a convenience flag
   * and does not cause a recursive operation on the VwSqlMgr's delete method 
   * @param fMarkForDelete
   */
  public void setMarkForDelete( boolean fMarkForDelete )
  { m_fMarkForDelete = fMarkForDelete; }
  
  /**
   * Returns the state of the marked for delete flag
   * @return
   */
  public boolean isMarkedForDelete()
  { return m_fMarkForDelete; }
  
  /**
   * Returns true if the object is dirty
   * 
   * @return true if the object is dirty, false otherwise
   */
  public boolean isDirty()
  { return m_mapDirtyFields.size() > 0; }


  /**
   * Returns true if the property is dirty
   * 
   * @param strPropName The name of the property to test
   * @return true if the object the specific property is dirty
   */
  public boolean isDirty( String strPropName )
  { return m_mapDirtyFields.containsKey( strPropName ); }
  
  
  /**
   * Traverses the entire object graph from the current object instance to determine if any object in the graph is dirty
   * @return true if any object in the graph is dirty
   */
  public boolean isGraphDirty()
  {
    m_listMarkedObjects = null;
    
    return testGraph( this, true, false );
    
  }

  /**
   * Performs the isGraphDirty but returns the objects that are dirty or null if all objects are clean
   * @return
   */
  @NoIntrospect
  public List<VwDVOBase>getDirtyObjects()
  {
    m_listMarkedObjects = new ArrayList<VwDVOBase>();
    testGraph( this, true, false );
    
    if ( m_listMarkedObjects.size() == 0 )
    {
      return null;
    }
    
    return m_listMarkedObjects;
    
  }

  
  /**
   * Traverses the entire object graph from the current object instance to determine if any object in the graph
   * <br>that has the mark for delete flag set
   * 
   * @return true if any object in the graph has the mark for delete flag set 
   */
  public boolean isGraphHasMarkedDeleteObjects()
  {
    m_listMarkedObjects = null;
    return testGraph( this, false, true );
    
  }

  /**
   * Performs the isGraphHasMarkedDeleteObject but returns the objects that are marked for deletion or null if 
   * <br>none are marked
   * @return
   */
  @NoIntrospect
  public List<VwDVOBase>getMarkedForDeleteObjects()
  {
    m_listMarkedObjects = new ArrayList<VwDVOBase>();
    testGraph( this, false, true );
    
    if ( m_listMarkedObjects.size() == 0 )
    {
      return null;
    }
    
    return m_listMarkedObjects;
    
  }
 
  
  /**
   * Test's the object for the flag to test
   * 
   * @param objToTest The object to test 
   * @param fTestDirty if true test object for dirty flag
   * @param fTestDelete if true test object for mark for delete flag
   * 
   * @return true if either of the testing parameters are true
   */
  private boolean testGraph( Object objToTest, boolean fTestDirty, boolean fTestDelete )
  {
    if ( objToTest == null )
    {
      return false;
    }
    
    if ( !(objToTest instanceof VwDVOBase ) )
    {
      return false;
    }
    
    if ( fTestDirty && ((VwDVOBase)objToTest).isDirty() )
    {
      if ( m_listMarkedObjects == null )
      {
        return true;
      }
      
      m_listMarkedObjects.add( (VwDVOBase )objToTest );
        
    }  

    if ( fTestDelete && ((VwDVOBase)objToTest).isMarkedForDelete() )
    {
      if ( m_listMarkedObjects == null )
      {
        return true;
      }
     
     m_listMarkedObjects.add( (VwDVOBase )objToTest );
        
    }  
    try
    {
      List<PropertyDescriptor> listProps = VwBeanUtils.getReadProperties( objToTest.getClass() );
    
      for ( PropertyDescriptor pd : listProps )
      {
        Method mthdRead = pd.getReadMethod();
        if ( mthdRead.getDeclaringClass() == VwDVOBase.class )
        {
          continue;
        }
        
        if ( VwBeanUtils.isSimpleType( mthdRead.getReturnType() ))  
        {
          continue;
        }
        
       if ( VwBeanUtils.isCollectionType( mthdRead.getReturnType() ))
       {
          if ( doCollectionDirtyCheck( objToTest, mthdRead, fTestDirty, fTestDelete ) )
          {
            if  ( m_listMarkedObjects == null )
            {
              return true;
            }
          }
       }
       else
       {
         Object objReturn = mthdRead.invoke( objToTest, null );
         if ( testGraph( objReturn, fTestDirty, fTestDelete ) )
         {
           if ( m_listMarkedObjects == null )
           {
             return true;
           }
         }
       }
         
       continue;
       
      }
     }
    catch( Exception ex )
    {
      ex.printStackTrace();
    }

    return false;
  }

  /**
   * Clears the dirty flag for this property
   * @param strPropName
   */
  public void clearDirty( String strPropName )
  {
    m_mapDirtyFields.remove( strPropName );

  }

  /**
   * Resets all objects in the graph to clean
   */
  public void clearGraphDirty()
  {
    clearGraphDirty( this );
  }

  /**
   * clears object for dirtiness
   * @param objToClear The VwDVO object to to clear dirty flags
   * @return
   */
  private void clearGraphDirty( Object objToClear )
  {
    if ( objToClear == null )
    {
      return;
    }
    
    if ( !(objToClear instanceof VwDVOBase ) )
    {
      return;
    }
    
     ((VwDVOBase)objToClear).setDirty( false );
      
    try
    {
      List<PropertyDescriptor> listProps = VwBeanUtils.getReadProperties( objToClear.getClass() );
    
      for ( PropertyDescriptor pd : listProps )
      {
        Method mthdRead = pd.getReadMethod();
        if ( mthdRead.getDeclaringClass() == VwDVOBase.class )
        {
          continue;
        }
          
        if ( VwBeanUtils.isSimpleType( mthdRead.getReturnType() ))  
        {
          continue;
        }
        
       if ( VwBeanUtils.isCollectionType( mthdRead.getReturnType() ))
         doCollectionClear( objToClear, mthdRead, true, false );
       else
       {
          Object objReturn = mthdRead.invoke( objToClear, null );
          clearGraphDirty( objReturn );
       }
         
      }
     }
    catch( Exception ex )
    {
      ex.printStackTrace();
    }
   
  }


  /**
   * Clears the entire graph of any objects that have the mark for delete flag set
   */
  public void clearGraphDelete()
  { clearGraphDelete(  this ); }
    

  /**
   * Clear's the graph of any objects marked for deletion
   * @param objToClear The starting point in the graph
   * @return
   */
  private void clearGraphDelete( Object objToClear )
  {
    if ( objToClear == null )
    {
      return;
    }
    
    if ( ! (objToClear instanceof VwDVOBase) )
    {
      return;
    }
    
     ((VwDVOBase)objToClear).setMarkForDelete( false );
      
    try
    {
      List<PropertyDescriptor> listProps = VwBeanUtils.getReadProperties( objToClear.getClass() );
    
      for ( PropertyDescriptor pd : listProps )
      {
        Method mthdRead = pd.getReadMethod();
        if ( mthdRead.getDeclaringClass() == VwDVOBase.class )
        {
          continue;
        }
        
        if ( VwBeanUtils.isSimpleType( mthdRead.getReturnType() ))  
        {
          continue;
        }
        
       if ( VwBeanUtils.isCollectionType( mthdRead.getReturnType() ))
         doCollectionClear( objToClear, mthdRead, false, true );
       else
       {
          Object objReturn = mthdRead.invoke( objToClear, null );
          clearGraphDelete( objReturn );
       }
         
      }
     }
    catch( Exception ex )
    {
      ex.printStackTrace();
    }
   
  }


  /**
   * Traverse objects in a collection and perform flag tests on each
   * 
   * @param objToTest The object that contains the collection
   * @param mthdCollection The collection getter
   * @param fTestDirty if true test object for dirty flag
   * @param fTestDelete if true test object for mark for delete flag
   * 
   * @return
   * @throws Exception
   */
  private boolean doCollectionDirtyCheck( Object objToTest, Method mthdCollection, boolean fTestDirty, boolean fTestDelete ) throws Exception
  {
    
    Object objCollection = mthdCollection.invoke( objToTest, (Object[])null );
    
    if ( objCollection == null )
    {
      return false;
    }
    
    if ( objCollection instanceof Collection )
    {
      return doCollectionDirtyTest( (Collection<?>)objCollection, fTestDirty, fTestDelete );
    }
    else
    if ( objCollection instanceof Map )
    {
      return doCollectionDirtyTest( ((Map<?,?>)objCollection).values(), fTestDirty, fTestDelete );
    }
    else
    if ( objCollection.getClass().isArray() )
    {
      return doArrrayCollectionTest( objCollection, fTestDirty, fTestDelete );
    }
    
    return false;
  }


  /**
   * Clears the dirty flag
   * @param objToClear
   * @param mthdCollection
   * @param fClearDirty if true clear dirty flag
   * @param fClearDelete if true clear mark for delete flag
   * 
   * @throws Exception
   */
  private void doCollectionClear( Object objToClear, Method mthdCollection, boolean fClearDirty, boolean fClearDelete ) throws Exception
  {
    Object objCollection = mthdCollection.invoke( objToClear, (Object[])null );
    
    if ( objCollection == null )
    {
      return;
    }
    
    if ( objCollection instanceof Collection )
    {
      clearCollection( (Collection<?>)objCollection, fClearDirty, fClearDelete );
    }
    else
    if ( objCollection instanceof Map )
    {
      clearCollection( ((Map<?,?>)objCollection).values(), fClearDirty, fClearDelete );
    }
    else
    if ( objCollection.getClass().isArray() )
    {
      clearArray( objCollection, fClearDirty, fClearDelete  );
    }
    
  }


  
  /**
   * Test each array element for dirtiness
   * 
   * @param objArray The array object to test
   * @param fTestDirty if true test object for dirty flag
   * @param fTestDelete if true test object for mark for delete flag
   * 
   * @return
   */
  private boolean doArrrayCollectionTest( Object objArray, boolean fTestDirty, boolean fTestDelete )
  {
    int nElements = Array.getLength( objArray );
    
    for ( int x = 0; x < nElements; x++ )
    {
      Object objToTest = Array.get( objArray, x );
      
      if ( testGraph( objToTest, fTestDirty, fTestDelete ))
      {
        if ( m_listMarkedObjects == null )
        {
          return true;
        }
      }
    }
    
    return false;
  }


  /**
   * Test each array element for dirtiness
   * @param objArray The array object to test
   * @param fClearDirty if true clear dirty flag
   * @param fClearDelete if true clear mark for delete flag
   * @return
   */
  private void clearArray( Object objArray, boolean fClearDirty, boolean fClearDelete  )
  {
    int nElements = Array.getLength( objArray );
    
    for ( int x = 0; x < nElements; x++ )
    {
      Object objToClear = Array.get( objArray, x );
      
      if ( fClearDirty)
      {
        clearGraphDirty( objToClear  );
      }
      
      if ( fClearDelete)
      {
        clearGraphDelete( objToClear );
      }
    }
    
  }


  /**
   * Perform dirty test on each object in the collection
   * @param collection The collection to iterate
   * 
   * @param fTestDirty if true test object for dirty flag
   * @param fTestDelete if true test object for mark for delete flag
   * 
   * @return true if any object in the collection is dirty
   */
  private boolean doCollectionDirtyTest( Collection<?> collection, boolean fTestDirty, boolean fTestDelete )
  {
    for ( Iterator<?> iElements = collection.iterator(); iElements.hasNext(); )
    {
      Object objElement = iElements.next();
      if ( testGraph( objElement, fTestDirty, fTestDelete ))
      {
        if ( m_listMarkedObjects == null )
        {
          return true;
        }
      }
    }
    
    return false;
     
  }  // end  doCollectionDirtyTest()

  /**
   * Clears each object in the collection based on the boolean properties
   * 
   * @param collection The collection to iterate
   * @param fClearDirty if true clear dirty flag
   * @param fClearDelete if true clear mark for delete flag
   * 
   * @return true if any object in the collection is dirty
   */
  private void clearCollection( Collection<?> collection, boolean fClearDirty, boolean fClearDelete  )
  {
    for ( Iterator<?> iElements = collection.iterator(); iElements.hasNext(); )
    {
      Object objElement = iElements.next();
      
      if ( fClearDirty )
      {
        clearGraphDirty( objElement );
      }
      
      if ( fClearDelete )
      {
        clearGraphDelete( objElement );
      }
      
    }
    
     
  }  // end  doCollectionDirtyTest()

  /**
   * Sets the dirty state of this object
   * 
   * @param fDirty true to unconditionally set this object to dirty, false to clear it
   */
  public void setDirty( boolean fDirty )
  {
    if ( fDirty )
    {
      m_mapDirtyFields.put( "dirty", "dirty" );
    }
    else
    {
      m_mapDirtyFields.clear();
    }
      
  } // end setDirty()
  
  
  /**
   * Returns an iterator of strings of all the dirty properties in this object
   * @return
   */
  public Iterator<String> getDirtyFields()
  { return m_mapDirtyFields.keySet().iterator(); }
  
  /**
   * Dirty test for Java primitives
   * 
   * @param strPropName The bean property name to retrieve it's current value
   * 
   * @param bValue The byte value to test test change for
   */
  protected void testDirty( String strPropName, byte bValue )
  { testDirty( strPropName, new Byte( bValue ) ); }


  /**
   * Dirty test for Java primitives
   * 
   * @param strPropName The bean property name to retrieve it's current value
   * 
   * @param chValue The char value to test test change for
   */
  protected void testDirty( String strPropName, char chValue )
  { testDirty( strPropName, new Character( chValue ) ); }
  
    
  /**
   * Dirty test for Java primitives
   * 
   * @param strPropName The bean property name to retrieve it's current value
   * 
   * @param fValue The boolean value to test test change for
   */
  protected void testDirty( String strPropName, boolean fValue )
  { testDirty( strPropName, new Boolean( fValue ) ); }

  
  /**
   * Dirty test for Java primitives
   * 
   * @param strPropName The bean property name to retrieve it's current value
   * 
   * @param sValue The short value to test test change for
   */
  protected void testDirty( String strPropName, short sValue )
  { testDirty( strPropName, new Short( sValue ) ); }
  
  
  /**
   * Dirty test for Java primitives
   * 
   * @param strPropName The bean property name to retrieve it's current value
   * 
   * @param nValue The int value to test test change for
   */
  protected void testDirty( String strPropName, int nValue )
  { testDirty( strPropName, new Integer( nValue ) ); }
  
  
  /**
   * Dirty test for Java primitives
   * 
   * @param strPropName The bean property name to retrieve it's current value
   * 
   * @param lValue The long value to test test change for
   */
  protected void testDirty( String strPropName, long lValue )
  { testDirty( strPropName, new Long( lValue ) ); }
  
  
  /**
   * Dirty test for Java primitives
   * 
   * @param strPropName The bean property name to retrieve it's current value
   * 
   * @param fltValue The float value to test test change for
   */
  protected void testDirty( String strPropName, float fltValue )
  { testDirty( strPropName, new Float( fltValue ) ); }

  
  /**
   * Dirty test for Java primitives
   * 
   * @param strPropName The bean property name to retrieve it's current value
   * 
   * @param dblValue The double value to test test change for
   */
  protected void testDirty( String strPropName, double dblValue )
  { testDirty( strPropName, new Double( dblValue ) ); }
 
  
  /**
   * Tests a property for a value change and sets the object dirty if a new value is detected
   * 
   * @param strPropName The name of the property to test
   * 
   * @param objNewValue the value to test
   */
  protected void testDirty( String strPropName, Object objNewValue )
  {
    try
    {
      Object objCurVal = VwBeanUtils.getValue( this, strPropName );
      
      if ( objCurVal == null )
      {
        if ( objNewValue != null )
        {
          m_mapDirtyFields.put( strPropName, null );
        }
      }
      else
      {
        if ( objNewValue != null )
        {
          if ( objNewValue.getClass().isArray() )
          {
            if ( !arrayCompare( objCurVal, objNewValue ))
            {
              m_mapDirtyFields.put( strPropName, null );
            }
          }
          else
          if ( !(objCurVal.equals( objNewValue ) ) )
          {
            m_mapDirtyFields.put( strPropName, null );
          }
            
        }
        else
        {
          m_mapDirtyFields.put( strPropName, null );
        }
          
      }
    }
    catch( Exception ex )
    {
      ex.printStackTrace();
      m_mapDirtyFields.put( strPropName, null );
    }
    
    
  } // end testDirty()


  /**
   * Compare two arrays
   * @param objCurVal array 1 the current array value
   * @param objNewValue array 2 the new array value
   * @return
   */
  private boolean arrayCompare( Object objCurVal, Object objNewValue )
  {
    int nLen = Array.getLength(objCurVal );
    
    for ( int x = 0; x < nLen; x++ )
    {
      Object cur1 = Array.get( objCurVal, x );
      Object new1 = Array.get( objNewValue, x );
      
      if ( !cur1.equals( new1 ))
      {
        return false;
      }
      
      
    }
    return true;
  }
  
  
} // end class VwDVOBase{}

// *** End of VwDVOBase.java ***

