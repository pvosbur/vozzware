package com.vozzware.ui;

import javax.swing.JButton;
import javax.swing.JDialog;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwDialogBase.java

Create Date: Apr 11, 2003
============================================================================================
*/
public class VwDialogBase extends JDialog
{

  public VwDialogBase( Frame owner, boolean fModal )
  {
    this( owner, "", fModal );

  } // end VwDialogBase()

	public VwDialogBase( Frame owner, String strTitle, boolean fModal )
	{
	  super( owner, strTitle, fModal );

      this.addComponentListener( new ComponentAdapter()
      {
        public void componentShown(ComponentEvent e) 
        {
          Dimension dimPanelSize = getPreferredSize();
          
          Insets ins = getInsets();
          dimPanelSize.height += ins.top + ins.bottom;
          dimPanelSize.width += ins.left + ins.right;
          
          setSize( dimPanelSize );
          
        }
      });
        
	} // end VwDialogBase()


	/**
	 * Sets the default button for this dialog
	 * @param btnDefault VwButton
	 */
	public void setDefaultButton( JButton btnDefault )
  { 	this.getRootPane().setDefaultButton( btnDefault ); }

	/**
	 * gets the default buuton set for this dialog or null if none set
	 * @return VwButton
	 */
	public JButton getDefaultButton()
	{
		Object objBtn = this.getRootPane().getDefaultButton();
		if ( objBtn instanceof VwButton )
			return (VwButton)objBtn;

	  return null;

	}

  /**
   * Centers a component within an encompassing component
   *
   * @param compToCenter The component to center
   * @param compEncompasing The encompaaing component, if null, then center within the desktop
   * @return A Point containing the x,y coordinates for the centered location
   */
  public static void center( Component compToCenter, Component compEncompasing )
  {
    Rectangle rct = null;

    if ( compEncompasing == null )  // Center to Screensize
    {
      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

      rct = new Rectangle( screenSize );
    }
    else
      rct = compEncompasing.getBounds();

    Dimension dim = compToCenter.getSize();

    int nxLeft = ( rct.width / 2 ) - ( dim.width / 2 ) + rct.x;
    int nyTop = ( rct.height / 2 ) - ( dim.height / 2 ) + rct .y;

    if ( nxLeft < 0 )
      nxLeft = 0;
    if ( nyTop < 0 )
      nyTop = 0;

    compToCenter.setLocation( new Point( nxLeft, nyTop ) );


  } // end center()

} // end class VwDialogBase{}

// *** End of VwDialogBase.java ***

