/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwSTextFieldListener.java

Create Date: Apr 11, 2003
============================================================================================
*/
package com.vozzware.ui;

/**
 * The VwTextFieldListener interface defines the interface for notification when
 * an VwTextField data validation operation fails.
 */
public interface VwTextFieldListener
{
  /**
   * Sent when data in an VwTextField is loosing focus and requires validation
   *
   * @param textEvent - The VwTextField event with the failing reason
   */
  public void loosingFocus( VwTextFieldEvent textEvent );


  /**
   * Sent when an invalid character is typed into an VwTextField.  All characters
   * typed into an VwTextField are validated against the edit mask, if one is defined.
   *
   * @param textEvent - The VwTextField event with the failing reason
   */
  public void invalidCharacter( VwTextFieldEvent textEvent );


  /**
   * Sent when a previous invalidCharacter() or validationFailed() event was sent
   * to inform interested objects they can cleanup or clear any status messages
   * that may be displayed.
   *
   * @param textEvent - The VwTextField event identifying the cleared field
   */
  public void clearValidationError( VwTextFieldEvent textEvent );


} // end interface VwTextFieldListener{}

// *** End of VwTextFieldListener.java
