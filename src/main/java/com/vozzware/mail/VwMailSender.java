/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwMailSender.java

============================================================================================
*/

package com.vozzware.mail;

import com.vozzware.util.VwFileUtil;
import com.vozzware.util.VwResourceMgr;

import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * This class wraps the low level Java Mail api for sending mail messages and mail attachments
 * 
 * @author Peter VosBurgh
 */
public class VwMailSender
{
  
  private Session m_sessionMail;
  private String  m_strUserName = null;
  private String  m_strPassword = null;
  private String  m_strSmtpHost = null;
  private String  m_strEncoding = "UTF-8";
  private boolean m_fAuthenticated = false;

  
  /**
   * Constructor
   * 
   * @param strBundleName The name of any resource bundle that defines one or more of the following keys
   * <br>mail.smtp.host the smtp host key - required
   * <br>mail.smtp.user the username to access the smtp server - optional depending on your smtp provider
   * <br>mail.smtp.password the passsord to access the smtp server - optional depending on your smtp provider
   * <br>mail.smtp.port The port to use if not using the default port of 25
   */
  public VwMailSender( String strBundleName ) throws Exception
  {
     
    VwResourceMgr.loadBundle( strBundleName, true );

    // Extract all mail properties from the resource bundles
    Properties mailProps = VwResourceMgr.extractProperties( "mail.smtp", null );

    setupMailProperties( mailProps );
    
    
  } // end VwMailSender()


  /**
   * Configure the mail properties as specified in the passed resource bundle
   * @param mailProps  The mail properties to configure
   * @throws Exception
   */
  private void setupMailProperties( Properties mailProps ) throws Exception
  {


    m_strUserName = mailProps.getProperty( "mail.smtp.user" );
    m_strPassword = mailProps.getProperty( "mail.smtp.password" );

    mailProps.remove( "mail.smtp.user" );
    mailProps.remove( "mail.smtp.password" );

    if ( m_strUserName == null )
 		  throw new Exception( "the smtp username cannot null, if you passed a bundle name make sure the mail.smtp.user key is specified" );

    if ( m_strPassword == null )
  		  throw new Exception( "the smtp password cannot null, if you passed a bundle name make sure the mail.smtp.password key is specified" );

    if ( m_strPassword.startsWith( "@${" ))
      m_strPassword = VwFileUtil.readFile( m_strPassword.substring( 1 ) );


	  if ( mailProps.getProperty( "mail.smtp.host" ) == null )
		  throw new Exception( "the smtp host cannot be null, if you passed a bundle name make sure the mail.smtp.host key is specified" );
    

    if ( mailProps.getProperty( "mail.smtp.auth" ) != null ) // value was defined otherwise key is echoed back
    {


        m_fAuthenticated = true;
        m_sessionMail = Session.getInstance( mailProps, new javax.mail.Authenticator()
                                               {
                                                  protected PasswordAuthentication getPasswordAuthentication()
                                                  {
                                                    return new PasswordAuthentication( m_strUserName, m_strPassword );
                                                  }
                                               });
    }
    else
    {
        m_sessionMail = Session.getDefaultInstance( mailProps, null );
    }
    

  }


  /**
   * Connect to SMTP with username and password
   * @param strSmtpHost The smtp host address
   * @param strSmtpUserName The smtp login userid
   * @param strSmtpPassword The smtp login password
   */
  public VwMailSender( String strSmtpHost, String strSmtpUserName, String strSmtpPassword ) throws Exception
  {

    Properties mailProps = new Properties();
    mailProps.put( "mail.smtp.host", strSmtpHost );
    mailProps.put( "mail.smtp.user", strSmtpUserName );
    mailProps.put( "mail.smtp.password", strSmtpPassword );

    setupMailProperties( mailProps );

   
  } // end VwMailSender()

  /**
   * Local host name used in the SMTP HELO or EHLO command. Defaults to InetAddress.getLocalHost().getHostName().
   * <br>Should not normally need to be set if your JDK and your name service are configured properly. 
   */
  public void setLocalHostName( String strLocalHostName )
  { m_sessionMail.getProperties().put( "mail.smtp.localhost", strLocalHostName ); }
  
  /**
   * Set a different port nbr other than the default port 25 to use when connecting to the SMTP server
   * @param nPortNbr The new port number to use
   */
  public void setPort( int nPortNbr )
  { 
    String strPort = String.valueOf( nPortNbr );
    m_sessionMail.getProperties().put( "mail.smtp.port", strPort);
    
  }
  
  
  /**
   * Sets the debug option which when on give verbose output messages to stdout. The default os off
   * @param fDebug if true turn on debug
   */
  public void setDebug( boolean fDebug )
  { m_sessionMail.setDebug( fDebug ); }
  

  /**
   * Sets the MIME character encoding to use -- default is UTF-8
   * @param strEncoding
   */
  public void setCharEncoding( String strEncoding )
  { m_strEncoding = strEncoding; }
  
  
  /**
   * Returns the MIME character encoding used on the mail message
   * @return
   */
  public String getCharEncoding()
  { return m_strEncoding; }
  
  
  /**
   * Sends a mail message
   * 
   * @param strFrom The senders from address (i.e., jdoe@theDoes.com)
   * @param strTo The recipient's adress i.e. bdoe@theDoes.com) <br><strong>Note</strong>
   * Multiple recipients may be separated by commas
   * @param strSubject The mails subject
   * @param strMailBody (optional) The mail body html markup can also be used for the mail body
   * 
   * @throws Exception if the mail message could not be delivered
   */
  public void send( String strFrom, String strTo, String strSubject, String strMailBody ) throws Exception
  { send( strFrom, null, strTo, null, strSubject, strMailBody ); }
  
  /**
   * Sends a mail message
   * 
   * @param strFrom The senders from address (i.e., jdoe@theDoes.com)
   * @param strFrom Name The name of the sender
   * @param strTo The recipient's address i.e. bdoe@theDoes.com) <br><strong>Note</strong>
   * @param strReplyTo The reply to name Multiple recipients may be separated by commas
   * @param strSubject The mails subject
   * @param strMailBody (optional) The mail body html markup can also be used for the mail body
   * 
   * @throws Exception if the mail message could not be delivered
   */
  public void send( String strFrom, String strFromName, String strTo, String strReplyTo, String strSubject, String strMailBody ) throws Exception
  {

    // construct the message
    MimeMessage msg = new MimeMessage( m_sessionMail );
    
    if ( strFromName != null )
      msg.setFrom( new InternetAddress( strFrom, strFromName, m_strEncoding) );
    else
      msg.setFrom( new InternetAddress( strFrom) );
 
    msg.setRecipients( Message.RecipientType.TO,
        InternetAddress.parse( strTo, false));

    if ( strReplyTo != null )
    {
      if ( strFromName != null )
        msg.setReplyTo( new InternetAddress[] { new InternetAddress( strReplyTo, strFromName, m_strEncoding) } );
      else
        msg.setReplyTo( new InternetAddress[] { new InternetAddress( strReplyTo ) } );
      
    }
  
    msg.setSubject( strSubject, m_strEncoding );
    
    if ( strMailBody != null )
    {
      msg.setText( strMailBody, m_strEncoding );
      
      if ( strMailBody.toLowerCase().indexOf( "<html>") >= 0 )
        msg.setHeader( "Content-type", "text/html" );
    }

     
    // send the thing off
    sendMsg( msg );
    
  } // end send()

  /**
   * Sends a mail message with multiple attachments 
   * 
   * @param strFrom The senders from address (i.e., jdoe@theDoes.com)
   * @param strTo The recipient's adress i.e. bdoe@theDoes.com) <br><strong>Note</strong>
   * Multiple recipients may be separated by commas
   * @param strSubject The mails subject
   * @param strMailBody (optional) The mail body html markup can also be used for the mail body
   * 
   * @throws Exception if the mail message could not be delivered
   */
  public void send( String strFrom, String strTo, String strSubject, String strMailBody, List<VwMailAttachment> listAttachments ) throws Exception
  { send( strFrom, null, strTo, null, strSubject, strMailBody, listAttachments ); }

  /**
   * Sends a mail message with multiple attachments 
   * 
   * @param strFrom The senders from address (i.e., jdoe@theDoes.com)
   * @param strTo The recipient's adress i.e. bdoe@theDoes.com) <br><strong>Note</strong>
   * Multiple recipients may be separated by commas
   * @param strSubject The mails subject
   * @param strMailBody (optional) The mail body html markup can also be used for the mail body
   * 
   * @throws Exception if the mail message could not be delivered
   */
  public void send( String strFrom, String strFromName, String strTo, String strReplyTo, String strSubject, String strMailBody, List<VwMailAttachment> listAttachments ) throws Exception
  {
    // construct the message
    MimeMessage msg = new MimeMessage( m_sessionMail );
    Multipart mpart = new MimeMultipart();
    
    MimeBodyPart part = new MimeBodyPart();
    
    
    // construct the message
    
    if ( strFromName != null )
      msg.setFrom( new InternetAddress( strFrom, strFromName, m_strEncoding) );
    else
      msg.setFrom( new InternetAddress( strFrom) );
 
    msg.setRecipients( Message.RecipientType.TO,
        InternetAddress.parse( strTo, false));

    if ( strReplyTo != null )
    {
      if ( strFromName != null )
        msg.setReplyTo( new InternetAddress[] { new InternetAddress( strReplyTo, strFromName, m_strEncoding) } );
      else
        msg.setReplyTo( new InternetAddress[] { new InternetAddress( strReplyTo ) } );
      
    }
  
    msg.setSubject( strSubject, m_strEncoding );
    
    if ( strMailBody != null )
    {
      msg.setText( strMailBody, m_strEncoding );
      
      if ( strMailBody.toLowerCase().indexOf( "<html>") >= 0 )
        msg.setHeader( "Content-type", "text/html" );
    }

    if ( strMailBody != null )
    {
      
      if ( strMailBody.toLowerCase().indexOf( "<html>") >= 0 )
        part.setContent( strMailBody, "text/html" );
      else
        part.setText( strMailBody );
    }
    
    mpart.addBodyPart( part );
    
    for ( VwMailAttachment ma : listAttachments)
    {
      mpart.addBodyPart( ma.getBodyPart() );
      
    } // end for)

    msg.setContent( mpart );

    sendMsg( msg );
     
   
  }
  
  /**
   * Send out the message using the transport method based on the presence/absense of the username and password parameters
   * @param msg
   * @throws Exception
   */
  private void sendMsg( Message msg ) throws Exception 
  {
    msg.setSentDate(new Date());
    

    if ( m_strUserName != null && m_fAuthenticated == false)
    {

      Transport tr = m_sessionMail.getTransport("smtp");
      tr.connect( m_strSmtpHost, m_strUserName, m_strPassword );
      msg.saveChanges();
      tr.sendMessage( msg, msg.getAllRecipients());
      tr.close();
    }
    else
      Transport.send(msg);
     
   
  }
  
  /**
   * Sends a mail message with attachment
   * 
   * @param strFrom The senders from address (i.e., jdoe@theDoes.com)
   * @param strTo The recipient's adress i.e. bdoe@theDoes.com) <br><strong>Note</strong>
   * Multiple recipients may be separated by commas
   * @param strSubject The mails subject
   * @param strMailBody (optional) The mail body html markup can also be used for the mail body
   * 
   * @throws Exception if the mail message could not be delivered
   */
  public void send( String strFrom, String strTo, String strSubject, String strMailBody, VwMailAttachment attachment ) throws Exception
  {
    List<VwMailAttachment> listAttach = new ArrayList<VwMailAttachment>(1);
    listAttach.add( attachment );
    
    send( strFrom, strTo, strSubject, strMailBody, listAttach );
    
  } // end send()
  
 
  private class Authenticator extends javax.mail.Authenticator 
  {
      private PasswordAuthentication authentication;

      public Authenticator(String username, String password) 
      {
              authentication = new PasswordAuthentication(username, password);
      }

      protected PasswordAuthentication getPasswordAuthentication() 
      {
              return authentication;
      }
  }
  
} 
  // end class VwMailSender{}

// *** End VwMailSender.java ***

