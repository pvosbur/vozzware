/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwLDAPAttrSearch.java

============================================================================================
*/
package com.vozzware.ldap;

import com.vozzware.util.VwResourceMgr;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/*
 * Retrieve several attributes of a particular entry.
 * 
 * [equivalent to getattrs.c in Netscape SDK]
 */
public class VwLDAPAttrSearch
{
  private DirContext m_ctx;

  /**
   * Constructor The Constructor attempts to connect to the the ldap server
   * based on the values defined in the ldap.properties bundle.
   * 
   * @throws Exception
   */
  public VwLDAPAttrSearch() throws Exception
  {
    setup();

  }

  private void setup() throws Exception
  {
    VwResourceMgr.loadBundle( "ldap", true );

    Hashtable env = new Hashtable();

    // Load required properties to connect and login to the ldap server
    env.put( Context.INITIAL_CONTEXT_FACTORY, VwResourceMgr.getString( "itc.ldap.initialCtxFactory" ) );
    env.put( Context.SECURITY_PRINCIPAL, VwResourceMgr.getString( "itc.ldap.securityPrincipal" ) );
    env.put( Context.SECURITY_CREDENTIALS, VwResourceMgr.getString( "itc.ldap.securityPrincipal" ) );

    /* Specify host and port to use for directory service */
    env.put( Context.PROVIDER_URL, VwResourceMgr.getString( "itc.ldap.providerUrll" ) );

    // The the directory context needed for future operations
    m_ctx = new InitialDirContext( env );

  }

  public Map doAttrSearch( String strLdapQuery, String[] astrSearchAttrs ) throws Exception
  {

    Attributes result = m_ctx.getAttributes( strLdapQuery, astrSearchAttrs );

    if (result == null)
      return null;

    Map mapAttrValues = new HashMap();

    // Enumerate the result attrubutes and build map of attribute values
    for (int x = 0; x < astrSearchAttrs.length; x++)
    {
      Attribute attr = result.get( astrSearchAttrs[x] );
      if (attr != null)
      {
        List listValues = new ArrayList();

        for ( NamingEnumeration vals = attr.getAll(); vals.hasMoreElements(); )
        {
          // Attribute attr1 = (Attribute)vals.nextElement();
          Object objAttr = vals.nextElement();
          
          if ( objAttr.getClass().isArray() )
          {
            String strVal = null;
            
            if ( objAttr instanceof byte[] )
              strVal = new String( (byte[]) objAttr );
            else
            if ( objAttr instanceof char[] )
              strVal = new String( (char[]) objAttr );
            
            listValues.add( strVal );
              
          }
          else
            listValues.add( objAttr.toString() );

        } // end for()
        
        mapAttrValues.put( astrSearchAttrs[x], listValues );
        
      } // end for

    } // end for()

    return mapAttrValues;

  } // end doAttrSearch()

}
