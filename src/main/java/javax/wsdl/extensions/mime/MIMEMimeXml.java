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
 *
 * This represents the mime:mimeXml WSDL element
 *
 * @author Peter Vosburgh
 */
public interface MIMEMimeXml extends ExtensibilityElement
{
  /**
   * Sets the part attribute value for this MIME mimeXml.
   *
   * @param strPart the part attribute value for this MIME mimeXml.
   */
  public void setPart( String strPart );

  /**
   * Gets the part attribute value for this MIME mimeXml.
   *
   * @return the part attribute value for this MIME mimeXml.
   */
  public String getPart();

} // end interface MIMEMimeXml{}

// *** End of MIMEMimeXml.java ***
