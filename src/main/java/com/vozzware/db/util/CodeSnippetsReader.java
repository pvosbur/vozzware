/*
============================================================================================

                        V o z z W o r k s   C o d e   G e n e r a t o r                       

                              2009 by V o z z W a r e   L L C                              

    Source File Name: CodeSnippetsReader.java

    Author:           

    Date Generated:   05-28-2008

    Time Generated:   07:10:41

============================================================================================
*/

package com.vozzware.db.util;

import com.vozzware.util.VwResourceStoreFactory;
import com.vozzware.xml.VwXmlToBean;
import org.xml.sax.InputSource;

import javax.xml.schema.util.XmlDeSerializer;
import java.net.URL;


public class CodeSnippetsReader
{


  /**
   * Reader

   */
  public static CodeSnippets read( URL urlDoc ) throws Exception
  {

    URL urlSchemaXSD = VwResourceStoreFactory.getInstance().getStore().getDocument( "VwDaoCodeSnippets.xml.xsd" );

    VwXmlToBean xtb = new VwXmlToBean();

    xtb.setFeature( XmlDeSerializer.EXPAND_MACROS, false  );
    return (CodeSnippets) xtb.deSerialize( new InputSource( urlDoc.openStream() ), CodeSnippets.class, urlSchemaXSD );
  } // End of read()


} // *** End of class CodeSnippetsReader{}

// *** End Of CodeSnippetsReader.java