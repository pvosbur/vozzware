/*
===========================================================================================

 
                               Copyright(c) 2000 - 2006 by

                      V o z z W a r e   L L C (Vw)

                                 All Rights Reserved

Source Name: VwTableColumnModel.java

Create Date: May 21, 2006

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.


============================================================================================
*/
package com.vozzware.ui;

import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import java.util.List;

public class VwTableColumnModel extends DefaultTableColumnModel
{
  private List m_listColAttrs;
  
  public VwTableColumnModel( List listColAttrs )
  {
    m_listColAttrs = listColAttrs;
  }
  public  void addColumn( TableColumn tc )
  {
    int ndx = tc.getModelIndex();
    return;
    
  }
}
