/*
 *
 * ============================================================================================
 *
 *                                     V o z z w a r e  L L C
 *
 *                                     Copyright(c) 2012 By
 *
 *                                         Vozzware LLC
 *
 *                             A L L   R I G H T S   R E S E R V E D
 *
 *  ============================================================================================
 * /
 */

package test.vozzware.mail;

import com.vozzware.mail.VwMailSender;
import org.junit.Test;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/*
============================================================================================

    Source File Name: TestMail.java

    Author:           petervosburgh
    
    Date Generated:   6/20/12

    Time Generated:   5:59 AM

============================================================================================
*/
public class TestMail
{

  @Test
  public void testSendMsg() throws Exception
  {

    VwMailSender ms = new VwMailSender( "mail" );
    String strBody = "<html><body><strong>Just</strong> Testing New Port</body></html>";
    ms.send( "support@armoredinfo.com", "support@armoredinfo.com", "pvosbur@gmail.com", null, "Test", strBody );


    return;


  }

  @Test
  public void test2()
  {
    final String username = "support@armoredinfo.com";
    final String password = "Royale7777$";
    Properties prop = new Properties();
    prop.put( "mail.smtp.auth", "true" );
    prop.put( "mail.smtp.host", "smtp.gmail.com" );
    prop.put( "mail.smtp.port", "587" );
    prop.put( "mail.smtp.starttls.enable", "true" );

    Session session = Session.getDefaultInstance( prop,
                                                  new javax.mail.Authenticator()
                                                  {
                                                    protected PasswordAuthentication getPasswordAuthentication()
                                                    {
                                                      return new PasswordAuthentication( username, password );
                                                    }
                                                  } );
    try
    {
      String body = "Dear Big Pooper Welcome";
      String htmlBody = "<strong>This is an HTML Message</strong>";
      String textBody = "This is a Text Message.";
      Message message = new MimeMessage( session );
      message.setFrom( new InternetAddress( "support@armoredinfo.com" ) );
      message.setRecipients( Message.RecipientType.TO, InternetAddress.parse( "pvosbur@gmail.com" ) );
      message.setSubject( "Testing Subject" );

      /*
      MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
      mc.addMailcap( "text/html;; x-java-content-handler=com.sun.mail.handlers.text_html" );
      mc.addMailcap( "text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml" );
      mc.addMailcap( "text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain" );
      mc.addMailcap( "multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed" );
      mc.addMailcap( "message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822" );
      CommandMap.setDefaultCommandMap( mc );
      */
      message.setText( htmlBody );
      message.setContent( textBody, "text/html" );
      Transport.send( message );

      System.out.println( "Done" );

    }
    catch ( MessagingException e )
    {
      e.printStackTrace();
    }

  }

}



