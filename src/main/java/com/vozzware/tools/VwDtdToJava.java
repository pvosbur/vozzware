/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwDtdToJava.java

============================================================================================
*/

package com.vozzware.tools;

import com.vozzware.codegen.DataType;
import com.vozzware.codegen.VwClassGen;
import com.vozzware.codegen.VwCodeOptions;
import com.vozzware.codegen.VwDVOGen;
import com.vozzware.codegen.VwPropertyDefinition;
import com.vozzware.util.VwDelimString;
import com.vozzware.util.VwLogger;
import com.vozzware.xml.dtd.VwDtdAttributeDecl;
import com.vozzware.xml.dtd.VwDtdElementDecl;
import com.vozzware.xml.dtd.VwDtdParser;

import javax.xml.schema.Element;
import javax.xml.schema.ModelGroup;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * This utility class converts certain requested schema types into java classes that hold
 * a parsed xml document instance as well as java classes that build xml documents from
 * defined data sources.
 *
 */
public class VwDtdToJava
{
  private File            m_fileDtdFile;     // The name of teh dtd file to parse

  private File            m_fileOptions = null; // XML file of additional options if not null

  private String          m_strPackage;         // Package for generated classes

  private String          m_strBasePath;        // Base path to start of package

  private String          m_strBaseClassName;   // Optional base class name if not null

  private VwCodeOptions  m_codeOpt;            // Setup options for code generation

  private Map             m_mapElements;        // Element map from dtd parser

  private Map             m_mapAttrs;           // Attribute map from dtd parser

  private Map             m_mapMembers = new HashMap();

  private boolean         m_fStartClassNamesWithUpperCase = true;

  private boolean         m_fNeedUtil = false;  // If true add java.util package to import list

  private String[]        m_astrForceClassGen;  // Array of classes to force generation of
  private boolean         m_fUseAttrNormalForm;

  /**
   * Constructor
   */
  public VwDtdToJava( File fileDtdFile, File fileOptions,
                       String strBasePath, String strPackage, String[] astrForceClassGen, boolean fUseAttrNormalForm  )
  {
    m_fileDtdFile = fileDtdFile;
    m_fileOptions = fileOptions;
    m_strBasePath = strBasePath;
    m_strPackage = strPackage;
    m_astrForceClassGen = astrForceClassGen;
    m_fUseAttrNormalForm = fUseAttrNormalForm;
    
    m_codeOpt = new VwCodeOptions();

    m_codeOpt.m_strAuthor = "Vw";
    m_codeOpt.m_strCopyright = "2001-2002 by Internet Technologies Company";

  } // end VwDtdToJava


  /**
   * Process the named schema according to the options
   */
  public void process() throws Exception
  {
    VwDtdParser parser = new VwDtdParser( m_fileDtdFile.toURL(), m_fileOptions );
    parser.process();

    m_mapElements = parser.getElements();

    m_mapAttrs = parser.getAttributes();

    for ( Iterator iElements = m_mapElements.values().iterator(); iElements.hasNext(); )
    {

      VwDtdElementDecl eleDecl = (VwDtdElementDecl)iElements.next();
      int nContentType = eleDecl.getContentType();

      if ( nContentType != VwDtdElementDecl.PARENT &&
           nContentType != VwDtdElementDecl.MIXED &&
           nContentType != VwDtdElementDecl.ANY &&
           nContentType != VwDtdElementDecl.EMPTY)
        continue;

      processParent( eleDecl );

    }  // end for()

  } // end process()



  /**
   * Create a java class file from the elements that make up this named complexType
   *
   * @param eleDecl The parent element to generate a .java file from
   */
  private void processParent( VwDtdElementDecl eleDecl ) throws Exception
  {

    String strBaseClassName = null;

    if ( hasAttributes( eleDecl ) && !m_fUseAttrNormalForm )
      strBaseClassName = "VwXmlBeanAdapter";

    String strPackage = null;

    if ( strPackage == null )
      strPackage = m_strPackage;

    String strClassName = eleDecl.getName();

    strClassName = makeJavaName( strClassName, true );

    m_mapMembers.clear();

    VwDVOGen dvoGen = new VwDVOGen( m_codeOpt, strClassName, strBaseClassName, strPackage, m_strBasePath );

    List<VwPropertyDefinition> listPropDefs = new ArrayList<VwPropertyDefinition>();

    m_fNeedUtil = false;

    int nContentType = eleDecl.getContentType();

    if ( nContentType == VwDtdElementDecl.MIXED )
    {
      VwPropertyDefinition propDef = new VwPropertyDefinition();
      propDef.setName(makeJavaName( strClassName, false ) );
      propDef.setDataType( DataType.STRING );
      listPropDefs.add(  propDef );
      

    }
    else
    if ( nContentType == VwDtdElementDecl.ANY )
    {
      VwPropertyDefinition propDef = new VwPropertyDefinition();
      propDef.setName( "content");
      propDef.setDataType( DataType.USERDEF );
      propDef.setUserType( "VwDataObject" );
      
      listPropDefs.add(  propDef );
      dvoGen.addInterface( "VwXmlAnyTypeBean" );
      
 
    }

    // *** Generate properties for each child element except ofr content type of ANY

    if ( m_fUseAttrNormalForm )
    {
      Object objAttrs = m_mapAttrs.get(  eleDecl.getName() );
      
      if ( objAttrs != null )
      {
        addAttributes( objAttrs, listPropDefs );
      }
    }
    if ( nContentType != VwDtdElementDecl.ANY && nContentType != VwDtdElementDecl.EMPTY)
      processGroup( eleDecl.getGroup(), dvoGen.getClassGen(), listPropDefs );


    // *** Write the java class file

    dvoGen.genDvo( listPropDefs, VwLogger.getInstance() );

  } // end processComplexType()


  /**
   * @param objAttrs
   * @param classGen
   */
  private void addAttributes( Object objAttrs, List<VwPropertyDefinition> listPropDefs  )
  {
    List<VwDtdAttributeDecl>listAttrs = null;
    
    if ( objAttrs instanceof List )
      listAttrs = (List<VwDtdAttributeDecl>)objAttrs;
    else
    {
      listAttrs = new ArrayList<VwDtdAttributeDecl>();
      listAttrs.addAll( (List<VwDtdAttributeDecl>)objAttrs );
    }
    
    for ( VwDtdAttributeDecl attr : listAttrs )
    {
      VwPropertyDefinition propDef = new VwPropertyDefinition();
      propDef.setName( makeJavaName( attr.getAttrName(), false ) );
      propDef.setDataType( DataType.STRING );
      listPropDefs.add( propDef );
     }
      
  }




  /**
   * Process the element group
   *
   * @param group The group of elements and or nested groups to process
   * classGen The Class generation instance to add the class properties
   */
  private void processGroup( ModelGroup group, VwClassGen classGen, List<VwPropertyDefinition> listPropDefs )
  {
    Iterator iContent = group.getContent().iterator();

    while( iContent.hasNext() )
    {
      Object objContent = iContent.next();
      if ( objContent instanceof ModelGroup )
      {
        processGroup( (ModelGroup)objContent, classGen,  listPropDefs );
        continue;

      }


      Element element = (Element)objContent;

      if ( m_mapMembers.containsKey( element.getName() ) )
        continue;       // member already added

      m_mapMembers.put( element.getName(), null );

      String strEleName = element.getName();
      VwDtdElementDecl eleDecl = (VwDtdElementDecl)m_mapElements.get( strEleName.toLowerCase() );

      if ( eleDecl == null )
      {
        continue;
      }

      Element elementType = null;

      String strOccurs = null;

      int nType = eleDecl.getContentType();

      if ( elementType != null )
        strOccurs = elementType.getMaxOccurs();
      else
        strOccurs = element.getMaxOccurs() ;

      if ( strOccurs == null )
      {
        strOccurs = group.getMaxOccurs();
      }

      if (  nType == VwDtdElementDecl.EMPTY )
      {

        if ( m_fUseAttrNormalForm )
        {
          String strPropName = strEleName;
          strPropName = makeJavaName( strPropName, false );

          String  strName = element.getName();

          strName = makeJavaName( strName, true );

          VwPropertyDefinition propDef = new VwPropertyDefinition();
          propDef.setName( strPropName );
          propDef.setDataType( DataType.USERDEF );
          propDef.setUserType( strName );
          
          listPropDefs.add( propDef );
          
        }
        else
        {
            classGen.addMethod( "get" + makeJavaName( element.getName(), true ), VwClassGen.PUBLIC, DataType.STRING,
              "    return null; ", "Placeholder for EMPTY content defined tags", null,
              null, 0, 0, 0, null, null, null );

        }

      }
      else
      if ( nType == VwDtdElementDecl.ANY && !m_fUseAttrNormalForm)
      {
        if ( strOccurs != null && !(strOccurs.equals( "1" )) )
        {
          VwPropertyDefinition propDef = new VwPropertyDefinition();
          propDef.setName( makeJavaName( element.getName(), false ) );
          propDef.setDataType( DataType.GT_LIST );
          propDef.setUserType( makeJavaName( element.getName(), true ) );
          
          listPropDefs.add( propDef );

         }
        else
        {
          String strPropName = strEleName;
          strPropName = makeJavaName( strPropName, false );

          String  strName = element.getName();

          strName = makeJavaName( strName, true );

          VwPropertyDefinition propDef = new VwPropertyDefinition();
          propDef.setName( strPropName );
          propDef.setDataType( DataType.USERDEF );
          propDef.setUserType( strName );
          
          listPropDefs.add( propDef );

        }
      }
      else
      if (  nType == VwDtdElementDecl.PCDATA  && !m_fUseAttrNormalForm )
      {

        if ( strOccurs != null && !(strOccurs.equals( "1" )) )
        {
          VwPropertyDefinition propDef = new VwPropertyDefinition();
          propDef.setName( makeJavaName( element.getName(), false ) );
          propDef.setDataType( DataType.GT_LIST );
          propDef.setUserType( makeJavaName( element.getName(), true ) );
          
          listPropDefs.add( propDef );
        }
        else
        {
          VwPropertyDefinition propDef = new VwPropertyDefinition();
          propDef.setName( makeJavaName( element.getName(), false ) );
          propDef.setDataType( DataType.STRING );
          
          listPropDefs.add( propDef );
        }

      }
      else
      if ( nType == VwDtdElementDecl.PARENT ||
           nType == VwDtdElementDecl.MIXED )
      {
        if ( strOccurs != null && !(strOccurs.equals( "1" )) )
        {
          VwPropertyDefinition propDef = new VwPropertyDefinition();
          propDef.setName( makeJavaName( element.getName(), false ) );
          propDef.setDataType( DataType.GT_LIST );
          propDef.setUserType( makeJavaName( element.getName(), true ) );
          
          listPropDefs.add( propDef );
        }
        else
        {
          String strName = null;
          String strPropName = strEleName;


          if ( elementType != null )
            strName = elementType.getName();
          else
            strName = element.getName();

         strName = makeJavaName( strName, true );

         strPropName = makeJavaName( strPropName, false );

         VwPropertyDefinition propDef = new VwPropertyDefinition();
         propDef.setName( strPropName );
         propDef.setDataType( DataType.USERDEF );
         propDef.setUserType( strName );
         
         listPropDefs.add( propDef );

        } // end else

      }

    }
  } // end processGroup()


  /**
   * @param strName
   * @return
   */
  private String makeJavaName( String strName, boolean fIsClassName )
  {
    StringBuffer sbClassName = new StringBuffer();
    boolean fNeedUpper = fIsClassName;
    
    if ( !fIsClassName )
      strName = strName.substring( 0, 1 ).toLowerCase() + strName.substring( 1 );
    
    for ( int x = 0; x < strName.length(); x++ )
    {
      char ch = strName.charAt( x );
      
      if ( fNeedUpper )
      {
        ch =  Character.toUpperCase( ch );
        fNeedUpper = false;
      }
      if ( ch == '-' || ch == '_' )
      {
        fNeedUpper = true;
        continue;
      }
      
      sbClassName.append( ch );
      
    } // end for()
    
    return sbClassName.toString();
  }


  /**
   * Test this elemenet and any of its children for attributes
   */
  private boolean hasAttributes( VwDtdElementDecl eleDecl )
  {
    if ( m_mapAttrs.containsKey( eleDecl.getName() ) )
      return true;

     ModelGroup group = eleDecl.getGroup();

    if ( group != null )
     return handleAttrGroup( group );

    return false;
  }


  private boolean handleAttrGroup( ModelGroup group )
  {
    for ( Iterator iElements = group.getContent().iterator(); iElements.hasNext(); )
    {
      Object obj = iElements.next();

      if ( obj instanceof Element )
      {
        if ( m_mapAttrs.containsKey( ((Element)obj).getName() ) )
          return true;
      }
      else
      if ( obj instanceof ModelGroup )
      {
        if ( handleAttrGroup( (ModelGroup)obj ) )
          return true;
      }

    } // end for()

    return false;
  }
  /**
   * Program entry point ( for command line use )
   */
  public static void main( String[] astrArgs )
  {
    File fileDtdFile = null;
    File fileXmlOptions = null;

    String strBasePath = null;
    String strPackage = null;
    String[] astrForcedClassGen = null;

    boolean fUseAttrNormalForm = false;
    
    for ( int x = 0; x < astrArgs.length; x++ )
    {
      if ( astrArgs[ x ].equalsIgnoreCase( "-n" ) )
      {
        fileDtdFile = new File( astrArgs[ ++x ] );
      }
      else
      if ( astrArgs[ x ].equalsIgnoreCase( "-x" ) )
      {
        fileXmlOptions = new File( astrArgs[ ++x ] );
      }
      else
      if ( astrArgs[ x ].equalsIgnoreCase( "-p" ) )
      {
        strPackage = astrArgs[ ++x ];
      }
      else
      if ( astrArgs[ x ].equalsIgnoreCase( "-a" ) )
      {
        fUseAttrNormalForm = true;
      }
      else
      if ( astrArgs[ x ].equalsIgnoreCase( "-b" ) )
      {
        strBasePath = astrArgs[ ++x ];
      }
      else
      if ( astrArgs[ x ].equalsIgnoreCase( "-f" ) )
      {
        VwDelimString dlms = new VwDelimString( ",", astrArgs[ ++x ] );
        astrForcedClassGen = dlms.toStringArray();
      }

    } // end for()

    if ( fileDtdFile == null )
    {
      showFormat();
      System.exit( -1 );

    }

    try
    {
      VwDtdToJava stj = new VwDtdToJava( fileDtdFile, fileXmlOptions,
                                           strBasePath, strPackage, astrForcedClassGen, fUseAttrNormalForm );
      stj.process();

    }
    catch( Exception e )
    {
      e.printStackTrace();
    }
  } // end main()


  /**
   * Display the command line fromat
   */
  private static void showFormat()
  {
    System.out.println( "VwDtdToJava -n Schema File name -p Package Name\n"
                        + "-b Base Path to which the Package directories will be appended to\n"
                        + "-f comma separated list of classes to force generation of"  );

  } // end showFormat()


} // end class VwDtdToJava{}

// *** End VwDtdToJava.java ***
