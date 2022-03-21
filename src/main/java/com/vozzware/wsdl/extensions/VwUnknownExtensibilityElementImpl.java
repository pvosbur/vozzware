/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwUnknownExtensibilityElementImpl.java

Create Date: Apr 11, 2006
============================================================================================
*/
package com.vozzware.wsdl.extensions;

import org.w3c.dom.Element;

import javax.wsdl.extensions.UnknownExtensibilityElement;

/**
 * @author pvosburgh
 *
  */
public class VwUnknownExtensibilityElementImpl implements UnknownExtensibilityElement
{
  private Element	m_element;
  
  
  /**
   * Default constructor
   *
   */
  public VwUnknownExtensibilityElementImpl()
  { ; }
  
  
  /**
   * Contructor that takes a DOM element
   * 
   * @param element The DOM element for the inknown extensibility handler
   */
  public VwUnknownExtensibilityElementImpl( Element element )
  { m_element = element; }
  
  
  /**
   * Get the parent (starting element) of the undefined extension
   * 
   * @return The DOM element of the parent unknown extension element
   */
  public Element getElement()
  { return m_element; }
  
  
  /**
   * Sets the element representing the unknown element handler
   * @param element
   */
  public void setElement( Element element )
  { m_element = element; }
  
} // end class VwUnknownExtensibilityElementImpl{}

// *** End of VwUnknownExtensibilityElementImpl.java ***

