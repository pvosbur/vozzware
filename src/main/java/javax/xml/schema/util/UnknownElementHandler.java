/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: UnknownElementHandler.java

============================================================================================
*/
package javax.xml.schema.util;

import org.w3c.dom.Element;

/**
 * @author P. VosVurgh
 *
 * Collects the element in and its children in a DOM Element
 */
public interface UnknownElementHandler
{
  public void unknownElement( Object objElementOwner, String strQNameParent, Element eleUnknown );  
  
} // end interface VwUnkownElementHandler{}

// *** End of VwUnkownElementHandler.java ***

