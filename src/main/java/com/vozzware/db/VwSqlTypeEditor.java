/*
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

  THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
  PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
  CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

  Source Name:   VwSqlTypeEditor.java


 ============================================================================
*/


package com.vozzware.db;                         // This package

import com.vozzware.util.VwExString;

import java.sql.Types;

/**
 * This is a static class that has a method to validate a String object and
 * an SQL data type from the Types class.
 */
public class VwSqlTypeEditor
{
  private static String m_strErrDesc = "";               // Err desc of failing edit

  private static java.util.ResourceBundle m_msgs = null; // Resource bundle for error meesages

  static
  {
    m_msgs = java.util.ResourceBundle.getBundle( "com.vozzware.db.vwdb" );
  }


  /**
   * Gets the error description of the last failing test
   *
   * @return A string with the last error desc, or an empty string if no error
   */
  public static String getErrDesc()
  { return m_strErrDesc; }

   
  /**
   * Tests the validity of the data given an SQL type
   *
   * @param strData - The data to test
   * @param sType - One of the Types constants
   *
   * @return True if the data is valid; False if the data fails.  The Error Description is set
   * if the test fails.
   */
  public static boolean isValid( String strData, short sType )
  {
    m_strErrDesc = "";  // Clear any left over errors

    switch( sType )
    {
	   case Types.TINYINT:
	   case Types.SMALLINT:
	   case Types.INTEGER:
	   case Types.BIGINT:

           if ( !VwExString.isNumeric( strData ) || strData.indexOf( '.' ) >= 0 )
           {
             m_strErrDesc = m_msgs.getString( "VwDb.NotInteger" );
             return false;
           }

           break;

     case Types.FLOAT:
	   case Types.REAL:
	   case Types.DOUBLE:
	   case Types.NUMERIC:
	   case Types.DECIMAL:

           if ( !VwExString.isNumeric( strData )  )
           {
             m_strErrDesc = m_msgs.getString( "VwDb.NotNumeric" );

             return false;
           }
           break;

    } // end switch()

    return true;

  } // end isValid()


} // end class VwSqlTypeEditor{}


// *** End of VwSqlTypeEditor.java ***
