/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwFormBase.java

============================================================================================
*/


package com.vozzware.ui;            // The package this class belongs to

import javax.swing.SwingUtilities;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;

/**
 * This class implements extended dialog functions such as tabbing amongst controls on a
 * form/dialog, setting the initial component focus, etc.  All java forms should be derived
 * from this class.
 *
 * @version 1.0
 */

public class VwFormBase extends Dialog
{
  private Frame m_parent;                       // Parent of this window

  protected boolean  m_fIsValid;                // Object validity flag


 /**
   * Constructs the form controller base class
   *
   * @param parent - The parent of this form.  Note!  The parent must be a descendant of the Frame class
   * @param strTitle - The text to appear in the form titlebar
   * @param fModal - The form modal flag.  If true the the form/dialog is modal.
  */
  public VwFormBase( Frame parent, String strTitle, boolean fModal )
  {
    super( parent, strTitle, fModal );

    m_parent = parent;
    setBackground( Color.lightGray );           // light gray is default color for forms/dialogs
    m_fIsValid = true;

  } // end VwFormBase()


  /**
   * Returns the value of the object validity flag
   *
   * @returns True if the current object is valid; otherwise, False is returned.
   */
  public boolean isValid()
  { return m_fIsValid; }


  /**
   * Center the current form within its parent
   */
  public void center()
  { center(  m_parent, this ); }


  /**
   * Centers a Frame or window-based component within another frame or window-based component
   *
   * @param compWithin - The frame or window component that will contain the centered component;
   * if null, the screen display dimensions are used.
   * @param compToCenter - The component to center
   */
  public static final void center( Component compWithin, Component compToCenter )
  {
    Rectangle rct = null;
    Point pt = new Point();

    if ( compWithin == null )  // Center to Screensize
    {
      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

      rct = new Rectangle( screenSize );
      pt.x = rct.x;
      pt.y = rct.y;
    }
    else
    {
      rct = compWithin.getBounds();
      pt.x = rct.x;
      pt.y = rct.y;
      SwingUtilities.convertPointToScreen( pt, compWithin );
    }
    

    Dimension dim = compToCenter.getSize();

    int nxLeft = ( rct.width / 2 ) - ( dim.width / 2 ) + pt.x;
    int nyTop = ( rct.height / 2 ) - ( dim.height / 2 ) + pt .y;

    if ( nxLeft < 0 )
      nxLeft = 0;
    if ( nyTop < 0 )
      nyTop = 0;

    compToCenter.setLocation( nxLeft, nyTop );

  } // end center()

} // end class VwFormBase

