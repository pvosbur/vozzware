package com.vozzware.ui;

import com.vozzware.components.VwTraceWindow;

import javax.swing.JFrame;
import java.awt.BorderLayout;

public class VwDesktopTraceWindow
{

  /**
   * @param args
   */
  public static void main( String[] astrArgs )
  {
    
    if ( astrArgs.length != 1 )
    {
      System.out.println( "Usage: VwDesktopTraceWindow <port number>");
      System.exit( 1 );
    }
    
    String strPort = astrArgs[ 0 ];
    JFrame frame = new JFrame( "VozzWorks Trace Window on Port: " + strPort);
    frame.getContentPane().setLayout( new BorderLayout() );
    VwTraceWindow tw = new VwTraceWindow( Integer.parseInt( strPort ) );
    frame.getContentPane().add( tw , BorderLayout.CENTER );
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize( 800, 400 );
    frame.setVisible( true );

  }

}
