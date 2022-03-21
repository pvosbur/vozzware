/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwMenuBar.java

Create Date: Apr 11, 2003
============================================================================================
*/
package com.vozzware.ui.menu;

import javax.swing.JMenuBar;
import java.util.HashMap;
import java.util.Map;


public class VwMenuBar extends JMenuBar
{
  private Map<String,VwMenu>   m_mapMenues = new HashMap<String,VwMenu>();


  /**
   * Adds an VwMenu to the menubar
   * @param menu The menu to add to the menubar
   */
  public void add( VwMenu menu )
  {
    super.add( menu );
    m_mapMenues.put(  menu.getText(), menu );

  } // end add()


  public void add( VwMenu menu, int ndx  )
  {
    super.add( menu, ndx );
    m_mapMenues.put(  menu.getText(), menu );

  } // end add()


  /**
   * Removes a menu from the menubar
   * @param strText The menu text identifying the menu to remove
   */
  public void remove( String strText )
  {
    VwMenu menu = m_mapMenues.get( strText );
    if ( menu != null )
    {
      super.remove( menu );
      m_mapMenues.remove( strText );
    }

  } // end remove()

  /**
   * Remove the menu from the menubar
   * @param menu the menu instance to remove
   */
  public void remove( VwMenu menu )
  {
    super.remove( menu );
    m_mapMenues.remove( menu.getText() );
  } // end remove()


  /**
   * Removes the menu from the menubar at the given index
   * @param ndx the index of the menu to remove
   */
  public void remove( int ndx  )
  {
    String strText = this.getMenu( ndx ).getText();
    super.remove( ndx );
    m_mapMenues.remove( strText );
  }

  /**
   * Get the menue instance by its menu text
   * @param strText The menu text used to retrieve the emnu
   * @return
   */
  public VwMenu getMenu( String strText )
  { return m_mapMenues.get(  strText ); }

  /**
   * Gets an VwMenu at the specified index position
   * @param ndx
   * @return
   */
  public VwMenu getMenuFromIndex( int ndx )
  {
    String strText = super.getMenu( ndx ).getText();
    return m_mapMenues.get(  strText );

  }


  /**
   * Get the index of the menu
   * 
   * @param strMenuID The menu text/ID of the menu item to get the index for
   * @return he index position on the menubar or -1 if the menu does not exist
   */
  public int getIndex( String strMenuID )
  {
    int nCount = this.getMenuCount();

    for ( int x = 0; x < nCount; x++ )
    {
      Object objMenu = this.getMenu( x );

      if ( objMenu != null )
      {
        if (((VwMenu)objMenu).getText().equalsIgnoreCase( strMenuID ) )
          return x;

      }
    }

    return -1; // Not found
  }
} // end class VwMenuBar{}

// *** End of VwMenuBar.java ***

