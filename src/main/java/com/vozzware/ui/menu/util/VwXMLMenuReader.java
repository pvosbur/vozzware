/*
===========================================================================================

 
                               Copyright(c) 2000 - 2006 by

                      V o z z W a r e   L L C (Vw)

                                 All Rights Reserved

Source Name: VwXMLMenuReader.java

Create Date: May 28, 2006

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.


============================================================================================
*/
package com.vozzware.ui.menu.util;

import com.vozzware.util.VwDocFinder;
import com.vozzware.xml.VwXmlToBean;
import org.xml.sax.InputSource;

import java.net.URL;

/**
 * This class reads XML menu specifications that conform to the VwMenuSpec.xsd schema (also located in this package)
 * @author P. VosBurgh
 *
 */
public class VwXMLMenuReader
{
  public static VwXMLMenuSpec read( URL urlXML ) throws Exception
  {
    VwXmlToBean xtb = new VwXmlToBean();
    xtb.setFeature( VwXmlToBean.ATTRIBUTE_MODEL, true );
    URL urlMenuSchema = VwDocFinder.findURL( "com/itc/ui/menu/VwMenuSpec.xsd");
    
    return (VwXMLMenuSpec)xtb.deSerialize( new InputSource( urlXML.openStream()), VwXMLMenuSpec.class, urlMenuSchema );
    
  }
  
} // end class VwXMLMenuReader{}

// *** End of VwXMLMenuReader.java ***

