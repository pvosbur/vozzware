/*
 ============================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

  THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
  PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
  CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

  Source Name:  VwDbDupKeyException.java


 ============================================================================
*/

package com.vozzware.db;
import java.sql.SQLException;
import java.util.ResourceBundle;

/**
 * This Exception is thrown when an a dup key constraint violation occurs.
 */
public class VwDbDupKeyException extends VwDataSourceException
{

  /**
   * Constructor
   *
   * @param sqle The initial SQLException thrown by the database driver
   */
  public VwDbDupKeyException( SQLException sqle )
  {
    super( sqle );

  } // end VwDbDupKeyException()


  /**
   * Returns the translated high level Vw description for this error. Use the SQLException
   * methods for specific driver descriptions
   *
   * @return A string containing the Vw translated description
   */
  public String getVwDesc()
  { return ResourceBundle.getBundle( "resources.properties.vwdb" ).getString( "Vw.Db.DupKey" ); }
  

} // end class VwDbDupKeyException{}


// *** End of VwDbDupKeyException.java ***

