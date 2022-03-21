/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: SOAPBody.java

============================================================================================
*/
package javax.wsdl.extensions.soap;

import javax.wsdl.extensions.ExtensibilityElement;
import java.util.List;


/**
 * This represents the soap:body WSDL element
 *
 * @author Peter VosBurgh
 */
public interface SOAPBody extends ExtensibilityElement
{
  /**
   * Sets the parts attribute for this SOAP body.
   * <br>This is a space delimited String of message part values
   *
   * @param strParts the parts attribute for this SOAP body.
   */
  public void setParts( String strParts );


  /**
   * Helper method to transform a List of Strings into a space delimited parts string required
   * for the parts attribute value
   *
   * @param listParts The List of part strings to transform
   */
  public void setPartsList( List listParts );

  /**
   * Gets the parts attribute for this SOAP body.
   *
   * @return the parts attribute for this SOAP body.
   */
  public String getParts();


  /**
   * Helper method to transform the space delimited parts list into a List of Strings
   *
   * @return A List of Strings representing the parts
   */
  public List getPartsList();

  /**
   * Sets the use attribute for this SOAP body element.
   *
   * @param strUse the use attribute for this SOAP body element.
   */
  public void setUse( String strUse );

  /**
   * Gets the use attribute for this SOAP body element.
   *
   * @return the use attribute for this SOAP body element.
   */
  public String getUse();

  /**
   * Sets the encodingStyle attribute for this SOAP body. Each style is delimited by a space
   *
   * @param strEncodingStyles the desired encodingStyles
   */
  public void setEncodingStyle( String strEncodingStyles );

  /**
   * Gets the encodingStyles for this SOAP body.
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
   * Sets the namespace URI attribute value for this SOAP body.
   *
   * @param strNamespaceURI he namespace URI attribute value
   */
  public void setNamespace( String strNamespaceURI );

  /**
   * Get the namespace URI attribute for this SOAP body.
   *
   * @return the namespace URI attribute
   */
  public String getNamespace();

} // end interface SOAPBody{}

// *** End of SOAPBody.java ***
