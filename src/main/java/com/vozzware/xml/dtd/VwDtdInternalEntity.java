/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwDtdInternalEntity.java

Create Date: Apr 11, 2006
============================================================================================
*/
package com.vozzware.xml.dtd;

public class VwDtdInternalEntity
{
  private String m_strName;           // Name of the entity
  private String m_strValue;          // Th entity value


  public VwDtdInternalEntity( String strName, String strValue )
  {
    m_strName = strName;
    m_strValue = strValue;
  }


  /**
   * Gets the entity name
   */
  public String getName()
  { return m_strName; }


  /**
   * Gets the entity value
   */
  public String getValue()
  { return m_strValue; }


} // end class VwDtdInternalEntity{}


// *** End of VwDtdInternalEntity.java ***




