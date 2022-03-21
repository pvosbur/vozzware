/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwDataObjList.java

Create Date: Apr 11, 2006
============================================================================================
*/
package com.vozzware.xml;

import org.xml.sax.Attributes;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;
/**
 * This class holds a List of VwDataObjects and implemets the VwServiceable interface
 * making it a valid container for use with the Opera Soap server.
 */
public class VwDataObjList extends LinkedList implements VwServiceable
{

  private String            m_strServiceName;   // Service name associated with the collection
  private Attributes  m_listAttr;


  /**
   * Default constructor
   *
   */
  public VwDataObjList()
  { super(); }


  /**
   * Gets The VwDataObject object at the index position requested
   *
   * @param ndx The index position in the list
   *
   * @return The VwDataObject at the index position in the list
   */
  public VwDataObject getDataObj( int ndx )
  { return (VwDataObject)get( ndx ); }


  /**
   * Return the service name associated with this collection. This method is used only
   * in conjunction with the Vw Opera server product.
   *
   */
  public String getServiceName()
  { return m_strServiceName; }


  /**
   * Sets the service name associated with this collection. This method is used only
   * in conjunction with the Vw Opera server product.
   *
   * @param strServiceName The name of the associated service
   *
   */
  public void setServiceName( String strServiceName )
  { m_strServiceName = strServiceName; }

  /**
   * Get service attributes as an Attributes
   *
   * @return The attribute list for the defined service or null if no attributes defined
   */
  public Attributes getServiceAttributes()
  { return m_listAttr; }

  /**
   * Sets the VwServiceableAdapter object with an Attributes
   *
   * @param listAttr A list service attributes
   */
  public void setServiceAttributes( Attributes listAttr )
  { m_listAttr = listAttr; }


  /**
   * Removes the VwServiceableAdapter object with an Attributes
   *
   */
  public void removeServiceAttributes()
  { m_listAttr = null; }


  /**
   * Finds an VwDataObject for a key and value
   *
   * @param strKey The name of the key in the VwDataOBject to retrieve the value for
   * @param objValue The value to compare
   *
   * @return The VwDataObject that matches the key and value or null if no match was found
   *
   */
  public VwDataObject find( String strKey, Object objValue )
  {
    Iterator iDobj = iterator();

    while( iDobj.hasNext() )
    {
      VwDataObject dobj = (VwDataObject)iDobj.next();

      Object dobjValue = dobj.get( strKey );

      if ( dobjValue != null && dobjValue.equals( objValue ) )
        return dobj;

    } // end while

    return null;        // No match found

  } // end find()


  /**
   * Finds an VwDataObject by an attribute value
   *
   * @param strKey The data object element key to retrieve the attribute for
   * @param strAttrName The attribute name
   * @param strAttrVal The attribute value
   */
  public VwDataObject findByAttribute( String strKey, String strAttrName, String strAttrVal )
  {
    for ( Iterator idobj = this.iterator(); idobj.hasNext(); )
    {
      VwDataObject dobj = (VwDataObject)idobj.next();

      dobj =  dobj.find( strKey, strAttrName, strAttrVal );

      if ( dobj != null )
        return dobj;

    } // end for

    return null; // No match

  } // end findByAttribute()


  /**
   * Removes an VwDataObject from the list for a specified key and value
   *
   * @param strKey The name of the key in the VwDataOBject to retrieve the value for
   * @param objValue The value to compare
   *
   * @return The VwDataObject that was removed or null if no match was found
   *
   */
  public VwDataObject remove( String strKey, Object objValue )
  {
    VwDataObject dobjRemove = find( strKey, objValue );

    if ( dobjRemove != null )
      this.remove( dobjRemove );

    return dobjRemove;

  } // end remove


  /**
   * Moves the VwDataObject identified by the key and value in this list to the
   * VwDataObjList specified dolRecipient. The VwDataObject found by the match\
   * is removed from this list.
   *
   * @param strKey The name of the key in the VwDataOBject to retrieve the value for
   * @param objValue The value to compare
   * #param dolRecipient The VwDataObjList that receives the found data object
   *
   * @return The VwDataObject that was moved or null if no match was found
   */
  public VwDataObject move( String strKey, Object objValue, VwDataObjList dobjRecipient )
  {
    VwDataObject dobjMove = remove( strKey, objValue );

    if ( dobjMove != null )
      dobjRecipient.add( dobjMove );

    return dobjMove;

  } // end move


   /**
   * Sorts the data objects in the list acording to the sort key
   *
   * @param strSortKey The name of the key in the VwDataOBject to use as the sort key
   *
   */
  public void sort( String strSortKey )
  {
    TreeMap tm = new TreeMap();

    Iterator iObjects = iterator();

    while ( iObjects.hasNext() )
    {

      VwDataObject dobj = (VwDataObject)iObjects.next();

      Object objSortKey = dobj.get( strSortKey );

      Object objSortData = tm.get( objSortKey );

      if ( objSortData instanceof List )
        ((List)objSortData).add( dobj );
      else
      if ( objSortData instanceof VwDataObject )
      {
        LinkedList list = new LinkedList();
        list.add( objSortData );
        list.add(  dobj );
        tm.put( objSortKey, list );
      }
      else
        tm.put( objSortKey, dobj );

    } // end while();

    // Remove all object from this list and re add them from the TreeMap sorted

    clear();

    Iterator iMap = tm.values().iterator();

    while( iMap.hasNext() )
    {
      Object objData = iMap.next();

      if ( objData instanceof List )
      {
        for ( Iterator iList = ((List)objData).iterator(); iList.hasNext(); )
          add( iList.next() );

      }
      else
        add( objData );

    } // end while

    tm = null;

  } // end sort

  /**
   * Copies the VwDataObject identified by the key and value in this list to the
   * VwDataObjList specified dolRecipient.
   *
   * @param strKey The name of the key in the VwDataOBject to retrieve the value for
   * @param objValue The value to compare
   * #param dolRecipient The VwDataObjList that receives the found data object
   *
   * @return The VwDataObject that was copied or null if no match was found
   */
  public VwDataObject copy( String strKey, Object objValue, VwDataObjList dobjRecipient )
  {
    VwDataObject dobjCopy = find( strKey, objValue );

    if ( dobjCopy != null )
      dobjRecipient.add( dobjCopy );

    return dobjCopy;

  } // end copy


  // For TESTING ONLY

  public static void main( String[] args )
  {

    return;

  }

} // end class VwDataObjList{}


// *** End of VwDataObjList.java ***

