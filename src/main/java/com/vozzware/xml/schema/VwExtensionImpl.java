/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwExtensionImpl.java

Create Date: Apr 11, 2006
============================================================================================
*/
package com.vozzware.xml.schema;

import com.vozzware.xml.schema.util.VwAttributeHelper;

import javax.xml.schema.All;
import javax.xml.schema.AnyAttribute;
import javax.xml.schema.Attribute;
import javax.xml.schema.AttributeGroup;
import javax.xml.schema.Choice;
import javax.xml.schema.ComplexType;
import javax.xml.schema.Extension;
import javax.xml.schema.Group;
import javax.xml.schema.ModelGroup;
import javax.xml.schema.Schema;
import javax.xml.schema.Sequence;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Mar 21, 2004
 * Time: 6:05:33 AM
 * To change this template use File | Settings | File Templates.
 */
public class VwExtensionImpl extends VwSchemaCommonImpl implements Extension
{
  private VwAttributeHelper  m_attrHelper = new VwAttributeHelper();
  private Object              m_objContent;

  private String              m_strBase;

  public void setSchema( Schema schema )
  { m_schema = schema; }
  
  /**
   * Sets the base attribute value
   * @param strBase the base attribute value
   */
  public void setBase( String strBase )
  { m_strBase = strBase; }


  /**
   * Gets the base attribute value
   * @return the base attribute value
   */
  public String getBase()
  { return m_strBase; }

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
  public void setAnyAttribute( AnyAttribute anyAttribute)
  { m_attrHelper.setAnyAttribute( anyAttribute ); }


  /**
   * Returns the AnyAttribute object is one was defined
   * @return the AnyAttribute object is one was defined or null
   */
  public AnyAttribute getAnyAttribute()
  { return m_attrHelper.getAnyAttribute(); }


  /**
   * Gets a Listof AttributeGroup objects
   *
   * @return  an List of Attribute Groups
   */
  public List getAttrGroups()
  { return m_attrHelper.getAttrGroups(); }


  /**
   * Gets a List of Attribute  objects
   *
   * @return  a List of Attribute objects
   */
  public List getAttibutes()
  { return m_attrHelper.getAttributes(); }

  
  /**
   * Gets a List of all Attribute and AttributeGroup objects
   * @return a List of all Attribute and AttributeGroup objects
   */
  public List getAllAttrContent()
  { return getAllAttrContent( true ); }

  /**
   * Gets a List of all Attribute and AttributeGroup objects
   * @return a List of all Attribute and AttributeGroup objects
   */
  public List getAllAttrContent( boolean fIncludeParent )
  { 
    
    List listAttrs = new ArrayList();
    if ( m_schema == null )
      return listAttrs;
    
    if ( fIncludeParent )
    {
      String strBase = getBase();
      
      int nPos = strBase.indexOf(  ':' );
      
      if ( nPos > 0 )
        strBase = strBase.substring( ++nPos );
      
      Object objBase = m_schema.getComplexObject( strBase );
      if ( objBase instanceof ComplexType)
      {
        List listTemp = ((ComplexType)objBase).getAttributes();
        if ( listTemp != null )
          listAttrs.addAll( listTemp );
        
      }
      
    } // end if
    
    List listTemp = m_attrHelper.getAllAttrContent();
    
    if ( listTemp != null )
      listAttrs.addAll( listTemp );
    
    if ( listAttrs.size() > 0 )
      return listAttrs;
    
    return null;
    
    
  }

  /**
   * Returns true if there are attribute or attributeGroup objects defined for this complexType
   */
  public boolean hasAttributes()
  { return m_attrHelper.hasAttributes(); }

  /**
   * Returns true if this complex content defines only attributes
   * @return true if this complex content defines only attributes false otherwise
   */
  public boolean isAttributeOnly()
  {
    if ( m_attrHelper.hasAttributes() && m_objContent == null )
      return true;

    return false;

  } // end isAttributeOnly

  /**
   * Sets the  m_btModel group content type to All
   *
   * @param groupAll The all m_btModel group content object
   *
   */
  public void setAll( All groupAll )
  { m_objContent = groupAll;  }


  /**
   * Gets the All m_btModel group content
   * @return the All m_btModel group content if the content type is All else null is returned
   */
  public All getAll()
  {
    if ( m_objContent instanceof All )
      return (All)m_objContent;

    return null;

  } // end getAll()


  /**
   * Returns true if the content type is the m_btModel group All
   * @return true if the content type is the m_btModel group All
   */
  public boolean isAllContent()
  { return (m_objContent instanceof All ); }


  /**
   * Sets the  m_btModel group content type to Choice
   *
   * @param groupChoice The Choice m_btModel group content object
   *
   */
  public void setChoice( Choice groupChoice )
  { m_objContent = groupChoice;  }


  /**
   * Gets the Choice m_btModel group content
   * @return the Choice m_btModel group content if the content type is Choice else null is returned
   */
  public Choice getChoice()
  {
    if ( m_objContent instanceof Choice )
      return (Choice)m_objContent;

    return null;

  } // end getAll()


  /**
   * Returns true if the content type is the m_btModel group Choice
   * @return true if the content type is the m_btModel group Choice
   */
  public boolean isChoiceContent()
  { return (m_objContent instanceof Choice ); }


  /**
   * Sets the  m_btModel group content type to Sequence
   *
   * @param groupSequence The Sequence m_btModel group content object
   *
   */
  public void setSequence( Sequence groupSequence )
  { m_objContent = groupSequence;  }


  /**
   * Gets the Sequence m_btModel group content
   * @return the Sequence m_btModel group content if the content type is Sequence else null is returned
   */
  public Sequence getSequence()
  {
    if ( m_objContent instanceof Sequence )
      return (Sequence)m_objContent;

    return null;

  } // end get()


  /**
   * Returns true if the content type is the m_btModel group Sequence
   * @return true if the content type is the m_btModel group Sequence
   */
  public boolean isSequenceContent()
  { return (m_objContent instanceof Sequence ); }


  /**
   * Sets the  m_btModel group content type to Group
   *
   * @param groupGroup The all m_btModel group content object
   *
   */
  public void setGroup( ModelGroup groupGroup )
  { m_objContent = groupGroup;  }


  /**
   * Gets the Group m_btModel group content
   * @return the Group m_btModel group content if the content type is Group else null is returned
   */
  public ModelGroup getGroup()
  {
    if ( m_objContent instanceof ModelGroup )
      return (ModelGroup)m_objContent;

    return null;

  } // end getGroup()


  /**
   * Returns true if the content for this complexType is group, all, choice or sequence

   * @return true if the content for this complexType is group, all, choice or sequence
   */
  public boolean isModelGroup()
  { return (m_objContent instanceof ModelGroup); }

  /**
   * Returns true if the content type is the m_btModel group Group
   * @return true if the content type is the m_btModel group Group
   */
  public boolean isGroupContent()
  { return (m_objContent instanceof Group ); }


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

    if ( this.getAnnotation() != null )
      listContent.add( getAnnotation() );

    if ( m_objContent != null )
      listContent.add( m_objContent );

    listContent.addAll( this.getAllAttrContent() );

    return listContent;

  } //end getContent()


} // end class VwExtensionImpl{}

// *** End of VwExtensionImpl.java ***

