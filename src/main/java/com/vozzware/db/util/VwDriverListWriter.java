/*
============================================================================================

                        V o z z W o r k s   C o d e   G e n e r a t o r                       

                              2009 by V o z z W a r e   L L C                              

    Source File Name: VwDriverListWriter.java

    Author:           

    Date Generated:   12-29-2007

    Time Generated:   06:56:55

============================================================================================
*/

package com.vozzware.db.util;

import com.vozzware.util.VwResourceStoreFactory;
import com.vozzware.xml.VwBeanToXml;

import javax.xml.schema.util.XmlDeSerializer;
import java.io.File;
import java.net.URL;


public class VwDriverListWriter
{


  /**
   * Writer 

   */
  public static String toString( VwDriverList objToWrite ) throws Exception
  {

    VwBeanToXml btx = config();
    return btx.serialize( null, objToWrite  );
  } // End of toString()



  /**
   * Writer 

   */
  public static void write( VwDriverList objToWrite, File fileToWrite ) throws Exception
  {
    VwBeanToXml btx = config();
    btx.serialize( null, objToWrite, fileToWrite  );
  } // End of write()

  
  private static VwBeanToXml config() throws Exception
  {
    URL urlSchemaXSD = VwResourceStoreFactory.getInstance().getStore().getDocument( "VwDatasourceDriver.xsd" );

    VwBeanToXml btx = new VwBeanToXml();

    btx.setFeature( XmlDeSerializer.ATTRIBUTE_MODEL, true  );

    btx.addSchema( urlSchemaXSD, VwDriverList.class.getPackage() );
    btx.setFormattedOutput( true, 0 );
    btx.setContentMethods( VwDriver.class, "getConnectionPool,getUrl"  );
    btx.setContentMethods( VwDriverList.class, "getDriver"  );
    return btx; 
  }

} // *** End of class VwDriverListWriter{}

// *** End Of VwDriverListWriter.java