/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwDataChangedEvent.java

Create Date: Apr 11, 2003
============================================================================================
*/

package com.vozzware.ui;

import com.vozzware.xml.VwDataObject;

import java.util.Enumeration;
import java.util.Vector;

/**
 * This class contains
 */
public class VwDataChangedEvent
{
  /**
   * Constant representing the Data Cleared event type.
   */
  public static final int DATA_CLEARED = 0;


  /**
   * Constant representing the Data Changed event type.
   */
  public static final int DATA_CHANGED = 1;


  private int         m_nEventType = DATA_CHANGED;  // Event type constant for this event

  private Vector      m_vecDataObjects;             // Vector of VwDataObjects representing
                                                    // the changed data
  private VwServiceable m_eventSource;          // Source Object issuing this event

  /**
   * Constructor
   *
   * @param nEventType The event type DATA_CHANGED or DATA_CLEARED. Event types of
   * DATA_CHANGED will always return an Enumeration of VwDataObject(s) that represent
   * the changed data. Objects that have the ability to represent multiple selected
   * data items like grids and multiple selection listboxes may have more than one
   * VwDataObject associated with this event. Usually one VwDataObject represents
   * the data changed event.
   * @param eventSource The source object that issued this event
   */
  public VwDataChangedEvent( int nEventType, VwServiceable eventSource )
  {
    m_eventSource = eventSource;

    if ( nEventType == DATA_CHANGED )
      m_vecDataObjects = new Vector();
    else
       m_vecDataObjects = null;

    m_nEventType = nEventType;

  } // end Constructor()


  /**
   * Adss an VwDataObject to the data changed list
   *
   * @param dataObj The VwDataObject containing the changed data
   */
  public void add( VwDataObject dataObj )
  { m_vecDataObjects.addElement( dataObj ); }


  /**
   * Return the event VwServiceable source object (The one that issued this event)
   */
  public VwServiceable getSource()
  { return m_eventSource; }


  /**
   * Returns an Enumeration of the VwDataObjects representing the data changed event
   *
   * @return an Enumeration of the VwDataObjects representing the data changed event
   */
  public Enumeration getChangedData()
  {

    class DataObjEnum implements Enumeration
    {
      private int         m_curDataObjNdx = -1;         // Current DataObject index for vector

      DataObjEnum()
      {
         m_curDataObjNdx = -1;
      }


      public boolean hasMoreElements()
      {
        if ( m_vecDataObjects == null )
          return false;

        if ( (m_curDataObjNdx + 1)< m_vecDataObjects.size() )
        {
          ++m_curDataObjNdx;
          return true;
        }

        return false;

      }

      public Object nextElement()
      {
        if ( m_vecDataObjects == null )
          return null;

       return m_vecDataObjects.elementAt( m_curDataObjNdx );

      }


    } // end class DataObjEnum

    return new DataObjEnum();

  } // end getChangedData()

} // end class VwDataChangedEvent{}

// *** End of VwDataChangedEvent.java ***

