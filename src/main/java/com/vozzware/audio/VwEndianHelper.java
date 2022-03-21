/*
 *
 * ============================================================================================
 *
 *                                A r m o r e d  I n f o   W e b
 *
 *                                     Copyright(c) 2012 By
 *
 *                                       Armored Info LLC
 *
 *                             A L L   R I G H T S   R E S E R V E D
 *
 *  ============================================================================================
 * /
 */

package com.vozzware.audio;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/*
============================================================================================

    Source File Name: Transaction.java

    Author:           petervosburgh
    
    Date Generated:   5/2/13

    Time Generated:   6:54 AM

============================================================================================
*/
public class VwEndianHelper
{

  /**
   * Convert 4 byte set in little endian to a long
   *
   * @param lOffset Start offset in file byte array
   * @return
   */
  public static long bytes2Long( byte[] abData, long lOffset )
  {

    long lVal = (long) abData[(int)lOffset] & 0xFF;
    lVal += ((long) abData[(int)++lOffset] & 0xFF) << 8;
    lVal += ((long) abData[(int)++lOffset] & 0xFF) << 16;
    lVal += ((long) abData[(int)++lOffset] & 0xFF) << 24;

    return lVal;


  }

  /**
   * Convert 2 byte set in little endian to an int
   *
   * @param lOffset Start offset in file byte array
   * @return
   */
  public static int bytes2Int( byte[] abData,  long lOffset )
  {

    int nVal = abData[(int)lOffset];
    nVal += (abData[(int)++lOffset]) << 8;

    return nVal;
  }

  /**
   * Converts an integer to a 4 byte array
   * @param nNbr The number to convert
   * @param fUseLittileEndian If true use little endian else use big endian
   * @return
   */
  public static byte[] intToBytes( int nNbr, boolean fUseLittileEndian )
  {
    ByteBuffer bb = ByteBuffer.allocate( 4 );

    if ( fUseLittileEndian )
    {
      bb.order( ByteOrder.LITTLE_ENDIAN ).putInt( nNbr );
    }
    else
    {
      bb.order( ByteOrder.BIG_ENDIAN ).putInt( nNbr );

    }

   return bb.array();

  }


  /**
   * Converts a long to a 8 byte array
   *
   * @param lNbr The number to convert
   * @param fUseLittileEndian If true use little endian else use big endian
   * @return
   */
  public static byte[] longToBytes( long lNbr, boolean fUseLittileEndian )
  {
    ByteBuffer bb = ByteBuffer.allocate( 8 );

    if ( fUseLittileEndian )
    {
      bb.order( ByteOrder.LITTLE_ENDIAN ).putLong( lNbr );
    }
    else
    {
      bb.order( ByteOrder.BIG_ENDIAN ).putLong( lNbr );

    }

   return bb.array();

  }

}
