/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

  THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
  PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
  CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

    Source File Name: VwStdSchemaTypeConverter.java

    Author:           P. VosBurgh


============================================================================================
*/

package com.vozzware.db;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;


public class VwStdSchemaTypeConverter implements VwSchemaTypeConverter
{
  private static Map s_mapPrimTypes = null;
  private static Map s_mapSqlTypes = null;

  private static VwStdSchemaTypeConverter  m_instance = null;

  /**
   * Constructor for singleton
   */
  private VwStdSchemaTypeConverter()
  {
    buildTypeMap();
  }


  /**
   * Return singleton instance of this class
   */
  public static synchronized VwStdSchemaTypeConverter getInstance()
  {
    if ( m_instance == null )
     m_instance = new VwStdSchemaTypeConverter();

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

    s_mapPrimTypes = new HashMap();

    s_mapPrimTypes.put( "string", "VARCHAR" );
    s_mapPrimTypes.put( "boolean", "CHAR" );
    s_mapPrimTypes.put( "Boolean", "CHAR" );
    s_mapPrimTypes.put( "byte", "CHAR" );
    s_mapPrimTypes.put( "Byte", "CHAR" );
    s_mapPrimTypes.put( "short", "SMALLINT" );
    s_mapPrimTypes.put( "Short", "SMALLINT" );
    s_mapPrimTypes.put( "int", "INTEGER" );
    s_mapPrimTypes.put( "integer", "INTEGER" );
    s_mapPrimTypes.put( "Integer", "INTEGER" );
    s_mapPrimTypes.put( "long", "BIGINT" );
    s_mapPrimTypes.put( "Long", "BIGINT" );
    s_mapPrimTypes.put( "unsignedByte", "CHAR" );
    s_mapPrimTypes.put( "unsignedSshort", "SMALLINT" );
    s_mapPrimTypes.put( "unsignedInt", "INTEGER" );
    s_mapPrimTypes.put( "unsignedLong", "BIGINT" );
    s_mapPrimTypes.put( "float", "DOUBLE" );
    s_mapPrimTypes.put( "Float", "DOUBLE" );
    s_mapPrimTypes.put( "double", "DOUBLE" );
    s_mapPrimTypes.put( "Double", "DOUBLE" );
    s_mapPrimTypes.put( "decimal", "DOUBLE" );
    s_mapPrimTypes.put( "date", "TIMESTAMP" );
    s_mapPrimTypes.put( "time", "TIMESTAMP" );
    s_mapPrimTypes.put( "ID", "VARCHAR" );
    s_mapPrimTypes.put( "IDREF", "VARCHAR" );
    s_mapPrimTypes.put( "QNAME", "VARCHAR" );
    s_mapPrimTypes.put( "ENTITY", "VARCHAR" );
    s_mapPrimTypes.put( "positiveInteger", "INTEGER" );
    s_mapPrimTypes.put( "nonPositiveInteger", "INTEGER" );
    s_mapPrimTypes.put( "nonNegativeInteger", "INTEGER" );
    s_mapPrimTypes.put( "negativeInteger", "INTEGER" );
    s_mapPrimTypes.put( "object", "BLOB" );


    // Build SQL Type map

    s_mapSqlTypes = new HashMap();

    s_mapSqlTypes.put( "INTEGER", new Integer( Types.NUMERIC ) );
    s_mapSqlTypes.put( "CHAR", new Integer( Types.CHAR ) );
    s_mapSqlTypes.put( "VARCHAR", new Integer( Types.VARCHAR ) );
    s_mapSqlTypes.put( "DOUBLE", new Integer( Types.DOUBLE ) );
    s_mapSqlTypes.put( "SMALLINT", new Integer( Types.TINYINT ) );
    s_mapSqlTypes.put( "BIGINT", new Integer( Types.BIGINT ) );
    s_mapSqlTypes.put( "BLOB", new Integer( Types.LONGVARBINARY ) );
    s_mapSqlTypes.put( "TIMESTAMP", new Integer( Types.TIMESTAMP ) );

  } // end buildTypeMap()

} // *** End of class VwStdSchemaTypeConverter{}

// *** End Of VwStdSchemaTypeConverter.java