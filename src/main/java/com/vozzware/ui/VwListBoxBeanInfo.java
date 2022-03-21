/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwListBoxBeanInfo.java

Create Date: Apr 11, 2003
============================================================================================
*/
package  com.vozzware.ui;

import java.awt.Image;
import java.beans.BeanInfo;
import java.beans.SimpleBeanInfo;

public class VwListBoxBeanInfo extends SimpleBeanInfo
{
  public Image getIcon(int iconKind)
  {
   
    if ( iconKind == BeanInfo.ICON_COLOR_16x16 )
      return loadImage( "itclist_color16.gif" );
    else
      return loadImage( "itclist.gif" );
  }
  /*
  public PropertyDescriptor[] getPropertyDescriptors()
  {
    VwLogFile lf = null;
    try
    {
      lf = new VwLogFile( "c:\\com\\itcbeanlog1.log", true );
      lf.writeLine( "In getPropertyDescriptors()" );
      PropertyDescriptor pd1 = new PropertyDescriptor( "VwTest",
                                                       VwListBox.class );
      pd1.setPropertyEditorClass(com.vozzware.ui.VwJEditor.class);
      lf.writeLine( "Returning array" );

      return new PropertyDescriptor[]{pd1};

    }
    catch( Exception e )
    {
      lf.writeLine( e.toString() );
      return null;
    }

    finally
    {
      lf.close();

    }
  }
  */
  
} // end class VwListBoxBeanInfo


// *** End of VwListBoxBeanInfo.java ***

