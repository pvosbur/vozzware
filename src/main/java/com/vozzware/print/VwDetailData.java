/*
===========================================================================================

 
                               Copyright(c) 2000 - 2006 by

                      V o z z W a r e   L L C (Vw)

                                 All Rights Reserved

Source Name: VwDetailData.java

Create Date: Sep 5, 2006

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.


============================================================================================
*/
package com.vozzware.print;

/**
 * @author P. VosBurgh
 *
 */
public class VwDetailData
{
  private Object    m_objData;  // The Java DVO representing one detial line
  private String    m_strId;    // The id of the detial reportLine attribute defined in the reportspec xml document
  
  
  /**
   * Constructor
   * 
   * @param objData The Java Data Value object (DVO) representing a detail line
   * @param strId The id of the detial line as defined in the reportLine element in the reportspec xml doc
   */
  public VwDetailData( Object objData, String strId )
  {
    m_objData = objData;
    m_strId  = strId;
  }
  
  public Object getData()
  {return m_objData; }
  
  
  public String getId()
  { return m_strId; }
  
  
} // end VwDetailData()

// *** End of VwDetailData.java ***

