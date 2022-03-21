/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwAppShellMenuMgr.java

Create Date: Apr 11, 2003
============================================================================================
*/
package com.vozzware.ui.menu;

import com.vozzware.ui.VwAction;

import java.awt.Component;
import java.net.URL;
import java.util.Collection;

public interface VwMenuMgr
{

  /**
   * Menu and menu item placement indicator
   */
  public static final int FIRST = 0;

  public static final int LAST = 1;

  public static final int BEFORE = 2;

  public static final int AFTER = 3;

  /**
   * Loads and creates menu objects from a menu specification xml document that conforms to the VwMenuSpec.xsd sceham file.
   * 
   * @param urlMenuSpec The URL to the menu XML specification document
   * @throws Exception
   */
  public void loadMenu( URL urlMenuSpec ) throws Exception;
  
  /**
   * Displays a popup menu
   * 
   * @param strPopupName The name of the popup menu as defined in a xml menu spec document
   * @param x The x location of the popup menu
   * @param y the y location of the popup menu
   * @throws Exception
   */
  public void showPopup( Component compParent, String strPopupName, int x, int y ) throws Exception;
  
  /**
   * Adds a toplevel menu (group) to the menu bar
   *
   * @param strNewGroupID The menu text/id of the new menu to add
   * @param nMnemonic The menu mnemonic key code or 0 for no mnemonic
   * @param nPlacement The placement constant on where to place the new menu group
   * @param strExistingGroupID  The ID of an exisiting gorup if placement is BEFORE or AFTER -- may be null otherwise
   * @throws Exception the the new group id exists or the existing gorup does not exist
   */
  public abstract void addGroup( String strNewGroupID, int nMnemonic, int nPlacement, String strExistingGroupID ) throws Exception; 

  /**
   * Removes a top level menu (group) from the menubar
   * @param strGroupID The menu text/ID of the menu group to remove
   *
   * @throws Exception if the menu groupID does not exist
   */
  public abstract void removeGroup( String strGroupID ) throws Exception; 

  /**
   * Adds a new menu action to an existing menu group
   * @param strGroupID
   * @param strMenuID
   * @param action
   * @throws Exception
   */
  public abstract void addAction( String strGroupID, String strMenuID,  int nPlacement, VwAction action ) throws Exception; 

  public abstract void addPopupAction( String strPopupName, String strMenuID, String strMenuName, int nPlacement, VwAction action ) throws Exception; 
  
  public abstract Collection<VwMenuItem>getActions( String strGroupID ) throws Exception; // end getActions()

  /**
   * Gets an VwAction for the requested menu id
   * @param strGroupID The group menu that the menu item belongs to
   * @param strMenuID  The menu id of the menu item to get the acvtion for
   * @return The VwAction assigned to the menu id or null if no action is currently assigned
   * @throws Exception if the group id doen not exist
   */
  public abstract VwAction getAction( String strGroupID, String strMenuID ) throws Exception;

  /**
   * Removes an action from a menu or menu group
   * @param strGroupID The menu group id (Usually the top level menubar item or a cascading menuitem)
   * @param strMenuID The menu id to remove the action handler from
   * @throws Exception
   */
  public abstract void removeAction( String strGroupID, String strMenuID ) throws Exception; 

  /**
   * Register a menu action with the menu manager
   * @param strGroupID The ID of the menu that exists on the menu bar.
   * @param strMenuID The menu id that belongs to the specidief group
   * @param action The action to register for this menu item
   * @throws Exception
   */
  public abstract void registerAction( String strGroupID, String strMenuID, VwAction action ) throws Exception; 

  public abstract void unRegisterAction( String strGroupID, String strMenuID ) throws Exception; 

  
  /**
   * Register actions for a popup menu
   * 
   * @param strPopupName The name of the popup menu
   * @param strMenuId    The menu item id the action is to be registered ti
   * @param action       The action handler
   * @throws Exception
   */
  public void registerPopupAction( String strPopupName, String strMenuId, VwAction action ) throws Exception;
  
  /**
   * Determines if a popup action exists
   * @param strPopupMenuName The popup menu name
   * @param strMenuId The menu id to test
   * @return true if a popup action is registered with the popup menu
   */
  public boolean isPopupMenuItemExists( String strPopupMenuName, String strMenuId ); 
  
  /**
   * Determines if a popup menu definition exists
   * @param strPopupMenuName The name of the popup menu to test
   * @return true if it exists, false otherwise
   */
  public boolean isPopupMenuExists( String strPopupMenuName );

  

}// end class VwAppShellMenuMgr {}

// *** end of VwAppShellMenuMgr.java ***
