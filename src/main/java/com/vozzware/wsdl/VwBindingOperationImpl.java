/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwBindingOperationImpl.java

Create Date: Apr 11, 2006
============================================================================================
*/
package com.vozzware.wsdl;

import com.vozzware.wsdl.extensions.VwExtensibilityElementSupportImpl;

import javax.wsdl.BindingFault;
import javax.wsdl.BindingInput;
import javax.wsdl.BindingOperation;
import javax.wsdl.BindingOutput;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.UnknownExtensibilityElement;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


/**
 * This interface represents a WSDL operation binding.
 *
 * @author Peter VosBurgh
 */
public class VwBindingOperationImpl extends VwWSDLCommonImpl implements BindingOperation
{
  private VwExtensibilityElementSupportImpl m_extSupport = new VwExtensibilityElementSupportImpl();

  private List            m_listFaults = new LinkedList();
  private BindingInput    m_bindingInput;
  private BindingOutput   m_bindingOutput;

  /**
   * Sets the input binding for this binding operation.
   *
   * @param bindingInput the binding input
   */
  public void setInput( BindingInput bindingInput )
  { m_bindingInput = bindingInput; }

  /**
   * Gets the input binding for this binding operation
   *
   * @return the input binding
   */
  public BindingInput getInput()
  { return m_bindingInput; }

  /**
   * Sets the output binding for this binding operation.
   *
   * @param bindingOutput the new output binding
   */
  public void setOutput( BindingOutput bindingOutput )
  { m_bindingOutput = bindingOutput; }

  /**
   * Get the output binding for this operation binding.
   *
   * @return the output binding for the operation binding
   */
  public BindingOutput getOutput()
  { return m_bindingOutput; }

	/**
	 * Adds  a fault binding.
   *
	 * @param bindingFault the binding fault to add
	 */
  public void addFault( BindingFault bindingFault )
  { m_listFaults.add( bindingFault ); }

  /**
   * Removes the specified binding fault
   *
   * @param bindingFault The binding fault to remove
   */
  public void removeFault( BindingFault bindingFault )
  { m_listFaults.remove( bindingFault ); }

  /**
   * Removes the specied binding fault by its name
   *
   * @param strName The name of the binding fault to remove
   */
  public void removeFault( String strName )
  {
    for ( Iterator iFaults = m_listFaults.iterator(); iFaults.hasNext(); )
    {
      BindingFault fault = (BindingFault)iFaults.next();

      if ( fault.getName() != null && fault.getName().equalsIgnoreCase( strName ) )
      {
        iFaults.remove();
        return;
      }
    } // end for

  } // end removeFault()

  /**
   * Removes all Binding faults
   */
  public void removeAllFaults()
  { m_listFaults.clear(); }

  /**
   * Get the specified fault binding by it's name
   *
   * @param strName the name of the binding fault to retrieve.
   *
   * @return The binding fault or null if the name does not exist
   */
  public BindingFault getFault( String strName )
  {
    for ( Iterator iFaults = m_listFaults.iterator(); iFaults.hasNext(); )
    {
      BindingFault fault = (BindingFault)iFaults.next();

      if ( fault.getName() != null && fault.getName().equalsIgnoreCase( strName ) )
      {
        return fault;
      }
    } // end for

    return null;

  } // end getFault()

	/**
	 * Gets a List all binding faults for this binding operation
   *
	 * @return a List all binding faults for this binding operatio
	 */
  public List getFaults()
  { return m_listFaults; }

  /**
   * Adds an extensibility element for specific service extensions
   *
   * @param extElement extensibility element for specific service extensions
   */
  public void addExtensibilityElement( ExtensibilityElement extElement )
  {  m_extSupport.addExtensibilityElement( extElement ); }

  /**
   * Removes the specified extensibility element
   *
   * @param extElement the extensibility element to remove
   */
  public void removeExtensibilityElement( ExtensibilityElement extElement )
  { m_extSupport.removeExtensibilityElement( extElement ); }

  /**
   * Removes all extensibility elements
   */
  public void removeAllExtensibilityElements()
  { m_extSupport.removeAllExtensibilityElements(); }


  /**
   * Gets all the extensibility elements defined.
   *
   * @return a List of all extensibility elements defined for this service
   */
  public List getExtensibilityElements()
  { return m_extSupport.getExtensibilityElements(); }



  /**
   * Adds an unknown extensibility element for specific service extensions
   *
   * @param unknownExtElementt the unknown extensibility element for specific service extensions
   */
  public void addUnknownExtensibilityElement( UnknownExtensibilityElement unknownExtElement )
  {  m_extSupport.addUnknownExtensibilityElement( unknownExtElement ); }

  /**
   * Removes the specified unknown extensibility element
   *
   * @param unknownExtElement the unknown extensibility element to remove
   */
  public void removeUnknownExtensibilityElement( UnknownExtensibilityElement unknownExtElement )
  { m_extSupport.removeUnknownExtensibilityElement( unknownExtElement ); }

  /**
   * Removes all unknown extensibility elements
   */
  public void removeAllUnknownExtensibilityElements()
  { m_extSupport.removeAllUnknownExtensibilityElements(); }


  /**
   * Gets all the unknown extensibility elements defined.
   *
   * @return a List of all unknown extensibility elements defined for this service
   */
  public List getUnknownExtensibilityElements()
  { return m_extSupport.getUnknownExtensibilityElements(); }

  /**
   * Removes all ExtensibilityElements and UnknownExtensibilityElements
   *
   */
  public List getAllElements()
  { return m_extSupport.getAllElements(); }
  
  
  /**
   * Gets the List of all ExtensibilityElements and UnknownExtensibilityElements
   * 
   * @return  the List of all ExtensibilityElements and UnknownExtensibilityElements
   */
  public void removeAllElements()
  { m_extSupport.removeAllElements(); }
  
  
  public List getContent()
  {
    List listContent = new LinkedList();

    listContent.addAll( getAllElements() );
    
    if ( m_bindingInput != null )
      listContent.add( m_bindingInput );

    if ( m_bindingOutput != null )
      listContent.add( m_bindingOutput );

    listContent.addAll( m_listFaults );

    return listContent;

  }
} // end class VwBindingOperationImpl{}

// *** End of VwBindingOperationImpl.java ***
