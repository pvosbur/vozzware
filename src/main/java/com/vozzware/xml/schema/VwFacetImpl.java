/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwFacetImpl.java

Create Date: Apr 11, 2006
============================================================================================
*/

package com.vozzware.xml.schema;

import javax.xml.schema.Facet;

/**
 * This represents the XMl Schema facet super class
 */
public class VwFacetImpl extends VwSchemaCommonImpl implements Facet
{

  private String                m_strFixed;
  private String                m_strValue;

  /**
   * Sets the Fixed atrribute property property
   *
   * @param strFixed The Fixed atrribute property property
   */
  public void setFixed( String strFixed )
  { m_strFixed = strFixed; }

  /**
   * Gets the fixed attribute property
   *
   * @return  The fixed property
   */
  public String getFixed()
  { return m_strFixed; }


  /**
   * Sets the facet's value
   *
   * @param strValue The facet's value
   */
  public void setValue( String strValue )
  { m_strValue = strValue; }

  /**
   * Gets the facet's value
   *
   * @return  The facet's value
   */
  public String getValue()
  { return m_strValue; }

  
  public boolean isParent()
  { return false; }
} // *** End of class VwFacetImpl{}

// *** End Of VwFacetImpl.java