/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwSOAPBodyImpl.java

Create Date: Apr 11, 2006
============================================================================================
*/
package com.vozzware.wsdl.extensions.soap;

import com.vozzware.util.VwDelimString;
import com.vozzware.wsdl.extensions.VwExtensibilityElementImpl;

import javax.wsdl.extensions.soap.SOAPBody;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


/**
 * Implementation class for the SOAPBody interface
 *
 * @author Peter VosBurgh
 */
public class VwSOAPBodyImpl extends VwExtensibilityElementImpl implements SOAPBody
{
  private String m_strParts;
  private String m_strUse;
  private String m_strEncodingStyles;
  private String m_strNamespaceURI;

  public boolean isParent()
  { return false; }
  
  /**
   * Sets the parts attribute for this SOAP body.
   * <br>This is a space delimited String of message part values
   *
   * @param strParts the parts attribute for this SOAP body.
   */
  public void setParts( String strParts )
  { m_strParts = strParts; }


  /**
   * Helper method to transform a List of Strings into a space delimited parts string required
   * for the parts attribute value
   *
   * @param listParts The List of part strings to transform
   */
  public void setPartsList( List listParts )
  {
    StringBuffer sb = new StringBuffer();

    for ( Iterator iParts = listParts.iterator(); iParts.hasNext(); )
    {
      if ( sb.length() > 0 )
       sb.append( " " );

      sb.append( (String)iParts.next() );

    } // end for()

    m_strParts = sb.toString();

  } // end setPartsList()


  /**
   * Gets the parts attribute for this SOAP body.
   *
   * @return the parts attribute for this SOAP body.
   */
  public String getParts()
  { return m_strParts; }


  /**
   * Helper method to transform the space delimited parts list into a List of Strings
   *
   * @return A List of Strings representing the parts
   */
  public List getPartsList()
  {
    List listParts = new LinkedList();

    if ( m_strParts == null )
      return listParts;

    VwDelimString dlmsParts = new VwDelimString( " ", m_strParts );

    return dlmsParts.toStringList();

  } // end getPartsList()

  /**
   * Sets the use attribute for this SOAP body element.
   *
   * @param strUse the use attribute for this SOAP body element.
   */
  public void setUse( String strUse )
  { m_strUse = strUse; }

  /**
   * Gets the use attribute for this SOAP body element.
   *
   * @return the use attribute for this SOAP body element.
   */
  public String getUse()
  { return m_strUse; }

  /**
   * Sets the encodingStyle attribute for this SOAP body. Each style is delimited by a space
   *
   * @param strEncodingStyles the desired encodingStyles
   */
  public void setEncodingStyle( String strEncodingStyles )
  { m_strEncodingStyles = strEncodingStyles; }

  /**
   * Gets the encodingStyles for this SOAP body.
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
   * Sets the namespace URI attribute value for this SOAP body.
   *
   * @param strNamespaceURI he namespace URI attribute value
   */
  public void setNamespace( String strNamespaceURI )
  { m_strNamespaceURI = strNamespaceURI; }

  /**
   * Get the namespace URI attribute for this SOAP body.
   *
   * @return the namespace URI attribute
   */
  public String getNamespace()
  { return m_strNamespaceURI; }

} // end class VwSOAPBodyImpl{}

// *** End of VwSOAPBodyImpl.java ***
