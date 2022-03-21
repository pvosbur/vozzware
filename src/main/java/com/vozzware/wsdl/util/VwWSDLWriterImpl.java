/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwWSDLWriterImpl.java

Create Date: Apr 11, 2006
============================================================================================
*/
package com.vozzware.wsdl.util;

import com.vozzware.wsdl.VwBindingFaultImpl;
import com.vozzware.wsdl.VwBindingImpl;
import com.vozzware.wsdl.VwBindingInputImpl;
import com.vozzware.wsdl.VwBindingOperationImpl;
import com.vozzware.wsdl.VwBindingOutputImpl;
import com.vozzware.wsdl.VwDefinitionImpl;
import com.vozzware.wsdl.VwFaultImpl;
import com.vozzware.wsdl.VwImportImpl;
import com.vozzware.wsdl.VwInputImpl;
import com.vozzware.wsdl.VwMessageImpl;
import com.vozzware.wsdl.VwOperationImpl;
import com.vozzware.wsdl.VwOutputImpl;
import com.vozzware.wsdl.VwPartImpl;
import com.vozzware.wsdl.VwPortImpl;
import com.vozzware.wsdl.VwPortTypeImpl;
import com.vozzware.wsdl.VwServiceImpl;
import com.vozzware.wsdl.VwTypesImpl;
import com.vozzware.wsdl.extensions.VwUnknownExtensibilityElementImpl;
import com.vozzware.wsdl.extensions.http.VwHTTPAddressImpl;
import com.vozzware.wsdl.extensions.http.VwHTTPBindingImpl;
import com.vozzware.wsdl.extensions.http.VwHTTPOperationImpl;
import com.vozzware.wsdl.extensions.http.VwHTTPUrlEncodedImpl;
import com.vozzware.wsdl.extensions.http.VwHTTPUrlReplacementImpl;
import com.vozzware.wsdl.extensions.mime.VwMIMEContentImpl;
import com.vozzware.wsdl.extensions.mime.VwMIMEMimeXmlImpl;
import com.vozzware.wsdl.extensions.mime.VwMIMEMultipartRelatedImpl;
import com.vozzware.wsdl.extensions.mime.VwMIMEPartImpl;
import com.vozzware.wsdl.extensions.soap.VwSOAPAddressImpl;
import com.vozzware.wsdl.extensions.soap.VwSOAPBindingImpl;
import com.vozzware.wsdl.extensions.soap.VwSOAPBodyImpl;
import com.vozzware.wsdl.extensions.soap.VwSOAPFaultImpl;
import com.vozzware.wsdl.extensions.soap.VwSOAPOperationImpl;
import com.vozzware.xml.schema.util.VwSchemaWriterImpl;

import javax.wsdl.Definition;
import javax.wsdl.util.WSDLWriter;
import javax.xml.schema.util.XmlFeatures;
import javax.xml.schema.util.XmlSerializer;
import javax.xml.schema.util.XmlSerializerFactory;
import java.io.File;
import java.io.FileWriter;
import java.net.URL;


public class VwWSDLWriterImpl implements WSDLWriter
{

  public String writeWsdl( Definition def ) throws Exception
  {
    XmlSerializer btx = XmlSerializerFactory.getSerializer( "com.vozzware.xml.VwBeanToXml");
    setProperties( btx );

    String strWsdl = btx.serialize( null, def );
    return strWsdl;

  } // end writeWsdl()

  /**
   * Writes the Schema object to the specified file
   * @param def The Schema object to write
   * @param fileWSDL The file to write
   * @throws Exception if any io error occur
   */
  public void writeWsdl( Definition def, File fileWSDL ) throws Exception
  {
    String strWsdl = def.toString();
    
    FileWriter writer = new FileWriter( fileWSDL );
    writer.write( strWsdl );
    writer.close();
    
  } // end writeSchema()

  public void setProperties( XmlSerializer btx ) throws Exception
  {
    btx.setFeature( XmlFeatures.ATTRIBUTE_MODEL, true );
    
    btx.setFeature( XmlFeatures.USE_NAMESPACES, true );
    
    VwSchemaWriterImpl.setProperties( btx );
    
    ClassLoader ldr = Thread.currentThread().getContextClassLoader();
    
    URL urlWsdl= ldr.getResource( "com/vozzware/wsdl/util/wsdl11.xsd" );
    
    // Add schemas
    btx.addSchema( urlWsdl, VwDefinitionImpl.class.getPackage() );

    URL urlSoap= ldr.getResource( "com/vozzware/wsdl/util/SoapBinding11.xsd" );
    btx.addSchema( urlSoap, VwSOAPAddressImpl.class.getPackage() );

    URL urlHttp= ldr.getResource( "com/vozzware/wsdl/util/HttpBinding11.xsd" );
    btx.addSchema( urlHttp, VwHTTPAddressImpl.class.getPackage() );

    URL urlMime= ldr.getResource( "com/vozzware/wsdl/util/MimeBinding11.xsd" );
    btx.addSchema(  urlMime, VwMIMEContentImpl.class.getPackage() );

    btx.setObjectElementName( VwDefinitionImpl.class, "definitions" );
    btx.setObjectElementName( VwMessageImpl.class, "message" );
    btx.setObjectElementName( VwPartImpl.class, "part" );
    btx.setObjectElementName( VwPortTypeImpl.class, "portType" );
    btx.setObjectElementName( VwOperationImpl.class, "operation" );
    btx.setObjectElementName( VwInputImpl.class, "input" );
    btx.setObjectElementName( VwOutputImpl.class, "output" );
    btx.setObjectElementName( VwFaultImpl.class, "fault" );
    btx.setObjectElementName( VwBindingImpl.class, "binding" );
    btx.setObjectElementName( VwBindingInputImpl.class, "input" );
    btx.setObjectElementName( VwBindingOutputImpl.class, "output" );
    btx.setObjectElementName( VwBindingFaultImpl.class, "fault" );
    btx.setObjectElementName( VwBindingOperationImpl.class, "operation" );
    btx.setObjectElementName( VwImportImpl.class, "import" );
    btx.setObjectElementName( VwServiceImpl.class, "service" );
    btx.setObjectElementName( VwPortImpl.class, "port" );
    btx.setObjectElementName( VwTypesImpl.class, "types" );

    btx.setObjectElementName( VwHTTPAddressImpl.class, "address" );
    btx.setObjectElementName( VwHTTPBindingImpl.class, "binding" );
    btx.setObjectElementName( VwHTTPOperationImpl.class, "operation" );
    btx.setObjectElementName( VwHTTPUrlReplacementImpl.class, "urlReplacement" );
    btx.setObjectElementName( VwHTTPUrlEncodedImpl.class, "urlEncoded" );


    btx.setObjectElementName( VwSOAPAddressImpl.class, "address" );
    btx.setObjectElementName( VwSOAPBindingImpl.class, "binding" );
    btx.setObjectElementName( VwSOAPOperationImpl.class, "operation" );
    btx.setObjectElementName( VwSOAPBodyImpl.class, "body" );
    btx.setObjectElementName( VwSOAPFaultImpl.class, "fault" );

    btx.setObjectElementName( VwMIMEContentImpl.class, "content" );
    btx.setObjectElementName( VwMIMEMimeXmlImpl.class, "mimeXml" );
    btx.setObjectElementName( VwMIMEMultipartRelatedImpl.class, "multipartRelated" );
    btx.setObjectElementName( VwMIMEPartImpl.class, "part" );
    btx.setObjectElementName( VwUnknownExtensibilityElementImpl.class, "" );

    btx.setContentMethods( VwUnknownExtensibilityElementImpl.class, "getElement");
    btx.setContentMethods( VwDefinitionImpl.class, "getContent");
    btx.setContentMethods( VwTypesImpl.class, "getContent");
    btx.setContentMethods( VwMessageImpl.class, "getContent");
    btx.setContentMethods( VwPortTypeImpl.class, "getOperations");
    btx.setContentMethods( VwOperationImpl.class, "getContent");
    btx.setContentMethods( VwBindingImpl.class, "getContent");
    btx.setContentMethods( VwBindingOperationImpl.class, "getContent");
    btx.setContentMethods( VwBindingInputImpl.class, "getContent");
    btx.setContentMethods( VwBindingOutputImpl.class, "getContent");
    btx.setContentMethods( VwBindingFaultImpl.class, "getContent");
    btx.setContentMethods( VwServiceImpl.class, "getContent");
    btx.setContentMethods( VwPortImpl.class, "getContent");
    btx.setContentMethods( VwMIMEMultipartRelatedImpl.class, "getContent");
    btx.setContentMethods( VwMIMEPartImpl.class, "getContent");

  }

  public static void main( String[] args )
  {

    try
    {
      VwWSDLReaderImpl rdr = new VwWSDLReaderImpl();
      String str = rdr.getClass().getPackage().getName();
      str = rdr.getClass().getPackage().toString();

      //Definition def = rdr.readWsdl( new URL( "file:///wsdl/LoginService.wsdl"));
      Definition def = rdr.readWsdl( new URL( "file:///wsif-2.0/test/mime/mime.wsdl"));

      WSDLWriter writer = new VwWSDLWriterImpl();
      String strXml = writer.writeWsdl( def );
      System.out.println( strXml  );


    }
    catch( Exception ex )
    {
      ex.printStackTrace();
    }
  }

} // end of class VwWSDLWriterImpl()

// *** End of VwWSDLWriterImpl.java ***
