/*
  ===========================================================================================

               V o z z W o r k s  F r a m e W o r k  L i b r a r i e s

                         Copyright(c) 2000 by

            I n t e r n e t   T e c h n o l o g i e s   C o m p a n y

                                 All Rights Reserved


  Source Name:  VwButtonBeanInfo.java


  ============================================================================================
*/

package  com.vozzware.ui;

import java.awt.Image;
import java.beans.BeanInfo;
import java.beans.SimpleBeanInfo;
/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwButtonBeanInfo.java

Create Date: Apr 11, 2005
============================================================================================
*/


public class VwButtonBeanInfo extends SimpleBeanInfo
{

  public Image getIcon(int iconKind)
  {

    if ( iconKind == BeanInfo.ICON_COLOR_16x16 )
      return loadImage( "itcbtn_color16.gif" );
    else
      return loadImage( "itcbtn.gif" );

  }

} // end class VwButtonBeanInfo{}


// *** End of VwButtonBeanInfo.java ***

