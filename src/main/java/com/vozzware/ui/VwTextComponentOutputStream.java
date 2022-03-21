/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwTextComponentOutputStream.java

Create Date: Apr 11, 2003
============================================================================================
*/
package com.vozzware.ui;

import javax.swing.text.JTextComponent;
import java.io.OutputStream;

public class VwTextComponentOutputStream extends OutputStream
{

	private JTextComponent	m_textComp;

	public VwTextComponentOutputStream( JTextComponent textComp  )
	{
		super();
		m_textComp = textComp;
	}

	public void write( int ch )
	{
		m_textComp.setText( m_textComp.getText() + (char)ch );
    }
} // end class VwTextComponentOutputStream{}

// *** end of VwTextComponentOutputStream.java ***

