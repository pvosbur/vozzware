/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: XmlCloseElementListener.java

============================================================================================
*/

package javax.xml.schema.util;


/**
 * This interface defines the method for custom tag listener objects.
 */
public interface XmlCloseElementListener
{

  /**
   * This method is invoked for registered custom tag listeners when a tag name registered
   * is closed.
   *
   * @param closeEvent The tag event object with context data for the event type
   */
  public void xmlTagClosed( XmlCloseElementEvent closeEvent );


} // end interface IVwXmlCloseTagListener{}

// *** End of IVwXmlCloseTagListener.java ***
