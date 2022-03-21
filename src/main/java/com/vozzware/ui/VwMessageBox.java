/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwMessageBox.java

Create Date: Apr 11, 2003
============================================================================================
*/

package com.vozzware.ui;             // This package

import java.awt.Frame;

public class VwMessageBox extends VwMessageBoxBase
{
  // *** Class Constructor ***

  public VwMessageBox( Frame parent, String strTitle, String strMsg )
  {
    super(  parent, strTitle, strMsg, INFO, true );

  } // end VwMessageBox()


} // end class VwMessageBox {}

// *** End of VwMessageBox.java ***
