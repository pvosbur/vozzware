/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwBase64.java

============================================================================================
*/
package com.vozzware.util;



/**
 * Decodes/Encodes MIME base64 messages
*/
public class VwBase64
{
  // *** Translation table

  private static byte m_achbase64Tbl[] = {
                      'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N',
                      'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b',
                      'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p',
                      'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3',
                      '4', '5', '6', '7', '8', '9', '+', '/'
                      };

  /**
   * Returns the index of the base64 char in the base64 alphabet table
   *
   * @param chChar - The character to lookup
   */
  private static byte getIndex( char chChar )
  {
    if ( chChar == '=' )
      return (byte)0;

    int nEntries = m_achbase64Tbl.length;

    for ( int x = 0; x < nEntries; x++ )
    {
      if ( m_achbase64Tbl[ x ] == chChar )
        return (byte)x;

    } // end for


    return 99;            // Invalid character

  } // end getIndex



  /**
   * Decodes a MIME base64 message
   *
   * @param abDecode - The base 64 encoded byte array to decode
   *
   * @return A byte array containing the decoded message, or null if the message is invalid
   */
  public static byte[] decode( byte[] abDecode )
  {


    int ndx = 0;                      // Index for decoded array

    if ( ( abDecode.length % 4 ) != 0 )
      return null;                    // Invalid base64 msg if not multiple of 4

    int nMsgLen = abDecode.length;    // Length of incomming msg

    int nSets = abDecode.length / 4;
    int nPads = 0;

    // Find out how many pad characters there are

    for( int x = abDecode.length -1; x >= 0; x-- )
    {
      if ( abDecode[ x ] != (byte)'=' )
        break;

      ++nPads;

    }

    // Create new byte array to hold decoded msg

    byte[] abDecoded = new byte[ abDecode.length - nSets - nPads ];

    int nOffset = 0;         // Index for the source input decode array

    while( true )
    {

      if ( nOffset >= nMsgLen )
        break;

      byte b1 = getIndex( (char)abDecode[ nOffset++ ] );
      byte b2;
      byte bSave;

      if ( b1 == 99 )
        return null;

      b1 <<= 2;

      if ( nOffset >= nMsgLen )
        break;

      if ( abDecode[ nOffset ] == (byte)'=' )
        break;                 // Pad character all done

      b2 = bSave = getIndex( (char)abDecode[ nOffset++ ] );

      if ( b2 == 99 )
        return null;

      b2 <<= 2;
      b2 = (byte)(b2 >> 6 &0x03);

      b1 |= b2;

      abDecoded[ ndx++ ] = b1;

      b1 = (byte)( bSave << 4 );


      if ( nOffset >= nMsgLen )
        break;

      if ( abDecode[ nOffset ] == (byte)'=' )
        break;                 // Pad character; all done

      b2 = bSave = getIndex( (char)abDecode[ nOffset++ ] );

      if ( b2 == 99 )
        return null;

      b2 <<= 2;

      b2 = (byte)(b2 >> 4 & 0x0F);

      b1 |= b2;

      abDecoded[ ndx++ ] = b1;

      b1 = (byte)(bSave << 6);

      if ( nOffset >= nMsgLen )
        break;

      if ( abDecode[ nOffset ] == (byte)'=' )
        break;                 // Pad character; all done

      b2 = bSave = getIndex( (char)abDecode[ nOffset++ ] );

      if ( b2 == 99 )
        return null;

      b2 <<= 2;

      b2 = (byte)(b2 >> 2 & 0x3F);

      b1 |= b2;

      abDecoded[ ndx++ ] = b1;

    } // end while()


    return abDecoded;


  } // end decode()

  /**
   * Encodes a string of characters to the Base64 MIME format
   *
   * @param abMsg - The string to encode
   *
   * @return A String containing the BASE 64 encoded message
   */
   public static byte[] encode( byte[] abMsg )
   {
     // Compute the length of the result byte array: Every 3 characters yields a fourth, plus
     // pad characters to make the return array a multiple of 4 X the original msg size

     int nSets = abMsg.length / 3;

     int nRemain = abMsg.length % 3;


     int nTot = abMsg.length + nSets + nRemain;

     // Make the length a multiple of 4

     if ( nTot < 4 )
       nTot = 4;
     else
     if ( nTot % 4 > 0 )
       nTot += ( nTot % 4 );

     // Create result array

     byte[] abRes = new byte[ nTot  ];

     int nOffset = 0;
     int ndx = 0;

     int nChars = 0;

     int nMsgLen = abMsg.length;

     while( true )
     {

       byte b1 = 0;
       byte b2 = 0;
       byte bSave = 0;

       if ( nOffset >= nMsgLen )
         break;

       b1 = b2 = abMsg[ nOffset++ ];

       b1 = (byte)( b1 >>> 2 & 0x3f);     // Make upper 6 bits into an index

       abRes[ ndx++ ] = m_achbase64Tbl[ b1 ];

       b2 &= 0x03;                        // Clear all but the 2 lower bits left over

       // Move the leftover two bits to the first 4 positions of the next 6 bit index

       b2 <<= 4;


       if ( nOffset >= nMsgLen )
       {
         abRes[ ndx++ ] = m_achbase64Tbl[ b2 ];
         break;
       }

       b1 = bSave = abMsg[ nOffset++ ];

       // The next 4 bits of the next byte are required

       b1 = (byte)(b1 >>> 4 & 0x0F );

       // *** Add em in

       b2 |= b1;

       abRes[ ndx++ ] = m_achbase64Tbl[ b2 ];

       b1 = (byte)((bSave & 0x0f) << (byte)2);

       if ( nOffset >= nMsgLen )
       {
         abRes[ ndx++ ] = m_achbase64Tbl[ b1 ];
         break;
       }

       b2 = bSave = abMsg[ nOffset++ ];

       b2 =  (byte)(b2 >>> 6 & 0x03);

       b1 |= b2;

       abRes[ ndx++ ] = m_achbase64Tbl[ b1 ];

       bSave &= 0x3f;             // Remainder 6 bits

       abRes[ ndx++ ] = (byte)m_achbase64Tbl[ bSave ];

     } // end while()

     int nNeed = 0;
     nChars = ndx;

     if ( nChars > 4 )
       nNeed = 4 - ( nChars % 4 );
     else
       nNeed = 4 - nChars;

     if ( nNeed == 4 )
       return abRes;

     for ( int x = 0; x < nNeed; x++ )
       abRes[ ndx++ ] = (byte)'=';

    return abRes;

 } // end encode()

} // end class VwBase64{}


// *** End of VwBase64.java
