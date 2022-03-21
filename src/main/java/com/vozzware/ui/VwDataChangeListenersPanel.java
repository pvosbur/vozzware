/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwDataChangedListenerPanel.java

Create Date: Apr 11, 2003
============================================================================================
*/

package com.vozzware.ui;

import com.vozzware.util.VwCfgParser;
import com.vozzware.util.VwDelimString;
import com.vozzware.util.VwExString;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.ResourceBundle;
import java.util.Vector;


public class VwDataChangeListenersPanel extends VwPropBasePanel  implements ListSelectionListener
{
  private JButton m_btnRemove = new JButton();
  private JButton m_btnAdd = new JButton();
  private JLabel jLabel1 = new JLabel();

  private VwDataChangeListenersPropertyEditor  m_pe;
  private VwDelimString m_dlmsProps = new VwDelimString( ",", "" );
  private Vector    m_vecList = new Vector();

  JScrollPane m_scrollPane = new JScrollPane();
  JButton m_btnApply = new JButton();
  JList m_listListeners = new JList();
  ResourceBundle m_msgs = null;

  VwCfgParser m_props = null;
  JButton m_btnActions = new JButton();
  JComboBox m_cboComponents = new JComboBox();

  public VwDataChangeListenersPanel( VwDataChangeListenersPropertyEditor  pe )
  {

    m_msgs = ResourceBundle.getBundle( "com.vozzware.ui.ui" );

    if ( VwComponentPropPropertyEditor.m_strClassPath == null ||
         (VwComponentPropPropertyEditor.m_strClassPath != null &&
          VwComponentPropPropertyEditor.m_strClassPath.length() == 0) )
    {

      JOptionPane.showMessageDialog(null, m_msgs.getString( "VwNeedPropFile" ) );
      close();
      return;
    }

    if ( VwCompNamePropertyEditor.m_strName == null ||
         (VwCompNamePropertyEditor.m_strName != null &&
          VwCompNamePropertyEditor.m_strName.length() == 0) )
    {

      JOptionPane.showMessageDialog(null, m_msgs.getString( "VwNeedCompVarName" ) );
      close();
      return;
    }


    m_pe = pe;

    m_props = VwComponentPropPropertyEditor.getPropFile();

    if ( m_props == null )
    {
      String strMsg = m_msgs.getString( "VwMissingPropFile" );
      strMsg = VwExString.replace( strMsg, "PROPNAME",
                               VwComponentPropPropertyEditor.getPropFilePath() );

      if ( JOptionPane.showConfirmDialog(this, strMsg ) != JOptionPane.YES_OPTION )
      {
        close();
        return;

      }
      else
      {
        try
        {
          m_props = new VwCfgParser( VwComponentPropPropertyEditor.getPropFilePath(),
                                     "#", "=", true, false, true );
        }
        catch( Exception e )
        {

          JOptionPane.showMessageDialog( this, e.toString() );
          close();
          return;
        }

      } // end else

    }
    // Build listbox from the listener list defined in the properties file

    try
    {

      // Get list of component names defined so far
      Hashtable htAvail = (Hashtable)VwCompNamePropertyEditor.m_htNames.clone();

      String str = m_props.getValue( VwCompNamePropertyEditor.m_strName +
                                      ".VwListeners" );

      // Remove this components name from the avail list
      htAvail.remove( VwCompNamePropertyEditor.m_strName );

      if ( str != null )
      {
        VwDelimString dlms = new VwDelimString( ",", str );

        String strPiece = null;

        while ( (strPiece = dlms.getNext()) != null )
        {
          htAvail.remove( strPiece );         // Remove from available list
          m_vecList.addElement( strPiece );   // put in selected list
        }

      }  // end if

      Enumeration keys = htAvail.keys();

      // Build combo box of available component names

      while ( keys.hasMoreElements() )
        m_cboComponents.addItem( (String)keys.nextElement() );

        
    } // end try
    catch( Exception e )
    {
    }

    m_listListeners.setListData( m_vecList );

    try
    {
      m_listListeners.addListSelectionListener( this );
      jbInit();
    }
    catch(Exception ex)
    {
      ex.printStackTrace();
    }

  }

  private void jbInit() throws Exception
  {
    this.setLayout(null);
    this.setMaximumSize(new Dimension(400, 300));
    this.setMinimumSize(new Dimension(400, 300));
    this.setPreferredSize(new Dimension(400, 300));
    m_btnRemove.setEnabled(false);
    m_btnRemove.setHorizontalTextPosition(SwingConstants.LEFT);
    m_btnRemove.setText("Remove From List");
    m_btnRemove.setBounds(new Rectangle(226, 105, 144, 27));
    m_btnRemove.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
        m_btnRemove_actionPerformed(e);
      }
    });
    m_btnAdd.setHorizontalAlignment(SwingConstants.LEFT);
    m_btnAdd.setText("<== Add To List");
    m_btnAdd.setBounds(new Rectangle(226, 58, 144, 27));
    m_btnAdd.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
        m_btnAdd_actionPerformed(e);
      }
    });
    jLabel1.setText("Select from list of available components to registers as listeners");
    jLabel1.setBounds(new Rectangle(6, 10, 395, 17));
    m_scrollPane.setBounds(new Rectangle(29, 97, 180, 160));
    m_btnApply.setToolTipText("Save all changes in the compoen\'s property file");
    m_btnApply.setText("Apply");
    m_btnApply.setBounds(new Rectangle(128, 273, 79, 27));
    m_btnApply.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
        m_btnApply_actionPerformed(e);
      }
    });
    m_btnActions.setEnabled(false);
    m_btnActions.setToolTipText("Define the actions this component will take for registered component\'s " +
    "data change event");
    m_btnActions.setText("Define Actions...");
    m_btnActions.setBounds(new Rectangle(229, 162, 139, 27));
    m_btnActions.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
        m_btnActions_actionPerformed(e);
      }
    });
    m_cboComponents.setBounds(new Rectangle(32, 60, 174, 24));
    this.add(jLabel1, null);
    this.add(m_btnRemove, null);
    this.add(m_btnAdd, null);
    this.add(m_scrollPane, null);
    this.add(m_btnActions, null);
    this.add(m_btnApply, null);
    this.add(m_cboComponents, null);
    m_scrollPane.getViewport().add(m_listListeners, null);
  }

  private void m_btnRemove_actionPerformed(ActionEvent e)
  {
    int[] aSel = m_listListeners.getSelectedIndices();

    // Put remove items back in the available list
    
    for ( int x = 0; x < aSel.length; x++ )
      m_cboComponents.addItem( m_vecList.elementAt( x ) );

    for ( int x = 0; x < aSel.length; x++ )
    {
      int ndx = aSel[x];
      if ( x > 0 )
        --ndx;
      m_vecList.removeElementAt(ndx);
    }

    m_listListeners.setListData( m_vecList );

  }

  private void m_btnAdd_actionPerformed(ActionEvent e)
  {
    addToList();
  }

  public void valueChanged( ListSelectionEvent e )
  {
    m_btnRemove.setEnabled( true  );
    m_btnActions.setEnabled( true  );
  }



  /**
   * Adss a listener from the text field to the list
   */
  private void addToList()
  {

    int nSel = m_cboComponents.getSelectedIndex();

    String strListener = (String)m_cboComponents.getSelectedItem();

    if ( nSel < 0 )
    {

      JOptionPane.showMessageDialog( this, m_msgs.getString( "VwNeedCompName" ) );
      m_cboComponents.requestFocus();
      return;
    }

    // Remove selected item from the available list
    m_cboComponents.removeItemAt( nSel );

    m_vecList.addElement( strListener );

    m_listListeners.setListData( m_vecList );

  } // end addToList()


  void m_btnApply_actionPerformed(ActionEvent e)
  {
    // *** Build Delimited string of component names from the current list

    String strKey = VwCompNamePropertyEditor.m_strName + ".VwListeners";

    if ( m_vecList.size() == 0 )
    {
      m_pe.setValue( null );
      enableOK();
      try
      {
        m_props.removeItem( strKey );
        m_props.updateFile();

      }
      catch( Exception e1)
      {;}
        
      return;
    }
      
    VwDelimString dlms = new VwDelimString( ",", "" );
    String[] astr = new String[ m_vecList.size() ];

    for( int x = 0; x < m_vecList.size(); x++ )
    {
      dlms.add( (String)m_vecList.elementAt( x ) );
      astr[x] = (String)m_vecList.elementAt( x );
    }

    try
    {
      try
      {
        m_props.updateItem( strKey, dlms.toString() );
      }
      catch( Exception e1 )
      {
        m_props.addItem( strKey, dlms.toString() );
      }

      m_props.updateFile();
    }
    catch( Exception e2 )
    {

      JOptionPane.showMessageDialog(this, e2.getMessage() );
      return;
    }

    m_pe.setValue( astr );

    enableOK();
    
  }

  void m_btnActions_actionPerformed(ActionEvent e)
  {

    int nSel = m_listListeners.getSelectedIndex();


    if ( nSel < 0 )
    {

      JOptionPane.showMessageDialog( this, m_msgs.getString( "VwNeedCompName" ) );
      m_cboComponents.requestFocus();
      return;
    }

    String strActionComponent = (String)m_listListeners.getSelectedValue();

    VwActionMgrPanel actionMgr =
      new VwActionMgrPanel( m_props,
                             VwCompNamePropertyEditor.m_strName,
                             strActionComponent );

    // Create a Jpanel with OK and Cancel Buttons

    JPanel actionPanel = new JPanel();
    actionPanel.setLayout( new FlowLayout() );

    JButton okBtn = new JButton( "Ok" );
    JButton cancelBtn = new JButton( "Cancel" );

    actionPanel.add( okBtn );
    actionPanel.add( cancelBtn );


    final JDialog actionDialog = new JDialog( getDialogParent() );
    actionDialog.setModal( true );
    actionDialog.setTitle( "Action Selection" );
    actionDialog.getContentPane().setLayout( new BorderLayout() );

    actionDialog.getContentPane().add( actionMgr, BorderLayout.CENTER );
    actionDialog.getContentPane().add( actionPanel, BorderLayout.SOUTH );

    cancelBtn.addActionListener( new ActionListener()
                                 {
                                   public void actionPerformed( ActionEvent ae )
                                   {
                                     actionDialog.dispose();
                                   }
                                  } );

    //setEnabled( false );
    actionDialog.pack();
    actionDialog.show();
    //setEnabled( true );


  }


} // end class VwDataChangeListenersPanel{}

// *** End of VwDataChangeListenersPanel.java ***
