/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwNumericTruncationException.java

============================================================================================
*/


package com.vozzware.util;

/**
 * This class defines an exception for a numeric truncation condition
 */
public class VwNumericTruncationException extends Exception
{
  /**
   * Constructs an exception instance with the error reason
   *
   * @param strText - A string with the error reason
   */
  public VwNumericTruncationException( String strText )
  { super( strText ); }

} // end class VwNumericTruncationException
