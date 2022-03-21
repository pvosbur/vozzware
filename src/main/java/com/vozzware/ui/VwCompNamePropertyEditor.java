/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwCompNamePropertyEditor.java

Create Date: Apr 11, 2003
============================================================================================
*/
package com.vozzware.ui;

import java.beans.PropertyEditorSupport;
import java.io.DataOutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Hashtable;

public class VwCompNamePropertyEditor extends PropertyEditorSupport
{

  static String m_strName;
  static DataOutputStream outs;
  static Hashtable m_htNames = new Hashtable();

  public VwCompNamePropertyEditor()
  {
    try
    {
      Socket s = new Socket( "localhost", 2600 );

      outs = new DataOutputStream( s.getOutputStream() );

      outs.writeBytes( "In Edit constructor" );
      System.setOut( new PrintStream( outs ) );
    }
    catch( Exception e )
    {

    }

  }

  public void setValue( Object o )
  {

    if ( o == null )
    {
      m_strName = null;
      return;
    }

    m_strName = (String)o;

    m_htNames.put( m_strName, " " );
    
    System.out.println( "Set Name is " + m_strName );

    firePropertyChange();
    
  }

  public Object getValue()
  { return getAsText(); }


  public void setAsText( String strName )
  { setValue( strName ); }


  public String getAsText()
  { System.out.println( "Getting name " + m_strName ); return m_strName; }


} // end class VwCompNamePertyEditor{}

// *** end of VwCompNamePropertyEditor.java ***

