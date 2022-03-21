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
 * the default object used in the VwDataObject for storing it's data elements.
 */
public class VwElement
{
  private String            m_strName;      // The element tag name
  private Object            m_objValue;     // The value of the element

  private Attributes        m_listAttr;      // A Map of any attributes defined for this element

  private Object            m_objChild;     // Either an VwDataObject or VwDataOBjList thst is
                                            // a child of this element

  /**
   * Constructor
   *
   * @param strName The element tag name
   * @param objValue The element value
   *
   */
  public VwElement( String strName, Object objValue )
  {
    m_strName = strName;
    m_objValue = objValue;
    m_listAttr = null;
    m_objChild = null;

  } // end VwElement


  /**
   * Constructor
   *
   * @param strName The element tag name
   * @param objValue The element value
   * @param listAttr The attribute list of VwXmlAttribute objects
   *
   */
  public VwElement( String strName, Object objValue, Attributes listAttr )
  {
    m_strName = strName;
    m_objValue = objValue;
    m_listAttr = listAttr;
    m_objChild = null;


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
   * Sets the element's value
   *
   * @return The element's value in a String
   */
  public void setValue( String objValue )
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
   * Sets a child object
   *
   * @param listChildElements The chile list of VwXnlElelment objects
   */
  public void setChildObject( VwDataObject dobjChild)
  { m_objChild = dobjChild; }


  /**
   * Sets a child object
   *
   * @param listChildElements The chile list of VwXnlElelment objects
   */
  public void setChildObject( VwDataObjList dobjChildList )
  { m_objChild = dobjChildList; }

  /**
   * Returns a List of any child elements
   */
  public Object getChildObject()
  { return m_objChild; }


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