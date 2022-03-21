/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwMath.java

============================================================================================
*/


package com.vozzware.util;


/**
 * This class presently defines a static standard deviation method
 */
public class VwMath
{

  /**
   * Calculates a standard deviation from a double array
   *
   * @param adblValues An array of double values used to calculate the standard deviation
   *
   * @return A double with the standard deviation for the input array
   */
  public static double stddev( double[] adblValues )
  {
    // This check protects us from a divide by zero if only one element is defined

    if ( adblValues.length == 1 )
      return  Math.sqrt( adblValues[ 0 ] );

    double dblMean = 0;

    for ( int x = 0; x < adblValues.length; x++ )
      dblMean += adblValues[ x ];

    dblMean /= adblValues.length;

    double dblTemp = 0.0;

    for ( int y = 0; y < adblValues.length; y++ )
      dblTemp += Math.pow( (( adblValues[y]) - dblMean ), 2 );

    dblTemp /= (adblValues.length  );
    return Math.sqrt( dblTemp );

  } // end stddev()

} // end class VwMath{}

// *** End of VwMath.java ***
