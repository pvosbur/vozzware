/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwHexDump.java

============================================================================================
*/

package com.vozzware.util;

public class VwHexDump
{
  public static String dump( byte[] abData, boolean fIncludeOffset, int nWidth )
  {

    int nOffset = 0;

    StringBuffer sbResult = new StringBuffer();

    int nTot = 0;

    if ( nWidth <= 0 )
      nWidth = 16;

    if ( nWidth > abData.length )
      nWidth = abData.length;

    while( nTot < abData.length )
    {
      if ( fIncludeOffset )
        sbResult.append( VwFormat.right( String.valueOf( nOffset ), 6, '0' ) ).append( " - " );
        sbResult.append( VwFormat.right( String.valueOf( nOffset + ( nWidth - 1 ) ), 6, '0' ) ).append( "  " );

      for ( int x = 0; x < nWidth; x++ )
      {
        if ( nTot >= abData.length )
        {
          sbResult.append( "   " );
          continue;
        }
        String str =  Integer.toHexString( abData[ nTot++ ] ).toUpperCase();
        if ( str.length() == 1 )
          str = "0" + str;
        else
        if ( str.length() > 2 )
         str = str.substring( str.length() - 2, str.length() );

        sbResult.append( str ).append( " " );
      }

      sbResult.append( "  " );

      for ( int x = 0; x < nWidth; x++ )
      {
        if ( nOffset >= abData.length )
          break;

        int nVal = abData[ nOffset ];

        if ( nVal < 20 || nVal > 127 )
          sbResult.append( "." );             // Non printable character
        else
          sbResult.append( (char)abData[ nOffset ] );

        ++nOffset;

      }

      sbResult.append( "\n" );

    } // end while

    return sbResult.toString();

  } // end dump

  public static String dump( String strData, boolean fIncludeOffset, int nWidth )
  { return dump( strData.getBytes(), fIncludeOffset, nWidth ); }
} // end class VwHexDump{}

// *** End of VwHexDump.java ***