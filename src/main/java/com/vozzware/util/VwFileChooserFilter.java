/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwFileChooserFilter.java

============================================================================================
*/


package com.vozzware.util;

import javax.swing.filechooser.FileFilter;
import java.io.File;

/**
 * This class implements the the FileFilter interface to provide a filter for wildcard directory
 * listing requests for the FileChooser. It wraps the VwFileFilterHandler which provides the actual 
 * functionality.
 */
 public class VwFileChooserFilter extends FileFilter
 {
   private VwFileFilterHandler   m_filterHandler;    // Handles wildcard filtering

   private String                 m_strDesc = "";

   /**
    * Constructs this class from a filter spec. I.E myfile.txt, *.txt, my*.*
    * This constrctor is used when case sensitivity is ignored

    * @param strFiler The file filter string which will test the file names passed
    * from the File.list() method. The wilecard charcaters '*' and '%' may be used.
    */
   public VwFileChooserFilter( String strFilter )
   { m_filterHandler = new VwFileFilterHandler( strFilter ); }


   /**
    * Constructs this class from a filter spec. I.E myfile.txt, *.txt, my*.*
    * This constrctor is used when case sensitivity is normally preserved

    * @param strFiler The file filter string which will test the file names passed
    * @param fIgnoreCase if the false passed , case sensitivity is preserved
    * from the File.list() method. The wilecard charcaters '*' and '%' may be used.
    */
   public VwFileChooserFilter( String strFilter, boolean fIgnoreCase )
   {  m_filterHandler = new VwFileFilterHandler( strFilter, fIgnoreCase ); }


   /**
    * Constructs this class from a filter spec. I.E myfile.txt, *.txt, my*.*
    * This constrctor is used when case sensitivity is normally preserved

    * @param strFiler The file filter string which will test the file names passed
    * @param fIgnoreCase if the false passed , case sensitivity is preserved
    * from the File.list() method. The wilecard charcaters '*' and '%' may be used.
    * @param fIncludeDir if true, return director names in the search
    */
   public VwFileChooserFilter( String strFilter, boolean fIgnoreCase, boolean fIncludeDirs )
   { m_filterHandler = new VwFileFilterHandler( strFilter, fIgnoreCase, fIncludeDirs ); }


   /**
    * Determines if a given file name passes the filter test
    *
    * @return True if the file name passes the filter test, which means it will be returned
    * in the File.list() method; False if it fails the filter test.
    */
  public boolean accept( File path )
  { return m_filterHandler.accept( path, null ); }


  /**
   * Gets the description string
   */
  public String getDescription()
  { return m_strDesc; }


  /**
   * Sets the description string
   *
   * @param strDesc The description string
   */
  public void setDescription( String strDesc )
  { m_strDesc = strDesc; }


} // end class VwFileChooserFilter{}


// *** End if VwFileChooserFilter.java ***

