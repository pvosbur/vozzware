/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: SchemaReader.java

============================================================================================
*/
package javax.xml.schema.util;

import javax.xml.schema.Schema;
import java.net.URL;

/**
 * This represents a genric interface to a Schema Reader
 */
public interface SchemaReader
{
  
  /**
   * Reads a schema document specified by the URL
   *
   * @param urlSchema The URL location of the schema document
   *
   * @return A Schema instance
   *
   * @throws Exception if any IO or format errors occur
   */
  public Schema readSchema( URL urlSchema ) throws Exception;

} // end interface SchemaReader{}

// *** End of SchemaReader.java ***

