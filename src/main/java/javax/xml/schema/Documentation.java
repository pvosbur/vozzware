/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: Documentation.java

============================================================================================
*/
package javax.xml.schema;

/**
 * This represents the Documentaion Annotaion xml component
 */
public interface Documentation extends SchemaCommon
{

  /**
   * Gets the URI assocaited with is documentaion element
   * @return the URI assocaited with is documentaion element (may be null)
   */
  public String getSource();

  /**
   * Sets the URI assocaiated with this documentation element
   * @param strURI The URI assocaiated with this documentation element
   */
  public void setSource( String strURI );

  /**
   * Gets the language this documentation content is specified in
   * @return
   */
  public String getLang();


  /**
   * Sets the language this documentation content is specified in
   * @param strLang The language abbreviation (i.e., english is en)
   */
  public void setLang( String strLang );


  /**
   * Sets the content for this documentaion.
   * @param objContent The content for this documentation element
   */
  public void setContent( Object objContent );


  /**
   * Gets the content for this documentaion element.
   * @return The content for this documentaion element.
   */
  public Object getContent();

} // end interface Documentation{}

// *** End of Documentation.java ***
