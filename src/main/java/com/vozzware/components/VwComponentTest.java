/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwComponentTest.java

============================================================================================
*/

package com.vozzware.components;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


public class VwComponentTest
{

  JFrame  m_frameTestApp;

  VwComponentTest()
  {

    m_frameTestApp = new JFrame( "Test Vw Wizard" );

    m_frameTestApp.addWindowListener( new WindowAdapter()
                                      {

                                        public void windowClosing( WindowEvent we )
                                        {
                                          System.exit( 0 );

                                        }
                                       } );


    GridLayout gl = new GridLayout( 0,4 );

    JPopupMenu pm = new JPopupMenu();
    pm.add( new JMenuItem( "Test1" ) );
    pm.addSeparator();
    pm.add( new JMenuItem( "Test -- 999" ) );


    //gl.setHgap( 20 );
    //gl.setVgap( 20 );

    VwIconWindow iw1 = new VwIconWindow( "j:\\image\\OrbConnect.gif", "s1" );
    iw1.setPupupMenu( pm );
    VwIconWindow iw2 = new VwIconWindow( "j:\\image\\OrbConnect.gif", "Data Module App Server" );
    VwIconWindow iw3 = new VwIconWindow( "j:\\image\\OrbConnect.gif", "Order It" );
    VwIconWindow iw4 = new VwIconWindow( "j:\\image\\OrbConnect.gif", null );
    iw4.setPupupMenu( pm );



    JPanel pl = new JPanel( gl );
    pl.setPreferredSize( new Dimension( 600, 600 ) );
    JScrollPane sp = new JScrollPane( pl );

    m_frameTestApp.getContentPane().add( sp );
    pl.add( iw2 );
    pl.add( iw3 );
    pl.add( iw4 );
    pl.add( iw1 );



    //VwWizardMgr wz = new VwWizardMgr( m_frameTestApp, " Test Wizard", false );

    //wz.add( new Panel1(),null );
    //wz.add( new Panel2(),null );
    //wz.add( new Panel3(),null );
    //wz.show();
    m_frameTestApp.show();
  }

  public static void main( String[] args )
  {
    try
    {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    }
    catch(Exception e)
    {
    }

    new VwComponentTest();

  } // end main


} // end VwComponentTest{}


// *** End of VwComponentTest.java ***