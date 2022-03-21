/*
============================================================================================

                        V o z z W o r k s   C o d e   G e n e r a t o r                       

                              2009 by V o z z W a r e   L L C                              

    Source File Name: CodeSnippetsWriter.java

    Author:           

    Date Generated:   05-28-2008

    Time Generated:   07:10:41

============================================================================================
*/

package com.vozzware.db.util;

import com.vozzware.util.VwResourceStoreFactory;
import com.vozzware.xml.VwBeanToXml;

import java.io.File;
import java.net.URL;


public class CodeSnippetsWriter
{

  private VwBeanToXml           m_btx;                          

  /**
   * Writer 

   */
  public static String toString( CodeSnippets objToWrite ) throws Exception
  {

    URL urlSchemaXSD = VwResourceStoreFactory.getInstance().getStore().getDocument( "VwDaoCodeSnippets.xml.xsd" );

    VwBeanToXml btx = new VwBeanToXml();


    btx.addSchema( urlSchemaXSD, CodeSnippets.class.getPackage() );
    btx.setFormattedOutput( true, 0 );
    return btx.serialize( null, objToWrite  );
  } // End of toString()



  /**
   * Writer 

   */
  public static void write( CodeSnippets objToWrite, File fileToWrite ) throws Exception
  {

    URL urlSchemaXSD = VwResourceStoreFactory.getInstance().getStore().getDocument( "VwDaoCodeSnippets.xml.xsd" );

    VwBeanToXml btx = new VwBeanToXml();


    btx.addSchema( urlSchemaXSD, CodeSnippets.class.getPackage() );
    btx.setFormattedOutput( true, 0 );
    btx.serialize( null, objToWrite, fileToWrite  );
  } // End of write()


} // *** End of class CodeSnippetsWriter{}

// *** End Of CodeSnippetsWriter.java