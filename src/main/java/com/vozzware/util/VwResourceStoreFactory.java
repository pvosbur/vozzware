/*
===========================================================================================

 
                             Copyright(c) 2000 - 2005 by

                      V o z z W a r e   L L C (Vw)

                             All Rights Reserved

Source Name: VwResourceStoreFactory.java

Create Date: Sep 24, 2006
============================================================================================
*/
package com.vozzware.util;

import java.util.ResourceBundle;

/**
 * Factory class for the the VwResourceStore object
 * @author petervosburghjr
 *
 */
public class VwResourceStoreFactory
{
  private static VwResourceStoreFactory    s_instance = null;
  private static VwResourceStore           m_store;
  
  /**
   * private singleton constructor
   *
   */
  private VwResourceStoreFactory()
  {
    ResourceBundle rb = null;
    
    try
    {
       rb = ResourceBundle.getBundle( "resource" );
    }
    catch( Exception ex )
    {
      ;
      
    }
    
    m_store = new VwResourceStore( rb );
    
  }
  
  
  /**
   * Returns the singleton factory instance
   * @return
   */
  public synchronized static VwResourceStoreFactory getInstance()
  {
    if ( s_instance == null )
      s_instance = new VwResourceStoreFactory();
    
    return s_instance;
    
  } // end VwResourceStoreFactory()
  
  
  public synchronized VwResourceStore getStore() throws Exception 
  { return m_store;  }
}

