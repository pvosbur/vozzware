/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: Types.java

============================================================================================
*/
package javax.wsdl;

import javax.wsdl.extensions.ExtensibilityElementSupport;
import javax.xml.schema.Schema;
import java.util.List;

/**
 * This interface represents Types section of a WSDL document.
 *
 * @author Peter VosBurgh
 */
public interface Types extends WSDLCommon, ExtensibilityElementSupport
{

  /**
   * Sets the base Schema object if the type is defined as an XML Schema
   *
   * @param schema The base Schema object (defined in jschema)
   */
  public void addSchema( Schema schema );


  /**
   * Gets the base Schema object if the type is defined as an XML Schema. If there a list of schemas, the first one is returned.
   * @return the base Schema object if the type is an XML Schema or null if a different type
   */
  public Schema getSchema();

  
  /**
   * Gets the schema count
   * @return the number of schemsa defined in the types section
   */
  public int getSchemaCount();
  
  
  /**
   * Gets the list of all XML schemas defined
   * @return
   */
  public List getSchemas();
  
  /**
   * Returns true if the type defined is an XML Schema
   *
   * @return true if the type defined is an XML Schema, false otherwise
   */
  public boolean isSchema();

} // end interface Types{}

// *** End of Types.java ***