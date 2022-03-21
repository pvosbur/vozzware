package test.vozzware.util;

/*
============================================================================================

    Source File Name: Transaction.java

    Author:           petervosburgh
    
    Date Generated:   3/3/22

    Time Generated:   4:24 PM

============================================================================================
*/
public class TestArgs
{
  private String m_strName;
  private int m_nAge;
  TestArgs( String strName, int nAge )
  {
    m_strName = strName;
    m_nAge = nAge;

  }
  public String getName()
  {
    return m_strName;
  }

  public int getAge()
  {
    return m_nAge;

  }
}