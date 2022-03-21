/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwReportModel.java

============================================================================================
*/

package com.vozzware.print;

/**
 * This interface defines a m_btModel for a specific report implementaion. The
 * implementor defines the names of the dynamic report headers and footers that
 * will be updated from data retrieved by the report implementor. The report
 * implementor should build a logical page in memory for the maximum number of
 * detail lines specified in each call to buildPage(). The default ReportViewer
 * will continually call this method until False is returned (indicating the
 * report is done), or an Exception is thrown.  After each call to buildPage()
 * that returns True, the default ReportViewer will call getDymanicHdrData()
 * for any dynamic headers specified, then getDetailLineData() until null is
 * returned, and finally getDymanicFooterData() for any dynamic footers
 * specified.
 */
public interface VwReportModel
{
  /**
   * Builds a logical page for the max nbr of detail line items specified. The
   * report object implementation should return True if there is a page to
   * print, otherwise False to indicate there is no more report data.
   *
   * @param nMaxDetailLines The maximum number of detail lines to build
   *
   * @exception Exception if any errors occur
   */
  public abstract boolean buildPage( int nMaxDetailLines ) throws Exception;


  /**
   * Get an Object ( a Data value object / VwDataObject) for the dynamic header data as specified by the
   * header name.
   *
   * @param strHdrName The name of the header to retrieve the data for
   *
   * @return An VwDataObject containing the header data
   *
   * @exception Exception if the header name requested was not previously
   * added with the addDynamicHdr() method.
   */
  public abstract Object getDynamicHdrData( String strHdrName ) throws Exception;


  /**
   * Get an Object ( a Data value object / VwDataObject) for the dynamic footer data as specified by the
   * footer name.
   *
   * @param strFooterName The name of the footer to retrieve the data for
   *
   * @return an VwDataObject containing the footer data
   *
   * @excepion Exception if the footer name requested was not previously
   * added with the addDynamicFooter() method.
   */
  public abstract Object getDynamicFooterData( String strFooterName ) throws Exception;


  /**
   * Gets an Object ( a Data value object / VwDataObject) containing the detail line columns for the next
   * detail line on the logical page. This method should return null when
   * all of the detail lines for a logical page have been printed, or when
   * the MaxDetailLines allowed for a page (see the buildPage() method) has
   * been reached.
   *
   * @return An VwDataObject containing the detail line columns or null if
   * no more detail lines exist for the logical page.
   */
  public abstract VwDetailData getDetailLineData();


} // end interface VwReportModel{}


// *** End VwReportModel.java ***
