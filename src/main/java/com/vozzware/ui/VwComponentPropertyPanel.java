/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwComponentPropertyPanel.java

Create Date: Apr 11, 2003
============================================================================================
*/

package com.vozzware.ui;

import com.vozzware.util.VwExString;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ResourceBundle;

public class VwComponentPropertyPanel extends VwPropBasePanel
{
  JLabel jLabel1 = new JLabel();
  JTextField m_txtClassPath = new JTextField();
  JLabel jLabel2 = new JLabel();
  JTextField m_txtPropName = new JTextField();

  JButton m_btnApply = new JButton();

  private VwComponentPropPropertyEditor m_propEditor;
  JLabel jLabel3 = new JLabel();

  private ResourceBundle m_msgs = null;

  public VwComponentPropertyPanel( VwComponentPropPropertyEditor propEditor )
  {

    m_msgs = ResourceBundle.getBundle( "com.vozzware.ui.ui" );

    if ( VwCompNamePropertyEditor.m_strName == null ||
         (VwCompNamePropertyEditor.m_strName != null &&
          VwCompNamePropertyEditor.m_strName.length() == 0) )
    {

        JOptionPane.showMessageDialog(this, m_msgs.getString( "VwNeedName" ) );
        close();
        return;
    }

    m_propEditor = propEditor;

    try
    {
      jbInit();
      m_txtPropName.requestFocus();

      String str = m_propEditor.getAsText();

      if ( str != null )
      {
        int nPos = str.indexOf( ';' );

        m_txtClassPath.setText( str.substring( 0, nPos ) );
        m_txtPropName.setText( str.substring( nPos + 1 ) );
      }

    }
    catch(Exception ex)
    {
      ex.printStackTrace();
    }
  }

  private void jbInit() throws Exception
  {
    jLabel1.setText("Class Path:");
    jLabel1.setBounds(new Rectangle(12, 40, 82, 17));
    this.setLayout(null);
    m_txtClassPath.setBounds(new Rectangle(91, 39, 131, 21));
    jLabel2.setText("Form Property File Name (including package name but no file extension)");
    jLabel2.setBounds(new Rectangle(14, 89, 405, 17));
    m_txtPropName.setBounds(new Rectangle(14, 131, 280, 21));
    this.setPreferredSize(new Dimension(300, 200));
    m_btnApply.setText("Apply");
    m_btnApply.setBounds(new Rectangle(140, 162, 79, 27));
    m_btnApply.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
        m_btnApply_actionPerformed(e);
      }
    });
    jLabel3.setText("Ex. mypackage.myformprops");
    jLabel3.setBounds(new Rectangle(14, 107, 392, 17));
    this.add(jLabel1, null);
    this.add(m_txtClassPath, null);
    this.add(jLabel2, null);
    this.add(m_btnApply, null);
    this.add(jLabel3, null);
    this.add(m_txtPropName, null);
  }

  void m_btnApply_actionPerformed(ActionEvent e)
  {
    ResourceBundle msgs = ResourceBundle.getBundle( "com.vozzware.ui.ui" );

    String strClassPath = m_txtClassPath.getText();
    String strPropName = m_txtPropName.getText();

    String strPathChar = new String( new char[]{ File.separatorChar } );

    if ( strClassPath.endsWith( strPathChar ) )
      strClassPath = VwExString.remove( strClassPath, strPathChar,
                                         strClassPath.length() - 1 );
                                         
    if ( strClassPath.length() == 0 )
    {
      JOptionPane.showMessageDialog( this, msgs.getString( "VwMissing.ClassPath" ) );
      m_txtClassPath.requestFocus();
      return;
    }

    if ( strPropName.length() == 0 )
    {
      JOptionPane.showMessageDialog( this, msgs.getString( "VwMissing.PropFileh" ) );
      return;
    }

    String strPropPath = strClassPath + File.separatorChar +
       strPropName.replace( '.', File.separatorChar ) + ".properties";

    File fileProps = new File( strPropPath );

    if ( !fileProps.exists() )
    {
      String strMsg = VwExString.replace( msgs.getString( "VwFile.NoExist" ),
                                           "<FILENAME>", strPropPath ) ;

      if ( JOptionPane.showConfirmDialog( this, strMsg ) != JOptionPane.YES_OPTION )
        return;

      try
      {
        fileProps.createNewFile();
      }
      catch( Exception ex )
      {
        JOptionPane.showMessageDialog(this, ex.toString() );
        return;
      }

    }

    enableOK();
    m_propEditor.setAsText( strClassPath + ";" + strPropName );

  }

} // end class VwComponentPropertyPanel{}

// *** end of VwComponentPropertyPanel.java ***

