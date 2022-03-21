/*
 ============================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

  THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
  PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
  CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

  Source Name:  VwDriverTranslationMsgs


 ============================================================================
*/

package com.vozzware.db;                   // The package this class belongs to

import java.sql.SQLException;

/**
 * This interface provides a template that a driver translation class must use
 * to provide a generic failure reason through the integer constants defined in this
 * interface. Because different database vendors use very different means of
 * communicating SQLExcetion failure reasons, a translation implementaion class
 * can translate vendor specific SQLState, Vendor codes, or error descriptions
 * to a common constant that an application can use to better describe the
 * database failure. NOTE!! The name of the implementation class is derived by
 * adding the string TranslationMsgs to the DbProductName as specified
 * in the VwDbMgrClass. For example if the DbProductName is Oracle, then the
 * expected implementation class is OracleTranslationMsgs.
 */
public interface VwDriverTranslationMsgs
{
  /**
   * Constant to indicate that the the message is untranslatable
   */
  public static final int UNKNOWN = 0;


  /**
   * Constant to indicate that a database server is down/unavailable
   */
  public static final int NOT_AVAILABLE = 1;


  /**
   * Constant to indicate that a chched session is no longer valid
   */
  public static final int INVALID_SESSION = 2;


  /**
   * Constant to indicate the user id or password is invalid
   */
  public static final int INVALID_LOGIN = 3;


  /**
   * Constant to indicate that a primary key (dup key) constraint has been raised
   */
  public static final int DUP_KEY = 4;


  /**
   * Constant to indicate a general error and to use the vendor's description
   */
  public static final int GENERAL_ERROR = 5;


  /**
   * Translate a vendor specififc SQLException reason to one of the constants
   * defined in this interface.
   *
   * @param sqlException The SQLException thrown by the driver
   *
   * @return One of the failure constants defined in this interface
   */
  public int getReason( SQLException ex );


} // end of interface VwDriverTranslationMsgs{}

// *** End VwDriverTranslationMsgs.java ***

