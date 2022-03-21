/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: Operation.java

============================================================================================
*/
package javax.wsdl;

import java.util.List;

/**
 * This represents the WSDL operation element of a portType.
 *
 * @author Peter VosBurgh
 */
public interface Operation extends WSDLCommon
{
  public static final int UNDEFINED = -1;

  /**
   * Constant for the One way operation style
   */
  public static final int ONE_WAY = 0;

  /**
   * Constatnt for the request-response operation style
   */
  public static final int REQUEST_RESPONSE = 1;

  /**
   * Constant for the solicit-response operation style
   */
  public static final int SOLICIT_RESPONSE = 3;

  /**
   * Constant for the notification operation style
   */
  public static final int NOTIFICATION = 4;


  /**
   * Sets the input message for this operation.
   *
   * @param input the input message
   */
  public void setInput( Input input );

  /**
   * Gets the input message for this operation.
   *
   * @return the input message (may be null)
   */
  public Input getInput();

  /**
   * Sets the output message for this operation.
   *
   * @param output the output message
   */
  public void setOutput( Output output );

  /**
   * Gets the output message for this operation.
   *
   * @return the output message (may be null)
   */
  public Output getOutput();

	/**
	 * Adds a fault message to this operation
   *
	 * @param fault the fault message to add
	 */
  public void addFault( Fault fault );


  /**
   * Removes a fault message from the List
   *
   * @param fault the fault message to remove
   */
  public void removeFault( Fault fault );


  /**
   * Removes all fault messages
   *
   */
  public void removeAllFaults();

  /**
   * Get the specified fault message.
   *
   * @param strName the name of the desired fault message.
   *
   * @return the corresponding fault message, or null if no fault exists for the name specified
   */
  public Fault getFault( String strName );

	/**
	 * Gets a List of the fault messages associated with this operation.
   *
	 * @return a List of fault messages
	 */
  public List getFaults();

	/**
	 * Set the style for this operation (request-response,
	 * one way, solicit-response or notification).
   *
	 * @param nStyle One of the static style constant define in this class
	 */
	public void setStyle( int nStyle );

	/**
	 * Get the operation type.
   *
	 * @return One of the static style constant define in this class
	 */
	public int getStyle();

	/**
	 * Set the parameter ordering for a request-response,
	 * or solicit-response operation.
   *
	 * @param strParamOrder  a space deimited String of named parameters
	 * containing the part names to reflect the desired
	 * order of parameters for RPC-style operations
	 */
	public void setParameterOrder( String strParamOrder );

  /**
   * Helper method to render a List of part name Strings to a space delimited string reqired
   * for the attribute xml rendering
   *
   * @param listPartnames A list String representing the parameter oder
   */
  public void setParameterOrderList( List listPartnames );


  /**
   * Gets the parameterOrder space delimited string of part names
   *
   * @return a space delimited string of part names
   */
  public String getParamOrder();


	/**
	 * Helper method to render space delimited parameterOrder string as a List of Strings
   *
	 * @return A List of Strings representing the paramaterOrder part names (may be null )
	 */
	public List getParameterOrderList();

} // end interface Operation{}

// *** End of Operation.java ***
