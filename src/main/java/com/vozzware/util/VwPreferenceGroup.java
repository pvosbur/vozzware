/*
 ============================================================================================

 V o z z W o r k s   C o d e   G e n e r a t o r                       

 2009 by I Technologies Corp                              

 Source File Name: VwPreferenceGroup.java

 Author:           Vw

 Date Generated:   06-16-2006

 Time Generated:   10:01:48

 ============================================================================================
 */

package com.vozzware.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class VwPreferenceGroup implements Serializable
{

  private String m_strName;

  private List<VwPreference> m_listPreference;

  
  /**
   * The default zero arg constructor
   */
  public VwPreferenceGroup()
  { ; }
  
  
  /**
   * Constructs with name of the preference group
   * 
   * @param strgroupName the name of the preference group
   */
  public VwPreferenceGroup( String strgroupName )
  { m_strName = strgroupName; }
  
  
  // *** The following members set or get data from the class members ***

  /**
   * Sets the name property
   * 
   * @param strname
   */
  public void setName( String strName )
  { m_strName = strName; }
  

  /**
   * Gets name property
   * 
   * @return The name property
   */
  public String getName()
  {  return m_strName; }

  /**
   * Adds a preference to this group
   * 
   * @param preference
   */
  public void addPreference( VwPreference preference )
  {
    if ( m_listPreference == null )
      m_listPreference = new ArrayList<VwPreference>();

     // Make sure we dont add dups
     if ( !m_listPreference.contains( preference ))
       m_listPreference.add( preference );
  }


  /**
   * Ads a preference to this group
   * @param strPrefName  The preference name
   * @param strPrefValue The preference value
   */
  public void addPreference( String strPrefName, String strPrefValue )
  {
    addPreference( new VwPreference( strPrefName, strPrefValue ));
  }

  /**
   * Gets list of preferences for this group
   * 
   * @return The preference property
   */
  public List<VwPreference> getPreference()
  {  return m_listPreference; }
  
  public void setPreference( List<VwPreference>listPreference )
  { m_listPreference = listPreference; }


  /**
   * Gets the named preference
   * @param strPrefName The name of the preference to retrieve
   * @return
   */
  public VwPreference getNamedPreference( String strPrefName )
  {
    if ( m_listPreference == null )
      return null;
    
    for ( VwPreference pref : m_listPreference )
    {
      if ( pref.getName().equalsIgnoreCase( strPrefName ))
        return pref;
    }
    
    return null;        // not found
  }


  /**
   * Gets the value for the named named preference
   * @param strPrefName The name of the preference to retrieve  its value
   * @return
   */
  public String getNamedPreferenceValue( String strPrefName )
  {
    if ( m_listPreference == null )
      return null;

    for ( VwPreference pref : m_listPreference )
    {
      if ( pref.getName().equalsIgnoreCase( strPrefName ))
        return pref.getValue();
    }

    return null;        // not found
  }

} // *** End of class VwPreferenceGroup{}

// *** End Of VwPreferenceGroup.java
