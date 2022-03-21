/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwListImpl.java

Create Date: Apr 11, 2006
============================================================================================
*/

package com.vozzware.xml.schema;

import javax.xml.schema.SimpleType;
import java.util.LinkedList;
import java.util.List;

/**
 * This class defines the XML Schema list
 */
public class VwListImpl extends VwSchemaCommonImpl implements javax.xml.schema.List
{
  private String                  m_strItemType;

  private Object                  m_objContent; // Either a schema list , restriction or union


  /**
   * Sets the content's itemType
   *
   * @param strItemType
   */
  public void setItemType( String strItemType )
  { m_strItemType = strItemType; }


  /**
   * Gets the ItemType property
   *
   * @return  The ItemType property
   */
  public String getItemType()
  { return m_strItemType; }



  /**
   * Set a simpleType content for this list
   *
   * @param simpleType The simpleType content
   *
    */
  public void setSimpleType( SimpleType simpleType )
  {
     m_objContent = simpleType;

  } // end setContent()


  /**
   * Gets the SimpleType content if the content contains a SimpleType
   * @return the SimpleType content if the content contains a SimpleType else null is returned
   */
  public SimpleType getSimpleType()
  {
    if ( m_objContent instanceof SimpleType )
      return (SimpleType)m_objContent;

    return null;

  } // end getSimpleType()


  /**
   * Return true if the content contains a SimpleType
   * @return  true if the content contains a SimpleType false otherwise
   */
  public boolean isSimpleType()
  { return (m_objContent instanceof SimpleType); }


  /**
   * Gets the content object fro this simple type
   *
   * @return One of the following: VwSchemaList, VwSchemaRestriction, VwSchemaUnion
   *  or null if no content
   */
  public List getContent()
  {
    List listContent = new LinkedList();

    if ( getAnnotation() != null )
      listContent.add( getAnnotation() );

    if ( m_objContent != null )
      listContent.add( m_objContent );

    return listContent;

  }

} // *** End of class VwListImpl{}

// *** End Of VwListImpl.java