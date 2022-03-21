/*
 *
 * ============================================================================================
 *
 *                                A r m o r e d  I n f o   W e b
 *
 *                                     Copyright(c) 2012 By
 *
 *                                       Armored Info LLC
 *
 *                             A L L   R I G H T S   R E S E R V E D
 *
 *  ============================================================================================
 * /
 */

package test.vozzware.xml;

import java.util.List;

/*
============================================================================================

    Source File Name: Transaction.java

    Author:           petervosburgh
    
    Date Generated:   6/30/13

    Time Generated:   6:32 AM

============================================================================================
*/
public class VwTestPersonList
{
  private List<VwTestPersonEx>m_listPersons;

  public void setVwTestPersonEx( List<VwTestPersonEx> listPersons)
  {
    m_listPersons = listPersons;
  }

  public List<VwTestPersonEx>getVwTestPersonEx()
  {
     return m_listPersons;
  }
}
