/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: Panel3.java

============================================================================================
*/

package com.vozzware.components;

import com.vozzware.ui.VwYesNoMsgBox;

import javax.swing.JButton;
import javax.swing.JLabel;
import java.awt.Rectangle;

public class Panel3 extends VwWizardPanel
{
  JLabel jLabel1 = new JLabel();
  JButton jButton1 = new JButton();

  boolean fProceed = false;

  public Panel3()
  {
    try
    {
      jbInit();
    }
    catch(Exception ex)
    {
      ex.printStackTrace();
    }
  }

  public boolean proceed( VwWizardMgr mgr )
  {
    VwYesNoMsgBox mb = new VwYesNoMsgBox( (java.awt.Frame)mgr.getParent(),
                                            "Confirmation",
                                            "Ok to proceed?" );

    mb.show();

    if ( mb.getReason() == VwYesNoMsgBox.NO )
      return false;

    return true;

  }

  private void jbInit() throws Exception
  {
    jLabel1.setText("Slide three");
    jLabel1.setBounds(new Rectangle(86, 41, 243, 17));
    this.setLayout(null);
    jButton1.setText("jButton1");
    jButton1.setBounds(new Rectangle(140, 164, 79, 27));
    this.add(jLabel1, null);
    this.add(jButton1, null);
  }
}// end class Panel3{}

// *** End Panel3.java ***
