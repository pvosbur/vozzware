/*
============================================================================================

                        V o z z W o r k s   C o d e   G e n e r a t o r                       

                              2009 by I Technologies Corp                              

    Source File Name: VwSecurityMasterReader.java

    Author:           

    Date Generated:   04-13-2007

    Time Generated:   08:56:09

============================================================================================
*/

package com.vozzware.security;

import com.vozzware.util.VwResourceStoreFactory;
import com.vozzware.xml.VwXmlToBean;
import org.xml.sax.InputSource;

import javax.xml.schema.util.XmlDeSerializer;
import java.net.URL;


public class VwSecurityMasterReader
{


  /**
   * Reader
   * 
   * @param urlDoc
   */
  public static VwSecurityMaster read( URL urlDoc ) throws Exception
  {

    URL urlSchemaXSD = VwResourceStoreFactory.getInstance().getStore().getDocument( "VwRolesSecurity.xsd" );

    VwXmlToBean xtb = new VwXmlToBean();

    xtb.setFeature( XmlDeSerializer.ATTRIBUTE_MODEL, true  );
    return (VwSecurityMaster) xtb.deSerialize( new InputSource( urlDoc.openStream() ), VwSecurityMaster.class, urlSchemaXSD );
  } // End of read()


} // *** End of class VwSecurityMasterReader{}

// *** End Of VwSecurityMasterReader.java