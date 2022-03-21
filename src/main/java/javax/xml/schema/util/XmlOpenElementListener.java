/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: XmlOpenElementListener.java

============================================================================================
*/

package javax.xml.schema.util;


/**
 * This interface defines the method for custom tag listener objects.
 */
public interface XmlOpenElementListener
{

  /**
   * This method is invoked for registered open tag listeners when the xml sax parser encounters
   * the start tag. This method can be used to set a java class handler for a tag that may
   * need to be resolved at runtime based on tag context such as parentage or attibutes.
   *
   * @param openEvent The open tag event object with tag context data
   */
  public void xmlTagOpen( XmlOpenElementEvent openEvent );


} // end interface IVwXmlOpenTagListener{}

// *** End of IVwXmlOpenTagListener.java ***
