/*
===========================================================================================

 
                               Copyright(c) 2000 - 2006 by

                      V o z z W a r e   L L C (Vw)

                                 All Rights Reserved

Source Name: VwMenuImplementor.java

Create Date: Sep 26, 2006

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.


============================================================================================
*/
package com.vozzware.ui.menu;

import javax.swing.Action;
import javax.swing.JMenuItem;

public interface VwMenuImplementor
{
  public JMenuItem add( Action a );

  public JMenuItem add( JMenuItem mi );

  public JMenuItem add( String strMenuText );
  
  public void setName( String strName );

  public void setText( String strName );

  public void addSeparator();
  
  
}
