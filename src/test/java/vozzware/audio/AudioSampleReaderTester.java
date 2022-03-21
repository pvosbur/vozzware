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

package test.vozzware.audio;

import com.vozzware.audio.VwEndianHelper;
import com.vozzware.audio.VwFmtChunk;
import com.vozzware.audio.VwRiffChunkImpl;
import com.vozzware.audio.VwWavReader;
import junit.framework.Assert;
import org.junit.Test;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;

/*
============================================================================================

    Source File Name: Transaction.java

    Author:           petervosburgh
    
    Date Generated:   5/2/13

    Time Generated:   6:26 AM

============================================================================================
*/
public class AudioSampleReaderTester
{


  @Test
  public void readWavFormat() throws Exception
  {

    try
    {

      ByteBuffer bb = ByteBuffer.allocate(4);
      bb.order( ByteOrder.LITTLE_ENDIAN ).putInt( 10 );

      byte[] abLongs = bb.array();


      int nTest = VwEndianHelper.bytes2Int( abLongs, 0 );



      //File fileWave= new File( "/Users/peter/Documents/PassionBaby2Vocal.wav" );
      File fileWave= new File( "/Users/petervosburgh/Documents/VozzMusic/mastered/Victoria-2014 (Mastered).wav" );

      VwWavReader wrdr = new VwWavReader( fileWave );

      List<VwRiffChunkImpl> listChunks = wrdr.getChunkList();

      // Dump Out ChinkList

      for ( VwRiffChunkImpl rChunk : listChunks )
      {
        System.out.println( rChunk.toString());
      }


      VwFmtChunk fmtChunk = wrdr.getFormatChunk();

      Assert.assertNotNull( "Did not expect null fmt chunk", fmtChunk );


      VwRiffChunkImpl dataChunk = wrdr.getDataChunk();

      Assert.assertNotNull( "Did not expect null fmt chunk", dataChunk );

      byte[] abSampleData = dataChunk.getData();

      int nSampleLen = abSampleData.length / 2;

      int[] aSamples = new int[ nSampleLen ];

      int nSample = -1;
      int nHiVal = -32767;
      int nLowVal = 32767;

      for ( int x = 0; x < nSampleLen; x+= 2 )
      {
        int nVal = VwEndianHelper.bytes2Int( abSampleData, x );

        if ( nVal > nHiVal )
          nHiVal = nVal;

        if ( nVal < nLowVal )
          nLowVal = nVal;

        aSamples[ ++nSample ] = nVal;

      }

      System.out.println( "Done");
    }
    catch( Exception ex )
    {
      ex.printStackTrace();
    }

  }

}
