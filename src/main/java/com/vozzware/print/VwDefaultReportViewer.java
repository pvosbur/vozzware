/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwDefaultReportViewer.java

============================================================================================
*/

package com.vozzware.print;

import com.vozzware.util.VwDocFinder;
import com.vozzware.xml.VwXmlToBean;
import org.xml.sax.InputSource;

import javax.xml.schema.util.XmlFeatures;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * This class implements a default report View consisting of header,
 * detail line, and footer information.
 */
public class VwDefaultReportViewer implements Printable
{
  class HdrFooterSpec
  {
    String      m_strName;        // The name of the header/footor
    VwLineView m_lineView;       // The LineViewer used to render the report line

    HdrFooterSpec( String strName, VwLineView lineView )
    {
      m_strName = strName;
      m_lineView = lineView;
    }

  }

  private VwReportModel        m_rptModel;             // The document suppling the report data to print

  private List                  m_listHdrs;             // List of HdrFootorSpec's that are the report headers
  private List                  m_listFooters;          // List of HdrFootorSpec's that are the report headers

  private Map                   m_mapDetailLineViews;   // Map of detail line views

  private double                m_dblLeftMargin = .5;   // Left page margin
  private double                m_dblRightMargin = .5;  // Rigth page margin
  private double                m_dblTopMargin = .5;    // Top page margin
  private double                m_dblBotMargin = .5;    // Bottom page margin

  private PageFormat            m_pf;                   // Page format object

  private VwReportSpec         m_rptSpec;              // The parsed report specification xml 
  
  private int                   m_nCurYPos = 0;         // Current Y index pod from top of page

  private int                   m_nSpaceLineHeight = 12;

  private Rectangle             m_rctView;              // Rectangle containg the page size

  private int                   m_prevNdx = -1;

  private int                   m_nMaxDetailLines = 30;

  /**
   * Constructor
   *
   * @param rptDoc An VwReportModel implementor that supplies the report data
   * @param pf A PageFormat object (not used; a PageFormat object based upon the
   * the default page of the PrinterJob is used internally).
   */
  public VwDefaultReportViewer( VwReportModel rptDoc, URL urlReportSpec, PageFormat pf ) throws Exception
  {
    m_rptModel = rptDoc;

    m_rctView = new Rectangle();

    m_pf = pf;

    m_listHdrs = new ArrayList();
    m_listFooters = new ArrayList();

    m_mapDetailLineViews = new HashMap();

    m_rptSpec = getReportSpec( urlReportSpec );
    
  } // end VwDefaultReportViewer()



  /**
   * @param urlReportSpec
   * @return
   */
  private VwReportSpec getReportSpec( URL urlReportSpec ) throws Exception
  {
    URL urlReportSpecXsd = VwDocFinder.findURL( "/com/itc/print/VwReportSpec.xsd" );
    
    if ( urlReportSpecXsd == null )
      throw new Exception( "Cannot find VwReportSpec.xsd in the expected package com/itc/print" );
    
    VwXmlToBean xtb = new VwXmlToBean();
    xtb.setFeature( XmlFeatures.ATTRIBUTE_MODEL, true );
    
    return (VwReportSpec)xtb.deSerialize( new InputSource( urlReportSpec.openStream() ), VwReportSpec.class, urlReportSpecXsd );
    
  } //end getReportSpec()



  /**
   * Prints the report on the printer selected from the print dialog.
   *
   * @param fShowPrintDlg Whether or not to display the standard Print Dialog
   *
   * @exception Throws Exception if any errors occur
   */
  public boolean printReport( boolean fShowPrintDlg ) throws Exception
  {
    // *** Get a printer job object from the factory

    PrinterJob pg = PrinterJob.getPrinterJob();

    if ( fShowPrintDlg )
    {
      if ( !pg.printDialog() )
        return false;                   // User hit cancel
    }

    m_pf  = pg.defaultPage();

    m_rctView.x = (int)(m_pf.getImageableX() * m_dblLeftMargin);
    m_rctView.y = (int)(m_pf.getImageableY() * m_dblTopMargin);

    m_rctView.width = (int)m_pf.getWidth() - m_rctView.x -
      (int)(m_pf.getImageableX() * m_dblRightMargin);

    m_rctView.height = (int)m_pf.getHeight() - m_rctView.y -
      (int)(m_pf.getImageableY() * m_dblBotMargin);

    pg.setPrintable( this );

    // *** Print the report

    pg.print();

    return true;

  } // end printReport()


  /**
   * Sets the max detail lines per page property
   *
   * @param nMaxDetailLines The max detail lines per page
   */
  public void setMaxDetailLines( int nMaxDetailLines )
  { m_nMaxDetailLines = nMaxDetailLines; }


  /**
   * Gets the max detail lines per page property value
   *
   * @param The max detail lines per page
   */
  public int getMaxDetailLines()
  { return m_nMaxDetailLines; }


  /**
   *
   * Adds a report header VwLineView object.  The headers are printed in
   * the order they were added.
   *
   * @param hdrLineView The VwLineView that renders the header line
   * @param strName The associated name of the line view if the data for the
   * header is from the report document; otherwise, the name can be null.
   *
   */
  public void addHeader( VwLineView hdrLineView, String strName )
  { m_listHdrs.add( new HdrFooterSpec( strName, hdrLineView ) ); }



  /**
   *
   * Adds a report footer VwLineView object.  The footers are printed
   * in the order they were added.
   *
   * @param The VwLineView that renders the footer line
   * @param The associated name of the line view if the data for the footer is
   * from the report document; otherwise, the name can be null.
   *
   */
  public void addFooter( VwLineView hdrLineView, String strName )
  { m_listFooters.add( new HdrFooterSpec( strName, hdrLineView ) ); }


  /**
   * Adds a detail VwLineView object used to render a report detail line.
   * The detail lines are printed in the order they were added.
   *
   * @param lvDetail An VwLineView object for the report detail line
   * @param strDetailViewName The associated name of the detail line view
   */
  public void addDetailLineView( VwLineView lvDetail, String strDetailViewName )
  { m_mapDetailLineViews.put( strDetailViewName, lvDetail ); }


  /**
   * Sets the left report margin in inches (if different from the default of 1/2 inch)
   *
   * @param The margin in inches (.5 would be specified for 1/2 inch, etc.)
   */
  public void setLeftMargin( double dblMargin )
  { m_dblLeftMargin = dblMargin; }


  /**
   * Sets the right report margin in inches (if different from the default of 1/2 inch)
   *
   * @param The margin in inches (.5 would be specified for 1/2 inch, etc.)
   */
  public void setRightMargin( double dblMargin )
  { m_dblRightMargin = dblMargin; }


  /**
   * Sets the top report margin in inches (if different from the default of 1/2 inch)
   *
   * @param The margin in inches (.5 would be specified for 1/2 inch, etc.)
   */
  public void setTopMargin( double dblMargin )
  { m_dblTopMargin = dblMargin; }


  /**
   * Sets the bottom report margin in inches (if different from the default of 1/2 inch)
   *
   * @param The margin in inches (.5 would be specified for 1/2 inch, etc.)
   */
  public void setBottomMargin( double dblMargin )
  { m_dblBotMargin = dblMargin; }


  /**
   * Sets the report margins in inches (if different from the default of 1/2 inch)
   *
   * @param The margin in inches (.5 would be specified for 1/2 inch, etc.)
   */
  public void setMargins( double dblLeftMargin, double dblRightMargin,
                          double dblTopMargin, double dblBotMargin )
  {
    m_dblLeftMargin = dblLeftMargin;
    m_dblRightMargin = dblRightMargin;
    m_dblTopMargin = dblTopMargin;
    m_dblBotMargin = dblBotMargin;

  } // end setMargins()


  /**
   * Required implementation of the Printable interface. This method is called by
   * the PrinterJob class until the NO_SUCH_PAGE constant is returned. Each call
   * to this method should print a page and return the PAGE_EXISTS constant until
   * the ReportDocument object has completed.
   *
   * @param g The Graphics context for the printer being used
   * @param pf A PageFormat object (not used, since a Pageformat object based
   * upon the default page of the PrinterJob is used internally).
   * @param ndx The page index number
   */
  public int print( Graphics g, PageFormat pf, int ndx ) throws PrinterException
  {
    try
    {
      g.setColor( new Color( 20, 20, 20 ));

      g.setClip( m_rctView.x, m_rctView.y, m_rctView.width, m_rctView.height );

      if ( ndx != m_prevNdx )
      {
        m_prevNdx = ndx;

        if ( !m_rptModel.buildPage( m_nMaxDetailLines ) )
          return Printable.NO_SUCH_PAGE;
        else
          return Printable.PAGE_EXISTS;
      }

      printHeadersFooters( g, m_listHdrs );

      VwDetailData detailData = null;
      
      VwLineView lvDetail = null;

      String strName = null;

      while ( (detailData = m_rptModel.getDetailLineData() ) != null )
      {
        lvDetail = (VwLineView)m_mapDetailLineViews.get( detailData.getId() );
        lvDetail.render( detailData.getData(), g, m_rctView, m_nCurYPos, m_nSpaceLineHeight );
        m_nCurYPos = lvDetail.getCurYPos();

      } // end while()

      printHeadersFooters( g, m_listFooters );

    }
    catch( Exception e )
    {
      e.printStackTrace();
      throw new PrinterException( e.toString() );
    }

    return Printable.PAGE_EXISTS;

  } // end print()


  /**
   * Prints all defined headers
   *
   * @param g The Graphics context for the printer being used
   * @param ec A Vector of HdrFooterSpec objects
   *
   * @exception Exception if any errors occur
   */
  private void printHeadersFooters( Graphics g, List listHdrFooters ) throws Exception
  {
    m_nCurYPos = m_rctView.y;

    for ( Iterator iHdrFooter = listHdrFooters.iterator(); iHdrFooter.hasNext(); )
    {
      HdrFooterSpec spec = (HdrFooterSpec)iHdrFooter.next();

      Object objData = null;

      if ( spec.m_strName != null )
      {
        if ( listHdrFooters == m_listHdrs )
          objData = m_rptModel.getDynamicHdrData( spec.m_strName );
        else
          objData = m_rptModel.getDynamicFooterData( spec.m_strName );
      }

      spec.m_lineView.render( objData, g, m_rctView, m_nCurYPos, m_nSpaceLineHeight );

      m_nCurYPos = spec.m_lineView.getCurYPos();

    } // end for()

  }  // end printHeadersFooters()


} // end class VwDefaultReportViewer{}

// *** End VwDefaultReportViewer.java ***







