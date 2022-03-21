/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: Facet.java

============================================================================================
*/
package javax.xml.schema;

public interface Facet extends SchemaCommon
{
  /**
   * Sets the Fixed atrribute property property
   *
   * @param strFixed The Fixed atrribute property property
   */
  public void setFixed( String strFixed );

  /**
   * Gets the fixed attribute property
   *
   * @return  The fixed property
   */
  public String getFixed();

  /**
   * Sets the facet's value
   *
   * @param strValue The facet's value
   */
  public void setValue( String strValue );

  /**
   * Gets the facet's value
   *
   * @return  The facet's value
   */
  public String getValue();
}