/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwFinderUI.java

Create Date: Apr 11, 2003
============================================================================================
*/
package com.vozzware.ui;

import com.vozzware.util.VwFileFinder;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class VwFinderUI extends VwPanel
{
  private BorderLayout borderLayout1 = new BorderLayout();
  private JPanel jPanel1 = new JPanel();
  private JLabel jLabel1 = new JLabel();
  private JTextField m_txtFileName = new JTextField();
  private JLabel jLabel2 = new JLabel();
  private JTextField m_txtSearchDir = new JTextField();
  private JButton m_btnSearchDir = new JButton();
  private TitledBorder titledBorder1;
  private JPanel jPanel2 = new JPanel();
  private TitledBorder titledBorder2;
  private JCheckBox m_chkSearchArchives = new JCheckBox();
  private JCheckBox m_chkRecurseArchives = new JCheckBox();
  private JCheckBox m_chkSearchArchivesOnly = new JCheckBox();
  private JLabel jLabel3 = new JLabel();
  private JTextArea m_taOutput = new JTextArea();
  private JScrollPane jScrollPane1 = new JScrollPane();
  private VwTextComponentOutputStream m_outs = new VwTextComponentOutputStream( m_taOutput );
  private Action	m_actionSearch;

  private FlowLayout m_flow = new FlowLayout();
  private JCheckBox m_chkIncludeSubDirs = new JCheckBox();

  private String    m_strArchivePath;   // Path within archive

	/**
	 *
	 */
	public VwFinderUI()
  {
    try
    {

      jbInit();
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
  }
  private void jbInit() throws Exception
  {
    titledBorder1 = new TitledBorder(BorderFactory.createEtchedBorder(Color.white,new Color(165, 163, 151)),"Search Criteria");
    titledBorder2 = new TitledBorder(BorderFactory.createEtchedBorder(Color.white,new Color(165, 163, 151)),"Search Options");
    this.setLayout(borderLayout1);
    jPanel1.setLayout(null);

    jLabel1.setFont(new java.awt.Font("Dialog", 1, 11));
    jLabel1.setText("Enter all or part of file name:");
    jLabel1.setBounds(new Rectangle(15, 19, 167, 19));
    m_txtFileName.setText("");
    m_txtFileName.setBounds(new Rectangle(15, 41, 178, 26));
    jLabel2.setFont(new java.awt.Font("Dialog", 1, 11));
    jLabel2.setText("Start search in:");
    jLabel2.setBounds(new Rectangle(15, 78, 177, 19));
    m_txtSearchDir.setText("");
    m_txtSearchDir.setBounds(new Rectangle(15, 100, 178, 26));
    m_btnSearchDir.setBounds(new Rectangle(200, 103, 71, 23));
    m_btnSearchDir.setFont(new java.awt.Font("Dialog", 1, 14));
    m_btnSearchDir.setText("...");
    m_btnSearchDir.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
	       m_btnSearchDir_actionPerformed(e);
      }
    });
    jPanel1.setBorder(titledBorder1);
    jPanel2.setBorder(titledBorder2);
    jPanel2.setBounds(new Rectangle(322, 22, 217, 145));
    jPanel2.setLayout(null);
    m_chkSearchArchives.setToolTipText("(.zip,.jar,.war and .ear files)");
    m_chkSearchArchives.setText("Search Standrad Archives");
    m_chkSearchArchives.setBounds(new Rectangle(17, 47, 192, 23));
    m_chkRecurseArchives.setToolTipText("Search archives that are in archives");
    m_chkRecurseArchives.setText("Recurse Archives");
    m_chkRecurseArchives.setBounds(new Rectangle(17, 74, 175, 23));
    m_chkSearchArchivesOnly.setText("Search Archives Only");
    m_chkSearchArchivesOnly.setBounds(new Rectangle(17, 106, 175, 23));
    jLabel3.setText("Results");
    jLabel3.setBounds(new Rectangle(240, 150, 51, 15));
    m_taOutput.setText("");
    jScrollPane1.setBounds(new Rectangle(13, 197, 525, 193));
    m_chkIncludeSubDirs.setText("Include sub directories");
    m_chkIncludeSubDirs.setBounds(new Rectangle(17, 19, 186, 23));
    this.add(jPanel1, BorderLayout.CENTER );
		 jPanel1.setPreferredSize( new Dimension ( 500, 200 ) );
    jPanel1.add(jLabel1, null);
    jPanel1.add(m_txtFileName, null);
    jPanel1.add(jLabel2, null);
    jPanel2.add(m_chkRecurseArchives, null);
    jPanel2.add(m_chkSearchArchives, null);
    jPanel2.add(m_chkIncludeSubDirs, null);
    jPanel1.add(jLabel3, null);
    jPanel1.add(jScrollPane1, null);
    jScrollPane1.getViewport().add(m_taOutput, null);
    jPanel1.add(m_txtSearchDir, null);
    jPanel1.add(m_btnSearchDir, null);
    jPanel1.add(jPanel2, null);

     m_actionSearch = new AbstractAction( "Search" )
	 {
			 public void actionPerformed ( ActionEvent ae )
			 {
				 doSearch();
       }
	};


	   this.addComponentListener( new ComponentAdapter()
		 {
			 public void componentResized( ComponentEvent ce )
			 {
					Dimension dim = ce.getComponent().getSize();
					Dimension dimSize  = jScrollPane1.getSize();
					dimSize.width = dim.width;
					jScrollPane1.setSize( dimSize );
					jScrollPane1.validate();
}
		 });
  }


  /**
   * Returns the serach action
   * @return
   */
  public Action getSearchAction()
  { return m_actionSearch; }


  /**
   * Start the filel search
   */
  private void doSearch()
  {
    String strPath = m_txtSearchDir.getText();
    String strFile = m_txtFileName.getText();

    int nPos = strFile.lastIndexOf( "/" );

    if ( nPos >= 0 )
    {
      m_strArchivePath = strFile.substring( 0, nPos );
      strFile = strFile.substring( ++nPos );

    }
    
    m_taOutput.setText( "" );

    boolean fIncludeSubDirs = m_chkIncludeSubDirs.isSelected();
    boolean fIncludeArchives = m_chkSearchArchives.isSelected();
    boolean fSearchArchivesOnly = m_chkSearchArchivesOnly.isSelected();
    try
    {
      VwFileFinder finder = new VwFileFinder( strPath, strFile, m_strArchivePath, fIncludeSubDirs, fIncludeArchives, fSearchArchivesOnly,  m_outs );
      finder.find();
    }
    catch( Exception ex )
    {
      JOptionPane.showMessageDialog( null, ex.toString() );

    }
  }

  void m_btnSearchDir_actionPerformed(ActionEvent e)
  {

  }

  
  public static void main( String[] args )
  {
    try
    {
      final JFrame  frame = new JFrame( "File Finder" );
      VwFinderUI finderUI = new VwFinderUI();
      frame.setContentPane( finderUI );
      frame.pack();
      
      SwingUtilities.invokeLater( new Runnable() 
      {
        public void run()
        {
          frame.setVisible( true );
        }
        
      });
      
    }
    catch ( Exception ex )
    {

    }
  } // end main()
} // class VwFinderUI{}

// *** End of VwFinderUI.java

  
