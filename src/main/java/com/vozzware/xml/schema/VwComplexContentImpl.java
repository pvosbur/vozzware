/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwComplexContentImpl.java

Create Date: Apr 11, 2006
============================================================================================
*/
package com.vozzware.xml.schema;

import javax.xml.schema.ComplexContent;
import javax.xml.schema.Extension;
import javax.xml.schema.ModelGroup;
import javax.xml.schema.Restriction;
import javax.xml.schema.Schema;
import java.util.LinkedList;
import java.util.List;

/**
 * This representsthe XML Schema complexContent element
 */
public class VwComplexContentImpl extends VwSchemaCommonImpl implements ComplexContent
{
  private boolean m_fMixed = false;

  private Object  m_objContent;

  public void setSchema( Schema schema )
  {
    m_schema = schema;
    
    if ( m_objContent instanceof VwExtensionImpl)
      ((VwExtensionImpl)m_objContent).setSchema( schema );
    
  }
  /**
   * Sets the mixed attribute property
   * @param fMixed true if content is mixed, false otherwise
   */
  public void setMixed( boolean fMixed )
  { m_fMixed = fMixed; }


  /**
   * Returns the state of the mixed property
   * @return
   */
  public boolean isMixed()
  { return m_fMixed; }

  /**
   * Returns true if the complexContent only defines attributes
   * @return true if the complexContent only defines attributes
   */
  public boolean isAttributeOnly()
  {

    if ( m_objContent instanceof Extension )
      return ((Extension) m_objContent).isAttributeOnly();

    return false;

  } // end isAttributeOnly()


  /**
   * Helper to return attributes
   * @return
   */
  public boolean hasAttributes()
  {
    if ( m_objContent instanceof Extension )
      return ((Extension) m_objContent).hasAttributes();

    return false;

  } // end hasAttributes()


  public List getAttributes( boolean fIncludeParent )
  {
    if ( m_objContent instanceof Extension )
      return ((Extension) m_objContent).getAllAttrContent( fIncludeParent );

    return null;
  }

  public List getAttributes()
  { return getAttributes( true ); }
  
  
  
  /**
   * Returns the superclass of the m_btModel group if one of all, choice, sequence or group
   * was defined
   *
   * @return the superclass of the m_btModel group if one of all, choice, sequence or group
   * was defined
   */
  public ModelGroup getModelGroup()
  {
    if ( m_objContent instanceof Extension )
      return ((Extension)m_objContent).getGroup();

    return null;

  }


  /**
   * Returns true if this complex content has a modelGroup
   * @return
   */
  public boolean hasModelGroup()
  {
    if ( m_objContent instanceof Extension )
      return ((Extension)m_objContent).isModelGroup();

    return false;

  }


  /**
   * Sets the Extension content type for this complexContent
   * @param extension The Extension object for this complexContent
   */
  public void setExtension( Extension extension )
  { m_objContent = extension; }


  /**
   * Gets the Extension content object if defined
   * @return the Extension content object if defined else null is returned
   */
  public Extension getExtension()
  {
    if ( m_objContent instanceof Extension )
      return  (Extension)m_objContent;

    return null;

  }

  /**
   * Returns true if the complexContent is an extension
   * @return true if the complexContent is an extension
   */
  public boolean isExtension()
  { return (m_objContent instanceof Extension ); }
  
  
  /**
   * Sets the Restriction content type for this complexContent
   * @param restriction The Restriction object for this complexContent
   */
  public void setRestriction( Restriction restriction )
  { m_objContent = restriction;  }


  /**
   * Gets the Restriction content object if defined
   * @return the Restriction content object if defined else null is returned
   */
  public Restriction getRestriction()
  {
    if ( m_objContent instanceof Restriction )
      return  (Restriction)m_objContent;

    return null;

  } // end getRestriction()

  /**
   * Returns true if the complexContent is an Restriction
   * @return true if the complexContent is an Restriction
   */
  public boolean isRestriction()
  { return (m_objContent instanceof Restriction ); }

  /**
   * Returns a List of the conent which may consist of an Annotation and either an ComplexExtension
   * or a ComplexRestriction
   * @return a List of the content which may consist of an Annotation and either an ComplexExtension
   * or a ComplexRestriction
   */
  public List getContent()
  {
    List listContent = new LinkedList();

    if ( this.getAnnotation() != null )
      listContent.add( this.getAnnotation() );

    if ( m_objContent != null )
      listContent.add( m_objContent );

    return listContent;

  } // end  getContent()


} // end class VwComplexContentImpl{}

// *** End of VwComplexContentImpl.java
