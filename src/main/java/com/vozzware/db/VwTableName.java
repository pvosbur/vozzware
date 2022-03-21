/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                         i  T e c h n o l o g i e s   C o r p (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwTableName.java

============================================================================================
*/
package com.vozzware.db;

public class VwTableName
{
  private   String  m_strSchema;
  private   String  m_strName;
  private   String  m_strAlias;
  
  /**
   * Constructor
   * 
   * @param strSchema The scheama name associated with the table (may be null)
   * @param strName The name of the table
   * @param strAliasThe name of the table alias (may be null)
   */
  public VwTableName( String strTableName )
  {
    int nPos = strTableName.indexOf( '.' );
    if ( nPos > 0 )
      m_strSchema = strTableName.substring( 0, nPos );

    int nAliasPos = strTableName.trim().lastIndexOf( ' ' );

    if ( nAliasPos > 0 )
      m_strAlias = strTableName.substring( nAliasPos + 1 );
    else
      nAliasPos = strTableName.length();

      
    if ( nPos > 0 )
      m_strName = strTableName.substring( ++nPos, nAliasPos );
    else
     m_strName = strTableName.substring( 0, nAliasPos );
    
  } // end VwTableName()

  public String getAlias()
  { return m_strAlias; }

  public String getName()
  { return m_strName; }

  public String getSchema()
  { return m_strSchema; }
  
  
} // end class VwTableName{}

// *** End of VwTableName.java

