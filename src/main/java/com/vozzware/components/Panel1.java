/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: Panel1.java

============================================================================================
*/

package com.vozzware.components;

import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.Rectangle;

public class Panel1 extends VwWizardPanel
{
  JLabel jLabel1 = new JLabel();
  JLabel jLabel2 = new JLabel();
  JTextField jTextField1 = new JTextField();

  public Panel1()
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

  private void jbInit() throws Exception
  {
    jLabel1.setText("This is the first panel in the wizard");
    jLabel1.setBounds(new Rectangle(57, 27, 264, 17));
    this.setLayout(null);
    jLabel2.setText("First Name:");
    jLabel2.setBounds(new Rectangle(58, 84, 73, 17));
    jTextField1.setText("jTextField1");
    jTextField1.setBounds(new Rectangle(145, 83, 117, 21));
    this.add(jLabel1, null);
    this.add(jLabel2, null);
    this.add(jTextField1, null);

    setSize( 300, 300 );
  }
}// end class Panel1{}

// *** End Panel1.java ***
