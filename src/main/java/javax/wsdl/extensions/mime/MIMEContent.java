/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: MIMEContent.java

============================================================================================
*/
package javax.wsdl.extensions.mime;

import javax.wsdl.extensions.ExtensibilityElement;


/**
 * This represents the mome:content WSDL element
 *
 * @author Peter VosBurgh
 */
public interface MIMEContent extends ExtensibilityElement
{
  /**
   * Sets the part attribute value for this MIME content.
   *
   * @param strPart the part attribute value for this MIME content.
   */
  public void setPart( String strPart );

  /**
   * Gets the part attribute value for this MIME content.
   *
   * @return the part attribute value for this MIME content.
   */
  public String getPart();

  /**
   * Sets the type attribute for this MIME content.
   *
   * @param strType the type attribute for this MIME content.
   */
  public void setType( String strType );

  /**
   * Gets the type attribute for this MIME content.
   *
   * @return the type attribute for this MIME content.
   */
  public String getType();

} // end interface MIMEContent{}

// *** End of MIMEContent.java ***
