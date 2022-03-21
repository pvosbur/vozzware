/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: Port.java

============================================================================================
*/
package javax.wsdl;

import javax.wsdl.extensions.ExtensibilityElementSupport;


/**
 * This represents the wsdl:port WSDL element
 *
 */
public interface Port extends WSDLCommon, ExtensibilityElementSupport
{

  /**
   * Set the binding name attribute for this port
   *
   * @param strBindingName the binding name associated with this port
   */
  public void setBinding( String strBindingName );

	/**
	 * Gets the binding name attribute
   *
	 * @return the binding name this port refers to.
	 */
	public String getBinding();

} // end interface Port{}

// *** End of Port.java ***