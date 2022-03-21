/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwServiceable.java

Create Date: Apr 11, 2006
============================================================================================
*/

package com.vozzware.xml;              // The package this class belongs to

import org.xml.sax.Attributes;

/**
 * This interface must be implemented by objects that can be passed back and forth to the
 * Vw Opera server.
 */
public abstract interface VwServiceable
{

  /**
   * Return the name of the service that the implementing object is associated with.
   */
  public abstract String getServiceName();

  /**
   * Sets the service name associated with this object. This method is used only
   * in conjunction with the Vw Opera server product.
   *
   * @param strServiceName The name of the associated service
   *
   */
  public abstract void setServiceName( String strServiceName );

  /**
   * Clear the contents of the container
   */
  public abstract void clear();

  /**
   * Get service attributes as an VwAttributeList
   */
  public abstract Attributes getServiceAttributes();

  /**
   * Sets the VwServiceable object with an VwAttributeList
   *
   * @param listAttr A list service attributes
   */
  public abstract void setServiceAttributes( Attributes listAttr );


  /**
   * Removes any service attributes
   *
   */
  public abstract void removeServiceAttributes();

} // end interface VwServiceable{}

// *** End of VwServiceable.java ***


