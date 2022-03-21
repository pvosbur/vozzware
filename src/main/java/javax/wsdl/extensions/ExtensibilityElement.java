/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: ExtensibilityElement.java

============================================================================================
*/
package javax.wsdl.extensions;

/**
 * This interface represents the super class for WSDL extensions
 *
 */
public interface ExtensibilityElement
{

  /**
   * Sets the wsdl:required attribute on WSDL extension elements
   */
  public void setRequired( boolean fRequired );

  /**
   * Returns true if the semantics of this extension are required, else false is returned
   *
   * @return true if the semantics of this extension are required, else false is returned
   */
  public boolean isRequired();

} // end interface ExtensibilityElement{}

// *** End of ExtensibilityElement.java ***