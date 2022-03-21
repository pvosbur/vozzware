/*
===========================================================================================

 
                               Copyright(c) 2000 - 2006 by

                      V o z z W a r e   L L C (Vw)

                                 All Rights Reserved

Source Name: VwTableHeadrClickedEvent.java

Create Date: May 17, 2006

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.


============================================================================================
*/
package com.vozzware.ui;

import java.awt.event.MouseEvent;

public class VwTableHeaderClickedEvent
{
  private MouseEvent m_me;
  private Object     m_objSource;
  private String     m_strColumnName;
  
  /**
   * Constructor
   * @param objSource       The object invoking this event
   * @param strColumnName   The name of the column clicked
   */
  public VwTableHeaderClickedEvent( MouseEvent me, Object objSource, String strColumnName )
  {
    m_me = me;
    m_objSource = objSource;
    m_strColumnName = strColumnName;
  }

  public MouseEvent getMouseEvent()
  { return m_me; }
  
  
  public Object getSource()
  { return m_objSource;  }

  public String getColumnName()
  { return m_strColumnName; }

  
} // end class VwTableHeadrClickedEvent()

// *** End of VwTableHeadrClickedEvent.java
