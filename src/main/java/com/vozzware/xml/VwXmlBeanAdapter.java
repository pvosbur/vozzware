/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwXmlBeanAdapter.java

Create Date: Apr 11, 2006
============================================================================================
*/

package com.vozzware.xml;

import org.xml.sax.Attributes;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * This adapter provides the mechanics to parse an Xml document into the derived class instance.
 */
public class VwXmlBeanAdapter implements VwXmlBean
{
  private HashMap           m_mapPropAttr = new HashMap();  // Attributes by property name

  private VwXmlToBean      m_xmlToBean;                    // Xml to bean conversion utility

  private static Object     s_mapSync = new Object();       // Singleton

  private boolean           m_fPreserveCase = false;

  private Map               m_mapMethosAlias = null;


  /**
   * Zero arg constructor
   */
  public VwXmlBeanAdapter()
  { ; }

  /**
   * Constructor to load the bean with data from an XML document as a file
   *
   * @param filePath The file path and name of the XNL document to load
   *
   * @exception IOExcepton if the path is invalid, Exception if the xml is invalid
   */
  public VwXmlBeanAdapter( File filePath, File fileSchema ) throws Exception
  {
    load( filePath, fileSchema );

  } // end VwXmlBeanAdapter



  /**
   * Constructor to load the bean with data from an XML document contained in a String
   *
   * @param strXML An XML document in String format
   *
   * @exception Exception if the xml is invalid
   */
  public VwXmlBeanAdapter( String strXML, File fileSchema ) throws Exception
  {
    load( strXML, fileSchema );
  } // end VwXmlBeanAdapter


  /**
   * Sets an VwAttribute list for a bean property
   */
  public void setAttributes( String strPropName, Attributes attrs )
  {
     Object obj = m_mapPropAttr.get( strPropName );

     if ( obj instanceof List )
       ((List)obj).add( attrs );
     else
     if ( obj instanceof Attributes )
     {
       LinkedList list = new LinkedList();
       list.add( obj );
       list.add( attrs );
       m_mapPropAttr.put( strPropName, list );
     }
     else
       m_mapPropAttr.put( strPropName, attrs );

  } // end setAttributes()


  /**
   * Retrieves the Attributes object or List of Attributes objects for the property specified.
   * <br>null is returned if there are no attributes defined.
   * <br>If the property is a collection, then a List of
   * <br>Attributes objects is returned in the same order that the tags were defined else
   * <br>just the Attributes object is returned.
   */
  public Object getAttributes( String strPropName )
  { return m_mapPropAttr.get( strPropName ); }


  /**
   * Load this bean from the xml document
   *
   * @param strXml The XML document to load the bean property data from
   */
  public void load( String strXml, File fileSchema ) throws Exception
  {
    m_xmlToBean = new VwXmlToBean( this.getClass(), false, fileSchema.toURL() );

    m_xmlToBean.parse( strXml, this );

  } // end makeBean


  /**
   * Load this bean from the xml document
   *
   * @param strXml The XML document to load the bean property data from
   */
  public void load( String strXml, URL urlSchema ) throws Exception
  {
    m_xmlToBean = new VwXmlToBean( this.getClass(), false, urlSchema );

    m_xmlToBean.parse( strXml, this );

  } // end makeBean


  /**
   * Load this bean from the xml document
   *
   * @param filePath The XML document to load the bean property data from
   */
  public void load( File filePath, File fileSchema ) throws Exception
  {
    m_xmlToBean = new VwXmlToBean( this.getClass(), false, fileSchema.toURL() );

    m_xmlToBean.parse( filePath, this );

  } // end makeBean



} // end class VwXmlBeanAdapter{}

// *** End of VwXmlBeanAdapter.java ***
