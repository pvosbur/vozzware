/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: AttributeGroup.java

============================================================================================
*/
package javax.xml.schema;

import java.util.List;

/**
 * This represents the XML Schema attributeGroup element
 */
public interface AttributeGroup extends SchemaCommon
{
  /**
   * Adds and AttributeGroup to this content
   *
   * @param attrGroup The AttributeGroup to add
   */
  public void addAttributeGroup( AttributeGroup attrGroup );

  /**
   * Gets the specified AttributeGroup
   *
   * @param strName The name of the AttributeGroup to get
   *
   * @return the specified AttributeGroup or null if named attribute does not exist
   */
  public AttributeGroup getAttributeGroup( String strName );

  /**
   * Removes the specified AttributeGroup object from this content
   *
   * @param attrGroup The AttributeGroup object to remove
   */
  public void removeAttributeGroup( AttributeGroup attrGroup );


  /**
   * Removes the specified AttributeGroup object from this content
   *
   * @param strName The name of AttributeGroup object to remove
   */
  public void removeAttributeGroup( String strName );

  /**
   * Adds an Attribute object to this content
   *
   * @param attr The Attribute to add
   */
  public void addAttribute( Attribute attr );

  /**
   * Gets the specified Attribute
   *
   * @param strName The name of the Attribute to get
   *
   * @return the specified Attribute or null if named attribute does not exist
   */
  public Attribute getAttribute( String strName );

  /**
   * Removes the specified Attribute object from this content
   *
   * @param attr The Attribute object to remove
   */
  public void removeAttribute( Attribute attr );

  /**
   * Removes the specified Attribute object from this content
   *
   * @param strName The name Attribute object to remove
   */
  public void removeAttribute( String strName );

  /**
   * Gets a List of all of the AttributeGroup and Attribute objects
   *
   * @return a List of all of the AttributeGroup and Attribute objects defined
   */
  public List getAttributes();

  /**
   * Sets the AnyAttribute
   * @param anyAttr The AnyAttribyte type (only one allowed)
   */
  public void setAnyAttribute( AnyAttribute anyAttr );

  /**
   * Gets the AnyAttribute type
   * @return he AnyAttribute type or null if none defined
   */
  public AnyAttribute getAnyAttribute();

  /**
   * Sets the Ref property
   *
   * @param strRef The reference name
   */
  public void setRef( String strRef );

  /**
   * Gets Ref property
   *
   * @return  The Ref property
   */
  public String getRef();

  /**
   * Gets a list of content defined for this AttributeGroup in the following order:
   * Annotation, AttributeGroup and Attributes, AnyAttribute
   *
   * @return a list of content defined for this AttributeGroup
   */
  public List getContent();

} // end interface AttributeGroup{}

// *** End of AttributeGroup.java ***
