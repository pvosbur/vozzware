/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwMIMEContentImpl.java

Create Date: Apr 11, 2006
============================================================================================
*/
package com.vozzware.wsdl.extensions.mime;

import com.vozzware.wsdl.extensions.VwExtensibilityElementImpl;

import javax.wsdl.extensions.mime.MIMEContent;


/**
 * Implementation class for the MIMEContent interface
 *
 * @author Peter VosBurgh
 */
public class VwMIMEContentImpl extends VwExtensibilityElementImpl implements MIMEContent
{
  private String  m_strPart;
  private String  m_strType;

  public boolean isParent()
  { return false; }
  
  /**
   * Sets the part attribute value for this MIME content.
   *
   * @param strPart the part attribute value for this MIME content.
   */
  public void setPart( String strPart )
  { m_strPart = strPart; }

  /**
   * Gets the part attribute value for this MIME content.
   *
   * @return the part attribute value for this MIME content.
   */
  public String getPart()
  { return m_strPart; }

  /**
   * Sets the type attribute for this MIME content.
   *
   * @param strType the type attribute for this MIME content.
   */
  public void setType( String strType )
  { m_strType = strType; }

  /**
   * Gets the type attribute for this MIME content.
   *
   * @return the type attribute for this MIME content.
   */
  public String getType()
  { return m_strType; }

} // end class VwMIMEContentImpl{}

// *** End of VwMIMEContentImpl.java ***
