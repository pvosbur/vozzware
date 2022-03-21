/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwWizardPanel.java

============================================================================================
*/

package com.vozzware.components;
import javax.swing.JPanel;

/**
 * All panels used in the VwWizardMgr must derived from this class. Before a
 * panel is displayed, the proceed() method is called to determine if the next
 * or previous panel is allowed.  If True is returned, the next panel will be
 * allowed; otherwise, the VwWizardMgr will stay on the current panel.  If
 * the proceed() method returns True, then the skip() method is called. If
 * skip() returns True, the VwWizardMgr skips to the next panel in the list.
 * A reference to the VwWizardMgr is passed to both of these methods to
 * allow context information to be passed.  The setUserObject() and
 * getUserObject() methods can be used to pass user defined context data
 * from panel to panel.
 */
public class VwWizardPanel extends JPanel
{
  int m_nPanelNbr;          // The panel sequence nbr in the panel list

  String m_strNameID;       // A user assigned panel name (optional)

  /**
   * This method is called by the VwWizardMgr before moving off the current
   * panel.  This method is typically used to validate data or execute some
   * process.  If this method is not overriden in the derived class, the
   * default implementation returns True.
   *
   * @param mgr The VwWizardMgr instance this panel belongs to.
   *
   * @return True if the VwWizardMgr can proceed to the next panel in the
   * list.  If False is returned, the current panel maintains the focus.
   */
  public boolean proceed( VwWizardMgr mgr )
  { return true; }


  /**
   * This method is called after the proceed() method, if the proceed() method
   * returns True.  The panel is not yet in view when this method is called.
   * If this method returns True, this panel is skipped in favor of the next
   * one in the sequence.  If this method is not overriden in the derived
   * class, the default implementation returns False.
   *
   * @param mgr The VwWizardMgr instance this panel belongs to.
   *
   * @return True if this panel should be skipped.
   */
  public boolean skip( VwWizardMgr mgr )
  { return false; }


  /**
   * Gets the panel sequence nbr for this panel
   *
   * @return The sequence nbr of this panel in the list.
   */
  public final int getPanelNbr()
  { return m_nPanelNbr; }


  /**
   * Gets the panel name (optionally assined by the panel creator)
   *
   * @return The panel name or null if none was assigned
   */
  public final String getPanelName()
  { return m_strNameID; }


} // end class VwWizardPanel{}


// *** End VwWizardPanel.java

