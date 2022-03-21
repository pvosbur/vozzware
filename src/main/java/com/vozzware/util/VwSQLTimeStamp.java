/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwSQLTimeStamp.java

============================================================================================
*/


package com.vozzware.util;

import java.util.Calendar;

class VwSQLTimeStamp
{
  private int   m_nMonth;       // Month
  private int   m_nDay;         // Day
  private int   m_nYear;        // Year
  private int   m_nHours;       // Hours
  private int   m_nMin;         // Minutes
  private int   m_nSecs;        // Seconds
  private int   m_nFraction;    // Fraction seconds


  /*
   * Initializes the current object using the specified values
   *
   * @param nMonth - The Month component
   * @param nDay - The Day component
   * @param nYear - The Year component
   * @param nHours - The Hours component
   * @param nMin - The Minutes component
   * @param nSecs - The Seconds component
   * @param nFract - The Fraction component
   */
  private void Assign( int nMonth, int nDay, int nYear, int nHours, int nMin, int nSecs,
                       int nFract )
  {
    m_nMonth = nMonth + 1;
    m_nDay = nDay;
    m_nYear = nYear;
    m_nHours = nHours;
    m_nMin = nMin;
    m_nSecs = nSecs;
    m_nFraction = nFract;

  } // end Assign()


  /**
   * Constructs a timestamp object for the default system date
   *
   */
  public VwSQLTimeStamp()
  {
    Calendar date = Calendar.getInstance();

    Assign( date.get( Calendar.MONTH ), date.get( Calendar.DAY_OF_MONTH ), date.get( Calendar.YEAR ),
            date.get( Calendar.HOUR ), date.get( Calendar.MINUTE ), date.get( Calendar.SECOND ), 0 );

  } // end VwSQLTimeStamp()


  /**
   * Constructs a timestamp from the given values
   *
   * @param nMonth - The Month component (1 - 12)
   * @param nDay - The Day component (1 - 31)
   * @param nYear - The Year component (4 digits required)
   * @param nHours - The Hours component (0 - 23)
   * @param nMin - The Minutes component (0 - 59)
   * @param nSecs - The Seconds component (0 - 59)
   * @param nFract - The Fraction component (limited to 32 bit integer size)
   */

  public VwSQLTimeStamp( int nMonth, int nDay, int nYear, int nHours, int nMin, int nSecs,
                           int nFract )
  {
    Assign( nMonth, nDay, nYear, nHours, nMin, nSecs, nFract );

  } // end VwSQLTimeStamp()


  /**
   * Sets the Month component
   *
   * @param nMonth - The Month number (1 - 12)
   */
  public final void setMonth( int nMonth )
  { m_nMonth = nMonth; }


  /**
   * Sets the Day component
   *
   * @param nDay - The Day number (1 - 31)
   */
  public  final void setDay( int nDay )
  { m_nDay = nDay; }


  /**
   * Sets the Year component
   *
   * @param nYear - The Year number (4 digits required)
   */
  public final void setYear( int nYear )
  { m_nYear = nYear; }


  /**
   * Sets the Hours component
   *
   * @param nHours - The number of Hours (0 - 23)
   */
  public final void setHours( int nHours )
  { m_nHours = nHours; }


  /**
   * Sets the Minutes component
   *
   * @param nMin - The number of Minutes (0 - 59)
   */
  public final void setMinutes( int nMin )
  { m_nMin = nMin; }


  /**
   * Sets the Seconds component
   *
   * @param nSecs - The number of Seconds (0 - 59)
   */
  public final void setSeconds( int nSecs )
  { m_nSecs = nSecs; }


  /**
   * Sets the Fraction component
   *
   * @param nFract - The Fraction number (limited to 32 bit integer size)
   */

  public final void setFraction( int nFract )
  { m_nFraction = nFract; }


  /**
   * Gets the Month component
   *
   * @return The Month component as an integer
   */
  public final int getMonth()
  { return m_nMonth; }


  /**
   * Gets the Days component
   *
   * @return The Days component as an integer
   */
  public final int getDay()
  { return m_nDay; }

  /**
   * Gets the Years component
   *
   * @return The Years component as an integer
   */
  public final int getYear()
  { return m_nYear; }


  /**
   * Gets the Hours component
   *
   * @return The Hours component as an integer
   */
  public final int getHours()
  { return m_nHours; }


  /**
   * Gets the Minutes component
   *
   * @return The Minutes component as an integer
   */
  public final int getMinutes()
  { return m_nMin; }


  /**
   * Gets the Seconds component
   *
   * @return The Seconds component as an integer
   */
  public final int getSeconds()
  { return m_nSecs; }


   /**
   * Gets the Fraction component
   *
   * @return the Fraction component as an integer
   */
 public final int getFraction()
  { return m_nFraction; }


} // end class VwSQLTimeStamp


// *** End VwSQLTimeStamp.java
