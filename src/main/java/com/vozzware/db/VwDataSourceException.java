/*
 ============================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

  THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
  PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
  CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

  Source Name:  VwDataSourceException.java


 ============================================================================
*/

package com.vozzware.db;
import java.sql.SQLException;


/**
 * This class is the base class for all Vw SQL exceptions. Each type of VwDatasource
 * exception must define the getVwDesc() method for the specific exception type.
 *
 */
public abstract class VwDataSourceException extends SQLException
{
  private SQLException m_sqle;
  
  /**
   * Constructor
   *
   * @param sqle The initial SQLExceotion thrown by the database driver
   */
  public VwDataSourceException( SQLException sqle )
  {
    super();

    m_sqle = sqle;
    
    // *** Set the super class with all exceptions initially thrown to preserve the original
    // *** reasons, states, codes

    /*
    setNextException( sqle );

    SQLException sx = null;

    // *** Get any more exceptions associated with this error
    while ( ( sx = sqle.getNextException() ) != null )
      setNextException( sx );
    */
    
  } // end VwDataSourceException()


  public String toString()
  { 
    SQLException sx = null;
    String strError = this.getClass().getName() + "\n" + "Vendor Code: " + m_sqle.getErrorCode() +
     "\nReason: " + m_sqle.toString();
    
    while ( ( sx = m_sqle.getNextException() ) != null )
      strError += "\n" + "Vendor Code: " + sx.getErrorCode() +
      "\nReason: " +sx.toString();

    return strError;
    
  }
  
  /**
   * Returns the translated high level Vw description for this error. Use the SQLException
   * methods for specific driver descriptions.
   *
   * @return A string containing the Vw translated description
   */
  public abstract String getVwDesc();

} // end class VwDataSourceException{}


// *** End of VwDataSourceException.java ***

