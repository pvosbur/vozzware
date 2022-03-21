/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: WSDLCommon.java
============================================================================================
*/
package javax.wsdl;

import com.vozzware.xml.namespace.Namespace;
import com.vozzware.xml.namespace.QName;

import javax.xml.schema.Attribute;
import javax.xml.schema.Documentation;
import java.io.Serializable;
import java.util.List;

/**
 * This interface represents the common elements of a WSDL document
 */
public interface WSDLCommon extends Serializable
{
  /**
   * Adds a namespace to this xml component
   * @param nameSpace The namespace to add
   */
  public void addNamespace( Namespace nameSpace );


  /**
   * Removes a namespace from an xml component
   * @param nameSpace The namespace to remove
   */
  public void removeNamespace( Namespace nameSpace );


  /**
   * Removes a namespace from the xml component by it's prefix value
   * @param strPrefix The prefix of the namespace to remove
   */
  public void removeNamespace( String strPrefix );


  /**
   * Removes all namespaces from this element
   */
  public void removeAllNamespaces();

  /**
   * Gets the Namespace object associated with this namespace URI. Or null if
   * there are no namespaces associated with this namespace URI.
   *
   * @return The Namespace object
   */
  public Namespace getNamespace( String strNamespaceURI );

  /**
   * Gets  list of Namespce instances
   * @return a List of Namespace objects
   */
  public List getNamespaces();


  /**
   * Gets the qualified name for this scheama component
   * @return The qualified name for this scheama component (may be null)
   */
  public QName getQName();


  /**
   * Sets the qualified name for this scheama component
   * @param qname The qualified name
   */
  public void setQName( QName qname );

  /**
   * Returns the user assigned NCNAME attribute (i.e., &lt;xsd:element name='someName' /&gt;
   * @return the user assigned NCNAME attribute
   */
  public String getName();


  /**
   * Sets the user assigned NCNAME attribute for this xml component
   * @param strName
   */
  public void setName( String strName );


  /**
   * Gets the ocumenation object for this WSDL element
   * @return the ocumenation object for this WSDL element (may be null)
   */
  public Documentation getDocumentation();

  /**
   * Sets the Documenation object for this WSDL element
   * @param doc The Annotation for this WSDL component
   */
  public void setDocumentation( Documentation doc );

  /**
   * Sets a user define attribute for a given WSDL element
   * @param attr
   */
  public void setUserAttribute( Attribute attr );

  /**
   * Gets a user defined Attrinute
   * @param strName The name of the user defined Attribute to get
   * @return
   */
  public Attribute getUserAttribute( String strName );
  
} // end WSDLCommon{}

// *** End of WSDLCommon.java ***
  