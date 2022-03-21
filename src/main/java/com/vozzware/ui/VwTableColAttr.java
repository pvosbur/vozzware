/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwTableColAttr.java

Create Date: Apr 11, 2003
============================================================================================
*/
package com.vozzware.ui;

import javax.swing.DefaultCellEditor;
import javax.swing.Icon;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.Color;
import java.awt.Font;


/**
 * This class provides additional column attributes for the VwTable.
 */
public class VwTableColAttr
{

  private String            m_strColName;     // The column name
  private String            m_strToolTip;     // The header tool tip text
  private Icon              m_colHdrIcon;     // Optional column Icon

  private boolean           m_fIsEditable;
  
  private int               m_nWidth;

  private Color             m_clrForeGround;
  private Color             m_clrBackGround;
  private Font              m_fontHeader;
  
  private TableCellEditor   m_cellEditor;     // Cell editor or null for TextField
  private TableCellRenderer m_cellRenderer;

  
  /**
   * Constructs with editable setting only
   * @param strColName The name of the column
   * @param fIsEditable true if column is editable, false if it's read only
   */
  public VwTableColAttr( String strColName, boolean fIsEditable )
  {
    m_strColName = strColName;
    m_fIsEditable = fIsEditable;
    
    // If editable, create a text field cell editor
    if ( m_fIsEditable )
      m_cellEditor = new DefaultCellEditor( new JTextField() );

  } // end VwTableColAttr()


  /**
   * Constructs with editable flag, cell editor and cell renderer objects
   *
   * @param strColName The name of the column
   * @param fIsEditable fIsEditable true if column is editable, false if it's read only
   * @param cellEditor  An optional cell editor if not null
   * @param m_cellRenderer An optional cell renderer if not null
   */
  public VwTableColAttr( String strColName, TableCellEditor cellEditor,
                          TableCellRenderer cellRenderer )
  {
    m_strColName = strColName;
    m_fIsEditable = true;
    m_cellEditor = cellEditor;
    m_cellRenderer = cellRenderer;

  } // end VwTableColAttr()

  
   
  public String getColName()
  { return m_strColName; }

  public void setColName( String strColName )
  { m_strColName = strColName; }

  public Icon getColHdrIcon()
  { return m_colHdrIcon; }

  public void setColHdrIcon( Icon colHdrIcon )
  {  m_colHdrIcon = colHdrIcon; }

  /**
   * Return The cell editable flag
   * @return The cell editable flag
   */
  public boolean isEditable()
  { return m_fIsEditable; }

  /**
   * Sets the cell editable flag
   * @param fIsEditable true if cell is editable, false otherwise
   */
  public void setEditable( boolean fIsEditable )
  {  m_fIsEditable = fIsEditable; }


  /**
   * Returns the cell editor to use (may be null)
   * @return the cell editor to use (may be null)
   */
  public TableCellEditor getCellEditor()
  { return m_cellEditor; }


  /**
   * Sets the cell editor to use ( may be null )
   * @param cellEditor the cell editor to use ( may be null )
   */
  public void setCellEditor( TableCellEditor cellEditor )
  { m_cellEditor = cellEditor; }


  /**
   * Returns the cell renderer to use ( may be null )
   * @return the cell renderer to use ( may be null )
   */
  public TableCellRenderer getCellRenderer()
  {  return m_cellRenderer; }

  /**
   * Sets the cell renderer to use
   * @param cellRenderer( may be null )
   */
  public void setCellRenderer( TableCellRenderer cellRenderer )
  { m_cellRenderer = cellRenderer; }

  /**
   *
   * @return The width of the column
   */
  public int getWidth()
  { return m_nWidth; }


  /**
   * Sets the initial width of the column
   * @param nWidth The initial column width
   */
  public void setWidth( int nWidth )
  { m_nWidth = nWidth;  }
  
  public void setToolTip( String strToolTip )
  { m_strToolTip = strToolTip; }
  
  public String getToolTip()
  { return m_strToolTip; }
  
  public void setColorForeGround( Color clrForeGround )
  { m_clrForeGround = clrForeGround; }
  
  public Color getColorForeGround()
  { return m_clrForeGround; }
  
  public void setColorBackGround( Color clrBackGround )
  { m_clrBackGround = clrBackGround; }
  
  public Color getColorBackGround()
  { return m_clrBackGround; }
  
  public void setHeaderFont( Font fontHeader )
  { m_fontHeader = fontHeader; }
  
  public Font getHeaderFont()
  { return m_fontHeader; }
  
} // end class VwTabelColAttr{}

// *** End of VwTabelColAttr.java ***

