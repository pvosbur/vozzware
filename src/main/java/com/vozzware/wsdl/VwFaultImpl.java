/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwFaultImpl.java

Create Date: Apr 11, 2006
============================================================================================
*/
package com.vozzware.wsdl;

import javax.wsdl.Fault;


/**
 * Implementation class for the Input interface
 *
 * @author Peter VosBurgh
 */
public class VwFaultImpl extends VwWSDLCommonImpl implements Fault
{
  private String m_strMessageName;

  public boolean isParent()
  { return false; }
  
  /**
   * Sets the message name for this fault message
   *
   * @param strMessageName the fault's message name
   */
  public void setMessage( String strMessageName )
  { m_strMessageName = strMessageName; }

  /**
   * Gets the fault's message name
   *
   * @return the fault's message name
   */
  public String getMessage()
  { return m_strMessageName; }

} // end class VwFaultImpl{}

// *** End of VwFaultImp.java ***
