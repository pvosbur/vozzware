/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwContentChangedListener.java

============================================================================================
*/

package com.vozzware.components;


/**
 * Components or models that need to know when content has been changed must implement
 * this interface.
 */
public interface IVwContentChangedListener
{

  public void contentChanged( VwContentChangedEvent cce );

} // end interface IVwContentSelectionListener{}

// *** End IVwContentSelectionListener.java ***
