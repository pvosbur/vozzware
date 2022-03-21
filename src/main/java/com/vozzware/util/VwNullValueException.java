/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwNullValueException.java

============================================================================================
*/


package com.vozzware.util;

/**
 * This class defines an exception for a null value condition
 */
public class VwNullValueException extends Exception
{
  private   String  m_strNullIdName;
  /**
   * Constructs an exception instance with the string "NULL Value" as its reason
   *
   * @param The data object id that is null
   */
  public VwNullValueException( String strNullIdName )
  { super( strNullIdName + " is NULL" ); m_strNullIdName = strNullIdName; }


  /**
   * Gets the name of the dataobject id that is NULL
   *
   * @return The name of the DataObject ID that is NULL
   */
  public String getNullIDName()
  { return m_strNullIdName; }


} // end VwNullValueException{}


// *** End of VwNullValueException.java ***

