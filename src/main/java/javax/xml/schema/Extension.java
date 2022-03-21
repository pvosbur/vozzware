/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: Extension.java

============================================================================================
*/
package javax.xml.schema;

import java.util.List;

/**
 * This supports the Xml Schema extension element for both complex and simple content
 */
public interface Extension extends SchemaCommon
{

  /**
   * Sets the base attribute value
   * @param strBase the base attribute value
   */
  public void setBase( String strBase );


  /**
   * Gets the base attribute value
   * @return the base attribute value
   */
  public String getBase();
  
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
   * Gets a Listof AttributeGroup objects
   *
   * @return  an List of Attribute Groups
   */
  public List getAttrGroups();


  /**
   * Gets a List of Attribute  objects
   *
   * @return  a List of Attribute objects
   */
  public List getAttibutes();

  /**
   * Gets a List of all Attribute and AttributeGroup objects
   * @return a List of all Attribute and AttributeGroup objects
   */
  public List getAllAttrContent();

  
  /**
   * Gets a List of all Attribute and AttributeGroup objects
   * @return a List of all Attribute and AttributeGroup objects
   */
  public List getAllAttrContent( boolean fIncludeParent );
  
  /**
   * Returns true if there are attribute or attributeGroup objects defined for this complexType
   */
  public boolean hasAttributes();


  /**
   * Returns true if this complex content defines only attributes
   * @return true if this complex content defines only attributes false otherwise
   */
  public boolean isAttributeOnly();

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
  public void setGroup( ModelGroup group );

  /**
   * Gets the m_btModel group content (if a m_btModel group content exists)
   * Returns the Group for this type if one exists or null otherwise
   */
  public ModelGroup getGroup();


  /**
   * Returns true if the content for this complexType is group, all, choice or sequence
   * @return true if the content for this complexType is group, all, choice or sequence
   */
  public boolean isModelGroup();


  /**
   * Get the conetnt object for this extension
   * @return
   */
  //public Object getContent();
  

} // end interface Extension{}

// *** End of Extension.java ***

