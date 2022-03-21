/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwElement.java

Create Date: Apr 11, 2006
============================================================================================
*/

package com.vozzware.xml;

import org.xml.sax.Attributes;

/**
 * This class holds the data and attributes for an xml element (tag). This is also
 * the default object used in the VwXmlDataObj for storing it's data elements.
 */
public class VwXmlElement
{
  private String            m_strName;      // The element tag name
  private String            m_strQName;      // The element tag name
  private Object            m_objValue;     // The value of the element

  private Attributes        m_listAttr;     // A list of any attributes defined for this element

  /**
   * Constructor
   *
   * @param strName The element tag name
   * @param objValue The element value
   *
   */
  public VwXmlElement( String strQName, String strName, Object objValue )
  {
    m_strQName = strQName;
    m_strName = strName;
    m_objValue = objValue;
    m_listAttr = null;

  } // end VwElement


  /**
   * Constructor
   *
   * @param strName The element tag name
   * @param objValue The element value
   * @param listAttr The attribute list of VwXmlAttribute objects
   *
   */
  public VwXmlElement( String strQName, String strName, Object objValue, Attributes listAttr )
  {
    m_strQName = strQName;
    m_strName = strName;
    m_objValue = objValue;
    m_listAttr = listAttr;


  } // end VwElement



  // Accessors

  /**
   * Gets the element name
   *
   * @return The element name in a String
   */
  public String getName()
  { return m_strName; }


  /**
   * Gets the qualified name
   * @return
   */
  public String getQName()
  { return m_strQName; }

  /**
   * Gets the element's value converted to a string
   *
   * @return The element's value in a String
   */
  public String getValue()
  {
    if ( m_objValue == null )
      return null;

    return m_objValue.toString();

  } // end getValue()


  /**
   * Gets the element's value as an Object
   *
   * @return The element's value as an Object
   */
  public Object getObject()
  { return m_objValue; }

  /**
   * Gets the element name
   *
   * @return The element name in a String
   */
  public void setName( String strName )
  { m_strName = strName; }


  /**
   * Sets the element's value as a String
   *
   * @return The element's value in a String
   */
  public void setObject( String strValue )
  { m_objValue  = strValue; }


  /**
   * Sets the element's value as an Object
   *
   * @return The element's value in a String
   */
  public void setObject( Object objValue )
  { m_objValue  = objValue; }

  /**
   * Sets the element's attribute list
   *
   * @param a List containing VwAttribute objects
   */
  public void setAttributes( Attributes listAttr )
  { m_listAttr = listAttr; }

  /**
   * Gets the element's attribute map
   *
   * @return The element's attributes in a List or null if no attributes exist for this element
   */
  public Attributes getAttributes()
  { return m_listAttr; }


  /**
   * Test to see if this element has attributes
   * @return
   */
  public boolean hasAttributes()
  { return m_listAttr != null && m_listAttr.getLength() >0 ; }
  /**
   * Returns the value of the attribute if the attribute exists
   *
   * @param strAttr The name of the attribute to retrieve
   *
   * @return The value of the attribute or null if the attribute does not exist
   */
  public String getAttribute( String strAttr )
  {
    if ( m_listAttr == null )
      return null;            // No attributes for this element

    return m_listAttr.getValue( strAttr );

  } // end getAttribute()



  /**
   * Returns object (The element's values ) converted to a string
   */
  public String toString()
  { 
    if ( m_objValue == null )
      return  null;
    
    return m_objValue.toString();
  }


} // end class VwElement{}

// *** End of VwElement.java ***