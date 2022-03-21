/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwXmlToXmlSchema.java

Create Date: Apr 11, 2006
============================================================================================
*/
package com.vozzware.xml.schema.tools;

import com.vozzware.util.VwLogger;
import com.vozzware.xml.VwDataObjList;
import com.vozzware.xml.VwDataObject;
import com.vozzware.xml.VwElement;
import com.vozzware.xml.VwElementList;
import com.vozzware.xml.VwXmlToDataObj;
import com.vozzware.xml.namespace.Namespace;
import com.vozzware.xml.namespace.QName;
import com.vozzware.xml.schema.VwSchemaImpl;
import org.xml.sax.Attributes;

import javax.xml.schema.Attribute;
import javax.xml.schema.ComplexType;
import javax.xml.schema.Element;
import javax.xml.schema.ModelGroup;
import javax.xml.schema.Schema;
import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author peter
 *
 */
public class VwXmlToXmlSchema
{
  private String  m_strTargetNamespace;
  private File    m_fileXml;
  private File    m_fileSchema;
  
  private Schema  m_xmlSchema;
  private Map<String,ComplexType>  m_mapTypes = new HashMap<String,ComplexType>();
  
  private boolean m_fHoldTagObjectsForDocLife = false;

  public VwXmlToXmlSchema( String strTargetNamespace, File fileXml, File fileSchema )
  { this( strTargetNamespace,fileXml,fileSchema, false ); }
  

  public VwXmlToXmlSchema( String strTargetNamespace, File fileXml, File fileSchema, boolean fHoldTagObjectsForDocLife )
  {
    m_strTargetNamespace = strTargetNamespace;
    m_fileXml = fileXml;
    m_fileSchema = fileSchema;
    m_fHoldTagObjectsForDocLife = fHoldTagObjectsForDocLife;
    
  } // end VwXmlToXmlSchema()
  
  public void makeSchema() throws Exception
  {
    VwLogger logger = VwLogger.getInstance();
    logger.info( null, "Creating schema " + m_fileSchema.getAbsolutePath()  );
    
    m_xmlSchema = new VwSchemaImpl();
    m_xmlSchema.setTargetNamespace( m_strTargetNamespace );
    m_xmlSchema.addNamespace( new Namespace( "xsd", "http://www.w3.org/2001/XMLSchema") );
    m_xmlSchema.addNamespace( new Namespace( "tns", m_strTargetNamespace) );
    m_xmlSchema.setQName( new QName( "xsd", "http://www.w3.org/2001/XMLSchema", "schema") );
    
    VwXmlToDataObj xtd = new VwXmlToDataObj( true, true );
    
    xtd.makeDataObjectsForParentTags();
    xtd.holdTagObjectsForDocLife( m_fHoldTagObjectsForDocLife );
    
    VwDataObject dobjTop = xtd.parse( m_fileXml, false );
    
    String strRootName = dobjTop.getRootElementName();
    
    Object obj = dobjTop.getObject( strRootName );
    
    if ( obj instanceof VwDataObject || obj instanceof VwElement )
    {
      String strTypeName = createTypeName( strRootName);
      Element eleRoot = m_xmlSchema.createElement( strRootName, strTypeName );
      eleRoot.setType( createTypeName( "tns:" + strTypeName ) );
      m_xmlSchema.addElement( eleRoot );
      
      
      if ( obj instanceof VwDataObject )
        processDataObject( strRootName, (VwDataObject)obj );
      else
      {
        dobjTop.remove( strRootName );
        processDataObject( strRootName, dobjTop );
        
      }
    }
    
    if ( obj == null )
    {
      String strTypeName = createTypeName( strRootName);
      Element eleRoot = m_xmlSchema.createElement( strRootName, strTypeName );
      eleRoot.setType( createTypeName( "tns:" + strTypeName ) );
    
      m_xmlSchema.addElement( eleRoot );
      processDataObject( strRootName, dobjTop );
      
    }
    
    String strSchema = m_xmlSchema.toString();
    FileWriter fw = new FileWriter( m_fileSchema );
    fw.write( strSchema  );
    fw.close();
 
    logger.info( null, "Schema " + m_fileSchema.getAbsolutePath() + " successfully created");

    
  }
  

  /**
   * @param listAttr
   * @param type
   */
  private void processAttributes( Attributes listAttr, ComplexType type )
  {
    for ( int x = 0; x < listAttr.getLength(); x++ )
    {
      Attribute attr = null;
      String strAttrName = listAttr.getLocalName( x );
      
      if ( type.getAttribute( strAttrName ) != null )
        continue;
      
      attr = m_xmlSchema.createAttribute( listAttr.getQName( x ), "xsd:string" );
      type.addAttribute( attr );
      
    }
    
  } // end  processAttributes()

  /**
   * Process the contents of the data object
   */
  private void processDataObject( String strDataObjName, VwDataObject dataObj )
  {
    // Iterate the map keys to build the xml document

    ComplexType cType = null;
    
    if ( m_xmlSchema != null )
    {
      cType = getComplexType( strDataObjName );
      
      
      Attributes listAttr = dataObj.getAttributeList( strDataObjName );
      
      if ( listAttr != null )
        processAttributes( listAttr, cType );
      
      Iterator iKeys = dataObj.keys();

      ModelGroup mGroup = null;
    
      if ( iKeys.hasNext() )
      {
        mGroup = cType.getModelGroup();
        if ( mGroup == null )
        {
          mGroup = m_xmlSchema.createSequence();
          cType.setModelGroup( mGroup );
        }
      }
      
      while ( iKeys.hasNext() )
      {

        String strKey = (String)iKeys.next();
        
        if ( strKey.equals( strDataObjName ))
          continue;
        
        Object objData = dataObj.get( strKey );
        
        if ( objData == null )
        {
          objData = dataObj.getObject( strKey );
        }
        
        
        Element element = mGroup.findElement( strKey );
        
        if ( element == null )
        {
          element = m_xmlSchema.createElement( strKey, null );
          mGroup.addElement( element );
        }
        
        if ( objData instanceof VwDataObject )
        {
          element.setType( createTypeName( "tns:" + strKey ) );
          processDataObject( strKey, (VwDataObject)objData );
        }
        else
        if ( objData instanceof VwDataObjList ) 
        {
          element.setType( createTypeName( "tns:" + strKey ) );
          element.setMaxOccurs( "unbounded");
          processDataObject( strKey, (VwDataObject)((VwDataObjList)objData).get( 0 ) );
        }
        else
        if ( objData instanceof VwElementList )
        {
          VwElement itcElement = (VwElement)((VwElementList)objData).get( 0 );
          
          if ( itcElement.getValue() == null )
          {
            element.setType( createTypeName( "tns:" + strKey ) );
            element.setMaxOccurs( "unbounded");
          }
          else
            element.setType( "xsd:string");
           
          processElement( strKey, element, itcElement );
        }
        else
        if ( objData instanceof VwElement)
        {
          processElement( strKey, element, (VwElement)objData );
        }  
        else
        if ( objData instanceof String )
          element.setType( "xsd:string");
        
      } // end while()
      
    } // end if


  } // end processDataObject()


 /**
  * Create a new ComplexType object for the name if it does not exist in the cache
  * @param strDataObjName
  * @return
  */
  private ComplexType getComplexType( String strDataObjName )
  {
    ComplexType cType = (ComplexType)m_mapTypes.get( strDataObjName );
    if ( cType == null )
    {
      cType = m_xmlSchema.createComplexType();
      m_mapTypes.put( strDataObjName, cType );
      m_xmlSchema.addComplexType( cType );
      cType.setName( createTypeName( makeComplexTypeName( strDataObjName ) ) );
    }
  
    return cType;
  }

  private String makeComplexTypeName( String strDataName )
  {
    String strCTypeName = strDataName.substring( 0, 1 ).toUpperCase() + strDataName.substring( 1 );
    
    return strCTypeName;
  }

  private void processElement( String strKey, Element element, VwElement itcElement)
  {
    
    Attributes eleAttrs = itcElement.getAttributes();
    Object objChild = itcElement.getChildObject();
    
    if ( eleAttrs != null )
    {
      ComplexType eleCtype = getComplexType( strKey );
       if ( eleAttrs != null )
      {
        processAttributes( eleAttrs, eleCtype );
        element.setType( "tns:" + makeComplexTypeName(strKey ));
      }
       
    }
    
    if ( objChild instanceof VwDataObjList )
    {
      processDataObject( strKey, (VwDataObject)((VwDataObjList)objChild).get( 0 ) );
      
    }
    
  }

  /**
   * @param strName
   * @return
   */
  private String createTypeName( String strName )
  {
    int nPos = strName.indexOf( ':' );
    String strTypeName = null;
    
    String strPrefix = "";
    if ( nPos > 0 )
    {
      strPrefix = strName.substring( 0, nPos );
      strTypeName = strName.substring( ++nPos );
      
    }
    else
      strTypeName = strName;
    
    strTypeName = strTypeName.substring( 0, 1 ).toUpperCase() + strTypeName.substring( 1 );
    
    if ( nPos >= 0 )
      return strPrefix + ":" + strTypeName;
    
    return strTypeName;
  }
  
  
  public static void main( String[] astrArgs )
  {
    File fileXml = null;
    File fileSchema = null;
    String strTargetNamespace = null;
    boolean fHoldTagObjectsForDocLife = false;
    
    for ( int x = 0; x < astrArgs.length; x++ )
    {
      if ( astrArgs[ x ].equalsIgnoreCase( "-x" ) )
      {
        fileXml = new File( astrArgs[ ++x ] );
      }
      else
      if ( astrArgs[ x ].equalsIgnoreCase( "-o" ) )
      {
        fileSchema = new File( astrArgs[ ++x ] );
      }
      else
      if ( astrArgs[ x ].equalsIgnoreCase( "-t" ) )
      {
        strTargetNamespace = astrArgs[ ++x ];
      }
      else
      if ( astrArgs[ x ].equalsIgnoreCase( "-h" ) )
      {
        fHoldTagObjectsForDocLife = true;
      }
      else
      {
        showFormat();
        System.exit( -1 );
        
      }
    } // end for()

    if ( fileSchema == null || fileXml == null)
    {
      showFormat();
      System.exit( -1 );

    }
    
    try
    {
      VwXmlToXmlSchema xts = new VwXmlToXmlSchema( strTargetNamespace, fileXml, fileSchema, fHoldTagObjectsForDocLife );
      xts.makeSchema();
    
    }
    catch( Exception ex )
    {
      ex.printStackTrace();
      System.exit( -1 );
    }
  }


  private static void showFormat()
  {
    System.out.println( "VwXmlToXmlSchema -x Input xml file name -o output scheama file path [-t target namespace of schema document] [-h Hold tag elements for doc life]" );
     
  }
} // end class VwXmlToXmlSchema{}

//*** End of VwXmlToXmlSchema.java ***
