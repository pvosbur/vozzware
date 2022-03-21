package com.vozzware.ui.beans;

import com.vozzware.ui.VwDialog;

import java.awt.Component;
import java.awt.Font;
import java.awt.Frame;

/**
 * Dilaog that displays a Font Chooser
 * @author petervosburghjr
 *
 */
public class VwFontChooserDialog extends VwDialog
{
  private VwFontChooserPanel m_fontChooserPanel;

  public VwFontChooserDialog( Component comp, String strTitleText, boolean fModel, Font fontInitial )
  {
    super( comp, new VwFontChooserPanel( fontInitial ), fModel, strTitleText );
    m_fontChooserPanel = (VwFontChooserPanel)getUserPanel();
    
  }

  public VwFontChooserDialog( Frame frameParent, String strTitleText, boolean fModel, Font fontInitial )
  {
    super( frameParent, new VwFontChooserPanel( fontInitial ), fModel, strTitleText );
    m_fontChooserPanel = (VwFontChooserPanel)getUserPanel();
    
  }
  
  /**
   * Get the chosen font from the dialog user selections
   * @return the font configured from the dialog chooser
   */
  public Font getChosenFont()
  { return m_fontChooserPanel.getChosenFont(); }
} // end class VwFontChooserDialog{}

// *** End of VwFontChooserDialog.java ***

