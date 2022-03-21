/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwMailMessage.java

============================================================================================
*/

package com.vozzware.mail;

import com.vozzware.util.VwDate;

import javax.mail.Address;
import javax.mail.Flags;
import javax.mail.Flags.Flag;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.NewsAddress;
import java.util.Date;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

/**
 * 
 * This class abstracts the java mail Message object. It adds higher level api's to manage attachements
 * @author Peter VosBurgh
 *
 */
public class VwMailMessage
{
  private Message	m_msg;		// The java mail message
  
  /**
   * Constructor for use by the VwMailReader
   * 
   * @param msg The java mail message object
   */
  VwMailMessage( Message msg )
  { m_msg = msg; }
  
  
  /**
   * Get the java mail Message object
   * @return
   */
  public Message getMessage()
  { return m_msg; }
  
  /**
   * Returns true if this mail message has attachments
   * @return
   * @throws Exception if a connection error occurs
   */
  public boolean hasAttachments() throws Exception
  { return (m_msg.getContent() instanceof Multipart); }
  
  
  /**
   * Returns a List of VwMailAttachment objects. The list may be an empty list if no attachments exist.
   * @return
   * @throws Exception
   */
  public List getAttachments() throws Exception
  {
    List listAttachments = new LinkedList();
   
    if ( m_msg.getContent() instanceof Multipart )
    {
      Multipart mpart = (Multipart)m_msg.getContent();
      
      for ( int x = 0; x < mpart.getCount(); x++ )
      {
        
        if ( (mpart.getBodyPart( x ).getContent() instanceof String) )
          continue;
        
        listAttachments.add( new VwMailAttachment( (MimeBodyPart)mpart.getBodyPart( x ) ) );
        
      }// end for()
      
    }// end if
       
    return listAttachments;
    
  }// end getAttachments()
  
  
  /**
   * Get the number of attachemnts this mail message has
   * @return
   * @throws Exception
   */
  public int getAttachmentCount() throws Exception
  {
    int nCount = 0;
    
    if ( m_msg.getContent() instanceof Multipart )
    {
      Multipart mpart = (Multipart)m_msg.getContent();
      
      for ( int x = 0; x < mpart.getCount(); x++ )
      {
        if ( ! (mpart.getBodyPart( x ).getContent() instanceof String) )
          ++nCount;
      }
    }
    
    return nCount;
    
  } // end getAttachmentCount()
  
  /**
   * Gets the message subject
   * @return
   * @throws Exception
   */
  public String getSubject() throws Exception
  { return m_msg.getSubject(); }
  
  /**
   * Gets the mail text
   * @return
   * @throws Exception
   */
  public String getMailText() throws Exception
  {
    Object objContent = m_msg.getContent();
    
    if ( objContent instanceof String )
      return (String)objContent;
    
    for ( int x = 0; x < ((Multipart)objContent).getCount(); x++ )
    {
      Object objPartContent = ((Multipart)objContent).getBodyPart( x ).getContent();
      
      if ( objPartContent instanceof String )
        return (String)objPartContent;
      
    } // end for()
    
    return "";		// No message body text found
    
  }// end getMailText
  
  /**
   * gets the mail address of the sender
   * @return
   * @throws Exception
   */
  public String getSender() throws Exception
  {
    Address[] addr = m_msg.getFrom();
    return getAddress( addr[ 0 ] );
    
  } // end getSender()
  
  public String getSentDate() throws Exception
  {
    Date date = m_msg.getSentDate();
    VwDate dtSent = new VwDate( date );
    return dtSent.format( VwDate.IETFDATE );
  }
  
  public String getReceivedtDate() throws Exception
  {
    Date date = m_msg.getReceivedDate();
    VwDate dtSent = new VwDate( date );
    return dtSent.format( VwDate.IETFDATE );
  }
  
  /**
   * gets an enumeration of Header objects from this mesage
   * @return
   * @throws Exception
   */
  public Enumeration getHeaders() throws Exception
  { return m_msg.getAllHeaders(); }
  
  
  /**
   * Get value(s) for a header type
   * @param strHeaderName
   * @return
   * @throws Exception
   */
  public String[] getHeaderValue( String strHeaderName ) throws Exception
  { return m_msg.getHeader( strHeaderName ); }
  
  /**
   * Returns a comma delimited string of all the recipients of this message
   * @return
   * @throws Exception
   */
  public String getAllRecipients() throws Exception
  {
    Address[] addrRecips = m_msg.getAllRecipients();
    
    StringBuffer sbRecips = new StringBuffer();
    
    for ( int x = 0; x < addrRecips.length; x++ )
    {
      if ( x > 0 )
        sbRecips.append( ',' );
      
      sbRecips.append( getAddress( addrRecips[ x ] ) );
       
    } // end for
    
    return sbRecips.toString();
    
  } // end getAllRecipients()
  
  public void remove() throws Exception
  { m_msg.setFlag( Flags.Flag.DELETED, true ); }
  
  
  /**
   * Gets the address component based on its concrete address type
   * @param addr The super class for all adress types
   * @return
   */
  private String getAddress( Address addr )
  {
    if ( addr instanceof InternetAddress )
      return ((InternetAddress)addr).getAddress();
    else
    if ( addr instanceof NewsAddress )
      return ((NewsAddress)addr).getNewsgroup();
    else
      return addr.toString();
    
  } // end getAddress()
  
  /**
   * Gets the message flags.
   * @return
   * @throws Exception
   */
  public Flags getFlags() throws Exception
  { return m_msg.getFlags(); }
  
  /**
   * This will return true if the mail message has been read. NOTE! this will not work for pop3 prototcols. It should
   * <br>work with imap prototcol
   * @return
   * @throws Exception
   */
  public boolean hasBeenRead() throws Exception 
  { 
    
    Flags flags = m_msg.getFlags();
    
    Flag[] aFlags = flags.getSystemFlags();
    
    for ( int y = 0; y < aFlags.length; y++ )
    {
      Flag f = aFlags[ y ];
      
      if (f == Flags.Flag.SEEN)
        return true;
    }
    
    return false;
  } // end getFlags()
  
} // end class VwMailMessage{}

// *** End VwMailMessage.java ***

