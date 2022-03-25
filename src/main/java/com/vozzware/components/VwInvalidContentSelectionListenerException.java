/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwInvalidContentSelectionListenerException.java

============================================================================================
*/

package com.vozzware.components;

import java.util.ResourceBundle;

public class VwInvalidContentSelectionListenerException extends Exception
{
  /**
   * Constructor
   * @param strText Exception text
   */
  public VwInvalidContentSelectionListenerException( String strText )
  {  super( strText );  }


  /**
   * Default constructor that uses the default invalid producer text
   */
  public VwInvalidContentSelectionListenerException()
  {
    super( ResourceBundle.getBundle( "resources.properties.components").getString( "Vw.Components.InvalidContentListenerr" ) );
  }


}//end class VwInvalidContentSelectionListenerException

//*** End VwInvalidContentSelectionListenerException.java ***
