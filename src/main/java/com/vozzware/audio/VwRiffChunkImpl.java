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

/*
============================================================================================

    Source File Name: Transaction.java

    Author:           petervosburgh
    
    Date Generated:   4/27/13

    Time Generated:   8:35 AM

============================================================================================
*/
public class VwRiffChunkImpl
{
  private byte[] m_abChunkData;
  
  private String m_strChunkType;
  private long   m_lOffset;
  private long   m_lLength;


  public VwRiffChunkImpl( byte[] abChunkData, String strChunkType, long lOffset, long lLength )
  {
    m_abChunkData = abChunkData;
    m_strChunkType = strChunkType;
    m_lOffset = lOffset;
    m_lLength = lLength;

  }



  public String toString()
  {
    return "\nChunk Type: " + m_strChunkType + ", Offset: " + m_lOffset + ", Length: " + m_lLength;
  }


  public String getChunkType()
  {
    return m_strChunkType;
  }


  public long getOffset()
  {
    return m_lOffset;
  }

  public void setOffset( long lOffset )
  {
    m_lOffset = lOffset;
  }

  public long getLength()
  {
    return m_lLength;
  }

  public void setLength( long lLength )
  {
    m_lLength = lLength;
  }

  /**
   * Gets the raw byte data for the length of this chunk
   * @return
   */
  public byte[] getData()
  {
    
    byte[] abChunkData = new byte[ (int)m_lLength ];
    
    System.arraycopy( m_abChunkData, (int)m_lOffset, abChunkData, 0, (int)m_lLength );
    
    return abChunkData;
  }  
} // end VwRiffChunkImpl{}
