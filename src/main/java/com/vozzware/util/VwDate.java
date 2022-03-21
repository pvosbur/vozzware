/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwDate.java

============================================================================================
*/

package com.vozzware.util;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

class VwMonths implements Serializable
{
  /**
	 * 
	 */
	private static final long serialVersionUID = -6404761604557365249L;
	
private static String  m_strFullName[]  = { "January", "February", "March",
                                              "April", "May", "June",
                                              "July", "August", "September",
                                              "October", "November", "December" };  // Full month name
  private static String  m_strShortName[] = { "JAN", "FEB", "MAR",
                                              "APR", "MAY", "JUN",
                                              "JUL", "AUG", "SEP",
                                              "OCT", "NOV", "DEC" };                // Abbreviated name
  private static short   m_sMonthNbr[] = {1,2,3,4,5,6,7,8,9,10,11,12};              // Month number
  private static int     m_nNbrDays[]  = {31,28,31,30,31,30,31,31,30,31,30,31};     // Number of days in the month


  /**
   * Returns Full Month name, given a month number
   *
   * @param nMonth - The month number to get String representation for
   *
   * @return String containing the full month name, or null if the month number is inavlid
   */
  static String getFullName( int nMonth )
  {
    if ( nMonth < 1 && nMonth > 12 )
      return null;

    return m_strFullName[ (nMonth - 1 ) ];

  } // end getFullName



  /**
   * Returns the 3 character abbreviated month name, given a month number
   *
   * @param nMonth - The Month number to get the String representation for
   *
   * @return String containing the abbreviated month name, or null if the month number is invalid
   */
  static String getShortName( int nMonth )
  {
    if ( nMonth < 1 && nMonth > 12 )
      return null;

    return m_strShortName[ (nMonth - 1 ) ];

  } // end getShortName



  /**
   * Returns the number of days in the month, given a month number
   *
   * @param nMonth - The Month number to get the number of days for
   *
   * @return An integer containing the number of days in the month, or -1 if the month number
   *         is invalid
   */
  static int getNbrDays( int nMonth )
  {
    if ( nMonth < 1 && nMonth > 12 )
      return -1;

    return m_nNbrDays[ (nMonth - 1 ) ];

  } // end getNbrDays



  /**
   * Returns the number of days in the month, given a month name
   *
   * @param strMonthName - The month name to get the number of days for
   *
   * @return An integer containing nbr of days in the month, or -1 if the month number
   *         is invalid
   */
  static int  getNbrDays( String strMonthName )
  { return getNbrDays( getMonthNbr( strMonthName ) ); }



  /**
   * Return the month number, given a short (3 character) or full month name
   *
   * @param strMonthName - The month name to get month number for
   *
   * @return An integer containing month number, or -1 if month name is inavlid
   */
  static int getMonthNbr( String strMonthName )
  {

    int nLen = strMonthName.length();

    for ( int x = 0; x < 12; x++ )
    {

      if ( nLen > 3 )
      {
        if ( m_strFullName[ x ].equalsIgnoreCase( strMonthName ) )
          return x + 1;
      }

      else
      {
        if ( m_strShortName[ x ].equalsIgnoreCase( strMonthName ) )
          return x + 1;
      }

    } // end for

    return -1;          // Invalid Month Name

  } // end getMonthNbr()

} // end class VwMonths()



/**
 * Date conversions & validations
 *
 * The format string specifications are based on the 'strftime' C runtime library function.
 * You can suppress leading zeroes on the m,d,y,Y,H,M,S specififiers by putting a - in front<br>
 * of the format specifier. i.e. %-m, %-d etc...<br>
 * The supported % formatters are listed below:<br>
 *
 * %a - Abbreviated weekday name<br>
 *
 * %A - Full weekday name<br>
 *
 * %b - Abbreviated month name<br>
 *
 * %B - Full month name<br>
 *
 * %p   The am/pm string for the 12 hour format only
 * 
 * %d - Day of month as decimal number (01 - 31)<br>
 *
 * %H - Hour in 24-hour format (00 - 23)<br>
 *
 * %I - Hour in 12-hour format (01 - 12)<br>
 *
 * %m - Month as decimal number (01 - 12)<br>
 *
 * %M - Minute as decimal number (00 - 59)<br>
 *
 * %S - Second as decimal number (00 - 59)<br>
 *
 * %t - Timezone offset from GMT<br>
 *
 * %y - Year without century, as decimal number (00 - 99)<br>
 *
 * %Y - Year with century, as decimal number<br>
 *
 * @version 1.0
*/
public class VwDate extends Object implements Serializable, Cloneable
{

  static final int DATE_ARRAY_SIZE = 9;     // Array Size needed to hold Date/Time components

  private String        m_strFormat;                // Default Date format
  
  private boolean       m_fIsLenient = true;        // The leniency of the date validation defaulted to false or not lenient

  private int[]         m_anTime = new int[6];      // Array to hold time values for

  private int[]         m_anDate = new int[3];      // Array to hold Month, Day, Year date values

  private int           m_nJulDate = -1;            // Julian day number for this date

  private String        m_strDate;                  // If database only supports string format dates, then this ptr is used

  private boolean       m_fIsStringSqlDate;         // Set to true if internal sql date is a string format date

  private String        m_strErrDesc;               // Description of last known error

  private static String m_strStaticErrDesc;         // Error desc for the static functions

  private static final String[] m_astrWeekDays = { null, "Sunday", "Monday", "Tuesday",
                                                   "Wednesday", "Thursday", "Friday",
                                                   "Saturday" };
  // *** Preformated standard Date strings

  /**
   * Standard USA Date format MM-DD-YYYY: 10-15-1997
   */
  public static final String  USADATE = "%m-%d-%Y";

  /**
   * Standard USA Date format MM-DD-YYYY Hours:minutes:Seconds : 10-15-1997 10:30:02
   */
  public static final String  USADATE_TIME = "%m-%d-%Y %H:%M:%S";

  
  /**
   * Usa long date in the form June 1, 2007
   */
  public static final String  USALONG = "%B %-d, %Y";
  
  /**
   * MM/DD/YY Format with 2 digit Year: 10/15/97
   */
  public static final String  MMDDDYY = "%m-%d-%y";

  /**
   * Standard European Date format DD/MM/YYYY with 4 digit year: 15/10/1997
   */
  public static final String  EURODATE = "%d/%m/%Y";

  /**
   * Standard European Date format DD/MM/YYYY Hours:minutes:Seconds with 4 digit year: 15/10/1997 10:30:01
   */
  public static final String  EURODATE_TIME = "%d/%m/%Y %H:%M:%S";
  
  /**
   * International Standard Date format YYYY-MM-DD: 1997-10-15
   */
  public static final String  ISODATE = "%Y-%m-%d";

  /**
   * YY/MM/DD format: 97/10/15
   */
  public static final String  YYMMDD  = "%y/%m/%d";


  /**
   * YY/MM/DD format: 97/10/15
   */
  public static final String  YYYYMMDD  = "%Y/%m/%d";

  /**
   * Oracle default SQL Date format: 15-OCT-97
   */
  public static final String  ORADATE  = "%d-%b-%y";

  /**
   * IETF Date format: Mon, 22 SEP 1997 8:55:22
   */
  public static final String  IETFDATE = "%a, %d %b %Y %H:%M:%S";

  /**
   * GMT Date format: Mon, 22 SEP 1997 8:55:22 GMT
   */
  public static final String  GMTDATE = "%a, %d %b %Y %H:%M:%S GMT";


  /**
   * GMT Date format: Mon, 22 SEP 1997 8:55:22 GMT
   */
  public static final String  GMTDATE2YR = "%A, %d-%b-%y %H:%M:%S GMT";


  /**
   * Timestamp in restricted ISO 8601 format 2002-07-14T08:57:01-5:00
   */
  public static final String  ISO8601 = "%Y-%m-%dT%H:%M:%S%t";

  // *** Day of week constants

  /**
   * Invalid Day of Week
   */
  public static final int INVALIDDAY = 0;

  /**
   *  Day Number constant for Sunday
   */
  public static final int SUNDAY = 1;

  /**
   *  Day Number constant for Monday
   */
  public static final int MONDAY = 2;

  /**
   *  Day Number constant for Tuesday
   */
  public static final int TUESDAY = 3;

  /**
   *  Day Number constant for Wednesday
   */
  public static final int WEDNESDAY = 4;

  /**
   *  Day Number constant for Thuraday
   */
  public static final int THURSDAY = 5;

  /**
   *  Day Number constant for Friday
   */
  public static final int FRIDAY = 6;

  /**
   *  Day Number constant for Saturday
   */
  public static final int SATURDAY = 7;


  /**
   * Builds instance from a Calendar date
   * @param cal
   */
  private void assign( Calendar cal )
  {
    m_strDate = null;
    m_fIsStringSqlDate = false;

    m_strFormat = USADATE;

    if ( !setComponents( cal.get( Calendar.MONTH ) + 1, cal.get(Calendar.DATE ), cal.get( Calendar.YEAR ),
         cal.get( Calendar.HOUR_OF_DAY ), cal.get( Calendar.MINUTE ), cal.get( Calendar.SECOND ), null ) )
       m_nJulDate = -1;

    m_strDate = null;

    m_fIsStringSqlDate = false;
    
  }
  /*
   *  Assigns a value to this instance from another date
   *
   *  @param VwDate date - The data to be assigned to this instance
   *
  */
  private void assign( VwDate date )
  {
    m_nJulDate = date.m_nJulDate;

    m_fIsStringSqlDate = date.m_fIsStringSqlDate;

    m_strDate = date.m_strDate;

    m_strFormat = date.m_strFormat;

    m_anTime = new int[5];
    System.arraycopy( date.m_anTime, 0, m_anTime, 0, m_anTime.length );
    

    m_anDate = new int[3];
    System.arraycopy( date.m_anDate, 0, m_anDate, 0, m_anDate.length );

  } // end assign()


  /**
   *  Sets the instance date to the current system date and time
   */
  private void systemDate()
  {  setComponents( -1, -1, -1, -1, -1, -1, m_strFormat ); }

  /*
   * Creates a date from the individual date and time components with the given format
   *
   * @param nMonth - Month value in the range of 1 - 12
   * @param nDay - Day value the range of 1 - 31
   * @param nYears - Year value in 2 digit or century format
   * @param nHours - Hours in the range 0 - 23
   * @param nMin - Minutes in the range of 0 - 59
   * @param nSecs - Seconds in the range of 0 - 59
   * @param strFormat - Default date/time format string
   *
   * @return True if the date/time is valid; otherwise False is returned
  */
  public final boolean setComponents( int nMonth, int nDay, int nYears, int nHours, int nMin,
                                      int nSecs, String strFormat )
  {
    // *** Create a temp system date to fill in any default components. Defaults have a
    // *** value of -1

    Calendar date = Calendar.getInstance();

    m_anDate = new int[3];

    // *** Load Date Array
    m_anDate[ 0 ] = nMonth;
    m_anDate[ 1 ] = nDay;
    m_anDate[ 2 ] = nYears;

    if ( m_anDate[ 0 ] < 0 )
      m_anDate[ 0 ] = date.get( Calendar.MONTH ) + 1;

    if ( m_anDate[ 1 ] < 0 )
      m_anDate[ 1 ] = date.get( Calendar.DAY_OF_MONTH );

    if ( m_anDate[ 2 ] < 0 )
      m_anDate[ 2 ] = date.get( Calendar.YEAR );

    // *** Load Time Array

    m_anTime[0] = nHours;
    m_anTime[1] = nMin;
    m_anTime[2] = nSecs;

    m_anTime[3] = date.get( Calendar.DAY_OF_WEEK );
    

    m_anTime[ 4 ] = TimeZone.getDefault().getRawOffset();

    // PBV -- added test for 24 time format HOUR_OF_DAY 2-27-1999

    if ( m_anTime[0] < 0 )
    {
      if ( strFormat == null || strFormat.indexOf( "%H" ) >= 0 )
        m_anTime[0] = date.get( Calendar.HOUR_OF_DAY );
      else
      if ( strFormat != null )
      {
        if ( strFormat.indexOf( "%I" ) >= 0 )
          m_anTime[0] = date.get( Calendar.HOUR );
        else
          m_anTime[0] = 0;
        
      }

    } // end if

    if ( m_anTime[1] < 0 )
    {
      if ( strFormat == null || strFormat.indexOf( "%M" ) >= 0 )
        m_anTime[1] = date.get( Calendar.MINUTE );
      else
        m_anTime[1] = 0;
      
    }
    
    if ( m_anTime[2] < 0 )
    {
      if ( strFormat == null || strFormat.indexOf( "%S" ) >= 0 )
        m_anTime[2] = date.get( Calendar.SECOND );
      else
        m_anTime[ 2 ] = 0;

    }
    // *** Check Date and time for valid data

    if ( !isValid( m_anDate ) )
    {
      m_nJulDate = -1;
      m_strErrDesc = m_strStaticErrDesc;

      return false;
    }

    if ( !isValidTime( m_anTime ) )
    {
      m_nJulDate = -1;
      m_strErrDesc = m_strStaticErrDesc;

      return false;
    }

    if ( strFormat == null )
      m_strFormat = USADATE;
    else
      m_strFormat = strFormat;

    m_nJulDate = getJulian( m_anDate );

    date = null;

    return true;

  } // end setComponents


  /**
   * Sets this date instance from a time in miilseconds. This is comaptable
   * to setting a Date or Calendar instance with the same value.
   * 
   * @param lTimeMillisecs time in milliseconds. 
   */
  public void setTime( long lTimeMillisecs )
  {
    Calendar cal = Calendar.getInstance();
    cal.setTimeInMillis( lTimeMillisecs );
    assign( cal );
    
  } // end setTime()
  
  
  public long getTime()
  { return toCalendar().getTimeInMillis();  }
  
  
  /**
   * Converts VwDate to Timestamp
   * @return
   */
  public Timestamp toTimestamp()
  {
    Calendar cal = Calendar.getInstance();
    
    if ( m_anDate[ 0 ] >= 0 )
      cal.set( Calendar.MONTH, (m_anDate[ 0 ] - 1));
    
    if ( m_anDate[ 1 ] >= 0 )
      cal.set( Calendar.DATE, m_anDate[ 1 ]);
    
    if ( m_anDate[ 2 ] >= 0 )
      cal.set( Calendar.YEAR, m_anDate[ 2 ] );
    
    if ( m_anTime[ 0 ] >= 0 )
      cal.set( Calendar.HOUR_OF_DAY, m_anTime[ 0 ] );
    
    if ( m_anTime[ 1 ] >= 0 )
      cal.set( Calendar.MINUTE, m_anTime[ 1 ] );
    
    if ( m_anTime[ 2 ] >= 0 )
      cal.set( Calendar.SECOND, m_anTime[ 2 ] );
    
    Timestamp ts = new Timestamp( cal.getTimeInMillis() );
    if ( m_anTime[ 0 ] == 0 && m_anTime[ 1 ] == 0 && m_anTime[ 2 ] == 0 )
      ts.setNanos( 0 );
    
    return ts;
    

  } // end toTimestamp()

  
  /**
   * Converts VwDate to Date
   * @return
   */
  public Date toDate()
  {
    Calendar cal = toCalendar();
    return new Date( cal.getTime().getTime() );

  } // end toDate()

  
  /**
   * Converts VwDate to Calendar date
   * @return
   */
  public Calendar toCalendar()
  {
    Calendar cal = Calendar.getInstance();
    
    cal.set( Calendar.MONTH, (m_anDate[ 0 ] - 1));
    cal.set( Calendar.DATE, m_anDate[ 1 ]);
    cal.set( Calendar.YEAR, m_anDate[ 2 ] );
    
    cal.set( Calendar.HOUR_OF_DAY, m_anTime[ 0 ] );
    cal.set( Calendar.MINUTE, m_anTime[ 1 ] );
    cal.set( Calendar.SECOND, m_anTime[ 2 ] );
    
    if ( m_anTime[ 0 ] == 0 && m_anTime[ 1 ] == 0 && m_anTime[ 2 ] == 0 )
      cal.set(  Calendar.MILLISECOND, 0 );
    else
      cal.set(  Calendar.MILLISECOND, m_anTime[ 3 ] );
      
    return cal;
    

  } // end toCalendar()
  

  /*
   * Break the String Date into its numeric components into the anArgDate Array
   * with index 0 == Month
   *            1 == Day
   *            2 == Year
   *            3 == Hours
   *            4 == Minutes
   *            5 == Seconds
   *            6 == Weekday Number -- Sunday = 1
   *
   * @param strDate - The date string to parse
   * @param strFormat - The input Date/Time format specifying the order of the components
   *
   * @return An integer array with the converted date values, or null if the date
   * is invalid
  */
  private static int[] cvtToNum( String strDate, String strFormat )
  {
    int[] anDateValues = VwDateTimeParser.parse( strDate, strFormat );

    if ( anDateValues == null )     // Date error
    {
      m_strStaticErrDesc = VwDateTimeParser.getErrDesc();
      return null;
    }

    return anDateValues;

  } // end of cvtToNum


  /*
   * Initialize the date array from a Julian date in standard USAFORMAT
   *
   * @param nDate - Julian day number
   *
   * @return An integer array of date values if the date is valid, otherwise null is returned
  */
  private static int[] cvtToNum( int nDate )
  { return cvtToNum( format( nDate, USADATE ), USADATE ); }


  /*
   ==================================================================================================
   Julian Date Conversion Routines

   This program calculates the Julian day number from a calendar date,
   and the date and day of the week from a Julian day.  The Julian date
   is in double precision floating point with the origin used by
   astronomers.  The calendar output converts fractional parts of a day into
   hours, minutes and seconds.  There is no year 0!  Enter B.C. years
   as negative; i.e., 2 B.C. as -2.

   The approximate range of dates handled is 4713 B.C. to 54,078 A.D.
   This should be adequate for most applications.

   B.C. dates are calculated by extending the Gregorian sequence of
   leap years and century years into the past.  This seems to be the
   only sensible definition, but the author doesn't know the official one.

   Note that the astronomical Julian day starts at noon on the previous
   calendar day.  Thus at midnight in the morning of the present calendar
   day, the Julian date ends in .5;  It rolls over to tomorrow at noon
   today.  ***** NOTE - This is the original note by Steve Moshier.  The
   current developer did not implement it this way.  There was no need
   for handling dates using double, so the developer converted routines
   to use signed longs to be consistent with remainder of the date routines.
   ATD

   The month finding algorithm is attributed to MEEUS.

   - Steve Moshier

   - Modified by Alonzo T. Dukes

   ==================================================================================================
  */



  /**
   * Convert a Date array to a Julian Day Number
   *
   * @param anNumDate - The array with the month, day, and year values to convert
   *
   * @return Returns Julian Day Number for the date parameter
  */
  private static int getJulian( int[] anNumDate )
  {
    int         lJulianDay;              // Julian Date calculated for return
    int         lTempYear;               // Used to calculate # of centuries, leap years
    int         lNumCenturies;           // Used to store # of centuries
    int         lNumCenNoLeapYears;      // Used to store # of century years NOT leap
    int         lJulCalLeapYears;        // Used to save # Julian Years/Leap Years
    int         lTempMonth;              // Used to handle determining correct month
    int         lTempDay;                // Used to save converted month
    int         lExponent;               // Exponent used in calculation
    int         nYear;                   // Original Year prior to conversion


    // *** Since date is valid, save to be further processed

    lTempMonth = anNumDate[0];
    lTempDay   = anNumDate[1];
    nYear     = anNumDate[2];

    // The origin should be chosen to be a century year
    // that is also a leap year.  4801 B.C. was chosen.

    lTempYear = (int)( nYear + 4800 );
    if ( nYear < 0 )                     // If year before Christ's birth
    {
      lTempYear += 1;
    }

    // The following magic arithmetic calculates a sequence
    // whose successive terms differ by the correct number of
    // days per calendar month.  It starts at 122 = March.
    // January and February come after December.

    if ( lTempMonth <= 2 )
    {
      lTempMonth += 12;
      lTempYear--;
    }

    lExponent = ( 306 * ( lTempMonth + 1 ) ) / 10;

    lNumCenturies = lTempYear/100;
    lNumCenNoLeapYears = ( lNumCenturies/4 ) - lNumCenturies;
    lJulCalLeapYears = ( 36525 * lTempYear )/100;

    // Add up these terms, plus the offset from dblJulianDay 0 to 1
    // Jan 4801 B.C.  Also adjust for the 122 days from the month
    // algorithm.

    lJulianDay = lNumCenNoLeapYears + lJulCalLeapYears +
                 lExponent + lTempDay - 32167;

    // Return results here

    return lJulianDay;

  } // end of getJulian()


  /**
   * Converts a Julian date to a string representation of the date
   *
   * @param nJulDate - Julian date to convert
   * @param anTime - Time array holding time components
   * @param strFormat - Date format for the result date string
   *
   * @return A string containing the formatted date; or null if the date is invalid
   */
  private static String julianToString( int nJulDate, int[] anTime, String strFormat )
  {
    int         nTempYear;           // Used to calculate # of centuries, leap years
    int         nNumCenturies;       // Used to store # of centuries
    int         nNumCenNoLeapYears;  // Used to store # of century years NOT leap
    int         nTemp;               // General Temporary Variable
    int         nTemp2;              // General Temporary Variable
    int         nTemp3;              // General Temporary Variable
    int         nTempMonth;          // Used to handle determining correct month
    int         nDay;
    int         nBC;                 // Before Christ/After Christ flag
    int         nYear;               // Original Year prior to conversion
    int         nMonth;              // Original Month prior to conversion

    int[]       anDate = new int[DATE_ARRAY_SIZE];

    // *** Fill in unused portions

    anDate[ 3 ] = anTime[ 0];
    anDate[ 4 ] = anTime[ 1 ];
    anDate[ 5 ] = anTime[ 2 ];
    anDate[ 6 ] = anTime[ 3 ];
    anDate[ 7 ] = anTime[ 4 ];
    anDate[ 8 ] = anTime[ 5 ];

    // First set the BC flag based on whether the
    // date is before January 1.0, 1 A.D.

    if ( nJulDate < 1721425 )
      nBC = 1;
    else
      nBC = 0;

    // Find the number of Gregorian centuries
    // since March 1, 4801 B.C.

    nNumCenturies = ( 100 * nJulDate + 3204500 ) / 3652425;

    // Transform to Julian calendar by adding in Gregorian
    // century years that are not leap years.
    //
    // Subtract 97 days to shift origin of m_lJunDate to March 1.
    // Add 122 days, which is where the magic arithmetic algorithm.
    // Add four (4) years to ensure the first leap year is detected.

    nNumCenNoLeapYears = nJulDate + 1486 + nNumCenturies - nNumCenturies / 4;

    // Offset 122 days, which is where the magic arithmetic
    // month formula sequence starts ( March 1 = 4 * 30.6 = 122.4)

    nTemp  = ( 100 * nNumCenNoLeapYears - 12210 ) / 36525;
    nTemp2 = ( 36525 * nTemp ) / 100;

    // Now determine the Month and Day

    nTemp3 = ( ( nNumCenNoLeapYears - nTemp2 ) * 100 ) / 3061;
    nDay = (int)(nNumCenNoLeapYears - nTemp2 - ( ( 306 * nTemp3 ) / 10 ) );
    nTempMonth = (int)(nTemp3 - 1);

    if ( nTemp3 > 13 )
      nTempMonth -= 12;

    // Get the Right Year

    nTempYear = nTemp - 4715;
    if ( nTempMonth > 2 )
      nTempYear--;

    // Now make sure the year is adjusted for BC vs AD

    if ( nBC > 0 )
      nTempYear = -nTempYear + 1;

    // Now convert date components to internal Date array values

    anDate[0] = nTempMonth;
    anDate[1] = nDay;
    anDate[2] = nTempYear;

    if ( strFormat == null )
      strFormat = USADATE;

    return VwDateTimeParser.format( anDate, strFormat );

  } // end julianToString()



  /**
   * Converts year to century
   *
   * @param nOrigYear - The year value to convert to century format
   *
   * @return - The year in century format, or -1 if the year > 9999
   */
  private static int formatCentury( int nOrigYear )
  {
    if ( nOrigYear > 9999 )
      return -1;               // Invalid year
    else
    if ( nOrigYear > 999 )
      return nOrigYear;        // Year is already in century format

    if ( nOrigYear > 99 )
       return -1;              // Invalid year

    if ( nOrigYear < 20 )      // 20th centry
      return  nOrigYear + 2000;
     return nOrigYear + 1900;  // 19th century

  } // end formatCentury()


  /**
   * Converts Century to a 2 digit year
   *
   * @param nCentury - the Century to convert to a 2 digit year
   *
   * @return The 3 digit year, or -1 if the century is > 9999
   */
  private static int formatYear( int nCentury )
  {
    if ( nCentury < 100 )
      return nCentury;          // Already in year format

    if ( nCentury > 9999 )
      return -1;                // Invalid year
    else
    if ( nCentury > 99 && nCentury < 1000 )
      return -1;                // Invalid year

    return nCentury % 100;      // Cut century leaving year

  } // end formatYear()



  /**
   * Verifies the Day is within correct range for the given date values
   *
   * @param nMonth - Month value in the range of 1 - 12
   * @param nDay - Day value the range of 1 - 31
   * @param nYear - Year value in century format
   *
   * @return True if the Day value is valid for the given date values; otherwise False is returned
   */
  private static boolean verifyRange( int nMonth, int nDay, int nYear )
  {
    if ( nMonth < 1 || nMonth > 12 )
    {
      m_strStaticErrDesc = "Month Must be Between 1 and 12";

      return false;
    }

    if ( nDay > VwMonths.getNbrDays( nMonth ) )
    {
      // *** Test for leap year if month is february & day = to 29

      if ( nMonth == 2 )
      {
         if ( !isLeapYear( nYear ) && ( nDay >= 29 ) )
         {
            m_strStaticErrDesc = "This is not a leap year, days cannot be > 28";

            return false;
         }
      }
      else
      {
        m_strStaticErrDesc = "Nbr Of Days Exceed Allowed For Month Specified";

        return false;                          // Invalid day
      }
    }

    if ( nDay < 1 )
    {
      m_strStaticErrDesc = "Nbr Of Days Cannot Be Less Than 1";

      return false;                          //  Day must be greater than 1
    }

    return true;

  } // end verifyRange()


  /**
   * Internally utilized Validity check routines
   */
  private static boolean isValid( int[] anNumDate )
  { return verifyRange( anNumDate[0], anNumDate[1], anNumDate[2] ); }


  /**
   * Validate time components
   *
   * @param nHours  Hours must be between 0 and 23
   * @param nMin    Minutes must be between 0 and 59
   * @param nSecs   Seconds must be between 0 and 59
   *
   * @return true if time components are valid else false is returned
   */
  public static boolean isValidTime( int nHours, int nMin, int nSecs )
  { return isValidTime( new int[] { nHours, nMin, nSecs } ); }

  /**
   * Validate the time components
   *
   * @param anTime - An array of time components (index 0 = hours, 1 = minutes, 2 = seconds)
   *
   * @return True if the time components are valid; otherwise False is returned
   */
  public static boolean isValidTime( int [] anTime )
  {
    if ( anTime[0] < 0 || anTime[0] > 23 )
    {
      m_strStaticErrDesc = "Hours Must Be Between 0 and 23";
      return false;
    }

    if ( anTime[1] < 0 || anTime[1] > 59 )
    {
      m_strStaticErrDesc = "Minutes Must Be Between 0 and 59";
      return false;
    }

    if ( anTime[2] < 0 || anTime[2] > 59 )
    {
      m_strStaticErrDesc = "Seconds Must Be Between 0 and 59";
      return false;
    }

    return true;

 } // end isValid()



  /**
   * Constructs a date from the current system date and time with a default USADATE format
   */
  public VwDate()
  {

    m_strDate = null;
    m_fIsStringSqlDate = false;

    systemDate();

  } //end VwDate()



  /**
   * Constructs a date from the current system date and time with a specified default format
   *
   * @param strFormat The default date format used to describe the input/output format of
   * a string representation date
   */
  public VwDate( String strFormat )
  {
    m_strDate = null;
    m_fIsStringSqlDate = false;

    m_strFormat = strFormat;

    systemDate();

  } // end VwDate()


  /**
   * Constructs a date from a Java Calendar object
   *
   * @param calendar The default date format used to describe the input/output format of
   * a string representation date
   */
  public VwDate( Calendar cal )
  {
    assign( cal );
    
  } // end VwDate()


  /**
   * Constructs a date from a Java Date object
   *
   * @param calendar The default date format used to describe the input/output format of
   * a string representation date
   */
  public VwDate( Date date )
  {
    m_strDate = null;
    m_fIsStringSqlDate = false;

    m_strFormat = USADATE;

    Calendar cal = Calendar.getInstance();
    cal.setTime( date );

    if ( !setComponents( cal.get( Calendar.MONTH ) + 1, cal.get(Calendar.DATE ), cal.get( Calendar.YEAR ),
         cal.get( Calendar.HOUR_OF_DAY ), cal.get( Calendar.MINUTE ), cal.get( Calendar.SECOND ), null ) )
       m_nJulDate = -1;

    m_strDate = null;

    m_fIsStringSqlDate = false;

  } // end VwDate()

  /**
   * Constructs date from another VwDate instance
   *
   * @param date an instance of VwDate
   */
  public VwDate( VwDate date )
  {
    assign( date );

  } //end VwDate


  /**
   * Constructs a date from a string date and input format mask
   *
   * @param strDate - A date in string form; e.g., "10-15-1997"
   * @param strFormat The format describing the date string; e.g., "%m-%d-%Y" describes above date string
   */
  public VwDate( String strDate, String strFormat )
  {
    // *** Parse string date into its date and time components

    int [] anDate = VwDateTimeParser.parse( strDate, strFormat );

    // *** null Array is an invalid date according to the strFormat string

    if ( anDate == null )
    {
      m_nJulDate = -1;
      m_strErrDesc = VwDateTimeParser.getErrDesc();
      return;
    }

    if ( !isValid( anDate ) )
    {
      m_nJulDate = -1;
      m_strErrDesc = m_strStaticErrDesc;
      return;

    }

    m_nJulDate = getJulian( anDate );

    m_strDate = null;

    m_fIsStringSqlDate = false;

    m_strFormat = strFormat;

    // Copy time and date components to instance arras

    m_anDate[0] = anDate[0];
    m_anDate[1] = anDate[1];
    m_anDate[2] = anDate[2];

    if ( anDate[6] <= 0 )
      m_anTime[3] = anDate[6] = getDayOfWeek();

    // *** if time was not specified in the current mask then set it

    if ( anDate[3] < 0 )
    {
      setTime();
      return;
    }

    m_anTime[0] = anDate[3];
    m_anTime[1] = anDate[4];
    m_anTime[2] = anDate[5];
    m_anTime[3] = anDate[6];
    m_anTime[4] = anDate[7];
    m_anTime[5] = anDate[8];

    
  } // end VwDate()


  /**
   * Constructs a date from date components.  Time components are set to the current time.
   * The default format is the USADATE format.
   *
   * @param nMonth Month number in the range of 1 - 12
   * @param nDay Day number in the range of 1 - 31
   * @param nYear The Year number
   */
  public VwDate( int nMonth, int nDay, int nYear )
  {
    if ( !setComponents( nMonth, nDay, nYear, -1, -1, -1, null ) )
       m_nJulDate = -1;

    m_strDate = null;

    m_fIsStringSqlDate = false;

  } // end VwDate()


  /**
   * Constructs a date from the given date and time components, with the default date format, USADATE.
   *
   * @param nMonth Month number in the range of 1 - 12
   * @param nDay Day number in the range of 1 - 31
   * @param nYear Year number
   * @param nHours Number of hours in the range of 0 - 23
   * @param nMinutes Number of minutes in the range of 0 - 59
   * @param nSeconds Number of seconds in the range of 0 - 59
   */
  public VwDate( int nMonth, int nDay, int nYear, int nHours, int nMinutes, int nSeconds )
  {
    if ( !setComponents( nMonth, nDay, nYear, nHours, nMinutes, nSeconds, null ) )
       m_nJulDate = -1;

    m_strDate = null;

    m_fIsStringSqlDate = false;

  } // end VwDate()


  /**
   * Constructs an VwDate from an VwSQLTimeStamp object
   *
   * @param timeStamp - An VwSQLTimeStamp object used to initialize the current date object
   */
  public VwDate( Timestamp timeStamp )
  {
    Calendar cal = Calendar.getInstance();
    cal.setTimeInMillis( timeStamp.getTime() );
    
    
    if ( !setComponents( cal.get( Calendar.MONTH ) + 1, cal.get(Calendar.DATE), cal.get(Calendar.YEAR),
        									cal.get( Calendar.HOUR_OF_DAY), cal.get( Calendar.MINUTE ),
        									    cal.get( Calendar.SECOND), null  )  )
    {
       m_nJulDate = -1;
       return;
    }


    m_strDate = null;

    m_fIsStringSqlDate = false;

  } // end VwDate()


  /**
   * Constructs a date from a Julian day number
   *
   * @param nJulDay The Julian Day number
   */
   public VwDate( int nJulDay )
   {
     
     if ( !isValid( nJulDay ) )
     {
       m_strErrDesc = m_strStaticErrDesc;
       m_nJulDate = -1;
     }

     m_strFormat = USADATE;

     m_nJulDate = nJulDay;

     m_anDate = cvtToNum( nJulDay );

     m_anTime[3] = getDayOfWeek();

     setTime();

   } // end VwDate()


  /**
   * Clones the current date instance
   *
   * @return A new date instance with values identical to the current object
   */
  public final Object clone()
  { return new VwDate( this ); }



  /**
   * Re-initializes the current date from a new date string
   *
   * @param strDate The new date used to initialize the current date object
   * @param strFormat The format specifier describing the date string components
   *
   * @return True if the new date is valid; otherwise False is returned
   */
   public final boolean setDate( String strDate, String strFormat )
   {
     m_nJulDate = -1;

     m_strFormat = strFormat;
     VwDate date = new VwDate( strDate, m_strFormat );
     if ( !date.isValid() )
     {
       m_strErrDesc = date.getErrDesc();
       return false;
     }

     assign( date );
     date = null;
     return true;

   } // end setDate()


  /**
   * Re-initializes the current date instance from a Julian date
   *
   * @param nJulDate - The Julian day number value
   *
   * @return True if the new date is valid; otherwise False is returned
   */
  public final boolean setDate( int nJulDate )
  {
    m_nJulDate = -1;

    if ( !isValid( nJulDate ) ) return false;

    m_nJulDate = nJulDate;

        // *** Parse string date into its date and time components

    int [] anDate = VwDateTimeParser.parse( julianToString( nJulDate, m_anTime, USADATE ), USADATE ) ;

    m_anDate[ 0 ] = anDate[ 0 ];
    m_anDate[ 1 ] = anDate[ 1 ];
    m_anDate[ 2 ] = anDate[ 2 ];

    return true;

   } // end setDate()


  /**
   * Re-initializes the current date object from another VwDate instance
   *
   * @param date The VwDate instance used to initialize the current object
   *
   * @return True if the new date is valid; otherwise False is returned
   */
   public final boolean setDate( VwDate date )
   {
     assign( date );

     return isValid();

   } // end setDate()


  /**
   * Update the instance date to the current time values
   */
  private final void setTime()
  {
    Calendar date = Calendar.getInstance();

    m_anTime[0] = date.get( Calendar.HOUR_OF_DAY );

    m_anTime[1] = date.get( Calendar.MINUTE );

    m_anTime[2] = date.get( Calendar.SECOND );


    m_anTime[ 4 ] = TimeZone.getDefault().getRawOffset();


    date = null;

  } // end setTime()



  /**
   * Returns the Julian day for this date instance
   *
   * @return The Julian day for this date as an integer
   */
   public final int getJulian()
   { return m_nJulDate; }


  /**
   *  Resets the current date mask to supplied value
   *
   * @param The new date mask string with the format specifiers
   */
  public final void setFormat( String strFormat )
  {  m_strFormat = strFormat;  }

  /**
   * Sets the leniency of the date validation
   * @param fIsLenient the value of the leniency, true for lenient false for
   * not lenient
   */
  public void setLenient( boolean fIsLenient )
  {  m_fIsLenient = fIsLenient;  }
  
  /**
   * Returns the value of the format leniency. The default value is false.
   * @return true if lenient, otherwise false
   */
  public boolean isLenient()
  {  return m_fIsLenient;  };
  
  /**
   * Sets the instance date to the current system date and time
   */
  public final void setCurrentDate()
  { systemDate(); }


  /**
   * Gets the last date error message
   *
   * @return A string containing the last known date error message
   */
  public final String getErrDesc()
  { return m_strErrDesc; }


  // *** Several validity checking methods

  /**
   * Test a string date and mask for validity
   *
   * @param strDate A date in string form to test for validity
   * @param The format mask that describes the date to be tested
   *
   * @return True if the date is a valid date; otherwise False is returned
   */
  public static boolean isValid( String strDate, String strFormat )
  {
    int[] anNumDate = cvtToNum( strDate, strFormat );

    if ( anNumDate == null )
      return false;

    if ( !isValid( anNumDate ) )
      return false;

    return true;

  } // end isValid()


  /**
   * Test Julian date
   *
   * @param nJulDate The Julian date to test
   */
  public static boolean isValid( int nJulDate )
  {
    int[] anNumDate = cvtToNum( nJulDate );

    if ( anNumDate == null )
      return false;

    return isValid( anNumDate );

  } // end is valid


  /**
   * Test the instance date for a valid date
   *
   * @return True if the VwDate object represents a valid date; otherwise False is returned
   */
  public boolean isValid()
  { 
    
    if ( m_nJulDate < 0 )
      return false;
    
    if( !m_fIsLenient )
      return VwDate.isValid( this.toString(), m_strFormat );
  
    return true;
  }


  /**
   * Re-formats a date string described by an Input format mask, to a date string formatted
   * according to an Output format mask.
   *
   * @param strDate The date string to re-format
   * @param strInFormat The Input format string describing the components of the date to re-format
   * @param strOutFormat The Ouput format string describing the format of the output date string
   *
   * @return A date string formatted according to the Output format mask or null if the date is invalid
   *
   */
  public static String format( String strDate, String strInFormat, String strOutFormat )
  {
    int[] anDate = cvtToNum( strDate, strInFormat );

    if ( anDate == null )
      return null;

    if ( !isValid( anDate ) )
      return null;


    String strFormatDate = VwDateTimeParser.format( anDate, strOutFormat );
    if ( strFormatDate == null )
    {
      m_strStaticErrDesc = VwDateTimeParser.getErrDesc();
      return null;
    }

    return strFormatDate;

  } // end format()


  /**
   * Formats a Julian date according to the given date format mask
   *
   * @param nDate The Julian date to format
   * @param strFormat The format specifier string
   *
   * @return A date string in the format specified
   */
  public static String format( int nDate, String strFormat )
  { int anTime[] = {-1,-1,-1,-1,-1 }; return julianToString( nDate, anTime, strFormat ); }


  /**
   * Format the current date object with the given date format mask
   *
   * @param strFormat The format specifier string.
   *
   * @return A date string in the format specified or null if the date is invalid
   */
  public String format( String strFormat )
  { 
    if ( m_nJulDate < 0 )
      return null;
    
    return julianToString( m_nJulDate, m_anTime, strFormat ); 
    
   } // end format()


  /**
   * Formats the instance date using the default format mask
   *
   * @return A date string formatted according to the default format mask or null if the date is invalid
   */
  public String format()
  { 
    if ( m_nJulDate < 0 )
      return null;
    
    return julianToString( m_nJulDate, m_anTime, m_strFormat );
    
  } // end format()

  /**
   * Formats the instance date using the default format mask
   *
   * @return A date string formatted according to the default format mask or null if the date is invalid
   */
  public String toString()
  { return format(); }


  /**
   * Determines if the Year is a leap year
   *
   * @param nYear The year to test
   *
   * @return True if the Year is a leap year; otherwise False is returned
   */
  public static boolean isLeapYear( int nYear )
  {
    // A year is a leap year if it is divisible by 4, and where it is
    // divisible by 100 it must also be divisible by 400.

    if ( (nYear % 4 ) > 0 )
      return false;                      // Year is not a leap year
    else
    if ( (nYear % 100) == 0)
    {
      if (nYear % 400 > 0 )
        return false;                    // Year is not a leap year
    }

    return true;                         // Year is a leap year

  } // end isLeapYear()


  /**
   * Returns the Julian date for the given date string and format mask
   *
   * @param strDate The date string to be converted to the Julian date
   * @param strFormat The input format mask describing the date string
   *
   * @return The Julian day number if the given date is valid, or -1 if the date is invalid
   */
  public static int getJulian( String strDate, String strFormat )
  {
    int [] anDate = VwDateTimeParser.parse( strDate, strFormat );

    if ( anDate == null )
    {
      m_strStaticErrDesc = VwDateTimeParser.getErrDesc();
      return -1;
    }

    if ( !isValid( anDate ) )
      return -1;

    return getJulian( anDate );

  } // end getJulian()


  /**
   * Gets the weekday number from the instance date
   *
   * @returns An integer from 1 to 7 (Sun = 1)
   */
  public int getDayOfWeek()
  { return toCalendar().get( Calendar.DAY_OF_WEEK ); }


  /**
   * Gets weekday number from the given date string
   *
   * @param strDate The date string for which to determine the weekday number
   * @param strFormat The input format mask describing the date string
   *
   * @returns An integer from 1 to 7 (Sun = 1)
   */
  public static final int getDayOfWeek( String strDate, String strFormat )
  {
    VwDate date = new VwDate(strDate, strFormat );
    return date.getDayOfWeek();
 
  } // end dayOfWeek()


  /**
   * Gets the weekday name for the given date string
   *
   * @param strDate The date string for which to determine the weekday name
   * @param strFormat The input format mask describing the date string
   *
   * @returns A string with the weekday name (e.g., "Sunday"), or null if the
   * date string is invalid
   */
  public static String getWeekDay( String strDate, String strFormat )
  { return m_astrWeekDays[ getDayOfWeek( strDate, strFormat ) ]; }


  /**
   * Gets the weekday name for the instance date
   *
   * @returns A string with the weekday name (e.g., "Sunday"), or null if the
   * date string is invalid
   */
  public final String getWeekDay()
  { return m_astrWeekDays[ getDayOfWeek( format(), m_strFormat ) ]; }


  /**
   * Gets weekday name that corresponds to a weekday number
   *
   * @param nWeekDay A weekday number in the range of 1 to 7 (Sunday = 1)
   *
   * @returns A string with the weeday name (e.g., "Sunday")
   *
   * @exception throws ArrayIndexOutOfBoundsException if nWeekDay is < 0 or > 7
   */
  public static String getWeekDay( int nWeekDay ) throws ArrayIndexOutOfBoundsException
  { return m_astrWeekDays[ nWeekDay ]; }


  /**
   * Calculates the difference in days between dates in string form, where the date formats
   * are the same.
   *
   * @param strDate1 First date string
   * @param strDate2 Second date string for the date to be subtracted from the First Date
   * @param strFormat Input format specifier for both dates
    *
   * @return The difference in days by subtracting the Second Date from the First
   *
   * @exception throws Exception if either of the dates is invalid
   */
   public static int getDifference( String strDate1, String strDate2,
                                    String strFormat ) throws Exception
   { return getDifference( strDate1, strDate2, strFormat, strFormat ); }


  /**
   * Calculates the difference in days between dates in string form, where the date formats
   * are different.
   *
   * @param strDate1 First date string
   * @param strDate2 Second date string for the date to be subtracted from the First Date
   * @param strFormat1 Input format specifier for the First Date
   * @param strFormat2 Input format specifier for the Second Date
   *
   * @return The difference in days by subtracting the Second Date from the First
   *
   * @exception throws Exception if either of the dates is invalid
   */
   public static int getDifference( String strDate1, String strDate2,
                                    String strFormat1,String strFormat2 ) throws Exception
   {
     int nDate1 = getJulian( strDate1, strFormat1 );
     if ( nDate1 < 0 )
       throw new Exception( "First date is invalid because : " + m_strStaticErrDesc );

     int nDate2 = getJulian( strDate2, strFormat2 );
     if ( nDate2 < 0 )
       throw new Exception( "Second date is invalid because : " + m_strStaticErrDesc );

     return nDate1 - nDate2;

   }

  /**
   * Calculates the difference in days between dates by subtracting the date specified in the
   * parameter from this date
   *
   * @param date The VwDate instance to subtract from this date
   *
   * @return The difference in days between the two dates
   */
  public int getDifference( VwDate date )
  { return m_nJulDate - date.m_nJulDate; }


  /**
   * Retrieves the Month component of the instance date
   *
   * @return The Month as an integer, or -1 if the date is invalid
   */
  public final int getMonth()
  {
    if ( !isValid() )
      return -1;

    return m_anDate[ 0 ];

  } // end getMonth()


  /**
   * Retrieves the Day component of the instance date
   *
   * @return The day as an integer, or -1 if the date is invalid
   */
  public final int getDay()
  {
    if ( !isValid() )
      return -1;

    return m_anDate[ 1 ];

  } // end getDay()


  /**
   * Retrieves the Year component of the instance date
   *
   * @return The Year as an integer, or -1 if the date is invalid
   */
  public final int getYear()
  {
    if ( !isValid() )
      return -1;

    return m_anDate[ 2 ];

  } // end getYear()


  /**
   * Retrieves the Hours component of the instance date
   *
   * @return The Hours as an integer, or -1 if the date is invalid
   */
  public final int getHours()
  {
    if ( !isValid() )
      return -1;

    return m_anTime[ 0 ];

  } // end getHours()


  /**
   * Sets the hours time component
   * @param nHours
   */
  public final void setHours( int nHours )
  { m_anTime[ 0 ] = nHours; }



  /**
   * Retrieves the Minutes component of the instance date
   *
   * @return The Minutes as an integer, or -1 if the date is invalid
   */
  public final int getMinutes()
  {
    if ( !isValid() )
      return -1;

    return m_anTime[ 1 ];

  } // end getMinutes()


  /**
   * Sets the minutes time component
   * @param nMinutes
   */
  public final void setMinutes( int nMinutes )
  { m_anTime[ 1 ] = nMinutes; }


  /**
   * Retrieves the Seconds component of the instance date
   *
   * @return The Seconds as an integer, or -1 if the date is invalid
   */
  public final int getSeconds()
  {
    if ( !isValid() )
      return -1;

    return m_anTime[ 2 ];

  } // end getSeconds()


  /**
   * Sets the seconds time component
   * @param nSeconds
   */
  public final void setSeconds( int nSeconds )
  { m_anTime[ 2 ] = nSeconds; }


  /**
   * Retrieves the timezone offset from the GMT zone
   *
   * @return the timezone offset from the GMT zone, or -1 if the date is invalid
   */
  public final int getTimeZoneOffset()
  {
    if ( !isValid() )
      return -1;

    return m_anTime[ 4 ];

  } // end getTimeZoneOffset()

  
  public int getZoneOffsetInHours( boolean fIncludeDST )
  {
    
    Calendar calDate =  toCalendar();
    int nZoneOffset = m_anTime[ 4 ];
    
    if ( fIncludeDST )
      nZoneOffset += calDate.get( Calendar.DST_OFFSET );
    
    nZoneOffset /= 1000; // convert to
    nZoneOffset /= 60;
    nZoneOffset /= 60;

    return Math.abs( nZoneOffset );

    
  }
  /**
   * Returns the the instance date plus the specified number of days.  The instance date
   * is NOT modified.
   *
   * @param nDays The number of days to add
   *
   * @return The Julian date reflecting the date addition
  */
  public int plusDays( int nDays )
  { return m_nJulDate + nDays; }


  /**
   * Returns the instance date minus the specified number of days.  The instance date
   * is NOT modified.
   *
   * @param nDays The number of days to subtract
   *
   * @return The Julian date reflecting the date subtraction
  */
  public int minusDays( int nDays )
  { return m_nJulDate - nDays; }


  /**
   * Adds the specified number of days to the instance date, and UPDATES the instance date
   *
   * @param nDays The number of days to add
  */
  public void addDays( int nDays )
  { 
    m_nJulDate += nDays;
    setDate( m_nJulDate );
  }


  /**
   * Subtracts the specified number of days from the instance date, and UPDATES the instance date
   *
   * @param nDays The number of days to subtract
  */
  public void subDays( int nDays) // modifies self
  { 
    m_nJulDate -= nDays;
    setDate( m_nJulDate );
  }


  /**
   *  Increments the date by one day, and UPDATES the instance date
  */
  public void inc()
  { setDate( ++m_nJulDate ); }


  /**
   * Decrements the date by one day, and UPDATES the instance date
  */
  public void dec()
  { setDate( --m_nJulDate ); }


    //
    // *** Date comparison methods
    //


  /**
   * Compares the instance date to another VwDate object
   *
   * @param date - The VwDate date that the instance date is compared to
   *
   * @returns True if the dates are equal; otherwise False is returned
  */
  public boolean equals( VwDate date )
  { return (m_nJulDate == date.m_nJulDate); }

  public boolean equals( Object obj )
  { 
    if ( obj instanceof VwDate )
      return (m_nJulDate == ((VwDate)obj).m_nJulDate); 
    
    if ( obj instanceof java.util.Date )
    {
      Calendar calTemp = Calendar.getInstance();
      calTemp.setTime( (Date)obj );
      obj = calTemp;
      
    }
    
    if ( obj instanceof Calendar )
    {
      Calendar calThis = toCalendar();
      Calendar calCompare = (Calendar)obj;
      
      return ( calThis.get( Calendar.MONTH ) == calCompare.get( Calendar.MONTH ) &&
          calThis.get( Calendar.DATE) == calCompare.get( Calendar.DATE ) &&
          calThis.get( Calendar.YEAR) == calCompare.get( Calendar.YEAR ) );
    }
    
   
    return false;
    
   }

  /**
   * Compares the instance date to a given Julian date
   *
   * @param nJulDate The Julian date that the instance date is compared to
   *
   * @returns True if the dates are equal; otherwise False is returned
  */
  public boolean equals( int nJulDate )
  { return ( m_nJulDate == nJulDate ); }


  /**
   * Compares the instance date and another VwDate object for inequality
   *
   * @param date The VwDate that the instance date is compared to
   *
   * @returns True if the dates are NOT equal; otherwise False is returned
  */
  public boolean notEqual( VwDate date )
  { return (m_nJulDate != date.m_nJulDate); }


  /**
   * Compares the instance date and the given Julian date for inequality
   *
   * @param nJulDate - The Julian date that the instance date is compared to
   *
   * @returns True if the dates are NOT equal; otherwise False is returned
  */
  public boolean notEqual( int nJulDate )
  { return ( m_nJulDate != nJulDate ); }


  /**
   * Determines if the instance date is earlier than the given VwDate object
   *
   * @param date - The VwDate date that the instance date is compared to
   *
   * @returns True if the instance date is earlier than the given date
  */
  public boolean lessThan( VwDate date )
  { return ( m_nJulDate <  date.m_nJulDate ); }


  /**
   * Determines if the instance date is earlier than the given Julian date
   *
   * @param nJulDate - The Julian date that the instance date is compared to
   *
   * @returns True if the instance date is earlier than the given Julian date
  */
  public boolean lessThan( int nJulDate )
  { return ( m_nJulDate < nJulDate ); }


  /**
   * Determines if the instance date is earlier than or equal to the given VwDate object
   *
   * @param date - The VwDate date that the instance date is compared to
   *
   * @returns True if the instance date is earlier than or equal to the given date
  */
  public boolean ltEq( VwDate date )
  { return ( m_nJulDate <=date.m_nJulDate ); }


  /**
   * Determines if the instance date is earlier than or equal to the given Julian date
   *
   * @param nJulDate - The Julian date that the instance date is compared to
   *
   * @returns True if the instance date is earlier than or equal to the given Julian date
  */
  public boolean ltEq( int nDate )
  { return ( m_nJulDate <= nDate ); }


  /**
   * Determines if the instance date is later than the given VwDate object
   *
   * @param date - The VwDate date that the instance date is compared to
   *
   * @returns True if the instance date is later than the given date
  */
  public boolean greaterThan( VwDate date )
  { return (m_nJulDate >  date.m_nJulDate); }


  /**
   * Determines if the instance date is later than the given Julian date
   *
   * @param nJulDate - The Julian date that the instance date is compared to
   *
   * @returns True if the instance date is later than the given Julian date
  */
  public boolean greaterThan( int nJulDate )
  { return ( m_nJulDate > nJulDate ); }


  /**
   * Determines if the instance date is later than or equal to the given VwDate object
   *
   * @param date - The VwDate date that the instance date is compared to
   *
   * @returns True if the instance date is later than or equal to the given date
  */
  public boolean gtEq( VwDate date )
  { return (m_nJulDate >= date.m_nJulDate ); }


  /**
   * Determines if the instance date is later than or equal to the given Julian date
   *
   * @param nJulDate - The Julian date that the instance date is compared to
   *
   * @returns True if the instance date is later than or equal to the given Julian date
  */
  public boolean gtEq( int nJulDate )
  { return ( m_nJulDate >= nJulDate ); }

  
  /**
   * Return -1 if date is < ctCompare date, 0 if date is = to dtCompare or 1 if sate is > dtCompare.
   * NOTE1 This compares date conponents only, time values are ignored
   * @param dtCompare The date to compare
   * @return
   */
  public int compareDate( VwDate dtCompare )
  {
    if ( m_nJulDate < dtCompare.m_nJulDate )
      return -1;
    else
    if ( m_nJulDate > dtCompare.m_nJulDate )
      return 1;
      
    return 0;
    
  }

  /**
   * Return -1 if date is < ctCompare date, 0 if date is = to dtCompare or 1 if sate is > dtCompare.
   * NOTE1 This compares date and time components
   * @param dtCompare The date to compare
   * @return
   */
  public int compareDateTime( VwDate dtCompare )
  {
    Calendar cal = toCalendar();
    Calendar calCompare = dtCompare.toCalendar();
    return cal.compareTo( calCompare );
    
    
  }
  
  public static void main( String[] args )
  {
    try
    {
      

      String strDate= "No Information Available\r\n\t\t\t"; 
      String strTime= "DSAS Close-Out Data"; 
      VwDate date = new VwDate( strDate + " " + strTime, "%m/%d/%Y %H:%M"); 

      if ( !date.isValid() )
        System.out.println("Date is in valid for the folowing reason " + date.getErrDesc() );
      
      String str = date.toString();
      if ( str == null )
        str = date.getErrDesc();
      
      VwDate today = new VwDate( 12,1,2004);
      
      int nDay = today.getDayOfWeek();
      
      
      return;
    }
    catch ( Exception ex )
    {
      ex.printStackTrace();
    }

  } // end main()
} // end class VwDate {}


// *** End VwDate.java

