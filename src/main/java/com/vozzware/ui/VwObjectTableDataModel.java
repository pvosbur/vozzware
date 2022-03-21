/*
  ===========================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

  THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
  PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
  CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

  Source Name:   VwResultsetTableDataModel.java


  ============================================================================================
*/
package com.vozzware.ui;

import com.vozzware.util.VwBeanUtils;

import java.util.List;
import java.util.Map;

/**
 * This class provides the functionality for the AbstractTableModel useing
 * Opera serviceSchema calls to obtain the data that will populate a JTable.
 */
public class VwObjectTableDataModel extends VwTableDataModel
{

  private   int                 m_nCurRowNbr = -1;
  private   int                 m_nInitialRows;
  
  private   List                m_listObjects;
  
  public VwObjectTableDataModel( List listObjects, List listColAttrs, boolean fUseRowHeader, int nInitialRows) throws Exception
  {
    super( listColAttrs, fUseRowHeader, nInitialRows );
    m_nInitialRows = nInitialRows;
    
    m_listObjects = listObjects;
    
  }
  
  /**
   * Get the value for a row,column nbr
   *
   * @param nRowNbr The row nbr the value is on
   * @param nColNbr The column nbr in the row to get the value for
   */
  public Object getValueAt( int nRowNbr, int nColNbr )
  {

    if ( m_listObjects == null || m_listObjects.size() == 0 )
      return "";
    
    if ( nRowNbr < 0 || nColNbr < 0 )
    {
      return "";
    }
    
    try
    {
      if ( nColNbr == 0 &&  hasRowHeader() )
        return String.valueOf(nRowNbr + 1);
      
      Object objRow = m_listObjects.get( nRowNbr );
      
      if ( objRow == null )
        return "";
      
      String strColName = getColumnName( nColNbr );
      
      if ( objRow instanceof Map )
        return ((Map)objRow).get( strColName );
      else
        return VwBeanUtils.getValue( objRow, strColName );
      
    }
    catch( Exception e )
    {
      return e.toString();
    }

  } // end getValueAt()

  /**
   * Get the value for a row,column nbr
   *
   * @param nRowNbr The row nbr the value is on
   * @param nColNbr The column nbr in the row to get the value for
   */
  public void setValueAt( Object objVal, int nRowNbr, int nColNbr )
  {
    Object objRow = m_listObjects.get( nRowNbr );
    
    if ( objRow == null )
      return;
    
    String strColName = getColumnName( nColNbr );
   
    try
    {
      if ( objRow instanceof Map )
        ((Map)objRow).put( strColName, objVal );
      else
        VwBeanUtils.setBeanProperty( objRow, strColName, objVal );
    }
    catch( Exception ex )
    {
      ex.printStackTrace();
    }

  }

  public void removeAllRows()
  { m_listObjects.clear(); }
  
  /**
   * 
   */
  public int getRowCount()
  { 
    if ( m_listObjects == null || m_listObjects.size() == 0  )
    {
      return m_nInitialRows;
    }
    
    return m_listObjects.size();
    
  } // end getRowCount()

  public List getRows()
  { return m_listObjects; }
  
  public void addRow( Object objRow )
  {
    m_listObjects.add( objRow );
  }
 } // end class VwSqlTableDataModel{}

// *** End of VwSqlTableDataModel.java ***
