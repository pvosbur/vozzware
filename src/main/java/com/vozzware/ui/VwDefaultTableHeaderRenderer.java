/*
===========================================================================================

 
                               Copyright(c) 2000 - 2006 by

                      V o z z W a r e   L L C (Vw)

                                 All Rights Reserved

Source Name: VwHeadrClickRenderer.java

Create Date: May 18, 2006

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.


============================================================================================
*/
package com.vozzware.ui;

import com.vozzware.util.VwExString;

import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.Method;
import java.util.Map;


/**
 * This class is the default VwTable header renderer. It uses either a JLabel or an
 * <br>AbstractButton JComponent for drawing the table headers. A Map of VwTableColAttr 
 * <br>objects is required and the header attributes are set with the column definition
 * <br>provided by each instance of VwTableColAttr
 * 
 * @author P. VosBurgh
 *
 */
public class VwDefaultTableHeaderRenderer  implements TableCellRenderer
{
  private   Map<String,VwTableColAttr>  m_mapTableColAttrs;

  private   Color               m_clrHeaderBG;
  private   Color               m_clrHeaderFG;

  private   Font                m_fontHeader;
  
  private   int                 m_nColCount;
  
  private   JComponent          m_compHeader;
  private   JComponent          m_compToolTipWin;
  private   JTable              m_table;
  
  private   int                 m_nInsetWidth;
  
  private   Icon                m_iconDefault;
  
  private   Method              m_mthdToolTpTextSetter;
  
  /**
   * Constructor]
   * 
   * @param mapVwTableColAttrs a Map of VwTableColAttr objects (one for each column header)
   * @param compHeader the JComponent used for rendering the header (must be JLabel or AbstractButton based
   */
  public VwDefaultTableHeaderRenderer( JTable tblOwner, Map<String,VwTableColAttr> mapTableColAttrs, JComponent compHeader )
  {
    m_mapTableColAttrs = mapTableColAttrs;
    m_table = tblOwner;
    m_compHeader = compHeader;
    Insets ins = m_compHeader.getInsets();
    m_nInsetWidth = ins.left + ins.right;
    m_clrHeaderBG = compHeader.getBackground();
    m_clrHeaderFG = compHeader.getForeground();
 
    Color colorHeader = m_compHeader.getBackground();
    m_iconDefault = new VwColorIcon( colorHeader, 1, 1 );
    
    if ( mapTableColAttrs != null )
    {
        setupListeners();
    }

  }
  
  
  
  /**
   * Sets a JComponent to be used as a tool tip window. The component must define 
   * @param compToolTip
   * @throws Exception
   */
  public void setToolTipComponent( JComponent compToolTip ) throws Exception
  { 
    m_mthdToolTpTextSetter = compToolTip.getClass().getMethod( "setText", new Class[]{String.class} );
    
    m_compToolTipWin = compToolTip; 
    
  }
  
  
  public JComponent getToolTipComponent()
  {  return m_compToolTipWin; }
  
  private void setupListeners()
  {
    
    // We only install a mouse listener if a tooltip component window was defined for mouse entered and exit events
    
    if ( m_compToolTipWin == null )
      return;
    
    final JTableHeader thdr = m_table.getTableHeader();
    
    thdr.addMouseListener( new MouseAdapter()
    {
      public void mouseExited( MouseEvent me )
      {
        if ( m_compToolTipWin != null )
          m_compToolTipWin.setVisible( false );
        
      }
      
      public void mouseEntered( final MouseEvent me )
      {
        int nCol = thdr.columnAtPoint( me.getPoint() );
        if ( nCol < 0 )
          return;
        
        String strHdrName = thdr.getColumnModel().getColumn( nCol ).getHeaderValue().toString();
        final VwTableColAttr tca = m_mapTableColAttrs.get( strHdrName );
        if ( tca != null && m_compToolTipWin != null )
        {
          SwingUtilities.invokeLater( new Runnable()
          {
            public void run()
            {
              try
              {
                Thread.sleep( 750 );
                m_mthdToolTpTextSetter.invoke( m_compToolTipWin, new Object[]{ tca.getToolTip() } );
                m_compToolTipWin.setVisible( true );
                Dimension dimLbl = adjustSize( tca.getToolTip() );
          
                m_compToolTipWin.setBounds( new Rectangle( me.getPoint().x, 10, dimLbl.width, dimLbl.height)  );
                return;
               
              }
              catch( Exception ex )
              {
                ;
              }
              
            }

            private Dimension adjustSize( String strToolTip )
            {
              FontMetrics fm = m_compToolTipWin.getFontMetrics( m_compToolTipWin.getFont() );
              int nLines = VwExString.count( strToolTip, '\n' ) + 1;
              
              String[] astrLines = strToolTip.split( "\n" );
              
              Dimension dim = new Dimension();
              if ( astrLines.length == 0 )
                dim.width = fm.stringWidth( strToolTip );
              else
              {
                for ( int x = 0; x < astrLines.length; x++ )
                {
                  int nWidth = fm.stringWidth( astrLines[ x ] );
                  
                  if ( nWidth > dim.width )
                    dim.width = nWidth;
                }
              }
              dim.height = nLines * fm.getHeight() + 4;
              return dim;
            }
          });
        }
        
        
      };
    });
    
    
  }

  public void setTableColAttrs( Map<String,VwTableColAttr> mapTableColAttrs, int nColCount )
  { 
    m_nColCount = nColCount;
    m_mapTableColAttrs = mapTableColAttrs;
    setupListeners();
  }
  
  
  /**
   * Provides the default rendering
   */
  public Component getTableCellRendererComponent( JTable tbl, Object obj, boolean fIsSelected,
                                                  boolean fHasFocus, int nRow, int nCol )
  {

    String strColName = obj.toString();
    VwTableColAttr tcAttr = m_mapTableColAttrs.get(  strColName  );
    
    if ( tcAttr.getToolTip() != null && m_compToolTipWin == null )
      m_compHeader.setToolTipText( tcAttr.getToolTip() );
    else
      m_compHeader.setToolTipText( null );

    
    if ( m_compHeader instanceof JLabel )
      setLableAttrs( tcAttr, (JLabel)m_compHeader );
    else
      setButtonAttrs( tcAttr, (AbstractButton)m_compHeader );
    
    if ( tcAttr.getColorBackGround() != null )
      m_compHeader.setBackground( tcAttr.getColorBackGround() );
    else
    if ( m_clrHeaderBG != null )
      m_compHeader.setBackground( m_clrHeaderBG );

    if ( tcAttr.getColorForeGround() != null )
      m_compHeader.setForeground( tcAttr.getColorForeGround() );
    else
    if ( m_clrHeaderFG != null )
      m_compHeader.setForeground( m_clrHeaderFG );
    
    if ( tcAttr.getHeaderFont() != null )
      m_compHeader.setFont( tcAttr.getHeaderFont() );
    else 
    if ( m_fontHeader != null )
      m_compHeader.setFont( m_fontHeader );
    
    return m_compHeader;
    
  } // end getTableCellRendererComponent()
  
  
  /**
   * Set attributes for an abstract button component
   * @param tcAttr The attribute object for this column
   * @param button  The button component to set
   */
  private void setButtonAttrs( VwTableColAttr tcAttr, AbstractButton button )
  {
    button.setText( tcAttr.getColName() );
    if ( tcAttr.getColHdrIcon() != null )
      button.setIcon( tcAttr.getColHdrIcon() );
    else
      button.setIcon( m_iconDefault );
    
  }
  
  /**
   * Set attributes for a JLabel component
   * @param tcAttr The attribute object for this column
   * @param label  The label component to set
   */
  private void setLableAttrs( VwTableColAttr tcAttr, JLabel label )
  {
    label.setText( tcAttr.getColName() );
    if ( tcAttr.getColHdrIcon() != null )
      label.setIcon( tcAttr.getColHdrIcon() );
    else
     label.setIcon( m_iconDefault );
    
  }
  
  /**
   * Sets an alternate font for the header
   * @param fontHeader The alternate font to use
   */
  public void setHeaderFont( Font fontHeader )
  { m_fontHeader = fontHeader; }
  
  /**
   * Gets the alternate header font or null for the JComponents default
   * @return
   */
  public Font getHeaderFont()
  { return m_fontHeader; }
  
  /**
   * Sets an alternate background color for the column header
   * @param clrHeaderBG
   */
  public void setHeaderBGColor( Color clrHeaderBG )
  { m_clrHeaderBG = clrHeaderBG; }
  
  /**
   * get the alternate background color for the column header 
   * @return the alternate background color for the column header -- null if useing the JComponent's defualt
   */
  public Color getHeaderBGColor()
  { return m_clrHeaderBG; }

  /**
   * Sets an alternate foreground color for the column header
   * @param clrHeaderBG
   */
  public void setHeaderFGColor( Color clrHeaderFG )
  { m_clrHeaderFG = clrHeaderFG; }

  /**
   * get the alternate foreground color for the column header 
   * @return the alternate foreground color for the column header -- null if useing the JComponent's defualt
   */
  public Color getHeaderFGColor()
  { return m_clrHeaderFG; }
  
  
} // end class VwHeadrClickRenderer{}

// *** End of VwHeadrClickRenderer
