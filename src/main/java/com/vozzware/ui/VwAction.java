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

package com.vozzware.ui;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;
import java.awt.event.ActionEvent;

/**
 * This class represents an indepentdent menu/toolbar action
 */
public abstract class VwAction extends AbstractAction
{

  private boolean   m_fSelected;                 // Action selected state

  /**
   * Constructs object from a native swing action
   * @param action The native swing action
   */
  public VwAction( Action action )
  {
    Object[] aKeys = getKeys();

    if ( aKeys == null )
      return;
    
     for ( int x = 0; x < aKeys.length; x++ )
       action.putValue( (String)aKeys[ x ], getValue( (String)aKeys[ x ] ) );

  } // end VwAction()


  /**
   * Default constructor
   */
  public VwAction()
  { super(); }

  /**
   * Constrcuts an action with a name and icon
   * @param strName
   * @param icon
   */
  public VwAction( String strName, VwIcon icon )
  {  super( strName, icon ); }


  /**
   * Constructs an action with just a name
   * @param strName
   */
  public VwAction( String strName )
  { super( strName ); }


  /**
   * Copies the properties of this action instances to the one specified in the parameter
   * @param action The action instance to receive the copied properties
   */
  public void copy( VwAction action )
  {
    Object[] aKeys = getKeys();

    for ( int x = 0; x < aKeys.length; x++ )
      action.putValue( (String)aKeys[ x ], getValue( (String)aKeys[ x ] ) );

  } // end copy()


  /**
   * Sets the action name
   * @param strName The name of the action. This will also be the menu text
   */
  public void setName( String strName )
  { putValue( Action.NAME, strName ); }


  public String getName()
  { return (String)getValue( Action.NAME ); }

  
  public void setIcon( VwIcon icon )
  { putValue( Action.SMALL_ICON, icon ); }

  public VwIcon getIcon()
  { return (VwIcon)getValue( Action.SMALL_ICON ); }
  
  /**
   * Sets the mnemonic keycode for this action
   * @param nKeyCode The keycode used for the mnemonic key character
   */
  public void setMnemonic( int nKeyCode )
  { putValue( Action.MNEMONIC_KEY, new Integer( nKeyCode ) ); }


  public void setAccelerator( KeyStroke keyStroke )
  { putValue( Action.ACCELERATOR_KEY, keyStroke ); }

  /**
   * Sets the tooltip text for this action
   * @param strToolTip
   */
  public void setToolTip( String strToolTip )
  { putValue( Action.SHORT_DESCRIPTION, strToolTip ); }


  /**
   * Sets the selected state of this action. This is used primarily to add check marks on menu items
   * @param fSelected
   */
  public void setSelected( boolean fSelected )
  {  m_fSelected = fSelected; }

  /**
   * The the selected state for this action
   * @return
   */
  public boolean getSelected()
  { return m_fSelected; }


  public void actionPerformed( ActionEvent ae )
  { actionPerformed( new VwActionEvent( ae ) ); }

  /**
   * Invoked on menu open/initialization events
   * @param ae The action event responsible for inition the event
   */
  public  void init( VwActionEvent ae )
  { ; }


  /**
   * Invoked when a menu item has been selected or a toolbar icon clicked
   * @param ae The action event responsible for inition the event
   */
  public abstract void actionPerformed( VwActionEvent ae );


} // end class VwAction{}

// *** End of VwAction.java ***
