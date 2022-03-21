/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                         i  T e c h n o l o g i e s   C o r p (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwRoleSecurityService.java

============================================================================================
*/
package com.vozzware.security;

import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class VwRoleSecurityService
{
  private VwSecurityMaster m_securityMaster;
  
  private Map   m_mapPrivilegesByRole = Collections.synchronizedMap( new HashMap() );
  private Map   m_mapRolesByRoleGroup = Collections.synchronizedMap( new HashMap() );
  private Map   m_mapRolesMaster = Collections.synchronizedMap( new HashMap() );
  private Map   m_mapPrivMaster = Collections.synchronizedMap( new HashMap() );
  private Map   m_mapPrivGroupMaster = Collections.synchronizedMap( new HashMap() );
  
  
  /**
   * Constructor
   * @param urlSecurityMasterDocument The URL to the Roles definition XML document
   * @throws Exception if any XML parsing errors occur
   */
  public VwRoleSecurityService( URL urlSecurityMasterDocument ) throws Exception
  {
    m_securityMaster = VwSecurityMasterReader.read( urlSecurityMasterDocument );
    
    buildRolesMaster();
    buildPrivilegeMaster();
    buildPrivilegeGroupMaster();
    
    expandRoles();
    expandPrivileges();
    
    
  } // end VwRoleSecurityService()

  

  /**
   * Tests to see if the request role name belongs to a role group
   * @param strRoleName
   * @param strRoleGroupName
   * @return
   */
  public boolean isRoleInGroup( String strRoleName, String strRoleGroupName ) throws Exception
  {
    Map mapRoles = (Map)m_mapRolesByRoleGroup.get( strRoleGroupName.toLowerCase() );
    
    if ( mapRoles == null )
      throw new Exception( "roleGroup '" + strRoleGroupName  + "' is not defined");
    
    if ( ! mapRoles.containsKey( strRoleName.toLowerCase() ) )
      return false;     // role not defined
    
    VwRole role = (VwRole)m_mapRolesMaster.get( strRoleName.toLowerCase() );
    
    return role.getEnabled().equalsIgnoreCase( "true" );
    
  } // end 
  
  
  /**
   * Tests to see if role is active
   * @param strRoleName
   * @return
   * @throws Exception
   */
  public boolean isActiveRole( String strRoleName ) throws Exception
  {
    VwRole role = (VwRole)m_mapRolesMaster.get( strRoleName.toLowerCase() );
    if ( role == null )
      throw new Exception( "role '" + strRoleName + "' is not defined");
    
    return role.getEnabled().equalsIgnoreCase( "true" );
    
  }

  /**
   * Test to see if the role was defined
   * @param strRoleName The role name to test
   * @return true if the role was defined, false otherwise
   * @throws Exception
   */
  public boolean hasRole( String strRoleName ) throws Exception
  {
    VwRole role = (VwRole)m_mapRolesMaster.get( strRoleName.toLowerCase() );
    if ( role == null )
      return false;
    
    return true;
    
  }

  
  /**
   * 
   * @param strRoleName
   * @param strPrivilegeName
   * @return
   * @throws Exception
   */
  public boolean hasPrivilege( String strRoleName, String strPrivilegeName ) throws Exception
  {
    VwRole role = (VwRole)m_mapRolesMaster.get( strRoleName.toLowerCase() );
    if ( role == null )
      throw new Exception( "role '" + strRoleName + "' is not defined");

    VwPrivilege priv = (VwPrivilege)m_mapPrivMaster .get( strPrivilegeName.toLowerCase() );
    if ( priv == null )
      throw new Exception( "privilege '" + strPrivilegeName + "' is not defined");
    
    if ( !priv.getEnabled().equalsIgnoreCase( "true" ))
      return false;
    
    Map mapPrivileges =  (Map)m_mapPrivilegesByRole.get( strRoleName.toLowerCase() );
    
    return mapPrivileges.containsKey(  strPrivilegeName.toLowerCase() ); 
    
    
  }
  
  
  /**
   * Build the privilege master map for fast lookup
   * @throws Exception
   */
  private void buildPrivilegeMaster() throws Exception
  {
    List listPrives = m_securityMaster.getPrivilege();
    
    if ( listPrives == null )
      throw new Exception( "Cannot have empty privilege list");
    
    for ( Iterator iPriv = listPrives.iterator(); iPriv.hasNext(); )
    {
      VwPrivilege priv = (VwPrivilege)iPriv.next();
      m_mapPrivMaster .put( priv.getName().toLowerCase(), priv );
      
    } // end for()
    
  } // end buildPrivilegeMaster

  /**
   * Build the privilege master map for fast lookup
   * @throws Exception
   */
  private void buildPrivilegeGroupMaster() throws Exception
  {
    List listPriveGroups = m_securityMaster.getPrivilegeGroup();
    
    if ( listPriveGroups == null )
      return;
    
    for ( Iterator iPrivGroups = listPriveGroups.iterator(); iPrivGroups.hasNext(); )
    {
      VwPrivilegeGroup privGroup = (VwPrivilegeGroup)iPrivGroups.next();
      
      Map mapPrivRefs = new HashMap();
      
      List listPrivRefs = privGroup.getPrivilegeRef();
      
      if ( listPrivRefs == null )
        throw new Exception( "privilegeGroup '" + privGroup.getName() + "' must contain at least one privilegeRef element");

      for ( Iterator iPrivRefs = listPrivRefs.iterator(); iPrivRefs.hasNext(); )
      {
        VwPrivilegeRef privRef = (VwPrivilegeRef)iPrivRefs.next();
      
        if ( !m_mapPrivMaster.containsKey( privRef.getRef().toLowerCase() ))
          throw new Exception( "privilegeRef '" + privRef.getRef() + "' refers to a privilege that is not defined" );
        
        mapPrivRefs.put( privRef.getRef().toLowerCase(), null );
        
      } // end for()
      
      m_mapPrivGroupMaster .put( privGroup.getName().toLowerCase(), mapPrivRefs );
      
    } // end for()
    
  } // end buildPrivilegeGroupMaster()

  /**
   * Build the RolesMaster map for fast lookup
   * @throws Exception
   */
  private void buildRolesMaster() throws Exception
  {
    List listRoles = m_securityMaster.getRole();
    
    if ( listRoles == null )
      throw new Exception( "Cannot have empty Role list");
    
    for ( Iterator iRoles = listRoles.iterator(); iRoles.hasNext(); )
    {
      VwRole role = (VwRole)iRoles.next();
      m_mapRolesMaster.put( role.getName().toLowerCase(), role );
      
    }
  }


  /**
   * 
   * @throws Exception
   */
  private void expandRoles() throws Exception
  {
    List listRoleGroups = m_securityMaster.getRoleGroup();
    
    if ( listRoleGroups == null )
      return;
    
    for ( Iterator iRoleGroups = listRoleGroups.iterator(); iRoleGroups.hasNext(); )
    {
      VwRoleGroup roleGroup = (VwRoleGroup)iRoleGroups.next();
      
      Map mapRoles = new HashMap();
      
      List listRoles = roleGroup.getRoleRef();

      for ( Iterator iRoles = listRoles.iterator(); iRoles.hasNext(); )
      {
        VwRoleRef roleRef = (VwRoleRef)iRoles.next();
        
        // make sure role reference is valid
        if ( !m_mapRolesMaster.containsKey( roleRef.getRef().toLowerCase() ))
          throw new Exception( "The roleGroup '" + roleGroup.getName() + "' references roleRef '" + roleRef.getRef() +
                               "' which is not a defined role");
        
        mapRoles.put( roleRef.getRef().toLowerCase(), null );  
        
      } // end for
      
      m_mapRolesByRoleGroup.put( roleGroup.getName().toLowerCase(), mapRoles );
      
    } // end for
    
  }


  /**
   * Create a map of privileges for each role
   *
   */
  private void expandPrivileges() throws Exception
  {
    List listRoles = m_securityMaster.getRole();
    
    // flatten out any privilege groups in the privileges by roles map for fast lookup
    for ( Iterator iRoles = listRoles.iterator(); iRoles.hasNext(); )
    {
      VwRole role = (VwRole)iRoles.next();
      
      Map mapRolePrivRefs = new HashMap();
      
      List listPriveGroupRefs =  role.getPrivilegeGroupRef();
      
      // See if any privilege groups exist aand expand them
      if ( listPriveGroupRefs != null )
      {
        for ( Iterator iPrivGroupRefs = listPriveGroupRefs.iterator(); iPrivGroupRefs.hasNext(); )
        {
          VwPrivilegeGroupRef privGroupRef = (VwPrivilegeGroupRef)iPrivGroupRefs.next();
          Map mapPrivRef = (Map)m_mapPrivGroupMaster.get( privGroupRef.getRef().toLowerCase() );
          mapRolePrivRefs.putAll( mapPrivRef );
          
        }
      }
      
      // put privileges in map for fast lookup
      setPrivileges(  mapRolePrivRefs, role.getPrivilegeRef() );
      
      m_mapPrivilegesByRole.put( role.getName().toLowerCase(), mapRolePrivRefs );
      
    } // end for()
    
  }

  
  /**
   * 
   * @param mapPrivileges
   * @param listPrivilege
   */
  private void setPrivileges( Map mapPrivileges, List listPrivilegeRef ) throws Exception 
  {
    
    for ( Iterator iPrivRef = listPrivilegeRef.iterator(); iPrivRef.hasNext(); )
    {
      VwPrivilegeRef privRef = (VwPrivilegeRef)iPrivRef.next();
      
      // make sure privilege is in master document
      /*
      if ( strPrivGroupName != null && (!m_mapPrivMaster.containsKey( privRef.getRef().toLowerCase() ) ))
          throw new Exception( "The privilegeGroup '" + strPrivGroupName + "' references privilege '" + privRef.getRef() +
                               "' which is not a defined privilege");
        
      */
      mapPrivileges.put( privRef.getRef().toLowerCase(), null );
      
    } // end for
    
  } // end setPrivileges()
}
