 /*
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

  THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
  PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
  CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

  Source Name:  VwDbMgr.java


 ============================================================================
*/

package com.vozzware.db;

 import com.vozzware.db.util.VwConnectionPool;
 import com.vozzware.db.util.VwConnectionProperty;
 import com.vozzware.db.util.VwDriver;
 import com.vozzware.db.util.VwDriverList;
 import com.vozzware.db.util.VwDriverListReader;
 import com.vozzware.db.util.VwUrl;
 import com.vozzware.util.VwDelimString;
 import com.vozzware.util.VwExString;
 import com.vozzware.util.VwFileUtil;
 import com.vozzware.util.VwLogger;
 import com.vozzware.util.VwResourceStoreFactory;
 import com.vozzware.util.VwStack;
 import com.vozzware.xml.VwDataObject;
 import com.vozzware.xml.VwElement;
 import com.vozzware.xml.VwElementList;
 import com.vozzware.xml.VwXmlFileConfig;

 import javax.naming.InitialContext;
 import javax.sql.DataSource;
 import java.io.File;
 import java.io.FileNotFoundException;
 import java.net.URL;
 import java.net.URLClassLoader;
 import java.sql.Connection;
 import java.sql.DatabaseMetaData;
 import java.sql.Driver;
 import java.sql.DriverManager;
 import java.sql.SQLException;
 import java.sql.Statement;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.Enumeration;
 import java.util.HashMap;
 import java.util.Hashtable;
 import java.util.Iterator;
 import java.util.LinkedList;
 import java.util.List;
 import java.util.Map;
 import java.util.Properties;
 import java.util.ResourceBundle;

 /**
 * Provides a generic interface to database-specific management classes and JDBC connection urls.
 * <br>This class shields the application from having to hardwire in code the JDBC urls and
 * driver manager class names.
 * <br>The application supplies a driver name and a Url Id as defined in the DatasourceDrivers.xml document.
 * 
 */
public class VwDbMgr
{
  private String                m_strDriverName; // The jdbc driver associated with this instance
  private String                m_strJNDIName;   // JNDI for appserver Datasource configurations
  private String                m_strUserID;     // User ID
  private String                m_strUrlId;
  private String                m_strPassword;   // Password
  private String                m_strArchive;
  private String                m_strDriverMgr;  // Name of jdbc driver manager class to load
  private String                m_strJdbcUrl;    // The Jdbc uril for establishing the connection
  private String                m_strDesc;       // Datasource Desc. Optional
  private String                m_strConnTestTable = null;

  private Driver                m_driver;
  
  private VwLogger m_logger;

  private InitialContext        m_ctx;            // used for JNDI Datasource lookups
  
 
  private static Map<String,ClassLoader>         s_mapLoaders = Collections.synchronizedMap( new HashMap<String,ClassLoader>() );
  
   // Load msg bundle
  private static ResourceBundle s_dbMsgs = ResourceBundle.getBundle( "resources.properties.vwdb" );


  private List<VwDatabase>     m_dbList;       // List of VwDatbase instances

  private VwSQLTypeConverterDriver m_SQLTypeCvtr;  // Driver class to convert datatypes
  private boolean               m_fSQLTypeCvtrLoaded = false;     // Flag indication the Driver was loaded.
  private boolean               m_fEnableStatemenmtCaching = true;
  

  private static Hashtable<String,VwDriverTranslationMsgs>  m_htXlateClasses = 
     new Hashtable<String,VwDriverTranslationMsgs>(); // Hash table of translation msg classes

  private String                m_strMsgXlateClass;      // Name of the database product

  private String                m_strDatabaseProdName;   // Database product name
  private String                m_strDatabaseVersion;    // Database product name
  private String                m_strDriverVersion;      // Driver version
  private String                m_strVendorDriverName;   // Driver name
  private String                m_strDataSourceName;   // Driver name
  private String                m_strPoolId;             // Name of connection pool id for this instance (if defined)
  private DatabaseMetaData      m_dbMetaData;            // Metdata class for this driver

  private VwDriverTranslationMsgs m_xlateMsgs;     // Active driver error msg xlation class

  private long                  m_lMaxWaitTime = 10000;  // default value of a 10 second wait time to get a cached connection

  private ClassLoader           m_ldr;
  
  private int                   m_nDatabaseType = -1;    // Will be one of the VwDatabase type constants when determined

  private static Object s_semifore = new Object();

  private static    Map<String,VwConnPool>  s_mapConnPools = Collections.synchronizedMap( new HashMap<String,VwConnPool>() );
  private Connection            m_conSupplied;
  
  private class VwConnPool
  {
    VwStack    m_stackLogins;
    int        m_nNbrMinConnections;
    int        m_nMaxConnections;
    Properties m_propsLogin;
    
  } // end class VwConnPool

  /**
   * Constructs an VwDbMgr object with a user supplied Connection object. Any calls to the login method will return
   * this connection instance
   * 
   * @param conn The user supplied JDBC Connection
   *
   */
  public VwDbMgr( Connection conn, VwLogger logger )
  {
    m_conSupplied = conn;
    m_logger = logger;

    m_dbList = new ArrayList<VwDatabase>();
    
  }

  /**
   * Constructs an VwDbMgr object (XML file version).  This constructor requires a file
   * <br>called "DatasourceDrivers.xml" in the directory where the property VWDOCS System property points
   * <br>to or in the /resources directory in the classpath.
   *
   * @param strDataSourceName  The name driver defined in the DatasourceDrivers.xml to load
   * @param strUrlId           The URL Id as defined in the DatasourceDrivers.xml
   *
   * @exception FileNotFoundException if the XML file does not exist, or the -DVWDOCS property
   * has not been set
   * Exception if an error occurs during construction
   */
  public VwDbMgr( String strDataSourceName, String strUrlId, VwLogger logger ) throws Exception, FileNotFoundException
  { this( strDataSourceName, strUrlId, null, logger ); }
  
 
  /**
   * Constructs an VwDbMgr object (XML file version).  This constructor requires a file
   * <br>called "DatasourceDrivers.xml" in the directory where the property VWDOCS System property points
   * <br>to or in the /resources directory in the classpath.
    *
   * @param strDriverName  The name driver defined in the DatasourceDrivers.xml to load
   * @param strUrlId       The URL Id as defined in the DatasourceDrivers.xml
   * @param strPassword    The password to use when building any connection pools. NOTE this overrides
   * the password defined in the <connectionPool> xml element
   * 
   * @exception FileNotFoundException if the XML file does not exist, or the -DVWDOCS property
   * has not been set
   * Exception if an error occurs during construction
   */
  public VwDbMgr( String strDriverName, String strUrlId, String strPassword, VwLogger logger  ) throws Exception, FileNotFoundException
  {

    m_strPassword = strPassword;

    m_logger = logger;

    if ( m_logger != null )
    {
      m_logger.info( this.getClass(),  "CREATING VwDbMgr FOR DRIVER: " + strDriverName + ", DRIVER URL: " +  strUrlId );
    }

    VwDriver driver = findDriver( null, strDriverName );

    if ( driver == null )
    {
      throw new Exception( VwExString.replace( s_dbMsgs.getString( "Vw.Db.DriverNotFound" ),
                                               "%1", strDriverName ) );
    }

    VwUrl url  = getUrl( driver, strUrlId );
    
    if ( url == null )
    {
      throw new Exception( VwExString.replace( s_dbMsgs.getString( "Vw.Db.NoUrlId" ), "%1", strUrlId ) );
    }


    m_strConnTestTable = driver.getConnectionTestTable();

    m_strUrlId = strUrlId;
    
    // *** Ok now that we have the ini file we have to get the driver manger to load

    String strDriverMgr = driver.getDriverClass();;


    String strMsgXlateClass = driver.getMsgXlateClass();

    // *** Not mandatory but get the description string if supplied

    String strDesc = driver.getDesc();


    m_strArchive = driver.getArchive();
    
    setupLoader();
    
    // Do common initialization

    init( strDriverName, strDriverMgr, url, strMsgXlateClass, strDesc );
    
    if ( url.getPool() != null )
    {
      createConnectionPool( findPool( driver, url ), url.getConnectionProperty() );
    }
    
  } // end VwDbMgr()


  /**
   * Attempt to find a connection pool in the pool list
   * 
   * @param driver
   * @param url
   * @return
   * @throws Exception
   */
  private VwConnectionPool findPool( VwDriver driver, VwUrl url ) throws Exception 
  {
    List listConnectionPool = driver.getConnectionPool();
    
    if ( listConnectionPool == null )
    {
      String strMsg = s_dbMsgs.getString( "Vw.DbMgr.poolNotDefined" );
      strMsg = VwExString.replace( strMsg, "%1", url.getPool() );
      strMsg = VwExString.replace( strMsg, "%2", url.getId() );
      throw new Exception( strMsg );
      
      
    }
    
    for ( Iterator iPools = listConnectionPool.iterator(); iPools.hasNext(); )
    {
      VwConnectionPool pool = (VwConnectionPool)iPools.next();
      
      if ( pool.getId().equalsIgnoreCase( url.getPool() ))
        return pool;
      
    }
    
    String strMsg = s_dbMsgs.getString( "Vw.DbMgr.poolNotDefined" );
    strMsg = VwExString.replace( strMsg, "%1", url.getPool() );
    strMsg = VwExString.replace( strMsg, "%2", url.getId() );
    throw new Exception( strMsg );
  }


  /**
   * Try to find a driver entry in the DatasourceDrivers.xml document
   * 
   * @param listDrivers The List of VwDriver objects to search
   * @param strDriverName The name of the driver to search
   */
  private static VwDriver findDriver( List<VwDriver> listDrivers, String strDriverName ) throws Exception
  {
    
    if ( listDrivers == null )
    {
      listDrivers = loadDrivers();
    }

    if ( listDrivers == null )
    {
      throw new Exception( "Could not find a list of drivers. Make sure you have a DatasourceDrivers.xm. file in your resources/docs path" );
    }

    for ( VwDriver driver : listDrivers  )
    {
       
      if ( driver.getId().equalsIgnoreCase( strDriverName ))
        return driver;
    }
    
    return null;
  }

  private static List<VwDriver> loadDrivers() throws Exception
  { return loadDrivers( null ); }
  
  private static List<VwDriver> loadDrivers( String strDriversPath ) throws Exception
  {
    URL urlDrivers = null;
    
    if ( strDriversPath != null || System.getProperty( "VWDOCS" ) != null )
    {
      if ( strDriversPath  == null )
      {
        strDriversPath = System.getProperty( "VWDOCS" );
      }
      if ( !strDriversPath.endsWith( "/" ) &&  !strDriversPath.endsWith( "\\"))
      {
        strDriversPath += "/";
      }

      File fileDriver = new File (strDriversPath + "DatasourceDrivers.xml" );
      urlDrivers = fileDriver.toURL();
    }
    else
    {
      urlDrivers = VwResourceStoreFactory.getInstance().getStore().getDocument( "DatasourceDrivers.xml" );
    }

    if ( urlDrivers == null )
    {
      throw new Exception( s_dbMsgs.getString( "Vw.DbMgr.noDriversFile" ) );
    }

    VwDriverList dl = VwDriverListReader.read( urlDrivers );
    
    List<VwDriver> listDrivers = dl.getDriver();
    
    if ( listDrivers == null )
    {
      return null;
    }
    
    return listDrivers;
    
  } // end loadDrivers()


  /**
   * Setup class loader if the Archive xml element was specified, else just assume needed archives
   * were specified in the classpath
   * @throws Exception
   */
  private void setupLoader() throws Exception
  {
    
    if ( m_strArchive == null )
    {
      m_ldr = this.getClass().getClassLoader();
      return;
      
    }
    
    m_ldr = s_mapLoaders.get( m_strArchive );
    
    if ( m_ldr != null )
    {
      return; // Already have one
    }
    
    VwDelimString dlms = new VwDelimString( ";", m_strArchive );
    
    String[] astrArchives = dlms.toStringArray();
    
    URL[] aUrls = new URL[ astrArchives.length ];
    
    for ( int x = 0; x < aUrls.length; x++ )
    {
      aUrls[x ] = new URL( "jar:file:" + astrArchives[ x ] + "!/" );
    }
    
    ClassLoader sysl = this.getClass().getClassLoader();
    
    m_ldr = new URLClassLoader( aUrls, sysl );
    
    s_mapLoaders.put( m_strArchive, m_ldr );
    
  } // end setupLoader()


  /**
   * Returns the enabled/disables jdbc statement caching. Statement caching is enabled by default.
   * @return
   */
  public boolean isStatementCachingEnabled()
  { return m_fEnableStatemenmtCaching; }
  
  
  /**
   * Enables/Disables statement caching
   * @param fEnableStatemenmtCaching
   */
  public void setEnabledStatementCaching( boolean fEnableStatemenmtCaching )
  { m_fEnableStatemenmtCaching = fEnableStatemenmtCaching; }
  
  
  /**
   * Returns a comma separated string of all jars and or zips defined for the JDBC driver support
   * for this databse instance for this JDBC vendor. 
   * @return
   */
  public String getArchive()
  { return m_strArchive; }
  
  
  /**
   * Constructs an VwDbMgr object with the specified parameters
   *
   * @param strDriverName  The driver named defined in the DatasourceDrivers.xml file
   * @param strDriverMgr  The JDBC driver class to load
   * @param strJdbcUrl  The JDBC connection url
   * @param strMsgXlateClass The name of the db message translation class
   * @param strDesc  The description of the datasource (may be null)
   *
   * @exception Exception if an error occurs during construction
   *
   */
  public VwDbMgr( String strDriverName, String strDriverMgr, String strJdbcUrl,
                  String strMsgXlateClass, String strDesc )
    throws Exception
  { 
    VwUrl url = new VwUrl();
    url.setTarget( strJdbcUrl );
    
    init( strDriverName, strDriverMgr, url, strMsgXlateClass, strDesc );
    
  }


  /**
   * Initializes the VwDbMgr object with the specified parameters
   *
   * @param strDataSourceName  The Datasource named defined in the DatasourceDrivers.xml file
   * @param strDriverMgr  The JDBC driver class to load
   * @param url  url property
   * @param strMsgXlateClass  The name of the db message translation class
   * @param strDesc  The description of the datasource (may be null)
   *
   * @exception Exception if an error occurs during construction
   *
   */
  private void init( String strDataSourceName, String strDriverMgr, VwUrl url,
                     String strMsgXlateClass, String strDesc ) throws Exception
  {

    if ( url.getTarget().startsWith( "jndi:"))
    {
      m_strJNDIName = url.getTarget().substring( 5 );
      
      m_ctx = new InitialContext();
      
    }
    else
    {
      m_strDataSourceName = strDataSourceName;

      m_strJdbcUrl = url.getTarget();

      if ( m_strDataSourceName == null )
      {
        throw new Exception( s_dbMsgs.getString( "Vw.Db.DriverNotFound" ) );
      }
      
      m_strDriverMgr = strDriverMgr;

      if ( m_strDriverMgr == null )
      {
        throw new Exception( s_dbMsgs.getString( "Vw.Db.Missing.Driver" ) );
      }
      
      // *** Now load the driver manager and try to connect to the data source

      Class clsDriver = Class.forName( m_strDriverMgr, true, m_ldr );
      m_driver = (Driver)clsDriver.newInstance();

      
    }
    
    m_dbList = new ArrayList<VwDatabase>();

    m_strMsgXlateClass = strMsgXlateClass;

    if ( m_strMsgXlateClass == null )
    {
      m_strMsgXlateClass = getXlateMsgBasedOnDriver();
    }

    // if a DbProductName is specifed and we don't have a translation class yet create one

    if ( m_strMsgXlateClass != null )
    {
       // See if we alreay have a msg translation class for this database type

       m_xlateMsgs =  m_htXlateClasses.get( m_strMsgXlateClass );

      Class clsXlateMsgs = null;

      if ( m_xlateMsgs == null )    // If null, create the instance
      {
        try
        {
          clsXlateMsgs = Class.forName( m_strMsgXlateClass, true, m_ldr );
          m_xlateMsgs = (VwDriverTranslationMsgs)clsXlateMsgs.newInstance();
          m_htXlateClasses.put( m_strMsgXlateClass, m_xlateMsgs );
        }
        catch( ClassNotFoundException cnf )
        {
          String strMsg = s_dbMsgs.getString( "Vw.Db.NoMsgClass" );
          strMsg = VwExString.replace( strMsg, "MSGCLASS", m_strMsgXlateClass );
          throw new Exception( strMsg );
        }

      } //end if ( m_xlateMsgs == null )

    } // end if ( m_strMsgXlateClass != null )

    // *** Not mandatory but get the description string if supplied

    m_strDesc = strDesc;

     
  } // end VwDbMgr()


  /**
   * Returns one of the VwDatabase statoc constants of the database type
   * @return an int representing the database type
   */
  public int getDatabaseType()
  { return m_nDatabaseType; }
  
  /**
   * Puts back a databse connection to the pool
   * @param db
   */
  public void putBackDatabase( VwDatabase db )
  { 
    VwConnPool connPool = (VwConnPool)s_mapConnPools.get( m_strPoolId.toLowerCase() );
    connPool.m_stackLogins.push( db );

    if ( m_logger != null && m_logger.isDebugEnabled()  )
    {

      m_logger.debug( this.getClass(), "PUTTING BACK DB CONNECTION TO POOL -- NEW COUNT IS: " + connPool.m_stackLogins.size()  );
    }

   }
  
  /**
   * Get a pooled VwDatabase object (i.e a jdbc Connection)
   * @return
   * @throws Exception
   */
  public VwDatabase getPooledDatabase() throws Exception
  {

    if ( m_logger != null && m_logger.isDebugEnabled()  )
    {
      m_logger.debug( this.getClass(), "Entering  getPooledDatabase()" );
    }

    VwConnPool connPool = s_mapConnPools.get(  m_strPoolId.toLowerCase() );

    if ( connPool == null )
    {
      throw new Exception( "No connection pool exists. Use createConnectionPool to first setup the pool" );
    }

    VwDatabase db = null;

    // see if pool is allowed to grow when empty
    if ( connPool.m_stackLogins.size() == 0 && connPool.m_nNbrMinConnections < connPool.m_nMaxConnections )
    {

      synchronized( s_semifore )
      {

        Connection con = m_driver.connect( m_strJdbcUrl, connPool.m_propsLogin );
        ++connPool.m_nNbrMinConnections;

        if ( m_logger != null && m_logger.isDebugEnabled()  )
        {
          m_logger.debug( this.getClass(), "POOL SIZE MIN CONNECTIONS REACHED"  );
          m_logger.debug( this.getClass(), "CREATING NEW DB CONNECTION ALLOCATION: " + connPool.m_nNbrMinConnections + " of max: " + connPool.m_nMaxConnections );
        }

      }

      db = login( connPool.m_propsLogin );
    }
    else
    {
      db = (VwDatabase) connPool.m_stackLogins.pop( m_lMaxWaitTime );

      if ( m_logger != null && m_logger.isDebugEnabled() )
      {
        m_logger.debug( this.getClass(), "GOT DB CONNECTION FROM POOL STACK, POOL NEW POOL COUNT IS: " + connPool.m_stackLogins.size() );

      }

    }

    long lElapsedTime = 0;

    // Stay in connection loop until we get a good test for max time has been exceeded
    while ( true )
    {
      if ( m_logger != null && m_logger.isDebugEnabled() )
      {
        m_logger.debug( this.getClass(), "TESTING POOLED CONNECTION VALID STATE");

      }

      try
      {
        db.setAutoCommitMode( true );
        Statement stmt = db.getConnection().createStatement();
        stmt.execute( "select 1"  );
        stmt.close();
        break;

      }
      catch ( Exception ex )
      {

        if ( m_logger != null  )
        {
          m_logger.error( this.getClass(), "GOT EXCEPTION TESTING FOR VALID CONNECTION Trying to re-create Connection : " + ex.toString() );

        }

        try
        {
          db = createNewConnection( connPool );
          db.setAutoCommitMode( true );
          break;

        }
        catch( Exception exInner )
        {

          if ( lElapsedTime > 300000 ) // 5 Minutes
          {
            if ( m_logger != null  )
            {
              m_logger.error( this.getClass(), "GOT EXCEPTION TESTING FOR VALID CONNRCTION Trying to re-create Connection : " + ex.toString() );

            }

            throw  new Exception( "DATABASE NOT AVAILABE MAX RETRIES EXCEEDED");

          }

          if ( m_logger != null  )
          {
            m_logger.error( this.getClass(), "RECREATING CONNECTION FAILED, RE_TRYING: " + ex.toString() );
          }

          Thread.sleep( 10000 );  // Try again in 10 seconds

          lElapsedTime += 10000;
          continue;

        } // catch Exception inner

      } // end catch Exception ex

    } // end while()


    return db;
      

  } // end getPooledDatabase()


  /**
   * Get the statck trace starting one past the calling methos
   * @return
   */
  private String getStackTrace()
  {
    if ( m_logger.getDebugVerboseLevel() < 3 )
    {
      return "";
    }

    StringBuffer sb = new StringBuffer(  );

    StackTraceElement[] stElements = Thread.currentThread().getStackTrace();

    int nStackElementsToGet = 0;
    for ( int x = 6, nLen = stElements.length; x < nLen; x++  )
    {

      String strClassName = stElements[ x ].getClassName();

      if ( strClassName.equals( "sun.reflect.NativeMethodAccessorImpl" )  ||
         strClassName.equals( "com.ai.aiweb.servlets.AiAppLandingServlet" ) )
      {
         break;

      }

      if ( ++nStackElementsToGet == 6 )
      {
        break;

      }


      sb.append( stElements[ x ] ).append( "\n" );

    }

    return sb.toString();
  }

  private VwDatabase createNewConnection( VwConnPool connPool )  throws Exception
  {
    if ( m_logger != null  )
     {
       m_logger.error( this.getClass(), "FOUND STALE POOL CONNECTION, RE-CREATING DB POOL ENTRY" );

     }

     // assume this is due to a stale connection, try and rebuild pool
     VwDatabase db = login(connPool.m_propsLogin );
     return db;

  }

  /**
   * Create a database connection
   *
   * @param propsLogin
   * @return
   * @throws Exception
   */
  private VwDatabase login( Properties propsLogin ) throws Exception
  {
    try
    {

      Connection con = null;

      if ( m_ctx != null )
      {
        DataSource ds = (DataSource) m_ctx.lookup( m_strJNDIName );
        con = ds.getConnection( m_strUserID, m_strPassword );

      }
      else
      {

        con = m_driver.connect( m_strJdbcUrl, propsLogin );

      }

      return setup( con );

    }
    catch( SQLException sqle )
    {
      if ( m_logger != null )
      {
        m_logger.error( this.getClass(), "ERROR CREATING DATABASE CONNECTION, REASON:" + sqle.getMessage(), sqle );
      }

      handleException( sqle );
      return null;

    } //end catch

  } // end login


  /**
   * Creates a database connection pool. i.e. a pool of VwDatabase objects. Each VwDatabase object
   * represents a JDBC Connection
   * 
   * @param pool The connection pool properties
   * 
   * @throws Exception
   */
  private  void createConnectionPool( VwConnectionPool pool, List<VwConnectionProperty>listConnectionProperties ) throws Exception
  {
    m_strPoolId = pool.getId();
   
    synchronized( s_mapConnPools )
    {
      VwConnPool connPool = (VwConnPool)s_mapConnPools.get(  pool.getId().toLowerCase() );
      
      if ( connPool != null )
      {
        return;
      }

      connPool = new VwConnPool();
      
      connPool.m_stackLogins = new VwStack();
      connPool.m_nNbrMinConnections = 0;
      connPool.m_nMaxConnections = 0;
      s_mapConnPools.put( pool.getId().toLowerCase(), connPool );
      
      int nPoolMinSize = Integer.parseInt( pool.getMin() );
      connPool.m_nNbrMinConnections = nPoolMinSize;
      
      if ( pool.getMax() != null )
      {
        connPool.m_nMaxConnections = Integer.parseInt( pool.getMax() );
      }

      if ( m_logger != null )
      {
        String strInfoMsg = "STARTING CREATION OF CONNECTION POOL WITH " + connPool.m_nNbrMinConnections;

        if ( connPool.m_nMaxConnections > 0  )
        {
          strInfoMsg +=", POOL MAX CONNECTION SIZE Is: " + connPool.m_nMaxConnections ;
        }

        m_logger.info( this.getClass(), strInfoMsg );
      }

      m_strUserID = pool.getUid();
      
      if ( m_strPassword == null )
      {
        m_strPassword = pool.getPwd();
      }

      if ( m_strPassword != null && m_strPassword.startsWith( "file:" ))
      {
        m_strPassword = getPasswordInFile( m_strPassword );

      }

      connPool.m_propsLogin = new Properties();
      connPool.m_propsLogin.put( "user", m_strUserID );
      connPool.m_propsLogin.put( "password",  m_strPassword );

      if ( listConnectionProperties != null )
      {
        for( VwConnectionProperty connProp : listConnectionProperties )
        {
          connPool.m_propsLogin.put( connProp.getName(), connProp.getValue() );

        }

      }

      if ( m_logger != null )
      {
        m_logger.info( this.getClass(),   "USING CONNECTION PROPERTIES: " + connPool.m_propsLogin.toString() );
      }

      for ( int x = 0; x < nPoolMinSize; x++ )
      {
        Connection con = m_driver.connect( m_strJdbcUrl, connPool.m_propsLogin );
        connPool.m_stackLogins.push( setup( con ) );
        
      } // end for()


      if ( m_logger != null )
      {

        String strMsg = "CONNECT POOL CREATED WITH: " + connPool.m_stackLogins.size() + " ENTRIES";

        if ( connPool.m_nMaxConnections > 0 )
        {
          strMsg += ", MAX SIZE ENTRIES: " + connPool.m_nMaxConnections;
        }

        m_logger.info( this.getClass(), strMsg );
      }
    } // edn synchronized( s_mapConnPool )
    
  } // end setPoolSize()

  /**
   * Gets the password from a file
   *
   * @param strPasswordPath
   * @return
   */
  private String getPasswordInFile( String strPasswordPath ) throws Exception
  {

    String strPath = strPasswordPath.substring( strPasswordPath.indexOf( ":") + 1 );

    File filePassword  = new File( VwExString.expandMacro( strPath ) );

    if ( !filePassword.exists() )
    {
      throw new Exception ("FIle Password: " + strPath + " does not exist or does not have proper access privliges");
    }

    String strPassword = VwFileUtil.readFile( filePassword );

    strPassword = VwExString.stripWhitespace( strPassword );

    return strPassword;
  }


  /**
   * Release All allocated connection pools
   * @throws Exception
   */
  public static void releaseAllPools() throws Exception
  {
    synchronized (s_mapConnPools )
    {
      for ( VwConnPool connPool : s_mapConnPools.values() )
      {
        freePool( connPool.m_stackLogins );
      }
      
      s_mapConnPools.clear();
    
    } // end synchronized
  }
  
  /**
   * Release a specific named pool
   * @param strPoolId The pool id to release
   * @throws Exception
   */
  public static void releasePool( String strPoolId ) throws Exception
  {
    synchronized (s_mapConnPools )
    {
      VwConnPool connPool = s_mapConnPools.get( strPoolId.toLowerCase() );
      
      if ( connPool != null )
      {
        freePool( connPool.m_stackLogins );
        s_mapConnPools.remove( strPoolId.toLowerCase() );
      }
    }
  }
  
  

  /**
   * Release the jdbc connections
   * @param stackLogins
   * @throws Exception
   */
  private static void freePool( VwStack stackLogins ) throws Exception
  {
    // 
    if ( stackLogins == null )
    {
      return;
    }
    
    VwDatabase db = null;
    
    while( true )
    {
      db = (VwDatabase)stackLogins.pop();
      
      if ( db == null )
      {
        break;
      }
      
      try
      {
        db.closeConn();
      }
      catch( Exception ex )
      { ; }
      
    }
    
  } // end freePool()


  /**
   * Parses the DatasourceDrivers.xml file
   *
   * @return The parent VwDataObject containg the parsed entries in the DatasourceDrivers.xml file
   *
   * @exception Exception if the -DVWDOCS property has not been set or there is not
   * a valid DatasourceDrivers.xml found in the classpath
   *
   */
  public static List getDriversFromXml() throws Exception
  { return getDriversFromXml( null ); }
  
  
  /**
   * Parses the DatasourceDrivers.xml file
   *
   * @return The parent VwDataObject containg the parsed entries in the DatasourceDrivers.xml file
   *
   * @exception Exception if the -DVWDOCS property has not been set or there is not
   * a valid DatasourceDrivers.xml found in the classpath
   *
   */
  public static List<VwDriver> getDriversFromXml( String strPath ) throws Exception
  { 
    // Get the doc path directory

    return loadDrivers( strPath );

  } // end getDriversFromXml()


  public static List<VwDriver> getDriverList() throws Exception
  { return getDriverList( null ); }
  
  /**
   * Gets the list of available driver names found in the DatasourceDrivers.xml file
   *
   */
  public static List<VwDriver> getDriverList( String strPath ) throws Exception
  {
    List<VwDriver> listDrivers = getDriversFromXml( strPath );

    return listDrivers;
  } // end getDriverList()



  public String getConnTestTable()
  {
    return m_strConnTestTable;

  }

  /**
   * Gets the list of defined connection pools for a driver
   *
   * @param strDriver The name of the driver to obtain its connection pools
   */
  public static Map getConnectionPoolMap( String strDriver ) throws Exception
  {
    VwDriver driver = findDriver( null, strDriver );

    if ( driver == null )
    {
      return null;
    }

    List<VwConnectionPool>  listConnectionPools = driver.getConnectionPool();
    
    if ( listConnectionPools == null )
    {
      return null;
    }

    Map<String,VwConnectionPool> mapConnectionPools = new HashMap<String,VwConnectionPool>();

    for ( VwConnectionPool pool : listConnectionPools  )
    {
      mapConnectionPools.put( pool.getId(), pool );
    }

 
    return mapConnectionPools;

  } // end get getUrlMap()

  /**
   * Gets the list of defined url's for a driver
   *
   * @param strDriver The name of the driver to obtain its url list
   */
  public static Map<String,String> getUrlMap( List<VwDriver> listDrivers, String strDriver ) throws Exception
  {
    VwDriver driver = findDriver( listDrivers, strDriver );

    if ( driver == null )
    {
      return null;
    }

    List<VwUrl> listUrls = driver.getUrl();
    
    if ( listUrls == null )
    {
      throw new Exception( s_dbMsgs.getString( "Vw.Db.Missing.Url" ) );
    }

    Map<String,String> mapUrls = new HashMap<String,String>();

    for ( VwUrl url : listUrls )
    {
      mapUrls.put( url.getId(), url.getTarget() );
    }
 
    return mapUrls;

  } // end get getUrlMap()

  /**
   * Returns an VwDatabject with the driver info
   *
   * @param strDriverName The name of the driver to retrieve
   *
   * @return An VwDataObject with the driver info or null if the driver is not defined
   * in the DatasourceDrivers.xml file
   */
  public static VwDriver getDriver( String strDriverName ) throws Exception
  { return findDriver( null, strDriverName );  } 

  /**
   * Extracts the specific URL based on the url id
   *
   * @param driver The data object containing the driver data
   * @param strUrlId The id of the URL entry to extract
   */
  public static VwUrl getUrl( VwDriver driver, String strUrlId ) throws Exception
  {
    List listUrls = driver.getUrl();
    
    if ( listUrls == null )
    {
      return null;
    }

    for ( Iterator iUrls = listUrls.iterator(); iUrls.hasNext(); )
    {
      VwUrl url = (VwUrl)iUrls.next();
      if ( url.getId().equalsIgnoreCase( strUrlId ))
      {
        return url;
      }
    }

    return null;

  } // end getUrl()


  /**
   * Returns a List of data source names that are defined in the DatasourceDrivers.xml file
   * 
   * String strDocRoot the classpath root to where the DataSourceDrivers.xml resides (may be null)
   * @return
   * @throws Exception
   */
  public static List getDataSourceMappings() throws Exception
  {
    List listMappings = new LinkedList();
    List listDrivers = loadDrivers();
    
    for ( Iterator iDrivers = listDrivers.iterator(); iDrivers.hasNext();  )
    {
      VwDriver driver = (VwDriver)iDrivers.next();

      String strDsourceName = driver.getId();
      listMappings.add( strDsourceName );

    }
    
    return listMappings;
    
  } // end getDataSourceMappings()

 
  
  /**
   * Returns a Map of driver data objects parsed from the the DatasourceDrivers.xml file and
   * the DatasourceMap.xml
   *
   */
  public static Map getDriverMap( String strUrlId) throws Exception
  {
    Map map = new HashMap();

    VwDataObject dobjDsources = VwXmlFileConfig.get( "DatasourceMap.xml", true );
    List listDrivers =  loadDrivers();

    VwElementList listDsources = null;

    Object objDsources = dobjDsources.getObject( "Datasource" );

    if ( objDsources == null )
    {
      throw new Exception( s_dbMsgs.getString( "Vw.Db.MissingDatasourceMap" ) );
    }

    if ( objDsources instanceof VwElement )
    {
      listDsources = new VwElementList();
      listDsources.add( objDsources );
    }
    else
    {
      listDsources = (VwElementList) objDsources;
    }

    for ( int x = 0; x < listDsources.size(); x++ )
    {

      VwElement eleDsource = listDsources.getElement( x );

      String strDsourceName = eleDsource.getAttribute( "ID" );

      if ( strDsourceName == null )
      {
        throw new Exception( s_dbMsgs.getString( "Vw.Db.MissingDatasourceId" ) );
      }

      VwDriver driver = findDriver( listDrivers, eleDsource.getValue());

      if ( driver  == null )
      {
        throw new Exception( VwExString.replace( s_dbMsgs.getString( "Vw.Db.DriverNotFound" ), "%1", eleDsource.getValue() ) );
      }

      VwDataObject dobjDriver = new VwDataObject( driver.getId() );
      
      String strID = strUrlId + eleDsource.getAttribute( "ID");
      VwUrl url = VwDbMgr.getUrl( driver, strID );

      dobjDriver.put( "ConnectUrl", url.getTarget() );

      map.put( strDsourceName.toLowerCase(), dobjDriver );

    } // end for()

    return map;

  } // end getDriverMap()



  /**
   * Gets the driver translation message class for this datasource
   *
   * @return The VwDriverTranslationMsgs class for this datasource, or null
   * if none was sepecified.
   */
  public VwDriverTranslationMsgs getDriverTranslationMsgs()
  { return (VwDriverTranslationMsgs)m_htXlateClasses.get( m_strDriverName ); }


  /**
   * Get the VwSQLTypeConverterDriver class name using the driver name and
   * database product name information.
   *
   * @param con Connection to a database
   *
   * @return The class name for the driver
   *
   */
  public final String getSQLTypeConverterDriverName( Connection con ) throws Exception
  {
    DatabaseMetaData md = con.getMetaData();
    
    String strDriverName = null;
    String strDriverTypeComponent = "jdbc_";
    String strDatabaseTypeComponent = "";

    String strDriverClass = "";
    String strDatabaseType = "";

    strDriverClass = getDriverClassName();

    try
    {
      m_strDatabaseProdName = md.getDatabaseProductName();
      m_strDatabaseVersion = md.getDatabaseProductVersion();
      m_strDriverName = md.getDriverName();
      m_strDriverVersion = md.getDriverVersion();

    }
    catch( Exception e )
    {
      return strDriverName;
    }

    if ( strDriverClass != null && strDriverClass.indexOf( "odbc" ) >= 0 )
    {
      strDriverTypeComponent = new String( "odbc_" );
    }

    strDatabaseTypeComponent = m_strDatabaseProdName.trim();
    strDatabaseTypeComponent = strDatabaseTypeComponent.replace(' ', '_' );
    strDatabaseTypeComponent = strDatabaseTypeComponent.replace('/', '_' );
    strDatabaseTypeComponent = strDatabaseTypeComponent.replace('\\', '_' );

    strDriverName = "SQLTypeDriver.SQLConvert_" + strDriverTypeComponent + strDatabaseTypeComponent;

    return strDriverName;

  } // End of getSQLTypeConverterDriverName()


  /**
   * Get the VwSQLTypeConverterDriver class
   *
   * @param con Connection to a database
   *
   * @return The type converter class for the driver, or null if no driver found
   *
   */
  public final VwSQLTypeConverterDriver getSQLTypeConverterDriver( Connection con ) throws Exception 
  {
    VwSQLTypeConverterDriver tempDriver = null;

    // Compute the VwSQLTypeConverterDriver Name

    String strDriverName = getSQLTypeConverterDriverName( con );

    if ( strDriverName == null )
    {
      return null;
    }
    
    try
    {

      Class object = Class.forName( strDriverName, true, m_ldr );

      Class classes[] =  object.getInterfaces();

      for( int i = 0; i < classes.length; i++ )
      {

        if ( classes[i].getName().indexOf( "VwSQLTypeConverterDriver" ) != -1 )
        {
           tempDriver = (VwSQLTypeConverterDriver)object.newInstance();
           break;
        }

      } // end for

    }
    catch ( Exception e )
    {
      ; // just means we don't have a driver for this class
    }

    return tempDriver;


  } //end public final String getSQLTypeConverterDriver()


  /**
   * Logs a user into the database
   *
   * @param strUID - The user ID account defined in the database
   * @param strPWD - The password for this user ID account
   *
   * @return The VwDatabase instance if the login is successful
   *
   * @exception VwDbServerNotAvailException if the database server machine is down or not responding
   * VwDbInvalidSessionException If a cached login is no longer valid,
   * VwDbInvalidUidPwdException if the userid/password is invalid,
   * SQLException for all other errors
   */
  public final VwDatabase login( String strUID, String strPWD ) throws Exception
  {
    m_strUserID = strUID;
    m_strPassword = strPWD;

    if ( m_logger != null && m_logger.isDebugEnabled() )
    {
      m_logger.debug( this.getClass(), "CREATING DATABASE CONNECTION");

    }

    if ( m_strPassword != null && m_strPassword.startsWith( "file:" ))
    {
      m_strPassword = getPasswordInFile( m_strPassword );
    }

    if ( m_strUserID == null )         // Alllow for data sources not requiring a a user id
    {
      m_strUserID = "";                // or password
    }

    if ( m_strPassword == null )
    {
      m_strPassword = "";
    }

    try
    {

      Connection con = null;
      
      if ( m_ctx != null )
      {
        DataSource ds = (DataSource)m_ctx.lookup( m_strJNDIName );
        con = ds.getConnection(m_strUserID, m_strPassword );
        
      }
      else
      {
        Properties props = new Properties();
        props.put( "user", m_strUserID );
        props.put( "password",  m_strPassword );
 
        con = m_driver.connect( m_strJdbcUrl, props );
        
      }
      
      return setup( con );

    } //end try
    catch( SQLException sqle )
    {
      if ( m_logger != null )
      {
        m_logger.error( this.getClass(), "ERROR CREATING DATABASE CONNECTION, REASON:" + sqle.getMessage(), sqle );
      }

      handleException( sqle );
      return null;

    } //end catch

  } // end login()



  /**
   * Logs a user into the database. This method is commonly used
   * for Jdbc vendors implementing connection pools where the user id and password do
   * not apply.
   *
   * @return The VwDatabase instance if the login is successful
   *
   * @exception VwDbServerNotAvailException if the database server machine is down or not responding
   * VwDbInvalidSessionException If a cached login is no longer valid,
   * VwDbInvalidUidPwdException if the userid/password is invalid,
   * SQLException for all other errors
   */
  public final VwDatabase login() throws Exception
  {

    m_strUserID = "";
    m_strPassword = "";

    try
    {
      Connection con = null;
      
      if ( m_conSupplied != null )
      {
        con = m_conSupplied;
      }
      else
      if ( m_ctx != null )
      {
        DataSource ds = (DataSource)m_ctx.lookup( m_strJNDIName );
        con = ds.getConnection();
        
      }
      else
      if ( m_strPoolId != null )
      {
        return getPooledDatabase();
      }
      else
      {
        con = DriverManager.getConnection( m_strJdbcUrl, new Properties() );
      }

      VwDatabase db =  setup( con );
      db.setAutoCommitMode( true );

      return db;

    } //end try
    catch( SQLException sqle )
    {
      if ( m_logger != null )
      {
        m_logger.error( this.getClass(), "ERROR CREATING DATABASE CONNECTION, REASON:" + sqle.getMessage(), sqle );
      }

      handleException( sqle );
      return null;

    } //end catch

  } // end login()


  /**
   * Does final setup with the JDBC Connection class
   *
   * @param con The valid database Connection
   *
   * @return The VwDatabase instance
   */
  private VwDatabase setup( Connection con ) throws Exception
  {
    con.setTransactionIsolation( Connection.TRANSACTION_READ_COMMITTED );

    m_dbMetaData = con.getMetaData();
    m_strDriverName = m_dbMetaData.getDriverName();
    
    if ( m_nDatabaseType < 0 )
    {
      determineDatabaseType();
    }
      
    // Attempt to load the SQLType Converter Class for this driver/database
    // if we haven't already tried

    if (  m_fSQLTypeCvtrLoaded == false )
    {
      m_fSQLTypeCvtrLoaded = true;
      m_SQLTypeCvtr = getSQLTypeConverterDriver( con );
    }

    // *** If we get here all is well, create a new instance of the database connection

    VwDatabase db = new VwDatabase( this, con, s_dbMsgs, m_SQLTypeCvtr );

         
    // *** Add Instance to the linked list

    m_dbList.add( db );

    return db;

  } // end setup{}


  /**
   * Trys to determine the database vendor type from the productname string and set the known constant
   * int which can be used for functionality based on the database type
   */
  private void determineDatabaseType() throws Exception 
  {
    String strDatabase = m_dbMetaData.getDatabaseProductName().toLowerCase();
    
    if ( strDatabase.indexOf( "oracle") >= 0 )
    {
      m_nDatabaseType = VwDatabase.ORACLE;
    }
    else
    if ( strDatabase.indexOf( "postgres") >= 0 )
    {
      m_nDatabaseType = VwDatabase.POSTGRESQL;
    }
    else
    if ( strDatabase.indexOf( "mysql") >= 0 )
    {
      m_nDatabaseType = VwDatabase.MYSQL;
    }
    else
    if ( strDatabase.indexOf( "db2") >= 0 || strDatabase.indexOf( "udb") >= 0)
    {
      m_nDatabaseType = VwDatabase.UDB;
    }
    else
    if ( strDatabase.indexOf( "sqlserver") >= 0 )
    {
      if ( strDatabase.indexOf( "microsoft") >= 0 )
      {
        m_nDatabaseType = VwDatabase.MSSQLSERVER;
      }
      else
      {
        m_nDatabaseType = VwDatabase.SQLSERVER;
      }
      
    }
    else  
    if ( strDatabase.indexOf( "derby") >= 0 )
    {
      m_nDatabaseType = VwDatabase.DERBY;
    }
    else
    {
      m_nDatabaseType = VwDatabase.UNKNOWN;
    }

  } // end determineDatabaseType()


  /**
   * Throws the proper translated VwDBXXXXException, or SQLException
   *
   * @param sqle The orignal SQLException thrown
   *
   * @exception  VwDbServerNotAvailException if the database server machine is down or not responding
   * VwDbInvalidSessionException If a cached login is no longer valid,
   * VwDbInvalidUidPwdException if the userid/password is invalid,
   * VwDBDupKeyException if a dup key constraint is violated
   * SQLException for all other errors
   */
   public void handleException( SQLException sqle ) throws VwDbServerNotAvailException,
                                                           VwDbInvalidSessionException,
                                                           VwDbInvalidUidPwdException,
                                                           VwDbDupKeyException,
                                                           SQLException
   {
     if ( m_xlateMsgs != null )
     {

       switch ( m_xlateMsgs.getReason( sqle ) )
       {

         case VwDriverTranslationMsgs.NOT_AVAILABLE:

              throw new VwDbServerNotAvailException( sqle );


         case VwDriverTranslationMsgs.INVALID_SESSION:

              throw new VwDbInvalidSessionException( sqle );


         case VwDriverTranslationMsgs.INVALID_LOGIN:

              throw new VwDbInvalidUidPwdException( sqle );


         case VwDriverTranslationMsgs.DUP_KEY:

              throw new VwDbDupKeyException( sqle );

       } // end switch

     } // end if

     throw sqle;   // For any other reason, or no translation class avai for this data source
                   // re-throw the original exception

   } // end handleException()



  /**
   * Closes the database connection for the specified VwDatabase instance
   *
   * @param db - The VwDatabase instance to be closeed
   *
   * @exception throws SQLException if the connection was already closed
   */
  public final void close( VwDatabase db ) throws Exception
  {
    
    if ( db == null )
    {
      return;
    }
    
    m_dbList.remove( db );
    
    if ( m_strPoolId != null )
    {
      putBackDatabase( db );
      return;
    }

    if ( m_logger != null && m_logger.isDebugEnabled() )
    {
      m_logger.debug( this.getClass(), "CLOSING DATABASE CONNECTION" );
    }

    db.closeConn();

  } // end close()

  
  /**
   * Closes all open VwDatabase objects associated with this datasource
   * @throws SQLException
   */
  public final void close() throws Exception
  {
    Object[] aDbs = m_dbList.toArray();

    for ( int x = 0; x < aDbs.length; x++  )
    {
      VwDatabase db = (VwDatabase)aDbs[ x ];
      
      db.closeConn();
      if ( m_logger != null )
      {
        m_logger.info( this.getClass(), "Closing Database connection: " + db.getDriverName() );
      }
      
    }

    if ( m_logger != null )
    {
      m_logger.info( this.getClass(), "De-registering Database driver: " + m_driver.getClass().getName() );
    }


    Enumeration<Driver> eDrivers = DriverManager.getDrivers();

    while( eDrivers.hasMoreElements() )
    {
      Driver driver = eDrivers.nextElement();
      DriverManager.deregisterDriver(driver );

    }
   } // end close()

  
  /**
   * Removes
   * @param dbToRemove
   */
  protected final void removeDB( VwDatabase dbToRemove ) throws Exception 
  {
    for ( Iterator iDb = m_dbList.iterator(); iDb.hasNext(); )
    {
      VwDatabase db = (VwDatabase)iDb.next();
      
      if ( dbToRemove == db )
      {
        iDb.remove();
      }
    }
  } // end close()
    
  
  /**
   * Gets the name of the connected datasource
   *
   * @returns A string with the datasource name
   */
  public final String getDriverName()
  { return m_strDriverName; }

  public String getDataSourceName()
  { return m_strDataSourceName; }
  
  
  /**
   * Gets the User ID for the current object
   *
   * @return A string with the logon User ID
   */
  public final String getUserID()
  { return m_strUserID; }


  /**
   * Gets the Password for the current object
   *
   * @return - A string with the logon password
   */
   public final String getPassword()
   { return m_strPassword; }


  /**
   * Gets the datasource "Desc" entry in the INI file if one was supplied
   *
   * @return - A string with the datasource Desc entry in the INI file if one was supplied;
   * otherwise, null is returned.
   */
   public final String getDesc()
   { return m_strDesc; }



  /**
   * Gets the "Driver Class Name" entry in the INI file used to handle this datasource
   *
   * @return - A string with the Driver Class Name entry in the INI file used for the datasource
   */
   public final String getDriverClassName()
   { return m_strDriverMgr; }


  /**
   * Returns the "URL JDBC" entry in the INI file used to connect to this datasource
   *
   * @return - A string with the JDBC URL entry in the INI file used to connect to the datasource
   */
   public final String getURL()
   { return m_strJdbcUrl; }


  /**
   * Returns the vendor's product name for the driver
   *
   * @return The vendor's product name for the driver
   */
  public String getVendorDriverName()
  { return m_strVendorDriverName; }


  /**
   * Returns The vendor's version number for the driver
   *
   * @return The vendor's version number for the driver
   */
  public String getDriverVersionNbr()
  { return m_strDriverVersion; }


  /**
   * Returns the vendor's product name for the database
   *
   * @return The vendor's version number for the database
   */
  public String getDatabaseName()
  { return m_strDatabaseProdName; }


  /**
   * Returns the vendor's version number for the database
   *
   * @return The vendor's version number for the driver
   */
  public String getDatabaseVersion()
  { return m_strDatabaseVersion; }

  /**
   * Try to find a known error xlate class based on driver type string based on the JdbcDriver used
   */
  private String getXlateMsgBasedOnDriver()
  {
    String strDriver = getDriverClassName();

    if ( strDriver == null )
    {
      return null;
    }
    
    if ( strDriver.startsWith( "oracle" ) )
    {
      return "com.vozzware.db.VwOraTranslationMsgs";
    }

    return null;

  }


  public String getURLId()
  {
    return m_strUrlId;
  }


 } // end class VwDbMgr

// *** End of VwDbMgr.java ***

