/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwAction.java

Create Date: Apr 11, 2006
============================================================================================
*/

package com.vozzware.ui.menu;

import com.vozzware.ui.VwAction;
import com.vozzware.ui.VwActionEvent;
import com.vozzware.ui.VwIcon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


/**
 * This class represents an indepentdent menu/toolbar action
 */
public abstract class VwMenuActionHandler extends VwAction 
{

  private List  m_listMenuListeners = Collections.synchronizedList( new ArrayList() );
  
  
  /**
   * Constructs object from a native swing action
   * @param action The native swing action
   */
  public VwMenuActionHandler( VwMenuListener menuListener )
  {
    super();
    addMenuListener( menuListener );
    
    
  } // end VwAction()

  /**
   * Constructs object from a native swing action
   * @param action The native swing action
   */
  public VwMenuActionHandler( String strName, VwIcon icon , VwMenuListener menuListener )
  {
    super( strName, icon );
    addMenuListener( menuListener );
    
    
  } // end VwAction()

  public void addMenuListener( VwMenuListener menuListener )
  { m_listMenuListeners.add( menuListener ); }
  
  public void removeMenuListener( VwMenuListener menuListener )
  { m_listMenuListeners.remove( menuListener ); }
  
  /**
   * Invoked on menu open/initialization events
   * @param ae The action event responsible for inition the event
   */
  public  void init( VwActionEvent ae )
  { 
    for ( Iterator iListeners = m_listMenuListeners.iterator(); iListeners.hasNext(); )
    {
      VwMenuListener menuListener = (VwMenuListener)iListeners.next();
      menuListener.init( ae );
    }
  }


  /**
   * Invoked when a menu item has been selected or a toolbar icon clicked
   * @param ae The action event responsible for inition the event
   */
  public void actionPerformed( VwActionEvent ae )
  {
    for ( Iterator iListeners = m_listMenuListeners.iterator(); iListeners.hasNext(); )
    {
      VwMenuListener menuListener = (VwMenuListener)iListeners.next();
      menuListener.menuItemSelected( ae );
      
      
    }
  }


} // end class VwAction{}

// *** End of VwAction.java ***
