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
 * This represents the mime:part WSDL element
 *
 * @author Peter VosBurgh
 */
public interface MIMEPart extends ExtensibilityElement
{
  /**
   * Adds an extensibility element to the mimePart
   *
   * @param extElement the extensibility element to add
   */
  public void addExtensibilityElement( ExtensibilityElement extElement );

  /**
   * Gets a List of the extensibility elements defined.
   *
   * @return a List of the extensibility elements defined.
   */
  public List getExtensibilityElements();

} // end interface MIMEPart{}

// *** End of MIMEPart.java ***
