/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwSchemaCommonImpl.java

Create Date: Apr 11, 2006
============================================================================================
*/
package com.vozzware.xml.schema;

import com.vozzware.xml.namespace.Namespace;
import com.vozzware.xml.namespace.QName;

import javax.xml.schema.Annotation;
import javax.xml.schema.Attribute;
import javax.xml.schema.Schema;
import javax.xml.schema.SchemaCommon;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class VwSchemaCommonImpl implements SchemaCommon
{
  private QName m_qName;
  private String m_strName;
  private String m_strId;
  private Annotation m_annotation;

  protected Schema m_schema;
  
  
  private List m_listNamespaces = new LinkedList();
  private List m_listUserAttrs = new LinkedList();

  private Map  m_mapUserAttrs = new HashMap();
  /**
   * Adds a namespace to this xml component
   * @param nameSpace The namespace to add
   */
  public void addNamespace( Namespace nameSpace )
  { m_listNamespaces.add( nameSpace ); }


  /**
   * Removes a namespace from an xml component
   * @param nameSpace The namespace to remove
   */
  public void removeNamespace( Namespace nameSpace )
  { m_listNamespaces.remove( nameSpace ); }


  /**
   * Removes a namespace from the xml component by it's prefix value
   * @param strPrefix The prefix of the namespace to remove
   */
  public void removeNamespace( String strPrefix )
  {
    for ( Iterator iNamespaces = m_listNamespaces.iterator(); iNamespaces.hasNext(); )
    {
      Namespace ns = (Namespace)iNamespaces.next();
      if ( ns.getPrefix().equals( strPrefix) )
      {
        iNamespaces.remove();
        return;
      }
    } // end for()

  } // end removeNamespace()

  /**
   * Removes all namespaces from the xml component 
   */
  public void removeAllNamespaces()
  { m_listNamespaces.clear(); }

  /**
   * Gets  list of Namespce instances
   * @return a List of Namespace objects
   */
  public List getNamespaces()
  { return m_listNamespaces; }


  /**
   * Returns the Namespace associated with the URI if the URI exists else null is returned
   * @param strUri The URI to serach in the namespace list for
   * 
   * @return the Namespace associated with the URI if the URI exists else null is returned
   */
  public Namespace getNamespace( String strUri )
  {
    for ( Iterator iNamespaces = m_listNamespaces.iterator(); iNamespaces.hasNext(); )
    {
      Namespace ns = (Namespace)iNamespaces.next();
      
      if ( ns.getURI().equals( strUri ))
        return ns;
      
    } // end ofr()
    
    return null;	// not found
    
  }// end getNamspace()

  
  /**
   * Gets the Namespace object by prefix
   * 
   * @param strPrefix The prefix to retrieve the namespace for
   * @return The namespace associated with the prefix or null if it does not exist
   */
  public Namespace getNamespaceByPrefix( String strPrefix )
  {
    for ( Iterator iNamespaces = m_listNamespaces.iterator(); iNamespaces.hasNext(); )
    {
      Namespace ns = (Namespace)iNamespaces.next();
      
      if ( ns.getPrefix().equals( strPrefix ))
        return ns;
      
    } // end for()
    
    return null;	// not found
    
  }
  /**
   * Returns the namespace prefix associated the the URI
   * 
   * @param strUri The URI to search
   * @return The namespace prefix if the URI was defined for this element type else null is retunred
   */
  public String getPrefix( String strUri )
  {
    Namespace ns = getNamespace( strUri );
    
    if ( ns != null )
      return ns.getPrefix();
    
    return null;
    
  } // end  getPrefix()
  
  /**
   * Gets the qualified name for this scheama component
   *
   * @return The qualified name for this scheama component (may be null)
   */
  public QName getQName()
  { return m_qName; }

  /**
   * Sets the qualified name for this scheama component
   *
   * @param qName The qualified name
   */
  public void setQName( QName qName )
  { m_qName = qName; }

  /**
   * Gets the schema object this element belongs to
   * @return the schema object this element belongs to
   */
 
  public Schema getSchema()
  { return m_schema; }
  
  /**
   * Returns the user assigned NCNAME attribute (i.e., &lt;xsd:element name='someName' /&gt;
   *
   * @return the user assigned NCNAME attribute
   */
  public String getName()
  { return m_strName; }

  /**
   * Sets the user assigned NCNAME attribute for this xml component
   *
   * @param strName
   */
  public void setName( String strName )
  { m_strName = strName; }

  /**
   * Gets the ID for this schema component
   *
   * @return The ID for this schema component (may be null)
   */
  public String getId()
  { return m_strId; }

  /**
   * Sets the ID for this schema component
   *
   * @param strId The unique ID for this xml component
   */
  public void setId( String strId )
  { m_strId = strId; }

  /**
   * Gets the Annotation for this schema component
   *
   * @return The Annotation for this schema component (may be null)
   */
  public Annotation getAnnotation()
  { return m_annotation; }

  /**
   * Sets the Annotation for this schema component
   *
   * @param annotation The Annotation for this schema component
   */
  public void setAnnotation( Annotation annotation )
  { m_annotation = annotation; }


  /**
   * Sets a user define attribute for a given schema element
   * @param attr
   */
  public void addUserAttribute( Attribute attr )
  {
    if ( !m_mapUserAttrs.containsKey( attr.getName() ) )
    {
      m_mapUserAttrs.put( attr.getName(), attr );
      m_listUserAttrs.add( attr );
    }
  }


  /**
   * Removes a user defined attribute for a given schema element
   * @param strAttrName The name of the attribute to remove
   */
  public void removeUserAttribute( String strAttrName )
  {
    Object objAttr = m_mapUserAttrs.get( strAttrName );

    if ( objAttr != null )
    {
      m_mapUserAttrs.remove( strAttrName );
      m_listUserAttrs.remove( objAttr );
      
    }
  }

  /**
   * Gets a user defined Attrinute
   * @param strName The name of the user defined Attribute to get
   * @return
   */
  public Attribute getUserAttribute( String strName )
  { return (Attribute)m_mapUserAttrs.get(  strName ); }
  
  /**
   * Gets a List of All user defined attributes not specified in the schema
   * @return a List of All user defined attributes not specified in the schema
   */
  public List getUserAttributes()
  { return m_listUserAttrs; }

} // end class VwSchemaCommonImpl{}

// *** End of VwSchemaCommonImpl.java ***
  