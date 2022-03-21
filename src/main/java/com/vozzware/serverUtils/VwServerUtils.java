package com.vozzware.serverUtils;

import com.vozzware.util.VwResourceMgr;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

/*
============================================================================================

    Source File Name: Transaction.java

    Author:           petervosburgh
    
    Date Generated:   11/11/20

    Time Generated:   8:10 AM

============================================================================================
*/
public class VwServerUtils
{
  private static String s_strServerIpAddress;

  /**
   * Gets the NIC Cards Ip4 address
   *
   * @return String with the ipv4 address on the nic Car
   * @throws Exception
   */
  public static String getNicAddress() throws Exception
  {

    Enumeration e = NetworkInterface.getNetworkInterfaces();

    String strServerNicCardName = VwResourceMgr.getString( "server.nicCardName" );

    if ( strServerNicCardName.equals( "server.nicCardName" ) )
    {
      throw new Exception( "server.nicCardName must be defined in the aiEnv Properties file" );

    }


    while ( e.hasMoreElements() )
    {
      NetworkInterface n = (NetworkInterface) e.nextElement();
      Enumeration ee = n.getInetAddresses();

      if ( n.getName().equals( strServerNicCardName ) )
      {
        while ( ee.hasMoreElements() )
        {
          InetAddress i = (InetAddress) ee.nextElement();
          String strHostAddress = i.getHostAddress();

          // We're looking for the IpV4 address
          if ( strHostAddress.indexOf( "." ) > 0 )
          {
            s_strServerIpAddress = strHostAddress;
            return strHostAddress;
          }
        }
      }
    }

    return null;

  }

  /**
   * Returns the servers ip address of code running on thos server
   * @return
   * @throws Exception
   */
  public static String getServerIpAddr() throws Exception
  {
    if ( s_strServerIpAddress == null )
    {
      getNicAddress();

    }

    return s_strServerIpAddress;
  }

}
