/*
============================================================================================

                        V o z z W o r k s   C o d e   G e n e r a t o r                       

                              2009 by I Technologies Corp                              

    Source File Name: VwReportSpecReader.java

    Author:           

    Date Generated:   08-07-2007

    Time Generated:   07:26:18

============================================================================================
*/

package com.vozzware.print;

import com.vozzware.util.VwResourceStoreFactory;
import com.vozzware.xml.VwXmlToBean;
import org.xml.sax.InputSource;

import javax.xml.schema.util.XmlDeSerializer;
import java.net.URL;


public class VwReportSpecReader
{


  /**
   * Reader
   * 
   * @param urlDoc
   */
  public static VwReportSpec read( URL urlDoc ) throws Exception
  {

    URL urlSchemaXSD = VwResourceStoreFactory.getInstance().getStore().getDocument( "VwReportSpec.xsd" );

    VwXmlToBean xtb = new VwXmlToBean();

    xtb.setFeature( XmlDeSerializer.ATTRIBUTE_MODEL, true  );
    return (VwReportSpec) xtb.deSerialize( new InputSource( urlDoc.openStream() ), VwReportSpec.class, urlSchemaXSD );
  } // End of read()


} // *** End of class VwReportSpecReader{}

// *** End Of VwReportSpecReader.java