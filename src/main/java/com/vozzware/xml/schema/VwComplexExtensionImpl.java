/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwComplexExtensionImpl.java

Create Date: Apr 11, 2006
============================================================================================
*/
package com.vozzware.xml.schema;

import javax.xml.schema.All;
import javax.xml.schema.Choice;
import javax.xml.schema.ComplexExtension;
import javax.xml.schema.Group;
import javax.xml.schema.ModelGroup;
import javax.xml.schema.Schema;
import javax.xml.schema.Sequence;
import java.util.LinkedList;
import java.util.List;

/**
 * This represents 
 */
public class VwComplexExtensionImpl extends VwExtensionImpl implements ComplexExtension
{
   private Object   m_content;

   public void setSchema( Schema schema )
   { super.setSchema( schema ); }
   
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
   * @param modelGroup The all m_btModel group content object
   *
   */
  public void setGroup( ModelGroup modelGroup )
  { m_content = modelGroup;  }


  /**
   * Gets the Group m_btModel group content
   * @return the Group m_btModel group content if the content type is Group else null is returned
   */
  public ModelGroup getGroup()
  {
    if ( m_content instanceof Group )
      return (Group)m_content;

    return null;

  } // end getGroup()


  /**
   * Returns true if the content for this complexType is group, all, choice or sequence

   * @return true if the content for this complexType is group, all, choice or sequence
   */
  public boolean isModelGroup()
  { return (m_content instanceof ModelGroup); }

  /**
   * Returns true if the content type is the m_btModel group Group
   * @return true if the content type is the m_btModel group Group
   */
  public boolean isGroupContent()
  { return (m_content instanceof Group ); }


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

    if ( m_content != null )
      listContent.add( m_content );

    listContent.addAll( this.getAllAttrContent() );

    return listContent;

  } //end getContent()

} // end class VwComplexExtensionImpl{}

// *** End of VwComplexExtensionImpl.java ***

