/*
===========================================================================================

 
                             Copyright(c) 2000 - 2005 by

                      V o z z W a r e   L L C (Vw)

                             All Rights Reserved

Source Name: VwSplashDisplayer.java

Create Date: Oct 28, 2006
============================================================================================
*/
package com.vozzware.ui;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JWindow;
import javax.swing.border.Border;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.net.URL;

public class VwSplashDisplayer extends JWindow
{
  private JLabel     m_lblContentDisplayer;
  private int        m_nSecsToShow;
  private VwIcon    m_icon;
  private Component  m_compCenterEnclosing;
  private boolean    m_fCenter = false;

  public VwSplashDisplayer( Frame frameParent, JLabel lblContent, int nSecsToShow, boolean fCenter ) throws Exception
  {
    super( frameParent );
    m_lblContentDisplayer = lblContent;
    m_nSecsToShow = nSecsToShow;  
    m_fCenter = fCenter;  
    initGUI();
  }

  public VwSplashDisplayer( Frame frameParent, URL urlImage, String strText, int nSecsToShow, boolean fCenter ) throws Exception
  {
    super( frameParent );
    m_nSecsToShow = nSecsToShow;  
    m_fCenter = fCenter;
    m_icon = new VwIcon( urlImage );

    m_lblContentDisplayer = new JLabel( m_icon, JLabel.CENTER );
    
    if ( strText != null )
    {
      m_lblContentDisplayer.setVerticalTextPosition( JLabel.BOTTOM );
      m_lblContentDisplayer.setHorizontalTextPosition( JLabel.CENTER );
      m_lblContentDisplayer.setText( strText );
    }
	
	
  }
  
  private void initGUI()
  {
    this.getContentPane().setLayout( new BorderLayout() );
    VwPanel panel = new VwPanel();
    panel.setLayout( new BorderLayout() );
    
    
    panel.add( m_lblContentDisplayer, BorderLayout.CENTER );
    
    Border border = BorderFactory.createRaisedBevelBorder();
        
    panel.setBorder( border );
    this.getContentPane().add( panel, BorderLayout.CENTER);
    
    this.addComponentListener( new ComponentAdapter()
    {
      public void componentShown(ComponentEvent e) 
      {
        
        VwSplashDisplayer.this.setSize( m_lblContentDisplayer.getPreferredSize() );
        
        if ( m_fCenter  )
        {
          VwDialogBase.center( VwSplashDisplayer.this, m_compCenterEnclosing  );
        }
        
        Thread th = new Thread( new Runnable()
        {
          public void run()
          {
            try
            {
              Thread.sleep( m_nSecsToShow * 1000 );
              dispose();
            }
            catch( Exception ex )
            {
              ;
            }
          }
        });
        
        th.start();
      }
    });
    
   }
  
   /**
    * Centers this splash window inside the enclosing component
    * @param compCenterEnclosing The cnclosing component to center this splash window in, If null this splash window is centered
    * within the desktop
    */
   public void center( Component compCenterEnclosing  )
   { 
     m_fCenter = true;
     m_compCenterEnclosing = compCenterEnclosing;
   }
   
}   


