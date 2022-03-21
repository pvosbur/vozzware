/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwRelativePosLayout.java

Create Date: Apr 11, 2003
============================================================================================
*/
package com.vozzware.ui;

import com.vozzware.util.VwQueue;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.LayoutManager2;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VwRelativePosLayout implements LayoutManager2
{

  private List<Component> m_listComponentSet = new ArrayList<Component>();

  private List<List<Component>> m_listCompRows = new ArrayList<List<Component>>();

  private Map<Component, VwRelativePosConstraints> m_mapContraints = new HashMap<Component, VwRelativePosConstraints>();

  private Map<Integer, VwQueue<ResizeControl>> m_mapWidthQueuesByRow = new HashMap<Integer, VwQueue<ResizeControl>>();

  private Map<Integer, Map<Component, ResizeControl>> m_mapWidthResizeByRow = new HashMap<Integer, Map<Component, ResizeControl>>();
  private Map<Integer, Map<Component, ResizeControl>> m_mapHeightResizeByRow = new HashMap<Integer, Map<Component, ResizeControl>>();

  private Map<Integer, List<ResizeControl>> m_mapWidthResizeableCompsByRow = new HashMap<Integer, List<ResizeControl>>();

  private Map<Integer, List<ResizeControl>> m_mapHeightResizeableCompsByRow = new HashMap<Integer, List<ResizeControl>>();

  private VwQueue<Integer> m_qRowResizeQ = new VwQueue<Integer>();

  private Map<Integer, Integer> m_mapLastRemainderWidth = new HashMap<Integer, Integer>();

  private Map<Integer, Integer> m_mapResizableRowNbrs = new HashMap<Integer, Integer>();
 
  private Map<Container,Boolean> m_mapInitializedContainers = new HashMap<Container, Boolean>();
  
  private boolean m_fLayoutDirty = false;

  private Dimension m_dimContainer = new Dimension( 0, 0 );
  private Dimension m_dimInitialSize = null;

  private Dimension m_dimPrevContainer = null;

  private int m_nRowsToResize = -1;

  private int m_nLastRowRemainder = -1;

  private boolean m_fHasResizableWidths = false;
  private boolean m_fHasResizableHeights = false;
  private boolean m_fIsRelative = false;
  
  
  class ResizeControl
  {

    ResizeControl( Component comp )
    {
      m_comp = comp;
    }

    Component m_comp;

    int m_nResizeAmt;

    public String toString()
    {
      return m_comp.getName() + " amt:" + m_nResizeAmt;
    }
  }

  public VwRelativePosLayout()
  { 
    return;
  }
  
  
  public VwRelativePosLayout( Dimension dimInitialSize )
  {
    m_dimInitialSize = dimInitialSize;
    
  }
  
  public Dimension getInitialSize()
  { return m_dimInitialSize; }
  
  /**
   * Adds new component to layout
   */
  public void addLayoutComponent( Component comp, Object objContstraints )
  {
    VwRelativePosConstraints rpc = null;

    if (objContstraints == null)
      rpc = new VwRelativePosConstraints(); // we can use a default if not
                                              // specified
    else
      if (!(objContstraints instanceof VwRelativePosConstraints))
        throw new RuntimeException( "Constraints object must be instance of VwRelativePosConstraints" );
      else
        rpc = (VwRelativePosConstraints)((VwRelativePosConstraints)objContstraints).clone();

    m_mapContraints.put( comp, rpc );

    if (m_listComponentSet.contains( comp ))
      throw new RuntimeException( "Component " + comp.toString() + " already exists" );

    m_listComponentSet.add( comp );

    if ( rpc.isRelative() )
    {
      if ( rpc.getRowNbr() < 0 )
        throw new RuntimeException( "Component " + comp.getName() + " cannot have a negative row number when using relative positioning" );

      if ( rpc.getColNbr() < 0 )
        throw new RuntimeException( "Component " + comp.getName() + " cannot have a negative column number when using relative positioning" );

      m_fIsRelative = true;
    
    }
    
    m_fLayoutDirty = true;

  }

  public float getLayoutAlignmentX( Container arg0 )
  {
    return 0;
  }

  public float getLayoutAlignmentY( Container arg0 )
  {
    return 0;
  }

  public void invalidateLayout( Container arg0 )
  {
    return;

  }

  public Dimension maximumLayoutSize( Container arg0 )
  {
    return null;
  }

  public void addLayoutComponent( String arg0, Component arg1 )
  {
    throw new RuntimeException( "method addLayoutComponent( String name, Component comp ) is not supported" );
  }

  public void layoutContainer( Container panel )
  {

    try
    {
      // Make sure a prior call to to the preferredLayoutSize has be made, if not ignore this request
      if ( !m_mapInitializedContainers.containsKey( panel ) )
        return;
      
      doLayout( panel, false );
  
    }
    catch( Exception ex )
    {
      throw new RuntimeException( ex.toString() );
    }
    return;

  }

  public Dimension minimumLayoutSize( Container arg0 )
  {
    return null;
  }

  public Dimension preferredLayoutSize( Container panel )
  {
    if ( m_dimContainer == null )
      m_dimContainer = new Dimension();
    
    try
    {
      Boolean fInitialized = m_mapInitializedContainers.get(  panel );
      if ( fInitialized == null )
      {
        m_mapInitializedContainers.put( panel, true );
        doLayout( panel, true );
      }
  
      if ( m_dimInitialSize != null )
        m_dimContainer = m_dimInitialSize;
      
    }
    catch( Exception ex )
    {
      throw new RuntimeException( ex.toString() );

    }
    
    panel.setPreferredSize( m_dimContainer );
    return m_dimContainer;

  }

  public void removeLayoutComponent( Component comp )
  {
    m_mapContraints.remove( comp );
    m_listComponentSet.remove( comp );
    m_fLayoutDirty = true;

  }

  
  public boolean hasResizableWidths()
  { return m_fHasResizableWidths; }


  public boolean hasResizableHeights()
  { return m_fHasResizableHeights;  }


  /**
   * Layout the components for a container
   * @param panel The container to layout
   * @param fInit if true setup initial sizes based on the VwRelativePosConstraints
   */
  private void doLayout( Container panel, boolean fInit ) throws Exception
  {
    Dimension dimContainer = panel.getSize();
    
    int nDiffWidth = 0;
    int nDiffHeight = 0;

    if ( fInit )
      m_dimPrevContainer = m_dimContainer = dimContainer;
    else
    {
      nDiffWidth = dimContainer.width - m_dimPrevContainer.width;
      nDiffHeight = dimContainer.height - m_dimPrevContainer.height;

    }

    
    // Debug only System.out.print("w = " + nDiffWidth + ", h = " + nDiffHeight + "\n");
    if (!m_fLayoutDirty && !fInit )
    {
      if (nDiffWidth == 0 && nDiffHeight == 0)
        return; // nothing to do

      m_dimPrevContainer = dimContainer;
      
      positionComponents( panel, dimContainer, nDiffWidth, nDiffHeight, fInit );
      return;
    }

    if ( m_fIsRelative )
      sortRelativeComponents();
    else
      sortAbolutePosComponents();
    
    positionComponents( panel, dimContainer, nDiffWidth, nDiffHeight, fInit );
    
    m_dimPrevContainer = dimContainer;
    
    m_fLayoutDirty = false;
  }

  /**
   * Position/re-position components based on new size of the container
   * 
   * @param dimPanelSize
   * @param nDiffWidth
   * @param nDiffHeight
   */
  private void positionComponents( Container panel, Dimension dimPanelSize, int nDiffWidth, int nDiffHeight,
                                   boolean fInit )
  {

    int nRowNbr = -1;

    if (m_nRowsToResize < 0  )
    {

      for ( List<Component> listRowComponents : m_listCompRows )
      {
        ++nRowNbr;
        List<ResizeControl> listHeightResizableComp = new ArrayList<ResizeControl>();
        for ( Component comp : listRowComponents )
        {
          VwRelativePosConstraints rpc = m_mapContraints.get( comp );
          if (rpc.isResizeHeight())
            listHeightResizableComp.add( new ResizeControl( comp ) );
        }

        if (listHeightResizableComp.size() > 0)
        {
          if (m_nRowsToResize < 0)
            m_nRowsToResize = 1;
          else
            ++m_nRowsToResize;

          m_mapHeightResizeableCompsByRow.put( nRowNbr, listHeightResizableComp );
          m_mapHeightResizeByRow.put( nRowNbr, new HashMap<Component, ResizeControl>() );
          m_mapResizableRowNbrs.put(  nRowNbr, null );
          
        }

      } // end for()
      
    } // end if

    if ( nDiffHeight != 0 && m_nRowsToResize > 0 )
    {
       int nResizeHeightFactor = Math.abs( nDiffHeight ) / m_nRowsToResize;
       int  nHeightRemainder = Math.abs( nDiffHeight ) % m_nRowsToResize;
       
       if ( nDiffHeight < 0 )
       {
         nResizeHeightFactor *= -1;
         nHeightRemainder *= -1;
       }
       if ( nResizeHeightFactor == 0 )
         queueHeightControls( nDiffHeight );
       else
         resizeAllHeightControls( nResizeHeightFactor, nHeightRemainder );
 
    }
    
    nRowNbr = -1;
    for ( List<Component> listRowComponents : m_listCompRows )
    {
      ++nRowNbr;

      int nWidthCompsToResize = 0;

      int nResizeWidthFactor = 0;
      int nWidthRemainder = 0;

      VwQueue<ResizeControl> qResizeWidthQueue = m_mapWidthQueuesByRow.get( nRowNbr );
      if (qResizeWidthQueue == null)
      {
        qResizeWidthQueue = new VwQueue<ResizeControl>();
        m_mapWidthQueuesByRow.put( nRowNbr, qResizeWidthQueue );
      }

      Map<Component, ResizeControl> mapWidthResize = m_mapWidthResizeByRow.get( nRowNbr );
      if (mapWidthResize == null)
      {
        mapWidthResize = new HashMap<Component, ResizeControl>();
        m_mapWidthResizeByRow.put( nRowNbr, mapWidthResize );

      }

      List<ResizeControl> listWidthResizableComp = m_mapWidthResizeableCompsByRow.get( nRowNbr );

      if ( listWidthResizableComp == null)
      {
        listWidthResizableComp = new ArrayList<ResizeControl>();
        // build list of re-sizable components for this row
        for ( Component comp : listRowComponents )
        {
          VwRelativePosConstraints rpc = m_mapContraints.get( comp );
          if (rpc.isResizeWidth())
            listWidthResizableComp.add( new ResizeControl( comp ) );
        }

        m_mapWidthResizeableCompsByRow.put( nRowNbr, listWidthResizableComp );

      }

 
      nWidthCompsToResize = listWidthResizableComp.size();

      if (nWidthCompsToResize > 0 )
      {
        nResizeWidthFactor = Math.abs( nDiffWidth ) / nWidthCompsToResize;
        nWidthRemainder = Math.abs( nDiffWidth ) % nWidthCompsToResize;

        if (nDiffWidth < 0)
        {
          nResizeWidthFactor *= -1;
          nWidthRemainder *= -1;
        }


      }

      if (nDiffWidth != 0)
      {
        if (nResizeWidthFactor == 0)
          queueWidthControls( listWidthResizableComp, qResizeWidthQueue, mapWidthResize, nDiffWidth );
        else
          resizedAllWidthControls( listWidthResizableComp, mapWidthResize, nResizeWidthFactor,
                                   nWidthRemainder, nRowNbr );

      }

      Map<Component, ResizeControl>mapResizeHeightControls = m_mapHeightResizeByRow.get(  nRowNbr );
      
      // Reposition components in this row
      for ( Component comp : listRowComponents )
      {

        Dimension dimSize = null;
        VwRelativePosConstraints rpc = m_mapContraints.get( comp );

        if (fInit)
          dimSize = setupComponent( panel, comp, rpc, nRowNbr );
         else
          dimSize = comp.getSize();

        Component compTop = rpc.getCompTop();
        Component compLeft = rpc.getCompLeft();

        // compute x,y positions based on the relative widths and heights
        int nXPos = 0;
        int nYPos = 0;
          
        if (compTop != null)
          nYPos = compTop.getY() + compTop.getHeight() + rpc.getRelY();
        else
          nYPos = rpc.getRelY();
        
        if (compLeft != null)
          nXPos = compLeft.getX() + compLeft.getWidth() + rpc.getRelX();
        else
          nXPos = rpc.getRelX();
        
        if (nDiffWidth != 0 && mapWidthResize.containsKey( comp ))
        {
          ResizeControl rc = mapWidthResize.get( comp );

          if ( !fInit )
            dimSize.width += rc.m_nResizeAmt;


        } // end if ( nReSizeFactor )

        if (nDiffHeight != 0 && mapResizeHeightControls != null && mapResizeHeightControls.containsKey( comp ))
        {
          ResizeControl rc = mapResizeHeightControls.get( comp );

          if ( !fInit )
          {
            dimSize.height += rc.m_nResizeAmt;
          }

        } // end if ( nReSizeFactor )

        comp.setSize( dimSize );

        comp.setLocation( nXPos, nYPos );

      } // end for

    } // end for

  } // end positionComponents()
  

  /**
   * Sets up the components size based on preferred size in characters (if specified) and minimum widths and heights
   * (in characters)
   * @param panel The panel container
   * @param comp The component to adjust
   * @param rpc The relative position constraints for the specified component
   * @return
   */
  private Dimension setupComponent( Container panel, Component comp, VwRelativePosConstraints rpc, int nRowNbr )
  {
    Font fontComp = comp.getFont();
    if (fontComp == null)
      fontComp = panel.getFont();

    FontMetrics fm = comp.getFontMetrics( fontComp );
    rpc.setCharWidth( fm.charWidth( 'W' ) );
    rpc.setCharHeight( fm.getHeight() );

    Dimension dimSizePref = comp.getPreferredSize();
    Dimension dimSize = comp.getSize();
    Dimension dimOrigSize = (Dimension)dimSize.clone();
    
    if ( rpc.isUsePreferredSize() )
      dimSize = dimSizePref;
    else
    {
      if ( dimSizePref.width > dimSize.width )
        dimSize.width = dimSizePref.width;
      
      if ( dimSizePref.height > dimSize.height )
        dimSize.height = dimSizePref.height;
      
      if (rpc.getPrefChars() > 0)
        dimSize.width = rpc.getPrefChars() * rpc.getCharWidth();
  
      if (rpc.getPrefHeightChars() > 0)
        dimSize.height = rpc.getPrefHeightChars() * rpc.getCharWidth();
  
      if ( dimSize.height < dimOrigSize.height )
        dimSize.height = dimOrigSize.height;
  
      if ( dimSize.width < dimOrigSize.width )
        dimSize.width = dimOrigSize.width;
  
      if (rpc.getMinChars() > 0)
      {
        int nMinCharWidth = rpc.getMinChars() * rpc.getCharWidth();
  
        if (dimSize.width < nMinCharWidth)
          dimSize.width = nMinCharWidth;
  
      }
  
      if (rpc.getMinHeightChars() > 0)
      {
        int nMinCharHeight = rpc.getMinHeightChars() * rpc.getCharHeight();
  
        if (dimSize.height < nMinCharHeight)
          dimSize.height = nMinCharHeight;
  
      }

    }
    if ( m_fIsRelative )
    {
      if ( nRowNbr == 0 )
        rpc.m_compTop = null;
      else
      {
        Component compTop = findComponentAbove(nRowNbr - 1, comp );
        if ( compTop == null )
          compTop = m_listCompRows.get( nRowNbr - 1).get(  0 );
        rpc.m_compTop = compTop;
        
      }
    }
    return dimSize;

  } // end setupComponent()

  /**
   * Queue up controls that can't be resized do to the new panel size difference
   * is < the nbr of controls that need resizing Any components currently in the
   * queue will be made eligible for resize based on the nDiffWidth amount
   * 
   * @param listResizeComps
   *          The list of components for a row that need resizing
   * @param qResizeWidthQueue
   *          The queue of components waiting to be resized
   * @param mapResize
   *          Map of components eligible for resize
   * @param nDiffWidth
   *          The total amt in pixels for growth or shrinkage
   */
  private void queueWidthControls( List<ResizeControl> listResizeComps, VwQueue<ResizeControl> qResizeWidthQueue,
      Map<Component, ResizeControl> mapResize, int nDiffWidth )
  {
    int absDiffWidth = Math.abs( nDiffWidth );

    int nQueueSize = qResizeWidthQueue.size();
    int nNbrToResize = listResizeComps.size();

    mapResize.clear();

    ResizeControl rcLastQueue = null;
    int x = 0;
    for ( ; x < absDiffWidth; x++ )
    {
      if (x >= nQueueSize)
        break;

      ResizeControl rc = qResizeWidthQueue.getNext();
      rc.m_nResizeAmt = (nDiffWidth < 0) ? -1 : 1;
      mapResize.put( rc.m_comp, rc );
      rcLastQueue = rc;

    }

    int nStart = 0;

    if (rcLastQueue == null)
      nStart = 0;
    else
      nStart = listResizeComps.indexOf( rcLastQueue ) + 1;

    if (nStart == listResizeComps.size())
      nStart = 0;

    // Add controls that can't be resized to the queue
    for ( int y = nStart; y < nNbrToResize; y++ )
    {
      ResizeControl rc = listResizeComps.get( y );
      if (x++ < absDiffWidth)
      {
        rc.m_nResizeAmt = (nDiffWidth < 0) ? -1 : 1;
        mapResize.put( rc.m_comp, rc );

      }
      // controls can't be resized yet, put in queue
      if (!mapResize.containsKey( rc.m_comp ))
      {
        if (!qResizeWidthQueue.contains( rc ))
          qResizeWidthQueue.add( rc );
      }

    }

    for ( int y = 0; y < nStart; y++ )
    {
      ResizeControl rc = listResizeComps.get( y );
      if (x++ < absDiffWidth)
      {
        rc.m_nResizeAmt = (nDiffWidth < 0) ? -1 : 1;
        mapResize.put( rc.m_comp, rc );

      }
      // controls can't be resized yet, put in queue
      if (!mapResize.containsKey( rc.m_comp ))
      {
        if (!qResizeWidthQueue.contains( rc ))
          qResizeWidthQueue.add( rc );
      }

    } // end for()

  } // end queueWidthControls(()

  
  /**
   * Queues up row numbers that can't be adjusted due to the height pixel difference < the number of rows
   * @param nDiffHeight The ehight difference from the last panel size
   */
  private void queueHeightControls( int nDiffHeight )
  {
    int absDiffHeight = Math.abs( nDiffHeight );

    Integer intLastRowAcllocated = null;
    
    int nRowsResized = 0;
    for( ; nRowsResized < absDiffHeight; nRowsResized++ )
    {
      Integer intRowNbr = m_qRowResizeQ.getNext();
      if ( intRowNbr == null )
        break;
      
      intLastRowAcllocated = intRowNbr;
      
      Map<Component, ResizeControl> mapResize = m_mapHeightResizeByRow.get( intRowNbr );
      mapResize.clear();
      
      List<ResizeControl> listResizeComps = m_mapHeightResizeableCompsByRow.get( intRowNbr );
      // Debug only System.out.println( "Resizing row: " + intRowNbr + " by 1");
      for ( ResizeControl rc : listResizeComps )
      {
        rc.m_nResizeAmt = (nDiffHeight < 0 )? -1 : 1;
        mapResize.put(  rc.m_comp, rc );
      } // end for
       
    } // end for

    int nNbrRows = m_listCompRows.size();
    Integer intStartRowNbr = null;
    
    if ( intLastRowAcllocated == null )
      intStartRowNbr = 0;
    else
      intStartRowNbr = intLastRowAcllocated + 1;
    
    
    for ( int y = intStartRowNbr;;  y++ )
    {
      
      if ( y >= nNbrRows )
      {
        if ( intLastRowAcllocated == null )
          break;
        else
          y = 0;
      }
       
      if ( intLastRowAcllocated != null && y == intLastRowAcllocated )
        break;
      
      if ( !m_mapResizableRowNbrs.containsKey( y ))
        continue;
      
      if ( nRowsResized++ < absDiffHeight )
      {
 
        List<ResizeControl> listResizeComps = m_mapHeightResizeableCompsByRow.get( y );
       
        Map<Component, ResizeControl> mapResize = m_mapHeightResizeByRow.get( y );
        // Debug only -- System.out.println( "Resizing row: " + y + " by 1");

        for ( ResizeControl rc : listResizeComps )
        {
          rc.m_nResizeAmt = (nDiffHeight < 0 )? -1 : 1;
          mapResize.put(  rc.m_comp, rc );
        } // end for
        
       
      }
      else
      {
        
        if ( m_mapHeightResizeByRow.containsKey( y ))
        {
          if ( !m_qRowResizeQ.contains( y ))
          {
            Map<Component, ResizeControl> mapResize = m_mapHeightResizeByRow.get( y );
            mapResize.clear();
            m_qRowResizeQ.add( y );
          }
        }
      }
      
     } // end for()
    
  } // end queueHeightControls(()

  
  /**
   * Adjust all height resizeable components in all rows by the 
   * @param nResizeHeightFactor The amount of height adjustment
   * @param nRemainder The remainder
   */
  private void resizeAllHeightControls( int nResizeHeightFactor, int nRemainder )
  {
    int nAbsRemainder = Math.abs( nRemainder );

    int nNbrRows = m_listCompRows.size();
    
    for ( int nRowNbr = 0; nRowNbr < nNbrRows; ++nRowNbr )
    {
      List<ResizeControl>listComps = m_mapHeightResizeableCompsByRow.get( nRowNbr );
      if ( listComps == null )
        continue;
      
      Map<Component, ResizeControl>mapResize = m_mapHeightResizeByRow.get(  nRowNbr );
      mapResize.clear();
      
      for ( ResizeControl rc : listComps )
      {
        rc.m_nResizeAmt = nResizeHeightFactor;
        mapResize.put( rc.m_comp, rc );
      }
    }
    

    if (nRemainder != 0)
    {
      int nRowNbr = m_nLastRowRemainder;

      for ( int x = 0; x < nAbsRemainder; x++ )
      {

        if (++nRowNbr >= nNbrRows)
          nRowNbr = 0;

        List<ResizeControl>listComps = m_mapHeightResizeableCompsByRow.get( nRowNbr );
        if ( listComps == null )
          continue;
 
        m_nLastRowRemainder = nRowNbr;
        
        Map<Component, ResizeControl>mapResize = m_mapHeightResizeByRow.get(  nRowNbr );

        for ( ResizeControl rc : listComps )
        {
          rc.m_nResizeAmt += (nResizeHeightFactor < 0) ? -1 : 1;
          mapResize.put( rc.m_comp, rc );
        }
 
      } // end for()

    } // end if
    
  } // end resizeAllHeightControls()
  
  /**
   * Adjusts the widths of all components in a row and gives any remainder space evenly to components in a round robin
   * @param listResizeComps The list of components for a row that need widths readjusted
   * @param mapResize The map of components that get resized
   * @param nDiffWidth The 
   * @param nResizeAmt
   * @param nRemainder
   * @return
   */
  private void resizedAllWidthControls( List<ResizeControl> listResizeComps, Map<Component, ResizeControl> mapResize,
                                        int nResizeAmt, int nRemainder, int nRowNbr )
  {
    int nAbsRemainder = Math.abs( nRemainder );

    mapResize.clear();

    for ( ResizeControl rc : listResizeComps )
    {
      rc.m_nResizeAmt = nResizeAmt;
      mapResize.put( rc.m_comp, rc );
    }

    if (nRemainder != 0)
    {
      Integer noNextCompNbr = null;
      noNextCompNbr = m_mapLastRemainderWidth.get( nRowNbr );
      if (noNextCompNbr == null)
        noNextCompNbr = 0;

      if (noNextCompNbr >= listResizeComps.size())
        noNextCompNbr = 0;

      for ( int x = 0; x < nAbsRemainder; x++ )
      {

        ResizeControl rc = listResizeComps.get( noNextCompNbr );
        rc.m_nResizeAmt += ((nResizeAmt < 0) ? -1 : 1);
        mapResize.put( rc.m_comp, rc );

        if (++noNextCompNbr >= listResizeComps.size())
          noNextCompNbr = 0;

      } // end for()

      if (noNextCompNbr >= listResizeComps.size())
        noNextCompNbr = 0;

      m_mapLastRemainderWidth.put( nRowNbr, noNextCompNbr );

    } // end if

  } // end resizedAllWidthControls()

  /**
   * Sort components by their absolute x,y positions as specified in the component itself
   */
  private void sortAbolutePosComponents() throws Exception
  {
    if ( m_listComponentSet.size() == 0 )
      return;
    
    Collections.sort( m_listComponentSet, new Comparator<Component>()
    {
      public int compare( Component comp1, Component comp2 )
      {

        if (comp1.getY() < comp2.getY())
          return -1;
        else
          if (comp1.getY() == comp2.getY())
            return 0;

        return 1;

      }

    });

    m_listCompRows = new ArrayList<List<Component>>();
    List<Component> listRow = new ArrayList<Component>();
    m_listCompRows.add( listRow );

    Component compPrev = m_listComponentSet.get( 0 );
    int nCurY = compPrev.getY();

    for ( Component comp : m_listComponentSet )
    {
      if (comp.getY() != nCurY)
      {
        if ( isWithinPriorCompsHeight( comp ))
          listRow.add( comp );
        else
        {
          listRow = new ArrayList<Component>();
          m_listCompRows.add( listRow );
          listRow.add( comp );

        }

      }
      else
        listRow.add( comp );

      nCurY = comp.getY();

      compPrev = comp;

    }

    // For each row list, sort by column location
    for ( List<Component> listCompRow : m_listCompRows )
    {
      Collections.sort( listCompRow, new Comparator<Component>()
      {
        public int compare( Component comp1, Component comp2 )
        {
          if (comp1.getX() < comp2.getX())
            return -1;
          else
          if (comp1.getX() == comp2.getX())
            return 0;  
          
          return 1;

        }

      } );

    }

    
    // For each row list, sort by column location
    for ( List<Component> listCompRow : m_listCompRows )
    {
      Collections.sort( listCompRow, new Comparator<Component>()
      {
        public int compare( Component comp1, Component comp2 )
        {
          if (comp1.getX() < comp2.getX())
            return -1;
          else
            return 1;  
          
 
        }

      } );

    }


    int nRows = m_listCompRows.size();

    // compute relative positions of each component
    // For each row list, sort by column location
    for ( int nRow = 0; nRow < nRows; nRow++ )
    {
      List<Component> listCompRow = m_listCompRows.get( nRow );

      for ( Component comp : listCompRow )
      {
        VwRelativePosConstraints rpc = m_mapContraints.get( comp );
        
        Component compLeft = findLeftComponent( listCompRow, comp );
        rpc.m_compLeft = compLeft;

        if (compLeft != null)
          rpc.setRelX( comp.getX() - (compLeft.getX() + compLeft.getWidth()) );
         else
          rpc.setRelX( comp.getX() );

        if ( compLeft != null && rpc.getRelX() < 0 && Math.abs( rpc.getRelX()) > 6  )
           throw new Exception( "Components " + compLeft.getName() + " and " + comp.getName() + " overlap");
        
        Component compTop = null;

        if (nRow > 0)
        {
          compTop = findComponentAbove(  nRow - 1, comp );
          rpc.m_compTop = null;
          if (compTop != null)
          {
            rpc.setRelY( comp.getY() - (compTop.getY() + compTop.getHeight()) );
            rpc.m_compTop =  compTop;
          }
          else
            rpc.setRelY( comp.getY() );
        }
        else
        {
          compTop = findComponentAbove( 0, comp );
          rpc.m_compTop = compTop;

          if (compTop != null)
            rpc.setRelY( comp.getY() - (compTop.getY() + compTop.getHeight() ) );
          else
            rpc.setRelY( comp.getY() );
        }
        
        if ( compTop != null && rpc.getRelY() < 0 && Math.abs( rpc.getRelY() ) > 6 )
          throw new Exception( "Components " + compTop.getName() + " and " + comp.getName() + " overlap");
 
      }
    }

  } // end sortAbolutePosComponents()

  
  /**
   * Test to see if this component is within a prior component's starting position and height
   * @param compTest The component to test
   * @return
   */
  private boolean isWithinPriorCompsHeight( Component compTest )
  {
    int nTestYPos = compTest.getY();
    int nTestHeight = compTest.getHeight();
    int nTestEndPos = nTestYPos + nTestHeight;
    
    for ( Component comp : m_listComponentSet )
    {
      int nYPos = comp.getY();
      int nEndPos = nYPos + comp.getHeight();
      
      if ( comp == compTest )
        return false;
      
      if ( nYPos > nTestEndPos || ( nYPos + comp.getHeight()) < nTestYPos )
        continue;       // Components from here are all below compTest
      
     
      Rectangle rctComp = new Rectangle( comp.getX(), 1, comp.getWidth(), 1 );
      Rectangle rctCompTest = new Rectangle( compTest.getX(), 1, compTest.getWidth(), 1 );
      
      if ( rctComp.intersects( rctCompTest ) || rctCompTest.intersects( rctComp) )
        continue;
      
      if ( nTestYPos >= nYPos && nTestYPos <= nEndPos )
        return true;
      
    } // end for()
    
    return false;
  }


  /**
   * Try to find the component in the row above the specified component. 
   * @param listCompRow The list of components in the row above the specified component
   * @param compCurrent The specified component
   * @return
   */
  private Component findComponentAbove( int nStartRowNbr, Component compCurrent )
  {
    
    for ( int x = nStartRowNbr; x >=0; x--)
    {
      List<Component> listCompRow = m_listCompRows.get(  x  );
      
      List<Component>listHeightResizeComponents = new ArrayList<Component>();
      
      for ( Component comp : listCompRow )
      {
        VwRelativePosConstraints rpc = m_mapContraints.get( comp );
        if ( rpc.isResizeHeight() )
          listHeightResizeComponents.add( comp );
      }
      
      // if we have any height resizeable components above this component, and any one of them is in the bounds
      // of (overlaps) this component, then return the resizeable component
      for ( Component comp : listHeightResizeComponents )
      {
        if ( comp == compCurrent )
          continue;
        
        if ( comp.getX()  >= compCurrent.getX() && comp.getX() <= (compCurrent.getX() + compCurrent.getWidth()))
          return comp;
        
      }
      
      // otherwise look for the first component in the row that in the x bounds of the compCurrent 
      for ( Component comp : listCompRow )
      {
        if ( comp == compCurrent )
          continue;
        
        int nXPos = comp.getX();
  
        if (nXPos + comp.getWidth() < compCurrent.getX() || nXPos > compCurrent.getX() + compCurrent.getWidth())
          continue;
  
        if ( comp.getY() > compCurrent.getY() )
          continue;
        
        return comp;
  
      }

    } // end for
    
    return null;
    
  } // end findComponentAbove()


  private Component findLeftComponent( List<Component> listCompRow, Component compTest )
  {
    int nCompPos = listCompRow.indexOf( compTest );
    
    for ( int x = nCompPos - 1; x >= 0; x-- )
    {
      Component comp = listCompRow.get( x );
      
      if ( compTest.getY() >= comp.getY() + comp.getHeight() )
        continue;
 
      if ( compTest.getY() + compTest.getHeight() < comp.getY() )
        continue;
      
       return comp;

    }
    
    return null;
    
  }
  
  /**
   * Sort components by their row and column positions
   */
  private void sortRelativeComponents()
  {
    
    // First sort by row number
    Collections.sort( m_listComponentSet, new Comparator<Component>()
    {
      public int compare( Component comp1, Component comp2 )
      {
        VwRelativePosConstraints rpc1 = m_mapContraints.get( comp1 );
        VwRelativePosConstraints rpc2 = m_mapContraints.get( comp2);
        
        if (rpc1.getRowNbr() < rpc2.getRowNbr() )
          return -1;
        else
        if (rpc1.getRowNbr() == rpc2.getRowNbr() )
          return 0;

        return 1;

      }

    });

    m_listCompRows = new ArrayList<List<Component>>();
    List<Component> listRow = new ArrayList<Component>();
    m_listCompRows.add( listRow );
    VwRelativePosConstraints rpcPrev = m_mapContraints.get(  m_listComponentSet.get( 0 ) );
    
    // make a component List for each row
    for ( Component comp : m_listComponentSet )
    {
      VwRelativePosConstraints rpcComp = m_mapContraints.get( comp );
     
      if ( rpcComp.getRowNbr() != rpcPrev.getRowNbr() )
      {
        listRow = new ArrayList<Component>();
        m_listCompRows.add( listRow );
        listRow.add( comp );
      }
      else
        listRow.add( comp );
      
      rpcPrev = m_mapContraints.get(  comp );
      
      
    } // end for()
    
    // sort each row by column nbr.
    for ( List<Component> listCompRow : m_listCompRows )
    {
      Collections.sort( listCompRow, new Comparator<Component>()
      {
        public int compare( Component comp1, Component comp2 )
        {
          VwRelativePosConstraints rpc1 = m_mapContraints.get( comp1 );
          VwRelativePosConstraints rpc2 = m_mapContraints.get( comp2);
          
          if (rpc1.getColNbr() < rpc2.getColNbr() )
            return -1;
          else
          if (rpc1.getColNbr() == rpc2.getColNbr())
            return 0;

          return 1;

        }

      });
      
    }

    
    // assign the left side component for each row
    for ( List<Component> listCompRow : m_listCompRows )
    {
      Component compPrev = listCompRow.get( 0 );
      for ( Component comp : listCompRow )
      {
        VwRelativePosConstraints rpc = m_mapContraints.get( comp );
        if ( comp != compPrev )
          rpc.m_compLeft = compPrev;
        else
          rpc.m_compLeft = null;
        
        compPrev = comp;
      } // end for()
        
    } // end for()
      
  } // end sortRelativeComponents()
  
  
} // end class VwRelativePosLayout{}

// end of VwRelativePosLayout.java ***
