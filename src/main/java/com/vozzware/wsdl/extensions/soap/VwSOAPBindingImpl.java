/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwSOAPBindingImpl.java

Create Date: Apr 11, 2006
============================================================================================
*/
package com.vozzware.wsdl.extensions.soap;

import com.vozzware.wsdl.extensions.VwExtensibilityElementImpl;

import javax.wsdl.extensions.soap.SOAPBinding;


/**
 * Implementation class for the SOAOBinding interface
 *
 * @author Peter VosBurgh
 */
public class VwSOAPBindingImpl extends VwExtensibilityElementImpl implements SOAPBinding
{
  private String  m_strStyle;
  private String  m_strTransportURI;

  public boolean isParent()
  { return false; }
  
  /**
   * Sets the style for this SOAP binding (rpc or document)
   *
   * @param strStyle the style for this SOAP binding (rpc or document)
   */
  public void setStyle( String strStyle )
  { m_strStyle = strStyle; }

  /**
   * Get the style for this SOAP binding.
   */
  public String getStyle()
  { return m_strStyle; }

  /**
   * Sets the SOAP transport URI.
   *
   * @param strTransportURI the SOAP transport URI.
   */
  public void setTransport( String strTransportURI )
  { m_strTransportURI = strTransportURI; }

  /**
   * Gets the transport URI for this binding.
   *
   * @return the transport URI for this binding.
   */
  public String getTransport()
  { return m_strTransportURI; }

} // end class VwSOAPBindingImpl{}

// *** End of VwSOAPBindingImpl.java ***
