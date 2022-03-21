/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwMenuMgrSwing.java

Create Date: Apr 11, 2003
============================================================================================
 */
package com.vozzware.ui.menu;

import com.vozzware.ui.VwAction;
import com.vozzware.ui.VwActionEvent;
import com.vozzware.ui.VwIcon;
import com.vozzware.ui.menu.util.VwXMLMenu;
import com.vozzware.ui.menu.util.VwXMLMenuItem;
import com.vozzware.ui.menu.util.VwXMLMenuReader;
import com.vozzware.ui.menu.util.VwXMLMenuSpec;
import com.vozzware.util.VwResourceStore;
import com.vozzware.util.VwResourceStoreFactory;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.MenuElement;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.Component;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is a Swing implementation of the VwAppShellMenuMgr
 * 
 * @author petervosburghjr
 * 
 */
public class VwMenuMgrSwing implements VwMenuMgr
{
  private VwMenuBar m_menuBar;

  private Map<VwMenuImplementor, String> m_mapListeners = new HashMap<VwMenuImplementor, String>();

  private Map<String, VwMenuImplementor> m_mapPopupCache = new HashMap<String, VwMenuImplementor>();

  private Map<String, Map<String, VwAction>> m_mapPopupActions = new HashMap<String, Map<String, VwAction>>();

  private Map<String, Map<String, MenuActionHandler>> m_mapActionHandlers = new HashMap<String, Map<String, MenuActionHandler>>();

  private static VwMenuMgr s_instance = null;

  private VwXMLMenuSpec m_menuSpec;

  class MenuActionHandler extends VwAction
  {
    public void actionPerformed( VwActionEvent ae )
    {
      Component objMenuComp = ((Component)ae.getSource());
      String strMenuName = objMenuComp.getName();

      if (strMenuName == null)
        return;

      int nPos = strMenuName.indexOf( '.' );

      if (nPos < 0)
        return;

      String strMenuId = strMenuName.substring( nPos + 1 );

      strMenuName = strMenuName.substring( 0, nPos );

      Map<String, VwAction> mapActions = m_mapPopupActions.get( strMenuName.toLowerCase() );
      if (mapActions == null)
        return;

      VwAction action = (VwAction)mapActions.get( strMenuId.toLowerCase() );

      if (action != null)
        action.actionPerformed( ae );
    }
  }

  /**
   * Constructs VwAppShellMenuMgr
   * 
   * @param menuBar
   *          The menu bar that this manager handles
   */
  public VwMenuMgrSwing( VwMenuBar menuBar )
  {
    m_menuBar = menuBar;

    s_instance = this;
  }

  /**
   * Gets the VwAppShellMenuMgr singleton instance
   * 
   * @return
   */
  public static synchronized VwMenuMgr getInstance()
  {

    if (s_instance == null)
      throw new RuntimeException( "Menu manager not availble" );

    return s_instance;

  }

  /**
   * Loads and creates menu objects from a menu specification xml document that
   * conforms to the VwMenuSpec.xsd sceham file.
   * 
   * @param urlMenuSpec
   *          The URL to the menu XML specification document
   * @throws Exception
   *           if the url can't be accessed
   */
  public void loadMenu( URL urlMenuSpec ) throws Exception
  {
    VwXMLMenuSpec spec = VwXMLMenuReader.read( urlMenuSpec );
    List<VwXMLMenu> listMenus = spec.getMenu();
    if (listMenus == null)
      throw new Exception( "Menu list is null, make sure the URL to the xml menu specification exists and is valid" );

    // look for popup menus and build cache
    String strFirstMenu = listMenus.get( 0 ).getType();
    if (!(strFirstMenu.equalsIgnoreCase( "popup" )) && !(strFirstMenu.equalsIgnoreCase( "menuBar" )))
      throw new Exception( "Invalid menu type :'" + strFirstMenu + "' must be either popup or menuBar" );

    for ( VwXMLMenu menu : listMenus )
    {

      if (menu.getType().equalsIgnoreCase( "popup" ))
      {
        VwPopupMenu popupMenu = new VwPopupMenu();
        popupMenu.setName( menu.getName() );

        popupMenu.setLightWeightPopupEnabled( false );
        createMenu( popupMenu, menu );
      }
      else
        if (menu.getType().equalsIgnoreCase( "menuBar" ))
        {
          if (m_menuBar == null)
            throw new Exception( "Missing Menubar Instance, Use constructor that takes the VwMenuBar" );

          VwMenu menuPullDown = new VwMenu();
          createMenu( menuPullDown, menu );

          m_menuBar.add( menuPullDown );

        }
    }
  }

  /**
   * Creates a popup menu from the XML specification and places the objects in
   * cache
   * 
   * @param menu
   *          The pared xml menu specification
   * @throws Exception
   */
  private void createMenu( VwMenuImplementor menuImpl, VwXMLMenu menu ) throws Exception
  {
    VwResourceStore resStore = VwResourceStoreFactory.getInstance().getStore();

    String strMenuName = menu.getName();
    menuImpl.setText( strMenuName );

    menuImpl.setName( strMenuName );

    List<VwXMLMenuItem> listMenuItems = menu.getMenuItem();

    createMenuItems( strMenuName, menuImpl, listMenuItems, resStore );

    m_mapPopupCache.put( menu.getName().toLowerCase(), menuImpl );

  } // end createMenu()

  private void createMenuItems( String strMenuName, VwMenuImplementor menuImpl, List<VwXMLMenuItem> listMenuItems,
      VwResourceStore resStore )
  {
    Map<String, MenuActionHandler> mapMenuActionHandlers = m_mapActionHandlers.get( strMenuName );
    if (mapMenuActionHandlers == null)
    {
      mapMenuActionHandlers = new HashMap<String, MenuActionHandler>();
      m_mapActionHandlers.put( strMenuName, mapMenuActionHandlers );

    }

    for ( VwXMLMenuItem menuItem : listMenuItems )
    {
      if (menuItem.getType() != null && menuItem.getType().equalsIgnoreCase( "separator" ))
        menuImpl.addSeparator();
      else
      {
        MenuActionHandler mah = new MenuActionHandler();
        mapMenuActionHandlers.put( menuItem.getId(), mah );
        mah.setName( menuItem.getName() );

        if (menuItem.getIcon() != null)
        {
          VwIcon icon = resStore.getIcon( menuItem.getIcon() );
          mah.setIcon( icon );

        }

        String strToolTip = menuItem.getToolTip();

        if (strToolTip == null)
          strToolTip = menuItem.getName();

        mah.setToolTip( strToolTip );

        if (menuItem.getEnabled() != null && menuItem.getEnabled().equals( "false" ))
          mah.setEnabled( false );

        if (menuItem.getMnemonic() != null)
          mah.setMnemonic( menuItem.getMnemonic().charAt( 0 ) );

        if (menuItem.getAccelerator() != null)
          mah.setAccelerator( KeyStroke.getKeyStroke( menuItem.getAccelerator() ) );

        if (menuItem.getMenuItem() != null)
        {
          VwMenu subMenu = new VwMenu( mah );

          subMenu.setName( strMenuName + "." + menuItem.getName() );

          menuImpl.add( subMenu );
          createMenuItems( strMenuName, subMenu, menuItem.getMenuItem(), resStore );

        }
        else
        {
          VwMenuItem jmenuItem = new VwMenuItem( mah );
          jmenuItem.setName( strMenuName + "." + menuItem.getId() );
          if (menuImpl instanceof VwMenu)
            ((VwMenu)menuImpl).add( jmenuItem );
          else
            menuImpl.add( jmenuItem );

        }

      }

    }

  }

  /**
   * Displays a popup menu
   * 
   * @param strPopupName
   *          The name of the popup menu as defined in a xml menu spec document
   * @param x
   *          The x location of the popup menu
   * @param y
   *          the y location of the popup menu
   * @throws Exception
   */
  public void showPopup( Component compParent, String strPopupName, final int x, final int y ) throws Exception
  {
    final VwPopupMenu popupMenu = (VwPopupMenu)m_mapPopupCache.get( strPopupName.toLowerCase() );

    if (popupMenu == null)
      throw new Exception( "Popup menu: '" + strPopupName + "' does not exist" );

    popupMenu.show( compParent, x, y );

  }

  public void addGroup( String strNewGroupID, int nMnemonic, int nPlacement, String strExistingGroupID )
      throws Exception
  {
    VwMenu menu = m_menuBar.getMenu( strNewGroupID );

    if (menu != null)
      throw new Exception( "Menu: " + strNewGroupID + " already exists" );

    if (strExistingGroupID != null)
    {
      menu = m_menuBar.getMenu( strExistingGroupID );

      if (menu == null)
        throw new Exception( "Menu: " + strExistingGroupID + " does not exist" );
    }

    int nItemNdx = 0;

    menu = new VwMenu( strNewGroupID );
    if (nMnemonic > 0)
      menu.setMnemonic( nMnemonic );

    switch ( nPlacement )
    {
      case FIRST:

        m_menuBar.add( menu, 0 );
        break;

      case LAST:

        m_menuBar.add( menu );
        break;

      case BEFORE:

        nItemNdx = m_menuBar.getIndex( strExistingGroupID );

        m_menuBar.add( menu, nItemNdx );
        break;

      case AFTER:

        nItemNdx = menu.getIndex( strExistingGroupID );

        m_menuBar.add( menu, ++nItemNdx );
        break;

      default:

        throw new Exception( "Invalid menu placement id" );

    } // end switch()

  } // end addGroup()

  
  /**
   * Removes a toplevel menu pull down from the menu nar
   */
  public void removeGroup( String strGroupID ) throws Exception
  {
    VwMenu menu = m_menuBar.getMenu( strGroupID );

    if (menu == null)
      throw new Exception( "Menu: " + strGroupID + " does not exist" );

    m_menuBar.remove( strGroupID );

  } // end removeGroup()

  
  
  /**
   * Adds an action to a toplevel menu bar
   */
  public void addAction( String strGroupID, String strMenuID, int nPlacement, VwAction action ) throws Exception
  {
    final VwMenu menu = m_menuBar.getMenu( strGroupID );

    if (menu == null)
      throw new Exception( "Menu: " + strGroupID + " does not exist" );

    if (menu.getMenuItem( action.getName() ) != null)
      throw new Exception( "Menu Item: " + strMenuID + " already exists" );

    VwMenuItem mi = new VwMenuItem( action );

    int nItemNdx = 0;

    switch ( nPlacement )
    {
      case FIRST:

        menu.add( mi, 0 );
        break;

      case LAST:

        menu.add( mi );
        break;

      case BEFORE:

        nItemNdx = menu.getIndex( strMenuID );

        if (nItemNdx < 0)
          throw new Exception( "Menu Item: " + strMenuID + " does not exist" );

        menu.add( mi, nItemNdx );
        break;

      case AFTER:

        nItemNdx = menu.getIndex( strMenuID );

        if (nItemNdx < 0)
          throw new Exception( "Menu Item: " + strMenuID + " does not exist" );

        menu.add( mi, ++nItemNdx );
        break;

      default:

        throw new Exception( "Invalid menu placement id" );

    } // end switch()

    registerAction( strGroupID, action.getName(), action );

  } // add addAction()

  /*
   * (non-Javadoc)
   * 
   * @see com.vozzware.ui.VwMenuMgr#getActions(java.lang.String)
   */
  public Collection<VwMenuItem> getActions( String strGroupID ) throws Exception
  {
    final VwMenu menu = m_menuBar.getMenu( strGroupID );

    if (menu == null)
      throw new Exception( "Menu: " + strGroupID + " does not exist" );

    return menu.getItemList();

  } // end getActions()

  
  public VwAction getAction( String strGroupID, String strMenuID ) throws Exception
  {
    final VwMenu menu = m_menuBar.getMenu( strGroupID );

    if (menu == null)
      throw new Exception( "Menu: " + strGroupID + " does not exist" );

    Object objMenuItem = menu.getMenuItem( strMenuID );

    if (objMenuItem != null)
      return ((VwMenuItem)objMenuItem).getMenuAction();

    return null;

  } // end getAction()

  /**
   * 
   */
  public void removeAction( String strGroupID, String strMenuID ) throws Exception
  {
    final VwMenu menu = m_menuBar.getMenu( strGroupID );

    if (menu == null)
      throw new Exception( "Menu: " + strGroupID + " does not exist" );

    menu.remove( strMenuID );

  } // end removeAction()

  public void registerAction( String strGroupID, String strMenuID, VwAction action ) throws Exception
  {

    String strId = null;

    // see if request is for a sub menu
    int ndx = strGroupID.indexOf( '.' );
    if (ndx > 0)
      strId = strGroupID.substring( 0, ndx );
    else
      strId = strGroupID;

    VwMenu menuSearch = m_menuBar.getMenu( strId );

    if (ndx > 0)
    {
      menuSearch = findSubMenu( menuSearch.getMenuComponents(), strGroupID );
    }

    if (menuSearch == null)
      throw new Exception( "Menu: " + strGroupID + " does not exist" );

    final VwMenu menu = menuSearch;

    if (!m_mapListeners.containsKey( menu ))
    {
      m_mapListeners.put( menu, null );

      menu.addMenuListener( new MenuListener()
      {
        public void menuSelected( MenuEvent e )
        {
          for ( VwMenuItem mi : menu.getItemList() )
          {
            VwAction action = (VwAction)mi.getAction();
            action.init( new VwActionEvent( this, 0, "init" ) );

          }
        }

        public void menuDeselected( MenuEvent e )
        {
        }

        public void menuCanceled( MenuEvent e )
        {
        }

      } );

    }

    VwMenuItem mi = menu.getMenuItem( strId + "." + strMenuID );

    // menu.get
    if (mi == null)
      throw new Exception( "Menu Item: " + strMenuID + " does not exist" );

    String strName = action.getName();
    if (strName == null)
      action.setName( mi.getText() );

    mi.setAction( action );

  } // end registerAction()

  private VwMenu findSubMenu( Component[] aComps, String strId )
  {
    for ( int x = 0; x < aComps.length; x++ )
    {
      if (aComps[x] instanceof VwMenu)
      {
        VwMenu subMenu = (VwMenu)aComps[x];
        String strName = aComps[x].getName();
        if (strName.equalsIgnoreCase( strId ))
          return subMenu;
        else
          findSubMenu( subMenu.getComponents(), strId );
      }
    }

    return null;
  }

  public void addPopupAction( String strPopupName, String strMenuID, String strMenuName, int nPlacement,
      VwAction action ) throws Exception
  {

    VwPopupMenu menu = (VwPopupMenu)m_mapPopupCache.get( strPopupName.toLowerCase() );

    if (menu == null)
      throw new Exception( "Popup menu '" + strPopupName + "' does not exist" );

    Map<String, MenuActionHandler> mapMenuHandlers = m_mapActionHandlers.get( strPopupName );

    MenuActionHandler mah = new MenuActionHandler();
    mah.setName( strMenuName );

    if (action.getIcon() != null)
      mah.setIcon( action.getIcon() );

    mah.setEnabled( action.isEnabled() );
    mapMenuHandlers.put( strMenuID, mah );

    VwMenuItem mi = new VwMenuItem( mah );
    mi.setText( strMenuName );
    mi.setName( strPopupName + "." + strMenuID );

    int nItemNdx = 0;

    switch ( nPlacement )
    {
      case FIRST:

        menu.add( mi, 0 );
        break;

      case LAST:

        menu.add( mi );
        break;

      case BEFORE:

        nItemNdx = findPopupItemIndex( menu, strMenuName );

        if (nItemNdx < 0)
          throw new Exception( "Menu Item: " + strMenuName + " does not exist" );

        menu.add( mi, nItemNdx );
        break;

      case AFTER:

        nItemNdx = findPopupItemIndex( menu, strMenuName );

        if (nItemNdx < 0)
          throw new Exception( "Menu Item: " + strMenuID + " does not exist" );

        menu.add( mi, ++nItemNdx );
        break;

      default:

        throw new Exception( "Invalid menu placement id" );

    } // end switch()

    registerPopupAction( strPopupName, strMenuID, action );

  } // add addAction()

  private int findPopupItemIndex( VwPopupMenu menu, String strItemName )
  {
    MenuElement[] aElements = menu.getSubElements();

    for ( int x = 0; x < aElements.length; x++ )
    {
      if (aElements[x].getComponent().getName().equalsIgnoreCase( strItemName ))
        return x;

    }

    return -1;
  }

  /**
   * Register actions for a popup menu
   * 
   * @param strPopupName
   *          The name of the popup menu
   * @param strMenuId
   *          The menu item id the action is to be registered ti
   * @param action
   *          The action handler
   * @throws Exception
   */
  public void registerPopupAction( String strPopupName, String strMenuId, VwAction action ) throws Exception
  {

    VwPopupMenu popup = (VwPopupMenu)m_mapPopupCache.get( strPopupName.toLowerCase() );

    if (popup == null)
      throw new Exception( "Popup menu '" + strPopupName + "' does not exist" );

    Map<String, VwAction> mapActions = (Map)m_mapPopupActions.get( strPopupName.toLowerCase() );
    Map<String, MenuActionHandler> mapMenuHandlers = m_mapActionHandlers.get( strPopupName );
    MenuActionHandler mah = mapMenuHandlers.get( strMenuId );

    if (mah == null)
      throw new Exception( "Popup menu Id '" + strMenuId + "' does not exist, and action cannot be registered" );

    // action.setName( mah.getName() );
    action.setIcon( mah.getIcon() );
    action.setToolTip( (String)mah.getValue( VwAction.SHORT_DESCRIPTION ) );

    if (mapActions == null)
    {
      mapActions = new HashMap<String, VwAction>();
      m_mapPopupActions.put( strPopupName.toLowerCase(), mapActions );
    }

    mapActions.put( strMenuId.toLowerCase(), action );

    if (!m_mapListeners.containsKey( popup ))
    {
      m_mapListeners.put( popup, null );

      popup.addPopupMenuListener( new PopupMenuListener()
      {
        public void popupMenuWillBecomeVisible( PopupMenuEvent e )
        {
          JPopupMenu menu = (JPopupMenu)e.getSource();

          String strName = menu.getName();
          Map mapActions = (Map)m_mapPopupActions.get( strName.toLowerCase() );

          Component[] aComps = menu.getComponents();

          setupMenuItems( aComps, mapActions );

        }

        private void setupMenuItems( Component[] aComps, Map mapActions )
        {
          for ( int x = 0; x < aComps.length; x++ )
          {
            if ((aComps[x] instanceof JMenu))
            {
              Component[] aSubComps = ((JMenu)aComps[x]).getMenuComponents();
              setupMenuItems( aSubComps, mapActions );
              ;
              continue;

            }
            else
              if (!(aComps[x] instanceof JMenuItem))
                continue;

            JMenuItem mi = (JMenuItem)aComps[x];
            String strMenuId = mi.getName();

            if (strMenuId == null)
              continue;

            int nPos = strMenuId.indexOf( '.' );

            if (nPos < 0)
              return;

            strMenuId = strMenuId.substring( ++nPos );

            VwAction action = (VwAction)mapActions.get( strMenuId.toLowerCase() );
            if (action != null)
            {
              action.init( new VwActionEvent( this, 0, "init" ) );
              mi.getAction().setEnabled( action.isEnabled() );
            }

          }

        }

        public void popupMenuWillBecomeInvisible( PopupMenuEvent e )
        {
        }

        public void popupMenuCanceled( PopupMenuEvent e )
        {
        }

      } );

    }

  } // end registerAction()

  /**
   * 
   */
  public void unRegisterAction( String strGroupID, String strMenuID ) throws Exception
  {

    VwMenu menu = m_menuBar.getMenu( strGroupID );

    if (menu == null)
      throw new Exception( "Menu: " + strGroupID + " does not exist" );

    VwMenuItem mi = menu.getMenuItem( strMenuID );

    if (mi == null)
      throw new Exception( "Menu Item: " + strMenuID + " does not exist" );

    mi.setAction( null );

  } // end registerAction()

  public boolean isPopupMenuItemExists( String strPopupMenuName, String strMenuId )
  {
    VwPopupMenu popup = (VwPopupMenu)m_mapPopupCache.get( strPopupMenuName.toLowerCase() );

    if (popup == null)
      return false;

    Map mapActions = (Map)m_mapPopupActions.get( strPopupMenuName.toLowerCase() );

    if (mapActions == null)
      return false;

    if (mapActions.containsKey( strMenuId.toLowerCase() ))
      return true;

    return false;

  }

  /**
   * Determines if a popup menu definition exists
   * 
   * @param strPopupMenuName
   *          The name of the popup menu to test
   * @return true if it exists, false otherwise
   */
  public boolean isPopupMenuExists( String strPopupMenuName )
  {
    return m_mapPopupCache.containsKey( strPopupMenuName.toLowerCase() );
  }

} // end class VwMenuMgrSwing{}

// *** End of VwMenuMgrSwing.java ***
