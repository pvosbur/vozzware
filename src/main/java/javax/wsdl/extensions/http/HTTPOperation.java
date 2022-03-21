/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: HTTPOperation.java

============================================================================================
*/
package javax.wsdl.extensions.http;

import javax.wsdl.extensions.ExtensibilityElement;


/**
 * This represents the wsdl:operation WSDL element
 *
 * @author Peter VosBurgh
 */
public interface HTTPOperation extends ExtensibilityElement
{
  /**
   * Sets the location URI for this HTTP operation.
   *
   * @param strlocationURI the location URI for this HTTP operation.
   */
  public void setLocation( String strlocationURI );

  /**
   * Gets the location URI for this HTTP operation.
   *
   * @return the location URI for this HTTP operation.
   */
  public String getLocation();

} // end interface HTTPOperation{}

// *** End of HTTPOperation.java ***
  