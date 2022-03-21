/*
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

  THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
  PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
  CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

  Source Name:   VwSqlTypeConverterDriver.java


 ============================================================================
*/


package com.vozzware.db;                         // Our package


/**
 * This interfaces is used to convert non-standard SQLTypes from the
 * database vendors' SQLType values to a SQLType value defined in the
 * java.sql.Types class.
 *
 * Classes which implement this interface are dynamically located and
 * instantiated by the ITCDatabase class when they are found on the class path.
 * The classes are located by computing the driver name for the database and
 * driver type via the following formula.  The format of the name of a class
 * which implements this interface is Vw<DriverName>SQLConversionDriver, stored
 * in the Vw<DriverName>SQLConversionDriver.class file.
 *
 */

public interface VwSQLTypeConverterDriver
{

  /**
   * This method uses the VwColumnInfo structure passed in to compute
   * the new type information.  This type information replaces the
   * information held in the class.
   *
   * @param colInfo - An VwColumnInfo object with the column type information
   */

	public void convertFromNativeSQLType( VwColInfo colInfo ) throws Exception;


} // end public interface VwSqlTypeConverterDriver{}


// *** end of VwSqlTypeConverterDriver.java ***

