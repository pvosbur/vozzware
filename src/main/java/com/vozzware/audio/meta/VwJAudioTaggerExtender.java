package com.vozzware.audio.meta;

import com.vozzware.util.VwExString;
import com.vozzware.util.VwFileUtil;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.audio.mp3.MP3AudioHeader;
import org.jaudiotagger.audio.mp4.Mp4AudioHeader;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.datatype.Artwork;
import org.jaudiotagger.tag.id3.ID3v1Tag;
import org.jaudiotagger.tag.id3.ID3v24Tag;
import org.jaudiotagger.tag.mp4.Mp4FieldKey;
import org.jaudiotagger.tag.mp4.Mp4Tag;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/*
============================================================================================

    Source File Name: Transaction.java

    Author:           petervosburgh
    
    Date Generated:   1/26/13

    Time Generated:   6:49 AM

============================================================================================
*/
public class VwJAudioTaggerExtender
{

  private AudioFile       m_af;

  private File            m_fileAudio;
  private String          m_strFileExt;

  private AudioHeader     m_audioHdr = null;
  private MP3AudioHeader  m_mp3AudioHdr = null;
  private Mp4AudioHeader  m_mp4AudioHdr = null;

  private Tag             m_audioTag;

  private ID3v1Tag        m_id3V1Tag;

  private ID3v24Tag       m_v24Tag;

  private Mp4Tag          m_mp4Tag;


  /**
   * Constructor
   *
   * @param fileToParse  The file to parse
   * @throws Exception  if the file cannot be read or is not a valid audio file
   */
  public VwJAudioTaggerExtender( File fileToParse ) throws Exception
  {
    m_af = AudioFileIO.read( fileToParse );
    m_fileAudio = fileToParse;

    setup();
  }

  /**
   * Find out what meta type we are and get the tag info for that type
   * @throws Exception
   */
  private void setup() throws Exception
  {

    m_strFileExt = VwExString.getFileExt( m_fileAudio );
    m_audioHdr = m_af.getAudioHeader();

    m_audioTag = m_af.getTag();

    if ( m_audioHdr instanceof MP3AudioHeader )
    {
      m_mp3AudioHdr = (MP3AudioHeader)m_audioHdr;


      if ( m_audioTag instanceof ID3v1Tag )
      {
        m_id3V1Tag = (ID3v1Tag)m_audioTag;
      }
      else
      if ( m_audioTag instanceof ID3v24Tag )
      {
        m_v24Tag = (ID3v24Tag)m_audioTag;
      }
    }
    else
    if ( m_audioHdr instanceof Mp4AudioHeader )
    {
      m_mp4AudioHdr = (Mp4AudioHeader)m_audioHdr;
      if ( m_audioTag instanceof Mp4Tag )
      {
        m_mp4Tag = (Mp4Tag)m_audioTag;
      }

    }


  }


  public boolean isWavFile()
  { return m_strFileExt.equalsIgnoreCase( "wav" ); }

  public boolean isMp3File()
  { return m_strFileExt.equalsIgnoreCase( "mp3" ); }


  public boolean isMp4File()
  { return m_strFileExt.equalsIgnoreCase( "m4a" ); }

  public int getTrackLength()
  {

    if ( m_audioHdr != null )
    {
      return m_audioHdr.getTrackLength();
    }

    return 0;

  }

  public String getTrackLengthAsString()
  {

    int nTrackLen = getTrackLength();

    int nMin = nTrackLen / 60;
    int nSecs = nTrackLen % 60;

    String strTime = "" + nMin + ":";

    if ( nSecs < 10 )
    {
      strTime += "0";
    }

    strTime += nSecs;

    return strTime;
  }

  public int getSampleRateAsNumber()
  {

    if ( m_audioHdr != null )
    {
      return m_audioHdr.getSampleRateAsNumber();
    }

    return 0;

  }

  public String getSampleRate()
  {

    if ( m_audioHdr != null )
    {
      return  m_audioHdr.getSampleRate();
    }

    return "N/A";

  }

  public String getChannels()
  {
    if ( m_audioHdr != null )
    {
      return m_audioHdr.getChannels();
    }

    return "N/A";
  }



  public boolean isVariableBitRate()
  {

    if ( m_audioHdr != null )
      return m_audioHdr.isVariableBitRate();

    return false;


  }



  public String getMpegVersion()
  {
    if ( m_mp4AudioHdr != null )
    {
      return "N/A";
    }

    if ( m_mp3AudioHdr == null  )
    {
      return "N/A";
    }

    return m_mp3AudioHdr.getMpegVersion();

  }

  public String getMpegLayer()
  {
    if ( m_mp4AudioHdr != null )
    {
      return "N/A";
    }


    if ( m_mp3AudioHdr == null  )
    {
      return "N/A";
    }

    return m_mp3AudioHdr.getMpegLayer();

  }


  public boolean isCopyrighted()
  {
    if ( m_mp4Tag != null )
    {
      return m_mp4Tag.getFirst( Mp4FieldKey.COPYRIGHT ) != null;
    }

    if ( m_mp3AudioHdr == null  )
    {
      return false;
    }

    return m_mp3AudioHdr.isCopyrighted();
  }


  public String getBitRate()
  {
    if ( m_audioHdr != null )
    {
      return m_audioHdr.getBitRate();
    }

    return "N/A";

  }

  public long getBitRateAsNumber()
  {
    if ( m_audioHdr != null )
    {
      return m_audioHdr.getBitRateAsNumber();
    }

    return 0;

  }

  public String getEncodingType()
  {

    if ( m_audioHdr != null )
    {
      return m_audioHdr.getEncodingType();
    }

    return "N/A";
  }



  public String getArtist()
  {
    if ( m_audioTag != null )
    {
      return m_audioTag.getFirst( FieldKey.ARTIST );
    }

    return "N/A";
  }


  public void setArtist( String strArtist ) throws Exception
  {
     m_audioTag.setField(FieldKey.ARTIST, strArtist );
  }


  public String getAlbum()
  {
    if ( m_audioTag != null )
    {
      return m_audioTag.getFirst( FieldKey.ALBUM );
    }

    return "N/A";
  }


  public void  setAlbum( String strAlbum ) throws Exception
  {
    m_audioTag.setField(FieldKey.ALBUM, strAlbum);

  }


  public String getGenre()
   {
     if ( m_audioTag != null )
     {
       return m_audioTag.getFirst( FieldKey.GENRE );
     }

     return "N/A";
   }


   public void  setGenre( String strAlbum ) throws Exception
   {
     m_audioTag.setField(FieldKey.GENRE, strAlbum);

   }

  public String getTitle()
  {
    if ( m_audioTag != null )
    {
      return m_audioTag.getFirst( FieldKey.TITLE );
    }

    return "N/A";
  }

  public void  setTitle( String strTitle ) throws Exception
  {
     m_audioTag.setField(FieldKey.TITLE, strTitle);

  }

  public String getTrackNbr()
  {
    if ( m_audioTag != null )
    {
      return m_audioTag.getFirst( FieldKey.TRACK );
    }

    return "N/A";
  }


  public void setTrackNbr( int nTrackNbr )  throws Exception
  {
    m_audioTag.setField( FieldKey.TRACK, String.valueOf( nTrackNbr ) );

  }


  public String getComposers()
  {
    if ( m_audioTag != null )
    {
      return m_audioTag.getFirst( FieldKey.COMPOSER );
    }

    return "N/A";
  }

  public void  setComposers( String strComposers ) throws Exception
  {
     m_audioTag.setField(FieldKey.COMPOSER, strComposers);

  }


  public String getCopyright()
  {
    if ( m_mp4Tag != null )
    {
      return m_mp4Tag.getFirst( Mp4FieldKey.COPYRIGHT );
    }

    return "N/A";

  }


  /**
   * Gets the first artwork if it exists
   *
   * @return The first Artwork object if there is artwork, else null is returned
   */
  public Artwork getFirstArtwork()
  {
    if ( m_audioTag != null )
    {
      return m_audioTag.getFirstArtwork();
    }

    return null;

  }


  /**
   * Gets a List of Artwork objects
   *
   * @return The list of Artwork objects or null if no artwork exists
   */
  public List<Artwork> getArtworkList()
  {
    if ( m_audioTag != null )
    {
      return m_audioTag.getArtworkList();
    }

    return null;
  }


  /**
   * Returns true if audio fule hs artwork, false otherwise
   * @return
   */
  public boolean hasArtwork()
  {

    return getFirstArtwork() != null;

  }


  /**
   * Extracts the first art work and saves it to the imageFile
   *
   * @param imageFile The File object representing the path and name of the artwork image to be saved.
   *                  <br>If the file extension is omitted, the extension based on the mime image type will be used
   *
   * @throws Exception
   */
  public void extractFirstArtwork( File imageFile ) throws Exception
  {
    Artwork aw = getFirstArtwork();

    if ( aw == null )
    {
      throw new Exception( "No Artwork is available for file: " + m_fileAudio.getAbsolutePath() );
    }

    String strExt = VwExString.getFileExt( imageFile );

    if ( strExt == null )
    {
      strExt = getImageExtentionFromMimeType( aw );
      imageFile = new File( imageFile.getAbsolutePath() + "." + strExt );
    }


    VwFileUtil.writeFile( imageFile, aw.getBinaryData() );

  }


  /**
   * Extracts all artwork form the file. If there is more than one image file, the number is appended to the base image
   * name as _n wher n is  sequential nbr starting at one. The first file keeps just  theb base image name
   *
   * @param strBaseImageName The base image name for each image extratced
   * @param strExtractDir The directort path where image files will be written to
   *
   * @return a list of File objects for each file created
   * @throws Exception
   */
  public List<File> extractAllArtwork( String strBaseImageName, String strExtractDir ) throws Exception
  {
    List<Artwork> listArtwork = getArtworkList();

    List<File>listExtratedFiles = new ArrayList<File>(  );

    int nPos = strBaseImageName.lastIndexOf( '.' );

    // Lop off file extension and will append ext by its image type

    if ( nPos > 0 )
    {
      strBaseImageName = strBaseImageName.substring( 0, nPos );
    }

    // Strip off any periods in the name if they exist
    strBaseImageName = VwExString.strip( strBaseImageName, "." );

    for ( int x = 0; x < listArtwork.size(); x++ )
    {

      String strImageName = strBaseImageName;

      if ( x > 0 )
      {
        strImageName += "_" + x;
      }

      Artwork aw = listArtwork.get( x );

      String strImageExt = getImageExtentionFromMimeType( aw );

      strImageName += "." + strImageExt;

      if ( !strExtractDir.endsWith( "/" ))
      {
        strExtractDir += "/";
      }

      // We have to strip out parens as they cant be used in backround urls
      if ( VwExString.findAny( strImageName, "()", 0 ) > 0 )
      {
        strImageName = VwExString.strip( strImageName,  "()");
      }

      // Images can't have spaces in them for html <img tags>
      strImageName = VwExString.replace( strImageName, " ", "_" );

      File fileImageExtract = new File( strExtractDir + strImageName );


      VwFileUtil.writeFile( fileImageExtract, aw.getBinaryData() );

      listExtratedFiles.add( fileImageExtract );

    } // end for()

    return listExtratedFiles;


  } // end extractAllArtwork{}



  /**
   * Extract the image extenion from the image mime tyoe
   * @param aw The artwork object
   * @return
   */
  public String getImageExtentionFromMimeType( Artwork aw )
  {
    String strMime = aw.getMimeType().substring(  aw.getMimeType().indexOf( "/" ) + 1 );

    if ( strMime.equals( "jpeg" ))
    {
      strMime = "jpg";
    }

    return strMime;

  }

  /**
   * Sets the initial artwork, any other artwork images are removed
   *
   * @param fileArtworkImage The image file for the artwork
   * @throws Exception
   */
  public void setFirstArtwork( File fileArtworkImage ) throws Exception
  {

    if ( !fileArtworkImage.canRead() )
    {
      throw new Exception( "Artwork File: " + fileArtworkImage.getAbsolutePath() + " is not readable");
    }

    m_audioTag.deleteArtworkField();

    Artwork aw = new Artwork();
    aw.setFromFile( fileArtworkImage );

    m_audioTag.setField( aw );

  }


  /**
   * Adds an artwork image or creates the inital one if no artwork exists
   *
   * @param fileArtworkImage The image file to add
   * @throws Exception
   */
  public void addArtwork( File fileArtworkImage ) throws Exception
  {

    if ( !fileArtworkImage.canRead() )
    {
      throw new Exception( "Artwork File: " + fileArtworkImage.getAbsolutePath() + " is not readable");
    }

    Artwork aw = new Artwork();
    aw.setFromFile( fileArtworkImage );

    m_audioTag.setField( aw );

  }


  /**
   * Removes all artwork from this audio file
   */
  public void removeArtwork()
  {
    m_audioTag.deleteArtworkField();

  }


  /**
   * Save any updates
   * @throws Exception
   */
  public void save() throws Exception
  {

    m_af.setTag( m_audioTag );
    m_af.commit();

  }



} // end VwJAudioTaggerExtender{}
