/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwAttributes.java

Create Date: Apr 11, 2006
============================================================================================
*/

package com.vozzware.xml;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

/**
 * This class holds the data and attributes for an xml element (tag). This is also
 * the default object used in the VwDataObject for storing it's data elements.
 */
public class VwAttributes extends AttributesImpl
{

  /**
   * Construct an empty Attributes object
   */
  public VwAttributes()
  { super(); }

  /**
   * Construct object from an existing Attributes object. The original Attributes
   * object is cloned.
   */
  public VwAttributes( Attributes attrs )
  {
    super();
    add( attrs );

  } // end VwAttributes()

  /**
   * Constructor
   *
   * @param strName The attribute name
   * @param strValue The attribute value
   *
   */
  public VwAttributes( String strName, String strValue )
  {
    super();
    addAttribute( "", strName, strName, "CDATA", strValue );

  } // end VwAttributes


  /**
   * Add additional attribute
   *
   * @param strName The attribute name
   * @param strValue The attribute value
   */
  public void add( String strName, String strValue )
  { addAttribute( "", strName, strName, "CDATA", strValue );  }


  /**
   * Add additional attributes from an Attributes object. Duplicates in the attr instance will
   * <br>will be ignored.
   *
   * @param attr Attributes to add
   */
  public void add( Attributes attr )
  {

    for ( int x = 0; x < attr.getLength(); x++ )
    {

      String strVal = getValue( attr.getLocalName( x ) );
      if ( strVal == null )
        addAttribute( attr.getURI( x ), attr.getLocalName( x ), attr.getQName( x ), attr.getType( x ),
                      attr.getValue( x ) );

    } // end for()


  } // end add()


  /**
   * Renders Attributes as an VwDataObjeList. Each Attribute definition is placed
   * in an VwDataObject
   */
  VwDataObjList toDataObjList()
  {

    VwDataObjList dobjList = new VwDataObjList();

    int nCount = this.getLength();

    for ( int x = 0; x < nCount; x++ )
    {
      VwDataObject dobjAttr = new VwDataObject();

      dobjAttr.put( "Uri", this.getURI( x ) );
      dobjAttr.put( "QNameOld", this.getQName( x ) );
      dobjAttr.put( "LocalName", this.getLocalName( x ) );
      dobjAttr.put( "Type", this.getType( x ) );
      dobjAttr.put( "Value", this.getValue( x ) );

      dobjList.add( dobjAttr );

    } // end for()

    return dobjList;

  } // end toDataObjList()


  // Testing Only
  public static void main( String[] args )
  {
    VwAttributes attr = new VwAttributes( "name", "joe" );
    VwAttributes attr2 = new VwAttributes( "color", "red" );

    attr.add( attr2 );

    return;
  }
} // end class VwAttributes{}

// *** End of VwAttributes.java ***