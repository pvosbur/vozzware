/*
============================================================================================

                       V o z z W o r k s   C o d e   G e n e r a t o r                      

                               2009 by V o z z W a r e   L L C                              

    Source File Name: VwSqlMappingDocumentWriter.java

    Author:           

    Date Generated:   06-22-2011

    Time Generated:   08:19:44

============================================================================================
*/

package com.vozzware.db.util;

import com.vozzware.util.VwResourceStoreFactory;
import com.vozzware.xml.VwBeanToXml;

import java.io.File;
import java.net.URL;


public class VwSqlMappingDocumentWriter
{


  /**
   * Serializes the bean to XML in a String
   * @throws Exception if any serialization errors occur
   */
  public static String toString( VwSqlMappingDocument objToWrite ) throws Exception
  {
    return toString( objToWrite, null );
  } // End of toString()



  /**
   * Serializes the bean to XML in a String
   * @throws Exception if any serialization errors occur
   */
  public static String toString( VwSqlMappingDocument objToWrite, String strCommentHeader ) throws Exception
  {
    VwBeanToXml btx = config( strCommentHeader );
    return btx.serialize( null, objToWrite );
  } // End of toString()



  /**
   * Serializes the bean to XML and writes the XML to the file specified
   * @throws Exception if any file io errors occur
   */
  public static void write( VwSqlMappingDocument objToWrite, File fileToWrite ) throws Exception
  {
    write( objToWrite, fileToWrite, null );
  } // End of write()



  /**
   * Serializes the bean to XML and writes the XML to the file specified
   * @throws Exception if any file io errors occur
   */
  public static void write( VwSqlMappingDocument objToWrite, File fileToWrite, String strCommentHeader ) throws Exception
  {
    VwBeanToXml btx = config( strCommentHeader );
    btx.serialize( null, objToWrite, fileToWrite  );
  } // End of write()



  /**
   * Configures VwBeanToXml properties
   */
  private static VwBeanToXml config( String strCommentHeader ) throws Exception
  {
    URL urlSchemaXSD = VwResourceStoreFactory.getInstance().getStore().getDocument( "VwSchemaMappingDocument.xsd" );

    VwBeanToXml btx = new VwBeanToXml( "<?xml version=\"1.0\"?>", null, true, 0 );
    
    if ( strCommentHeader != null )
       btx.setDocumentCommentHeader( strCommentHeader );
    
    btx.addSchema( urlSchemaXSD, VwSqlMappingDocument.class.getPackage() );
      
    return btx;
  } // End of config()


} // *** End of class VwSqlMappingDocumentWriter{}

// *** End Of VwSqlMappingDocumentWriter.java