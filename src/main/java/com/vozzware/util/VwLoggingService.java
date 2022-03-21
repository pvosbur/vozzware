package com.vozzware.util;

/*
============================================================================================

    Source File Name: 

    Author:           petervosburgh
    
    Date Generated:   12/23/15

    Time Generated:   7:38 AM

============================================================================================
*/

import java.util.HashMap;
import java.util.Map;

public class VwLoggingService
{
  private static Map<String,String>s_mapLogPrefixByThreadId = new HashMap<>();

  private VwLogger m_logger;

  /**
   *
   * @param logger
   */
  public VwLoggingService( VwLogger logger )
  {
    m_logger = logger;
  }
} // end VwLoggingService()
