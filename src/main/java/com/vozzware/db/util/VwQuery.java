/*
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

  THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
  PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
  CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

    Source File Name: VwQuery.java

    Author:           Vw

    Date Generated:   11-03-2005

    Time Generated:   12:10:14

============================================================================================
*/

package com.vozzware.db.util;



public class VwQuery extends VwSqlStatement
{

  private String                 m_strQuery;                     
  private String                 m_strQuerySet;                  

  // *** The following members set or get data from the class members *** 

  /**
   * Sets the query property
   * 
   * @param strquery
   */
  public void setQuery( String strQuery )
  { m_strQuery = strQuery;
 }

  /**
   * Gets query property
   * 
   * @return  The query property
   */
  public String getQuery()
  { return m_strQuery; }

  /**
   * Sets the querySet property
   * 
   * @param strquerySet
   */
  public void setQuerySet( String strQuerySet )
  { m_strQuerySet = strQuerySet;
 }

  /**
   * Gets querySet property
   * 
   * @return  The querySet property
   */
  public String getQuerySet()
  { return m_strQuerySet; }
} // *** End of class VwQuery{}

// *** End Of VwQuery.java