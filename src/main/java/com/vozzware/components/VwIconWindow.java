/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwIconWindow.java

============================================================================================
*/

package com.vozzware.components;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedList;


/**
 * All panels used in the VwWizardMgr must derived from this class. Before a
 * panel is displayed, the proceed() method is called to determine if the next
 * or previous panel is allowed.  If True is returned, the next panel will be
 * allowed; otherwise, the VwWizardMgr will stay on the current panel.  If
 * the proceed() method returns True, then the skip() method is called. If
 * skip() returns True, the VwWizardMgr skips to the next panel in the list.
 * A reference to the VwWizardMgr is passed to both of these methods to
 * allow context information to be passed.  The setUserObject() and
 * getUserObject() methods can be used to pass user defined context data
 * from panel to panel.
 */
public class VwIconWindow extends JComponent implements DropTargetListener
{

  public static final int ITC_SELECTED = 70000;
  public static final int ITC_GOTFOCUS = 70001;
  public static final int ITC_LOSTFOCUS = 70002;
  public static final int ITC_DROPPED = 70003;

  private ImageIcon     m_icon;               // The Image from the gif file

  private String        m_strIconText;        // Associated Text

  private int           m_iconX = 0;          // Position of the icon
  private Font          m_font = new Font( "Dialog", 0, 10 );

  private int           m_iconWidth;          // The width of the icon
  private int           m_iconHeight;         // The height of the icon

  private boolean       m_fHasFocus = false;  // Window focus fflag

  private Color         m_clrBackground = Color.white;
  private Color         m_clrText = Color.black;
  private Color         m_clrSelectedBackground = Color.black;
  private Color         m_clrSelectedText = Color.white;

  private LinkedList    m_listActionListeners = new LinkedList();

  private JPopupMenu    m_userPopupMenu;      // User assigned popup menu

  private Object        m_obj;                // User assigned object

  private DropTarget    m_dropTarget;         // DragNDrop

  private DataFlavor[]  m_adfAllowedTypes;     // Allowable data flavors for drop target

  private Object        m_dropData;           // User data associated with a drop event

  /**
   * Constructor
   *
   * @param strGifFileName The gif file to load
   * @param strIconText Optional Text painted below the icon
   */
  public VwIconWindow( URL url, String strIconText )
  {
    m_icon = new ImageIcon( url );

    init( strIconText );

  }


  /**
   * Constructor
   *
   * @param strGifFileName The gif file to load
   * @param strIconText Optional Text painted below the icon
   */
  public VwIconWindow( String strGifFileName, String strIconText )
  {
    m_icon = new ImageIcon( strGifFileName );

    init( strIconText );

  } // end VwIconWindow

  private void init( String strIconText )
  {

    m_strIconText = strIconText;

    m_iconWidth = m_icon.getIconWidth();
    m_iconHeight = m_icon.getIconHeight();

    addAncestorListener( new AncestorListener()
    {
      public void ancestorAdded( AncestorEvent event )
      {
        Graphics g = getGraphics();
        FontMetrics fm = g.getFontMetrics();
      } // end ancestorAdded()

      public void ancestorMoved(AncestorEvent event)
      { ; }

      public void ancestorRemoved(AncestorEvent event)
      { ; }


    }); // end addAncestorListener(

    // Get a focus listener
    addFocusListener( new FocusListener()
    {
      public void focusGained( FocusEvent fe )
      {
        fireActionEvent( ITC_GOTFOCUS );

        m_fHasFocus = true;
        repaint();

      } // end focusGained()

      public void focusLost( FocusEvent fe )
      {
        fireActionEvent( ITC_LOSTFOCUS );
        m_fHasFocus = false;
        repaint();

      } // end focusLost()

    } ); // end addFocusListener()

    // Get mouse click activity
    addMouseListener( new MouseAdapter()
    {
      public void mouseClicked( MouseEvent me )
      {
        if ( me.getClickCount() > 1 &&
            ((me.getModifiers() & MouseEvent.BUTTON1_MASK) == MouseEvent.BUTTON1_MASK ) )   // Double click
        {
           fireActionEvent( ITC_SELECTED );
        }

        requestFocus();
      } // end mouseClicked()

      public void mouseReleased( MouseEvent e )
      {

        requestFocus();
        // Activate popup menu if user specified one
        if ( e.isPopupTrigger() )
        {
          if ( m_userPopupMenu != null )
            m_userPopupMenu.show( VwIconWindow.this, e.getX(), e.getY() );
        }
      } // end mouseReleased()

    }); // end addMouseListener()

  } // end VwIconWindow

  /**
   * Enables this icon window to be a drop target. Whwn an allowable drop occurs, this
   * window sends an ActionEvent to all registered listeners with the id set to ITC_DROPPED.
   *
   *
   * @param adfAllowedTypes An array of DataFlvor objects that defines the allowable data types
   * for a drop operation. If the this param is null, it disables this window as a drop target.
   */
  public void setDropEnabled( DataFlavor[] adfAllowedTypes )
  {
    m_adfAllowedTypes = adfAllowedTypes;
    if ( adfAllowedTypes == null )
    {
      if ( m_dropTarget != null )
      {
        m_dropTarget.removeDropTargetListener( this );
        m_dropTarget.setActive( false );
        m_dropTarget = null;
      }

      return;

    }

    m_dropTarget = new DropTarget( this, this );


  }
  /**
   * Gets the Icon text
   */
  public String getIconText()
  { return m_strIconText; }


  /**
   * Returns the user data associated with a drop event or null if no drop event occured
   */
  public Object getDropData()
  { return m_dropData; }


  /**
   * Gets the Font used for the icon text
   *
   * @return The Font object used to paint the icon window's text
   */
  public Font getTextFont()
  { return m_font; }


  /**
   * Sets the Font used for the icon text
   *
   * @parem font The Font object to  use to paint the icon window's text
   */
  public void setTextFont( Font font )
  { m_font = font; }

  /**
   * Gets the current background color
   *
   * @return The Color used to paint the icon window's background
   */
  public Color getBackgroundColor()
  { return m_clrBackground; }


  /**
   * Sets the current background color
   *
   * @param clrBackground The Color used to paint the icon window's background
   */
  public void setBackgroundColor( Color clrBackground )
  { m_clrBackground = clrBackground; }


  /**
   * Gets the current background color when the window has the focus
   *
   * @return The Color used to paint the icon window's background
   */
  public Color getSelectedBackgroundColor()
  { return m_clrSelectedBackground; }


  /**
   * Sets the background color to use when this window has the focus
   *
   * @param clrBackground The Color used to paint the icon window's background when it has the focus
   */
  public void setSelectedBackgroundColor( Color clrSelectedBackground )
  { m_clrSelectedBackground  = clrSelectedBackground; }



  /**
   * Gets the current text color
   *
   * @return The Color used to paint the icon window's text
   */
  public Color getTextColor()
  { return m_clrText; }


  /**
   * Sets the current background color
   *
   * @param clrText The Color used to paint the icon window's text
   */
  public void setTextColor( Color clrText )
  { m_clrText = clrText; }


  /**
   * Gets the current text color when the window has the focus
   *
   * @return The Color used to paint the icon window's text when it has the focus
   */
  public Color getSelectedTextColor()
  { return m_clrSelectedText; }


  /**
   * Sets the text color to use when this window has the focus
   *
   * @param clrText The Color used to paint the icon window's text when it has the focus
   */
  public void setSelectedTextColor( Color clrSelectedText )
  { m_clrSelectedText  = clrSelectedText; }



  /**
   * Adds an action Listener
   *
   * @param sctionListener The action Listeners to add
   */
  public synchronized void addActionListener( ActionListener actionListener )
  { m_listActionListeners.add( actionListener ); }


  /**
   * Removes an action Listener
   *
   * @param sctionListener The action Listeners to remove
   */
  public synchronized void removeActionListener( ActionListener actionListener )
  { m_listActionListeners.remove( actionListener ); }


  /**
   * Sets the popup menu to be activated on the popup mouse activation click
   *
   * @param userPopupMenu The users's popup menu to activate on the popup mouse click
   */
  public void setPupupMenu( JPopupMenu userPopupMenu )
  { m_userPopupMenu = userPopupMenu;  }


  /**
   * Gets the popup menu (if any ) previously set
   *
   */
  public JPopupMenu getPupupMenu()
  { return m_userPopupMenu;  }


  /**
   * Sets a user data object associated with this icon window
   *
   * @param obj The user Object
   */
  public void setUserObject( Object obj )
  { m_obj = obj; }


  /**
   * Gets the user data object associated with this icon window or null if none assigned
   *
   */
  public Object getUserObject()
  { return m_obj; }

  /**
   * Draw the icon and text
   */
  public void paint( Graphics g )
  {

    int  nTextX = 0;

    if ( m_strIconText != null )
    {
      g.setFont( m_font );
      FontMetrics fm = g.getFontMetrics();

      int nTextLen = fm.stringWidth( m_strIconText );

      if ( nTextLen > m_iconWidth )
        m_iconX = nTextLen / 2 - m_iconWidth / 2;
      else
        nTextX = m_iconWidth / 2 - nTextLen / 2;

      if ( m_fHasFocus )
        g.setColor( m_clrSelectedBackground );
      else
        g.setColor( m_clrBackground );

      g.fillRect(nTextX , m_icon.getIconHeight() + fm.getLeading(), nTextLen, fm.getHeight() );

     }

     // Draw the icon
     m_icon.paintIcon( this, g, m_iconX, 0 );

     // If specified, draw the text
     if ( m_strIconText != null )
     {
       if ( m_fHasFocus )
         g.setColor( m_clrSelectedText );
       else
         g.setColor( m_clrText );

       g.drawString( m_strIconText, nTextX , m_icon.getIconHeight() + 10 );
     }

  } // end paint


  /**
   * Fire ActionEvents to all registered listeners
   */
  private synchronized void fireActionEvent( int id )
  {
    ActionEvent ae = new ActionEvent( this, id, null );

    for ( Iterator iListeners = m_listActionListeners.iterator(); iListeners.hasNext(); )
    {
      ActionListener al = (ActionListener)iListeners.next();
      al.actionPerformed( ae );

    } // end for()

  }


  // **** Drop Events ****

  /**
   * is invoked when you are dragging over the DropSite
   *
   */
  public void dragEnter( DropTargetDragEvent event )
  { ; }


  /**
   * is invoked when you are exit the DropSite without dropping
   *
   */

  public void dragExit( DropTargetEvent event )
  { ; }

  /**
   * is invoked when a drag operation is going on
   *
   */
  public void dragOver( DropTargetDragEvent event )
  { ; }

  /**
   * a drop has occurred
   *
   */
  public void drop( DropTargetDropEvent event )
  {

    try
    {
      Transferable transferable = event.getTransferable();

      if ( m_adfAllowedTypes != null )
      {
        for ( int x = 0; x < m_adfAllowedTypes.length; x++ )
        {

          if ( transferable.isDataFlavorSupported( m_adfAllowedTypes[ x ]  ) )
          {
            event.acceptDrop( DnDConstants.ACTION_COPY_OR_MOVE );
            event.getDropTargetContext().dropComplete( true );
            m_dropData = transferable.getTransferData( m_adfAllowedTypes[ x ] );
            fireActionEvent( ITC_DROPPED );
            return;
          } // end if

        } // end for

      } // end if

      event.rejectDrop();        // DataFlavor not supported

    }
    catch( Exception exception )
    {
      exception.printStackTrace();
      System.err.println( "Exception" + exception.getMessage() );
      event.rejectDrop();
    }

  } // end drop()

  /**
   * is invoked if the use modifies the current drop gesture
   *
   */
  public void dropActionChanged( DropTargetDragEvent event )
  { ;  }

} // end class VwIconWindow{}


// *** End VwIconWindow.java

