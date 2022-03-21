/*
===========================================================================================

 
                               Copyright(c) 2000 - 2006 by

                      V o z z W a r e   L L C (Vw)

                                 All Rights Reserved

Source Name: VwHeadrClickRenderer.java

Create Date: May 18, 2006

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.


============================================================================================
*/
package com.vozzware.ui;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author petervosburghjr
 *
 */
public abstract class VwTableHeadrClickRenderer  extends VwDefaultTableHeaderRenderer
{
  private List<VwTableHdrClickListener>          m_listClickListeners = new ArrayList<VwTableHdrClickListener>();
  
  private JTable        m_table;
  
  private int           m_nIgnoreColumn = -1;
  
  private JTableHeader  m_tblHdr;
  
  private boolean       m_fNeedListeners = true;
  
  public VwTableHeadrClickRenderer( JTable table, Map<String,VwTableColAttr> mapVwTableColAttrs, JComponent compHeader, int nIgnoreColumn )
  { 
    super( table, mapVwTableColAttrs, compHeader );
    
    m_table = table;
    m_tblHdr = m_table.getTableHeader();
    m_nIgnoreColumn = nIgnoreColumn;
    if ( m_fNeedListeners )
      installMouseListener();
    
    m_fNeedListeners = false;
    
    
  }
  

  /**
   * Adds a table header clicked listener
   * @param hdrClickListener the listener to add
   */
  public void addHeaderClickListener( VwTableHdrClickListener hdrClickListener )
  { m_listClickListeners.add( hdrClickListener ); }
  
  /**
   * Remove a table header clicked listener
   * @param hdrClickListener the listener to remove
   */
  public void removeHeaderClickListener( VwTableHdrClickListener hdrClickListener )
  { m_listClickListeners.remove( hdrClickListener ); }
  
  
  /**
   * Default handler if not overridden
   * @param strHeaderName The name of the header column clicked
   */
  protected void headerClicked( MouseEvent me,  int nColNbr, String strHeaderName )
  {
    try
    {
      m_tblHdr.setCursor( Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR ) );
      fireHdrClickedEvent( me, m_table, strHeaderName  );
    }
    finally
    {
      m_tblHdr.setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ) );
      
    }
  }
  
  
  /**
   * fire the table header clicked event
   * @param objSource The jtable source object
   * @param strHdrValue the header text value
   */
  public void fireHdrClickedEvent( MouseEvent me, Object objSource, String strHdrValue )
  {
    VwTableHeaderClickedEvent ce = new VwTableHeaderClickedEvent( me, objSource, strHdrValue );
    
    for ( Iterator iListeners = m_listClickListeners.iterator(); iListeners.hasNext(); )
    {
      VwTableHdrClickListener cl = (VwTableHdrClickListener)iListeners.next();
      cl.headerClicked( ce );
      
    } // end for()
    
  } // end fireHdrClickedEvent()
  
  /**
   * Install mouse listener to detect header click events
   *
   */
  private void installMouseListener()
  {
    
    final JTableHeader thdr = m_table.getTableHeader();
    
    
    thdr.addMouseListener( new MouseAdapter()
    {
      public void mousePressed( MouseEvent me )
      {
        if ( me.isPopupTrigger() )
          tableHeaderPopupTrigger( me );
         
      }

      public void mouseReleased( MouseEvent me )
      {
        if ( me.isPopupTrigger() )
          tableHeaderPopupTrigger( me );
         
      }
      
    
    });
  }
  
  private void tableHeaderPopupTrigger(MouseEvent me)
  {
    me.consume();
    
    int nCol = m_table.columnAtPoint( me.getPoint() );
    
    if ( nCol < 0 || nCol == m_nIgnoreColumn )
      return;
    
    final String strHeaderText = m_tblHdr.getColumnModel().getColumn( nCol ).getHeaderValue().toString();
    headerClicked( me, nCol, strHeaderText );
  }

  
} // end class VwHeadrClickRenderer{}

// *** End of VwHeadrClickRenderer
