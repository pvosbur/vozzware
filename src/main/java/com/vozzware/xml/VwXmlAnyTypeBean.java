/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwXmlAnyTypeBean.java

Create Date: Apr 11, 2006
============================================================================================
*/

package com.vozzware.xml;


/**
 * This interface is to identify beans that are define in dtd'd or xml schemas has having any content
 * Xml document.
 */
public interface VwXmlAnyTypeBean
{
  /**
   * Sets the conetnt of this ANY type bean with an VwDataObject holding child tag data
   *
   * @param dobjContent Data object with tag content
   */
  public void setContent( VwDataObject dobjContent );


  /**
   * Gets the content for this tag
   */
  public VwDataObject getContent();

} // end interface VwXmlAnyTypeBean{}

// *** End of VwXmlAnyTypeBean.java ***
