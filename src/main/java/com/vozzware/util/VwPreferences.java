/*
============================================================================================

                        V o z z W o r k s   C o d e   G e n e r a t o r                       

                              2009 by I Technologies Corp                              

    Source File Name: VwPreferences.java

    Author:           Vw

    Date Generated:   06-16-2006

    Time Generated:   10:01:48

============================================================================================
*/

package com.vozzware.util;

import java.io.Serializable;
import java.util.List;


public class VwPreferences implements Serializable
{

  private List<VwPreferenceGroup>  m_listPreferenceGroup;          

  // *** The following members set or get data from the class members *** 

  /**
   * Sets the preferenceGroup property
   * 
   * @param listPreferenceGroup
   */
  public void setPreferenceGroup( List<VwPreferenceGroup> listPreferenceGroup )
  { m_listPreferenceGroup = listPreferenceGroup;
 }

  /**
   * Gets preferenceGroup property
   * 
   * @return  The preferenceGroup property
   */
  public List<VwPreferenceGroup> getPreferenceGroup()
  { return m_listPreferenceGroup; }
} // *** End of class VwPreferences{}

// *** End Of VwPreferences.java