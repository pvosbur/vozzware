/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: ExtensibilityElementSupport.java

============================================================================================
*/
package javax.wsdl.extensions;

import java.util.List;

/**
 * Interface to handle all WSDL elements that support extesnibility elements
 */
public interface ExtensibilityElementSupport
{

  /**
   * Adds an extensibility element for specific service extensions
   *
   * @param extElement extensibility element for specific service extensions
   */
  public void addExtensibilityElement( ExtensibilityElement extElement );

  /**
   * Removes the specified extensibility element
   *
   * @param extElement the extensibility element to remove
   */
  public void removeExtensibilityElement( ExtensibilityElement extElement );

  /**
   * Removes all extensibility elements
   */
  public void removeAllExtensibilityElements();


  /**
   * Gets all the extensibility elements defined.
   *
   * @return a List of all extensibility elements defined for this service
   */
  public List getExtensibilityElements();

  /**
   * Gets the List of all ExtensibilityElements and UnknownExtensibilityElements
   * 
   * @return  the List of all ExtensibilityElements and UnknownExtensibilityElements
   */
  public List getAllElements();
  
  /**
   * Removes all ExtensibilityElements and UnknownExtensibilityElements
   *
   */
  public void removeAllElements();
  
  /**
   * Adds an unknown extensibility element for specific service extensions
   *
   * @param extElement the unknown extensibility element for specific service extensions
   */
  public void addUnknownExtensibilityElement( UnknownExtensibilityElement extElement );

  /**
   * Removes the specified unknown extensibility element
   *
   * @param extElement the unknown extensibility element to remove
   */
  public void removeUnknownExtensibilityElement( UnknownExtensibilityElement extElement );

  /**
   * Removes all unknown xtensibility elements
   */
  public void removeAllUnknownExtensibilityElements();


  /**
   * Gets all the unknown extensibility elements defined.
   *
   * @return a List of all unknown extensibility elements defined for this service
   */
  public List getUnknownExtensibilityElements();
  
} // end interface ExtensibilityElementSupport{}

// *** End of ExtensibilityElementSupport.java ***
