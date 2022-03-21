/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwTActionMgrPanel.java

Create Date: Apr 11, 2005
============================================================================================
*/
package com.vozzware.ui;

import com.vozzware.util.VwCfgParser;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.util.Vector;

public class VwActionMgrPanel extends JPanel
{
  private JLabel jLabel1 = new JLabel();
  private JTextArea m_taConditions = new JTextArea();
  private JLabel jLabel3 = new JLabel();
  private JLabel jLabel2 = new JLabel();
  private JButton m_btnAdd = new JButton();
  private JButton m_btnRemove = new JButton();
  private JButton m_btnEdit = new JButton();
  private JButton m_btnMoveUp = new JButton();
  private JButton m_btnMoveDown = new JButton();
  private JButton m_btnApply = new JButton();  // List of Selected actions

  private VwActionMgrDataModel m_actionData = null;    // Model to handle action data

  private Vector                m_vecActionList;        // List of available actions

  private Vector                m_vecSelectedActions;   // Current action selection list

  private JList m_listSelActions = new JList();
  private JList m_listAvailActions = new JList();

  private VwActionDescriptor   m_curActionDesc = null;
  private String                m_strCurActionName = null;


  /**
   * Constructor
   */
  public VwActionMgrPanel( VwCfgParser props, String strCompName,
                            String strActionCompName )
  {
    try
    {
      jbInit();

      m_actionData = new VwActionMgrDataModel( props, strCompName,
                                                strActionCompName );

      m_vecActionList = m_actionData.getActionList();

      m_vecSelectedActions = m_actionData.getSelectedActions();

      m_btnRemove.setEnabled( false );
      m_btnAdd.setEnabled( false );
      m_btnMoveUp.setEnabled( false );
      m_btnMoveDown.setEnabled( false );
      //m_btnEdit.setEnabled( false );


      // *** Build selected and master action combo box lists

      buildSelectionLists();
    }
    catch(Exception ex)
    {
      JOptionPane.showMessageDialog( this, ex.toString() );

    }
  }

  private void jbInit() throws Exception
  {
    jLabel1.setText("Available Actions");
    jLabel1.setBounds(new Rectangle(22, 9, 109, 17));
    this.setLayout(null);
    m_taConditions.setBounds(new Rectangle(13, 242, 387, 90));
    jLabel3.setText("Conditions that will cause action specified to execute");
    jLabel3.setBounds(new Rectangle(50, 223, 312, 15));
    jLabel2.setText("Selected Actions");
    jLabel2.setBounds(new Rectangle(235, 9, 103, 17));
    m_btnAdd.setToolTipText("Add a new action ");
    m_btnAdd.setText(" Add ===>");
    m_btnAdd.setBounds(new Rectangle(28, 182, 89, 27));
    m_btnAdd.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
        m_btnAdd_actionPerformed(e);
      }
    });
    m_btnRemove.setToolTipText("Remove an existing action");
    m_btnRemove.setText("<===  Remove");
    m_btnRemove.setBounds(new Rectangle(230, 182, 127, 27));
    m_btnRemove.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
        m_btnRemove_actionPerformed(e);
      }
    });
    m_btnEdit.setToolTipText("Edit action parameters");
    m_btnEdit.setText("Edit");
    m_btnEdit.setBounds(new Rectangle(400, 145, 108, 27));
    m_btnEdit.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
        m_btnEdit_actionPerformed(e);
      }
    });
    m_btnMoveUp.setToolTipText("Re-order execution of actions");
    m_btnMoveUp.setText("Move Up");
    m_btnMoveUp.setBounds(new Rectangle(400, 35, 108, 27));
    m_btnMoveUp.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
        m_btnMoveUp_actionPerformed(e);
      }
    });
    m_btnMoveDown.setText("Move Down");
    m_btnMoveDown.setBounds(new Rectangle(400, 92, 108, 27));
    m_btnMoveDown.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
        m_btnMoveDown_actionPerformed(e);
      }
    });
    this.setPreferredSize(new Dimension(550, 300));
    m_btnApply.setText("Apply");
    m_btnApply.setBounds(new Rectangle(400, 182, 106, 27));
    m_btnApply.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
        m_btnApply_actionPerformed(e);
      }
    });
    m_listSelActions.setBounds(new Rectangle(227, 32, 133, 140));
    m_listSelActions.addListSelectionListener(new javax.swing.event.ListSelectionListener()
    {

      public void valueChanged(ListSelectionEvent e)
      {
        m_listSelActions_valueChanged(e);
      }
    });
    m_listAvailActions.setBounds(new Rectangle(16, 32, 135, 138));


    m_listAvailActions.addListSelectionListener( new ListSelectionListener()
    {
      public void valueChanged( ListSelectionEvent lse )
      { m_btnAdd.setEnabled( true ); }

    }  );

    this.add(jLabel3, null);
    this.add(m_taConditions, null);
    this.add(m_listSelActions, null);
    this.add(m_listAvailActions, null);
    this.add(jLabel1, null);
    this.add(jLabel2, null);
    this.add(m_btnAdd, null);
    this.add(m_btnRemove, null);
    this.add(m_btnMoveUp, null);
    this.add(m_btnApply, null);
    this.add(m_btnEdit, null);
    this.add(m_btnMoveDown, null);
  }

  /**
   * Build the selection list combobox and master action list combo box
   */
  private void buildSelectionLists()
  {
    // Build selection list combo box

    m_listSelActions.setListData( m_vecSelectedActions );

    // *** Buld master actions from master list less the selected list

    for ( int x = 0; x < m_vecActionList.size(); x++ )
    {

      String strActionItem = null;
      String strSelItem = null;

      boolean fFound = false;

      for ( int y = 0; y < m_vecSelectedActions.size(); y++ )
      {
        strActionItem = (String)m_vecActionList.elementAt( x );
        strSelItem = (String)m_vecSelectedActions.elementAt( y );

        if ( strActionItem.equals( strSelItem ) )
        {
          m_vecActionList.removeElementAt( x );
          x -= 1;
          break;
        }

      } // end for (y..)

    } // end for ( x.. )

    m_listAvailActions.setListData( m_vecActionList );

    if ( m_vecActionList.size() == 0 )
      m_btnAdd.setEnabled( false );

  } // end buildSelectionLists();


  void m_btnAdd_actionPerformed(ActionEvent e)
  {
    int[] anSel = m_listAvailActions.getSelectedIndices();
    String strItem = null;

    for ( int x = 0; x < anSel.length; x++ )
    {
      strItem = (String)m_vecActionList.elementAt( anSel[ x ]  );
      m_vecSelectedActions.addElement( strItem );
      m_actionData.setActionDescriptor( strItem,
                                        new VwActionDescriptor() );
    }

    for ( int x = 0; x < anSel.length; x++ )
    {
       m_vecActionList.removeElementAt( anSel[ x ] - x );
    }

    m_listSelActions.setListData( m_vecSelectedActions );
    m_listAvailActions.setListData( m_vecActionList );

    if ( m_vecActionList.size() == 0 )
      m_btnAdd.setEnabled( false );

  }

  void m_btnRemove_actionPerformed(ActionEvent e)
  {

    int[] anSel = m_listSelActions.getSelectedIndices();

    String strRemoveAction = null;

    for ( int x = 0; x < anSel.length; x++ )
    {
      strRemoveAction = (String)m_vecSelectedActions.elementAt( anSel[ x ]  );
      m_vecActionList.addElement( strRemoveAction );

      // Tell data manager to remove as well


      if ( m_strCurActionName != null && m_strCurActionName.equals( strRemoveAction ) )
        m_strCurActionName = null;

      m_actionData.removeActionDescriptor( strRemoveAction );

    }

    for ( int x = 0; x < anSel.length; x++ )
    {
      m_vecSelectedActions.removeElementAt( anSel[ x ] - x );
    }


    m_listSelActions.setListData( m_vecSelectedActions );
    m_listAvailActions.setListData( m_vecActionList );

    if ( m_vecSelectedActions.size() == 0 )
    {
      m_btnRemove.setEnabled( false );
      m_btnMoveUp.setEnabled( false );
      m_btnMoveDown.setEnabled( false );
      m_btnEdit.setEnabled( false );

    }
  }

  void m_btnEdit_actionPerformed(ActionEvent e)
  {

    Container c = getParent();
    Dialog parent = null;

    while( c != null )
    {

      if ( c instanceof Dialog )
      {
        parent = (Dialog)c;
        break;
      }

      c = c.getParent();
    }

    if ( parent == null )
    {
      JOptionPane.showMessageDialog( this, "Could Not Find Dialog parent" );
      return;
    }

    VwServiceEditPanel servicePanel = new VwServiceEditPanel( parent );
    servicePanel.show();
    servicePanel.dispose();

  }

  void m_btnMoveUp_actionPerformed(ActionEvent e)
  {

  }

  void m_btnApply_actionPerformed(ActionEvent e)
  {

    if ( m_strCurActionName != null ) // no previous
    {

      String strCondition = m_taConditions.getText();

      m_curActionDesc.setCondition( strCondition );
      m_actionData.setActionDescriptor( m_strCurActionName, m_curActionDesc );

    }
  
    try
    {
      m_actionData.apply();
    }
    catch( Exception ex )
    {

       JOptionPane.showMessageDialog( this, ex.toString() );
       
    }
  }

  void m_btnMoveDown_actionPerformed(ActionEvent e)
  {

  }

  void m_listSelActions_valueChanged(ListSelectionEvent e)
  {
    m_btnRemove.setEnabled( true );
    String strAction = (String)m_listSelActions.getSelectedValue();

    if ( m_strCurActionName != null ) // no previous
    {

      String strCondition = m_taConditions.getText();

      m_curActionDesc.setCondition( strCondition );
      m_actionData.setActionDescriptor( m_strCurActionName, m_curActionDesc );

    }

    if ( strAction == null )
    {

      m_taConditions.setText( "" );
      return;
    }
          
    m_strCurActionName = strAction;

    // *** Descriptor for current selection
    m_curActionDesc = m_actionData.getActionDescriptor( strAction );

    // If new entry create descriptor
    if ( m_curActionDesc == null )
    {
      m_taConditions.setText( "" );
      m_curActionDesc = new VwActionDescriptor();

    }

    m_taConditions.setText( m_curActionDesc.getCondition() );

  } 


} // end class VwActionMgrPanel{}

// *** end of VwActionMgrPanel>java ***

