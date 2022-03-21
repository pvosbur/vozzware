/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwElementList.java

Create Date: Apr 11, 2006
============================================================================================
*/
package com.vozzware.xml;

import org.xml.sax.Attributes;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * This class holds a List of VwElement objects. It has helper methods for retrieving
 * the element values without the need for type casting.
 */
public class VwElementList extends LinkedList
{

  /**
   * Default constructor
   *
   */
  public VwElementList()
  { super(); }

  /**
   * Gets the element's value
   *
   * @param ndx The index position in the list
   *
   * @return The value of the element as a string at the index position in the list
   */
  public String getValue( int ndx )
  {
    VwElement element = (VwElement)get( ndx );

    if ( element == null )
      return null;

    return element.getValue();

  } // end getValue()


   /**
   * Gets the element's value as an object
   *
   * @param ndx The index position in the list
   *
   * @return The value of the element as a string at the index position in the list
   */
  public Object getObject( int ndx )
  {
    VwElement element = (VwElement)get( ndx );

    if ( element == null )
      return null;

    return element.getObject();

  } // end getObject()


  /**
   * Gets the element's name
   *
   * @param ndx The index position in the list
   *
   * @return The name of the element at the index position in the list
   */
  public String getName( int ndx )
  {
    VwElement element = (VwElement)get( ndx );

    if ( element == null )
      return null;

    return element.getName();

  } // end getColName()


  /**
   * Gets the elements attribute list for the position specified
   *
   * @param ndx The index position in the list
   *
   * @return The VwAttribute object at the index position in the list
   *
   */
  public Attributes getAttributeList( int ndx )
  {
    VwElement element = (VwElement)get( ndx );

    if ( element == null )
      return null;

    return element.getAttributes();

  } // end getAttributeList()

  /**
   * Gets The VwElement object at the list position requested
   *
   * @param ndx The index position in the list
   *
   * @return The VwElement object at the index position in the list
   */
  public VwElement getElement( int ndx )
  {
    VwElement element = (VwElement)get( ndx );

    if ( element == null )
      return null;

    return element;

  } // end getElement()


  /**
   * Finds an VwElement in the list that matches an attribute name and value
   *
   * @param strAttrName The name of the attribute to match on
   * @param strAttrValue The value of the attribute to match on
   *
   * @return The VwElement in the list that matches an element's attribute and value or
   * null if no match occurs
   *
   */
  public VwElement find( String strAttrName, String strAttrValue )
  {
    for ( Iterator iElements = this.iterator(); iElements.hasNext(); )
    {

      VwElement element = (VwElement)iElements.next();

      String strAttribute =  element.getAttribute( strAttrName );

      if ( strAttribute == null )
        continue;

      if ( strAttribute.equalsIgnoreCase( strAttrValue ) )
        return element;

    } // end for()

    return null;

  } // end find

} // end class VwElementList{}


// *** End of VwElementList.java ***

