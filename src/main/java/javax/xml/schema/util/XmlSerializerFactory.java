/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: XmlSerializer.java

============================================================================================
*/
package javax.xml.schema.util;

/**
 * 
 * Factory class to create an XmlSerializer object
 * 
 * @author P.VosBurgh
 *
 */
public class XmlSerializerFactory
{
  /**
   * Creates the XmlSerializer from the name of the implementing class defined the schema.properties file
   * @return an XmlSerializer object
   */
  public static XmlSerializer getSerializer() throws Exception
  {
    return null;
    
  }

  public static XmlSerializer getSerializer( String strClassName ) throws Exception
  {  return (XmlSerializer)Class.forName( strClassName ).newInstance(); }
  
} // end class XmlSerializerFactory{}

// *** End of XmlSerializerFactory.java ***

