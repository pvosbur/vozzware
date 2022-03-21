/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwXmlBean.java

Create Date: Apr 11, 2006
============================================================================================
*/

package com.vozzware.xml;

import org.xml.sax.Attributes;

/**
 * This interface describes the contract that a bean must follow to be created from an
 * Xml document.
 */
public interface VwXmlBean
{

  /**
   * Sets an Attributes for a bean property
   */
  public void setAttributes( String strPropName, Attributes listAttr );


  /**
   * Retrieves an Attributes for the property specified or null if prop name
   * or attribute list does not exist
   */
  public Object getAttributes( String strPropName );


} // end interface VwXmlBean{}

// *** End of VwXmlBean.java ***
