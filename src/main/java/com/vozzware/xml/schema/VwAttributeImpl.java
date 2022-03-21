/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwAttributeImpl.java

Create Date: Apr 11, 2006
============================================================================================
*/
package com.vozzware.xml.schema;

import javax.xml.schema.Attribute;
import javax.xml.schema.SimpleType;
import java.util.LinkedList;
import java.util.List;

public class VwAttributeImpl extends VwSchemaCommonImpl implements Attribute
{
  private String      m_strDefault;
  private String      m_strFixed;
  private String      m_strForm;
  private String      m_strType;
  private String      m_strRef;
  private String      m_strUse;

  private SimpleType  m_simpleType;

  /**
   * Default constructor
   *
   */
  public VwAttributeImpl()
  { ; }
  
  /**
   * Constructor to initialize with a name and type
   * @param strName The attribute's name
   * @param strType  The attrubute's typw QName
   */
  public VwAttributeImpl( String strName, String strType )
  { 
    setName(strName);
    m_strType = strType;
    
  } // end VwAttributeImpl()

  public String toString()
  { return "name: " + getName() + ", type: " + m_strType; }
  
  public boolean isParent()
  { return m_simpleType != null; }
  /**
   * Gets the attribute's default value
   * @return the attribute's default value
   */
  public String getDefault()
  { return m_strDefault; }

  /**
   * Sets the attribute's default value
   * @param strDefault the attribute's default value
   */
  public void setDefault( String strDefault )
  { m_strDefault = strDefault; }

  /**
   * gets the attributes fixed value
   * @return
   */
  public String getFixed()
  { return m_strFixed; }


  /**
   * Sets thee attribute's fixed value
   * @param strFixed The fixed attribute value
   */
  public void setFixed( String strFixed )
  { m_strFixed = strFixed; }

  /**
   * Gets the attribute's from value
   * @return the attribute's from value
   */
  public String getForm()
  { return m_strForm; }

  /**
   * Sets the attribute's form value
   * @param strForm The attribute's form value
   */
  public void setForm( String strForm )
  { m_strForm = strForm; }

  /**
   * Gets the reference to a globally defined attribute
   * @return the reference to a globally defined attribute
   */
  public String getRef()
  { return m_strRef; }

  /**
   * Sets the reference to a globally defined attribute
   * @param strRef the reference to a globally defined attribute
   */
  public void  setRef( String strRef )
  { m_strRef = strRef; }

  /**
   * Gets the name of a built in schema type this attribute represents
   * @return The name of a built in schema type this attribute represents
   */
  public String getType()
  { return m_strType; }

  /**
   * Sets the name of a built in schema type this attribute represents
   * @param strType the name of a built in schema type this attribute represents
   */
  public void setType( String strType )
  { m_strType = strType; }

  /**
   * Gets the use attribute property
   * @return the use attribute property
   */
  public String getUse()
  { return m_strUse; }

  /**
   * Sets the use attribute property
   * @param strUse use attribute property
   */
  public void setUse( String strUse )
  { m_strUse = strUse; }

  /**
   * Gets the simpleType content for this attribute
   * @return the simpleType content for this attribute or null
   */
  public SimpleType getSimpleType()
  { return m_simpleType; }


  /**
   * Sets the simpleType content for this attribute
   * @param simpleType the simpleType content for this attribute
   */
  public void setSimpleType( SimpleType simpleType )
  { m_simpleType = simpleType; }

  /**
   * Gets List of all content
   * @return
   */
  public List getContent()
  {
    List listContent = new LinkedList();

    if ( getAnnotation() != null )
      listContent.add( getAnnotation() );

    if ( m_simpleType != null )
      listContent.add( m_simpleType );

    return listContent;

  } // end getContent()
} // end class VwAttributeImpl

// *** End of VwAttributeImpl.java ***
