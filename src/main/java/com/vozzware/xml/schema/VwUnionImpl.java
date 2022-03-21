/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwUnionImpl.java

Create Date: Apr 11, 2006
============================================================================================
*/

package com.vozzware.xml.schema;

import javax.xml.schema.SimpleType;
import javax.xml.schema.Union;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * This class defines the XML Schema list
 */
public class VwUnionImpl extends VwSchemaCommonImpl implements Union
{
  private String                  m_strMemberTypes;

  private List                    m_listSimpleTypes = new LinkedList();


  /**
   * Sets the content's memberTypes property
   *
   * @param strMemberTypes
   */
  public void setMemberTypes( String strMemberTypes )
  { m_strMemberTypes = strMemberTypes; }

  /**
   * Gets MemberTypes property
   *
   * @return  The MemberTypes property
   */
  public String getMemberTypes()
  { return m_strMemberTypes; }



  /**
   * Adds a simpleType content to this list
   *
   * @param simpleType The simpleType content
   *
   */
  public void addSimpleType( SimpleType simpleType )
  {  m_listSimpleTypes.add( simpleType ); }

  /**
   * Removes the specified SimpleType from the list of simple types
   * @param simpleType The SimpleType instace to remove
   */
  public void removeSimpleType( SimpleType simpleType )
  { m_listSimpleTypes.remove( simpleType ); }


  /**
   * Removes the specified SimpleType from the list of simple types
   * @param strName The name of the globally defined SimpleType instace to remove
   */
  public void removeSimpleType( String strName )
  {
    for ( Iterator iTypes = m_listSimpleTypes.iterator(); iTypes.hasNext(); )
    {
      SimpleType simpleType = (SimpleType)iTypes.next();

      if ( simpleType.getName().equalsIgnoreCase( strName ) )
      {
        iTypes.remove();
        return;
      }

    } // end for()

  } // end removeSimpleType()

  /**
   * Gets a List of all the simple types defined
   * @return
   */
  public List getSimplTypes()
  { return m_listSimpleTypes; }


  /**
   * Gets an Iterator to the list of simpleTypes
   *
   * @return an Iterator to the list of simpleTypes
   */
  public List getContent()
  {
    List listContent = new LinkedList();

    if ( getAnnotation() != null )
      listContent.add( getAnnotation() );

    listContent.addAll( m_listSimpleTypes );

    return listContent;

  } // end getContent()



} // *** End of class VwUnionImpl{}

// *** End Of VwUnionImpl.java