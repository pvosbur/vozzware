package com.vozzware.util;

import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import nl.martijndwars.webpush.Subscription;
import nl.martijndwars.webpush.Utils;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.appender.AppenderLoggingException;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.Serializable;
import java.security.Security;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/*
============================================================================================

    Source File Name: 

    Author:           petervosburgh
    
    Date Generated:   2/20/16

    Time Generated:   5:34 AM

============================================================================================
*/
@Plugin(name = "VwPwaPushLoggerAppender", category = "Core", elementType = "appender", printObject = false)
public class VwLogAppenderPWAPushImpl extends AbstractAppender
{

  private final ReadWriteLock rwLock = new ReentrantReadWriteLock();
  private final Lock readLock = rwLock.readLock();

  private String        m_strIpAddress;
  private String        m_strFilter;
  private String        m_strLogCategory;
  private String        m_strBackupLogProperties;
  private String        m_strInstanceName;
  private PushService   m_pushService;
  private Subscription  m_subscription;
  private String        m_strMsgHdrs;

  private boolean       m_bEnabled;

  private VwLogger      m_backupLogger;

  protected VwLogAppenderPWAPushImpl( String strInstanceName, boolean bEnabled, String name, Filter filter,
                                      Layout<? extends Serializable> layout, final boolean ignoreExceptions ) throws Exception
  {
    super( name, filter, layout, ignoreExceptions );

    m_strInstanceName = strInstanceName;

    m_bEnabled = bEnabled;
    setupPushService();
  }

  @PluginFactory
  public static VwLogAppenderPWAPushImpl createAppender( String strInstanceName,
                                                         boolean bEnabled,
                                                         @PluginAttribute("name") String name,
                                                         @PluginElement("Layout") Layout<? extends Serializable> layout,
                                                         @PluginElement("Filter") final Filter filter ) throws Exception
  {

    if ( name == null )
    {
      LOGGER.error( "No name provided for VwLogAppenderPWAPushImpl" );
      return null;
    }

    if ( layout == null )
    {
      layout = PatternLayout.createDefaultLayout();
    }

    return new VwLogAppenderPWAPushImpl( strInstanceName, bEnabled, name, filter, layout, true );

  }

  /**
   * Sets the PWA's subscription object
   * @param subscription
   */
  public void setSubscription( Subscription subscription )
  {
    m_subscription = subscription;

  }

  public void setEnabled( boolean bEnabled )
  {
    m_bEnabled = bEnabled;
  }

  /**
   * Setup the pwa push servise
   * @throws Exception
   */
  private void setupPushService()  throws Exception
  {
    String strKey  = m_strInstanceName + ":";

    String strServerNicCardName = VwResourceMgr.getString( strKey + "server.nicCardName", (String)null );

    if ( strServerNicCardName != null )
    {
      m_strIpAddress = VwExString.getNicAddress( strServerNicCardName );
    }

    m_strLogCategory = VwResourceMgr.getString( strKey + "vw.log4j.db.category", (String)null  );
    m_strFilter = VwResourceMgr.getString( strKey + "vw.log4j.db.filterSearch", (String)null );

    String strMsgHdrs = VwResourceMgr.getString( strKey + "vw.log4j.pwsPushMsgHdrs", (String)null );

    if ( strMsgHdrs != null )
    {
      String[] astrMsgHdrs = strMsgHdrs.split( ",");

      m_strMsgHdrs = "";

      for ( int x =0; x < astrMsgHdrs.length; x++ )
      {
        String[] astrPieces = astrMsgHdrs[ x ].split( "=" );
        if ( astrPieces.length != 2 )
        {
          throw new Exception( "Invalid message hdr entry: " + astrMsgHdrs[ x ] + ". format is <hdr name>=<hdr value>");
        }

        if ( x > 0 )
        {
          m_strMsgHdrs += ",";
        }

        m_strMsgHdrs += "\"" + astrPieces[ 0 ] + "\":\"" + astrPieces[ 1 ] + "\"";

      }

    }


    m_strBackupLogProperties = VwResourceMgr.getString( strKey + "vw.log4j.db.backuoLogProperties", (String)null  );

    if ( m_strBackupLogProperties == null )
    {
      m_strBackupLogProperties = "VwLogAppenderPushImpl";

    }

    m_backupLogger = new VwLogger( m_strBackupLogProperties );

    try
     {

       VwVapidKeys vkeys = new VwVapidKeys();

       Map<String,String> mapVapidKeys = VwResourceMgr.loadPropFileToMap( "/var/cr8web/pwa/push_notification_keys.properties" );

       vkeys.setPublicKey( mapVapidKeys.get( "public_key") );
       vkeys.setPrivateKey( mapVapidKeys.get( "private_key") );


       Security.addProvider( new BouncyCastleProvider() );
       m_pushService = new PushService();
       m_pushService.setPublicKey( Utils.loadPublicKey( vkeys.getPublicKey() ) );
       m_pushService.setPrivateKey( Utils.loadPrivateKey( vkeys.getPrivateKey() ));

     }
     catch( Exception ex )
     {
       ex.printStackTrace();
     }

  }

  @Override
  public void append( LogEvent event )
  {

    if ( m_subscription == null || !m_bEnabled )
    {
      return;

    }

    while( true )
    {
      readLock.lock();

      try
      {
        final byte[] bytes = getLayout().toByteArray( event );

        String strMsg = new String( bytes, "UTF-8" );


        String strFilterVal = null;

        if ( m_strFilter != null )
        {
          int nPos = strMsg.indexOf( m_strFilter );

          if ( nPos >= 0 )
          {
            strFilterVal = getFilterValue( nPos + m_strFilter.length(), strMsg );
          }
        }

        String strLogLevel = event.getLevel().toString();

        String[] astrMsgPieces = strMsg.split( "__");

        String strMsgProducerClass = null;

        if ( astrMsgPieces.length == 2 )
        {
          strMsgProducerClass = astrMsgPieces[ 0 ];
        }

        strMsg = VwExString.strip( strMsg, "\n\r"  );

        String strJSONMsg = "{";

        if ( m_strMsgHdrs != null )
        {
          strJSONMsg += m_strMsgHdrs + ",\"logMsg\":\"" + strMsg + "\"}";
        }
        else
        {
          strJSONMsg += "\"notificationTypeFk\":\"PWA_LOG_MSG\",\"ignoreSessionId\":true,\"logMsg\":\"" + strMsg + "\"}";

        }

        Notification webPushNotification = new Notification( m_subscription, strJSONMsg );

        m_pushService.send( webPushNotification );
      }

      catch ( Exception ex )
      {
        if ( !ignoreExceptions() )
        {
          throw new AppenderLoggingException( ex );
        }
      }
      finally
      {
        readLock.unlock();
        return;
      }

    } // end while
  }


  private String getFilterValue( int nStartPos, String strMsg )
  {

    int nMsgLen = strMsg.length();
    int nFilterEndPos = 0;

    // Suck up any white space to filter value
    for ( ; nStartPos < nMsgLen; nStartPos++ )
    {
      if ( !Character.isWhitespace(  strMsg.charAt( nStartPos ) ))
      {
        break;
      }
    }

    // Suck up any white space to filter value
    for ( nFilterEndPos = nStartPos; nFilterEndPos < nMsgLen; nFilterEndPos++ )
    {
      if ( Character.isWhitespace(  strMsg.charAt( nFilterEndPos ) ))
      {
        break;
      }
    }


    String strFilterVal = strMsg.substring( nStartPos, nFilterEndPos );

    return strFilterVal;

  }
}
