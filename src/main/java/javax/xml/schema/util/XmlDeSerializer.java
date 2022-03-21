/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: XmlDeSerializer.java

============================================================================================
*/
package javax.xml.schema.util;


import org.xml.sax.InputSource;

import java.net.URL;

/**
 * @author Administrator
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public interface XmlDeSerializer extends XmlFeatures
{
 
  /**
   * Sets the XML element name thats associated with the top level bean name if the element name is different
   * than the class name of the toplevel bean
   * 
   * @param strTopLevelElementName The element name that will represent the top levelbean name
   */
  public void setTopLevelElementName( String strTopLevelElementName );
  
  
  /**
   * Deserialize an XML document to the Java object specidied in the top level class parameter
   * 
   * @param inps The input source of the XML document 
   * @param clsTopLevel The top level class type of the object to be deserialized in to
   * @param urlSchema An XML schema/DTD to use for deSerialization help (May be null )
   * 
   * @return an object of the top level class type containing the deSerialized XML document
   */
  public Object deSerialize( InputSource inps, Class clsTopLevel, URL urlSchema ) throws Exception;
  
  
  /**
   * Registers a listener for an XML close element event
   * 
   * @param strElementName The name of the XML element to listen for
   * @param strURI The associated URI for this element (may be null if N/A)
   * @param iOpenTagListener The implementing listener class
   * 
   * @throws Exception
   */
  public void setCloseElementListener( String strElementName, String strURI, XmlCloseElementListener iCloseTagListener ) throws Exception;
  
  /**
   * Registers an open element listener. Only one listener is allowed per element name.
   *
   * @param strElementName The name of the XML element to listen for
   * @param strURI The associated URI for this element (may be null if N/A)
   * @param iOpenTagListener The implementing listener class
   *
   * @exception Exception if the element to register already has been registered
   *
   */
  public void setOpenElementListener( String strElementName, String strURI, XmlOpenElementListener iOpenTagListener ) throws Exception;
  
  
  /**
   * Sets a Java class type to handel deserialazation
   * 
   * @param strElementName The xml element name
   * @param clsHandler
   */
  public void setElementHandler( String strElementName, Class clsHandler );
  
  /**
   * Assocaite a Class name that is different from the xml tag name
   * @param strXmlElementName
   * @param strURI The URI associated with this xml element
   * @param clsHandler The class to handle this element
   */
  public void setElementHandler( String strXmlTagName, String strURI, Class clsHandler );

  /**
   * 
   * @param unknowElementHandler
   */
  public void setUnknownElementHanlder( UnknownElementHandler unknowElementHandler );
  																			
  /**
   * Forces the use of a difeerent setter method to be invoked for the name of the element specified. This is
   * usefull for using a common setter method on a super class type
   * 
   * @param strLocalName The local name of the xml element
   * @param strURI The uri namespace of the element (may be null)
   * @param strSetterAliasName The setter method name to use
   */
  public void addObjectSetterAlias( String strLocalName, String strURI, String strSetterAliasName );
  
  /**
   * If true (the default) any tag or attribute data value that has the form ${propertyname} will automaticially
   * be resolved ( if avaliable ) else the original string data will be returned
   * @param fExpandMacros
   */
  public void setExpandMacros( boolean fExpandMacros );
  
  
}