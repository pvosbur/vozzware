
/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwServiceEditPaneljava

Create Date: Apr 11, 2003
============================================================================================
*/
package com.vozzware.ui;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;



public class VwServiceEditPanel extends JDialog
{
  JPanel jpanelBase  = new JPanel();
  JLabel jLabel1 = new JLabel();
  JScrollPane jScrollPane1 = new JScrollPane();
  JList m_listServices = new JList();
  JScrollPane jScrollPane2 = new JScrollPane();
  JList m_listParams = new JList();
  JLabel jLabel2 = new JLabel();
  JLabel m_lblParamName = new JLabel();
  JLabel jLabel3 = new JLabel();
  JLabel m_lblDataType = new JLabel();
  JTextField jTextField1 = new JTextField();
  JLabel jLabel4 = new JLabel();
  JButton m_btnClose = new JButton();
  JButton m_btnApply = new JButton();
  JButton m_btnServiceInfo = new JButton();
  JLabel jLabel5 = new JLabel();

  class ServiceDesc
  {
    String    m_strName;
    String    m_strDesc;

  }
  public VwServiceEditPanel( Dialog parent )
  {
    super( parent, "Service Definition", true );

    try
    {
      jbInit();
      pack();
      setDefaultCloseOperation( WindowConstants.DO_NOTHING_ON_CLOSE );
     }
    catch(Exception ex)
    {
      ex.printStackTrace();
    }
  }

  private void jbInit() throws Exception
  {
    jpanelBase.setPreferredSize(new Dimension(600, 420));

    jLabel1.setText("Available Services");
    jLabel1.setBounds(new Rectangle(10, 13, 119, 17));
    jpanelBase.setLayout(null);
    jScrollPane1.setBounds(new Rectangle(6, 41, 211, 305));
    jScrollPane2.setBounds(new Rectangle(257, 45, 205, 188));
    jLabel2.setText("Required Parameters");
    jLabel2.setBounds(new Rectangle(289, 26, 130, 17));
    m_lblParamName.setText("jLabel3");
    m_lblParamName.setBounds(new Rectangle(357, 258, 149, 17));
    jLabel3.setText("Data Type");
    jLabel3.setBounds(new Rectangle(260, 331, 74, 17));
    m_lblDataType.setText("jLabel4");
    m_lblDataType.setBounds(new Rectangle(355, 331, 69, 17));
    jTextField1.setText("jTextField1");
    jTextField1.setBounds(new Rectangle(357, 292, 121, 21));
    jLabel4.setText("Initial Value:");
    jLabel4.setBounds(new Rectangle(260, 291, 72, 17));
    m_btnClose.setText("Close");
    m_btnClose.setBounds(new Rectangle(130, 373, 79, 27));
    m_btnClose.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
        m_btnClose_actionPerformed(e);
      }
    });
    m_btnApply.setText("Apply");
    m_btnApply.setBounds(new Rectangle(276, 372, 79, 27));
    m_btnServiceInfo.setText("Service Info...");
    m_btnServiceInfo.setBounds(new Rectangle(133, 9, 121, 27));
    jLabel5.setText("Selected Param:");
    jLabel5.setBounds(new Rectangle(258, 257, 94, 17));
    jpanelBase.add(jLabel1, null);
    jpanelBase.add(jScrollPane1, null);
    jpanelBase.add(m_btnApply, null);
    jpanelBase.add(m_lblDataType, null);
    jpanelBase.add(jLabel3, null);
    jpanelBase.add(m_btnClose, null);
    jpanelBase.add(m_btnServiceInfo, null);
    jpanelBase.add(jLabel2, null);
    jpanelBase.add(jScrollPane2, null);
    jpanelBase.add(jLabel5, null);
    jpanelBase.add(m_lblParamName, null);
    jpanelBase.add(jLabel4, null);
    jpanelBase.add(jTextField1, null);
    jScrollPane2.getViewport().add(m_listParams, null);
    jScrollPane1.getViewport().add(m_listServices, null);
    this.getContentPane().add( jpanelBase, null );
  }

  void m_btnClose_actionPerformed(ActionEvent e)
  {
    dispose();
  }


 
} // end class VwServiceEditPanel {}

// *** end of VwServiceEditPanel.java ***

