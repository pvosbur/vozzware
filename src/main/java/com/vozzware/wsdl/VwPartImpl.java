/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwPartImpl.java

Create Date: Apr 11, 2006
============================================================================================
*/
package com.vozzware.wsdl;

import javax.wsdl.Part;

/**
 * This represents the WSDL part element type
 *
 * @author Peter VosBurgh
 */
public class VwPartImpl extends VwWSDLCommonImpl implements Part
{
  private String  m_strElementName;
  private String  m_strType;

  /**
   * Sets the part's element attribute
   * @param strElementName  The name of the element in the schema sction to refer to
   */
  public void setElement( String strElementName )
  { m_strElementName  = strElementName; }

  /**
   * Gets the part's element attribute
   *
   * @return the part's element attribute
   */
  public String getElement()
  { return m_strElementName; }

  /**
   * Sets the part's type attribute
   * @param strType
   */
  public void setType( String strType )
  { m_strType = strType; }

  /**
   * Gets the part's type attribute
   *
   * @return the part's type attribute
   */
  public String getType()
  { return m_strType; }


  public boolean isParent()
  { return false; }
  
} // end class VwPartImpl{}

// *** End of VwPartImpl.java ***
