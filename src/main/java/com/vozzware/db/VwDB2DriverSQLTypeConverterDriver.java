/*
 ============================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

  THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
  PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
  CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

  Source Name:   VwDB2DriverSQLTypeConverterDriver.java


 ============================================================================
*/


package com.vozzware.db;                         // Our package


public class VwDB2DriverSQLTypeConverterDriver implements VwSQLTypeConverterDriver
{

  public VwDB2DriverSQLTypeConverterDriver()
  {
  }

  public void convertFromNativeSQLType( VwColInfo colInfo ) throws Exception
  {
    // Look at the SQLType value.  If it is a SQLType 9, 10 or 11, convert it
    // to the appropriate Type.

    switch ( colInfo.getSQLType() )
    {
      case 9:

          colInfo.setSQLType( java.sql.Types.DATE );

        break;

      case 10:

          colInfo.setSQLType( java.sql.Types.TIME );

        break;

      case 11:

          colInfo.setSQLType( java.sql.Types.TIMESTAMP );

        break;

    } // End of switch

  } // End of convertFromNativeSQLType()


} // End of class VwDB2DriverSQLTypeConverterDriver{}


// *** End of VwDB2DriverSQLTypeConverterDriver.java ***

