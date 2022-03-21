/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwWizardMgr.java

============================================================================================
*/

package com.vozzware.components;

import com.vozzware.ui.VwYesNoMsgBox;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;
import java.util.Vector;

public class VwWizardMgr extends JDialog
{
  private JFrame    m_parent;           // Parent Frame owner window

  private ResourceBundle m_msgs;        // Message properties file

  private JButton   m_btnNext;          // Button to invoke next panel in the wizard sequence
  private JButton   m_btnPrev;          // Button to invoke previous panel in the wizard sequence
  private JButton   m_btnCancel;        // Button to close the wizard

  private Dimension m_dimPanels;        // The dimension of the largest panel
  private Dimension m_dimButtons;       // The dimension of the navigation button panel

  private Vector    m_vecUserPanels;    // Vector of user panels to be displayed

  private int       m_nPanelCursor;     // Current panel displayed

  private JPanel    m_panelUser;        // Panel to hold the current user panel

  private boolean   m_fForward;         // If True, panels are moving in the forward (Next)
                                        // direction, else the backward (Prev) direction

  private Object    m_userObj = null;   // User defined object that can be passed from panels


  /**
   * Class Constructor
   *
   * @param parent The Parent JFrame object
   * @param strTitle The title for the wizard titlebar
   * @param fModal If True, this wizard is modal; otherwise, it is modeless
   */
  public VwWizardMgr( JFrame parent, String strTitle, boolean fModal )
  {
    super( parent, strTitle, fModal );

    m_vecUserPanels = new Vector();

    m_nPanelCursor = 0;

    m_dimPanels = new Dimension( 0, 0 );

    m_panelUser = new JPanel( new BorderLayout() );

    m_msgs = ResourceBundle.getBundle("com.vozzware.components.components");

    m_parent = parent;

    setup();

    this.getContentPane().add( m_panelUser, BorderLayout.CENTER );

  } // end VwWizardMgr()


  /**
   * Adds a user panel to the end of the viewing sequence
   *
   * @param panel A user panel implementing the VwWizardPanel interface
   * @param strPanelID An optional panel Id used to instruct the VwWizardMgr
   * to skip to a specific panel.  Null may be passed if Ids are not needed.
   */
  public void add( VwWizardPanel panel, String strPanelID )
  {
    panel.m_strNameID = strPanelID;

    m_vecUserPanels.addElement( panel );

    panel.doLayout();

    Dimension d = panel.getMinimumSize();

    // See if this panel is the largest and if so adjust

    if ( d.height > m_dimPanels.height )
     m_dimPanels.height = d.height;


    if ( d.width > m_dimPanels.width )
     m_dimPanels.width = d.width;

  } // end add()


  /**
   * Override of show() to get the inset dimensions and reset the VwWizardMgr
   * size.
   */
  public void show()
  {
    super.show();
    Insets ins = this.getInsets();

    Dimension d = new Dimension();

    d.height = ins.top + ins.bottom + m_dimButtons.height + m_dimPanels.height;
    d.width =  ins.left + ins.right + m_dimButtons.width + m_dimPanels.width;
    setSize( d );

    m_fForward = true;

    if ( m_vecUserPanels.size() > 0 )
      displayPanel();

  } // end show()


  /**
   * Sets a user assigned Object that can be passed from panel to panel
   *
   * @param obj An object used to transfer information from panel to panel
   */
  public final void setUserObject( Object obj )
  { m_userObj = obj; }


  /**
   * Gets a user assigned Object if one was defined
   *
   * @return obj The user assigned object if defined
   */
  public final Object getUserObject()
  { return m_userObj; }


  /**
   * Sets up the base wizard panel and buttons
   */
  private void setup()
  {
    this.getContentPane().setLayout( new BorderLayout() );

    // *** Create the control wizard push buttons
    m_btnNext = new JButton( m_msgs.getString( "Vw.Components.Next" ) );
    m_btnPrev = new JButton( m_msgs.getString( "Vw.Components.Prev" ) );
    m_btnCancel = new JButton( m_msgs.getString( "Vw.Components.Cancel" ) );

    // Install button click events

    m_btnCancel.addActionListener( new ActionListener()
                                   {
                                     public void actionPerformed( ActionEvent ae )
                                     {
                                       if ( confirmCancel() )
                                        VwWizardMgr.this.dispose();

                                     } } );

    m_btnNext.addActionListener( new ActionListener()
                                     {
                                       public void actionPerformed( ActionEvent ae )
                                       {  setupNextPanel( 1 ); }
                                     } );

    m_btnPrev.addActionListener( new ActionListener()
                                     {
                                       public void actionPerformed( ActionEvent ae )
                                       {  setupNextPanel( -1 ); }
                                     } );

    // *** Create panel with FlowLayout to hold the control buttons

    JPanel buttonPanel = new JPanel( new FlowLayout() );

    buttonPanel.add( m_btnPrev );
    buttonPanel.add( m_btnNext );
    buttonPanel.add( m_btnCancel );

    m_dimButtons = buttonPanel.getMinimumSize();

    // The control buutons go on bottom most part the the Wizard panel
    this.getContentPane().add( buttonPanel, BorderLayout.SOUTH );

  } // end setup()


  /**
   * Diplays the panel at the cursor position or continues to skip panels until
   * the end or beginning of the panel list is encountered.
   */
  private void displayPanel()
  {
    // *** Display the panel at the current panel cursor position or skip
    // *** until we get to the next panel that skip() returns false

    VwWizardPanel panel = null;

    while( true )
    {
      // *** Get the panel object at the current cursor position
      panel = (VwWizardPanel)m_vecUserPanels.elementAt( m_nPanelCursor );

      // If we could skip next or previous, call the skip method
      if ( (m_fForward && m_nPanelCursor < m_vecUserPanels.size() - 1 )  ||
         ( !m_fForward && m_nPanelCursor > 0 ) )
      {
        if ( panel.skip( this ) )
        {
          if ( m_fForward )
            ++m_nPanelCursor;
          else
            --m_nPanelCursor;

          continue;
        }

      } // end if

      // *** We're at the end of the list if we get here. Disable the Next or Previos button
      // *** depending on our direction

      if ( m_nPanelCursor == ( m_vecUserPanels.size() - 1 ) )
          m_btnNext.setEnabled( false );
      else
          m_btnNext.setEnabled( true );

      if ( m_nPanelCursor == 0 )
         m_btnPrev.setEnabled( false );
      else
         m_btnPrev.setEnabled( true );
      break;               // End of list display this one

    } // end while( true )

    // *** Display the current panel

    m_panelUser.removeAll();    // Remove the current user panel for the user panel container

    panel.m_nPanelNbr = m_nPanelCursor;

    panel.validate();
    panel.requestFocus();
    m_panelUser.add( panel, BorderLayout.CENTER);
    m_panelUser.validate();
    m_panelUser.repaint();
    repaint();
    validate();

  } // end displayPanel()


  /**
   * Displays the Cancel confirmation msg box
   *
   * @return True if the user wants to cancel the wizard session;
   * otherwise, False is returned.
   */
  private boolean confirmCancel()
  {
    VwYesNoMsgBox mb = new VwYesNoMsgBox( (java.awt.Frame)m_parent,
                                            m_msgs.getString( "Vw.Components.ConfirmTitle" ),
                                            m_msgs.getString( "Vw.Components.ConfirmMsg" ) );

    mb.show();

    if ( mb.getReason() == VwYesNoMsgBox.NO )
      return false;

    return true;

  } // end confirmCancel()


  /**
   * This method calls the proceed() method for the current panel and advances
   * the panel cursor if proceed() returns True.
   *
   * @param nInc 1 to advance to the next panel, -1 to return to the previous
   * panel.
   */
  private void setupNextPanel( int nInc )
  {
    // *** Get the current panel displayed
    VwWizardPanel panel = (VwWizardPanel)m_vecUserPanels.elementAt( m_nPanelCursor );

    if ( panel.proceed( this ) )
    {
      if ( nInc < 0 )
        m_fForward = false;
      else
        m_fForward = true;

      m_nPanelCursor += nInc;
      displayPanel();
    }

  } // end setupNextPanel()


} // end class VwWizardMgr{}


// *** End of VwWizardMgr.java ***

