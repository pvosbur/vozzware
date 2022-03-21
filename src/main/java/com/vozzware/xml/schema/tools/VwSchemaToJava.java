/*
 ============================================================================================
 
 Copyright(c) 2000 - 2006 by

 V o z z W a r e   L L C (Vw)

 All Rights Reserved

 THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
 PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
 CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

 Source Name: VwSchemaToJava.java

 Create Date: Apr 11, 2006
 ============================================================================================
 */
package com.vozzware.xml.schema.tools;

import com.vozzware.codegen.DataType;
import com.vozzware.codegen.VwClassGen;
import com.vozzware.codegen.VwCodeOptions;
import com.vozzware.codegen.VwCodeSnippetMgr;
import com.vozzware.codegen.VwDVOGen;
import com.vozzware.codegen.VwPropertyDefinition;
import com.vozzware.util.VwDate;
import com.vozzware.util.VwDelimString;
import com.vozzware.util.VwExString;
import com.vozzware.util.VwInputSource;
import com.vozzware.util.VwLogger;
import com.vozzware.util.VwStack;
import com.vozzware.util.VwTextParser;
import com.vozzware.xml.VwXmlToBean;
import com.vozzware.xml.schema.util.VwSchemaReaderImpl;

import javax.wsdl.util.WSDLFactory;
import javax.xml.schema.Annotation;
import javax.xml.schema.AppInfo;
import javax.xml.schema.Attribute;
import javax.xml.schema.AttributeGroup;
import javax.xml.schema.Choice;
import javax.xml.schema.ComplexContent;
import javax.xml.schema.ComplexType;
import javax.xml.schema.Element;
import javax.xml.schema.Include;
import javax.xml.schema.ModelGroup;
import javax.xml.schema.Restriction;
import javax.xml.schema.Schema;
import javax.xml.schema.SimpleContent;
import javax.xml.schema.SimpleType;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * This utility generates Java classes from XML Schema complexType definitions
 * and element definitions that are defined with anonymous complexTypes.
 * 
 */
public class VwSchemaToJava
{

  private Map<String,String> m_mapGennedTypes = new HashMap<String,String>(); // Map of generated class types

  private File m_fileSchema; // The schema file to process

  private String m_strPackage; // Package for generated classes

  private String m_strBasePath; // Base path to start of package

  private String m_strReaderType; // Return class type for reader

  private String m_strClassName;

  private Schema m_schema; // The schema that we're processing

  private boolean m_fUseTargetNamespaceAsPackage;

  private boolean m_fAttrNormalForm;

  private boolean m_fNoMacroExpansion = false;

  private boolean m_fUseObjects;

  private boolean m_fGenIncludes = true;
  
  private Map<String,String>m_mapTypeIncludes = null;
  private Map<String,String>m_mapTypeExcludes = null;
  
  private VwLogger m_logger;

  private Map<String,List<Element>> m_mapContentMethods = new HashMap<String,List<Element>>();

  private VwCodeOptions            m_codeOpts;

  private VwCodeSnippetMgr         m_codeSnippetMgr = new VwCodeSnippetMgr();

  private static ResourceBundle s_props = ResourceBundle.getBundle( "com.vozzware.tools.vwtools" );

  /**
   * Constructor
   * 
   * @param fileSchema
   *          File object that represents the XML Schema
   * @param strBasePath
   *          The path to the start of the output folder. Classes will be placed
   *          underneath according <br>
   *          to the package name
   * @param strPackage
   *          The package to be used for the generated classses
   * @param fUseTargetNamespaceAsPackage
   *          If true, use the target namespace as the package name
   * @param fAttrNormalForm
   *          If true, The attributes of the schema are generated as class
   *          properties and the element tags <br>
   *          are generated as the class type
   */
  public VwSchemaToJava( File fileSchema, String strBasePath, String strPackage, boolean fUseTargetNamespaceAsPackage,
      boolean fAttrNormalForm, boolean fUseObjects ) throws Exception
  {
    this( fileSchema, strBasePath, strPackage, null, fUseTargetNamespaceAsPackage, fAttrNormalForm, fUseObjects, false, true, null, null );
  }

  /**
   * Constructor
   * 
   * @param fileSchema
   *          File object that represents the XML Schema
   * @param strBasePath
   *          The path to the start of the output folder. Classes will be placed
   *          underneath according <br>
   *          to the package name
   * @param strPackage
   *          The package to be used for the generated classses
   * @param strReaderType
   *          the return class type of the document if you want a schema reader
   *          class created
   * @param fUseTargetNamespaceAsPackage
   *          If true, use the target namespace as the package name
   * @param fAttrNormalForm
   *          If true, The attributes of the schema are generated as class
   *          properties and the element tags <br>
   *          are generated as the class type
   */
  public VwSchemaToJava( File fileSchema, String strBasePath, String strPackage, String strReaderType,
      boolean fUseTargetNamespaceAsPackage, boolean fAttrNormalForm, boolean fUseObjects,
      boolean fNoMacroExpansion, boolean fGenIncludes, String strTypeIncludes, String strTypeExcludes ) throws Exception
  {
    m_fileSchema = fileSchema;
    m_strBasePath = strBasePath;
    m_strPackage = strPackage;
    m_strReaderType = strReaderType;

    m_fUseObjects = fUseObjects;
    
    m_fUseTargetNamespaceAsPackage = fUseTargetNamespaceAsPackage;
    m_fAttrNormalForm = fAttrNormalForm;
    m_fNoMacroExpansion = fNoMacroExpansion;
    m_fGenIncludes = fGenIncludes;
    
    m_codeOpts = setupCodeOpts();

    m_logger = VwLogger.getInstance();

    if ( strTypeIncludes != null )
      m_mapTypeIncludes = buildMap( strTypeIncludes );
    
    if ( strTypeExcludes != null )
      m_mapTypeExcludes = buildMap( strTypeExcludes );
    
  } // end VwSchemaToJava

  private Map<String,String> buildMap( String strTypes )
  {
    VwDelimString dlms = new VwDelimString( strTypes );
    return dlms.toMap(true );
    
  }

  /**
   * Process the XML Schema
   * @throws Exception
   */
  public void process() throws Exception
  {
    
    m_codeSnippetMgr.loadCodeSnippets( "VwSchemaToJavaCodeSnippets.xml" );

    String strErrMsg = null;
    if (m_fileSchema == null)
    {
      strErrMsg = s_props.getString( "tools.noSchema" );
      m_logger.fatal( null, strErrMsg );
      throw new Exception( strErrMsg );
    }

    if (m_strBasePath == null)
    {
      strErrMsg = s_props.getString( "tools.noBase" );
      m_logger.fatal( null, strErrMsg );
      throw new Exception( strErrMsg );
    }

    m_logger.info( null, "Processing input specification from '" + m_fileSchema.getAbsolutePath() + "'" );

    if ( m_mapTypeIncludes != null )
    {
      m_logger.info( null, "Only processsing the following specified complexType(s) : " + dumpMapKeys( m_mapTypeIncludes ) );
    }

    if ( m_mapTypeExcludes != null )
    {
      m_logger.info( null, "Processsing all complexType(s) but the following : " + dumpMapKeys( m_mapTypeExcludes ) );
    }

    VwSchemaReaderImpl rdr = new VwSchemaReaderImpl();
    
    VwStack<Schema>stackSchemas = new VwStack<Schema>();
     
    // if the file is a wsdl document, we will get our schema instance to process from the wsdl framework
    if ( m_fileSchema.getAbsolutePath().endsWith( ".wsdl" ))
      m_schema = WSDLFactory.getInstance().newReader().extractSchema( m_fileSchema.toURL() );
    else
      m_schema = rdr.readSchema( m_fileSchema.toURL() );
    
    stackSchemas.push( m_schema );

    if ( m_fGenIncludes )
      findAllSchemas( stackSchemas, m_schema );

    while( true )
    {
      Schema schema = stackSchemas.pop();
      if ( schema == null )
        break;
      
      m_schema = schema;
      String strTNS = schema.getTargetNamespace();
      
      if ( strTNS != null )
        m_logger.info( null, "Processing schema with target namespace : " + strTNS );
      else
        m_logger.warn( null, "No target namespace defined for schema" );
          
      if (m_fUseTargetNamespaceAsPackage)
      {
  
        if (strTNS == null)
          throw new Exception( s_props.getString( "tools.noNamespace" ) );
  
        m_strPackage = makePackageFromNamespace( strTNS );
      }
      
      if (m_strPackage == null)
        throw new Exception( s_props.getString( "tools.noPackage" ) );
  
      m_logger.info( null, "Using target namespace as package '" + m_strPackage + "'" );
  
      Iterator iComponents = schema.getContent().iterator();
  
      while ( iComponents.hasNext() )
      {
        Object component = iComponents.next();
  
        if (component instanceof Element)
          processElement( (Element)component );
        else
          if (component instanceof ComplexType)
            processComplexType( (ComplexType)component, null );
  
      } // end while()
  
      if (m_strReaderType != null)
      {
        genReaderClass();
        genWriterClass();
      }

    }
    
    m_logger.info( null, "Done!" );

  } // end process()


  /**
   * Dump out the values of the map
   * @param mapValues
   * @return
   */
  private String dumpMapKeys( Map<String, String> mapValues )
  {
    StringBuffer sb = new StringBuffer();
    
    for( String strVal : mapValues.values() )
    {
      if ( sb.length() > 0 )
        sb.append( ", " );
      
      sb.append( strVal );
    }
    
    return sb.toString();
  }

  /**
   * Walk up a schema chain of included schema so the first schema include is at the top of the stack
   *
   * @param stackSchemas The stack of current schema
   * @param schema The schema to check for includes
   */
  private void findAllSchemas( VwStack<Schema> stackSchemas, Schema schema )
  {
    
    List<Include>list = schema.getIncludes();
    if ( list != null  )
    {
      for ( Include include : list )
      {
        Schema schemaInclude = include.getSchema();
        stackSchemas.push(  schemaInclude );
        findAllSchemas( stackSchemas, schemaInclude );
      }
    }
    
  }

  private void genReaderClass() throws Exception
  {
    m_logger.info( null, "Generating Reader class for type '" + m_strReaderType );

    VwClassGen classGen = new VwClassGen( m_codeOpts, m_strReaderType + "Reader", null, m_strPackage, null,
        VwClassGen.CLASS );

    classGen.setOutputDirectory( m_strBasePath );
    classGen.addImport( "java.net.URL", null );
    classGen.addImport( "org.xml.sax.InputSource", null );
    classGen.addImport( "com.vozzware.util.VwResourceStoreFactory", null );
    classGen.addImport( "com.vozzware.xml.VwXmlToBean", null );

    if (m_fAttrNormalForm || m_fNoMacroExpansion )
       classGen.addImport( "javax.xml.schema.util.XmlDeSerializer", null );

    Map<String,String>mapMacroSubstitutions = new HashMap<String, String>();
    
    if ( m_fNoMacroExpansion)
      mapMacroSubstitutions.put( "expandMacros", "xtb.setFeature( XmlDeSerializer.EXPAND_MACROS, false  );\n" );
    else
      mapMacroSubstitutions.put( "expandMacros", "" );

    if ( m_fAttrNormalForm )
      mapMacroSubstitutions.put( "anfFlag", "xtb.setFeature( XmlDeSerializer.ATTRIBUTE_MODEL, true  );\n" );
    else
      mapMacroSubstitutions.put( "anfFlag", "" );

    mapMacroSubstitutions.put( "xsdDocument", m_fileSchema.getName() );
    mapMacroSubstitutions.put( "className", m_strReaderType );
    
    String strReaderCode = m_codeSnippetMgr.getSnippet( mapMacroSubstitutions, "reader" );
    
    VwClassGen.MethodParams[] aMthdParams = classGen.allocParams( 1 );
    aMthdParams[0].m_eDataType = DataType.USERDEF;
    aMthdParams[0].m_strName = "urlDoc";
    aMthdParams[0].m_strUserDefType = "URL";

    classGen.addMethod( "read", VwClassGen.PUBLIC, DataType.USERDEF, strReaderCode, "Reader", null,
        "Exception", VwClassGen.ISSTATIC, 0, 0, m_strReaderType, aMthdParams, null );

    classGen.generate( m_logger );

  } // end

  private void genWriterClass() throws Exception
  {
    m_logger.info( null, "Generating Writer class for type '" + m_strReaderType );

    VwClassGen classGen = new VwClassGen( m_codeOpts, m_strReaderType + "Writer", null, m_strPackage, null,
                                            VwClassGen.CLASS );

    classGen.setGenCloneCode( false );

    classGen.setOutputDirectory( m_strBasePath );
    classGen.addImport( "java.net.URL", null );
    classGen.addImport( "java.io.File", null );
    classGen.addImport( "com.vozzware.util.VwResourceStoreFactory", null );
    classGen.addImport( "com.vozzware.xml.VwBeanToXml", null );

    		
    Map<String,String>mapMacroSubstitutions = new HashMap<String, String>();
 
    if ( m_fAttrNormalForm )
    {
      classGen.addImport( "javax.xml.schema.util.XmlDeSerializer", null );
      mapMacroSubstitutions.put( "anfFlag", "\n    btx.setFeature( XmlDeSerializer.ATTRIBUTE_MODEL, true  );" );
    }
    else
      mapMacroSubstitutions.put( "anfFlag", "" );


    StringBuffer sbContentMethods = new StringBuffer();
    for ( String strClassName : m_mapContentMethods.keySet() )
    {
      
      List<Element> listContentMethods = m_mapContentMethods.get( strClassName );
      VwDelimString dlmsMethodNames = new VwDelimString();
      
      for ( Element element : listContentMethods)
      {
        String strName = "get" + element.getName().substring( 0, 1 ).toUpperCase() + element.getName().substring( 1 );
        dlmsMethodNames.add( strName );
      }
      
      if ( listContentMethods.size() == 0  )
        continue;
        
      sbContentMethods.append( "\n    btx.setContentMethods( " ).append( strClassName ).append( ".class, \"" );
      sbContentMethods.append( dlmsMethodNames.toString() ).append( "\"  );" );
      
    }

    if ( sbContentMethods.length() > 0 )
    {
      sbContentMethods.append( "\n" );
      mapMacroSubstitutions.put( "contentMethdsList", sbContentMethods.toString() );
    }
    else
      mapMacroSubstitutions.put( "contentMethdsList", "" );

    mapMacroSubstitutions.put( "xsdDocument", m_fileSchema.getName() );
    mapMacroSubstitutions.put( "className", m_strReaderType );
    mapMacroSubstitutions.put( "additionalSchemaList", "" );
    
    String strConfigCode = m_codeSnippetMgr.getSnippet( mapMacroSubstitutions, "config" );

    VwClassGen.MethodParams[] aMthdParams = classGen.allocParams( 1 );
    aMthdParams[0].m_eDataType = DataType.USERDEF;
    aMthdParams[0].m_strName = "objToWrite";
    aMthdParams[0].m_strUserDefType = m_strReaderType;

    classGen.addMethod( "toString", VwClassGen.PUBLIC, DataType.STRING, "    return toString( objToWrite, null );", "Serializes the bean to XML in a String", null,
        "Exception", VwClassGen.ISSTATIC, 0, 0, null, aMthdParams, "if any serialization errors occur" );

    aMthdParams = classGen.allocParams( 2 );
    aMthdParams[0].m_eDataType = DataType.USERDEF;
    aMthdParams[0].m_strName = "objToWrite";
    aMthdParams[0].m_strUserDefType = m_strReaderType;

    aMthdParams[1].m_eDataType = DataType.STRING;
    aMthdParams[1].m_strName = "CommentHeader";
 
    classGen.addMethod( "toString", VwClassGen.PUBLIC, DataType.STRING, m_codeSnippetMgr.getSnippet( null, "toString" ), "Serializes the bean to XML in a String", null,
        "Exception", VwClassGen.ISSTATIC, 0, 0, null, aMthdParams, "if any serialization errors occur" );
 
    
    aMthdParams = classGen.allocParams( 2 );
    aMthdParams[0].m_eDataType = DataType.USERDEF;
    aMthdParams[0].m_strName = "objToWrite";
    aMthdParams[0].m_strUserDefType = m_strReaderType; 

    aMthdParams[1].m_eDataType = DataType.USERDEF;
    aMthdParams[1].m_strName = "fileToWrite";
    aMthdParams[1].m_strUserDefType = "File"; 

    classGen.addMethod( "write", VwClassGen.PUBLIC, DataType.VOID, "    write( objToWrite, fileToWrite, null );", "Serializes the bean to XML and writes the XML to the file specified", null,
                        "Exception", VwClassGen.ISSTATIC, 0, 0, null, aMthdParams, "if any file io errors occur" );


    aMthdParams = classGen.allocParams( 3 );
    aMthdParams[0].m_eDataType = DataType.USERDEF;
    aMthdParams[0].m_strName = "objToWrite";
    aMthdParams[0].m_strUserDefType = m_strReaderType; 

    aMthdParams[1].m_eDataType = DataType.USERDEF;
    aMthdParams[1].m_strName = "fileToWrite";
    aMthdParams[1].m_strUserDefType = "File"; 

    aMthdParams[2].m_eDataType = DataType.STRING;
    aMthdParams[2].m_strName = "CommentHeader";

    classGen.addMethod( "write", VwClassGen.PUBLIC, DataType.VOID, m_codeSnippetMgr.getSnippet( null, "writeFile" ), "Serializes the bean to XML and writes the XML to the file specified", null,
        "Exception", VwClassGen.ISSTATIC, 0, 0, null, aMthdParams, "if any file io errors occur" );

    aMthdParams = classGen.allocParams( 1 );
    aMthdParams[0].m_eDataType = DataType.STRING;
    aMthdParams[0].m_strName = "CommentHeader";

    classGen.addMethod( "config", VwClassGen.PRIVATE, DataType.USERDEF, strConfigCode, "Configures VwBeanToXml properties", null,
        "Exception", VwClassGen.ISSTATIC, 0, 0, "VwBeanToXml", aMthdParams, null );

    classGen.generate( m_logger );

  } // end

  /**
   * @return
   */
  private VwCodeOptions setupCodeOpts()
  {
    VwCodeOptions codeOpts = new VwCodeOptions();

    codeOpts.m_strName = "V o z z W a r e   L L C";
    codeOpts.m_strAuthor = "Vw";
    codeOpts.m_strCopyright = "2009 by V o z z W a r e   L L C";
    VwDate today = new VwDate();

    codeOpts.m_strAuthor = "";

    codeOpts.m_sPubOrder = 1;
    codeOpts.m_sPrivOrder = 2;
    codeOpts.m_sProtOrder = 3;
    codeOpts.m_sDefOrder = 4;
    codeOpts.m_fUseHungarian = true;

    return codeOpts;

  }

  /**
   * Process a schema element (Element). If the element is not complex i.e. it
   * doesent define anononmous complex types then skip this element
   */
  private void processElement( Element element ) throws Exception
  {

    // Element must contain a complex type content inoreder to qualifiy to be a
    // class
    if (element.getComplexType() == null)
      return;

    ComplexType type = element.getComplexType();

    if (!type.hasChildElements())
      return; // No sub elements if this case is true

    m_logger.info( null, "Processing element '" + element.getName() + "'" );

    processComplexType( type, element.getName() );

  } // end processElement(()

  /**
   * Create a java class file from the elements that make up this named
   * complexType
   * 
   * @param type  The complex type containg the elemenet data
   */
  private void processComplexType( ComplexType type, String strName ) throws Exception
  {

    String strTypeName = type.getName();
    if ( strName != null )
      strTypeName = strName;
    
    if ( m_mapTypeIncludes != null )
    {
      if ( !m_mapTypeIncludes.containsKey( strTypeName.toLowerCase() ))
        return;
    }
    
    if ( m_mapTypeExcludes != null )
    {
      if ( m_mapTypeExcludes.containsKey( strTypeName.toLowerCase() ))
        return;
    }
    
    if (!m_fAttrNormalForm && !type.hasChildElements())
      return; // No sub elements if this case is true

    m_logger.info( null, "Processing complexType '" + type.getName() );

    // See if we have an appInfo inside an annotation defining the class name
    // and packages to use

    AppInfo appInfo = getAnnotation( type.getAnnotation() );

    m_strClassName = null;
    String strPackage = null;

    if (appInfo != null)
    {
      m_strClassName = getAppInfoData( appInfo, "class" );

      if (m_strClassName != null)
      {
        m_strClassName = getClassName( m_strClassName );
        strPackage = getPackageName( strPackage );
      }

    }

    if (m_strClassName == null)
    {
      if (strName != null)
        m_strClassName = strName;
      else
        m_strClassName = type.getName();

    }

    if (strPackage == null)
      strPackage = m_strPackage;

    String strBaseClassName = getBaseClassName( type );
    String strBaseClassPackage = null;
    
    if (!m_fAttrNormalForm && strBaseClassName == null && type.hasAttributes())
      strBaseClassName = "VwXmlBeanAdapter";
    else
    if (strBaseClassName != null)
    {
       strBaseClassName = Character.toUpperCase( strBaseClassName.charAt( 0 ) ) + strBaseClassName.substring( 1 );
       // if base class is in a different schema packe then we need to add an import
       strBaseClassPackage = checkBaseClassPackage( strBaseClassName );
       
          
    }
    
    m_mapGennedTypes.put( m_strClassName, null );

    m_strClassName = Character.toUpperCase( m_strClassName.charAt( 0 ) ) + m_strClassName.substring( 1 );

    if (m_fAttrNormalForm)
      m_mapContentMethods.put( m_strClassName, new ArrayList<Element>() );

    VwDVOGen dvoGen = new VwDVOGen( m_codeOpts, m_strClassName, strBaseClassName, strPackage, m_strBasePath );
    
    if (strBaseClassName != null && strBaseClassName.equals( "VwXmlBeanAdapter" ))
      dvoGen.addImport( "com.vozzware.xml.VwXmlBeanAdapter", null );

    if ( strBaseClassPackage != null )
      dvoGen.addImport( strBaseClassPackage, null );
    
    List<VwPropertyDefinition> listPropDefs = new ArrayList<VwPropertyDefinition>();
    processType( type, dvoGen.getClassGen(), listPropDefs );

    // *** Write the java class file
    dvoGen.genDvo( listPropDefs, m_logger );
    m_logger.info( null, "Generated class : " + strPackage + "." + m_strClassName );
  } // end processComplexType()

  private String checkBaseClassPackage( String strBaseClassName ) throws Exception
  {
    // test this schema first
    
    if ( m_schema.getComplexObject( strBaseClassName ) != null )
      return null;
    
    List<Include> list = m_schema.getIncludes();
    if ( list == null )
      return null;
    
    for ( Include inc : list )
    {
      Schema schema = inc.getSchema();
      Object objType = schema.getComponent( strBaseClassName );
      
      if ( objType != null )
      {
        String strTns = schema.getTargetNamespace();
        String strPackage = makePackageFromNamespace( strTns );
        strPackage += "." + strBaseClassName;
        return strPackage;
        
      }
      
    }
    
    return null;
  }

  private String makePackageFromNamespace( String strTNS ) throws Exception
  {
    String strPackage = null;
    
    if (strTNS.startsWith( "http" ) || strTNS.startsWith( "www." ))
    {
      int nPos = 0;
       if (strTNS.startsWith( "http" ) )
         nPos = strTNS.indexOf( "://" ) + 3;
       else
         nPos = 4; // strip off the www. from namespace for package
       
      if (nPos < 0)
        throw new Exception( s_props.getString( "tools.invalid.namespace" ) );

      strTNS = strTNS.substring( nPos );
 
      nPos = strTNS.indexOf( '/' );

      String strBaseUrl = null;
      String strPath = "";
      StringBuffer sbPackage = new StringBuffer();

      if (nPos > 0)
      {
        strBaseUrl = strTNS.substring( 0, nPos );
        strPath = strTNS.substring( ++nPos );

        VwTextParser parser = new VwTextParser( new VwInputSource( strBaseUrl ), strBaseUrl.length() - 1, -1 );
        parser.setDelimiters( "." );
        StringBuffer sbWord = new StringBuffer();

        int nRet = parser.getToken( sbWord );
        while ( nRet != VwTextParser.EOF )
        {
          String strWord = sbWord.toString();
          sbPackage.append( strWord );
          nRet = parser.getToken( sbWord );
        }

        strPath = VwExString.replace( strPath, '/', '.' );
        sbPackage.append( '.' ).append( strPath );
        strPackage = sbPackage.toString();

      }
      else
        strPackage = strTNS;

    }
    else
      strPackage = strTNS;
 
    return strPackage;
  }

  /**
   * Process the elements in an anonymous type
   * 
   * @param type
   *          The complex type object
   * @param classGen
   *          The class code generator that will have the elements in this type
   *          added to
   * 
   */
  private void processType( ComplexType type, VwClassGen classGen, List<VwPropertyDefinition> listPropDefs ) throws Exception
  {

    if (type.hasAttributes() && m_fAttrNormalForm)
      processAttributes( type.getAttributes( false ), listPropDefs );

    if (type.isModelGroup())
      processModelGroup( type, type.getModelGroup(), classGen, listPropDefs, type.getModelGroup().getMaxOccurs() );
    else
    if (type.isComplexContent())
    {
      ComplexContent content = type.getComplexContent();

      if (content.hasModelGroup())
        processModelGroup( type, content.getModelGroup(), classGen, listPropDefs, content.getModelGroup().getMaxOccurs() );

    } // end if

  } // end processType()

  /**
   * Getnerate attributes as class properties
   * 
   * @param listAttributes
   *          The list of Attribute objects to process
   * @param classGen
   *          The code generator instance
   */
  private void processAttributes( List listAttributes, List<VwPropertyDefinition> listPropDefs ) throws Exception                          
  {
    for ( Iterator iAttr = listAttributes.iterator(); iAttr.hasNext(); )
    {
      Object objAttr = iAttr.next();
      String strName = null;

      if (objAttr instanceof Attribute)
      {
        Attribute attr = (Attribute)objAttr;

        setClassMember( attr, listPropDefs );
        
      }
      else
      if (objAttr instanceof AttributeGroup)
      {
        AttributeGroup attrGroup = (AttributeGroup)objAttr;
        strName = attrGroup.getName();
        if (strName == null)
          strName = attrGroup.getRef();

        if (strName == null)
          throw new Exception( "Invalid attributeGroup definition, both name and ref cannot be null" );

        attrGroup = m_schema.getAttributeGroup( strName );

        if (attrGroup == null)
          throw new Exception( "No attributGroup named '" + strName + "' exists" );

        for ( Iterator iAttrs = attrGroup.getAttributes().iterator(); iAttrs.hasNext(); )
        {
          Attribute attr = (Attribute)iAttrs.next();
          setClassMember( attr, listPropDefs );

        }

      }

    }
  }

  private void setClassMember( Attribute attr, List<VwPropertyDefinition> listPropDefs ) throws Exception
  {
    
    String  strName = attr.getName();
    if (strName == null)
    {
      strName = attr.getRef();

      if (strName == null)
        throw new Exception( "Invalid attribute definition, both name and ref cannot be null" );

      attr = m_schema.getAttribute( strName );
    }

    strName = attr.getName();
    
    String strBaseType = attr.getType();
    
    if ( strBaseType == null )
      strBaseType = "string";
    
    String strType = VwXmlToBean.getJavaType( strBaseType );

    VwPropertyDefinition propDef = new VwPropertyDefinition();
    propDef.setName( strName );
    propDef.setDataType( convertType( strType ) );
    propDef.setInitialValue( attr.getDefault() );
    listPropDefs.add( propDef );
    
    
  } // end setClassMember()

  /**
   * Process The ModelGroup content
   * 
   * @param content
   *          The complex content object
   * @param classGen
   *          The class code generator that will have the elements in this type
   *          added to
   * 
   */
  private void processModelGroup( ComplexType ctype, ModelGroup group, VwClassGen classGen, List<VwPropertyDefinition> listPropDefs, String strMaxOccurs ) throws Exception
  {
    Iterator iObjects = group.getContent().iterator();
     
    Attribute attrCollection = group.getUserAttribute( "collection" );
    Attribute attrClassType = group.getUserAttribute( "choiceClassType" );
    
    if ( group instanceof Choice  )
    {
      if ( strMaxOccurs != null )
      {
        String strElementName = null;
        String strCollectionType = null;
        
        if (  attrCollection != null )
        {
          strElementName = group.getUserAttribute( "collection" ).getType();
          Element eleCollection = ctype.findElement( strElementName  );
          
          if ( eleCollection == null )
            throw new Exception( "Cannot not find the refered to element name '" + strElementName + "' as specified in the collection attribute");
          strCollectionType = eleCollection.getType();
          
        }
        else
        {
          strElementName = "choiceList";
          strCollectionType = ctype.getName();
          VwPropertyDefinition propDef = new VwPropertyDefinition();
          propDef.setName( strElementName );
          propDef.setDataType( DataType.GT_LIST );
          propDef.setUserType( strCollectionType );
          propDef.setInitialValue( null );
          listPropDefs.add(  propDef );
          
        }
        
        doAddToListMethods( iObjects, strElementName, strCollectionType, classGen );
        return;
        
      }
      else
      {
        if ( attrClassType != null )
        {
          doChoiceMethods( iObjects, attrClassType.getType(),  classGen, listPropDefs );
          return;
        }
      }
      
    }
    
    while ( iObjects.hasNext() )
    {
      Object obj = iObjects.next();

      if (obj instanceof ModelGroup)
      {
        String strOccurs = ((ModelGroup)obj).getMaxOccurs();

        if (strOccurs != null)
          processModelGroup( ctype, (ModelGroup)obj, classGen, listPropDefs, strOccurs );
        else
          processModelGroup( ctype, (ModelGroup)obj, classGen, listPropDefs, strMaxOccurs );
      }
      else
      if (obj instanceof Element)
      {
        Element element = (Element)obj;

        if (m_fAttrNormalForm && m_strClassName != null)
        {
          List listContentMethods = (List)m_mapContentMethods.get( m_strClassName );
          listContentMethods.add( element );
        }

        String strName = element.getName();
        String strType = element.getType();

        if (strName == null)
        {
          strName = element.getRef();
          strName = stripNamespace( strName );
          strType = getRefType( strName );
        }

        if (strType != null)
        {
          Object objComp = m_schema.getComponent( strType );

          if (objComp instanceof ComplexType)
          {
            if (!m_fAttrNormalForm && ((ComplexType)objComp).isAttributeOnly())
              continue; // Attribute only elements are not added as class
                        // properties

            if (((ComplexType)objComp).isSimpleContent())
            {
              SimpleContent simpleContent = (SimpleContent)((ComplexType)objComp).getContent();

              String strBaseType = simpleContent.getType();

              strType = VwXmlToBean.getJavaType( strBaseType );

              if (!addedCollection( element, strMaxOccurs, listPropDefs, strName ))
              {
                VwPropertyDefinition propDef = new VwPropertyDefinition();
                propDef.setName( strName );
                propDef.setDataType( convertType( strType ) );
                propDef.setInitialValue( element.getDefault() );
                listPropDefs.add(  propDef );
                
              }

              continue;

            }

          }
          else
            strType = extractType( strType );

        }

        if (addedCollection( element, strMaxOccurs, listPropDefs, strName ))
        {
          // if ( m_fAttrNormalForm && element.hasAttributes() )
          // processAttrinutes
          if (element.isComplexType())
            processElement( element );

        }
        else
        if (element.isComplexType())
        {

          if (!m_fAttrNormalForm && element.getComplexType().isAttributeOnly())
            continue; // Don't add attributes only as properties of the
                      // class

          // A complexType with no element group is an empty element i.e
          // attributes only
          if (!(element.getComplexType()).hasChildElements())
          {
            strName = Character.toUpperCase( strName.charAt( 0 ) ) + strName.substring( 1 );

            VwPropertyDefinition propDef = new VwPropertyDefinition();
            propDef.setName( strName );
            propDef.setDataType( convertType( strType ) );
            propDef.setInitialValue( element.getDefault() );
            listPropDefs.add(  propDef );

            classGen.addMethod( "get" + strName, VwClassGen.PUBLIC, DataType.STRING, "    return null; ",
                "Placeholder for EMPTY content defined tags", null, null, 0, 0, 0, null, null, null );

          }
          else
          {
 
            strType = strName.substring( 0,1 ).toUpperCase() + strName.substring( 1 );
            
            VwPropertyDefinition propDef = new VwPropertyDefinition( strName, DataType.USERDEF, strType );
            listPropDefs.add(  propDef );

            processElement( element );
          }
        }
        else
        {
          if (element.isSimpleType())
            strType = processSimpleType( element.getSimpleType() );
          
          DataType eCodeGenType = convertType( strType );

          if (eCodeGenType != DataType.USERDEF)
            strType = null;

          strType = getElementType( strType );
          VwPropertyDefinition propDef = new VwPropertyDefinition();
          propDef.setName( strName );
          propDef.setDataType( eCodeGenType );
          propDef.setUserType( strType );
          propDef.setInitialValue( element.getDefault() );
          listPropDefs.add(  propDef );
          
 
        } // end else

      } // end else

    } // end while()

  } // end processModelGroup

  private String getElementType( String strType )
  {

    if (strType != null)
    {
      Object objType = m_schema.getComponent( strType );

      strType = stripNamespace( strType );

      if (objType instanceof ComplexType)
      {
        if (((ComplexType)objType).hasChildElements())
          strType = Character.toUpperCase( strType.charAt( 0 ) ) + strType.substring( 1 );
      }

    }
    
    return strType;

  }

  /**
   * This uses the add method to a List instead of the set property
   * @param iGroupObjects
   * 
   * @param classGen
   */
  private void doAddToListMethods( Iterator iGroupObjects, String strCollectionName, String strCollectionType, VwClassGen classGen )
  {

    classGen.addImport( "java.util.ArrayList", null );
    StringBuffer sbCode = new StringBuffer();

    strCollectionType = stripNamespace( strCollectionType );
    
    String strJavaName = strCollectionType.substring( 0, 1 ).toUpperCase() + strCollectionType.substring( 1 ); 
    
     
    strCollectionName = strCollectionName.substring( 0, 1 ).toUpperCase() + strCollectionName.substring( 1 );
    String strDataName = "m_" + classGen.getHungPrefix( 0, DataType.LIST ) +  strCollectionName;

    sbCode.append( "\n    if ( " ).append(  strDataName );
    sbCode.append( " == null )\n      ").append( strDataName ).append( " = new ArrayList<" ).append( strJavaName ).append( ">();");
    sbCode.append( "\n\n    ").append(  strDataName ).append( ".add(  objToAdd );" );
    
    String strMethodName = "addTo" + strCollectionName + "List";
    
    VwClassGen.MethodParams[] aMthdParams = classGen.allocParams( 1 );
    aMthdParams[0].m_eDataType = DataType.USERDEF;
    aMthdParams[0].m_strName = "objToAdd";
    aMthdParams[0].m_strUserDefType = strJavaName;
    
    classGen.addMethod( strMethodName, VwClassGen.PROTECTED, DataType.VOID, sbCode.toString(), null, null,
        null, 0, 0, 0, null, aMthdParams, null );
    
    
    while ( iGroupObjects.hasNext() )
    {
      Object obj = iGroupObjects.next();

      if (obj instanceof Element)
      {
        Element element = (Element)obj;

        String strName = element.getName();
        String strType = element.getType();

        if (strName == null)
        {
          strName = element.getRef();
          strType = getRefType( strName );
        }

        strType = stripNamespace( strType );
        
        String strTypeDataName = strType.substring( 0, 1 ).toLowerCase() + strType.substring( 1 );
        
        if (strType != null)
        {
          aMthdParams = classGen.allocParams( 1 );
          aMthdParams[0].m_eDataType = DataType.USERDEF;
          aMthdParams[0].m_strName = strTypeDataName;
          aMthdParams[0].m_strUserDefType = strType;
          
          StringBuffer sbAddCode = new StringBuffer( "    ").append(  strMethodName ).append( "( " );
          sbAddCode.append( strTypeDataName ).append( "  );" );
          classGen.addMethod( "add" + strName.substring( 0, 1 ).toUpperCase() + strName.substring( 1 ), VwClassGen.PUBLIC, DataType.VOID, sbAddCode.toString(), null, null,
              null, 0, 0, 0, null, aMthdParams, null );
                 
        }
      } // end if
    } // end while()
    
  }

  /**
   * This uses the add method to a List instead od the set property
   * @param iGroupObjects
   * 
   * @param classGen
   */
  private void doChoiceMethods( Iterator iGroupObjects,  String strChoiceTypeName, VwClassGen classGen, List<VwPropertyDefinition> listProps )
  {
    if ( strChoiceTypeName == null )
      strChoiceTypeName = "Object";
    
    int nPos = strChoiceTypeName.indexOf( ':' );
    
    strChoiceTypeName = strChoiceTypeName.substring( ++nPos );
    
    String strChoiceTypeDataName = strChoiceTypeName.substring( 0, 1).toLowerCase() + strChoiceTypeName.substring( 1 );
    
    listProps.add(  new VwPropertyDefinition( strChoiceTypeDataName, DataType.USERDEF, strChoiceTypeName ) );
     
    VwClassGen.MethodParams[] aMthdParams = classGen.allocParams( 1 );
 
    List<Element> listContentMethods = m_mapContentMethods.get( m_strClassName );

    while ( iGroupObjects.hasNext() )
    {
      Object obj = iGroupObjects.next();

      if (obj instanceof Element)
      {
        Element element = (Element)obj;

        String strName = element.getName();
        String strType = element.getType();

        if (strName == null)
        {
          strName = element.getRef();
          strType = getRefType( strName );
        }

        strType = stripNamespace( strType );
        
        String strTypeDataName = strType.substring( 0, 1 ).toLowerCase() + strType.substring( 1 );
        
        if (strType != null)
        {
          
          if (m_fAttrNormalForm && m_strClassName != null)
          {
            listContentMethods.add( element );
          }

          
          if ( strName.equalsIgnoreCase( strChoiceTypeDataName ))
            continue;   // we already created a setter and getter for this but we needed to include this name for the content method if attr normal form
          
          aMthdParams = classGen.allocParams( 1 );
          aMthdParams[0].m_eDataType = DataType.USERDEF;
          aMthdParams[0].m_strName = strTypeDataName;
          aMthdParams[0].m_strUserDefType = strType;
          
          StringBuffer sbSetCode = new StringBuffer( "    m_").append(  strChoiceTypeDataName ).append( " = " );
          sbSetCode.append( strTypeDataName ).append( ";" );
          classGen.addMethod( "set" + strName.substring( 0, 1 ).toUpperCase() + strName.substring( 1 ), VwClassGen.PUBLIC, DataType.VOID, sbSetCode.toString(), null, null,
              null, 0, 0, 0, null, aMthdParams, null );

          StringBuffer sbGetCode = new StringBuffer( "    if ( m_" );
          sbGetCode.append( strChoiceTypeDataName ).append( " instanceof " ).append( strType ).append( "  )\n    " );
          sbGetCode.append( "  return (" ).append( strType ).append( ")m_").append(  strChoiceTypeDataName ).append( ";" );
          sbGetCode.append( "\n\n    return null;\n" );
          
          classGen.addMethod( "get" + strName.substring( 0, 1 ).toUpperCase() + strName.substring( 1 ), VwClassGen.PUBLIC, DataType.USERDEF, sbGetCode.toString(), null, null,
              null, 0, 0, 0, strType, null, null );
 
        }
      } // end if
    } // end while()
    
  }
  
  private String stripNamespace( String strType )
  {

    int nPos = strType.indexOf( ':' );

    if (nPos >= 0)
      return strType.substring( ++nPos );

    return strType;

  }

  /**
   * Determins if this element represenets a collection of elements
   */
  private DataType checkCollectionType( Element element )
  {

    if (element.getUserAttribute( "collection" ) != null)
    {
      String strType = element.getUserAttribute( "collection" ).getType();

      int nPos = strType.indexOf( ':' );
      if ( nPos > 0 )
      {
        String strObjType = strType.substring( nPos + 1 );
        strType = strType.substring( 0, nPos );

      }
      if (strType.indexOf( "LinkedList" ) >= 0)
        return DataType.LINKED_LIST;
      else
      if (strType.indexOf( "LinkedList" ) >= 0)
        return DataType.LIST;
      else
      if (strType.indexOf( "HashMap" ) >= 0)
        return DataType.HASH_MAP;
      else
      if (strType.indexOf( "TreeMap" ) >= 0)
        return DataType.TREE_MAP;
      else
      if (strType.indexOf( "Map" ) >= 0)
        return DataType.GT_MAP;
      else
        return null;
    }

    if (element.getMaxOccurs() != null
        && (element.getMaxOccurs().equals( "*" ) || element.getMaxOccurs().equals( "unbounded" )))
      return DataType.GT_LIST;

    return null;

  } // end checkCollectonType()

  /**
   * Check to see if this element is a collection type and if so, add it to the
   * class def.
   * 
   * @param element
   * @param strMaxOccurs
   * @param classGen
   * @param strName
   * @return
   */
  private boolean addedCollection( Element element, String strMaxOccurs, List<VwPropertyDefinition> listPropDefs, String strName )
  {
    DataType eCollectionType = null;

    if (strMaxOccurs != null && (strMaxOccurs.equals( "unbounded" )))
      eCollectionType = DataType.GT_LIST;
    else
      eCollectionType = checkCollectionType( element );

    if (eCollectionType != null )
    {
      
      String strType = element.getType();
      
      if ( strType  != null)
        strType = extractType( strType );
      else
        strType = strName;
      
      if ( strType.equals( "int" ))
        strType = "integer"; // this is a fixup so that the collection for an int will be an Integer class type
      
      VwPropertyDefinition propDef = new VwPropertyDefinition();
      propDef.setName( strName );
      propDef.setDataType( eCollectionType );
      propDef.setUserType( strType.substring( 0, 1 ).toUpperCase() +  strType.substring( 1 ) );

      String strDefault = element.getDefault();

      propDef.setInitialValue( strDefault );
      listPropDefs.add( propDef );
      
      return true;

    }

    return false;
  }

  /**
   * Return a java data type from an VwSchemaSimpleType
   * 
   */
  private String processSimpleType( SimpleType simpleType )
  {

    if (simpleType.getContent() instanceof Restriction)
    {
      String strBaseType = ((Restriction)simpleType.getContent()).getBase();
      return extractType( strBaseType );
    }

    return null;

  } // end processSimpleType()

  /**
   * Extracts a java type from a schema type
   * 
   * @param strSchemaType
   *          The schema type to extract.
   */
  private String extractType( String strSchemaType )
  {
    String strType = null;

    if (strSchemaType != null)
    {
      int nPos = strSchemaType.indexOf( ':' );

      if (nPos > 0)
        strSchemaType = strSchemaType.substring( nPos + 1 );

      // See if this is a primitive type
      strType = VwXmlToBean.getJavaType( strSchemaType );

      if (strType == null)
        strType = strSchemaType;

    } // end if

    return strType;

  } // end extractTYpe()

  /**
   * Get the datatype for referenced schema element
   * 
   * @param strRefName
   *          The name of the referenced type or element
   */
  private String getRefType( String strRefName )
  {
    int nPos = strRefName.indexOf( ':' );
    
    Object obj = m_schema.getComponent( strRefName.substring( ++nPos ) );

    if (obj instanceof Element)
    {
      Element element = (Element)obj;

      String strType = element.getType();

      if (element.isComplexType() && strType == null)
        return element.getName();

      if (element.getType() != null)
        return extractType( strType );

      return "string";

    }
    else
      if (obj instanceof ComplexType)
        return ((ComplexType)obj).getName();

    return null;

  } // end getRefType()

  /**
   * Extract an VwAppInfo from the annotation object if one exists
   * 
   * @param annotation
   *          The annotation to find te appInfo object in
   * 
   * @return An VwAppInfo object if one exists in the annotation or null
   */
  private static AppInfo getAnnotation( Annotation annotation )
  {
    if (annotation == null)
      return null; // Nothing to do

    Iterator iContent = annotation.getContent().iterator();

    while ( iContent.hasNext() )
    {
      Object objContent = iContent.next();

      if (objContent instanceof AppInfo)
        return (AppInfo)objContent;

    } // end while()

    return null; // No appInfo content found

  } // end getAnnotation()

  /**
   * Get appInfo data from key specified
   * 
   * @param appInfo
   *          The appInfo object
   * @param strKey
   *          The key to get the value for
   * 
   * @return The data to the right of the = sign if key is found or null for no
   *         match
   */
  private static String getAppInfoData( AppInfo appInfo, String strKey )
  {
    VwDelimString dlms = new VwDelimString( ";", appInfo.getContent().toString() );

    String strEntry = null;

    String strSearch = strKey + "=";

    while ( (strEntry = dlms.getNext()) != null )
    {
      int nPos = strEntry.indexOf( strSearch );

      if (nPos >= 0)
        return strEntry.substring( strSearch.length() );

    } // end while()

    return null; // No match

  } // end getAppInfoData()

  /**
   * Extract the class name from a fully qualified package and class
   * 
   * @param strClass
   *          The class name
   * 
   * @return Just the class name
   * 
   */
  private static String getClassName( String strClass )
  {
    int nPos = strClass.lastIndexOf( '.' );

    if (nPos > 0)
      return strClass.substring( nPos + 1 );

    return strClass; // No package was specified

  } // end getClassName()

  /**
   * Extract the package name from a fully qualified package and class
   * 
   * @param strClass
   *          The class name
   * 
   * @return Just the package or null if no package specified
   * 
   */
  private static String getPackageName( String strClass )
  {
    int nPos = strClass.lastIndexOf( '.' );

    if (nPos > 0)
      return strClass.substring( 0, nPos );

    return null; // No package was specified

  } // end getClassName()

  /**
   * Convert java types to Codegen types
   * 
   * @param strType
   *          One of the Java types
   */
  private DataType convertType( String strType )
  {
    if (strType == null)
      return DataType.STRING; // String is the default if no type was
                                  // specified

    int nPos = strType.indexOf( ':' );
    if (nPos > 0)
      strType = strType.substring( ++nPos );

    DataType eCodeGenType = DataType.USERDEF; // Userdef is the default
    if (strType.equals( "String" ))
      eCodeGenType = DataType.STRING;

    if ( m_fUseObjects )
    {
      if (strType.equalsIgnoreCase( "int" ) || strType.equalsIgnoreCase( "integer" ))
        eCodeGenType = DataType.INT_OBJ;
      else
      if (strType.equals( "boolean" ))
          eCodeGenType = DataType.BOOLEAN_OBJ;
     else
     if (strType.equals( "byte" ))
         eCodeGenType = DataType.BYTE_OBJ;
     else
     if (strType.equals( "char" ))
       eCodeGenType = DataType.CHAR_OBJ;
     else
     if (strType.equals( "boolean" ))
         eCodeGenType = DataType.BOOLEAN_OBJ;
     else
     if (strType.equals( "short" ))
          eCodeGenType = DataType.SHORT_OBJ;
     else
     if (strType.equals( "long" ))
        eCodeGenType = DataType.LONG_OBJ;
     else
     if (strType.equals( "float" ))
       eCodeGenType = DataType.FLOAT_OBJ;
     else
     if (strType.equals( "double" ))
       eCodeGenType = DataType.DOUBLE_OBJ;
      
    }
    else
    {
      if (strType.equals( "int" ))
        eCodeGenType = DataType.INT;
      else
      if (strType.equals( "boolean" ))
          eCodeGenType = DataType.BOOLEAN;
     else
     if (strType.equals( "byte" ))
         eCodeGenType = DataType.BYTE;
     else
     if (strType.equals( "char" ))
       eCodeGenType = DataType.CHAR;
     else
     if (strType.equals( "boolean" ))
         eCodeGenType = DataType.BOOLEAN;
     else
     if (strType.equals( "short" ))
          eCodeGenType = DataType.SHORT;
     else
     if (strType.equals( "long" ))
        eCodeGenType = DataType.LONG;
     else
     if (strType.equals( "float" ))
       eCodeGenType = DataType.FLOAT;
     else
     if (strType.equals( "double" ))
       eCodeGenType = DataType.DOUBLE;
    }
    
    return eCodeGenType;

  } // end convertType()

  /**
   * Returns the name of the extension if this schema type is an extension
   */
  private String getBaseClassName( ComplexType type )
  {

    if (!m_fAttrNormalForm && type.isAttributeOnly())
      return null;

    if (!type.isComplexContent())
      return null;

    ComplexContent content = type.getComplexContent();

    if (content.isExtension() || content.isRestriction())
    {
      String strBase = null;

      if (content.isExtension())
      {
        if (!m_fAttrNormalForm && content.getExtension().isAttributeOnly())
          return null;

        strBase = content.getExtension().getBase();
        
      }
      else
      {
        if (!m_fAttrNormalForm && content.getRestriction().isAttributeOnly())
          return null;

        strBase = content.getRestriction().getBase();

      }

      Object objBase = m_schema.getComponent( strBase );

      if (objBase instanceof ComplexType)
      {
        if (!m_fAttrNormalForm && ((ComplexType)objBase).isAttributeOnly())
          return null;
      }

      return stripNamespace( strBase );

    }

    return null;

  } // end getBaseClassName();

  
  /**
   * Program entry point ( for command line use )
   */
  public static void main( String[] astrArgs )
  {
    File fileSchema = null;

    String strBasePath = null;
    String strPackage = null;
    String strReaderType = null;
    String strTypeIncludes = null;
    String strTypeExcludes = null;
    
    boolean fAttrNormalForm = false;
    boolean fUseObjectTypes = false;
    boolean fNoMacroExpansion = false;
    boolean fGenIncludes = true;
    
    // boolean fCreateReaderWriter = false;

    boolean fUseTargetNamespaceAsPackage = false;

    for ( int x = 0; x < astrArgs.length; x++ )
    {
      if (astrArgs[x].equalsIgnoreCase( "-f" ))
        fileSchema = new File( fixPaths( astrArgs[++x] ) );
      else
      if (astrArgs[x].equalsIgnoreCase( "-p" ))
        strPackage = astrArgs[++x];
      else
      if (astrArgs[x].equalsIgnoreCase( "-t" ))
        fUseTargetNamespaceAsPackage = true;
      else
      if (astrArgs[x].equalsIgnoreCase( "-a" ))
        fAttrNormalForm = true;
      else
      if (astrArgs[x].equalsIgnoreCase( "-u" ))
        fUseObjectTypes = true;
      else
      if (astrArgs[x].equalsIgnoreCase( "-o" ))
         strBasePath = fixPaths( astrArgs[++x] );
      else
      if (astrArgs[x].equalsIgnoreCase( "-r" ))
        strReaderType = astrArgs[++x];
      else
      if (astrArgs[x].equalsIgnoreCase( "-m" ))
        fNoMacroExpansion = true;
      else
      if (astrArgs[x].equalsIgnoreCase( "-g" ))
        fGenIncludes = false;
      else
      if (astrArgs[x].equalsIgnoreCase( "-x" ))
        strTypeExcludes = astrArgs[ ++x] ;
      else
      if (astrArgs[x].equalsIgnoreCase( "-i" ))
        strTypeIncludes = astrArgs[ ++x] ;
      
 
    } // end for()

    if (fileSchema == null || strBasePath == null )
    {
      showFormat();
      System.exit( -1 );

    }

    try
    {


    if ( !fileSchema.exists() )
    {
      System.err.println( "The schema file '" + fileSchema.getAbsolutePath() + "' does not exist");
      System.exit( -1 );
      
    }
    
    
      VwSchemaToJava stj = new VwSchemaToJava( fileSchema, strBasePath, strPackage, strReaderType,
                                                 fUseTargetNamespaceAsPackage, fAttrNormalForm,
                                                 fUseObjectTypes, fNoMacroExpansion, fGenIncludes, strTypeIncludes, strTypeExcludes );
      stj.process();

    }
    catch ( Exception e )
    {
      e.printStackTrace();
    }

  } // end main()


  /**
   * If path specifies start with a ./ then expand the current directory and add in the path to form an absolute path
   * @param strPath The path to fixup if it starts with ./
   * @return   The fixed up path
   */
  private static String fixPaths( String strPath )
  {

    strPath = VwExString.expandMacro( strPath );

    if ( !strPath.startsWith( "./" ))
      return strPath;       // nothing to do

    File curDir = new File( ".");

    String strAbsoluteCurDir = curDir.getAbsolutePath();

    return strAbsoluteCurDir.substring( 0, strAbsoluteCurDir.length() -1  ) + strPath.substring( 2 );


  }

  /**
   * Display the command line fromat
   */
  private static void showFormat()
  {
    System.out.println( "VwSchemaToJava -f <Input schema file name>\n"
        + "-o <output directory>\n"
        + "[-r <top level typename> generate a reader and writer class to load generated objects from xml instance data into the specified top level type name]\n"
        + "[-p <the package name> package mame if not using target namespace]\n"
        + "[-t use target namespace as package name]\n"
        + "[-a attribute normal form]\n"
        + "[-u use java object types]\n"
        + "[-g do not create objects in included schemas]\n"
        + "[-m do not resolve(expand) any referenced ${name}]\n"
        + "[-i <comma separated list of schema types to include in DVO generation>]\n"
        + "[-x <comma separated list of schema types to exclude from DVO generation>]");

  } // end showFormat()


} // end class VwSchemaToJava{}

// *** End of VwSchemaToJava.java ***
