/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwInputImpl.java

Create Date: Apr 11, 2006
============================================================================================
*/
package com.vozzware.wsdl;

import javax.wsdl.Input;


/**
 * Implementation class for the Input interface
 *
 * @author Peter VosBurgh
 */
public class VwInputImpl extends VwWSDLCommonImpl implements Input
{
  private String m_strMessageName;

  public boolean isParent()
  { return false; }
  
  /**
   * Sets the message name for this input message
   *
   * @param strMessageName the input's message name
   */
  public void setMessage( String strMessageName )
  { m_strMessageName = strMessageName; }

  /**
   * Gets the input's message name
   *
   * @return the input's message name
   */
  public String getMessage()
  { return m_strMessageName; }

} // end class VwInputImpl{}

// *** End of VwInputImp.java ***
