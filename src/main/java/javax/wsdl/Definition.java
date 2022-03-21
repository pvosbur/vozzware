/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: Definition.java

============================================================================================
*/
package javax.wsdl;

import org.w3c.dom.Element;

import javax.wsdl.extensions.ExtensibilityElementSupport;
import javax.wsdl.extensions.UnknownExtensibilityElement;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.wsdl.extensions.soap.SOAPBinding;
import javax.wsdl.extensions.soap.SOAPBody;
import javax.wsdl.extensions.soap.SOAPOperation;
import java.util.List;

/**
 * This interface represents a WSDL definition.
 *
 * @author Peter VosBurgh
 */
public interface Definition extends WSDLCommon, ExtensibilityElementSupport
{
  /**
   * Sets the document base URI of this definition. Can be used to
   * represent the origin of the Definition.
   *
   * @param strDocumentBaseURI the definition's URI of this definition
   */
  public void setDocumentBaseURI( String strDocumentBaseURI );

  /**
   * Get the document base URI of this definition.
   *
   * @return the document base URI
   */
  public String getDocumentBaseURI();


  /**
   * Set the target namespace for this definition
   *
   * @param strTargetNamespace the target namespace
   */
  public void setTargetNamespace( String strTargetNamespace );

  /**
   * Get the target namespace for this definition
   *
   * @return the target namespace
   */
  public String getTargetNamespace();

  /**
   * Gets the defalut namespace (URI) if one exists
   * @return the default namespace URI if it exists else null is returned
   */
  public String  getDefaultNamespace();
  
  
  /**
   * Set the types section.
   */
  public void setTypes(Types types);

  /**
   * Get the types section.
   *
   * @return the types section
   */
  public Types getTypes();

  /**
   * Adds an import to this WSDL description.
   *
   * @param wsdlImport the import to be added
   */
  public void addImport( Import wsdlImport );


  /**
   * Get the list of imports for the specified namespaceURI.
   *
   * @param strNamespaceURI the namespaceURI associated with the
   * desired imports.
   *
   * @return a list of the corresponding imports, or null if
   * there weren't any matching imports
   */
  public List getImports( String strNamespaceURI );

  /**
   * Gets a List of all imports
   *
   * @return a List of all imports
   */
  public List getImports();


  /**
   * Removes the specified import
   * @param wsdlImport the import to remove
   */
  public void removeImport( Import wsdlImport );

  /**
   * Removes all imports for a giben namespace URI
   * @param strNamepsaceURI The namespace URI of the imports to remove
   */
  public void removeImports( String strNamepsaceURI );


  /**
   * Removes all imports from this definition
   */
  public void removeAllImports();

  /**
   * Add a message to this WSDL description.
   *
   * @param message the message to be added
   */
  public void addMessage( Message message );

  /**
   * Get the specified message. Also checks imported documents.
   *
   * @param strQName the name of the desired message in prefix:localprt format
   *
   * @return the corresponding message. May be null if no message was defined.
   */
  public Message getMessage( String strQName );

  /**
   * Remove the specified message from this definition.
   *
   * @param strQName the name of the message to remove in prefix:localprt format
   */
  public void removeMessage( String strQName );


  /**
   * Remove the specified Message instance
   *
   * @param msg The Messgae instance to remove
   */
  public void removeMessage( Message msg );


  /**
   * Removes all messages from this definition
   */
  public void removeAllMessages();

  /**
   * Gets a List all the defined messages.
   */
  public List getMessages();


  /**
   * Adds a portType to this WSDL definition
   *
   * @param portType the portType to be added
   */
  public void addPortType( PortType portType );

  /**
   * Get the specified portType. Also checks imported documents.
   *
   * @param strQName the Name of the portType to retrieve in prefix:localpart format.
   *
   * @return the portType, or null if no portTypes exists for this name.
   */
  public PortType getPortType( String strQName );

  /**
   * Remove the specified portType from this definition.
   *
   * @param strQName the name of the portType to remove in prefix:localpart format.
   */
  public void removePortType( String strQName );


  /**
   * Removes the specified PortType instance
   * @param portType The PortType instance to remove
   */
  public void removePortType( PortType portType );
  

  /**
   * Removes all porttYpes from this WSDL definition
   */
  public void removeAllortTypes();


  /**
   * Gets a List of  the deifined portTypes.
   *
   * @return a List of  the deifined portTypes.
   */
  public List getPortTypes();

  /**
   * Adds a binding to this definition
   *
   * @param binding the binding to add
   */
  public void addBinding( Binding binding );


  /**
   * Get the specified binding. Also checks imported documents.
   *
   * @param strQname the name of the desired binding in prefix:localpart format.
   *
   * @return the corresponding binding, or null if there wasn't
   * any matching binding
   */
  public Binding getBinding( String strQname );

  /**
   * Remove the specified binding from this definition.
   *
   * @param strQname the name of the binding to remove in prefix:localpart format.
   *
   */
  public void removeBinding( String strQname );

  /**
   * Removes the specified binding instance
   * @param binding the binding instance to remove
   */
  public void removeBinding( Binding binding );

  /**
   * Gets a List of the defined bindings.
   */
  public List getBindings();


  /**
   * Removes all bindings from this definition
   */
  public void removeAllBindings();


  /**
   * Add a service to this WSDL description.
   *
   * @param service the service to be added
   */
  public void addService( Service service );

  /**
   * Get the specified service. Also checks imported documents.
   *
   * @param strQName the QName of the service to retrieve in prefix:localpart format.
   * @return the service, or null if there wasn't one defined for this name.
   */
  public Service getService( String strQName );

  /**
   * Removes the specified service from this WSDL definition.
   *
   * @param strQName the QName of the service to remove in prefix:localpart format.
   */
  public void removeService( String strQName );


  /**
   * Removes the specidied Service instance
   *
   * @param service The Service instance to remove
   */
  public void removeService( Service service );

  /**
   * Gets a List of the defined httpServices.
   *
   * @return a List of the defined httpServices.
   */
  public List getServices();


  /**
   * Creates a new binding.
   *
   * @return the newly created binding
   */
  public Binding createBinding();

  /**
   * Creates a new binding fault.
   *
   * @return the newly created binding fault
   */
  public BindingFault createBindingFault();

  /**
   * Creates a new binding input.
   *
   * @return the newly created binding input
   */
  public BindingInput createBindingInput();

  /**
   * Create a new binding operation.
   *
   * @return the newly created binding operation
   */
  public BindingOperation createBindingOperation();

  /**
   * Creates a new binding output.
   *
   * @return the newly created binding output
   */
  public BindingOutput createBindingOutput();

  /**
   * Creates a new fault.
   *
   * @return the newly created fault
   */
  public Fault createFault();

  /**
   * Creates a new import.
   *
   * @return the newly created import
   */
  public Import createImport();

  /**
   * Creates a new input.
   *
   * @return the newly created input
   */
  public Input createInput();

  /**
   * Creates a new message.
   *
   * @return the newly created message
   */
  public Message createMessage();

  /**
   * Creates a new operation.
   *
   * @return the newly created operation
   */
  public Operation createOperation();

  /**
   * Creates a new output.
   *
   * @return the newly created output
   */
  public Output createOutput();

  /**
   * Creates a new part.
   *
   * @return the newly created part
   */
  public Part createPart();

  /**
   * Creates a new port.
   *
   * @return the newly created port
   */
  public Port createPort();

  /**
   * Creates a new port type.
   *
   * @return the newly created port type
   */
  public PortType createPortType();

  /**
   * Creates a new service.
   *
   * @return the newly created service
   */
  public Service createService();

  /**
   * Create a SOAPAddress extensibility element
   * @return the newly created SOAPAddress
   */
  public SOAPAddress createSOAPAddress();
  
  /**
   * Create SOAPBody extensibility element
   * @return the newly created SOAPBody
   */
  public SOAPBody createSOAPBody();
  
  
  /**
   * Create a SOAPBinding extensibility element
   * @return the newly created SOAPBinding
   */
  public SOAPBinding createSOAPBinding();
  
  /**
   * Create a SOAPOperation extensibility element
   * @return the newly created SOAPOperation
   */
  public SOAPOperation createSOAPOperation();
  
  /**
   * Creates a new types section.
   *
   * @return the newly created types section
   */
  public Types createTypes();
  
  /**
   * Creates a new UnknownExtensibilityElement
   * 
   * @param element The DOM Eelement holding the tag data
   * 
   * @return the newely created UnknownExtensibilityElement
   */
  public UnknownExtensibilityElement createUnknownExtensibilityElement( Element element );
  

} // end interface Defition{}

// *** End of Definition.java ***
