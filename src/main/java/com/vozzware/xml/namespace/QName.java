/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: QName.java

Create Date: Apr 11, 2006
============================================================================================
*/
package com.vozzware.xml.namespace;

import java.io.Serializable;

public class QName implements Serializable
{

  private String  m_strURI;
  private String  m_strLocalPart;
  private String  m_strPrefix;

  public QName( String strPrefix, String strURI, String strLocalPart )
  {
    m_strURI = strURI;
    m_strLocalPart = strLocalPart;
    m_strPrefix = strPrefix;

  } // end QNameOld()

  
  public QName( Namespace nameSpace,  String strLocalPart )
  {
    m_strURI = nameSpace.getURI();
    m_strPrefix = nameSpace.getPrefix();
    m_strLocalPart = strLocalPart;

  } // end QNameOld()
  
  /**
   * Constructs a QName from a namespaceURI and local name
   * @param strURI The URI that reprsents this QName
   * @param strLocalPart The local name
   */
  public QName( String strURI, String strLocalPart )
  {
    m_strURI = strURI;
    m_strLocalPart = strLocalPart;
    m_strPrefix = "";

  } // end QName()

  /**
   * Constructs QName with just a local part
   * @param strLocalPart The local name
   */
  public QName( String strLocalPart )
  {
    m_strURI = "";
    m_strLocalPart = strLocalPart;
    m_strPrefix = "";

  } // end QName()

  /**
   * Returns the namespceURI part of the QName
   * @return The namespceURI part of the QName
   */
  public String getNamespaceURI()
  { return m_strURI; }


  /**
   * Returns the local part of the QName
   * @return The local part of the QName
   */
  public String getLocalPart()
  { return m_strLocalPart; }


  /**
   * Returns the namespace prefix used with this xml tag
   * @return The prefix for this xml tag
   */
  public String getPrefix()
  { return m_strPrefix; }

  /**
   * Compares two QNames for equalitiy (i.e., the namespaceURI and localparts are equal)
   * @param qname The other QName object
   * @return  true if the namespaceURI and localpart names are equal
   */
  public boolean equals( QName qname )
  { return ( m_strURI.equals( qname.m_strURI ) && m_strLocalPart.equals(  qname.m_strLocalPart ) ); }


  /**
   * Returns the string representation on the elemment using the format prefix:localpart. If there
   * is no prefix portion, then just the localpart name is returned
   *
   * @return The string representation on the elemment using the format prefix:localpart
   */
  public String toElementName()
  {
    if ( m_strPrefix != null && m_strPrefix.length() > 0 )
      return m_strPrefix + ":" + m_strLocalPart;

    return m_strLocalPart;

  } // end toElementName()

  /**
   * Returns String representation of this QName in the format {namespaceURI}localPart
   * @return
   */
  public String toString()
  {
    return ((m_strURI.length() == 0)
            ? m_strLocalPart
            : '{' + m_strURI + '}' + m_strLocalPart);

  } // end toString()
} // end class QName{}

//*** End of QName.java ***