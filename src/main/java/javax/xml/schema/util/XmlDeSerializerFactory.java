/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: XmlDeSerializerFactory.java

============================================================================================
*/
package javax.xml.schema.util;

/**
 * Factory class to create an XmlDeSerializer object

 * @author P.VosBurgh
 *
 */
public class XmlDeSerializerFactory
{
  /**
   * Creates the XmlDeSerializer from the name of the implementing class defined the schema.properties file
   * @return an XmlDeSerializer object
   */
  public static XmlDeSerializer getDeSerializer() throws Exception
  {
    return null;
    
  }

  public static XmlDeSerializer getDeSerializer( String strClassName ) throws Exception
  {  return (XmlDeSerializer)Class.forName( strClassName ).newInstance(); }
  
}
