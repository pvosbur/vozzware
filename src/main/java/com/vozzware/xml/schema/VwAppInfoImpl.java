/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwAppInfoImpl.java

Create Date: Apr 11, 2006
============================================================================================
*/
package com.vozzware.xml.schema;

import javax.xml.schema.AppInfo;

public class VwAppInfoImpl extends VwSchemaCommonImpl implements AppInfo
{
  private String m_strSourceURI;
  private Object m_objContent;

  /**
   * Gets the URI assocaited with is appInfo element
   *
   * @return the URI assocaited with is appInfo element (may be null)
   */
  public String getSource()
  { return m_strSourceURI; }

  /**
   * Sets the URI assocaiated with this appInfo element
   *
   * @param strSourceURI The URI assocaiated with this appInfo element
   */
  public void setSource( String strSourceURI )
  { m_strSourceURI = strSourceURI; }

  /**
   * Sets the content for this appInfo element.
   *
   * @param objContent The content for this appInfo element
   */
  public void setContent( Object objContent )
  { m_objContent = objContent; }

  /**
   * Gets the content for this appInfo element.
   *
   * @return The content for this appInfo element.
   */
  public Object getContent()
  { return m_objContent; }

} // end class VwAppInfoImpl{}

// *** End of VwAppInfoImpl.java ***
  