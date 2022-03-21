package com.vozzware.util;

import com.vozzware.serverUtils.VwServerUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.appender.FileAppender;
import org.apache.logging.log4j.core.appender.RollingFileAppender;
import org.apache.logging.log4j.core.appender.rolling.DefaultRolloverStrategy;
import org.apache.logging.log4j.core.appender.rolling.SizeBasedTriggeringPolicy;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.PatternLayout;

import java.io.File;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/*
============================================================================================

    Source File Name: 

    Author:           petervosburgh
    
    Date Generated:   2/20/16

    Time Generated:   7:04 AM

============================================================================================
*/

/**
 * @author P. VosBurgh
 *
 * This class wraps the log4j Logger for easy custom configuring and message generation. This call manages loggers with
 * <br/>different configurations based on the instance name passed to getIntance. If the instance name is a properties file
 * <br/>then the logger is configured based on the property file settings. When getInstance( "myname" ) method is invoked
 * <br/>VwLogger checks to see if a logger instance already exists for that name and it returns that instance if it exists.
 * <br/>Otherwise it is configured and then cached by that name.
 * <br/>The following properties are recognized:
 * <br/>vw.log4j.msgPattern The log4j messagepattern to use for any appender created
 * <br/>vw.log4j.sendToConsole if true create a console appender
 * <br/>vw.log4j.level one of debug,info,warn,error or fatal values
 * <br/>vw.log4j.debugVerboseLevel  sets a user assign debug verbose level that can be reference by any classes using the the logger to determine how debug data to dump
 * <br/>vw.log4j.logfile The path and name to a logi file
 * <br/>vw.log4j.rollingLogfile The path and name to a rolling log file log file
 * <br/>vw.log4j.rollingLogfileSize The max size in bytes before the file rolls
 * <br/>vw.log4j.rollingLogfileMaxFiles The Maimum files to create when rolling over - default is 1
 *
 * <br/>The following properties can be used to create a logger appender that logs its events to a database. The database appender uses the Vozzworks
 * <br/>database feamewordk. The following table schema first needs to be added to your database: Replace [YOUR TABLE_NAME] with the name you want to use
 * create table [YOUR_TABLE_NAME]
 (
   seq_nbr serial not null,
   log_level varchar(10) not null,
   log_category varchar(20) null,
   log_date_time timestamp not null,
   log_filter varchar(80) null,
   log_msg text not null,

   primary key( seq_nbr)
 );

 * <br/>vw.log4j.db.dsourceName The datasource name defined in the DatasourceDrivers.cml file
 * <br/>vw.log4j.db.dsourceId  The datasource Id name defined in the DatasourceDrivers.cml file
 * <br/>vw.log4j.db.uid The user id used to log into the database
 * <br/>vw.log4j.db.pwd The password used to log in to the database
 * <br/>vw.log4j.db.category A user assigned catagory (may me null) for searching
 * <br/>vw.log4j.db.filterSearch a filetr search that may be extracted from the log message useful for filtering
 * <br/>vw.log4j.db.tableName The name of the table that replace the [YOUR_TABLE_NAME] in the schema above
 *
 */
public class VwLogger
{

  private LoggerContext m_ctx;
  private Configuration m_config;
  private LoggerConfig m_loggerConfig;

  private Logger m_logger;                                       // The log4j Logger instance

  private String  m_strInstanceName;

  private Layout<? extends Serializable> m_instanceLayout;
  private Layout<? extends Serializable> m_dbLayout;

  private static  String    s_strDefaultPattern = "%-5p, [%d{dd MMM yyyy HH:mm:ss SSS}], %m, %rEx{full}\n"; // The default message pattern

  private static  Map<String, Level> s_mapLevels = new HashMap<String, Level>();

  private static  Map<String,VwLogger> s_mapInstances = Collections.synchronizedMap( new HashMap<String, VwLogger>() );

  private int     m_nVerboseLevel = 1;

  static
  {
    s_mapLevels.put( "trace", Level.TRACE );
    s_mapLevels.put( "debug", Level.DEBUG );
    s_mapLevels.put( "info", Level.INFO );
    s_mapLevels.put( "warn", Level.WARN );
    s_mapLevels.put( "error", Level.ERROR );
    s_mapLevels.put( "fatal", Level.FATAL );

  }

  class FileSpec
  {
    String  m_strName;
    String  m_strPattern;
    long    m_lMaxRolloverSize;
    int    m_nMaxFiles = 1;

    FileSpec( String strName, String strPattern )
    {
      m_strName = strName;
      m_strPattern = strPattern;
    }

    FileSpec( String strName, String strPattern, long lMaxRolloverSize, int nMaxFiles )
    {
      m_strName = strName;
      m_strPattern = strPattern;
      m_lMaxRolloverSize = lMaxRolloverSize;
      m_nMaxFiles = nMaxFiles;
    }

  } // end class FileSpec

  public VwLogger( String strPropFileName ) throws Exception
  {

    m_ctx = (LoggerContext) LogManager.getContext( false);
    m_config = m_ctx.getConfiguration();

    m_strInstanceName = strPropFileName;

    if ( m_strInstanceName  == null )
    {
      m_strInstanceName  = "VwLogger.class";
    }

    if (  m_strInstanceName != null &&  m_strInstanceName.endsWith( ".properties" ))
    {

      try
      {

        VwResourceMgr.loadProperties( m_strInstanceName, false );
        configureFromProps();

      }
      catch( Exception ex )
      {
        configureDefaults( ex );
        throw ex;
      }

    }
    else
    {
      configureDefaults( null );
    }



  }

  public VwLogger()
  {
    return;
  }


  /**
   * Gets the singlton instance with the default confuration that only has a console sppender and sets the out level
   * to debug (the lowest)
   *
   * @return The default singleton instance
   * @throws Exception
   */
  public synchronized static VwLogger getInstance() throws Exception
  { return getInstance( null );  }

  /**
   * Gets the instance based on the instance name passed. If a logger exists with this name then that instance is returnd.
   * <br>If the instance name is a property file i.e. any name ending in .properties,
   * <br>then the logger will be configured according to the property specifications.
   *
   * @return The logger instance assigned to the instance name.
   *
   * @throws Exception If a properties file is specified
   */
  public synchronized static VwLogger getInstance( String strInstanceName ) throws Exception
  {
    VwLogger logger = null;

    if ( strInstanceName != null )
    {
      logger = (VwLogger)s_mapInstances.get( strInstanceName );
    }
    else
    {
      logger = (VwLogger)s_mapInstances.get( "DEFAULT" );
    }

    if ( logger == null )
    {
      logger = new VwLogger( strInstanceName );

      if ( strInstanceName != null )
      {
        s_mapInstances.put( strInstanceName, logger );
      }
      else
      {
        s_mapInstances.put( "DEFAULT", logger );
      }

    }

    logger.setInstanceName( strInstanceName );

    return logger;

  } // end getInstance()


  /**
   * Sets the logger debug verbose level
   *
   * @param nVerboseLevel the verbose level can be an integer from 1 to 3
   */
  public void setDebugVerboseLevel( int nVerboseLevel )
  {
    m_nVerboseLevel = nVerboseLevel;

  }

  /**
   * Gets the instance name of this logger instance
   * @return
   */
  public String getInstanceName()
  {
    return m_strInstanceName;
  }


  public void setInstanceName( String strInstanceName )
  {
    m_strInstanceName = strInstanceName;

  }

  /**
   * Adds a new appender to this logger instance
   *
   * @param appender The Appender to add
   */
  public void addAppender( Appender appender )
  {  m_config.addAppender( appender ); }

  /**
   * Returns the verbose level of the logger
   * @return
   */
  public int getDebugVerboseLevel()
  {
    return m_nVerboseLevel;
  }

  public void debug( Class clsProducer, String strMsg )
  {
    m_logger.debug( makeMsg( clsProducer, strMsg ) );
  }

  public void debug( String strMsg )
   {
     m_logger.debug( strMsg );
   }

  public void info( String strMsg )
  {
    m_logger.info( strMsg );
  }

  public void info( Class clsProducer, String strMsg )
  {
    m_logger.info( makeMsg( clsProducer, strMsg ) );
  }

  public void warn( Class clsProducer, String strMsg )
  { m_logger.warn( makeMsg( clsProducer, strMsg ) ); }


  public void warn( Class clsProducer, String strMsg, Throwable t )
  { m_logger.warn( "*" + makeMsg( clsProducer, strMsg ), t ); }


  public void error( Class clsProducer, String strMsg )
   { m_logger.error( "***" + makeMsg( clsProducer, strMsg ) ); }


   public void error( Class clsProducer, String strMsg, Throwable t )
   { m_logger.error( "***" + makeMsg( clsProducer, strMsg ), t ); }

  public void error(  String strMsg, Throwable t )
  { m_logger.error( "***" + strMsg, t ); }

   public void fatal( Class clsProducer, String strMsg )
   { m_logger.fatal( "*****" + makeMsg( clsProducer, strMsg ) ); }


   public void fatal( Class clsProducer, String strMsg, Throwable t )
   { m_logger.fatal( "*****" + makeMsg( clsProducer, strMsg ), t ); }

  public void trace( String strMsg, Throwable t )
  {
    if ( m_logger.isTraceEnabled() )
      m_logger.trace( makeMsg( null, strMsg ), t );
  }

  /**
   * Returns a set of all the logger instances names configured with this class
   * @return
   */
  public static Set<String> getInstanceNames()
  {
    return s_mapInstances.keySet();

  }

  /**
   * Sets the new level for controlling output
   * @param level
   */
  public void setLevel( Level level )
  {

    LoggerConfig loggerCcofig = m_config.getLoggerConfig( m_strInstanceName );
    loggerCcofig.setLevel( level );
    m_ctx.updateLoggers(m_config);

  }

  /**
   * Sets the log level using string representation must be one of "info|debug|error|fatal|warn|trace"
   *
   * @param strLogLevel The log level as a string
   */
  public void setLevelAsString( String strLogLevel )
  {
    setLevel( s_mapLevels.get( strLogLevel.toLowerCase() ) );

  }


  /**
   * Gets the logging level currently in effect
   * @return
   */
  public Level getLevel()
  { return m_logger.getLevel(); }

  public String getLevelAsString()
  {
    return m_logger.getLevel().toString();
  }


  /**
   * Restores the logger to its initial settings. If a property file was specified, it re-installs the appenders
   * <br>according to the property file settings else the standard default configuration is used.
   *
   */
  public void restore() throws Exception
  {

    if ( m_strInstanceName == null )
    {
      configureDefaults( null );
    }

    configureFromProps();

  } // end restore()

  /**
   * Returns a set of all the logger instances configured with this class
   * @return
   */
  public static Collection<VwLogger> getLoggerInstances()
  {
    return s_mapInstances.values();

  }

  /**
   * Sets the default message layout string used when creating appenders
   * @param strDefaultPattern The log4j pattern string
   */
  public static void setDefaultPattern( String strDefaultPattern )
  { s_strDefaultPattern = strDefaultPattern; }


  /**
   * Gets the default log4j pattern string
   * @return The pattern layout string
   */
  public static String getDefaultPattern()
  { return s_strDefaultPattern; }

  /**
   * Makes a message from a Class name and message string
   *
   * @param clsProducer The name of the cllass that produced the message
   * @param strMsg The message
   * @return
   */
  private String makeMsg( Class clsProducer, String strMsg )
  {
    String strMsgMeta = "";

    if ( clsProducer != null )
    {
      strMsgMeta += clsProducer.getName() + ", ";
    }

    try
    {
      strMsgMeta += "Server Ip: " + VwServerUtils.getServerIpAddr() + ", ";
    }
    catch( Exception ex )
    {

    }

    return strMsgMeta + strMsg;
  }

  /**
   * Returns true if the debug level is in effect
   * @return
   */
  public boolean isDebugEnabled()
  { return m_logger.isDebugEnabled(); }


  /**
   * Gets an appender instance by is instance nane and appender name
   * @param strInstanceName The instance name of the logger
   * @param strAppenderName The name of the appender to get
   * @return
   */
  public static Appender getAppender( String strInstanceName, String strAppenderName )
  {
    VwLogger logger = s_mapInstances.get( strInstanceName );

    Map<String, Appender> mapAppenders = logger.m_loggerConfig.getAppenders();

    return mapAppenders.get( strAppenderName );

  }

  public Appender getAppender( String strAppenderName )
  {
    Map<String, Appender> mapAppenders = m_loggerConfig.getAppenders();

    return mapAppenders.get( strAppenderName );

  }

  /**
   * Gets a comma separated string of all appender names
   * @return
   */
  protected List<String> getAppenderNames()
  {
    String strAppenderNames = "";

    VwLogger logger = s_mapInstances.get( m_strInstanceName );

    Map<String, Appender> mapAppenders = logger.m_loggerConfig.getAppenders();

    List<String> listAppenderNames = new ArrayList<>(  );;

    for ( Appender appender : mapAppenders.values() )
    {
      listAppenderNames.add( appender.getName() );
    }

    return listAppenderNames;

  }

  
  protected boolean removeAppender( String strAppenderName )
  {

    m_loggerConfig.removeAppender(strAppenderName  );
    return true;
  }


  /**
   * Configure appenders from property file settings
   * @throws Exception
   */
  private void configureFromProps() throws Exception
  {
    AppenderRef ref = AppenderRef.createAppenderRef( "File", null, null);
    AppenderRef[] refs = new AppenderRef[] {ref};
    m_loggerConfig = LoggerConfig.createLogger( "false", Level.INFO, m_strInstanceName,
                                                "true", refs, null, m_config, null );

    String strKey = m_strInstanceName + ":";

    m_nVerboseLevel =  VwResourceMgr.getInt( strKey + "vw.log4j.debugVerboseLevel", 1 );

    String strPatternLayout = VwResourceMgr.getString( strKey +  "vw.log4j.msgPattern", s_strDefaultPattern );
    boolean fUseConsole = VwResourceMgr.getBoolean( strKey + "vw.log4j.sendToConsole", false );

    m_instanceLayout = PatternLayout.createLayout( strPatternLayout, null, null, null, Charset.defaultCharset(), false, false, null, null );

    if ( fUseConsole )
    {
       createConsoleAppender();
    }

    if ( VwResourceMgr.getString( strKey + "vw.log4j.logfile", (String)null ) != null )
    {
      createLogFileAppender();
    }

    if ( VwResourceMgr.getString( strKey + "vw.log4j.rollingLogfile", (String)null ) != null )
    {
      createRollingLogFileAppender();
    }

    if ( VwResourceMgr.getString( strKey + "vw.log4j.db.dsourceName", (String)null ) != null )
    {
      String strDbPattern =  VwResourceMgr.getString( strKey + "vw.log4j.db.msgPattern", (String)null );


      if ( strDbPattern != null )
      {
        m_dbLayout = PatternLayout.createLayout( strDbPattern, null, null, null, Charset.defaultCharset(), false, false, null, null );

      }

      createDatabaseAppender();
    }

    if ( VwResourceMgr.getString( strKey + "vw.log4j.pwaPushAppender", (String)null ) != null )
    {
       creatPwaPushAppender( VwResourceMgr.getBoolean( strKey + "vw.log4j.pwaPushAppender.enabled") );
    }


    m_config.addLogger( m_strInstanceName, m_loggerConfig);

    m_ctx.updateLoggers();

    String strLevel = VwResourceMgr.getString( strKey + "vw.log4j.level", (String)null );

    if ( strLevel == null )
    {
      strLevel = "info";

    }
    setLevelAsString( strLevel );

    m_logger = m_ctx.getLogger( m_strInstanceName );

  }

  /**
    * Configures bare bones defaults because the MMSProperties file is not available
    */
   private void configureDefaults( Exception ex ) throws Exception
   {
     AppenderRef ref = AppenderRef.createAppenderRef( "File", null, null);
     AppenderRef[] refs = new AppenderRef[] {ref};
     m_loggerConfig = LoggerConfig.createLogger( "false", Level.INFO, m_strInstanceName,
                                                 "true", refs, null, m_config, null );

     String strKey = m_strInstanceName + ":";

     String strPatternLayout = VwResourceMgr.getString( strKey + "vw.log4j.msgPattern", s_strDefaultPattern );

     m_instanceLayout = PatternLayout.createLayout( strPatternLayout, null, null, null, Charset.defaultCharset(), false, false, null, null );


     createConsoleAppender();

     if ( ex != null )
     {
       fatal( this.getClass(), "Error logger defaults are being configured because property loader threw this Exception", ex );
     }

     m_config.addLogger( m_strInstanceName, m_loggerConfig);

     m_ctx.updateLoggers();

     m_logger = m_ctx.getLogger( m_strInstanceName );

   }


  /**
   * Creats a console appender
   *
   * @throws Exception
   */
  protected void createConsoleAppender() throws Exception
  {
    Appender appConsole = ConsoleAppender.createAppender( m_instanceLayout, null, null, "CONSOLE_APPENDER", false, true );
    appConsole.start();

    m_config.addAppender( appConsole );

    m_loggerConfig.addAppender(appConsole, null, null);

  }


  /**
    * Creats a console file appender
    *
     * @throws Exception
    */
  protected void createLogFileAppender()  throws Exception
  {
    String strKey = m_strInstanceName + ":";

    String strLogFile = VwResourceMgr.getString( strKey +  "vw.log4j.logfile", (String)null );

    if ( strLogFile == null )
    {
      throw new Exception( "The log file name must be specified using proprty vw.log4j.logfile");
    }

    Appender logFileAppender = FileAppender.createAppender( strLogFile, "true", "true", "LOG_FILE_APPENDER", "true",
                                                            null, "true", "8192", m_instanceLayout, null, "false", null, m_config );

    logFileAppender.start();
    m_config.addAppender( logFileAppender );
    m_loggerConfig.addAppender( logFileAppender, null, null );


  } // end createLogFileAppender()


  /**
   * Creats a rolling file appender
   *
   * @throws Exception
   */
  protected void createRollingLogFileAppender()  throws Exception
  {

    String strKey = m_strInstanceName + ":";

    String strRollingLogFile = VwResourceMgr.getString( strKey + "vw.log4j.rollingLogfile", (String)null );

    if ( strRollingLogFile == null )
    {
      throw new Exception( "The rolling log file name must be specified using proprty vw.log4j.rollingLogfile");
    }


    String strRollOverSize = VwResourceMgr.getString( strKey + "vw.log4j.rollingLogfileSize", (String)null );

    if ( strRollOverSize == null )
    {
      strRollOverSize = "10MB";
    }

    SizeBasedTriggeringPolicy policy = SizeBasedTriggeringPolicy.createPolicy( strRollOverSize );

    String strMaxRollOvers = VwResourceMgr.getString( strKey + "vw.log4j.rollingLogMaxFiles", (String)null );

    if ( strMaxRollOvers == null )
    {
      strMaxRollOvers = "100";

    }

    DefaultRolloverStrategy strat = DefaultRolloverStrategy.createStrategy( strMaxRollOvers, "1", null, null, null, false, m_config);

    String strRollingLogFilePattern = VwResourceMgr.getString( strKey + "vw.log4j.rollingLogfileFilePattern", (String)null );

    if ( strRollingLogFilePattern == null )
    {
      int nPos = strRollingLogFile.lastIndexOf( '.' );

      if ( nPos > 0 )
      {
        strRollingLogFilePattern = strRollingLogFile.substring( 0, nPos ) + "-%d{yyyy-MM-dd}-%i" + strRollingLogFile.substring( nPos );
      }
      else
      {
        throw new Exception( "Rolling log file name must end with a file extension like .log, .txt etx ...");

      }
    }

    Appender rollingAppender = RollingFileAppender.createAppender( strRollingLogFile, strRollingLogFilePattern, "true", "ROLLING_FILE_APPENDER", "true",
                                                                   "8192", "true", policy, strat, m_instanceLayout, null, "false", "false", null, m_config );

    rollingAppender.start();
    m_config.addAppender( rollingAppender );
    m_loggerConfig.addAppender( rollingAppender, null, null );

  } // end createRollingLogFileAppender()

  /**
   * Creat database appender
   * @throws Exception
   */
  protected void createDatabaseAppender()  throws Exception
  {
     Layout<? extends Serializable> layout;

     if ( m_dbLayout != null )
     {
       layout = m_dbLayout;
     }
     else
     {
       layout = m_instanceLayout;
     }

     Appender dbAppender = VwLogAppenderDbImpl.createAppender( m_strInstanceName, "DbAppender", layout, null );
     dbAppender.start();

     m_config.addAppender( dbAppender );
     m_loggerConfig.addAppender( dbAppender, null, null );

   } // endcreateDatabaseAppender()


  /**
   * Create PWA push notification appender
   * @throws Exception
   */
  protected void creatPwaPushAppender( boolean bEnabled )  throws Exception
  {
    Appender pwaPushAppender = VwLogAppenderPWAPushImpl.createAppender( m_strInstanceName, bEnabled, "PWAPushAppender", m_instanceLayout, null );
    pwaPushAppender.start();

    m_config.addAppender( pwaPushAppender );
    m_loggerConfig.addAppender( pwaPushAppender, null, null );

  }


  /**
   * Clear all logs created with VwLogger
   * @throws Exception
   */
  public static void clearAll() throws Exception
  {
    Set<String> listInstanceNames = getInstanceNames();

    if ( listInstanceNames == null )
    {
      return;
    }

    Collection<VwLogger> loggers = getLoggerInstances();

    if ( loggers == null )
    {
      return;

    }

    for ( VwLogger logger :  loggers )
    {
      logger.clearLog();
    }

  } // end clearAll()


  /**
   *  Clears the log file associated with this logger instance. It sets the file length to zero bytes.
   *  <br/>if  any rolling logs are available(i.e. mylog.log, mylog.log.1, mylog.log.2 ..., then those are deleted
   *
    * @throws Exception if any file io errors occur
   */
  public void clearLog() throws Exception
  {

    Map<String, Appender> mapAppenders = m_config.getAppenders();

    for ( Appender app : mapAppenders.values() )
    {

      // Look for file appenders
      if ( app instanceof FileAppender || app instanceof RollingFileAppender )
      {
        clearFileLog( app );
      }
      else
      if ( app instanceof VwLogAppenderDbImpl )
      {
        clearDatabaseLog( ( VwLogAppenderDbImpl)app );
      }

    } // end for

  } // end clearLog()


  /**
   * Clears all rows in the database log table
   * @param appnDb
   * @throws Exception
   */
  private void clearDatabaseLog( VwLogAppenderDbImpl appnDb ) throws Exception
  {

    appnDb.clearLogTable();


  } // end  clearDatabaseLog()


  /**
   * Clears a file based Appender can be Instance of FileAppender or RollingFIleAppender
   */
  private void clearFileLog( Appender appFile ) throws Exception
  {
    String strFileName = null;

    if ( appFile instanceof FileAppender )
    {
      strFileName = ( (FileAppender) appFile ).getFileName();
    }
    else
    {
      strFileName = ( (RollingFileAppender) appFile ).getFileName();
    }


    int nPos = strFileName.lastIndexOf( '/' );

    String strDirPath = strFileName.substring( 0, nPos );

    File fileDir = new File( strDirPath );
    File[] aLogFiles = fileDir.listFiles();

    if ( aLogFiles == null )
    {
      return;

    }


    for ( int x = 0; x < aLogFiles.length; x++ )
    {
      // If file is an exact match, then truncate it
      if ( aLogFiles[ x ].getAbsolutePath().equals( strFileName ) )
      {
        RandomAccessFile file = new RandomAccessFile( aLogFiles[ x ], "rw" );
        file.setLength( 0 );
        file.close();
      }
      else
      if ( aLogFiles[ x ].getAbsolutePath().startsWith( strFileName ) ) // this is a rolling file extension so delete it
      {
        aLogFiles[ x ].delete();
      }

    } // end for

  }  // end clearFileLog()


} // end class VwLogger{}
