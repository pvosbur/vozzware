/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: Binding.java

============================================================================================
*/
package javax.wsdl;

import javax.wsdl.extensions.ExtensibilityElementSupport;
import java.util.List;

/**
 * This represents the WSDL binding element
 *
 * @author Peter VosBurgh
 */
public interface Binding extends WSDLCommon, ExtensibilityElementSupport
{

  /**
   * Set the port type this is a binding for.
   *
   * @param strType the port type associated with this binding
   */
  public void setType( String strType );

  /**
   * Get the port type this is a binding for.
   *
   * @return the associated port type
   */
  public String getType();

  /**
   * Add an operation binding to binding.
   *
   * @param bindingOperation the operation binding to be added
   */
  public void addBindingOperation( BindingOperation bindingOperation );


  /**
   * Remove an operation binding from the binding.
   *
   * @param bindingOperation the operation binding to be removed
   */
  public void removeBindingOperation( BindingOperation bindingOperation );

  /**
   * Removes all bound operations from the binding
   *
   */
  public void removeAllBindingOperations();

  /**
   * Get the specified operation binding. Note that operation names can
   * be overloaded within a PortType. In case of overloading, the
   * names of the input and output messages can be used to further
   * refine the search.
   *
   * @param name the name of the desired operation binding.
   * @param inputName the name of the input message; if this is null
   * it will be ignored.
   * @param outputName the name of the output message; if this is null
   * it will be ignored.
   * @return the corresponding operation binding, or null if there wasn't
   * any matching operation binding
   */
  public BindingOperation getBindingOperation(String name,
											                        String inputName,
											                        String outputName);

  /**
   * Get all the operation bindings defined here.
   */
  public List getBindingOperations();


} // end interface Binding{}

// *** End Binding.java ***
