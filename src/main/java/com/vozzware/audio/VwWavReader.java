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
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/*
============================================================================================

    Source File Name: Transaction.java

    Author:           petervosburgh
    
    Date Generated:   4/27/13

    Time Generated:   8:34 AM

============================================================================================
*/
public class VwWavReader
{
  private File m_fileWave;

  private byte[] m_abFile;

  private List<VwRiffChunkImpl> m_listRiffChunks;


  /**
   * Constructor
   *
   * @param fileWave The wave file to process
   *
   * @throws Exception If file is not a valid wav file
   */
  public VwWavReader( File fileWave ) throws Exception
  {
    m_fileWave = fileWave;

    processWavData( new FileInputStream( m_fileWave ) );

  }


  /**
   * Process the contents of teh wav file data
   *
   * @param insWavFile The inputstream to the wav file
   * @throws Exception
   */
  private void processWavData( InputStream insWavFile ) throws Exception
  {

    m_abFile = new byte[ (int)m_fileWave.length() ];

    insWavFile.read( m_abFile );

    m_listRiffChunks = new ArrayList<VwRiffChunkImpl>();


    validateHeader();


  }


  /**
   * Validate the wav file header
   * @throws Exception
   */
  private void validateHeader() throws Exception
  {

    String strId = new String( m_abFile, 0, 4 );

    if ( !strId.equals( "RIFF" ) )
    {
      throw new Exception( "This file does not appear to be valid wave file");
    }

    // Get File file

    long lFileSize = VwEndianHelper.bytes2Long( m_abFile, 4 ) + 8;

    if ( lFileSize != m_fileWave.length() )
    {
      throw new Exception( "This file length in the wave heder does not match the file size");
    }

    String strType = new String( m_abFile, 8, 4 );

    if ( !strType.equals( "WAVE" ) )
    {
      throw new Exception( "Expected Header type to be 'WAVE' but got strType");
    }

    buildChunkList();
  }


  /**
   * Build a list of the wav file chunks
   */
  private void buildChunkList()
  {
    long lOffset = 12;

    while ( lOffset < m_abFile.length )
    {
      if ( (m_abFile.length - lOffset) < 4 )
      {
        return;
      }

      String strChunkType = new String( m_abFile, (int)lOffset, 4 );

      lOffset += 4;

      long lChunkLen =  VwEndianHelper.bytes2Long( m_abFile, lOffset );


      VwRiffChunkImpl rChunk = null;

      if ( strChunkType.startsWith( "fmt" ))
      {
        rChunk = new VwFmtChunk( m_abFile, lOffset, lChunkLen );
      }
      else
      {
        rChunk = new VwRiffChunkImpl( m_abFile, strChunkType, lOffset, lChunkLen );
      }

      m_listRiffChunks.add( rChunk );

      lOffset += lChunkLen + 4;

    }
  }


  public List<VwRiffChunkImpl>getChunkList()
  { return m_listRiffChunks; }


  /**
   * Gets the format chunk
   * @return
   */
  public VwFmtChunk getFormatChunk()
  {
    return (VwFmtChunk)getChunkByType( "fmt " );

  }


  /**
   * Gets the data chunk
   * @return
   */
  public VwRiffChunkImpl getDataChunk()
  {
    return getChunkByType( "data" );

  }

  /**
   * Get a RIFF chunk by its type
   *
   * @param strChunkType The chunk type tp retrieve
   * @return
   */
  public VwRiffChunkImpl getChunkByType( String strChunkType )
  {
    if ( m_listRiffChunks == null )
    {
      return null;
    }

    for ( VwRiffChunkImpl rChunk : m_listRiffChunks )
    {
      if ( rChunk.getChunkType().equalsIgnoreCase( strChunkType ))
      {
        return rChunk;
      }
    }

    return null;

  }
}
