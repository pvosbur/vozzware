/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwMIMEMimeXmlImpl.java

Create Date: Apr 11, 2006
============================================================================================
*/
package com.vozzware.wsdl.extensions.mime;

import com.vozzware.wsdl.extensions.VwExtensibilityElementImpl;

import javax.wsdl.extensions.mime.MIMEMimeXml;


/**
 *
 * Implementation class for the MIMEMimeXml interface
 *
 * @author Peter Vosburgh
 */
public class VwMIMEMimeXmlImpl extends VwExtensibilityElementImpl implements MIMEMimeXml
{
  private String m_strPart;

  public boolean isParent()
  { return false; }
  
  /**
   * Sets the part attribute value for this MIME mimeXml.
   *
   * @param strPart the part attribute value for this MIME mimeXml.
   */
  public void setPart( String strPart )
  { m_strPart = strPart; }

  /**
   * Gets the part attribute value for this MIME mimeXml.
   *
   * @return the part attribute value for this MIME mimeXml.
   */
  public String getPart()
  { return m_strPart; }

} // end class VwMIMEMimeXmlImpl{}

// *** End of VwMIMEMimeXmlImpl.java ***

