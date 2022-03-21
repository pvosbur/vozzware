/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwMoney.java

============================================================================================
*/

package com.vozzware.util;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * The VwMoney class fixes money arithmetic rounding errors that typically
 * result when dealing with floating point data types i.e. float and double's.
 * This class converts double money values to pennies represented by the long
 * data type when doing arithmetic operations. The toString method formats
 * the final value as a string back to dollars and cents. The getValueString method
 * returns the value formatted with commas and a dollar sign
 */
public class VwMoney
{
  private long          m_lVal = 0;     // Holds the accululated result

  private NumberFormat  m_fmt = NumberFormat.getCurrencyInstance( Locale.getDefault() );



  /**
   * Default constructor which initializes the accumulator to zero and uses the US Loacale
   */
  public VwMoney()
  {
    m_lVal = 0;
  }



  /**
   * Constructor that initializes the accumulator from the passed parameter
   *
   * @param money The VwMoney instance to initialize this instance with
   */
  public VwMoney( VwMoney money )
  { m_lVal = money.m_lVal; }


  /**
   * Constructor that initializes the accumulator from another VwMoney instance
   *
   * @param dblVal The initial value to start with
   */
  public VwMoney( double dblVal )
  { m_lVal = round( dblVal ); }


  /**
   * Constructor that initializes the accumulator from another VwMoney instance
   *
   * @param strVal The initial value to start with
   */
  public VwMoney( String strVal )
  { this( Double.parseDouble( strVal ) ); }

  /**
   * Add a value to the accumulator
   *
   * @param dblVal The value to add
   *
   */
  public void add( double dblVal )
  { m_lVal += round( dblVal ); }


  /**
   * Add a value to the accumulator
   *
   * @param strVal The value to add
   *
   */
  public void add( String strVal )
  { add( Double.parseDouble( strVal ) ); }


  /**
   * Add a value to the accumulator from an VwMoney instance
   *
   * @param money The VwMoney instance
   *
   */
  public void add( VwMoney money )
  { m_lVal += money.m_lVal; }


  /**
   * Subtract a value from the accumulator
   *
   * @param dblVal The value to subtract
   *
   */
  public void sub( double dblVal )
  { m_lVal -= round( dblVal ); }


  /**
   * Subtract a value from the accumulator
   *
   * @param dblVal The value to subtract
   *
   */
  public void sub( String strVal )
  { sub( Double.parseDouble( strVal ) ); }


  /**
   * Subtract a value from the accumulator from an VwMoney instance
   *
   * @param money The VwMoney instance
   *
   */
  public void sub( VwMoney money )
  { m_lVal -= money.m_lVal; }

  /**
   * Muiltiply the accumulator value by the parameter
   *
   * @param dblVal The value that the accumulator will be multiplied with
   *
   */
  public void mult( double dblVal )
  {
    double dblAcculm = Double.parseDouble( toString() );
    dblAcculm *= dblVal;
    m_lVal = round( dblAcculm );

  } // end mult


  /**
   * Muiltiply the accumulator value by the parameter
   *
   * @param strVal The value that the accumulator will be multiplied with
   *
   */
  public void mult( String strVal )
  { mult( Double.parseDouble( strVal ) ); }

  /**
   * Muiltiply the accumulator value by the value in the VwMoney instance
   *
   * @param money The VwMoney instance
   *
   */
  public void mult( VwMoney money )
  { mult( money.getDouble() ); }

  /**
   * Divide the accumulator by the parameter
   *
   * @param dblVal The value that the accumulator will be divided by
   *
   */
  public void div( double dblVal )
  {
    double dblAcculm = Double.parseDouble( toString() );
    dblAcculm /= dblVal;
    m_lVal = round( dblAcculm );

  } // end div


  /**
   * Divide the accumulator by the parameter
   *
   * @param strVal The value that the accumulator will be divided by
   *
   */
  public void div( String strVal )
  { div( Double.parseDouble( strVal ) ); }

  /**
   * Divide the accumulator by the parameter
   *
   * @param dblVal The value that the accumulator will be divided by
   *
   */
  public void div( VwMoney money )
  { div( money.getDouble() ); }



  /**
   * Rounds the double when it converts it to a long if needed
   */
  private long round( double dblVal )
  {
    long lOrig = (long)(dblVal * 100 );

    long lTemp = (long)(dblVal * 1000);

    long lDiff = lTemp - (lOrig * 10);
    if ( Math.abs( lDiff )  >= 5 )
    {
      if ( lOrig < 0 )
        --lOrig;
      else
        ++lOrig;
    }

    return lOrig;

  } // end round

  /**
   * Returns the accumulator as a long
   */
  public long getLong()
  { return m_lVal; }


  /**
   * Returns the accumulator as a double
   */
  public double getDouble()
  { return Double.parseDouble( toString() ); }


  /**
   * Return the accumulator formatted with two decimal places
   */
  public String toString()
  {
    String str = String.valueOf( m_lVal );

    int nPlaces = 2;

    boolean fNeg = false;

    if ( str.startsWith( "-" ) )
    {
      fNeg = true;
      ++nPlaces;
    }

    if ( str.length() < nPlaces )
    {
      int nPad = nPlaces - str.length();

      StringBuffer sb = new StringBuffer( nPad );

      for ( int x = 0; x < nPad; x++ )
        sb.append( '0' );

      if ( fNeg )
      {
        sb.append( str.substring( 1 ) );
        String strTemp = sb.toString().substring( 0, 2 );

        if ( strTemp.equals( "00" ) )
          return "0.00";

        return "-0." + strTemp;
      }

      sb.append( str );

      return "0." + sb.toString().substring( 0, 2 );
    }


    return  str.substring( 0, str.length() - 2) + "." + str.substring( str.length() - 2 );

  }

  /**
   * Formats the final result with a dollar sign and commas
   */
  public String getValueString()
  {
    String strVal = toString();
    return m_fmt.format( Double.parseDouble( strVal ) );

  }
} // end class VwMoney{}

// *** End of VwMoney.java ***
