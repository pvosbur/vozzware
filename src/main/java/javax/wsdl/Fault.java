/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: Fault.java

============================================================================================
*/
package javax.wsdl;

/**
 * This interface represents a fault message of the protType operation
 *
 * @author Matthew J. Duftler
 */
public interface Fault extends WSDLCommon
{
  /**
   * Sets the message name for this fault message
   *
   * @param strMessage the fault's message name
   */
  public void setMessage( String strMessage );

  /**
   * Gets the fault's message name
   *
   * @return the fault's message name
   */
  public String getMessage();

} // end interface Fault{}

// *** End of Fault.java ***
