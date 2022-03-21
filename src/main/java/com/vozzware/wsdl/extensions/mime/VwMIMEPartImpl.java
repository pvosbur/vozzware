/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwMIMEPartImpl.java

Create Date: Apr 11, 2006
============================================================================================
*/
package com.vozzware.wsdl.extensions.mime;

import com.vozzware.wsdl.extensions.VwExtensibilityElementImpl;

import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.mime.MIMEPart;
import java.util.LinkedList;
import java.util.List;

/**
 * Implementation class for the MIMEPart interface
 *
 * @author Peter VosBurgh
 */
public class VwMIMEPartImpl extends VwExtensibilityElementImpl implements MIMEPart
{
  private List m_listMimeParts = new LinkedList();

  /**
   * Adds an extensibility element to the mimePart
   *
   * @param extElement the extensibility element to add
   */
  public void addExtensibilityElement( ExtensibilityElement extElement )
  { m_listMimeParts.add( extElement ); }

  /**
   * Gets a List of the extensibility elements defined.
   *
   * @return a List of the extensibility elements defined.
   */
  public List getExtensibilityElements()
  { return m_listMimeParts; }


  public List getContent()
  {
    List listContent = new LinkedList();
    if ( getDocumentation() != null )
      listContent.add( getDocumentation() );
    
    listContent.addAll( getExtensibilityElements() );
    return listContent;

  }
} // end class VwMIMEPartImpl{}

// *** End ofVwMIMEPartImpl.java ***
