/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwComponentPropPropertyEditor.java

Create Date: Apr 11, 2003
============================================================================================
*/
package com.vozzware.ui;

import com.vozzware.util.VwCfgParser;

import java.awt.Component;
import java.beans.PropertyEditorSupport;
import java.io.File;

public class VwComponentPropPropertyEditor extends PropertyEditorSupport
{
  /*
   * Each component using properties has one of these
   */

  static String       m_strClassPath;


  public void setValue( Object o )
  {

    if ( o == null )
    {
      m_strClassPath = null;
      return;
    }

    m_strClassPath = (String)o;
    firePropertyChange();

  } // end setValue()


  public Object getValue()
  { return getAsText(); }


  public void setAsText( String strClassPath )
  { setValue( strClassPath ); }


  public String getAsText()
  { return m_strClassPath; }


  public Component getCustomEditor()
  {
    return new VwComponentPropertyPanel( this );
  }

  public boolean supportsCustomEditor()
  { return true; }


  /**
   * Gets the fully qualified path of the property file
   */
  static String getPropFilePath()
  {
    if ( m_strClassPath == null )
      return null;

    String strClassPath = null;
    String strPropFileName;

    int nPos = m_strClassPath.indexOf( ';' );

    if ( nPos > 0 )
    {
      strPropFileName = m_strClassPath.substring( nPos + 1 );
      strClassPath = m_strClassPath.substring( 0, nPos );

    }
    else
    {
      m_strClassPath = null;
      return null;

    }


    String strPathChar = new String( new char[]{File.separatorChar } );

    String strPath = null;
    strPath = strClassPath + strPathChar;

    strPath += strPropFileName.replace( '.', File.separatorChar ) +
      ".properties";

    return strPath;

  } // end getPropFilePath()

  /**
   * Returns an VwCfgParser instance for the current property file name
   *
   * @return an VwCfgParser instance for the current property file name or null
   * if none specified
   */
  static VwCfgParser getPropFile()
  {

    if ( m_strClassPath == null )
      return null;

    try
    {
      return new VwCfgParser( getPropFilePath(), "#", "=", false, false, false );
    }
    catch( Exception e )
    {
      return null;
    }

  } // end getPropFile()

} // end class VwComponentPropPropertyEditor{}

// *** end of VwComponentPropPropertyEditor.java ***

