/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwHTTPUrlReplacementImpl.java

Create Date: Apr 11, 2006
============================================================================================
*/
package com.vozzware.wsdl.extensions.http;

import com.vozzware.wsdl.extensions.VwExtensibilityElementImpl;

import javax.wsdl.extensions.http.HTTPUrlReplacement;

/**
 * Implementation class for the HTTPUrlReplacement interface
 *
 * @author Peter VosBurgh
 */
public class VwHTTPUrlReplacementImpl extends VwExtensibilityElementImpl implements HTTPUrlReplacement
{
  public boolean isParent()
  { return false; }

} // end class VwHTTPUrlReplacementImpl{}

// *** End of VwHTTPUrlReplacementImpl.java