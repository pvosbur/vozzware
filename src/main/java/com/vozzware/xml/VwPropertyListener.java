/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwPropertyListener.java

Create Date: Apr 11, 2006
============================================================================================
*/
package com.vozzware.xml;

/**
 * Implementations of this interface listen for property discoveries by the VwBeanToXml processes.
 */
public interface VwPropertyListener
{
  /**
   * This method is sent by the VwBeanToXml process for discovery of each property.
   * @param propEvent The property event
   * @return true to process the property, false to ignore the property
   */
  public boolean handleProperty( VwPropertyEvent propEvent );

} // end interface VwPropertyListener{}

// *** End of VwPropertyListener.java ***
