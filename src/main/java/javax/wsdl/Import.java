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
package javax.wsdl;

/**
 * This represents the WSDL import element
 *
 * @author Peter VosBurgh
 */
public interface Import extends WSDLCommon
{

  /**
   * Returns the imported wsdl document
   * @return the imported wsdl document
   */
  public Definition getDefinition();

  /**
   * Sets the strNamespace attribute
   * @param strNamespace the strNamespace attribute
   */
  public void setNamespace( String strNamespace  );

  /**
   * Gets the wsdl location URI attribute
   * @return  the wsdl location URI attribute
   */
  public String getNamespace();

  /**
   * Sets the wsdl loaction URI attribute
   * @param strSchemaLocationURI the wsdl loaction URI attribute
   * @throws InvalidWsdlLocationException if the wsdl cannot be found
   */
  public void setLocation( String strSchemaLocationURI  ) throws InvalidWsdlLocationException;

  /**
   * Gets the wsdl location URI attribute
   * @return  the wsdl location URI attribute
   */
  public String getLocation();

} // end interface Import{}

// *** End of Import.java ***
