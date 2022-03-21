/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwPickListSelectionListener.java

============================================================================================
*/

package com.vozzware.components;

public interface IVwPickListSelectionListener
{

  /**
   * Sent by an VwPickList object when an item has been selected/deselected
   * @param event The event describing the change for item affected
   */
  public void selectionStateChanged( VwPickListSelectionEvent event );

} // end interface IVwPickListSelectionListener{}

// *** End of IVwPickListSelectionListener.java ***

