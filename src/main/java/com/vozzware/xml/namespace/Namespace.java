/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: Namespace.java

Create Date: Apr 11, 2006
============================================================================================
*/
package com.vozzware.xml.namespace;

/**
 * This class holds an XML namespace definition which consists of a prefix and URI component
 */
public class Namespace
{
  private String  m_strPrefix;
  private String  m_strURI;

  /**
   * Constructor
   * @param strPrefix The namespace prefix
   * @param strURI  The namespace URI
   */
  public Namespace( String strPrefix, String strURI )
  {
    m_strPrefix = strPrefix;
    m_strURI = strURI;

    if ( m_strPrefix == null )
      m_strPrefix = "";

  } // end Namespace()


  /**
   * Gets the namespace prefix
   * @return the namespace prefix
   */
  public String getPrefix()
  {  return m_strPrefix; }


  /**
   * Gets the URI portion of the namespace
   * @return the URI portion of the namespace
   */
  public String getURI()
  {  return m_strURI; }

  
  /**
   * Returns a formmated string in the form xmlns="prefix:URI"
   * @return
   */
  public String toString()
  {
    String strNamespace = "xmlns=\"";

    if ( m_strPrefix.length() > 0 )
      strNamespace += m_strPrefix + ":";

    return strNamespace + m_strURI + "\"";

  } // end toString()

 } // end class Namespace{}

// *** End of Namespace.java ***
