/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwAttributeHelper.java

Create Date: Apr 11, 2006
============================================================================================
*/
package com.vozzware.xml.schema.util;

import javax.xml.schema.AnyAttribute;
import javax.xml.schema.Attribute;
import javax.xml.schema.AttributeGroup;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This class manages Attribute, AttributeGroup and AnyAttribute content
 */
public class VwAttributeHelper
{
  private List                  m_listAttrGroups = new ArrayList();

  private List                  m_listAttributes = new ArrayList();

  private AnyAttribute          m_anyAttribute;

  /**
   * Adds an Attribute to this complexType
   *
   * @param attribute The attribute to add
   */
  public void addAttribute( Attribute attribute )
  { m_listAttributes.add( attribute ); }

  /**
   * Removes the specified attribute from the complexType's attribute list
   *
   * @param attribute The attribute object to remove
   */
  public void removeAttribute( Attribute attribute )
  { m_listAttributes.remove( attribute ); }


  /**
   * Removes the specified attribute from the complexType's attribute list
   *
   * @param strAttrName The name of the attribute to remove
   */
  public void removeAttribute( String strAttrName )
  {
    for ( Iterator iAttrs = m_listAttributes.iterator(); iAttrs.hasNext(); )
    {
      Attribute attr = (Attribute)iAttrs.next();

      if ( attr.getName().equalsIgnoreCase( strAttrName) )
      {
        iAttrs.remove();
        return;
      }
    } // end for()

  } // end removeAttribute()

  /**
   * Adds an AttributeGroup to this complexType
   *
   * @param attributeGroup The attribute group to add
   */
  public void addAttributeGroup( AttributeGroup attributeGroup )
  { m_listAttrGroups.add( attributeGroup ); }


  /**
   * Removes the specified attribute group from the content list
   *
   * @param attributeGroup The Attribute group object to remove
   */
  public void removeAttributeGroup( AttributeGroup attributeGroup )
  { m_listAttrGroups.remove( attributeGroup ); }

  /**
   * Removes the specified attribute group from the content list
   *
   * @param strName The name of the AttributeGroup object to remove
   */
  public void removeAttributeGroup( String strName )
  {
    for ( Iterator iGroups = m_listAttrGroups.iterator(); iGroups.hasNext(); )
    {
      AttributeGroup attrGroup = (AttributeGroup)iGroups.next();

      if ( attrGroup.getName().equalsIgnoreCase( strName) )
      {
        iGroups.remove();
        return;
      }
    } // end for()

  }

  /**
   * Adds an AttributeGroup to this complexType
   * @param anyAttribute The attribute to add
   */
  public void setAnyAttribute( AnyAttribute anyAttribute )
  { m_anyAttribute = anyAttribute; }


  /**
   * Returns the AnyAttribute object is one was defined
   * @return the AnyAttribute object is one was defined or null
   */
  public AnyAttribute getAnyAttribute()
  { return m_anyAttribute; }


  /**
   * Gets a List of AttributeGroup objects
   *
   * @return  an List of AttributeGroup objects - may be empty
   */
  public List getAttrGroups()
  { return m_listAttrGroups; }


  /**
   * Gets a List of Attribute objects
   *
   * @return  an List of Attribute objects - may be empty
   */
  public List getAttributes()
  { return m_listAttributes; }


  /**
   * Gets a List of all Attribute and AttributeGroup objects
   * @return a List of all Attribute and AttributeGroup objects
   */
  public List getAllAttrContent()
  {
    List listAttrContent = new ArrayList();
    listAttrContent.addAll( m_listAttributes );
    listAttrContent.addAll( m_listAttrGroups );

    if ( m_anyAttribute != null )
      listAttrContent.add(  m_anyAttribute );

    return listAttrContent;

  }  // end getAllAttrContent()

  /**
   * Returns true if there are attribute, attributeGroup or AnyAttribute objects defined for this complexType
   */
  public boolean hasAttributes()
  { return (m_listAttributes.size() > 0 || m_listAttrGroups.size() > 0 || m_anyAttribute != null); }


} // end class VwAttributeHelper{}

// *** End of VwAttributeHelper.java ***

