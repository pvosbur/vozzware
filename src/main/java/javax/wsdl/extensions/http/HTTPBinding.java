/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: HTTPBinding.java

============================================================================================
*/
package javax.wsdl.extensions.http;

import javax.wsdl.extensions.ExtensibilityElement;

/**
 * This represents the htt:binding WSDL element
 *
 * @author Peter VosBurgh
 */
public interface HTTPBinding extends ExtensibilityElement
{
  /**
   * Set the verb for this HTTP binding.
   *
   * @param strVerb the verb for this HTTP binding.
   */
  public void setVerb( String strVerb );

  /**
   * Gets the verb for this HTTP binding.
   *
   * @return the verb for this HTTP binding.
   */
  public String getVerb();

} // end interface HTTPBinding{}

// *** End of HTTPBinding.java ***


