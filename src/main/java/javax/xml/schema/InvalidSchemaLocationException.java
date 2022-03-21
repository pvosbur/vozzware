/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: InvalidSchemaLocationException.java

============================================================================================
*/
package javax.xml.schema;

/**
 * Exception class to handle invalid or unreachable schema locations
 */
public class InvalidSchemaLocationException extends Exception
{

  public InvalidSchemaLocationException( String strReason )
  { super( strReason ); }
  
} // end class InvalidSchemaLocationException{}

// *** End of InvalidSchemaLocationException.java ***

