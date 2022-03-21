/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: XmlCloseElementEvent.java

============================================================================================
*/

package javax.xml.schema.util;

import org.xml.sax.Attributes;

import java.util.Stack;

/**
 * Class that holds a custom tag event which includes the name of the tag and any data or attributes
 */
public class XmlCloseElementEvent
{

  private String      m_strLocalName;     // Local Name of the tag
  private String      m_strQName;         // Qualified Name of the tag
  private String      m_strURI;
  private String      m_strTagAlias;
  private String      m_strTagData;       // Tag data or null

  private Attributes  m_tagAttr;          // Tag attributes if they exist

  private Object      m_objTagHandler;    // Object tag handler ( valid only on close tag events)

  private Stack       m_stackParentage;   // Stack that has tag parentage


  /**
   * Constructor
   *
   * @param strLocalName   The local name of the tag
   * @param strQName       The qualified name of the tag
   * @param strURI         The URI if the QName has prefix
   * @param objBean        The bean instance this xml tag property belongs to
   * @param strTagData     Tag data or null if no data
   * @param tagAttr        Tag attributs or null if no attributes
   */
  public XmlCloseElementEvent( String strLocalName, String strQName, String strURI, Object objBean, String strTagData, Attributes tagAttr, Stack stackParentage )
  {
    m_strLocalName = strLocalName;
    m_strQName = strQName;
    m_strURI = strURI;
    m_objTagHandler = objBean;
    m_strTagData = strTagData;
    m_tagAttr = tagAttr;
    m_stackParentage = stackParentage;

  } // end VwXmlCloseTagEvent()


  /**
   * Gets the name of the tag this event is for
   */
  public String getLocalName()
  { return m_strLocalName; }

 /**
  * Gets the qualified name
  * @return
  */
  public String getQName()
  { return m_strQName; }


  /**
   * gets the URI if qualified name is used
   * @return
   */
  public String getURI()
  { return m_strURI; }

  /**
   * Get the bean instance associated with this tag
   */
  public Object getTagHandler()
  { return m_objTagHandler; }


  /**
   * Gets the data for the tag this event is for - may be null
   */
  public String getTagData()
  { return m_strTagData; }


  /**
   * Gets the Attributes for this tag - may be null
   */
  public Attributes getTagAttr()
  { return m_tagAttr; }


  public Stack getTagParentage()
  { return m_stackParentage; }

  /**
   * Sets an alias for this tag
   * @param strTagAlias
   */
  public void setTagAlias( String strTagAlias )
  { m_strTagAlias = strTagAlias; }

  /**
   * Gets the event assigned tag alias. may be null
   * @return
   */
  public String getTagAlias()
  { return m_strTagAlias; }

} // end class VwXmlCloseTagEvent{}

// *** End of VwXmlCloseTagEvent.java ***
