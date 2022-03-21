/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwExtensibilityElementImpl.java

Create Date: Apr 11, 2006
============================================================================================
*/
package com.vozzware.wsdl.extensions;

import com.vozzware.wsdl.VwWSDLCommonImpl;

import javax.wsdl.extensions.ExtensibilityElement;

/**
 * Implementation class for the ExtensibilityElement interface
 *
 */
public class VwExtensibilityElementImpl extends VwWSDLCommonImpl implements ExtensibilityElement
{
  private boolean m_fRequired;

  /**
   * Sets the wsdl:required attribute on WSDL extension elements
   */
  public void setRequired( boolean fRequired )
  { m_fRequired = fRequired; }

  /**
   * Returns true if the semantics of this extension are required, else false is returned
   * 
   * @return true if the semantics of this extension are required, else false is returned
   */
  public boolean isRequired()
  { return m_fRequired; }

} // end class VwExtensibilityElementImpl{}

// *** End of VwExtensibilityElementImpl.java ***