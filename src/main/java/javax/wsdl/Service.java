/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: Service.java

Create Date: Apr 11, 2006
============================================================================================
*/
package javax.wsdl;

import javax.wsdl.extensions.ExtensibilityElementSupport;
import java.util.List;

/**
 * This interface represents a service, which groups related
 * ports to provide some functionality.
 *
 * @author Paul Fremantle
 * @author Nirmal Mukhi
 * @author Matthew J. Duftler
 */
public interface Service extends WSDLCommon, ExtensibilityElementSupport
{
  /**
   * Adds a port to this service.
   *
   * @param port the port to be added
   */
  public void addPort( Port port );


  /**
   * Removes a port from this service.
   *
   * @param port the port to be removed
   */
  public void removePort( Port port );


  /**
   * Removes a Port given the Port's name
   *
   * @param strName The name of the port to remove
   */
  public void removePort( String strName );

  /**
   * Removes all ports from this service.
   *
   */
  public void removeAllPorts();

  /**
   * Get the specified port.
   *
   * @param strName the name of the port to retrieve.
   *
   * @return the corresponding port, or null if the name does not exist.
   */
  public Port getPort( String strName );

  /**
   * Gets a List of all the defined ports.
   *
   * @return a List of all the defined ports.
   */
  public List getPorts();


} // end interface Service{}

// *** End of Service.java ***
