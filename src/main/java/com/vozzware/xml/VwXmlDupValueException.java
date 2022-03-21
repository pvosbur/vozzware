/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwXmlDupValueException.java

Create Date: Apr 11, 2006
============================================================================================
*/
package com.vozzware.xml;

public class VwXmlDupValueException extends Exception
{

  /**
   * Constructor
   *
   * @param strDesc A description of the offending key
   */
  VwXmlDupValueException( String strDesc )
  { super( strDesc ); }

} // end of class VwXmlDupValueExceptio{}

// *** End of VwXmlDupValueException.java ***