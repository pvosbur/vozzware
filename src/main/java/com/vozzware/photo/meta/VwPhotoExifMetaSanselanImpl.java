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

package com.vozzware.photo.meta;

import org.apache.sanselan.Sanselan;
import org.apache.sanselan.common.IImageMetadata;
import org.apache.sanselan.formats.jpeg.JpegImageMetadata;
import org.apache.sanselan.formats.tiff.TiffField;
import org.apache.sanselan.formats.tiff.TiffImageMetadata;
import org.apache.sanselan.formats.tiff.constants.TagInfo;
import org.apache.sanselan.formats.tiff.constants.TiffConstants;
import org.imgscalr.AsyncScalr;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;

/*
============================================================================================

    Source File Name: Transaction.java

    Author:           petervosburgh
    
    Date Generated:   3/5/13

    Time Generated:   5:46 AM

============================================================================================
*/

/**
 * This is photo meta impl class for the  org.apache.sanselan.Sanselan implemetaion
 *
 */
public class VwPhotoExifMetaSanselanImpl
{

  private InputStream m_filePhotoInputStream;

  private IImageMetadata m_imageMetaData;

  private JpegImageMetadata m_jpegMetadata;


  /**
   * Constrctor
   *
   * @param strFileName The phot jpeg file to process
   * @throws Exception
   */
  public VwPhotoExifMetaSanselanImpl( InputStream fileInputStream, String strFileName ) throws Exception
  {

    m_filePhotoInputStream = fileInputStream;

    m_imageMetaData = Sanselan.getMetadata( fileInputStream, strFileName );

    if (m_imageMetaData instanceof JpegImageMetadata )
   	{
   		m_jpegMetadata = (JpegImageMetadata)m_imageMetaData;

    }

  } // end VwPhotoExifMetaSanselanImpl


  /**
   * Rreturn true if the file to process has jpeg meta data to process
   *
   * @return true for valid meta data, false other wize
   */
  public boolean canProcessMetaData()
  {
    return m_jpegMetadata != null;

  }


  /**
   * Get the value for the meta tag
   *
   * @param tagInfo The TagInfo to retrieve
   *
   * @return  The tag value if available, null otherwise
   */
  public String getMetaTagValue( TagInfo tagInfo  )
  {
    if ( m_jpegMetadata == null )
    {
      return null;
    }

    TiffField field = m_jpegMetadata.findEXIFValue( tagInfo );
  	if (field == null )
  		return null;

		String strVal = field.getValueDescription();

    // Some meta values have binary zeroes in the the string '\u0000' that need to be stripped out, this looks like a bug in the software

    int nPos = strVal.indexOf( '\u0000' );

    if ( nPos >= 0 )
    {
      strVal = strVal.substring( 0, nPos );
    }

    return strVal;


  }


  /**
   * get the standard meta tags of interest
   * @return
   */
  public VwStdPhotoMetaData getStandardMeta() throws Exception
  {
    if ( m_jpegMetadata == null )
    {
      return null;
    }

    VwStdPhotoMetaData photoMeta = new VwStdPhotoMetaData();

    photoMeta.setOriginalDate( getMetaTagValue( TiffConstants.EXIF_TAG_DATE_TIME_ORIGINAL ) );
    photoMeta.setCreateDate( getMetaTagValue( TiffConstants.EXIF_TAG_CREATE_DATE ) );
    photoMeta.setXResolution( getMetaTagValue( TiffConstants.TIFF_TAG_XRESOLUTION ) );
    photoMeta.setMeteringMode( getMetaTagValue( TiffConstants.EXIF_TAG_METERING_MODE ) );
    photoMeta.setMake( getMetaTagValue( TiffConstants.TIFF_TAG_MAKE ) );
    photoMeta.setModel( getMetaTagValue( TiffConstants.TIFF_TAG_MODEL ) );
    photoMeta.setImageWidth( getMetaTagValue( TiffConstants.EXIF_TAG_EXIF_IMAGE_WIDTH ) );
    photoMeta.setImageLength( getMetaTagValue( TiffConstants.EXIF_TAG_EXIF_IMAGE_LENGTH ) );
    photoMeta.setType( getMetaTagValue( TiffConstants.EXIF_TAG_IMAGE_TYPE ) );
    photoMeta.setIso( getMetaTagValue( TiffConstants.EXIF_TAG_ISO ) );
    photoMeta.setFocalLength( getMetaTagValue( TiffConstants.EXIF_TAG_FOCAL_LENGTH ) );
    photoMeta.setExposureTime( getMetaTagValue( TiffConstants.EXIF_TAG_EXPOSURE_TIME ) );
    photoMeta.setAperture( getMetaTagValue( TiffConstants.EXIF_TAG_APERTURE_VALUE ) );
    photoMeta.setShutterSpeed( getMetaTagValue( TiffConstants.EXIF_TAG_SHUTTER_SPEED_VALUE ) );
    photoMeta.setWhiteBalance( getMetaTagValue( TiffConstants.EXIF_TAG_WHITE_BALANCE_1 ) );

    TiffImageMetadata tmd = m_jpegMetadata.getExif();
    if ( tmd != null )
    {
      TiffImageMetadata.GPSInfo gpsInfo = tmd.getGPS();
  		if (null != gpsInfo)
  		{
  			double longitude = gpsInfo.getLongitudeAsDegreesEast();
  			double latitude = gpsInfo.getLatitudeAsDegreesNorth();
        photoMeta.setGpsLatitude( String.valueOf( latitude ) );
        photoMeta.setGpsLongitude( String.valueOf( longitude ));

      }

    }

    return photoMeta;

  }


  /**
   * Gets a thumbnail image from the this image
   *
   * @return
   * @throws Exception
   */
  public BufferedImage getThumbnailImage( int nWidthScale ) throws Exception
  {
    BufferedImage img = ImageIO.read( m_filePhotoInputStream ); // load image
    BufferedImage scaledImg = AsyncScalr.resize( img, nWidthScale ).get();

    return scaledImg;


  }


  /**
   * Writes the thumbnail image to the file specified
   *
   * @param fileThumbnail The file path and name of the thumbnail image
   * @throws Exception
   */
  public void writeThumbnailImage( File fileThumbnail, int nWidthScale  ) throws Exception
  {

    if ( fileThumbnail.isDirectory() )
      throw new Exception( "Thumbnail file: " + fileThumbnail.getAbsolutePath() + " is not a writable file");

    BufferedImage bi = getThumbnailImage( nWidthScale );

    ImageIO.write( bi, "jpg", fileThumbnail );

  }
} // end VwPhotoExifMetaSanselanImpl{}
