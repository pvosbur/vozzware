/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwSoapParser.java

Create Date: Apr 11, 2006
============================================================================================
*/

package com.vozzware.xml;

class Customer
{
  String  m_strName;
  String  m_strSsn;
  
  int     m_nAge;
  
  public String getSsn()
  {
    return m_strSsn;
  }
  public void setSsn( String ssn )
  {
    m_strSsn = ssn;
  }
  public String getName()
  {
    return m_strName;
  }
  public void setName( String name )
  {
    m_strName = name;
  }
  public int getAge()
  {
    return m_nAge;
  }
  public void setAge( int age )
  {
    m_nAge = age;
  }
  
  
}


// *** End of VwSoapParser.java
