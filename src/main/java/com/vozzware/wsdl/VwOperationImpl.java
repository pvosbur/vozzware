/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwOperationImpl.java

Create Date: Apr 11, 2006
============================================================================================
*/
package com.vozzware.wsdl;

import com.vozzware.util.VwDelimString;

import javax.wsdl.Fault;
import javax.wsdl.Input;
import javax.wsdl.Operation;
import javax.wsdl.Output;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


/**
 * This represents the WSDL operation element of a portType.
 *
 * @author Peter VosBurgh
 */
public class VwOperationImpl extends VwWSDLCommonImpl implements Operation
{
  private Object[]  m_aOperationOrder = new Object[ 2 ];

  private List      m_listFaults = new LinkedList();

  private int       m_nStyle = UNDEFINED;

  private String    m_strParamOrder;


  /**
   * Sets the input message for this operation.
   *
   * @param input the input message
   */
  public void setInput( Input input )
  {
    // if an Input already exists, replace it with this one
    for ( int x = 0; x < m_aOperationOrder.length; x++ )
    {

      if ( m_aOperationOrder[ x ] instanceof Input )
      {
        m_aOperationOrder[ x ] = input;
        return;

      }
      else
      if ( m_aOperationOrder[ x ] == null )
      {
        m_aOperationOrder[ x ] = input;
        return;

      }

    } // end for

  } // end setInput()

  /**
   * Gets the input message for this operation.
   *
   * @return the input message (may be null)
   */
  public Input getInput()
  {
    for ( int x = 0; x < m_aOperationOrder.length; x++ )
    {

      if ( m_aOperationOrder[ x ] instanceof Input )
        return (Input)m_aOperationOrder[ x ];

    }

    return null;    // Does not exist

  } // end getInput()

  /**
   * Sets the output message for this operation.
   *
   * @param output the output message
   */
  public void setOutput( Output output )
  {
    // if an Outputt already exists, replace it with this one
    for ( int x = 0; x < m_aOperationOrder.length; x++ )
    {

      if ( m_aOperationOrder[ x ] instanceof Output )
      {
        m_aOperationOrder[ x ] = output;
        return;

      }
      else
      if ( m_aOperationOrder[ x ] == null )
      {
        m_aOperationOrder[ x ] = output;
        return;

      }

    } // end for

  } // end setOutput()

  /**
   * Gets the output message for this operation.
   *
   * @return the output message (may be null)
   */
  public Output getOutput()
  {
    for ( int x = 0; x < m_aOperationOrder.length; x++ )
    {

      if ( m_aOperationOrder[ x ] instanceof Output )
        return (Output)m_aOperationOrder[ x ];

    }

    return null;    // Does not exist

  } // end getOutput()

	/**
	 * Adds a fault message to this operation
   *
	 * @param fault the fault message to add
	 */
  public void addFault( Fault fault )
  { m_listFaults.add( fault ); }


  /**
   * Removes a fault message from the List
   *
   * @param fault the fault message to remove
   */
  public void removeFault( Fault fault )
  { m_listFaults.remove( fault ); }


  /**
   * Removes all fault messages
   *
   */
  public void removeAllFaults()
  { m_listFaults.clear(); }

  /**
   * Get the specified fault message.
   *
   * @param strName the name of the desired fault message.
   *
   * @return the corresponding fault message, or null if no fault exists for the name specified
   */
  public Fault getFault( String strName )
  {
    for ( Iterator iFaults = m_listFaults.iterator(); iFaults.hasNext(); )
    {
      Fault faultToGet = (Fault)iFaults.next();

      String strToGet = faultToGet.getName();

      if ( strToGet != null && strToGet.equalsIgnoreCase( strName ) )
        return faultToGet;

    } // end for()

    return null;      // No matching name
  }

	/**
	 * Gets a List of the fault messages associated with this operation.
   *
	 * @return a List of fault messages
	 */
  public List getFaults()
  { return m_listFaults; }

	/**
	 * Set the style for this operation (request-response,
	 * one way, solicit-response or notification).
   *
	 * @param nStyle One of the static style constant define in this class
	 */
	public void setStyle( int nStyle )
  { m_nStyle = nStyle; }

	/**
	 * Get the operation type.
   *
	 * @return One of the static style constant define in this class
	 */
	public int getStyle()
  {
    if ( m_nStyle == Operation.UNDEFINED )
    {
      if ( m_aOperationOrder[ 0 ] instanceof Input )
      {
        if ( m_aOperationOrder[ 1 ] instanceof Output )
          m_nStyle = Operation.REQUEST_RESPONSE;
        else
          m_nStyle = Operation.ONE_WAY;
      }
      else
      if ( m_aOperationOrder[ 0 ] instanceof Output )
      {
        if ( m_aOperationOrder[ 1 ] instanceof Input )
          m_nStyle = Operation.SOLICIT_RESPONSE;
        else
          m_nStyle = Operation.NOTIFICATION;
      }
    }

    return m_nStyle;

  } // end getStyle()

	/**
	 * Set the parameter ordering for a request-response,
	 * or solicit-response operation.
   *
	 * @param strParamOrder  a space deimited String of named parameters
	 * containing the part names to reflect the desired
	 * order of parameters for RPC-style operations
	 */
	public void setParameterOrder( String strParamOrder )
  { m_strParamOrder = strParamOrder; }

  /**
   * Helper method to render a List of part name Strings to a space delimited string reqired
   * for the attribute xml rendering
   *
   * @param listPartNames A list String representing the parameter oder
   */
  public void setParameterOrderList( List listPartNames )
  {
    StringBuffer sb = new StringBuffer();

    for ( Iterator iPartNames = listPartNames.iterator(); iPartNames.hasNext(); )
    {
      if ( sb.length() > 0 )
        sb.append( " " );

      sb.append( (String)iPartNames.next() );
    }

    m_strParamOrder = sb.toString();

  } // end setParameterOrderList()


  /**
   * Gets the parameterOrder space delimited string of part names
   *
   * @return a space delimited string of part names
   */
  public String getParamOrder()
  { return m_strParamOrder; }


	/**
	 * Helper method to render space delimited parameterOrder string as a List of Strings
   *
	 * @return A List of Strings representing the paramaterOrder part names
	 */
	public List getParameterOrderList()
  {

    if ( m_strParamOrder != null )
    {
      VwDelimString dlmsParamOrder = new VwDelimString( " ", m_strParamOrder );
      return dlmsParamOrder.toStringList();
    }

    return null;

  } // end getParameterOrderList()

  /**
   * Gets all child content
   * @return a List of all the chile content objects
   */
  public List getContent()
  {
    getStyle();
    List listContent = new LinkedList();

    if ( this.getDocumentation() != null )
      listContent.add( this.getDocumentation() );

    switch( m_nStyle )
    {
      case ONE_WAY:

           if ( m_aOperationOrder[ 0 ] instanceof Input )
              listContent.add( m_aOperationOrder[ 0 ] );
           else
           if ( m_aOperationOrder[ 1 ] instanceof Input )
             listContent.add( m_aOperationOrder[ 1 ] );

           break;

      case NOTIFICATION:

           if ( m_aOperationOrder[ 0 ] instanceof Output )
             listContent.add( m_aOperationOrder[ 0 ] );
           else
           if ( m_aOperationOrder[ 1 ] instanceof Output )
             listContent.add( m_aOperationOrder[ 1 ] );

           break;

      case REQUEST_RESPONSE:

           if ( m_aOperationOrder[ 0 ] instanceof Input )
           {
             listContent.add( m_aOperationOrder[ 0 ] );

             if ( m_aOperationOrder[ 1 ] instanceof Output )
               listContent.add( m_aOperationOrder[ 1 ] );

           }
           else
           if ( m_aOperationOrder[ 1 ] instanceof Input )
           {
             listContent.add( m_aOperationOrder[ 1 ] );
             if ( m_aOperationOrder[ 0 ] instanceof Output )
               listContent.add( m_aOperationOrder[ 0 ] );

           }

           break;

      case SOLICIT_RESPONSE:

           if ( m_aOperationOrder[ 0 ] instanceof Output )
           {
             listContent.add( m_aOperationOrder[ 0 ] );

             if ( m_aOperationOrder[ 1 ] instanceof Input )
               listContent.add( m_aOperationOrder[ 1 ] );

           }
           else
           if ( m_aOperationOrder[ 1 ] instanceof Output )
           {
             listContent.add( m_aOperationOrder[ 1 ] );
             if ( m_aOperationOrder[ 0 ] instanceof Input )
               listContent.add( m_aOperationOrder[ 0 ] );

           }

           break;

    } // end switch()

    listContent.addAll( m_listFaults );

    return listContent;

  } // end getContent()

} // end class VwOperationImpl{}

// *** End of VwOperationImpl.java ***
