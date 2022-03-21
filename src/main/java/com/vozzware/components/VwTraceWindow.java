/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwTraceWindow.java

============================================================================================
*/

package com.vozzware.components;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class VwTraceWindow extends JPanel implements Runnable
{
  private BorderLayout borderLayout1 = new BorderLayout();
  private JPanel m_viewerPanel = new JPanel();
  private BorderLayout borderLayout2 = new BorderLayout();
  private BorderLayout borderLayout3 = new BorderLayout();
  private JPanel m_actionPanel = new JPanel();
  private JTextArea m_taTraceText = new JTextArea();
  private JScrollPane m_textScroller = new JScrollPane( m_taTraceText );
  private JButton     m_btnClear = new JButton( "Clear" );


  private int         m_nPort; // The Port nbr to listen on

  /**
   * Innser class to handle the incomming socket data
   */
  class Data
  {
    private ServerSocket  m_servSocket = null;

    /**
     * Constructor
     *
     * @param nPort The port nbr to listen on
     */
    Data( int nPort ) throws IOException
    {
      m_servSocket = new ServerSocket( nPort );

    } // end Data()

    /**
     * Closes the socket
     */
    void close() throws IOException
    {
      m_servSocket.close();
    }

    /**
     * Listens for incommimg data and writes it to the JTextArea
     */
    void startListener() throws IOException
    {
      while( true )
      {
        Socket soc = m_servSocket.accept();

        java.io.InputStreamReader is =
          new java.io.InputStreamReader( soc.getInputStream() );

        java.io.BufferedReader inStream = null;

        inStream = new java.io.BufferedReader( is );

        while( true )
        {
          String s = inStream.readLine();
          if ( s == null )
            break;

          m_taTraceText.append( s + "\r\n" );

        } // end while

        inStream.close();
        soc.close();

      } // wnd while

    }
  } // end class data


  /**
   * Constructor
   *
   * @param The Port nbr to listen on
   */
  public VwTraceWindow( int nPort )
  {
    try
    {
      jbInit();
      m_nPort = nPort;
      m_btnClear.addActionListener( new ActionListener()
                                    {
                                      public void actionPerformed( ActionEvent ae )
                                      {
                                        m_taTraceText.setText( "" );
                                      }

                                     });

      m_taTraceText.setEditable( false );
      Thread t = new Thread( this );
      t.start();
    }
    catch(Exception ex)
    {
      ex.printStackTrace();
    }
  }

  private void jbInit() throws Exception
  {
    this.setLayout(borderLayout1);
    m_viewerPanel.setLayout(borderLayout2);
    m_actionPanel.setLayout(borderLayout3);
    this.add(m_viewerPanel, BorderLayout.CENTER);
    m_viewerPanel.add(m_textScroller, BorderLayout.CENTER);
    m_actionPanel.add( m_btnClear, BorderLayout.CENTER );
    this.add(m_actionPanel, BorderLayout.SOUTH);
  }


  /**
   * The the width and height of the viewer. This panel will resize the textarea
   * panel and action button panels
   *
   * @param dimSize The total viewing width and height of the component
   */
  public void setSize( Dimension  dimSize )
  {
    // *** get height of the action button panel and we will resize
    // *** the text viwer panel based on parent panels total height
    // *** and width

    int nActionHeight = (int)m_actionPanel.getHeight();
    int nHeight = (int)dimSize.getHeight() - nActionHeight;
    m_viewerPanel.setSize( nHeight ,
                           (int)dimSize.getWidth() );

    m_actionPanel.setSize( nActionHeight, (int)dimSize.getWidth() );
    super.setSize( dimSize );
    super.revalidate();
  }


  public void run()
  {
    try
    {

      Data data = new Data( m_nPort );
      data.startListener();
    }
    catch( Exception e )
    {
      System.out.println( e.toString() );

    }
  }
}//end class VwTraceWindow{}

//*** End of VwTraceWindow.java ***
