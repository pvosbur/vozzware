/*
 ============================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

  THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
  PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
  CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

  Source Name:   VwDbExport.java


 ============================================================================
*/

package com.vozzware.db;                         // Our package


import com.vozzware.util.VwLogger;

/**
 * This class is used to export relational database tables in a schema to a standard
 * flat file format that can be imported across database vendor products.  The input to
 * this class is the VwDbInpExpList (import/export) class. You first use this class
 * do define the schemas and tables with in the schemas to import/export. The format of
 * the flat file is as follows:
 *

 */
public class VwDbExport
{

  private String                m_strDriverName;
  private String                m_strUrlId;
  
  private VwDbImpExpList       m_expList;
  
  private VwDbExportRenderer   m_dbExpRenderer;
  
  private VwDbMgr              m_dbMgr;
  private VwDatabase           m_dbExport;
  
  /**
   * 
   * @param strDriverName
   * @param strUrlId
   * @param strUserId
   * @param strPassword
   * @param expList
   * @param dbExportRenderer
   * @throws Exception
   */
  public VwDbExport( String strDriverName, String strUrlId, String strUserId, String strPassword,
                      VwDbImpExpList expList, VwDbExportRenderer dbExportRenderer )
                       throws Exception
  {
    m_strDriverName = strDriverName;
    m_strUrlId = strUrlId;
    m_expList = expList;
    m_dbExpRenderer = dbExportRenderer;


    m_dbMgr = new VwDbMgr( m_strDriverName, m_strUrlId, VwLogger.getInstance() );
    m_dbExport = m_dbMgr.login( strUserId, strPassword );
    
    
  } // end Constructor  VwDbExport()



  /**
   * Starts the export process.
   *
   * @exception Exception when any errors occur
   */
  public void export() throws Exception
  {

  } // end export()



} // end class VwDbExport{}

// *** End of VwDbExport.java ***

