/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwKeyEvent.java

Create Date: Apr 11, 2003
============================================================================================
*/
package com.vozzware.ui;

import java.awt.Component;
import java.awt.event.KeyEvent;

public class VwKeyEvent extends KeyEvent
{
  public VwKeyEvent(Component source,
                     int id,
                     long when,
                     int modifiers,
                     int keyCode,
                     char keyChar,
                     int keyLocation )
  { super( source, id, when, modifiers, keyCode, keyChar, keyLocation ); }

}
