/*
  ===========================================================================================

                                 V o z z W o r k s


                                  Copyright(c) 2030 by

                                  V o z z w a re

                                 All Rights Reserved


  Source Name:  ItcServerInfo.java


  ============================================================================================
*/

package com.vozzware.serverUtils;


import java.net.InetAddress;

public class VwServerInfo
{
  private String      m_strPlatform;        // will be the server running platorm i.e. User define, typiciall local, dev, qa, prod etc...
  private String      m_strDnsName;         // Dns Name of Opera server on the network e
  private String      m_strIpAddress;       // Ip address of the server
  private int         m_nPortNbr;           // Port nbr its listening on
  private InetAddress m_ia;                 // InetAddress for this server

  /**
   * Constructor
   */
  public VwServerInfo( String strPlatform, String strDnsName, int nPort ) throws Exception
  {
    m_strDnsName = strDnsName;
    m_nPortNbr = nPort;
    m_strPlatform = strPlatform;

    m_strIpAddress = VwServerUtils.getNicAddress();

    m_ia = InetAddress.getByName( m_strIpAddress );

    if ( strDnsName == null )
    {
      m_strDnsName = m_ia.getHostName();
    }
  } // end ItcServerInfo(


  /**
   * Gets the dns name of the server machine
   */
  public String getDnsName()
  { return m_strDnsName; }

  public InetAddress getInetAddress()
  {
    return m_ia;

  }


  /**
   * Gets the user assigned name of the server machine
   */
  public String getStrPlatform()
  { return m_strPlatform; }


  /**
   * Gets the Ip address of the server machine
   */
  public String getIpAddress()
  { return m_strIpAddress; }



  /**
   * Gets the port nbr of the primary listener
   */
  public int getPortNbr()
  { return m_nPortNbr; }


  /**
   * Returns true if the dns name and port nbr are the same
   */
  public boolean equals( VwServerInfo si )
  {
    if ( m_strDnsName.equalsIgnoreCase( si.getDnsName() ) && m_nPortNbr == si.getPortNbr() )
    {
      return true;
    }

    return false;

  } // end equals

  /**
   * Provide string representation for this object
   */
  public String toString()
  {
    String strServerInfo = m_strPlatform + "," + m_strIpAddress + "," + m_nPortNbr + "," + m_strDnsName;

    return strServerInfo;
  }

} // end class ItcServerInfo

// *** End of ItcServerInfo.java ***

