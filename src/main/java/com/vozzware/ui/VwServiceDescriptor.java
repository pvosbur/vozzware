/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwServiceDescriptor.java

Create Date: Apr 11, 2003
============================================================================================
*/

package com.vozzware.ui;

import com.vozzware.util.VwDelimString;

class VwServiceDescriptor
{

  private String        m_strServiceName;       // Name of service to execute

  private String[]      m_astrParams;           // Required param list
  private String[]      m_astrSuppliedValues;   // Default or supplie values


  /**
   * Sets the service property
   *
   * @param strPropName   The property name
   * @param strPropValue  The property value
   */
  final void setProperty( String strPropName, String strPropValue )
  {
    if ( strPropName.equals( "name" ) )
      m_strServiceName = strPropValue;
    else
    {
      VwDelimString dlms = new VwDelimString( ",", strPropValue );

      m_astrParams = new String[ dlms.count() ];

      m_astrSuppliedValues = new String[ m_astrParams.length ];


      String strParam = null;

      int ndx = -1;

      while( ( strParam = dlms.getNext() ) != null )
      {
        ++ndx;

        int nPos = strParam.indexOf( '=' );

        if ( nPos > 0 )
        {
          m_astrParams[ ndx ] = strParam.substring( 0, nPos );
          m_astrSuppliedValues[ ndx ] = strParam.substring( nPos + 1 );
        }
        else
        {
          m_astrParams[ ndx ] = strParam;
          m_astrSuppliedValues[ ndx ] = null;

        } // end else

      } // end while

    } // end else

  } // end setProperty()



  /**
   * Gets the service name
   */
  final String getName()
  { return m_strServiceName; }


  /**
   * Sets the service name
   */
  final void setName( String strServiceName )
  { m_strServiceName = strServiceName; }


  /**
   * Gets the service param list
   */
  final String[] getParamList()
  { return m_astrParams; }


  /**
   * Sets the service param list
   *
   * @param astrParams an array of param names
   */
  final void setParamList( String[] astrParams )
  { m_astrParams = astrParams; }


  /**
   * Gets the service param value list
   */
  final String[] getParamValList()
  { return m_astrSuppliedValues; }


  /**
   * Sets the service param  supplied value list
   *
   * @param astrParams an array of param names
   */
  final void setParamValsList( String[] astrSuppliedValues )
  { m_astrSuppliedValues = astrSuppliedValues; }

} // end class VwServiceDescriptor{}


// *** End if VwServiceDescriptor.java ***
