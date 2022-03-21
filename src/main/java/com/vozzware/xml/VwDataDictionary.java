/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwDataDictionary.java

Create Date: Apr 11, 2006
============================================================================================
*/

package com.vozzware.xml;              // The package this class belongs to


/**
 * This class is a container for data formatting and validation attributes.  Since it is
 * derived from VwDataObject, this class can serialize the current object into a byte
 * array data stream, so it can easily be sent over some communications medium to a server.
 * The message stream can then be de-serialized back into the data elements (i.e., the
 * data formatting and validation attributes).
 */
public class VwDataDictionary extends VwDataObject
{
  /**
   * Constant for the Editmask entry key
   */
  public static final String EDIT_MASK = "EDIT_MASK";

  /**
   * Constant for the Values/Ranges entry key
   */
  public static final String VALUES_RANGES = "VALUES_RANGES";


  /**
   * Constant for the Value separator property
   */
  public static final String VALUE_SEP = "VALUE_SEP";

  /**
   * Constant for the Range separator property
   */
  public static final String RANGE_SEP = "RANGE_SEP";

  /**
   * Constant for the maximum character input allowed
   */
  public static final String MAX_INPUT = "MAX_INPUT";

  /**
   * Constant for the minimum character input allowed
   */
  public static final String MIN_INPUT = "MIN_INPUT";

  /**
   * Constant for the entry required flag
   */
  public static final String ENTRY_REQUIRED = "ENTRY_REQUIRED";

  /**
   * Constant for the Dictionary item name
   */
  public static final String ITEM_NAME = "ITEM_NAME";

  /**
   * Constant for the Dictionary item name
   */
  public static final String DATA_TYPE = "DATA_TYPE";


  private static String[] m_astrKeys = { EDIT_MASK, VALUES_RANGES, VALUE_SEP, RANGE_SEP,
                                         MAX_INPUT, MIN_INPUT, ENTRY_REQUIRED, ITEM_NAME, DATA_TYPE };

  /**
   * Constructs an empty VwDataDictionary object
   *
   */
  public VwDataDictionary()
  {
    super( "VwDictionaryService" );

  } // end VwDataDictionary()


  /**
   * Constructs an empty VwDataDictionary
   *
   */
  public VwDataDictionary( VwDataObject copyObj )
  {
    super( copyObj );

    VwServiceFlags.setServiceName( this, "VwDictionaryService" );

  } // end VwDataDictionary()


  /**
   * Adds an VwSmartData data item to this object
   *
   * @param obj - The VwSmartData-derived data item to be added
   */
  public final void add( String strKey, String strData ) throws Exception
  {
    for ( int x = 0; x < m_astrKeys.length; x++ )
    {

      if ( m_astrKeys.equals( strKey ) )      // *** Passed edits so class supers add method to add the item
      {
        super.add( strKey, strData );
        return;

      } // end for()

    }

    throw new Exception( "Illegal Key specified. Must be one of the public constants "
                          + "defined in the VwDataDictionary class" );

  } // end add()


} // end class VwDataDictionary


// *** End of VwDataDictionary.java ***
