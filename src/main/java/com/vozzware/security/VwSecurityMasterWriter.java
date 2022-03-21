/*
============================================================================================

                        V o z z W o r k s   C o d e   G e n e r a t o r                       

                              2009 by I Technologies Corp                              

    Source File Name: VwSecurityMasterWriter.java

    Author:           

    Date Generated:   04-13-2007

    Time Generated:   08:56:09

============================================================================================
*/

package com.vozzware.security;

import com.vozzware.util.VwResourceStoreFactory;
import com.vozzware.xml.VwBeanToXml;

import javax.xml.schema.util.XmlDeSerializer;
import java.net.URL;


public class VwSecurityMasterWriter
{


  /**
   * Writer 
   * 
   * @param objToWrite
   */
  public static String write( VwSecurityMaster objToWrite ) throws Exception
  {

    URL urlSchemaXSD = VwResourceStoreFactory.getInstance().getStore().getDocument( "VwRolesSecurity.xsd" );

    VwBeanToXml btx = new VwBeanToXml();

    btx.setFeature( XmlDeSerializer.ATTRIBUTE_MODEL, true  );

    btx.addSchema( urlSchemaXSD, VwSecurityMaster.class.getPackage() );
    btx.setFormattedOutput( true, 0 );
    return btx.serialize( null, objToWrite  );
  } // End of write()


} // *** End of class VwSecurityMasterWriter{}

// *** End Of VwSecurityMasterWriter.java