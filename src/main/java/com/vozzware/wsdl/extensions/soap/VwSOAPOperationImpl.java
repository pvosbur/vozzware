/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwSOAPOperationImpl.java

Create Date: Apr 11, 2006
============================================================================================
*/
package com.vozzware.wsdl.extensions.soap;

import com.vozzware.wsdl.extensions.VwExtensibilityElementImpl;

import javax.wsdl.extensions.soap.SOAPOperation;


/**
 * Implementation class for the SOAOOperation interface
 *
 * @author Peter VosBurgh
 */
public class VwSOAPOperationImpl extends VwExtensibilityElementImpl implements SOAPOperation
{
  private String  m_strSoapActionURI;
  private String  m_strStyle;

  public boolean isParent()
  { return false; }
  
	/**
	 * Sets the SOAP action attribute.
   *
	 * @param strSoapActionURI the SOAP action attribute.
	 */
	public void setSoapAction( String strSoapActionURI )
  { m_strSoapActionURI = strSoapActionURI; }

	/**
	 * Gets the value of the SOAP action attribute.
   *
	 * @return the SOAP action attribute's value
	 */
	public String getSoapAction()
  { return m_strSoapActionURI; }

  /**
   * Sets the style attribute
   *
   * @param strStyle the style attribute
   */
  public void setStyle( String strStyle )
  { m_strStyle = strStyle; }

  /**
   * Gets the style attribute for this SOAP operation.
   *
   * @return the style attribute
   */
  public String getStyle()
  { return m_strStyle; }

} // end class VwSOAPOperationImpl{}

// *** End of VwSOAPOperationImpl.java ***
