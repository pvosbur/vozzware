/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwRadioButtonBeanInfo.java

Create Date: Apr 11, 2003
============================================================================================
*/
package  com.vozzware.ui;

import java.awt.Image;
import java.beans.BeanInfo;
import java.beans.SimpleBeanInfo;


public class VwRadioButtonBeanInfo extends SimpleBeanInfo
{

  public Image getIcon(int iconKind)
  {

    if ( iconKind == BeanInfo.ICON_COLOR_16x16 )
      return loadImage( "JdbRadioButton_Color16.gif" );
    else
      return loadImage( "JdbRadioButton_Color32.gif" );

  }


} // end class VwRadioButtonBeanInfo{}


// *** End of VwRadioButtonBeanInfo.java ***

