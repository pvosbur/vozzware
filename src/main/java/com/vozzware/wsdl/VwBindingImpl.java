/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwBindingImpl.java

Create Date: Apr 11, 2006
============================================================================================
*/
package com.vozzware.wsdl;

import com.vozzware.wsdl.extensions.VwExtensibilityElementSupportImpl;

import javax.wsdl.Binding;
import javax.wsdl.BindingOperation;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.UnknownExtensibilityElement;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * This represents the WSDL binding element
 *
 * @author Peter VosBurgh
 */
public class VwBindingImpl extends VwWSDLCommonImpl implements Binding
{
  private String            m_strType;
  private List              m_listBindingOps = new LinkedList();

  private VwExtensibilityElementSupportImpl m_extSupport = new VwExtensibilityElementSupportImpl();

  /**
   * Set the port type this is a binding for.
   *
   * @param strType the port type associated with this binding
   */
  public void setType( String strType )
  { m_strType = strType; }

	/**
	 * Get the port type this is a binding for.
   *
	 * @return the associated port type
	 */
	public String getType()
  { return m_strType; }

  /**
   * Add an operation binding to binding.
   *
   * @param bindingOp the operation binding to be added
   */
  public void addBindingOperation( BindingOperation bindingOp )
  {  m_listBindingOps.add( bindingOp ); }


  /**
   * Remove an operation binding from the binding.
   *
   * @param bindingOp the operation binding to be removed
   */
  public void removeBindingOperation( BindingOperation bindingOp )
  { m_listBindingOps.remove( bindingOp ); }


  /**
   * Removes all bound operations from the binding
   *
   */
  public void removeAllBindingOperations()
  { m_listBindingOps.clear(); }

  /**
   * Get the specified operation binding. Note that operation names can
   * be overloaded within a PortType. In case of overloading, the
   * names of the input and output messages can be used to further
   * refine the search.
   *
   * @param strName the name of the desired operation binding.
   * @param strInputName the name of the input message; if this is null
   * it will be ignored.
   * @param strOutputName the name of the output message; if this is null
   * it will be ignored.
   *
   * @return the corresponding operation binding, or null if there wasn't
   * any matching operation binding
   */
  public BindingOperation getBindingOperation( String strName,
											                         String strInputName,
											                         String strOutputName )
  {
    for ( Iterator iOps = m_listBindingOps.iterator(); iOps.hasNext(); )
    {
      BindingOperation op = (BindingOperation)iOps.next();

      if ( op.getName().equalsIgnoreCase( strName ) )
      {
        if ( strInputName != null )
        {
          if ( op.getInput().getName() == null )
            continue;

          if ( !op.getInput().getName().equalsIgnoreCase( strInputName ) )
            continue;

        }

        if ( strOutputName != null )
        {
          if ( op.getOutput().getName() == null )
            continue;

          if ( !op.getOutput().getName().equalsIgnoreCase( strOutputName ) )
            continue;

        }

        return op;

      } // end if

    } // end for

    return null;    // Not found

  } // end

  /**
   * Get all the operation bindings defined here.
   */
  public List getBindingOperations()
  { return m_listBindingOps; }


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
    listContent.addAll( m_listBindingOps );

    return listContent;
    

  }
} // end class VwBindingImpl

// *** End VwBindingImpl.java ***
