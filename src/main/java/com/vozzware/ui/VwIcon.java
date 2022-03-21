/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwIcon.java

Create Date: Apr 11, 2003
============================================================================================
*/
package com.vozzware.ui;

import javax.swing.ImageIcon;
import java.net.URL;

public class VwIcon extends ImageIcon
{
  private URL   m_urlImageLocation;
  
  public VwIcon( ImageIcon icon )
  {
    super( icon.getImage() );
    
  }
  
  
  public VwIcon( URL urlImageLocation )
  {
    super( urlImageLocation );
    m_urlImageLocation = urlImageLocation;
  }
  
  
  /**
   * Returns the URL to the image for this icon
   * @return
   */
  public URL getURL()
  { return m_urlImageLocation; }
  
}
