/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

  THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
  PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
  CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

  Source File Name: VwSchemaDbTypesFactory.java

    Author:           P. VosBurgh


============================================================================================
*/

package com.vozzware.db;

import java.util.ResourceBundle;


public class VwSchemaDbTypesFactory
{

  /**
   * Constant for ORACLE database vendor
   */
  public static final int ORACLE = 0;

  /**
   * Constant for DB2/UDP database vendor
   */
  public static final int UDB = 1;

  /**
   * Constant for Sybase/Sql Server  database vendor
   */
  public static final int MYSQL = 2;

  /**
   * Constant for Sybase/Sql Server  database vendor
   */
  public static final int SQL_SERVER = 3;

  /**
   * Constant for Genericr  database vendor
   */
  public static final int GENERIC = 4;


  /**
   * Return an VwSchemaTypeConverter instance for the database type requested.
   *
   * @param nType One of the public defined database type constants defined in this class
   *
   * @exception Exception if the nType constant is invalid
   */
  public static VwSchemaTypeConverter getConverter( int nType  ) throws Exception
  {
     if ( nType < 0 && nType > 3 )
       throw new Exception( ResourceBundle.getBundle( "resources.properties.schemaMsgs" ).getString( "Vw.Schema.TypeConverter.InvalidDbType" ) );

     VwSchemaTypeConverter converter = null;

     switch( nType )
     {
       case ORACLE:

            converter =  VwOraSchemaTypeConverter.getInstance();
            break;

       default:

            converter =  VwStdSchemaTypeConverter.getInstance();

     } // end switch()

     return converter;


  } // end getConverter()


  /**
   * Return the String representation types in their ordinal values
   */
  public static String[] getSupportedDbTypes()
  { return new String[] { "ORACLE", "DB2", "SYBASE", "GENERIC" }; }



} // *** End of class VwSchemaDbTypesFactory{}

// *** End Of VwSchemaDbTypesFactory.java