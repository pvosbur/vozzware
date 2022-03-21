/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: AppInfo.java

============================================================================================
*/
package javax.xml.schema;

import com.vozzware.xml.schema.AnyTypeContent;

public interface AppInfo extends AnyTypeContent, SchemaCommon
{
  /**
   * Gets the URI assocaited with is appInfo element
   * @return the URI assocaited with is appInfo element (may be null)
   */
  public String getSource();

  /**
   * Sets the URI assocaiated with this appInfo element
   * @param strURI The URI assocaiated with this appInfo element
   */
  public void setSource( String strURI );


} // end interface AppInfo{}

// *** End of AppInfo.java ***
  