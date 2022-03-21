/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwSOAPAddressImpl.java

Create Date: Apr 11, 2006
============================================================================================
*/
package com.vozzware.wsdl.extensions.soap;

import com.vozzware.wsdl.extensions.VwExtensibilityElementImpl;

import javax.wsdl.extensions.soap.SOAPAddress;


/**
 * Implementation class for the SOAOAddress interface
 *
 * @author Peter VosBurgh
 */
public class VwSOAPAddressImpl extends VwExtensibilityElementImpl implements SOAPAddress
{
  private String m_strLocationURI;

  public boolean isParent()
  { return false; }
  
  /**
   * Sets the location URI for this SOAP address.
   *
   * @param strLocationURI the desired location URI
   */
  public void setLocation( String strLocationURI )
  { m_strLocationURI = strLocationURI; }

  /**
   * Gets the location URI for this SOAP address.
   *
   * @return the location URI for this SOAP address.
   */
  public String getLocation()
  { return m_strLocationURI; }


} // end class VwSOAPAddressImpl{}

// *** End of VwSOAPAddressImpl.java ***
