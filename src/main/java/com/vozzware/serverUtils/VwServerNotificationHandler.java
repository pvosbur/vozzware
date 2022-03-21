package com.vozzware.serverUtils;

/*
============================================================================================

    Source File Name: Transaction.java

    Author:           petervosburgh
    
    Date Generated:   11/18/20

    Time Generated:   7:54 AM

============================================================================================
*/
public interface VwServerNotificationHandler
{

  /**
   * Callback notification when a server comes online or offline
   *
   * @param bOnline if trie server is coming online else its going offline
   * @param serverInfo The server info object for the server activity
   */
  void serverChange( boolean bOnline, VwServerInfo serverInfo );

} // end VwServerNotificationHandler{}
