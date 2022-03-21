/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwServiceProperties.java

Create Date: Apr 11, 2003
============================================================================================
*/
package com.vozzware.ui;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import java.awt.Frame;
import java.awt.Rectangle;

public class VwServiceProperties extends JDialog
{
  JPanel panel1 = new JPanel();
  JLabel jLabel1 = new JLabel();
  JComboBox m_cboServiceList = new JComboBox();
  JLabel jLabel2 = new JLabel();
  JScrollPane jScrollPane1 = new JScrollPane();
  JTable m_tblParams = new JTable();
  JButton m_btnOk = new JButton();
  JButton m_btnCancel = new JButton();

  public VwServiceProperties( Frame frame )
  {
    super(frame, "Service Properties", true);
    try 
    {
      jbInit();
      pack();
    }
    catch(Exception ex)
    {
      ex.printStackTrace();
    }
  }

  void jbInit() throws Exception
  {
    panel1.setLayout(null);
    jLabel1.setText("Available Services");
    jLabel1.setBounds(new Rectangle(7, 14, 109, 17));
    m_cboServiceList.setBounds(new Rectangle(126, 12, 239, 24));
    jLabel2.setText("Required Parameters (Enter any default parameter values in the cells " +
    "below)");
    jLabel2.setBounds(new Rectangle(27, 67, 439, 17));
    jScrollPane1.setBounds(new Rectangle(6, 107, 472, 69));
    m_btnOk.setText("Ok");
    m_btnOk.setBounds(new Rectangle(118, 198, 79, 27));
    m_btnCancel.setText("Cancel");
    m_btnCancel.setBounds(new Rectangle(278, 198, 79, 27));
    getContentPane().add(panel1);
    panel1.add(jLabel1, null);
    panel1.add(m_cboServiceList, null);
    panel1.add(jScrollPane1, null);
    panel1.add(jLabel2, null);
    panel1.add(m_btnOk, null);
    panel1.add(m_btnCancel, null);
    jScrollPane1.getViewport().add(m_tblParams, null);
  }
} // end class VwServiceProperties{}

// *** end of VwServiceProperties.java ***

 