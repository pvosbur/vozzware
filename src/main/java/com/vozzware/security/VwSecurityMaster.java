/*
============================================================================================

                        V o z z W o r k s   C o d e   G e n e r a t o r                       

                              2009 by I Technologies Corp                              

    Source File Name: VwSecurityMaster.java

    Author:           

    Date Generated:   04-13-2007

    Time Generated:   08:56:09

============================================================================================
*/

package com.vozzware.security;

import java.util.List;


public class VwSecurityMaster
{

  private List                   m_listPrivilege;                
  private List                   m_listPrivilegeGroup;           
  private List                   m_listRole;                     
  private List                   m_listRoleGroup;                

  // *** The following members set or get data from the class members *** 

  /**
   * Sets the privilege property
   * 
   * @param listprivilege
   */
  public void setPrivilege( List listPrivilege )
  { m_listPrivilege = listPrivilege;
 }

  /**
   * Gets privilege property
   * 
   * @return  The privilege property
   */
  public List getPrivilege()
  { return m_listPrivilege; }

  /**
   * Sets the privilegeGroup property
   * 
   * @param listprivilegeGroup
   */
  public void setPrivilegeGroup( List listPrivilegeGroup )
  { m_listPrivilegeGroup = listPrivilegeGroup;
 }

  /**
   * Gets privilegeGroup property
   * 
   * @return  The privilegeGroup property
   */
  public List getPrivilegeGroup()
  { return m_listPrivilegeGroup; }

  /**
   * Sets the role property
   * 
   * @param listrole
   */
  public void setRole( List listRole )
  { m_listRole = listRole;
 }

  /**
   * Gets role property
   * 
   * @return  The role property
   */
  public List getRole()
  { return m_listRole; }

  /**
   * Sets the roleGroup property
   * 
   * @param listroleGroup
   */
  public void setRoleGroup( List listRoleGroup )
  { m_listRoleGroup = listRoleGroup;
 }

  /**
   * Gets roleGroup property
   * 
   * @return  The roleGroup property
   */
  public List getRoleGroup()
  { return m_listRoleGroup; }
} // *** End of class VwSecurityMaster{}

// *** End Of VwSecurityMaster.java