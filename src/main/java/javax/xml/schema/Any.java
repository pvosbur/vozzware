/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: Any.java

============================================================================================
*/
package javax.xml.schema;

public interface Any extends SchemaCommon
{

  /**
   * Sets the MaxOccurs property
   *
   * @param strMaxOccurs The maxOccurs value
   */
  public void setMaxOccurs( String strMaxOccurs );

  /**
   * Gets MaxOccurs property
   *
   * @return  The MaxOccurs property
   */
  public String getMaxOccurs();

  /**
   * Sets the MinOccurs property
   *
   * @param strMinOccurs
   */
  public void setMinOccurs( String strMinOccurs );

  /**
   * Gets MinOccurs property
   *
   * @return  The MinOccurs property
   */
  public String getMinOccurs();


  /**
   * Sets the strNamespace attribute
   * @param strNamespace the Namespace attribute
   */
  public void setNamespace( String strNamespace  );

  /**
   * Gets the namespace attribute
   * @return  The namespace attribute
   */
  public String getNamespace();


  /**
   * Sets the processContents attribute
   * @param strProcessContents the processContents attribute
   */
  public void setProcessContents( String strProcessContents );


  /**
   * Gets the processContents attribute
   * @return the processContents attribute
   */
  public String getProcessContents();
}