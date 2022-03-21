/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwMessageBoxBase.java

Create Date: Apr 11, 2003
============================================================================================
*/

package com.vozzware.ui;                                   // This package

import java.awt.Button;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class VwMessageBoxBase extends Dialog
{
  // *** Message box Styles

  public static final int INFO = 1;
  public static final int YES_NO = 2;
  public static final int YES_NO_CANCEL = 3;

  // *** Message box reason closing codes

  public static final int OK = 1;           // OK button was hit
  public static final int YES = 2;          // Yes button hit
  public static final int NO = 3;           // No Button hit
  public static final int CANCEL = 4;       // Cancel button hit

  private static final int BTN_WIDTH = 50;
  private static final int BTN_HEIGHT = 20;
  private static final int BTN_SPACING = 10;

  private int m_nReason;                    // Reason for closing the message box

  private Frame     m_parent;               // Parent's frame window

  private Label[]	  m_alblMsg = null;       // Array of labels one for each message line

  private Button    m_btnOk = null;         // Ok push button to close message box
  private Button    m_btnYes = null;        // Yes push button to close message box
  private Button    m_btnNo = null;         // No push button to close message box
  private Button    m_btnCancel = null;     // Cancel push button to close message box

  private int       m_xTextStart;           // Starting x position of text

  private int       m_yTextStart;           // Starting y position of text

  private int       m_nLines = 0;           // Number of lines in the message

  private int       m_nLineHeight;          // Height of a single line of text

  private int       m_nMaxStringWidth = 0;  // Longest text length

  private int       m_nTotButtonWidth;      // Total width of buttons

  private int       m_nyTextMargin;

  class ButtonListener implements ActionListener
  {
    public void actionPerformed( ActionEvent e )
    {
      Button btn = (Button)e.getSource();

      if ( btn == m_btnOk )
        m_nReason = OK;
      else
      if ( btn == m_btnYes )
        m_nReason = YES;
      else
      if ( btn == m_btnNo )
        m_nReason = NO;
      else
      if ( btn == m_btnCancel )
        m_nReason = CANCEL;

	    m_parent.requestFocus();
      dispose();

    } // end actionPerformed()

  } // end class ButtonListener{}

  ButtonListener m_btnListener = new ButtonListener();

  /**
   * Constructor with parameters
   *
   * @param parent - The parent frame object for the message box
   * @param strTitle - A string with the message box title
   * @param strMsg - A string with the message for the message box
   * @param nStyle - The style of the message box: INFO, YES_NO, or YES_NO_CANCEL
   * @param fModal - If True, the message box is modal
   */
  public VwMessageBoxBase( Frame parent, String strTitle, String strMsg, int nStyle, boolean fModal )
  {
    super(  parent, strTitle, fModal );

    m_parent = parent;

    setResizable( false );

    setBackground( Color.lightGray );

    addNotify();

    setLayout( null );

    Dimension dim = getDimensions( strMsg, nStyle );

	  Rectangle rect = m_parent.getBounds();

	  Insets ins = getInsets();

	  int nxMbStart = ins.left + ( rect.width / 2 - dim.width / 2 );
	  int nyMbStart = ins.top + ( rect.height / 2  );

	  if ( nxMbStart < 10 )
	    nxMbStart = 10;

	  if ( nyMbStart < 10 )
	    nyMbStart = 10;

    setBounds( nxMbStart, nyMbStart, dim.width, dim.height );

	  m_xTextStart = dim.width / 2 - m_nMaxStringWidth / 2;

    createButtons( dim, nStyle );

    int nyTextStart = m_nyTextMargin;

    // *** Recompute starting position of our Labels and add then to this container

    for ( int x = 0; x < m_alblMsg.length; x++ )
    {
      m_alblMsg[ x ].setLocation( m_xTextStart, nyTextStart ); // New x,y pos

      add( m_alblMsg[ x ] );                            // Add Label to this container

      nyTextStart += m_nLineHeight;                     // Next start pos
    }

  } // end VwMessageBoxBase()


  /**
   * Returns the reason the message box was closed
   *
   * @return The integer constant for the message box closing reason: OK, YES, NO, or CANCEL
   *
   */
  public int getReason()
  { return m_nReason; }


  /**
   * Computes the message box dimensions based upon the number of lines and the font size
   *
   * @param strMsg - The original message passed to the constructor
   * @param nStyle - The style of the message box: INFO, YES_NO, or YES_NO_CANCEL
   *
   * @return A Dimension object with the width and height required for the message box
   */
   private Dimension getDimensions( String strMsg, int nStyle )
   {
     // *** Need graphics context

     Graphics g = getGraphics();

     // Font metrics to compute line heights

	   FontMetrics fm = g.getFontMetrics();

     int nNbrButtons = 1;

     if ( nStyle == YES_NO )
       nNbrButtons = 2;

     if ( nStyle == YES_NO_CANCEL )
       nNbrButtons = 3;

     // *** Count the nbr of lines in the string I.E Nbr of '\n' chars

     int nLastPos = 0;
     int nPrevPos = 0;

     while( nLastPos >= 0 )
     {
       m_nLines++;
       nLastPos = strMsg.indexOf( '\n', nPrevPos );
       nPrevPos = nLastPos + 1;
     }

     // *** Create an array of labels, one for each message line

     m_alblMsg = new Label[ m_nLines ];

     nLastPos = 0;

     // *** Build array of strings

     for ( int x = 0; x < m_nLines; x++ )
     {
       nLastPos = strMsg.indexOf( '\n', nPrevPos );

       if ( nLastPos < 0 )
         nLastPos = strMsg.length();

       m_alblMsg[x] = new Label( strMsg.substring( nPrevPos, nLastPos ) );

       int nStringWidth = fm.stringWidth( m_alblMsg[x].getText() );

       m_alblMsg[x].setBounds( 0, 0, nStringWidth * 2, fm.getHeight() );

       if ( nStringWidth > m_nMaxStringWidth )
         m_nMaxStringWidth = nStringWidth;

       nPrevPos = ++nLastPos;

     } // end for()

     Insets ins = getInsets();

     m_nLineHeight = m_nyTextMargin = fm.getHeight();

     m_nyTextMargin += ins.top;

     m_nTotButtonWidth = ( nNbrButtons * BTN_WIDTH ) + nNbrButtons * BTN_SPACING;

     int nTotWidth = ( m_nTotButtonWidth > m_nMaxStringWidth ) ? m_nTotButtonWidth : m_nMaxStringWidth;

     int nMbWidth = nTotWidth + 20;           // Also account for Borders
	   int nMbHeight = m_nLines * m_nLineHeight + ins.top  + m_nyTextMargin +
                     ins.top + ins.bottom;    // Account for title bar

     return new Dimension( nMbWidth, nMbHeight );

  } // end getDimensions()


  /**
   * Creates the message box buttons based upon the given dimensions and style
   *
   * @param dim - A Dimension object with the width and height for the message box
   * @param nStyle - The style of the message box: INFO, YES_NO, or YES_NO_CANCEL
   */
  void createButtons( Dimension dim, int nStyle )
  {
    int xBtnStart = 0;
    int yBtnStart = 0;
    int nTotBtnWidth = 0;

    Insets ins = getInsets();

    switch( nStyle )
    {
      case INFO:

           m_btnOk = new Button( "OK" );
           m_btnOk.addActionListener( m_btnListener );

	         xBtnStart = ( dim.width / 2 ) - (BTN_WIDTH / 2 );

           if ( xBtnStart < ins.left )
             xBtnStart = ins.left + 1;

	         yBtnStart = m_nLineHeight * m_nLines + ins.top + m_nyTextMargin;

           m_btnOk.setBounds( xBtnStart, yBtnStart, BTN_WIDTH, BTN_HEIGHT );

           add( m_btnOk );

	         m_btnOk.requestFocus();

           break;

      case YES_NO:
      case YES_NO_CANCEL:

           m_btnYes = new Button( "Yes" );
           m_btnNo = new Button( "No" );

           // *** Add button click listeners

           m_btnYes.addActionListener( m_btnListener );
           m_btnNo.addActionListener( m_btnListener );

           nTotBtnWidth = BTN_WIDTH * 2 + BTN_SPACING * 2;

           if ( nStyle == YES_NO_CANCEL )
             nTotBtnWidth += BTN_WIDTH;

	         xBtnStart = ( dim.width / 2 ) - (nTotBtnWidth / 2 );

           if ( xBtnStart < ins.left )
             xBtnStart = ins.left + 1;

	         yBtnStart = m_nLineHeight * m_nLines + ins.top + m_nyTextMargin;

           m_btnYes.setBounds( xBtnStart, yBtnStart, BTN_WIDTH, BTN_HEIGHT );

           add( m_btnYes );

           m_btnNo.setBounds( xBtnStart + BTN_SPACING + BTN_WIDTH, yBtnStart, BTN_WIDTH, BTN_HEIGHT );

           add( m_btnNo );

	         m_btnNo.requestFocus();

           if ( nStyle == YES_NO )
             break;

           m_btnCancel = new Button( "Cancel" );
           m_btnCancel.addActionListener( m_btnListener );

           add( m_btnCancel );

           m_btnCancel.setBounds( xBtnStart + BTN_SPACING * 2 + BTN_WIDTH * 2, yBtnStart, BTN_WIDTH, BTN_HEIGHT );

    } // end switch()

  } // end createButtons()


} // end class VwMessageBoxBase {}

// *** end of VwMessageBoxBase.java ***


