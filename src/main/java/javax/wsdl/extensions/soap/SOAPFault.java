/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: SOAPFault.java

============================================================================================
*/
package javax.wsdl.extensions.soap;

import javax.wsdl.extensions.ExtensibilityElement;
import java.util.List;


/**
 * This represents the soap:fault WSDL element
 *
 * @author Peter VosBurgh
 */
public interface SOAPFault extends ExtensibilityElement
{

  /**
   * Sets the name for this SOAP fault.
   *
   * @param strName the name of the SOAP fault
   */
  public void setName( String strName );

  /**
   * Gets the name of this SOAP fault.
   *
   * @return the name of this SOAP fault.
   */
  public String getName();

  /**
   * Sets the use attribute for this SOAP fault element.
   *
   * @param strUse the use attribute for this SOAP fault element.
   */
  public void setUse( String strUse );

  /**
   * Gets the use attribute for this SOAP fault element.
   *
   * @return the use attribute for this SOAP fault element.
   */
  public String getUse();

  /**
   * Sets the encodingStyle attribute for this SOAP fault. Each style is delimited by a space
   *
   * @param strEncodingStyles the desired encodingStyles
   */
  public void setEncodingStyle( String strEncodingStyles );

  /**
   * Gets the encodingStyles for this SOAP fault.
   */
  public String getEncodingStyle();

  /**
   * Helper method to render encodingStyle attribute space delimited string as List of Strings
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
   * Sets the namespace URI attribute value for this SOAP fault.
   *
   * @param strNamespaceURI he namespace URI attribute value
   */
  public void setNamespace( String strNamespaceURI );

  /**
   * Get the namespace URI attribute for this SOAP fault.
   *
   * @return the namespace URI attribute
   */
  public String getNamespace();

} // end interface SOAPFault{}

// *** End of SOAPFault.java ***
