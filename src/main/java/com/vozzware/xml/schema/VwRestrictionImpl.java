/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwRestrictionImpl.java

Create Date: Apr 11, 2006
============================================================================================
*/
package com.vozzware.xml.schema;

import javax.xml.schema.FractionDigits;
import javax.xml.schema.Length;
import javax.xml.schema.MaxExclusive;
import javax.xml.schema.MaxInclusive;
import javax.xml.schema.MaxLength;
import javax.xml.schema.MinExclusive;
import javax.xml.schema.MinInclusive;
import javax.xml.schema.MinLength;
import javax.xml.schema.Pattern;
import javax.xml.schema.Restriction;
import javax.xml.schema.TotalDigits;
import javax.xml.schema.WhiteSpace;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class VwRestrictionImpl extends VwExtensionImpl implements Restriction
{
   private List m_listFacets = new LinkedList();

  /**
   * Adds a Length facet
   * @param length  the length facet to add
   */
  public void addLength( Length length )
  { m_listFacets.add( length); }


  /**
   * Adds a MinLength facet
   * @param minLength the MinLength facet to add
   */
  public void addMinLength( MinLength minLength )
  { m_listFacets.add( minLength); }

  /**
   * Adds a MaxLength facet
   * @param maxLength the MaxLength facet to add
   */
  public void addMaxLength( MaxLength maxLength )
  { m_listFacets.add( maxLength); }

  /**
   * Adds a pattern facet
   * @param pattern  the pattern facet to add
   */
  public void addPattern( Pattern pattern )
  { m_listFacets.add( pattern ); }


  /**
   * Adds an Enumerationn facet
   * @param enumeration  Enumerationn facet to add
   */
  public void addEnumeration( javax.xml.schema.Enumeration enumeration )
  { m_listFacets.add( enumeration ); }


  /**
   * Adds a WhiteSpace facet
   * @param whiteSpace  the WhiteSpace facet to add
   */
  public void addWhiteSpace( WhiteSpace whiteSpace )
  { m_listFacets.add( whiteSpace ); }


  /**
   * Adds a MinExclusive facet
   * @param minExclusive The MinExclusive facet to add
   */
  public void addMinExclusive( MinExclusive minExclusive )
  { m_listFacets.add( minExclusive ); }


  /**
   * Adds a maxExclusive facet
   * @param maxExclusive The maxExclusive facet to add
   */
  public void addMaxExclusive( MaxExclusive maxExclusive )
  { m_listFacets.add( maxExclusive ); }

  /**
   * Adds a MinInclusive facet
   * @param minInclusive The MinInclusive facet to add
   */
  public void addMinInclusive( MinInclusive minInclusive )
  { m_listFacets.add( minInclusive ); }


  /**
   * Adds a MaxInclusive facet
   * @param maxInclusive The MaxInclusive facet to add
   */
  public void addMaxInclusive( MaxInclusive maxInclusive )
  { m_listFacets.add( maxInclusive ); }


  /**
   * Adds a TotalDigits facet
   * @param totalDigits The TotalDigits facet to add
   */
  public void addTotalDigits( TotalDigits totalDigits )
  { m_listFacets.add( totalDigits ); }


  /**
   * Adds a FractionDigits facet
   * @param FractionDigits The FractionDigits facet to add
   */
  public void addFractionDigits( FractionDigits FractionDigits )
  { m_listFacets.add( FractionDigits ); }

  public List getContent()
  {
    List listContent = new ArrayList();

    List listSuperContent = super.getContent();
    if ( listSuperContent != null )
      listContent.addAll(  listSuperContent );
    
    listContent.addAll( m_listFacets );

    return listContent;

  } // end getContent
} // end class VwRestrictionImpl{}

//*** End of VwRestrictionImpl.java ***  
