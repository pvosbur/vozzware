/*
============================================================================================

    Source File Name: Transaction.java

    Author:           petervosburgh
    
    Date Generated:   12/7/21

    Time Generated:   7:47 AM

============================================================================================
*/
package com.vozzware.util;

/**
 *  This class takes a name filter to determin if a name string matches thes rules of the filter. The filter<br/>
 *  works a follows: beginsWith <string to match>*  ex. VwName* excpets any name that start with VwName
 *                   endsWith *<string to match>    ex. *Matcher excepts any name ending in Matcher
 *                   contains *<string to match>*   ex. *Wildcard*  excepts and name that conatins Wildcard
 *                   exact    <string to match>     ex  VwNameWildcardMatcher must match the entire string VwNameWildcardMatcher
 */
public class VwNameWildcardMatcher
{
  private enum MatchType { exact, startsWith, endsWith, contains};
  private MatchType m_eMatchType;

  private String[] m_astrFilters;
  private boolean m_bIgnoreCase = false;

  /**
   *  Default constructor. THis contains no filter which defaults to extact match the the name past
   */
  public VwNameWildcardMatcher()
  {
    m_astrFilters = null;
    m_bIgnoreCase = false;
  }


  /**
   *
   * @param bIgnoreCase
   */
  public VwNameWildcardMatcher( boolean bIgnoreCase )
  {
    m_astrFilters = null;
    m_bIgnoreCase = bIgnoreCase;
  }

  public VwNameWildcardMatcher( String strFilter )
  {
    this( strFilter, false );

  }

  public VwNameWildcardMatcher( String[] astrFilters )
  {
    this( astrFilters, false );

  }

  public VwNameWildcardMatcher( String strFilter, boolean bIgnoreCase )
  {
    m_astrFilters = new String[]{ strFilter};
    m_bIgnoreCase = bIgnoreCase;

  }

  public VwNameWildcardMatcher( String[] astrFilters, boolean bIgnoreCase )
  {
    m_astrFilters = astrFilters;
    m_bIgnoreCase = bIgnoreCase;

  }

  /**
   * Sets a sing filter to match
   * @param strFilter The filter string to apply
   */
  public void setFilter( String strFilter )
  {
    m_astrFilters = new String[]{strFilter};
  }

  /**
   * Sets multiple filter patterns which act as an 'or' contdition. If any of the filters pass its a match
   *
   * @param astrFilters an array of filter strings as defined inn the class doc
   */
  public void setFilter( String[] astrFilters )
  {
    m_astrFilters = astrFilters;
  }

  /**
   * Returns true if the name statisfies any of the filters passed
   *
   * @param strName The name to test
   * @return
   */
  public boolean hasMatch( String strName )
  {
    boolean bMatch = true;

    boolean bBreak = false;

    int nNameLen = strName.length();

    int nPos = strName.lastIndexOf( '.' );

    if ( m_bIgnoreCase )
    {
      strName = strName.toUpperCase();
    }

    if ( m_astrFilters == null )
    {
      m_astrFilters = new String[]{strName} ;
    }


    for ( int x = 0; x < m_astrFilters.length; x++ )
    {
      String strFilter = m_astrFilters[ x ];

      if ( m_bIgnoreCase )
      {
        strFilter = strFilter.toUpperCase();
      }

      if ( strFilter.equals( "*" ) )
      {
        return true;
      }

      if ( strFilter.startsWith( "*" ) )
      {
        strFilter = strFilter.substring( 1 );

        if ( strFilter.endsWith( "*" ) )
        {
          strFilter = strFilter.substring( 0, strFilter.length() - 1 );
          m_eMatchType = MatchType.contains;
        }
        else
        {
          m_eMatchType = MatchType.endsWith;

        }
      }
      else
      if ( strFilter.endsWith( "*" ) )
      {
        strFilter = strFilter.substring( 0, strFilter.length() - 1 );
        m_eMatchType = MatchType.startsWith;

      }
      else
      {
        m_eMatchType = MatchType.exact;

      }

      switch ( m_eMatchType )
      {
        case startsWith:

          bMatch = strName.startsWith( strFilter );  // Second file part test matches so we're all done
          break;

        case endsWith:

          bMatch = strName.endsWith( strFilter );  // Second file part test matches so we're all done
          break;

        case contains:

          bMatch = strName.contains( strFilter );  // Second file part test matches so we're all done
          break;

        default:

          bMatch = strName.equals( strFilter );
          break;

      } // end switch()

      if ( bMatch )
      {
        return true;
      }
    } // end for()

    return bMatch;

  }
}
