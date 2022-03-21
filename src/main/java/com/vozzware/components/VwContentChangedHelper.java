/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwContentChangedHelper.java

============================================================================================
*/

package com.vozzware.components;

import java.util.Iterator;
import java.util.LinkedList;


/**
 * This class manages the the registered VwContentChanged listeners. The class handles
 * the registration and removeal of listeners as well as the fireContentChanged events.
 */
public class VwContentChangedHelper
{
  private LinkedList   m_listListeners = new LinkedList(); // Registered contentChanged listeners


  /**
   * Adds a contentChangedListener
   * @param contentChangedListener The contentChangedListener
   */
  public synchronized void addContentChangedListener( IVwContentChangedListener contentChangedListener )
  {  m_listListeners.add( contentChangedListener ); }



  /**
   * Remove a contentChangedListener
   * @param contentChangedListener The contentChangedListener
   */
  public synchronized void removeContentChangedListener( IVwContentChangedListener contentChangedListener )
  { m_listListeners.remove( contentChangedListener ); }


  /**
   * Send VwContentChangedEvent's to registered listeners
   * @param cce The content changed event
   */
  public synchronized void fireContentsChanged( VwContentChangedEvent cce )
  {
    for ( Iterator iListeners = m_listListeners.iterator(); iListeners.hasNext(); )
    {
      IVwContentChangedListener contentChangedListener = (IVwContentChangedListener)iListeners.next();

      contentChangedListener.contentChanged( cce );

    } // end for
  }


} // End class VwContentChangedHelper{}

// *** End of VwContentChangedHelper.java ***

