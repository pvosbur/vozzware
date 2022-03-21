/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: Part.java

============================================================================================
*/
package javax.wsdl;

/**
 * This represents the WSDL part element type
 *
 * @author Peter VosBurgh
 */
public interface Part extends WSDLCommon
{
  /**
   * Sets the part's element attribute
   * @param strElementName  The name of the element in the schema sction to refer to
   */
  public void setElement( String strElementName  );

  /**
   * Gets the part's element attribute
   *
   * @return the part's element attribute
   */
  public String getElement();

  /**
   * Sets the part's type attribute
   * @param strTypeName
   */
  public void setType( String strTypeName );

  /**
   * Gets the part's type attribute
   *
   * @return the part's type attribute
   */
  public String getType();

} // end interface Part{}

// *** End of Part.java ***
