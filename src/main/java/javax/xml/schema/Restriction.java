/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: Restriction.java

============================================================================================
*/
package javax.xml.schema;

/**
 * This represents the XML Schema restriction element
 */
public interface Restriction extends Extension
{
  /**
   * Adds a Length facet
   * @param length The length facet to add
   */
  public void addLength( Length length );

  /**
   * Adds a MinLength facet
   * @param minLength The MinLength facet to add
   */
  public void addMinLength( MinLength minLength );


  /**
   * Adds a MaxLength facet
   * @param maxLength The MaxLength facet to add
   */
  public void addMaxLength( MaxLength maxLength );


  /**
   * Adds a Pattern facet
   * @param pattern  the Pattern facet to add
   */
  public void addPattern( Pattern pattern );


  /**
   * Adds a javax.xml.schema.Enumeration facet
   * @param enumeration  the javax.xml.schema.Enumeration facet to add
   */
  public void addEnumeration( javax.xml.schema.Enumeration enumeration );


  /**
   * Adds a WhiteSpace facet
   * @param whiteSpace  the WhiteSpace facet to add
   */
  public void addWhiteSpace( WhiteSpace whiteSpace  );

  /**
   * Adds a MaxInclusive facet
   * @param maxInclusive The MaxInclusive facet to add
   */
  public void addMaxInclusive( MaxInclusive maxInclusive );


  /**
   * Adds a MaxExclusive facet
   * @param maxExclusive The MaxExclusive facet to add
   */
  public void addMaxExclusive( MaxExclusive maxExclusive );


  /**
   * Adds a MinInclusive facet
   * @param minInclusive The XaxExclusive facet to add
   */
  public void addMinInclusive( MinInclusive minInclusive );


  /**
   * Adds a MinExclusive facet
   * @param minExclusive The MaxInclusive facet to add
   */
  public void addMinExclusive( MinExclusive minExclusive );


  /**
   * Adds a TotalDigits facet
   * @param totalDigits The TotalDigits facet to add
   */
  public void addTotalDigits( TotalDigits totalDigits );


  /**
   * Adds a FractionDigits facet
   * @param fractionDigits The FractionDigits facet to add
   */
  public void addFractionDigits( FractionDigits fractionDigits );

} // end interface Restriction{}

// *** End of Restriction.java ***

