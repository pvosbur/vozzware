/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwOutputImpl.java

Create Date: Apr 11, 2006
============================================================================================
*/
package com.vozzware.wsdl;

import javax.wsdl.Output;


/**
 * Implementation class for the Output interface
 *
 * @author Peter VosBurgh
 */
public class VwOutputImpl extends VwWSDLCommonImpl implements Output
{
  private String m_strMessageName;

  public boolean isParent()
  { return false; }
  
  /**
   * Sets the message name for this output message
   *
   * @param strMessageName the output's message name
   */
  public void setMessage( String strMessageName )
  { m_strMessageName = strMessageName; }

  /**
   * Gets the output's message name
   *
   * @return the output's message name
   */
  public String getMessage()
  { return m_strMessageName; }

} // end class VwOutputImpl{}

// *** End of VwOutputImpl.java ***
