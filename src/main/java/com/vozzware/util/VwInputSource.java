/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwInputSource.java

============================================================================================
*/

package com.vozzware.util;

import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;

/**
 *
 */
public class VwInputSource
{
  private Reader m_reader;        // Generic Reader for this input source
  private long    m_lLen = 0;     // Length of input source (if known) or zero

  /**
   * Constructor for a File input source
   * @param fileInput  The File object
   * @throws Exception if the File cannot be found
   */
  public VwInputSource( File fileInput )  throws Exception
  {
    m_reader = new FileReader( fileInput );
    m_lLen = fileInput.length();

  } // end VwInputSource()


  /**
   * Constructor for a String input source
   * @param strInp  The String input source
   * @throws Exception
   */
  public VwInputSource( String strInp ) throws Exception
  {
    m_reader = new StringReader( strInp );
    m_lLen = strInp.length();

  } // end VwInputSource()

  /**
   * Constructor for an InputStream input source
   * @param insStream The InputStream  input spurce
   * @throws Exception
   */
  public VwInputSource( InputStream insStream ) throws Exception
  {
    m_reader = new InputStreamReader( insStream );
    m_lLen = 0;

  } // end VwInputSource()


  /**
   * Reads the entire contents of the input source into a StringBuffer
   * @return
   * @throws Exception if any IO errors occur
   */
  public StringBuffer readAll() throws Exception
  {
    long lLen = m_lLen;
    if ( lLen == 0 )
      lLen = 4096;      // Read in 4 k chunks

    StringBuffer sb = new StringBuffer( (int)lLen );
    char[]  chBuff = new char[ (int)lLen ];

    int nActual = 0;
    while( true )
    {
      int nGot = m_reader.read( chBuff );

      if ( nGot < 0 )
        break;        // EOF

      nActual += nGot;

      sb.append( chBuff, 0, nGot );


    } // end while()

    sb.setLength( nActual );
    return sb;
  }

} // enc class VwInputSource{}

// *** End of VwInputSource.java ***

