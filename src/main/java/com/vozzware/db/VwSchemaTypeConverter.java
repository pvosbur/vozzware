/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

  THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
  PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
  CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

    Source File Name: VwSchemaTypeConverter.java

    Author:           P. VosBurgh


============================================================================================
*/

package com.vozzware.db;


public interface VwSchemaTypeConverter
{

  /**
   * Converts a schema type into a SQL type
   *
   * @param strSchemaType The Xml Schema data type to convert
   *
   * @return an VwColInfo instance with the SQl Type and Sql type name defined
   */
  public VwColInfo convertType( String strSchemaType );

} // *** End of interface VwSchemaTypeConverter{}

// *** End Of VwSchemaTypeConverter.java