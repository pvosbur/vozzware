/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: HTTPAddress.java

============================================================================================
*/
package javax.wsdl.extensions.http;

import javax.wsdl.extensions.ExtensibilityElement;

/**
 * This represents the http:address WSDL element
 *
 * @author Peter VosBurgh
 */
public interface HTTPAddress extends ExtensibilityElement
{
  /**
   * Sets the location URI for this HTTP address.
   *
   * @param strLocationURI the location URI for this HTTP address.
   */
  public void setLocation( String strLocationURI );

  /**
   * Gets the location URI for this HTTP address.
   *
   * @return the location URI for this HTTP address. 
   */
  public String getLocation();

} // end interface HTTPAddress{}

// *** End of HTTPAddress.java ***
