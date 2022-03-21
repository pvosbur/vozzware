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

import java.io.FileOutputStream;

/*
============================================================================================

    Source File Name: Transaction.java

    Author:           petervosburgh
    
    Date Generated:   5/2/13

    Time Generated:   6:27 AM

============================================================================================
*/
public class VwFmtChunk extends VwRiffChunkImpl
{

  private int  m_nAudioFormat;
  private int  m_nNbrChannels;
  private long m_lSampleRate;
  private long m_lByteRate;
  private int  m_nBlockAlign;
  private int  m_nBitsPerSample;


  public VwFmtChunk( byte[] abData, long lOffset, long lLength )
  {
    super( abData, "fmt ", lOffset, lLength );


    getFormatFields( abData, lOffset + 4 );
  }

  protected void write( FileOutputStream fos ) throws Exception
  {

  }

  private void getFormatFields( byte[] abData, long lOffset )
  {
    m_nAudioFormat = VwEndianHelper.bytes2Int( abData, lOffset );

    lOffset += 2;

    m_nNbrChannels = VwEndianHelper.bytes2Int( abData, lOffset );

    lOffset += 2;

    m_lSampleRate = VwEndianHelper.bytes2Long( abData, lOffset );

    lOffset += 4;

    m_lByteRate = VwEndianHelper.bytes2Long( abData, lOffset );

    lOffset += 4;

    m_nBlockAlign = VwEndianHelper.bytes2Int( abData, lOffset );

    lOffset += 2;

    m_nBitsPerSample = VwEndianHelper.bytes2Int( abData, lOffset );


  }


  public int getAudioFormat()
  {
    return m_nAudioFormat;
  }

  public void setAudioFormat( int nAudioFormat )
  {
    m_nAudioFormat = nAudioFormat;
  }

  public int getNbrChannels()
  {
    return m_nNbrChannels;
  }

  public void setNbrChannels( int nNbrChannels )
  {
    m_nNbrChannels = nNbrChannels;
  }

  public long getSampleRate()
  {
    return m_lSampleRate;
  }

  public void setSampleRate( long lSampleRate )
  {
    m_lSampleRate = lSampleRate;
  }

  public long getByteRate()
  {
    return m_lByteRate;
  }

  public void setByteRate( long lByteRate )
  {
    m_lByteRate = lByteRate;
  }

  public int getBlockAlign()
  {
    return m_nBlockAlign;
  }

  public void setBlockAlign( int nBlockAlign )
  {
    m_nBlockAlign = nBlockAlign;
  }

  public int getBitsPerSample()
  {
    return m_nBitsPerSample;
  }

  public void setBitsPerSample( int nBitsPerSample )
  {
    m_nBitsPerSample = nBitsPerSample;
  }
} // end VwFmtChunk{}
