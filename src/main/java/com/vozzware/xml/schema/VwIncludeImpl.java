/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwIncludeImpl.java

Create Date: Apr 11, 2006
============================================================================================
*/
package com.vozzware.xml.schema;

import com.vozzware.xml.schema.util.VwSchemaReaderImpl;
import org.xml.sax.SAXException;

import javax.xml.schema.Include;
import javax.xml.schema.InvalidSchemaLocationException;
import javax.xml.schema.Schema;


/**
 * This represents the XMl Schema include element tag
 *
 * @author P. VosBurgh
 */
public class VwIncludeImpl extends VwSchemaCommonImpl implements Include
{
  private String  m_strSchemaLocationURI;

  private Schema  m_schemaInclude;

  /**
   * Sets the schema loaction URI attribute
   * @param strSchemaLocationURI the schema loaction URI attribute
   * @throws InvalidSchemaLocationException if the schema cannot be found
   */
  public void setSchemaLocation( String strSchemaLocationURI  ) throws InvalidSchemaLocationException
  {
    loadSchema( strSchemaLocationURI );
    m_strSchemaLocationURI = strSchemaLocationURI;

  }

  /**
   * Gets the schema location URI attribute
   * @return  the schema location URI attribute
   */
  public String getSchemaLocation()
  { return m_strSchemaLocationURI; }

  /**
   * Gets the included schema document
   * @return the included schema document
   */
  public Schema getSchema()
  { return m_schemaInclude; }


  /**
   * Load and serialize schema
   * @param strSchemaLocationURI
   */
  private void loadSchema( String strSchemaLocationURI )  throws InvalidSchemaLocationException
  {
    try
    {


      VwSchemaReaderImpl rdr = new VwSchemaReaderImpl();
      m_schemaInclude = rdr.readSchema( strSchemaLocationURI );

    }
    catch( Exception ex )
    {
      if ( ex instanceof SAXException )
        throw new RuntimeException( ex.toString() );

      throw new InvalidSchemaLocationException( ex.toString() );
    }


  } // end getSchema()


  public boolean isParent()
  { return this.getAnnotation() == null; }

} // *** End of class VwIncludeImpl{}

// *** End of VwIncludeImpl.java