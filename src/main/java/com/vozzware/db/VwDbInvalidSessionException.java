/*
 ============================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

  THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
  PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
  CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

  Source Name:  VwDbInvalidSessionException.java


 ============================================================================
*/

package com.vozzware.db;
import java.sql.SQLException;
import java.util.ResourceBundle;

/**
 * This Exception is thrown when a cached data source session is no longer valid.
 * This typically happens when a database has been brought down and restarted,
 * or the database server is no longer running.
 */
public class VwDbInvalidSessionException extends VwDataSourceException
{

  /**
   * Constructor
   *
   * @param sqle The initial SQLException thrown by the database driver
   */
  public VwDbInvalidSessionException( SQLException sqle )
  {
    super( sqle );

  } // end VwDbInvalidSessionException()


  /**
   * Returns the translated high level Vw description for this error. Use the SQLException
   * methods for specific driver descriptions
   *
   * @return A string containing the Vw translated description
   */
  public String getVwDesc()
  { return ResourceBundle.getBundle( "resources.properties.vwdb" ).getString( "Vw.Db.InvalidSession" ); }


} // end class VwDbInvalidSessionException{}


// *** End of VwDbInvalidSessionException.java ***

