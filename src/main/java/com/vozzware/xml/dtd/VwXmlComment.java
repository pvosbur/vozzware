/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwXmlComment.java

Create Date: Apr 11, 2006
============================================================================================
*/

package com.vozzware.xml.dtd;


public class VwXmlComment
{
  private String    m_strComment;           // Comment text


  /**
   * Constructor
   *
   * @param strComment The comment text
   */
  public VwXmlComment( String strComment )
  { m_strComment = strComment; }

  /**
   * Return the comment text
   *
   * @return String containing the comment
   */
  public String getComment()
  { return m_strComment; }

} // end class VwXmlComment{}

// *** End of VwXmlComment.java ***



