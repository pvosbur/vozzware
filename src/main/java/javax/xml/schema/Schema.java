/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: Schema.java

============================================================================================
*/
package javax.xml.schema;

import java.util.List;

/*
 * This interface represents the xml schema component definition
 *
 * @author Peter VosBurgh
 */
public interface Schema extends SchemaCommon
{

  /**
   * Merge all included schemas into this schema instance 
   */
  public void mergeIncludes();
  
  /**
   * Adds an Annotation to the global schema component list
   *
   * @param annotation The annotation component to add
   */
  public void addAnnotation( Annotation annotation );

  /**
   * Returns a List of globally defined annotations.
   * @return
   */
  public List getAnnotations();

  /**
   * Remove the annotation
   * @param annotation The Annotation object to remove
   */
  public void removeAnnoation( Annotation annotation );


  /**
   * Remove all globally defined annotations.
   */
  public void removeAllAnnotations();


  /**
   * Adds an Element to the global schema component list
   *
   * @param element The element to add
   */
  public void addElement( Element element );


  /**
   * Gets The Element for the name request or null if no element exists for this name
   * @param strName The name of the scheam element to retrieve
   * @return The Element for the name request or null if no element exists for this name
   */
  public Element getElement( String strName );


  /**
   * Removes the element from this schema
   * @param strName the name of the element to remove
   */
  public void removeElement( String strName );


  /**
   * Removes all globally defined elements from this schema.
   */
  public void removeAllElements();


  /**
   * Get all globally defined elements for this schema
   * @return a List of all globally defined elements for this schema
   */
  public List getElements();


  /**
   * Gets a list of any Include objects for this schema
   * @return a list of any Include objects for this schema or null if non exist
   */
  public List<Include>getIncludes();

  /**
   * Gets a list of any Import objects for this schema
   * @return a list of any Import objects for this schema or null if non exist
   */
  public List<Import>getImports();

  /**
   * Adds an AttributeGroup component to the global schema component list
   *
   * @param attrGroup The attribute group to add
   */
  public void addAttributeGroup( AttributeGroup attrGroup );

  /**
   * Get the AttributGroup for the name specified
   * @param strName  the NCNAME of the AttributeGroup to get
   * @return the AttributGroup for the name specified or null if the name for the AttributeGroup does not exist
   */
  public AttributeGroup getAttributeGroup( String strName );


  /**
   * Returns the List of all AttributeGroups defined for this schema
   * @return the List of all AttributeGroups defined for this schema
   */
  public List getAttributeGroups();


  /**
   * Removes the AttributeGroup for the name specified
   * @param strName The NCNAME of the AttributeGroup to remove
   */
  public void removeAttributeGroup( String strName );


  /**
   * Removes all AttributeGroups from this schema
   */
  public void removeAllAttributeGroups();


  /**
   * Adds an Attribute component to the global schema component list
   *
   * @param attr The attribute to add
   */
  public void addAttribute( Attribute attr );


  /**
   * Returns a List of all globally defined Attributes
   * @return a List of all globally defined Attributes
   */
  public List getAttributes();
  /**
   * Gets the globally defined Attribute
   * @param strName The name of the attribute to get
   * @return The globally defined Attribute or null if the named attribute does not exist
   */
  public Attribute getAttribute( String strName );

  /**
   * Removes the globally defined Attribute
   * @param strName The name of the Attribute to remove
   */
  public void removeAttribute( String strName );

  /**
   * Removes all globally defined Attributes from this schema
   */
  public void removeAllAttributes();

  /**
   * Adds a ComplexType component to the global schema component list
   * @param complexType The ComplexType object to add
   */
  public void addComplexType( ComplexType complexType );

  /**
   * This helper adds a complex type from a Java class file, The Java class is introspected
   * and the getter properties are used to from the child group elements 
   * @param clsJava
   * 
   * @param strModelGroupType The group type all,choice or sequence
   * 
   * @param strClassNameAlias The type name to generate in place of the class name
   */
  public void addComplexType( Class clsJava, String strModelGroupType, String strClassNameAlias ) throws Exception;
  
  /**
   * Returns a List of all globally defined ComplexTypes
   * @return a List of all globally defined ComplexTypes
   */
  public List getComplexTypes();
  /**
   * Gets the globally defined ComplexType
   * @param strName The name of the ComplexType to get
   * @return The globally defined ComplexType or null if the named attribute does not exist
   */
  public ComplexType getComplexType( String strName );

  /**
   * Removes the globally defined ComplexType
   * @param strName The name of the ComplexType to remove
   */
  public void removeComplexType( String strName );

  /**
   * Removes all globally defined ComplexTypes from this schema
   */
  public void removeAllComplexTypes();

  
  /**
   * Gets the cpmplex object if it exists
   * 
   * @param strName The name of the object
   * @return
   */
  public Object getComplexObject( String strName );
  
  /**
   * Adds a SimpleType component to the global schema component list
   * @param simpleType The SimpleType object to add
   */
  public void addSimpleType( SimpleType simpleType );

  /**
   * Returns a List of all globally defined ComplexTypes
   * @return a List of all globally defined ComplexTypes
   */
  public List getSimpleTypes();
  /**
   * Gets the globally defined ComplexType
   * @param strName The name of the ComplexType to get
   * @return The globally defined ComplexType or null if the named attribute does not exist
   */
  public SimpleType getSimpleType( String strName );

  /**
   * Removes the globally defined ComplexType
   * @param strName The name of the ComplexType to remove
   */
  public void removeSimpleType( String strName );

  /**
   * Removes all globally defined ComplexTypes from this schema
   */
  public void removeAllSimpleTypes();


  /**
   * Adds an Import object to the content
   * @param imp The Import object to add
   */
  public void addImport( Import imp );


  /**
   * Adds an Include object to the content
   * @param include The Include object to add
   */
  public void addInclude( Include include );
  
  /**
    * Gets a global schema component
    *
    * @return The Schema Component for the name requested or null if the name does not exist
    */
  public Object getComponent( String strName );


  /**
   * Gets an iterator to the List of global defined schema components
   */
  public List getContent();

  /**
   * Sets the AttributeFormDefault property
   *
   * @param strAttributeFormDefault
   */
  public void setAttributeFormDefault( String strAttributeFormDefault );

  /**
   * Gets AttributeFormDefault property
   *
   * @return  The AttributeFormDefault property
   */
  public String getAttributeFormDefault();

  /**
   * Sets the BlockDefault property
   *
   * @param strBlockDefault
   */
  public void setBlockDefault( String strBlockDefault );

  /**
   * Gets BlockDefault property
   *
   * @return  The BlockDefault property
   */
  public String getBlockDefault();

  /**
   * Sets the ElementFormDefault property
   *
   * @param strElementFormDefault
   */
  public void setElementFormDefault( String strElementFormDefault );

  /**
   * Gets ElementFormDefault property
   *
   * @return  The ElementFormDefault property
   */
  public String getElementFormDefault();

  /**
   * Sets the FinalDefault property
   *
   * @param strFinalDefault
   */
  public void setFinalDefault( String strFinalDefault );

  /**
   * Gets FinalDefault property
   *
   * @return  The FinalDefault property
   */
  public String getFinalDefault();

  /**
   * Sets the TargetNamespace property
   *
   * @param strTargetNamespace
   */
  void setTargetNamespace( String strTargetNamespace );

  /**
   * Gets TargetNamespace property
   *
   * @return  The targetNamespace property or null if no targetNamespace is defined
   */
  public String getTargetNamespace();


  /**
   * Sets the default namespace for thie schema
   * @param strDefNamespace
   */
  public void setDefaultNamespace( String strDefNamespace );

  /**
   * Gets the default namespace defined for this schema
   * @return the default namespace defined for this schema or null if no default namespace is defined
   */
  public String getDefaultNamespace();

  /**
   * Sets the Version property
   *
   * @param strVersion
   */
  public void setVersion( String strVersion );

  /**
   * Gets Version property
   *
   * @return  The Version property
   */
  public String getVersion();


  /**
   * Gets the language for all human readable information in the schema
   * @return the language for all human readable information in the schema
   */
  public String getLang();


  /**
   * Sets the language for all human readable information in the schema
   * @param strLang The language abbreviation (i.e., english is en)
   */
  public void setLang( String strLang );


  // *** Schema factory methods to create the schema component types

  /**
   * Creates the All component
   * @return
   */
  public All createAll();

  /**
   * Creates the Annotaion component
   * @return
   */
  public Annotation createAnnotation();

  /**
   * Creats the Any component
   * @return
   */
  public Any createAny();

  /**
   * Creates the AppInfo component
   * @return
   */
  public AppInfo createAppInfo();

  /**
   * Creates the Attribute
   * @return
   */
  public Attribute createAttribute();


  /**
   * Creates the Attribute initialized with a name and type
   * 
   * @param strName the attribute's name
   * @param strType The attribute's data type (must be a QName)
   * @return
   */
  public Attribute createAttribute( String strName, String strType );

  /**
   * Creates the AttributeGroup
   * @return
   */
  public AttributeGroup createAttributeGroup();

  /**
   * Creates the Choice
   * @return
   */
  public Choice createChoice();


  /**
   * Creates a ComplexContent
   * @return
   */
  public ComplexContent createComplexContent();

  /**
   * Creates a ComplexExtension
   * @return
   */
  public ComplexExtension createComplexExtension();

  /**
   * Creates a ComplexRestriction
   * @return
   */
  public ComplexRestriction createComplexRestriction();


  /**
   * Creates a ComplexType
   * @return
   */
  public ComplexType createComplexType();


  /**
   * Creates a ComplexType initialized with a name
   * 
   * @param strName The name of the complex type
   * @return The newely created complexType
   */
  public ComplexType createComplexType( String strName );

  /**
   * Creates the Documentation
   * @return
   */
  public Documentation createDocumentaion();

  /**
   * Creates the Element
   * @return
   */
  public Element createElement();

  /**
   * Creates the Element with an initialized name and type
   * 
   * @param strName The name of the element
   * @param strType The type of the element
   * @return
   */
  public Element createElement( String strName, String strType );
  
  /**
   * Creates the Enumeration
   * @return
   */
  public Enumeration createEnumeration();

  /**
   * Creates the Extension
   * @return
   */
  public Extension createExtension();


  /**
   * Creates the FractionDigits
   * @return
   */
  public FractionDigits createFractionDigits();

  /**
   * Creates the Group
   * @return
   */
  public Group createGroup();

  /**
   * Creates the Import
   * @return
   */
  public Import createImport();


  /**
   * Creates the Include
   * @return
   */
  public Include createInclude();


  /**
   * Creates the Length
   * @return
   */
  public Length createLength();


  /**
   * Creates the List
   * @return
   */
  public javax.xml.schema.List createList();


  /**
   * Creates the MinInclusive
   * @return
   */
  public MinInclusive createMinInclusive();


  /**
   * Creates the MaxExclusive
   * @return
   */
  public MaxExclusive createMaxExclusive();

  /**
   * Creates the MaxLength
   * @return
   */
  public MaxLength createMaxLength();


  /**
   * Creates the MinLength
   * @return
   */
  public MinLength createMinLength();


  /**
   * Creates the Pattern
   * @return
   */
  public Pattern createPattern();


  /**
   * Creates the Restriction
   * @return
   */
  public Restriction createRestriction();


  /**
   * Creates the Sequence
   * @return
   */
  public Sequence createSequence();


  /**
   * Creates the SimpleContent
   * @return
   */
  public SimpleContent createSimpleContent();


  /**
   * Creates the SimpleType
   * @return
   */
  public SimpleType createSimpleType();


  /**
   * Creates the TotalDigits
   * @return
   */
  public TotalDigits createTotalDigits();


  /**
   * Creates the Union
   * @return
   */
  public Union createUnion();


  /**
   * Creates the WhiteSpace
   * @return
   */
  public WhiteSpace createWhiteSpace();


} // end interface Schema{}

// *** End of Schema.java ***
