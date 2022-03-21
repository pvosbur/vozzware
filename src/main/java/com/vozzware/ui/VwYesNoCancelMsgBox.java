/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwYesNoCancelMsgBox.java

Create Date: Apr 11, 2003
============================================================================================
*/

package com.vozzware.ui;

import java.awt.Frame;

public class VwYesNoCancelMsgBox extends VwMessageBoxBase
{
  /**
   * Constructs an VwYesNoCancelMsgBox object as specified
   *
   * @param parent - The parent frame object for the message box
   * @param strTitle - A string with the message box title
   * @param strMsg - A string with the message for the message box
   */
  public VwYesNoCancelMsgBox( Frame parent, String strTitle, String strMsg )
  {
    super( parent, strTitle, strMsg, YES_NO_CANCEL, true );

  } // end VwYesNoCancelMsgBox()


} // end class VwYesNoCancelMsgBox{}


// *** End of VwYesNoCancelMsgBox.java ***
