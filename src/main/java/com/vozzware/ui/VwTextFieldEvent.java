/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwTextFieldEvent.java

Create Date: Apr 11, 2003
============================================================================================
*/

package com.vozzware.ui;

import javax.swing.text.JTextComponent;

/**
 * This class defines the validation failure reason for an VwTextField error.
 */
public class VwTextFieldEvent
{
  private JTextComponent      m_textComp;     // The JTextComponentd instance in error

  private String              m_strReason;    // Failing validation reason


  /**
   * Constructs an VwTextFieldEvent object with the given parameters
   */
  public VwTextFieldEvent( JTextComponent textComp, String strReason )
  { m_textComp = textComp; m_strReason = strReason; }


  /**
   * Gets the offending VwTextField instance
   *
   * @return - The offending VwTextField instance
   */
  public final Object getSource()
  { return m_textComp; }


  /**
   * Gets the failing validation reason
   *
   * @return A string with the validation failure reason
   */
  public final String getReason()
  { return m_strReason; }


} // end class VwTextFieldEvent{}

// *** End of VwTextFieldEvent.java ***

