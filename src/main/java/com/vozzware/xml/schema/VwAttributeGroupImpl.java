/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwAttributeGroupImpl.java

Create Date: Apr 11, 2006
============================================================================================
*/
package com.vozzware.xml.schema;

import javax.xml.schema.AnyAttribute;
import javax.xml.schema.Attribute;
import javax.xml.schema.AttributeGroup;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This represents the XML Schema attributeGroup element
 */
public class VwAttributeGroupImpl extends VwSchemaCommonImpl implements AttributeGroup
{
  private List          m_listContent = new LinkedList();

  private Map           m_mapContent = new HashMap();

  private AnyAttribute  m_anyAttribute;

  private String        m_strRef;

  /**
   * Adds and AttributeGroup to this content
   *
   * @param attrGroup The AttributeGroup to add
   */
  public void addAttributeGroup( AttributeGroup attrGroup )
  {
    m_listContent.add(  attrGroup );
    m_mapContent.put( attrGroup.getName(), attrGroup );

  }


  /**
   * Gets the specified AttributeGroup
   *
   * @param strName The name of the AttributeGroup to get
   *
   * @return the specified AttributeGroup or null if named attribute does not exist
   */
  public AttributeGroup getAttributeGroup( String strName )
  { return (AttributeGroup)m_mapContent.get(  strName ); }

  /**
   * Removes the specified AttributeGroup object from this content
   *
   * @param attrGroup The AttributeGroup object to remove
   */
  public void removeAttributeGroup( AttributeGroup attrGroup )
  { m_listContent.remove( attrGroup ); }


  /**
   * Removes the specified AttributeGroup object from this content
   *
   * @param strName The name of AttributeGroup object to remove
   */
  public void removeAttributeGroup( String strName )
  {
    Object objContent = m_mapContent.get(  strName );

    if ( objContent != null )
    {
      m_mapContent.remove( strName );
      m_listContent.remove( objContent );
    }
  }

  /**
   * Adds an Attribute object to this content
   *
   * @param attr The Attribute to add
   */
  public void addAttribute( Attribute attr )
  {

    if ( !m_mapContent.containsKey( attr.getName() ) )
    {
      m_listContent.add( attr );
      m_mapContent.put( attr.getName(), attr );
    }

  }

  /**
   * Gets the specified Attribute
   *
   * @param strName The name of the Attribute to get
   *
   * @return the specified Attribute or null if named attribute does not exist
   */
  public Attribute getAttribute( String strName )
  {  return (Attribute)m_mapContent.get( strName ); }

  /**
   * Removes the specified Attribute object from this content
   *
   * @param attr The Attribute object to remove
   */
  public void removeAttribute( Attribute attr )
  {
    Object objContent = m_mapContent.get( attr.getName() );

    if ( objContent != null )
    {
      m_mapContent.remove( attr.getName() );
      m_listContent.remove( objContent );
    }

  }

  /**
   * Removes the specified Attribute object from this content
   *
   * @param strName The name Attribute object to remove
   */
  public void removeAttribute( String strName )
  {
    Object objContent = m_mapContent.get( strName );

    if ( objContent != null )
    {
      m_mapContent.remove( strName );
      m_listContent.remove( objContent );
    }
  }

  /**
   * Gets a List of all of the AttributeGroup and Attribute objects
   *
   * @return a List of all of the AttributeGroup and Attribute objects defined
   */
  public List getAttributes()
  { return m_listContent; }

  /**
   * Sets the AnyAttribute
   * @param anyAttribute The AnyAttribyte type (only one allowed)
   */
  public void setAnyAttribute( AnyAttribute anyAttribute )
  { m_anyAttribute = anyAttribute; }

  /**
   * Gets the AnyAttribute type
   * @return he AnyAttribute type or null if none defined
   */
  public AnyAttribute getAnyAttribute()
  { return m_anyAttribute; }


  /**
   * Sets the Ref property
   *
   * @param strRef The reference name
   */
  public void setRef( String strRef )
  { m_strRef = strRef; }

  /**
   * Gets Ref property
   *
   * @return  The Ref property
   */
  public String getRef()
  { return m_strRef; }

  /**
   * Gets a list of content defined for this AttributeGroup in the following order:
   * Annotation, AttributeGroup and Attributes, AnyAttribute
   *
   * @return a list of content defined for this AttributeGroup
   */
  public List getContent()
  {
    List listContent = new LinkedList();

    if ( getAnnotation() != null )
      listContent.add( getAnnotation() );

    listContent.addAll( m_listContent );

    if ( m_anyAttribute != null )
      listContent.add( m_anyAttribute );

    return listContent;


  } // end

} // end class VwAttributeGroupImpl{}

// *** End of VwAttributeGroupImpl.java ***
