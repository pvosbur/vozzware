/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwDupValueException.java

============================================================================================
*/


package com.vozzware.util;

/**
 * This class defines an exception for a duplicate value condition
 */
public class VwMissingResourceException extends Exception
{

  private String    m_strResourceName;
  private String    m_strExpectedLocation;
  private String    m_strComments;
  
  /**
   * 
   * @param strResourceName
   * @param strExpectedLocation
   */
  public VwMissingResourceException( String strResourceName, String strExpectedLocation )
  { 
    m_strResourceName = strResourceName;
    m_strExpectedLocation = strExpectedLocation;
    
  } // end VwMissingResourceException()

  /**
   * 
   * @param strResourceName
   * @param strExpectedLocation
   * @param strComments
   */
  public VwMissingResourceException( String strResourceName, String strExpectedLocation, String strComments )
  { 
    m_strResourceName = strResourceName;
    m_strExpectedLocation = strExpectedLocation;
    m_strComments = strComments;
    
  } // end VwMissingResourceException()
  
  
  /**
   * Format message
   */
  public String toString()
  {
    StringBuffer sb = new StringBuffer( "The required resource '");
    sb.append( m_strResourceName ).append( "' could not be found at the expected location: ").append( m_strExpectedLocation );
    
    if ( m_strComments != null )
      sb.append( "\n").append( m_strComments );
    
    return sb.toString();
    
  } // end toString()

} // end VwDupValueException{}

// *** End of VwDupValueException.java ***
