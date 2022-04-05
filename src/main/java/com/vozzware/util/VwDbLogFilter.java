package com.vozzware.util;

import org.apache.logging.log4j.core.LogEvent;

import java.util.logging.Filter;
import java.util.logging.LogRecord;


/*
============================================================================================

    Source File Name: Transaction.java

    Author:           petervosburgh
    
    Date Generated:   6/13/20

    Time Generated:   8:22 AM

============================================================================================
*/
public class VwDbLogFilter implements Filter
{

  @Override
  public boolean isLoggable( LogRecord loggingEvent)
  {
    Object objMessage = loggingEvent.getMessage();
    String strRenderedMsg = loggingEvent.getMessage();

    return true;

  }

}
