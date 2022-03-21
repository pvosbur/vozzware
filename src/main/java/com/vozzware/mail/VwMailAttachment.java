package com.vozzware.mail;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.internet.MimeBodyPart;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;

/**
 * @author pvosburg
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class VwMailAttachment
{
  private BodyPart	m_mailPart;
  
  /**
   * Constructor
   * 
   * @param strFileName The filename used to represent the attchment in the mail item
   * 
   * @param urlAttachment The url to the attachment file itself
   * 
   * @throws Exception The the attachement file referenced by the url cannot be opened or read
   */
  public VwMailAttachment( String strFileName, URL urlAttachment ) throws Exception
  {
    m_mailPart = new MimeBodyPart();
    
    File fileAttachment = new File( urlAttachment.toURI() );
    
    FileDataSource fileDataSource = new FileDataSource( fileAttachment ){
      @Override
      public String getContentType() {
          return "application/octet-stream";
      }
  };
  
  m_mailPart.setDataHandler(new DataHandler(fileDataSource));
    
  m_mailPart.setFileName( strFileName );
    
    
  }
  /**
   * Constructor for use by VwMailMessage
   * 
   * @param mailPart The java mail BodyPart containg the attachment 
   */
  VwMailAttachment( BodyPart mailPart )
  { m_mailPart = mailPart; }
  
  /**
   * Gets the original file name of the attachement
   * @return
   * @throws Exception
   */
  public String getFileName() throws Exception
  { return m_mailPart.getFileName(); }
  
  /**
   * Returns the MIME type of the attachment
   * @return
   * @throws Exception
   */
  public String getType() throws Exception
  { return m_mailPart.getContentType(); }
  
  /**
   * Gets the size of the attachment
   * @return
   * @throws Exception
   */
  public int getSize() throws Exception
  { return m_mailPart.getSize(); }
  
  /**
   * Return the BodyPart 
   * @return
   */
  public BodyPart getBodyPart()
  { return m_mailPart; }
  
  /**
   * Saves the attachement to a local file system
   * @param fileLocation The file directory
   * @param strFileName The name to save it under. If null then the original name as specified by the sender will be used
   */
  public void save( File fileLocation, String strFileName ) throws Exception
  {
    if ( !fileLocation.isDirectory() )
      throw new Exception( "The File location object must be a valid directory" );
    
    if ( !fileLocation.exists() )
      throw new Exception( "The File location directory: " + fileLocation.getAbsolutePath() + ", does not exist." );
    
    String strSaveName = null;
    
    if ( strFileName != null )
      strSaveName = strFileName;
    else
      strSaveName = m_mailPart.getFileName();
    
    String strFinalPath = fileLocation.getAbsolutePath();
    if ( !strFinalPath.endsWith( File.separator ) )
      strFinalPath += File.separator;
    
    strFinalPath += strSaveName;
    
    final int SIZE;

    int nSize = m_mailPart.getSize();
    
    if ( nSize > 65535 )
      SIZE = 65535;
    else
    if ( nSize > 32768 )

      SIZE = 32768;
    else
      SIZE = 16384;
    
    byte[] abData = new byte[ SIZE ];
    
    int nGot = 0;
    
    InputStream inps = m_mailPart.getInputStream();
    
    FileOutputStream fos = new FileOutputStream( strFinalPath );
    
    while( true )
    {
      nGot = inps.read( abData );
      
      if ( nGot <= 0 )
        break;
      
      fos.write( abData, 0, nGot );
      
      if ( nGot < SIZE )
        break;
      
    } // end while()
    
    fos.close();
    
    inps.close();
    
  }// end save()
  
} // end class VwMailAttachment{}

// *** End VwMailAttachment.java ***

