package com.vozzware.util;

/*
============================================================================================

    Source File Name: Transaction.java

    Author:           petervosburgh
    
    Date Generated:   5/6/20

    Time Generated:   10:35 AM

============================================================================================
*/
public class VwVapidKeys
{
  private String m_strPrivateKey;
  private String m_strPublicKey;

  public void setPublicKey( String strPublicKey )
  {
    m_strPublicKey = strPublicKey;
  }

  public String getPublicKey()
  {
    return m_strPublicKey;
  }

  public void setPrivateKey( String strPrivateKey )
  {
    m_strPrivateKey = strPrivateKey;
  }

  public String getPrivateKey()
  {
    return m_strPrivateKey;
  }

}
