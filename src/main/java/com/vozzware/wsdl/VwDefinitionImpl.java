/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwDefinitionImpl.java

Create Date: Apr 11, 2006
============================================================================================
*/
package com.vozzware.wsdl;

import com.vozzware.wsdl.extensions.VwExtensibilityElementSupportImpl;
import com.vozzware.wsdl.extensions.VwUnknownExtensibilityElementImpl;
import com.vozzware.wsdl.extensions.soap.VwSOAPAddressImpl;
import com.vozzware.wsdl.extensions.soap.VwSOAPBindingImpl;
import com.vozzware.wsdl.extensions.soap.VwSOAPBodyImpl;
import com.vozzware.wsdl.extensions.soap.VwSOAPOperationImpl;
import com.vozzware.wsdl.util.VwWSDLWriterImpl;
import com.vozzware.xml.namespace.Namespace;
import com.vozzware.xml.namespace.QName;
import org.w3c.dom.Element;

import javax.wsdl.Binding;
import javax.wsdl.BindingFault;
import javax.wsdl.BindingInput;
import javax.wsdl.BindingOperation;
import javax.wsdl.BindingOutput;
import javax.wsdl.Definition;
import javax.wsdl.Fault;
import javax.wsdl.Import;
import javax.wsdl.Input;
import javax.wsdl.Message;
import javax.wsdl.Operation;
import javax.wsdl.Output;
import javax.wsdl.Part;
import javax.wsdl.Port;
import javax.wsdl.PortType;
import javax.wsdl.Service;
import javax.wsdl.Types;
import javax.wsdl.WSDLCommon;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.UnknownExtensibilityElement;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.wsdl.extensions.soap.SOAPBinding;
import javax.wsdl.extensions.soap.SOAPBody;
import javax.wsdl.extensions.soap.SOAPOperation;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * This interface represents a WSDL definition.
 *
 * @author Peter VosBurgh
 */
public class VwDefinitionImpl extends VwWSDLCommonImpl implements Definition
{
  private String    m_strDocumentBaseURI;
  private String    m_strTargetNamespace;

  private List      m_listContent = new ArrayList();

  private List      m_listMessages = new ArrayList();
  private List      m_listPortTypes = new ArrayList();
  private List      m_listBindings = new ArrayList();
  private List      m_listServices = new ArrayList();
  private List      m_listImports = new ArrayList();

  private Types     m_types;

  private Namespace  m_nsWsdl;   // Namespace for this wsdl document if defined

  private VwExtensibilityElementSupportImpl m_extSupport = new VwExtensibilityElementSupportImpl();

  public final static String XML_WSDL1_1_URI = "http://schemas.xmlsoap.org/wsdl/";
  
  /**
   * Override to store namespace and prefix associated with this document
   */
  public void addNamespace( Namespace ns )
  {
    super.addNamespace( ns );
    
    if ( ns.getURI().equals( XML_WSDL1_1_URI ) )
      m_nsWsdl = ns;
    
  } // end addNamespace()
  
  
  /**
   * Removes namespace for prefix and sets the schema namespace to null if it equals the scheam uri
   */
  public void removeNamespace( String strPrefix )
  {
    if ( m_nsWsdl != null && m_nsWsdl.getPrefix().equals( strPrefix ))
    {
      m_nsWsdl = null;
      setQName( null );
      
    }
    
    super.removeNamespace( strPrefix );
    
  } // end removeNamespace()
  
  public void removeAllNamespaces()
  {
    m_nsWsdl = null;
    super.removeAllNamespaces();
    
  } // end removeAllNamespaces()
  
  /**
   * Gets a List of the complete content for this definition
   * @return a List of the complete content for this definition
   */
  public List getContent()
  {
    List listContent = new ArrayList();

    if ( this.getDocumentation() != null )
      listContent.add( this.getDocumentation() );

    listContent.addAll( m_listContent );

    return listContent;

  } // end getContent()

  /**
   * Sets the document base URI of this definition. Can be used to
   * represent the origin of the Definition.
   *
   * @param strDocumentBaseURI the definition's URI of this definition
   */
  public void setDocumentBaseURI( String strDocumentBaseURI )
  { m_strDocumentBaseURI = strDocumentBaseURI; }

  /**
   * Get the document base URI of this definition.
   *
   * @return the document base URI
   */
  public String getDocumentBaseURI()
  { return m_strDocumentBaseURI; }


  /**
   * Set the target namespace for this definition
   *
   * @param strTargetNamespace the target namespace
   */
  public void setTargetNamespace( String strTargetNamespace )
  { m_strTargetNamespace = strTargetNamespace; }

  /**
   * Get the target namespace for this definition
   *
   * @return the target namespace
   */
  public String getTargetNamespace()
  { return m_strTargetNamespace; }

  /**
   * Gets the defalut namespace (URI) if one exists
   * @return the default namespace URI if it exists else null is returned
   */
  public String getDefaultNamespace()
  {
    List listNamespaces = getNamespaces();
    
    for (Iterator iter = listNamespaces.iterator(); iter.hasNext();)
    {
      Namespace ns = (Namespace)iter.next();
      if ( ns.getPrefix().length() == 0 )
         return ns.getURI();
     
    } // end for()
    
    return null;
    
  } // end getDefaultNamespace()
  

  /**
   * Set the types section.
   */
  public void setTypes( Types types )
  {
    m_types = types;
    m_listContent.add( types );
  }


  /**
   * Get the types section.
   *
   * @return the types section
   */
  public Types getTypes()
  { return m_types; }

  /**
   * Adds an import to this WSDL description.
   *
   * @param wsdlImport the import to be added
   */
  public void addImport( Import wsdlImport )
  {
    m_listContent.add( wsdlImport );
    m_listImports.add(  wsdlImport );

  } // end addImport()


  /**
   * Get the list of imports for the specified namespaceURI.
   *
   * @param strNamespaceURI the namespaceURI associated with the
   * desired imports.
   *
   * @return a list of the corresponding imports, or null if
   * there weren't any matching imports
   */
  public List getImports( String strNamespaceURI )
  {
    List listImports = new ArrayList();

    for ( Iterator iImports = m_listImports.iterator(); iImports.hasNext(); )
    {
      Import imp = (Import)iImports.next();

      if ( imp.getNamespace() != null && imp.getNamespace().equalsIgnoreCase( strNamespaceURI) )
         listImports.add( imp );
    } // end for()

    return listImports;

  } // end getImports()

  /**
   * Gets a List of all imports
   * 
   * @return a List of all imports
   */ 
  public List getImports()
  { return m_listImports; }


  /**
   * Removes the specified import
   * @param wsdlImport the import to remove
   */ 
  public void removeImport( Import wsdlImport )
  {
    m_listContent.remove( wsdlImport );
    m_listImports.remove( wsdlImport );

  } // end removeImport()

  /**
   * Removes all imports for a giben namespace URI
   * @param strNamespaceURI The namespace URI of the imports to remove
   */ 
  public void removeImports( String strNamespaceURI )
  {
    for ( Iterator iImports = m_listImports.iterator(); iImports.hasNext(); )
    {
      Import imp = (Import)iImports.next();

      if ( imp.getNamespace() != null && imp.getNamespace().equalsIgnoreCase( strNamespaceURI ) )
      {
         iImports.remove();
         m_listContent.remove( imp );
      }
    } // end for()

  } // end removeImports()


  /**
   * Removes all imports from this definition
   */ 
  public void removeAllImports()
  {
    m_listContent.removeAll( m_listImports );
    m_listImports.clear();

  }

  /**
   * Add a message to this WSDL description.
   *
   * @param message the message to be added
   */
  public void addMessage( Message message )
  {
    m_listContent.add( message );
    m_listMessages.add( message );

  } // end addMessage

  /**
   * Get the specified message. Also checks imported documents.
   *
   * @param strQName the name of the desired message. in prefix:localpart format
   *
   * @return the corresponding message. May be null if no message was defined.
   */
  public Message getMessage( String strQName )
  {
    for ( Iterator iMsgs = m_listMessages.iterator(); iMsgs.hasNext(); )
    {
      Message msg = (Message)iMsgs.next();

      if ( msg.getName() != null && msg.getName().equalsIgnoreCase( strQName ) )
        return msg;
    } // end for()

    return null;    // nor found

  }

  /**
   * Remove the specified message from this definition.
   *
   * @param strQName the name of the message to remove in prefix:localpart format
   *
   */
  public void removeMessage( String strQName )
  {
    for ( Iterator iMsgs = m_listMessages.iterator(); iMsgs.hasNext(); )
    {
      Message msg = (Message)iMsgs.next();

      if ( msg.getName() != null && msg.getName().equalsIgnoreCase( strQName ) )
      {
        m_listContent.remove( msg );
        iMsgs.remove();
        return;

      }
    } // end for()

  } // end removeMessage()


  /**
   * Remove the specified Message instance
   *
   * @param msg The Messgae instance to remove
   */
  public void removeMessage( Message msg )
  {
    m_listContent.remove( msg );
    m_listMessages.remove( msg );

  } // end removeMessage()


  /**
   * Removes all messages from this definition
   */
  public void removeAllMessages()
  {
    m_listContent.removeAll( m_listMessages );
    m_listMessages.clear();
  } // end removeAllMessages()

  /**
   * Gets a List all the defined messages.
   */
  public List getMessages()
  { return m_listMessages; }


  /**
   * Adds a portType to this WSDL definition
   *
   * @param portType the portType to be added
   */
  public void addPortType( PortType portType )
  {
    m_listContent.add( portType );
    m_listPortTypes.add( portType );
  }

  /**
   * Get the specified portType. Also checks imported documents.
   *
   * @param strQName the QName of the portType to retrieve in prefix:localpart format.
   *
   * @return the portType, or null if no portTypes exists for this name.
   */
  public PortType getPortType( String strQName )
  {
    for ( Iterator iPortTypes = m_listPortTypes.iterator(); iPortTypes.hasNext(); )
    {
      PortType portType = (PortType)iPortTypes.next();

      if ( portType.getName() != null && portType.getName().equalsIgnoreCase( strQName ) )
        return portType;

    } // end for()

    return null;    // No Match

  } // end getPortType()

  /**
   * Remove the specified portType from this definition.
   *
   * @param strQName the name of the portType to remove in prefix:localpart format.
   */
  public void removePortType( String strQName )
  {
    for ( Iterator iPortTypes = m_listPortTypes.iterator(); iPortTypes.hasNext(); )
    {
      PortType portType = (PortType)iPortTypes.next();

      if ( portType.getName() != null && portType.getName().equalsIgnoreCase( strQName ) )
      {
        iPortTypes.remove();
        m_listContent.remove( portType );
        return;
      }
    } // end for()

  } // end removePortType()

  /**
   * Removes the specified PortType instance
   * @param portType The PortType instance to remove
   */
  public void removePortType( PortType portType )
  {
    m_listContent.remove( portType );
    m_listPortTypes.remove( portType );

  } // ends removePortType()

  /**
   * Removes all porttYpes from this WSDL definition
   */
  public void removeAllortTypes()
  { m_listPortTypes.clear(); }


  /**
   * Gets a List of  the deifined portTypes.
   *
   * @return a List of  the deifined portTypes.
   */
  public List getPortTypes()
  { return m_listPortTypes; }


  /**
   * Adds a binding to this definition
   *
   * @param binding the binding to add
   */
  public void addBinding( Binding binding )
  {
    m_listContent.add( binding );
    m_listBindings.add( binding );

  } // end addBinding()


  /**
   * Get the specified binding. Also checks imported documents.
   *
   * @param strQName the name of the desired binding in prefix:locpart format.
   * @return the corresponding binding, or null if there wasn't
   * any matching binding
   */
  public Binding getBinding( String strQName )
  {
    for ( Iterator iBindings = m_listBindings.iterator(); iBindings.hasNext(); )
    {
      Binding binding = (Binding)iBindings.next();

      if ( binding.getName() != null && binding.getName().equalsIgnoreCase( strQName ) )
        return binding;

    } // end for()

    return null;    // No match

  } // end getBinding()

  /**
   * Remove the specified binding from this definition.
   *
   * @param strQName the nAame of the binding to remove
   *
   */
  public void removeBinding( String strQName )
  {
    for ( Iterator iBindings = m_listBindings.iterator(); iBindings.hasNext(); )
    {
      Binding binding = (Binding)iBindings.next();

      if ( binding.getName() != null && binding.getName().equalsIgnoreCase( strQName ) )
      {
        iBindings.remove();
        m_listContent.remove( binding );
        return;
      }
    } // end for()

  } // end removeBinding()


  /**
   * Removes the specified binding instance
   * @param binding the binding instance to remove
   */
  public void removeBinding( Binding binding )
  {
    m_listContent.remove( binding );
    m_listBindings.remove( binding );

  } // end removeBinding()

  /**
   * Gets a List of the defined bindings.
   */
  public List getBindings()
  { return m_listBindings; }


  /**
   * Removes all bindings from this definition
   */
  public void removeAllBindings()
  { m_listBindings.clear(); }


  /**
   * Add a service to this WSDL description.
   *
   * @param service the service to be added
   */
  public void addService( Service service )
  {
    m_listContent.add( service );
    m_listServices.add( service );

  }


  /**
   * Get the specified service. Also checks imported documents.
   *
   * @param strQName the name of the service to retrieve in prefix:locapart format.
   *
   * @return the service, or null if there wasn't one defined for this name.
   */
  public Service getService( String strQName )
  {
    for ( Iterator iServices = m_listServices.iterator(); iServices.hasNext(); )
    {
      Service service = (Service)iServices.next();

      if ( service.getName() != null && service.getName().equalsIgnoreCase( strQName ) )
        return service;

    } // end for()

    return null;      // No Match
  }
  /**
   * Removes the specified service from this WSDL definition.
   *
   * @param strQName the name of the service to remove in prefix:locapart format.
   */
  public void removeService( String strQName )
  {
    for ( Iterator iServices = m_listServices.iterator(); iServices.hasNext(); )
    {
      Service service = (Service)iServices.next();

      if ( service.getName() != null && service.getName().equalsIgnoreCase( strQName ) )
      {
        iServices.remove();
        m_listContent.remove( service );
        return;
      }
    } // end for()

  } // end removeService()


  /**
   * Removes the specified service instance
   *
   * @param service The service instance to remove
   */
  public void removeService( Service service )
  {
    m_listContent.remove( service );
    m_listServices.remove( service );

  }
  /**
   * Removes all httpServices defined
   */
  public void removeAllServices()
  {
    m_listContent.removeAll( m_listServices );
    m_listServices.clear();

  } // end removeAllServices()

  /**
   * Gets a List of the defined httpServices.
   *
   * @return a List of the defined httpServices.
   */
  public List getServices()
  { return m_listServices; }


  /**
   * Creates a new binding and adds it to the Binding list.
   *
   * @return the newly created binding
   */
  public Binding createBinding()
  {
    Binding binding = new VwBindingImpl();
    return binding;

  } // end createBinding()

  /**
   * Creates a new binding fault.
   *
   * @return the newly created binding fault
   */
  public BindingFault createBindingFault()
  { return new VwBindingFaultImpl(); }

  /**
   * Creates a new binding input.
   *
   * @return the newly created binding input
   */
  public BindingInput createBindingInput()
  { return new VwBindingInputImpl(); }

  /**
   * Create a new binding operation.
   *
   * @return the newly created binding operation
   */
  public BindingOperation createBindingOperation()
  { return new VwBindingOperationImpl(); }

  /**
   * Creates a new binding output.
   *
   * @return the newly created binding output
   */
  public BindingOutput createBindingOutput()
  { return new VwBindingOutputImpl(); }

  /**
   * Creates a new fault.
   *
   * @return the newly created fault
   */
  public Fault createFault()
  { return new VwFaultImpl(); }

  /**
   * Creates a new import and adds it to the content list
   *
   * @return the newly created import
   */
  public Import createImport()
  {
    Import imp = new VwImportImpl();
    return imp;

  } // end createImport

  /**
   * Creates a new input.
   *
   * @return the newly created input
   */
  public Input createInput()
  { return new VwInputImpl(); }

  /**
   * Creates a new message and adds it to the Definition's message List.
   *
   * @return the newly created message
   */
  public Message createMessage()
  {
    Message msg = new VwMessageImpl();
    return msg;

  } // end createMessage()

  /**
   * Creates a new operation.
   *
   * @return the newly created operation
   */
  public Operation createOperation()
  { return new VwOperationImpl(); }

  /**
   * Creates a new output.
   *
   * @return the newly created output
   */
  public Output createOutput()
  { return new VwOutputImpl(); }

  /**
   * Creates a new part.
   *
   * @return the newly created part
   */
  public Part createPart()
  { return new VwPartImpl(); }

  /**
   * Creates a new port.
   *
   * @return the newly created port
   */
  public Port createPort()
  { return new VwPortImpl(); }

  /**
   * Creates a new port type.
   *
   * @return the newly created port type
   */
  public PortType createPortType()
  {
    PortType portType = new VwPortTypeImpl();
    return portType;

  } // end createPortType()

  /**
   * Creates a new service.
   *
   * @return the newly created service
   */
  public Service createService()
  {
    Service service = new VwServiceImpl();
    return service;

  } // end createService()

  /**
   * Create a SOAPAddress extensibility element
   * @return the newly created SOAPAddress
   */
  public SOAPAddress createSOAPAddress()
  { return new VwSOAPAddressImpl(); }
  
 
  /**
   * Create a SOAPBinding extensibility element
   * @return the newly created SOAPBinding
   */
  public SOAPBinding createSOAPBinding()
  { return new VwSOAPBindingImpl(); }
  
  
  /**
   * Create SOAPBody extensibility element
   * @return the newly created SOAPBody
   */
  public SOAPBody createSOAPBody()
  {
    SOAPBody soapBody = new VwSOAPBodyImpl();
    return soapBody;
    
  }
  
  
  /**
   * Create a SOAPOperation extensibility element
   * @return the newly created SOAPOperation
   */
  public SOAPOperation createSOAPOperation()
  { return new VwSOAPOperationImpl(); }
  
  
  /**
   * Creates a new types section.
   *
   * @return the newly created types section
   */
  public Types createTypes()
  {

    Types types = new VwTypesImpl();
    return types;

  } // end createTypes()


  /**
   * Checks to see if a namespace was defined for the schema document, and if so sets the QName ofr each of the schema
   * elements when they are created
   * 
   * @param The elements superclass that takes a QName
   * @param strLocalPart the local part name of the element
   * @return
   */
  private Object checkForSchemaNamespace( WSDLCommon wsdlObj, String strLocalPart )
  {
    if ( m_nsWsdl != null )
    {
      wsdlObj.setQName( new QName( m_nsWsdl, strLocalPart ) );
    }
    
    return wsdlObj;
    
  } // end checkForSchemaNamespace()
  
  
  /**
   * Creates a new UnknownExtensibilityElement
   * 
   * @param element The DOM Eelement holding the tag data
   * 
   * @return the newely created UnknownExtensibilityElement
   */
  public UnknownExtensibilityElement createUnknownExtensibilityElement( Element element )
  { return new VwUnknownExtensibilityElementImpl( element ); }
  
  /**
   * Adds an extensibility element for specific service extensions
   *
   * @param extElement extensibility element for specific service extensions
   */
  public void addExtensibilityElement( ExtensibilityElement extElement )
  { m_listContent.add( extElement ); }

  /**
   * Removes the specified extensibility element
   *
   * @param extElement the extensibility element to remove
   */
  public void removeExtensibilityElement( ExtensibilityElement extElement )
  { m_listContent.remove( extElement ); }

  /**
   * Removes all extensibility elements
   */
  public void removeAllExtensibilityElements()
  { m_extSupport.removeAllExtensibilityElements( m_listContent ); }


  /**
   * Gets all the extensibility elements defined.
   *
   * @return a List of all extensibility elements defined for this service
   */
  public List getExtensibilityElements()
  { return m_extSupport.getExtensibilityElements( m_listContent ); }


  /**
   * Adds an unknown extensibility element for specific service extensions
   *
   * @param unknownExtElementt the unknown extensibility element for specific service extensions
   */
  public void addUnknownExtensibilityElement( UnknownExtensibilityElement unknownExtElement )
  {  m_listContent.add( unknownExtElement ); }

  /**
   * Removes the specified unknown extensibility element
   *
   * @param unknownExtElement the unknown extensibility element to remove
   */
  public void removeUnknownExtensibilityElement( UnknownExtensibilityElement unknownExtElement )
  { m_listContent.remove( unknownExtElement ); }

  /**
   * Removes all unknown extensibility elements
   */
  public void removeAllUnknownExtensibilityElements()
  { m_extSupport.removeAllUnknownExtensibilityElements( m_listContent ); }


  /**
   * Gets all the unknown extensibility elements defined.
   *
   * @return a List of all unknown extensibility elements defined for this service
   */
  public List getUnknownExtensibilityElements()
  { return m_extSupport.getUnknownExtensibilityElements( m_listContent ); }

  /**
   * Gets the List of all ExtensibilityElements and UnknownExtensibilityElements
   * 
   * @return  the List of all ExtensibilityElements and UnknownExtensibilityElements
   */
  public List getAllElements()
  { return m_extSupport.getAllElements( m_listContent ); }
  
  
  /**
   * Removes all ExtensibilityElements and UnknownExtensibilityElements
   *
   */
  public void removeAllElements()
  { m_extSupport.removeAllElements( m_listContent ); }
  
  public String toString()
  {
    VwWSDLWriterImpl writer = new VwWSDLWriterImpl();
    try
    {
      return writer.writeWsdl( this );
    }
    catch( Exception ex )
    {
      ex.printStackTrace();
    }
    
    return "";
    
  }
} // end class VwDefitionImpl{}

// *** End of VwDefinitionImpl.java ***
