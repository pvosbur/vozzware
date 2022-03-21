/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwDialog.java

Create Date: Apr 11, 2003
============================================================================================
*/
package com.vozzware.ui;

import com.vozzware.util.VwResourceMgr;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.util.List;


/**
 * This class provides a dynamic JDialog both m_btModel and modelss for the JPanel derived
 * class specified in the constructor. This class automaticially creates OK and Cancel action
 * buttons if the dialog style is modal. The addActionButton method allows for additional buttons
 * to be added by the user of this class.
 */
public class VwDialog extends JDialog
{
  private VwPanel  m_actionPanel;
  private String    m_strCommand = "Cancel";
  private JPanel    m_userPanel;
  private boolean   m_fWasCancelled = true;
  private boolean   m_fNoActionPanel = false;
  //private Map      m_actionButtonListeners = new HashMap();

  /**
   * Constructor
   * 
   * @param compParent The parent component of the dialog
   * @param userPanel The user JPanel to be displayed
   * @param fModal if true, the dialog is model otherwise its modeless
   * @param strTitle The title bar text of the dialog
   */
  public VwDialog( Component compParent, JPanel userPanel, boolean fModal, String strTitle )
  { this( findParent( compParent ), userPanel, fModal, strTitle ); }
  
  
  /**
   * Constructor
   * 
   * @param dlgParent The parent of this dialog
   * @param userPanel The user JPanel to be displayed
   * @param fModal if true, the dialog is model otherwise its modeless
   * @param strTitle The title bar text of the dialog
   */
  public VwDialog( JDialog dlgParent, JPanel userPanel, boolean fModal, String strTitle )
  {
    super( dlgParent, strTitle, fModal );
    setup( userPanel, strTitle );
    
  }



  /**
   * Constructor
   * @param frameParent
   * @param userPanel
   * @param fModal
   * @param strTitle
   */
  public VwDialog( Frame frameParent, JPanel userPanel, boolean fModal, String strTitle )
  {
    super( frameParent, strTitle, fModal );
    setup( userPanel, strTitle );
    
  } // end VwDialog()

  
  /**
   * Sets up the dialog 
   * @param userPanel The userPanel to install
   * @param strTitle The dialog's title bar text
   */
  private void setup( JPanel userPanel, String strTitle )
  {
    this.getContentPane().setLayout( new BorderLayout() );

    this.getContentPane().add( userPanel, BorderLayout.CENTER );

    m_userPanel = userPanel;
    
    FlowLayout fl = new FlowLayout();
    m_actionPanel = new VwPanel();
    m_actionPanel.setLayout( fl );
    this.getContentPane().add( m_actionPanel, BorderLayout.SOUTH );
 
    
    this.addComponentListener( new ComponentAdapter()
    {
      public void componentShown(ComponentEvent e) 
      {
        Dimension dimPanelSize = m_userPanel.getPreferredSize();
        if ( m_actionPanel.getComponentCount() == 0 && m_fNoActionPanel == false )
          createStandardActionPanel( FlowLayout.CENTER );
        
        
        Insets ins = VwDialog.this.getInsets();
        Dimension dimButtons = m_actionPanel.getPreferredSize();
        
        if ( dimButtons.height < 20 )
          dimButtons.height = 20;
        
        
        dimPanelSize.height += ins.top + ins.bottom + dimButtons.height;
        dimPanelSize.width += ins.left + ins.right;
        
        VwDialog.this.setSize( dimPanelSize );
       
      }
    });
    
  }
  
  
  /**
   * Do not create standard action panel with an Ok and Cancel button if true
   * @param fNoActionPanel Do not create standard action panel with an Ok and Cancel button if true
   */
  public void setRemoveStandardActionButtons( boolean fNoActionPanel )
  { m_fNoActionPanel = fNoActionPanel; }
  
  
  /**
   * Find the Frame instance for a component
   * @param comp The component to search it's ancestors
   * @return
   */
  public static Frame findParent( Component comp )
  {
    Container parent = comp.getParent();
    
    while( parent != null)
    {
      if ( parent instanceof Frame )
        return (Frame)parent;
      
      parent = parent.getParent();
      
    }
    
    return null;
    
  }
  
  /**
   * Return the UserPanel instance supplied in the constructor
   * @return
   */
  public JPanel getUserPanel()
  { return m_userPanel; }
  
  
  /**
   * Returns true if the Canel button was clicked to close the dialog
   * @return
   */
  public boolean wasCancelled()
  { return m_fWasCancelled; }
  
  protected void setCancelled( boolean fCancelled )
  { m_fWasCancelled = fCancelled; }
  
  /**
   * Gets the command string of the last button that was clicked on the dialog
   * @return
   */
  public String getButtonCommand()
  { return m_strCommand; }
  
  /**
   * Sets the default button for this dialog
   * @param btnDefault VwButton
   */
  public void setDefaultButton( JButton btnDefault )
  { this.getRootPane().setDefaultButton( btnDefault ); }

  
  /**
   * Sets the FlowLayout layout manager's button alignment 
   * @param nAlign One the static constants of the FLowLayout class
   */
  public void setActionPanelButtonAlignment( int nAlign )
  { ((FlowLayout)m_actionPanel.getLayout()).setAlignment( nAlign ); }
  
  /**
   * Sets the default based on the button text 
   * @param strButtonText the text on the button to search for and make the default
   */
  public void setDefaultButton( String strButtonText )
  {
    Component[] aComp = m_actionPanel.getComponents();
    
    for ( int x = 0; x < aComp.length; x++ )
    {
      if ( aComp[ x ] instanceof JButton )
      {
        JButton btn = (JButton)aComp[ x ];
        if ( btn.getText().equalsIgnoreCase(  strButtonText ))
        {
          this.getRootPane().setDefaultButton( (JButton)aComp[ x ] );
          return;
        }
      }
    }
  }
  
  
  /**
   * gets the default buuton set for this dialog or null if none set
   * @return VwButton
   */
  public JButton getDefaultButton()
  { 	return this.getRootPane().getDefaultButton(); }

  
  /**
   * Create an action panel of VwButtons ased on the list of Actions
   * @param listActions
   * @param nAlign
   */
  public void createActionPanel( List<Action> listActions, int nAlign )
  {
    ((FlowLayout)m_actionPanel.getLayout()).setAlignment( nAlign );

    for ( Action btnAction : listActions )
    {
      VwButton btn = new VwButton();
      btn.setAction( btnAction );
      m_actionPanel.add( btn );
      
    } // end for()

   } // end createActionPanel()

   
   /**
    * Adss a buuton to the action panel that when clicked will close the dialog
    * @param strButtonText The text to appear on the button
    * @param icon And optional icon 
    * @param fDefault if true this is the default button
    * @param nPos
    * @return
    */
   public VwButton addButton( String strButtonText, Icon icon, boolean fDefault, int nPos )
   {
     VwButton btn = new VwButton();
     btn.setName( strButtonText );
     Action actionButton = new AbstractAction( strButtonText, icon )
     {
       public void actionPerformed( ActionEvent ae )
       {
         handleButtonAction( ae ); 
       }

      };

     btn.setAction( actionButton );
     
     if ( nPos < 0 )
       m_actionPanel.add( btn );
     else
       m_actionPanel.add( btn, nPos );

     if ( fDefault)
       setDefaultButton( btn );
     
     return btn;

   }
     

  protected void handleButtonAction( ActionEvent ae )
  {
    m_fWasCancelled = true;
    
    m_strCommand = ae.getActionCommand();
    JButton btnAction = (JButton)ae.getSource();
    String strButtonName = btnAction.getName();
    if ( strButtonName == null )
      strButtonName = "";
    
    if ( m_userPanel instanceof VwActionPanel )
    {
      VwActionPanel actionPanel = (VwActionPanel)m_userPanel;
        
      actionPanel.actionPerformed( ae );
      m_fWasCancelled = true;
      
      if ( actionPanel.shouldDispose() )
      {
        
        if ( ! (strButtonName.equalsIgnoreCase( "cancel" ) ) )
          m_fWasCancelled = false;
        
        VwDialog.this.dispose();
      }
      
      return;
      
    }
    
    if ( ! (strButtonName.equalsIgnoreCase( "cancel" ) ) )
      m_fWasCancelled = false;
    
    // Action was accepted by user panel so kill dialog
    VwDialog.this.dispose();
  }

    /**
    * Adss an OK button at the position specified that disposes the dialog
    * @param actionOk The Action object for the Ok button or null for default dispose action
    * @param nPos The position to add the button in the action panel or -1 to append
    * @return
    */
   public VwButton addOkButton( Action actionOk, int nPos )
   {
     VwButton btnOk = new VwButton();
     btnOk.setName( "ok" );
     if ( actionOk == null )
     {
       String strOk = VwResourceMgr.getString( "VwDialogButton.ok", "Ok" );
       actionOk = new AbstractAction(  strOk )
       {
         public void actionPerformed( ActionEvent ae )
         {
           handleButtonAction( ae );           
         }
       };

       actionOk.putValue( Action.MNEMONIC_KEY, new Integer( KeyEvent.VK_O ));
     }

     btnOk.setAction( actionOk );

     if ( nPos < 0 )
       m_actionPanel.add( btnOk );
     else
       m_actionPanel.add( btnOk, nPos );

     return btnOk;

   }

  /**
   * Adss a Cancel button at the position specified that disposes the dialog
   * @param actionCancel The Action object for the Cancel button or null for default dispose action
   * @param nPos The position to add the button in the action panel or -1 to append
   * @return
   */
  public VwButton addCancelButton( Action actionCancel, int nPos )
  {
    VwButton btnCancel = new VwButton();
    btnCancel.setName( "cancel" );
    if ( actionCancel == null )
    {
      String strCancel = VwResourceMgr.getString( "VwDialogButton.cancel", "Cancel" );
      actionCancel = new AbstractAction(  strCancel )
      {
        public void actionPerformed( ActionEvent ae )
        {
          handleButtonAction( ae );
        }
      };
      
      actionCancel.putValue( Action.MNEMONIC_KEY, new Integer( KeyEvent.VK_C ));

    }

    btnCancel.setAction( actionCancel );

    if ( nPos < 0 )
      m_actionPanel.add(  btnCancel );
    else
      m_actionPanel.add(  btnCancel, nPos );

    return btnCancel;

  }

 /**
  * Creates a standard action panel with an OK ancd close button
  * @param nAlign One the FlowLayout align options
  */
  public void createStandardActionPanel( int nAlign )
  {
    ((FlowLayout)m_actionPanel.getLayout()).setAlignment( nAlign );
    VwButton btnOK = addOkButton( null, -1);
    addCancelButton( null, -1 );
    this.setDefaultButton( btnOK );

 
  }
} // end class VwDialog{}

// *** End of VwDialog.java ***
