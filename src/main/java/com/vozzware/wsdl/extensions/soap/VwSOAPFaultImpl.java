/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwSOAPFaultImpl.java

Create Date: Apr 11, 2006
============================================================================================
*/
package com.vozzware.wsdl.extensions.soap;

import com.vozzware.util.VwDelimString;
import com.vozzware.wsdl.extensions.VwExtensibilityElementImpl;

import javax.wsdl.extensions.soap.SOAPFault;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


/**
 * Implementation class for the SOAPFault interface
 *
 * @author Peter VosBurgh
 */
public class VwSOAPFaultImpl extends VwExtensibilityElementImpl implements SOAPFault
{
  private String m_strName;
  private String m_strUse;
  private String m_strEncodingStyles;
  private String m_strNamespaceURI;

  public boolean isParent()
  { return false; }
  
  /**
   * Sets the name for this SOAP fault.
   *
   * @param strName the name of the SOAP fault
   */
  public void setName( String strName )
  { m_strName= strName; }


  /**
   * Gets the name of this SOAP fault.
   *
   * @return the name of this SOAP fault.
   */
  public String getName()
  { return m_strName; }


  /**
   * Sets the use attribute for this SOAP fault element.
   *
   * @param strUse the use attribute for this SOAP fault element.
   */
  public void setUse( String strUse )
  { m_strUse = strUse; }

  /**
   * Gets the use attribute for this SOAP fault element.
   *
   * @return the use attribute for this SOAP fault element.
   */
  public String getUse()
  { return m_strUse; }

  /**
   * Sets the encodingStyle attribute for this SOAP fault. Each style is delimited by a space
   *
   * @param strEncodingStyles the desired encodingStyles
   */
  public void setEncodingStyle( String strEncodingStyles )
  { m_strEncodingStyles = strEncodingStyles; }

  /**
   * Gets the encodingStyles for this SOAP fault.
   */
  public String getEncodingStyle()
  { return m_strEncodingStyles; }

  /**
   * Helper method to render encodingStyle attribute space delimited string as List of Strings
   * @return the encoding styles as List of Strings
   */
  public List getEncodingStyles()
  {
    List listStyles = new LinkedList();

    if ( m_strEncodingStyles == null )
      return listStyles;

    VwDelimString dlmsStyles = new VwDelimString( " ", m_strEncodingStyles );

    return dlmsStyles.toStringList();

  } // end getEncodingStyles()

  /**
   * Helper method to convert a List of encoding style strings to a space delimited string required
   * for attribute value serialization
   *
   * @param listEncodingStyles
   */
  public void setEncodingStyles( List listEncodingStyles )
  {
    StringBuffer sb = new StringBuffer();

    for ( Iterator iStyles = listEncodingStyles.iterator(); iStyles.hasNext(); )
    {
      if ( sb.length() > 0 )
       sb.append( " " );

      sb.append( (String)iStyles.next() );

    } // end for()

    m_strEncodingStyles = sb.toString();

  } // end setEncodingStyles()

  /**
   * Sets the namespace URI attribute value for this SOAP fault.
   *
   * @param strNamespaceURI he namespace URI attribute value
   */
  public void setNamespace( String strNamespaceURI )
  { m_strNamespaceURI = strNamespaceURI; }

  /**
   * Get the namespace URI attribute for this SOAP fault.
   *
   * @return the namespace URI attribute
   */
  public String getNamespace()
  { return m_strNamespaceURI; }

} // end class VwSOAPfaultImpl{}

// *** End of VwSOAPfaultImpl.java ***
