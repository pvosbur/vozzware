/*
===========================================================================================

 
                               Copyright(c) 2000 - 2006 by

                      V o z z W a r e   L L C (Vw)

                                 All Rights Reserved

Source Name: VwTableModelChangeListener.java

Create Date: Nov 6, 2006

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.


============================================================================================
*/
package com.vozzware.ui;

/**
 * This class is the listener for the regietered table m_btModel change listeners.
 * The event is fired when the structure of the m_btModel changes. i.e. different colum attributes specified.
 * @author P. VosBurgh
 *
 */
public interface VwTableModelChangeListener
{
  public void columnAttributesChanged( VwTableModelChangedEvent tmce );
  
} // end interface VwTableModelChangeListener()

// *** end of VwTableModelChangeListener.java ***

