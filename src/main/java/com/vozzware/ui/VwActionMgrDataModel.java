/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwActionMgrDataModel.java

Create Date: Apr 11, 2006
============================================================================================
*/

package com.vozzware.ui;

import com.vozzware.util.VwCfgParser;

import java.util.Vector;

class VwActionMgrDataModel
{

  private String[]      m_astrActionList = {  "ExecService", "Enable", "EnableAll",
                                              "Disable", "DisableAll", "Clear",
                                              "ClearAll" };

  private Vector        m_vecActionList;
  
  private VwCfgParser  m_props;              // In memory property file
  private String        m_strCompName;        // Name of component action list applies to
  private String        m_strActionCompName;  //

  private VwComponentDescriptor m_compDesc;  // Component descriptor
  /**
   * Constructor
   *
   * @param props The property file in memory for storing actions
   */
  VwActionMgrDataModel( VwCfgParser props, String strCompName,
                         String strActionCompName ) throws Exception
  {
    m_props = props;
    m_strCompName = strCompName;
    m_strActionCompName = strActionCompName;
    m_vecActionList = new Vector();

    for ( int x = 0; x < m_astrActionList.length; x++ )
      m_vecActionList.addElement( m_astrActionList[ x ] );

    m_compDesc = new VwComponentDescriptor( m_strCompName + "." + m_strActionCompName,
                                             m_props );

  } // end VwActionMgrDataModel()


  /**
   * Returns the master list of actions to choose from
   *
   * @return A String array of actions to choose from
   */
  final Vector getActionList()
  { return (Vector)m_vecActionList.clone(); }


  /**
   * Gets the current selected actions for a given component event
   *
   * @return a String array of actions
   */
  final Vector getSelectedActions()
  {
    return m_compDesc.getSelectedActions();

  } // end getSelectedActions()


  /**
   * Get the VwActionDescriptor for action requested
   */
  final VwActionDescriptor getActionDescriptor( String strAction )
  { return m_compDesc.getActionDescriptor(  strAction ); }


  /**
   * Sets the VwActionDescriptor for action requested
   */
  final void setActionDescriptor( String strAction,
                                  VwActionDescriptor actionDesc )
  { m_compDesc.setActionDescriptor( strAction, actionDesc ); }



  /**
   * Remove the VwActionDescriptor for action requested
   */
  final void removeActionDescriptor( String strAction )
  { m_compDesc.removeActionDescriptor( strAction ); }

  /*
   * Saves changes to the action list to the property file
   *
   */
  public void apply() throws Exception
  { m_compDesc.save(); }


} // end class VwActionMgrDataModel{}

// *** End of VwActionMgrDataModel.java ***
