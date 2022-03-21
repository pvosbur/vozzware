/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwCodeOptions.java

============================================================================================
*/

package com.vozzware.codegen;

public class VwCodeOptions
{
  /**
   * Alings open class and method brace under the keyword  I.E class MyClass
   *                                                                 {
   *                                                                 }
   */
  public static final int ALIGN_UNDER = 1;

  /**
   * Alings open class and method brace on same line as the definition I.E class MyClass{
   *                                                                             }
   */
  public static final int ALIGN_ON = 2;

  public int         m_nIndentation    = 2;     // Nbr of spaces of indentation
  public int         m_nOpenBraceStyle = ALIGN_UNDER;     // 1 = align open class or method brace
                                                // under name 0 = traditional on same line
  public boolean     m_fUseHungarian   = true;  // If TRUE use hungarian notation
  public boolean     m_fUseMbrPrefix   = true;  // If TRUE use defined member prefix I.E m_
  public boolean     m_fUseFunDoc      = true;  // If TRUE write function header documentation

  public short       m_sScreenWidth    = 100;   // Nbr of chars for screen centering
  public short       m_sPrivOrder      = 1;     // Order of private data and members
  public short       m_sProtOrder      = 3;     // Order of protected data and members
  public short       m_sPubOrder       = 4;     // Order of public data and methods
  public short       m_sDefOrder       = 2;     // Order of Default data and methods

  public String      m_strMbrPre       = "m_";  // Data member prefix
  public String      m_strArray        = "a";   // Hungarian base for array data types
  public String      m_strBool         = "f";   // Hungarian notation for public boolean data types
  public String      m_strByte         = "b";   // Hungarian notation for  byte data types
  public String      m_strChar         = "ch";  // Hungarian notation for signed character data types
  public String      m_strShort        = "s";   // Hungarian notation for public short data types
  public String      m_strInt          = "n";   // Hungarian notation for public int data types
  public String      m_strLong         = "l";   // Hungarian notation for long data types
  public String      m_strFloat        = "flt"; // Hungarian notation for float data types
  public String      m_strDbl          = "dbl"; // Hungarian notation for double data types
  public String      m_strString       = "str"; // Hungarian notation for public String data types
  public String      m_strDlmSring     = "dlms";// Hungarian notation for delimited String data types
  public String      m_strDate         = "dt";  // Hungarian notation for date data types
  public String      m_strTime         = "tm";  // Hungarian notation for time data types
  public String      m_strTimeStamp    = "ts";  // Hungarian notation for Sql Timestamp data types
  public String      m_strList         = "list"; // Hungarian notation for blobB data types
  public String      m_strMap          = "map"; // Hungarian notation for blobB data types
  public String      m_strBlob         = "blb"; // Hungarian notation for blobB data types
  public String      m_strName = "";            // Name that goes in file header block
  public String      m_strCopyright = "";       // User defined code copyright notice
  public String      m_strAuthor = null;        // Author of source file

} // end struct VwCodeOptions

// *** End of VwCodeOptions.java

