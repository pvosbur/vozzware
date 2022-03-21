/*
===========================================================================================

 
                               Copyright(c) 2000 - 2006 by

                      V o z z W a r e   L L C (Vw)

                                 All Rights Reserved

Source Name: VwInsertEvent.java

Create Date: Jul 11, 2006

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.


============================================================================================
*/
package com.vozzware.ui;

import java.awt.event.ActionEvent;

public class VwInsertEvent extends ActionEvent
{
  private String    m_strInsertText;
  private int       m_nPropagationCount = 0;
  private int       m_nCursorPos = -1;
  
  /**
   * 
   * @param objSrc
   * @param strInsertText The text to insert at current curor position
   */
  public VwInsertEvent( Object objSrc, String strInsertText )
  {
    super( objSrc, 9999, "insert");
    m_strInsertText = strInsertText;
  }

  /**
   * 
   * @param objSrc
   * @param strInsertText The text to insert at current curor position
   */
  public VwInsertEvent( Object objSrc, String strInsertText, int nCursorPos )
  {
    super( objSrc, 9999, "insert");
    m_strInsertText = strInsertText;
    m_nCursorPos = nCursorPos;
    
  }

  /**
   * 
   * @param objSrc
   * @param strInsertText
   * @param nPropagationCount
   */
  public VwInsertEvent( Object objSrc, String strInsertText, int nPropagationCount, int nCursorPos )
  {
    super( objSrc, 9999, "insert");
    m_strInsertText = strInsertText;
    m_nPropagationCount = nPropagationCount;
    m_nCursorPos = nCursorPos;
  }
  
  public String getText()
  { return m_strInsertText; }
  
  public int getPropagationCount()
  { return m_nPropagationCount; }
  
  public int getCursorPos()
  { return m_nCursorPos; }
  
} // end class VwInsertEvent{}

// *** End of VwInsertEvent.java ***

