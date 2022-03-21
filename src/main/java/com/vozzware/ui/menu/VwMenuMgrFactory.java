/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwMenuMgrFactory.java

Create Date: Apr 11, 2003
============================================================================================
*/
package com.vozzware.ui.menu;

public class VwMenuMgrFactory
{
  private static VwMenuMgrFactory s_instance = null;
  private static VwMenuMgr s_menuMgr = null;
  
  public synchronized static VwMenuMgrFactory getInstance()
  {
    if ( s_instance == null )
      s_instance = new VwMenuMgrFactory();
    
    return s_instance;
    
  }
  private VwMenuMgrFactory()
  {
    ;
  }
  
  public VwMenuMgr getMenuMgr()
  { 
    return s_menuMgr;
  }
  
  public void registerInstance( VwMenuMgr menuMgr ) throws Exception
  {
    if ( s_menuMgr != null )
      s_menuMgr = null;
    
    s_menuMgr = menuMgr;
    
  }
  
  public void unRegisterInstance( VwMenuMgr menuMgr ) throws Exception
  {
    s_menuMgr = null;
  }
  
} // end class VwMenuMgrFactory{}
// *** end of VwMenuMgrFactory.java ***

