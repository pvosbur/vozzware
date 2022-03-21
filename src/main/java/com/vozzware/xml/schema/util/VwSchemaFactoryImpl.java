/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwSchemaFactoryImpl.java

Create Date: Apr 11, 2006
============================================================================================
*/
package com.vozzware.xml.schema.util;

import com.vozzware.xml.namespace.Namespace;
import com.vozzware.xml.namespace.QName;
import com.vozzware.xml.schema.VwSchemaImpl;

import javax.xml.schema.Schema;
import javax.xml.schema.util.SchemaFactory;
import javax.xml.schema.util.SchemaReader;
import javax.xml.schema.util.SchemaWriter;

public class VwSchemaFactoryImpl extends SchemaFactory
{

  public Schema newSchema()
  { 
    Schema schema = new VwSchemaImpl();
    schema.addNamespace( new Namespace( "xsd", VwSchemaImpl.XML_SCHEMA1_0_URI));
    schema.setQName( new QName("xsd", VwSchemaImpl.XML_SCHEMA1_0_URI, "schema" ) );
    
    return schema;
  }

  public SchemaReader newReader()
  { return new VwSchemaReaderImpl(); }


  public SchemaWriter newWriter()
  { return new VwSchemaWriterImpl(); }

} // end interface VwSchemaFactoryImpl{}

// *** End of VwSchemaFactoryImpl.java ***

