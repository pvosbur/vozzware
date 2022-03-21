/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: Input.java

============================================================================================
*/
package javax.wsdl;


/**
 * This represents the input message of the portType operation
 *
 * @author Peter VosBurgh
 */
public interface Input extends WSDLCommon
{
  /**
   * Sets the message name for this input message
   *
   * @param strMessage the input's message name
   */
  public void setMessage( String strMessage );

  /**
   * Gets the input's message name
   *
   * @return the input's message name
   */
  public String getMessage();

} // end interface Input{}

// *** End of Input.java ***
