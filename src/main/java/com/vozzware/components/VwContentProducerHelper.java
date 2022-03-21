/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwContentProducerHelper.java

============================================================================================
*/

package com.vozzware.components;

import java.awt.Component;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * The implementor of this interface provides a view of the available content to view or
 * modify. <br>This could be a JTree, JToolBar, JList or any type of JComponent that displays the objects
 * that can be selected by the user. <br>
 * When an object is selected, the implementor sends an VwContentSelectedEvent to all registered listeners.
 */
public class VwContentProducerHelper  implements IVwContentProducer
{
  private LinkedList  m_listContentListeners = new LinkedList();

  private Component   m_compProducer; // The content producer component


  /**
   * Installs the component that displays the available content
   *
   * @param compProducer A component that displays available content for  registered
   * content viewers.
   */
  public void setProducer( Component compProducer )
  { m_compProducer = compProducer; }

  /**
   * Register a content viewer for content selection changes
   *
   * @param listener The IITcContentViewer
   */
  public synchronized void addContentSelectionListener( IVwContentSelectionListener listener )
  {  m_listContentListeners.add( listener );  }

  /**
   * Unregister a content viewer
   *
   * @param listener The content viewer
   */
  public synchronized void removeContentSelectionListener( IVwContentSelectionListener listener )
  { m_listContentListeners.remove( listener ); }


  /**
   * Fire contentSelected events to all registered listeners
   *
   * @param objSelection The selected content object
   */
  public synchronized void fireContentSelectedEvent( VwContentSelectedEvent event )
  {
    for ( Iterator iListeners = m_listContentListeners.iterator(); iListeners.hasNext(); )
    {
      IVwContentSelectionListener listener = (IVwContentSelectionListener)iListeners.next();
      listener.contentSelected( event );

    } // end fireContentSelectedEvent()

  }  // end fireContentSelectedEvent()


} // End class VwContentProducerHelper{}

// *** End of VwContentProducerHelper.java ***

