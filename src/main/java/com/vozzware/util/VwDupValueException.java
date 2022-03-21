/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwDupValueException.java

============================================================================================
*/


package com.vozzware.util;

/**
 * This class defines an exception for a duplicate value condition
 */
public class VwDupValueException extends Exception
{

  /**
   * Constructs an exception instance with the string "Duplicate Value" as its reason
   *
   */
  public VwDupValueException()
  { super( "Duplicate Value" ); }


  /**
   * Constructs an exception instance with the error reason
   *
   * @param strText - A string with the error reason
   */
  public VwDupValueException( String strText )
  { super( strText ); }

} // end VwDupValueException{}

// *** End of VwDupValueException.java ***
