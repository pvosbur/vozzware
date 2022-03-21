/*
 ============================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

  THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
  PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
  CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

  Source Name:   VwDbExpDesc.java


 ============================================================================
*/

package com.vozzware.db;                         // Our package

/**
 * This class holds the description and file location of an exported table.
 * It is package scope only for internal use.
 */
class VwDbExpDesc
{
  String  m_strName;        // The schema:table name pair used as key in the hash table
  long    m_lOffset;        // Starting file offset within the exported file where the data resides
  long    m_lRowCount;      // The nbr of rows exported

} // end class VwDbExpDesc{}

// *** End of VwDbExpDesc.java ***
  