/*
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

  THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
  PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
  CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

  Source Name:   VwDbImpExpList.java


 ============================================================================
*/


package com.vozzware.db;                         // Our package

import com.vozzware.util.VwDelimString;

import java.util.Hashtable;

/**
 * This class is used to define a list of database schemas and their corresponding
 * tables used by the VwDbExport and VwDbImport classes. This class is required
 * parameter in both of those class constructors.
 */
public class VwDbImpExpList
{

  Hashtable   m_htSchemas = new Hashtable();    // Hash table of schemas and their table lists

  /**
   * Adds a schema and it's table list to this object
   *
   * @param strSchemaName The name of the database schema
   * @param dlmsTableList A delimited string of table names. Use a single asterisk in the
   * VwDelimString for all tables in the schema.
   *
   */
  public void addSchema( String strSchemaName, VwDelimString dlmsTableList )
  {

    if ( strSchemaName == null )
      return;       // Don't allow nulls here

    if ( dlmsTableList == null )
      return;       // Don't allow nulls here

    m_htSchemas.put( strSchemaName, dlmsTableList );

  } // end addSchema()

} /// end class VwDbImpExpList{}

// *** End of VwDbImpExpList.java

