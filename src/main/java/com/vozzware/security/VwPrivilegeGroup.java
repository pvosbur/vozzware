/*
============================================================================================

                        V o z z W o r k s   C o d e   G e n e r a t o r                       

                              2009 by I Technologies Corp                              

    Source File Name: VwPrivilegeGroup.java

    Author:           

    Date Generated:   04-13-2007

    Time Generated:   08:56:09

============================================================================================
*/

package com.vozzware.security;

import java.util.List;


public class VwPrivilegeGroup
{

  private String                 m_strName;                      
  private String                 m_strEnabled;                   
  private List                   m_listPrivilegeRef;             

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
   * Sets the privilegeRef property
   * 
   * @param listprivilegeRef
   */
  public void setPrivilegeRef( List listPrivilegeRef )
  { m_listPrivilegeRef = listPrivilegeRef;
 }

  /**
   * Gets privilegeRef property
   * 
   * @return  The privilegeRef property
   */
  public List getPrivilegeRef()
  { return m_listPrivilegeRef; }
} // *** End of class VwPrivilegeGroup{}

// *** End Of VwPrivilegeGroup.java