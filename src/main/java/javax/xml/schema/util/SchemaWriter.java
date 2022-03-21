/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: SchemaWriter.java

============================================================================================
*/
package javax.xml.schema.util;

import javax.xml.schema.Schema;
import java.io.File;

/**
 * Interface to define a Schema writer
 */
public interface SchemaWriter
{
  
  /**
   * Writes the schema object to a String
   * @param schema The Schema object to write
   * @return A String representation of the XML schema
   * @throws Exception
   */
  public String writeSchema( Schema schema ) throws Exception;
  
  
  /**
   * Writes the Schema object to the specified file
   * @param schema The Schema object to write
   * @param fileSchema The file to write
   * @throws Exception if any io error occur
   */
  public void writeSchema( Schema schema, File fileSchema ) throws Exception;
  
} // end interface SchemaWriter{}

// *** End of SchemaWriter.java ***

