/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwSimpleTypeImpl.java

Create Date: Apr 11, 2006
============================================================================================
*/

package com.vozzware.xml.schema;

import javax.xml.schema.Restriction;
import javax.xml.schema.SimpleType;
import javax.xml.schema.Union;
import java.util.LinkedList;
import java.util.List;

/**
 * This class implements the XML Schema SimpleType
 */
public class VwSimpleTypeImpl extends VwSchemaCommonImpl implements SimpleType
{

  private String      m_strFinal;

  private Object      m_objContent; // Either a schema list , restriction or union

  /**
   * Gets the final SimpleType attribute
   * @return the final SimpleType attribute or null if not defined
   */
  public String getFinal()
  { return m_strFinal; }


  /**
   * Sets the filan SimpleType attribute
   * @param strFinal  The final simple type attribute
   */
  public void setFinal( String strFinal )
  {  m_strFinal = strFinal;  }

  /**
   * Set a restriction content for this simpleType
   *
   * @param content The restriction content
   *
   */
  public void setRestriction( Restriction content )
  { m_objContent = content; }


  /**
   * Returns the Restriction content if the content type is a Restriction
   * @return the Restriction content if the content type is a Restriction else null is returned
   */
  public Restriction getRestriction()
  {
    if ( m_objContent instanceof Restriction  )
      return (Restriction)m_objContent;

    return null;

  } // end getRestriction()


  /**
   * Returns true if the content is a Restriction
   * @return
   */
  public boolean isRestriction()
  { return (m_objContent instanceof Restriction); }


  /**
   * Set a list content for this simpleType
   *
   * @param content The list content
   *
   */
  public void setList( javax.xml.schema.List content )
  { m_objContent = content; }



  /**
   * Returns the List content if the content type is a List
   * @return the List content if the content type is a List else null is returned
   */
  public javax.xml.schema.List getList()
  {
    if ( m_objContent instanceof javax.xml.schema.List  )
      return (javax.xml.schema.List)m_objContent;

    return null;

  } // end getList()

  /**
   * Returns true if the content is a schema List
   * @return
   */
  public boolean isList()
  { return (m_objContent instanceof javax.xml.schema.List); }

  /**
   * Set a union content for this simpleType
   *
   * @param content The union content
   */
  public void setUnion( Union content )
  {  m_objContent = content; }

  /**
   * Returns the Union content if the content type is a Union
   * @return the Union content if the content type is a Union else null is returned
   */
  public Union getUnion()
  {
    if ( m_objContent instanceof Union  )
      return (Union)m_objContent;

    return null;

  } // end getUnion()


  /**
   * Returns true if the content is a Union
   * @return
   */
  public boolean isUnion()
  { return (m_objContent instanceof Union); }

  /**
   * Gets a List of the content defined for this simple type
   *
   * @return Annotation (if defrined ) and one of the following: VwSchemaList, VwSchemaRestriction, VwSchemaUnion
   *  or and empty list if no content has been defined
   */
  public List getContent()
  {
    List listContent = new LinkedList();

    if ( getAnnotation() != null )
      listContent.add( getAnnotation() );

    if ( m_objContent != null )
      listContent.add( m_objContent );

    return listContent;
  } // end getContent()

  /**
   * Returns the base type if this is a restriction
   */
  public String getType()
  {
    if ( m_objContent instanceof Restriction )
      return ((Restriction)m_objContent).getBase();

    return null;

  } // end getType

} // *** End of class VwSimpleTypeImpl{}

// *** End Of VwSimpleTypeImpl.java