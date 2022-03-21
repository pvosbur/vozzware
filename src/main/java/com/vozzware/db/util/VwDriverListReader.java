/*
============================================================================================

                        V o z z W o r k s   C o d e   G e n e r a t o r

                              2009 by V o z z W a r e   L L C

    Source File Name: VwMobileAppReader.java

    Author:

    Date Generated:   12-29-2007

    Time Generated:   06:56:55

============================================================================================
*/

package com.vozzware.db.util;

import com.vozzware.util.VwResourceStoreFactory;
import com.vozzware.xml.VwXmlToBean;
import org.xml.sax.InputSource;

import javax.xml.schema.util.XmlDeSerializer;
import java.net.URL;


public class VwDriverListReader
{


  /**
   * Reader

   */
  public static VwDriverList read( URL urlDoc ) throws Exception
  {

    URL urlSchemaXSD = VwResourceStoreFactory.getInstance().getStore().getDocument( "VwDatasourceDriver.xsd" );

    VwXmlToBean xtb = new VwXmlToBean();

    xtb.setFeature( XmlDeSerializer.ATTRIBUTE_MODEL, true  );
    return (VwDriverList) xtb.deSerialize( new InputSource( urlDoc.openStream() ), VwDriverList.class, urlSchemaXSD );
  } // End of read()


} // *** End of class VwMobileAppReader{}

// *** End Of VwMobileAppReader.java
