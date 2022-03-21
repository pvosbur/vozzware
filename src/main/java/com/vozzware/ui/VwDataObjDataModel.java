/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwDataObjDataModel.java

Create Date: Apr 11, 2003
============================================================================================
*/
package com.vozzware.ui;

import com.vozzware.xml.VwDataObjList;
import com.vozzware.xml.VwDataObject;

import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.FontMetrics;
import java.util.Iterator;

/**
 * This class provides the functionality for the AbstractTableModel using
 * dataobjects to store table rows. The column data is stored a name value pairs in the
 * data object.
 */
public class VwDataObjDataModel extends AbstractTableModel
{

  class VwModelDataObj extends VwDataObject
  {
    VwModelDataObj( boolean fPreserveCase, boolean fPreserveDataOrder )
    { super( fPreserveCase, fPreserveDataOrder ); }

    VwModelDataObj( VwDataObject dataObj )
    { super( dataObj ); }


  } // end class VwModelDataObj


  private   VwDataObjList        m_dobjListData;   // List of data object results
  private   VwDataObject         m_dobjColumns;    // Initial column definitions
  private   VwModelDataObj       m_dobjEmptyRow;   // Used to clone new rows with columns names set


  private   boolean[]             m_afCellEditable; // Cell editable flags

  private   int                   m_nMaxRows;       // Max rows allowed or zero for any number
  private   int                   m_nColCount = 0;

  private   boolean               m_fUseRowHdr = true; // Col zero acts as row header

  private   VwTable              m_table;         // The associated VwTable
  private   String[]              m_astrColNames;

  private   boolean               m_fModelChanged = false;

  private   static int            m_nCharWidth = 0;


  /**
   * Constructor - Builds column attributes for existing elemenet names in the data object<br>
   * The table is initially empty
   *
   * @param dobjColumns DataObject that will be used to determine the names
   * and number of columns in the table and optionally collumn attrubutes if
   * The column name strores an VwTableColAttr object as it's data value
   *
   * @param nMaxRows The maximum nbr of rows to allow or 0 to allow any number
   */
  public VwDataObjDataModel( VwTable table, VwDataObject dobjColumns,
                              boolean fUseRowHdr, int nInitialRows, int nMaxRows )
  {
    m_table = table;
    //m_table.setAutoResizeMode( m_nColResizeMode );

    m_fUseRowHdr = fUseRowHdr;
    m_table.m_fHasRowHeaders = m_fUseRowHdr;

    if ( m_fUseRowHdr )
    {
      dobjColumns.put( "ITCHDR", "" );
      dobjColumns.changeDataOrder( "ITCHDR", 0 );
    }

    m_nColCount = dobjColumns.size();

    m_table.setModel( this );
    update( dobjColumns, nInitialRows, nMaxRows );


  } // end



  /**
   *
   * @param dobjColumns
   * @param nMaxRows
   */
  public void update( VwDataObject dobjColumns, int nInitialRows, int nMaxRows )
  {

    m_fModelChanged = true;
    m_nColCount = dobjColumns.size();

    m_dobjColumns = dobjColumns;

    m_dobjEmptyRow = new VwModelDataObj( true, true );

    try
    {
      for ( int x = 0; x < m_dobjColumns.size(); x++ )
        m_dobjEmptyRow.put( m_dobjColumns.getKey( x ), (String)null );

    }
    catch( Exception ex )
    { ; }

    m_nMaxRows = nMaxRows;

    m_astrColNames = new String[ dobjColumns.size() ];
    m_afCellEditable = new boolean[ dobjColumns.size() ];

    m_dobjListData = new VwDataObjList();

    if ( nInitialRows > 0 )
    {
      VwDataObject dobjRow = new VwDataObject( dobjColumns );
      try
      {
       for ( int x = 0; x < dobjRow.size(); x++ )
         dobjRow.put( (String)dobjRow.getKey( x ), "" );
      }
      catch( Exception e )
      { ; }

      m_dobjListData.add( dobjRow );
      for ( int x = 1; x < nInitialRows; x++ )
      {
        m_dobjListData.add( new VwDataObject( dobjRow ) );
      }

    }

    this.fireTableStructureChanged();
    buildColumnAttributes();

  } // end update()

  public String getColumnName( int nColNbr )
  {
    if ( m_fUseRowHdr )
      ++nColNbr;

    if ( m_astrColNames == null || m_astrColNames.length == 0 )
      return super.getColumnName( nColNbr );

    return m_astrColNames[ nColNbr ];

  } // end getColumnName()

  /**
   * Refresh the m_btModel
   */
  public void refresh()
  {
    m_table.setModel( this );

    m_fModelChanged = true;

    this.fireTableStructureChanged();
    buildColumnAttributes();
  }

  /**
   *
   * @return
   */
  public boolean modelChanged()
  {
    boolean fTemp = m_fModelChanged;
    m_fModelChanged = false;  // Clear flag on query
    return fTemp;

  }

  /**
   * Constructor - Loads the data m_btModel from an VwDataObjList. The column header names
   * are built from the key names in the first dataobject in the list
   *
   * @param dobjColumns DataObject that will be used to determine the names
   * and number of columns in the table
   *
   * @param nMaxRows The maximum nbr of rows to allow or 0 to allow any number
   */
  public VwDataObjDataModel( VwTable table, VwDataObjList dobjListData,
                              boolean fUseRowHdr, int nInitialRows, int nMaxRows ) throws Exception
  {
    m_table = table;
    m_fUseRowHdr = fUseRowHdr;
    m_nMaxRows = nMaxRows;

    m_dobjListData = dobjListData;

    if ( dobjListData.size() == 0 )
      throw new Exception( "Cannot pass an empty list" );

    m_dobjColumns = dobjListData.getDataObj( 0 );

    m_nColCount = m_dobjColumns.size();
    m_table.setModel( this );

   } // end


  /**
   * Create a new VwDataObject with the column placeholders set according to the column definitions
   * @return
   */
   public VwDataObject createDataObj()
   { return new VwModelDataObj( m_dobjEmptyRow ); }


   /**
   * Builds column attributes for the key names in the dataobject
   *
   * @param dobj The data object to build the column headers names
   */
  private void buildColumnAttributes( )
  {
    int nColNbr = -1;
    int nColResizeMode = JTable.AUTO_RESIZE_OFF;

    // Get column names for the key names in the data object
    TableColumnModel tcm = m_table.getColumnModel();

    boolean fAddToModel = false;

    if ( tcm.getColumnCount() == 0 )
      fAddToModel = true;

    int nTblWidth = m_table.getWidth();

    int nTotWidth = 0;

    for ( Iterator iKeys = m_dobjColumns.keys(); iKeys.hasNext(); )
    {

      ++nColNbr;

      TableColumn tc = null;

      if ( fAddToModel )
      {
        tc = new TableColumn();
        tcm.addColumn( tc );
      }
      else
        tc = tcm.getColumn( nColNbr );

      String strKey = (String)iKeys.next();

      VwTableColAttr colAttr = (VwTableColAttr)m_dobjColumns.get( strKey );


      if ( m_fUseRowHdr && nColNbr == 0 )
      {
        m_astrColNames[ nColNbr ] = "";
        tc.setCellRenderer( new HdrCellRenderer()  );
        tc.setHeaderValue( " " );
        tc.setResizable( false );
        tc.setPreferredWidth( 40 );
        nTotWidth += 40;

        continue;
      }
      else
        m_astrColNames[ nColNbr ] = colAttr.getColName();

      m_afCellEditable[ nColNbr ] = colAttr.isEditable();

      // Install default textfield editor if none specified and cell is marked editable
      TableCellEditor editor = colAttr.getCellEditor();
      if ( editor == null && m_afCellEditable[ nColNbr ] == true )
        tc.setCellEditor( new DefaultCellEditor ( m_table.m_txtCellEditor ) );
      else
        tc.setCellEditor( editor );

      tc.setCellRenderer( colAttr.getCellRenderer() );
      tc.setHeaderValue( colAttr.getColName() );

      int nWidth = colAttr.getWidth();

      if ( nWidth < 0 )
      {
        nColResizeMode = JTable.AUTO_RESIZE_LAST_COLUMN;
        nWidth = nTblWidth - nTotWidth;
      }
      else
      if ( nWidth == 0 )
      {
         if ( m_nCharWidth == 0 )
         {
           FontMetrics fm = m_table.getFontMetrics( m_table.getTableHeader().getFont() );

           m_nCharWidth = fm.charWidth( 'W' );
         }

         nWidth = m_nCharWidth * colAttr.getColName().length();
      }

      nTotWidth += nWidth;

      tc.setPreferredWidth( nWidth );
      tc.setResizable( true );

    } // end for( Iterator iKeys = ... )

    m_table.setAutoResizeMode( nColResizeMode );

  } // end buildColumnAttributes()


  /**
   * Forces a reload of the m_btModel. This should be called if any of the column attribute
   * objects have changed
   */
  public void reloadModel()
  {
    this.fireTableStructureChanged();
    buildColumnAttributes();

  } // end reloadModel

  /**
   * Adds a new column to an exisitng table
   *
   * @param objColKey a object representing a unique column key
   * @param colAttr The column attributes
   */
  public void addColumn( String strKey, VwTableColAttr colAttr )
  {
    String[] astrColNames = new String[ m_astrColNames.length + 1 ];
    System.arraycopy( m_astrColNames, 0, astrColNames, 0, m_astrColNames.length );
    astrColNames[ m_astrColNames.length ] = colAttr.getColName();

    m_astrColNames = astrColNames;
    m_nColCount = m_astrColNames.length;

    boolean[] afCellEditable = new boolean[ m_astrColNames.length ];
    System.arraycopy( m_afCellEditable, 0, afCellEditable, 0, m_afCellEditable.length );
    afCellEditable[  m_afCellEditable.length ] = colAttr.isEditable();
    m_afCellEditable = afCellEditable;

    m_fModelChanged = true;

    m_dobjColumns.put( strKey, colAttr );

    this.fireTableStructureChanged();
    buildColumnAttributes();

  } // end addColumn()


  /**
   * Removes all columns from the m_btModel
   */
  public void removeAllColumns()
  {
    m_astrColNames = new String[ 0 ];
    m_afCellEditable = new boolean[ 0 ];
    m_dobjColumns.clear();

    m_nColCount = 0;
    m_fModelChanged = true;

    this.fireTableStructureChanged();
    //buildColumnAttributes();

  } // end removeAllColumns()


  /**
   * Removes a column from  an exisitng table
   *
   * @param strColName The name of the column to remove
   * @param colAttr The column attributes
   */
  public void removeColumn( String strColKey )
  {

    if ( ! m_dobjColumns.exists( strColKey ) )
      return;

    int nColNbrToRemove = -1;

    try
    {
      nColNbrToRemove = m_dobjColumns.getIdOrderNbr( strColKey );
    }
    catch( Exception ex )
    {
      ex.printStackTrace();
      return;
    }
    m_dobjColumns.remove( strColKey );

    int nNewLen = m_astrColNames.length - 1;

    if ( nNewLen == 0 )
    {
      m_astrColNames = new String[ 0 ];
      m_afCellEditable = new boolean[ 0 ];

      m_nColCount = 0;

    }
    else
    {
      String[] astrColNames = new String[ nNewLen ];

      boolean[] afCellEditable = new boolean[ nNewLen ];

      int nNewNdx = -1;
      for ( int x = 0; x < m_astrColNames.length; x++ )
      {
        if ( x != nColNbrToRemove )
        {
          astrColNames[ ++nNewNdx ] = m_astrColNames[ x ];
          afCellEditable[ nNewNdx ] = m_afCellEditable[ x ];
        }

      } // end for

      m_nColCount = nNewLen;
      m_astrColNames = astrColNames;
      m_afCellEditable = afCellEditable;

    } // end else

    for ( Iterator iRows = m_dobjListData.iterator(); iRows.hasNext(); )
    {
      VwDataObject dobjRow = (VwDataObject)iRows.next();
      dobjRow.remove( strColKey );
    }

    m_fModelChanged = true;

    this.fireTableStructureChanged();
    buildColumnAttributes();

  } // end removeColumn()


 /**
  * Returns the cell editable state
  *
  * @param nRowNbr The row nbr ( ignored in this case)
  * @param nColNbr The column number
  * @return true if cell is editable, false otherwise (the default)
  */
  public boolean isCellEditable( int nRowNbr, int nColNbr )
  { return m_afCellEditable[ nColNbr ]; }


  /**
    * Sets a cell's editable state
    * @param nColNbr The column number
    * @param fEditable true if cell is editable, false otherwise
    */
    public void setCellEditable( int nColNbr, boolean fEditable )
    {
      if ( m_fUseRowHdr )
        ++nColNbr;

      m_afCellEditable[ nColNbr ] = fEditable;

    } // end setCellEditable()

  /**
   * Get column count for service result
   *
   * @return The nbr of result columns from a sql service
   */
  public int getColumnCount()
  {
     return m_nColCount;
  }


  /**
   * Get row count for service result
   *
   * @return The nbr of result columns from a sql service
   */
  public int getRowCount()
  {
    if ( m_nMaxRows > 0 )
      return m_nMaxRows;

    if ( m_dobjListData == null )
      return 0;

    // DEBUG ONLYSystem.out.println( "Row Count: " + m_dobjListData.size() );

    return m_dobjListData.size();


  }


  /**
   * Get the value for a row,column nbr
   *
   * @param nRowNbr The row nbr the value is on
   * @param nColNbr The column nbr in the row to get the value for
   */
  public Object getValueAt( int nRowNbr, int nColNbr )
  {

    // DEBUGGING System.out.println( "Getting Value for Column: " + nColNbr );
    if ( nColNbr == 0 && m_fUseRowHdr )
      return "" + nRowNbr;

    if ( m_dobjListData.size() == 0 )
      return "";

    if ( nRowNbr > m_dobjListData.size() )
      return "";

    VwDataObject dobjRow = m_dobjListData.getDataObj( nRowNbr );

    if ( dobjRow == null )
      return "";


    if ( nColNbr >= dobjRow.size() )
      return "";

    try
    {
      return dobjRow.get( nColNbr );
    }
    catch( Exception e )
    {
      return e.toString();
    }

  } // end getValueAt()


  /**
   * Set the value for a row nbr,column nbr
   *
   * @param nRowNbr The row nbr the value is on
   * @param nColNbr The column nbr in the row to set the value for
   */
  public void setValueAt( Object objVal, int nRowNbr, int nColNbr )
  {
    VwDataObject dobjRow = null;

    // Add row if needed
    if ( nRowNbr > m_dobjListData.size() || m_dobjListData.size() == 0 )
    {
      dobjRow = new VwDataObject( false, true );
      if ( m_fUseRowHdr )
        dobjRow.put( "ITCHDR", "" );

      m_dobjListData.add( dobjRow );

    }
    else
     dobjRow = m_dobjListData.getDataObj( nRowNbr );

    dobjRow.put( m_astrColNames[ nColNbr ], objVal );


  } // end setValueAt()

 /**
  * Gets the data object for the row specified
  *
  * @param nRowNbr The row nbr to get the data object for
  *
  */
 public VwDataObject getRowData( int nRowNbr ) throws Exception
 {
   if ( nRowNbr >= m_dobjListData.size() )
     return null;

    return m_dobjListData.getDataObj( nRowNbr );

 }

  /**
   * Add a row of data to the table
   * @param dobjRow The column data to be added as the next row in the table
   */
  public void addRow( VwDataObject dobjRow )
  {
    if ( m_fUseRowHdr )
    {
      dobjRow.put( "ITCHDR", "" );
      dobjRow.changeDataOrder( "ITCHDR", 0 );
    }

    m_dobjListData.add( dobjRow );

  }

  /**
   * Removes all rows from the table
   */
  public void removeAllRows()
  { m_dobjListData.clear(); }


  /**
   * Removes the specified row nbr
   * @param nRowNbr The row nbr to remove
   *
   * @exception ArrayIndexOutOfBoundsException if the the row nbr exceeds the nbr of rows
   */
  public void removeRow( int nRowNbr )
  {  m_dobjListData.remove( nRowNbr ); }


  /**
   * Update row nbr with new data
   * @param nRowNbr The row nbr to update
   * @param dobjRow The row update data
   */
  public void updateRow( int nRowNbr, VwDataObject dobjRow )
  {
    if ( m_fUseRowHdr )
    {
      dobjRow.put( "ITCHDR", "" );
      dobjRow.changeDataOrder( "ITCHDR", 0 );
    }

    m_dobjListData.set( nRowNbr, dobjRow );

  } // end updateRow()


  /**
  * Flushes the content of the cell being edited to the m_btModel
  * @param tbl
  */
  public void flush()
  {
   int nRow = m_table.getEditingRow();
   int nCol = m_table.getEditingColumn();

   if ( nRow < 0 )
     return;

   TableCellEditor ce = m_table.getCellEditor( nRow, nCol );
   ce.stopCellEditing();

   setValueAt( ce.getCellEditorValue(), nRow, nCol );

  } // end


  /**
   * Size/re-size table header columns This is invoked by the VwTable instance when
   * the table has been re-sized
   */
  public void sizeColumns()
  {
    this.fireTableStructureChanged();
    buildColumnAttributes();
    m_table.validate();

 } // end sizeColumns()

} // end class VwDataObjDataModel{}

// *** End of VwDataObjDataModel.java ***
