/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwLineView.java

============================================================================================
*/

package com.vozzware.print;

import com.vozzware.util.VwEdit;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class VwLineView
{
  private static final  int LEFT = 0;
  private static final  int RIGHT = 1;
  private static final  int CENTERED = 2;
  private static final  int ABSOLUTE = 3;
  private static final  int RELATIVE = 4;
  
  private static String[] s_astrJustify = { "left", "right", "centered",  "absolute", "realtive" };
  
  private VwReportLine         m_rptLine;              // Attributes and data positions for a specific report line
  
  private int                   m_nSpaceLines;          // Nbr space lines to advance before rendering

  private int                   m_nYPos;                // Maintains new Y position based on largest font

  private static Map            s_mapCachedFonts = null; // Table of cached fonts

  
  /**
   * Constructor
   *
   * @param nSpaceLines The nbr of space lines to advance before rendering the line
   */
  public VwLineView( VwReportLine rptLine, int nSpaceLines )
  {
    m_rptLine = rptLine;
    m_nSpaceLines = nSpaceLines;

    if ( s_mapCachedFonts == null )
      s_mapCachedFonts = new HashMap();

  } // end VwLineView()



  /**
   * Gets the new updated Y coordinate
   *
   * @return The updated Y coordinate
   */
  public int getCurYPos()
  { return m_nYPos; }


  /**
   * Renders the line on the Graphics context
   *
   * @param dataObj The DataObject to use for dynamic data rendering
   * @param g The Graphics context for the rendering device
   * @param rctView A Rectangle object with the View information
   * @param nCurYPos The current Y position
   * @param nSpaceLineHeight The height of a space line, in pixels
   *
   * @exception Exception if no data element exists in the VwDataObject
   */
  public void render( Object objData, Graphics g, Rectangle rctView,
                      int nCurYPos, int nSpaceLineHeight ) throws Exception
  {
    m_nYPos = nCurYPos;

    m_nYPos += ( m_nSpaceLines * nSpaceLineHeight );

    int nMaxFontHeight = 0;

    for ( Iterator iPos = m_rptLine.getElement().iterator(); iPos.hasNext(); )
    {
      VwElementAttribute eleAttr = (VwElementAttribute)iPos.next();

      Font font = getFont( eleAttr );

      if ( font != null )
        g.setFont( font );

      if ( eleAttr.getForeground() != null )
        g.setColor( getColor( eleAttr.getForeground() ));
      else
        g.setColor( Color.black );

      FontMetrics fm = g.getFontMetrics();

      if ( fm.getHeight() > nMaxFontHeight )
        nMaxFontHeight = fm.getAscent();

      if ( eleAttr.getType().equalsIgnoreCase( "text" ) )
      {
        String strData = null;
        VwEdit edit = null;

        if ( eleAttr.getFormat() != null )
           edit = new VwEdit( eleAttr.getFormat() );
        
        if ( edit != null )
        {
          if ( strData.trim().length() > 0 )
            strData = edit.format( strData );
        }

        formatTextData( g, strData, eleAttr, rctView, m_nYPos, isUnderlined( eleAttr )  );
      }

    } // end for()

    m_nYPos += nMaxFontHeight;

  } // end render()

  /**
   * @param eleAttr
   * @return
   */
  private boolean isUnderlined( VwElementAttribute eleAttr )
  {
    // TODO Auto-generated method stub
    return false;
  }



  /**
   * @param foreground
   * @return
   */
  private Color getColor( String foreground )
  {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * Gets a font from the font cache table, if one exists.  If the font is not
   * in the cache, it is added, and a reference to the new font is returned.
   *
   * @param eleAttr The VwElementAttribute containing the font information
   *
   * @return A reference to the specified Font object
   */
  private Font getFont( VwElementAttribute eleAttr )
  {
    if ( eleAttr.getFontName() == null )
      return null;

    String strFontKey = eleAttr.getFontName() + eleAttr.getFontStyle() + eleAttr.getFontSize();

    Font font = (Font)s_mapCachedFonts.get( strFontKey );

    if ( font == null )
    {
      font = makefont( eleAttr );
      s_mapCachedFonts.put( strFontKey, font );
    }

    return font;

  } // end getFont()


  /**
   * @param eleAttr
   * @return
   */
  private Font makefont( VwElementAttribute eleAttr )
  {
    // TODO Auto-generated method stub
    return null;
  }



  /**
   * Renders text data
   *
   * @param g The graphics context to render the data on
   * @param strData The text data to render
   * @param eleAttr An element position object
   * @param rctView A Rectangle object with the View information
   * @param nCurYPos The current Y position
   * @param fIsUnderlined If True, the text is underlined; otherwise, it is not
   */
  private void formatTextData( Graphics g, String strData, VwElementAttribute eleAttr,
                               Rectangle rctView, int nCurYPos, boolean fIsUnderlined  )
  {
    FontMetrics fm = g.getFontMetrics();
    int nDataLen = fm.stringWidth( strData );
    int xPos = 0;
    int yPos = 0;

    int nJustify = convertJustify( eleAttr.getJustify() );
    

    switch( nJustify )
    {

      case LEFT:

           xPos = rctView.x;
           yPos = nCurYPos;

           break;

      case CENTERED:

           xPos = (rctView.width / 2 ) - (nDataLen / 2) + rctView.x;
           yPos = nCurYPos;
           break;

      case RIGHT:

           xPos = rctView.x + rctView.width - nDataLen;
           yPos = nCurYPos;

           break;

      case ABSOLUTE:

           xPos = Integer.parseInt( eleAttr.getPos() );
           yPos = nCurYPos;
           
           break;
           
      case RELATIVE:
        
           xPos = Integer.parseInt( eleAttr.getPos() );
           yPos = nCurYPos;
        
    } // end switch( nPosType )

      g.drawString( strData, xPos, yPos + fm.getAscent() );
      if ( fIsUnderlined )
        g.drawLine( xPos, yPos + fm.getAscent() + 1, xPos + nDataLen,
                    yPos + fm.getAscent()+ 1);

  } // end formatTextData()



  /**
   * @param justify
   * @return
   */
  private int convertJustify( String strJustify )
  {
    if( strJustify == null )
      return RELATIVE;
    
    for ( int x = 0; x < s_astrJustify.length; x++ )
    {
      if ( strJustify.equalsIgnoreCase( s_astrJustify[ x ] ))
        return x;
      
    }
    
    return RELATIVE;
  }


} // end class VwLineView{}


// *** End VwLineView.java ***

