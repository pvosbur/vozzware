/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwModelessMsgBox.java

Create Date: Apr 11, 2003
============================================================================================
*/
package com.vozzware.ui;                 // This package

import javax.swing.JPanel;
import java.awt.Button;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
/**
 * Modeless message box that can be defined with action buttons for
 * custom behavior.  If action buttons are used, then the user of this class must implement
 * the ActionListener interface.
 *
 * NOTE: The user is responsible for calling the dispose() method to destroy the message box.
 */
public class VwModelessMsgBox extends VwDialog
{
  private static final int BTN_SPACING = 10;

  private Frame  m_parent;		            // Parent's frame window

  private VwLabel[]	  m_alblMsg = null;	      // Array of labels one for each message line

  private List      m_listBtns = null;	      // Optional array of action buttons

  private int       m_nNbrMsgLines;         // Nbr of message text lines to create

  private int       m_nMaxCharsPerLine;     // Max width of msg line based on font char width

  private String    m_strMsg;               // Initial msg text from constructor

  private Component m_compToCenterIn;       // Parent component to center this message box within

  private boolean   m_fNeedLayout = true;   // Flag to perform layout (done once)

  /**
   * Constructs a modeless message box object as specified
   *
   * @param parent - The parent frame object
   * @param strCaption - A string with the title bar text
   * @param strMsg - A string with the initial message
   * @param nNbrMsgLines - The number of message lines to create (optional if strMsg is not null)
   * @param nMaxCharsPerLine - The size of the message lines to be created
   * @param compToCenterIn - Component to center this message box in (null = full screen)
   *
   */
  public VwModelessMsgBox( Frame parent, String strCaption, String strMsg, int nNbrMsgLines,
                            int nMaxCharsPerLine, Component compToCenterIn )
  {
    super( parent, new VwPanel(), false, strCaption );

	m_parent = parent;

    m_strMsg = strMsg;

    m_nNbrMsgLines = nNbrMsgLines;

    m_nMaxCharsPerLine = nMaxCharsPerLine;

    m_compToCenterIn = compToCenterIn;
    //setResizable( false );

    setBackground( Color.lightGray );

    addNotify();

    getUserPanel().setLayout( null );

    createLabels();

  } // end VwModelessMsgBox()


  /**
   * Constructs a basic message box with a line of text
   *
   * @param parent - The parent frame object
   * @param strCaption - A string with the title bar text
   * @param strMsg - A string with the initial message
   * @param compToCenterIn - Component to center this messag box in (null = full screen)
   *
  */
  public VwModelessMsgBox( Frame parent, String strCaption, String strMsg,
                            Component compToCenterIn )
  { this( parent, strCaption, strMsg, 0, 0, compToCenterIn );  }


  /**
   * Constructs a message box with an empty message string to be set later
   *
   * @param parent - The parent frame object
   * @param strCaption - A string with the title bar text
   * @param nNbrMsgLines - The number of message lines to create
   * @param nMaxCharsPerLine - The size of the message lines to be created
   * @param compToCenterIn - Component to center this messag box in (null = full screen)
   *
  */
  public VwModelessMsgBox( Frame parent, String strCaption, int nNbrMsgLines,
                            int nMaxCharsPerLine, Component compToCenterIn )
  { this( parent, strCaption, null, nNbrMsgLines, nMaxCharsPerLine, compToCenterIn );  }


  /**
   * Adds a button to the modeless message box.  Button(s) will be centered within
   * the text lines at the bottom of the message box.
   *
   * @param strLabel - A string with the button label
   * @param actionListener - An ActionListener object to receive button events
   */
  public void addButton( String strLabel, ActionListener actionListener )
  {
    if ( m_listBtns == null )
      m_listBtns = new LinkedList();

    VwButton btn = new VwButton();      // Cretae the button
    btn.setText( strLabel );
    btn.addActionListener( actionListener );  // add action listener

    m_listBtns.add( btn );              // Put button in the vector

    add( btn );                               // Add the button to this conrainer

  } // end addButton()


  /**
   * Sets the message text for the line number (0 based) specified
   *
   * @exception  ArrayIndexOutOfBoundsException if the line number is greater than
   * or equal to the total number of message lines determined by the constructor parameters.
   */
  public final void setMsgLineText( int nLineNbr, String strText )
  { m_alblMsg[ nLineNbr ].setText( strText ); }


  /**
   * Gets the number of message lines defined for this message box
   *
   * @return An int with the number of message lines for this message box
   */
  public final int getNbrOfMsgLines()
  { return m_nNbrMsgLines; }


  /**
   * Overrides the show() method to determine the size of the message box and
   * display the message box.
   */
  public void show()
  {
    if ( m_fNeedLayout )
      layoutMsgBox();

    super.show();
    requestFocus();

  } // end show()


  /**
   * Creates the array of line labels for the message box
   */
  private void createLabels()
  {
    int nLines = 0;       // Line count for pre-defined message
    JPanel panel = getUserPanel();
    
    // *** Count the nbr of lines in the string I.E Nbr of '\n' chars

    if ( m_strMsg != null )
    {
      int nLastPos = 0;
      int nPrevPos = 0;

      while( nLastPos >= 0 )
      {
        nLines++;
        nLastPos = m_strMsg.indexOf( '\n', nPrevPos );
        nPrevPos = nLastPos + 1;

      } // end while()

    } // end if

    if ( nLines > m_nNbrMsgLines )
      m_nNbrMsgLines = nLines;

    // *** Create an array of labels, one for each message line

    m_alblMsg = new VwLabel[ m_nNbrMsgLines ];

    int nLastPos = 0;
    int nPrevPos = 0;

    for ( int x = 0; x < nLines; x++ )
    {
      nLastPos = m_strMsg.indexOf( '\n', nPrevPos );
      if ( nLastPos < 0 )
        nLastPos = m_strMsg.length();

      m_alblMsg[x] = new VwLabel();
      m_alblMsg[x].setText( m_strMsg.substring( nPrevPos, nLastPos ) );

      panel.add( m_alblMsg[x] );    // add label to the container

      nPrevPos = ++nLastPos;

    } // end for()

    // *** Add any additional labels if tot nbr of lines requested exceeds predefined
    // *** mesg or if no predefined message was specified

    for ( int x = nLines; x < m_nNbrMsgLines; x++ )
    {
      m_alblMsg[x] = new VwLabel();
      panel.add( m_alblMsg[x] );    // add label to the container

    } // end for

  } // end createLabels()


  /**
   * Lays out the text and buttons of the message box
  */
  private void layoutMsgBox()
  {
    m_fNeedLayout = false;

    // *** Need graphics context

    Graphics g = getGraphics();

    // Font metrics to comput lien heights

	FontMetrics fm = g.getFontMetrics();

    int nMaxStringWidth = 0;

    if ( m_nMaxCharsPerLine > 0 )
      nMaxStringWidth = fm.stringWidth( "W" ) * m_nMaxCharsPerLine;

    for ( int x = 0; x < m_nNbrMsgLines; x++ )
    {

      int nStringWidth = fm.stringWidth( m_alblMsg[x].getText() );

      // *** if the length a line exceeds the max then adjust thje max to a predefined msg text

      if ( nStringWidth > nMaxStringWidth )
        nMaxStringWidth = nStringWidth;

    } // end for()

    int nTitleWidth = fm.stringWidth( getTitle() ) + 64;

    if ( nTitleWidth > nMaxStringWidth  )
      nMaxStringWidth = nTitleWidth;

    // Now readjust the label widths

    Insets ins = getInsets();

    // *** Acount ofr insets in max width

    for ( int x = 0; x < m_nNbrMsgLines; x++ )
      m_alblMsg[x].setSize( nMaxStringWidth, fm.getHeight() );

    int nMaxBtnWidth = 0;

    if ( m_listBtns != null )
    {
      for ( Iterator iBtns = m_listBtns.iterator(); iBtns.hasNext(); )
      {
        Button btn = (Button)iBtns.next();

        int nStringWidth = fm.stringWidth( btn.getLabel() ) + 10;
        nMaxBtnWidth += nStringWidth + BTN_SPACING;
        btn.setSize( nStringWidth, fm.getHeight() + 8 );

      } // end for()

    } // end if

    // Compute total size

    int nLineHeight = fm.getHeight();
    int nyTextMargin = nLineHeight;

    nyTextMargin += ins.top;

    int nTotWidth = ( nMaxBtnWidth > nMaxStringWidth ) ? nMaxBtnWidth : nMaxStringWidth;

    int nMbWidth = nTotWidth + 20;                        // Also account for Borders
	  int nMbHeight = m_nNbrMsgLines * nLineHeight + ins.top  + nyTextMargin +
                    ins.bottom;

    if ( m_listBtns != null )
      nMbHeight += (fm.getHeight() + 12 ) * 2;            // Account for button height

    getUserPanel().setPreferredSize( new Dimension( nMbWidth, nMbHeight ) );

	int xTextStart = nMbWidth / 2 - nMaxStringWidth / 2;

    int nyStart = nyTextMargin;

    // *** Recompute starting position of our Labels and add then to this container

    for ( int x = 0; x < m_alblMsg.length; x++ )
    {
      m_alblMsg[ x ].setLocation( xTextStart, nyStart );  // New x,y pos
      nyStart += nLineHeight;                             // Next start pos
    }

    nyStart += fm.getHeight() + 12;   // Add space between last label and button
                                      // start
    if ( m_listBtns != null )
    {
      int xBtnStart = ( nMbWidth / 2 ) - nMaxBtnWidth / 2;

      for ( Iterator iBtns = m_listBtns.iterator(); iBtns.hasNext(); )
      {
        Button btn = (Button)iBtns.next();
        btn.setLocation( xBtnStart, nyStart );
        Dimension dim = btn.getSize();

        xBtnStart += dim.width + BTN_SPACING;
      }

    } // end ( m_listBtns != null )

    // Center the message box

    VwFormBase.center( m_compToCenterIn, this );

  } // end layoutMsgBox()


} // end class VwModelessMsgBox{}

// *** end of VwModelessMsgBox.java ***

