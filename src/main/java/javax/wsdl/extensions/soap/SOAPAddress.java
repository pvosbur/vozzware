/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: SOAPAddress.java

============================================================================================
*/
package javax.wsdl.extensions.soap;

import javax.wsdl.extensions.ExtensibilityElement;


/**
 * This represents the soap:address WSDL element
 *
 * @author Peter VosBurgh
 */
public interface SOAPAddress extends ExtensibilityElement
{
  /**
   * Sets the location URI for this SOAP address.
   *
   * @param strLocationURI the desired location URI
   */
  public void setLocation( String strLocationURI );

  /**
   * Gets the location URI for this SOAP address.
   *
   * @return the location URI for this SOAP address.
   */
  public String getLocation();

} // end interface SOAPAddress{}

// *** End of SOAPAddress.java ***
