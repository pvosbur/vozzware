/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: Schema.java

============================================================================================
*/
package javax.xml.schema;

import com.vozzware.xml.namespace.Namespace;
import com.vozzware.xml.namespace.QName;

import java.util.List;

/**
 * This interface represents the common elements of the xml schema components
 */
public interface SchemaCommon extends java.io.Serializable
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
   * Removes all namespaces from the xml component 
   */
  public void removeAllNamespaces();

  /**
   * Gets  list of Namespce instances
   * @return a List of Namespace objects
   */
  public List getNamespaces();
  
  /**
   * Returns the Namespace associated with the URI if the URI exists else null is returned
   * @param strUri The URI to serach in the namespace list for
   * 
   * @return the Namespace associated with the URI if the URI exists else null is returned
   */
  public Namespace getNamespace( String strUri );
  
  
  /**
   * Gets the Namespace object by prefix
   * 
   * @param strPrefix The prefix to retrieve the namespace for
   * @return The namespace associated with the prefix or null if it does not exist
   */
  public Namespace getNamespaceByPrefix( String strPrefix );
  
  /**
   * Returns the namespace prefix associated the the URI
   * 
   * @param strUri The URI to search
   * @return The namespace prefix if the URI was defined for this element type else null is retunred
   */
  public String getPrefix( String strUri );
  

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
   * Gets the schema object this element belongs to
   * @return the schema object this element belongs to
   */
  public Schema getSchema();
  
  
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
   * Gets the ID for this schema component
   * @return The ID for this schema component (may be null)
   */
  public String getId();

  /**
   * Sets the ID for this schema component
   * @param strID The unique ID for this xml component
   */
  public void setId( String strID );

  /**
   * Gets the Annotation for this schema component
   * @return The Annotation for this schema component (may be null)
   */
  public Annotation getAnnotation();

  /**
   * Sets the Annotation for this schema component
   * @param annotation The Annotation for this schema component
   */
  public void setAnnotation( Annotation annotation );

  /**
   * Adds a user defined attribute for a given schema element
   * @param attr
   */
  public void addUserAttribute( Attribute attr );

  /**
   * Removes a user defined attribute for a given schema element
   * @param strAttrName The name of the attribute to remove
   */
  public void removeUserAttribute( String strAttrName );

  /**
   * Gets a user defined Attrinute
   * @param strName The name of the user defined Attribute to get
   * @return
   */
  public Attribute getUserAttribute( String strName );


  /**
   * Gets a List of All user defined attributes not specified in the schema
   * @return a List of All user defined attributes not specified in the schema
   */
  public List getUserAttributes();

} // end SchemaCommon{}

// *** End of SchemaCommon.java ***
  