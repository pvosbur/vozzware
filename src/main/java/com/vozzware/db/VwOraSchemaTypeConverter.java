/*
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

  THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
  PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
  CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

    Source File Name: VwOraSchemaTypeConverter.java

    Author:           P. VosBurgh


============================================================================================
*/

package com.vozzware.db;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;


public class VwOraSchemaTypeConverter implements VwSchemaTypeConverter
{
  private static Map<String,String> s_mapPrimTypes = null;
  private static Map<String,Integer> s_mapSqlTypes = null;

  private static VwOraSchemaTypeConverter  m_instance = null;

  /**
   * Constructor for singleton
   */
  private VwOraSchemaTypeConverter()
  {
    buildTypeMap();
  }


  /**
   * Return singleton instance of this class
   */
  public static synchronized VwOraSchemaTypeConverter getInstance()
  {
    if ( m_instance == null )
     m_instance = new VwOraSchemaTypeConverter();

    return m_instance;

  } // end getInstance()


  /**
   * Converts a schema type into a SQL type
   *
   * @param strSchemaType The Xml Schema data type to convert
   *
   * @return an VwColInfo instance with the SQl Type and Sql type name defined
   */
  public VwColInfo convertType( String strSchemaType )
  {
    int nPos = strSchemaType.indexOf( ':' );

    if ( nPos > 0 )
     strSchemaType = strSchemaType.substring( ++nPos ) ;

    String strSqlType = (String)s_mapPrimTypes.get( strSchemaType );

    if ( strSqlType == null )
      return null;


    VwColInfo ci = new VwColInfo();

    ci.setSQLTypeName( strSqlType );
    ci.setSQLType( ((Integer)s_mapSqlTypes.get( strSqlType ) ).intValue() );

    return ci;

  }

  /**
   * Build type converter maps
   */
  private void buildTypeMap()
  {

    s_mapPrimTypes = new HashMap<String,String>();

    s_mapPrimTypes.put( "string", "varchar2" );
    s_mapPrimTypes.put( "boolean", "char" );
    s_mapPrimTypes.put( "Boolean", "char" );
    s_mapPrimTypes.put( "byte", "char" );
    s_mapPrimTypes.put( "Byte", "char" );
    s_mapPrimTypes.put( "short", "number" );
    s_mapPrimTypes.put( "Short", "number" );
    s_mapPrimTypes.put( "int", "number" );
    s_mapPrimTypes.put( "integer", "number" );
    s_mapPrimTypes.put( "Integer", "number" );
    s_mapPrimTypes.put( "long", "number" );
    s_mapPrimTypes.put( "Long", "number" );
    s_mapPrimTypes.put( "unsignedByte", "char" );
    s_mapPrimTypes.put( "unsignedSshort", "number" );
    s_mapPrimTypes.put( "unsignedInt", "number" );
    s_mapPrimTypes.put( "unsignedLong", "number" );
    s_mapPrimTypes.put( "float", "decimal" );
    s_mapPrimTypes.put( "Float", "decimal" );
    s_mapPrimTypes.put( "double", "decimal" );
    s_mapPrimTypes.put( "Double", "decimal" );
    s_mapPrimTypes.put( "decimal", "decimal" );
    s_mapPrimTypes.put( "date", "date" );
    s_mapPrimTypes.put( "time", "date" );
    s_mapPrimTypes.put( "ID", "varchar2" );
    s_mapPrimTypes.put( "IDREF", "varchar2" );
    s_mapPrimTypes.put( "QNAME", "varchar2" );
    s_mapPrimTypes.put( "ENTITY", "varchar2" );
    s_mapPrimTypes.put( "positiveInteger", "number" );
    s_mapPrimTypes.put( "nonPositiveInteger", "number" );
    s_mapPrimTypes.put( "nonNegativeInteger", "number" );
    s_mapPrimTypes.put( "negativeInteger", "number" );
    s_mapPrimTypes.put( "object", "OBJECT" );


    // Build SQL Type map

    s_mapSqlTypes = new HashMap<String,Integer>();

    s_mapSqlTypes.put( "number", new Integer( Types.NUMERIC ) );
    s_mapSqlTypes.put( "char", new Integer( Types.CHAR ) );
    s_mapSqlTypes.put( "varchar2", new Integer( Types.VARCHAR ) );
    s_mapSqlTypes.put( "decimal", new Integer( Types.DECIMAL ) );
    s_mapSqlTypes.put( "date", new Integer( Types.DATE ) );

  } // end buildTypeMap()

} // *** End of class VwOraSchemaTypeConverter{}

// *** End Of VwOraSchemaTypeConverter