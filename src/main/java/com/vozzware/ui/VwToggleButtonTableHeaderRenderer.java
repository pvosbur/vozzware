/*
===========================================================================================

 
                               Copyright(c) 2000 - 2006 by

                      V o z z W a r e   L L C (Vw)

                                 All Rights Reserved

Source Name: VwToggleButtonTableHeaderRendereraderRenderer.java

Create Date: May 18, 2006

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.


============================================================================================
*/

package com.vozzware.ui;

import javax.swing.AbstractButton;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.Map;

public class VwToggleButtonTableHeaderRenderer extends VwTableHeadrClickRenderer
{
  
  private   JTable              m_table;
  private   boolean[]           m_afSelFlags;
  
  
  public VwToggleButtonTableHeaderRenderer( JTable table, Map<String,VwTableColAttr> mapTableColAttrs, int nIgnoreColNbr )
  { 
    super( table, mapTableColAttrs, new JToggleButton(), nIgnoreColNbr );
    
    if ( mapTableColAttrs != null )
      m_afSelFlags = new boolean[ mapTableColAttrs.size() ];
    
    m_table = table;
  }
  

  protected void headerClicked( MouseEvent me, int nColNbr, String strHeaderName )
  {
    if ( m_afSelFlags == null )
      return;

    for ( int x = 0; x < m_afSelFlags.length; x++ )
    {
      if ( x != nColNbr )
        m_afSelFlags[ x ] = false;
    }
    
    m_afSelFlags[ nColNbr ] = true;
    
    fireHdrClickedEvent( me, m_table, strHeaderName  );
  }
  
  public void setTableColAttrs( Map mapTableColAttrs, int nColCount )
  { 
    // the column count could be different from the map size if a column was selected
    // more than one once in the select list
    super.setTableColAttrs( mapTableColAttrs, nColCount );
    m_afSelFlags = new boolean[ nColCount ];
    
    
  }
  
  public Component getTableCellRendererComponent( JTable tbl, Object obj, boolean fIsSelected,
      boolean fHasFocus, int nRow, int nCol )
  {

    Component compHeader = super.getTableCellRendererComponent( tbl, obj, fIsSelected, fHasFocus, nRow, nCol );
    
    
    ((AbstractButton)compHeader).setSelected( m_afSelFlags[ nCol ] );
    
    return compHeader;
  }
  
  
} // VwToggleButtonTableHeaderRendereruttonTableHeaderRenderer() 
