/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwHTTPBindingImpl.java

Create Date: Apr 11, 2006
============================================================================================
*/
package com.vozzware.wsdl.extensions.http;

import com.vozzware.wsdl.extensions.VwExtensibilityElementImpl;

import javax.wsdl.extensions.http.HTTPBinding;

/**
 * Implementaion class for the HTTPBinding interface
 *
 * @author Peter VosBurgh
 */
public class VwHTTPBindingImpl extends VwExtensibilityElementImpl implements HTTPBinding
{
  private String m_strVerb;

  public boolean isParent()
  { return false; }
  
  /**
   * Sets the verb for this HTTP binding.
   *
   * @param strVerb the verb for this HTTP binding.
   */
  public void setVerb( String strVerb )
  { m_strVerb = strVerb; }

  /**
   * Gets the verb for this HTTP binding.
   *
   * @return the verb for this HTTP binding.
   */
  public String getVerb()
  { return m_strVerb; }

} // end class VwHTTPBindingImpl{}

// *** End of VwHTTPBindingImpl.java ***


