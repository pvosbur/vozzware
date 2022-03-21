/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwWSDLReaderImpl.java

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
import com.vozzware.wsdl.extensions.soap.VwSOAPOperationImpl;
import com.vozzware.xml.schema.VwSchemaImpl;
import com.vozzware.xml.schema.util.VwSchemaReaderImpl;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import javax.wsdl.Definition;
import javax.wsdl.Types;
import javax.wsdl.extensions.ExtensibilityElementSupport;
import javax.wsdl.util.WSDLReader;
import javax.xml.schema.Documentation;
import javax.xml.schema.InvalidSchemaLocationException;
import javax.xml.schema.Schema;
import javax.xml.schema.util.UnknownElementHandler;
import javax.xml.schema.util.XmlCloseElementEvent;
import javax.xml.schema.util.XmlCloseElementListener;
import javax.xml.schema.util.XmlDeSerializer;
import javax.xml.schema.util.XmlDeSerializerFactory;
import javax.xml.schema.util.XmlOpenElementEvent;
import javax.xml.schema.util.XmlOpenElementListener;
import java.io.File;
import java.net.URL;
import java.util.Stack;

public class VwWSDLReaderImpl implements XmlCloseElementListener, XmlOpenElementListener,WSDLReader, UnknownElementHandler
{

  /**
   * Reads an XML Schema specified by the URL
   *
   * @param urlSchema The URL to the schema document
   * @return a Schema instance
   *
   * @throws Exception if any IO or xml format errors in the schema document occur
   */
  public Definition readWsdl( URL urlSchema ) throws Exception
  {
    XmlDeSerializer xtb = XmlDeSerializerFactory.getDeSerializer( "com.vozzware.xml.VwXmlToBean");

    setWsdlProperties( xtb );

    InputSource ins = new InputSource( urlSchema.openStream() );

    Definition wsdlDef = (Definition)xtb.deSerialize( ins, VwDefinitionImpl.class, null );

    return wsdlDef;

  } // end readSchema()

  /**
   * Helper to just extract an XML Schema (if one exists) from a wsdl document
   * @param urlWsdl a URL to the wsdl document
   * @return a Schema instance if one is defined in the types section of the WSDL document 
   * @throws Exception
   */
  public Schema extractSchema( URL urlWsdl ) throws Exception
  {
    Definition def = readWsdl( urlWsdl );
    Types types = def.getTypes();
    if ( types == null )
      throw new Exception( "WSDL document: " + urlWsdl.toExternalForm() + " does not contain a types section, no schema exists");
    
    Schema schema = types.getSchema();
    
    if ( schema == null )
      throw new Exception( "WSDL document: " + urlWsdl.toExternalForm() + " does not contain a valis schema in the types section");
      
    return schema;
    
  }

  /**
   *
   * @param strLocationURI
   * @return
   * @throws Exception
   */
  public Definition readWsdl( String strLocationURI ) throws Exception
  {
    XmlDeSerializer xtb = XmlDeSerializerFactory.getDeSerializer( "com.vozzware.xml.VwXmlToBean");

    setWsdlProperties( xtb );
 
    URL urlSchema = null;

    if ( !strLocationURI.startsWith( "http:") && !strLocationURI.startsWith( "file:"))
    {
      // Assume this is a local file and we need to create a local file URL

      File fileCurDir = new File( ".");

      strLocationURI =  "file:///" +  fileCurDir.getAbsolutePath() + "/" + strLocationURI;

    }
    try
    {
      urlSchema = new URL( strLocationURI );
    }
    catch( Exception ex )
    {
      throw new InvalidSchemaLocationException( ex.toString() );

    }

    
    InputSource ins = new InputSource( urlSchema.openStream() );

    Definition wsdlDef = (Definition)xtb.deSerialize( ins, VwDefinitionImpl.class, null );

    return wsdlDef;

  }


  private void setWsdlProperties( XmlDeSerializer xtb ) throws Exception
  {
    VwSchemaReaderImpl.setSchemaProperties( xtb, this );

    xtb.setUnknownElementHanlder( this );
    xtb.setOpenElementListener( "operation", "http://schemas.xmlsoap.org/wsdl/", this );
    xtb.setOpenElementListener( "input", "http://schemas.xmlsoap.org/wsdl/", this );
    xtb.setOpenElementListener( "output", "http://schemas.xmlsoap.org/wsdl/", this );
    xtb.setOpenElementListener( "fault", "http://schemas.xmlsoap.org/wsdl/", this );

    xtb.setCloseElementListener( "operation", "http://schemas.xmlsoap.org/wsdl/", this );

    xtb.setTopLevelElementName( "definitions" );
    
    xtb.setElementHandler( "types", VwTypesImpl.class );
    xtb.setElementHandler( "schema", VwSchemaImpl.class );
    xtb.setElementHandler( "message", VwMessageImpl.class );
    xtb.setElementHandler( "part", VwPartImpl.class );
    xtb.setElementHandler( "portType", VwPortTypeImpl.class );
    xtb.setElementHandler( "binding", VwBindingImpl.class );
    xtb.setElementHandler( "service", VwServiceImpl.class );
    xtb.setElementHandler( "port", VwPortImpl.class );

    xtb.setElementHandler( "address", "http://schemas.xmlsoap.org/wsdl/soap/", VwSOAPAddressImpl.class );
    xtb.setElementHandler( "binding", "http://schemas.xmlsoap.org/wsdl/soap/", VwSOAPBindingImpl.class );
    xtb.setElementHandler( "body", "http://schemas.xmlsoap.org/wsdl/soap/", VwSOAPBodyImpl.class );
    xtb.setElementHandler( "operation", "http://schemas.xmlsoap.org/wsdl/soap/", VwSOAPOperationImpl.class );

    xtb.setElementHandler( "address", "http://schemas.xmlsoap.org/wsdl/http/", VwHTTPAddressImpl.class );
    xtb.setElementHandler( "binding", "http://schemas.xmlsoap.org/wsdl/http/", VwHTTPBindingImpl.class );
    xtb.setElementHandler( "urlEncoded", "http://schemas.xmlsoap.org/wsdl/http/", VwHTTPUrlEncodedImpl.class );
    xtb.setElementHandler( "urlReplacement", "http://schemas.xmlsoap.org/wsdl/http/", VwHTTPUrlReplacementImpl.class );
    xtb.setElementHandler( "operation", "http://schemas.xmlsoap.org/wsdl/http/", VwHTTPOperationImpl.class );

    xtb.setElementHandler( "mimeXml", "http://schemas.xmlsoap.org/wsdl/mime/", VwMIMEMimeXmlImpl.class);
    xtb.setElementHandler( "part", "http://schemas.xmlsoap.org/wsdl/mime/", VwMIMEPartImpl.class);
    xtb.setElementHandler( "content", "http://schemas.xmlsoap.org/wsdl/mime/", VwMIMEContentImpl.class);
    xtb.setElementHandler( "multipartRelated", "http://schemas.xmlsoap.org/wsdl/mime/", VwMIMEMultipartRelatedImpl.class);

    xtb.addObjectSetterAlias( "address", "http://schemas.xmlsoap.org/wsdl/soap/" , "extensibilityElement");
    xtb.addObjectSetterAlias( "binding", "http://schemas.xmlsoap.org/wsdl/soap/", "extensibilityElement");
    xtb.addObjectSetterAlias( "body", "http://schemas.xmlsoap.org/wsdl/soap/", "extensibilityElement");
    xtb.addObjectSetterAlias( "operation", "http://schemas.xmlsoap.org/wsdl/soap/", "extensibilityElement");

    xtb.addObjectSetterAlias( "address", "http://schemas.xmlsoap.org/wsdl/http/" , "extensibilityElement");
    xtb.addObjectSetterAlias( "binding", "http://schemas.xmlsoap.org/wsdl/http/", "extensibilityElement");
    xtb.addObjectSetterAlias( "operation", "http://schemas.xmlsoap.org/wsdl/http/", "extensibilityElement");
    xtb.addObjectSetterAlias( "urlReplacement", "http://schemas.xmlsoap.org/wsdl/http/", "extensibilityElement");
    xtb.addObjectSetterAlias( "urlEncoded", "http://schemas.xmlsoap.org/wsdl/http/", "extensibilityElement");

    xtb.addObjectSetterAlias( "mimeXml", "http://schemas.xmlsoap.org/wsdl/mime/", "extensibilityElement");
    xtb.addObjectSetterAlias( "part", "http://schemas.xmlsoap.org/wsdl/mime/", "extensibilityElement");
    xtb.addObjectSetterAlias( "content", "http://schemas.xmlsoap.org/wsdl/mime/", "extensibilityElement");
    xtb.addObjectSetterAlias( "multipartRelated", "http://schemas.xmlsoap.org/wsdl/mime/", "extensibilityElement");

  } // end setSchemaProperties()

  /**
   *
   * @param openEvent
   */
  public void xmlTagOpen( XmlOpenElementEvent openEvent )
  {
    String strTagName = openEvent.getLocalName();

    if ( strTagName.equals( "operation" ) )
      resolveOperationType( openEvent );
    else
    if ( strTagName.equals( "input" ) || strTagName.equals( "output" )   || strTagName.equals( "fault" ) )
      resolveOperationIOType( openEvent, strTagName );

  } // end xmlTagOpen()

  
  public void unknownElement( Object objElementOwner, String strQNameParent, Element eleUnknown )
  {
    if ( objElementOwner instanceof ExtensibilityElementSupport)
      ((ExtensibilityElementSupport)objElementOwner).addUnknownExtensibilityElement( new VwUnknownExtensibilityElementImpl( eleUnknown ));
  }
  
  private void resolveOperationIOType( XmlOpenElementEvent openEvent, String strTagName  )
  {
    // walk up the stack parentage to determine the kind of operation this is
    Stack stackParentage = openEvent.getTagParentage();

    stackParentage.pop(); // Not interested in current tag name

    int nNbrItems = stackParentage.size();

    for ( int x = 0; x < nNbrItems; x++ )
    {
      String strTag = (String)stackParentage.pop();

      if ( strTag.indexOf( "binding") >= 0 )
      {
        if ( strTagName.equals( "input" ) )
          openEvent.setTagHandlerClass( VwBindingInputImpl.class  );
        else
        if ( strTagName.equals( "output" ) )
          openEvent.setTagHandlerClass( VwBindingOutputImpl.class  );
        else
        if ( strTagName.equals( "fault" ) )
          openEvent.setTagHandlerClass( VwBindingFaultImpl.class  );

        return;
      }

      if ( strTag.indexOf( "portType") >= 0 )
      {
        if ( strTagName.equals( "input" ) )
          openEvent.setTagHandlerClass( VwInputImpl.class  );
        else
        if ( strTagName.equals( "output" ) )
          openEvent.setTagHandlerClass( VwOutputImpl.class  );
        else
        if ( strTagName.equals( "fault" ) )
          openEvent.setTagHandlerClass( VwFaultImpl.class  );

        return;
      }

    }
  }


  /**
   * Resolve class handler for the overloaded operation tag based on parentage
   * @param openEvent The tagEvent from the xml reader
   */
  private void resolveOperationType( XmlOpenElementEvent openEvent )
  {
    // walk up the stack parentage to determine the kind of operation this is
    Stack stackParentage = openEvent.getTagParentage();

    stackParentage.pop(); // Not interested in current tag name

    int nNbrItems = stackParentage.size();

    for ( int x = 0; x < nNbrItems; x++ )
    {
      String strTag = (String)stackParentage.pop();

      if ( strTag.indexOf( "binding") >= 0 )
      {
        if ( openEvent.getLocalName().equals( "operation"))
          openEvent.setTagAlias( "bindingOperation" );

        openEvent.setTagHandlerClass( VwBindingOperationImpl.class );
        return;
      }

      if ( strTag.indexOf( "portType") >= 0 )
      {
        openEvent.setTagHandlerClass( VwOperationImpl.class );
        return;
      }

    }

  } // end resolveOperationType(

  /**
   * Handle tag close to set Documentation content
   * @param closeEvent
   */
  public void xmlTagClosed( XmlCloseElementEvent closeEvent )
  {

    String strName = closeEvent.getLocalName();

    if ( strName.equals( "operation") )
    {
      Stack stackParentage = closeEvent.getTagParentage();

      int nNbrItems = stackParentage.size();

      for ( int x = 0; x < nNbrItems; x++ )
      {
        String strTag = (String)stackParentage.pop();

        if ( strTag.indexOf( "binding") >= 0 )
        {
            closeEvent.setTagAlias( "bindingOperation" );

            return;
        }
      } // end for()

      return;

    } // end if

    Object objBean = closeEvent.getTagHandler();
    String strData = closeEvent.getTagData();

    ((Documentation)objBean).setContent( strData );


  } // end
  
  
  public static void main( String[] args )
  {
    try
    {
      VwWSDLReaderImpl rdr = new VwWSDLReaderImpl();
      File fileSchema = new File( "\\generatedWsdl\\GetEmp.wsdl" );
      Definition def = rdr.readWsdl( fileSchema.toURL() );

      return;
    }
    catch( Exception ex )
    {
      ex.printStackTrace();
    }
  }
} // end class VwWSDLReaderImpl{}

//*** End of VwWSDLReaderImpl.java ***