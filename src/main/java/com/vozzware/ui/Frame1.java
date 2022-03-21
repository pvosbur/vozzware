package com.vozzware.ui;

import com.vozzware.xml.VwDataObject;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowEvent;

public class Frame1 extends JFrame
{
  JPanel contentPane;
  BorderLayout borderLayout1 = new BorderLayout();
  JScrollPane jScrollPane1 = new JScrollPane();
  VwTable m_table = new VwTable();


  //Construct the frame
  public Frame1()
  {
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    try
    {
      jbInit();
      setup();

    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
  }

  private void setup()
  {
    VwDataObject dobj = new VwDataObject( true, true );
    final JComboBox box = new JComboBox( new Object[]{ "Red","Green","Blue" } );

    VwTableColAttr attr = new VwTableColAttr( "Subject", false );
    attr.setWidth( 150 );
    dobj.put( "Subject", attr );

    attr = new VwTableColAttr( "Name", true );
    attr.setWidth( 70 );
    dobj.put( "Name", attr );

    attr = new VwTableColAttr( "Favorate_Color", new DefaultCellEditor( box ), null );
    attr.setWidth( -1 );
    dobj.put( "Favorate_Color", attr );
    m_table.setRowHeight( 20 );


    final VwDataObjDataModel model = new VwDataObjDataModel( m_table, dobj, false, 0, 0 );


    dobj = new VwDataObject( true, true );
    dobj.put( "Subject", "Math" );
    dobj.put( "Name", "Matt" );
    dobj.put( "Favorate_Color", "red" );
    model.addRow( dobj );

    /*

    m_table.addMouseListener( new MouseAdapter()
    {
       int nCount = -1;

      public void mouseClicked( MouseEvent me )
      {
        if ( ++nCount == 0 )
        {
          m_btModel.removeColumn( "Name" );
        }
        else
        {
          m_btModel.removeColumn(  "Favorate_Color" );

        }

      }
    });

    */
    /*
    dobj = new VwDataObject( true, true );
    dobj.put( "Subject", "English" );
    dobj.put( "Name", "Kelley" );
    dobj.put( "Favorate_Color", "blue" );
    m_btModel.addRow( dobj );
    */

  }

  //Component initialization
  private void jbInit() throws Exception
  {
    contentPane = (JPanel) this.getContentPane();
    contentPane.setLayout(borderLayout1);
    this.setSize(new Dimension(600, 300));
    this.setTitle("Frame Title");
    contentPane.add(jScrollPane1, BorderLayout.CENTER);
    jScrollPane1.getViewport().add(m_table, null);

  }
  //Overridden so we can exit when window is closed
  protected void processWindowEvent(WindowEvent e)
  {
    super.processWindowEvent(e);
    if (e.getID() == WindowEvent.WINDOW_CLOSING)
    {
      try
      {
      VwDataObjDataModel model = (VwDataObjDataModel)m_table.getModel();
      model.flush();
      VwDataObject dobjRow = model.getRowData( 2 );

      System.out.println( "Subject: " + dobjRow.get( "Subject" ) );
      System.out.println( "Name: " + dobjRow.get( "Name" ) );
      System.out.println( "Color: " + dobjRow.get( "Favorate_Color" ) );
      }
      catch( Exception ex )
      { ; }

      System.exit(0);
    }
  }
}