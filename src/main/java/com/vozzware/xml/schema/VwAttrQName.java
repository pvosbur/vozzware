/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwAttrQName.java

Create Date: Apr 11, 2006
============================================================================================
*/
package com.vozzware.xml.schema;

public interface VwAttrQName
{

  /**
   * Gets the fully qualified name for an attribute (i.e., xsd:lang)
   * @param strAttrName The local part name of the attribute
   * @return The fully qualified name of the attribute in the form prefix:localpart or
   * just the local part if the attribute is not qualified
   */
  public String getAttrQname( String strAttrName );

} // end interface VwAttrQName{}

// *** End of VwAttrQName.java ***
