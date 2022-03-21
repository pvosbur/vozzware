/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: Include.java

============================================================================================
*/
package javax.xml.schema;

public interface Include
{
  /**
   * Sets the schema loaction URI attribute
   * @param strSchemaLocationURI the schema loaction URI attribute
   * @throws InvalidSchemaLocationException if the schema cannot be found
   */
  public void setSchemaLocation( String strSchemaLocationURI  ) throws InvalidSchemaLocationException;

  /**
   * Gets the schema location URI attribute
   * @return  the schema location URI attribute
   */
  public String getSchemaLocation();

  /**
   * Gets the included schema document
   * @return the included schema document
   */
  public Schema getSchema();
}