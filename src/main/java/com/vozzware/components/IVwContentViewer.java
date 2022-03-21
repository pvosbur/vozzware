/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: IVwContentViewer.java

============================================================================================
*/
package com.vozzware.components;


/**
 * The implementor of this interface provides the view of the data content and sends
 * contentChanged events to registerdd listeners
 */
public interface IVwContentViewer
{
  /**
   * Installs the component that displays the available content
   *
   * @param compProducer A component that displays available content for  registered
   * content viewers.
   */
  public void setContent( Object objContent );


  /**
   * Flush all content fromm view into its object representation and notify any registered
   * contentChangedListeners if any of the content was modified
   */
  public void flushContent();



  /**
   * Adds a contentChangedListener
   * @param contentChangedListener The contentChangedListener
   */
  public void addContentChangedListener( IVwContentChangedListener contentChangedListener );


  /**
   * Remove a contentChangedListener
   * @param contentChangedListener The contentChangedListener
   */
  public void removeContentChangedListener( IVwContentChangedListener contentChangedListener );


  /**
   * Send VwContentChangedEvent's to registered listeners
   * @param cce The content changed event
   */
  public void fireContentsChanged( VwContentChangedEvent cce );


} // End interface IVwContentProducer{}

// *** End of IVwContentProducer.java ***

