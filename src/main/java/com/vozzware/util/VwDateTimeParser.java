/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwDateTimeParser.java

============================================================================================
*/


package com.vozzware.util;

import java.util.Date;
import java.util.StringTokenizer;


/**
 * This class takes a string containing an input Format Specification, and a Date/Time in string form, and parses
 * the Date/Time, converting the string data to binary.  The binary data is stored in an array.  The input Format
 * Specification is based on the 'strftime' C runtime library function.
 *
 * The supported % formatters are listed below:
 *
 * %a Abbreviated weekday name
 *
 * %A Full weekday name
 *
 * %b Abbreviated month name
 *
 * %B Full month name
 *
 * %d Day of month as decimal number (01 - 31)
 *
 * %H Hour in 24-hour format (00 - 23)
 *
 * %I Hour in 12-hour format (01 - 12)
 * 
 * %p Am/Pm indicator
 *
 * %m Month as decimal number (01 - 12)
 *
 * %M Minute as decimal number (00 - 59)
 *
 * %S Second as decimal number (00 - 59)
 *
 * %t Timezone Offset
 *
 * %y Year without century, as decimal number (00 - 99)
 *
 * %Y Year with century, as decimal number
 *
 */
class VwDateTimeParser
{
  private static String m_strDays[] = { "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };
  private static String m_strMonths[] = {"January", "February", "March", "April", "May", "June", "July", "August",
                                         "September", "October", "November", "December" };           // Full name of month

  private static final Date  m_curDate = new Date();

  private static String m_strErrDesc;

  /**
    * Get the error description string
    *
    * @return The last error desc string
   */

  public static String getErrDesc()
  { return m_strErrDesc; }

  /**
   * Parses a date string into an integer array based upon the Format Specifier string, strFormat.
   * The array indices are setup as follows:
   *
   * 0 = Month
   * 1 = Day
   * 2 = Year/Century
   * 3 = Hours
   * 4 = Minutes
   * 5 = Seconds
   * 6 = Day of Week Sunday = 1
   *
   * Any array data that is not otherwise initialized is set to -1.
   *
   * @param strDate The Date in string form, i.e., "5/15/1997"
   * @param strFormat The Format Specifier for the date, i.e., "%m/%d/%Y" describes above
   * date.
   *
   * @return The integer array containing the binary values for the date, or null if the date
   * and format don't match.
  */

  public static final int[] parse( String strDate, String strFormat )
  {
    int[] anDate = new int[ VwDate.DATE_ARRAY_SIZE ];

    for ( int x = 0; x < anDate.length; x++ )
      anDate[ x ] = -1;

    anDate[3 ] = anDate[ 4 ] = anDate[ 5 ] = anDate[ 7 ] = 0;     // Set time components to zero

    // *** Create tokenizers to parse format string and date

    StringTokenizer stFormat = new StringTokenizer( strFormat, "%" );
    StringTokenizer stDate = new StringTokenizer( strDate, "AaPpT/-,.: " );

    while( stFormat.hasMoreTokens() )
    {
      String strPiece = stFormat.nextToken();
      String strDatePiece;

      if ( !stDate.hasMoreTokens() )
      {
        //m_strErrDesc = "Missing Date Components According To Specified Date Format";
        return anDate;
      }

      strDatePiece = stDate.nextToken();

      int nSwitchNdx = 0;
      
      if ( strPiece.charAt( 0 ) == '-') // bypass suppress leading zero indicator
      {
        ++nSwitchNdx;
      }
      
      switch( (int)strPiece.charAt( nSwitchNdx ) )
      {
              
        case (int)'m':

             try
             {
               anDate[ 0 ] = Integer.parseInt( strDatePiece );
              
             } catch ( NumberFormatException ne )
             {
              m_strErrDesc = "Months Must Be Numeric";
              return null; // Invalid data
             }

             break;

        case (int)'d':

             try
             {
               anDate[ 1 ] = Integer.parseInt( strDatePiece );
             }
             catch ( NumberFormatException ne )
             {
               m_strErrDesc = "Days Must Be Numeric";
               return null; // Invalid data
             }

             break;

        case (int)'y':
        case (int)'Y':

             try
             {
               anDate[ 2 ] = Integer.parseInt( strDatePiece );
               
               
             }
             catch ( NumberFormatException ne )
             {
               m_strErrDesc = "Years Must Be Numeric";
               return null; // Invalid data
             }

             if ( anDate[2] < 1000 && strPiece.charAt( 0 ) == 'Y' )
             {
               m_strErrDesc = "Century Format Specified But Years is < 1000";
               return null;
             }

             break;

        case (int)'a':
        case (int)'A':

             int nDayNbr = getDayNbr( strDatePiece );
             if ( nDayNbr < 0 )
             {
               m_strErrDesc = "Invalid Day Name";

               return null;
             }

             anDate[ 6 ] = nDayNbr;

             break;

        case (int)'b':
        case (int)'B':

             int nMonthNbr = getMonthNbr( strDatePiece );
             if ( nMonthNbr < 0 )
             {
               m_strErrDesc = "Invalid Month Name";

               return null;
             }

             anDate[ 0 ] = nMonthNbr;

             break;

        case (int)'H':
        case (int)'I':

             try
             {
               anDate[ 3 ] = Integer.parseInt( strDatePiece );
               if ( strPiece.charAt( nSwitchNdx ) == 'H' )
               {
                 // if %H format is specified and there is a pm on the date, convert hours to 24 hour format if not already done. i.e. hours is < 12 with pm on the date
                 if ( anDate[ 3 ] < 12 && strDate.toLowerCase().indexOf( "pm" ) > 0 )
                   anDate[ 3 ] += 12;
               }
               
               
             }
             catch ( NumberFormatException ne )
             {
                m_strErrDesc = "Hours Must Be Numeric";
                return null; // Invalid data
             }

             break;

        case (int)'M':

             try
             {
               anDate[ 4 ] = Integer.parseInt( strDatePiece );
               
             }
             catch ( NumberFormatException ne )
             {
               m_strErrDesc = "Minutes Must Be Numeric";
               return null; // Invalid data
             }

             break;

        case (int)'S':

             try
             {
               anDate[ 5 ] = Integer.parseInt( strDatePiece );
               
               
             }
             catch ( NumberFormatException ne )
             {
               m_strErrDesc = "Seconds Must Be Numeric";
               return null; // Invalid data
             }

             
             break;

         case (int)'p':
         case (int)'P':

              if ( strDate.toLowerCase().indexOf( "pm") > 0 )
                anDate[ 8 ] = 1;  // Turn on flag to indeicate a pm format string
              break;
         
         case (int)'t':

              anDate[ 7 ] = Integer.parseInt( strDatePiece ) * 3600000;

              int nPos = strDate.lastIndexOf( strDatePiece ) - 1;

              if ( strDate.charAt( nPos ) == '-' )
                anDate[ 7 ] *= -1;

             break;

         default:

             m_strErrDesc = "Invalid Format Specifier";
             return null;       // invalid format specifier

      } // end switch()

    } // end while( stFormat.hasTokens )

    // Fill in defualts for mask components not specified

    if ( anDate[0] < 0 )
      anDate[0] = 1;

    if ( anDate[1] < 0 )
      anDate[1] = 1;

    if ( anDate[2] < 0 )
      anDate[2] = 1970;

    return anDate;

  } // end parse()


  /**
   * Formats a Date/Time string based upon the given date array values and the
   * Format Specifier string.
   *
   * @param anDateArray integer array containing the binary date/time values.  See parse(), above, for
   * the array index to date/time component mapping.
   *
   * @param strFormat Format Specifier string used to format the date
   *
   * @return The Formatted Date/Time string
  */

  public static final String format( int[] anDateArray, String strFormat )
  {
    StringBuffer sbDate = new StringBuffer();
    

    int nStartNdx = 0;

    int nFormatLen = strFormat.length();

    int nOffset = strFormat.indexOf( "%" );
    if ( nOffset < 0 )
    {
      m_strErrDesc = "Invalid Format Specifier";
      return null;
    }

    while( nOffset >= 0 )
    {
      boolean fSuppressZeroes = false;
      // *** Get any characters prior to start of format specifier

      sbDate.append( strFormat.substring( nStartNdx, nOffset ) );

      ++nOffset;

      if ( strFormat.charAt( nOffset ) == '-')
      {
        fSuppressZeroes = true;
        ++nOffset;
      }
      
      switch( (int)strFormat.charAt( nOffset ) )
      {
        case (int)'m':

             if ( anDateArray[ 0 ] < 0 )
             {
               m_strErrDesc = "No Month Value Specified On Input Date";
               return "";
             }

             // *** Pad with leading zero

             if ( !fSuppressZeroes && anDateArray[ 0 ] < 10 )
               sbDate.append( "0" );

             sbDate.append( String.valueOf( anDateArray[ 0 ] ) );
             break;

        case (int)'d':

             if ( anDateArray[ 1 ] < 0 )
             {
               m_strErrDesc = "No Day Value Specified On Input Date";
               return "";
             }

             // *** Pad with leading zero

             if ( !fSuppressZeroes && anDateArray[ 1 ] < 10 )
               sbDate.append( "0" );

             sbDate.append( String.valueOf( anDateArray[ 1 ] ) );
             break;

        case (int)'y':

             if ( anDateArray[ 2 ] < 0 )
             {
               m_strErrDesc = "No Year Value Specified On Input Date";
               return "";
             }

             StringBuffer sbTemp = new StringBuffer();

             // *** Pad with leading zero

             if ( !fSuppressZeroes && anDateArray[ 2 ] < 10 )
               sbTemp.append( "0" );

             sbTemp.append( String.valueOf( anDateArray[ 2 ] ) );

             if ( anDateArray[ 2 ] > 99 )
               sbTemp = new StringBuffer( sbTemp.toString().substring( 2, 4 ) );

             sbDate.append( sbTemp );
             break;

        case (int)'Y':

             if ( anDateArray[ 2 ] < 0 )
             {
               m_strErrDesc = "No Year Value Specified On Input Date";
               return "";
             }

            if ( !fSuppressZeroes && anDateArray[ 2 ] < 10 )
              sbDate.append( "0" );

            sbDate.append( String.valueOf( anDateArray[ 2 ] ) );
            break;

        case (int)'a':

             if ( anDateArray[ 6 ] < 0 )
             {
               m_strErrDesc = "No Weekday Value Specified On Input Date";
               return null;
             }

             sbDate.append( m_strDays[ (anDateArray[ 6 ] - 1) ].substring( 0, 3 ) );
             break;


        case (int)'A':

             if ( anDateArray[ 6 ] < 0 )
             {
               m_strErrDesc = "No Weekday Value Specified On Input Date";
               return "";
             }

             sbDate.append( m_strDays[ (anDateArray[ 6 ] - 1) ] );
             break;


        case (int)'b':

             if ( anDateArray[ 0 ] < 0 )
             {
               m_strErrDesc = "No Month Value Specified On Input Date";
               return "";
             }

             sbDate.append( m_strMonths[ (anDateArray[ 0 ] - 1) ].substring( 0, 3 ) );
             break;

        case (int)'B':

             if ( anDateArray[ 0 ] < 0 )
             {
               m_strErrDesc = "No Month Value Specified On Input Date";
               return null;
             }

             sbDate.append( m_strMonths[ (anDateArray[ 0 ] - 1) ] );
             break;

        case (int)'H':
        case (int)'I':

             if ( anDateArray[ 3 ] < 0 )
               anDateArray[ 3 ] = 0;

             // *** Pad with leading zero

             if ( !fSuppressZeroes && anDateArray[ 3 ] < 10 )
               sbDate.append( "0" );

             if ( strFormat.charAt( nOffset ) == 'I')
             {
               if ( anDateArray[ 3 ] > 12 )
                 sbDate.append( String.valueOf( anDateArray[ 3 ] - 12 )  );
               else
                 sbDate.append( String.valueOf( anDateArray[ 3 ] )  );
             }
             else
               sbDate.append( String.valueOf( anDateArray[ 3 ] )  );
                           
             
             break;

        case (int)'M':

             if ( anDateArray[ 4 ] < 0 )
               anDateArray[ 4 ] = 0;
        
             // *** Pad with leading zero

             if ( !fSuppressZeroes && anDateArray[ 4 ] < 10 )
               sbDate.append( "0" );
             
             sbDate.append( String.valueOf( anDateArray[ 4 ] ) );
             break;

        case (int)'S':

             if ( anDateArray[ 5 ] < 0 )
               anDateArray[ 5 ] = 0;
               

             // *** Pad with leading zero

             if ( !fSuppressZeroes && anDateArray[ 5 ] < 10 )
               sbDate.append( "0" );

             sbDate.append( String.valueOf( anDateArray[ 5 ] ));
             break;

        case (int)'p':

             if ( strFormat.indexOf( "%H" ) >= 0 )
               break;   // 24 hour formate specified, ignore am/pm indicator
             // if time is in 24 hour format ignore the am.pm indicator
             if ( anDateArray[ 3 ] > 12  )
               sbDate.append( "pm" );
             else
               sbDate.append( "am" );
               
             break;
        
        case (int)'t':

             String strOffSign = null;
             if ( anDateArray[ 7 ] < 0 )
               strOffSign = "-";
             else
               strOffSign = "+";

             String strOffAmt = String.valueOf( Math.abs( anDateArray[ 7 ] )  / 3600000 );
             if ( strOffAmt.length() == 1 )
               strOffAmt = "0" + strOffAmt;

             sbDate.append( strOffSign + strOffAmt + ":00" );

             break;


         default:

             m_strErrDesc = "Invalid Format Specifier";
             return null;       // invalid format specifier

      } // end switch()

      // *** Copy Rest for format string

      nStartNdx = ++nOffset;
      nOffset = strFormat.indexOf( "%", nStartNdx );

    } // end while( stFormat.hasTokens )

    // *** Add in any format string characters following last % sign
    if ( nStartNdx < nFormatLen )
      sbDate.append( strFormat.substring( nStartNdx, nFormatLen ) );

    return sbDate.toString();

  } // end format()


  /*
   * Returns the index number for a day of the week where Sunday = 1
   *
   * @param strDay - String containing the day of the week
   *
   * @return The day number, or -1 if the day string is invalid
   */

   private static int getDayNbr( String strDay )
   {
     int nDayLen = strDay.length();

     for ( int x = 0; x < 7; x++ )
     {
       if ( strDay.equalsIgnoreCase( m_strDays[ x ].substring( 0, nDayLen ) ) )
         return x + 1;
     }

     return -1;         // Not Found

   } // end getDayNbr()


  /*
   * Returns the index number for the abbreviated or full month name
   *
   * @param strMonth - A string containing the name of the month
   *
   * @return The month number, or -1 if the month name is invalid
   */

   private static int getMonthNbr( String strMonth )
   {
     int nMonthLen = strMonth.length();

     for ( int x = 0; x < 12; x++ )
     {
       
       if ( m_strMonths[ x ].length() >= nMonthLen && strMonth.equalsIgnoreCase( m_strMonths[ x ].substring( 0, nMonthLen ) ) )
         return x + 1;
     }

     return -1;         // Not Found

   } // end getMonthNbr()


} // end class VwDateTimeParser{}


// *** End of VwDateTimeParser.java
