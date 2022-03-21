/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwTable.java

============================================================================================
*/

package com.vozzware.components;

public class VwContentChangedEvent
{
  private Object    m_objSrc;             // Object that invoked the event

  private Object    m_objChangedContent;  // the new content

  public VwContentChangedEvent( Object objSrc, Object objChangedContent )
  {
    m_objSrc = objSrc;
    m_objChangedContent = objChangedContent;

  } // end VwContentChangedEvent()


  /**
   * The Object that invoked this event
   * @return  The Object that invoked this event
   */
  public Object getObjSrc()
  {
    return m_objSrc;
  }

  /**
   * The changed data
   * @return The changed data
   */
  public Object getObjChangedContent()
  {
    return m_objChangedContent;
  }

} // end class VwContentChangedEvent{}

// *** End of VwContentChangedEvent.java ***

