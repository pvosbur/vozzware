/*
  ===========================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

  THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
  PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
  CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

  Source Name:   VwSqlData.java


  ============================================================================================
*/

package com.vozzware.db;

import java.lang.reflect.Method;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is used by the VwService class and stores a cached Service
 * definition. This class is part of the SQL Service automated support.
 */
public class VwSqlData
{
  public  String      m_strSQL;                   // SQL or called procedure to execute
  public  String[]    m_astrTableList;            // List of table(s) this service uses
  public  short[]     m_asResultTypes;            // Corresponding result datatypes
  public  short[]     m_asResultParamNbr;         // Param nbr if this is a stored proc
  public  ArrayList   m_listParams;               // Parameter array of VwDataObject names (may be aliased_
  public  ArrayList   m_listResults;              // Result column array (could be alias names)
  public  short[]     m_asParamTypes;             // Corresponding param datatypes fro table Types.
  public  short[]     m_asParamNbr;               // Param nbr if this is a stored proc
  public  int         m_nStmtType;                // Statement type. One of the class VwSQLParser type
                                                  // constants
  public  Class<?>    m_clsStmtClass;             // The Class that a select statement will be loaded into or the
  
  public  Class<?>    m_clsTargetClass;           // The Class that has the target property
  public  Method	    m_mthdTargetProperty;				  // The setter method from the class target that 
                                                  // the object will be put
  public  Class<?>    m_clsMethodParam;
  
  public  boolean     m_fIsTargetPropList;        // If true, the target property is a list
  public  boolean     m_fReuseDataObj;            // If true reuse input dataobject for output

  public  List<VwSqlData>  m_listSqlDataDependencies;  // A List of dependent VwSqlData that must be executed
                                                  // for each row occurrence 
  
  public  boolean     m_fIsDynamicWhere = false;
  
  Object              m_objData;
  
  Map                 m_mapProps; 
  
  static  Map<VwDatabase,Map<VwSqlData,Statement>> s_mapStmtCache = Collections.synchronizedMap( new HashMap<VwDatabase,Map<VwSqlData,Statement>>() );
  
  synchronized Statement getStatement( VwDatabase db )
  {
    Map<VwSqlData,Statement> mapStatements = s_mapStmtCache.get( db );
    
    if ( mapStatements == null )
    {
      mapStatements = new HashMap<VwSqlData, Statement>();
      s_mapStmtCache.put(  db, mapStatements );
    }
    
    return mapStatements.get(  this );
    
    
  } // end getStatement
  
  synchronized void cacheStatement( VwDatabase db, Statement stmt )
  {
    
    Map<VwSqlData,Statement> mapStatements = s_mapStmtCache.get( db );
    
    if ( mapStatements == null )
    {
      mapStatements = new HashMap<VwSqlData, Statement>();
      s_mapStmtCache.put(  db, mapStatements );
    }
    
    mapStatements.put( this, stmt );
    
  }
  
} // end class VwSqlData{}


// *** End of VwSqlData.java ***
