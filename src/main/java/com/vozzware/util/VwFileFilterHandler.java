/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwFileFilterHandler.java

============================================================================================
*/

package com.vozzware.util;

import java.io.File;

/**
 * This class implements the the FilenameFilter interface to provide a filter for wildcard directory
 * listing requests
 */
 public class VwFileFilterHandler
 {
   private String[][] m_astrFilter;     // The file filter string index 0 = base, index 1 = ext

   private boolean    m_fIncludeDirs = false;   // If true include directory entries
   private boolean    m_fWildcards;     // if true a wildcard search routine is done
   private boolean    m_fIgnoreCase;    // Case sensitivity flag - true by default

   private String[]   m_astrFileTypes;  // Nbr of different filter requests

   /**
    * Constructs this class from a filter spec. I.E myfile.txt, *.txt, my*.*
    * This constrctor is used when case sensitivity is ignored

    * @param strFiler The file filter string which will test the file names passed
    * from the File.list() method. The wilecard charcaters '*' and '%' may be used.
    */
   public VwFileFilterHandler( String strFilter )
   {  init( strFilter, true ); }


   /**
    * Constructs this class from a filter spec. I.E myfile.txt, *.txt, my*.*
    * This constrctor is used when case sensitivity is normally preserved

    * @param strFiler The file filter string which will test the file names passed
    * @param fIgnoreCase if the false passed , case sensitivity is preserved
    * from the File.list() method. The wilecard charcaters '*' and '%' may be used.
    */
   public VwFileFilterHandler( String strFilter, boolean fIgnoreCase )
   {  init( strFilter, fIgnoreCase ); }


   /**
    * Constructs this class from a filter spec. I.E myfile.txt, *.txt, my*.*
    * This constrctor is used when case sensitivity is normally preserved

    * @param strFiler The file filter string which will test the file names passed
    * @param fIgnoreCase if the false passed , case sensitivity is preserved
    * from the File.list() method. The wilecard charcaters '*' and '%' may be used.
    * @param fIncludeDir if true, return director names in the search
    */
   public VwFileFilterHandler( String strFilter, boolean fIgnoreCase, boolean fIncludeDirs )
   {  m_fIncludeDirs = fIncludeDirs; init( strFilter, fIgnoreCase ); }

   /**
    * Common init for the different constructors
    *
    * @param strFiler The file filter string which will test the file names passed
    * @param fIgnoreCase If False, case sensitivity is preserved
    */
   private void init( String strFilter, boolean fIgnoreCase )
   {

     m_fWildcards = false;

     if ( strFilter.equals( "*" ) )
     {
       strFilter = "*.*";
     }

     VwDelimString dlmsFilter = new VwDelimString( ",", strFilter );

     m_astrFileTypes = dlmsFilter.toStringArray();

     m_fIgnoreCase = fIgnoreCase;

     m_astrFilter = new String[m_astrFileTypes.length][ 2 ];

     for ( int x = 0; x < m_astrFileTypes.length; x++ )
     {
       int nPos = m_astrFileTypes[ x ].lastIndexOf( '.' );

       if ( fIgnoreCase )
       {
         if ( nPos > 0 )
         {
           m_astrFilter[ x ][ 0 ] = m_astrFileTypes[ x ].substring( 0, nPos ).toUpperCase();

           if ( (nPos + 1 ) < m_astrFileTypes[ x ].length() )
           {
             m_astrFilter[x ][ 1 ] = m_astrFileTypes[ x ].substring( nPos + 1 ).toUpperCase();
           }
       }
       else
       if ( nPos == 0 )
       {
         m_astrFilter[ x ][ 0 ] = null;
       }
       else
       {
         m_astrFilter[ x ][ 0 ] = m_astrFileTypes[ x ].toUpperCase();
       }
       }
       else
       {

         if ( nPos > 0 )
         {
           m_astrFilter[x ][ 0 ] = m_astrFileTypes[ x ].substring( 0, nPos );

           if ( (nPos + 1 ) < m_astrFileTypes[ x ].length() )
           {
             m_astrFilter[x ][ 1 ] = m_astrFileTypes[ x ].substring( nPos + 1 );
           }

         }
         else
         if ( nPos == 0 )
         {
           m_astrFilter[x ][ 0 ] = null;
         }
         else
         {
           m_astrFilter[ x ][ 0 ] = strFilter;
         }

       } // end else


       if ( m_astrFileTypes[ x ].indexOf( '*' ) >= 0 || m_astrFileTypes[ x ].indexOf( '%' ) >= 0 )
       {
         m_fWildcards = true;
       }

     } // end for()

   } // end VwFileFilterHandler()


   /**
    * Determines if a given file name passes the filter test
    *
    * @return True if the file name passes the filter test, which means it will be returned
    * in the File.list() method; False if it fails the filter test.
    */
   public boolean accept( File dir, String strName )
   {
     File tFile = null;

     if ( strName != null )
     {
       String strFullFile = dir.getAbsolutePath() + File.separatorChar + strName;
       tFile = new File( strFullFile );
     }
     else
     {
       strName = dir.getName();
       tFile = dir;

     }

     if ( tFile.isDirectory() )
     {
       if ( m_fIncludeDirs )
       {
         return true;
       }
       else
       {
         return false;
       }
     }

     // *** First see if there
     if ( m_fWildcards )
     {
       return doWildCards( strName );
     }

     for ( int x = 0; x < m_astrFileTypes.length; x++ )
     {
       if ( m_fIgnoreCase )
       {
         if ( strName.equalsIgnoreCase( m_astrFileTypes[ x ] ) )
         {
           return true;             // File passes filter
         }
       }
       else
       {
         if ( strName.equals( m_astrFileTypes[ x ] ) )
         {
           return true;             // File passes filter
         }
       } // ned else

     } // end for()

     return false;              // File fails filter

   } // end accept()


   /**
    * Looks at the wildcard characters in the filter to see if the file name passes
    * the filter.
    *
    * @param strName The file name to test
    *
    * @return True if the file name passes the filter test; False if it fails
    */
   private boolean doWildCards( String strName )
   {

     boolean fMatch = true;

     boolean fBreak = false;

     int nPos = strName.lastIndexOf( '.' );

     String[] astrName = new String[ 2 ];

     if ( nPos >= 0 )
     {
       astrName[ 0 ] = strName.substring( 0, nPos );
       astrName[ 1 ] = strName.substring( nPos + 1 );
     }
     else
     {
       astrName[ 0 ] = strName;
     }

     if ( m_fIgnoreCase )
     {
       astrName[ 0 ] = astrName[ 0 ].toUpperCase();
       if ( astrName[ 1 ] != null )
       {
         astrName[ 1 ] = astrName[ 1 ].toUpperCase();
       }
     }

     for ( int i = 0; i < m_astrFileTypes.length; i++ )
     {
       fMatch = true;

       for ( int x = 0; x < 2; x++ )
       {
         String strFilterChunk = m_astrFilter[ i ][ x ];
         String strNameChunk = astrName[ x ];

         if ( strFilterChunk == null && strNameChunk == null )
         {
           return true;            // Found a match in a directory name or file with no extension
         }

         if ( strNameChunk == null && strFilterChunk != null )
         {
           if ( strFilterChunk.equals( "*" ) )
           {
             return true;
           }
           else
           {
             return false;
           }
         }

         if ( strFilterChunk == null || strNameChunk == null )
         {
           return false;           // mismatch
         }

         int nFilterLen = strFilterChunk.length();
         int nNameLen = strNameChunk.length();

         if ( strFilterChunk.equals( strNameChunk ) && x == 1 )
         {
           return true;              // Direct Match
         }

         fBreak = false;

         for ( int y = 0; y < nFilterLen; y++ )
         {

           if ( y >= nNameLen && (strFilterChunk.charAt( y ) != '*' ) )
           {
             fMatch = false;
             fBreak = true;
             break;         // Filter wants more characters to match than exist in the file name

           }

           switch( (int)strFilterChunk.charAt( y ) )
           {
             case (int)'*':

                   if ( x == 1 )
                   {
                     return true;  // Second file part test matches so we're all done
                   }

                   fBreak = true;
                   break;

             case (int)'%':        // Don't care if there is a match, look at next character
                  continue;

             default:
                  if ( strFilterChunk.charAt( y ) !=  strNameChunk.charAt( y ) )
                  {
                    fMatch = false;
                    fBreak = true;

                    break;   // Characters in name and filter do not match
                  }

           } // end switch()

           if ( fBreak )
           {
             break;                 // Go to outer for loop
           }


         } // end for( y... )

         if ( fMatch == false )
         {
           break;                  // Look at next filter
         }

         if ( !fBreak && ( nNameLen != nFilterLen ) )
         {
           fMatch = false;
           break;           // Filter < name so we have a mismmtach

         }

       } // end for( x ... )

       if ( fMatch )
       {
         return true;
       }

     } // end for( i )

     return false;

   } // end doWildcards()


} // end class VwFileFilterHandler{}


// *** End if VwFileFilterHandler.java ***

