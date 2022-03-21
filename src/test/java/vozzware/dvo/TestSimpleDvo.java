package test.vozzware.dvo;

import java.io.Serializable;

/*
============================================================================================

    Source File Name: Transaction.java

    Author:           petervosburgh
    
    Date Generated:   2/8/15

    Time Generated:   7:39 AM

============================================================================================
*/
public class TestSimpleDvo implements Serializable
{
  private String m_strFirstName;
  private String m_strLastName;
  private int m_nAge;

  public String getFirstName()
  {
    return m_strFirstName;
  }

  public void setFirstName( String strFirstName )
  {
    m_strFirstName = strFirstName;
  }

  public String getLastName()
  {
    return m_strLastName;
  }

  public void setLastName( String strLastName )
  {
    m_strLastName = strLastName;
  }

  public int getAge()
  {
    return m_nAge;
  }

  public void setAge( int nAge )
  {
    m_nAge = nAge;
  }
}
