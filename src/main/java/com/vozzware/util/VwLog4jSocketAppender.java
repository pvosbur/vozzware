/*
===========================================================================================

 
                               Copyright(c) 2000 - 2006 by

                                V o z z W a r e   L L C (Vw)

                                 All Rights Reserved

Source Name: VwTextComponentAppender.java

Create Date: Jul 23, 2006

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.


============================================================================================
*/
package com.vozzware.util;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.LoggingEvent;

import java.io.DataOutputStream;
import java.net.Socket;

/**
 * @author P. VosBurgh
 * This class is a Socket appender for Log4j. This differs from the built in log4j appender in that this transmits
 * the formatted logging text according to the PatternLayout specified where as the log4j appender send a serialization of
 * the LoggingEvent object.
 *
 */
public class VwLog4jSocketAppender extends AppenderSkeleton
{

  private Socket m_sock;
  private DataOutputStream m_outs;
  private PatternLayout   m_pl;
  private String m_strHost;
  private int m_nPort;
  
  /**
   * 
   * @param pl
   * @param strHost
   * @param nPort
   * @throws Exception
   */
  public VwLog4jSocketAppender( PatternLayout pl, String strHost, int nPort  ) throws Exception
  {
    super();
    m_pl = pl;
    init( strHost, nPort );
   }

  /**
   * Default constructor for property driven initialization
   */
  public VwLog4jSocketAppender()
  {  m_pl = new PatternLayout( VwLogger.getDefaultPattern() );  }

  
  /**
   * Constructs appeender with the host and port 
   * @param strHost
   * @param nPort
   * @throws Exception
   */
  public VwLog4jSocketAppender( String strHost, int nPort ) throws Exception
  {
    super();
    m_pl = new PatternLayout( VwLogger.getDefaultPattern() );
    init( strHost, nPort );

  }
  
 
  /**
   * Setup soocke and output stream
   * @param strHost
   * @param nPort
   * @throws Exception
   */
  private void init( String strHost, int nPort ) throws Exception
  {
    m_sock = new Socket( strHost, nPort );
    m_outs = new DataOutputStream( m_sock.getOutputStream() );
   
  }

  /**
   * Sets the host name of the receiving socket
   * @param strHost
   */
  public void setHost( String strHost )
  { m_strHost = strHost; }
  
  
  /**
   * Set the port to send the log data to
   * @param nPort
   */
  public void setPort( int nPort )
  { m_nPort = nPort; }
  
  /**
   * Set the display pattern of the emssage
   * @param strPattern
   */
  public void setPattern( String strPattern )
  { m_pl = new PatternLayout( strPattern ); }
  
  /**
   * Write data to the stream
   */
  @Override
  protected void append( LoggingEvent lg )
  {
    try
    {
      if ( m_outs == null )
        init( m_strHost, m_nPort );
      
      String strMsg = m_pl.format( lg  );
      m_outs.writeBytes( strMsg );
    }
    catch( Exception ex )
    {
      close();
    }

  }

  /**
   * @see org.apache.log4j.AppenderSkeleton#close()
   */
  public void close()
  {
    try
    {
      m_outs.close();
      m_sock.close();
    }
    catch( Exception ex )
    {
      
    }
  }

  /**
   * @see org.apache.log4j.AppenderSkeleton#requiresLayout()
   */
  public boolean requiresLayout()
  {
     return false;
  }

}
