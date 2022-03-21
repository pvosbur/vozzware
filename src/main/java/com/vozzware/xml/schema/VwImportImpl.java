/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwImportImpl.java

Create Date: Apr 11, 2006
============================================================================================
*/
package com.vozzware.xml.schema;

import javax.xml.schema.Import;

/**
 * This represents the XMl Schema import element tag
 *
 * @author P. VosBurgh
 */
public class VwImportImpl extends VwIncludeImpl implements Import
{
  private String  m_strNamespace;


  /**
   * Sets the strNamespace attribute
   * @param strNamespace the strNamespace attribute
   */
  public void setNamespace( String strNamespace  )
  { m_strNamespace = strNamespace;  }

  /**
   * Gets the schema location URI attribute
   * @return  the schema location URI attribute
   */
  public String getNamespace()
  { return m_strNamespace; }

} // *** End of class VwImportImpl{}

// *** End of VwImportImpl.java