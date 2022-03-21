/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwSchemaImpl.java

Create Date: Apr 11, 2006
============================================================================================
*/
package com.vozzware.xml.schema;

import com.vozzware.xml.namespace.Namespace;
import com.vozzware.xml.namespace.QName;
import com.vozzware.xml.schema.util.VwSchemaWriterImpl;

import javax.xml.schema.All;
import javax.xml.schema.Annotation;
import javax.xml.schema.Any;
import javax.xml.schema.AppInfo;
import javax.xml.schema.Attribute;
import javax.xml.schema.AttributeGroup;
import javax.xml.schema.Choice;
import javax.xml.schema.ComplexContent;
import javax.xml.schema.ComplexExtension;
import javax.xml.schema.ComplexRestriction;
import javax.xml.schema.ComplexType;
import javax.xml.schema.Documentation;
import javax.xml.schema.Element;
import javax.xml.schema.Enumeration;
import javax.xml.schema.Extension;
import javax.xml.schema.FractionDigits;
import javax.xml.schema.Group;
import javax.xml.schema.Import;
import javax.xml.schema.Include;
import javax.xml.schema.Length;
import javax.xml.schema.MaxExclusive;
import javax.xml.schema.MaxLength;
import javax.xml.schema.MinInclusive;
import javax.xml.schema.MinLength;
import javax.xml.schema.ModelGroup;
import javax.xml.schema.Pattern;
import javax.xml.schema.Restriction;
import javax.xml.schema.Schema;
import javax.xml.schema.SchemaCommon;
import javax.xml.schema.Sequence;
import javax.xml.schema.SimpleContent;
import javax.xml.schema.SimpleType;
import javax.xml.schema.TotalDigits;
import javax.xml.schema.Union;
import javax.xml.schema.WhiteSpace;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * This class implements the Schema interface
 */
public class VwSchemaImpl extends VwSchemaCommonImpl implements Schema
{
  private List    m_listAnnotations = new ArrayList();
  private List    m_listContent = new ArrayList();
  private List    m_listElements = new ArrayList();
  private List    m_listAttributes = new ArrayList();
  private List    m_listAttrGroups = new ArrayList();
  private List    m_listComplexTypes = new ArrayList();
  private List    m_listSimpleTypes = new ArrayList();

  private Map     m_mapContent = new HashMap();
  private Map     m_mapComplexObjects = new HashMap();

  private String  m_strAttrFormDefault;
  private String  m_strBlockDefault;
  private String  m_strElementFormDefault;
  private String  m_strFinalDefault;
  private String  m_strTargetNamespace;
  private String  m_strDefaultNamespace;
  private String  m_strVersion;
  private String  m_strLang;
  
  private static 	Map s_mapPrimSchemaTypes = new HashMap();
  private static 	Map s_mapJavaTypes = new HashMap();
  
  public final static String XML_SCHEMA1_0_URI = "http://www.w3.org/2001/XMLSchema";
    
  private Namespace	m_nsSchema;		// Namespace for this schema if defined
  
  static
  {
    buildTypeMaps();
  }
  
 
  /**
   * Merge all included schemas into this schema instance 
   */
  public void mergeIncludes()
  {
    List<VwSchemaImpl> listSchemas = new ArrayList<VwSchemaImpl>();
    findAllSchemas( listSchemas, this );
    
    for ( VwSchemaImpl schema : listSchemas )
    {
      m_listAnnotations.addAll( schema.m_listAnnotations );
      m_listAttrGroups.addAll( schema.m_listAttrGroups );
      m_listAttributes.addAll( schema.m_listAttributes );
      m_listComplexTypes.addAll( schema.m_listComplexTypes );
      m_listContent.addAll( schema.m_listContent );
      m_listElements.addAll( schema.m_listElements );
      m_listSimpleTypes.addAll( schema.m_listSimpleTypes );
      m_mapComplexObjects.putAll( schema.m_mapComplexObjects );
      m_mapContent.putAll( schema.m_mapContent );
      
    }
  }

  /**
   * Walk up a schema chain of included schemas and add each scheam to the list
   *
   * @param listchemas The list to add each included schema to
   * @param schema The schema to check for includes
   */
  private void findAllSchemas( List<VwSchemaImpl> listSchemas, Schema schema )
  {
    
    List<Include>list = schema.getIncludes();
    if ( list != null  )
    {
      for ( Include include : list )
      {
        VwSchemaImpl schemaInclude = (VwSchemaImpl)include.getSchema();
        listSchemas.add( schemaInclude );
        findAllSchemas( listSchemas, schemaInclude );
      }
    }
    
  } // end findAllSchemas()

  /**
   * Override to store namespace and prefix associated with this document
   */
  public void addNamespace( Namespace ns )
  {
    super.addNamespace( ns );
    
    if ( ns.getURI().equals( XML_SCHEMA1_0_URI ) )
      m_nsSchema = ns;
    
  } // end addNamespace()
  
  
  /**
   * Removes namespace for prefix and sets the schema namespace to null if it equals the scheam uri
   */
  public void removeNamespace( String strPrefix )
  {
    if ( m_nsSchema != null && m_nsSchema.getPrefix().equals( strPrefix ))
    {
      m_nsSchema = null;
      setQName( null );
      
    }
    
    super.removeNamespace( strPrefix );
    
  } // end removeNamespace()
  
  public void removeAllNamespaces()
  {
    m_nsSchema = null;
    super.removeAllNamespaces();
    
  } // end removeAllNamespaces()
  
  /**
   * Adds an Annotation to schema component list
   *
   * @param annotation The annotation component to add
   */
  public void addAnnotation( Annotation annotation )
  {
    m_listContent.add( annotation );
    m_listAnnotations.add( annotation );

  } // end addAnnotation()

  public void setAnnotation( Annotation annotation )
  { addAnnotation( annotation ); }

  /**
   * Returns a List of globally defined annotations.
   * @return
   */
  public List getAnnotations()
  { return m_listAnnotations; }


  /**
   * Remove the annotation
   * @param annotation The Annotation object to remove
   */
  public void removeAnnoation( Annotation annotation )
  {
    m_listContent.remove( annotation );
    m_listAnnotations.remove( annotation );

  } // end removeAnnoation()


  /**
   * Remove all globally defined annotations.
   */
  public void removeAllAnnotations()
  {
    for ( Iterator iElements = m_listAnnotations.iterator(); iElements.hasNext(); )
    {
      Object obj = iElements.next();
      m_listContent.remove( obj );
      iElements.remove();
    }

  }  // end removeAllAnnotations()


  /**
   * Adds an Element to the list of schema components
   *
   * @param element The element to add
   */
  public void addElement( Element element )
  {
    ((VwSchemaCommonImpl)element).m_schema = this;

    m_listContent.add( element );
    m_listElements.add( element );
    m_mapContent.put( element.getName(), element );

    if ( element.hasModelGroup( this ) )
    {
      m_mapComplexObjects.put( element.getName().toLowerCase(), element );
      checkModelGroup( element.getModelGroup( this ) );
    }
    
  } // end addElement()


  /**
   * @param modelGroup
   */
  private void checkModelGroup( ModelGroup modelGroup )
  {
    for ( Iterator iContent = modelGroup.getContent().iterator(); iContent.hasNext(); )
    {
      Object objContent = iContent.next();
      
      if ( objContent instanceof ModelGroup )
        checkModelGroup( (ModelGroup)objContent );
      else
      if ( objContent instanceof Element )
      {
        if ( ((Element)objContent).isComplexType() )
          m_mapComplexObjects.put( ((Element)objContent).getName().toLowerCase(), objContent );
      }
        
    } // end for()
    
  } // end checkModelGroup()

  /**
   * Gets The Element for the name request or null if no element exists for this name
   * @param strName The name of the scheam element to retrieve
   * @return The Element for the name request or null if no element exists for this name
   */
  public Element getElement( String strName )
  {
    Object obj = getSchemaContent( strName );
    if (  obj instanceof Element )
      return (Element)obj;

    return null;

  } // end getElement()


  /**
   * Removes the element from this schema
   * @param strName the name of the element to remove
   */
  public void removeElement( String strName )
  {
    Object obj = m_mapContent.get( strName );

    if ( obj instanceof Element )
    {
      m_mapContent.remove( strName );
      m_listElements.remove( obj );
      m_listContent.remove( obj );
    }

  } // end removeElement()


  /**
   * Removes all globally defined elements from this schema.
   */
  public void removeAllElements()
  {
    for ( Iterator iElements = m_listElements.iterator(); iElements.hasNext(); )
    {
      Object objElement = iElements.next();
      m_listContent.remove( objElement );
      m_mapContent.remove( ((Element)objElement).getName() );
    }

    m_listElements.clear();

  } // end removeAllElements()


  /**
   * Get all globally defined elements for this schema
   * @return a List of all globally defined elements for this schema
   */
  public List getElements()
  { return m_listElements; }

  /**
   * Gets a list of any Include objects for this schema
   * @return a list of any Include objects for this schema or null if non exist
   */
  public List<Include>getIncludes()
  {
    List<Include>list = new ArrayList<Include>();
    for( Iterator iObjects = m_listContent.iterator(); iObjects.hasNext(); )
    {
      Object objContent = iObjects.next();
      if ( objContent instanceof Include )
        list.add( (Include)objContent );
    }
    
    if ( list.size() > 0 )
      return list;
    
    return null;
    
  } // end getIncludes()

  /**
   * Gets a list of any Include objects for this schema
   * @return a list of any Include objects for this schema or null if non exist
   */
  public List<Import>getImports()
  {
    List<Import>list = new ArrayList<Import>();
    for( Iterator iObjects = m_listContent.iterator(); iObjects.hasNext(); )
    {
      Object objContent = iObjects.next();
      if ( objContent instanceof Import )
        list.add( (Import)objContent );
    }
    
    if ( list.size() > 0 )
      return list;
    
    return null;
    
  } // end getImports()


  /**
   * Adds an VwSchemaAttrGroup to ithe list
   *
   * @param attrGroup The attribute to add
   */
  public void addAttributeGroup( AttributeGroup attrGroup )
  {
    m_listContent.add( attrGroup );
    m_mapContent.put( attrGroup.getName(), attrGroup );
    m_listAttrGroups.add( attrGroup );

  } // end addAttributeGroup()


  /**
   * Get the AttributGroup for the name specified
   * @param strName  the NCNAME of the AttributeGroup to get
   * @return the AttributGroup for the name specified or null if the name for the AttributeGroup does not exist
   */
  public AttributeGroup getAttributeGroup( String strName )
  {
    
    Object obj = getSchemaContent( strName );

    if ( obj instanceof AttributeGroup )
      return (AttributeGroup)obj;

    return null;

  } // end getAttributeGroup()


  /**
   * Returns the List of all AttributeGroups defined for this schema
   * @return the List of all AttributeGroups defined for this schema
   */
  public List getAttributeGroups()
  { return m_listAttrGroups; }


  /**
   * Removes the AttributeGroup for the name specified
   * @param strName The NCNAME of the AttributeGroup to remove
   */
  public void removeAttributeGroup( String strName )
  {
    Object obj = m_mapContent.get(  strName );

    if ( obj instanceof AttributeGroup )
    {
      m_listAttrGroups.remove( obj );
      m_listContent.remove( obj );
      m_mapContent.remove( obj );
    }

  } // end removeAttributeGroup()


  /**
   * Removes all AttributeGroups from this schema
   */
  public void removeAllAttributeGroups()
  {
    for ( Iterator iElements = m_listAttrGroups.iterator(); iElements.hasNext(); )
    {
      Object objElement = iElements.next();
      m_listContent.remove( objElement );
      m_mapContent.remove( ((AttributeGroup)objElement).getName() );
    }

    m_listAttrGroups.clear();

  } // end removeAllAttributeGroups()


  /**
   * Adds an VwSchemaAttribute to ithe list
   *
   * @param attr The attribute to add
   */
  public void addAttribute( Attribute attr )
  {
    m_listContent.add( attr );
    m_mapContent.put( attr.getName(), attr );
    m_listAttributes.add( attr );

  } // end addAttribute()


  /**
   * Returns a List of all globally defined Attributes
   * @return a List of all globally defined Attributes
   */
  public List getAttributes()
  { return m_listAttributes; }

  /**
   * Gets the globally defined Attribute
   * @param strName The name of the attribute to get
   * @return The globally defined Attribute or null if the nameed attribute does not exist
   */
  public Attribute getAttribute( String strName )
  {
    Object obj = getSchemaContent( strName );

    if ( obj instanceof Attribute )
      return (Attribute)obj;

    return null;

  } // end getAttribute()


  /**
   * Removes the globally defined Attribute
   * @param strName The name of the Attribute to remove
   */
  public void removeAttribute( String strName )
  {
    Object obj = m_mapContent.get(  strName );

    if ( obj instanceof Attribute )
    {
      m_listAttributes.remove( obj );
      m_listContent.remove( obj );
      m_mapContent.remove( obj );
    }

  } // end removeAttribute()


  /**
   * Removes all globally defined Attributes from this schema
   */
  public void removeAllAttributes()
  {
    for ( Iterator iElements = m_listAttributes.iterator(); iElements.hasNext(); )
    {
      Object objElement = iElements.next();
      m_listContent.remove( objElement );
      m_mapContent.remove( ((Attribute)objElement).getName() );
    }

    m_listAttributes.clear();

  }


  /**
   * Adds a ComplexType component to the global schema component list
   * @param complexType The ComplexType object to add
   */
  public void addComplexType( ComplexType complexType )
  {
    ((VwComplexTypeImpl)complexType).setSchema( this );
    
    m_listContent.add( complexType );
    m_listComplexTypes.add( complexType );
    
    String strTypeName = complexType.getName();
      
    if ( strTypeName != null )
    {
      m_mapContent.put( strTypeName, complexType );
      m_mapComplexObjects.put( strTypeName.toLowerCase(), complexType );
    
    }
    if ( complexType.hasModelGroup() )
      checkModelGroup( complexType.getModelGroup() );
    
  } // end addComplexType()

  /**
   * This helper adds a complex type from a Java class file, The Java class is introspected
   * and the getter properties are used to from the child group elements 
   * @param clsJava
   * 
   * @param strModelGroupType The group type all,choice or sequence
   * 
   * @param strClassNameAlias The type name to generate in place of the class name
   */
  public void addComplexType( Class clsJava, String strModelGroupType, String strClassNameAlias ) throws Exception
  {
    addComplexType( getComplexTypeFromClass( clsJava, strModelGroupType, strClassNameAlias ) );
    
  } // end addComplexType()
  
  
  /**
   * @param clsJava
   * @param strModelGroupType
   * @param strClassNameAlias
   * @return
   */
  private ComplexType getComplexTypeFromClass( Class clsJava, String strModelGroupType, String strClassNameAlias ) throws Exception
  {

    ComplexType ctype = createComplexType();
    
    
    String strName = null;
    
    if ( strClassNameAlias != null )
      strName = strClassNameAlias;
    else
    {
      strName = clsJava.getName();
      
      int nPos = strName.lastIndexOf( "." );
      
      if ( nPos >= 0 )
        strName = strName.substring( ++nPos );
      
    }
    
    ctype.setName( strName );
    
    ModelGroup group = null;
    
    if ( strModelGroupType.equals( "all") )
      group = createAll();
    else
    if ( strModelGroupType.equals( "sequence") )
      group = createSequence();
    else
    if ( strModelGroupType.equals( "choice") )
      group = createChoice();
      
    ctype.setModelGroup( group );
    
    
    PropertyDescriptor[] aProps = Introspector.getBeanInfo( clsJava ).getPropertyDescriptors();
    
    for ( int x = 0; x < aProps.length; x++ )
    {
      String strPropName = aProps[ x ].getName();
      
      if ( strPropName.equals( "class") )
        continue;
      
      Method m = aProps[ x ].getReadMethod();
      
       
      if ( m != null )
      {
        Class clsRetType = m.getReturnType();
        
        Element element = createElement();
        
        element.setName(  strPropName );
        
        group.addElement( element );
        
        String strSchemaType = (String)s_mapJavaTypes.get( clsRetType );
        
        if ( strSchemaType != null )
        {
          element.setType( strSchemaType );
          if ( clsRetType.isArray() )
            element.setMaxOccurs( "unbounded" );
          
        }
        else
        if ( Collection.class.isAssignableFrom( clsRetType ) || Map.class.isAssignableFrom( clsRetType ) 
               || Vector.class.isAssignableFrom( clsRetType ) || Hashtable.class.isAssignableFrom( clsRetType ) )
        {
            element.setType( "unknownType");
            element.setMaxOccurs( "unbounded" );
            
        }
        else
        {
          // We have a new complext type (user defined)
          
          String strTypeName = clsRetType.getName();
          String strPackageName = strTypeName;
          
          if ( clsRetType.isArray() )
          {  
            element.setMaxOccurs( "unbounded" );
            strPackageName = strPackageName.substring( 2 ); // Strip off the [L so we can create a non array class type
            
            if ( strPackageName.endsWith( ";") )
              strPackageName = strPackageName.substring( 0, strPackageName.length() - 1 );
            
         
          }
          
          int nPos = strTypeName.lastIndexOf( '.' );
          
          if ( nPos >= 0 )
            strTypeName = strTypeName.substring( ++nPos );
          
          element.setType( strTypeName );
          
          Class clsCType = Class.forName( strPackageName );
          
          // Add new complecType
          addComplexType( clsCType, strModelGroupType, null );
          
            
        } // end else
          
      } // end if ( m!=null)
      
    } // end for()
    
    return ctype;
  }

  /**
   * Returns a List of all globally defined ComplexTypes
   * @return a List of all globally defined ComplexTypes
   */
  public List getComplexTypes()
  { return m_listComplexTypes; }

  /**
   * Gets the globally defined ComplexType
   * @param strName The name of the ComplexType to get
   * @return The globally defined ComplexType or null if the named attribute does not exist
   */
  public ComplexType getComplexType( String strName )
  {
    Object obj = getSchemaContent(  strName );

    if ( obj instanceof ComplexType )
      return (ComplexType)obj;

    return null;

  }

  /**
   * Removes the globally defined ComplexType
   * @param strName The name of the ComplexType to remove
   */
  public void removeComplexType( String strName )
  {
    Object obj = m_mapContent.get(  strName );

    if ( obj instanceof ComplexType )
    {
      m_listAttributes.remove( obj );
      m_listContent.remove( obj );
      m_mapContent.remove( obj );
    }

  }

  /**
   * Removes all globally defined ComplexTypes from this schema
   */
  public void removeAllComplexTypes()
  {
    for ( Iterator iElements = m_listComplexTypes.iterator(); iElements.hasNext(); )
    {
      Object objElement = iElements.next();
      m_listContent.remove( objElement );
      m_mapContent.remove( ((Attribute)objElement).getName() );
    }

    m_listComplexTypes.clear();

  }


  /**
   * Adds a SimpleType component to the global schema component list
   * @param simpleType The SimpleType object to add
   */
  public void addSimpleType( SimpleType simpleType )
  {
    m_listContent.add( simpleType );
    m_listSimpleTypes.add( simpleType );
    m_mapContent.put( simpleType.getName(), simpleType );

  } // end addSimpleType()

  /**
   * Returns a List of all globally defined SimpleTypes
   * @return a List of all globally defined SimpleTypes
   */
  public List getSimpleTypes()
  { return m_listSimpleTypes; }


  /**
   * Gets the complex object if it exists
   * 
   * @param strName The name of the object
   * @return
   */
  public Object getComplexObject( String strName )
  { return m_mapComplexObjects.get( strName.toLowerCase() ); }

  /**
   * Gets the globally defined SimpleType
   * @param strName The name of the SimpleType to get
   * @return The globally defined SimpleType or null if the named simpleType does not exist
   */
  public SimpleType getSimpleType( String strName )
  {
    Object obj = getSchemaContent(  strName );

    if ( obj instanceof SimpleType )
      return (SimpleType)obj;

    return null;

  }


  /**
   * Removes the globally defined SimpleType
   * @param strName The name of the SimpleType to remove
   */
  public void removeSimpleType( String strName )
  {
    Object obj = m_mapContent.get(  strName );

    if ( obj instanceof SimpleType )
    {
      m_listSimpleTypes.remove( obj );
      m_listContent.remove( obj );
      m_mapContent.remove( obj );
    }

  }

  /**
   * Removes all globally defined SimpleTypes from this schema
   */
  public void removeAllSimpleTypes()
  {
    for ( Iterator iElements = m_listSimpleTypes.iterator(); iElements.hasNext(); )
    {
      Object objElement = iElements.next();
      m_listContent.remove( objElement );
      m_mapContent.remove( ((Attribute)objElement).getName() );
    }

    m_listSimpleTypes.clear();

  }



  /**
   * Adds an Import object to the content
   * @param imp The Import object to add
   */
  public void addImport( Import imp )
  {
    m_listContent.add( imp );
  }


  /**
   * Adds an Include object to the content
   * @param include The Include object to add
   */
  public void addInclude( Include include )
  {
    m_listContent.add( include );
  }

  /**
   * Gets a global schema component
   *
   * @return The VwSchemaComponent for the name requested or null if the name does not exist
   */
  public Object getComponent( String strName )
  { return getSchemaContent( strName );  }


  /**
   * Gets  the List of schema content
   */
  public List getContent()
  { return m_listContent; }


  /**
   * Sets the AttributeFormDefault property
   *
   * @param strAttrFormDefault
   */
  public void setAttributeFormDefault( String strAttrFormDefault )
  { m_strAttrFormDefault = strAttrFormDefault;  }


  /**
   * Gets AttributeFormDefault property
   *
   * @return The AttributeFormDefault property
   */
  public String getAttributeFormDefault()
  { return m_strAttrFormDefault; }


  /**
   * Sets the BlockDefault property
   *
   * @param strBlockDefault
   */
  public void setBlockDefault( String strBlockDefault )
  { m_strBlockDefault = strBlockDefault; }


  /**
   * Gets BlockDefault property
   *
   * @return The BlockDefault property
   */
  public String getBlockDefault()
  { return m_strBlockDefault; }


  /**
   * Sets the ElementFormDefault property
   *
   * @param strElementFormDefault
   */
  public void setElementFormDefault( String strElementFormDefault )
  { m_strElementFormDefault = strElementFormDefault; }


  /**
   * Gets ElementFormDefault property
   *
   * @return The ElementFormDefault property
   */
  public String getElementFormDefault()
  { return m_strElementFormDefault; }


  /**
   * Sets the FinalDefault property
   *
   * @param strFinalDefault
   */
  public void setFinalDefault( String strFinalDefault )
  { m_strFinalDefault = strFinalDefault; }


  /**
   * Gets FinalDefault property
   *
   * @return The FinalDefault property
   */
  public String getFinalDefault()
  { return m_strFinalDefault; }


  /**
   * Sets the TargetNamespace property
   *
   * @param strTargetNamespace
   */
  public void setTargetNamespace( String strTargetNamespace )
  { m_strTargetNamespace = strTargetNamespace;  }


  /**
   * Gets TargetNamespace property
   *
   * @return The TargetNamespace property
   */
  public String getTargetNamespace()
  { return m_strTargetNamespace; }


  /**
   * Sets the default namespace for thie schema
   * @param strDefaultNamespace
   */
  public void setDefaultNamespace( String strDefaultNamespace )
  { m_strDefaultNamespace = strDefaultNamespace; }


  /**
   * Gets the default namespace defined for this schema
   * @return the default namespace defined for this schema or null if no default namespace is defined
   */
  public String getDefaultNamespace()
  { return m_strDefaultNamespace; }


  /**
   * Sets the Version property
   *
   * @param strVersion
   */
  public void setVersion( String strVersion )
  { m_strVersion = strVersion;  }


  /**
   * Gets Version property
   *
   * @return The Version property
   */
  public String getVersion()
  { return m_strVersion;  }


  /**
   * Gets the language for all human readable information in the schema
   * @return the language for all human readable information in the schema
   */
  public String getLang()
  {  return m_strLang;  }


  /**
   * Sets the language for all human readable information in the schema
   * @param strLang The language abbreviation (i.e., english is en)
   */
  public void setLang( String strLang )
  { m_strLang = strLang; }

  // *** Schema factory methods to create the schema component types

  /**
   * Creates the All component
   * @return
   */
  public All createAll()
  { return (All)checkForSchemaNamespace( new VwAllImpl(), "all" ); }

 
  /**
   * Creates the Annotaion component
   * @return
   */
  public Annotation createAnnotation()
  {  return  (Annotation)checkForSchemaNamespace( new VwAnnotationImpl(), "annotation" ); }

  /**
   * Creats the Any component
   * @return
   */
  public Any createAny()
  { return (Any)checkForSchemaNamespace( new VwAnyImpl(), "any" ); }

  /**
   * Creates the AppInfo component
   * @return
   */
  public AppInfo createAppInfo()
  { return (AppInfo)checkForSchemaNamespace(  new VwAppInfoImpl(), "appInfo" ); }

  /**
   * Creates the Attribute
   * @return
   */
  public Attribute createAttribute()
  { return (Attribute)checkForSchemaNamespace( new VwAttributeImpl(), "attribute" ); }

  /**
   * Creates the Attribute initialized with a name and type
   * 
   * @param strName the attribute's name
   * @param strType The attribute's data type (must be a QName)
   * @return
   */
  public Attribute createAttribute( String strName, String strType )
  { return  (Attribute)checkForSchemaNamespace( new VwAttributeImpl( strName, strType ), "attribute" ); }
  
  

  /**
   * Creates the AttributeGroup
   * @return
   */
  public AttributeGroup createAttributeGroup()
  { return (AttributeGroup)checkForSchemaNamespace( new VwAttributeGroupImpl(), "attributeGroup" ); }

  /**
   * Creates the Choice
   * @return
   */
  public Choice createChoice()
  { return (Choice)checkForSchemaNamespace( new VwChoiceImpl(), "choice" ); }


  /**
   * Creates a ComplexContent
   * @return
   */
  public ComplexContent createComplexContent()
  { return (ComplexContent)checkForSchemaNamespace( new VwComplexContentImpl(), "complexContent" ); }

  /**
   * Creates a ComplexExtension
   * @return
   */
  public ComplexExtension createComplexExtension()
  { return (ComplexExtension)checkForSchemaNamespace( new VwComplexExtensionImpl(), "extension" ); }

  /**
   * Creates a ComplexRestriction
   * @return
   */
  public ComplexRestriction createComplexRestriction()
  { return (ComplexRestriction)checkForSchemaNamespace( new VwComplexRestrictionImpl(), "restriction" ); }


  /**
   * Creates a ComplexType
   * @return
   */
  public ComplexType createComplexType()
  { return (ComplexType)checkForSchemaNamespace( new VwComplexTypeImpl(), "complexType" ); }

  /**
   * Creates a ComplexType initialized with a name
   * 
   * @param strName The name of the complex type
   * @return The newely created complexType
   */
  public ComplexType createComplexType( String strName )
  { return (ComplexType)checkForSchemaNamespace( new VwComplexTypeImpl( strName ), "complexType" ); }
  

  /**
   * Creates the Documentation
   * @return
   */
  public Documentation createDocumentaion()
  { return (Documentation)checkForSchemaNamespace( new VwDocumentationImpl(), "documentation" ); }

  /**
   * Creates the Element
   * @return
   */
  public Element createElement()
  { return (Element)checkForSchemaNamespace( new VwElementImpl(), "element" ); }

  
  /**
   * Constructs with an initialized name and type
   * 
   * @param strName The element's name
   * @param strType the elements type (QName)
   */
  public Element createElement( String strName, String strType )
  { return (Element)checkForSchemaNamespace( new VwElementImpl( strName, strType ), "element" ); }
  
  /**
   * Creates the Enumeration
   * @return
   */
  public Enumeration createEnumeration()
  { return (Enumeration)checkForSchemaNamespace( new VwEnumerationImpl(), "enumeration" ); }

  /**
   * Creates the Extension
   * @return
   */
  public Extension createExtension()
  { return (Extension)checkForSchemaNamespace( new VwExtensionImpl(), "extension" ); }


  /**
   * Creates the FractionDigits
   * @return
   */
  public FractionDigits createFractionDigits()
  { return (FractionDigits)checkForSchemaNamespace( new VwFractionDigitsImpl(), "fractionDigits" ); }

  /**
   * Creates the Group
   * @return
   */
  public Group createGroup()
  { return (Group)checkForSchemaNamespace( new VwGroupImpl(), "group" ); }

  /**
   * Creates the Import
   * @return
   */
  public Import createImport()
  { return (Import)checkForSchemaNamespace( new VwImportImpl(), "import" ); }


  /**
   * Creates the Include
   * @return
   */
  public Include createInclude()
  { return (Include)checkForSchemaNamespace( new VwIncludeImpl(), "include" ); }


  /**
   * Creates the Length
   * @return
   */
  public Length createLength()
  { return (Length)checkForSchemaNamespace( new VwLengthImpl(), "length" ); }


  /**
   * Creates the List
   * @return
   */
  public javax.xml.schema.List createList()
  { return (javax.xml.schema.List)checkForSchemaNamespace( new VwListImpl(), "list" ); }


  /**
   * Creates the MinInclusive
   * @return
   */
  public MinInclusive createMinInclusive()
  { return (MinInclusive)checkForSchemaNamespace( new VwMinInclusiveImpl(), "minInclusive" ); }


  /**
   * Creates the MaxExclusive
   * @return
   */
  public MaxExclusive createMaxExclusive()
  { return (MaxExclusive)checkForSchemaNamespace( new VwMaxExclusiveImpl(), "maxExclusive" ); }

  /**
   * Creates the MaxLength
   * @return
   */
  public MaxLength createMaxLength()
  { return (MaxLength)checkForSchemaNamespace( new VwMaxLengthImpl(), "maxLength" ); }

  /**
   * Creates the MinLength
   * @return
   */
  public MinLength createMinLength()
  { return (MinLength)checkForSchemaNamespace( new VwMinLengthImpl(), "minLength" ); }


  /**
   * Creates the Pattern
   * @return
   */
  public Pattern createPattern()
  { return (Pattern)checkForSchemaNamespace( new VwPatternImpl(), "pattern" ); }


  /**
   * Creates the Restriction
   * @return
   */
  public Restriction createRestriction()
  { return (Restriction)checkForSchemaNamespace( new VwRestrictionImpl(), "restriction" ); }


  /**
   * Creates the Sequence
   * @return
   */
  public Sequence createSequence()
  { return (Sequence)checkForSchemaNamespace( new VwSequenceImpl(), "sequence" ); }


  /**
   * Creates the SimpleContent
   * @return
   */
  public SimpleContent createSimpleContent()
  { return (SimpleContent)checkForSchemaNamespace( new VwSimpleContentImpl(), "simpleContent" ); }


  /**
   * Creates the SimpleType
   * @return
   */
  public SimpleType createSimpleType()
  { return (SimpleType)checkForSchemaNamespace( new VwSimpleTypeImpl(), "simpleType" ); }


  /**
   * Creates the TotalDigits
   * @return
   */
  public TotalDigits createTotalDigits()
  { return (TotalDigits)checkForSchemaNamespace( new VwTotalDigitsImpl(), "totalDigits" ); }


  /**
   * Creates the Union
   * @return
   */
  public Union createUnion()
  { return (Union)checkForSchemaNamespace( new VwUnionImpl(), "union" ); }


  /**
   * Creates the WhiteSpace
   * @return
   */
  public WhiteSpace createWhiteSpace()
  { return (WhiteSpace)checkForSchemaNamespace( new VwWhiteSpaceImpl(), "whiteSpace" ); }


  /**
   * Checks to see if a namespace was defined for the schema document, and if so sets the QName for each of the schema
   * elements when they are created
   * 
   * @param The elements superclass that takes a QName
   * @param strLocalPart the local part name of the element
   * @return
   */
  private Object checkForSchemaNamespace( SchemaCommon schemaObj, String strLocalPart )
  {
    if ( m_nsSchema != null )
    {
      schemaObj.setQName( new QName( m_nsSchema, strLocalPart ) );
    }
    
    return schemaObj;
    
  } // end checkForSchemaNamespace()
  
  
  private Object getSchemaContent( String strKey )
  {
    int ndx = strKey.indexOf( ':' );
    
    if ( ndx > 0 )
    {
      /* revisit this
      Namespace ns = this.getNamespaceByPrefix( strKey.substring( 0, ndx ));
      if ( ns == null )
        return null;
      
      if ( m_strTargetNamespace != null && m_strTargetNamespace.equals( ns.getURI() ))
      */
      
      return m_mapContent.get( strKey.substring( ++ndx ) );
    }
    
    return m_mapContent.get( strKey );
    
  } // end getSchemaContent()
  
  private static void buildTypeMaps()
  {
    
    // Schema to java primitive type mappings
    s_mapPrimSchemaTypes.put( "string", "String" );
    s_mapPrimSchemaTypes.put( "boolean", "boolean" );
    s_mapPrimSchemaTypes.put( "byte", "byte" );
    s_mapPrimSchemaTypes.put( "unsignedByte", "byte" );
    s_mapPrimSchemaTypes.put( "short", "short" );
    s_mapPrimSchemaTypes.put( "unsignedShort", "short" );
    s_mapPrimSchemaTypes.put( "integer", "int" );
    s_mapPrimSchemaTypes.put( "int", "int" );
    s_mapPrimSchemaTypes.put( "unsignedInt", "int" );
    s_mapPrimSchemaTypes.put( "positiveInteger", "int" );
    s_mapPrimSchemaTypes.put( "nonPositiveInteger", "int" );
    s_mapPrimSchemaTypes.put( "nonNegativeInteger", "int" );
    s_mapPrimSchemaTypes.put( "negativeInteger", "int" );
    s_mapPrimSchemaTypes.put( "long", "long" );
    s_mapPrimSchemaTypes.put( "unsignedLong", "long" );
    s_mapPrimSchemaTypes.put( "float", "float" );
    s_mapPrimSchemaTypes.put( "double", "double" );
    s_mapPrimSchemaTypes.put( "decimal", "double" );
    s_mapPrimSchemaTypes.put( "duration", "double" );
    s_mapPrimSchemaTypes.put( "date", "String" );
    s_mapPrimSchemaTypes.put( "time", "String" );
    s_mapPrimSchemaTypes.put( "dateTime", "String" );
    s_mapPrimSchemaTypes.put( "ID", "String" );
    s_mapPrimSchemaTypes.put( "IDREF", "String" );
    s_mapPrimSchemaTypes.put( "QNAME", "String" );
    s_mapPrimSchemaTypes.put( "ENTITY", "String" );
    s_mapPrimSchemaTypes.put( "object", "Object" );

    // Java to schema type mappings
    s_mapJavaTypes.put( "String", "string"  );
    s_mapJavaTypes.put( String.class, "string"  );
    s_mapJavaTypes.put( String[].class, "string"  );
    s_mapJavaTypes.put( "boolean", "boolean" );
    s_mapJavaTypes.put( boolean[].class, "boolean" );
    s_mapJavaTypes.put( Boolean.class, "boolean" );
    s_mapJavaTypes.put( Boolean[].class, "boolean" );
    s_mapJavaTypes.put( Boolean.TYPE, "boolean" );
    s_mapJavaTypes.put( "byte", "byte" );
    s_mapJavaTypes.put( byte[].class, "byte" );
    s_mapJavaTypes.put( Byte.class, "byte" );
    s_mapJavaTypes.put( Byte[].class, "byte" );
    s_mapJavaTypes.put( Byte.TYPE, "byte" );
    s_mapJavaTypes.put( "short", "short" );
    s_mapJavaTypes.put( short[].class, "short" );
    s_mapJavaTypes.put( Short.class, "short" );
    s_mapJavaTypes.put( Short[].class, "short" );
    s_mapJavaTypes.put( Short.TYPE, "short" );
    s_mapJavaTypes.put( "int", "int" );
    s_mapJavaTypes.put( int[].class, "int" );
    s_mapJavaTypes.put( Integer.class, "int" );
    s_mapJavaTypes.put( Integer[].class, "int" );
    s_mapJavaTypes.put( Integer.TYPE, "int" );
    s_mapJavaTypes.put( "long", "long" );
    s_mapJavaTypes.put( long[].class, "long" );
    s_mapJavaTypes.put( "float", "float" );
    s_mapJavaTypes.put( BigInteger.class, "long" );
    s_mapJavaTypes.put( BigDecimal.class, "double" );
    s_mapJavaTypes.put( BigInteger[].class, "long" );
    s_mapJavaTypes.put( BigDecimal[].class, "double" );
    s_mapJavaTypes.put( Long.class, "long" );
    s_mapJavaTypes.put( Long[].class, "long" );
    s_mapJavaTypes.put( Long.TYPE, "long" );
    s_mapJavaTypes.put( "float", "float" );
    s_mapJavaTypes.put( float[].class, "float" );
    s_mapJavaTypes.put( Float.class, "float" );
    s_mapJavaTypes.put( Float[].class, "float" );
    s_mapJavaTypes.put( Float.TYPE, "float" );
    s_mapJavaTypes.put( "double", "double" );
    s_mapJavaTypes.put( double[].class, "double" );
    s_mapJavaTypes.put( Double.class, "double" );
    s_mapJavaTypes.put( Double[].class, "double" );
    s_mapJavaTypes.put( Double.TYPE, "double" );
    s_mapJavaTypes.put( Date.class, "dateTime" );
    s_mapJavaTypes.put( Date[].class, "dateTime" );
    s_mapJavaTypes.put( Timestamp.class, "dateTime" );
    s_mapJavaTypes.put( Timestamp[].class, "dateTime" );

  } // end buildTypeMaps()
  
  public String toString()
  {
    VwSchemaWriterImpl schemaWriter = new VwSchemaWriterImpl();
    try
    {
      return schemaWriter.writeSchema( this );
    }
    catch( Exception ex )
    {
      ex.printStackTrace();
    }

    return null;
  }
} // end class VwSchemaImpl{}

// *** End of VwSchemaImpl.java ***
