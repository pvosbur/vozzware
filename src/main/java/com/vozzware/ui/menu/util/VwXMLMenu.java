/*
============================================================================================

                        V o z z W o r k s   C o d e   G e n e r a t o r                       

                              2009 by I Technologies Corp                              

    Source File Name: VwXMLMenu.java

    Author:           Vw

    Date Generated:   05-30-2006

    Time Generated:   10:21:59

============================================================================================
*/

package com.vozzware.ui.menu.util;

import java.util.List;


public class VwXMLMenu
{

  private String                 m_strName;                      
  private String                 m_strType;                      
  private String                 m_strPos;                       
  private List<VwXMLMenuItem>   m_listMenuItem;                 

  // *** The following members set or get data from the class members *** 

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
   * Sets the pos property
   * 
   * @param strpos
   */
  public void setPos( String strPos )
  { m_strPos = strPos;
 }

  /**
   * Gets pos property
   * 
   * @return  The pos property
   */
  public String getPos()
  { return m_strPos; }

  /**
   * Sets the menuItem property
   * 
   * @param listmenuItem
   */
  public void setMenuItem( List<VwXMLMenuItem> listMenuItem )
  { m_listMenuItem = listMenuItem;
 }

  /**
   * Gets menuItem property
   * 
   * @return  The menuItem property
   */
  public List<VwXMLMenuItem> getMenuItem()
  { return m_listMenuItem; }
} // *** End of class VwXMLMenu{}

// *** End Of VwXMLMenu.java