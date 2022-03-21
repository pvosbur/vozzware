/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: BindingOperation.java

============================================================================================
*/
package javax.wsdl;

import javax.wsdl.extensions.ExtensibilityElementSupport;
import java.util.List;


/**
 * This interface represents a WSDL operation binding.
 *
 * @author Peter VosBurgh
 */
public interface BindingOperation extends WSDLCommon, ExtensibilityElementSupport
{

  
  /**
   * Sets the input binding for this binding operation.
   *
   * @param bindingInput the binding input
   */
  public void setInput( BindingInput bindingInput );

  /**
   * Gets the input binding for this binding operation
   *
   * @return the input binding
   */
  public BindingInput getInput();

  /**
   * Sets the output binding for this binding operation.
   *
   * @param bindingOutput the new output binding
   */
  public void setOutput( BindingOutput bindingOutput );

  /**
   * Get the output binding for this operation binding.
   *
   * @return the output binding for the operation binding
   */
  public BindingOutput getOutput();

	/**
	 * Adds  a fault binding.
   *
	 * @param bindingFault the binding fault to add
	 */
  public void addFault( BindingFault bindingFault );

  /**
   * Removes the specified binding fault
   *
   * @param bindingFault The binding fault to remove
   */
  public void removeFault( BindingFault bindingFault );

  /**
   * Removes the specied binding fault by its name
   *
   * @param strName The name of the binding fault to remove
   */
  public void removeFault( String strName );

  /**
   * Removes all Binding faults
   */
  public void removeAllFaults();

  /**
   * Get the specified fault binding by it's name
   *
   * @param name the name of the binding fault to retrieve.
   *
   * @return The binding fault or null if the name does not exist
   */
  public BindingFault getFault( String name );

	/**
	 * Gets a List all binding faults for this binding operation
   *
	 * @return a List all binding faults for this binding operatio
	 */
  public List getFaults();

} // end interface BindingOperation{}

// *** End of BindingOperation.java ***
