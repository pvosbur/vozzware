/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: ExtensibilityElementSupport.java

============================================================================================
*/
package javax.wsdl.extensions;

import org.w3c.dom.Element;

/**
 * @author pvosburgh
 *
  */
public interface UnknownExtensibilityElement
{
  
  /**
   * Get the parent (starting element) of the undefined extension
   * 
   * @return The DOM element of the parent unknown extension element
   */
  public Element getElement();
  
} // end class UnknownExtensibilityElement{}

// *** End of UnknownExtensibilityElement.java ***

