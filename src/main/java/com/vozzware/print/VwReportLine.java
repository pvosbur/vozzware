/*
============================================================================================

                        V o z z W o r k s   C o d e   G e n e r a t o r                       

                              2009 by I Technologies Corp                              

    Source File Name: VwReportLine.java

    Author:           

    Date Generated:   08-07-2007

    Time Generated:   07:26:17

============================================================================================
*/

package com.vozzware.print;

import java.util.List;


public class VwReportLine
{

  private String                 m_strType;                      
  private String                 m_strId;                        
  private String                 m_strRef;                       
  private String                 m_strFontName;                  
  private String                 m_strFontSize;                  
  private String                 m_strFontStyle;                 
  private String                 m_strForeground;                
  private String                 m_strBackground;                
  private List<VwElementAttribute>  m_listElement;                  

  // *** The following members set or get data from the class members *** 

  /**
   * Sets the type property
   * 
   * @param strtype
   */
  public void setType( String strType )
  { m_strType = strType; }

  /**
   * Gets type property
   * 
   * @return  The type property
   */
  public String getType()
  { return m_strType; }

  /**
   * Sets the id property
   * 
   * @param strid
   */
  public void setId( String strId )
  { m_strId = strId; }

  /**
   * Gets id property
   * 
   * @return  The id property
   */
  public String getId()
  { return m_strId; }

  /**
   * Sets the ref property
   * 
   * @param strref
   */
  public void setRef( String strRef )
  { m_strRef = strRef; }

  /**
   * Gets ref property
   * 
   * @return  The ref property
   */
  public String getRef()
  { return m_strRef; }

  /**
   * Sets the fontName property
   * 
   * @param strfontName
   */
  public void setFontName( String strFontName )
  { m_strFontName = strFontName; }

  /**
   * Gets fontName property
   * 
   * @return  The fontName property
   */
  public String getFontName()
  { return m_strFontName; }

  /**
   * Sets the fontSize property
   * 
   * @param strfontSize
   */
  public void setFontSize( String strFontSize )
  { m_strFontSize = strFontSize; }

  /**
   * Gets fontSize property
   * 
   * @return  The fontSize property
   */
  public String getFontSize()
  { return m_strFontSize; }

  /**
   * Sets the fontStyle property
   * 
   * @param strfontStyle
   */
  public void setFontStyle( String strFontStyle )
  { m_strFontStyle = strFontStyle; }

  /**
   * Gets fontStyle property
   * 
   * @return  The fontStyle property
   */
  public String getFontStyle()
  { return m_strFontStyle; }

  /**
   * Sets the foreground property
   * 
   * @param strforeground
   */
  public void setForeground( String strForeground )
  { m_strForeground = strForeground; }

  /**
   * Gets foreground property
   * 
   * @return  The foreground property
   */
  public String getForeground()
  { return m_strForeground; }

  /**
   * Sets the background property
   * 
   * @param strbackground
   */
  public void setBackground( String strBackground )
  { m_strBackground = strBackground; }

  /**
   * Gets background property
   * 
   * @return  The background property
   */
  public String getBackground()
  { return m_strBackground; }

  /**
   * Sets the element property
   * 
   * @param listelement
   */
  public void setElement( List<VwElementAttribute> listElement )
  { m_listElement = listElement; }

  /**
   * Gets element property
   * 
   * @return  The element property
   */
  public List<VwElementAttribute> getElement()
  { return m_listElement; }
} // *** End of class VwReportLine{}

// *** End Of VwReportLine.java