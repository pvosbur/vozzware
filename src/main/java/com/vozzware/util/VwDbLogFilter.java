package com.vozzware.util;

import org.apache.log4j.spi.Filter;

/*
============================================================================================

    Source File Name: Transaction.java

    Author:           petervosburgh
    
    Date Generated:   6/13/20

    Time Generated:   8:22 AM

============================================================================================
*/
public class VwDbLogFilter extends Filter
{

  @Override
  public int decide(org.apache.log4j.spi.LoggingEvent loggingEvent)
  {
    Object objMessage = loggingEvent.getMessage();
    String strRenderedMsg = loggingEvent.getRenderedMessage();

    return Filter.ACCEPT;

  }

}
