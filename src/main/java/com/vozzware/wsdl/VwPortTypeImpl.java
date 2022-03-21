/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwPortTypeImpl.java

Create Date: Apr 11, 2006
============================================================================================
*/
package com.vozzware.wsdl;

import javax.wsdl.Operation;
import javax.wsdl.PortType;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * This represents the port type WSDL element
 *
 * @author Peter VosBurgh
 */
public class VwPortTypeImpl extends VwWSDLCommonImpl implements PortType
{
  private List m_listOperations = new LinkedList();
  private Map  m_mapOperations = new HashMap();

  /**
   * Add an operation to this port type.
   *
   * @param operation the operation to be added
   */
  public void addOperation( Operation operation )
  {
    if ( !m_mapOperations.containsKey( operation.getName() ) )
    {
      m_listOperations.add( operation );
      m_mapOperations.put( operation.getName(), operation );
    }

  } // end addOperation()


  /**
   * Remove an operation from this port type.
   *
   * @param operation the operation to be removed
   */
  public void removeOperation( Operation operation )
  { removeOperation( operation.getName() ); }


  /**
   * Remove an operation from this port type by its name
   *
   * @param strName the name of the operation to be removed
   */
  public void removeOperation( String strName )
  {
    Operation op = (Operation)m_mapOperations.get(  strName );

    if ( op != null )
      m_listOperations.remove( op );

  } // end removeOperation()

  /**
   * Removes all operations from this port type.
   *
   */
  public void removeAllOperations()
  {
    m_listOperations.clear();
    m_mapOperations.clear();

  } // end removeAllOperations()

  /**
   * Gets the portType operation by its name
   *
   * @param strName the name of the portType operation to retrieve
   */
  public Operation getOperation( String strName )
  { return (Operation)m_mapOperations.get(  strName ); }

  /**
   * Gets a List of the operations for this portType
   */
  public List getOperations()
  { return m_listOperations; }

} // end class VwPortTypeImpl{}

// *** End of VwPortTypeImpl.java ***
