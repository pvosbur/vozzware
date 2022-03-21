package test.vozzware.util;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;

/*
============================================================================================

    Source File Name: 

    Author:           petervosburgh
    
    Date Generated:   1/25/16

    Time Generated:   3:05 PM

============================================================================================
*/
@Configuration
@EnableMBeanExport
@ManagedResource
public class JmxTestBean
{
  private String m_strName;


  public JmxTestBean()
  {
    System.out.printf( "In JmxTestBean Constructor" );
  }
  @ManagedAttribute(description="Sets The name")
  public void setName( String strName )
  {
    m_strName = strName;
  }

  @ManagedAttribute(description="Gets the Name")
  public String getName()
  {
    return m_strName;
  }
}
