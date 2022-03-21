/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwTree.java

Create Date: Apr 11, 2003
============================================================================================
*/

package com.vozzware.ui;

import com.vozzware.components.IVwContentProducer;
import com.vozzware.components.IVwContentSelectionListener;
import com.vozzware.components.VwContentProducerHelper;
import com.vozzware.components.VwContentSelectedEvent;
import com.vozzware.xml.VwDataObject;

import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.Component;
import java.awt.datatransfer.StringSelection;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * This class extends the Swing JTree control.  Its primary purpose is to make the data aware
 * in a 3 tier environment.  The VwTree talks to the Opera Server and uses the Opera
 * Services to get its data.
 */
public class VwTree extends JTree  implements IVwContentProducer,
                                                DropTargetListener,
                                                DragSourceListener,
                                                DragGestureListener

{
  private VwContentProducerHelper  m_contentHelper = new VwContentProducerHelper();

  private boolean                   m_fContentProducerEnabled;

  private String                    m_strService;     // Name of service to execute

  private String                    m_strParamNames;  // Column param names

  private String                    m_strParamValues; // Column param values

  private VwDataObject             m_dataObj;        // VwDataObject for comuncation to the appserver


  private boolean                   m_fLoaded = false;// Used only for the IDE bean interface

  private DropTarget                m_dropTarget;     // Drop Target interface
  private DragSource                m_dragSource;     // Drag Source interface


  /**
   * Constructs the Tree
   *
   */
  public VwTree()
  {
    super();
    m_dataObj = null;

    setup();

  }

  /**
   * Standard initialzation
   */
  private void setup()
  {

    // Register standard listener events

    this.addTreeExpansionListener( new TreeExpansionListener()
    {
      public void  treeCollapsed( TreeExpansionEvent e )
      {
        TreePath tp = e.getPath();

        if ( tp == null )
          return;

      }

      public void treeExpanded( TreeExpansionEvent e )
      {
        TreePath tp = e.getPath();

        if ( tp == null )
          return;

      }
    } );

    addTreeSelectionListener( new TreeSelectionListener()
    {
      public void  valueChanged( TreeSelectionEvent e )
      {

        TreePath tp = e.getNewLeadSelectionPath();

        if ( tp == null )
          return;

        if ( m_fContentProducerEnabled )
        {
          Object objNode = tp.getLastPathComponent();
          if ( objNode instanceof DefaultMutableTreeNode )
             m_contentHelper.fireContentSelectedEvent( new VwContentSelectedEvent( this, ((DefaultMutableTreeNode)objNode).getUserObject() ) );
        }

      } // end value changed

    } );

    this.addTreeWillExpandListener( new TreeWillExpandListener()
    {
      public void  treeWillCollapse( TreeExpansionEvent e )
      {
        TreePath tp = e.getPath();
        if ( tp == null )
          return;

      }

      public void treeWillExpand( TreeExpansionEvent e )
      {
        TreePath tp = e.getPath();

        if ( tp == null )
          return;

      }
    } );


    addMouseListener( new MouseAdapter()
    {
      public void mouseClicked( MouseEvent me )
      {
        // Get treepath based on mouse Coordinates
        TreePath tp = getPathForLocation( me.getX(), me.getY() );

        if ( tp == null )
          return;

        // Get the tree node for this path
        DefaultMutableTreeNode tn = (DefaultMutableTreeNode)tp.getLastPathComponent();

        if ( me.getClickCount() > 1 )
          ;

      } // end mouseClicked();


      public void mouseReleased( MouseEvent me )
      {
        // Get treepath based on mouse Coordinates
        TreePath tp = getPathForLocation( me.getX(), me.getY() );

        DefaultMutableTreeNode tn = null;

        // Get the tree node for this path
        if ( tp != null )
          tn = (DefaultMutableTreeNode)tp.getLastPathComponent();


        if ( me.isPopupTrigger() )
        {
          ;
        }
      }
    });

  } // end init()


  public void setContentProducerEnabled( boolean fContentProducerEnabled )
  { m_fContentProducerEnabled = fContentProducerEnabled; }


  /**
   * Sets the comma delimited string of parameter names used to setup the service
   * parameters required to execute a Service.
   *
   * @param strParamName - The comma delimited string of parameter names
   */
  public final void setParamNames( String strParamNames )
  { m_strParamNames = strParamNames; }


  /**
   * Gets the comma delimited string of parameter names used to setup the service parameters required
   * to execute a service
   *
   * @return - The comma separated string of parameter names
   */
  public final String getParamNames()
  { return m_strParamNames; }


  /**
   * Sets the set of parameter values corrresponding to the parameter names
   *
   * @param strParamValues - A comma delimited string of parameter values
   */
  public final void setParamValues( String strParamValues )
  { m_strParamValues = strParamValues; }


  /**
   * Gets the set of parameter values corrresponding to the parameter names
   *
   * @return - A comma delimited string of parameter values
   */
  public final String getParamValues()
  { return m_strParamValues; }


  /**
   * Sets the name of the Service to execute when the setLoadGridFromService()
   * method is called.
   *
   * @param strService - The name of the service to execute
   */
  public final void setService( String strService )
  { m_strService = strService; }


  /**
   * Gets the cuurent Service name setting that will be executed when the
   * setLoadGridFromService() method is called.
   *
   * @return - The name of the currently defined service, or null if the service
   * is not defined.
   */
  public final String getService()
  { return m_strService; }

  // *** Content producer methods

  /**
   * Installs the component that displays the available content
   *
   * @param compProducer A component that displays available content for  registered
   * content viewers.
   */
  public void setProducer( Component compProducer )
  { m_contentHelper.setProducer( compProducer ); }

  /**
   * Register a content viewer for content selection changes
   *
   * @param listener The IITcContentViewer
   */
  public void addContentSelectionListener( IVwContentSelectionListener listener )
  { m_contentHelper.addContentSelectionListener( listener ); }

  /**
   * Unregister a content viewer
   *
   * @param listener The content viewer
   */
  public void removeContentSelectionListener( IVwContentSelectionListener listener )
  { m_contentHelper.removeContentSelectionListener( listener ); }



  // *** Drag N Drop events ****

  /**
   * is invoked when the user changes the dropAction
   *
   */
  public void dropActionChanged( DragSourceDragEvent event )
  {
    ;
  }


  /**
   * is invoked if the use modifies the current drop gesture
   *
   */
  public void dropActionChanged ( DropTargetDragEvent event )
  { ; }


  public void dragExit( DropTargetEvent event )
  { ; }

  public void dragOver( DropTargetDragEvent event )
  {

  }

  public void dragEnter( DropTargetDragEvent event )
  {
  }

  public void drop( DropTargetDropEvent event )
  {
    try
    {
      boolean fAcceptedDrop = false;
      event.acceptDrop( DnDConstants.ACTION_LINK );
      event.dropComplete( fAcceptedDrop );
    }
    catch( Exception ex )
    {}

  } // end drop()



  /**
   * a drag gesture has been initiated
   *
   */

  public void dragGestureRecognized( DragGestureEvent event )
  {
    TreePath[] atp = getSelectionPaths();

    if ( atp == null )
      return;           // Seletion not registered yet


    // as the name suggests, starts the dragging
    m_dragSource.startDrag( event, DragSource.DefaultCopyDrop,
                             new StringSelection( "XMLEDIT" ), this );

  }

  /**
   * this message goes to DragSourceListener, informing it that the dragging
   * has ended
   *
   */

  public void dragDropEnd( DragSourceDropEvent event )
  {
    return ;
  }

  /**
   * this message goes to DragSourceListener, informing it that the dragging
   * has entered the DropSite
   *
   */

  public void dragEnter( DragSourceDragEvent event )
  {

  }

  /**
   * this message goes to DragSourceListener, informing it that the dragging
   * has exited the DropSite
   *
   */
  public void dragExit( DragSourceEvent event )
  {
    ;

  }

  /**
   * this message goes to DragSourceListener, informing it that the dragging is currently
   * ocurring over the DropSite
   *
   */
  public void dragOver( DragSourceDragEvent event )
  {;}


} // end class VwTree{}

// *** End of VwTree.java ***

