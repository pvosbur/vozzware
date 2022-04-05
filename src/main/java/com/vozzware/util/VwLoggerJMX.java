package com.vozzware.util;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Set;

/*
============================================================================================

    Source File Name: 

    Author:           petervosburgh
    
    Date Generated:   1/23/16

    Time Generated:   9:42 AM

============================================================================================
*/
@Configuration
@EnableMBeanExport
@ManagedResource(objectName = "Vozzworks:name=VwLogger")
public class VwLoggerJMX
{

  VwLogger m_curLogger;


  public VwLoggerJMX()
  {
    return;

  }

  @PostConstruct
  public void init() throws Exception
  {
    String[] astrInstanceNames = getInstanceNames();

    if ( astrInstanceNames != null && astrInstanceNames.length > 0 )
    {
      m_curLogger = VwLogger.getInstance( astrInstanceNames[ 0 ] );

    }
  }

  @ManagedOperation
  public String[] getInstanceNames()  throws Exception
  {
    Set<String> instanceNames = VwLogger.getInstanceNames();
    String[] astrInstanceNames = new String[ instanceNames.size() ];

    instanceNames.toArray( astrInstanceNames  );

    return astrInstanceNames;
   }

  @ManagedOperation
  public boolean turnOnPWAAppender()  throws Exception
  {
    if ( m_curLogger != null )
    {
      VwLogAppenderPWAPushImpl appender = (VwLogAppenderPWAPushImpl) m_curLogger.getAppender( "PWAPushAppender" );
      appender.setEnabled( true );
    }

    return false;
  }

  @ManagedOperation
  public boolean turnoffPWAAppender()  throws Exception
  {
    if ( m_curLogger != null )
    {
      VwLogAppenderPWAPushImpl appender = (VwLogAppenderPWAPushImpl) m_curLogger.getAppender( "PWAPushAppender" );
      appender.setEnabled( false );
    }

    return false;
   }

  @ManagedOperation
   public List<String> getAppenderNames( String strInstanceName ) throws Exception
   {

     return m_curLogger.getAppenderNames();
   }

  @ManagedOperation
  public void setCurrentLogger( String strInstanceName ) throws Exception
  {
    m_curLogger = VwLogger.getInstance( strInstanceName );
  }


  @ManagedAttribute(description="Gets the instance name of the current VwLogger")
  public String getCurrentInstanceName()
  {
    if ( m_curLogger != null )
    {
      return m_curLogger.getInstanceName();
    }

    return null;
  }


  @ManagedAttribute(description="Sets the Logging Level")
  public void setLevelAsString( String strLevel )
  {
    if ( m_curLogger != null )
    {
      m_curLogger.setLevelAsString( strLevel );
    }
  }


  @ManagedAttribute(description="Gets the Logging Level")
  public String getLevelAsString()
  {
    if ( m_curLogger != null )
    {
      return m_curLogger.getLevelAsString();
    }

    return null;

  }

  @ManagedAttribute(description="Sets the debug logging verbose Level")
  public void setDebugVerboseLevel( int nDebugVerboseLevel )
  {
    if ( m_curLogger != null )
    {
      m_curLogger.setDebugVerboseLevel( nDebugVerboseLevel );
    }

  }

  @ManagedAttribute(description="Gets the debug Logging verbose Level")
  public int getDebugVerboseLevel()
  {
    if ( m_curLogger != null )
    {
      return m_curLogger.getDebugVerboseLevel();
    }

    return -1;
  }


  @ManagedOperation
  public void clearLog() throws Exception
  {
    if ( m_curLogger != null )
    {
      m_curLogger.clearLog();
    }

  }

  @ManagedOperation
  public void clearAll() throws Exception
  {
     VwLogger.clearAll();
  }

}
