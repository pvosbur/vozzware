/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwDtdExternalEntity.java

Create Date: Apr 11, 2006
============================================================================================
*/
package com.vozzware.xml.dtd;

public class VwDtdExternalEntity extends VwDtdInternalEntity
{

  private int  m_nType;   // ID Type SYSTEM or PUBLIC

  /**
   * Constant for a SYSTEM ID external entity type
   */
  public static final int SYSTEM = 0;

  /**
   * Constant for a PUBLIC ID external entity type
   */
  public static final int PUBLIC = 1;

  /**
   * Constructor
   * @param strName   Name of the entity
   * @param strValue  Value of the public or system id
   * @param nType     The id type one of the constants
   */
  public VwDtdExternalEntity( String strName, String strValue, int nType )
  {
    super( strName, strValue );
    m_nType = nType;

  } // end VwDtdExternalEntity()


  /**
   * Gets the entity type
   * @return One of the type constants SYSTEM or PUBLIC based on the entity type
   */
  public int getType()
  { return m_nType; }



} // end class VwDtdExternalEntity{}


// *** End of VwDtdExternalEntity.java ***




