package com.vozzware.codegen;

import com.vozzware.codegen.VwClassGen.MethodParams;
import com.vozzware.db.VwColInfo;
import com.vozzware.db.VwDatabase;
import com.vozzware.db.VwSchemaObjectMapper;
import com.vozzware.db.util.VwConstraint;
import com.vozzware.db.util.VwDAOProperties;
import com.vozzware.db.util.VwSql;
import com.vozzware.db.util.VwSqlMapping;
import com.vozzware.db.util.VwSqlMappingDocument;
import com.vozzware.db.util.VwSqlStatement;
import com.vozzware.util.VwExString;
import com.vozzware.util.VwInputSource;
import com.vozzware.util.VwLogger;
import com.vozzware.util.VwTextParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is used to generate data value objects (DVO)
 * @author petervosburghjr
 *
 */
public class VwDAOGen
{
  private   String                      m_strClassName;
  private   String                      m_strSuperClassName;
  
  
  private VwClassGen m_classGen;
  private VwClassGen m_classGenInterface;
  private VwClassGen m_classGenFactory;
  
  private VwDatabase m_dataBase;
  
  private VwCodeOptions m_codeOpts;
  
  private   String                      m_strPackage;
   
  private   List<String>                m_listImports = new ArrayList<String>();
  
  private VwLogger m_logger;
  
  private   boolean                     m_fIsSingleton = true;
  
  private VwDAOProperties m_daoProps;

  private VwCodeSnippetMgr m_codeSnippetMgr = new VwCodeSnippetMgr();
  
  private VwSqlMappingDocument m_sqlMappings;
  private boolean                       m_fGenListFromPrimaryKey = true;
  
  /**
   * 
   * @param sqlMappings
   * @param codeOpts
   * @param database
   * @param daoProps
   * @param logger
   * @throws Exception
   */ 
  public VwDAOGen( VwSqlMappingDocument sqlMappings, VwCodeOptions codeOpts, VwDatabase database, VwDAOProperties daoProps, VwLogger logger ) throws Exception
  {
    
    m_sqlMappings = sqlMappings;
    m_codeOpts = codeOpts;
    m_strClassName = daoProps.getClassName();
    m_strSuperClassName = daoProps.getExtensionClass();
    m_strPackage = daoProps.getPackage();
    m_dataBase = database;
    m_daoProps = daoProps;

    m_logger = logger;

    String strOutputPath = daoProps.getBasePath();

    if ( m_logger == null )
    {
      m_logger = VwLogger.getInstance();
    }
    
    m_codeSnippetMgr.loadCodeSnippets( "VwDaoCodeSnippets.xml" );

    if ( m_strSuperClassName != null )
    {
      int nPos = m_strSuperClassName.lastIndexOf( '.' );
      if ( nPos > 0 )
      {
         m_strSuperClassName = m_strSuperClassName.substring( ++nPos );
        
      }
    }
    else
    {
      m_strSuperClassName = "VwDbResourceMgrImpl";
    }
    
    m_classGen = new VwClassGen( m_codeOpts, m_strClassName + "Impl", m_strSuperClassName,
                                 m_strPackage, null, VwClassGen.CLASS );
    
    m_classGen.addInterface( m_strClassName );
    
    m_classGen.setOutputDirectory( strOutputPath );

    m_classGenInterface = new VwClassGen( m_codeOpts, m_strClassName, "VwDbResourceMgr",
                                          m_strPackage, null, VwClassGen.INTERFACE );

    m_classGenInterface.setOutputDirectory( strOutputPath );

    m_classGenInterface.addImport( "com.vozzware.db.VwTimestampOutOfSyncException", null );
    m_classGenInterface.addImport( "com.vozzware.db.VwDbResourceMgr", null );
    m_classGenInterface.addImport( "java.util.Date", null );
    m_classGenInterface.addImport( "java.sql.Timestamp", null );

    m_classGenFactory = new VwClassGen( m_codeOpts, m_strClassName + "Factory", null,
                                        m_strPackage, null, VwClassGen.CLASS );
   
    m_classGenFactory.setOutputDirectory( strOutputPath );
    m_classGenFactory.setSkipJavaGenIfExists( true ); // don't re-gen factory class if one exists
    
    // Add in minimum required imports
    m_classGen.addImport( "com.vozzware.db.VwDbResourceMgrImpl", null );
    m_classGen.addImport( "java.net.URL", null );
    m_classGen.addImport( "java.util.Date", null );
    m_classGen.addImport( "java.sql.Timestamp", null );
    m_classGen.addImport( "com.vozzware.db.VwSqlMgr", null );
    m_classGen.addImport( "com.vozzware.db.VwTimestampOutOfSyncException", null );
    m_classGen.addImport( "com.vozzware.util.VwLogger", null );
    m_classGen.addImport( "com.vozzware.util.VwResourceMgr", null );
    m_classGen.addImport( "com.vozzware.util.VwResourceStoreFactory", null );
    m_classGen.addImport( "com.vozzware.util.VwStackTraceWriter", null );
    
    
    if ( m_fIsSingleton)
    {
      doSingletonConstructor();
    }
    
    
    doDAOFactory();
 }
 
  /**
   * Add the getXXXDAODactory method
   */
  private void doDAOFactory()
  {
    m_classGenFactory.addMethod( "get" + m_strClassName, VwClassGen.PUBLIC, DataType.USERDEF,
                                 "    return " + m_strClassName + "Impl.getInstance();", "Factory accessor",
                                 null, "Exception", VwClassGen.ISSTATIC, 0, 0, m_strClassName, null,
                                 "if any database connection errors occur" );
   
  }
  
  /**
   * Create the private constructor and singleton getInstance method
   */
  private void doSingletonConstructor()
  {
    MethodParams[] aParams = getConstructorParams();
    
    m_classGen.addConstructor( VwClassGen.PROTECTED, getSingletonConstructorCode(), "Singleton Constructor", "Exception", 0, aParams, null );
    
    m_classGen.addDataMbr( "s_instance", DataType.USERDEF, "singleton DAO instance", VwClassGen.PRIVATE, VwClassGen.ISSTATIC, "null", null, m_strClassName );

    aParams = m_classGen.allocParams( 2 );

    aParams[ 0 ].m_strName = "loggerSql";
    aParams[ 0 ].m_eDataType = DataType.USERDEF;
    aParams[ 0 ].m_strUserDefType = "VwLogger";

    aParams[ 1 ].m_strName = "loggerDbMgr";
    aParams[ 1 ].m_eDataType = DataType.USERDEF;
    aParams[ 1 ].m_strUserDefType = "VwLogger";

    m_classGen.addMethod( "getInstance", VwClassGen.PUBLIC, DataType.USERDEF, getSingletonCode(), "Singleton access method", null, "Exception",
                          VwClassGen.ISSTATIC | VwClassGen.IS_SYNCHRONIZED, 0, 0, m_strClassName, aParams, "if any database connection errors occur" );
  }

  /**
   * Gets the singleton getInstance() method code
   * @return
   */
  private String getSingletonCode()
  {
    Map<String,String>mapMacroSubstitutions = new HashMap<String,String>();
    mapMacroSubstitutions.put( "className", m_strClassName );
    mapMacroSubstitutions.put( "instanceName", "s_instance" );
    String strCode = m_codeSnippetMgr.getSnippet( mapMacroSubstitutions, "getInstance" );
    return strCode;

  } // end getSingletonCode()
  

  /**
   * Create constructor parameters
   * 
   * @return
   */
  private MethodParams[] getConstructorParams()
  {
    MethodParams[] aParams = m_classGen.allocParams( 5 );
    aParams[ 0 ].m_strName = "DriverId";
    aParams[ 0 ].m_eDataType = DataType.STRING;
 
    aParams[ 1 ].m_strName = "UrlId";
    aParams[ 1 ].m_eDataType = DataType.STRING;

    aParams[ 2 ].m_strName = "urlMappingDoc";
    aParams[ 2 ].m_eDataType = DataType.USERDEF;
    aParams[ 2 ].m_strUserDefType = "URL";

    aParams[ 3 ].m_strName = "loggerSql";
    aParams[ 3 ].m_eDataType = DataType.USERDEF;
    aParams[ 3 ].m_strUserDefType = "VwLogger";

    aParams[ 4 ].m_strName = "loggerDbMgr";
    aParams[ 4 ].m_eDataType = DataType.USERDEF;
    aParams[ 4 ].m_strUserDefType = "VwLogger";

    return aParams;
  }

  
  /**
   * Get the singleton private constructor code
   * @return
   */
  private String getSingletonConstructorCode()
  {
    String strCode = "    " + m_codeSnippetMgr.getSnippet( null, "constructor" );
    return strCode;
  }

  /**
   * Generate the DAO
   * @throws Exception
   */
  public void genDao() throws Exception
  {
    m_logger.info( "Starting DAO generation" );

    genSqlMgrUtils();

    // add mappiings for dao generation
    for ( VwSqlMapping sqlMapping : m_sqlMappings.getSqlMapping() )
    {
      try
      {
        addMapping( sqlMapping );
      }
      catch( Exception ex )
      {
        throw ex;
      }
    }
 
    
    genSaveAndDelete();   // create the save and delete dao methods for any Object mapping
    
    m_classGenInterface.generate( m_logger );
    m_classGen.generate( m_logger );
    
    m_classGenFactory.generate( m_logger );

    m_logger.info( "DAO generation complete!" );

  }


  /**
   * Adds getSqlMgr to the interface
   */
  private void genSqlMgrUtils()
  {

    /*
    String strDoc = "Get the VwSqlMgr for direct SQL execution";

    m_classGenInterface.addMethod( "getSqlMgr", VwClassGen.PUBLIC, DataType.USERDEF, null,
                                    strDoc, null, "Exception",
                                    0, 0, 0, "VwSqlMgr", null, "if any database errors occur" );

    strDoc = "Close database resources";

    VwClassGen.MethodParams[] aMethodParams = getObjectMethodParams( "sqlMgr", "The VwSqlMgr instance" );
    aMethodParams[ 0 ].m_strUserDefType = "VwSqlMgr";
    aMethodParams[ 0 ].m_eDataType = DataType.USERDEF;


    m_classGenInterface.addMethod( "closeResources", VwClassGen.PUBLIC, DataType.VOID, null,
                                    strDoc, null, "Exception",
                                    0, 0, 0, null, aMethodParams, "if any database errors occur" );

    */

    String strDoc = "Direct SQL execution";

    //** Create the generic exec method for VwSqlMgr
    MethodParams[] aMethodParams = m_classGen.allocParams( 2 );
    aMethodParams[ 0 ].m_strName = "Sql";
    aMethodParams[ 0 ].m_eDataType = DataType.STRING;
    aMethodParams[ 0 ].m_strComment = "The sql to execute";

    aMethodParams[ 1 ].m_strName = "objParams";
    aMethodParams[ 1 ].m_eDataType = DataType.OBJECT;
    aMethodParams[ 1 ].m_strComment = "Object containing param(s) a String, a map or a bean";

    m_classGenInterface.addMethod( "exec", VwClassGen.PUBLIC, DataType.VOID, null,
                                   strDoc, null, "Exception",
                                   0, 0, 0, null, aMethodParams, "if any database errors occur" );

    m_classGen.addMethod( "exec", VwClassGen.PUBLIC, DataType.VOID, getExecCode( "exec"),
                          strDoc, null, "Exception",
                          0, 0, 0, null, aMethodParams, "if any database errors occur" );

  }


  /**
   * Return the Class generator instance
   * @return
   */
  public VwClassGen getClassGen()
  { return m_classGen; }


  /**
   * Add a sql mapping from which fetch and save methods will be created
   * @param sqlMapping
   */
  public void addMapping( VwSqlMapping sqlMapping )
  {
    VwSqlStatement stmtFind = sqlMapping.getFindBy();
    if ( stmtFind != null )
    {
      genGetMethodsForMapping( sqlMapping.getId(), sqlMapping, stmtFind );
    }

    VwSqlStatement stmtDelete = sqlMapping.getDeleteBy();
    if ( stmtDelete != null )
    {
      genSaveOrDeleteForMapping( "delete", sqlMapping, stmtDelete );
    }

    VwSqlStatement stmtUpdate = sqlMapping.getUpdateBy();
    if ( stmtUpdate != null )
    {
      genSaveOrDeleteForMapping( "save", sqlMapping, stmtUpdate );
    }

    VwSqlStatement stmtQuery = sqlMapping.getQuery();
    if ( stmtQuery != null )
    {
      genQueryForMapping( sqlMapping, stmtQuery );
    }

  }

  /**
   * Generate DAO generic save and delete methods for an object
   */
  private void genSaveAndDelete()
  {

    MethodParams[] aMethodParams = getObjectMethodParams( "objToSave", "The bean object to save" );

    String strDoc = "Saves the object\n   *<br>This methods performs and insert or update based on the existence of the primary key";
    String strSyncDoc = "Saves the object\n   *<br>This methods performs and insert or update based on the existence of the primary key" +
                        "\n   *<br>if an upadte operation results then a timestamp check is performed. Please refer to the syncSave method" +
                        "\n   *<br>javadoc on the VwSqlMgr class for complete details";

    String strExceptionDesc = "if the row changed in between the read and the save, if any database errors occur";


    // Standard Object save
    m_classGen.addMethod( "save", VwClassGen.PUBLIC, DataType.VOID, getSaveCode( "save", "objToSave", false ),
                          strDoc, null, "Exception",
                          0, 0, 0, null, aMethodParams, "if any database errors occur" );

    m_classGenInterface.addMethod( "save", VwClassGen.PUBLIC, DataType.VOID, null,
                                   strDoc, null, "Exception",
                                   0, 0, 0, null, aMethodParams, "if any database errors occur" );


    // Save with a different mapping id
    MethodParams[] aMappingIdMethodParams = getObjectMethodParamsWithMappingId( "objToSave", "The bean object to save" );

    m_classGen.addMethod( "save", VwClassGen.PUBLIC, DataType.VOID, getSaveOrUpdateCodeMappingId( "save", "save" ),
                          strDoc, null, "Exception",
                          0, 0, 0, null, aMappingIdMethodParams, "if any database errors occur" );

    m_classGenInterface.addMethod( "save", VwClassGen.PUBLIC, DataType.VOID, null,
                                   strDoc, null, "Exception",
                                   0, 0, 0, null, aMappingIdMethodParams, "if any database errors occur" );

    aMethodParams = getObjectMethodParams( "objToSave", "The bean object to save" );
    m_classGen.addMethod( "syncSave", VwClassGen.PUBLIC, DataType.VOID, getSaveCode( "syncSave", "objToSave", true ),
                          strSyncDoc, null, "VwTimestampOutOfSyncException, Exception",
                          0, 0, 0, null, aMethodParams, strExceptionDesc );

    m_classGenInterface.addMethod( "syncSave", VwClassGen.PUBLIC, DataType.VOID, null,
                                   strSyncDoc, null, "VwTimestampOutOfSyncException, Exception",
                                   0, 0, 0, null, aMethodParams, strExceptionDesc );


    aMethodParams = getObjectMethodParams( "objToDelete", "The bean object to delete" );

    strDoc = "Deletes the object by its primary key";

    m_classGen.addMethod( "delete", VwClassGen.PUBLIC, DataType.VOID, getDeleteCode( "delete", false, false, "objToDelete" ),
                          strDoc, null, "Exception",
                          0, 0, 0, null, aMethodParams, "if any database errors occur" );

    m_classGenInterface.addMethod( "delete", VwClassGen.PUBLIC, DataType.VOID, null,
                                   strDoc, null, "Exception",
                                   0, 0, 0, null, aMethodParams, " if any database errors occur" );

    strDoc = "Deletes the entire object graph";

    m_classGen.addMethod( "deleteAll", VwClassGen.PUBLIC, DataType.VOID, getDeleteCode( "deleteAll", true, false, "objToDelete" ),
                          strDoc, null, "Exception",
                          0, 0, 0, null, aMethodParams, " if any database errors occur" );

    m_classGenInterface.addMethod( "deleteAll", VwClassGen.PUBLIC, DataType.VOID, null,
                                   strDoc, null, "Exception",
                                   0, 0, 0, null, aMethodParams, " if any database errors occur" );

    strExceptionDesc = "if the row changed in between the read and the delete, if any database errors occur";
    m_classGen.addMethod( "syncDelete", VwClassGen.PUBLIC, DataType.VOID, getDeleteCode( "syncDelete", false, true, "objToDelete" ),
                          strDoc, null, "VwTimestampOutOfSyncException, Exception",
                          0, 0, 0, null, aMethodParams, strExceptionDesc );

    m_classGenInterface.addMethod( "syncDelete", VwClassGen.PUBLIC, DataType.VOID, null,
                                   strDoc, null, "VwTimestampOutOfSyncException, Exception",
                                   0, 0, 0, null, aMethodParams, strExceptionDesc );

    strDoc = "Deletes the entire object graph";

    m_classGen.addMethod( "syncDeleteAll", VwClassGen.PUBLIC, DataType.VOID, getDeleteCode( "syncDeleteAll", true, true, "objToDelete" ),
                          strDoc, null, "VwTimestampOutOfSyncException, Exception",
                          0, 0, 0, null, aMethodParams, strExceptionDesc );

    m_classGenInterface.addMethod( "syncDeleteAll", VwClassGen.PUBLIC, DataType.VOID, null,
                                   strDoc, null, "VwTimestampOutOfSyncException, Exception",
                                   0, 0, 0, null, aMethodParams, strExceptionDesc );

    // *** generic exists
    strDoc = "See if the object exists based on its primary key(s)";

    aMethodParams = getObjectMethodParams( "objToTest", "See if the object exists based on its primary key(s)" );
    String strExistsCode = m_codeSnippetMgr.getSnippet( null, "exists" );

    m_classGen.addMethod( "exists", VwClassGen.PUBLIC, DataType.BOOLEAN, strExistsCode,
                          strDoc, null, "Exception",
                          0, 0, 0, null, aMethodParams, " if any database erors occur" );

    m_classGenInterface.addMethod( "exists", VwClassGen.PUBLIC, DataType.BOOLEAN, null,
                                   strDoc, null, "Exception",
                                   0, 0, 0, null, aMethodParams, " if any database erors occur"  );

  }


  /**
   * Generate saveBy or deleteBy methods if and finders were defined for selects
   * @param strOperation  the operation either save or delete
   * @param sqlMapping  the mapping object for this object
   * @param stmt the Statement object in the object model
   */
  private void genSaveOrDeleteForMapping( String strOperation, VwSqlMapping sqlMapping, VwSqlStatement stmt  )
  {

    String strMappingId = sqlMapping.getId();

    List<VwConstraint> listConstraints = stmt.getConstraint();


    String strObjectName = findObjectNameFromQuery( stmt.getSql() );
    for ( VwConstraint constraint : listConstraints )
    {
      String strId = constraint.getId();
      if ( strId.equalsIgnoreCase( "primaryKey" ))
        continue;


      String strMethodName = strOperation;


      int nPos = strMappingId.lastIndexOf( '.' );
      strMethodName += getMethodNameFromMappingId( strMappingId.substring( ++nPos ) );

      String strFinnderName = strId;

      if ( !strFinnderName.toLowerCase().startsWith( "by" ))
      {
        if ( !Character.isUpperCase( strFinnderName.charAt( 0 ) ))
          strFinnderName = Character.toUpperCase( strFinnderName.charAt( 0 ) ) + strFinnderName.substring( 1 ) ;

        strMethodName += "By" + strFinnderName;
      }
      else
      {
        strMethodName += Character.toUpperCase( strId.charAt( 0 ) ) + strId.substring( 1 );
      }

      MethodParams[] aMethodParams =  m_classGen.allocParams( 21 );

      //** Create the generic exec method for VwSqlMgr
      aMethodParams = m_classGen.allocParams( 1 );

      aMethodParams[ 0 ].m_strName = "objTo" + Character.toUpperCase( strOperation.charAt( 0 ) ) + strOperation.substring( 1 );
      aMethodParams[ 0 ].m_eDataType = DataType.OBJECT;
      aMethodParams[ 0 ].m_strComment = "Object to " + strOperation;

      String strDoc = strOperation + " " + strObjectName + " by " + strId + " constraint id";


      m_classGen.addMethod( strMethodName, VwClassGen.PUBLIC, DataType.VOID, getSaveOrUpdateCode( sqlMapping.getId(), strOperation, strId ),
                            strDoc, strObjectName + " or null if not found", "Exception",
                            0, 0, 0, strObjectName, aMethodParams, " if any database errors occur" );

      m_classGenInterface.addMethod( strMethodName, VwClassGen.PUBLIC, DataType.VOID, null,
                                     strDoc, strObjectName + " or null if not found", "Exception",
                                     0, 0, 0, strObjectName, aMethodParams, " if any database errors occur" );


    } // end for

  }

  /**
   *
   * @param sqlMapping
   * @param stmt
   */
  private void genQueryForMapping( VwSqlMapping sqlMapping, VwSqlStatement stmt  )
  {
    String strMappingId = sqlMapping.getId() ;

    String strSql = stmt.getSql().get( 0 ).getBody();
    String strOperation = strSql.substring( 0, strSql.indexOf( " ") );

    List<VwConstraint> listConstraints = stmt.getConstraint();

    String strObjectName = null;

    if ( sqlMapping.getImplementsClassName() != null )
    {
      strObjectName =  sqlMapping.getImplementsClassName();

    }
    else
    {
      strObjectName = findObjectNameFromQuery( stmt.getSql() );
    }


    for ( VwConstraint constraint : listConstraints )
    {
      String strId = constraint.getId();
      if ( strId.equalsIgnoreCase( "primaryKey" ))
      {
        continue;
      }

      String strMethodName = strOperation;

      int nPos = strMappingId.lastIndexOf( '.' );
      strMethodName += getMethodNameFromMappingId( strMappingId.substring( ++nPos ) );

      String strFinderName = strId;

      if ( !strFinderName.toLowerCase().startsWith( "by" ))
      {
        if ( !Character.isUpperCase( strFinderName.charAt( 0 ) ))
        {
          strFinderName = Character.toUpperCase( strFinderName.charAt( 0 ) ) + strFinderName.substring( 1 ) ;
        }

        strMethodName += "By" + strFinderName;
      }
      else
      {
        strMethodName += Character.toUpperCase( strId.charAt( 0 ) ) + strId.substring( 1 );
      }

      MethodParams[] aMethodParams =  m_classGen.allocParams( 2 );

      //** Create the generic exec method for VwSqlMgr
      aMethodParams = m_classGen.allocParams( 1 );

      aMethodParams[ 0 ].m_strName = "objParams";
      aMethodParams[ 0 ].m_eDataType = DataType.OBJECT;
      aMethodParams[ 0 ].m_strComment = "Object parameters to " + strOperation;

      String strDoc = strOperation + " " + strObjectName + " by " + strId + " constraint id";

      m_classGen.addMethod( strMethodName, VwClassGen.PUBLIC, DataType.VOID, getExecQueryCode( strMethodName, strSql ),
                            strDoc, strObjectName + " or null if not found", "Exception",
                            0, 0, 0, strObjectName, aMethodParams, " if any database errors occur" );

      m_classGenInterface.addMethod( strMethodName, VwClassGen.PUBLIC, DataType.VOID, null,
                                     strDoc, strObjectName + " or null if not found", "Exception",
                                     0, 0, 0, strObjectName, aMethodParams, " if any database errors occur" );


    } // end for

  }

  /**
   * Generate DAO get methods for a sqlMapping id. A getXXX method will be generated for each constraint defined
   * in the mapping
   * @param strMappingId The mapping id in the xsm document
   * @param stmtFind
   */
  private void genGetMethodsForMapping( String strMappingId, VwSqlMapping sqlMapping, VwSqlStatement stmtFind  )
  {

    List<VwConstraint> listConstraints = stmtFind.getConstraint();

    String strObjectName = null;

    String strClassName = sqlMapping.getClassName();

    if ( strClassName != null && isScalerResult( strClassName ) )         // This is a custom id so use that as the object name
    {
      strObjectName = strMappingId;
      strObjectName = Character.toUpperCase( strObjectName.charAt( 0 ) ) + strObjectName.substring( 1 );

      genScalerResultQueryCode( strClassName, strObjectName, sqlMapping );
      return;
    }

    if ( strClassName != null )         // This is a custom id so use that as the object name
    {
      strObjectName = strMappingId;
      strObjectName = Character.toUpperCase( strObjectName.charAt( 0 ) ) + strObjectName.substring( 1 );
      strClassName = findObjectNameFromQuery( stmtFind.getSql() ); // this will get the fully qualified class name
    }
    else
    {
      if ( sqlMapping.getImplementsClassName() != null )
      {
        strObjectName = sqlMapping.getImplementsClassName();
      }
      else
      {
        strObjectName = findObjectNameFromQuery( stmtFind.getSql() );
      }


      strClassName = strObjectName;

      m_classGen.addImport( strClassName, null );
      m_classGenInterface.addImport( strClassName, null );

    }

    if ( strObjectName == null ) // no base sql statement found so we will use the mapping id
    {
      m_logger.warn( this.getClass(), "Cannot find object reference from into clause, skipping mapping '" + strMappingId + "'" );
      return;
    }


    String strTableName = findTableNameFromQuery( stmtFind.getSql() );
    String strSchema = null;
    int nPos = strTableName.indexOf( '.' );
    if ( nPos > 0 )
    {
      strSchema = strTableName.substring( 0, nPos );
      strTableName = strTableName.substring( ++nPos );
    }
    else
    {
      strSchema = m_dataBase.getDbMgr().getUserID();
    }

    List<VwColInfo> listPrimaryKeys = null;

    try
    {

      listPrimaryKeys = m_dataBase.getPrimaryKeys( null, strSchema, strTableName );
    }
    catch ( Exception ex )
    {
      ex.printStackTrace();
      m_logger.error( this.getClass(), "Database failure retrieving column info for table '" + strTableName + "'", ex );
      return;
    }


    if ( sqlMapping.getImplementsClassName() != null )
    {
      m_classGen.addImport( sqlMapping.getImplementsClassName(), null );
      m_classGenInterface.addImport( sqlMapping.getImplementsClassName(), null );

    }

    if ( sqlMapping.getInheritClassName() != null )
    {
      m_classGen.addImport( sqlMapping.getInheritClassName(), null );
      m_classGenInterface.addImport( sqlMapping.getInheritClassName(), null );

    }

    if ( sqlMapping.getClassName() != null )
    {
      m_classGen.addImport( strClassName, null );
      m_classGenInterface.addImport( strClassName, null );

    }

    String strReturnTypeName = null;

    if ( sqlMapping.getImplementsClassName() != null )
    {
      strReturnTypeName = sqlMapping.getImplementsClassName();
    }
    else
    if ( sqlMapping.getInheritClassName() != null )
    {
      strReturnTypeName = sqlMapping.getInheritClassName();
    }
    else
    if ( sqlMapping.getClassName() != null )
    {
      strReturnTypeName = sqlMapping.getClassName();
    }
    else
    {
      strReturnTypeName = strObjectName;
    }

    // strip off and package qualifier to just get the object name
    nPos = strReturnTypeName.lastIndexOf( '.' );

    if ( nPos > 0 )
    {
      strReturnTypeName = strReturnTypeName.substring( ++nPos );
    }

    // Generate methods for each constraint defined
    for ( VwConstraint constraint : listConstraints )
    {
      String strId = constraint.getId();
      if ( strId.equalsIgnoreCase( "primaryKey" ))
      {
        nPos = strMappingId.lastIndexOf( '.' );

        String strMethodName = "get" + getMethodNameFromMappingId( strMappingId.substring( ++nPos ) );

        MethodParams[] aMethodParams = getMethodParams( listPrimaryKeys );

        m_classGen.addMethod( strMethodName, VwClassGen.PUBLIC, DataType.USERDEF, getFindByPrimaryKeyCode( strMethodName, strClassName, strMappingId, aMethodParams ),
                              "Fetch " + strObjectName + " by primary key", strObjectName + " or null if not found", "Exception",
                              0, 0, 0, strReturnTypeName, aMethodParams, " if any database errors occur" );

        m_classGenInterface.addMethod( strMethodName, VwClassGen.PUBLIC, DataType.USERDEF, null,
                                       "Fetch " + strObjectName + " by primary key", strObjectName + " or null if not found", "Exception",
                                       0, 0, 0, strReturnTypeName, aMethodParams, " if any database errors occur" );

        if ( m_fGenListFromPrimaryKey)
        {
          strMethodName += "List";
          m_classGen.addImport( "java.util.List", null );
          m_classGenInterface.addImport( "java.util.List", null );

          m_classGen.addMethod( strMethodName, VwClassGen.PUBLIC, DataType.GT_LIST, getFindAllCode( strMethodName, strReturnTypeName, strMappingId ),
                                "Fetch " + strObjectName + " List", strObjectName + " List or null if not found", "Exception",
                                0, 0, 0, strReturnTypeName, null, " if any database errors occur" );

          m_classGenInterface.addMethod( strMethodName, VwClassGen.PUBLIC, DataType.GT_LIST, null,
                                         "Fetch " + strObjectName + " List", strObjectName + " List or null if not found", "Exception",
                                         0, 0, 0, strReturnTypeName, null, " if any database errors occur" );
        }

      }
      else
      {
        String strMethodName = "get";
        String strAllMethodName = "get";

        m_classGen.addImport( "java.util.List", null );
        m_classGenInterface.addImport( "java.util.List", null );

        // generate a get for a defined finder
        nPos = strMappingId.lastIndexOf( '.' );
        strMethodName += getMethodNameFromMappingId( strMappingId.substring( ++nPos ) );
        strAllMethodName +=  getMethodNameFromMappingId( strMappingId.substring( nPos ) ) + "List";

        String strFinnderName = strId;

        if ( !strFinnderName.toLowerCase().startsWith( "by" ))
        {
          if ( !Character.isUpperCase( strFinnderName.charAt( 0 ) ))
            strFinnderName = Character.toUpperCase( strFinnderName.charAt( 0 ) ) + strFinnderName.substring( 1 ) ;

          strMethodName += "By" + strFinnderName;
          strAllMethodName += "By" + strFinnderName;
        }
        else
        {
          strMethodName += Character.toUpperCase( strId.charAt( 0 ) ) + strId.substring( 1 );
          strAllMethodName += Character.toUpperCase( strId.charAt( 0 ) ) + strId.substring( 1 );
        }

       MethodParams[] aMethodParams = getFinderMethodParams();

       String strFetch = "Fetch " + strObjectName + " by " + strId + " constraint id";
       String strFetchAll = "Fetch all " + strObjectName + "(s) by " + strId + " constraint id";
       // gen findBY
       m_classGen.addMethod( strMethodName, VwClassGen.PUBLIC, DataType.USERDEF, getFindByConstraintCode( strMethodName, false, strMappingId, strId, strClassName, aMethodParams ),
                             strFetch, strObjectName + " or null if not found", "Exception",
                             0, 0, 0, strReturnTypeName, aMethodParams, " if any database errors occur" );

       m_classGenInterface.addMethod( strMethodName, VwClassGen.PUBLIC, DataType.USERDEF, null,
                                      strFetch, strObjectName + " or null if not found", "Exception",
                                      0, 0, 0, strReturnTypeName, aMethodParams, " if any database errors occur" );

        // gen findAllBY
        m_classGen.addMethod( strAllMethodName, VwClassGen.PUBLIC, DataType.GT_LIST, getFindByConstraintCode( strAllMethodName, true, strMappingId, strId, strClassName, aMethodParams ),
                              strFetchAll, strObjectName + " or null if not found", "Exception",
                              0, 0, 0, strReturnTypeName, aMethodParams, " if any database errors occur" );

        m_classGenInterface.addMethod( strAllMethodName, VwClassGen.PUBLIC, DataType.GT_LIST, null,
                                       strFetchAll, strObjectName + " or null if not found", "Exception",
                                       0, 0, 0, strReturnTypeName, aMethodParams, " if any database errors occur" );

      }

    }
  } // end genGetMethodsForMapping


  /**
   *
   * @param strClassName
   * @param strObjectName
   * @param sqlMapping
   */
  private void genScalerResultQueryCode( String strClassName, String strObjectName, VwSqlMapping sqlMapping )
  {
    String strMappingId = sqlMapping.getId();

    String strContstraintId = sqlMapping.getFindBy().getConstraint().get( 0 ).getId();

    String strQuerySql = sqlMapping.getFindBy().getSql().get(0).getBody();

    String strMethodName = "get";
    String strAllMethodName = "get";

    if ( strClassName.startsWith( "List"))
    {
      m_classGen.addImport( "java.util.List", null );
      m_classGenInterface.addImport( "java.util.List", null );
    }


    // generate a get for a defined finder
    strMethodName += getMethodNameFromMappingId( strMappingId );
    strAllMethodName +=  getMethodNameFromMappingId( strMappingId) + "List";


    if ( !strContstraintId.toLowerCase().startsWith( "by" ))
    {
      if ( !Character.isUpperCase( strContstraintId.charAt( 0 ) ))
        strContstraintId = Character.toUpperCase( strContstraintId.charAt( 0 ) ) + strContstraintId.substring( 1 ) ;

      strMethodName += "By" + strContstraintId;
      strAllMethodName += "By" + strContstraintId;
    }
    else
    {
      strMethodName += Character.toUpperCase( strContstraintId.charAt( 0 ) ) + strContstraintId.substring( 1 );
      strAllMethodName += Character.toUpperCase( strContstraintId.charAt( 0 ) ) + strContstraintId.substring( 1 );
    }

   MethodParams[] aMethodParams = getObjectMethodParams( "objParams", "");

   String strFetch = "Get " + strObjectName + " by " + strContstraintId + " constraint id";
   String strFetchAll = "Get all " + strObjectName + "(s) by " + strContstraintId + " constraint id";

   if ( !strClassName.startsWith( "List" ))
   { // gen findBY
     m_classGen.addMethod( strMethodName, VwClassGen.PUBLIC, DataType.USERDEF, getExecScalerResultCode( strMethodName, strClassName, strQuerySql, "execWithScalerResult" ),
                           strFetch, strObjectName + " or null if not found", "Exception",
                           0, 0, 0, strClassName, aMethodParams, " if any database errors occur" );

     m_classGenInterface.addMethod( strMethodName, VwClassGen.PUBLIC, DataType.USERDEF, null,
                                    strFetch, strObjectName + " or null if not found", "Exception",
                                    0, 0, 0, strClassName, aMethodParams, " if any database errors occur" );
   }
   else
   { // gen findAllBY
     m_classGen.addMethod( strAllMethodName, VwClassGen.PUBLIC, DataType.USERDEF, getExecScalerResultCode( strMethodName, strClassName, strQuerySql, "execWithScalerListResult" ),
                           strFetchAll, strObjectName + " or null if not found", "Exception",
                           0, 0, 0, strClassName, aMethodParams, " if any database errors occur" );

     m_classGenInterface.addMethod( strAllMethodName, VwClassGen.PUBLIC, DataType.USERDEF, null,
                                    strFetchAll, strObjectName + " or null if not found", "Exception",
                                    0, 0, 0, strClassName, aMethodParams, " if any database errors occur" );

   }
  }

  /**
   * Returns true if the class name is a java sclter class i.e. is an sclater type "String", "Integer", "Double" or a List collection type List<String>, List<Integer> ...
   * @param strClassName
   * @return
   */
  public static boolean isScalerResult( String strClassName )
  {
    if ( strClassName.startsWith( "List" ) || strClassName.equals( "String")  || strClassName.equals( "Integer") || strClassName.equals( "Long")
      || strClassName.equals( "Float") || strClassName.equals( "Double") || strClassName.equals( "Boolean") || strClassName.equals( "Char") )
    {
      return true;
    }

    return false;
  }

  /**
   * Returns the method name from the mapping id
   *
   * @param strMappingId
   * @return
   */
  private String getMethodNameFromMappingId( String strMappingId )
  {

    String strMethodName = strMappingId;

    String[] astrMethodPrefixs = null;
    String[] astrMethodSuffixes = null;

    if ( m_daoProps.getTypePrefix() != null )  // strip off prefix from class type if there is a match
    {
      astrMethodPrefixs = m_daoProps.getTypePrefix().split( ",");

      for ( int x = 0; x < astrMethodPrefixs.length; x++ )
      {
        if ( strMethodName.startsWith( astrMethodPrefixs[ x ]) ) // strip off prefix from class type if there is a match
        {
          strMethodName = strMethodName.substring( astrMethodPrefixs[ x ].length() );
        }
      }

    }

    if ( m_daoProps.getTypeSuffix() != null )  // strip off prefix from class type if there is a match
    {
      astrMethodSuffixes = m_daoProps.getTypeSuffix().split( ",");

      for ( int x = 0; x < astrMethodSuffixes.length; x++ )
      {
        if ( strMethodName.endsWith( astrMethodSuffixes[ x ] ) ) // strip off prefix from class type if there is a match
        {
          strMethodName = strMethodName.substring( 0, strMethodName.length() - astrMethodSuffixes[ x ].length() );
        }
      }

    }

    return strMethodName;


  }


  /**
   * Getds method paramater obhect for findBy methods
   * @return
   */
  private MethodParams[] getFinderMethodParams()
  {
    MethodParams[] aParams = m_classGen.allocParams( 1 );
    aParams[ 0 ].m_strName = "objFinderKey";
    aParams[ 0 ].m_eDataType = DataType.OBJECT;
    aParams[ 0 ].m_strComment = "Primary Key Object";

    return aParams;
  }


  private MethodParams[] getObjectMethodParams( String strBeanName, String strComment )
  {
    MethodParams[] aParams = m_classGen.allocParams( 1 );
    aParams[ 0 ].m_strName = strBeanName;
    aParams[ 0 ].m_eDataType = DataType.OBJECT;
    aParams[ 0 ].m_strComment = strComment;

    return aParams;
  }

  private MethodParams[] getObjectMethodParamsWithMappingId( String strBeanName, String strComment )
  {
    MethodParams[] aParams = m_classGen.allocParams( 2 );
    aParams[ 0 ].m_strName = strBeanName;
    aParams[ 0 ].m_eDataType = DataType.OBJECT;
    aParams[ 0 ].m_strComment = strComment;

    aParams[ 1 ].m_strName = "MappingId";
    aParams[ 1 ].m_eDataType = DataType.STRING;
    aParams[ 1 ].m_strComment = "The mapping id specified in the xsm document";

    return aParams;
  }

  /**
   *
   * @param fUseFindAll
   * @param strMappingId
   * @param strConstraintName
   * @param aMethodParams
   * @return
   */
  private String getFindByConstraintCode( String strMethodName, boolean fUseFindAll, String strMappingId, String strConstraintName, String strClassName, MethodParams[] aMethodParams )
  {
    Map<String,String>mapMacroSubstitutions = new HashMap<String,String>();

    // The class name is the mapping id unless a class name override (strClassName) is specified

    if ( strMappingId.indexOf( '.') > 0 )  // This is a fully qualified path so it is the mapping id
    {
      mapMacroSubstitutions.put( "mappingId", strMappingId );
    }


    if ( strClassName != null )
    {
      mapMacroSubstitutions.put( "className", strClassName );
      mapMacroSubstitutions.put( "id", strMappingId );

      if ( strMappingId.indexOf( '.') < 0 )
      {
        mapMacroSubstitutions.put( "mappingId", strClassName );

      }
    }
    else
    {
      mapMacroSubstitutions.put( "className", strMappingId );

    }

    mapMacroSubstitutions.put( "finderName", "\"" + strConstraintName + "\"" );

    MethodParams param = aMethodParams[ 0 ];
    String strSnippetName = null;

    if ( fUseFindAll )
    {
      if ( strClassName != null )
        strSnippetName = "findAllByCustomId";
      else
        strSnippetName = "findAllBy";
    }
    else
    {
      if ( strClassName != null )
        strSnippetName = "findByCustomId";
      else
        strSnippetName = "findBy";
    }

    mapMacroSubstitutions.put( "value", param.m_strName );

    String strLogDebug = getLoggerCode(  strMethodName );

    return strLogDebug +   m_codeSnippetMgr.getSnippet( mapMacroSubstitutions, strSnippetName );
  }


  /**
   *
   * @param strClassName
   * @return
   */
  private String getFindAllCode( String strMethodName, String strClassName, String strMappingId )
  {
    Map<String,String>mapMacroSubstitutions = new HashMap<String,String>();
    mapMacroSubstitutions.put( "className", strClassName );
    mapMacroSubstitutions.put( "mappingId", strMappingId );


    String strLogDebug = getLoggerCode(  strMethodName );

    return strLogDebug +  m_codeSnippetMgr.getSnippet( mapMacroSubstitutions, "findAll" );

  }



  /**
   *  Gets the findByPrimaryKey code
    * @return
   */
  private String getFindByPrimaryKeyCode( String strMethodName, String strClassName, String strMappingId, MethodParams[] aMethodParams )
  {
    Map<String,String>mapMacroSubstitutions = new HashMap<String,String>();
    mapMacroSubstitutions.put( "className", strClassName );
    mapMacroSubstitutions.put( "mappingId", strMappingId );

    if ( aMethodParams.length == 1 )
    {
      MethodParams param = aMethodParams[ 0 ];
      mapMacroSubstitutions.put( "value", param.m_eDataType.hungarianName() + param.m_strName );
      return m_codeSnippetMgr.getSnippet( mapMacroSubstitutions, "primaryKey" );
   }
    
    m_classGen.addImport( "java.util.Map", null );
    m_classGen.addImport( "java.util.HashMap", null );

    StringBuffer sbMapCode = new StringBuffer();
    sbMapCode.append( "Map<String,Object> mapCompositeKeys = new HashMap<String,Object>();\n" );
    for ( int x = 0; x < aMethodParams.length; x++ )
    {
      String strKeyName = aMethodParams[ x ].m_strName;
      strKeyName = Character.toLowerCase( strKeyName.charAt(  0 ) ) + strKeyName.substring( 1 );
      sbMapCode.append( "    mapCompositeKeys.put( \"" );
      sbMapCode.append(  strKeyName );
      sbMapCode.append(  "\", " );
      sbMapCode.append( aMethodParams[ x ].m_eDataType.hungarianName() + aMethodParams[ x ].m_strName  );
      sbMapCode.append( " );" );
      
    }
    
    mapMacroSubstitutions.put( "mapCode", sbMapCode.toString() );

    String strLogDebug = getLoggerCode(  strMethodName );

    return strLogDebug +  m_codeSnippetMgr.getSnippet( mapMacroSubstitutions, "compositKey" );
   
  }


  /**
   * Returns the code for execScaler ro execScalerList depending on the strSnippetType
   * @param strClassName
   * @return
   */
  private String getExecScalerResultCode( String strMethodName, String strClassName, String strQuerySql, String strSnippetType )
  {
    Map<String,String>mapMacroSubstitutions = new HashMap<String,String>();
    mapMacroSubstitutions.put( "className", strClassName );
    mapMacroSubstitutions.put( "sql", strQuerySql );
    mapMacroSubstitutions.put( "value", "objParams" );


    String strLogDebug = getLoggerCode(  strMethodName );

    return strLogDebug +  m_codeSnippetMgr.getSnippet( mapMacroSubstitutions, strSnippetType );

  }


  /**
   * Gets the save code snippet
   * @param strObjName
   * @param fSyncSave
   * @return
   */
  private String getSaveCode( String strMethodName, String strObjName, boolean fSyncSave  )
  {
    Map<String,String>mapMacroSubstitutions = new HashMap<String,String>();
    mapMacroSubstitutions.put( "value", strObjName );

    if ( fSyncSave )
      mapMacroSubstitutions.put( "saveType", "syncSave" );
    else 
      mapMacroSubstitutions.put( "saveType", "save" );
    
    String strLogDebug = getLoggerCode(  strMethodName );

    return strLogDebug +  m_codeSnippetMgr.getSnippet( mapMacroSubstitutions, "save" );
   
  }

  /**
   * Get generic exec code
   * @return
   */
  private String getExecCode( String strMethodName )
  {
    Map<String,String>mapMacroSubstitutions = new HashMap<String,String>();

    String strLogDebug = getLoggerCode(  strMethodName );

    return strLogDebug +  m_codeSnippetMgr.getSnippet( mapMacroSubstitutions, "exec" );

  }

  /**
   * Get generic exec code
   * @return
   */
  private String getExecQueryCode( String strMethodName, String strSql )
  {
    Map<String,String>mapMacroSubstitutions = new HashMap<String,String>();

    String strLogDebug = getLoggerCode(  strMethodName );

    return strLogDebug +  "\n" + "    String strSql = \"" + strSql + "\";\n" +  m_codeSnippetMgr.getSnippet( mapMacroSubstitutions, "exec" );

  }

  /**
   * Get generic exec code
   * @return
   */
  private String getSaveOrUpdateCode( String strMethodName, String strOperation, String strConstraintId )
  {
    Map<String,String>mapMacroSubstitutions = new HashMap<String,String>();
    mapMacroSubstitutions.put( "id", strConstraintId );
    mapMacroSubstitutions.put( "mappingId", strMethodName );

    String strLogDebug = getLoggerCode(  strMethodName );

    return strLogDebug + m_codeSnippetMgr.getSnippet( mapMacroSubstitutions, strOperation + "By" );

  }


  /**
   * Get generic exec code
   * @return
   */
  private String getSaveOrUpdateCodeMappingId( String strMethodName,  String strOperation )
  {
    Map<String,String>mapMacroSubstitutions = new HashMap<String,String>();

    String strLogDebug = getLoggerCode(  strMethodName );

    return strLogDebug +  m_codeSnippetMgr.getSnippet( mapMacroSubstitutions, strOperation + "ByMappingId" );

  }

  /**
   * Get delete code
   * @param fDeleteAll
   * @param fSyncDelete
   * @param strObjName
   * @return
   */
  private String getDeleteCode( String strMethodName, boolean fDeleteAll, boolean fSyncDelete, String strObjName )
  {
    Map<String,String>mapMacroSubstitutions = new HashMap<String,String>();
    mapMacroSubstitutions.put( "value", strObjName );
    
    String strDeleteType = null;
    if ( fDeleteAll )
    {
      if ( fSyncDelete )
      {
        strDeleteType = "syncDeleteAll";
      }
      else 
      {
        strDeleteType = "deleteAll";
      }
    }
    else
    {
      if ( fSyncDelete )
      {
        strDeleteType = "syncDelete";
      }
      else
      {
        strDeleteType = "delete";
      }
    }
    
    mapMacroSubstitutions.put( "deleteType", strDeleteType);

    String strLogDebug = getLoggerCode(  strMethodName );

    return strLogDebug +  m_codeSnippetMgr.getSnippet( mapMacroSubstitutions, "delete" );
  }


  private String getLoggerCode( String strMethodName )
  {

    return "    if ( m_loggerSql.isDebugEnabled() )\n    {\n      m_loggerSql.debug( this.getClass(), \"Entering DAO Method: "
           + strMethodName + "()\" );\n    }\n\n";

  }


  /**
   * Build a method parameter object from the number of keys and data type names
   * @param listPrimaryKeys
   * @return
   */
  private MethodParams[] getMethodParams( List<VwColInfo> listPrimaryKeys )
  {  
    DataType[] eKeyTypes =  getPrimaryKeyTypes( listPrimaryKeys );
    
    MethodParams[] aParams = m_classGen.allocParams( eKeyTypes.length );
    
    for ( int x = 0; x < aParams.length; x++ )
    {
      if ( eKeyTypes[ x ] == DataType.VW_DATE )
      {
        m_classGen.addImport( "com.vozzware.util.VwDate", null );
        m_classGenInterface.addImport( "com.vozzware.util.VwDate", null );
      }
      
      String strKeyName = VwExString.makeJavaName( listPrimaryKeys.get( x  ).getColumnName(), false );
      aParams[ x ].m_strName = strKeyName;
      aParams[ x ].m_eDataType = eKeyTypes[ x ];
      aParams[ x ].m_strComment = "Primary Key";
    }
    
    return aParams;
      
  }


  /**
   * Get the Name of the returned object from the select statement. It will be following the into statement
   * of the first select string (there will be multiple select strings if this is an object graph)
   * 
   * @param listSql The list of sql statements look for the of type base
   * @return
   */
  private String findObjectNameFromQuery( List<VwSql> listSql )
  {
    for ( VwSql sql : listSql )
    {
      if ( sql.getId().equals( "base" ))
      {
        String strSql = sql.getBody();
        int nPos = strSql.toLowerCase().indexOf( " into " );
        if ( nPos < 0 )
          return null;
        
        int nEndPos = strSql.indexOf( ';' );
        
        String strObjName = strSql.substring( nPos + " into ".length(), nEndPos );
        return strObjName.trim();

        
      }
    }
    
    return null;
  }


  /**
   * Find the table name from the first select statement
   * @param listSql
   * @return
   */
  private String findTableNameFromQuery( List<VwSql> listSql )
  {
    for ( VwSql sql : listSql )
    {
      if ( sql.getId().equals( "base" ))
      {
        String strSql = sql.getBody();
        
        try
        {
          StringBuffer sbToken = new StringBuffer();
          VwTextParser tp = new VwTextParser( new VwInputSource( strSql ));
          
          int nPos = tp.findToken( "from" );
          if ( nPos < 0 )
            return null;
          
          tp.setCursor( nPos + "from".length() );
          // next token following the from will be the table name
          if ( tp.getToken( sbToken ) == VwTextParser.EOF )
            return null;
          
          return sbToken.toString();
          
        }
        catch( Exception ex )
        {
          return null;
        }
        
      }
    }
    
    return null;
  }

  /**
   * Get the DataTypes for each primary key
   * @param listPrimaryKeys list of primary keys 
   * @return
   */
  private DataType[] getPrimaryKeyTypes( List<VwColInfo> listPrimaryKeys )
  {
    DataType[] aeTypes = new DataType[ listPrimaryKeys.size() ];
    
    int ndx = -1;
    for ( VwColInfo ci : listPrimaryKeys )
    {
      aeTypes[ ++ndx ] = VwSchemaObjectMapper.convertSQLType( ci, false, false );
    }
    
    return aeTypes;
  }


  /**
   * Adds an import
   * @param strImport
   * @param strComment
   */
  public void addImport( String strImport, String strComment )
  {
    String strElement = strImport;
    if ( strComment != null )
      strElement += ":" + strComment;
    
    m_listImports.add( strElement );
    
  }
 
    
  
} // end class VwDVOGen{}

// *** End of VwDVOGen.java ***

