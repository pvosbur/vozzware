/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwPickList.java

============================================================================================
*/

package com.vozzware.components;

import com.vozzware.ui.VwAction;
import com.vozzware.ui.VwActionEvent;
import com.vozzware.ui.VwIcon;
import com.vozzware.ui.VwListBox;
import com.vozzware.ui.menu.VwMenuMgr;
import com.vozzware.util.VwDelimString;

import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.ListCellRenderer;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import java.util.LinkedList;

public class VwPickList extends VwListBox
{
  private PickListModel     m_listModel = new PickListModel();

  private Color             m_clrSelectedForeground = Color.blue;
  private Color             m_clrSelectedBackground = Color.white;

  private JCheckBox         m_chkItemCheck = new JCheckBox();
  private JLabel            m_lblIcon= new JLabel();

  private Font              m_chkBoxFont = new Font( "Times New Roman", 0, 10 );

  private int               m_nLineHeight;

  private Icon              m_iconDefault = null;
  
  private java.util.List    m_listSelectionListeners = new LinkedList();

  private JPopupMenu m_selPopup = new JPopupMenu();

  
  private VwAction         m_actionSelAll;
  private VwAction         m_actionSelNone;
  
  private VwMenuMgr        m_menuMgr;
  private String            m_strPopupMenuName;
  
  private Color             m_dbPickListColor = Color.white; 
    
  PickListCellRenderer      m_plCellRenderer;
  
  class PickListModel extends DefaultListModel
  {
    public void fireChange()
    {
      int nSize = this.getSize() - 1;

      this.fireContentsChanged( this, 0,  nSize );

    }

  } // end class {}

  /**
   * Sets background color of the listbox
   */
  public void setPickListBackground( Color clrBackground )
  {
    super.setBackground( clrBackground );
    m_dbPickListColor = clrBackground;
    m_plCellRenderer.setBackground( clrBackground );
  
  }

  /**
   * Class that holds the item text and selected mode for display
   */
  class PickListItem
  {
    String      m_strText;      // Item Text
    boolean     m_fSelected;    // Selected Mode

    Color       m_clrBackground;
    Color       m_clrForeground;

    VwIcon     m_icon;
    
    PickListItem( String strText )
    {
      m_strText = strText;
      m_fSelected = false;
      m_icon = null;
      
    } // end PickListItem()

    PickListItem( String strText, VwIcon icon )
    {
      m_strText = strText;
      m_fSelected = false;
      m_icon = icon;
      
    } // end PickListItem()
    
  } // end class PickListItem{}

  /**
   * Pick list renderer uses check boxes for its display
   */
  class PickListCellRenderer extends JPanel implements ListCellRenderer
  {
     
     PickListCellRenderer()
     {
       this.setLayout( new BoxLayout( this, BoxLayout.X_AXIS) );
       
       this.add( m_lblIcon );
       this.add( m_chkItemCheck );
       this.setBackground( m_dbPickListColor );
       
       m_chkItemCheck.setBackground( m_dbPickListColor );

       m_chkItemCheck.setFont( m_chkBoxFont );


     }
     public Component getListCellRendererComponent( JList list,
                                                    Object value,
                                                    int ndx,
                                                    boolean isSelected,
                                                    boolean cellHasFocus )
     {


       PickListItem item = (PickListItem)value;

       if ( item.m_icon != null )
         m_lblIcon.setIcon( item.m_icon );
       else
         m_lblIcon.setIcon( m_iconDefault );
         
       m_chkItemCheck.setText( item.m_strText );
       m_chkItemCheck.setSelected( item.m_fSelected );
       
       if ( item.m_fSelected )
       {
         m_chkItemCheck.setForeground( m_clrSelectedForeground );
         m_chkItemCheck.setBackground( m_clrSelectedBackground );
       }
       else
       {
         if ( item.m_clrBackground != null )
           m_chkItemCheck.setBackground( item.m_clrBackground );
         else
           m_chkItemCheck.setBackground( Color.white );

         if ( item.m_clrForeground != null )
           m_chkItemCheck.setForeground( item.m_clrForeground );
         else
           m_chkItemCheck.setForeground( Color.black );

       }

       m_chkItemCheck.setBackground( m_dbPickListColor );
              
       this.doLayout();
       return this;

     } // end getListCellRendererComponent()

  } // end class PickListCellRenderer{}


  private PickListItem[]    m_aPickListItems;

  public VwPickList()
  { this( null, null ); }
  
  
  /**
   * Constructor
   */
  public VwPickList( VwMenuMgr menuMgr, String strPopupMenuName )
  {
    super();
    
    m_menuMgr = menuMgr;
    m_strPopupMenuName = strPopupMenuName;
    m_plCellRenderer = new PickListCellRenderer();    
    
    this.setCellRenderer( m_plCellRenderer );

    try
    {
      buildPopup();
    }
    catch( Exception ex )
    {
      ex.printStackTrace();
      JOptionPane.showMessageDialog( null, ex.toString() );
    }
    
    FontMetrics fm = m_chkItemCheck.getFontMetrics( m_chkBoxFont );
    m_nLineHeight = fm.getHeight() + fm.getLeading();

    this.setFixedCellHeight( m_nLineHeight );

    this.addMouseListener( new MouseAdapter()
    {

      public void mouseReleased( MouseEvent me )
      {
        if ( me.isPopupTrigger() )
        {
          doPopup(me );
          
        }

      }

      private void doPopup(MouseEvent me)
      {
        if ( m_menuMgr != null )
        {
          try
          {
            m_menuMgr.registerPopupAction( m_strPopupMenuName, "selNone", m_actionSelNone );
            m_menuMgr.registerPopupAction( m_strPopupMenuName, "selAll", m_actionSelAll );
            m_menuMgr.showPopup( VwPickList.this, m_strPopupMenuName, me.getX(), me.getY() );
          }
          catch( Exception ex )
          {
            JOptionPane.showMessageDialog( VwPickList.this, ex.toString() );
            return;
          }
        }
        else
          m_selPopup.show( VwPickList.this, me.getX(), me.getY() );
        
        me.consume();
		
	    }

      public void mousePressed( MouseEvent me )
      {

        if ( me.isPopupTrigger()  )
        {
        	doPopup(me);
            return;
        }
        if ( me.getButton() == 0 && me.getModifiers() == 4 )
          return;
        
        if ( me.getButton() > 1 )
          return;
    
        
        int ndx = VwPickList.this.locationToIndex( me.getPoint() );
    
        m_aPickListItems[ ndx ].m_fSelected =  !(m_aPickListItems[ ndx ].m_fSelected );
        fireSelectionEvents( m_aPickListItems[ ndx ], ndx );
    
        VwPickList.this.repaint( 0 );
      }
    });

  } // end VwPickList()

  
  /**
   * enables/diables the Select All and SelectNone menuitems
   * @param fEnable
   */
  public void enableMenuItems( boolean fEnable )
  {
    m_actionSelAll.setEnabled( fEnable );
    
    m_actionSelNone.setEnabled( fEnable );
      
  }
  
  /**
   * Sets the menu mgr to use for popups from some client instance using the pick list.
   * All menu actions are assumed to be registered by the clinet object
   *  
   * @param menuMgr The VwMeneuMgr to use when displaying the popup menu. This overrides
   * the default popup action
   */
  public void setMenuMgr( VwMenuMgr menuMgr, String strPopupMenuName )
  { 
    m_menuMgr = menuMgr;
    m_strPopupMenuName = strPopupMenuName;
    
  }
  
  
  /**
   * Adss a new meuitem and action handler to this popup
   * @param action The menu action 
   * 
   * @param fAddSep if true, add a menu separator 
   */
  public void addPopupAction( Action action, String strMenuId, boolean fAddSep )
  {
    if ( fAddSep )
      m_selPopup.addSeparator();
    
    JMenuItem mi = new JMenuItem( action );
    
    mi.setName( m_strPopupMenuName + "." +  strMenuId );
    m_selPopup.add( mi );
    
  }
    

  /**
   * Build popup menu for selectAll/SelectNne columns
   */
  private void buildPopup() throws Exception
  {
    
    m_actionSelAll = new VwAction()
    {
      public void actionPerformed( VwActionEvent ae )
      {
        selectAll();
      }
    };

    m_actionSelNone = new VwAction()
    {
      public void actionPerformed( VwActionEvent ae )
      {
        selectNone();
      }
    };


    if ( m_menuMgr != null )
    {
      if ( ! m_menuMgr.isPopupMenuItemExists(m_strPopupMenuName, "selNone" ))
      {
        m_menuMgr.addPopupAction( m_strPopupMenuName, "selNone", "Select None", VwMenuMgr.FIRST, m_actionSelNone );
        m_menuMgr.addPopupAction( m_strPopupMenuName, "selAll", "Select All", VwMenuMgr.FIRST, m_actionSelAll );
      }
      
    }
    else
    {
      JMenuItem miSelNone = new JMenuItem( "Select none" );
      miSelNone.addActionListener( m_actionSelNone );
      JMenuItem miSelAll = new JMenuItem( "Select all" );
      miSelAll.addActionListener( m_actionSelAll );
      m_selPopup.add( miSelAll );
      m_selPopup.add( miSelNone );

    }
    
  }

  /**
   * Adds a pick list selection listener
   * @param listener The listener to receive VwPickListSelectionEvents
   */
  public synchronized void addSelectionListener( IVwPickListSelectionListener listener )
  {
    if ( m_listSelectionListeners.lastIndexOf( listener ) < 0 )
      m_listSelectionListeners.add( listener );

  } // end addSelectionListener()


  /**
   * Removes a pick list selection listener
   * @param listener The listener to remove
   */
  public synchronized void removeSelectionListener( IVwPickListSelectionListener listener )
  {
    m_listSelectionListeners.remove( listener );

  } // end removeSelectionListener()


  private synchronized void fireSelectionEvents( PickListItem item, int nItemNbr )
  {

    VwPickListSelectionEvent event = new VwPickListSelectionEvent( this, item.m_strText, item.m_fSelected, nItemNbr);

    for ( Iterator iListeners = m_listSelectionListeners.iterator(); iListeners.hasNext(); )
    {
      IVwPickListSelectionListener listener = (IVwPickListSelectionListener)iListeners.next();

      listener.selectionStateChanged( event );

    } // end for()

  } // end fireSelectionEvents


  /**
   * Returns the height of a listbox item
   * @return
   */
  public int getLineHeight()
  { return m_nLineHeight; }


  public void setDefaultIcon( VwIcon iconDefault )
  { m_iconDefault = iconDefault; }
  
  /**
   * Sets the list boxbox data from an array of strings
   *
   * @param astrString array of strings to be displayed as check boxes
   */
  public void setPickListData( String[] astrPickListData )
  {

    m_aPickListItems = new PickListItem[ astrPickListData.length ];

    for ( int x = 0; x < astrPickListData.length; x++ )
    {
      m_aPickListItems[ x ] = new PickListItem( astrPickListData[ x ] );
      m_listModel.addElement( m_aPickListItems[x ] );
    }

    this.setModel( m_listModel );

  } // end setPickListData()


  /**
   * Sets the foreground and background color attributes for an item in the list
   *
   * @param nItemNbr index in the list to set color attributes for
   *
   * @param clrForeground The foreground color
   * @param clrBackground The background color
   */
  public void setItemColors( int nItemNbr, Color clrForeground, Color clrBackground )
  {
    m_aPickListItems[ nItemNbr ].m_clrForeground = clrForeground;
    m_aPickListItems[ nItemNbr ].m_clrBackground = clrBackground;

  } // end setItemColors()


  /**
   * Sets the Vwon to display for the item numbr in the pick list
   * @param nItemNbr
   * @param icon
   */
  public void setIcon( int nItemNbr, VwIcon icon  )
  { m_aPickListItems[ nItemNbr ].m_icon = icon; }
  
  
  /**
   * Return a list of selected items in a String array or null if no items selected
   */
  public String[] getSelectedItems()
  {
    VwDelimString dlmsSelected = new VwDelimString();

    for ( int x = 0; x < m_aPickListItems.length; x++ )
    {
      if ( m_aPickListItems[ x ].m_fSelected )
        dlmsSelected.add( m_aPickListItems[ x].m_strText );
    }

    String[] astrSelected = null;

    if ( dlmsSelected.count() > 0 )
      astrSelected = dlmsSelected.toStringArray();

    return  astrSelected;

  }  // end getSelectedItems()


  public void selectAll()
  {
    selectAll( true );
    
  }
  
  
  /**
   * Selects all items in the list
   * 
   * @param fSendNotification if true, fire notifiaction selection event
  */
  public void selectAll( boolean fSendNotification )
  {
    for ( int x = 0; x < m_aPickListItems.length; x++ )
    {
       m_aPickListItems[ x ].m_fSelected = true;
       if ( fSendNotification )
         fireSelectionEvents( m_aPickListItems[ x ], x );

    }  // end for

    this.repaint( 0 );

  } // end selectAll()


  public void selectNone()
  {
    selectNone( true );
    
  }
  
  
  /**
   * De-Selects all items in the list
   * 
   * @param fSendNotification if true, fire notifiaction selection event
  */
  public void selectNone( boolean fSendNotification )
  {
    for ( int x = 0; x < m_aPickListItems.length; x++ )
    {
       m_aPickListItems[ x ].m_fSelected = false;

       if ( fSendNotification )
         fireSelectionEvents( m_aPickListItems[ x ], x );

    }  // end for

    this.repaint( 0 );
  } // end selectNone()
  

  /**
   * Select/deselect item
   * @param nItemNbr The item nbr in the list to affect
   * @param fSelect true to select, false to deselect
   */
  public void selectItem( int nItemNbr, boolean fSelect )
  {
    selectItem( nItemNbr, fSelect );
    
  }
  
  /**
   * Select/deselect item
   * @param nItemNbr The item nbr in the list to affect
   * @param fSelect true to select, false to deselect
   * @param fSendNotification if true, fire notifiaction selection event
   */
  public void selectItem( int nItemNbr, boolean fSelect, boolean fSendNotification )
  {
    m_aPickListItems[ nItemNbr ].m_fSelected = fSelect;
    
    if ( fSendNotification )
      fireSelectionEvents( m_aPickListItems[ nItemNbr ], nItemNbr );
    

  }

} // end class VwPickList{}

// *** End of VwPickList.java ***