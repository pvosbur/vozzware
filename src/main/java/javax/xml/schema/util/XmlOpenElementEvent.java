/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: XmlOpenElementEvent.java

============================================================================================
*/

package javax.xml.schema.util;

import org.xml.sax.Attributes;

import java.util.Stack;

/**
 * This class
 */
public class XmlOpenElementEvent
{

  private String      m_strLocalName;     // Name of the tag
  private String      m_strQName;         // Name of the tag
  private String      m_strURI;
  private String      m_strTagAlias;

  private Attributes  m_tagAttr;          // Tag attributes if they exist

  private Class       m_clsTagHandler;    // Class that should handle this tag ( can be set on open tag events)

  private Stack       m_stackParentage;   // Stack that has tag parentage

  public XmlOpenElementEvent( String strLocalName, String strQName,String strURI, Attributes tagAttr, Stack stackParentage )
  {
    m_strLocalName = strLocalName;
    m_strQName = strQName;
    m_strURI = strURI;
    m_clsTagHandler = null;
    m_tagAttr = tagAttr;
    m_stackParentage = stackParentage;  

  }


  /**
   * Gets the local name of the tag this event is for
   */
  public String getLocalName()
  { return m_strLocalName; }


  /**
   * Gets the QName (qualified name) of the xml tag this event is fo. format prefix:localname
   * @return the QName (qualified name) of the xml tag this event is for format prefix:localname
   */
  public String getQName()
  { return m_strQName; }

  /**
   * Returns the URI assocated with this tag if a QName was used for this tag
   * @return the URI assocated with this tag if a QName was used for this tag
   */
  public String getURI()
  { return m_strURI; }

  /**
   * The the Java Class to be used to handle the tag data and attributes
   * @param clsTagHandler The Java class tag handler
   */
  public void setTagHandlerClass( Class clsTagHandler )
  { m_clsTagHandler = clsTagHandler; }


  /**
   * The cuurent assigne Java class tag handler or null if none assigned
   * @return
   */
  public Class getTagHandlerClass()
  { return m_clsTagHandler; }

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

  /**
   * Gets the Attributes for this tag - may be null
   */
  public Attributes getTagAttr()
  { return m_tagAttr; }

  /**
   * Returns a Stack containg the parentage of QNames
   * @return
   */
  public Stack getTagParentage()
  { return m_stackParentage; }

} // end class VwXmlOpenTagEvent{}

// *** End of VwXmlOpenTagEvent.java ***
