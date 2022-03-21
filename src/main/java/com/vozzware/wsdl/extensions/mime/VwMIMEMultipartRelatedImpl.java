/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwMIMEMultipartRelatedImpl.java

Create Date: Apr 11, 2006
============================================================================================
*/
package com.vozzware.wsdl.extensions.mime;

import com.vozzware.wsdl.extensions.VwExtensibilityElementImpl;

import javax.wsdl.extensions.mime.MIMEMultipartRelated;
import javax.wsdl.extensions.mime.MIMEPart;
import java.util.LinkedList;
import java.util.List;

/**
 * Implementation class for the MIMEMultipartRelated interface 
 *
 * @author Peter VosBurgh
 */
public class VwMIMEMultipartRelatedImpl extends VwExtensibilityElementImpl implements MIMEMultipartRelated
{
  private List m_listMimeParts = new LinkedList();
  
  public boolean isParent()
  { return m_listMimeParts.size() > 0 ; }

  /**
   * Adds a MIME part to this MIME multipart related.
   *
   * @param mimePart the MIMEPart to be added
   */
  public void addMIMEPart( MIMEPart mimePart )
  { m_listMimeParts.add( mimePart ); }

  public void addExtensibilityElement( MIMEPart mimePart )
  { m_listMimeParts.add( mimePart ); }

  /**
   * Gets a List of the MIME multiparts defined.
   *
   * @return a List of the MIME multiparts defined.
   */
  public List getMIMEParts()
  { return m_listMimeParts; }

  public List getContent()
  {
    List listContent = new LinkedList();
    if ( getDocumentation() != null )
      listContent.add( getDocumentation() );

    listContent.addAll( m_listMimeParts );
    return listContent;

  }
} // end class VwMIMEMultipartRelatedImpl{}

// *** End of VwMIMEMultipartRelatedImpl.java ***
