/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwDataChangedListenerPropertyEditor.java

Create Date: Apr 11, 2003
============================================================================================
*/
package com.vozzware.ui;

import com.vozzware.util.VwCfgParser;
import com.vozzware.util.VwDelimString;

import java.awt.Component;
import java.beans.PropertyEditorSupport;

public class VwDataChangeListenersPropertyEditor extends PropertyEditorSupport
{

  String[] m_atrCompNames = new String[ 0 ];
  
  public void setValue( Object o )
  {
    if ( o == null )
      m_atrCompNames = new String[ 0 ];
    else
      m_atrCompNames = (String[])o;

    firePropertyChange();

  } // end setValue()

  public Object getValue()
  { return m_atrCompNames; }

  public String getAsText()
  {


    VwCfgParser propFile = VwComponentPropPropertyEditor.getPropFile();

    if ( propFile == null )
      return null;

    return propFile.getValue( VwCompNamePropertyEditor.m_strName + ".VwListeners" );

  }

  public Component getCustomEditor()
  {
    return new VwDataChangeListenersPanel(this);
  }

  public boolean supportsCustomEditor()
  { return true; }


  public String getJavaInitializationString()
  {

    VwCfgParser propFile = VwComponentPropPropertyEditor.getPropFile();

    if ( propFile == null )
      return " null ";

    String strVal = propFile.getValue( VwCompNamePropertyEditor.m_strName + ".VwListeners" );

    if ( strVal == null )
      return " null ";

    String strRet = " new String[] { ";

    String strTemp = null;

    VwDelimString dlms = new VwDelimString( ",", strVal );

    int nCount = 0;

    while( (strTemp = dlms.getNext() ) != null )
    {
      if ( ++nCount > 1 )
        strRet += ",";

      strRet += "\"" + strTemp + "\"";

    } // end while()

    strRet += " }";

    return strRet;
    

  } // end getJavaInitializationString();


} // end class VwDataChangeListenersPropertyEditor{}

// *** end of VwDataChangeListenersPropertyEditor.java ***

