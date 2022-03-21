/*
===========================================================================================

 
                               Copyright(c) 2000 - 2006 by

                      V o z z W a r e   L L C (Vw)

                                 All Rights Reserved

Source Name: VwTableModelChangedEvent.java

Create Date: Nov 6, 2006

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.


============================================================================================
*/
package com.vozzware.ui;

import java.util.List;

public class VwTableModelChangedEvent
{
  private Object                  m_objSource;
  private List<VwTableColAttr>   m_listTableColAttrs;
  
  
  /**
   * Constructor
   * @param objSource The source table data m_btModel
   * @param listTableColAttrs The List of VwTableColAttr objects
   */
  public VwTableModelChangedEvent( Object objSource, List<VwTableColAttr> listTableColAttrs )
  {
    m_objSource = objSource;
    m_listTableColAttrs  = listTableColAttrs ;
    
  } // end VwTableModelChangedEvent()


  /**
   * Gets the List if VwTableColAttr objects
   * @return the List if VwTableColAttr objects
   */
  public List<VwTableColAttr> getTableColAttrs()
  { return m_listTableColAttrs; }


  /**
   * Get the VwTableDataModel derived class that fired this event
   * @return the VwTableDataModel derived class that fired this event
   */
  public Object getobjSource()
  { return m_objSource; }


   
} // end class VwTableModelChangedEvent{}

// end of VwTableModelChangedEvent.java ***

