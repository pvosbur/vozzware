/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwFormat.java

============================================================================================
*/


package com.vozzware.util;


public class VwFormat extends Object
{
  /**
   * Left justifies a string by adding fill characters to the right of the
   * string, as necessary to a form a new string with the specified length.
   *
   * @param strOrig - The string to be left justified
   * @param nSize - The length of the new string
   * @param chFill - The fill character
   *
   * @return A new string, containing the original string left justified and right
   * is less than the original string length, a substring with the first nSize characters
   * of the original string is returned in the new string.
   */
  public static String left( String strOrig, int nSize, char chFill )
  {
    String str;

    int nCurLen = strOrig.length();

    if ( nSize < 0 )
      nSize = nCurLen + 1;
    
    if ( nCurLen > nSize )
    {
      str = strOrig.substring( 0, nSize );
      nCurLen = nSize;
    }
    else
      str = strOrig;

    StringBuffer sb = new StringBuffer( nSize );

    // *** new requested size is less then the original just return the string
    if ( nSize < nCurLen )
      return str;

    // *** Insert string at beginning

    sb.insert( 0, str );

    int nFill = nSize - nCurLen;

    // Add fill characters to end of string

    for ( int ndx = 0; ndx < nFill; ndx++ )
      sb.append( chFill );


    return sb.toString();

  } // end left()


  /**
   * Right justifies a string by adding fill characters to the left of the
   * string, as necessary to a form a new string with the specified length.
   *
   * @param strOrig - The string to be right justified
   * @param nSize - The length of the new string
   * @param chFill - The fill character
   *
   * @return A new string, containing the original string right justified and left
   * padded with the fill character to the specified length; if the specified length
   * is less than the original string length, a substring with the first nSize characters
   * of the original string is returned in the new string.
   */
  public static String right( String strOrig, int nSize, char chFill )
  {
	String str;

    int nCurLen = strOrig.length();

	if ( nCurLen > nSize )
    {
      str = strOrig.substring( 0, nSize );
      nCurLen = nSize;
    }
    else
      str = strOrig;

	StringBuffer sb = new StringBuffer( nSize );

	if ( nSize < nCurLen )
      return str;

	int ndx = 0;
    int nFill = nSize - nCurLen;

	for ( ndx = 0; ndx < nFill; ndx++ )
      sb.append( chFill );

    // *** Insert text following the fill characters

	sb.insert( ndx, str );

    return sb.toString();

  } // end right()


  /**
   * Centers a string by adding fill characters to the left and right of the
   * string, as necessary to a form a new string with the specified length.
   *
   * @param strOrig - The string to be centered
   * @param nSize - The length of the new string
   * @param chFill - The fill character
   *
   * @return A new string, containing the original string centered, and left and right
   * padded with the fill character to the specified length; if the specified length
   * is less than the original string length, a substring with the first nSize characters
   * of the original string is returned in the new string.
   *
   * NOTE: If the number of pad characters is uneven, the extra pad character is
   * placed on the right side of the new string.
   */
  public static String center( String strOrig, int nSize, char chFill )
  {
	String str;

    int nCurLen = strOrig.length();

	if ( nCurLen > nSize )
    {
      str = strOrig.substring( 0, nSize );
      nCurLen = nSize;
    }
    else
     str = strOrig;

	StringBuffer sb = new StringBuffer( nSize );

	if ( nSize < nCurLen )
      return str;

	int nLeft = ( nSize / 2 - nCurLen / 2 );

	int ndx = 0;

	for ( ndx = 0; ndx < nLeft; ndx++ )
      sb.append( chFill );

	sb.insert( ndx, str );

	int nRight = nSize - ( nLeft + nCurLen );

	for ( ndx = 0; ndx < nRight; ndx++ )
      sb.append( chFill );

    return sb.toString();

  } // end center()


} // end VwFormat

