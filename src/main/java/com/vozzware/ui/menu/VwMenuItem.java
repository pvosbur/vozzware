/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwMenuItem.java

Create Date: Apr 11, 2003
============================================================================================
*/
package com.vozzware.ui.menu;

import com.vozzware.ui.VwAction;
import com.vozzware.ui.VwIcon;

import javax.swing.JMenuItem;

/**
 * This class abstracts GUI specific menue item
 */
public class VwMenuItem extends JMenuItem
{
  /**
   * Default constructor
   */
  public VwMenuItem()
  { super(); }

  /**
   * Constructs menu item with an associated action
   * @param action
   */
  public VwMenuItem( VwAction action )
  { super( action ); }


  /**
   * Constructs menu item with a text string
   * @param strText
   */
  public VwMenuItem( String strText )
  { super( strText ); }


  /**
   * Constructs menu item with an icon
   * @param icon
   */
  public VwMenuItem( VwIcon icon )
  { super( icon ); }


  /**
   * sets the associated action to this menu item
   * @param action
   */
  public void setMenuAction( VwAction action )
  { setAction( action );  }
  /**
   * Returns the VwAction for this menu
   * @return The VwAction for this menu or null if no VwAction is defined.
   */
  public VwAction getMenuAction()
  {
    Object objAction = getAction();

    if ( objAction instanceof VwAction )
      return (VwAction)objAction;

    return null;

  } // end getMenuAction()

} // end class VwMenuItem{}

// *** End of VwMenuItem.java ***

