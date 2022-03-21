/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwMenu.java

Create Date: Apr 11, 2003
============================================================================================
*/
package com.vozzware.ui.menu;

import javax.swing.Action;
import javax.swing.JMenu;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class VwMenu extends JMenu implements VwMenuImplementor
{
  private Map<String,VwMenuItem>   m_mapMenuItems = new HashMap<String,VwMenuItem>();


  /**
   * Default constructor
   */
  public VwMenu()
  { super(); }

  public VwMenu( Action action )
  { super( action ); }
  
  /**
   * Constructor
   *
   * @param strMenuText The menu text
   */
  public VwMenu( String strMenuText )
  { super( strMenuText ); }


  /**
   * Constructor
   *
   * @param strMenuText The menu text
   * @param nMnemonic The menu mnemonic key code
   */
  public VwMenu( String strMenuText, int nMnemonic )
  {
    super( strMenuText );
    this.setMnemonic( nMnemonic);
  }


  /**
   * Adds an VwMenuItem to the menu
   * @param menuItem The menuItem to add to the menuItembar
   */
  public void add( VwMenuItem menuItem )
  {
    super.add( menuItem );
    m_mapMenuItems.put(  menuItem.getName(), menuItem );

  } // end add()

  /**
   * Adds a menu item at the specified index
   * @param menuItem
   * @param ndx
   */
  public void add( VwMenuItem menuItem, int ndx )
  {
    super.add( menuItem, ndx );
    m_mapMenuItems.put(  menuItem.getText().trim(), menuItem );

  } // end add()

  /**
   * Removes a menuItem from the menu
   * @param strText The menuItem text identifying the menuItem to remove
   */
  public void remove( String strText )
  {
    VwMenuItem menuItem = (VwMenuItem)m_mapMenuItems.get( strText );
    if ( menuItem != null )
    {
      super.remove( menuItem );
      m_mapMenuItems.remove( strText );
    }

  } // end remove()

  /**
   * Remove the menuItem from the menuItembar
   * @param menuItem the menuItem instance to remove
   */
  public void remove( VwMenuItem menuItem )
  {
    super.remove( menuItem );
    m_mapMenuItems.remove( menuItem.getText() );

  } // end remove()


  /**
   * Removes the menuItem from the menuItembar at the given index
   * @param ndx the index of the menuItem to remove
   */
  public void remove( int ndx  )
  {
    String strText = this.getItem( ndx ).getText();
    super.remove( ndx );
    m_mapMenuItems.remove( strText );
  }

  /**
   * Get the index of a menu item
   * @param strText the menu item text to get the index for
   * @return
   */
  public int getIndex( String strText )
  {
    int nCount = this.getItemCount();

    for ( int x = 0; x < nCount; x++ )
    {
      Object objItem = this.getItem( x );

      if ( objItem != null )
      {
        if ( ((VwMenuItem)objItem).getText().equalsIgnoreCase( strText ) )
         return x;
      }
    }

    return -1;

  } // end getIndex()


  /**
   * Get the menuIteme instance by its menuItem text
   * @param strText The menuItem text used to retrieve the emnu
   * @return
   */
  public VwMenuItem getMenuItem( String strText )
  { return m_mapMenuItems.get(  strText ); }


  /**
   * Gets an VwMenuItem at the specified index position
   * @param ndx
   * @return
   */
  public VwMenuItem getMenuItem( int ndx )
  {
    Object objItem = super.getItem( ndx );
    String strText = null;

    if ( objItem != null )
    {
      strText = ((VwMenuItem)objItem).getText();
      return m_mapMenuItems.get(  strText );
    }

    return null;

  } // end getMenuItem()

  /**
   * Returns a Iterator to the menu item collection
   * @return
   */
  public Collection<VwMenuItem> getItemList()
  { return m_mapMenuItems.values();  }


} // end class VwMenu{}

// *** End of VwMenu.java ***

