package com.vozzware.util;

import com.vozzware.db.VwDatabase;
import com.vozzware.db.VwDbMgr;
import com.vozzware.db.VwSqlMgr;
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
import org.postgresql.util.PSQLException;

import java.io.Serializable;
import java.util.HashMap;
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
@Plugin(name = "VwDbLoggerAppender", category = "Core", elementType = "appender", printObject = false)
public class VwLogAppenderDbImpl extends AbstractAppender
{

  private final ReadWriteLock rwLock = new ReentrantReadWriteLock();
  private final Lock readLock = rwLock.readLock();

  private int m_nDbRetrys = 0;
  private int m_nDbMaxRetrys = 0;

  private VwDbMgr m_dbMgr;
  private VwDatabase m_db;
  private VwSqlMgr m_sqlm;

  private String m_strDataSourceName;
  private String m_strDataSourceId;
  private String m_strUid;
  private String m_strPwd;
  private String m_strIpAddress;
  private String m_strFilter;
  private String m_strTableName;
  private String m_strLogSql;
  private String m_strLogCategory;
  private String m_strBackupLogProperties;
  private String m_strInstanceName;

  private VwLogger m_backupLogger;

  protected VwLogAppenderDbImpl( String strInstanceName, String name, Filter filter,
                                 Layout<? extends Serializable> layout, final boolean ignoreExceptions ) throws Exception
  {
    super( name, filter, layout, ignoreExceptions );

    m_strInstanceName = strInstanceName;

    setupDatabaseAccess();
  }

  @PluginFactory
  public static VwLogAppenderDbImpl createAppender( String strInstanceName,
    @PluginAttribute("name") String name,
    @PluginElement("Layout") Layout<? extends Serializable> layout,
    @PluginElement("Filter") final Filter filter ) throws Exception
  {

    if ( name == null )
    {
      LOGGER.error( "No name provided for VwLogAppenderDbImpl" );
      return null;
    }

    if ( layout == null )
    {
      layout = PatternLayout.createDefaultLayout();
    }

    return new VwLogAppenderDbImpl( strInstanceName, name, filter, layout, true );

  }


  private void setupDatabaseAccess()  throws Exception
  {
    String strKey  = m_strInstanceName + ":";

    m_strIpAddress = VwExString.getNicAddress( VwResourceMgr.getString( strKey + "server.nicCardName", (String)null ));
    m_strDataSourceName = VwResourceMgr.getString( strKey + "vw.log4j.db.dsourceName", (String)null );
    m_strDataSourceId = VwResourceMgr.getString( strKey + "vw.log4j.db.dsourceId", (String)null  );
    m_strUid = VwResourceMgr.getString( strKey +  "vw.log4j.db.uid", (String)null  );
    m_strPwd = VwResourceMgr.getString( strKey + "vw.log4j.db.pwd", (String)null  );

    m_strLogCategory = VwResourceMgr.getString( strKey + "vw.log4j.db.category", (String)null  );
    m_strFilter = VwResourceMgr.getString( strKey + "vw.log4j.db.filterSearch", (String)null );
    m_strTableName = VwResourceMgr.getString( strKey + "vw.log4j.db.tableName", (String)null  );

    m_nDbMaxRetrys = VwResourceMgr.getInt( strKey + "vw.log4j.db.maxRetrys", 5 );

    m_strBackupLogProperties = VwResourceMgr.getString( strKey + "vw.log4j.db.backuoLogProperties", (String)null  );

    if ( m_strBackupLogProperties == null )
    {
      m_strBackupLogProperties = "VwLogAppenderDbImpl";

    }

    m_backupLogger = new VwLogger( m_strBackupLogProperties );

    if ( m_strDataSourceName == null )
    {
      throw new Exception( "The \"vw.log4j.db.dsourceName\" property must be defined" );

    }

    if ( m_strDataSourceId == null )
    {
      throw new Exception( "The \"vw.log4j.db.dsourceId\" property must be defined" );

    }

    if ( m_strUid == null )
    {
      throw new Exception( "The \"vw.log4j.db.uid\" property must be defined" );

    }

    if ( m_strPwd == null )
    {
      throw new Exception( "The \"vw.log4j.db.pwd\" property must be defined" );

    }

    if ( m_strTableName == null )
    {
      throw new Exception( "The \"vw.log4j.db.tableName\" property must be defined" );

    }


    login();

    if ( VwResourceMgr.getBoolean( strKey + "vw.log4j.db.clear.category"  ) )
    {
      String strClearSql = "delete from " + m_strTableName + " where log_category = :logCategory";
      m_sqlm.exec( strClearSql, m_strLogCategory );

    }
    else
    if ( VwResourceMgr.getBoolean( strKey + "vw.log4j.db.clear.all" ) )
    {
      String strClearSql = "delete from " + m_strTableName ;
      m_sqlm.exec( strClearSql , null );

    }

    m_strLogSql = "insert into " + m_strTableName + "( log_level, log_ip_address, log_category, log_date_time_pk, log_class_name, log_msg) values( :level, :ipAddress, :category, current_timestamp, :logClassName,  :msg)";

  }

  /**
   * Login to the database and get a connectoion
   * @throws Exception
   */
  private void login() throws Exception
  {
    m_dbMgr = new VwDbMgr( m_strDataSourceName, m_strDataSourceId, m_backupLogger );
    m_db = m_dbMgr.login( m_strUid, m_strPwd );
    m_sqlm = new VwSqlMgr( m_db );

  }

  public void clearLogTable() throws Exception
  {
    m_sqlm.exec( "delete from " + m_strTableName, null );

  }
  @Override
  public void append( LogEvent event )
  {

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

        Map<String, String> mapParams = new HashMap<>();

        String strLogLevel = event.getLevel().toString();

        String[] astrMsgPieces = strMsg.split( "__");

        String strMsgProducerClass = null;

         if ( astrMsgPieces.length == 2 )
         {
           strMsgProducerClass = astrMsgPieces[ 0 ];
         }

        mapParams.put( "level", strLogLevel );
        mapParams.put( "category", m_strLogCategory );
        mapParams.put( "ipAddress", m_strIpAddress );
        mapParams.put( "logClassName", strMsgProducerClass );
        mapParams.put( "msg", astrMsgPieces[ astrMsgPieces.length - 1 ] );

        m_sqlm.exec( m_strLogSql, mapParams );

      }

      catch ( Exception ex )
      {
        if ( ex instanceof PSQLException )
        {
           try
           {
             login();
             append( event );
             return;
           }
           catch( Exception lginEx )
           {
             if ( ++m_nDbRetrys < m_nDbMaxRetrys )
             {
               continue;
             }
             else
             {
               return;
             }
           }
        }

        if ( !ignoreExceptions() )
        {
          throw new AppenderLoggingException( ex );
        }
      }
      finally
      {
        readLock.unlock();
        m_nDbRetrys = 0;
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
