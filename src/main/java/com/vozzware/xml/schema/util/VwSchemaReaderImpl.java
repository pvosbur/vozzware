/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwSchemaReaderImpl.java

Create Date: Apr 11, 2006
============================================================================================
*/
package com.vozzware.xml.schema.util;

import com.vozzware.util.VwDocFinder;
import com.vozzware.util.VwExString;
import com.vozzware.util.VwResourceStoreFactory;
import com.vozzware.xml.schema.VwAllImpl;
import com.vozzware.xml.schema.VwAnnotationImpl;
import com.vozzware.xml.schema.VwAnyImpl;
import com.vozzware.xml.schema.VwAppInfoImpl;
import com.vozzware.xml.schema.VwAttributeGroupImpl;
import com.vozzware.xml.schema.VwAttributeImpl;
import com.vozzware.xml.schema.VwChoiceImpl;
import com.vozzware.xml.schema.VwComplexContentImpl;
import com.vozzware.xml.schema.VwComplexTypeImpl;
import com.vozzware.xml.schema.VwDocumentationImpl;
import com.vozzware.xml.schema.VwElementImpl;
import com.vozzware.xml.schema.VwEnumerationImpl;
import com.vozzware.xml.schema.VwExtensionImpl;
import com.vozzware.xml.schema.VwFractionDigitsImpl;
import com.vozzware.xml.schema.VwImportImpl;
import com.vozzware.xml.schema.VwIncludeImpl;
import com.vozzware.xml.schema.VwLengthImpl;
import com.vozzware.xml.schema.VwMaxExclusiveImpl;
import com.vozzware.xml.schema.VwMaxInclusiveImpl;
import com.vozzware.xml.schema.VwMaxLengthImpl;
import com.vozzware.xml.schema.VwMinExclusiveImpl;
import com.vozzware.xml.schema.VwMinInclusiveImpl;
import com.vozzware.xml.schema.VwMinLengthImpl;
import com.vozzware.xml.schema.VwPatternImpl;
import com.vozzware.xml.schema.VwRestrictionImpl;
import com.vozzware.xml.schema.VwSchemaImpl;
import com.vozzware.xml.schema.VwSequenceImpl;
import com.vozzware.xml.schema.VwSimpleContentImpl;
import com.vozzware.xml.schema.VwSimpleTypeImpl;
import com.vozzware.xml.schema.VwTotalDigitsImpl;
import com.vozzware.xml.schema.VwWhiteSpaceImpl;
import org.xml.sax.InputSource;

import javax.xml.schema.Documentation;
import javax.xml.schema.InvalidSchemaLocationException;
import javax.xml.schema.Schema;
import javax.xml.schema.util.SchemaReader;
import javax.xml.schema.util.XmlCloseElementEvent;
import javax.xml.schema.util.XmlCloseElementListener;
import javax.xml.schema.util.XmlDeSerializer;
import javax.xml.schema.util.XmlDeSerializerFactory;
import java.io.File;
import java.net.URL;


public class VwSchemaReaderImpl implements XmlCloseElementListener, SchemaReader
{

  /**
   * Reads an XML Schema specified by the URL
   *
   * @param urlSchema The URL to the schema document
   * @return a Schema instance
   *
   * @throws Exception if any IO or xml format errors in the schema document occur
   */
  public Schema readSchema( URL urlSchema ) throws Exception
  {
    XmlDeSerializer xtb = XmlDeSerializerFactory.getDeSerializer( "com.vozzware.xml.VwXmlToBean");
    
    setSchemaProperties( xtb, this );

    InputSource ins = new InputSource( urlSchema.openStream() );

    Schema schema = (Schema)xtb.deSerialize( ins, VwSchemaImpl.class, null );

    return schema;

  } // end readSchema()

  /**
   *
   * @param strLocationURI
   * @return
   * @throws Exception
   */
  public Schema readSchema( String strLocationURI ) throws Exception
  {
    XmlDeSerializer xtb = XmlDeSerializerFactory.getDeSerializer( "com.vozzware.xml.VwXmlToBean");
    setSchemaProperties( xtb, this );

    // first see if the resource store can find this
    URL urlSchema = null;
    
    if ( strLocationURI.startsWith( "http:" ) || strLocationURI.startsWith( "file:" ) )
        urlSchema = new URL( strLocationURI );
    else
    {  
      
      if ( VwExString.findAny( strLocationURI, "/\\", 0 ) < 0 ) // no path separaters defined so assume the VwResourceSore can find this
        urlSchema = VwResourceStoreFactory.getInstance().getStore().getDocument( strLocationURI );
    
      if ( urlSchema == null ) // see if it's in a different classpath location
        urlSchema = VwDocFinder.findURL( strLocationURI );
      
      if ( urlSchema == null ) // assume an absolute directory path
      {
        File file = new File( strLocationURI );
        if ( file.exists() )
          urlSchema = file.toURL();
      }
        
    }

    
    if ( urlSchema == null )
    {
      String strMsg = "Cannot file the file '" + strLocationURI + "' specified in the <include> tag";
      System.err.println( strMsg );
      throw new InvalidSchemaLocationException( strMsg );
    }

    InputSource ins = new InputSource( urlSchema.openStream() );


    Schema schema = (Schema)xtb.deSerialize( ins, VwSchemaImpl.class, null );

    return schema;

  }


  public static void setSchemaProperties( XmlDeSerializer xtb, XmlCloseElementListener ctl ) throws Exception
  {

    xtb.setCloseElementListener( "documentation", null, ctl );
    xtb.setFeature( XmlDeSerializer.ATTRIBUTE_MODEL, true  );
    
    xtb.setTopLevelElementName( "schema" );
    
    xtb.setElementHandler( "attribute", VwAttributeImpl.class );
    xtb.setElementHandler( "attributeGroup", VwAttributeGroupImpl.class );
    xtb.setElementHandler( "element", VwElementImpl.class );
    xtb.setElementHandler( "include", VwIncludeImpl.class );
    xtb.setElementHandler( "import", VwImportImpl.class );
    xtb.setElementHandler( "annotation", VwAnnotationImpl.class );
    xtb.setElementHandler( "appinfo", VwAppInfoImpl.class );
    xtb.setElementHandler( "documentation", VwDocumentationImpl.class );
    xtb.setElementHandler( "simpleType", VwSimpleTypeImpl.class );
    xtb.setElementHandler( "simpleContent", VwSimpleContentImpl.class );
    xtb.setElementHandler( "complexType", VwComplexTypeImpl.class );
    xtb.setElementHandler( "complexContent", VwComplexContentImpl.class );
    xtb.setElementHandler( "extension", VwExtensionImpl.class );
    xtb.setElementHandler( "restriction", VwRestrictionImpl.class );
    xtb.setElementHandler( "any", VwAnyImpl.class );
    xtb.setElementHandler( "sequence", VwSequenceImpl.class );
    xtb.setElementHandler( "all", VwAllImpl.class );
    xtb.setElementHandler( "choice", VwChoiceImpl.class );
    xtb.setElementHandler( "length", VwLengthImpl.class );
    xtb.setElementHandler( "minLength", VwMinLengthImpl.class );
    xtb.setElementHandler( "maxLength", VwMaxLengthImpl.class );
    xtb.setElementHandler( "pattern", VwPatternImpl.class );
    xtb.setElementHandler( "enumeration", VwEnumerationImpl.class );
    xtb.setElementHandler( "whiteSpace", VwWhiteSpaceImpl.class );
    xtb.setElementHandler( "maxInclusive", VwMaxInclusiveImpl.class );
    xtb.setElementHandler( "maxExclusive", VwMaxExclusiveImpl.class );
    xtb.setElementHandler( "minInclusive", VwMinInclusiveImpl.class );
    xtb.setElementHandler( "minExclusive", VwMinExclusiveImpl.class );
    xtb.setElementHandler( "totalDigits", VwTotalDigitsImpl.class );
    xtb.setElementHandler( "fractionDigits", VwFractionDigitsImpl.class );

  } // end setSchemaProperties()


  public void xmlTagClosed( XmlCloseElementEvent closeEvent )
  {
    Object objBean = closeEvent.getTagHandler();
    String strData = closeEvent.getTagData();

    ((Documentation)objBean).setContent( strData );


  } // end
} // end class VwSchemaReaderImpl

// *** End of VwSchemaReaderImpl.java ***
