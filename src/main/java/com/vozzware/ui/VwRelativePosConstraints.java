/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwRelativePosConstraints.java

Create Date: Apr 11, 2003
============================================================================================
*/
package com.vozzware.ui;

import java.awt.Component;

public class VwRelativePosConstraints implements Cloneable
{
  private boolean m_fReSizeWidth = false;
  private boolean m_fReSizeHeight = false;
  private boolean m_fIsRelative = false;
  private boolean m_fUsePreferredSize = false;
  
  private int     m_nMinChars = -1;
  private int     m_nPrefChars = -1;
  private int     m_nMaxChars = -1;
  private int     m_nMinHeightChars = -1;
  private int     m_nMaxHeightChars = -1;
  private int     m_nPrefHeightChars = -1;
  private int     m_nRowNbr = -1;
  private int     m_nColNbr = -1;
  
  private int     m_nRelX = -1;
  private int     m_nRelY = -1;
  
  private int     m_nCharWidth = -1;  // width of widest character based on the components font
  private int     m_nCharHeight = -1; // Height of a character  based on the components font
  
  protected Component m_compLeft = null;
  protected Component m_compTop = null;
  
  
  public boolean isResizeWidth()
  { return m_fReSizeWidth; }
  
  
  public void setResizeWidth( boolean fReSizeWidth )
  { m_fReSizeWidth = fReSizeWidth; }
  
 
  
  public boolean isResizeHeight()
  { return m_fReSizeHeight; }
  
  
  public void setResizeHeight( boolean fReSizeHeight )
  { m_fReSizeHeight = fReSizeHeight; }
 
  
  
  public boolean isRelative()
  { return m_fIsRelative; }


  public void setRelative( boolean fIsRelative )
  { m_fIsRelative = fIsRelative; }


  public boolean isUsePreferredSize()
  {
    return m_fUsePreferredSize;
  }


  public void setUsePreferredSize( boolean userPreferredSize )
  {
    m_fUsePreferredSize = userPreferredSize;
  }


  public int getMinChars()
  { return m_nMinChars; }
  
  
  public void setMinChars( int minChars )
  { m_nMinChars = minChars; }
  
  
  public int getPrefChars()
  { return m_nPrefChars; }
  
  
  public void setPrefChars( int prefChars )
  { m_nPrefChars = prefChars; }
  
  
  public int getMaxChars()
  { return m_nMaxChars; }
  
  
  public void setMaxChars( int maxChars )
  { m_nMaxChars = maxChars; }
  
  
  public int getMinHeightChars()
  { return m_nMinHeightChars; }


  public void setMinHeightChars( int minHeightChars )
  { m_nMinHeightChars = minHeightChars; }


  public int getMaxHeightChars()
  { return m_nMaxHeightChars; }


  public void setMaxHeightChars( int maxHeightChars )
  { m_nMaxHeightChars = maxHeightChars; }


  public int getPrefHeightChars()
  { return m_nPrefHeightChars; }


  public void setPrefHeightChars( int prefHeightChars )
  { m_nPrefHeightChars = prefHeightChars; }


  public int getRelX()
  { return m_nRelX; }


  public void setRelX( int relX )
  { m_nRelX = relX; }


  public int getRelY()
  { return m_nRelY; }


  public void setRelY( int relY )
  {  m_nRelY = relY; }

  
  public int getRowNbr()
  { return m_nRowNbr; }


  public void setRowNbr( int nRowNbr )
  { m_nRowNbr = nRowNbr;  }


  public int getColNbr()
  { return m_nColNbr; }


  public void setColNbr( int nColNbr )
  { m_nColNbr = nColNbr; }


  public Component getCompLeft()
  { return m_compLeft;  }



  public Component getCompTop()
  { return m_compTop; }

 
  public void setCharWidth( int nCharWidth )
  { m_nCharWidth = nCharWidth; }
  
  public int getCharWidth()
  { return m_nCharWidth; }


  public void setCharHeight( int nCharHeight )
  { m_nCharHeight = nCharHeight; }

  public int getCharHeight()
  { return m_nCharHeight; }


  public Object clone()
  {
    VwRelativePosConstraints rpcClone = new VwRelativePosConstraints();
    rpcClone.m_fReSizeWidth = m_fReSizeWidth;
    rpcClone.m_fReSizeHeight = m_fReSizeHeight;
    rpcClone.m_fIsRelative = m_fIsRelative;
    rpcClone.m_fUsePreferredSize = m_fUsePreferredSize;
    rpcClone.m_nMaxChars = m_nMaxChars;
    rpcClone.m_nMinChars = m_nMinChars;
    rpcClone.m_nPrefChars = m_nPrefChars;
    rpcClone.m_nMinHeightChars = m_nMinHeightChars;
    rpcClone.m_nMaxHeightChars = m_nMaxHeightChars;
    rpcClone.m_nPrefHeightChars = m_nPrefHeightChars;
    
    rpcClone.m_compLeft = m_compLeft;
    rpcClone.m_compTop = m_compTop;
    rpcClone.m_nRelX = m_nRelX;
    rpcClone.m_nRelY = m_nRelY;
    rpcClone.m_nRelY = m_nRelY;
    rpcClone.m_nRowNbr = m_nRowNbr;
    rpcClone.m_nColNbr = m_nColNbr;
    
    rpcClone.m_nCharHeight = m_nCharHeight;
    
    return rpcClone;
    
  }
} // end class RelativePosConstraints{}

// *** End class RelativePosConstraints.java ***

