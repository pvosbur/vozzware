/*
============================================================================================

                        V o z z W o r k s   C o d e   G e n e r a t o r                       

                              2009 by I Technologies Corp                              

    Source File Name: VwPreference.java

    Author:           Vw

    Date Generated:   06-16-2006

    Time Generated:   10:01:48

============================================================================================
*/

package com.vozzware.util;


import java.io.Serializable;

public class VwPreference implements Serializable
{

  private String                 m_strName;                      
  private String                 m_strValue;                     

  
  /**
   * Default zero arg constructor
   */
  public VwPreference()
  { ; }
  
  
  /**
   * Construct with preference name and value
   * 
   * @param strPrefName The name of the preference
   * @param strPrefValue  The value for the preference
   */
  public VwPreference( String strPrefName, String strPrefValue )
  {
    m_strName = strPrefName;
    m_strValue = strPrefValue;
    
  }
  
  
  // *** The following members set or get data from the class members *** 

  /**
   * Sets the name property
   * 
   * @param strName
   */
  public void setName( String strName )
  { m_strName = strName; }

  /**
   * Gets name property
   * 
   * @return  The name property
   */
  public String getName()
  { return m_strName; }

  /**
   * Sets the value property
   * 
   * @param strValue
   */
  public void setValue( String strValue )
  { m_strValue = strValue; }

  /**
   * Gets value property
   * 
   * @return  The value property
   */
  public String getValue()
  { return m_strValue; }
  
} // *** End of class VwPreference{}

// *** End Of VwPreference.java