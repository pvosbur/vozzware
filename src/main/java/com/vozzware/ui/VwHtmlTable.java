/*
============================================================================================

                       V o z z W o r k s   C o d e   G e n e r a t o r

                                    Copyright(c) 2016 By

                        V   o   z   z   w   a   r   e   L   L   C   .

                            A L L   R I G H T S   R E S E R V E D

    Source File Name: VwHtmlTable.java

    Author:           Armored Info LLC

    Date Generated:   04-3-2016

============================================================================================
*/

package com.vozzware.ui;

import com.vozzware.util.VwBeanUtils;

import java.util.List;


public class VwHtmlTable
{
  private List m_listUserDataObjects;
  private StringBuffer m_sbTableHtml = new StringBuffer( "<table" );
  private String[] m_astrPropNameOrder;
  private VwColDescriptor[] m_aColDescriptors;

  public VwHtmlTable()
  {

  }

  public String toHtml( List listUserDataObjects, String[] astrPropNameOrder ) throws Exception
  {
    return toHtml( listUserDataObjects, astrPropNameOrder, null, null );
  }

  /**
   * Creates an HTML table with options to add CSS inline styles
   * @param listUserDataObjects The object that contains the data for each <td></td>
   * @param astrPropNameOrder   The order in which the <td></td> should be created. Use the listUserDataObjects property name for order.
   * @param aColDescriptors     The column descriptors for all inline CSS. This array contains the inline CSS style
   * @param strTableInlineCss   The string css value for <table></table> inline css
   * @return
   * @throws Exception
   */
  public String toHtml( List listUserDataObjects, String[] astrPropNameOrder, VwColDescriptor[] aColDescriptors, String strTableInlineCss ) throws Exception
  {
    m_listUserDataObjects  = listUserDataObjects;
    m_astrPropNameOrder = astrPropNameOrder;
    m_aColDescriptors = aColDescriptors;

    if ( strTableInlineCss != null )
    {
      m_sbTableHtml.append( " style='" );
      m_sbTableHtml.append( strTableInlineCss ).append( ">" );
    }
    else
    {
      m_sbTableHtml.append( ">" );
    }


    for ( int x = 0, nLen = m_listUserDataObjects.size(); x < nLen; x++ )
    {
      generateRow( m_listUserDataObjects.get( x ) );
    }

    m_sbTableHtml.append( "</table>" );

    return m_sbTableHtml.toString();
  }

  /**
   * Generates the <tr></tr> rows
   * @param objRow  The object data value for the <td></td>
   * @throws Exception
   */
  private void generateRow( Object objRow ) throws Exception
  {
    m_sbTableHtml.append( "<tr>" );

    generateCol( objRow );

    m_sbTableHtml.append( "</tr>" );
  }

  /**
   * Generates the <td></td> columns
   * @param objRow  The object data value for the <td></td>
   * @throws Exception
   */
  private void generateCol( Object objRow ) throws Exception
  {

    for ( int x = 0, nLen = m_astrPropNameOrder.length; x < nLen; x++ )
    {
      Object objVal = VwBeanUtils.getValue( objRow, m_astrPropNameOrder[ x ] );

      String strVal = null;

      if ( objVal != null )
      {
        strVal = objVal.toString();
      }

      m_sbTableHtml.append( "<td" );

      if ( m_aColDescriptors != null )
      {
        processColDescriptor( m_aColDescriptors[ x ], strVal );
      }
      else
      {
        m_sbTableHtml.append( ">" );

        addString( strVal );
      }

      m_sbTableHtml.append( "</td>" );
    }

  }

  /**
   * Processes the CSS inline styles for each <td></td> column
   * @param colDescriptor The object array with the inline CSS data
   * @param strVal        The value for the <td></td>
   */
  private void processColDescriptor( VwColDescriptor colDescriptor, String strVal )
  {
    String strTdInlineCss = colDescriptor.getTdInlineCss();

    if ( strTdInlineCss != null )
    {
      m_sbTableHtml.append( " style='" ).append( strTdInlineCss ).append( "'" );
    }

    m_sbTableHtml.append( ">" );

    switch ( colDescriptor.getColType() )
    {
      case img:

           addImage( colDescriptor, strVal );
           break;

      case string:

           addString( strVal );
           break;

    } // end switch()
  }

  /**
   * Adds the <img> tag with its corresponding src url
   * @param colDescriptor The object with <img> inline CSS style data
   * @param strImgSrc     The <img> src url
   */
  private void addImage( VwColDescriptor colDescriptor, String strImgSrc )
  {
    m_sbTableHtml.append( "<img src='" ).append( strImgSrc ).append( "'" );

    String strImgInlineCss = colDescriptor.getImgInlineCss();

    if ( strImgInlineCss != null )
    {
      m_sbTableHtml.append( " style='").append( strImgInlineCss ).append( "'" );
    }

    m_sbTableHtml.append( "/>" );
  }

  /**
   * Appends the value in the <td></td>
   * @param strValue  The <td></td> value
   */
  private void addString( String strValue )
  {
    m_sbTableHtml.append( strValue );
  }

}
