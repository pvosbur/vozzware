/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwSchemaToDdl.java

Create Date: Apr 11, 2006
============================================================================================
 */
package com.vozzware.xml.schema.tools;

import com.vozzware.db.VwColInfo;
import com.vozzware.db.VwSchemaDbTypesFactory;
import com.vozzware.db.VwSchemaTypeConverter;
import com.vozzware.db.VwSqlStmtGen;

import javax.xml.schema.Attribute;
import javax.xml.schema.AttributeGroup;
import javax.xml.schema.ComplexType;
import javax.xml.schema.Element;
import javax.xml.schema.ModelGroup;
import javax.xml.schema.Schema;
import javax.xml.schema.SimpleType;
import javax.xml.schema.util.SchemaFactory;
import java.io.File;
import java.io.FileWriter;
import java.sql.DatabaseMetaData;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * This utility class creates CREATE TABLE DDL statements from the complex types
 * defined in an XML Schema
 * 
 */
public class VwSchemaToDdl
{
  private static Map<String,String> s_mapPrimTypes; // Schema primitive types to Java maps map
  private static Map<String,Integer> s_mapDatabaseTypes = new HashMap<String, Integer>();
  
  private File m_fileSchema;                        // The schema file to process

  private Schema m_schema;                          // The schema that we're processing

  private String[] m_astrSubsetTypes;               // Gen only types in the array if not null

  private String m_strSchemaName;                   // Name of schema the prefix table names with

  private StringBuffer m_sbDdl;

  private String m_strDatabaseType;               
                                                 
  private String m_strRiDataType;

  private int m_nDefCharSize = 20;

  private Map<String,String> m_mapTypesGenerated = new HashMap<String,String>();

  private VwSchemaTypeConverter m_typeConverter;

  private boolean m_fAttrNormalForm = false;
  private boolean m_fGenUnderScores = false;
  
  static
  {
    s_mapPrimTypes = new HashMap<String,String>();
    buildPrimTypesMap();
  }

  /**
   * Constructor for a schema residing on disk
   * 
   * @param fileSchema
   *          The schema file to parse
   * @param subset
   *          array of types in the schema that are to be genned to ddl
   * @param strSchemaName
   *          table name prefix to use
   * @param nDbVenderType
   *          One of the VwSchemaDbTypesFactory constants
   */
  public VwSchemaToDdl( File fileSchema, String[] astrSubsetTypes, String strSchemaName,
                         String strDatabaseType, boolean fAttrNormalForm, boolean fGenUnderScores, String strRiDataType  )
  {
    m_fileSchema = fileSchema;
    m_astrSubsetTypes = astrSubsetTypes;
    m_strSchemaName = strSchemaName;
    m_strDatabaseType = strDatabaseType;
    m_nDefCharSize = 1;
    m_fAttrNormalForm = fAttrNormalForm;
    m_fGenUnderScores = fGenUnderScores;
    m_strRiDataType = strRiDataType;
    
  } // end VwSchemaToDdl()

  /**
   * Constructor for an already parsed schema
   * 
   * @param dobjSchema
   *          A data object fully loaded with parsed Xml Schema data
   * @param subset
   *          array of types in the schema that are to be genned to ddl
   * @param strSchemaName
   *          table name prefix to use
   * @param nDbVenderType
   *          One of the VwSchemaDbTypesFactory constants
   */
  public VwSchemaToDdl( Schema schema, String[] astrSubsetTypes, String strSchemaName,
      String strDatabaseType, boolean fAttrNormalForm, boolean fGenUnderScores, String strRiDataType )
  {
    m_schema = schema;
    m_astrSubsetTypes = astrSubsetTypes;
    m_strSchemaName = strSchemaName;
    m_strDatabaseType = strDatabaseType;
    m_nDefCharSize = 1;
    m_fAttrNormalForm = fAttrNormalForm;
    m_fGenUnderScores = fGenUnderScores;
    m_strRiDataType = strRiDataType;

  } // end VwSchemaToDdl()

  /**
   * Process the named schema acordcing to the options
   */
  public String process() throws Exception
  {

    m_sbDdl = new StringBuffer( 1024 );

    Integer intType = s_mapDatabaseTypes.get( m_strDatabaseType.toLowerCase() );
    if ( intType == null )
      throw new Exception( "Invalid database type, must be one of oracle,udb,mysql,sqls or generic");
    
    // Get schema to DDL data type converter for db vendor specified
    m_typeConverter = VwSchemaDbTypesFactory.getConverter( intType );

    if (m_fileSchema != null)
    {

      m_schema = SchemaFactory.getInstance().newReader().readSchema( m_fileSchema.toURL() );

    } // end if

    Iterator iComponents = m_schema.getContent().iterator();

    while ( iComponents.hasNext() )
    {
      Object component = iComponents.next();

      if (component instanceof Element)
        processElement( (Element)component );
      else
      if (component instanceof ComplexType)
        processComplexType( (ComplexType)component, null );

    } // end while()

    return m_sbDdl.toString();

  } // end process()

  /**
   * Process a schema element (VwSchemaElement). If the element is not complex
   * i.e. it doesent define anononmous complex types then skip this element
   */
  private void processElement( Element element ) throws Exception
  {
    // Element must contain a complex type content inoreder to qualifiy to be a
    // class
    if (!element.isComplexType())
      return;

    ComplexType type = element.getComplexType();

    if (!type.hasChildElements())
      return; // No sub elements if this case is true

    processComplexType( type, element.getName() );

  } // end processElement(()

  /**
   * Create a java class file from the elements that make up this named
   * complexType
   * 
   * @param type
   *          The complex type containg the elemenet data
   */
  private void processComplexType( ComplexType type, String strName ) throws Exception
  {


    if (strName == null)
      strName = type.getName();

    if (strName != null)
    {
      if (!m_mapTypesGenerated.containsKey( strName.toLowerCase() ))
        m_mapTypesGenerated.put( strName.toLowerCase(), null );
      else
        return;
    }

    // If the subset array is not null and this name is not in the array, then
    // skip this entry

    if (m_astrSubsetTypes != null)
    {
      if (!isin( strName ))
        return;

    }

    // List List Of VwColInfo classes that contain the column data needed for
    // the table ddl
    List<VwColInfo> listColumns = new ArrayList<VwColInfo>();

    processType( type, listColumns );

    if ( listColumns.size() == 0)
      return;

    String strDdl = VwSqlStmtGen.genTableDDL( strName, m_strSchemaName, listColumns, m_fGenUnderScores );

    m_sbDdl.append( "\n\n" + strDdl + ";" );

  } // end processComplexType()

  /**
   * Process the elements in the complexType
   * 
   * @param type
   *          The complex type object
   * 
   */
  private void processType( ComplexType type, List<VwColInfo> listColumns ) throws Exception
  {
    if (type.hasAttributes() && m_fAttrNormalForm)
      processAttributes( type.getAttributes( false ), listColumns );

    if (type.hasModelGroup())
      processElementGroup( type.getModelGroup(), listColumns );

  } // end processType()

  /**
   * Process The elementGroup content
   * 
   * @param group
   *          The The element group
   */
  private void processElementGroup( ModelGroup group, List<VwColInfo> listColumns ) throws Exception
  {
    Iterator iObjects = group.getContent().iterator();

    while ( iObjects.hasNext() )
    {
      Object obj = iObjects.next();

      if (obj instanceof ModelGroup)
        processElementGroup( (ModelGroup)obj, listColumns );
      else
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

        if (strType != null)
        {
          Object objComp = m_schema.getComponent( strType );
          VwColInfo ci = m_typeConverter.convertType( "int" );
          
          
          ci.setNullable( DatabaseMetaData.columnNullable );
          int nType = ci.getSQLType();
         
          if (nType == Types.CHAR || nType == Types.VARCHAR)
            ci.setColSize( m_nDefCharSize );

          ci.setColumnName( formatName( strName + "_fk") );
          listColumns.add( ci );

          if (objComp instanceof ComplexType)
            processComplexType( (ComplexType)objComp, strName );
          else
            if (objComp instanceof Element)
              processElement( (Element)objComp );

        }

        if (element.isComplexType())
          processElement( element );
        else
        {
          if (element.isSimpleType())
            strType = processSimpleType( element.getSimpleType() );

          VwColInfo ci = m_typeConverter.convertType( strType );

          if (ci == null)
            continue; // This is not a primitive type so skip entry

          ci.setNullable( DatabaseMetaData.columnNullable );

          int nType = (int)ci.getSQLType();

          if (nType == Types.CHAR || nType == Types.VARCHAR)
            ci.setColSize( m_nDefCharSize );

          ci.setColumnName( formatName( strName ) );
          listColumns.add( ci );

        } // end else

      } // end if

    } // end while()

  } // end processElementGroup

  
  private String formatName( String strName )
  {
    if ( ! m_fGenUnderScores )
      return strName;
    
    StringBuffer sbName = new StringBuffer();
    
    for ( int x = 0; x < strName.length(); x++ )
    {
      char ch = strName.charAt( x );
      
      if ( Character.isUpperCase( ch ))
      {
        if ( x > 0  )
          sbName.append( '_' );
        
        sbName.append( Character.toLowerCase( ch ) );
      }
      else
        sbName.append( Character.toLowerCase( ch ) );
      
    }
    
    return sbName.toString();
  }

  /**
   * Getnerate attributes as class properties
   * 
   * @param listAttributes
   *          The list of Attribute objects to process
   * @param classGen
   *          The code generator instance
   */
  private void processAttributes( List listAttributes, List<VwColInfo> listColumns ) throws Exception                          
  {
    for ( Iterator iAttr = listAttributes.iterator(); iAttr.hasNext(); )
    {
      Object objAttr = iAttr.next();
      String strName = null;

      if (objAttr instanceof Attribute)
      {
        Attribute attr = (Attribute)objAttr;
        
        addColumnFromAttribute( attr, listColumns );
         
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
          addColumnFromAttribute( attr, listColumns );

        }

      }

    }
  }

  /**
   * Create an VwColInfo database def from the attribute def
   * @param attr The Attribute containing the data properties
   * @param listColumns The list to add the VwColInfo obkect to
   */
  private void addColumnFromAttribute( Attribute attr, List<VwColInfo> listColumns )
  {
    String strType = attr.getType();
    VwColInfo ci = m_typeConverter.convertType( strType );

    if (ci == null)
      return; // This is not a primitive type so skip entry

    ci.setNullable( DatabaseMetaData.columnNullable );

    int nType = (int)ci.getSQLType();

    if (nType == Types.CHAR || nType == Types.VARCHAR)
      ci.setColSize( m_nDefCharSize );

    ci.setColumnName( formatName( attr.getName() ) );
    listColumns.add( ci );
    
  }

  /**
   * Return a java data type from an VwSchemaSimpleType
   * 
   */
  private String processSimpleType( SimpleType simpleType )
  {

    if (simpleType.isRestriction())
    {
      String strBaseType = simpleType.getRestriction().getBase();
      return extractType( strBaseType );
    }

    return null;

  } // end processSimpleType()

  /**
   * Get the datatype for referenced schema element
   * 
   * @param strRefName
   *          The name of the referenced type or element
   */
  private String getRefType( String strRefName )
  {
    Object obj = m_schema.getComponent( strRefName );

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
      strType = (String)s_mapPrimTypes.get( strSchemaType );

      if (strType == null)
        strType = strSchemaType;

    } // end if

    return strType;

  } // end extractTYpe()

  /**
   * determins if the name in question is the the subset array
   * 
   * @param strName
   *          The name to test for
   * 
   * @return true if the name is in the array, otherwise false is returned
   */
  private boolean isin( String strName )
  {
    for ( int x = 0; x < m_astrSubsetTypes.length; x++ )
    {
      if (strName.equals( m_astrSubsetTypes[x] ))
        return true;
    }

    return false;

  } // end isin()

  /**
   * Build the primitive type conversion map
   */
  private static void buildPrimTypesMap()
  {
    s_mapPrimTypes.put( "string", "String" );
    s_mapPrimTypes.put( "boolean", "boolean" );
    s_mapPrimTypes.put( "Boolean", "Boolean" );
    s_mapPrimTypes.put( "byte", "byte" );
    s_mapPrimTypes.put( "Byte", "Byte" );
    s_mapPrimTypes.put( "short", "short" );
    s_mapPrimTypes.put( "Short", "Short" );
    s_mapPrimTypes.put( "int", "int" );
    s_mapPrimTypes.put( "integer", "int" );
    s_mapPrimTypes.put( "Integer", "Integer" );
    s_mapPrimTypes.put( "long", "long" );
    s_mapPrimTypes.put( "Long", "Long" );
    s_mapPrimTypes.put( "unsignedByte", "byte" );
    s_mapPrimTypes.put( "unsignedSshort", "short" );
    s_mapPrimTypes.put( "unsignedInt", "int" );
    s_mapPrimTypes.put( "unsignedLong", "long" );
    s_mapPrimTypes.put( "float", "float" );
    s_mapPrimTypes.put( "Float", "Float" );
    s_mapPrimTypes.put( "double", "double" );
    s_mapPrimTypes.put( "Double", "Double" );
    s_mapPrimTypes.put( "decimal", "double" );
    s_mapPrimTypes.put( "date", "String" );
    s_mapPrimTypes.put( "time", "String" );
    s_mapPrimTypes.put( "ID", "String" );
    s_mapPrimTypes.put( "IDREF", "String" );
    s_mapPrimTypes.put( "QNAME", "String" );
    s_mapPrimTypes.put( "ENTITY", "String" );
    s_mapPrimTypes.put( "positiveInteger", "int" );
    s_mapPrimTypes.put( "nonPositiveInteger", "int" );
    s_mapPrimTypes.put( "nonNegativeInteger", "int" );
    s_mapPrimTypes.put( "negativeInteger", "int" );
    s_mapPrimTypes.put( "object", "Object" );

    s_mapDatabaseTypes.put( "oracle", 0 );
    s_mapDatabaseTypes.put( "udb", 1 );
    s_mapDatabaseTypes.put( "mysql", 2 );
    s_mapDatabaseTypes.put( "sqls", 3 );
    s_mapDatabaseTypes.put( "generic", 4 );

    
  } // end buildPrimTypesMap()

  /**
   * Program entry point ( for command line use )
   */
  public static void main( String[] astrArgs )
  {
    File fileSchema = null;

    String strOutputFilePath = null;
    String strDatabaseType = "4";
    
    String strSchemaName = null;
    String strRiDatatype = null;
    
    boolean fAttrNormalForm = false;
    boolean fGenUnderScores = false;
    

    for ( int x = 0; x < astrArgs.length; x++ )
    {
      if (astrArgs[x].equalsIgnoreCase( "-f" ))
        fileSchema = new File( astrArgs[++x] );
      else
      if (astrArgs[x].equalsIgnoreCase( "-a" ))
        fAttrNormalForm = true;
      else
      if (astrArgs[x].equalsIgnoreCase( "-o" ))
         strOutputFilePath = astrArgs[++x];
      else
      if (astrArgs[x].equalsIgnoreCase( "-u" ))
        fGenUnderScores = true;
      else
      if (astrArgs[x].equalsIgnoreCase( "-d" ))
        strDatabaseType = astrArgs[++x];
      else
      if (astrArgs[x].equalsIgnoreCase( "-r" ))
        strRiDatatype = astrArgs[++x];
      
    } // end for()

    if (fileSchema == null)
    {
      showFormat();
      System.exit( -1 );

    }

    try
    {
      VwSchemaToDdl stj = new VwSchemaToDdl( fileSchema, null, strSchemaName, strDatabaseType, fAttrNormalForm, fGenUnderScores, strRiDatatype );
      String strDdl = stj.process();
      File fileDdl = new File( strOutputFilePath );
      FileWriter fw = new FileWriter( fileDdl );
      fw.write( strDdl );
      fw.close();

      System.out.println( strDdl );


    }
    catch ( Exception e )
    {
      e.printStackTrace();
    }
  } // end main()

  /**
   * Display the command line format
   */
  private static void showFormat()
  {
    System.out.println( "VwSchemaToDdl -f Schema File name -o output file name -d database type (oracle,udb,mysql,sqls,generic)"
                        + "\n               [-u] generate underscrores for mixed case names] [-a schema is in ANF form"
                        + "\n               [-r datatype] generate referential integrity using the datatype for primary and foreign keys");

  } // end showFormat()

} // end class VwSchemaToDdl{}

// *** End of VwSchemaToDdl.java ***
