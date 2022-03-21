/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: Import.java

============================================================================================
*/
package javax.xml.schema;

public interface Import extends Include
{
  /**
   * Sets the strNamespace attribute
   * @param strNamespace the strNamespace attribute
   */
  public void setNamespace( String strNamespace  );

  /**
   * Gets the schema location URI attribute
   * @return  the schema location URI attribute
   */
  public String getNamespace();
}