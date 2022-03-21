/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwWSDLCommonImpl.java

Create Date: Apr 11, 2006
============================================================================================
*/
package com.vozzware.wsdl;

import com.vozzware.xml.namespace.Namespace;
import com.vozzware.xml.namespace.QName;

import javax.wsdl.WSDLCommon;
import javax.xml.schema.Attribute;
import javax.xml.schema.Documentation;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Impl class for the WSDLCommon interface
 */
public class VwWSDLCommonImpl implements WSDLCommon
{
  private QName         m_qName;
  private String        m_strName;
  private String        m_strId;
  private Documentation m_doc;

  private List m_listNamespaces = new LinkedList();

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
   * Removes all namespaces from this element
   */
  public void removeAllNamespaces()
  { m_listNamespaces.clear(); }


  /**
   * Gets the Namespace object associated with this namespace URI. Or null if
   * there are no namespaces associated with this namespace URI.
   *
   * @return The Namespace object
   */
  public Namespace getNamespace( String strNamespaceURI )
  {
    for ( Iterator iNamespaces = m_listNamespaces.iterator(); iNamespaces.hasNext(); )
    {
      Namespace ns = (Namespace)iNamespaces.next();
      if ( ns.getURI().equals( strNamespaceURI) )
      {
        return ns;
      }
    } // end for()

    return null;    // None found

  } // end getNameSpace()


  /**
   * Gets  list of Namespce instances
   * @return a List of Namespace objects
   */
  public List getNamespaces()
  { return m_listNamespaces; }


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
   * Gets the Documentation for this WSDL component
   *
   * @return The Documentationfor this WSDL component (may be null)
   */
  public Documentation getDocumentation()
  { return m_doc; }

  /**
   * Sets the Documentation for this schema component
   *
   * @param doc The Documentation for this schema component
   */
  public void setDocumentation( Documentation doc )
  { m_doc = doc; }


  /**
   * Sets a user define attribute for a given schema element
   * @param attr
   */
  public void setUserAttribute( Attribute attr )
  { m_mapUserAttrs.put( attr.getName(), attr ); }

  /**
   * Gets a user defined Attrinute
   * @param strName The name of the user defined Attribute to get
   * @return
   */
  public Attribute getUserAttribute( String strName )
  { return (Attribute)m_mapUserAttrs.get(  strName ); }
  

} // end class VwWSDLCommonImpl{}

// *** End of VwWSDLCommonImpl.java ***
  