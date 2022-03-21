/*
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

  THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
  PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
  CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

  Source Name:  VwDbServerNotAvailException.java


 ============================================================================
*/

package com.vozzware.db;
import java.sql.SQLException;
import java.util.ResourceBundle;

/**
 * This Exception is thrown when a database server is not responding to a client
 * request. Typically it means the database server is down or not responding.
 */
public class VwDbServerNotAvailException extends VwDataSourceException
{

  /**
   * Constructor
   *
   * @param sqle The initial SQLException thrown by the database driver
   */
  public VwDbServerNotAvailException( SQLException sqle )
  {
    super( sqle );

  } // end VwDbServerNotAvailException()


  /**
   * Returns the translated high level Vw description for this error. Use the SQLException
   * methods for specific driver descriptions.
   *
   * @return A string containing the Vw translated description
   */
  public String getVwDesc()
  { return ResourceBundle.getBundle( "com.vozzware.db.vwdb" ).getString( "Vw.Db.NotAvail" ); }


} // end class VwDbServerNotAvailException{}


// *** End of VwDbServerNotAvailException.java ***

