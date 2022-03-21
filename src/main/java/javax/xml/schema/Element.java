/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: Element.java

============================================================================================
*/
package javax.xml.schema;

/**
 * This represents the schema element xml component
 */
public interface Element extends SchemaCommon
{

  /**
   * Sets the Abstract state of this element
   *
   * @param fAbstract true if this element is abstract, false otherwise
   */
  public void setAbstract( Boolean fAbstract );

  /**
   * Gets Abstract property
   *
   * @return  true if this is an abstract element, false otherwise
   */
  public Boolean isAbstract();

  /**
   * A space delimited string of any combination of 'extension' 'restriction' 'substitution or #all
   *
   * @param strBlock the element's block attribute value
   */
  public void setBlock( String strBlock );

  /**
   * Gets elements block attribute value
   *
   * @return  elements block attribute value
   */
  public String getBlock();

  /**
   * Sets the Default property
   *
   * @param strDefault
   */
  public void setDefault( String strDefault );

  /**
   * Gets Default property
   *
   * @return  The Default property
   */
  public String getDefault();

  /**
   * Sets the Final property
   *
   * @param strFinal  A space delimited string of any combination of 'extension' or 'restriction'
   */
   public void setFinal( String strFinal );

  /**
   * Gets Final property
   *
   * @return  The Final property
   */
   public String getFinal();

  /**
   * Sets the Fixed property
   *
   * @param strFixed
   */
   public void setFixed( String strFixed );

  /**
   * Gets Fixed property
   *
   * @return  The Fixed property
   */
   public String getFixed();

  /**
   * Sets the Form property
   *
   * @param strForm
   */
   public void setForm( String strForm );

  /**
   * Gets Form property
   *
   * @return  The Form property
   */
   public String getForm();


  /**
   * Determins if this element is a parent. This is a helper method
   */
   public boolean isParent();

  /**
   * Returns the number of direct children if this element is a parent. This is a helper method
   */
   public int getChildCount();


  /**
   * Sets the MaxOccurs property
   *
   * @param strMaxOccurs The maxOccurs value
   */
  public void setMaxOccurs( String strMaxOccurs );

  /**
   * Gets MaxOccurs property
   *
   * @return  The MaxOccurs property
   */
  public String getMaxOccurs();

  /**
   * Sets the MinOccurs property
   *
   * @param strMinOccurs
   */
  public void setMinOccurs( String strMinOccurs );

  /**
   * Gets MinOccurs property
   *
   * @return  The MinOccurs property
   */
  public String getMinOccurs();

  /**
   * Sets the Nillable property
   *
   * @param fNillibale true if this element can be null, false otherwise
   */
  public void setNillable( Boolean fNillibale );

  /**
   * Gets Nillable property
   *
   * @return  The Nillable property
   */
  public Boolean isNillable();

  /**
   * Sets the Ref property
   *
   * @param strRef
   */
  public void setRef( String strRef );

  /**
   * Gets Ref property
   *
   * @return  The Ref property
   */
  public String getRef();

  /**
   * Sets the SubstituationGroup property
   *
   * @param strSubstitutionGroup The substitutionGroup
   */
   public void setSubstituationGroup( String strSubstitutionGroup );

  /**
   * Gets SubstituationGroup property
   *
   * @return  The SubstituationGroup property
   */
   public String getSubstituationGroup();


  /**
   * Sets the Type property
   *
   * @param strType
   */
  public void setType( String strType );

  /**
   * Gets Type property
   *
   * @return  The Type property
   */
  public String getType();

  /**
   * Sets the elements content to contain a complexType
   *
   * @param complexType The complexType content for this element
   */
  public void setComplexType( ComplexType complexType );


  /**
   * Returns true if the content is a ComplexType
   * @return
   */
  public boolean isComplexType();

  /**
   * Returns the ComplexType object if the content is a ComplexType else null is returned
   * @return
   */
  public ComplexType getComplexType();

  /**
   * Helper method to determine if this element has a choice,sequence or all group child
   *
   * @param schema The parent schema object
   * @return true if this element has one of the m_btModel group elements:choice,sequence or all,
   * false otherwise
   */
  public boolean hasModelGroup( Schema schema );

  /**
   * Helper method to return the ModelGroup if this element has one.
   *
   * @param schema The parent schema object
   * @return the ModelGroup if this element has one, null otherwise
   */
  public ModelGroup getModelGroup( Schema schema );

  /**
   * Sets the elements content to contain a simpleType
   *
   * @param simpleType The simpleType content for this element
   */
  public void setSimpleType( SimpleType simpleType );

  /**
   * Returns the SimpleType object if the content is a SimpleType else null is returned
   * @return
   */
  public SimpleType getSimpleType();


  /**
   * Returns true if the content is a SimpleType
   * @return
   */
  public boolean isSimpleType();


  /**
   * Helper method to determine if this element has attributes defined
   *
   * @param schema The parent schema object
   * @return true if this element has attributes, false otherwise
   */
  public boolean hasAttributes( Schema schema );


  /**
   * Helper method to return a List of Attribhute objects defined for this element
   *
   * @param schema The parent schema object
   *
   * @return a List of Attribhute objects defined for this element if they exist otherwise null is returned
   */
  public java.util.List getAttributes( Schema schema );

  /**
   * Helper method to determine if this element is a collection. (i.e., maxOccurs > 1
   *
   * @return true if this element represents a collection, false otherwise
   */
  public boolean isCollection();
  
} // end interface Element{}

// *** End of Element.java ***
