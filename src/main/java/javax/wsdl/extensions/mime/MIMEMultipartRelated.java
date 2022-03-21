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
import java.util.List;

/**
 * This represents the mime:multipartRelated WSDL element
 *
 * @author Peter VosBurgh
 */
public interface MIMEMultipartRelated extends ExtensibilityElement
{
  /**
   * Adds a MIME part to this MIME multipart related.
   *
   * @param mimePart the MIMEPart to be added
   */
  public void addMIMEPart( MIMEPart mimePart );

  /**
   * Gets a List of the MIME multiparts defined.
   *
   * @return a List of the MIME multiparts defined.
   */
  public List getMIMEParts();


  public List getContent();

} // end interface MIMEMultipartRelated{}

// *** End of MIMEMultipartRelated.java ***
