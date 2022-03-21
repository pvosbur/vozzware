/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwByteArrayDataSource.java

============================================================================================
*/

package com.vozzware.mail;

import javax.activation.DataSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

/**
 * This class implements a
 * DataSource from: an InputStream a byte array a String
 * 
 * @author John Mani
 * @author Bill Shannon
 * @author Max Spivak
 */
public class VwByteArrayDataSource implements DataSource
{
  private byte[] m_abData; // data holder

  private String m_strMimeType; // content-m_strMimeType

  /**
   * Create fromm an input stream
   * 
   * @param is
   * @param type
   */
  public VwByteArrayDataSource( InputStream is, String strType ) throws Exception 
  {
    m_strMimeType = strType;
    byte[] abTemp = new byte[ 65536 ];
    ByteArrayOutputStream os = new ByteArrayOutputStream();
   
    while( true )
    {
      int nGot = is.read( abTemp );
      
      if ( nGot < 0 )
        break;
      
      os.write( abTemp, 0, nGot );
      
    }
      
    m_abData = os.toByteArray();

  }

  /* Create a DataSource from a byte array */
  public VwByteArrayDataSource( byte[] data, String type )
  {
    m_abData = data;
    m_strMimeType = type;
  }

  public VwByteArrayDataSource( String data, String strMimeType )
  {
    try
    {
      // Assumption that the string contains only ASCII
      // characters! Otherwise just pass a charset into this
      // constructor and use it in getBytes()
      m_abData = data.getBytes( "iso-8859-1" );
    }
    catch ( UnsupportedEncodingException uex )
    {
    }
    m_strMimeType = strMimeType;
  }

  /**
   * Return an InputStream for the m_abData. Note - a new stream must be
   * returned each time.
   */
  public InputStream getInputStream() throws IOException
  {
    if ( m_abData == null )
      throw new IOException( "No data defined" );
    
    return new ByteArrayInputStream( m_abData );
  }

  public OutputStream getOutputStream() throws IOException
  {
    throw new IOException( "cannot do this" );
  }

  public String getContentType()
  {
    return m_strMimeType;
  }

  public String getName()
  {
    return "dummy";
  }
}// end class VwByteArrayDataSource{}

// *** End VwByteArrayDataSource.java ***