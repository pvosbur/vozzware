package test.vozzware.dvo;

import java.io.Serializable;

/*
============================================================================================

    Source File Name: Transaction.java

    Author:           petervosburgh
    
    Date Generated:   2/8/15

    Time Generated:   8:22 AM

============================================================================================
*/
public class TestAddress implements Serializable
{
  private String m_strAddrLine1;
  private String m_strCity;
  private String m_strState;
  private String m_strZip;


  public TestAddress()
  {
    ;


  }

  public TestAddress( String strAddr1, String strCity, String strState, String strZip )
  {
    m_strAddrLine1 = strAddr1;
    m_strCity = strCity;
    m_strState = strState;
    m_strZip = strZip;


  }

  public String getAddrLine1()
  {
    return m_strAddrLine1;
  }

  public void setAddrLine1( String strAddrLine1 )
  {
    m_strAddrLine1 = strAddrLine1;
  }

  public String getCity()
  {
    return m_strCity;
  }

  public void setCity( String strCity )
  {
    m_strCity = strCity;
  }

  public String getState()
  {
    return m_strState;
  }

  public void setState( String strState )
  {
    m_strState = strState;
  }

  public String getZip()
  {
    return m_strZip;
  }

  public void setZip( String strZip )
  {
    m_strZip = strZip;
  }
}
