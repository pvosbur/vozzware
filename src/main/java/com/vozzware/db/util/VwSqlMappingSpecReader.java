/*
============================================================================================

                       V o z z W o r k s   C o d e   G e n e r a t o r                      

                               2009 by V o z z W a r e   L L C                              

    Source File Name: VwSqlMappingSpecReader.java

    Author:           

    Date Generated:   05-21-2011

    Time Generated:   09:25:01

============================================================================================
*/

package com.vozzware.db.util;

import com.vozzware.util.VwResourceStoreFactory;
import com.vozzware.xml.VwXmlToBean;
import org.xml.sax.InputSource;

import javax.xml.schema.util.XmlDeSerializer;
import java.net.URL;


public class VwSqlMappingSpecReader
{


  /**
   * Reader
   */
  public static VwSqlMappingSpec read( URL urlDoc ) throws Exception
  {
    URL urlClassgenSchema = VwResourceStoreFactory.getInstance().getStore().getDocument( "VwSchemaObjectMapper.xsd" );
     
    VwXmlToBean xtb = new VwXmlToBean();
    xtb.setFeature( XmlDeSerializer.ATTRIBUTE_MODEL, true  );
    
    return (VwSqlMappingSpec)xtb.deSerialize( new InputSource( urlDoc.openStream() ), VwSqlMappingSpec.class, urlClassgenSchema  );
  } // End of read()


} // *** End of class VwSqlMappingSpecReader{}

// *** End Of VwSqlMappingSpecReader.java