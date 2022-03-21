/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwContentSelectionListener.java

============================================================================================
*/

package com.vozzware.components;


/**
 * Content viewers implement this interface. The VwContentSelectedEvenet contains
 * the object to view / modify.
 */
public interface IVwContentSelectionListener
{

  /**
   * This method is invoked by the IVwContProducer whenever the user selects content.
   * The object to be viewed is sent in the event.
   *
   * @param event The event containing the IVwContentProducer object and the Object
   * content to be viewed.
   */
  public void contentSelected( VwContentSelectedEvent event  );



} // end interface IVwContentSelectionListener{}

// *** End IVwContentSelectionListener.java ***
