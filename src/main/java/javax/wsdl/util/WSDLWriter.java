/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: WSDLReader.java

============================================================================================
*/
package javax.wsdl.util;

import javax.wsdl.Definition;
import java.io.File;

/**
 * This represents a genric interface to a WSDL Reader
 */
public interface WSDLWriter
{
  
  /**
   * Writes the WSDL document from the Definition object to a String
   *
   * @param wsdlDef The Wsdl Definition object with the WSDL content to write
   *
   * @return A wsdl document ibn a string object
   *
   * @throws Exception if any IO or format errors occur
   */
  public String writeWsdl( Definition wsdlDef ) throws Exception;


  /**
   * Writes the WSDL document from the Definition object to the file specified 
   * @param wsdlDef The Definition object graph contating the elements to write
   * @param fileWSDL The file will be wriiten
   * @return
   * @throws Exception
   */
  public void writeWsdl( Definition wsdlDef, File fileWSDL ) throws Exception;
  
} // end interface WSDLReader{}

// *** End of WSDLReader.java ***

