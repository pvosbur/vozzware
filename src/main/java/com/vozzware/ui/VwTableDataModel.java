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

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This is the super class for VwTable data models. It requires a List of VwTableColAttr
 * objects that define the table header attributes such a column with, cell editing and rendering.
 * Each column can have its own background, foreground color, font and icon.
 */
public abstract class VwTableDataModel extends AbstractTableModel
{

  private   boolean                           m_fUseRowHdr = true;  // Col zero acts as row header
  private   boolean                           m_fIsDirty = false;
  

  private   List<VwTableColAttr>             m_listTableColAttrs;
  private   List<VwTableModelChangeListener> m_listTableListeners = new ArrayList<VwTableModelChangeListener>();
  
  
  /**
   * Constructor
   *  
   * @param listTableColAttrs The List of VwTableColAttr objects
   * @param fUseRowHdr if true make the first column a rown counter columnb
   * @param nInitialRows the number of initial empty rows to create
   */
  public VwTableDataModel( List<VwTableColAttr> listTableColAttrs, boolean fUseRowHdr, int nInitialRows )
  {
    m_listTableColAttrs = listTableColAttrs;
    
    m_fUseRowHdr = fUseRowHdr;
    
    if ( m_fUseRowHdr )
    {  
      if ( m_listTableColAttrs == null )
       m_listTableColAttrs = new ArrayList<VwTableColAttr>();
      
      m_listTableColAttrs.add( 0, new VwTableColAttr( "", false ) );
      
    }
    
  } // endVwTableDataModel( 


  /**
   * Get the dirty flag for this model
   * @return
   */
  public boolean isDirty()
  { return m_fIsDirty; }
  
  
  /**
   * Set the dirty flag state for this model
   * @param fIsDirty
   */
  public void setDirty( boolean fIsDirty )
  { m_fIsDirty = fIsDirty; }
  

  public void addTableModelChangeListener( VwTableModelChangeListener tmcl )
  { m_listTableListeners.add( tmcl );  }
  
  
  public void removeTableModelChangeListener( VwTableModelChangeListener tmcl )
  { m_listTableListeners.remove( tmcl ); }
  
  
  /**
   * Sets a new List of VwTableColAttr objects
   * 
   * @param listTableColAttrs The new List of VwTableColAtytr objects
   */
  public void setColumnsAttrs( List<VwTableColAttr> listTableColAttrs )
  { 
    m_listTableColAttrs = listTableColAttrs;
    
    if ( m_fUseRowHdr )
      m_listTableColAttrs.add( 0, new VwTableColAttr( "", false ) );
    
    fireTableColumnsChangedEvent();
    
  } // end setColumnsAttrs()
  
  
  /**
   * Fires the VwTableModelChangedEvent for each registered listener
   *
   */
  public synchronized void fireTableColumnsChangedEvent()
  {
    VwTableModelChangedEvent tme = new VwTableModelChangedEvent( this, m_listTableColAttrs );
    
    for ( Iterator<VwTableModelChangeListener> iListeners = m_listTableListeners.iterator(); iListeners.hasNext();  )
    {
      VwTableModelChangeListener tmcl = iListeners.next();
      tmcl.columnAttributesChanged( tme );
      
    }

    
    this.fireTableStructureChanged();
    this.fireTableDataChanged();
    
  } // end fireTableColumnsChangedEvent()
  
  
  /**
   * Get the column name for the column number requested
   * 
   * @param nColNbr The column number to get the  name for
   */
  public String getColumnName( int nColNbr )
  {
    if ( nColNbr == 0 && m_fUseRowHdr )
      return "";

 
    return m_listTableColAttrs.get( nColNbr ).getColName();

  } // end getColumnName()

  
  
  /**
   * Computes the sum of all the widths set in each VwCollAttr object
   * @return
   */
  public int getTotalColumnWidth()
  {
    int nTotColWidth = 0;
    
    if ( m_listTableColAttrs == null )
      return 0;
    
    for ( Iterator<VwTableColAttr> iColAttrs = m_listTableColAttrs.iterator(); iColAttrs.hasNext(); )
    {
      VwTableColAttr colAttr = iColAttrs.next();
      nTotColWidth += colAttr.getWidth();
      
    }
    
    return nTotColWidth;
    
  } // end getTotalColumnWidth()
  
  
  /**
   * Return the List of VwTableColAttr attribute objects
   * @return the List of VwTableColAttr attribute objects or null if not yet set
   */
  public List<VwTableColAttr> getColumnAttributes()
  { return m_listTableColAttrs; }
  
   
  /**
   * Returns true if col number zero is the row header column
   * @return
   */
  public boolean hasRowHeader()
  { return m_fUseRowHdr; }
  
  /**
   * Refresh the m_btModel
   */
  public void refresh()
  {
    fireTableColumnsChangedEvent();
  }

 

  /**
   * Adds a new column to an exisitng table
   *
   * @param objColKey a object representing a unique column key
   * @param colAttr The column attributes
   */
  public void addColumn(  VwTableColAttr colAttr )
  { m_listTableColAttrs.add( colAttr );  } 


 
 /**
  * Returns the cell editable state
  *
  * @param nRowNbr The row nbr ( ignored in this case)
  * @param nColNbr The column number
  * @return true if cell is editable, false otherwise (the default)
  */
  public boolean isCellEditable( int nRowNbr, int nColNbr )
  { 
    VwTableColAttr colAttr = (VwTableColAttr)m_listTableColAttrs.get( nColNbr );
    return colAttr.isEditable();
    
  }

  /**
   * Get column count for service result
   *
   * @return The nbr of result columns from a sql service
   */
  public int getColumnCount()
  { return m_listTableColAttrs.size(); }


 /**
  * Flushes the content of the cell being edited to the m_btModel
  * @param tbl
  */
  public void flush( VwTable table )
  {
   int nRow = table.getEditingRow();
   int nCol = table.getEditingColumn();

   if ( nRow < 0 )
     return;

   TableCellEditor ce = table.getCellEditor( nRow, nCol );
   ce.stopCellEditing();

   setValueAt( ce.getCellEditorValue(), nRow, nCol );

  } // end




} // end class VwDataObjDataModel{}

// *** End of VwDataObjDataModel.java ***
