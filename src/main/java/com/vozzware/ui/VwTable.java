/*
 ============================================================================================
 
 Copyright(c) 2000 - 2006 by

 V o z z W a r e   L L C (Vw)

 All Rights Reserved

 THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
 PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
 CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

 Source Name: VwTable.java

 Create Date: Apr 11, 2006
 ============================================================================================
 */

package com.vozzware.ui;

import javax.swing.Icon;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class HdrCellRenderer implements TableCellRenderer
{
  private JToggleButton m_btnRowHdr = new JToggleButton();

  private Color m_btnOrigColor = m_btnRowHdr.getBackground();

  private Font m_btnFont = m_btnRowHdr.getFont();

  private Font m_fontBold = new Font( m_btnFont.getName(), Font.BOLD, m_btnFont.getSize() );

  public Component getTableCellRendererComponent( JTable tbl, Object obj, boolean fIsSelected, boolean f2, int nRow,
      int nCol )
  {

    if (nRow < 0)
      return m_btnRowHdr;

    if (fIsSelected)
      m_btnRowHdr.setFont( m_fontBold );
    else
      m_btnRowHdr.setFont( m_btnFont );

    if (nRow == ((VwTable)tbl).m_nMouseRow)
    {
      m_btnRowHdr.setSelected( true );

      m_btnRowHdr.setBackground( Color.black );
      m_btnRowHdr.setForeground( Color.white );
    }
    else
    {
      m_btnRowHdr.setSelected( false );
      m_btnRowHdr.setBackground( m_btnOrigColor );
      m_btnRowHdr.setForeground( Color.black );
    }

    m_btnRowHdr.setText( "" + obj );
    return m_btnRowHdr;
  }

} // end inner class HdrCellRenderer{}

/**
 * This class extends the Swing JTable control. Its primary purpose is to make
 * the data aware in a 3 tier environment. The VwTable talks to the Opera
 * Server and uses the Opera Services to get its data.
 */
public class VwTable extends JTable
{
  private String m_strService; // Name of service to execute

  private String m_strParamNames; // Column param names

  private String m_strParamValues; // Column param values

  private boolean m_fNeedSize = true;

  boolean m_fHasRowHeaders = false;

  boolean m_fRowHdrSelected = false;

  int m_nMouseRow = -1;

  int m_nRowCursor = 0; // current row for squentail get operations

  VwTextField m_txtCellEditor = new VwTextField();

  private VwDefaultTableHeaderRenderer m_headerRenderer = null;

  private JToggleButton m_btnHeader = new JToggleButton();

  private VwTableDataModel m_model;

  private Map<Integer, List<TableCellEditor>> m_mapCellEditorsByColumn = new HashMap<Integer, List<TableCellEditor>>();

  /**
   * Constructs the grid control
   * 
   */
  public VwTable()
  {
    super();

    this.setGridColor( Color.black );


  } // end VwTable()
  

  /**
   * Sets the m_btModel used for this table. It must be a derived class from
   * VwTableDataModel
   * 
   * @param m_btModel
   */
  public void setModel( VwTableDataModel model )
  {
    m_model = model;
    m_model.addTableModelChangeListener( new VwTableModelChangeListener()
    {

      public void columnAttributesChanged( VwTableModelChangedEvent tmce )
      {
        createColumns( tmce.getTableColAttrs() );

      }
    } );

    super.setModel( model );

  } // end setModel

  public void setFont( Font fontCell )
  {
    super.setFont( fontCell );

    //FontMetrics fm = this.getFontMetrics( fontCell );
    //setRowHeight( fm.getHeight() );

  }

  /**
   * Give the focus to the cell at the specified row and col position
   * 
   * @param nRow -
   *          The row number of the cell
   * @param nCol -
   *          The column number of the cell
   * 
   */
  public final void setCellFocus( int nRow, int nCol )
  {
    editCellAt( nRow, nCol );
  }

  public void setHeaderRenderer( VwDefaultTableHeaderRenderer tcr )
  {
    m_headerRenderer = tcr;
    this.getTableHeader().setDefaultRenderer( tcr );

  }

  public VwDefaultTableHeaderRenderer getHeaderRenderer()
  {
    return m_headerRenderer;
  }

  /**
   * Sets the comma delimited string of parameter names used to setup the
   * service parameters required to execute a Service.
   * 
   * @param strParamName -
   *          The comma delimited string of parameter names
   */
  public final void setParamNames( String strParamNames )
  {
    m_strParamNames = strParamNames;
  }

  /**
   * Gets the comma delimited string of parameter names used to setup the
   * service parameters required to execute a service
   * 
   * @return - The comma separated string of parameter names
   */
  public final String getParamNames()
  {
    return m_strParamNames;
  }

  /**
   * Sets the set of parameter values corresponding to the parameter names
   * 
   * @param strParamValues -
   *          A comma delimited string of parameter values
   */
  public final void setParamValues( String strParamValues )
  {
    m_strParamValues = strParamValues;
  }

  /**
   * Gets the set of parameter values corresponding to the parameter names
   * 
   * @return - A comma delimited string of parameter values
   */
  public final String getParamValues()
  {
    return m_strParamValues;
  }

  /**
   * When each row in the table can have a different cell editor for the same
   * column, this method can be used to return the cell editor for a specific
   * row and column
   * 
   * @param listCellEditors
   *          a List of cell editors (one for each row in the table) for a
   *          specific column nbr
   * @param nColNbr
   *          The column nbr in the table the cell editors are for
   */
  public void setCellEditorsByColumn( List<TableCellEditor> listCellEditors, int nColNbr )
  {
    m_mapCellEditorsByColumn.put( nColNbr, listCellEditors );
  }

  /**
   * This overrides the JTables method to provide a cell editor for the row and
   * column specified. A prior invocation of the setCellEditorsByColumn is
   * required
   */
  public TableCellEditor getCellEditor( int nRow, int nColNbr )
  {
    List<TableCellEditor> listEditors = m_mapCellEditorsByColumn.get( nColNbr );
    if (listEditors == null)
      return super.getCellEditor( nRow, nColNbr );

    return listEditors.get( nRow );

  } // end getCellEditor()

  /**
   * Create table columns from the List of VwTableColAttrs
   * 
   */
  public void createColumns( List<VwTableColAttr> listTableColAttrs )
  {
    final JTableHeader tblHdr = getTableHeader();

    tblHdr.addMouseListener( new MouseAdapter()
    {
      public void mouseExited( MouseEvent e )
      {
        Cursor curText = Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR );
        Component comp = tblHdr.getParent();

        // this is necessary under eclipse becuase mouce feedback is'nt properly
        // handled
        while ( comp != null )
        {
          comp.setCursor( curText );
          comp = comp.getParent();
        }

      }

    } );

    TableCellRenderer cr = tblHdr.getDefaultRenderer();
    Font fontHeader = null;

    boolean fUseRowHeader = m_model.hasRowHeader();

    Insets ins = m_btnHeader.getInsets();

    removeAllColumns();
    int ndx = -1;

    for ( VwTableColAttr colAttr : listTableColAttrs )
    {
      TableColumn tc = new TableColumn( ++ndx );

      if (colAttr.getHeaderFont() != null)
        fontHeader = colAttr.getHeaderFont();
      else
        fontHeader = tblHdr.getFont();

      FontMetrics fm = getFontMetrics( fontHeader );

      if (colAttr.getCellEditor() != null)
        tc.setCellEditor( colAttr.getCellEditor() );

      tc.setHeaderValue( colAttr.getColName() );

      if (ndx == 0 && fUseRowHeader)
        tc.setCellRenderer( new HdrCellRenderer() );

      if ( colAttr.getCellRenderer() != null )
        tc.setCellRenderer( colAttr.getCellRenderer() );
      else
       tc.setHeaderRenderer( cr );

      
      int nWidth = fm.stringWidth( colAttr.getColName() );
      if (nWidth == 0)
        nWidth = fm.stringWidth( "999" );

      if (colAttr.getColHdrIcon() != null)
      {
        Icon icon = colAttr.getColHdrIcon();
        int nIconWidth = icon.getIconWidth();
        if (nIconWidth == 0)
          nIconWidth = 20;

        nWidth += nIconWidth + 10;

      } // end if
      else
        nWidth += 10;

      nWidth += (ins.left + ins.right);

      colAttr.setWidth( nWidth );
      tc.setWidth( nWidth );
      tc.setPreferredWidth( nWidth );

      addColumn( tc );

    } // end for

    Map mapColAttrs = convertToMap( listTableColAttrs );

    if (m_headerRenderer != null)
      m_headerRenderer.setTableColAttrs( mapColAttrs, listTableColAttrs.size() );

  } // end createColumns()

  private Map<String, VwTableColAttr> convertToMap( List<VwTableColAttr> listTableColAttrs )
  {
    Map<String, VwTableColAttr> mapColAttrs = new HashMap<String, VwTableColAttr>();

    for ( VwTableColAttr tca : listTableColAttrs )
      mapColAttrs.put( tca.getColName(), tca );

    return mapColAttrs;

  } // end convertToMap()

  /**
   * Removes all columns from the m_btModel
   */
  public void removeAllColumns()
  {

    TableColumnModel tcm = getTableHeader().getColumnModel();
    int nColCount = tcm.getColumnCount();

    // Remove existing columns
    for ( int x = 0; x < nColCount; x++ )
    {
      TableColumn tc = tcm.getColumn( 0 );
      removeColumn( tc );
    }

  } // end removeAllColumns()

} // end class VwTable

// *** End of VwTable.java ***

