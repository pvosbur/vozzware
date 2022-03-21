/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwDocumentationImpl.java

Create Date: Apr 11, 2006
============================================================================================
*/
package com.vozzware.xml.schema;

import javax.xml.schema.Documentation;

public class VwDocumentationImpl extends VwSchemaCommonImpl implements Documentation, VwAttrQName
{
  private String m_strSourceURI;
  private String m_strLang;
  private Object m_objContent;


  /**
   * Gets the URI assocaited with is documentaion element
   *
   * @return the URI assocaited with is documentaion element (may be null)
   */
  public String getSource()
  { return m_strSourceURI; }

  /**
   * Sets the URI assocaiated with this documentation element
   *
   * @param strSourceURI The URI assocaiated with this documentation element
   */
  public void setSource( String strSourceURI )
  { m_strSourceURI = strSourceURI; }

  /**
   * Gets the language this documentation content is specified in
   *
   * @return
   */
  public String getLang()
  {  return m_strLang;  }

  /**
   * Sets the language this documentation content is specified in
   *
   * @param strLang The language abbreviation (i.e., english is en)
   */
  public void setLang( String strLang )
  {  m_strLang = strLang;  }

  /**
   * Sets the content for this documentaion.
   *
   * @param objContent The content for this documentation element
   */
  public void setContent( Object objContent )
  { m_objContent = objContent;  }

  /**
   * Gets the content for this documentaion element.
   *
   * @return The content for this documentaion element.
   */
  public Object getContent()
  { return m_objContent; }


  /**
   * Gets the fully qualified name for an attribute (i.e., xsd:lang)
   * @param strAttrName The local part name of the attribute
   * @return The fully qualified name of the attribute in the form prefix:localpart or
   * just the local part if the attribute is not qualified
   */
  public String getAttrQname( String strAttrName )
  {
    if ( strAttrName.equalsIgnoreCase( "lang") )
      return "xml:lang";

    return strAttrName;

  }

} // end class VwDocumentationImpl{}

// end of VwDocumentationImpl.java ***
