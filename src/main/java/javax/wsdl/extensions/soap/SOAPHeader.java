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
import java.util.List;


/**
 *
 * This represents the soap:header WSDL element
 *
 * @author Peter VosBurgh
 */
public interface SOAPHeader extends ExtensibilityElement
{
  /**
   * Sets the message name attribute for this SOAP header.
   *
   * @param strMessage the message name attribute
   */
  public void setMessage( String strMessage );

  /**
   * Gets the message name attribute for this SOAP header.
   *
   * @return the message name attribute
   */
  public String getMessage();

  /**
   * Sets the part attribute
   * @param strPart the part attribute
   */
  public void setPart( String strPart );


  /**
   * Gets the soap:header part attribute
   * @return the soap:header part attribute
   */
  public String getPart();

  /**
   * Sets the use attribute for this SOAP header element.
   *
   * @param strUse the use attribute for this SOAP header element.
   */
  public void setUse( String strUse );

  /**
   * Gets the use attribute for this SOAP header element.
   *
   * @return the use attribute for this SOAP header element.
   */
  public String getUse();

  /**
   * Sets the encodingStyle attribute for this SOAP header. Each style is delimited by a space
   *
   * @param strEncodingStyles the desired encodingStyles
   */
  public void setEncodingStyle( String strEncodingStyles );

  /**
   * Gets the encodingStyles for this SOAP header.
   */
  public String getEncodingStyle();

  /**
   * Helper method to render encodingStyle attribute space delimited string as List of Strings
   *
   * @return the encoding styles as List of Strings
   */
  public List getEncodingStyles();

  /**
   * Helper method to convert a List of encoding style strings to a space delimited string required
   * for attribute value serialization
   *
   * @param listEncodingStyles
   */
  public void setEncodingStyles( List listEncodingStyles );

  /**
   * Sets the namespace URI attribute value for this SOAP header.
   *
   * @param strNamespaceURI he namespace URI attribute value
   */
  public void setNamespace( String strNamespaceURI );

  /**
   * Get the namespace URI attribute for this SOAP header.
   *
   * @return the namespace URI attribute
   */
  public String getNamespace();

  /**
   * Adds a SOAPHeaderFault to this SOAP header
   * @param soapHeaderFault the SOAPHeaderFault to add
   */
  public void addSOAPHeaderFault( SOAPHeaderFault soapHeaderFault );

  /**
   * Removes the specified SOAPHeaderFault from this SOAP header
   *
   * @param soapHeaderFault The SOAPHeaderFault to remove
   */
  public void removeSOAPHeaderFault( SOAPHeaderFault soapHeaderFault );

  /**
   * Removes all SOAPHeaderFaults from this SOAP header
   */
  public void removeAllSOAPHeaderFaults();

  /**
   * Gets a List of the SOAPHeaderFaults for this SOAP header
   *
   * @return a List of the SOAPHeaderFaults for this SOAP header
   */
  public List getSOAPHeaderFaults();

} // end interface SOAPHeader{}

// *** End of SOAPHeader.java ***
