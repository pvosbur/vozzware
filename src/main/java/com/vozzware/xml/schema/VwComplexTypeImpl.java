/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwComplexTypeImpl.java

Create Date: Apr 11, 2006
============================================================================================
*/

package com.vozzware.xml.schema;

import com.vozzware.xml.schema.util.VwAttributeHelper;

import javax.xml.schema.All;
import javax.xml.schema.Annotation;
import javax.xml.schema.AnyAttribute;
import javax.xml.schema.Attribute;
import javax.xml.schema.AttributeGroup;
import javax.xml.schema.Choice;
import javax.xml.schema.ComplexContent;
import javax.xml.schema.ComplexType;
import javax.xml.schema.Element;
import javax.xml.schema.Group;
import javax.xml.schema.ModelGroup;
import javax.xml.schema.Schema;
import javax.xml.schema.Sequence;
import javax.xml.schema.SimpleContent;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * This represents the XMl Schema complexType
 */
public class VwComplexTypeImpl extends VwSchemaCommonImpl implements ComplexType
{

  private Boolean               m_fAbstract;
  private Boolean               m_fMixed;
  private String                m_strBlock;
  private String                m_strFinal;

  private Object                m_content = null;

  private VwAttributeHelper    m_attrHelper = new VwAttributeHelper();

  // *** The following members set or get data from the class members ***

  
  /**
   * Default constructor
   */
  public VwComplexTypeImpl()
  { ; }
  
   
  /**
   * Constructs with an name 
   * @param strName The name of the complexType
   */
  public VwComplexTypeImpl( String strName )
  { setName( strName ); }
 
  
  public void setSchema( Schema schema )
  {
    m_schema = schema;
    
    if ( m_content instanceof VwComplexContentImpl )
      ((VwComplexContentImpl)m_content).setSchema( schema );
    
  }
  /**
   * Sets the Abstract property
   *
   * @param fAbstract
   */
  public void setAbstract( Boolean fAbstract )
  { m_fAbstract = fAbstract; }

  /**
   * Gets Abstract property
   *
   * @return  The Abstract property
   */
  public Boolean getAbstract()
  { return m_fAbstract; }


  /**
   * Sets the mixed property
   *
   * @param fMixed
   */
  public void setMixed( Boolean fMixed )
  { m_fMixed = fMixed; }


  /**
   * Gets Mixed property
   *
   * @return  The Abstract property
   */
  public Boolean getMixed()
  { return m_fMixed; }

  /**
   * Sets the Block property
   *
   * @param strBlock
   */
  public void setBlock( String strBlock )
  { m_strBlock = strBlock; }

  /**
   * Gets Block property
   *
   * @return  The Block property
   */
  public String getBlock()
  { return m_strBlock; }


  /**
   * Sets the Final property
   *
   * @param strFinal
   */
  public void setFinal( String strFinal )
  { m_strFinal = strFinal; }

  /**
   * Gets Final property
   *
   * @return  The Final property
   */
  public String getFinal()
  { return m_strFinal; }

  /**
   * Sets the object's content as SimpleContent
   *
   * @param simpleContent The simple content
   *
   */
  public void setSimpleContent( SimpleContent simpleContent )
  {  m_content = simpleContent;  }


  /**
   * Gets the SimpleContent content type if it exists
   * Returns the SimpleContent object if it exists
   *
   */
  public SimpleContent getSimpleContent()
  {
    if ( m_content instanceof SimpleContent )
      return (SimpleContent)m_content;

    return null;

  } // end getSimpleContent()


  /**
   * Returns true if this complexType is a simpleContent
   * @return  true if this complexType is a simpleContent
   */
  public boolean isSimpleContent()
  { return m_content instanceof SimpleContent; }


  /**
   * Sets the object's content as complex
   *
   * @param complexContent class holding the complex content
   *
   */
  public void setComplexContent( ComplexContent complexContent )
  { m_content = complexContent;  } // end setComplexContent


  /**
   * Gets the ComplexContent content type if it exists
   * Returns the ComplexContent object if it exists
   *
   */
  public ComplexContent getComplexContent()
  {
    if ( m_content instanceof ComplexContent )
      return (ComplexContent)m_content;

    return null;

  } // end getComplexContent()


  /**
   * Returns true if this complexType is a simpleContent
   * @return  true if this complexType is a simpleContent
   */
  public boolean isComplexContent()
  { return m_content instanceof ComplexContent; }


  /**
   * Sets the  m_btModel group content type to All
   *
   * @param groupAll The all m_btModel group content object
   *
   */
  public void setAll( All groupAll )
  { m_content = groupAll;  }


  /**
   * Gets the All m_btModel group content
   * @return the All m_btModel group content if the content type is All else null is returned
   */
  public All getAll()
  {
    if ( m_content instanceof All )
      return (All)m_content;

    return null;

  } // end getAll()


  /**
   * Returns true if the content type is the m_btModel group All
   * @return true if the content type is the m_btModel group All
   */
  public boolean isAllContent()
  { return (m_content instanceof All ); }


  /**
   * Sets the  m_btModel group content type to Choice
   *
   * @param groupChoice The Choice m_btModel group content object
   *
   */
  public void setChoice( Choice groupChoice )
  { m_content = groupChoice;  }


  /**
   * Gets the Choice m_btModel group content
   * @return the Choice m_btModel group content if the content type is Choice else null is returned
   */
  public Choice getChoice()
  {
    if ( m_content instanceof Choice )
      return (Choice)m_content;

    return null;

  } // end getAll()


  /**
   * Returns true if the content type is the m_btModel group Choice
   * @return true if the content type is the m_btModel group Choice
   */
  public boolean isChoiceContent()
  { return (m_content instanceof Choice ); }


  /**
   * Sets the  m_btModel group content type to Sequence
   *
   * @param groupSequence The Sequence m_btModel group content object
   *
   */
  public void setSequence( Sequence groupSequence )
  { m_content = groupSequence;  }


  /**
   * Gets the Sequence m_btModel group content
   * @return the Sequence m_btModel group content if the content type is Sequence else null is returned
   */
  public Sequence getSequence()
  {
    if ( m_content instanceof Sequence )
      return (Sequence)m_content;

    return null;

  } // end get()


  /**
   * Returns true if the content type is the m_btModel group Sequence
   * @return true if the content type is the m_btModel group Sequence
   */
  public boolean isSequenceContent()
  { return (m_content instanceof Sequence ); }


  /**
   * Sets the  m_btModel group content type to Group
   *
   * @param groupGroup The all m_btModel group content object
   *
   */
  public void setGroup( Group groupGroup )
  { m_content = groupGroup;  }


  /**
   * Gets the Group m_btModel group content
   * @return the Group m_btModel group content if the content type is Group else null is returned
   */
  public Group getGroup()
  {
    if ( m_content instanceof Group )
      return (Group)m_content;

    return null;

  } // end getGroup()


  /**
   * Returns true if the content type is the m_btModel group Group
   * @return true if the content type is the m_btModel group Group
   */
  public boolean isGroupContent()
  { return (m_content instanceof Group ); }


  /**
   * Return the data type if this has simple content
   */
  public String getType()
  {
    if ( m_content instanceof SimpleContent )
      return ((SimpleContent)m_content).getType();

    return null;

  } // end getType()


  /**
   * Gets a List of AttributeGroup objects
   *
   * @return  an List of AttributeGroup objects - may be empty
   */
  public List getAttrGroups()
  { return m_attrHelper.getAttrGroups(); }


  /**
   * Gets a List of Attribute objects
   *
   * @return  an List of Attribute objects - may be empty
   */
  public List getAttributes( boolean fIncludeParent )
  {
    if ( m_content instanceof ComplexContent )
     return ((VwComplexContentImpl)m_content).getAttributes( fIncludeParent );

    return m_attrHelper.getAllAttrContent();

  }
  
  /**
   * Helper method to return all Atrribute objects  whether they are in Attribute groups or
   * single Attribute definition as well as as any parent attributes if
   * this complexType is an extension or restrtriction
   *
   * @param fIncludeBase if true include any parent attributes or groups, false to only return attributes for this type
   * @return A List of all Attribute objects defined for this complexType
   */
  public List getAttributes()
  { return getAttributes( true ); }
  
 
  /**
   * Gets the named attribute
   * @param strAttrName The name of the attribute to get This uses the QName not the local name
   * 
   * @return The Attribute for name requested or null if it does not exist
   */
  public Attribute getAttribute( String strAttrName )
  {
    List listAttr = getAttributes();
    if ( listAttr == null )
      return null;
    
    
    for ( Iterator iattrs = listAttr.iterator(); iattrs.hasNext(); )
    {
      Attribute attr = (Attribute)iattrs.next();
      
      if ( attr.getName().equalsIgnoreCase( strAttrName ))
        return attr;
      
    }
    return null;
  }
  
  public Element findElement( String strElementName )
  {
    ModelGroup group = getModelGroup();
    if ( group == null )
      return null;
    
    List listGroupContent = group.getContent();
    
    return searchGroup( strElementName, listGroupContent );
    
    
  }
  private Element searchGroup( String strElementName, List listGroupContent )
  {
    
    if ( listGroupContent == null )
      return null;
    
    for ( Iterator iObjects = listGroupContent.iterator(); iObjects.hasNext(); )
    {
      Object obj = iObjects.next();
      
      if ( obj instanceof ModelGroup )
      {
        Element eleFound = searchGroup( strElementName, ((ModelGroup)obj ).getContent() );
        if ( eleFound != null )
          return eleFound;
      }
      else
      if ( obj instanceof Element )
      {
        if ( ((Element)obj).getName() != null && ((Element)obj).getName().equalsIgnoreCase( strElementName ))
          return (Element)obj;
      }
       
    }
    return null;
  }


  /**
   * Returns true if there are attributes or groups
   */
  public boolean hasAttributes()
  {
    if ( m_content instanceof ComplexContent )
      return ((ComplexContent)m_content).hasAttributes();
    
    return m_attrHelper.hasAttributes(); }



  /**
   * Returns true if the content is one of ComplexContent or ModelGroup
   */
  public boolean hasChildElements()
  {
    if ( m_content instanceof ModelGroup )
      return true;

    if ( m_content instanceof ComplexContent )
    {
      if ( ((ComplexContent)m_content).hasModelGroup() )
        return true;
    }

    return false;

  } // end hasChildElements()


  /**
   * Returns true if this complexType is an attribute definition only
   * @return
   */
  public boolean isAttributeOnly()
  {
    if ( hasAttributes() && m_content == null  )
      return true;

    if ( m_content instanceof ComplexContent )
      return ((ComplexContent)m_content).isAttributeOnly();

    return false;

  } // end isAttributeOnly()

  /**
   * Helper method to determine if this complex type has a choice,sequence or all group child
   *
   * @return true if this complex type has one of the m_btModel group elements:choice,sequence or all,
   * false otherwise
   */
  public boolean hasModelGroup()
  {
    if ( m_content instanceof ModelGroup )
      return true;

    if ( m_content instanceof ComplexContent )
      return ((ComplexContent)m_content).hasModelGroup();

    return false;

  } // end hasModelGroup()


  /**
   * Returns the ModelGroup for this element if child tags exist
   */
  public ModelGroup getModelGroup()
  {
    if ( m_content instanceof ModelGroup )
      return (ModelGroup)m_content;

    if ( m_content instanceof ComplexContent )
      return ((ComplexContent)m_content).getModelGroup();

    return null;

  } // end getGroup

  /**
   * Allows any type of m_btModel group to be set
   * @param group either an ALL,Sequence or Choice instance
   */
  public void setModelGroup( ModelGroup group )
  { m_content = group; }
  

  /**
   * Returns true if the content for this complexType is group, all, choice or sequence

   * @return true if the content for this complexType is group, all, choice or sequence
   */
  public boolean isModelGroup()
  { return (m_content instanceof ModelGroup); }


  /**
   * Adds an Attribute to this complexType
   *
   * @param attribute The attribute to add
   */
  public void addAttribute( Attribute attribute )
  { m_attrHelper.addAttribute( attribute ); }

  /**
   * Removes the specified attribute from the complexType's attribute list
   *
   * @param attribute The attribute object to remove
   */
  public void removeAttribute( Attribute attribute )
  { m_attrHelper.removeAttribute( attribute ); }



  /**
   * Removes the specified attribute from the complexType's attribute list
   *
   * @param strAttrName The name of the attribute to remove
   */
  public void removeAttribute( String strAttrName )
  { m_attrHelper.removeAttribute( strAttrName ); }


  /**
   * Adds an AttributeGroup to this complexType
   *
   * @param attributeGroup The attribute group to add
   */
  public void addAttributeGroup( AttributeGroup attributeGroup )
  { m_attrHelper.addAttributeGroup( attributeGroup ); }


  /**
   * Removes the specified attribute group from the content list
   *
   * @param attributeGroup The Attribute group object to remove
   */
  public void removeAttributeGroup( AttributeGroup attributeGroup )
  { m_attrHelper.removeAttributeGroup( attributeGroup ); }

  /**
   * Removes the specified attribute group from the content list
   *
   * @param strName The name of the AttributeGroup object to remove
   */
  public void removeAttributeGroup( String strName )
  { m_attrHelper.removeAttributeGroup( strName ); }


  /**
   * Adds an AttributeGroup to this complexType
   * @param anyAttribute The attribute to add
   */
  public void setAnyAttribute( AnyAttribute anyAttribute )
  { m_attrHelper.setAnyAttribute( anyAttribute ); }


  /**
   * Returns the AnyAttribute object is one was defined
   * @return the AnyAttribute object is one was defined or null
   */
  public AnyAttribute getAnyAttribute()
  { return m_attrHelper.getAnyAttribute(); }


  /**
   * Gets a List of all the content objects defined for this complexType
   *
   * @return  a List of the content objects defined for this complexType in the following order:
   * <br> Annotation (if defined), One of (SimpleContent,ComplexContent,All,Sequence,Choice,Group),
   * <br> All Attribute and AttributeGroup objects and AnyAttribute
   */
  public List getContent()
  {
    List listContent = new LinkedList();

    Annotation anno = getAnnotation();

    if ( anno != null )
      listContent.add( anno );

    if ( m_content != null )
      listContent.add( m_content );

    listContent.addAll( m_attrHelper.getAllAttrContent() );

    return listContent;

  } // end getContent()

} // *** End of class VwComplexTypeImpl{}

// *** End Of VwComplexTypeImpl.java