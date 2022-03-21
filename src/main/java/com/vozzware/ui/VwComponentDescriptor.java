/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwComponentDescriptor.java

Create Date: Apr 11, 2003
============================================================================================
*/

package com.vozzware.ui;

import com.vozzware.util.VwCfgParser;
import com.vozzware.util.VwDelimString;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;



class VwComponentDescriptor
{

  private Vector        m_vecSelectedActions;   // Current selected actions for component
  private Hashtable     m_htActions;            // Hash table of action descriptors

  private String        m_strKey;               // Lookup key for hashtable

  private VwCfgParser  m_props;                // In memory properties file

  /**
   * Constructor Builds the Vector of selection actions and the Hshtable of properties
   * for each action
   *
   * @param strKey properties file key for storing action attributes
   * @param props The in memory properties file maintaind by the VwCfgParser class
   */
  VwComponentDescriptor( String strKey, VwCfgParser props ) throws Exception
  {
    m_props = props;

    m_strKey = strKey;

    m_vecSelectedActions = new Vector();

    m_htActions = new Hashtable();

    String strSelActions = m_props.getValue( m_strKey + ".Actions" );

    if ( strSelActions == null )
      strSelActions = "";

    VwDelimString dlms = new VwDelimString( ",", strSelActions );

    String strItem = dlms.getNext();

    while( strItem != null )
    {
      m_vecSelectedActions.addElement( strItem );

      // *** Build acction descriptor
      buildActionDescriptor( strItem );

      strItem = dlms.getNext();
    }

  } // end VwComponentDescriptor()



  /**
   * Return the vector of selected actions
   */
  Vector getSelectedActions()
  { return m_vecSelectedActions; }


  /**
   * Gets action descriptior for action request.
   *
   * @param strAction Name of action to get descriptor for
   *
   * @return VwActionDescriptor for action requested or null if nothing defined
   */
  final VwActionDescriptor getActionDescriptor( String strAction )
  { return (VwActionDescriptor)m_htActions.get( strAction ); }


  /*
   * Sets the action descriptor for an action
   *
   * @param strAction The name of the action
   * @param actionDesc The VwActionDescriptor for a given action
   */
  final void setActionDescriptor( String strAction, VwActionDescriptor actionDesc )
  { m_htActions.put( strAction, actionDesc ); }



  /*
   * Removes the action descriptor
   *
   * @param strAction The name of the action to remove
   */
  final void removeActionDescriptor( String strAction )
  { m_htActions.remove( strAction ); }


  /**
   * Writes content to the property file
   */
  final void save() throws Exception
  {

    // *** remove old references in the prop file to the key we've changed

    m_props.removeItems( m_strKey );

    // Add in current info


    if ( m_htActions.size() > 0 )
    {
      VwDelimString dlmsActionList = new VwDelimString();

      Enumeration enumList = m_htActions.keys();

      while ( enumList.hasMoreElements() )
        dlmsActionList.add( (String)enumList.nextElement() );

      m_props.addItem( m_strKey + "." + "Actions", dlmsActionList.toString() );

      enumList = m_htActions.keys();

      // *** Build each action property string

      while ( enumList.hasMoreElements() )
      {
        String strAction = (String)enumList.nextElement();

        VwActionDescriptor actDesc =
         (VwActionDescriptor)m_htActions.get( strAction );

        String strCondition = actDesc.getCondition();
        VwDelimString dlmsActionValue = new VwDelimString( ";", "" );

        if ( strCondition != null && strCondition.length() > 0 )
          dlmsActionValue.add( "condition=" + strCondition );

        VwServiceDescriptor servDesc = actDesc.getServiceDescriptor();

        if ( servDesc != null )
        {
          dlmsActionValue.add( "name=" + servDesc.getName() );

          String[] astrParams   = servDesc.getParamList();
          String[] astrParamVal = servDesc.getParamValList();

          if ( astrParams != null )
          {
            VwDelimString dlmsParamList = new VwDelimString();

            for ( int x = 0; x < astrParams.length; x++ )
            {
              String strParam = null;

              if ( astrParamVal[ x ] != null )
                strParam = astrParams[ x ] + ":" + astrParamVal[ x ];
              else
                strParam = astrParams[ x ];

              dlmsParamList.add( strParam );

            } // end for

            dlmsActionValue.add( dlmsParamList.toString() );

          } // end if ( astrParams != null )

        } // end if ( servDesc != null )

        // add in new action property string
        if ( dlmsActionValue.count() > 0 )
          m_props.addItem( m_strKey + "." + strAction, dlmsActionValue.toString() );

      } // end while
      
    } // end if ( m_htAction.size() > 0 )

    // Apply changes to disk
    m_props.updateFile();

  } // end save()

  /**
   * Builds action descriptor for action item if additional properties were defined
   *
   * @param strAction The name of the action
   */
  private void buildActionDescriptor( String strAction ) throws Exception
  {

    String strActionKey = m_strKey + "." + strAction;

    String strProps = m_props.getValue( strActionKey );

    if ( strProps == null )
    {
      m_htActions.put( strAction, new VwActionDescriptor() );
      return;       // No addition properties defined
    }
    
    m_htActions.put( strAction,
                     new VwActionDescriptor( new VwDelimString( ";", strProps ) ) );

  } // end buildActionDescriptor()

  
} // end class VwComponentDescriptor{}


// *** End of VwComponentDescriptor.java ***

