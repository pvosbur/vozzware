/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwMailBox.java

============================================================================================
*/

package com.vozzware.mail;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Peter VosBurgh
 *
 * This class is an abstraction for the java mail folder object. It adds many higher level helper
 * methods for managing mail messages.
 */
public class VwMailBox
{
  private Folder	m_folder;		// An Opened Mail folder
  
  /**
   * package scope constructor to be used by the VwMailReader
   * 
   * @param folder The Folder object from javax.mail
   */
  VwMailBox( Folder folder )
  { m_folder = folder; }
  
  /**
   * Gets the number of mail messages in this folder
   * 
   * @return The number of mail messages in this folder
   * @throws Exception if there were connection errors
   */
  public int getMessageCount() throws Exception
  { return m_folder.getMessageCount(); }
  
  
  /**
   * Gets a List of VwMailMessage objects. The list may be an empty list if no mail messages exist
   * @return
   */
  public List getAllMessages() throws Exception
  {
    List listMessages = new LinkedList();
    
    Message[] aMsgs = m_folder.getMessages();
    
    for ( int x = 0; x < aMsgs.length; x++ )
      listMessages.add( new VwMailMessage( aMsgs[ x ] ) );
    
    return listMessages;
    
  }// end getAllMessages()
  
  
  /**
   * Removes all mail messages from the box NOTE! The VwMailBox must be closed for this to take effect.
   * @throws Exception
   */
  public void removeAll() throws Exception
  {
    Message[] aMsgs = m_folder.getMessages();
    
    for ( int x = 0; x < aMsgs.length; x++ )
      aMsgs[ x ].setFlag( Flags.Flag.DELETED, true );
    
  } // end removeAll()
  
  /**
   * Closes the mailbox and expunges any messages marked for deletion
   * @throws Exception
   */
  public void close() throws Exception 
  { m_folder.close( true ); }
  
} // end class VwMailBox{}

// *** End VwMailBox.java ***



