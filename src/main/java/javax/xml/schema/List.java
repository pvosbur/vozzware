/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: List.java

============================================================================================
*/
package javax.xml.schema;


/**
 * This represents the XML Schema List element type
 */
public interface List extends SchemaCommon
{
  /**
   * Sets the content's itemType
   *
   * @param strItemType
   */
  public void setItemType( String strItemType );

  /**
   * Gets the ItemType property
   *
   * @return  The ItemType property
   */
  public String getItemType();

  /**
   * Set a simpleType content for this list
   *
   * @param simpleType The simpleType content
   *
    */
  public void setSimpleType( SimpleType simpleType );

  /**
   * Gets the SimpleType content if the content contains a SimpleType
   * @return the SimpleType content if the content contains a SimpleType else null is returned
   */
  public SimpleType getSimpleType();

  /**
   * Return true if the content contains a SimpleType
   * @return  true if the content contains a SimpleType false otherwise
   */
  public boolean isSimpleType();

  /**
   * Gets the content object fro this simple type
   *
   * @return One of the following: VwSchemaList, VwSchemaRestriction, VwSchemaUnion
   *  or null if no content
   */
  public java.util.List getContent();

} // end interface List{}

// *** End of List.java