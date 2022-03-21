/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwAnyImpl.java

Create Date: Apr 11, 2006
============================================================================================
*/
package com.vozzware.xml.schema;

import javax.xml.schema.Any;

public class VwAnyImpl extends VwSchemaCommonImpl implements Any
{
  private String  m_strMaxOccurs;
  private String  m_strMinOccurs;
  private String  m_strNamespace;
  private String  m_strProcessContents;

  /**
   * Sets the MaxOccurs property
   *
   * @param strMaxOccurs The maxOccurs value
   */
  public void setMaxOccurs( String strMaxOccurs )
  { m_strMaxOccurs = strMaxOccurs; }

  /**
   * Gets MaxOccurs property
   *
   * @return  The MaxOccurs property
   */
  public String getMaxOccurs()
  { return m_strMaxOccurs; }

  /**
   * Sets the MinOccurs property
   *
   * @param strMinOccurs
   */
  public void setMinOccurs( String strMinOccurs )
  { m_strMinOccurs = strMinOccurs; }

  /**
   * Gets MinOccurs property
   *
   * @return  The MinOccurs property
   */
  public String getMinOccurs()
  { return m_strMinOccurs; }


  /**
   * Sets the strNamespace attribute
   * @param strNamespace the Namespace attribute
   */
  public void setNamespace( String strNamespace )
  { m_strNamespace = strNamespace; }
  /**
   * Gets the namespace attribute
   * @return  The namespace attribute
   */
  public String getNamespace()
  { return m_strNamespace; }


  /**
   * Sets the processContents attribute
   * @param strProcessContents the processContents attribute
   */
  public void setProcessContents( String strProcessContents )
  { m_strProcessContents = strProcessContents; }


  /**
   * Gets the processContents attribute
   * @return the processContents attribute
   */
  public String getProcessContents()
  { return m_strProcessContents; }

} // end class VwAnyImpl{}

// *** End of VwAnyImpl.java ***