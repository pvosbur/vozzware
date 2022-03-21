/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwExString.java

============================================================================================
*/


package com.vozzware.util;

import java.io.File;
import java.lang.reflect.Array;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @(#) VwExString.java
 * This class extends the capabilities of the Java String class. All methods are static.
 *
 */
public class VwExString
{
  
  private  static Map<String,String>s_mapCharEntities;
  
  static
  {
    loadEntitiyMap();
    
  }

  /**
   * Removes the file name from a complete directory path.  E.g., if the file path is
   * "C:\Myfiles\test.txt", then this method returns "test.txt" if fWithExt is True,
   * or "test" if fWithExt is False.
   *
   * @param strFilePath - A string with the complete file path
   *
   * @param fWithExt - If True, the file name is returned with the file extension;
   * if False, the file name is returned without the extension.
   *
   * @return A string containing the file name, with or without the extension,
   * depending upon the value of fWithExt.
  */
  public static final String getFileName( String strFilePath, boolean fWithExt )
  {
    String strFileName;                         // Holds extracted table name

    // *** Strip out file path to get just the file name

    int nPos = strFilePath.lastIndexOf( '\\' );

    if ( nPos < 0 )
    {
      nPos = strFilePath.lastIndexOf( '/' );
    }
    
    if ( nPos  >= 0 )                           // if Npos > 0 we have a path in front of file name
    {
      strFileName = strFilePath.substring( nPos + 1 );
    }
    else
    {
      strFileName = strFilePath;                // No path set file equla to param passed
    }


    // *** Strip off any file name extension if specified, if ext is not wanted

    if ( fWithExt == false )
    {
      nPos =  strFileName.lastIndexOf( '.' );
      if ( nPos >= 0 )
      {
        strFileName = strFileName.substring( 0, nPos );
      }
    }

    return strFileName;

  } // end getFileName()


  /**
   * Converts a File system path string to URL syntax format.
   * <br>i.e. c:\someDirectory\foo.bar becomes file:///c:/someDirectory/foo.bar
   * @param strPath The file path string to convert
   * @return The file path string converted to URL string format
   */
  public static String pathToURLFormat( String strPath )
  {
    String strUrl = "file:///" + strPath;

    return strUrl.replace( File.separatorChar, '/' );

  } // end pathToURLFormat

  /**
   * Converts a file: URL format to a File path format for the os it's running on
   * <br>i.e. file:///c:/someDirectory/foo.bar becomes c:\someDirectory\foo.bar becomes
   * @param strUrl The url string to convert
   * @return The file path string converted to URL string format
   */
  public static String urlToPathFormat( String strUrl )
  {
    int nPos = strUrl.indexOf( ':' );

    if ( nPos < 0 )
    {
      throw new RuntimeException( "Invalid URL Format " );
    }

    for ( ++nPos; nPos < strUrl.length(); nPos++ )
    {
      if ( strUrl.charAt( nPos ) != '/' )
      {
        break;
      }

    }

    String strPath = strUrl.substring( nPos );

    return strPath.replace( '/', File.separatorChar );

  } // end urlToPathFormat

  /**
   * Converts a file: URL format to a File path format for the os it's running on
   * <br>i.e. file:///c:/someDirectory/foo.bar becomes c:\someDirectory\foo.bar becomes
   * @param url The URL to convert
   * @return The file path string converted to URL string format
   */
  public static String urlToPathFormat( URL url )
  { return urlToPathFormat( url.toString() ); }


  /**
   * Converts a web query string name value/value pairs to a map
   *
   * @param strQueryString The query string
   * @return
   */
  public static Map<String,String>queryStringToMap( String strQueryString )
  {
    String[] astrQueryPieces = strQueryString.split( "&" );
    Map<String,String>mapParams = new HashMap<>(  );;

    for ( int x = 0; x < astrQueryPieces.length; x++ )
    {
      int nPos =  astrQueryPieces[ x ].indexOf( '=' );

      String strKey =  astrQueryPieces[ x ].substring( 0, nPos );
      String strVal = astrQueryPieces[ x ].substring( ++nPos );

      mapParams.put( strKey, strVal );

    }

    return mapParams;

  }

  /**
   * Extracts the file extension from a file path.  E.g., if the complete file path
   * is "test.txt", then this function returns "txt".
   *
   * @param  file - The file object to get the extension for
   *
   * @return A string with the base file extension, or null if no extension is found
  */
  public static final String getFileExt( File file )
  {
    return getFileExt( file.getName() );

  }

  /**
   * Returns a comma separated list of the array values
   *
   * @param astrValues
   * @return
   */
  public static final String toString( String[] astrValues )
  {
    if ( astrValues == null )
    {
      return "";

    }

    String strRes = "";

    for ( int x = 0; x < astrValues.length; x++ )
    {
      if ( strRes.length() > 0 )
      {
        strRes += ", ";
      }

      strRes += astrValues[ x ];

    }

    return strRes;

  } // end toString()
  /**
   * Extracts the file extension from a file path.  E.g., if the complete file path
   * is "test.txt", then this function returns "txt".
   *
   * @param  strFilePath - A string with the complete file path
   *
   * @return A string with the base file extension, or null if no extension is found
  */
  public static final String getFileExt( String strFilePath )
  {
    String strFileName;                          // Holds extracted file name

    // *** Strip out file path to get just the file name

    int nPos = strFilePath.lastIndexOf( '\\' );

    if ( nPos  >= 0 )
    {
      strFileName = strFilePath.substring( nPos + 1 );
    }
    else
    {
      strFileName = strFilePath;                 // else no path specified so file name is the path name
    }

    // *** Look for extension

    nPos =  strFileName.lastIndexOf( '.' );

    if ( nPos >= 0 )
    {
      return  strFileName.substring( nPos + 1 );
    }

    return null;                                // no extension found

  } // end getFileName()


  /**
   * Extracts the file path from a complete file path.  E.g., if the complete file path is
   * "C:\Myfiles\text.txt", then this function returns "\MyFiles".
   *
   * @param strFilePath - A string with the complete file path
   *
   * @return A string with the base directory path, or null if no path is found
   */
  public static final String getDirPath( String strFilePath )
  {
    // *** Strip out file path to get just the table name

    int nPos = strFilePath.lastIndexOf( "/" );

    // ok could be could be a dos separater
    if ( nPos < 0 )
    {
      nPos = strFilePath.lastIndexOf( "\\" );
    }

    if ( nPos  >= 0 )
    {
      return strFilePath.substring( 0, nPos );
    }

    return null;                            // No path found

  } // end getDirPath()



  /**
   * Extracts the drive letter form a directory path (System dependent for DOS style
   * disk devices).  E.g., if the file path is "C:\Myfiles\test.txt", then this function
   * returns "C".
   *
   * @param strFilePath - A string with the full file path
   *
   * @return A string with the drive letter, or null if no drive letter is found
  */
  public static final String getDrive( String strFilePath )
  {
   int nPos = strFilePath.indexOf( ':' );

   if ( nPos > 0 )
   {
     return strFilePath.substring( 0, 1 );
   }

   return null;


  } // end getDrive()


  /**
   * Converts strings that contain uppercase and underscore characters to a standard Java convention<br>
   * by making the first character of the string upper case, the rest of the characaters lower case<br>
   * and removes underscore converting the following character to upper case.<br> For example the string HELLO_THERE
   * would be converted to HelloThere.<br> This is commonly used to convert database catalog names to Java style
   * bean property names.
   * 
   * @param strOrigName The string to convertd
   * @param fLowerCaseFirstLetter if true make first letter lower case
   * @return The converted string
   */
  public static final String makeJavaName( String strOrigName, boolean fLowerCaseFirstLetter )
  {

    String strTemp = strOrigName.toLowerCase();

    // These could be table names in a schema.table convention so strip off any prefix
    int nPos = strTemp.lastIndexOf( "." );

    if ( nPos > 0)
    {
      strTemp = strTemp.substring( ++nPos );
    }

    StringBuffer sb = new StringBuffer();

    // Make first character upper case

    if ( fLowerCaseFirstLetter )
    {
      sb.append( strTemp.substring( 0, 1).toLowerCase() );
    }
    else
    {
      sb.append( strTemp.substring( 0, 1).toUpperCase() );
    }

    // Remove underscores and convert character following underscore to uppercase
    for ( int x = 1; x < strTemp.length(); x++ )
    {
       if ( strTemp.charAt( x ) == '_' )
       {
         if ( ( x + 1 ) < strTemp.length() )
         {
           sb.append( Character.toUpperCase( strTemp.charAt( x + 1 ) ) );
           x += 1;    // bypass underscore
         } // end if

         continue;

       } // end if

       sb.append( strTemp.charAt( x ) );

    }  // end for()

    return sb.toString();
    
  } // end makeJavaName

  
  /**
   * Determines if the string is numeric.  Commas, leading spaces, and one decimal point
   * are allowed.
   *
   * @param strTest - The string to test
   *
   * @return True if the string is numeric; otherwise False is returned
  */
  public static final boolean isNumeric( String strTest )
  {
    if ( strTest == null )
    {
      return false;
    }

    if ( strTest.length() == 0 )
    {
      return false;
    }

    // *** First trim any leading white space

    String strTemp = strTest.trim();

    int nDecimalCount = 0;                    // Keeks decimal count, if more than one then string is not numeric
    int nLen = strTemp.length();

    if ( nLen == 0 )
    {
      return false;                           // If after trim we are zero we're all done
    }

    // *** If the first character is not a digit it must be a plus or minus sign or decimal point

    if ( !isdigit( strTemp.charAt( 0 ) ) )
    {
      if ( strTemp.charAt( 0 ) != '+' && strTemp.charAt( 0 ) != '-' && strTemp.charAt( 0 ) != '.' )
      {
        return false;                         // Invalid character
      }

      if ( strTemp.charAt( 0 ) == '.' )
      {
        ++nDecimalCount;
      }
    }

    // *** Scan rest of string for valid numerics

    for( int ndx = 1; ndx < nLen; ndx++ )
    {
      if ( strTemp.charAt( ndx ) == '.' )
      {
        if ( ++nDecimalCount > 1 )
        {
          return false;                       // Only one decimal point allowed
        }
      }
      else
      if ( !isdigit( strTemp.charAt( ndx ) ) ) // If character is not a digit it must be a comma or string is not numeric
      {
        if ( strTemp.charAt( ndx ) != ',' )
        {
          return false;
        }
      }

    } // end for

   return true;                               // All tests passed

  } // end isNumeric()


  /**
   * Determines if the given string contains only the digits 0 - 9 and possibly a comma
   *
   * @param strVal - The string to test
   * @param fAllowCommas - If True, commas are ignored when testing; if False, commas
   * that are detected will cause the test to fail.
   *
   * @return True if all characters pass the applicable tests (the numeric test is mandatory;
   * the comma test is optional); otherwise False is returned.
  */
  public static final boolean isIntegral( String strVal, boolean fAllowCommas )
  {
    if ( strVal == null )
    {
      return false;
    }

    int nLen = strVal.length();

    if ( nLen == 0 )
    {
      return false;
    }

    for ( int x = 0; x < nLen; x++ )
    {
      if ( !isdigit( strVal.charAt( x ) ) )
      {
        if ( strVal.charAt( x ) == ',' )
        {
          if ( fAllowCommas )
          {
            continue;
          }
        }

        return false; // invalid character

      } // end if

    } // end for()

    return true;      // passed the test

  } // end isIntegral()


  /**
   * Changes every occurrence of the search substring in a given string with the replace substring
   *
   * @param strOrig - The string to be searched
   * @param strSearch -  The substring to be searched for and replaced
   * @param strReplace - The substring used to replace the search substring
   *
   * @return A new string with the results of the search and replace operation
   */
  public static final String replace( String strOrig, String strSearch, String strReplace )
  { return replace( strOrig, strSearch, strReplace, 0 ); }


  /**
   * Changes every occurrence of the search string in a given string, beginning at the specified
   * start position, with the replace string.
   *
   * @param strOrig - The string to be searched
   * @param strSearch -  The substring to be searched for and replaced
   * @param strReplace - The substring used to replace the search substring, if null the strSearch string is just omitted
   * @param nStartPos - The position in the original string to start the search
   *
   * @return A new string with all applicable substrings replaced.
   */
  public static final String replace( String strOrig, String strSearch, String strReplace, int nStartPos )
  {
    int nLen = strOrig.length();
    
    StringBuffer sbResult = new StringBuffer( nLen );                           // String to build final result

    int nOff = strOrig.indexOf( strSearch, nStartPos );

    if ( nOff < 0  )
    {
      return strOrig;                                                           // No substring found, return the orig string
    }

    int nStartOff = 0;
    int nSearchLen = strSearch.length();

    while( nOff >= 0 )
    {
      int nSubLen = nOff - nStartOff;

      sbResult.append( strOrig.substring( nStartOff, (nStartOff + nSubLen)) );  // Copy up to found substring
      if ( strReplace != null )
      {
        sbResult.append( strReplace );                                           // Add in replacement string
      }

      nOff += nSearchLen;                                                       // Move base ptr past search
      nStartOff  = nOff;
      nOff = strOrig.indexOf( strSearch, nOff );

    } // end while

    // If next start position is not at the end of the string, add the
    // rest of tthe base string to the result string

    if ( nStartOff < nLen )
    {
      sbResult.append( strOrig.substring( nStartOff ) );                     // copy rest of string string
    }

    return sbResult.toString();                                                 // return changed string

  } // end replace()

  
  /**
   * replace marker patterns in a string from an array of String values.
   * 
   * @param strOrig The string that needs values replaced
   * @param strIndexMarker The index marker can be a '{' followed by an index nbr and a closing '}' i.e., {0}, {1} etc... or 
   * it can be something like %1, %2 ... which does not require a closing marker. In this case the index number is scanned from the 2cd character
   * of the marker sequence until the first non digit character is encountered.
   * @param astrValues The array of string values used for marker substitution
   * @return A New String containing the substituted values
   * @throws Exception
   */
  public static final String replace( String strOrig, String strIndexMarker, String[] astrValues ) throws Exception
  {
    int nLen = strOrig.length();
    
    StringBuffer sbResult = new StringBuffer( nLen );           // String to build final result

    int nStartPos = 0;
    
    int nOff = strOrig.indexOf( strIndexMarker, nStartPos );

    if ( nOff < 0  )
    {
      return strOrig;                                           // No substring found, return the orig string
    }

    int nStartOff = 0;

    while( nOff >= 0 )
    {
      int nSubLen = nOff - nStartOff;

      sbResult.append( strOrig.substring( nStartOff, (nStartOff + nSubLen)) ); // Copy up to found substring
      char ch = strOrig.charAt( nOff );
      int ndx = -1;
      String strIndex = "";

      if ( ch == '{')
      {
        while( true )
        {
          if ( ++nOff >= nLen )
          {
            throw new Exception( "Invalid index place marker, expecting closing }");
          }
          
          if ( ch == '}' )
          {
            break;
          }

          strIndex += strOrig.charAt( nOff );
          
        }
      } // end if
      else
      {
        while( true )
        {
          if ( ++nOff >= nLen )
          {
            throw new Exception( "Invalid index place marker, expecting closing }");
          }
          
          if ( !Character.isDigit( strOrig.charAt( nOff ) ))
          {
            break;
          }
          
          strIndex += strOrig.charAt( nOff );
          
        } // end while
        
      } // end else
      
      ndx = Integer.parseInt( strIndex );
        
      
      if ( ndx >= astrValues.length )
      {
        throw new Exception( "Invalid index value: " + ndx + " it is out of bounds with array supplied" );
      }
      
      sbResult.append( astrValues[ ndx ] );   
      nStartOff  = nOff;
      nOff = strOrig.indexOf( strIndexMarker, nOff );

    } // end while

    // If next start position is not at the end of the string, add the
    // rest of the base string to the result string

    if ( nStartOff < nLen )
    {
      sbResult.append( strOrig.substring( nStartOff ) );                     // copy rest of string string
    }

    return sbResult.toString();                                                 // return changed string
    
  } // end replace()


  /**
   * replace marker patterns in a string from a map String values. using the ${macroName} pattern
   * @param strOrig  The original string to expnad
   * @param mapValues The map of replacement values
   * @return
   * @throws Exception
   */
  public static final String replace( String strOrig, Map<String,String> mapValues ) throws Exception
  {return replace( strOrig, "${", "}", mapValues); }


  /**
   * replace marker patterns in a string from a map String values. using the ${macroName} pattern
   * @param strOrig  The original string to expnad
   * @param objValues a bean where macro names are properties
   * @return
   * @throws Exception
   */
  public static final String replace( String strOrig, Object objValues ) throws Exception
  {return replace( strOrig, "${", "}", objValues); }


  /**
   * replace marker patterns in a string from a map String values.
   *
   * @param strOrig The string that needs values replaced
   * @param strMarkerStart A string or character sequrnce that starts the macro to be expanded
   * @param strMarkerEnd The character sequence thyat ends the macro
   * @param objValues either a map or a value object bean
   * @return A New String containing the substituted values
   * @throws Exception
   */
  public static final String replace( String strOrig, String strMarkerStart, String strMarkerEnd, Object objValues ) throws Exception
  {
    int nLen = strOrig.length();
    int nMarkerStartLen = strMarkerStart.length();
    int nMarkerEndLen = strMarkerEnd.length();
    
    StringBuffer sbResult = new StringBuffer( nLen );                           // String to build final result

    int nStartPos = 0;
    
    int nOff = strOrig.indexOf( strMarkerStart, nStartPos );

    if ( nOff < 0  )
    {
      return strOrig;                                                           // No substring found, return the orig string
    }

    int nStartOff = 0;

    while( nOff >= 0 )
    {
      int nSubLen = nOff - nStartOff;

      sbResult.append( strOrig.substring( nStartOff, (nStartOff + nSubLen)) ); // Copy up to found substring

      nOff += nMarkerStartLen;
      
      int nPos = strOrig.indexOf( strMarkerEnd, nOff );
      if ( nPos < 0 )
      {
        throw new Exception( "Found macro start marker '" + strMarkerStart + "' at offset " + (nOff - nMarkerStartLen) + " but did not find marker end '" + strMarkerEnd + "'");
      }
      
      
      // Variable name is in between
      
      String strMacroName = strOrig.substring( nOff, nPos );
      
      String strMacroValue = null;

      if ( objValues instanceof Object[] )
      {
        strMacroValue = (String)Array.get( objValues, Integer.parseInt( strMacroName ) );
      }
      else
      if ( objValues instanceof Map)
      {
        strMacroValue = (String)resolvMacroValue( strMacroName, (Map) objValues );

      }
      else
      {
        strMacroValue = (String)resolvMacroValue( strMacroName, objValues );
      }
      
      if ( strMacroValue == null )   // no value found retrun the macro name
      {
        strMacroValue = "${" + strMacroName +"}";
      }

      nPos += nMarkerEndLen;
      
      sbResult.append( strMacroValue );   
      nStartOff  = nPos;
      nOff = strOrig.indexOf( strMarkerStart, nOff );

    } // end while

    // If next start position is not at the end of the string, add the
    // rest of tthe base string to the result string

    if ( nStartOff < nLen )
    {
      sbResult.append( strOrig.substring( nStartOff ) );                     // copy rest of string string
    }

    return sbResult.toString();                                                 // return changed string
    
  } // end replace()
  /**
   * Replace any character in the character array (ach) with the replement character (chReplace).
   *
   * @param strOrig - The string to be searched
   * @param ach array of characters to replace
   * @param chReplace - The character to replace
   *
   * @return A new string with all applicable substrings replaced.
   */
  public static final String replace( String strOrig, char[] ach, char chReplace )
  { return replace( strOrig, ach, chReplace, 0 ); }
  
  
  /**
   * Replace any character in the character array (ach) with the replement character (chReplace) beginning
   * at the start position specified.
   *
   * @param strOrig - The string to be searched
   * @param ach array of characters to replace
   * @param chReplace - The character to replace
   * @param nStartPos - The position in the original string to start the search
   *
   * @return A new string with all applicable substrings replaced.
   */
  public static final String replace( String strOrig, char[] ach, char chReplace, int nStartPos )
  {
    int nLen = strOrig.length();
    StringBuffer sbResult = new StringBuffer( nLen );                            // String to build final result

    for ( int x = nStartPos; x < nLen; x++ )
    {
      char ch = strOrig.charAt( x );
      boolean fReplaced = false;
      
      for ( int j = 0; j < ach.length; j++ )
      {
        if ( ach[ j ] == ch )
        {
          sbResult.append( chReplace );	// add replacement character
          fReplaced = true;
        
          break;
        }
        
      }
      
      if ( fReplaced )
      {
        continue;
      }

      sbResult.append( ch );  // add original character
    }
    
    return sbResult.toString();
    
  } // end replace()

  /**
   * Replace any character the the character array (ach) with the replement character (chReplace)
   * 
   *
   * @param strOrig - The string to be searched
   * @param chSearch The  character to replace
   * @param chReplace - The character to replace
   *
   * @return A new string with all applicable substrings replaced.
   */
  public static final String replace( String strOrig, char chSearch, char chReplace )
  { return replace( strOrig, chSearch, chReplace, 0 ); }
  
  
  /**
   * Replace any character the the character array (ach) with the replement character (chReplace) beginning with
   * at the start position specified.
   *
   * @param strOrig - The string to be searched
   * @param chSearch The character to replace
   * @param chReplace - The character to replace
   *
   * @return A new string with all applicable substring's replaced.
   */
  public static final String replace( String strOrig, char chSearch, char chReplace, int nStartPos )
  {
    int nLen = strOrig.length();
    StringBuffer sbResult = new StringBuffer( nLen );                            // String to build final result

    for ( int x = nStartPos; x < nLen; x++ )
    {
      
      char ch = strOrig.charAt( x );

      if ( ch  == chSearch )
      {
        sbResult.append( chReplace );
      }
      else
      {
        sbResult.append( ch );  // add original character
      }
    }
    
    return sbResult.toString();
    
  } // end replace()

  
  
  /**
   * Removes every occurrence of the search substring from a given string
   *
   * @param strOrig - The string to be searched
   * @param strRemove - The substring to be searched for and removed
   *
   * @return A new string with all occurrences of the substring removed.
  */
  public static final String remove( String strOrig, String strRemove )
  { return remove( strOrig, new String[]{ strRemove }, 0 ); }


  public static final String remove( String strOrig, String strRemove, int nStartPos )
  {
    return remove( strOrig, new String[]{ strRemove }, nStartPos );
  }

  /**
   * Removes every occurrence of the search substring from a given string,
   * starting at the position specified.
   *
   * @param strOrig - The string to be searched
   * @param astrRemove - Array of substrings to be searched for and removed
   * @param nStartPos - The position in the original string to start the search from

   * @return A new string with all applicable substrings removed.
  */
  public static final String remove( String strOrig, String[] astrRemove, int nStartPos )
  {

    StringBuffer sbResult = new StringBuffer( strOrig ); // buffer to build final result

    for ( int x = 0; x < astrRemove.length; x++ )
    {
      int nStartOff = sbResult.indexOf( astrRemove[ x ], nStartPos );

      if ( nStartOff < 0  )
      {
        continue;                                 // No substring occurrence found, return the orig string
      }

      int nRemoveLen = astrRemove[x].length();

      while( nStartOff >= 0 )
      {
        // *** copy up to found sub string that we're removing

        sbResult.delete( nStartOff, (nStartOff + nRemoveLen)  );
        nStartOff = sbResult.indexOf( astrRemove[x] );

      } // end while

    }

    return sbResult.toString(); // return changed string

  } // end remove()



  /**
   * Stretch a string with a space fill character for the amount of gap space.
   * i.e. The string hello with a gap of 1  becomes h e l l o
   * 
   * @param strString The string to stretch
   * @param nGap The number of fill characters between each original staring character
   * 
   * @return A new String stretched with the with space(s) filled 
   */
  public static String stretch( String strString, int nGap )
  { return stretch( strString, nGap, ' ' ); }
  

  /**
   * Stretch a string with the fill character specified and the amount of gap space.
   * i.e. The string hello with a gap of 1 and a space fill character becomes h e l l o
   * 
   * @param strString The string to stretch
   * @param nGap The number of fill characters between each original staring character
   * @param chFill The fill character to use
   * 
   * @return A new String stretched with the fill characxter specified
   */
  public static String stretch( String strString, int nGap, char chFill )
  {
    if ( strString == null )
    {
      return null;
    }
    
    StringBuffer sb = new StringBuffer();
    int nLen = strString.length() - 1;
    
    for ( int x = 0; x < nLen; x++ )
    {
      sb.append( strString.charAt( x ) );
      
      for ( int y = 0; y < nGap; y++)
      {
        sb.append( chFill );
      }
    }
    
    sb.append( strString.charAt( nLen ) );
    return sb.toString();
    
  } // end stretch()
  
  /**
   * Trims any leading white space characters from the given string
   *
   * @param strOrig - The string to be left trimmed
   *
   * @return A new string with the leading white space characters removed
   */
  public static String ltrim( String strOrig )
  {
    if ( strOrig == null )
    {
      return strOrig;      // Nothing to do
    }

    int nLen = strOrig.length();
    int x = 0;

    for ( x = 0; x < nLen; x++ )
    {
      if ( isWhiteSpace( strOrig.charAt( x ) ) )
      {
        continue;
      }

      break;            // Found first non-whitespace character
    }

    return new String( strOrig.substring( x ) );

  } // end ltrim()


  /**
   * Trims any trailing white space characters from the given string
   *
   * @param strOrig - The string to be right trimmed
   *
   * @return A new string with the trailing white space characters removed
   */
  public static String rtrim( String strOrig )
  {
    if ( strOrig == null )
    {
      return strOrig;      // Nothing to do
    }

    int nLen = strOrig.length();
    int x = nLen - 1;

    for ( ; x >= 0; x-- )
    {
      if ( isWhiteSpace( strOrig.charAt( x ) ) )
      {
        continue;
      }

      break;            // Found first non-whitespace character
    }

    return new String( strOrig.substring( 0, x + 1 ) );

  } // end rtrim()


  /**
   * Left pads a string with a specified number of pad characters
   *
   * @param strOrig - The string to be left padded
   * @param chPad - The pad character to use
   * @param nLen - The total size of the result string. The difference will be left padded with
   * the chPad Character
   *
   * @return A new string, left padded as specified
   */
  public static String lpad( String strOrig, char chPad, int nLen )
  {
    if ( strOrig == null )
    {
      return strOrig;      // Nothing to do
    }

    if ( nLen <= 0 )
    {
      return strOrig;      // Needs a positive count
    }

    int nDiff = nLen - strOrig.length();

    if ( nDiff < 1 )
    {
      return strOrig;
    }

    char[] achPadArr = new char[ nDiff ];

    // *** Fill array with pad characters

    for ( int x = 0; x < nDiff; x++ )
    {
      achPadArr[ x ] = chPad;
    }

    StringBuffer sb = new StringBuffer();
    sb.append( achPadArr );
    sb.append( strOrig );

    return sb.toString();

  } // end lpad()


  /**
   * Right pads a string with a specified number of pad characters
   *
   * @param strOrig - The string to be right padded
   * @param chPad - The pad character to use
   * @param nLen - The total size of the result string. The difference will be right padded with
   * the chPad Character
   *
   * @return A new string, right padded as specified
   */
  public static String rpad( String strOrig, char chPad, int nLen )
  {
    if ( strOrig == null )
    {
      return strOrig;      // Nothing to do
    }

    if ( nLen <= 0 )
    {
      return strOrig;      // Needs a positive count
    }

    int nDiff = nLen - strOrig.length();

    if ( nDiff < 1 )
    {
      return strOrig;      // Nothing to do
    }

    char[] achPadArr = new char[ nDiff ];

    // *** Fill array with pad characters

    for ( int x = 0; x < nDiff; x++ )
    {
      achPadArr[ x ] = chPad;
    }

    StringBuffer sb = new StringBuffer();
    sb.append( strOrig );
    sb.append( achPadArr );

    return sb.toString();

  } // end rpad()


  /**
   * Determines if a character is in the range 0-9
   *
   * @param ch - The character to test
   *
   * @return True if the character is a digit; otherwise False
  */
  public static final boolean isdigit( char ch )
  { return ( ch >= '0' && ch <= '9' ); }


  /**
   * Determines if a character is contained within in a given string
   *
   * @param ch - The character to search for
   * @param strTest - The string containing the characters to test
   *
   * @return True if the character is a contained in the test string; False if
   * the test string does not contain the search character.
   */
  public static final boolean isin( char ch, String strTest )
  { return ( strTest.indexOf( ch ) >= 0 ) ? true : false ; }


  /**
   * Determines if a string is an element of a string array
   *
   * @param strTest - The string to search for
   * @param astrArray - The string array to be searched
   * @param fIgnoreCase - If True, the search ignores case; if False, the search is
   * case sensitive.
   *
   * @return True if the search string is an element of the array; False if it is not.
   *
   */
  public static final boolean isin( String strTest, String[] astrArray, boolean fIgnoreCase )
  {
    // *** Protect against null objects

    if ( strTest == null || astrArray == null )
    {
      return false;
    }


    for ( int x = 0; x < astrArray.length; x++ )
    {
      if ( fIgnoreCase )
      {
        if ( strTest.equalsIgnoreCase( astrArray[ x ] ) )
        {
          return true;
        }
      }
      else
      if ( strTest.equals( astrArray[ x ] ) )
      {
        return true;
      }

    } // end for()

    return false;     // Not found

  } // end isin

  /**
   * Removes from a given string any of the characters specified in a strip character string
   *
   * @param strOrig - The string to be searched for strip characters
   * @param strStripChars - The string of characters to be stripped from strOrig
   *
   * @return A new string in which all characters of the original string that match
   * any strip character have been removed.
   */
  public static final String strip( String strOrig, String strStripChars )
  {
    int nOrigNdx;

    // Create string buufer of orig string size to put the no stripped characters in

    StringBuffer sb = new StringBuffer( strOrig.length() );

    for ( nOrigNdx = 0; nOrigNdx < strOrig.length(); nOrigNdx++ )
    {

      char ch = strOrig.charAt( nOrigNdx );

      // *** If not in strip list then add it to the string buffer.

      if ( strStripChars.indexOf( ch ) < 0 )
      {
        sb.append( ch );
      }

    } // end for()

    return sb.toString();

  } // end strip()



  /**
   * Removes all white space from a given string
   *
   * @param strOrig - The string to be searched for strip characters
   *
   * @return A new string striped of whitespace
   */
  public static final String stripWhitespace( String strOrig )
  {
    return strip( strOrig, "\r\n\t " );
  } // end strip()



  /**
   * Removes from a given string all characters EXCEPT a leading + or - sign, a decimal point,
   * and digits.  A special version of strip() for numeric data.
   *
   * @param strOrig - The string with numeric data to be searched for strip characters
   *
   * @return A new string with only valid numeric data that can be converted to a
   * binary equivalent.
  */
  public static final String numericStrip( String strOrig )
  {
    int nOrigNdx;

    // Create string buufer of ori string size to put the no stripped characters in

    StringBuffer sb = new StringBuffer( strOrig.length() );

    for ( nOrigNdx = 0; nOrigNdx < strOrig.length(); nOrigNdx++ )
    {

      char ch = strOrig.charAt( nOrigNdx );

      // *** If not in strip list then add it to the string buffer.

      if ( Character.isDigit( ch ) || ch == '+' || ch == '-' || ch == 'E' || ch == 'e'
           || ch == '.' )
      {
        sb.append( ch );
      }

    } // end for()

    return sb.toString();

  } // end numericStrip()

  /**
   * Counts occurrences of a character from the beginning of a string for the length of the string
   *
   * @param strString - The string with the characters to be counted
   * @param ch - The character to count
   *
   * @return The nbr of times the character is found in the string
  */
  public static int count( String strString, char ch)
  { return count( strString, ch, 0, -1 ); }


  /**
   * Counts occurrences of a character
   *
   * @param strString - The string with the characters to be counted
   * @param ch - The character to count
   * @param nStartPos - The position in the string to start counting
   * @param nEndPos - The position in the string to stop counting or -1 for the length of the string
   *
   * @return The nbr of times the character is found in the string
  */
  public static int count( String strString, char ch, int nStartPos, int nEndPos )
  {

    if ( strString == null )                 // Sanity check
    {
      return 0;
    }

    int nCount = 0;

    if ( nEndPos == -1 )
    {
      nEndPos = strString.length();
    }

    if ( nStartPos > nEndPos )
    {
      return 0;
    }

    for ( int x  = nStartPos; x < nEndPos; x++ )
    {

      if ( strString.charAt( x ) == ch )
      {
        ++nCount;                           // Found a match
      }

    } // end for()

    return nCount;                          // Final result

  } // end count()


  /**
   * Counts the number of occurrences of a substring in a given string
   *
   * @param strTest - The string to be searched
   * @param strSub - The substring to be searched for
   *
   * @return The number of occurrences of substring in the given string
  */
  public static int count( String strTest, String strSub )
  { return count( strTest, strSub, 0, -1 ); }

  /**
   * Counts the number of occurrences of a substring in a given string
   *
   * @param strTest - The string to be searched
   * @param strSub - The substring to be searched for
   * @param nStartPos - The position in the string to start searching
   * @param nEndPos - The position in the string to stop searching or -1 for the length of the string
   *
   * @return The nbr of times the characters is found in the string
  */
  public static int count( String strTest, String strSub, int nStartPos, int nEndPos )
  {
    // If any strings are null, the answer is zero

    if ( strTest == null || strSub == null )
    {
      return 0;
    }

    if ( nEndPos < 0 )
    {
      nEndPos = strTest.length();
    }

    int nStrLen = nEndPos - nStartPos;

    if ( nStrLen <  0 )
    {
      return 0;
    }

    if ( nStartPos < 0 || nStartPos >= nStrLen )
    {
      return 0;
    }

    int nSubLen = strSub.length();

    if ( nSubLen == 0 )
    {
      return 0;
    }

    int nCount = 0;

    // *** Find first occurrence

    int nPos = strTest.indexOf( strSub, nStartPos );

    while( nPos >= 0 && nPos < nStrLen )
    {
      ++nCount;
      nPos += nSubLen;
      nPos = strTest.indexOf( strSub, nPos );

    } // end while()

    return nCount;

  } // end count()


  /**
   * Convert milliseconds to a string in hours,minutes and seconds
   * @param fLongForm if true the string returned is in the form n hour(s), n minute(s) and n second(s)<br>
   *                  else it's in the form HH:MM:SS
   * @param lMilliSecs
   * @return
   */
  public static String milliSecsToTime( boolean fLongForm, long lMilliSecs )
  {

    // Convert to hours minutes and seconds
    long lSecs = lMilliSecs / 1000;
    lMilliSecs = lMilliSecs % 1000;

    long lMinutes = lSecs / 60;
    lSecs = lSecs % 60;
    long lHours = lMinutes / 60;
    lMinutes = lMinutes % 60;

    if ( fLongForm )
    {
      StringBuffer sb = new StringBuffer();
      String strTime = doMinutes( lMinutes, lSecs, lMilliSecs );

      if ( lHours > 0 )
      {

       sb.append( lHours ).append( " hour" );
       if ( lHours > 1 )
       {
         sb.append( "s" );
       }

       if ( strTime != null )
       {
         if ( strTime.indexOf( "and" ) < 0 )
         {
           sb.append( " and " );
         }
         else
         {
           sb.append( ", " );
         }

         sb.append( strTime );
       }

       return sb.toString();
      }

      if ( strTime != null )
      {
        return strTime;
      }

      return "";
    }
    else
    {
      StringBuffer sb = new StringBuffer();
      String strHours = String.valueOf( lHours );
      String strMin = String.valueOf( lMinutes );
      String strSecs = String.valueOf( lSecs );

      if ( strHours.length() < 2 )
      {
        strHours = "0" + strHours;
      }

      if ( strMin.length() < 2 )
      {
        strMin = "0" + strMin;
      }

      if ( strSecs.length() < 2 )
      {
        strSecs = "0" + strSecs;
      }

      sb.append( strHours ).append( ":" ).append( strMin ).append( ":" ).append( strSecs );

      if ( lMilliSecs > 0 )
      {
        sb.append( "." ).append( lMilliSecs );
      }

      return sb.toString();
    }


  }


  /**
   * Handle seconds part
   * @param lSeconds
   * @param lMilliSecs
   * @return
   */
  private static String doMinutes( long lMinutes, long lSeconds, long lMilliSecs )
  {
    String strSeconds = doSeconds( lSeconds, lMilliSecs );


    if ( lMinutes > 0  )
    {
      StringBuffer sb = new StringBuffer( );
      sb.append( lMinutes ).append( " minute" );

      if ( lMinutes > 1 )
      {
        sb.append( "s" );
      }
      if ( strSeconds != null )
      {
        if ( lSeconds > 0 && lMilliSecs > 0 )
          sb.append( ", " );
        else
          sb.append( " and " );

        sb.append( strSeconds );
      }

      return sb.toString();

    }

    return strSeconds;

  }

  /**
   * Handle seconds part
   * @param lSeconds
   * @param lMilliSecs
   * @return
   */
  private static String doSeconds( long lSeconds, long lMilliSecs )
  {
    String strMilliSecs= doMillisecs( lMilliSecs );

    if ( lSeconds > 0  )
    {
      StringBuffer sb = new StringBuffer( );
      sb.append( lSeconds ).append( " second" );

      if ( lSeconds > 1 )
      {
        sb.append( "s" );
      }

      if ( strMilliSecs != null )
      {
        sb.append( " and " ).append( strMilliSecs );
      }

      return sb.toString();

    }

    return strMilliSecs;

  }

  /**
   * Add miiliseconds part in non zero
   * @param lMilliSecs  millisecond count
   * @return
   */
  private static String doMillisecs( long lMilliSecs )
  {
    if ( lMilliSecs > 0 )
    {
      StringBuffer sb = new StringBuffer( );
      sb.append( lMilliSecs ).append( " millisecond" );

      if ( lMilliSecs > 1 )
      {
        sb.append( "s" );
      }

      return sb.toString();
    }

    return null;

  }


  /**
   * Expands a string containing the characters ${propertyKey} using properties loaded with the VwResourceMgr or
   * the system properties. Bundle values are searched first then the System properties.
   * The value inside the {} characters represents a property key of the value to be substituted
   * 
   * @param strValue a string containing one or more ${propertyKey} macros to be expanded
   * 
   * @return The macro value if macro name is defined else the original value is returned.
   */
  public static String expandMacro( String strValue )
  { return expandMacro( strValue, null ); }
  
  
  /**
   * Expands a string containing the characters ${propertyKey} using properties loaded with the VwResourceMgr.
   * The value inside the {} characters represents a property key of the value to be substituted
   * 
   * @param strValue a string containing one or more ${propertyKey} macros to be expanded
   * @param objParams an additional map of values to do a macro name lookup. If specified,
   * this map is search first the the resource bundles and finally the System properties
   * @return
   */
  public static String expandMacro( String strValue, Object objParams  )
  {
    if ( strValue == null )
    {
      return null;
    }
    
    int nPos = strValue.indexOf( "${" );
    if ( nPos < 0 )
    {
      return strValue;
    }
    
    StringBuffer sbValue = new StringBuffer();


    while ( nPos >= 0  )
    {

      Object strMacroValue = null;

      // append any characters up to the start of the macro the macro
      if ( nPos > 0 )
      {
        sbValue.append( strValue.substring( 0, nPos ) );
      }
      int nEndPos = strValue.indexOf( "}", nPos );
      
      if ( nEndPos < 0 )
      {
        return strValue;    // mising end delimiter just return orig value
      }

      String strMacro = strValue.substring( nPos, nEndPos + 1  );
      nPos += 2;

      String strMacroName = strValue.substring( nPos, nEndPos );

      if ( strMacroName.charAt( 0 ) == '@' )   // this is an object instance name with a property name
      {

        if ( objParams instanceof Map )
        {
          strMacroValue = processObjectProperty( strMacroName.substring( 1 ),(Map)objParams );
        }
        else
        {
          strMacroValue = processObjectProperty( strMacroName.substring( 1 ),objParams );

        }

        if ( strMacroValue == null )     // Map not defined so return orig value
        {
          strMacroValue = strMacro;
        }
      }
      else
      {
        if ( objParams instanceof Map )
        {
          strMacroValue = resolvMacroValue( strMacroName, (Map)objParams );
        }
        else
        {
          strMacroValue = resolvMacroValue( strMacroName, objParams );

        }

        if ( strMacroValue == null )
        {
          strMacroValue = strMacro;
        }
        else
        if ( strMacroValue.equals( strMacroName ) )
        {
          strMacroValue = strMacro;
        }

      }
      
      sbValue.append( strMacroValue );
      strValue = strValue.substring( ++nEndPos );
      
      nPos = strValue.indexOf( "${" );
    } // end while
    
    if ( strValue.length() > 0 )
    {
      sbValue.append( strValue ); // append any traing characters foloowing macro
    }

    if ( sbValue.length() == 0 )
    {
      return strValue; // No macro definition, just return original name string
    }

    return sbValue.toString();
    
  } // end expandMacro()


  /**
   * Resolve macro value from a map
   * @param strMacroName
   * @param mapAdditionalValues Map of replacement values
   * @return
   */
  private static Object resolvMacroValue( String strMacroName, Map mapAdditionalValues )
  {

    Object strMacroValue = null;

    if ( mapAdditionalValues != null )
    {
      strMacroValue = mapAdditionalValues.get( strMacroName );
    }

    if ( strMacroValue == null )
    {
      strMacroValue = VwResourceMgr.getString( strMacroName );

      // Not in a resource bundle, see if its a system property
      if ( strMacroValue.equals( strMacroName ))
      {
        strMacroValue = checkSystemProperties(  strMacroName );
      }

    }

    return strMacroValue;

  }


  /**
   * Resolve macroo value from a bean property
   * @param strMacroName
   * @param objBean
   * @return
   */
  private static Object resolvMacroValue( String strMacroName, Object objBean )
  {
    Object strMacroValue = null;


    if ( objBean == null )
    {
      // Check to see if its a system property
      strMacroValue =  checkSystemProperties( strMacroName );

      if ( strMacroValue.equals( strMacroName ) )
      {
        return VwResourceMgr.getString( (String)strMacroValue );

      }

      return strMacroValue;

    }

    if ( VwBeanUtils.isSimpleType( objBean ))
    {
      return objBean.toString();

    }

    try
    {
      strMacroValue = VwBeanUtils.getValue( objBean, strMacroName );

    }
    catch( Exception ex )
    {
      return checkSystemProperties( strMacroName );
    }


    return strMacroValue;

  }

  /**
   * Check to see if macro name is a system property
   *
   * @param strMacroName The macro name
   *
   * @return The system property if found else returns the macro name
   */
  private static String checkSystemProperties( String strMacroName )
  {
    Properties propsSys = System.getProperties();

    String strMacroValue = propsSys.getProperty( strMacroName );

    if ( strMacroValue == null )
    {
      return strMacroName;
    }

    return strMacroValue;

  }


  private static String processObjectProperty( String strMacroName, Object objReplaceParams )
  {
    int nPos = strMacroName.indexOf( '.' ); // extract instance name from property name

    if ( nPos < 0 )
    {
      return null;
    }

    Object objInstance = resolvMacroValue( strMacroName.substring( 0, nPos ), objReplaceParams ); // skip the '@' char

    if ( objInstance == null )  // not define in map
    {
      return null;
    }

    try
    {
      Object objVal =  VwBeanUtils.getValue( objInstance, strMacroName.substring( ++nPos ) );

      if ( objVal == null )
      {
        return null;

      }

      return objVal.toString();

    }
    catch( Exception ex )
    {
      return null;

    }
  }


  /**
   * Determines if a given character is a white-space character
   *
   * @param ch - The character to test
   *
   * @return True if the character is a white-space character; otherwise, False is returned
   *
   */
  public static final boolean isWhiteSpace( char ch )
  {
    if ( ch == ' ' || ch == '\t' || ch == '\r' || ch == '\n' )
    {
      return true;
    }

    return false;

  } // end isWhiteSpace()


  /**
   * Determines if a given string is all whitespace characters
   *
   * @param strTest String we will test whitespace characters fom
   *
   * @return True if the string contains all whitespace characters; otherwise False is returned
   *
   */
  public static final boolean isWhiteSpace( String strTest )
  {
    if ( strTest == null )
    {
      return true;                                    // If NULL was passed just return an empty string.
    }

    int ndx = -1;
    int len = strTest.length();

    while( ++ndx < len )
    {
      char ch = strTest.charAt( ndx );

      if ( ch == ' ' || ch == '\t' || ch == '\r' || ch == '\n' )
      {
        continue;
      }

      break;
    }

    if ( ndx < len )
    {
      return false;
    }

    return true;                                   // Found non-whitespace character

  } // end isWhiteSpace()


  /**
   * Advances the index past the next whitespace in the search string
   *
   * @param strTest The string to be searched
   * @param nStartPos The position in the string to start the search
   * @param nInc The character increment value: 1 to search forward from the starting position;
   * -1 to search backwards from the current position.
   *
   * @return The index of the next non white-space character, or the original start position
   * if no more white space characters are found.
   */
  public static final int eatWhiteSpace( String strTest, int nStartPos, int nInc )
  {
    int nLen = strTest.length();
    int nSavePos = nStartPos;

    // *** Depending on the direction we have to be > 0 or < the string length

    while( nStartPos >= 0 && nStartPos < nLen )
    {
      char ch = strTest.charAt( nStartPos );
      if ( ch == ' ' || ch == '\t' || ch == '\r' || ch == '\n' )
      {
        nStartPos += nInc;
        continue;
      }

      return nStartPos;
    }

    return nSavePos;          // end of string

  } // end eatWhiteSpace()


  /**
   * Gets a token (word) starting at the current position of a string until either the first white space or delimiter char is found
   * or the beginning or end of string is found (depending on the direction)
   *
   * @param strSrc The string source to get the token
   * @param sb  StringBuffer that the token will be placed in
   * @param nStartPos The starting position in the string
   * @param nInc The increment value use 1 to move forwards, -1 to move backwards
   * @param strDelimiters A string of additional delimiters to terminate the scan
   *
   * @return The position of the token found
   */
  public static final int getToken( String strSrc, StringBuffer sb, int nStartPos, int nInc, String strDelimiters )
  {
    if ( strDelimiters == null )
    {
      strDelimiters = "";
    }

    sb.setLength( 0 );
    int nLen = strSrc.length();

    nStartPos = VwExString.eatWhiteSpace( strSrc, nStartPos, nInc );

    int nCursor = nStartPos;

    for ( ; nCursor < nLen && nCursor >= 0; nCursor+= nInc )
    {
      char ch = strSrc.charAt( nCursor );

      if ( VwExString.isWhiteSpace( ch  ) ||  VwExString.isin( ch, strDelimiters ))
      {
        break;
      }

      if ( nInc > 0 )
      {
        sb.append( ch );
      }
      else
      {
        sb.insert( 0, ch );
      }

    }


    if ( nInc > 0 )  // If moving forward the cursor has to be put back to the starting the token
    {
      nCursor -= sb.length();

    }
    else            // Moving backwards so bump cursor up by one to point to the first of cursor
    {
      ++nCursor;
    }

    return nCursor;

  }


  /**
   * Gets a token (word) starting at the current position of a string until either the first white space or delimiter char is found
   * or the beginning or end of string is found (depending on the direction)
   *
   * @param strSrc The string source to get the token
   * @param nStartPos The starting position in the string
   * @param nInc The increment value use 1 to move forwards, -1 to move backwards
   * @param strDelimiters A string of additional delimiters to terminate the scan
   *
   * @return The position of the token found
   */
  public static final String getToken( String strSrc, int nStartPos, int nInc, String strDelimiters )
  {
    if ( strDelimiters == null )
    {
      strDelimiters = "";
    }

    StringBuffer sb = new StringBuffer( 15 );

    int nLen = strSrc.length();

    nStartPos = VwExString.eatWhiteSpace( strSrc, nStartPos, nInc );

    int nCursor = nStartPos;

    for ( ; nCursor < nLen && nCursor >= 0; nCursor+= nInc )
    {
      char ch = strSrc.charAt( nCursor );

      if ( VwExString.isWhiteSpace( ch  ) ||  VwExString.isin( ch, strDelimiters ))
      {
        break;
      }

      if ( nInc > 0 )
      {
        sb.append( ch );
      }
      else
      {
        sb.insert( 0, ch );
      }

    }

    return sb.toString();

  }

  /**
   * Finds a token (word) starting at the current position of a string until either the first white space or delimiter char is found
   * or the beginning or end of string is found (depending on the direction)
   *
   * @param strSrc The string source to get the token
   * @param strSearchToken The token to stop the search at or null to just get the position of the next token
   * @param nStartPos The starting position in the string
   * @param nInc The increment value use 1 to move forwards, -1 to move backwards
   * @param strDelimiters A string of additional delimiters to terminate the scan
   * @return
   */
  public static final int findToken( String strSrc, String strSearchToken, int nStartPos, int nInc, String strDelimiters )
  {
    if ( strDelimiters == null )
    {
      strDelimiters = "";
    }

    boolean fStartsWith = false;

    if ( strSearchToken.startsWith( "@@" ))
    {
      strSearchToken = strSearchToken.substring( 2 );

      fStartsWith = true;

    }

    StringBuffer sb = new StringBuffer( 15 );

    String strToken = null;

    int nLen = strSrc.length();

    nStartPos = VwExString.eatWhiteSpace( strSrc, nStartPos, nInc );

    int nCursor = nStartPos;

    while ( nCursor < nLen && nCursor >= 0 )
    {
      for ( ; nCursor < nLen && nCursor >= 0; nCursor+= nInc )
      {
        char ch = strSrc.charAt( nCursor );

        if ( VwExString.isWhiteSpace( ch  ) ||  VwExString.isin( ch, strDelimiters ))
        {
          break;
        }

        if ( nInc > 0 )
        {
          sb.append( ch );
        }
        else
        {
          sb.insert( 0, ch );
        }

      } // end for()


      strToken = sb.toString();

      if ( strSearchToken == null )
      {
        break;
      }

      if ( fStartsWith )
      {
         if (  strToken.startsWith( strSearchToken ) )
         {
           break;
         }
      }
      else
      if ( strToken.equals( strSearchToken ) )
      {
        break;
      }

      sb.setLength( 0 ); // clear out buffer
      nCursor += nInc;   // advance cursor past token just found

      strToken = null;

    } // end while


    if ( strToken == null )
    {
      return -1; // not found
    }

    if ( nInc > 0 )  // If moving forward the cursor has to be put back to the starting the token
    {
      nCursor -= strToken.length();

    }
    else            // Moving backwards so bump cursor up by one to point to the first of cursor
    {
      ++nCursor;
    }

    return nCursor;

  }

  /**
   * Finds the next whitespace character in the given string
   *
   * @param strTest - The string to be searched
   * @param nStartPos - The position in the string to start the search
   * @param nInc - The character increment value: 1 to search forward from the starting position;
   * -1 to search backwards from the current position.
   *
   * @return The index of the next whitespace character, or -1 if no more whitespace
   * characters are found.
   */
  public static final int findWhiteSpace( String strTest, int nStartPos, int nInc )
  {
    int nLen = strTest.length();

    // *** Depending on the direction we have to be > 0 or < the string length

    while( nStartPos >= 0 && nStartPos < nLen )
    {
      char ch = strTest.charAt( nStartPos );
      if ( ch == ' ' || ch == '\t' || ch == '\r' || ch == '\n' )
      {
        return nStartPos;
      }

      nStartPos += nInc;
      continue;
    }

    return -1;          // end of string

  } // end findWhiteSpace()


  /**
   * Looks for the first occurrence of any character in a given character list, starting
   * after the specified position.
   *
   * @param strTest - The string to be searched
   * @param strFindList - A string containing the characters to search for
   * @param nStartPos - The position in the string to start the search
   *
   * @return The position within the string if any of the listed characters if found;
   * otherwise, -1 is returned.
   */
  public static final int findAny( String strTest, String strFindList, int nStartPos )
  {
    if ( strTest == null )
    {
      return -1;
    }

    if (strFindList == null)
    {
      return -1;
    }

    if ( nStartPos >= strTest.length() )
    {
      return -1;
    }

    int nFindLen = strFindList.length();

    int nSrcLen = strTest.length();

    for ( int x = nStartPos; x < nSrcLen; x++ )
    {
      char chSrc = strTest.charAt( x );

      for ( int y = 0; y < nFindLen; y++ )
      {
        if ( chSrc == strFindList.charAt( y ) )
        {
          return x;
        }

      } // end inner for

    } // end outer for()

    return -1;                                    // match not found

  } // end findAny()


  /**
   * Gets the next word or token from a string given a starting position and a direction.
   * The word or token that is returned in the string buffer is determined by the first
   * sequence of characters found after a white-space character or any delimiters in the
   * delimiter string have been found.
   *
   * @param strSearch - The string to be searched
   * @param nStartPos - The position in the string to start the search
   * @param sbResult - The StringBuffer the word or token is returned in
   * @param nDirInd - The search direction: if a positive value, forwards; if a negative
   * value, backwards.
   * @param strDelim - An optional string of delimiters used determine the end of search
   *
   * @return The index in the string where the search was terminated, i.e., when the first
   * white-space character or delimiter (if applicable) is found.  The word/token is returned
   * in the StringBuffer parameter in this case.  A -1 is returned when the end of the string
   * is encountered first.
   */
   public static int getWord( String strSearch, int nStartPos,
                              StringBuffer sbResult, int nDirInd, String strDelim )
   {
     int nInc = (nDirInd < 0)? -1 : 1;

     int nLen = strSearch.length(); // Length of search string

     sbResult.setLength( 0 );       // Clear StringBuffer

     if ( nStartPos < 0 || nStartPos >= nLen )
     {
       return -1;                     // return end of string
     }

     // *** Begin the search

     // *** first suck up any characters that are white space or delimiters to get us to the
     // *** start of the next word

     int x = nStartPos;
     char ch = ' ';

     boolean fIsDelim = false;

     for ( ; (x >=0 && x < nLen); x += nInc )
     {
       ch = strSearch.charAt( x );

       // Found a delimiter return the delimiter

       if ( strDelim != null && isin( ch, strDelim ) )
       {
         fIsDelim = true;
         break;
       }

       if ( isin( ch, " \t\r\n" ) )
       {
         continue;
       }

       break;                    // At the start of the next word

     } // end for

     int nFinish = x;            // Save index of bail out point

     if ( x < 0 || x >= nLen )
     {

       if ( x < 0 )              // rest x for proper substring retrieval
       {
         x = 0;
       }

       if ( x >= nLen )
       {
         x = nLen;
       }

       nFinish = -1;

     } // end if

     // Check to see if bailout was caused by a delimiter
     if ( fIsDelim )
     {
       fIsDelim = false;

       if ( nInc < 0 )                // Moving backwards?
       {
         // **** Moving backwards and the last character in the string is a delimiter
         if ( x == nStartPos )
         {
           sbResult.append( ch );     // Return the delimiter
           return x - 1;
         }

         else                         // moving backawrds, but check to see if the char past
                                      // Delimiter is white space and if it is return the delimter
         {
           if ( isWhiteSpace( strSearch.charAt( x  + 1 ) ) )
           {
             sbResult.append( ch );   // Return the delimiter
             return x - 1;
           }

         } // end else

       } // end if ( nInc < 0 )
       else
       {
         // Moving forward
         if ( x == nStartPos )
         {
            sbResult.append( ch );

            if ( (x + 1 ) >= nLen )
            {
              return -1;
            }
            else
            {
              return x + 1;
            }
         }
         else
         {
           if ( isWhiteSpace( strSearch.charAt( x - 1 ) ) )
           {
             sbResult.append( ch );     // Return the delimiter
             return x + 1;
           }
         } // end else

       } // end else

     } //end if ( isDelim else

     if ( nFinish < 0 )
     {
       return -1;
     }

     if ( nInc < 0 )
     {
       nStartPos = x + 1;       // Mark the start of the next word
     }
     else
     {
       nStartPos = x;
     }

     x += nInc;

     // *** This search terminates at the next white space or delimiter character

     for ( ; (x >=0 && x < nLen); x += nInc )
     {
       ch = strSearch.charAt( x );

       if ( strDelim != null && isin( ch, strDelim ) )
         break;

       if ( isin( ch, " \t\r\n" ) )
         break;

     } // end for

     nFinish = x;   // Save x final position

     if ( x >= nLen )
     {
       nFinish = -1;
     }

     if ( nInc < 0 )
     {
       sbResult.append( strSearch.substring( x + 1, nStartPos ) );
     }
     else
     {
       sbResult.append( strSearch.substring( nStartPos, x ) );
     }

     return nFinish;    // Return the index that caused our stopping point

   } // end of getWord()


  /**
   * Extract comments out from a string for the following comment patterns
   * <br>-- standard oracle comment single line
   * <br>// standard language comment single line
   * <br>/* standrad block comment
   *
   * @param strText The text string containing comments
   *
   * @return a string with the comments stripped out
   *
   */
  public  static String extractComments( String strText )
  {
    StringBuffer sb = new StringBuffer( strText.length() );

    int nTextLen = strText.length();

    for ( int x = 0; x < nTextLen; x++ )
    {
      char ch = strText.charAt( x );

      if ( ch == '-' || ch == '/' )
      {

        if ( (x + 1) < nTextLen  )
        {
          char chNext = strText.charAt( x + 1 );

          if ( chNext ==  '-' || chNext == '/' || chNext == '*' )
          {
            x = findEndOfComment( strText, chNext, x + 2 );
            continue;
          }

        }
      }

      sb.append( ch );    // Not a comment char, put in statement buffer

    } // end for()

    return sb.toString();

  } // end extractComments()

  /**
   * Finds the position in the string where the comment ends
   *
   * @param strText The text to search
   * @param chComment The initial comment character
   * @param nStartPos The position within the text to start searching
   * @return The position of the end comment character
   */
  private static int findEndOfComment( String strText, char chComment, int nStartPos )
  {
    int nTextLen = strText.length();
    char chEndComment = '*';

    if ( chComment == '-' || chComment == '/' )
    {
      chEndComment = '\n';
    }

    int x = nStartPos;
    for ( ; x < nTextLen; x++ )
    {
      char ch = strText.charAt( x );

      if ( ch == chEndComment )
      {
        if ( ch == '\n' )
        {
          return x;         // All done as is is a line comment
        }

        // See if the is a */ for block comment end
        if ( (x + 1) < nTextLen && strText.charAt( ( x + 1 ) ) == '/')
        {
          return x + 1;     // Blcok comment end
        }

      } // end if

    } // end for()

    return x;

  } // end findEndOfComment()

  
  /**
   * Expands common xml/html character entity definitions 
   * The current entities recognized for expansion are: &quote, &amp, &apos, &lt, &gt, &nbsp
   * 
   * @param strData The string to test &entities for
   * @return The string with the entity values replaced or the original string if no entities were found
   * @throws Exception
   */
  public static String expandCharacterEntities( String strData ) throws Exception
  {
    int nPos = getEntityPos( strData, 0 );
    int nStringPos = 0;
    
    StringBuffer sb = new StringBuffer();
    
    while( nPos >= 0 )
    {
      // Get string piece up to & character
      sb.append( strData.substring(nStringPos, nPos ) );
      // Extract the & entity
      int nAmpEndPos = strData.indexOf( ';', nPos );
      
      if ( nAmpEndPos < 0 )
      {
        return strData;    // if no terminating semicolon assume its not an entity so just return original data
      }
      
      String strEntity = strData.substring( nPos, nAmpEndPos + 1 );
      String strEntityValue = s_mapCharEntities.get( strEntity );
      if ( strEntityValue != null )
      {
        sb.append( strEntityValue );
      }
      
      nStringPos = nAmpEndPos + 1;
      
      // look for next entity
      nPos = getEntityPos( strData, nStringPos );
    }
    
    if ( nStringPos < strData.length() )
    {
      sb.append( strData.substring( nStringPos ) );
    }
    
    return sb.toString();
  }

  /**
   * Splits a string into an array of strings based on the delimiter. NOTE unlike<br>
   * the Java String version of split the delimiter is looked at in its entire value not<br>
   * an array of characters. in addition, all empty strings resulting in consecutive delimiter characters removed.<br>
   * ex. String "one<br>two" with the delimiter of "<br>" yields a String[]{"one","two"}.<br>
   * ex. String "one            two" with a delimiter of " " also yields a String[]{"one","two"}

   * @param strOrig The original string to split
   * @param strDelimiter  The delimiter to split on
   * @return an array of string split by the delimiter, if not delimiter is found, then the original string is returned
   */
  public static String[] split( String strOrig, String strDelimiter )
  {
    List<String>list = new ArrayList<String>();

    int nEndPos = 0;

    while( true )
    {
      int nPos =  strOrig.indexOf( strDelimiter, nEndPos );
      if ( nPos < 0 )
      {
        break;
      }

      String strAdd = strOrig.substring( nEndPos, nPos );

      nEndPos = nPos + strDelimiter.length();

      // Don't add empty strings, this happens when there are consecutive delimiters
      if ( strAdd.length() == 0 )
      {
        continue;
      }

      list.add( strAdd  );


    }

    list.add( strOrig.substring( nEndPos ) );

    String[] astrPieces = new String[ list.size() ];
    list.toArray( astrPieces );

    return astrPieces;

  }


  /**
   * Static initializer for character entity map
   */
  private static void loadEntitiyMap()
  {
    s_mapCharEntities = new HashMap<String, String>();
    s_mapCharEntities.put( "&quote;", "\"" );
    s_mapCharEntities.put( "&amp;", "&" );
    s_mapCharEntities.put( "&apos;", "'" );
    s_mapCharEntities.put( "&lt;", "<" );
    s_mapCharEntities.put( "&gt;", ">" );
    s_mapCharEntities.put( "&nbsp;", " " );
    
  }

  private static int getEntityPos( String strData, int nCurPos )
  {
    int nPos = strData.indexOf( '&', nCurPos );
    
    if ( nPos < 0 )
    {
      return -1;
    }
    
     
    if ( nPos +1  < strData.length() )
    {
      char ch = strData.charAt( nPos + 1 );
      if ( VwExString.isWhiteSpace( ch ) )
      {
        return -1;    // Not an entity
      }
      
      return nPos;   // looks like an entity
    }
    else
    {
      return -1;    // The ampersand was the last char in the string not an entity
    }
  }

  public static String getNicAddress( String strServerNicCardName ) throws Exception
  {
    Enumeration e = NetworkInterface.getNetworkInterfaces();


    if ( strServerNicCardName == null)
    {
      throw new Exception ("server.nicCardName must be defined in the Properties file" );

    }

    while( e.hasMoreElements() )
    {
      NetworkInterface n = (NetworkInterface) e.nextElement();
      Enumeration ee = n.getInetAddresses();

      if ( n.getName().equals( strServerNicCardName ))
      {
        while ( ee.hasMoreElements() )
        {
          InetAddress i = (InetAddress) ee.nextElement();
          String strHostAddress = i.getHostAddress();

          // We're looking for the IpV4 address
          if ( strHostAddress.indexOf( "." ) > 0 )
          {
            return strHostAddress;
          }
        }
      }
    }

    return null;

  }
} // end class VwExString{}

// *** End VwExString.java ***

