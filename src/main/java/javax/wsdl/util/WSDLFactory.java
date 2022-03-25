/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: WSDLFactory.java

============================================================================================
*/
package javax.wsdl.util;

import javax.wsdl.Definition;
import java.util.ResourceBundle;

public abstract class WSDLFactory
{
  private static WSDLFactory s_instance = null;

  /**
   * Private constructor for singleton
   */
  protected WSDLFactory()
  { ;  }

  
  public synchronized static WSDLFactory getInstance() throws Exception
  {

    if ( s_instance == null )
    {
      ResourceBundle resource = ResourceBundle.getBundle( "resources.properties.wsdl" );

      String strImplClass = resource.getString( "javax.wsdl.factoryImpl" );

      Class clsFac = Class.forName( strImplClass );
      s_instance =  (WSDLFactory)clsFac.newInstance();
    }

    return s_instance;

  }

  public abstract Definition newWsdl();

  public abstract WSDLReader newReader();

  public abstract WSDLWriter newWriter();
  
} // end interface WSDLFactory{}

// *** End of WSDLFactory.java ***

