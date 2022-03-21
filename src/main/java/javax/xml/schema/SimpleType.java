/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: SimpleType.java

============================================================================================
*/
package javax.xml.schema;


public interface SimpleType extends SchemaCommon
{
  /**
   * Gets the final SimpleType attribute
   * @return the final SimpleType attribute or null if not defined
   */
  public String getFinal();

  /**
   * Sets the filan SimpleType attribute
   * @param strFinal  The final simple type attribute
   */
  public void setFinal( String strFinal );

  /**
   * Set a restriction content for this simpleType
   *
   * @param content The restriction content
   *
   */
  public void setRestriction( Restriction content );

  /**
   * Returns the Restriction content if the content type is a Restriction
   * @return the Restriction content if the content type is a Restriction else null is returned
   */
  public Restriction getRestriction();

  /**
   * Returns true if the content is a Restriction
   * @return
   */
  public boolean isRestriction();

  /**
   * Set a list content for this simpleType
   *
   * @param content The list content
   *
   */
  public void setList( javax.xml.schema.List content );

  /**
   * Returns the List content if the content type is a List
   * @return the List content if the content type is a List else null is returned
   */
  public javax.xml.schema.List getList();

  /**
   * Returns true if the content is a schema List
   * @return
   */
  public boolean isList();

  /**
   * Set a union content for this simpleType
   *
   * @param content The union content
   */
  public void setUnion( Union content );

  /**
   * Returns the Union content if the content type is a Union
   * @return the Union content if the content type is a Union else null is returned
   */
  public Union getUnion();

  /**
   * Returns true if the content is a Union
   * @return
   */
  public boolean isUnion();

  /**
   * Gets a List of the content defined for this simple type
   *
   * @return Annotation (if defrined ) and one of the following: VwSchemaList, VwSchemaRestriction, VwSchemaUnion
   *  or and empty list if no content has been defined
   */
  public java.util.List getContent();

  /**
   * Returns the base type if this is a restriction
   */
  public String getType();

} // end interface SimpleType{}

// *** End of SimpleType.java ***
  