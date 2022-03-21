/*
============================================================================================

                        V o z z W o r k s   C o d e   G e n e r a t o r                       

                              2009 by I Technologies Corp                              

    Source File Name: VwXMLMenuItem.java

    Author:           Vw

    Date Generated:   05-30-2006

    Time Generated:   10:21:59

============================================================================================
*/

package com.vozzware.ui.menu.util;

import java.util.List;


public class VwXMLMenuItem
{

  private String                 m_strId;                      
  private String                 m_strName;                      
  private String                 m_strType;                      
  private String                 m_strIcon;                      
  private String                 m_strEnabled;                   
  private String                 m_strMnemonic;                  
  private String                 m_strToolTip;                   
  private String                 m_strAccelerator;               
  private List                   m_listSubMenu;                  

  // *** The following members set or get data from the class members *** 

  /**
   * Sets the Id property
   * 
   * @param strname
   */
  public void setId( String strId )
  { m_strId = strId;
 }

  /**
   * Gets name property
   * 
   * @return  The Id property
   */
  public String getId()
  { return m_strId; }
  
  /**
   * Sets the name property
   * 
   * @param strname
   */
  public void setName( String strName )
  { m_strName = strName;
 }

  /**
   * Gets name property
   * 
   * @return  The name property
   */
  public String getName()
  { return m_strName; }

  /**
   * Sets the type property
   * 
   * @param strtype
   */
  public void setType( String strType )
  { m_strType = strType;
 }

  /**
   * Gets type property
   * 
   * @return  The type property
   */
  public String getType()
  { return m_strType; }

  /**
   * Sets the icon property
   * 
   * @param stricon
   */
  public void setIcon( String strIcon )
  { m_strIcon = strIcon;
 }

  /**
   * Gets icon property
   * 
   * @return  The icon property
   */
  public String getIcon()
  { return m_strIcon; }

  /**
   * Sets the enabled property
   * 
   * @param strenabled
   */
  public void setEnabled( String strEnabled )
  { m_strEnabled = strEnabled;
 }

  /**
   * Gets enabled property
   * 
   * @return  The enabled property
   */
  public String getEnabled()
  { return m_strEnabled; }

  /**
   * Sets the mnemonic property
   * 
   * @param strmnemonic
   */
  public void setMnemonic( String strMnemonic )
  { m_strMnemonic = strMnemonic;
 }

  /**
   * Gets mnemonic property
   * 
   * @return  The mnemonic property
   */
  public String getMnemonic()
  { return m_strMnemonic; }

  /**
   * Sets the toolTip property
   * 
   * @param strtoolTip
   */
  public void setToolTip( String strToolTip )
  { m_strToolTip = strToolTip;
 }

  /**
   * Gets toolTip property
   * 
   * @return  The toolTip property
   */
  public String getToolTip()
  { return m_strToolTip; }

  /**
   * Sets the accelerator property
   * 
   * @param straccelerator
   */
  public void setAccelerator( String strAccelerator )
  { m_strAccelerator = strAccelerator;
 }

  /**
   * Gets accelerator property
   * 
   * @return  The accelerator property
   */
  public String getAccelerator()
  { return m_strAccelerator; }

  /**
   * Sets the subMenu property
   * 
   * @param listsubMenu
   */
  public void setMenuItem( List listSubMenu )
  { m_listSubMenu = listSubMenu;
 }

  /**
   * Gets subMenu property
   * 
   * @return  The subMenu property
   */
  public List getMenuItem()
  { return m_listSubMenu; }
} // *** End of class VwXMLMenuItem{}

// *** End Of VwXMLMenuItem.java