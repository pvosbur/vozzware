/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwWSDLFactoryImpl.java

Create Date: Apr 11, 2006
============================================================================================
*/
package com.vozzware.wsdl.util;

import com.vozzware.wsdl.VwDefinitionImpl;

import javax.wsdl.Definition;
import javax.wsdl.util.WSDLFactory;
import javax.wsdl.util.WSDLReader;
import javax.wsdl.util.WSDLWriter;

public class VwWSDLFactoryImpl extends WSDLFactory
{

  public Definition newWsdl()
  { 
    Definition definition = new VwDefinitionImpl();
    return definition;
  }

  public WSDLReader newReader()
  { return new VwWSDLReaderImpl(); }


  public WSDLWriter newWriter()
  { return new VwWSDLWriterImpl(); }

} // end class VwSchemaFactoryImpl{}

// *** End of VwSchemaFactoryImpl.java ***

