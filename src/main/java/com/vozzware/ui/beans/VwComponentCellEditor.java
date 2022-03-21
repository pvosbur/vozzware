package com.vozzware.ui.beans;

import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import java.awt.Component;

public abstract class VwComponentCellEditor extends AbstractCellEditor implements TableCellEditor
{
  private Component m_compEditor;
  
  /**
   * Constructor
   * @param compEditor The component that handles the cell editing
   */
  public VwComponentCellEditor( Component compEditor )
  {
    m_compEditor = compEditor;
    
  }

  /**
   * Return the derived classes editing component
   */
  public Component getTableCellEditorComponent( JTable table, Object value, boolean isSelected, int row, int column )
  {  return m_compEditor;  }


  /**
   * Just returns the component that was passed in the constructor
   * @return
   */
  public Component getComponent()
  { return m_compEditor; }
  
}
