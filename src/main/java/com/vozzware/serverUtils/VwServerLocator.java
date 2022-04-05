/*
  ===========================================================================================

                                 I t c   S e r v e r s


                                  Copyright(c) 2000 by

                   I n t e r n e t  T e c h n o l o g i e s   C o m p a n y

                                 All Rights Reserved


  Source Name:  VwServerLocator.java


  ============================================================================================
*/


package com.vozzware.serverUtils;


import com.vozzware.util.VwDelimString;
import com.vozzware.util.VwExString;
import com.vozzware.util.VwLogger;
import com.vozzware.util.VwResourceMgr;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.LinkedList;
import java.util.List;

public class VwServerLocator extends Thread
{
  private List m_listServers = new LinkedList();

  private int  m_nMcastPort = VwResourceMgr.getInt( "server.multicast.port" ); // Port the datagrams are sent to
  private int  m_nMcastResponsePort = VwResourceMgr.getInt( "server.multicast.response.port" ); // Port the datagrams are sent to

  private VwLogger m_logger;

  public VwServerLocator( VwLogger logger )
  {
    m_logger = logger;

  }
  /*
   * Override of the Thrads run method for implementation
   */
  public void run()
  {
    MulticastSocket ms = null;
    InetAddress ia = null;
    DatagramSocket ds = null;

    try
    {
      String strMcastAddress = VwResourceMgr.getString( "server.multicast.address");

      ia = InetAddress.getByName( strMcastAddress );
      String strReq = "IDENTITY";

      ds = new DatagramSocket();

      DatagramPacket dp = new DatagramPacket( strReq.getBytes(), strReq.length(), ia, m_nMcastPort );

      ds.send( dp );
      ds.close();

      // *** Create standard DatagramSocket for replys

      ds = new DatagramSocket( m_nMcastResponsePort );

      // *** Set a 2 second timeout
      ds.setSoTimeout( 120000 );

      // *** Stay in receive loop until we time out
      for ( ;; )
      {
        DatagramPacket dp1 = new DatagramPacket( new byte[200], 200 );
        ds.receive( dp1 );

        String strServer = new String( dp1.getData(), 0, dp1.getLength() );

        m_logger.debug( this.getClass(), "GOt Respone for IDENTITY Requesst, Server: " + strServer );

        String[] astrInfoPieces = strServer.split( ",");

        m_listServers.add( new VwServerInfo( astrInfoPieces[0], astrInfoPieces[3], Integer.valueOf( astrInfoPieces[2] ) ) );


      } // end for()

    } // end try
    catch ( Exception e )
    {
      return;  // we get here on a timeout when no more servers respond
    }

    finally
    {

      try
      {
        ms.leaveGroup( ia );
        ms.close();
        ds.close();
        ms = null;
        ds = null;

      }
      catch( Exception e ){}
    }

  } // end run

  /**
   * Returns a List of ServerInfo objects for any servers responding to the multicast
   *
   * @return a List of ServerInfo objects for any servers responding to the multicast
   */
  public List getServerList( )
  { return m_listServers; }


} // end class VwServerLocator{}

// *** End of VwServerLocator.java ***

