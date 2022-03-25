/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwCfgParser.java

Create Date: Apr 11, 2005
============================================================================================
*/

package com.vozzware.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ResourceBundle;
import java.util.Vector;


/**
 * Parses an ascii config style file in the format of "Name <token separator> Value".
 * The constructor takes the file name to parse, a string describing a comment tokens,
 * and a string describing the Name/Value token delimiter.
 *
 * Usage: new VwCfgParser( "myfile.cfg", "REM", "=" )
 *
 * Where:        <"myfile.cfg"> is the name of the ascii config style file to parse.
 *               <"REM"> is a comment string for lines to be ignored by the parser.
 *               <"="> is the delimiter.  E.g., "AUTO=Yes" where "AUTO" is the Name, "="
 *                is the delimiter, and "Yes" is the Value.
 *
 * @version 1.0
*/

public class VwCfgParser extends Object
{

  private Vector          m_vecNameVal;       // Vector table of the Name/Value pairs

  private boolean         m_fIsValid = false; // Object validity flag

  private String          m_strFileName;      // Name of file to operate on or create

  private String          m_strDelim;         // Name/value delimiter

  private String          m_strErrDesc = "";  // Last error description

  private byte[]          m_abBuff;           // Buffer to hold input file

  private int             m_nOffset = -1;     // Cursor index into file

  private int             m_nLen;             // Length of buffer

  private ResourceBundle  m_msgs = ResourceBundle.getBundle( "resources.properties.vwutil" );


  class Record
  {
    boolean             m_fIsComment;       // if true item is a comment
    String              m_strName;          // The param name
    String              m_strValue;         // The param value
    String              m_strComment;       // Optional line comment

    // *** Constructs a coomrnt record
    Record( String strComment )
    { m_fIsComment = true; m_strComment = strComment; }

    // *** Constrcuts a name/val record
    Record( String strName, String strValue, String strComment )
    {
      m_fIsComment = false;
      m_strName = strName;
      m_strValue = strValue;
      m_strComment = strComment;
    } // end Record()

  } // end class Record{}


  /**
   * Constructs the Cfg Parser
   *
   * @param strFileName - The name of the file to parse
   * @param strComment - The sequences of char(s) that start a line comment
   * @param strDelim - The char(s) that define the Name/Value pair delimiter
   *
   */
   public VwCfgParser( String strFileName, String strComment, String strDelim )
     throws FileNotFoundException, Exception
   { this( strFileName, strComment, strDelim, false, true, false ); }

  /**
   * Constructs the Cfg Parser
   *
   * @param strFileName  The name of the file to parse
   * @param strComment  The sequences of char(s) that start a line comment
   * @param strDelim  The char(s) that define the Name/Value pair delimiter
   * @param fPreserveComments  if true preserve all comments. This option is
   * useful when your are updating this file and you want to preserve the original comments.
   *
   */
   public VwCfgParser( String strFileName, String strComment,
                        String strDelim, boolean fPreserveComments,
                        boolean fTreatWhiteSpaceAsComments,
                        boolean fCreate )
     throws FileNotFoundException, Exception
   {

     m_strFileName = strFileName;
     m_strDelim = strDelim;
     m_msgs.getString("VwUtil.DupKey");

     // *** Create vector for the name value pairs

     m_vecNameVal = new Vector();

     try
     {
       // *** Try to open input stream

       FileInputStream cfgFile = new FileInputStream( strFileName );
       File inFile = new File( strFileName );

       m_nLen = (int)inFile.length();         // Save file length

       m_abBuff = new byte[ m_nLen ];         // Create buffer to hold entire file

       int nLineNbr = 0;                      // Keep track of line numbers

       // *** Read entire file into buffer

       cfgFile.read( m_abBuff );

       // *** Build vector of entries
       while( true )
       {

         String strLine = getLine();
         if ( strLine == null )
           break;

         ++nLineNbr;

         // Test to see if the line starts with the comment sequence

         if ( strLine.startsWith( strComment ) )
         {
           if ( fPreserveComments )
             m_vecNameVal.addElement( new Record( strLine ) );

           continue;      // Comment line found, get next line

         }

         // *** If line only contains white spaces characters then ignore it

         if ( VwExString.isWhiteSpace( strLine ) )
         {
           if ( fPreserveComments )
             m_vecNameVal.addElement( new Record( strLine ) );

           continue;
         }

         int nPos = strLine.indexOf( strDelim );

         if ( nPos < 0 )
         {
           m_strErrDesc = "Invalid entry on line : " + String.valueOf( nLineNbr )
                        + " No value delimiter found";
           cfgFile.close();

           return;
         }

         // *** Get all characters up to delimiter ( keyword )

         String strKeyWord = strLine.substring( 0, nPos );

         // *** Rest of string is the value plus any comments

         String strValue = strLine.substring( nPos + 1 );

         // Value is all characters up to first space or tab

         nPos = VwExString.findAny( strValue, "\r\n\t ", 0 );

         // If a space or tab was found then get charaters up to that position -- anything
         // past that is assumed a comment

         String strComments = null;

         if ( nPos > 0 )
         {
           if ( fPreserveComments && fTreatWhiteSpaceAsComments)
             strComments = strValue.substring( nPos );

           if ( fTreatWhiteSpaceAsComments )
             strValue = strValue.substring( 0, nPos );
         }

         m_vecNameVal.addElement( new Record( strKeyWord, strValue, strComments ) );

       } // end while

       m_fIsValid = true;

       cfgFile.close();

       m_abBuff = null;     // Don't need this memory any more
       return;
     } // end try
     catch( FileNotFoundException fnf )
     {
       if ( !fCreate )
         throw fnf;

       // Create the file

     }

   } // end VwCfgParser


   /*
    * Returns the next line in the file buffer
    *
    * @return A String containing the next line in the file buffer
    */
   private final String getLine()
   {
     int nCurPos = m_nOffset + 1;     // Mark current position
     boolean fLineContinue = false;

     String strLine = "";

     while( true )
     {

       if ( ++m_nOffset >= m_nLen )
         break;

       if ( m_abBuff[ m_nOffset ] == '\\' )
       {
         fLineContinue = true;
         ++m_nOffset;
       }

       // We're looking for a cr or cr/lf pair
       if ( m_abBuff[ m_nOffset ] == '\r' || m_abBuff[ m_nOffset ] == '\n' )
       {
         if ( fLineContinue )
         {
           fLineContinue = false;

           strLine += new String( m_abBuff, nCurPos, ( m_nOffset - 1 - nCurPos ) );

           // If a carraige return was found bump up by one so the next call will properly
           // bypass line feed (If the following charater is a line feed)

           if ( m_abBuff[ m_nOffset ] == '\r' )
           {
             if ( ( m_nOffset + 1 ) < m_nLen )
            {
               if ( m_abBuff[ ( m_nOffset + 1 ) ] == '\n' )
                 ++m_nOffset;
             }

           }

           nCurPos = m_nOffset + 1;
           continue;

         } // end if ( fLineContinue )


         // *** Create string from current line position in buffer

         strLine += new String( m_abBuff, nCurPos, ( m_nOffset - nCurPos ) );

         // If a carraige return was found bump up by one so the next call will properly
         // bypass line feed (If the following charater is a line feed)

         if ( m_abBuff[ m_nOffset ] == '\r' )
         {
           if ( ( m_nOffset + 1 ) < m_nLen )
           {
             if ( m_abBuff[ ( m_nOffset + 1 ) ] == '\n' )
               ++m_nOffset;
           }

         }

         return strLine;

       } // end if

       fLineContinue = false;

     } // end while()

     if ( m_nOffset > m_nLen )
       return null;

     return new String( m_abBuff, nCurPos, ( m_nOffset - nCurPos ) );

   } // end getLine()

   /**
    * Returns the object validity state
    *
    * @return True if object is valid after construction
    */
   public final boolean  isValid()
   { return m_fIsValid; }


   /**
    * Returns last error desc
    *
    * @return A String with the last known error
    */
    public final String getErrDesc()
    { return m_strErrDesc; }


    /**
     * Lookup the value for the Name (i.e., keyword) specified
     *
     * @param strName The Name part of the Name/Value pair
     *
     * @return A String with the value if the strName exists; otherwise a null string is returned
     */
    public final String getValue( String strName )
    {
      Record rec = findRecord( strName );

      if ( rec != null )
        return rec.m_strValue;

      return null;          // Name not found

    } // end getValue


    /**
     * Adds a name/value pair to the file
     *
     * @param strName The param name
     * @param strValue The param value to add
     *
     * @exception Exception if the name value already exists
     */
    public void addItem( String strName, String strValue ) throws Exception
    {
      if ( findRecord( strName ) != null )
        throw new Exception( m_msgs.getString( "VwUtil.DupKey" ) );

      // Add new entry

      m_vecNameVal.addElement( new Record( strName, strValue, null ) );

    } // end addItem()


    /**
     * Adds a name/value pair and comment that follows the the value to the file
     *
     * @param strName The param name
     * @param strValue The param value to add
     * @param strComment The comment that follows the value
     *
     * @exception Exception if the name value already exists
     */
    public void addItem( String strName, String strValue, String strComment ) throws Exception
    {
      if ( findRecord( strName ) != null )
        throw new Exception( m_msgs.getString( "VwUtil.DupKey" ) );

      // Add new entry

      m_vecNameVal.addElement( new Record( strName, strValue, strComment ) );

    } // end addItem()



    /**
     * Updates the data associated with the name
     *
     * @param strName The param name
     * @param strValue The param value to update
     *
     * @exception Exception if the name does not exist
     */
    public void updateItem( String strName, String strValue ) throws Exception
    {
      Record rec = findRecord( strName );
      if ( rec == null )
        throw new Exception( m_msgs.getString( "VwUtil.KeyNotFound" ) );

      rec.m_strValue = strValue;

    } // end UpdateItem()



    /**
     * Updates the data associated with the name with comments
     *
     * @param strName The item name
     * @param strValue The param value to update
     * @param strComment The new comment
     *
     * @exception Exception if the name does not exist
     */
    public void updateItem( String strName, String strValue, String strComment )
      throws Exception
    {
      Record rec = findRecord( strName );
      if ( rec == null )
        throw new Exception( m_msgs.getString( "VwUtil.KeyNotFound" ) );

      rec.m_strValue = strValue;
      rec.m_strComment = strComment;

    } // end UpdateItem()


    /**
     * Removes the named item from the file
     *
     * @param strName The item name
     *
     * @exception Exception if the named item does not exist
     */
    public void removeItem( String strName ) throws Exception
    {

      for ( int x = 0; x < m_vecNameVal.size(); x++ )
      {
        Record rec = (Record)m_vecNameVal.elementAt( x );
        if ( rec.m_fIsComment )
          continue;

        if ( rec.m_strName.equalsIgnoreCase( strName ) )
        {
          m_vecNameVal.removeElementAt( x );
          return;
        }

      } // end for()

      throw new Exception( m_msgs.getString( "VwUtil.KeyNotFound" ) );

    } // end removeItem()


    /**
     * Removes all items that match the key. This allows removal on partial key matches.
     * The key match is done from the begining of the key.
     *
     * @param strName The partial key item to remove
     *
     * @return The nbr of items removed
     */
    public int removeItems( String strName ) throws Exception
    {
      int nRemoved = 0;

      for ( int x = 0; x < m_vecNameVal.size(); x++ )
      {
        Record rec = (Record)m_vecNameVal.elementAt( x );
        if ( rec.m_fIsComment )
          continue;

        if ( rec.m_strName.toLowerCase().startsWith( strName.toLowerCase() ) )
        {
          m_vecNameVal.removeElementAt( x );
          x-= 1;
          ++nRemoved;
        }

      } // end for()

      return nRemoved;

    } // end removeItem()



    /**
     * Updates or creates the named file with the new values
     *
     * @exception IOException if any write errors occur
     */
    public void updateFile() throws IOException
    {

      String strTemp = null;
      String strNewLine = null;

      if ( File.separatorChar == '/' )
        strNewLine = "\n";
      else
        strNewLine = "\r\n";

      int nPos = m_strFileName.lastIndexOf( File.separatorChar );

      if ( nPos >= 0 )
        strTemp = m_strFileName.substring( 0, nPos + 1 );
      else
        strTemp = "";

      strTemp += "itctemp.txt";

      File fileTemp = new File( strTemp );

      // Always try to delete, don't care if its not there
      fileTemp.delete();

      FileWriter ftemp = new FileWriter( fileTemp );

      // *** Write out the entries in the vector

      for ( int x = 0; x < m_vecNameVal.size(); x++ )
      {
        Record rec = (Record)m_vecNameVal.elementAt( x );

        if ( rec.m_fIsComment )
          ftemp.write( rec.m_strComment + strNewLine );
        else
        {

          String strLine = rec.m_strName + m_strDelim + rec.m_strValue;
          if ( rec.m_strComment != null )
            strLine += rec.m_strComment;

          strLine += strNewLine;

          ftemp.write( strLine );

        } // end else

      } // end for()

      ftemp.close();

      // Delete the orig file and rename our new temp file back to the orig file

      File origFile = new File( m_strFileName );

      origFile.delete();

      if ( !fileTemp.renameTo( origFile ) )
        throw new IOException( VwExString.replace( m_msgs.getString( "VwUtil.CreateError" ),
                                                    "<FILENAME>", m_strFileName ) );

    } // end updateFile()



    /**
     * Adds a comment line to the end of the file
     *
     * @param strComment The comment to add
     */
    public void addComment( String strComment )
    { m_vecNameVal.addElement( new Record( strComment ) ); }



    /**
     * Adds a comment line in front of a data name requested
     *
     * @param strName The param name where the comment should be placed in front of
     * @param strComment The comment to add
     *
     * @exception Exception if the name does not exist
     */
    public void addComment( String strName, String strComment ) throws Exception
    {

      for ( int x = 0; x < m_vecNameVal.size(); x++ )
      {
        Record rec = (Record)m_vecNameVal.elementAt( x );
        if ( rec.m_fIsComment )
          continue;

        if ( rec.m_strName.equalsIgnoreCase( strName ) )
        {
          m_vecNameVal.insertElementAt( new Record( strComment ), x );
          return;
        }

      } // end for()

      throw new Exception( m_msgs.getString( "VwUtil.KeyNotFound" ) );


    } // end comment

    /**
     * Finds a Record given the name
     *
     * @param The lookup name key
     *
     * @return The Record object for this name or null if not found
     */
    private Record findRecord( String strName )
    {

      for ( int x = 0; x < m_vecNameVal.size(); x++ )
      {

        Record rec = (Record)m_vecNameVal.elementAt( x );

        if ( rec.m_fIsComment )
          continue;

         if ( strName.equalsIgnoreCase( rec.m_strName ) )
           return rec;

      } // end for()

      return null;           // Not found

    } //end findRecord()
}  // end class VwCfgParser{}

// *** End of VwCfgParser.java ***
