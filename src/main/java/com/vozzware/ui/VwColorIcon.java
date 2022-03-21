/*
===========================================================================================

 
                               Copyright(c) 2000 - 2006 by

                      V o z z W a r e   L L C (Vw)

                                 All Rights Reserved

Source Name: VwColorIcon.java

Create Date: May 14, 2006

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.


============================================================================================
*/
package com.vozzware.ui;

import javax.swing.Icon;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

public class VwColorIcon implements Icon
{
  private Color m_clrIcon;
  private int   m_nWidth;
  private int   m_nHeight;
  
  public VwColorIcon( Color clrIcon, int nWidth, int nHeight )
  {
    m_clrIcon = clrIcon;
    m_nWidth = nWidth;
    m_nHeight = nHeight;
  }
  
  public int getIconHeight()
  { return m_nHeight; }

  public int getIconWidth()
  { return m_nWidth; }
  
  public void paintIcon(Component c, Graphics g, int x, int y)
  {
    g.setColor( m_clrIcon );

    int nCompHeight = c.getHeight();
    
    int nyOffset = (nCompHeight - m_nHeight) / 2;
    
    g.fillRect( x, y + nyOffset, m_nWidth, m_nHeight );
    
    
  }
  
}
