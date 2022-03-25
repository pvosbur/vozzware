/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwStack.java

============================================================================================
*/


package com.vozzware.util;

import java.lang.reflect.Array;
import java.util.ResourceBundle;

/**
 * This class defines a stack data structure. This stack unlike the Jdk stack
 * has a blocking pop method that will wait until a different thread issues a push operation.
 */
public class VwStack<T>
{

  class Node
  {
    T m_objData = null;                 // Ref to the user data
    Node m_next = null;                 // Next Item (single Linked List)

  } // end class Node

  private Node m_nodeHead = null;       // Headnode of the List
  private int m_nElements = 0;          // Nbr of elements in the stack
  private Class<T>m_clsTypeParam;

  public VwStack()
  {
    ;
  }

  public VwStack( Class<T>cls )
  {
    m_clsTypeParam = cls;
  }

  /**
   * Determines if there is a data object on the stack, and, if there is, returns
   * the data object but does not remove the object from the stack.
   *
   * @return The user data object if there is an object on the stack, otherwise
   * null is returned.
   */
  public final synchronized T peek()
  {
    if ( m_nodeHead == null )                          // is there any data on stack??
    {
      return null;
    }

    return m_nodeHead.m_objData;

  } // end peek()


  /**
   * Determines if there is a data object on the stack, that is nLevel deep and, if there is, returns
   * the data object but does not remove the object from the stack.
   *
   * @return The user data object if there is an object on the stack, otherwise
   * null is returned.
   */
  public final synchronized T peek( int nLevel )
  {
    if ( m_nodeHead == null )                          // is there any data on stack??
    {
      return null;
    }

    Node nodeNext = m_nodeHead;

    // navigate down to the level requested if we can
    for ( int x = 0; x < nLevel; x++ )
    {
      nodeNext =  nodeNext.m_next;
      if ( nodeNext == null )
      {
        return null;             // cantg go any further;
      }
    }

    return nodeNext.m_objData;


  } // end peek()

  /**
   * Pops the top object off the stack
   *
   * @return The user data object at the top of the stack, which is removed from the stack;
   * null if the stack is empty.
   */
  public synchronized T pop()
  {
    Node nodeTos;                          // Top of stack

    if ( peek() != null )                   // Any data on stack?
    {
      nodeTos = m_nodeHead;                // Top of stack
      m_nodeHead = m_nodeHead.m_next;      // Reset head to next item

      m_nElements--;                       // Decrement count
      nodeTos.m_next = null;

      return nodeTos.m_objData;
    }

    return null;                         // Nothing left

  }  // end pop()


  /**
   * This version of pop() blocks until a data object is available on the stack (pushed by
   * another thread) or the maximum wait time specified has elapsed.
   *
   * @param lMaxWaitTime - The maximum time in milliseconds to wait until a data object is pushed
   * on the stack.  A value of 0 indicates an indefinite wait; use this option with caution, since
   * this method will never return if no other thread pushes an entry into the stack.
   *
   * @return The user data object at the top of the stack, which is removed from the stack;
   *
   * @exception throws an InterruptedException if the maximum wait time has elapsed, and there are
   * still no items in the stack.
   */
  public synchronized final T pop( long lMaxWaitTime ) throws Exception
  {
    T data = pop();        // Try first to see if there is data

    if ( data != null )
    {
      return data;              // There is data so return it
    }

    // No data so we block

    try
    {
      wait( lMaxWaitTime );
      // Sleep so other threads can breath
    }
    catch ( Exception i )
    {
      throw new Exception( ResourceBundle.getBundle( "resources.properties.vwutil" ).getString( "VwUtil.Stack.WaitExceeded" ) );
    }

    data = pop();

    if ( data == null )
    {
      throw new Exception( ResourceBundle.getBundle( "resources.properties.vwutil" ).getString( "VwUtil.Stack.WaitExceeded" ) );
    }

    return data;                // Return data

  } // end pop()


  /**
   * Pushes a data object onto to the stack
   *
   * @param objData - User data object to push on the stack
   */
  public synchronized void push( T objData )
  {
    Node nodeNew = new Node();           // Allocate memory from free store
    nodeNew.m_objData = objData;         // Assign the data reference

    nodeNew.m_next = m_nodeHead;         // make the head the next node
    m_nodeHead = nodeNew;                // and make the new node the head
    m_nElements++;                       // increment count
    notify();

  } // end push()


  /**
   * Returns the objects on the stack as an array starting at the top of the statck
   * @return
   */
  public T[] toArray() throws Exception
  {
    T[] aElements = null;

    if ( m_clsTypeParam != null )
    {
      aElements = (T[])Array.newInstance( m_clsTypeParam, m_nElements );
    }
    else
    {
      aElements = (T[]) new Object[ m_nElements ];
    }

    Node node = m_nodeHead;
    
    for ( int x = 0; x < m_nElements; x++ )
    {
      aElements[ x ] = node.m_objData;
      node = node.m_next;
      
    }
    
    return aElements;
    
  } // end toArray()
  
  
  /**
   * Returns the number of elements on the stack
   *
   * @return - An integer with the number of elements
   */
  public synchronized final int size()
  {
    return m_nElements;
  }


  /**
   * Remove all the data objects from the stack
   */
  public synchronized void removeAll()
  {
    Node nodeNext = null;

    while ( m_nodeHead != null )           // while there are items
    {
      nodeNext = m_nodeHead.m_next;
      m_nodeHead.m_next = null;
      m_nodeHead.m_objData = null;
      m_nodeHead = nodeNext;

    } // end while()

    m_nElements = 0;

  } // end removeAll()


  /**
   * Cleanup the stack by removing all the data objects from the stack
   */
  public void finalize()
  {
    removeAll();
  }


}  // end class VwStack{}

// *** End of VwStack.java ***
