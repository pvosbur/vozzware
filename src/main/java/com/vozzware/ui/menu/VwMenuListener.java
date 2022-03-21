/*
===========================================================================================

 
                               Copyright(c) 2000 - 2006 by

                      V o z z W a r e   L L C (Vw)

                                 All Rights Reserved

Source Name: VwMenuListener.java

Create Date: May 26, 2006

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.


============================================================================================
*/
package com.vozzware.ui.menu;

import com.vozzware.ui.VwActionEvent;

public interface VwMenuListener
{
  public void init( VwActionEvent ae );

  public void menuItemSelected( VwActionEvent ae );
  
} // end interface {}

// *** End of VwMenuListener.java ***

