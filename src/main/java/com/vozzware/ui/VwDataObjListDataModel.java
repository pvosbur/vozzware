/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwDataObjListDataModel.java

Create Date: Apr 11, 2003
============================================================================================
*/
package com.vozzware.ui;

import com.vozzware.db.VwColInfo;
import com.vozzware.util.VwFormat;

import javax.swing.AbstractListModel;
import java.sql.DatabaseMetaData;
import java.util.List;

/**
 * This class provides the functionality for the AbstractTableModel using
 * dataobjects to store table rows. The column data is stored a name value pairs in the
 * data object.
 */
public class VwDataObjListDataModel extends AbstractListModel
{

  private   List<VwColInfo>        m_listData;   // List of data object results

  /**
   * Constructor
   *
   * @param dobjlistData List of data objects that will be used to pupulate the list box
   * @param strDobjKey The key in the data object to use for list data
   *
   */
  public void setData( List<VwColInfo> listData )
  {
    m_listData = listData;

    this.fireContentsChanged( this, 0, m_listData.size() );

  } // end


  /**
   * Gets the size of the list
   */
  public int getSize()
  {
    if ( m_listData == null )
      return 0;

    return m_listData.size();
  }

  /**
   * Gets the data for the index requested
   *
   */
  public Object getElementAt( int x )
  {
    VwColInfo ci = (VwColInfo)m_listData.get( x );
    String strLine = VwFormat.left( ci.getTableName() + "." + ci.getColumnName(), 35, ' ' );
    strLine += VwFormat.left( ci.getSQLTypeName(), 10, ' ' );
    strLine += VwFormat.right( String.valueOf( ci.getColSize() ), 7, ' ' );
    strLine += VwFormat.right( String.valueOf( ci.getNbrDecimalDigits() ), 6, ' ' ) + "         ";

    String strAllowNulls = "NO";

    if ( DatabaseMetaData.columnNullable == ci.getNullable() )
      strAllowNulls = "YES";

    strLine += strAllowNulls;

    return strLine;

  }


  /**
   * Clears the List
   */
  public void clear()
  {
    if ( m_listData != null )
    {
      m_listData.clear();

      this.fireIntervalRemoved( this, 0, 0 );

    }

  } // end clear

} // end class VwDataObjListDataModel{}

// *** End of VwDataObjListDataModel.java ***
