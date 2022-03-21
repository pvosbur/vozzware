/*
===========================================================================================

 
                               Copyright(c) 2000 - 2006 by

                      V o z z W a r e   L L C (Vw)

                                 All Rights Reserved

Source Name: VwActionPanel.java

Create Date: Feb 25, 2007

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.


============================================================================================
*/
package com.vozzware.ui;

import java.awt.event.ActionEvent;

/**
 * This interface is used by panels incorporating the VwDialog shell to handle button events
 */
public interface VwActionPanel
{
  /**
   * Returns true if the dialog should be disposed following the action handle
   * @return
   */
  public boolean shouldDispose();
  
  /**
   * Button action handler
   * @param ae
   */
  public void actionPerformed( ActionEvent ae );
  
}
