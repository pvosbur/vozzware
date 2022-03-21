/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwSchemaWriterImpl.java

Create Date: Apr 11, 2006
============================================================================================
*/
package com.vozzware.xml.schema.util;

import com.vozzware.xml.schema.VwAllImpl;
import com.vozzware.xml.schema.VwAnnotationImpl;
import com.vozzware.xml.schema.VwAnyImpl;
import com.vozzware.xml.schema.VwAppInfoImpl;
import com.vozzware.xml.schema.VwAttributeImpl;
import com.vozzware.xml.schema.VwChoiceImpl;
import com.vozzware.xml.schema.VwComplexContentImpl;
import com.vozzware.xml.schema.VwComplexExtensionImpl;
import com.vozzware.xml.schema.VwComplexRestrictionImpl;
import com.vozzware.xml.schema.VwComplexTypeImpl;
import com.vozzware.xml.schema.VwDocumentationImpl;
import com.vozzware.xml.schema.VwElementImpl;
import com.vozzware.xml.schema.VwEnumerationImpl;
import com.vozzware.xml.schema.VwExtensionImpl;
import com.vozzware.xml.schema.VwFractionDigitsImpl;
import com.vozzware.xml.schema.VwImportImpl;
import com.vozzware.xml.schema.VwIncludeImpl;
import com.vozzware.xml.schema.VwLengthImpl;
import com.vozzware.xml.schema.VwListImpl;
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
import com.vozzware.xml.schema.VwSimpleTypeImpl;
import com.vozzware.xml.schema.VwTotalDigitsImpl;
import com.vozzware.xml.schema.VwUnionImpl;
import com.vozzware.xml.schema.VwWhiteSpaceImpl;

import javax.xml.schema.Schema;
import javax.xml.schema.util.SchemaWriter;
import javax.xml.schema.util.XmlFeatures;
import javax.xml.schema.util.XmlSerializer;
import javax.xml.schema.util.XmlSerializerFactory;
import java.io.File;
import java.io.FileWriter;
import java.net.URL;

/**
 * This class creates a schema document from a Schema instance
 */
public class VwSchemaWriterImpl implements SchemaWriter
{

  /**
   * De-serializes the Schema instance to xml
   *
   * @param schema The Schema instance to de-serialize
   * @return a String containing the XML schema document
   *
   * @throws Exception
   */
  public String writeSchema( Schema schema ) throws Exception
  {


    XmlSerializer btx = XmlSerializerFactory.getSerializer( "com.vozzware.xml.VwBeanToXml" );
    
    setProperties( btx );

    String strXml = btx.serialize( null, schema ); //

    return strXml;
  } // writeSchema()

  /**
   * Writes the Schema object to the specified file
   * @param schema The Schema object to write
   * @param fileSchema The file to write
   * @throws Exception if any io error occur
   */
  public void writeSchema( Schema schema, File fileSchema ) throws Exception
  {
    String strSchema = schema.toString();
    
    FileWriter writer = new FileWriter( fileSchema );
    writer.write( strSchema );
    writer.close();
    
  } // end writeSchema()
  

  /**
   * Sets the properties on the VwBeanToXml writer necessary to write the schema document
   * @param btx the de-serializer to write out the scheama
   * @throws Exception
   */
  public static void setProperties( XmlSerializer btx ) throws Exception
  {

    ClassLoader ldr = Thread.currentThread().getContextClassLoader();
    
    URL urlSchema = ldr.getResource( "com/vozzware/xml/schema/util/schema.dtd" );
    
    btx.addSchema( urlSchema, VwIncludeImpl.class.getPackage() );
    btx.setFeature( XmlFeatures.ATTRIBUTE_MODEL, true );
    btx.setFeature( XmlFeatures.USE_NAMESPACES, true );
    btx.setFormattedOutput( true, 0 );
    
    btx.setObjectElementName( VwAttributeImpl.class, "attribute" );
    btx.setObjectElementName( VwSchemaImpl.class, "schema" );
    btx.setObjectElementName( VwIncludeImpl.class, "include" );
    btx.setObjectElementName( VwImportImpl.class, "import" );
    btx.setObjectElementName( VwAnnotationImpl.class, "annotation" );
    btx.setObjectElementName( VwDocumentationImpl.class, "documentation" );
    btx.setObjectElementName( VwAppInfoImpl.class, "appinfo" );
    btx.setObjectElementName( VwElementImpl.class, "element" );
    btx.setObjectElementName( VwComplexTypeImpl.class, "complexType" );
    btx.setObjectElementName( VwSimpleTypeImpl.class, "simpleType" );
    btx.setObjectElementName( VwComplexContentImpl.class, "complexContent" );
    btx.setObjectElementName( VwExtensionImpl.class, "extension" );
    btx.setObjectElementName( VwComplexExtensionImpl.class, "extension" );
    btx.setObjectElementName( VwRestrictionImpl.class, "restriction" );
    btx.setObjectElementName( VwComplexRestrictionImpl.class, "restriction" );
    btx.setObjectElementName( VwAnyImpl.class, "any" );
    btx.setObjectElementName( VwAllImpl.class, "all" );
    btx.setObjectElementName( VwChoiceImpl.class, "choice" );
    btx.setObjectElementName( VwSequenceImpl.class, "sequence" );
    btx.setObjectElementName( VwListImpl.class, "list" );
    btx.setObjectElementName( VwUnionImpl.class, "union" );
    btx.setObjectElementName( VwLengthImpl.class, "length" );
    btx.setObjectElementName( VwMinLengthImpl.class, "minLength" );
    btx.setObjectElementName( VwMaxLengthImpl.class, "maxLength" );
    btx.setObjectElementName( VwPatternImpl.class, "pattern" );
    btx.setObjectElementName( VwEnumerationImpl.class, "enumeration" );
    btx.setObjectElementName( VwWhiteSpaceImpl.class, "whiteSpace" );
    btx.setObjectElementName( VwMaxInclusiveImpl.class, "maxInclusive" );
    btx.setObjectElementName( VwMaxExclusiveImpl.class, "maxExclusive" );
    btx.setObjectElementName( VwMinInclusiveImpl.class, "minInclusive" );
    btx.setObjectElementName( VwMinExclusiveImpl.class, "minExclusive" );
    btx.setObjectElementName( VwTotalDigitsImpl.class, "totalDigits" );
    btx.setObjectElementName( VwFractionDigitsImpl.class, "fractionDigits" );

    btx.setContentMethods( VwAnyImpl.class, "getAnnotation");
    btx.setContentMethods( VwAttributeImpl.class, "getContent");
    btx.setContentMethods( VwSchemaImpl.class, "getContent");
    btx.setContentMethods( VwAnnotationImpl.class, "getContent");
    btx.setContentMethods( VwDocumentationImpl.class, "getContent");
    btx.setContentMethods( VwAppInfoImpl.class, "getContent");
    btx.setContentMethods( VwElementImpl.class, "getContent");
    btx.setContentMethods( VwComplexTypeImpl.class, "getContent");
    btx.setContentMethods( VwComplexContentImpl.class, "getContent");
    btx.setContentMethods( VwSimpleTypeImpl.class, "getContent");
    btx.setContentMethods( VwListImpl.class, "getContent");
    btx.setContentMethods( VwUnionImpl.class, "getContent");
    btx.setContentMethods( VwExtensionImpl.class, "getContent");
    btx.setContentMethods( VwRestrictionImpl.class, "getContent");
    btx.setContentMethods( VwAllImpl.class, "getContent");
    btx.setContentMethods( VwSequenceImpl.class, "getContent");
    btx.setContentMethods( VwChoiceImpl.class, "getContent");

  } // end setProperties()



} // end class VwSchemaWriterImpl{}

// *** End of VwSchemaWriterImpl.hava ***
