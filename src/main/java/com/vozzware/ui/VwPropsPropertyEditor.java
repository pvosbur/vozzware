/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwPropsPropertyEdotor.java

Create Date: Apr 11, 2003
============================================================================================
*/
package com.vozzware.ui;

import java.beans.PropertyEditorSupport;

public class VwPropsPropertyEditor extends PropertyEditorSupport
{

  static String m_strPropFile = null;


  public void setValue( Object o )
  {
    m_strPropFile = (String)o;
    firePropertyChange();

  }

  public Object getValue()
  { return m_strPropFile; }


} // end class VwPropsPropertyEditor{}

// *** end of VwPropsPropertyEditor.java ***

