/*
============================================================================================

                        V o z z W or k s    C o d e   G e n e r a t o r                       

                              2009 by I Technologies Corp                              

    Source File Name: VwReportSpecWriter.java

    Author:           

    Date Generated:   08-07-2007

    Time Generated:   07:26:18

============================================================================================
*/

package com.vozzware.print;

import com.vozzware.util.VwResourceStoreFactory;
import com.vozzware.xml.VwBeanToXml;

import javax.xml.schema.util.XmlDeSerializer;
import java.io.File;
import java.net.URL;


public class VwReportSpecWriter
{


  /**
   * Writer 
   * 
   * @param objToWrite
   */
  public static String toString( VwReportSpec objToWrite ) throws Exception
  {

    URL urlSchemaXSD = VwResourceStoreFactory.getInstance().getStore().getDocument( "VwReportSpec.xsd" );

    VwBeanToXml btx = new VwBeanToXml();

    btx.setFeature( XmlDeSerializer.ATTRIBUTE_MODEL, true  );

    btx.addSchema( urlSchemaXSD, VwReportSpec.class.getPackage() );
    btx.setFormattedOutput( true, 0 );
    btx.setContentMethods( VwReportSpec.class, "getReportLine"  );
    btx.setContentMethods( VwReportLine.class, "getElement"  );
    return btx.serialize( null, objToWrite  );
  } // End of toString()



  /**
   * Writer 
   * 
   * @param objToWrite   * 
   * @param fileToWrite
   */
  public static void write( VwReportSpec objToWrite, File fileToWrite ) throws Exception
  {

    URL urlSchemaXSD = VwResourceStoreFactory.getInstance().getStore().getDocument( "VwReportSpec.xsd" );

    VwBeanToXml btx = new VwBeanToXml();

    btx.setFeature( XmlDeSerializer.ATTRIBUTE_MODEL, true  );

    btx.addSchema( urlSchemaXSD, VwReportSpec.class.getPackage() );
    btx.setFormattedOutput( true, 0 );
    btx.setContentMethods( VwReportSpec.class, "getReportLine"  );
    btx.setContentMethods( VwReportLine.class, "getElement"  );
    btx.serialize( null, objToWrite, fileToWrite  );
  } // End of write()


} // *** End of class VwReportSpecWriter{}

// *** End Of VwReportSpecWriter.java