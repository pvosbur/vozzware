/*
===========================================================================================

 
                               Copyright(c) 2000 - 2006 by

                      V o z z W a r e   L L C (Vw)

                                 All Rights Reserved

Source Name: VwEditorCaretEvent.java

Create Date: Jul 17, 2006

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.


============================================================================================
*/
package com.vozzware.ui;

public class VwEditorCaretEvent
{
 private int    m_nCaretPos;
 private int    m_nRow;
 private int    m_nCol;
 
 public VwEditorCaretEvent( int nCaretPos, int nRow, int nCol )
 {
   m_nCaretPos = nCaretPos;
   m_nRow = nRow;
   m_nCol = nCol;
 }
 
 public int getPosition()
 { return m_nCaretPos; }
 
 public int getRowNbr()
 { return m_nRow; }
 
 public int getColNbr()
 { return m_nCol; }
 
}
