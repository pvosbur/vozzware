/*
============================================================================================
 

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwObjectSQLMapper.java

============================================================================================
*/

package com.vozzware.db;

import com.vozzware.codegen.DataType;
import com.vozzware.codegen.VwCodeOptions;
import com.vozzware.codegen.VwDAOGen;
import com.vozzware.codegen.VwDVOGen;
import com.vozzware.codegen.VwPropertyDefinition;
import com.vozzware.codegen.VwSqlGenerator;
import com.vozzware.db.util.VwConnection;
import com.vozzware.db.util.VwConstraint;
import com.vozzware.db.util.VwDAOProperties;
import com.vozzware.db.util.VwDbConnection;
import com.vozzware.db.util.VwDbObjCommon;
import com.vozzware.db.util.VwDbQuery;
import com.vozzware.db.util.VwDbSchema;
import com.vozzware.db.util.VwExtendsDescriptor;
import com.vozzware.db.util.VwExtendsHierarchyDescriptor;
import com.vozzware.db.util.VwFinder;
import com.vozzware.db.util.VwKeyDescriptor;
import com.vozzware.db.util.VwMappingTableConstraint;
import com.vozzware.db.util.VwObjectProperties;
import com.vozzware.db.util.VwOrm;
import com.vozzware.db.util.VwPrimaryKeyGeneration;
import com.vozzware.db.util.VwProcedure;
import com.vozzware.db.util.VwSql;
import com.vozzware.db.util.VwSqlMapping;
import com.vozzware.db.util.VwSqlMappingDocMgr;
import com.vozzware.db.util.VwSqlMappingDocument;
import com.vozzware.db.util.VwSqlMappingDocumentReader;
import com.vozzware.db.util.VwSqlMappingSpec;
import com.vozzware.db.util.VwSqlMappingSpecReader;
import com.vozzware.db.util.VwSqlStatement;
import com.vozzware.db.util.VwTableSpec;
import com.vozzware.util.VwBeanUtils;
import com.vozzware.util.VwDate;
import com.vozzware.util.VwDelimString;
import com.vozzware.util.VwExString;
import com.vozzware.util.VwFileUtil;
import com.vozzware.util.VwLogger;
import com.vozzware.util.VwResourceStoreFactory;
import com.vozzware.util.VwStack;
import org.apache.logging.log4j.core.Appender;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.ResultSetMetaData;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * This class generates data value objects from columns defined in relational tables or sql query result set columns.
 */
public class VwObjectSQLMapper
{
  private String                          m_strPackage;                     // Name of package that class is in
  private String                          m_strSourcePath;                  // 
  private String                          m_strClassPath;
  private String                          m_strAuthor;                      // Person running code generator
  private String                          m_strDriverId;
  private String                          m_strDriverUrlId;
  private String                          m_strUid;
  private String                          m_strPwd;
  private String                          m_strCompanyName;
  private String                          m_strPrimeKeyPolicy;
  private String                          m_strSeqTableName;
  private String                          m_strSeqColName;
  private String                          m_strSeqName;
  private String                          m_strSqlMappingDoc;
  private String                          m_strExcludeCols;
  private String                          m_strIncludeCols;
  private String                          m_strClassName;
  private String                          m_strTableName;
  private String                          m_strSchema;
  private String                          m_strIndentSpaces;

  private List<VwColInfo>                 m_listPrimaryKeys;
  private List<VwForeignKeyInfo>          m_listForeignKeys;
  private List<VwPrimaryKeyGeneration>    m_listMappingPrimaryKeys;

  private boolean                         m_fOverwriteXsm;
  private boolean                         m_fDAOXMLOnly;
  private boolean                         m_fDVOOnly;
  private boolean                         m_fNoModifyDVOs;
  private boolean                         m_fUseObjectTypes = true;             // If true use Java object types (not primitives) for internal property data types
  private boolean                         m_fUseHungarian = true;               // if true generate data types and parameters using hungarian notation
  private boolean                         m_fUseDirtObjectDetection = false;           // if true 
  private boolean                         m_fGenCode = true;
  private boolean                         m_fArgGenCode = true;
  private boolean                         m_fDocChanges = false;
  private boolean                         m_fHasPrimeKeyGenOverrides = false;

  private int                             m_nMaxLineLength = 80;

  private int                             m_nIndentLength = 22;



  private VwSqlMgr                       m_sqlMgr = null;

  private VwDatabase                     m_db = null;
  
  private VwSqlMappingDocument           m_sqlMappingDoc;                             // Dao documnet holding the out xml definitions
  
  private VwSqlMappingDictionary         m_daoDict;
  
  private VwSqlMappingSpec               m_spec;
  
  private VwLogger m_logger;
  
  private File                           m_fileDaoMappingDoc;
  
  private VwCodeOptions m_codeOpts = new VwCodeOptions();
  
  private Map<String,String>             m_mapPrimeKeysSupplied = new HashMap<String,String>();
  
  private Map<String,VwTableSpec>        m_mapOrmTables = new HashMap<String,VwTableSpec>();
  private Map<String,String>             m_mapOrmSqlByMappingId = new HashMap<>(  );;


  
  // Set up query so we can get a result set meta data
  private StringBuffer                   m_sbSelect;
  private StringBuffer                   m_sbWhere;
  private StringBuffer                   m_sbExists;

  private StringBuffer                   m_sbInsert;
  private StringBuffer                   m_sbUpdate;
  private StringBuffer                   m_sbDelete;
  private StringBuffer                   m_sbProc;
  private StringBuffer                   m_sbTsCheck;
  

  private List<Appender>                 m_listLoggerAppenders = null;
  
  private Map<String,String>             m_mapViews = new HashMap<String,String>();
  private Map<String,List<VwPropertyDefinition>>  m_mapDvoPropdefsByJavaName = new HashMap<String, List<VwPropertyDefinition>>();
  private Map<String,PropertyDescriptor> m_mapExistinfClassPropertyDescriptors;
  private Map<String,List<String>>       m_mapIncludes;
  private Map<String,String>             m_mapProcessedRelatedTables;
  private Map<VwTableRelationship,String>m_mapProcessedParentTables = new HashMap<VwTableRelationship,String>();

  private VwTableRelationship            m_trParent;
  
  private VwDelimString                  m_dlmsOmitColumns = null;
  private boolean                        m_fTreatChar1AsBoolean = false;
  private boolean                        m_fGenDAO;
  private String                         m_strDVOSuperClass;
  private String                         m_strBaseTable;
  private String                         m_strTimestampCheck;

  private VwDbObjCommon                  m_dbCommon;


  /**
   * Constant for the Java primitive type code generation 
   */
  public static final int JAVA_PRIMITIVE_TYPES = 1;

  
  /**
   * Constant for the Java object type code generation 
   */
  public static final int JAVA_OBJECT_TYPES = 2;
  
  
  /**
   * Constant for the Java object type for primary key only code generation 
   */
  public static final int JAVA_OBJECT_TYPE_PRIME_KEY_ONLY = 3;


  public static final int GEN_ALL = 0;
  
  public static final int GEN_DVO_ONLY = 1;

  public static final int GEN_XML_ONLY = 2;



  /**
   * Default constructor
   *
   */
  public VwObjectSQLMapper()
  { ; }

  
  /**
   * Constructor for bypassing the xml m_specification xml document. The indened use here is for GUI feeders instead of
   * XML docuemnts.
   * 
   * @param mappingSpec This class m_specifies the DAO output properties and database connection information
   * @param fNoModifyDVOs don't modify dvos
   * @param nGenType
   */
  public VwObjectSQLMapper( VwSqlMappingSpec mappingSpec, boolean fNoModifyDVOs, int nGenType ) throws Exception
  {
    m_fNoModifyDVOs = fNoModifyDVOs;
    setCodeGenFlags( nGenType );
    
    m_spec = mappingSpec;

  } // end VwObjectSQLMapper()


  /**
   * Parses the xml specification document
   * @param urlFileName url to the file to parser
   * @param fGenDAO    generate a DAO for this spec
   * @param nGenType   gen type dvo only or xsm doc
   * @param strIncludes  objects defined in the xmp spec to include
   * @throws Exception
   */
  public void parse( URL urlFileName,  boolean fGenDAO, int nGenType, boolean fOverWriteXsm, String strIncludes  ) throws Exception
  {

    setCodeGenFlags( nGenType );
    
    m_logger = VwLogger.getInstance();
    m_fGenDAO = fGenDAO;

    m_fOverwriteXsm = fOverWriteXsm;

    if ( strIncludes != null )
    {
      makeIncludesMap( strIncludes );
    }

    m_logger.info( null, "Processing input m_specification from '" + urlFileName.toURI() + "'");
    
    m_spec = VwSqlMappingSpecReader.read( urlFileName );
    
    runMapper();    
 
  } // end parse()


  /**
   * Make map of object types to include when generating output
   * @param strIncludes
   * @throws Exception
   */
  private void makeIncludesMap( String strIncludes ) throws Exception
  {
    VwDelimString dlms = new VwDelimString( strIncludes );
    m_mapIncludes = new HashMap<String, List<String>>();
    
    for ( Iterator<String> ipiece = dlms.iterator(); ipiece.hasNext(); )
    {
      String strPiece = ipiece.next();
      int nPos = strPiece.indexOf( "=" );

      if ( nPos < 0 )
      {
        throw new Exception( "Missing the '=' operator in the includes option " + strPiece + ", must be in the form objectName=Value");
      }
      
      String strObjectName = strPiece.substring( 0, nPos );
      String strValue = strPiece.substring( ++nPos );
      
      List<String>listValues = m_mapIncludes.get( strObjectName );
      
      if ( listValues == null )
      {
        listValues = new ArrayList<String>();
        m_mapIncludes.put( strObjectName, listValues );
      }
      
      listValues.add( strValue );
      
    } // end for()
    
  } // end makeIncludesMap


  public void runMapper() throws Exception
  {
    if ( !setup() )
    {
      return;
    }
    
    loadViewMap();
    
    run();
   
  }
  
  private void loadViewMap() throws Exception
  {
    String[] astrViews = m_db.getViews( null, m_strSchema, null );
    if ( astrViews == null )
    {
      return;
    }
    
    for ( int x = 0; x < astrViews.length; x++ )
    {
      m_mapViews.put( astrViews[ x ].toLowerCase(), null );
    }
    
  }


  public void addAppender( Appender appender )
  {
    if ( m_listLoggerAppenders == null )
    {
      m_listLoggerAppenders = new ArrayList<Appender>();
    }
    
    m_listLoggerAppenders.add( appender );
    
  }

  /**
   * Set the code generation options
   * @param nGenType The code gen type
   */
  private void setCodeGenFlags( int nGenType )
  {
    switch( nGenType )
    {
      case GEN_DVO_ONLY:
        
           m_fDVOOnly = true;
           break;

      case GEN_XML_ONLY:
        
           m_fDAOXMLOnly = true;
           break;
           
    } // end switch()
    
    
  }


  /**
   * Run class and ir sql code generator
   */
  private void run() throws Exception
  {
    m_strIndentSpaces = VwExString.lpad(  "",' ', m_nIndentLength );

    List<VwDbObjCommon> listMappings = m_spec.getSpecMappings();
    
    if ( listMappings == null )
    {
      m_logger.error( null, "No Sql mappings have been defined in m_spec document '" );
      return;
      
    }
    
    // Process all object mappings inn the order defined
    for ( VwDbObjCommon objMapping :  listMappings )
    {
      
      if ( objMapping instanceof VwDbSchema  )
      {
        processSchema( (VwDbSchema)objMapping );
      }
      else
      if ( objMapping instanceof VwOrm  )
      {
        genFromRelationships( (VwOrm)objMapping );
      }
      else
      if ( objMapping instanceof VwTableSpec  )
      {
        VwTableSpec tbl = (VwTableSpec)objMapping;
        
        m_strSchema = ((VwTableSpec)objMapping).getSchema();
        
        String strName = tbl.getName();

        if ( !inIncludeList( "table", strName ))
        {
          m_logger.info( null, "Skipping table entry '" + tbl.getName() + "' because it was not in the include list");
          continue;
          
        }

        int nPos = strName.indexOf( '.' );
        if ( nPos > 0 )
        {
          m_strSchema = strName.substring( 0, nPos );
          strName = strName.substring( ++nPos );
          tbl.setName( strName );
          
        }
        
        
        m_logger.info( null, "" ); // generate blank line
        m_logger.info( null, "Processing table entry '" + tbl.getName() + "'");
        genCode( tbl, null, null, tbl.getFinder() );
      }
      else
      if ( objMapping instanceof VwDbQuery  )
      {
        VwDbQuery query = (VwDbQuery)objMapping;
        if ( !inIncludeList( "query", query.getInheritClassName() ))
        {
          m_logger.info( null, "Skipping query entry '" + query.getInheritClassName()  + "' because it was not in the include list");
          continue;
          
        }

        m_logger.info( null, "" ); // generate blank line
        m_logger.info( null, "Processing query for id: '" + query.getId() + "'" );
        genCode( query, null, null, query.getFinder() );
      }
      else
      if ( objMapping instanceof VwProcedure  )
      {
        VwProcedure proc = (VwProcedure)objMapping;
        
        if ( !inIncludeList( "proc", proc.getName() ))
        {
          m_logger.info( null, "Skipping procedure entry '" + proc.getName()  + "' because it was not in the include list");
          continue;
          
        }
        
        m_logger.info( null, "" ); // generate blank line
        m_logger.info( null, "Processing stored procedure '" + proc.getName() + "' for class '" + proc.getInheritClassName() + "'" );
        genCode( proc, null, null, null );
      }
      else
      {
        m_logger.error( null, "Unrecognized Mapping Object '" + objMapping.getClass().getName() + "'" );
        
      }
    
    }
    
    if ( m_fGenDAO )
    {
      generateDAO();
    }
    
    if ( m_fDVOOnly )
    {
      return;
    }
    
    writeXSMDocument();
    
    
  }


  /**
   * Write the SqlMapping (.xsm) Document
   * @throws Exception
   */
  private void writeXSMDocument() throws Exception
  {
    if ( !m_fDocChanges )
    {
      m_logger.info( null, "No changes made to " + m_strSqlMappingDoc);
      
      return;
    }
    
    
    if ( File.separatorChar == '\\')
    {
      m_strSqlMappingDoc = m_strSqlMappingDoc.replace( '/', '\\' );
    }
    
    VwSqlMappingDocMgr.write( m_sqlMappingDoc, m_codeOpts, m_strAuthor, m_strSqlMappingDoc );
    m_logger.info( null, m_strSqlMappingDoc + " successfully generated!");
    
  } // end writeDAODocument()


  /**
   * Uses VwDAOGem to create the java DAO interface, imple and factory files
   * @throws Exception
   */
  private void generateDAO() throws Exception
  {
    VwDAOProperties daoProps = m_spec.getDaoProperties();

    if ( daoProps == null )
    {
      daoProps = new VwDAOProperties();
    }

    String strDaoName = daoProps.getClassName();

    if ( strDaoName == null ) // make DAO name from the same name as the spec name
    {
      strDaoName = m_fileDaoMappingDoc.getAbsolutePath();
    
      int nPos = strDaoName.lastIndexOf( '/' );
      strDaoName = strDaoName.substring( ++nPos );

      nPos = strDaoName.lastIndexOf( '.' );

      strDaoName = strDaoName.substring( 0, nPos );

      daoProps.setClassName( strDaoName );

    }


    String strDaoPackage = daoProps.getPackage();

    if ( strDaoPackage == null )
    {
      int nPos = m_strPackage.lastIndexOf( '.' );
    
      strDaoPackage = m_strPackage.substring( 0, ++nPos ) + "dao";
      daoProps.setPackage( strDaoPackage );

    }

    if (  daoProps.getBasePath() == null )
    {
      daoProps.setBasePath( m_strSourcePath );
    }

    VwDAOGen daoGen = new VwDAOGen( m_sqlMappingDoc, m_codeOpts, m_db, daoProps, m_logger );
    daoGen.genDao();
    
    
  } // end writeDAODocument()



  /**
   * Run initialization stuff
   */
  private boolean setup() throws Exception
  {
    
    Map mapProps = System.getProperties();
    
    m_logger = VwLogger.getInstance();
    
    if ( m_listLoggerAppenders != null )
    {
      for ( Iterator iAppenders = m_listLoggerAppenders.iterator(); iAppenders.hasNext(); )
      {
        Appender appender = (Appender)iAppenders.next();
        m_logger.addAppender( appender );
      }
    }
    
    if ( m_fDAOXMLOnly )
      m_fGenCode = false;
    else
      m_fGenCode = true;
    
    m_fArgGenCode = m_fGenCode;     // Hold onto original state

    m_strPrimeKeyPolicy = m_spec.getKeyGenerationPolicy();
    if ( m_strPrimeKeyPolicy == null )
      m_strPrimeKeyPolicy = "none";
    
    
    m_strSeqName = m_spec.getSequenceName();
    m_strSeqTableName = m_spec.getSequenceTableName();
    m_strSeqColName = m_spec.getSequenceColName();
    
    m_strTimestampCheck = m_spec.getTimestampColName();
    
    m_strSqlMappingDoc = m_spec.getSqlMappingDocument();
    
    if ( m_strSqlMappingDoc == null && !m_fDVOOnly )
      throw new Exception( "The sqlMappingDocument must be specified");
 
    if ( m_strSqlMappingDoc != null && !m_fDVOOnly )
    {
      m_strSqlMappingDoc = VwExString.replace( m_strSqlMappingDoc, "${", "}", mapProps );
      
      if ( !m_strSqlMappingDoc.endsWith( ".xsm" ))
        m_strSqlMappingDoc += ".xsm";
      
      m_fileDaoMappingDoc = new File( m_strSqlMappingDoc );

      if ( m_fOverwriteXsm )
      {
        m_fileDaoMappingDoc.delete();

      }

      if ( m_fileDaoMappingDoc.exists() )
      {
        if ( !m_fileDaoMappingDoc.canWrite() )
        {
          m_logger.fatal( null, "SQL Mapping XML Document '" + m_strSqlMappingDoc + "' is read only and cannot be modified, Terminating process");
          return false;
          
        }
      }
    
      if ( !m_fileDaoMappingDoc.exists() )
        m_logger.info( null, "'" + m_strSqlMappingDoc + "' does not exist and will be created");
      else      
        m_logger.info( null, "'" + m_strSqlMappingDoc + "' exists and will be updated");
    
    } // end if m_strDaoMappingDoc != null )
    
    if ( m_fNoModifyDVOs)
      m_logger.info( null, "The No modify DVO's option is on and existing DVO's will not be modified" );
      
    if ( m_fDVOOnly )
      m_logger.info( null, "DVO Only option is on and only Java class generation will take place" );
    else
    if ( m_fDAOXMLOnly )
      m_logger.info( null, "Sql Mapping document only option is on and Only Java DVO's that don't exist will be generated" );
    else
      m_logger.info( null, "Both Java DVO's and Sql Mapping document will be generated" );


    m_strExcludeCols = m_spec.getOmitColumns();

    VwConnection conn = m_spec.getConnection();
    VwObjectProperties objProps = m_spec.getObjectProperties();

    m_strAuthor = m_spec.getAuthor();

    if ( m_strAuthor == null )
      m_strAuthor = "";
    
    if ( m_strAuthor.length() > 0 )
      m_logger.info( null, "Author: " + m_strAuthor );
    
      
    m_strSourcePath = objProps.getBasePath();
    if ( m_strSourcePath == null )
      m_strSourcePath = (new File( "." )).getAbsolutePath();

    m_strSourcePath = VwExString.replace( m_strSourcePath, "${", "}", mapProps );
    m_logger.info( null, "Source Path: " + m_strSourcePath );
    
    m_strClassPath = objProps.getClassPath();
    
    m_strPackage = objProps.getPackage();
    
    if ( objProps.getTreatChar1AsBoolean() != null )
      m_fTreatChar1AsBoolean = Boolean.parseBoolean( objProps.getTreatChar1AsBoolean() );

    if ( ! (conn instanceof VwDbConnection)  )
    {
      m_strDriverId = conn.getDriverId();
      m_strDriverUrlId = conn.getDriverUrl();
      m_strUid = conn.getUid();
      m_strPwd = conn.getPwd();
    
      setupDatabase();
    }
    else
    {
      m_db = ((VwDbConnection)conn).getDatabase();
      if ( m_db.getDbMgr() != null )
        m_strUid = m_db.getDbMgr().getUserID();
 
    }
    
    if ( m_strSchema == null )
      m_strSchema = m_strUid;     // The default
    
    // Create new document if one does not exist
    if ( m_fileDaoMappingDoc != null )
    {
      if ( !m_fileDaoMappingDoc.exists() )
      m_sqlMappingDoc = new VwSqlMappingDocument();
      else
        m_sqlMappingDoc = VwSqlMappingDocumentReader.read( m_fileDaoMappingDoc.toURL() );

      m_daoDict = new VwSqlMappingDictionary( m_sqlMappingDoc );
      
    }
   
    String strUseJavaObjects = objProps.getUseJavaObjects();
    
    if ( strUseJavaObjects != null && strUseJavaObjects.equalsIgnoreCase( "false"))
      m_fUseObjectTypes = false;

    m_strDVOSuperClass = objProps.getSuperClass();
    
    String strUseDirtyObjectDetect = objProps.getUseDirtyObjectDetection();
    
    m_fUseDirtObjectDetection = false;
    
    if ( strUseDirtyObjectDetect != null && strUseDirtyObjectDetect.equalsIgnoreCase( "true"))
        m_fUseDirtObjectDetection = true;
    
    return true;
    
  } // end setup


  /**
   * Process the entire database schema fro each scheam named
   * 
   * @param dbSchema The VwDbSchema object to process
   * @throws Exception
   */
  private void processSchema( VwDbSchema dbSchema ) throws Exception
  {
    m_strSchema = dbSchema.getName();
    
    if ( !inIncludeList( "schema", m_strSchema ) )
    {
      m_logger.info( null, "Skipping schema '" + dbSchema.getName() + "' because it was not in the includes list");
      return;
    }
    
    m_logger.info( null, "" ); // generate blank line
    m_logger.info( null, "Processing schema '" + dbSchema.getName() + "'");
      
    String[] astrTables = m_db.getTables( null, dbSchema.getName(), null );

    if ( astrTables == null )
    {
      m_logger.error( null, "No tables were found for schema '" + dbSchema.getName() + "'");
      return;
      
    }
    
    
    for ( int x = 0; x < astrTables.length; x++ )
    {
      VwTableSpec dbTable = new VwTableSpec();
      dbTable.setName( astrTables[ x ] );
      m_logger.info( null, "Processing Table '" + astrTables[ x ] + "'");
     
      genCode( dbTable, null, null, null );
      
    } // end for()
    
  } // end processSchema()
  

  private boolean inIncludeList( String strObjectName, String strValue )
  {
    if ( m_mapIncludes == null )
      return true; // map is null, so return true for calling process to  process this entry
    
    List<String>listIncludeEntries = m_mapIncludes.get(  strObjectName );
    if ( listIncludeEntries == null )
      return false;
    
    
    return (listIncludeEntries.indexOf( strValue ) >= 0);
  }


  /**
   * Login to the requeste database and get a connection
   * @throws Exception
   */
  private void setupDatabase() throws Exception
  {

    if ( m_strDriverId == null )
    {
      throw new Exception( "Missing <driverId> tag, cannot continue" );
    }
    
    if ( m_strDriverUrlId == null )
    {
      throw new Exception( "Missing <driverUrlId> tag, cannot continue" );
    }


    if ( m_strUid == null )
    {
      throw new Exception( "Missing <uid> tag (User Id) for logging into database, cannot continue" );
    }

    if ( m_strPwd == null )
    {
      throw new Exception( "Missing <pwd> tag (password) for logging into database, cannot continue" );
    }

    
    // Login to database

    // *** Log into database
    m_logger.info( null, "Connecting to datasource driver " + m_strDriverId + " using URLID " + m_strDriverUrlId );
    
    VwDbMgr dbMgr = new VwDbMgr( m_strDriverId, m_strDriverUrlId, m_logger );
    m_db = dbMgr.login( m_strUid, m_strPwd );

    m_logger.info( null, "Database connection successful" );


  } // end setupDatabase

  /**
   * Generate Java DVO classes and DAO Document for the types m_specified
   * @param dbCommon
   * @param tr
   * @param strQuerySet
   * @param listFinders
   * @throws Exception
   */
  public void genCode( VwDbObjCommon dbCommon, VwTableRelationship tr, String strQuerySet, List<VwFinder> listFinders  ) throws Exception
  {
    m_dbCommon = dbCommon;

    m_listMappingPrimaryKeys = new ArrayList<VwPrimaryKeyGeneration>();
    boolean fGenCode = m_fArgGenCode;

    m_sbSelect = m_sbWhere =  m_sbExists =  m_sbInsert = m_sbUpdate =  m_sbDelete =  m_sbProc = m_sbTsCheck = null;
    m_fHasPrimeKeyGenOverrides = false;
    
    try
    {
      m_sqlMgr = new VwSqlMgr( m_db );
      
      if ( m_strPackage == null )
      {
        throw new Exception( "Missing package=<your class package name>,"
                               + "for the database classs.\n"
                               + " This is a required attribute, cannot continue" );
      }
  
      // Check for local overrides
      
      String              strPackage = null;
      String              strClassName = null;

      List                listTableColumns = null;
      VwExtendsHierarchyDescriptor vwExtendsHierarchyDescriptor = null;

     // *** Check for overrides
      if ( dbCommon != null )
      {
        if ( dbCommon.getOmitColumns() != null )
        {
          handleOmitColumns( dbCommon  );
        }

        if ( dbCommon.getNoDVO() != null && dbCommon.getNoDVO().equals( "true" ) )
        {
          fGenCode = false;         // Overrrive from command line for this mapping
        }
        

        if ( dbCommon instanceof VwTableSpec )
        {
          setupPrimaryKeyGeneration( dbCommon );

          String strExcludeCols = ((VwTableSpec)dbCommon).getExcludeCols();

          if ( strExcludeCols != null )
          {
            if ( m_strExcludeCols != null )
            {
              m_strExcludeCols += "," + strExcludeCols;
            }
            else
            {
              m_strExcludeCols = strExcludeCols;
            }

          }

          m_strIncludeCols = ((VwTableSpec)dbCommon).getIncludeCols();

          if ( m_strExcludeCols != null && m_strIncludeCols != null )
          {
            m_logger.error( null, "Cannot have both includeCols and excludeCols option for table '" + ((VwTableSpec)dbCommon).getName() + "'" );
            System.exit( 1 );
          }
        }
      }

      if ( strPackage == null )
      {
        strPackage = m_strPackage;
      }

      m_strTableName = null;

      String strMappingId = null;

      List<VwKeyDescriptor> listForeignKeyDescriptors = null;
      List<VwKeyDescriptor> listPrimeKeySuppliers = new ArrayList<VwKeyDescriptor>();
      List<VwColInfo> listProcColumns = null;

      VwDVOGen dvoGen = null;

      if ( dbCommon instanceof VwTableSpec || dbCommon instanceof VwDbQuery  )
      {
        //if ( fGenCode )
        {
          listTableColumns = getTableColumns( dbCommon );
        }
      }
      else
      if ( dbCommon instanceof VwProcedure )
      {
        m_sbProc = new StringBuffer();

        VwProcedure proc = (VwProcedure)dbCommon;
        if ( proc.getSql() != null )
        {
          m_sbProc.append( proc.getSql()  );
        }
        else
        {
          listProcColumns = m_db.getProcedureColumns( null, m_strSchema, proc.getName(), null );
        }
      }
      else
      {
        m_strTableName = tr.getName();

        VwDbObjCommon table = getSqlTableMapping(  m_strTableName );

        setupPrimaryKeyGeneration( table );

        if ( table instanceof VwTableSpec )
        {
          // See if we have a key gen policy override
          dbCommon = table;

          // See if this is an override class name
          strClassName = table.getInheritClassName();

        }

        listTableColumns = tr.getColumns();
        m_listPrimaryKeys = tr.getPrimeKeys();
        m_listForeignKeys = tr.getForeignKeys();

        // we need to add any foreign keys columns to the exclude list because those are defined
        // in the related objects

        listForeignKeyDescriptors = checkForeignKeys( tr );

      }

      /* todo PBV 6/28/19
      if ( listTableColumns == null )
      {
        return;
      }

      */
      if ( dbCommon instanceof VwDbQuery && dbCommon.getInheritClassName() == null )
      {
        m_strClassName = null;
        fGenCode = false;
        strMappingId = dbCommon.getId();
      }
      else
      if ( dbCommon != null )
      {
        strClassName = dbCommon.getClassName();

        if ( strClassName == null || (dbCommon.getGenDvoFromTable() != null && dbCommon.getGenDvoFromTable().equals( "true" )) )
        {
          m_strClassName = VwExString.makeJavaName( m_strTableName, false );
        }
        else
        {
          fGenCode = false;
          m_strClassName = strClassName;
        }

        if ( dbCommon.getId() != null )
        {
          strMappingId = dbCommon.getId();  // This is an override from the default classname
        }
      }
      else
      {
        m_strClassName = VwExString.makeJavaName( m_strTableName, false );
      }


      if( strMappingId == null )
      {
        if ( strClassName == null )
           strMappingId = strPackage + "." + m_strClassName;
        else
          strMappingId = strClassName;
      }


      if ( dbCommon instanceof VwTableSpec && dbCommon.getExtends() != null )
      {
        vwExtendsHierarchyDescriptor = getExtendsDescriptor( (VwTableSpec)dbCommon );
      }

      File fileJava = null;

      if ( m_strClassName != null )
      {
        fileJava = getJavaFile( strPackage );
      }

      // If the gen DVO code is off but the DVO does not exist, generate the DVO

      if ( fGenCode || ( fileJava != null && !fileJava.exists() ) )
      {
        dvoGen = setupCodeGenerator( strPackage, vwExtendsHierarchyDescriptor );

        // From here out reset the class name to the one passed in the attribute if set

        if ( strClassName != null )
        {
          m_strClassName = strClassName;  // override
        }

        if ( dvoGen != null )
        {
          if ( listTableColumns != null )
          {
            buildJavaClassFromTableCols( dvoGen, listTableColumns, tr, listPrimeKeySuppliers, vwExtendsHierarchyDescriptor );
          }
          else
          if ( listProcColumns != null )
          {
            buldJavaClassFromProcCols( dvoGen, listProcColumns );
          }
        }
      } // end if
      else
      if ( tr != null )
      {
        for ( Iterator iRelated = tr.getRelationships().values().iterator(); iRelated.hasNext(); )
        {
          VwTableRelationship trRelated = (VwTableRelationship)iRelated.next();
          checkPrimeKeySupplier( listPrimeKeySuppliers, tr, trRelated );
        }
      }

      if ( m_fDVOOnly )
      {
        return;
      }

      if ( m_strTableName != null && m_mapViews.containsKey( m_strTableName.toLowerCase() ))
      {
        buildViewSQL( listTableColumns );
      }
      else
      if ( listTableColumns != null && m_strTableName != null )
      {
        buildTableSQL( dbCommon, strQuerySet, listTableColumns, tr,  vwExtendsHierarchyDescriptor );
      }
      else
      if ( listProcColumns != null )
      {
        buildProcSQL( (VwProcedure)dbCommon, listProcColumns );
      }

      if ( m_listMappingPrimaryKeys.size() == 0 )
      {
        m_listMappingPrimaryKeys = null;
      }


      createOrUpdateDAODoc( dbCommon, strMappingId, listPrimeKeySuppliers, listForeignKeyDescriptors, listFinders, vwExtendsHierarchyDescriptor );

    } // end try
    finally
    {
      if ( m_sqlMgr != null )
        m_sqlMgr.close();
    }

  } // end genCode()


  /**
   * Setup with primary key defaults if not specidied by the table
   * @param dbCommon
   */
  private void setupPrimaryKeyGeneration( VwDbObjCommon dbCommon )
  {
    // Set these properties with the defualts if they are not defined
    if ( dbCommon.getKeyGenerationPolicy() == null )
    {
      dbCommon.setKeyGenerationPolicy( m_strPrimeKeyPolicy );
      dbCommon.setSequenceName( m_strSeqName );
      dbCommon.setSequenceColName( m_strSeqColName );
      dbCommon.setSequenceTableName( m_strSeqTableName );

    }
  }


  /**
   * Build sql for views. Onlu findBy is generated for views
   * @param listColumns
   */
  private void buildViewSQL( List listColumns )
  {
    m_sbSelect = new StringBuffer( "select ");
    String strIndent = VwExString.lpad( "", ' ', 21 );

    int nLineLen = 0;

    for ( Iterator iColInfo = listColumns.iterator(); iColInfo.hasNext(); )
    {

      VwColInfo ci = (VwColInfo)iColInfo.next();
      String strColName = ci.getColumnName();
      String strPropName = ci.getColumnAliasName();

      if ( nLineLen > 70 )
      {
        m_sbSelect.append( "\r\n" ).append( strIndent );
        nLineLen = 0;
      }

      m_sbSelect.append( strColName ).append( " " ).append( strPropName );
      if ( iColInfo.hasNext() )
        m_sbSelect.append( ", " );

      nLineLen += strColName.length() + strPropName.length() + 2;

    } // end for()


    m_sbSelect.append( "\r\n" );
    m_sbSelect.append( strIndent ).append( " from " ).append( m_strSchema ).append( "." ).append( m_strTableName );

    m_sbSelect.append( "\r\n" );
    m_sbSelect.append( strIndent ).append( " into " ).append( m_strPackage ).append( ".");

    if ( m_strClassName == null )
      m_strClassName = VwExString.makeJavaName( m_strTableName, false  );

    m_sbSelect.append( m_strClassName ).append( ";" );

    m_sbWhere =  new StringBuffer();

    m_sbExists = new StringBuffer( "select 1 from " );
    m_sbExists.append( m_strSchema ).append( "." ).append( m_strTableName );

    if ( m_strTimestampCheck != null )
      doTimestampCheck();

    m_sbWhere.append( "\r\n").append( strIndent ).append( "  where " );
    int nKeyCount = 0;

    if ( m_listPrimaryKeys != null && m_listPrimaryKeys.size() == 0  )
      m_sbWhere.append( m_strTableName ).append(".primaryKey = :primaryKey");
    else
    {
      for ( Iterator iKeys = m_listPrimaryKeys.iterator(); iKeys.hasNext(); )
      {
        if ( nKeyCount++ > 0 )
          m_sbWhere.append( " and ");

        VwColInfo ci = (VwColInfo)iKeys.next();

        m_sbWhere.append( ci.getColumnName() ).append( " = :" ).append( VwExString.makeJavaName( ci.getColumnName(), true ));

      } // end for()
    } // end else

  } // end buildViewSQL


  /**
   * Create the timestamp check SQL
   */
  private void doTimestampCheck()
  {
    m_sbTsCheck = new StringBuffer( "select " );
    m_sbTsCheck.append( m_strTimestampCheck ).append( " \"");
    m_sbTsCheck.append( VwExString.makeJavaName( m_strTimestampCheck, true ) ).append( "\"" );
    m_sbTsCheck.append( " from " ).append( m_strSchema ).append( "." ).append( m_strTableName );

  }


  /**
   *  This methods generates the SqlMapping document (.xsm ) file
   */
  private void createOrUpdateDAODoc( VwDbObjCommon dbCommon, String strMappingId, List<VwKeyDescriptor> listPrimeKeySuppliers,
      List<VwKeyDescriptor> listForeignKeyDescriptors, List<VwFinder> listFinders, VwExtendsHierarchyDescriptor vwExtendsHierarchyDescriptor ) throws Exception
  {
    // We need to save Orm sql for use by derived tables from VwOrm tags

    // Override mapping id if inherited class name was specified
    if ( dbCommon.getInheritClassName() != null  )
    {
      strMappingId = dbCommon.getInheritClassName();

    }

    if ( dbCommon instanceof VwOrm )
    {
      m_mapOrmSqlByMappingId.put( strMappingId, m_sbSelect.toString() );
    }


    VwExtendsDescriptor vwExtendsDescriptor =null;

    if ( vwExtendsHierarchyDescriptor != null )
    {
      vwExtendsDescriptor = vwExtendsHierarchyDescriptor.getExtendsDescriptor();

      doOrmSqlFixup( vwExtendsHierarchyDescriptor, strMappingId );

    }
    
    m_fDocChanges = true;

    // Add A sql mapping entry for this class
    String strSqlRef = null;

    if ( dbCommon != null )
    {
      strSqlRef = dbCommon.getSqlId();
    }

    if ( strSqlRef == null )
    {
      strSqlRef = "base";
    }

    List<VwSqlMapping> listSqlMappings = m_sqlMappingDoc.getSqlMapping();

    if ( listSqlMappings == null )
    {
      listSqlMappings = new ArrayList<VwSqlMapping>();
      m_sqlMappingDoc.setSqlMapping( listSqlMappings );
    }

    VwSqlMapping sqlMapping = m_daoDict.getSqlMapping( strMappingId );

    if ( sqlMapping == null )
    {
      sqlMapping = new VwSqlMapping();
      listSqlMappings.add( sqlMapping );
      m_daoDict.updateSqlMapping( strMappingId, sqlMapping );

    }

    sqlMapping.setExtendsClass( vwExtendsDescriptor );

    if ( dbCommon.getInheritClassName() !=null )
    {
      sqlMapping.setInheritClassName( dbCommon.getInheritClassName() );
    }
    else
    if ( dbCommon.getClassName() !=null )
    {
      sqlMapping.setClassName( dbCommon.getClassName() );
    }

    if ( dbCommon.getImplementsClassName() !=null )
    {
      sqlMapping.setImplementsClassName(  dbCommon.getImplementsClassName() );

    }
    
    sqlMapping.setId( strMappingId );

    if ( dbCommon instanceof VwProcedure )
    {
      doProc( (VwProcedure)dbCommon, m_sbProc, sqlMapping );
      return;
    }

    if ( m_strPrimeKeyPolicy.startsWith( "override" ) )
    {
      if ( m_fHasPrimeKeyGenOverrides)
      {
        sqlMapping.setPrimaryKeyGeneration( m_listMappingPrimaryKeys );
      }
    }
    else
    {
      sqlMapping.setPrimaryKeyGeneration( m_listMappingPrimaryKeys );
    }

    // Only do the following checks for object graph definitions
    if ( dbCommon instanceof VwOrm || dbCommon instanceof VwTableSpec || dbCommon == null )
    {
      List<VwKeyDescriptor> listExistForeignKeyDescriptors = sqlMapping.getForeignKey();

      // Incorporate exisitng foreign key descriptors and add any new ones
      if ( listExistForeignKeyDescriptors != null && listExistForeignKeyDescriptors.size() > 0  )
      {
        fixupKeyDescriptors( strMappingId, listExistForeignKeyDescriptors, listForeignKeyDescriptors );
        if ( listExistForeignKeyDescriptors.size() > 0 )
        {
          sqlMapping.setForeignKey( listExistForeignKeyDescriptors );
        }
        else
        {
          sqlMapping.setForeignKey( listForeignKeyDescriptors );
        }
      }
      else
      {
        sqlMapping.setForeignKey( listForeignKeyDescriptors );
      }

      // Incorporate exisitng prime key suppliers and add any new ones
      List<VwKeyDescriptor> listExistPrimeKeySuppliers = sqlMapping.getPrimaryKeySupplier();
      if ( listExistPrimeKeySuppliers != null && listExistPrimeKeySuppliers.size() > 0  )
      {

        fixupKeyDescriptors( strMappingId, listExistPrimeKeySuppliers, listPrimeKeySuppliers );
        if ( listExistPrimeKeySuppliers.size() >0 )
        {
          sqlMapping.setPrimaryKeySupplier( listExistPrimeKeySuppliers );
        }
         else
        {
          sqlMapping.setPrimaryKeySupplier( listPrimeKeySuppliers );
        }
      }
      else
      {
        sqlMapping.setPrimaryKeySupplier( listPrimeKeySuppliers );
      }

    }

    sqlMapping.setId( strMappingId );
    String strStmtId = "primaryKey";

    // *** Query

    VwSqlStatement query = sqlMapping.getFindBy();
    if ( query == null )
    {
      query = new VwSqlStatement();
    }

    // If a "base" sql id exists, then the primaryKey and exists constraints will always point to "base" else they will point
    // to the first object graph defined

    String strPrimeKeySqlRef = strSqlRef;

    VwConstraint constraintPrimaryKey = null;

    if ( getSql( "base", query.getSql() ) != null )
    {
      strPrimeKeySqlRef = "base";
    }


    List<VwConstraint> listConstraints = query.getConstraint();

    // if this list was passed in, then it overrides any existing constraints
    if ( listFinders != null && listFinders.size() > 0 )
    {
      if ( listConstraints != null )
      {
        listConstraints = new ArrayList<VwConstraint>( listConstraints ); // Add in any previous constraints
      }
      else
      {
        listConstraints = new ArrayList<VwConstraint>();
      }
    }

    if ( m_sbWhere != null  )
    {
      if ( listConstraints == null )
      {
        listConstraints = new ArrayList<VwConstraint>();
      }
    }

    if ( m_sbWhere != null )
    {
      VwConstraint constraint = getConstraint( strStmtId, strPrimeKeySqlRef, listConstraints );
      if ( constraint == null )
      {
        constraint = new VwConstraint();
        listConstraints.add( constraint );
      }


      constraint.setSqlRef( strPrimeKeySqlRef );
      constraint.setId( strStmtId );
      constraint.setWhere( m_sbWhere.toString() );
      constraintPrimaryKey = constraint;

    }

    if ( listFinders != null )
    {
      for ( VwFinder finder : listFinders )
      {
        String strFinderSqlRef = finder.getSqlRef();
        if ( strFinderSqlRef == null )
        {
          strFinderSqlRef = strSqlRef;
        }

        VwConstraint constraint = getConstraint( finder.getId(), strFinderSqlRef, listConstraints );
        if ( constraint == null )
        {
          constraint = new VwConstraint();
          listConstraints.add( constraint );
        }

        constraint.setMappingConstraints( finder.getMappingTableConstraint() );
        constraint.setSqlRef( strFinderSqlRef );
        constraint.setId( finder.getId() );
        constraint.setWhere( finder.getWhere() );
      }
    }


    // *** timestamp check
    if ( m_sbTsCheck != null )
    {
       VwSqlStatement tsCheck = new VwSqlStatement();
       List<VwSql>listSql = new ArrayList<VwSql>();
       VwSql sql = new VwSql();

       List<VwConstraint> listPrimeKeyConstraints = new ArrayList<VwConstraint>();
       constraintPrimaryKey.setSqlRef( "base" );
       listPrimeKeyConstraints.add( constraintPrimaryKey );
       tsCheck.setConstraint( listPrimeKeyConstraints );

       sql = new VwSql();
       sql.setId( "base" );
       sql.setBody( m_sbTsCheck.toString() );
       listSql.add( sql );
       tsCheck.setSql( listSql );

       sqlMapping.setTimestampCheck( tsCheck);

    }

    VwSql sql = null;
    List<VwSql> listSql = query.getSql();

    if ( listSql == null )
    {
      listSql = new ArrayList<VwSql>( 1 );
    }

    sql = getSql( strSqlRef, listSql );

    if ( sql == null )
    {
      sql = new VwSql();
      listSql.add( sql );

    }

    // Custom query
    if ( dbCommon instanceof VwDbQuery )
    {
      String strQuery = ((VwDbQuery)dbCommon).getSql();

      // do sql fixup sql to  add the into and classname statement for storing to the dvo
      String strPackage = null;

      if ( ((VwDbQuery)dbCommon).getPackage() != null )
      {
        strPackage = ((VwDbQuery)dbCommon).getPackage();
      }
      else
      {
        strPackage = m_strPackage;
      }


      if ( strPackage == null )
      {
        throw new Exception( "Package name is missing for query id " + ((VwDbQuery)dbCommon).getId());
      }

      String strClassName = null;

      if ( dbCommon.getInheritClassName() != null )
      {
        strClassName = dbCommon.getInheritClassName();
      }
      else
      {
        strClassName = dbCommon.getClassName();

      }
      
      if ( strQuery.toLowerCase().startsWith( "select" ) && !VwDAOGen.isScalerResult( strClassName ))
      {
        strQuery += "\r\n       into " + strPackage + "." + strClassName + ";";
      }

      sql.setBody( strQuery );
      sql.setId( strSqlRef );
      query.setSql( listSql );

      query.setConstraint( listConstraints );

      if ( strQuery.toLowerCase().startsWith( "select" ))
      {
        sqlMapping.setFindBy( query );
      }
      else
      if ( strQuery.toLowerCase().startsWith( "delete" ))
      {
        sqlMapping.setQuery( query );

      }
      if ( strQuery.toLowerCase().startsWith( "update" ))
      {
        sqlMapping.setQuery( query );

      }


      return;

    }

    // this happens if another object graph contains this table, we don't want to wipe out the original object graph sql
    boolean fUpdateQuery = true;


    if ( strSqlRef.equals( "base" ) && query.getSql() != null && dbCommon == null )
    {
      fUpdateQuery = false;
    }

    if ( fUpdateQuery )
    {
      query.setConstraint( listConstraints );

      sql.setId( strSqlRef );
      sql.setBody( m_sbSelect.toString() );

      query.setSql( listSql );

      sqlMapping.setFindBy( query );

    }


    // *** exists
    if ( m_sbExists != null )
    {
      VwSqlStatement exists = sqlMapping.getExists();

      if ( exists == null )
      {
        exists = new VwSqlStatement();
      }


      listSql = exists.getSql();
      if ( listSql == null )
      {
        listSql = new ArrayList<VwSql>( 1 );
      }

      exists.setConstraint( listConstraints );

      sql = getSql( "base", listSql );

      if ( sql == null )
      {
        sql = new VwSql();
        listSql.add( sql );
      }

      sql.setId( "base" );

      sql.setBody( m_sbExists.toString() );
      exists.setSql( listSql );

      sqlMapping.setExists( exists );

    }


    // *** Insert
    if ( m_sbInsert != null )
    {
      sqlMapping.setInsert( m_sbInsert.toString() );
    }

    // *** Update ****
    if ( m_sbUpdate != null )
    {
      VwSqlStatement stmt = sqlMapping.getUpdateBy();
      if ( stmt == null )
      {
        stmt = new VwSqlStatement();
      }

      List<VwConstraint>listUpdateConstraints = stmt.getConstraint();
      if ( listUpdateConstraints != null )
      {
        listUpdateConstraints = updateConstraints( listUpdateConstraints, listConstraints );
      }
      else
      {
        listUpdateConstraints = listConstraints;
      }

      stmt.setConstraint( listUpdateConstraints );

      listSql = stmt.getSql();

      if ( listSql == null )
      {
        listSql = new ArrayList<VwSql>( 1 );
      }

      sql = getSql( "base", listSql );

      if ( sql == null )
      {
        sql = new VwSql();
        listSql.add( sql );
      }

      sql.setId( "base" );
      sql.setBody( m_sbUpdate.toString() );
      stmt.setSql( listSql );

      sqlMapping.setUpdateBy( stmt );

    }

    // *** delete

    if ( m_sbDelete != null )
    {

      VwSqlStatement stmt = sqlMapping.getDeleteBy();
      if ( stmt == null )
      {
        stmt = new VwSqlStatement();
      }

      List<VwConstraint>listDeleteConstraints = stmt.getConstraint();
      if ( listDeleteConstraints != null )
      {
        listDeleteConstraints = updateConstraints( listDeleteConstraints, listConstraints );
      }
      else
      {
        listDeleteConstraints = listConstraints;
      }

      stmt.setConstraint( listDeleteConstraints );

      listSql = stmt.getSql();

      if ( listSql == null )
      {
        listSql = new ArrayList<VwSql>( 1 );
      }

      sql = getSql( "base", listSql );

      if ( sql == null )
      {
        sql = new VwSql();
        listSql.add( sql );
      }

      sql.setId( "base" );
      sql.setBody( m_sbDelete.toString() );
      stmt.setSql( listSql );
      sqlMapping.setDeleteBy( stmt );
    }
    
  } // end createOrUpdateDAODoc()


  /**
   * If this table mapping extends an orm table then we have to copy the other relational sql statements fro the orm sql to this
   * mapping and adjust the class into objects to this one
   *
   * @param vwExtendsHierarchyDescriptor
   * @param strMappingId
   */
  private void doOrmSqlFixup( VwExtendsHierarchyDescriptor vwExtendsHierarchyDescriptor, String strMappingId )
  {
    VwStack<VwDbObjCommon> stackHierarchy = vwExtendsHierarchyDescriptor.getStackHierarchy();

    VwDbObjCommon dbSuper = stackHierarchy.peek();

    if ( ! (dbSuper instanceof VwOrm ) )
    {
       return; // nothing todo
    }

    String strTableName = null;
    String strOrmMappingId = null;

    String strInherritClassName = dbSuper.getInheritClassName();

    String strFixupName = null;

    strTableName =  ((VwOrm)dbSuper).getBaseTable();
    strOrmMappingId =  m_strPackage + "."  + VwExString.makeJavaName( strTableName, false );

    if ( strInherritClassName != null )
    {
       strFixupName = strInherritClassName;
    }
    else
    {
      strFixupName = strOrmMappingId;

    }

    String strOrmSql = m_mapOrmSqlByMappingId.get( strFixupName );

    // we copy all additional sql statems past the base sql to the derrived classes sql
    int nPos = strOrmSql.indexOf( ";") + 1;

    String strCopySql = strOrmSql.substring( nPos );

    // We replace the class into names to point to the derrived class

    String[] astrSqlPieces = strCopySql.split( ";" );

    for ( int x = 0; x < astrSqlPieces.length; x++ )
    {
      String strSqlPiece = astrSqlPieces[ x ];

      // get the 'for' part

      nPos = strSqlPiece.indexOf( " for ");

      strSqlPiece = VwExString.replace( strSqlPiece, strFixupName, strMappingId, nPos ) + ";";

      // Append this sql to the select buffer

      m_sbSelect.append( m_strIndentSpaces ).append( strSqlPiece );

    }



   }


  /**
   * This method does a fixup on a statements constraints. In essence the statements constraints will always be updated
   * by the primaryKey constraint as the table definition could have changed from previous version
   * @param listOrigConstraints The origianl constraints in teh xsm document
   * @param listNewConstraints The newly generated constarints
   * @return
   */
  private List<VwConstraint> updateConstraints( List<VwConstraint> listOrigConstraints, List<VwConstraint> listNewConstraints )
  {
    VwConstraint constraintPrimarykey = null;
    for ( VwConstraint constraint : listNewConstraints )
    {
      if ( constraint.getId().equalsIgnoreCase( "primaryKey" ))
      {
        constraintPrimarykey = constraint;
        break;

      }
    }

    for ( Iterator<VwConstraint> iter = listOrigConstraints.iterator(); iter.hasNext(); )
    {
      VwConstraint constraint = iter.next();

      if ( constraint.getId().equalsIgnoreCase( "primaryKey" ))
      {
        iter.remove();
        listOrigConstraints.add( constraintPrimarykey );
        break;

      }
    }

    return listOrigConstraints;
  }


  private void fixupKeyDescriptors( String strClassName, List listOrigDescriptors, List listNewDescriptors )
  {
    int nPos = strClassName.lastIndexOf( '.' );
    strClassName = strClassName.substring( ++nPos );

    // This first test makes sure any previously defined references to beans still exist
    // in the java class. User could have manually deleted the bean reference.
    //If bean ref does not exist in the java class file, remove the descriptor form the orig list

    for ( Iterator iExistKeyDesc = listOrigDescriptors.iterator(); iExistKeyDesc.hasNext(); )
    {
      VwKeyDescriptor keyDescExist = (VwKeyDescriptor)iExistKeyDesc.next();

      if ( m_mapDvoPropdefsByJavaName != null )
      {
        List<VwPropertyDefinition> listPropDefs = m_mapDvoPropdefsByJavaName.get( strClassName.toLowerCase() );
        if ( listPropDefs != null )
        {
          boolean fFoundProp = false;

           for ( VwPropertyDefinition propDef : listPropDefs )
           {
             if ( keyDescExist.getBeanProperty().equalsIgnoreCase( propDef.getName() ) )
             {
               fFoundProp = true;
               break;
             }
           }

           if ( !fFoundProp )
            iExistKeyDesc.remove();


        }
      }
    }

    if ( listNewDescriptors == null )
      return;

    // Add in any newley defined descriptors
    for ( Iterator iNewKeyDesc = listNewDescriptors.iterator(); iNewKeyDesc.hasNext(); )
    {
      VwKeyDescriptor keyDescNew = (VwKeyDescriptor)iNewKeyDesc.next();

      boolean fExist = false;
      for ( Iterator iExistKeyDesc = listOrigDescriptors.iterator(); iExistKeyDesc.hasNext(); )
      {
        VwKeyDescriptor keyDescExist = (VwKeyDescriptor)iExistKeyDesc.next();
        if ( keyDescExist.getBeanProperty().equalsIgnoreCase( keyDescNew.getBeanProperty() )&&
             keyDescExist.getForeignKeyProperty().equalsIgnoreCase( keyDescNew.getForeignKeyProperty() )&&
             keyDescExist.getPrimeKeyProperty().equalsIgnoreCase( keyDescNew.getPrimeKeyProperty() ) )
        {
          fExist = true;
          break;
        }
      } // end for

      if ( !fExist )
      {
        listOrigDescriptors.add( keyDescNew );
      }

    } // end for

  } // end fixup

  /**
   * Gets a sqlmapiing VwTableSpec by the table name
   * @param strTableName
   * @return
   */
  private VwDbObjCommon getSqlTableMapping( String strTableName )
  {
    for ( VwDbObjCommon dbCommon : m_spec.getSpecMappings()  )
    {
      if ( dbCommon instanceof VwTableSpec )
      {
        if ( ((VwTableSpec)dbCommon).getName().equals( strTableName ) )
        {
          return dbCommon;
        }
      }
      else
      if ( dbCommon instanceof VwOrm )
      {
        if ( ((VwOrm)dbCommon).getBaseTable().equals( strTableName ) )
        {
          return dbCommon;
        }

      }
    }

    return null;

  }

  /**
   * gets the VwSql object in the list
   * @param strSqlId The id of the sql object to retrieve
   * @param listSql The list of sql objects
   *
   * @return The VwSql object for the id or null if not found
   */
  private VwSql getSql( String strSqlId, List listSql )
  {

    if ( listSql == null )
      return null;

    for ( Iterator iSql = listSql.iterator(); iSql.hasNext(); )
    {
      VwSql sql = (VwSql)iSql.next();


      if ( sql.getId().equalsIgnoreCase( strSqlId ))
        return sql;

    } // end for

    return null;

  } // end getSql

  /**
   * gets the VwConstraint object in the list
   * @param strId The id of the constraint to retrieve
   * @param listConstraints  The list of VwConstraint objects
   *
   * @return The VwConstraint object for the id or null if not found
   */
  private VwConstraint getConstraint( String strId, String strSqlRef, List<VwConstraint> listConstraints )
  {
    for ( VwConstraint constraint : listConstraints )
    {
      if ( constraint.getId().equalsIgnoreCase( strId ) && constraint.getSqlRef().equalsIgnoreCase( strSqlRef ))
        return constraint;

    } // end for

    return null;

  } // end getSql

  /**
   * Build a List of columns from the result set meta data from either a table definition or user defined select statement
   *
   * @param dbCommon This will be either an VwTable or VwDbQuery object
   * @return a List of VwColInfo objects
   */
  private List<VwColInfo> getTableColumns( VwDbObjCommon dbCommon ) throws Exception
  {

    String strSQL = null;
    boolean fIsView = false;
    Map<String,String> mapUserDefPrimeKeyCols = null;

    if ( dbCommon instanceof VwTableSpec )
    {
      m_sbSelect = new StringBuffer();
      m_strTableName = ((VwTableSpec)dbCommon).getName();

      // get any user assisted primary key columns -- could be a view or table with no ri defined
      String strPrimeKeyCols = ((VwTableSpec)dbCommon).getPrimaryKeyCols();
      if ( strPrimeKeyCols != null )
      {
        VwDelimString dlms = new VwDelimString( strPrimeKeyCols );
        mapUserDefPrimeKeyCols = dlms.toMap( true );
      }

    }
    else
    {
      m_sbSelect = null;

      strSQL = ((VwDbQuery)dbCommon).getSql();

      if ( strSQL.startsWith( "update" ))
      {
        return null;
      }

      // Replace param placeholders with the key word null so that we can run the query to the the colums
      strSQL = replaceUserParams( strSQL );


    }

    if ( m_strTableName != null )
    {

      fIsView = m_mapViews.containsKey( m_strTableName.toLowerCase() );

      // get all columns from table
      if ( m_strSchema != null )
        strSQL = "select * from " + m_strSchema + "." + m_strTableName;
      else
        strSQL = "select * from " +  m_strTableName;

      if ( m_strClassName == null )
      {
        if ( m_strTableName == null )
          throw new Exception( "The <className> tag is required if the generated class is from a sql statement" );

         m_strClassName = VwExString.makeJavaName( m_strTableName, false );

      }
    }


    if ( strSQL == null )
      throw new Exception( "The <table> tag or the <sql> is required to create the class" );


    try
    {
      m_sqlMgr.exec( strSQL, null, false );
    }
    catch( Exception ex )
    {
      m_logger.error( null, ex.getMessage() );
      throw ex;

    }

    if ( m_strTableName != null )
    {

      if ( mapUserDefPrimeKeyCols == null )
      {
        m_listPrimaryKeys = m_db.getPrimaryKeys( null, m_strSchema, m_strTableName );
        m_listForeignKeys = m_db.getForeignKeys( null, m_strSchema, m_strTableName );
      }
      else
      {
        m_listPrimaryKeys = new ArrayList<VwColInfo>();
        m_listForeignKeys = new ArrayList<VwForeignKeyInfo>();
      }
      if ( m_listPrimaryKeys.size() == 0 && !fIsView )
        m_logger.warn( null, "No primary key defined for table '" + m_strSchema + "." + m_strTableName + "' You will need to do fixups in the <where> ");

      if ( m_strClassName == null )
      {
        if ( m_strTableName == null )
          throw new Exception( "The <className> tag is required if the generated class is from a sql statement" );

         m_strClassName = VwExString.makeJavaName( m_strTableName, false );

      } // end if
    } // end if

    // Build VwColInfo objects for each result column in the metadata -- this will be used for sql statement generation
    // and java object property generation

    ResultSetMetaData md = m_sqlMgr.getMetaData();

    if ( md == null )
    {
      return new ArrayList<VwColInfo>();

    }

    List<VwColInfo>listColumns = new ArrayList<VwColInfo>( md.getColumnCount() );


    Map<String,String> mapExcludeCols = null;
    Map<String,String> mapIncludeCols = null;

    if ( m_strExcludeCols != null )
    {
      VwDelimString dlmsExcludeCols = new VwDelimString( ",", m_strExcludeCols );
      mapExcludeCols = dlmsExcludeCols.toMap( true );

    }

    if ( m_strIncludeCols != null )
    {
      VwDelimString dlmsIncludeCols = new VwDelimString( ",", m_strIncludeCols );
      mapIncludeCols = dlmsIncludeCols.toMap( true );

    }

    // build column list from result set meta data
    for ( int x = 0; x < md.getColumnCount(); x++ )
    {
      String strColName = md.getColumnLabel( x + 1 );

      if ( mapIncludeCols != null && !mapIncludeCols.containsKey( strColName.toLowerCase() ) )
        continue;

      if ( mapExcludeCols != null && mapExcludeCols.containsKey( strColName.toLowerCase() ) )
        continue;

      String strPropName = null;

      if ( strColName.indexOf( '_' ) >= 0  )
        strPropName = VwExString.makeJavaName( strColName, true );
      else
        strPropName = strColName;

      VwColInfo ci = new VwColInfo();
      ci.setColumnName( strColName );
      ci.setColumnAliasName( strPropName );
      ci.setSQLType( md.getColumnType( x + 1 ) );
      ci.setNbrDecimalDigits( md.getScale( x + 1 ) );
      ci.setColSize( md.getPrecision( x + 1 ));
      listColumns.add( ci );

      if ( mapUserDefPrimeKeyCols != null )
      {
        if ( mapUserDefPrimeKeyCols.containsKey( ci.getColumnName().toLowerCase() ) )
        {
          m_listPrimaryKeys.add( ci );
        }
      }

    } // end for

    return listColumns;

  } // end getTableColumns()


  /**
   * Replace user paramName that start with a colan to the jdvc ?
   * @param strSQL
   * @return
   */
  private String replaceUserParams( String strSQL )
  {
    int nPos = 0;
    int nLastPos = 0;

    StringBuffer sb = new StringBuffer();

    while( (nPos = strSQL.indexOf( ":", nLastPos )) >= 0 )
    {
      sb.append( strSQL.substring( nLastPos, nPos ) );

      // A double colan is a tylecast operator in Postgres so inclue 2cg ':' and move on to next occurrence
      if( strSQL.charAt( nPos + 1  ) == ':' )
      {
        sb.append( "::" );
        nLastPos = nPos + 2;
        continue;

      }

      sb.append( " null " );

      nLastPos = nPos;

      for ( int x = nPos; x < strSQL.length(); x++ )
      {
        if ( VwExString.isWhiteSpace( strSQL.charAt( x ) ) || strSQL.charAt( x ) == '\"' || strSQL.charAt( x ) == ')' || strSQL.charAt( x ) == '\''  )
        {
          break;
        }

        ++nLastPos;

      }


    }

    if ( nLastPos < strSQL.length() )
    {
      sb.append( strSQL.substring( nLastPos ) );
    }


    return sb.toString();

  }


  /**
   * Create and initialize Java code generator
   * @param strPackage
   * @return
   * @throws Exception
   */
  private VwDVOGen setupCodeGenerator( String strPackage, VwExtendsHierarchyDescriptor extendsHierarchyDescriptor ) throws Exception
  {
    VwExtendsDescriptor extendsDescriptor = null;

    if ( extendsHierarchyDescriptor != null )
    {
      extendsDescriptor = extendsHierarchyDescriptor.getExtendsDescriptor();
    }

    m_mapExistinfClassPropertyDescriptors = null;

    String strFilePath = m_strSourcePath;
    VwFileUtil.makeDirs( strFilePath );

    File fileJava = getJavaFile( strPackage );

    if ( fileJava.exists() && !fileJava.canWrite() )
    {
      m_logger.error( null, "Java DVO class file '" + m_strClassName + "' is read only and cannot be modified" );
      return null;

    }

    if ( fileJava.exists() && m_fNoModifyDVOs )
    {
      m_logger.warn( null, "Not generating Java DVO class '" + m_strClassName + "' because the No modify DVO option is on" );
      return null;
    }

    File fileJavaClass = null;

    if ( m_strClassPath != null )
    {
      fileJavaClass = getJavaClassFile( strPackage );
    }

    if ( fileJavaClass != null && fileJavaClass.exists() )
    {
      ClassLoader ldrParent = this.getClass().getClassLoader();

      File fileClassPath = new File( m_strClassPath );

      URLClassLoader loader = new URLClassLoader( new URL[] { fileClassPath.toURL() }, ldrParent );

      String strJavaClassName = strPackage + "." + m_strClassName;

      Class<?>  clsJava = Class.forName( strJavaClassName, true, loader );
      Class<?> clsSuper = clsJava.getSuperclass();
      PropertyDescriptor[] aProps = null;

      if ( clsSuper != null )
      {
        aProps = Introspector.getBeanInfo( clsJava, clsSuper ).getPropertyDescriptors();
      }
      else
      {
        aProps = Introspector.getBeanInfo( clsJava ).getPropertyDescriptors();
      }

      m_mapExistinfClassPropertyDescriptors = new HashMap<String, PropertyDescriptor>();

      for ( int x = 0; x < aProps.length; x++ )
      {
        m_mapExistinfClassPropertyDescriptors.put( aProps[ x ].getName().toLowerCase(), aProps[ x ] );
      }

    }

    setupCodeOpts();

    m_codeOpts.m_fUseHungarian = m_fUseHungarian;

    String strDVOSuperClass = null;

    if ( extendsDescriptor != null )
    {
      strDVOSuperClass = extendsDescriptor.getSuperClass();
    }
    else
    {
      strDVOSuperClass = m_strDVOSuperClass;
    }

    VwDVOGen dvoGen = new VwDVOGen( m_codeOpts, m_strClassName, strDVOSuperClass, strPackage, m_strSourcePath );
    dvoGen.setUseDirtyObjectDetection( m_fUseDirtObjectDetection );
    return dvoGen;


  } // end setupCodeGenerator()


  private File getJavaClassFile( String strPackage )
  {
    if ( m_strClassPath == null )
      return null;

    String strFilePath = m_strClassPath;

    if ( !strFilePath.endsWith( File.separator ) )
      strFilePath += File.separator;

    strFilePath += strPackage.replace( '.', File.separatorChar );

    String strFullPath = null;

    strFullPath = strFilePath + File.separatorChar + m_strClassName + ".class";
    return  new File(  strFullPath  );

  }

  private File getJavaFile( String strPackage )
  {
    String strFilePath = m_strSourcePath;

    if ( !strFilePath.endsWith( File.separator ) )
      strFilePath += File.separator;

    strFilePath += strPackage.replace( '.', File.separatorChar );

    String strFullPath = null;

    strFullPath = strFilePath + File.separatorChar + m_strClassName + ".java";
    return  new File(  strFullPath  );
  }


  /**
   * Build a java class sourxe file from column definitions in the table
   * @param dvoGen
   * @param listColumns
   * @param tr
   * @param listPrimeKeySuppliers
   * @throws Exception
   */
  private void buildJavaClassFromTableCols( VwDVOGen dvoGen, List<VwColInfo> listColumns, VwTableRelationship tr,
                                            List<VwKeyDescriptor> listPrimeKeySuppliers, VwExtendsHierarchyDescriptor extendsHierarchyDescriptor ) throws Exception
  {

    VwExtendsDescriptor extendsDescriptor = null;

    if ( extendsHierarchyDescriptor != null )
    {
      extendsDescriptor = extendsHierarchyDescriptor.getExtendsDescriptor();

    }

    if ( m_strClassName.equals( "ConfigUser" ))
    {
      System.out.println( "");
    }


    List<VwPropertyDefinition>listPropDefs = m_mapDvoPropdefsByJavaName.get(  m_strClassName.toLowerCase() );

    if ( listPropDefs == null )
    {
      listPropDefs = new ArrayList<VwPropertyDefinition>();
      m_mapDvoPropdefsByJavaName.put( m_strClassName.toLowerCase(), listPropDefs );
    }

    // build class properties for each column in the resultset
    for ( VwColInfo ci : listColumns )
    {
      String strColName = ci.getColumnName();

      // check if this is a primary key column defined in a superclass and omit it if it is

      if ( extendsDescriptor != null  )
      {
       if ( isSuperClassPrimeKeyProperty( strColName, extendsDescriptor )   )
       {
         continue;
       }
       
      }

      if ( isOmitColumn( strColName ))
      {
        continue;
      }


      String strPropName = VwExString.remove( ci.getColumnAliasName(), "\"" );

      if ( propDefExists( strPropName, listPropDefs ))
      {
        continue; // already in list
      }

      DataType eDataType = convertSQLType( ci, m_fUseObjectTypes, m_fTreatChar1AsBoolean );

      int nArraySize = 0;

      if ( isBinary( ci.getSQLType() ) )
      {
        nArraySize = ci.getColSize();
      }

      VwPropertyDefinition propDef = new VwPropertyDefinition();
      propDef.setName( strPropName );
      propDef.setArraySize( nArraySize );
      propDef.setDataType( eDataType );

      if ( eDataType == DataType.VW_DATE )
      {
        propDef.setUserType( "VwDate" );
      }
      else
      if ( eDataType == DataType.DATE )
      {
        propDef.setUserType( "Date" );
      }

      listPropDefs.add( propDef  );

    } // end for()

    // if this is an object graph, generate getters and setters for the object relationships
    if ( tr != null )
    {
      // Create getters/setters methods for related tables
      String strUserType = null;

      for ( VwTableRelationship trRelated : tr.getRelationships().values() )
      {
         checkPrimeKeySupplier( listPrimeKeySuppliers, tr, trRelated );

        DataType eDataType = DataType.USERDEF;
        String strDataName = VwExString.makeJavaName( trRelated.getName(), true );

        if ( propDefExists( strDataName, listPropDefs ))
        {
          continue;
        }

        if ( trRelated.getRelationType() == VwTableRelationship.OBJECT )
        {
          strUserType = VwExString.makeJavaName( trRelated.getName(), false );
        }
        else
        {
          strUserType = VwExString.makeJavaName( trRelated.getName(), false );
          eDataType = DataType.GT_LIST;
        }

        VwPropertyDefinition propDef = new VwPropertyDefinition();
        propDef.setName( strDataName );
        propDef.setDataType( eDataType );
        propDef.setUserType( strUserType );
        listPropDefs.add( propDef  );


      } // end for

    }

    if ( m_mapExistinfClassPropertyDescriptors != null )
    {
      for ( PropertyDescriptor pd :  m_mapExistinfClassPropertyDescriptors.values() )
      {

        DataType eDataType = DataType.USERDEF;

        String strPropName = pd.getName();
        Class<?> clsType = pd.getPropertyType();


        if ( propDefExists( strPropName, listPropDefs ))
        {
          continue;
        }

        String strUserType = null;

        if ( VwBeanUtils.isGenericReturnType( pd ))
        {
          strUserType = VwBeanUtils.getGenericReturnType( pd );
        }
        else
        {
          strUserType = clsType.getName();
        }

        int nPos = strUserType.lastIndexOf( '.' );

        strUserType = strUserType.substring( ++nPos );

        if (clsType == List.class )
        {
          eDataType = DataType.GT_LIST;

        }

        VwPropertyDefinition propDef = new VwPropertyDefinition();
        propDef.setName( strPropName );
        propDef.setDataType( eDataType );
        propDef.setUserType( strUserType );
        listPropDefs.add( propDef  );


      } // end for

    } // end if

    // generate the dvo
    dvoGen.genDvo( listPropDefs, m_logger );
    
  } // end buildJavaClassFromTableCols(


  /**
   * Test to see if a property name in the sub class is in the super class
   *
   * @param strColName The sub class ctable column name to test
   * @param extendsDescriptor The extends descripto with the super class primary key properties
   * @return
   */
  private boolean isSuperClassPrimeKeyProperty( String strColName, VwExtendsDescriptor extendsDescriptor )
  {
    String strPropName = VwExString.makeJavaName( strColName, true );

    for ( String strSuperPrimeKeyPropName : extendsDescriptor.getSuperPrimeKeyProperties() )
    {
      if ( strPropName.equals( strSuperPrimeKeyPropName ) )
      {
        return true;
      }
    }

    return false;
  }


  /**
   * Test existing property def list for the existence of the property name
   * @param strPropName The property name to test
   * @param listPropDefs The list of VwProprtyDefinition objects
   * @return true if strPropName is in the list
   */
  private boolean propDefExists( String strPropName, List<VwPropertyDefinition> listPropDefs )
  {
    for ( VwPropertyDefinition pd : listPropDefs )
    {
      if ( pd.getName().equals( strPropName ))
        return true;
      
    }
    
    return false;
  }


  private boolean isBinary( short sType )
  {
    switch( sType )
    {
      
            
       case Types.BLOB:
       case Types.BINARY:
       case Types.VARBINARY:
       case Types.LONGVARBINARY:
         
            return true;
              

    } // end switch()
    
    return false;

  }


  /**
   * Test to se if column name is in the omit list
   * @param strColName
   * @return
   */
  private boolean isOmitColumn( String strColName )
  {
    if ( m_dlmsOmitColumns == null )
    {
      return false;
    }
    
    return m_dlmsOmitColumns.isIn( strColName );
    
  } // end isOmitColumn()


  /**
   * @param listColumns
   */
  private void buildTableSQL( VwDbObjCommon dbCommon, String strQuerySet, List<VwColInfo> listColumns, VwTableRelationship tr,
                              VwExtendsHierarchyDescriptor extendsHierarchyDescriptor  ) throws Exception
  {

    VwTableSpec tableSpec = null;

    String strPrimeKeyPolicy = null;

    if ( dbCommon instanceof VwTableSpec )
    {
      tableSpec = (VwTableSpec)dbCommon;

      if ( extendsHierarchyDescriptor != null )
      {
        tableSpec.setTableAlias( "a" );

      }

      strPrimeKeyPolicy = tableSpec.getKeyGenerationPolicy();
    }
    else
    {
      // Make a tablespec from an orm tag
      VwOrm orm = (VwOrm)dbCommon;

      tableSpec = new VwTableSpec();
      tableSpec.setName( orm.getBaseTable() );
      tableSpec.setSequenceColName( orm.getSequenceColName() );
      tableSpec.setSequenceName( orm.getSequenceName() );
      tableSpec.setSequenceTableName( orm.getSequenceTableName() );
      tableSpec.setSequenceType( orm.getSequenceType() );
      tableSpec.setKeyGenerationPolicy( orm.getKeyGenerationPolicy() );
      tableSpec.setTableAlias( orm.getBaseTableJoin() );
      strPrimeKeyPolicy = orm.getKeyGenerationPolicy();

      if ( strPrimeKeyPolicy == null )
      {
         strPrimeKeyPolicy = m_strPrimeKeyPolicy;
      }

      m_sbSelect = new StringBuffer( strQuerySet );
      m_sbExists = new StringBuffer( "select 1 from " ).append( tableSpec.getName() );

      if ( tableSpec.getTableAlias() != null )
      {
        m_sbExists.append( " ").append( tableSpec.getTableAlias() );
      }


    }


    if ( dbCommon instanceof VwTableSpec )
    {
      m_sbSelect = genSelectStatement( tableSpec, extendsHierarchyDescriptor );
    }

    String strTableAliasOverride = null;

    if ( extendsHierarchyDescriptor != null )
    {
      strTableAliasOverride = "a"; // Table alias given to base table for primary key
    }

    m_sbUpdate = genUpdateStatement( tableSpec, strTableAliasOverride );

    List<String> listInsertValues = new ArrayList<String>();

    String strTableName = null;
    if ( m_strTableName != null )
    {
      if ( m_strSchema != null )
      {
        strTableName = m_strSchema + "." + m_strTableName;
      }
      else
      {
        strTableName = m_strTableName;
      }
    }

    m_sbInsert = genInsertStatement( tableSpec, strTableAliasOverride, strPrimeKeyPolicy );
    m_sbDelete = new StringBuffer( "delete from " );
    m_sbDelete.append( strTableName );

    if ( tableSpec != null && tableSpec.getTableAlias() != null )
    {
      m_sbDelete.append( " " ).append( tableSpec.getTableAlias() );

    }

    m_listMappingPrimaryKeys = new ArrayList<VwPrimaryKeyGeneration>();
    

    if (  m_strTableName != null )
    {

      if ( m_strTimestampCheck != null )
      {
        doTimestampCheck();
      }

      for ( VwColInfo ci : m_listPrimaryKeys )
      {
        if ( isForeignKey( ci ) )
        {
          continue;
        }

        if ( tr != null && associativeTable( tr ) )
        {
          continue;
        }

        // Primary key type must be an /long/integer UUID type to qualify for auto primary key generation
        if (  !isInregralType(  ci ) && !strPrimeKeyPolicy.equalsIgnoreCase( "uuid" ))
        {
          continue;
        }

        if ( strPrimeKeyPolicy != null && !(strPrimeKeyPolicy.equalsIgnoreCase( "none" )) && !(strPrimeKeyPolicy.equalsIgnoreCase( "internal" )) )
        {
          setupPrimaryKey( ci, m_listMappingPrimaryKeys, strPrimeKeyPolicy, tableSpec  );
        }

      } // end for()

    } // end if
   
  } // end doTableSQL()


  /**
   * Generates a select statement for a query set, a single table or a set of tables that extend other table(s)
   *
   * @param tableSpec
   * @param hierarchyDescriptor
   * @return
   * @throws Exception
   */
  private StringBuffer genSelectStatement( VwTableSpec tableSpec, VwExtendsHierarchyDescriptor hierarchyDescriptor  )  throws Exception
  {
    if ( hierarchyDescriptor != null )
    {
      return genSelectFromHierarchyDescriptor( tableSpec, hierarchyDescriptor );

    }

    VwSqlGenerator sqlGen = setupSqlGenerator();

    VwTableDef selectTable = new VwTableDef( m_db, null, tableSpec.getSchema(), tableSpec.getName() );
    selectTable.setTableAlias( tableSpec.getTableAlias()  );

    m_sbWhere = sqlGen.genPrimaryKeyWhereClause( selectTable, tableSpec.getTableAlias()  );

    m_sbExists = sqlGen.genExists( selectTable, tableSpec.getTableAlias());

    if ( m_strIncludeCols != null )
    {
      return sqlGen.genSelect( selectTable, null, new VwDelimString( m_strIncludeCols ), getSelectClassInto( tableSpec ) );

    }
    else
    if ( m_strExcludeCols != null )
    {
      return sqlGen.genSelect( selectTable, new VwDelimString( m_strExcludeCols ), null, getSelectClassInto( tableSpec ) );

    }

    return sqlGen.genSelect( selectTable, getSelectClassInto( tableSpec ) );


  }

  /**
   *
   * @param tableSpec
   * @param strTableAliasOverride
   * @return
   * @throws Exception
   */
  private StringBuffer genUpdateStatement( VwTableSpec tableSpec,  String strTableAliasOverride ) throws Exception
  {
     VwSqlGenerator sqlGen = setupSqlGenerator();

     VwTableDef updateTable = new VwTableDef( m_db, null, tableSpec.getSchema(), tableSpec.getName() );

     if ( strTableAliasOverride != null )
     {
       updateTable.setTableAlias( strTableAliasOverride );

     }
     else
     {
       updateTable.setTableAlias( tableSpec.getTableAlias() );
     }

     if ( m_strIncludeCols != null )
     {
       return sqlGen.genUpdate( updateTable, null, new VwDelimString( m_strIncludeCols ) );
     }
     else
     if ( m_strExcludeCols != null )
     {
       return sqlGen.genUpdate( updateTable, new VwDelimString( m_strExcludeCols ), null );
     }

    return sqlGen.genUpdate( updateTable );

     // No include or excludes
  }


  /**
   *
   * @param tableSpec
   * @param strTableAliasOverride
   * @return
   * @throws Exception
   */
  private StringBuffer genInsertStatement( VwTableSpec tableSpec,  String strTableAliasOverride, String strPrimaryKeyPolicy ) throws Exception
  {
     VwSqlGenerator sqlGen = setupSqlGenerator();

     VwTableDef insertTable = new VwTableDef( m_db, null, tableSpec.getSchema(), tableSpec.getName() );

     if ( strTableAliasOverride != null )
     {
       insertTable.setTableAlias( strTableAliasOverride );

     }
     else
     {
       insertTable.setTableAlias( tableSpec.getTableAlias() );
     }

     if ( m_strIncludeCols != null )
     {
       return sqlGen.genInsert( insertTable, null, new VwDelimString( m_strIncludeCols ), strPrimaryKeyPolicy );
     }
     else
     if ( m_strExcludeCols != null )
     {
       return sqlGen.genInsert( insertTable, new VwDelimString( m_strExcludeCols ), null, strPrimaryKeyPolicy );
     }

    return sqlGen.genInsert( insertTable, strPrimaryKeyPolicy );

     // No include or excludes
  }

  /**
   * Create the VwSqlGeneraror
   * @return
   * @throws Exception
   */
  private VwSqlGenerator setupSqlGenerator() throws Exception
  {
    VwSqlGenerator sqlGen = new VwSqlGenerator( m_db );
    sqlGen.setLineIndentSpaces( m_nIndentLength );

    return sqlGen;
  }


  /**
   * This method uses the hierarchy descriptor to generate a joined select from all table in the extends chain
   * @param hierarchyDescriptor
   *
   * @return
   */
  private StringBuffer genSelectFromHierarchyDescriptor( VwDbObjCommon dbCommon, VwExtendsHierarchyDescriptor hierarchyDescriptor ) throws Exception
  {
    // Create the tabledefs from the hierarchy stack
    VwStack<VwDbObjCommon>  stack = hierarchyDescriptor.getStackHierarchy();

    VwDbObjCommon[] aTableSpecs = stack.toArray();

    List<VwTableDef>listTableDefs = new ArrayList<>(  );;

    for ( int x = 0; x < aTableSpecs.length; x++ )
    {
      String strTableName = null;

      if ( aTableSpecs[ x ] instanceof VwTableSpec )
      {
        strTableName =  ((VwTableSpec)aTableSpecs[ x ]).getName();
      }
      else
      if ( aTableSpecs[ x ] instanceof VwOrm )
      {
        strTableName = ((VwOrm)aTableSpecs[ x ]).getBaseTable();
      }
      
      listTableDefs.add( new VwTableDef( m_db, null, null, strTableName ) );
    }


    VwSqlGenerator sqlGen = new VwSqlGenerator( m_db );

    sqlGen.setLineIndentSpaces( 22 );

    m_sbWhere = sqlGen.genPrimaryKeyWhereClause( listTableDefs.get( 0 ), "a" );

    // Get the last table in the hierarchy for the exists
    m_sbExists = sqlGen.genExists( listTableDefs.get( listTableDefs.size() - 1 ), "a" );

    if ( m_strIncludeCols != null )
    {
      return sqlGen.genJoinedSelect( listTableDefs,  null, new VwDelimString( m_strIncludeCols ), getSelectClassInto( dbCommon ) );
    }
    else
    if ( m_strExcludeCols != null )
    {
      return sqlGen.genJoinedSelect( listTableDefs,  new VwDelimString( m_strExcludeCols ), null, getSelectClassInto( dbCommon ) );
    }

    return sqlGen.genJoinedSelect( listTableDefs, getSelectClassInto( dbCommon ) );

  }

  /**
   * Builds a select class into statement for the java class that will be the target of the select statement column result data
   * @param dbCommon
   * @return
   */
  private String getSelectClassInto( VwDbObjCommon dbCommon )
  {
    StringBuffer sbSelectInto = new StringBuffer();
    String strClassName = getClassName( dbCommon );

    if ( strClassName == null )
    {
      strClassName = VwExString.makeJavaName( m_strTableName, false  );
    }

    if ( strClassName.indexOf( '.' ) < 0 )
    {
      sbSelectInto.append( m_strPackage ).append( ".");   // Class name does not contain a package so use the base package for the set
    }


    sbSelectInto.append( strClassName ).append( ";" );

    return sbSelectInto.toString();


  }

  /**
   * Gets the class name by definition in the xml document
   * @param dbCommon
   * @return
   */
  public String getClassName( VwDbObjCommon dbCommon )
  {
    if ( dbCommon.getInheritClassName() != null )
    {
      return dbCommon.getInheritClassName();
    }
    else
    if ( dbCommon.getClassName() != null )
    {
      return dbCommon.getClassName();
    }

    return null;

  }


  /**
   * Returns true if a column is an integral type
   * @param ci
   * @return
   */
  private boolean isInregralType( VwColInfo ci )
  {
    switch( ci.getSQLType() )
    {
      case Types.BIGINT:
      case Types.INTEGER:
        
           return true;
           
      case Types.NUMERIC:
        
           if ( ci.getNbrDecimalDigits() == 0 )
              return true;
        
    }
    
    return false;
  }


  private boolean isForeignKey( VwColInfo ci )
  {
    
    for ( Iterator ifkeys = m_listForeignKeys.iterator(); ifkeys.hasNext();  )
    {
      VwForeignKeyInfo fki = (VwForeignKeyInfo)ifkeys.next();
      
      if ( ci.getColumnName().equalsIgnoreCase( fki.getFkColName() ))
        return true;
      
    }
    return false;
  }


  /**
   * @param proc
   * @param listProcColumns
   */
  private void buildProcSQL( VwProcedure proc, List listProcColumns )
  {
    m_sbProc.append( "{ call " ).append( proc.getName() ).append( "( " );
    
    for ( Iterator iParams = listProcColumns.iterator(); iParams.hasNext(); )
    {
      VwColInfo ci = (VwColInfo)iParams.next();
      m_sbProc.append( ":" ).append( VwExString.makeJavaName( ci.getColumnName(), true) );
      
      if ( iParams.hasNext() )
        m_sbProc.append( "," );
      
    } // end for()
    
    m_sbProc.append( " ) }" );
    
    
  } // end buildProcSQL()

  /**
   * @param dvoGen
   * @param listProcColumns
   */
  private void buldJavaClassFromProcCols( VwDVOGen dvoGen, List<VwColInfo> listProcColumns ) throws Exception
  {
    List<VwPropertyDefinition> listPropDefs = new ArrayList<VwPropertyDefinition>();
    
    for ( VwColInfo ci : listProcColumns )
    {
      DataType eDataType = convertSQLType( ci, m_fUseObjectTypes, m_fTreatChar1AsBoolean );
      
      String strPropName = VwExString.makeJavaName( ci.getColumnName(), true );
      VwPropertyDefinition propDef = new VwPropertyDefinition();
      propDef.setName( strPropName );
      propDef.setDataType( eDataType );
      
     }
    
      
    dvoGen.genDvo( listPropDefs, m_logger );
    
  } // end doProcColumns


  
  /**
   * setup for stored proc call
   * @param proc
   * @param sqlMapping
   */
  private void doProc( VwProcedure proc, StringBuffer sbProc, VwSqlMapping sqlMapping  )
  {
    VwSqlStatement sqlStmt = new VwSqlStatement();
    VwSql sql = new VwSql();
    sql.setId( "proc" );
    
    sql.setBody( sbProc.toString() );
    List<VwSql> listSql = new ArrayList<VwSql>();
    listSql.add( sql );
    sqlStmt.setSql( listSql );
    sqlMapping.setProc( sqlStmt );
    
  } // end doProc()
  
  /**
   * Test to see if this table is an associative table (primary keys are foreign keys )
   * @param tr The relationship info
   * @return
   */
  private boolean associativeTable( VwTableRelationship tr )
  {

    List<VwForeignKeyInfo> listForeignKeys = tr.getForeignKeys();
    if ( listForeignKeys == null )
      return false;

    List<VwColInfo> listPrimaryKeys = tr.getPrimeKeys();
    if ( listPrimaryKeys == null )
      return false;
    
    if ( listForeignKeys.size() != listPrimaryKeys.size() )
      return false;
    
    for ( VwColInfo ci : listPrimaryKeys  )
    {
       
      boolean fMatch = false;
      for ( VwForeignKeyInfo fki : listForeignKeys )
      {
        
        if ( fki.getFkColName().equals( ci.getColumnName() )  )
        {
          fMatch = true;
          break;
        }
      }
      
      if ( !fMatch )
        return false;
      
    } // end for 
    return true;
  }

  /**
   * Build an VwPrimaryKey object entry for the DAO document
   * @param ci The VwColInfo describing the primary key column
   * @param listMappingPrimaryKeys The list to the VwPrimeKey object to
   * @param strPrimeKeyPolicy
   * @param tableSpec The ORM table definition from the .xml mapping doc
   */
  private void setupPrimaryKey( VwColInfo ci, List<VwPrimaryKeyGeneration> listMappingPrimaryKeys, String strPrimeKeyPolicy, VwTableSpec tableSpec )
  {

   // create the entries for the xsm doc
   
    if ( primaeyKeySupplied( ci )) // no entry if this primary key column supplied by a parent object
    {
      return;
    }

    String strSeqTableName = null;
    String strSeqName = null;
    String strSeqColName = null;

    strSeqTableName = tableSpec.getSequenceTableName();
    strSeqName = tableSpec.getSequenceName();
    strSeqColName = tableSpec.getSequenceColName();

    if ( strSeqTableName == null )
    {
      strSeqTableName = m_strSeqTableName;

    }

    if ( strSeqName == null )
    {
      strSeqName = m_strSeqName;
    }

    if ( strSeqColName == null )
    {
      strSeqColName = m_strSeqColName;
    }

    VwPrimaryKeyGeneration pkey  = new VwPrimaryKeyGeneration();
    pkey.setKeyGenerationPolicy( strPrimeKeyPolicy );
    pkey.setBeanProperty( formatPropName(VwExString.makeJavaName( ci.getColumnName(), true ) ) );
    
   
    if ( strPrimeKeyPolicy != null )
    {
      if ( strPrimeKeyPolicy.equalsIgnoreCase( "table_seq"))
      {
        pkey.setSequenceTableName( strSeqTableName );
        
        if ( strSeqColName == null )
        {
          strSeqColName = "XXPlease assign Seq column NameXX";
        }
        else
        {
          strSeqColName =  checkForPatternGeneration( ci, strSeqColName);
        }
        
        pkey.setSequenceColName( strSeqColName );
      }
      else
      if ( strPrimeKeyPolicy.equalsIgnoreCase( "oracle_seq") || strPrimeKeyPolicy.equalsIgnoreCase( "postgres_seq"))
      {
        if ( strSeqName == null )
        {
          strSeqName = "XXPlease assign Seq NameXX";
        }
        else
        {
          strSeqName = checkForPatternGeneration( ci, strSeqName);
        }
        
        pkey.setSequenceName( strSeqName );
        
      }
    }
    
    listMappingPrimaryKeys.add( pkey );
    
  }

  /**
   * Returns true if the primary key / or a piece of a composite primary key
   * is supplied by a parent object
   * 
   * @param ci The primary key info class
   * @return true if the primary key / or a piece of a composite primary key
   * is supplied by a parent object, false otherwise
   */
  private boolean primaeyKeySupplied( VwColInfo ci )
  {
    String strPK = VwExString.makeJavaName( ci.getColumnName(), true ).toLowerCase();
    String strTable = VwExString.makeJavaName( ci.getTableName(), true ).toLowerCase();
    
    String strSuppliedCol = (String)m_mapPrimeKeysSupplied.get( strTable );
    if ( strSuppliedCol != null && strPK.equals( strSuppliedCol ))
    {
      return true;
    }
    
    return false;
    
  }

  /**
   * Returns a a hierarchy descriptor the table extends hierarchy
   * @param dbCommon The table spec of the starting point
   * @return
   * @throws Exception
   */
  private VwExtendsHierarchyDescriptor getExtendsDescriptor( VwTableSpec dbCommon ) throws Exception
  {
    VwExtendsDescriptor vwExtendsDescriptor = new VwExtendsDescriptor();
    VwExtendsHierarchyDescriptor extendsHierarchyDescriptor= new VwExtendsHierarchyDescriptor( vwExtendsDescriptor );

    String strExtendsTable = dbCommon.getExtends();

    VwDbObjCommon superTable = getSqlTableMapping( strExtendsTable );

    if ( superTable == null )
    {
      throw new Exception( "The extension table: " +strExtendsTable + " defined im mapping table: " + dbCommon.getName() + " does not exists" );
    }

    String strSuperClass = superTable.getInheritClassName();

    // If no inherrited class define, get it from the extends table name
    if (  strSuperClass == null )
    {
      strSuperClass = m_strPackage + "." + VwExString.makeJavaName( strExtendsTable, false );
    }

    vwExtendsDescriptor.setSuperClass( strSuperClass );

    String strTableName = dbCommon.getName();

    List<VwForeignKeyInfo> listForeignKeys = m_db.getForeignKeys( null, null, strTableName );

    if ( listForeignKeys.size() == 0 )
    {
      throw new Exception( "Table: " + strTableName + " must define a foreign key to its super table: " + strExtendsTable );
    }

    List<VwColInfo>listPrimeKeys = m_db.getPrimaryKeys( null, null, strTableName );

    if ( listPrimeKeys.size() == 0 )
    {
      throw new Exception( "Table: " + strTableName + " must define a primary key" );

    }

    // Get the foreign key in this table that is the primary key of the super class table
    List<String>listSuperPrimeKeyProperties = new ArrayList<>(  );;
    List<String>listPrimeKeyProperties = new ArrayList<>(  );;

    int nKeyNbr = -1;

    for ( VwForeignKeyInfo fki : listForeignKeys )
    {
      ++nKeyNbr;

      if ( isKeyAPrimeKey( fki, listPrimeKeys ) )
      {
        listSuperPrimeKeyProperties.add( VwExString.makeJavaName( fki.getFkColName(), true ) );
        listPrimeKeyProperties.add( VwExString.makeJavaName( listForeignKeys.get( nKeyNbr ).getPkColName(), true ) );
      }
    }

    vwExtendsDescriptor.setSuperPrimeKeyProperties( listSuperPrimeKeyProperties );
    vwExtendsDescriptor.setPrimeKeyProperties( listPrimeKeyProperties );


    // Now get all super classes from this point
    VwStack<VwDbObjCommon>stackTableHierarchy = extendsHierarchyDescriptor.getStackHierarchy();
    stackTableHierarchy.push( dbCommon );
    getSuperClasses( dbCommon, stackTableHierarchy );

    return extendsHierarchyDescriptor;

  }

  /**
   * This method walks up the table extends chain and build a stack of the table extends hierarchy
   * @param tableSpec
   * @param stackTableHierarchy
   */
  private void getSuperClasses( VwDbObjCommon tableSpec, VwStack<VwDbObjCommon>stackTableHierarchy )
  {
    String strExtendsTable = tableSpec.getExtends();

    if ( strExtendsTable == null )
    {
      return;
    }

    VwDbObjCommon superTable = getSqlTableMapping( strExtendsTable );

    stackTableHierarchy.push( superTable );

    getSuperClasses( superTable, stackTableHierarchy );

  }


  /**
   *
   * @param fki
   * @param listPrimeKeys
   * @return
   */
  private boolean isKeyAPrimeKey( VwForeignKeyInfo fki, List<VwColInfo>listPrimeKeys )
  {
    for ( VwColInfo primeKeyInfo : listPrimeKeys )
    {
      if ( primeKeyInfo.getColumnName().equals( fki.getFkColName() ) )
      {
        return true;
      }
    }

    return false;

  }

  /**
   *
   * @param tr
   * @return
   */
  private List<VwKeyDescriptor> checkForeignKeys( VwTableRelationship tr )
  {
    VwKeyDescriptor fkeyDesc = null;
    List<VwForeignKeyInfo> listForeignKeys = tr.getForeignKeys();
    List<VwKeyDescriptor> listForeignKeyDescriptors = null;
    if ( listForeignKeys != null )
    {
      listForeignKeyDescriptors = new ArrayList<VwKeyDescriptor>();

      for ( VwForeignKeyInfo fki : listForeignKeys )
      {
 
        // Don't add a foreign key entry if there is no object relationship
        m_mapProcessedParentTables.clear();
        
        if ( parentSupplied( m_trParent, fki ) )
        {
          continue;
        }
        
        // Make sure we have an object relationship defined for this foreign key
        Map mapRelatedTables = tr.getRelationships();
        if ( mapRelatedTables == null || mapRelatedTables.size() == 0 )
        {
          continue;
        }
        
        boolean hasRelationShip = false;  
        for ( Iterator iRelatedTables = mapRelatedTables.keySet().iterator(); iRelatedTables.hasNext(); )
        {
          String strRelatedTableName = (String)iRelatedTables.next();
          
          if ( fki.getPkTableName().equalsIgnoreCase( strRelatedTableName ))
          {
            hasRelationShip = true;
            break;
          }
        }
        
        if ( !hasRelationShip )
        {
          continue;
        }
        
        fkeyDesc =  new VwKeyDescriptor();
        String strFkTableName = VwExString.makeJavaName( fki.getPkTableName(), true);
        String strFkColName = VwExString.makeJavaName( fki.getPkColName(), true);
        
        fkeyDesc.setBeanProperty( strFkTableName );
        fkeyDesc.setPrimeKeyProperty( strFkColName );
        fkeyDesc.setForeignKeyProperty( VwExString.makeJavaName( fki.getFkColName(), true) );
        listForeignKeyDescriptors.add( fkeyDesc );
       
      } // end for()
      
    } // end if
    
    return listForeignKeyDescriptors;
    
  }

  /**
   * Checkes to see if thos foreign key is supplied by a parent object
   * @param tr The Table relationship object
   * @param fki The foreign key colum to test
   * @return
   */
  private boolean parentSupplied( VwTableRelationship tr, VwForeignKeyInfo fki )
  {
    Map mapRelatedTables = tr.getRelationships();
   
    for ( Iterator iRelated = mapRelatedTables.values().iterator(); iRelated.hasNext(); )
    {
      VwTableRelationship trRelated = (VwTableRelationship)iRelated.next();
      m_mapProcessedParentTables.put( trRelated, null );
      if ( trRelated.getName().equalsIgnoreCase( fki.getFkTableName() ) && tr.getName().equalsIgnoreCase( fki.getPkTableName() ) )
        return true;
    }

    for ( Iterator iRelated = mapRelatedTables.values().iterator(); iRelated.hasNext(); )
    {
      VwTableRelationship trRelated = (VwTableRelationship)iRelated.next();
      if ( m_mapProcessedParentTables.containsKey( trRelated ) )
          continue;
      
      if ( parentSupplied( trRelated, fki ) )
        return true;
    }
    
    return false;
  }

  
  /**
   * 
   * @param listPrimeKeySuppliers
   * @param tr
   * @param trRelated
   */
  private void checkPrimeKeySupplier( List<VwKeyDescriptor> listPrimeKeySuppliers, VwTableRelationship tr, VwTableRelationship trRelated )
  {

    if (trRelated.getForeignKeys() == null )
      return;

    // I can't be a primary key supplier to this table if i have a foreign key to it
    for ( VwForeignKeyInfo fki : tr.getForeignKeys() )
    {
      if ( fki.getPkTableName().equalsIgnoreCase( trRelated.getName() ))
        return;
      
    }

    VwDelimString dlmsPrimeKeyProps = new VwDelimString();
    VwDelimString dlmsForiegnKeyProps = new VwDelimString();

    Map<String,String>mapDups = new HashMap<String, String>();
    
    for ( VwColInfo ci : tr.getPrimeKeys() )
    {
      String strPK = ci.getColumnName();
      String strTableName = ci.getTableName();
      
      
      for ( VwForeignKeyInfo fki : trRelated.getForeignKeys() )
      {
        String strFK = fki.getPkColName();
        String strPkTableName = fki.getPkTableName();
        if ( strPkTableName.equalsIgnoreCase( strTableName ) && strPK.equalsIgnoreCase( strFK ))
        {
          m_mapPrimeKeysSupplied.put( VwExString.makeJavaName( trRelated.getName(), false ).toLowerCase(),
                                      VwExString.makeJavaName( fki.getFkColName(), false ).toLowerCase() );

          String strPkPropName = VwExString.makeJavaName( strPK, true );
          
          if ( mapDups.containsKey( strPkPropName  ))
            continue;
          
          mapDups.put( strPkPropName, null );
          
          dlmsPrimeKeyProps.add( strPkPropName );
          dlmsForiegnKeyProps.add( VwExString.makeJavaName( fki.getFkColName(), true ) );

        }
      }
    }
    
    if ( dlmsPrimeKeyProps.count() == 0 )
      return;
    
    VwKeyDescriptor kdesc = new VwKeyDescriptor();
    kdesc.setBeanProperty( VwExString.makeJavaName( trRelated.getName(), true ));
    kdesc.setPrimeKeyProperty( dlmsPrimeKeyProps.toString() );
    kdesc.setForeignKeyProperty( dlmsForiegnKeyProps.toString() );
    listPrimeKeySuppliers.add( kdesc );

  }



  /**
   * Make name a java formatted property name bu assuring first leetr is lower case
   * @param strName
   * @return
   */
  private String formatPropName( String strName )
  { return Character.toLowerCase( strName.charAt( 0 ) ) + strName.substring( 1 ); }

  /**
   * Check for a pattern name characters in name and expand them
   * current paaterns ar %t (table name) and %c (column name)
   * 
   * @param ci The VwColInfo containg the table and column name
   * 
   * @param strSeqName the actual name or a name with pattern characters that will be expanded
   * @return
   */
  private String checkForPatternGeneration( VwColInfo ci, String strSeqName )
  {
    if ( strSeqName.indexOf( '%') < 0 )
      return strSeqName;
    
    StringBuffer sbSeqName = new StringBuffer();
    
    VwDelimString dlms = new VwDelimString( "%", strSeqName );
    
    for ( Iterator iPieces = dlms.iterator(); iPieces.hasNext(); )
    {
      String strPiece = (String)iPieces.next();
      
      char ch = strPiece.charAt( 0 );
      
      switch( ch )
      {
        case 't':
             sbSeqName.append( ci.getTableName() );
             break;
             
        case 'c':
          sbSeqName.append( ci.getColumnName() );
          break;
        
      }
      
      sbSeqName.append( strPiece.substring( 1 ).trim() );
      
    }
    
    return sbSeqName.toString() ;
    
  } // end checkForPatternGeneration()

  /**
   * @return
   */
  private void setupCodeOpts()
  {
    if ( m_strCompanyName == null )
      m_strCompanyName = "V o z z w a r e   L L C.";
    else
      m_strCompanyName = formatCompanyName();
    
    m_codeOpts.m_strName = m_strCompanyName;
    VwDate today = new VwDate();
    
    m_codeOpts.m_strCopyright = "Copyright(c) " + today.getYear() + " By\n" + m_codeOpts.m_strName +
     "\nA L L   R I G H T S   R E S E R V E D";

    m_codeOpts.m_strAuthor = m_strAuthor;

    m_codeOpts.m_sPubOrder = 1;
    m_codeOpts.m_sPrivOrder = 2;
    m_codeOpts.m_sProtOrder = 3;
    m_codeOpts.m_sDefOrder = 4;
    
  }

  public void genFromRelationships( VwOrm orm ) throws Exception
  {
    m_mapProcessedRelatedTables = new HashMap<String,String>();
    
    handleOmitColumns( orm  );
    
    if ( orm.getTable() != null )
    {
      setupOrmTableMap( orm.getTable() );
    }
    
    m_strSchema = orm.getSchema();
    
    m_strBaseTable = orm.getBaseTable();

    if ( m_strBaseTable == null )
    {
      throw new Exception( "The baseTable attribute was not specified, it could be mis-spelled or just omitted. ");
    }

    if ( !inIncludeList( "orm", m_strBaseTable ))
    {
      m_logger.info( null, "Skipping orm entry '" + m_strBaseTable + "' because it was not in the include list");
      return;
      
    }

    int nPos = m_strBaseTable.indexOf( '.' );
    
    if ( nPos > 0 )
    {
      m_strSchema = m_strBaseTable.substring( 0, nPos );
      m_strBaseTable = m_strBaseTable.substring( ++nPos );
      orm.setBaseTable( m_strBaseTable );
      
    }

    if ( orm.getNoDVO() != null && orm.getNoDVO().equalsIgnoreCase( "true" ))
    {
      m_fGenCode = false;
    }

    m_logger.info( null, "" ); // generate blank line

    if ( m_strSchema != null )
    {
      m_logger.info( null, "Processing object relational mapping for base table '" + m_strSchema + "." + m_strBaseTable + "'");
    }
    else
    {
      m_logger.info( null, "Processing object relational mapping for base table '" + m_strBaseTable + "'");
    }

    Map<String, String> mapTable2Class = buildTableToClassMap( m_strBaseTable, orm );

    VwTableRelationship tr = null;
    
    String strIncludeTables = orm.getIncludeTables();
    String strExcludeTables = orm.getExcludeTables();
    String strBaseTableJoins = orm.getBaseTableJoin();

    VwTableRelationshipBuilder trb = new VwTableRelationshipBuilder( m_db, null, m_strSchema, m_strBaseTable );
    
    if ( orm.getBaseRelationshipsOnly() != null && orm.getBaseRelationshipsOnly().equals( "true" ))
    {
      trb.setBaseTableRelationshipsOnly( true );
    }
    
    if ( orm.getRelationshipLevel() != null )
    {
      trb.setRelationshipLevel( Integer.parseInt( orm.getRelationshipLevel() ) );
    }
    
    if ( strIncludeTables != null )
    {
      trb.setIncludeTables( strIncludeTables  );
    }
    else
    if ( strExcludeTables != null )
    {
      trb.setExcludeTables( strExcludeTables );
    }
      
    if ( orm.getParentTables() != null )
    {
      trb.setParentTables( orm.getParentTables() );
    }
     
    tr = trb.getRelatedTables();
    
    if ( tr == null )
    {
      m_logger.error( null, "No columns exist for table '" + m_strBaseTable + "'. This is most likly due to an invalid table name. ");
      return;
      
    }

    if ( orm.getMappingTableConstraint() != null )
    {
      setMappingTableConstraints( orm.getMappingTableConstraint(), tr );

    }
    m_trParent = tr;


    handleOmitColumns( orm );
    String strQuerySet = VwSqlStmtGen.genSelectSet(  tr, mapTable2Class, m_strPackage, false, strBaseTableJoins, m_nIndentLength, m_dlmsOmitColumns.toStringArray() );

    genObjectGraph( tr, strQuerySet, orm );


  } // end genFromRelationships()

  /**
   * Set the VwMappingTableConstraint defined for any related tables
   * @param listMappingConstraints
   * @param trBase
   * @throws Exception
   */
  private void setMappingTableConstraints( List<VwMappingTableConstraint>listMappingConstraints, VwTableRelationship trBase ) throws Exception
  {

    for ( VwMappingTableConstraint mappingConstraint : listMappingConstraints )
    {
      String strTableName = mappingConstraint.getTableName();

      VwTableRelationship trMap = trBase.getTableRelationship( strTableName );

      if ( trMap == null )
      {
        throw new Exception( "The mappingTableConstraing table name: " + strTableName + " could not be found in the orm table relation list");
      }

      trMap.setMappingTableConstraint( mappingConstraint );

    }
  }


  /**
   * Build map of table to class names if overrrides were specified
   * @param strTable
   * @param objCommon
   * @return
   */
  private Map<String, String> buildTableToClassMap( String strTable, VwDbObjCommon objCommon )
  {

    Map<String, String> mapTable2Class = new HashMap<String, String>( );

    String strClassOverrideName = objCommon.getInheritClassName();

    if ( strClassOverrideName != null )
    {
      mapTable2Class.put( strTable, strClassOverrideName );
    }

    if ( ! (objCommon instanceof VwOrm) )
    {
      return mapTable2Class;
    }

    List<VwTableSpec>listRelatedTableSpecs  = ((VwOrm)objCommon).getTable();

    // Build table2Class override map if needed

    if ( listRelatedTableSpecs != null )
    {
      for ( VwTableSpec tblSpec : listRelatedTableSpecs )
      {
        strClassOverrideName = tblSpec.getInheritClassName();
        if ( strClassOverrideName != null )
        {
          mapTable2Class.put( tblSpec.getName().toLowerCase(), strClassOverrideName );
        }
      }
    }

    return mapTable2Class;

  }


  /**
   * Remove any columns from generation if the omitt columns was specified
   * @param objCommon Super class of all the object generation types
   */
  private void handleOmitColumns( VwDbObjCommon objCommon )
  {
    String strOmitColumns = objCommon.getOmitColumns();

    if ( strOmitColumns == null )
    {
      strOmitColumns = m_spec.getOmitColumns();
    }

    if ( strOmitColumns == null )
    {
      return;
    }

    VwDelimString dlms = m_dlmsOmitColumns = new VwDelimString( strOmitColumns );;
    
  }

  /**
   *
   * @param spec
   * @param tr
   * @param orm
   * @param fNoModifyDVOs
   * @param nGenFlags
   * @return
   * @throws Exception
   */
  public String genFromRelationships( VwSqlMappingSpec spec, VwTableRelationship tr, VwOrm orm, boolean fNoModifyDVOs, int nGenFlags ) throws Exception
  {
    m_spec = spec;
    m_trParent = tr;
    
    m_strSchema = orm.getSchema();
    
    handleOmitColumns( orm  );

    if ( orm.getTable() != null )
    {
      setupOrmTableMap( orm.getTable() );
    }
    
    if ( orm.getNoDVO() != null && orm.getNoDVO().equalsIgnoreCase( "true" ))
    {
      m_fGenCode = false;
    }
    
    m_fNoModifyDVOs = fNoModifyDVOs;
    
    setCodeGenFlags( nGenFlags );
    
    m_logger = VwLogger.getInstance();

    m_logger.info( null, "Processing object relational mapping for base table '" + tr.getName() + "'");

    setup();

    Map<String, String> mapTable2Class = buildTableToClassMap( orm.getBaseTable(), orm );

    handleOmitColumns( orm );
    String strQuerySet = VwSqlStmtGen.genSelectSet( tr, mapTable2Class ,m_strPackage, false, null, 25, m_dlmsOmitColumns.toStringArray() );
    genObjectGraph( tr , strQuerySet, orm );
    
    m_mapProcessedRelatedTables.clear();
    
    if ( m_fDVOOnly )
    {
      return strQuerySet;
    }
    
    // Write the DAO Xnl document
    writeXSMDocument();
    
    return strQuerySet;
    
  } // end genFromRelationships()


  /**
   * Put orm defined tbale object in map
   * @param listTables
   */
  private void setupOrmTableMap( List listTables )
  {
    m_mapOrmTables.clear();
    
    for ( Iterator iTables = listTables.iterator(); iTables.hasNext(); )
    {
      VwTableSpec table = (VwTableSpec)iTables.next();
      m_mapOrmTables.put( table.getName().toLowerCase(), table );
    }
  } // end setupOrmTableMap()


  /**
   * 
   * @param tr
   * @throws Exception
   */  
  private void genObjectGraph( VwTableRelationship tr, String strQuerySet, VwOrm orm ) throws Exception
  {

    if ( m_mapProcessedRelatedTables == null )
      m_mapProcessedRelatedTables = new HashMap<String,String>();
    
    if ( m_mapProcessedRelatedTables.containsKey( tr.getName().toLowerCase()) )
        return;
    
    m_mapProcessedRelatedTables.put(tr.getName().toLowerCase(), null );
    
    List<VwFinder> listFinders = null;
    if ( orm != null )
    {
      listFinders = orm.getFinder();

      if ( listFinders == null )
      {
        listFinders = new ArrayList();
      }

      addFindByPrimaryKey( orm, tr, listFinders );
    }

    genCode( orm, tr, strQuerySet, listFinders );
    
    // now generate the code for the related tables
    
    for ( Iterator iRelated = tr.getRelationships().values().iterator(); iRelated.hasNext(); )
    {
      VwTableRelationship trRelated = (VwTableRelationship)iRelated.next();
      if ( trRelated.getPrimeKeys() == null )
        continue;
      
      genObjectGraph( trRelated, null, null );      
    }
    
  } // end genObjectGraph()


  /**
   * Search the list of finders and if there is not one for "primaryKey" add it to the list
   * @param orm
   * @param listFinders
   */
  private void addFindByPrimaryKey( VwOrm orm, VwTableRelationship tr, List<VwFinder> listFinders ) throws Exception
  {

    for ( VwFinder finder : listFinders )
    {
      if ( finder.getId().equals( "primaryKey" ) )
      {
        return;
      }

    }

    List<VwColInfo> listPrimeKeys = tr.getPrimeKeys();

    if ( listPrimeKeys == null )
    {
      throw new Exception( "No primary keys were defined for Orm Table: " + orm.getBaseTable() + " and is a requirement");
    }

    VwFinder findByPrimaryKey = new VwFinder();
    findByPrimaryKey.setId( "primaryKey" );
    findByPrimaryKey.setSqlRef( "base" );


    StringBuffer sbWhere = new StringBuffer();

    findByPrimaryKey.setWhere( "where "  );

    int nKeyCount = 0;

    for ( VwColInfo ci : listPrimeKeys )
    {
      if ( ++nKeyCount > 1 )
      {
        sbWhere.append( " and ");
      }

      if ( ci.getColumnAliasName() != null )
      {
        sbWhere.append( ci.getColumnAliasName() ).append( ".");

      }

      sbWhere.append( ci.getColumnName() ).append( " = :").append( VwExString.makeJavaName( ci.getColumnName(), true ));
    }

    findByPrimaryKey.setWhere( sbWhere.toString() );;

    listFinders.add( 0, findByPrimaryKey );
  }


  /**
   * Determins if the column name is part of or is the primary key
   * 
   * @param listPrimeKeys
   * @param strColumnName
   * @return
   */
  private boolean isPrimaryKey( List listPrimeKeys, String strColumnName )
  {
    if ( listPrimeKeys == null )
      return false;
    
    for ( Iterator iKeys = listPrimeKeys.iterator(); iKeys.hasNext(); )
    {
      VwColInfo ci = (VwColInfo)iKeys.next();
      String strKey = (String)ci.getColumnName();
      if ( strKey.equals( strColumnName ))
        return true;
      
    }
    
    return false;
    
  } // end isPrimaryKey()


  /**
   * Test if column name is a foreign key
   * @param listForeignKeys
   * @param strColumnName
   * @return
   */
  private boolean isForeignKey( List listForeignKeys, String strColumnName )
  {
    if ( listForeignKeys == null )
      return false;
    
    for ( Iterator iKeys = listForeignKeys.iterator(); iKeys.hasNext(); )
    {
      VwForeignKeyInfo fki = (VwForeignKeyInfo)iKeys.next();
      String strKey = (String)fki.getPkColName();
      if ( strKey.equals( strColumnName ))
        return true;
      
    }
    
    return false;
    
  } // end isPrimaryKey()



  /**
   * Format company name for dvo comment copyright header
   * @return formated String for company name
   */
  private String formatCompanyName()
  {
    VwDelimString dlms = new VwDelimString( " ", m_strCompanyName );
    StringBuffer sb = new StringBuffer();
    int nPieceCount = 0;
    while ( dlms.hasMoreElements() )
    {
      if ( nPieceCount++ > 0 )
        sb.append( "   " );
      
      String strPiece = (String)dlms.getNext();
      strPiece = VwExString.stretch( strPiece, 1 );
      sb.append( strPiece );
    }
    
    return sb.toString();
    
  } // end formatCompanyName()


  /**
   *
   *
   * @param nType One of the SQL Types
   * @param nScale Nbr of decimals if numeric
   */

  /**
   *
   * Convert SQL types to Codegen types
   * @param ci Database column info object from schema meta data
   * @param fUseObjectTypes If true use Java Objects instead of primitive types
   * @param fTreatChar1AsBoolean if true, threat char types as boolean
   * @return
   */
  public static DataType convertSQLType( VwColInfo ci, boolean fUseObjectTypes, boolean fTreatChar1AsBoolean  )
  {
    switch( ci.getSQLType() )
    {
      
      case Types.DATE:
      case Types.TIMESTAMP:
        
           return DataType.DATE;
      
      
      case Types.INTEGER:
      case Types.TINYINT:
      case Types.SMALLINT:
        
           if ( fUseObjectTypes )
             return DataType.INT_OBJ;
           else
             return DataType.INT;

      case Types.DOUBLE:
      case Types.FLOAT:
      case Types.REAL:
        
           if ( fUseObjectTypes )
             return DataType.DOUBLE_OBJ;
           else
             return DataType.DOUBLE;
        
      case Types.BIGINT:
        
           if ( fUseObjectTypes )
             return DataType.LONG_OBJ;
           else
             return DataType.LONG;
          
        
           
      case Types.NUMERIC:
      case Types.DECIMAL:

           if ( ci.getNbrDecimalDigits() > 0 )
           {
             if ( fUseObjectTypes )
               return DataType.DOUBLE_OBJ;
             else
               return DataType.DOUBLE;
           }
           else
           {
             if ( fUseObjectTypes )
             {
               if ( ci.getColSize() >= 10 )
                 return DataType.LONG_OBJ;
               else
                 return DataType.INT_OBJ;
               
             }
             else
             {
               if ( ci.getColSize() >= 10 )
                 return DataType.LONG;
               else
                 return DataType.INT;
             }
           }

       case Types.BIT:
         
            if ( fUseObjectTypes )
              return DataType.BOOLEAN_OBJ;
            else
              return DataType.BOOLEAN;
            
       case Types.BLOB:
       case Types.BINARY:
       case Types.VARBINARY:
       case Types.LONGVARBINARY:
         
            return DataType.BYTE;
          
       case Types.CHAR:
         
            if ( ci.getColSize() == 1 && fTreatChar1AsBoolean )
            {
              if ( fUseObjectTypes )
                return DataType.BOOLEAN_OBJ;
              else
                return DataType.BOOLEAN;
            }
            
            return DataType.STRING;
            
       default:
            return DataType.STRING;

    } // end switch()


  } // end converStringType()




  /**
   * Entry for execution
   *
   */
  public static void main( String[] astrArgs )
  {
    try
    {
      
      boolean fDVOOnly = false;
      boolean fDAOXMLOnly = false;
      boolean fGenDAO = false;
      boolean fOverwriteXsm = false;

      String  strIncludes = null;
      
      int nGenType = GEN_ALL;
      
      if ( astrArgs.length == 0 )
      {
        showArgs();
        System.exit( 1 );
      }
      
      if ( astrArgs.length > 1  )
      {
        for ( int x = 1; x < astrArgs.length; x++ )
        {
          if ( astrArgs[ x ].equals( "-x"))
          {
            fDAOXMLOnly = true;
            nGenType = VwObjectSQLMapper.GEN_XML_ONLY;
          }
          else
          if ( astrArgs[ x ].equals( "-d"))
          {
            fDVOOnly = true;
            nGenType = VwObjectSQLMapper.GEN_DVO_ONLY;
          }
          else
          if ( astrArgs[ x ].equals( "-o"))
            fOverwriteXsm = true;
          else
          if ( astrArgs[ x ].equals( "-g"))
            fGenDAO = true;
          else
          if ( astrArgs[ x ].equals( "-i"))
            strIncludes = astrArgs[ ++x ];
          else
          {
           showArgs();
           return;
          }
        } // end for()
      
      } // end if
    
      if ( fDVOOnly && fDAOXMLOnly )
      {
        VwLogger.getInstance().error( null, "You cannot specify both the -x (XML generation only and -d (DVO generation only ) options" );
        System.exit( -1 );
      }
      
      VwObjectSQLMapper dbcg = new VwObjectSQLMapper();

      URL urlSpecDoc = VwResourceStoreFactory.getInstance().getStore().getDocument( astrArgs[ 0 ] );

      if ( urlSpecDoc == null )
      {
        VwLogger.getInstance().error( null, "Cannot find the input spec document : " + astrArgs[ 0 ] +", make sure this is in your resources/docs folder" );
        System.exit( -1 );
      }

      dbcg.parse( urlSpecDoc,  fGenDAO,  nGenType, fOverwriteXsm, strIncludes );

    }
    catch( Exception e )
    {
      try
      {
        VwLogger.getInstance().fatal( null,  "VwObjectSqlMapper failed, stacktrace listed below:", e );
      }
      catch( Exception exi )
      {
        exi.printStackTrace();

      }

      e.printStackTrace();
    }

  } // end main

  private static void showArgs()
  {
    try
    {
      VwLogger.getInstance().info( null, "Format: VwObjectSqlMapper <input m_specification document> [-d Generate Java DVO objects only" +
        "\n-x Generate/Update DAO XML Document only " +
        "\n-n Do Not modify existing DVO's" +
        "\n-g Generate DAO interface, implementation and factory files for sql mappings" );
    }
    catch( Exception ex )
    {
      ex.printStackTrace();
    }
  }

} // end class VwObjectSQLMapper{}


// *** End of VwObjectSQLMapper.java ***
