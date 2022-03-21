/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: ComplexContent.java

============================================================================================
*/
package javax.xml.schema;

import java.util.List;

/**
 * This representsthe XML Schema complexContent element
 */
public interface ComplexContent extends SchemaCommon
{

  /**
   * Sets the mixed attribute property
   * @param fMixed true if content is mixed, false otherwise
   */
  public void setMixed( boolean fMixed );


  /**
   * Returns the state of the mixed property
   * @return
   */
  public boolean isMixed();

  /**
   * Returns true if the complexContent only defines attributes
   * @return true if the complexContent only defines attributes
   */
  public boolean isAttributeOnly(); 

  /**
   * Returns the superclass of the m_btModel group if one of all, choice, sequence or group
   * was defined
   *
   * @return the superclass of the m_btModel group if one of all, choice, sequence or group
   * was defined
   */
  public ModelGroup getModelGroup();


  /**
   * Returns true if this complex content has a modelGroup
   * @return
   */
  public boolean hasModelGroup();

  /**
   * Helper method to determine if this complex content has attribute definitions
   * @return
   */
  public boolean hasAttributes();


  /**
   * Helper method to return attribute definitions for this complexContent
   *
   * @return
   */
  public List getAttributes();

  /**
   * Helper method to return attribute definitions for this complexContent
   *
   * @param fIncludeParent if true, return attributes from an extension or restriction
   * @return
   */
  public List getAttributes( boolean fIncludeParent );
 /**
   * Sets the Extension content type for this complexContent
   * @param extension The Extension object for this complexContent
   */
  public void setExtension( Extension extension );


  /**
   * Gets the Extension content object if defined
   * @return the Extension content object if defined else null is returned
   */
  public Extension getExtension();

  /**
   * Returns true if the complexContent is an extension
   * @return true if the complexContent is an extension
   */
  public boolean isExtension();
  
  /**
   * Sets the Restriction content type for this complexContent
   * @param restriction The Restriction object for this complexContent
   */
  public void setRestriction( Restriction restriction );


  /**
   * Gets the Restriction content object if defined
   * @return the Restriction content object if defined else null is returned
   */
  public Restriction getRestriction();

  
  /**
   * Returns true if the complexContent is a restriction
   * @return true if the complexContent is a restriction
   * 
   */
  public boolean isRestriction();  
  
  /**
   * Returns a List of the conent which may consist of an Annotation and either an ComplexExtension
   * or a ComplexRestriction
   * @return a List of the content which may consist of an Annotation and either an ComplexExtension
   * or a ComplexRestriction
   */
  public List getContent();


} // end interface ComplexContent{}

// *** End of ComplexContent.java
