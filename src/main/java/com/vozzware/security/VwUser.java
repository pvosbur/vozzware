/*
===========================================================================================

 
                               Copyright(c) 2000 - 2006 by

                      V o z z W a r e   L L C (Vw)

                                 All Rights Reserved

Source Name: VwUser.java

Create Date: Oct 23, 2006

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.


============================================================================================
*/
package com.vozzware.security;

import com.vozzware.util.VwDelimString;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class VwUser
{
  
  private VwRoleSecurityService    m_roleService;
  
  private Map       m_mapRoles;
  
  private String    m_strActiveRole;
  private String    m_strUserId;
  
  /**
   * Constructor Creates user with a List of role(s) thay can play
   * @param roleService The role service for looking up privileges
   * @param strActiveRole The active role 
   * @param listRoles
   */
  public VwUser( VwRoleSecurityService roleService, String strActiveRole, List listRoles )
  {
    m_roleService = roleService;
    m_strActiveRole = strActiveRole;
    setRoleNames( listRoles );
    
  }
  
  public VwUser( VwRoleSecurityService roleService, String strActiveRole, Map mapRoles )
  {
    m_roleService = roleService;
    m_strActiveRole = strActiveRole;
    setRoleNames( mapRoles );
    
  }
  
  public VwUser( VwRoleSecurityService roleService, String strActiveRole, String strRoles )
  {
    m_roleService = roleService;
    m_strActiveRole = strActiveRole;
    setRoleNames( strRoles );
    
  }
  
  
  public void setUserId( String strUserId )
  { m_strUserId = strUserId; }
  
  public String getUserId()
  { return m_strUserId; }
  
  
  /**
   * Sets the active role this user is playing
   * @param strActiveRole The name of the active role this user is playing
   */
  public void setActiveRole( String strActiveRole )
  { m_strActiveRole  = strActiveRole; }
  
  /**
   * Gets the name of the active role this user is playing
   * @return the name of the active role this user is playing
   */
  public String getActiveRole()
  { return m_strActiveRole; }
  
  
  /**
   * Sets the names of all possible roles this user can play
   * @param listRoles a List of Strings -- each string is a role name this user can play
   */
  public void setRoleNames( List listRoles )
  {
    if ( m_mapRoles == null )
      m_mapRoles = new HashMap();
    
    for ( Iterator iRoleNames = listRoles.iterator(); iRoleNames.hasNext();  )
      m_mapRoles.put( iRoleNames.next().toString().toLowerCase(), null );
    
  } // end setRoleNames()
  
  /**
   * Sets the names of all possible roles this user can play
   * @param mapRoles a map of String keys -- each map key is a string representing a role name this user can play
   */
  public void setRoleNames( Map mapRoles )
  { m_mapRoles = mapRoles; }
  
  
  /**
   * Sets the names of all possible roles this user can play
   * @param strRoleNames a comma separated string of role names each representing a role name this user can play
   */
  public void setRoleNames( String strRoleNames )
  {
    VwDelimString dlms = new VwDelimString( strRoleNames);
    for ( Iterator iRoleNames = dlms.iterator(); iRoleNames.hasNext();  )
      m_mapRoles.put( iRoleNames.next().toString().toLowerCase(), null );
    
  } // end setRoleNames()
  
  
  /**
   * Performs a case insensitive test to determine if this user plays the role requested
   * @param strRoleName The name of the role to test for
   * @return true if the user plays the requested role, false otherwise
   */
  public boolean hasRole( String strRoleName ) throws Exception
  { return m_roleService.isActiveRole( strRoleName ); }
  
  /**
   * Performs a case insensitive test to determine if this user has the named privilege for the active role
  * 
   * @param strPrivilege The name of the privilege to test fo
   * @return true if the user has the named privilege for the active role, false otherwise
   */
  public boolean hasPrivilege( String strPrivilege ) throws Exception
  {
    return m_roleService.hasPrivilege( m_strActiveRole, strPrivilege );
  }
  
  public boolean isInRoleGroup( String strRoleGroup ) throws Exception
  { return m_roleService.isRoleInGroup( m_strActiveRole, strRoleGroup ); }
  
} // end VwUser{}
