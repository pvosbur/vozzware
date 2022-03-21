/*
============================================================================================

                        V o z z W or k s    C o d e   G e n e r a t o r                       

                              2009 by I Technologies Corp                              

    Source File Name: VwRoleGroup.java

    Author:           

    Date Generated:   04-13-2007

    Time Generated:   08:56:09

============================================================================================
*/

package com.vozzware.security;

import java.util.List;


public class VwRoleGroup
{

  private String                 m_strName;                      
  private String                 m_strEnabled;                   
  private List                   m_listRoleRef;                  

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
   * Sets the roleRef property
   * 
   * @param listroleRef
   */
  public void setRoleRef( List listRoleRef )
  { m_listRoleRef = listRoleRef;
 }

  /**
   * Gets roleRef property
   * 
   * @return  The roleRef property
   */
  public List getRoleRef()
  { return m_listRoleRef; }
} // *** End of class VwRoleGroup{}

// *** End Of VwRoleGroup.java