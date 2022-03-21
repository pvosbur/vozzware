/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwMailReader.java

============================================================================================
*/

package com.vozzware.mail;

import javax.mail.Folder;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.URLName;
import java.util.Properties;

/**
 * @author P. VosBurgh
 * 
 * Thic class reads mailboxes based on the URLName object settings. It returns an VwMailBox object
 * <br>which can then get all of the mail items and attachments for thos mail items.
 *
 */
public class VwMailReader
{
  private static VwMailReader m_instance = null;
  
  private Session m_session;
  
  
  /**
   * private constructor to enforce singleton
   *
   */
  private VwMailReader()
  { 
    Properties props = System.getProperties();
    
    //     Get a Session object
    m_session = Session.getDefaultInstance( props, null);
    
  } // end VwMailReader()
  
  /**
   * Gets the singleton VwMailReader instance
   * @return
   */
  public static synchronized VwMailReader getInstance()
  {
    if ( m_instance == null )
      m_instance = new VwMailReader();
    
    return m_instance;
    
  } // end getIntance()
  

  /**
   * Gets the default mailbox for the mail url specified
   * 
   * @param urlName The mail url used to connect to the mail server
   * @return The default mailbox 
   * @throws Exception if the mail url is invalid or the mail server could not be connected to
   */
  public VwMailBox getMailBox( URLName urlName ) throws Exception
  { return getMailBox( null, urlName, false ); }

  
  /**
   * Gets the default mailbox for the mail url specified, with debugging options
   * 
   * @param strMailUrl The mail url used to connect to the mail server
   * @param fDebug if true a debug trance of the connection process is sent to std out
   * @return The default mailbox 
   * @throws Exception if the mail url is invalid or the mail server could not be connected to
   */
  public VwMailBox getMailBox( URLName urlName, boolean fDebug ) throws Exception
  { return getMailBox( null, urlName, fDebug ); }
  
  
  /**
   * Attempts to retrieve the VwMailBox from the UrlName format string
   *
   * @param strMailBoxName The the name of the mailbox to retrieve or null for the default 
   * @param urln The mail url name to retrieve the maolbox for
   * @param fDebug if true a debug trance of the connection process is sent to std out
   * 
   * @return A VwMailBox for working with maill messages if successful
   * @throws Exception if the VwMailBox could not be connected to
   */
  public VwMailBox getMailBox( String strMailBoxName, URLName urlName, boolean fDebug ) throws Exception
  {
    m_session.setDebug( fDebug );
    
    Store store = null;
    store = m_session.getStore( urlName );
    store.connect();
    
    Folder folder = null;
    
    if ( strMailBoxName == null )
      folder = store.getFolder( "INBOX" );
    
    else
      folder = store.getFolder( strMailBoxName );

    folder.open( Folder.READ_WRITE );
    
    return new VwMailBox( folder );
    
  } // end getMailBox()
  
  /**
   * Gets a list of all available mailboxes for this mailurl
   * 
   * @param urln The mail URLName to the the list of available mailboxes 
   * @return an array of VwMailBox objects or a zero length array if no mailboxes available
   * @throws Exception
   */
  public VwMailBox[] listAllMailBoxes( URLName urln ) throws Exception
  {
    Store store = null;
    store = m_session.getStore( urln );
    store.connect();
    
    Folder folder = store.getDefaultFolder();
    
    Folder[] aFolders = folder.list();
    
    VwMailBox[] aBoxes = new VwMailBox[ aFolders.length ];
    
    for ( int x = 0; x < aFolders.length; x++ )
      aBoxes[ x ] = new VwMailBox( aFolders[ x ] );
    
    return aBoxes;
   
  } // end listAllMailBoxes()
  
} // end class VwMailReader{}

// *** End VwMailReader.java ***

