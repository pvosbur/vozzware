/**
* VwQueue.java
*
*
*/

package com.vozzware.util;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * 
 * @author petervosburghjr
 *
 */
public class VwQueue<T>
{
  private List<T>  m_listQueue = Collections.synchronizedList( new LinkedList<T>() );
  
  
  /**
   * Return the first element in the queue or null if the queue is empty
   * @return
   */
  public synchronized T peek()
  { 
    if ( m_listQueue.size() == 0 )
      return null;      // Empty queue
    
    return m_listQueue.get( 0  );
    
  } // end peek()
  
  
  /**
   * Returns the nbr of elements in the queue
   * @return
   */
  public int size()
  { return m_listQueue.size(); }
  
  
  /**
   * Returns true if object is the the queue
   * @param gtObj
   * @return
   */
  public boolean contains( T gtObj )
  { return m_listQueue.contains( gtObj ); }
  
  
  /**
   * Adds an object of type generic type T to the queue 
   * @param gtObj
   */
  public synchronized void add( T gtObj )
  { 
    m_listQueue.add( gtObj ); 
    notifyAll();
    
  }
  
 
  /**
   * Proivdes a thread blocking getNext(). If no elements are in the queue this method
   * will block until and entry is added or the lWaitTime is reached
   * @param lWaitTime The max time in millisencds to wait for an entry to be added to the queue
   * @return
   * @throws Exception If the wait time is reached
   */
  public synchronized T getNext( long lWaitTime ) throws Exception
  {
    if ( m_listQueue.size() == 0 )
    {
      wait( lWaitTime );
    }
    return getNext();
    
  }

  /**
   * Gets the next element from the queue or null if the queue is empty
   * @return
   */
  public T getNext()
  {
    if ( m_listQueue.size() == 0 )
      return null;
    
    T gtElement = m_listQueue.get( 0 );
    
    m_listQueue.remove( 0 );
    
    return gtElement;
    
  } // end getNext
  
  
  /**
   * Removes the object (it it exists) from the queue
   * @param gtObject The object to remove
   */
  public void remove( T gtObject )
  { m_listQueue.remove( gtObject ); }

  /**
   * Removes the object at the specified index
   * @param ndx the queue entry number to remove
   */
  public void remove( int ndx )
  { m_listQueue.remove( ndx ); }

  /**
   * Removes all elements form the queue
   */
  public void clear()
  { m_listQueue.clear(); }
  
  
  public String toString()
  { return m_listQueue.toString(); }
    

  /**
   * Returns the List of entries in the queue
   * @return
   */
  public List<T>getAll()
  { return m_listQueue; }
  
} // end class VwQueue{}

// *** end VwQueue.java ***

