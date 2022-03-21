/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwActionEvent.java

============================================================================================
*/
package com.vozzware.ui;

import java.awt.event.ActionEvent;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Feb 14, 2004
 * Time: 7:59:23 AM
 * To change this template use File | Settings | File Templates.
 */
public class VwActionEvent extends ActionEvent
{
  public VwActionEvent( Object objSrc, int id, String strCommand)
  { super( objSrc, id, strCommand ); }

  public VwActionEvent( ActionEvent ae )
  { super( ae.getSource(), ae.getID(), ae.getActionCommand(), ae.getWhen(), ae.getModifiers()); }
  
} // end class VwActionEvent {}

// *** End of VwActionEvent.java 
