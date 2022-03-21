/*
 *
 * ============================================================================================
 *
 *                                     V o z z w a r e  L L C
 *
 *                                     Copyright(c) 2016 By
 *
 *                                     V o z z w a r e  L L C
 *
 *                             A L L   R I G H T S   R E S E R V E D
 *
 *  ============================================================================================
 * /
 */

package com.vozzware.audio;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

/*
============================================================================================

    Source File Name: Transaction.java

    Author:           petervosburgh
    
    Date Generated:   4/27/13

    Time Generated:   8:34 AM

============================================================================================
*/
public class VwWaveWriter
{
  private File m_fileWave;

  private FileOutputStream m_fileWriter;

  private List<VwRiffChunkImpl> m_listRiffChunks;


  /**
   * Constructor
   *
   * @param fileWave a File Object representing the
   *
   */
  public VwWaveWriter( File fileWave ) throws Exception
  {

    if (!fileWave.canWrite() )
    {
      throw new Exception( "The File : " + fileWave.getAbsolutePath() + " Is not writable");
    }

    m_fileWave = fileWave;

  }


  /**
   * Adds a RIFF chunk to be written
   *
   * @param chunkToAdd The WAVE RIFF chuck to add to the file
   * @throws Exception
   */
  private void add( VwRiffChunkImpl chunkToAdd ) throws Exception
  {
    m_listRiffChunks.add( chunkToAdd );

  }



  /**
   * Validate the wav file header
   * @throws Exception
   */
  private void writeHeader() throws Exception
  {

    m_fileWriter = new FileOutputStream( m_fileWave );
    m_fileWriter.write( "RIFF".getBytes() );

    // Get the length of the chunks

    long lTotChunkLength = 0;

    for ( VwRiffChunkImpl rChunk : m_listRiffChunks )
    {
      lTotChunkLength += rChunk.getLength() + 8; // also include the 4 byte chunk id the the 4 byte length byte
    }

    byte[] abLength = VwEndianHelper.intToBytes( (int)lTotChunkLength, true );

    m_fileWriter.write( abLength );

    m_fileWriter.write( "WAVE".getBytes() );


  }



  public List<VwRiffChunkImpl>getChunkList()
  { return m_listRiffChunks; }


 } // end class VwWaveWriter{}
