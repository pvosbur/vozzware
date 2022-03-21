package com.vozzware.audio;

import java.io.FileOutputStream;

/*
============================================================================================

    Source File Name: 

    Author:           petervosburgh
    
    Date Generated:   4/13/16

    Time Generated:   8:00 AM

============================================================================================
*/
public interface VwRiffChunk
{
  void write( FileOutputStream fos ) throws Exception;

  long getLength();

  void setLength( long lRiffLength );

  String getChunkType();

  void setChunkType( String strChunkType );

}
