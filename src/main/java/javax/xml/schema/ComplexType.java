/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: ComplexType.java

============================================================================================
*/
package javax.xml.schema;

import java.util.List;

/**
 * This interfaces reprsents the XML Schema complexType element
 */
public interface ComplexType extends SchemaCommon
{
  /**
   * Search for the named element in the complexType's content
   * @param strElementName The name of the element to search for
   * @return
   */
  public Element findElement( String strElementName );
  
  /**
   * Sets the Abstract property
   *
   * @param fAbstract The abstract state
   */
  public void setAbstract( Boolean fAbstract );

  /**
   * Gets the Abstract property
   *
   * @return  The Abstract property
   */
  public Boolean getAbstract();

  /**
   * Sets the mixed content property
   *
   * @param fMixed The mixed content property -
   */
  public void setMixed( Boolean fMixed );

  /**
   * Gets Mixed property
   *
   * @return  The Mixed content property
   */
  public Boolean getMixed();

  /**
   * Sets the Block property which can a space delimited list of #all extension or restrivtion
   *
   * @param strBlock
   */
  public void setBlock( String strBlock );

  /**
   * Gets Block property
   *
   * @return  The Block property
   */
  public String getBlock();


  /**
   * Sets the Final property
   *
   * @param strFinal
   */
  void setFinal( String strFinal );

  /**
   * Gets Final property
   *
   * @return  The Final property
   */
  public String getFinal();

  /**
   * Sets the object's content as simple
   *
   * @param simpleContent The simple content
   *
   */
  public void setSimpleContent( SimpleContent simpleContent );

  /**
   * Returns the SomplexContent object if it exists
   *
   * @return the SomplexContent object if it exists or null otherwise
   */
  public SimpleContent getSimpleContent();

  /**
   * Returns true if this complexType is a simpleContent
   * @return  true if this complexType is a simpleContent
   */
  public boolean isSimpleContent();

  /**
   * Sets the object's content as complex
   *
   * @param complexContent class holding the complex content
   *
   */
  public void setComplexContent( ComplexContent complexContent );

  /**
   * Returns true if the content for this complexType is complexContent
   *
   * @return if the content for this complexType is complexContent
   */
  public boolean isComplexContent();



  /**
   * Returns the ComplexContent object if it exists
   *
   * @return the ComplexContent object if it exists or null otherwise
   */
  public ComplexContent getComplexContent();


  /**
   * Sets the  m_btModel group content type to All
   *
   * @param all The all m_btModel group content object
   *
   */
  public void setAll( All all );

  /**
   * Gets the All m_btModel group content
   * @return the All m_btModel group content if the content type is All else null is returned
   */
  public All getAll();


  /**
   * Returns true if the content type is the m_btModel group All
   * @return true if the content type is the m_btModel group All
   */
  public boolean isAllContent();

  /**
   * Sets the m_btModel group content to choice
   *
   * @param choice the choice m_btModel group content
   *
   */
  public void setChoice( Choice choice );


  /**
   * Gets the Choice m_btModel group content
   * @return the Choice m_btModel group content if the content type is Choice else null is returned
   */
  public Choice getChoice();


  /**
   * Returns true if the content type is the m_btModel group Choice
   * @return true if the content type is the m_btModel group Choice
   */
  public boolean isChoiceContent();


  /**
   * Sets the m_btModel group content to sequence
   *
   * @param sequence the choice m_btModel group content
   *
   */
  public void setSequence( Sequence sequence );


  /**
   * Gets the Sequence m_btModel group content
   * @return the Sequence m_btModel group content if the content type is Sequence else null is returned
   */
  public Sequence getSequence();


  /**
   * Returns true if the content type is the m_btModel group Sequence
   * @return true if the content type is the m_btModel group Sequence
   */
  public boolean isSequenceContent();

  /**
   * Sets a named m_btModel group
   *
   * @param group The named m_btModel group
   */
  public void setGroup( Group group );

  /**
   * Gets the m_btModel group content (if a m_btModel group content exists)
   * Returns the Group for this type if one exists or null otherwise
   */
  public Group getGroup();


  /**
   * Returns true if the content for this complexType is group, all, choice or sequence

   * @return true if the content for this complexType is group, all, choice or sequence
   */
  public boolean isModelGroup();


  /**
   * Adds an Attribute to this complexType
   *
   * @param attribute The attribute to add
   */
  public void addAttribute( Attribute attribute );

  /**
   * Removes the specified attribute from the complexType's attribute list
   *
   * @param attribute The attribute object to remove
   */
  public void removeAttribute( Attribute attribute );

  
  /**
   * Gets the named attribute
   * @param strAttrName The name of the attribute to get
   * 
   * @return The Attribute for name requested or null if it does not exist
   */
  public Attribute getAttribute( String strAttrName );
  
  /**
   * Removes the specified attribute from the complexType's attribute list
   *
   * @param strAttrName The name of the attribute to remove
   */
  public void removeAttribute( String strAttrName );

  
  /**
   * Adds an AttributeGroup to this complexType
   *
   * @param attributeGroup The attribute group to add
   */
  public void addAttributeGroup( AttributeGroup attributeGroup );

  
  
  /**
   * Removes the specified attribute group from the content list
   *
   * @param attributeGroup The Attribute group object to remove
   */
  public void removeAttributeGroup( AttributeGroup attributeGroup );

  /**
   * Removes the specified attribute group from the content list
   *
   * @param strName The name of the AttributeGroup object to remove
   */
  public void removeAttributeGroup( String strName );

  /**
   * Adds an AttributeGroup to this complexType
   * @param anyAttribute The attribute to add
   */
  public void setAnyAttribute( AnyAttribute anyAttribute);


  /**
   * Returns the AnyAttribute object is one was defined
   * @return the AnyAttribute object is one was defined or null
   */
  public AnyAttribute getAnyAttribute();

  /**
   * Helper method to return all Atrribute objects whether they are in Attribute groups or
   * single Attribute definition.
   *
   * @return A List of all Attribute objects defined for this complexType as well as as any parent attributes if
   * this complexType is an extension or restrtriction
   */
  public List getAttributes();

  
  /**
   * Helper method to return all Atrribute objects  whether they are in Attribute groups or
   * single Attribute definition as well as as any parent attributes if
   * this complexType is an extension or restrtriction
   *
   * @param fIncludeBase if true include any parent attributes or groups, false to only return attributes for this type
   * @return A List of all Attribute objects defined for this complexType
   */
  public List getAttributes( boolean fIncludeBase );
  
  /**
   * Gets a List of all the content objects defined for this complexType
   *
   * @return  a List of the content objects defined for this complexType in the following order:
   * <br> Annotation (if defined), One of (SimpleContent,ComplexContent,All,Sequence,Choice,Group),
   * <br> All Attribute and AttributeGroup objects and AnyAttribute
   */
  public List getContent();

  /**
   * Helper method to return the data type if the content is simpleContent
   *
   * @return The simpleContent data type
   */
  public String getType();

  /**
   * Gets a List to the attribute and or AttributeGroup objects
   *
   * @return  an List to the attribute/group list - may be empty
   */
  public List getAttrGroups();

  /**
   * Returns true if there are attribute or attributeGroup objects defined for this complexType
   */
  public boolean hasAttributes();

  /**
   * Returns true if the content is one of ComplexContent or ModelGroup
   */
  public boolean hasChildElements();

  /**
   * Helper method to determine if this complex type has a choice,sequence or all group child
   *
   * @return true if this complex type has one of the m_btModel group elements:choice,sequence or all,
   * false otherwise
   */
  public boolean hasModelGroup();

  /**
   * Returns the ModelGroup super class instance if there is a Sequence, choice, group or all.
   * May return null.
   *
   * @return the ModelGroup super class instance if there is a Sequence, choice, group or all,
   * null otherwise
   */
  public ModelGroup getModelGroup();

  /**
   * Allows any type of m_btModel group to be set
   * @param group either an ALL,Sequence or Choice instance
   */
  public void setModelGroup( ModelGroup group );
  
  /**
   * Returns true if this complexType is an attribute definition only
   * @return
   */
  public boolean isAttributeOnly();


} // end interface ComplexType{}

// *** End of ComplexType.java ***
