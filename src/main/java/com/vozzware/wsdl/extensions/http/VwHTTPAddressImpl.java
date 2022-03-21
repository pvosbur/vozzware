/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwHTTPAddressImpl.java

Create Date: Apr 11, 2006
============================================================================================
*/
package com.vozzware.wsdl.extensions.http;

import com.vozzware.wsdl.extensions.VwExtensibilityElementImpl;

/**
 * Implementation class for the HTTPAddress interface
 *
 * @author Peter VosBurgh
 */
public class VwHTTPAddressImpl extends VwExtensibilityElementImpl
{
  public String m_strLocationURI;

  public boolean isParent()
  { return false; }
  
  /**
   * Set the location URI for this HTTP address.
   *
   * @param strLocationURI the location URI for this HTTP address.
   */
  public void setLocation( String strLocationURI )
  { m_strLocationURI = strLocationURI; }

  /**
   * Gets the location URI for this HTTP address.    
   * 
   * @return the location URI for this HTTP address. 
   */
  public String getLocation()
  { return m_strLocationURI;}

} // end class VwHTTPAddressImpl{}

// *** End of VwHTTPAddressImpl.java ***
  