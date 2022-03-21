/*
============================================================================================
 

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwDAODocMgr.java

============================================================================================
*/

package com.vozzware.db.util;

import com.vozzware.codegen.VwCodeOptions;
import com.vozzware.util.VwDate;
import com.vozzware.util.VwDelimString;
import com.vozzware.util.VwFileUtil;
import com.vozzware.util.VwFormat;

import java.io.File;

/**
 * This class generates data value objects from columns defined in relational tables or sql query result set columns.
 */
public class VwSqlMappingDocMgr
{

  
  public static void write( VwSqlMappingDocument mappingDoc, VwCodeOptions codeOpts, String strAuthor, String strDocPath ) throws Exception
  {
     String strBackup = strDocPath + ".bak";
     
    File fileMappingDoc = new File( strDocPath );
 
    // copy existing doc with a .bak extension if the doc to write exists

    if ( fileMappingDoc.exists() )
      VwFileUtil.copy( fileMappingDoc, strBackup,  null, false );
    
    VwSqlMappingDocumentWriter.write( mappingDoc, new File( strDocPath ), createVwHeader( codeOpts, strAuthor, strDocPath ) );
    
  }
  
  private static String createVwHeader( VwCodeOptions codeOpts, String strAuthor, String strDocName )
  {
    // *** Build standard header file comment block

    int nWidth = codeOpts.m_sScreenWidth - 8;

    StringBuffer sbHeader = new StringBuffer( "<!--\n" );
    sbHeader.append( VwFormat.left( "=", nWidth, '=' ) ).append( "\n\n" );
    
    sbHeader.append(  VwFormat.center( "V o z z W o r k s   XML  D o c u m e n t   G e n e r a t o r", nWidth, ' ' ) );

    if ( codeOpts.m_strCopyright != null )
    {

      VwDelimString dlms = new VwDelimString( "\n", codeOpts.m_strCopyright );
      String strLine = null;

      while ((strLine = dlms.getNext() ) != null  )
      {
        sbHeader.append(  "\n\n" ).append(  VwFormat.center( strLine, nWidth, ' ' ) );

      } // end while()

    } // end if

    sbHeader.append(  "\n\n    Source File Name: ").append(  strDocName );


    if ( strAuthor != null )
      sbHeader.append(  "\n\n    Author:           ").append( strAuthor );

    VwDate dtToday = new VwDate();
    
    sbHeader.append( "\n\n    Date Generated:   ").append( dtToday.format( VwDate.USADATE ) );
    sbHeader.append( "\n\n    Time Generated:   ").append(  dtToday.format( "%H:%M:%S" ) ).append( "\n\n" );

    sbHeader.append( VwFormat.left( "=", nWidth, '=' ) ).append( "\n-->\n\n" );
    
    return sbHeader.toString();

  }

  
} // end class VwDAODocMgr{}


// *** End of VwDAODocMgr.java ***
