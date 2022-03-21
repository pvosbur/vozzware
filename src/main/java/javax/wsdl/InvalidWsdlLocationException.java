/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: InvalidWsdlLocationException.java

============================================================================================
*/
package javax.wsdl;

/**
 * Exception class to handle invalid or unreachable wsdl locations
 */
public class InvalidWsdlLocationException extends Exception
{

  public InvalidWsdlLocationException( String strReason )
  { super( strReason ); }
  
} // end class InvalidWsdlLocationException{}

// *** End of InvalidSWsdlLocationException.java ***

