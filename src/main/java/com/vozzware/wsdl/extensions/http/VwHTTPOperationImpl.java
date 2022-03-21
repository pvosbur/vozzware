/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwHTTPOperationImpl.java

Create Date: Apr 11, 2006
============================================================================================
*/
package com.vozzware.wsdl.extensions.http;

import com.vozzware.wsdl.extensions.VwExtensibilityElementImpl;

import javax.wsdl.extensions.http.HTTPOperation;


/**
 * Implementation class for the HTTPOperation interface
 *
 * @author Peter VosBurgh
 */
public class VwHTTPOperationImpl extends VwExtensibilityElementImpl implements HTTPOperation
{
  private String  m_strLocationURI;

  public boolean isParent()
  { return false; }
  
  /**
   * Sets the location URI for this HTTP operation.
   *
   * @param strLocationURI the location URI for this HTTP operation.
   */
  public void setLocation( String strLocationURI )
  { m_strLocationURI = strLocationURI; }

  /**
   * Gets the location URI for this HTTP operation.
   *
   * @return the location URI for this HTTP operation.
   */
  public String getLocation()
  { return m_strLocationURI; }

} // end class VwHTTPOperationImpl{}

// *** End of VwHTTPOperationImpl.java ***
  