/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwContentProducer.java

============================================================================================
*/

package com.vozzware.components;

import java.awt.Component;

/**
 * The implementor of this interface provides a view of the available content to view or
 * modify. <br>This could be a JTree, JToolbar, JList or any type of JComponent that displays the objects
 * that can be selected by the user. <br>
 * When an object is selected, the implementor sends an VwContentSelectedEvent to all registered listeners.
 */
public interface IVwContentProducer
{
  /**
   * Installs the component that displays the available content
   *
   * @param compProducer A component that displays available content for  registered
   * content viewers.
   */
  public void setProducer( Component compProducer );

  /**
   * Register a content viewer for content selection changes
   *
   * @param listener The IITcContentViewer
   */
  public void addContentSelectionListener( IVwContentSelectionListener listener );

  /**
   * Unregister a content viewer
   *
   * @param listener The content viewer
   */
  public void removeContentSelectionListener( IVwContentSelectionListener listener );

} // End interface IVwContentProducer{}

// *** End of IVwContentProducer.java ***

