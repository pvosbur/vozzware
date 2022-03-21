/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwContentSelectedEvent.java

============================================================================================
*/

package com.vozzware.components;

public class VwContentSelectedEvent
{
  private Object    m_objSource;        // Object invoking the event

  private Object    m_objSelectionData; // The data to be shown

  public VwContentSelectedEvent( Object objSource, Object objSelectionData )
  {
    m_objSource = objSource;
    m_objSelectionData = objSelectionData;

  } // end VwContentSelectedEvent()


  /**
   * Get the invoking source object ( The content producer )
   *
   * @return The souce object that invoked this event
   */
  public Object getSource()
  {
    return m_objSource;
  }

  /**
   * Get the content data to view
   *
   * @return  the selected content data from the content producer
   */
  public Object getSelectionData()
  {
    return m_objSelectionData;
  }


} // end class VwContentSelectedEvent{}

// *** End of VwContentSelectedEvent.java ***

