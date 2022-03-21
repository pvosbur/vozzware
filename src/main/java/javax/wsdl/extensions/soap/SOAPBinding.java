/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: SOAPBinding.java

============================================================================================
*/
package javax.wsdl.extensions.soap;

import javax.wsdl.extensions.ExtensibilityElement;


/**
 * This represents the soap:binding WSDL element
 *
 * @author Peter VosBurgh
 */
public interface SOAPBinding extends ExtensibilityElement
{
  /**
   * Sets the style for this SOAP binding (rpc or document)
   *
   * @param strStyle the style for this SOAP binding (rpc or document)
   */
  public void setStyle( String strStyle );

  /**
   * Get the style for this SOAP binding.
   */
  public String getStyle();

  /**
   * Sets the SOAP transport URI.
   *
   * @param strTransportURI the SOAP transport URI.
   */
  public void setTransport( String strTransportURI );

  /**
   * Gets the transport URI for this binding.
   *
   * @return the transport URI for this binding.
   */
  public String getTransport();

} // end interface SOAPBinding{}

// *** End of SOAPBinding.java ***
