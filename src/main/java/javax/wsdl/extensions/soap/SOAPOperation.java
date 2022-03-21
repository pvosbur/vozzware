/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: SOAPHeader.java

============================================================================================
*/
package javax.wsdl.extensions.soap;

import javax.wsdl.extensions.ExtensibilityElement;


/**
 * This reresents the soap:operation WSDL element
 *
 * @author Peter VosBurgh
 */
public interface SOAPOperation extends ExtensibilityElement
{
	/**
	 * Sets the SOAP action attribute.
   *
	 * @param strSoapActionURI the SOAP action attribute.
	 */
	public void setSoapAction( String strSoapActionURI );

	/**
	 * Gets the value of the SOAP action attribute.
   *
	 * @return the SOAP action attribute's value
	 */
	public String getSoapAction();

  /**
   * Sets the style attribute
   *
   * @param strStyle the style attribute
   */
  public void setStyle( String strStyle );

  /**
   * Gets the style attribute for this SOAP operation.
   *
   * @return the style attribute
   */
  public String getStyle();

} // end interface SOAPOperation{}

// *** End of SOAPOperation.java ***
