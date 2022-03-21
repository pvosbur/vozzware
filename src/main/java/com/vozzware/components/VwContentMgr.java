/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwContentMgr.java

============================================================================================
*/

package com.vozzware.components;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import java.awt.BorderLayout;
import java.awt.Component;

/**
 * This class provides a shell to manage IVwContentProducer's and IVwContentSelectionListener's (viewers)
 *
 *
 *
 */
public class VwContentMgr extends JPanel
{
   private IVwContentProducer  m_producer;             // The producer component
   private IVwContentSelectionListener  m_selListener; // The selection Listener component


  /**
   * Contructs a standard explorer type view with an IITcContentProducer in the left pane
   * and an IVwContentSelectionListener in the right pane
   *
   */
  public VwContentMgr( IVwContentProducer producer, IVwContentSelectionListener listener ) throws
     VwInvalidContentProducerException, VwInvalidContentSelectionListenerException
  {
    if ( ! (producer instanceof Component ) )
      throw new VwInvalidContentProducerException();

    if ( ! (listener instanceof Component ) )
        throw new VwInvalidContentSelectionListenerException();

    m_producer = producer;
    m_selListener = listener;

    JSplitPane splitter = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT );
    JScrollPane producerScroller = new JScrollPane( (Component)producer );
    producer.addContentSelectionListener( listener );
    splitter.setLeftComponent( producerScroller );
    splitter.setRightComponent( (Component)listener );
    splitter.setDividerLocation( 150 );

    this.setLayout( new BorderLayout() );

    this.add( splitter, BorderLayout.CENTER );


  }  // end VwContentMgr


  /**
   *
   * @return The content producer object
   */
  public IVwContentProducer getProducer()
  {
    return m_producer;
  }


  /**
   *
   * @return  The content selection listener
   */
  public IVwContentSelectionListener getListener()
  {
    return m_selListener;
  }

} // End class VwContentMgr{}

// *** End of VwContentMgr.java ***

