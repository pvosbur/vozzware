/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwActionDescriptor.java

Create Date: Apr 11, 2005
============================================================================================
*/

package com.vozzware.ui;

import com.vozzware.util.VwDelimString;

import java.util.ResourceBundle;

class VwActionDescriptor
{

  private String    m_strCondition;             // Condition that will execute action if not null

  private VwServiceDescriptor  m_serviceDesc;  // Service descriptor if sevice action

  private ResourceBundle m_msgs = ResourceBundle.getBundle( "com.vozzware.ui.ui" );


  /**
   * Default constructor
   */
  VwActionDescriptor()
  { ; }


  /**
   * Constructor Builds action descriptor for property string. If the action is an "ExecService"
   * then an VwServiceDescriptor will also be created
   *
   * @param The property value delimited string for this action
   *
   * @exception Exceptiom if an invalid property is encountered
   */
  VwActionDescriptor( VwDelimString dlmsProps ) throws Exception
  {
    String strProp = dlmsProps.getNext();

    while( strProp != null )
    {

      if ( strProp.startsWith( "condition=" ) )
      {
        int nPos = strProp.indexOf( '=' );
        m_strCondition = strProp.substring( nPos + 1 );
      }
      else
      if ( strProp.startsWith( "name=" ) || strProp.startsWith( "params=" ) )
      {
        if ( m_serviceDesc == null )
          m_serviceDesc = new VwServiceDescriptor();

        String strPropName = null;
        String strPropValue = null;

        int nPos = nPos = strProp.indexOf( '=' );

        strPropName = strProp.substring( 0, nPos );
        strPropValue = strProp.substring( nPos + 1 );
        m_serviceDesc.setProperty( strPropName, strPropValue );


      } // end if
      else
        throw new Exception( strProp + " " + m_msgs.getString( "VwInvalidActionProp" ) );

      // Next property

      strProp = dlmsProps.getNext();

    } // end while()

    
  } // end VwActionDescriptor()


  /**
   * Gets the action condition or null if N/A
   */
  String getCondition()
  { return m_strCondition; }


  /**
   * Sets the action condition
   *
   * @param strCondition The new action condition string
   */
  void setCondition( String strCondition )
  { m_strCondition = strCondition; }


  /**
   * Gets the service descriptor if the action is an ExecService
   */
  VwServiceDescriptor getServiceDescriptor()
  { return m_serviceDesc; }


  /**
   * Gets the service descriptor if the action is an ExecService
   */
  final void setServiceDescriptor( VwServiceDescriptor serviceDesc )
  { m_serviceDesc = serviceDesc; }

} // end class VwActionDescriptor{}


// *** End of VwActionDescriptor.java ***

