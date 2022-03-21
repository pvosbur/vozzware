/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwImportImpl.java

Create Date: Apr 11, 2006
============================================================================================
*/
package com.vozzware.wsdl;

import com.vozzware.wsdl.util.VwWSDLReaderImpl;
import org.xml.sax.SAXException;

import javax.wsdl.Definition;
import javax.wsdl.Import;
import javax.wsdl.InvalidWsdlLocationException;
import javax.xml.schema.InvalidSchemaLocationException;


/**
 * This represents the XMl Schema include element tag
 *
 * @author P. VosBurgh
 */
public class VwImportImpl extends VwWSDLCommonImpl implements Import
{
  private String      m_strLocationURI;
  private String      m_strNamespace;

  private Definition  m_wsdlInclude;


  /**
   * Sets the strNamespace attribute
   * @param strNamespace the strNamespace attribute
   */
  public void setNamespace( String strNamespace  )
  { m_strNamespace = strNamespace; }

  /**
   * Gets the wsdl location URI attribute
   * @return  the wsdl location URI attribute
   */
  public String getNamespace()
  { return m_strNamespace; }

  /**
   * Sets the schema loaction URI attribute
   * @param strLocationURI the schema loaction URI attribute
   * @throws javax.wsdl.InvalidWsdlLocationException if the schema cannot be found
   */
  public void setLocation( String strLocationURI  ) throws InvalidWsdlLocationException
  {
    try
    {
      loadWsdl( strLocationURI );
      m_strLocationURI = strLocationURI;

    }
    catch( Exception ex )
    {
      if ( ex instanceof SAXException )
        throw new RuntimeException( ex.toString() );

      throw new InvalidWsdlLocationException( ex.toString() );
    }

  }

  /**
   * Gets the schema location URI attribute
   * @return  the schema location URI attribute
   */
  public String getLocation()
  { return m_strLocationURI; }

  /**
   * Gets the included schema document
   * @return the included schema document
   */
  public Definition getDefinition()
  { return m_wsdlInclude; }


  /**
   * Load and serialize schema
   * @param strLocationURI
   */
  private void loadWsdl( String strLocationURI )  throws InvalidSchemaLocationException
  {

    try
    {


      VwWSDLReaderImpl rdr = new VwWSDLReaderImpl();
      m_wsdlInclude = rdr.readWsdl( strLocationURI );

    }
    catch( Exception ex )
    {
      if ( ex instanceof SAXException )
        throw new RuntimeException( ex.toString() );

      throw new InvalidSchemaLocationException( ex.toString() );
    }


  } // end getSchema()


  public boolean isParent()
  { return this.getDocumentation() != null; }

} // *** End of class VwImportImpl{}

// *** End of VwImportImpl.java