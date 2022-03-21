/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwToolBar.java

Create Date: Apr 11, 2003
============================================================================================
*/
package com.vozzware.ui;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JToolBar;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class VwToolBar extends JToolBar
{
  private Map   m_mapComponents = new HashMap();

  /**
   * Mouse listener that paints/removes button borders when mouse eneters/exts button area
   */
  class ButtonMouseListener extends MouseAdapter
  {
    public void mouseEntered(  MouseEvent e )
    {
      if ( ! ((AbstractButton)e.getSource()).isEnabled() )
        return;

       ((AbstractButton)e.getSource()).setBorderPainted( true  );
    }

    public void mouseExited(  MouseEvent e )
    {
      if ( ! ((AbstractButton)e.getSource()).isEnabled() )
        return;

       ((AbstractButton)e.getSource()).setBorderPainted( false  );
    }

  } // end class ButtonMouseListener{}

  /**
   * Constructor
   * @param strID The id of this toolBar
   */
  public VwToolBar( String strID )
  {
    this.setName( strID );

  } // end VwToolBar()


  /**
   * Adds an VwButton to the toolBar
   * @param btn The component to add to the toolBar
   */
  public void add( VwButton btn, String strID )
  {
    super.add( btn );

    btn.setName( strID );
    m_mapComponents.put( strID, btn );

    btn.setText( "" );
    btn.setFocusPainted( false );
    btn.setBorderPainted( false );
    btn.addMouseListener( new ButtonMouseListener() );

  } // end add()

  /**
   * Adds an VwButton to the toolBar at the specified index
   *
   * @param component The button component to add
   * @param ndx The index position to add on the toolbar
   */
  public void add( VwButton component, String strID, int ndx  )
  {
    super.add( component, ndx );
    component.setName( strID );
    m_mapComponents.put(  strID, component );

  } // end add()


  /**
   * Adds an VwComboBox to the toolBar
   * @param component The component to add to the toolBar
   */
  public void add( VwComboBox component, String strID )
  {
    super.add( component );
    component.setName( strID );
    m_mapComponents.put(  strID, component );

  } // end add()

  /**
   * Adds an VwComboBox to the toolBar at the specified index
   *
   * @param component The combo box component to add
   * @param ndx The index position to add on the toolbar
   */
  public void add( VwComboBox component, String strID, int ndx  )
  {
    super.add( component, ndx );
    component.setName( strID );
    m_mapComponents.put(  strID, component );

  } // end add()


  /**
   * Removes a component from the toolBar
   * @param strID The component text identifying the component to remove
   */
  public void remove( String strID )
  {
    VwButton component = (VwButton)m_mapComponents.get( strID );
    if ( component != null )
    {
      super.remove( component );
      m_mapComponents.remove( strID );
    }

  } // end remove()

  /**
   * Removes toolbar component at specified index
   * @param ndx The position starting left of the component to remove
   */
  public void remove( int ndx )
  {
    Object objComp = super.getComponent( ndx );

    if ( objComp instanceof VwButton )
    {
      super.remove( ndx );

      m_mapComponents.remove( ((VwButton)objComp).getName() );
    }

   } // end remove()


  /**
   * Get the menue instance by its component text
   * @param strID The ID of the component to retrieve
   * @return
   */
  public JComponent getComponent( String strID )
  { return (VwButton)m_mapComponents.get(  strID ); }


  /**
   * Gets an VwButton at the specified index position
   * @param ndx
   * @return
   */
  public JComponent getComponentFromIndex( int ndx )
  {
    Object objComp = super.getComponent( ndx );

    return (VwButton)objComp;

  }


  /**
   * Get the index of the component
   *
   * @param strCompID The component ID of the component item to get the index for
   * @return he index position on the toolBar or -1 if the component does not exist
   */
  public int getIndex( String strCompID )
  {
    int nCount = super.getComponentCount();

    for ( int x = 0; x < nCount; x++ )
    {
      Object objComp = super.getComponent( x );

      if ( objComp instanceof VwButton )
      {
        if (((VwButton)objComp).getName().equalsIgnoreCase( strCompID ) )
          return x;

      }
    }

    return -1; // Not found
  }


  /**
   * Gets an Iterator to actions for this toolbar
   * @return
   */
  public Iterator getActions()
  {
    List listActions = new LinkedList();

    for ( Iterator iComp = m_mapComponents.values().iterator(); iComp.hasNext(); )
    {
      Object comp = (JComponent)iComp.next();

      if ( comp instanceof VwComboBox )
        listActions.add( ((VwComboBox)comp).getAction() );
      else
      if ( comp instanceof AbstractButton )
        listActions.add( ((AbstractButton)comp).getAction() );

    }

    return listActions.iterator();

  } // end getActions()

} // end clas VwToolBar{}

// *** End of VwToolBar.java ***

