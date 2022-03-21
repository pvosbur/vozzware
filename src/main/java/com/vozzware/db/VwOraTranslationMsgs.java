/*
 ============================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

  THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
  PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
  CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

  Source Name:  VwOraTranslationMsgs


 ============================================================================
*/

package com.vozzware.db;

import java.sql.SQLException;

/**
 * This implements the VwDriverTranslationMsgs interface for the Oracle Jdbc Driver
 * SQLException messages.
 */
public class VwOraTranslationMsgs implements VwDriverTranslationMsgs
{

  /**
   * Translate a vendor specififc SQLException reason to one of the constants
   * defined in the VwDriverTranslationMsgs interface.
   *
   * @param sqlException The SQLException thrown by the driver
   *
   * @return One of the failure constants defined in the VwDriverTranslationMsgs
   * interface.
   */
  public int getReason( SQLException ex )
  {
    //System.out.println( "OraTranslation messages: Error code: " + ex.getErrorCode()
    //                    + "\nMsg: " + ex.getMessage() );

    switch ( ex.getErrorCode() )
    {
       case 0:

            String strMsg = ex.getMessage().toLowerCase();

            if ( strMsg.startsWith( "refused" ) )
              return NOT_AVAILABLE;

            if ( strMsg.startsWith( "connection" ) ||
                 strMsg.indexOf( "broken pipe" ) > 0  )
              return INVALID_SESSION;

            return NOT_AVAILABLE;

       case 17002:

            strMsg = ex.getMessage().toLowerCase();
            if ( strMsg.indexOf( "connection reset" ) > 0 ||
                 strMsg.indexOf( "broken pipe" ) > 0  )
              return INVALID_SESSION;

            if ( strMsg.indexOf( "network adapter" ) >= 0 ||
                 strMsg.indexOf( "connection refused" ) >= 0 )

              return NOT_AVAILABLE;

            break;

       case 1017:
            return INVALID_LOGIN;

       case 17410:

            return INVALID_SESSION;

       case 1:

            return DUP_KEY;

    } // end switch()

    return GENERAL_ERROR;

  } // end getReason()


} // end of class VwOraTranslationMsgs{}


// *** End VwOraTranslationMsgs.java ***

