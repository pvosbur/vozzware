/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwActionMgrPropertyEditor.java

Create Date: Apr 11, 2005
============================================================================================
*/
package com.vozzware.ui;

import com.vozzware.util.VwCfgParser;

import java.awt.Component;
import java.beans.PropertyEditorSupport;

public class VwActionMgrPropertyEditor extends PropertyEditorSupport
{

  private boolean   m_fHasActions = false;

  public void setValue( Object o )
  {
    if ( o == null )
      m_fHasActions = false;
    else
      m_fHasActions = ((Boolean)o).booleanValue();

    firePropertyChange();

  } // end setValue()

  public Object getValue()
  { return new Boolean( m_fHasActions ); }


  public Component getCustomEditor()
  {

    try
    {
      VwCfgParser props = new VwCfgParser( VwComponentPropPropertyEditor.getPropFilePath(),
                                     "#", "=", true, false, true );
      return new VwActionMgrPanel( props, VwCompNamePropertyEditor.m_strName,
                                    "Init" );

    }
    catch( Exception e )
    {
      e.printStackTrace();
    }

    return null;
    
  }

  public boolean supportsCustomEditor()
  { return true; }

} // end class VwActionMgrPropertyEditor{}

// *** End of VwActionMgrPropertyEditor.java ***
