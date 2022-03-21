/*
 ============================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

  THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
  PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
  CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

  Source Name:   VwDbImpExpListener.java


 ============================================================================
*/


package com.vozzware.db;                         // Our package

/**
 * This is the InportExport listener interface for receiving inport export events
 */
public interface VwDbImpExpListener
{

  /**
   * This method is sent when an inport/export action occurs. The VwInpExpEvent
   * describes the action
   *
   * @param VwDbImpExpEvent The event object describing the inport/export event
   */
  public abstract void inportExportAction( VwDbImpExpEvent inpExpEvent );

} // end interface VwDbImpExpListener{}

// *** End of VwDbImpExpListener.java ***
