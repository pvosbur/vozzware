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

import java.io.File;
import java.net.URL;
import java.util.List;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface XmlSerializer extends XmlFeatures
{
  
  /**
   * Serialize a List of beans to an XML document
   * 
   * @param strDocRoot The root element of the document or null to use the default
   * @param listBeans The list of toplevel beans to serialize
   * 
   * @return A String containing the XML document
   */
  public String serialize( String strDocRoot, List listBeans ) throws Exception;
  
  
  /**
   * Serialize a List of beans to an XML document and write the document to the file specified
   * @param strDocRoot The root element of the document or null to use the default
   * @param listBeans The list of toplevel beans to serialize
   * @param fileXML The File object the xml document will be written to
   * @throws Exception if any io errors occur
   */
  public void serialize( String strDocRoot, List listBeans, File fileXML ) throws Exception;

  /**
   * Serialize the object specified to an XNL document
   * 
   * @param strDocRoot The root element of the document. If omitted, the bean name( or alias) is the document root
   * @param objBean The object to serialize
   * 
   * @return A String containing the XML document
   */
  public String serialize( String strDocRoot, Object objBean ) throws Exception;
  
  
  /**
   * Serialize the object specified to an XML document and writes the document to the file specified
   * @param strDocRoot The root element of the document or null to use the default
   * @param objBean The object to serialize
   * @param fileXML The File object the xml document will be written to
   * @throws Exception if any io errors occur
   */
  public void serialize( String strDocRoot, Object objBean, File fileXML  ) throws Exception;  
  
  
  /**
   * Adds a schema/dtd for serialazation 
   *
   * @param urlSchema The URL to the schema/dtd
   */
  public void addSchema(  URL urlSchema, Package pkg  ) throws Exception;
  
  
  /**
   * Sets the XML element name to generate when an object of the class type is serialized
   * 
   * @param clsObject The class of the object being serialized
   * @param strElementame The element name to genertae
   */
  public void setObjectElementName( Class clsObject, String strElementame );
  
  /**
   * Used when the seriaization m_btModel is the ATTRIBUTE_MODEL. By default, only all methods that represent attributes
   * as defined in the schem/dtd are serialized. This method adds additonal class methods to serialize theri content
   * 
   * @param clsObject The class of the object to serialize
   * @param strMethodNames a comma separated list of method name to include in the output
   * 
   * @throws Exception
   */
  public void setContentMethods( Class clsObject, String strMethodNames ) throws Exception;
  
  /**
   * Sets the xml tag data string that will be generated for boolean properties that return a value of true
   *
   * @param strTrueValue The value that will be generated for true boolean properties
   */
  public void setTrueBooleanTransform( String strTrueValue );


  /**
   * Gets current string transform value for a true boolean state
   *
   */
  public String getTrueBooleanTransform();


  /**
   * Sets the xml tag data string that will be generated for boolean properties that return a value of false
   *
   * @param strFalseValue The value that will be generated for false boolean properties
   */
  public void setFalseBooleanTransform( String strFalseValue );

  /**
   * Gets current string transform value for a false boolean state
   *
   */
  public String getFalseBooleanTransform();
  
  
  /**
   * Sets the formatted output feature which is off by default. Formatted output adds CRLF pairs as well as indentaion
   * of child elements.
   * 
   * @param fFormatOutput true to turn on formatting, false to turn off
   * 
   * @param nStartingIndentLevel The starting indentaion level. each level nbr adds two spaces of indentaion
   */
  public void setFormattedOutput( boolean fFormatOutput, int nStartingIndentLevel );
  
  
  /**
   * The default value to use for null menthod values. The default behaviour is to not output xml for properties
   * that return null.
   * 
   * @param strDefaultForNulls The value to output for properties that return null during serialization
   */
  public void setDefaultForNulls( String strDefaultForNulls );
  
  
  
} // end interface XmlSerializer{}

// end of XmlSerializer.java ***

