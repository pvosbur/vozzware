/*
===========================================================================================

 
                               Copyright(c) 2000 - 2006 by

                      V o z z W a r e   L L C (Vw)

                                 All Rights Reserved

Source Name: VwDbConnection.java

Create Date: Oct 11, 2006

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.


============================================================================================
*/
package com.vozzware.db.util;

import com.vozzware.db.VwDatabase;

public class VwDbConnection extends VwConnection
{
  private VwDatabase   m_db;
  
  public VwDbConnection( VwDatabase db )
  { m_db = db; }
  
  public VwDatabase getDatabase()
  { return m_db; }
  
}
