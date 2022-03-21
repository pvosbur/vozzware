/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwServiceImpl.java

Create Date: Apr 11, 2006
============================================================================================
*/
package com.vozzware.wsdl;

import com.vozzware.wsdl.extensions.VwExtensibilityElementSupportImpl;

import javax.wsdl.Port;
import javax.wsdl.Service;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.UnknownExtensibilityElement;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Implementation class for the Service interface
 *
 * @author Paul Fremantle
 * @author Nirmal Mukhi
 * @author Matthew J. Duftler
 */
public class VwServiceImpl extends VwWSDLCommonImpl implements Service
{
  private List m_listPorts = new LinkedList();

  private Map  m_mapPorts = new HashMap();

  private VwExtensibilityElementSupportImpl m_extSupport = new VwExtensibilityElementSupportImpl();

  /**
   * Adds a port to this service.
   *
   * @param port the port to be added
   */
  public void addPort( Port port )
  {
    if ( !m_mapPorts.containsKey( port.getName() ) )
    {
      m_mapPorts.put( port.getName(), port );
      m_listPorts.add( port );

    }

  } // end addPort()


  /**
   * Removes a port from this service.
   *
   * @param port the port to be removed
   */
  public void removePort( Port port )
  { removePort( port.getName() ); }



  /**
   * Removes a Port given the Port's name
   *
   * @param strName The name of the port to remove
   */
  public void removePort( String strName )
  {
    Port portToRemove = (Port)m_mapPorts.get( strName );

    if ( portToRemove != null )
      m_listPorts.remove( portToRemove );

  } // end removePort

  /**
   * Removes all ports from this service.
   *
   */
  public void removeAllPorts()
  {
    m_listPorts.clear();
    m_mapPorts.clear();

  } // end removeAllPorts()

  /**
   * Get the specified port.
   *
   * @param strName the name of the port to retrieve.
   *
   * @return the corresponding port, or null if the name does not exist.
   */
  public Port getPort( String strName )
  { return (Port)m_mapPorts.get( strName ); }

  /**
   * Gets a List of all the defined ports.
   *
   * @return a List of all the defined ports.
   */
  public List getPorts()
  { return m_listPorts; }

  /**
   * Adds an extensibility element for specific service extensions
   *
   * @param extElement extensibility element for specific service extensions
   */
  public void addExtensibilityElement( ExtensibilityElement extElement )
  {  m_extSupport.addExtensibilityElement( extElement ); }

  /**
   * Removes the specified extensibility element
   *
   * @param extElement the extensibility element to remove
   */
  public void removeExtensibilityElement( ExtensibilityElement extElement )
  { m_extSupport.removeExtensibilityElement( extElement ); }

  /**
   * Removes all extensibility elements
   */
  public void removeAllExtensibilityElements()
  { m_extSupport.removeAllExtensibilityElements(); }


  /**
   * Gets all the extensibility elements defined.
   *
   * @return a List of all extensibility elements defined for this service
   */
  public List getExtensibilityElements()
  { return m_extSupport.getExtensibilityElements(); }


  /**
   * Adds an unknown extensibility element for specific service extensions
   *
   * @param unknownExtElementt the unknown extensibility element for specific service extensions
   */
  public void addUnknownExtensibilityElement( UnknownExtensibilityElement unknownExtElement )
  {  m_extSupport.addUnknownExtensibilityElement( unknownExtElement ); }

  /**
   * Removes the specified unknown extensibility element
   *
   * @param unknownExtElement the unknown extensibility element to remove
   */
  public void removeUnknownExtensibilityElement( UnknownExtensibilityElement unknownExtElement )
  { m_extSupport.removeUnknownExtensibilityElement( unknownExtElement ); }

  /**
   * Removes all unknown extensibility elements
   */
  public void removeAllUnknownExtensibilityElements()
  { m_extSupport.removeAllUnknownExtensibilityElements(); }


  /**
   * Gets all the unknown extensibility elements defined.
   *
   * @return a List of all unknown extensibility elements defined for this service
   */
  public List getUnknownExtensibilityElements()
  { return m_extSupport.getUnknownExtensibilityElements(); }

  /**
   * Removes all ExtensibilityElements and UnknownExtensibilityElements
   *
   */
  public List getAllElements()
  { return m_extSupport.getAllElements(); }
  
  
  /**
   * Gets the List of all ExtensibilityElements and UnknownExtensibilityElements
   * 
   * @return  the List of all ExtensibilityElements and UnknownExtensibilityElements
   */
  public void removeAllElements()
  { m_extSupport.removeAllElements(); }
  

  public List getContent()
  {
    List listContent = new LinkedList();

    if ( getDocumentation() != null )
      listContent.add( getDocumentation() );;

    listContent.addAll( getAllElements() );

    listContent.addAll( m_listPorts );

    return listContent;
    
  }
} // end class VwServiceImpl{}

// *** End of VwServiceImpl.java ***
