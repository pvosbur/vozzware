/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwPageNbrElementPos.java

============================================================================================
*/

package com.vozzware.print;



/**
 * This class defines a page number element position object that renders a
 * page number.
 */
public class VwPageNbrElementPos
{

  private int       m_nPageNbr = 0;       // Starting Page Nbr

  private String    m_strNbrPrefix;      // Optional Prefix to the page numbers
  private String    m_strNbrSuffix;      // Optional Prefix to the page numbers

  /**
   * Default constructor which prints just the page nbr starting at nbr 1
   *
   * @param nPosType One of the position constants defined in this class (LEFT,
   * CENTERED, RIGHT, or ABSOLUTE).
   * @param nXPos The absolute position on the line in device units
   * @param nYOffset An optional y coordinate offset from the start of the line
   * or zero.
   */
  public VwPageNbrElementPos( int nPosType, int nXPos, int nYOffset )

  {

    m_nPageNbr = 1;


  } // end VwDataObjElementPos()


  /**
   * Constructor which specifies the starting page nbr and an optional prefix
   * and suffix.
   *
   * @param nStartPageNbr The starting page number
   * @param strNbrPrefix An optional string which is prefixed to the page
   * number, if supplied; null if not supplied.
   * @param strNbrSuffix An optional string which is suffixed to the page
   * number, if supplied; null if not supplied.
   * @param nPosType One of the position constants defined in this class (LEFT,
   * CENTERED, RIGHT, or ABSOLUTE).
   * @param nXPos The absolute position on the line in device units
   * @param nYOffset An optional y coordinate offset from the start of the line
   * or zero.
   */
  public VwPageNbrElementPos( int nStartPageNbr, String strNbrPrefix, String strNbrSuffix,
                            int nPosType, int nXPos, int nYOffset,
                            int nElementWidth, int nJustify )
  {

    m_nPageNbr = nStartPageNbr;
    m_strNbrPrefix = strNbrPrefix;
    m_strNbrSuffix = strNbrSuffix;

  } // end VwDataObjElementPos()


  /**
   * Returns the page number formatted as specified in the constructor
   *
   * @return A page number formatted as stated
   */
  public Object getData() throws Exception
  {
    String strPage = "";

    if ( m_strNbrPrefix != null )
      strPage += m_strNbrPrefix;

    strPage += m_nPageNbr++;

    if ( m_strNbrSuffix != null )
      strPage += m_strNbrSuffix;

    return strPage;

  } // end getData()


} // end class VwPageNbrElementPos{}


// *** End VwPageNbrElementPos.java ***

