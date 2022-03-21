/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwStyledWordDescriptor.java

Create Date: Apr 11, 2003
============================================================================================
*/
package com.vozzware.ui;

import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.Color;
import java.awt.Font;
import java.util.HashMap;
import java.util.Map;


/**
 * Class that implements syntax hiliting
 */
public class VwStyledWordDescriptor
{
  private Map       m_mapStyledWords;

  private SimpleAttributeSet m_attrSet;

  /**
  * Constructor
  * @param astrStyledWords Words that belong to this common attribute set
  * @param font The font that will be applied to any word in this word set
  * @param clrForeground The foreground color that will be applied to any word in this word set
  * @param clrBackground The background that will be applied to any word in this word set
  */
 public VwStyledWordDescriptor( String[] astrStyledWords, Font font, Color clrForeground, Color clrBackground )
 { this( astrStyledWords, font, clrForeground, clrBackground, false, false ); }

   /**
   * Constructor
   * @param astrStyledWords Words that belong to this common attribute set
   * @param font The font that will be applied to any word in this word set
   * @param clrForeground The foreground color that will be applied to any word in this word set
   * @param clrBackground The background that will be applied to any word in this word set
   * @param fUnderline Apply underlineing if true
   * @param fStrikeThrough SApply strikethrough if true
   */
  public VwStyledWordDescriptor( String[] astrStyledWords, Font font, Color clrForeground, Color clrBackground, boolean fUnderline, boolean fStrikeThrough  )
  {
    if (  astrStyledWords != null )
    {
      m_mapStyledWords = new HashMap();
      for ( int x = 0; x < astrStyledWords.length; x++ )
        m_mapStyledWords.put( astrStyledWords[ x ].toLowerCase(), null );
    }

    m_attrSet = new SimpleAttributeSet();

    // Set color properties
    if ( clrForeground !=  null )
      StyleConstants.setForeground( m_attrSet, clrForeground );

    if ( clrBackground !=  null )
      StyleConstants.setBackground( m_attrSet, clrBackground );

    if ( font == null )
      return;

    // Set font properties
    StyleConstants.setFontFamily( m_attrSet, font.getFamily() );

    StyleConstants.setFontSize( m_attrSet, font.getSize() );

    StyleConstants.setBold( m_attrSet, font.isBold() );

    StyleConstants.setItalic( m_attrSet, font.isItalic() );

    if ( fUnderline )
      StyleConstants.setUnderline( m_attrSet, true );

    if ( fStrikeThrough)
      StyleConstants.setStrikeThrough( m_attrSet, true );

  } // end VwStyledWordDescriptor()


  /**
   * Gets the Attribute set for a word
   * @param strWord The word to serach for in this word group
   * @return The AttributeSet if the word is in this list else null is returned
   */
  public AttributeSet getAttrSet( String strWord )
  {
    if ( m_mapStyledWords == null )
      return m_attrSet;

    if ( m_mapStyledWords.containsKey(  strWord.toLowerCase() ))
      return m_attrSet;

    return null;  // No Match

  } // end getAttrs(
} // end class VwStyledWordDescriptor{}

// *** End of VwStyledWordDescriptor.java ***

