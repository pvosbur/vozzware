/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: Union.java

============================================================================================
*/
package javax.xml.schema;

import java.util.List;

/**
 * This represents the Xml Schema union element type
 */
public interface Union extends SchemaCommon
{
  /**
   * Sets the content's memberTypes property
   *
   * @param strMemberTypes
   */
  public void setMemberTypes( String strMemberTypes );

  /**
   * Gets MemberTypes property
   *
   * @return  The MemberTypes property
   */
  public String getMemberTypes();

  /**
   * Adds a simpleType content to this list
   *
   * @param simpleType The simpleType content
   *
   */
  public void addSimpleType( SimpleType simpleType );

  /**
   * Removes the specified SimpleType from the list of simple types
   * @param simpleType The SimpleType instace to remove
   */
  public void removeSimpleType( SimpleType simpleType );

  /**
   * Removes the specified SimpleType from the list of simple types
   * @param strName The name of the globally defined SimpleType instace to remove
   */
  public void removeSimpleType( String strName );

  /**
   * Gets a List of all the simple types defined
   * @return
   */
  public List getSimplTypes();

  /**
   * Gets an Iterator to the list of simpleTypes
   *
   * @return an Iterator to the list of simpleTypes
   */
  public List getContent();

} // end interface Union{}

// *** End of Union.java ***
