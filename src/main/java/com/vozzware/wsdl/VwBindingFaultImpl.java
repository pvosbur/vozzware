/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwBindingFaultImpl.java

Create Date: Apr 11, 2006
============================================================================================
*/

package com.vozzware.wsdl;


import com.vozzware.wsdl.extensions.VwExtensibilityElementSupportImpl;

import javax.wsdl.BindingFault;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.UnknownExtensibilityElement;
import java.util.List;

/**
 * Implementation class for the BindingFault interface
 *
 * @author Peter VosBurgh
 */
public class VwBindingFaultImpl extends VwWSDLCommonImpl implements BindingFault
{

  private VwExtensibilityElementSupportImpl m_extSupport = new VwExtensibilityElementSupportImpl();


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
  { return getAllElements(); }
  
} // end class VwBindingFaultImpl{}

// *** End of VwBindingFaultImpl.java ***
