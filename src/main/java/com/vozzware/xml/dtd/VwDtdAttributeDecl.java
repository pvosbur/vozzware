/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwDtdAttributeDecl.java

Create Date: Apr 11, 2006
============================================================================================
*/
package com.vozzware.xml.dtd;

import com.vozzware.util.VwDelimString;
import com.vozzware.util.VwExString;

/**
 * This utility class converts certain requested schema types into java classes that hold
 * a parsed xml document instance as well as java classes that build xml documents from
 * defined data sources.
 *
 */
public class VwDtdAttributeDecl
{

  /**
   * Constant for the CDATA attribute type
   */
  public static final int CDATA = 0;


  /**
   * Constant for the user define enumerated list of values
   */
  public static final int ENUM = 1;

  /**
   * Constant for the ID attribute type
   */
  public static final int ID = 2;


  /**
   * Constant for the IDREF attribute type
   */
  public static final int IDREF = 3;


  /**
   * Constant for the IDREFS attribute type
   */
  public static final int IDREFS = 4;


  /**
   * Constant for the NMTOKEN attribute type
   */
  public static final int NMTOKEN = 5;

  /**
   * Constant for the NMTOKENS attribute type
   */
  public static final int NMTOKENS = 6;

  /**
   * Constant for the NMTOKENS attribute type
   */
  public static final int ENTITY = 7;

  /**
   * Constant for the ENTITIES attribute type
   */
  public static final int ENTITIES = 8;

  /**
   * Constant for the NOTATION attribute type
   */
  public static final int NOTATION = 9;

  /**
   * Constant for the predefined xml:
   */
  public static final int XML = 10;

  private static final String[] s_astrTypes = { "CDATA", "ENUM", "ID", "IDREFS", "NMTOKEN",
                                                "NMTOKENS", "ENTITY", "ENTITIES", "NOTATION",
                                                "xml:" };


  private String                m_strDefValue;    // The default value

  private String                m_strValue;       // Attribute value

  private String                m_strAttrName;    // The name of the attribute

  private String                m_strElementName; // The name of the element this attribute belongs

  private int                   m_nType;          // Attribute type

  private VwDelimString        m_dlmsEnums;      // String array of enum values if type is ENUM


  /**
   * Constructor - only used by the VwDtdParser to pased the unparesed element content
   *
   * @param strElementName The name of the element
   * @param strContent The m_btModel content
   */
  VwDtdAttributeDecl( String strElementName, String strAttrName, String strType,
                       String strDefValue, String strValue )
  {

    m_nType = getType( strType );

    m_strElementName = strElementName;
    m_strAttrName = strAttrName;
    m_strDefValue = strDefValue;
    m_strValue = strValue;

  } // end VwDtdAttributeDecl


  /**
   * Return the content type constant
   */
  public int getType()
  { return m_nType; }


  /**
   * Return the types string name
   * @param nType The value of the type constant
   * @return
   */
  public String getTypeAsString( int nType )
  { return s_astrTypes[ nType ]; }

  /**
   * Return the enumerated type list if the attribute type is an ENUM else
   * this will return null
   *
   * @return
   */
  public VwDelimString getEnumList()
  { return m_dlmsEnums; }


  /**
   * Return the element name
   */
  public String getElementName()
  { return m_strElementName; }


  /**
   * Return the attribute name
   */
  public String getAttrName()
  { return m_strAttrName; }

  /**
   * Return the default value
   */
  public String getDefaultValue()
  { return m_strDefValue; }


  /**
   * Return the default value
   */
  public String getValue()
  { return m_strValue; }


  /**
   * Get the int constant equiv for the string stype
   *
   * @param strType The attribute type as a string
   */
  private int getType( String strType )
  {
    if ( strType.startsWith( "(" ) )
    {
      parse( strType );
      return ENUM;
    }

    for ( int x= 0; x < s_astrTypes.length; x++ )
    {
      if ( s_astrTypes[ x ].equals( strType ) )
        return x;

    } // end for()

    return 0;

  } // end getType()


  /**
   * Parse the enumerated list into a String array
   *
   * @param strContent The unparsed element content m_btModel
   */
  private void parse( String strEnum )
  {
    strEnum = strEnum.substring( 1, strEnum.length() - 1 );
    strEnum = VwExString.replace( strEnum, "|", "," );
    m_dlmsEnums = new VwDelimString( ",", strEnum );

  }  // end parse


} // end class VwDtdAttributeDecl{}

// *** End of VwDtdAttributeDecl.java ***
