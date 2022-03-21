/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwPropBasePanel.java

Create Date: Apr 11, 2003
============================================================================================
*/
package com.vozzware.ui;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.AncestorEvent;
import java.awt.Button;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;

public class VwPropBasePanel extends JPanel
{
  private boolean m_fFound = false;

  private Component m_compOkButton;     // OK button on property dialog
  private Dialog    m_dialogParent = null;

  public VwPropBasePanel()
  {
    try
    {
      jbInit();
    }
    catch(Exception ex)
    {
      ex.printStackTrace();
    }
  }


  public Dialog getDialogParent()
  { return m_dialogParent; }


  private void jbInit() throws Exception
  {
    this.addAncestorListener(new javax.swing.event.AncestorListener()
    {

      public void ancestorAdded(AncestorEvent e)
      {
      }

      public void ancestorMoved(AncestorEvent e)
      {
        this_ancestorMoved(e);
      }

      public void ancestorRemoved(AncestorEvent e)
      {
      }
    });
    this.setLayout(null);
  }

  /**
   * Enable the OK button
   */
  protected void enableOK()
  {
    if ( m_compOkButton != null )
      m_compOkButton.setEnabled( true );

  }


  /**
   * close the dialog
   */
  protected void close()
  {
    m_dialogParent.dispose();

  }
  void this_ancestorMoved(AncestorEvent e)
  {
    m_fFound = false;

    Container c = getParent();
    while( c != null )
    {

      if ( c instanceof Dialog )
      {
        m_dialogParent = (Dialog)c;
        break;
      }

      c = c.getParent();
    }

    if ( c == null )
    {
      JOptionPane.showMessageDialog( this, "Could Not Find Dialog parent" );
      return;
    }

    Component[] ac = c.getComponents();

    m_compOkButton = findButton( ac );

    if ( m_compOkButton != null )
      m_compOkButton.setEnabled( false );
    else
      JOptionPane.showMessageDialog( this, "Could Not Find Expected Ok Button" );

  } // end this_ancestorMoved(AncestorEvent e)


  /**
   * Find the Ok button on the vendor's IDE property panel
   *
   * @param ac Array of Component objects
   *
   * @return The button object (Component) if found else null
   */
  private Component findButton( Component[] ac )
  {
    if ( ac == null )
      return null;

    // *** Loop thru Components for Button classes

    for ( int x = 0; x < ac.length; x++ )
    {
      Component c = ac[ x ];
      if ( c instanceof Button  )
      {
        if ( ((Button)c).getLabel().equalsIgnoreCase( "ok" ) )
          return c;
      }
      else
      if ( c instanceof JButton  )
      {

        if ( ((JButton)ac[x]).getText().equalsIgnoreCase( "ok" ) )
          return c;
      }
      else
      //if ( c.getClass().getColName().equalsIgnoreCase( "com.borland.jbcl.control.ButtonControl" ) // Borlands  IDE
      //{
      //  if ( ((com.borland.jbcl.control.ButtonControl)ac[x]).getLabel().equalsIgnoreCase( "ok" )  )
      //    return c;
      //}
      //else
      if ( c instanceof Container )
      {
        c =  findButton( ((Container)ac[ x ]).getComponents() );
        if ( c != null )
          return c;
      }

    } // end for()

    return null;

  } // end findButton()

} // end class VwPropBasePanel{}

// end of VwPropBasePanel.java ***

