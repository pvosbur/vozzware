/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwJInternalFrameWrapper.java

============================================================================================
*/

package com.vozzware.components;

import javax.swing.JInternalFrame;
import java.awt.Component;

/**
 * This class wraps a Component based object in a JInternalFrame  for use
 * in a JDesktopPane
 *
 */
public class VwJInternalFrameWrapper extends JInternalFrame
{

  /**
   * Contructs a standard explorer type view with an IITcContentProducer in the left pane
   * and an IVwContentSelectionListener in the right pane
   *
   */
  public VwJInternalFrameWrapper( Component compContent )
  {

    this.getContentPane().add( compContent );

  }  // end VwJInternalFrameWrapper

} // End class VwJInternalFrameWrapper{}

// *** End of VwJInternalFrameWrapper.java ***

