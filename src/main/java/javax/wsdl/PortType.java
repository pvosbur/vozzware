/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: PortType.java

Create Date: Apr 11, 2006
============================================================================================
*/
package javax.wsdl;

import java.util.List;

/**
 * This represents the port type WSDL element
 *
 * @author Peter VosBurgh
 */
public interface PortType extends WSDLCommon
{

  /**
   * Add an operation to this port type.
   *
   * @param operation the operation to be added
   */
  public void addOperation( Operation operation );


  /**
   * Remove an operation from this port type.
   *
   * @param operation the operation to be removed
   */
  public void removeOperation( Operation operation );


  /**
   * Remove an operation from this port type by its name
   *
   * @param strName the name of the operation to be removed
   */
  public void removeOperation( String strName );

  /**
   * Removes all operations from this port type.
   *
   */
  public void removeAllOperations();

  /**
   * Gets the portType operation by its name
   *
   * @param strName the name of the portType operation to retrieve
   */
  public Operation getOperation( String strName );

  /**
   * Gets a List of the operations for this portType
   */
  public List getOperations();

} // end interface PortType{}

// *** End of PortType.java ***
