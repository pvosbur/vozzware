/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: SchemaFactory.java

============================================================================================
*/
package javax.xml.schema.util;

import javax.xml.schema.Schema;
import java.util.ResourceBundle;

public abstract class SchemaFactory
{
  private static SchemaFactory s_instance = null;

  /**
   * Private constructor for singleton
   */
  protected SchemaFactory()
  { ;  }

  
  public synchronized static SchemaFactory getInstance() throws Exception
  {

    if ( s_instance == null )
    {
      ResourceBundle resource = ResourceBundle.getBundle( "javax.xml.schema.util.schema" );

      String strImplClass = resource.getString( "javax.schema.factoryImpl" );

      Class clsFac = Class.forName( strImplClass );
      s_instance =  (SchemaFactory)clsFac.newInstance();
    }

    return s_instance;

  }

  public abstract Schema newSchema();

  public abstract SchemaReader newReader();

  public abstract SchemaWriter newWriter();
  
} // end interface SchemaFactory{}

// *** End of SchemaFactory.java ***

