/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwTypesImpl.java

Create Date: Apr 11, 2006
============================================================================================
*/
package com.vozzware.wsdl;

import com.vozzware.wsdl.extensions.VwExtensibilityElementSupportImpl;

import javax.wsdl.Types;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.UnknownExtensibilityElement;
import javax.xml.schema.Schema;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * This interface represents Types section of a WSDL document.
 *
 * @author Peter VosBurgh
 */
public class VwTypesImpl extends VwWSDLCommonImpl implements Types
{
  private List    m_listExtElements = new LinkedList();

  private List    m_listSchemas = new ArrayList();
  
  private VwExtensibilityElementSupportImpl m_extSupport = new VwExtensibilityElementSupportImpl();

  /**
   * Sets the base Schema object if the type is defined as an XML Schema
   *
   * @param schema The base Schema object (defined in jschema)
   */
  public void addSchema( Schema schema )
  { m_listSchemas.add( schema ); }


  public int getSchemaCount()
  { return m_listSchemas.size(); }

  /**
   * Gets the base Schema object if the type is defined as an XML Schema
   * @return the base Schema object if the type is an XML Schema or null if a different type
   */
  public Schema getSchema()
  { return (Schema)m_listSchemas.get( 0 ); }

  
  /**
   * Gets the base Schema object if the type is defined as an XML Schema
   * @return the base Schema object if the type is an XML Schema or null if a different type
   */
  public List  getSchemas()
  { return m_listSchemas; }
  
  /**
   * Returns true if the type defined is an XML Schema
   *
   * @return true if the type defined is an XML Schema, false otherwise
   */
  public boolean isSchema()
  { return m_listSchemas.get( 0 ) != null; }

  /**
   * Adds an extensibility element for specific service extensions
   *
   * @param extElement extensibility element for specific service extensions
   */
  public void addExtensibilityElement( ExtensibilityElement extElement )
  {  m_extSupport.addExtensibilityElement( extElement ); }

  /**
   * Removes the specified extensibility element
   *
   * @param extElement the extensibility element to remove
   */
  public void removeExtensibilityElement( ExtensibilityElement extElement )
  { m_extSupport.removeExtensibilityElement( extElement ); }

  /**
   * Removes all extensibility elements
   */
  public void removeAllExtensibilityElements()
  { m_extSupport.removeAllExtensibilityElements(); }


  /**
   * Gets all the extensibility elements defined.
   *
   * @return a List of all extensibility elements defined for this service
   */
  public List getExtensibilityElements()
  { return m_extSupport.getExtensibilityElements(); }


  /**
   * Adds an unknown extensibility element for specific service extensions
   *
   * @param unknownExtElementt the unknown extensibility element for specific service extensions
   */
  public void addUnknownExtensibilityElement( UnknownExtensibilityElement unknownExtElement )
  {  m_extSupport.addUnknownExtensibilityElement( unknownExtElement ); }

  /**
   * Removes the specified unknown extensibility element
   *
   * @param unknownExtElement the unknown extensibility element to remove
   */
  public void removeUnknownExtensibilityElement( UnknownExtensibilityElement unknownExtElement )
  { m_extSupport.removeUnknownExtensibilityElement( unknownExtElement ); }

  /**
   * Removes all unknown extensibility elements
   */
  public void removeAllUnknownExtensibilityElements()
  { m_extSupport.removeAllUnknownExtensibilityElements(); }


  /**
   * Gets all the unknown extensibility elements defined.
   *
   * @return a List of all unknown extensibility elements defined for this service
   */
  public List getUnknownExtensibilityElements()
  { return m_extSupport.getUnknownExtensibilityElements(); }

  /**
   * Removes all ExtensibilityElements and UnknownExtensibilityElements
   *
   */
  public List getAllElements()
  { return m_extSupport.getAllElements(); }
  
  
  /**
   * Gets the List of all ExtensibilityElements and UnknownExtensibilityElements
   * 
   * @return  the List of all ExtensibilityElements and UnknownExtensibilityElements
   */
  public void removeAllElements()
  { m_extSupport.removeAllElements(); }
  
  
  /**
   * Gets a List of all Types content
   *
   * @return a List of all Types content
   */
  public List getContent()
  {
    List listContent = new LinkedList();

    if ( this.getDocumentation() != null )
     listContent.add( this.getDocumentation() );

    //if ( m_schema != null )
      listContent.addAll( m_listSchemas );

    listContent.addAll( getAllElements() );

    return listContent;

  } // end getContent()


} // end class VwTypesImpl{}

// *** End of VwTypesImpl.java ***