/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwHTTPUrlEncodedImpl.java

Create Date: Apr 11, 2006
============================================================================================
*/
package com.vozzware.wsdl.extensions.http;

import com.vozzware.wsdl.extensions.VwExtensibilityElementImpl;

import javax.wsdl.extensions.http.HTTPUrlEncoded;

/**
 * Implementation class for the HTTPUrlEncoded interface
 *
 * @author Peter VosBurgh
 */
public class VwHTTPUrlEncodedImpl extends VwExtensibilityElementImpl implements HTTPUrlEncoded
{
  public boolean isParent()
  { return false; }
  
} // end class VwHTTPUrlEncodedImpl{}

// *** End of VwHTTPUrlEncodedImpl.java ***
  