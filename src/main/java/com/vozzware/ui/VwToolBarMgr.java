/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwToolBarMgr.java

Create Date: Apr 11, 2003
============================================================================================
*/
package com.vozzware.ui;

import javax.swing.AbstractButton;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * This class manages an application's top level window toolbar
 */
public class VwToolBarMgr
{
  private static Map s_mapToolBars = Collections.synchronizedMap( new HashMap() );
  private static Map s_mapListeners = Collections.synchronizedMap( new HashMap() );

  private static VwToolBarMgr s_instance = null;

  private VwPanel m_panelToolBars;

  /**
   * Menu and menu item placement indicator
   */
  public static final int FIRST = 0;
  public static final int LAST = 1;
  public static final int BEFORE = 2;
  public static final int AFTER = 3;

  /**
   * Singleton Constructor
   */
  private VwToolBarMgr()
  {
    m_panelToolBars = new VwPanel();
    m_panelToolBars.setLayout(  new FlowLayout() );

  } // end VwToolBarMgr()

  /**
   * Gets the VwToolBarMgr singleton instance
   * @return
   */
  public static synchronized VwToolBarMgr getInstance()
  {

    if ( s_instance == null )
      s_instance = new VwToolBarMgr();

    return s_instance;

  } // end getInstance()


  /**
   * Gets the panel that holds the tool bars
   * @return The panel that holds the tool bars
   */
  public VwPanel getPanel()
  { return m_panelToolBars; }


  /**
   * Removes a top level menu (group) from the menubar
   * @param strToolBarID The menu text/ID of the menu group to remove
   *
   * @throws Exception if the menu groupID does not exist
   */
  public void removeToolBar( String strToolBarID ) throws Exception
  {
    VwToolBar toolbar = getToolBar( strToolBarID );

    s_mapToolBars.remove( toolbar );
    m_panelToolBars.remove( toolbar );

  } // end removeToolBar()


  /**
   * Adds a new Toolbar to the end of the panel
   * @param toolBar The toolbar to add
   */
  public void addToolBar( VwToolBar toolBar) throws Exception
  { addToolBar( toolBar, null, VwToolBarMgr.LAST ); }


  /**
   * Adds a new toolbar component
   *
   * @param toolBar
   * @param strPlacementCompID
   * @param nPlacement
   * @throws Exception
   */
  public void addToolBar( VwToolBar toolBar,  String strPlacementCompID, int nPlacement ) throws Exception
  {
    if ( s_mapToolBars.containsKey( toolBar.getName() ) )
      throw new Exception( "ToolBar: " + toolBar.getName() + " already exists");

    s_mapToolBars.put( toolBar.getName(), toolBar );

    int nItemNdx = 0;

    switch( nPlacement )
    {
      case FIRST:

           m_panelToolBars.add( toolBar, 0 );
           break;

      case LAST:

            m_panelToolBars.add( toolBar );
           break;

      case BEFORE:

           nItemNdx = findToolBarIndex( strPlacementCompID );

           if ( nItemNdx < 0 )
             throw new Exception( "ToolBar Component: " +  strPlacementCompID + " does not exist");

           m_panelToolBars.add( toolBar, nItemNdx );
           break;

      case AFTER:

           nItemNdx = findToolBarIndex( strPlacementCompID );

           if ( nItemNdx < 0 )
             throw new Exception( "ToolBar Component: " + strPlacementCompID + " does not exist");

           m_panelToolBars.add( toolBar, ++nItemNdx );
           break;

       default:

           throw new Exception( "Invalid ToolBar placement id");

    } // end switch()

  } // add addAction()


  /**
   * Find the toolBar component index with in the panel
   * @param strID The toolBar component name id to locate
   * @return
   */
  private int findToolBarIndex( String strID )
  {
    Component[] aToolBars = m_panelToolBars.getComponents();

    for ( int x = 0; x < aToolBars.length; x++ )
    {
      if ( aToolBars[ x ].getName().equalsIgnoreCase( strID ) )
        return x;
    }
    return -1;  // nOt Found
  }

  /**
   * Gets an Iterator to a TollBar's actions
   * @param strToolBarID
   * @return
   * @throws Exception
   */
  public Iterator getActions( String strToolBarID ) throws Exception
  {
    final VwToolBar toolBar = (VwToolBar)s_mapToolBars.get( strToolBarID );

    if ( toolBar == null )
      throw new Exception( "ToolBar: " + strToolBarID + " does not exist");

    return toolBar.getActions();

   } // end getActions()


  /**
   * Gets an VwAction for the requested menu id
   * @param strToolBarID The group menu that the menu item belongs to
   * @param strCompID  The menu id of the menu item to get the acvtion for
   * @return The VwAction assigned to the menu id or null if no action is currently assigned
   * @throws Exception if the group id doen not exist
   */
  public VwAction getAction( String strToolBarID, String strCompID ) throws Exception
  {
    final VwToolBar toolBar = getToolBar( strToolBarID );

    Object objComp = toolBar.getComponent( strCompID );

    if ( objComp instanceof AbstractButton )
      return (VwAction)((AbstractButton)objComp).getAction();

    if ( objComp instanceof VwComboBox )
      return (VwAction)((VwComboBox)objComp).getAction();

    return null;

  } // end getAction()


  /**
   * Removes an action from a menu or menu group
   * @param strToolBarID
   * @param strCompID
   * @throws Exception
   */
  public void removeComponent( String strToolBarID, String strCompID ) throws Exception
  {
    final VwToolBar toolBar = getToolBar( strToolBarID );

    toolBar.remove( strCompID );

  } // end removeAction()


  /**
   * Register a menu action with the menu manager
   * @param strToolBarID The ID of the menu that exists on the menu bar.
   * @param strCompID The menu id that belongs to the specidief group
   * @param action The action to register for this menu item
   * @throws Exception
   */
  public void registerAction( String strToolBarID, String strCompID, VwAction action ) throws Exception
  {

    final VwToolBar toolBar = getToolBar( strToolBarID );

    Object objComp = toolBar.getComponent( strCompID );

    if ( objComp instanceof VwComboBox )
    {

      if ( !s_mapListeners.containsKey( objComp ))
      {
        s_mapListeners.put( objComp, null );

        VwComboBox combo = (VwComboBox)objComp;

        combo.addItemListener( new ItemListener()
        {
          public void itemStateChanged( ItemEvent ie)
          {
            if ( ie.getStateChange() == ItemEvent.SELECTED )
            {

            }
          }
        });
      }
    }
    else
    if ( objComp instanceof AbstractButton )
    {
      ((AbstractButton)objComp).setAction( action );
      if ( objComp instanceof VwButton )
        ((VwButton)objComp).setText( "" );

    }
  } // end registerAction()


  public void unRegisterAction( String strToolBarID, String strCompID ) throws Exception
  {

    VwToolBar toolBar = getToolBar( strToolBarID );
    Object objComp = toolBar.getComponent( strCompID );

    if ( objComp instanceof AbstractButton )
      ((AbstractButton)objComp).setAction( null );
    else
    if ( objComp instanceof VwComboBox )
     ((VwComboBox)objComp).setAction( null );

  } // end registerAction()



  /**
   * Gets the toolbar for the specidied id
   * @param strToolBarID The id of the toolbar to retrieve
   * @return The VwToolBar instace
   * @throws Exception the if id does not exist
   */
  private VwToolBar getToolBar( String strToolBarID ) throws Exception
  {
    VwToolBar toolBar = (VwToolBar)s_mapToolBars.get( strToolBarID );

    if ( toolBar == null )
      throw new Exception( "ToolBar: " + strToolBarID + " does not exist");

    return toolBar;

  } // end getToolBar()

}  // end class VwToolBarMgr{}

// *** End of VwToolBarMgr.java ***

