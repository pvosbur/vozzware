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
import javax.xml.schema.Schema;
import java.net.URL;

/**
 * This represents a genric interface to a WSDL Reader
 */
public interface WSDLReader
{
  
  /**
   * Reads a wsdl document specified by the URL
   *
   * @param urlWsdl The URL location of the wsdl document
   *
   * @return A wsdl Definition instance
   *
   * @throws Exception if any IO or format errors occur
   */
  public Definition readWsdl( URL urlWsdl ) throws Exception;

  /**
   * Reads a WSDL document specified by a URI string
   *
   * @param strLocationURI The uri location string
   *
   * @return A wsdl Definition instance
   *
   * @throws Exception if any IO or format errors occur
   */
  public Definition readWsdl( String strLocationURI ) throws Exception;


  /**
   * Helper to just extract an XML Schema (if one exists) from a wsdl document
   * @param urlWsdl a URL to the wsdl document
   * @return a Schema instance if one is defined in the types section of the WSDL document 
   * @throws Exception
   */
  public Schema extractSchema( URL urlWsdl ) throws Exception;
} // end interface WSDLReader{}

// *** End of WSDLReader.java ***

