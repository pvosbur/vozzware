/*
===========================================================================================

 
                               Copyright(c) 2000 - 2006 by

                      V o z z W a r e   L L C (Vw)

                                 All Rights Reserved

Source Name: VwTextComponentAppender.java

Create Date: Jul 23, 2006

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.


============================================================================================
*/
package com.vozzware.ui;

import com.vozzware.util.VwLogger;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.LoggingEvent;

import javax.swing.text.JTextComponent;

/**
 * @author P. VosBurgh
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class VwTextComponentAppender extends AppenderSkeleton
{

  private JTextComponent  m_textComp;
  private PatternLayout   m_pl;
  
  public VwTextComponentAppender( PatternLayout pl, JTextComponent textComp  )
  {
    super();
    m_pl = pl;
    m_textComp = textComp;
  }

  public VwTextComponentAppender(JTextComponent textComp  )
  {
    super();
    m_pl = new PatternLayout( VwLogger.getDefaultPattern() );
    m_textComp = textComp;
  }
  
 
  /* (non-Javadoc)
   * @see org.apache.log4j.AppenderSkeleton#append(org.apache.log4j.spi.LoggingEvent)
   */
  protected void append( LoggingEvent lg )
  {
    String strMsg = m_pl.format( lg  );
    m_textComp.setText( m_textComp.getText() + strMsg );

  }

  /* (non-Javadoc)
   * @see org.apache.log4j.AppenderSkeleton#close()
   */
  public void close()
  {
    // TODO Auto-generated method stub

  }

  /* (non-Javadoc)
   * @see org.apache.log4j.AppenderSkeleton#requiresLayout()
   */
  public boolean requiresLayout()
  {
    // TODO Auto-generated method stub
    return false;
  }

}
