/*
============================================================================================

                       V o z z W o r k s   C o d e   G e n e r a t o r                      

                               2009 by V o z z W a r e   L L C                              

    Source File Name: VwSqlMappingDocumentReader.java

    Author:           

    Date Generated:   06-22-2011

    Time Generated:   08:19:44

============================================================================================
*/

package com.vozzware.db.util;

import com.vozzware.util.VwResourceStoreFactory;
import com.vozzware.xml.VwXmlToBean;
import org.xml.sax.InputSource;

import java.net.URL;


public class VwSqlMappingDocumentReader
{


  /**
   * Reader
   */
  public static VwSqlMappingDocument read( URL urlDoc ) throws Exception
  {
    URL urlClassgenSchema = VwResourceStoreFactory.getInstance().getStore().getDocument( "VwSchemaMappingDocument.xsd" );
     
    VwXmlToBean xtb = new VwXmlToBean();
        
    return (VwSqlMappingDocument)xtb.deSerialize( new InputSource( urlDoc.openStream() ), VwSqlMappingDocument.class, urlClassgenSchema  );
  } // End of read()


} // *** End of class VwSqlMappingDocumentReader{}

// *** End Of VwSqlMappingDocumentReader.java