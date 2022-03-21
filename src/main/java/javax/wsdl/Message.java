/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: Message.java

============================================================================================
*/
package javax.wsdl;

import java.util.List;


/**
 * This represents the WSDL message element
 *
 * @author Peter VosBurgh
 */
public interface Message extends WSDLCommon
{

  /**
   * Adds a part to this message.
   *
   * @param part the part to be added
   */
  public void addPart( Part part );


  /**
   * Removes part from this message.
   *
   * @param part the part to be removed
   */
  public void removePart( Part part );


  /**
   * Removes a Psrt by its name
   *
   * @param strPartName The name of the part to remove
   */
  public void removePart( String strPartName  );
  
  /**
   * Removes all parts from this message.
   */
  public void removeAllParts();

  /**
   * Gets the specified part.
   *
   * @param strPartName the name of part to retrieve
   *
   * @return the corresponding part, or null if there wasn't
   * any matching part
   */
  public Part getPart( String strPartName );

  /**
   * Gets a List the parts defined here.
   */
  public List getParts();

} // end interface Message{}

/// *** End of Message.java ***
