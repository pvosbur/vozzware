/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: Attribute.java

============================================================================================
*/
package javax.xml.schema;


public interface Attribute extends SchemaCommon
{

  /**
   * Gets the attribute's default value
   * @return the attribute's default value
   */
  public String getDefault();

  /**
   * Sets the attribute's default value
   * @param strDefault the attribute's default value
   */
  public void setDefault( String strDefault );

  /**
   * gets the attributes fixed value
   * @return
   */
  public String getFixed();

  /**
   * Sets thee attribute's fixed value
   * @param strFixed The fixed attribute value
   */
  public void setFixed( String strFixed );

  /**
   * Gets the attribute's from value
   * @return the attribute's from value
   */
  public String getForm();

  /**
   * Sets the attribute's form value
   * @param strForm The attribute's form value
   */
  public void setForm( String strForm );

  /**
   * Gets the reference to a globally defined attribute
   * @return the reference to a globally defined attribute
   */
  public String getRef();

  /**
   * Sets the reference to a globally defined attribute
   * @param strRef the reference to a globally defined attribute
   */
  public void  setRef( String strRef );

  /**
   * Gets the name of a built in schema type this attribute represents
   * @return The name of a built in schema type this attribute represents
   */
  public String getType();

  /**
   * Sets the name of a built in schema type this attribute represents
   * @param strType the name of a built in schema type this attribute represents
   */
  public void setType( String strType );

  /**
   * Gets the use attribute property
   * @return the use attribute property
   */
  public String getUse();

  /**
   * Sets the use attribute property
   * @param strUse use attribute property
   */
  public void setUse( String strUse );

  /**
   * Gets the simpleType content for this attribute
   * @return the simpleType content for this attribute or null
   */
  public SimpleType getSimpleType();


  /**
   * Sets the simpleType content for this attribute
   * @param simpleType the simpleType content for this attribute
   */
  public void setSimpleType( SimpleType simpleType );

} // end interface Attribute

// *** End of Attribute.java ***
