/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: Output.java


============================================================================================
*/
package javax.wsdl;


/**
 * This represents the output message of the protType operation
 *
 * @author Peter VosBurgh
 */
public interface Output extends WSDLCommon
{
  /**
   * Sets the message name for this output message
   *
   * @param strMessage the output's message name
   */
  public void setMessage( String strMessage );

  /**
   * Gets the output's message name
   *
   * @return the output's message name
   */
  public String getMessage();

} // end interface Output{}

// *** End of Output.java ***
