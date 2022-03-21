/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwFileChooser.java

Create Date: Apr 11, 2003
============================================================================================
*/

package com.vozzware.ui;

import com.vozzware.util.VwDelimString;
import com.vozzware.util.VwExString;
import com.vozzware.util.VwFileChooserFilter;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import java.awt.Component;
import java.io.File;

/**
 * This class extends the Swing JTable control.  Its primary purpose is to make the data aware
 * in a 3 tier environment.  The VwFileChooser talks to the Opera Server and uses the Opera
 * Services to get its data.
 */
public class VwFileChooser
{
  private String    m_strCurrentDir;

  /**
   * Constructs the grid control
   *
   * @param strCurrentDir The directory to use for open and save dialogs. If null,
   * <br> the current directory is used
   */
  public VwFileChooser()
  { super(); }

  /**
   * Constructs the grid control
   *
   * @param strCurrentDir The directory to use for open and save dialogs. If null,
   * <br> the current directory is used
   */
  public VwFileChooser( String strCurrentDir )
  {
    super();                 // Call super class constructor

    m_strCurrentDir = strCurrentDir;

  }

  /**
   * Show the file open dialog using the ui JFileChooser dialog. This will stay in a loop
   * until the user selects one of the allowed file type(s) or cancels.
   *
   * @param parentComp The parent window component
   * @param strFileTypes a comma separated list of allowable file extension types.
   * <br>i.e. *.doc,*.txt ....
   * @param strFileDesc A description string of the file types. i.e. ( word documents, text files)
   *
   */
   public File getFileName( Component parentComp, String strFileTypes, String strFileDesc )
   { return getFileName( parentComp, strFileTypes, strFileDesc, false ); }


  /**
   * Show the file open dialog using the ui JFileChooser dialog. This will stay in a loop
   * until the user selects one of the allowed file type(s),cancels or chooses a directory if the fAllowDirectories is true.
   *
   * @param parentComp The parent window component
   * @param strFileTypes a comma separated list of allowable file extension types.
   * <br>i.e. *.doc,*.txt ....
   * @param strFileDesc A description string of the file types. i.e. ( word documents, text files)
   * @param fAllowDirectories if true, allow both file names and directories to be choosen, else allow only file names defined in the strFileTypes parameter 
   */
  public File getFileName( Component parentComp, String strFileTypes, String strFileDesc, boolean fAllowDirectories )
  {
    parentComp.setEnabled( false );

    VwDelimString dlmstr = new VwDelimString( ",", strFileTypes );

    String[] astrFileTypes = dlmstr.toStringArray();

    File fileChosen = null;

    JFileChooser fc = null;
    fc = new JFileChooser();

    VwFileChooserFilter fcf = new VwFileChooserFilter( strFileTypes, true, true );
    fcf.setDescription( strFileDesc );
    fc.addChoosableFileFilter( fcf );
    //fc.setFileFilter( fcf );
    fc.setAcceptAllFileFilterUsed( false );
    if ( m_strCurrentDir != null )
      fc.setCurrentDirectory( new File( m_strCurrentDir ) );
    else
      fc.setCurrentDirectory( new File( "." ) );

    int nSearchMode = fAllowDirectories? JFileChooser.FILES_AND_DIRECTORIES : JFileChooser.FILES_ONLY;
    fc.setFileSelectionMode( nSearchMode );
    while( true )
    {

      String strFile = null;


      int returnVal = fc.showOpenDialog( parentComp );

      if( returnVal == JFileChooser.APPROVE_OPTION )
      {
        fileChosen = fc.getSelectedFile();

        strFile = fileChosen.getName();

        if ( !fAllowDirectories )
        {
          String strExt = VwExString.getFileExt( strFile );
  
          if ( ( strExt == null) || ( !isIn( astrFileTypes, strExt ) ) )
          {
            JOptionPane.showMessageDialog( parentComp, "File must be one of type " + strFileDesc );
            fileChosen = null;      // Invalid file
            continue;
  
          }
        }
        
        break;


      } // end if

      break;

    } // end while()

    parentComp.setEnabled( true );

    return fileChosen;

  } // end getFileName()



  /**
   * Show the file open dialog using the ui JFileChooser dialog. This method is for getting
   * directory names only.
   *
   * @param parentComp The parent window component
   * @param strFileTypes a comma separated list of allowable file extension types.
   * <br>i.e. *.doc,*.txt ....
   * @param strFileDesc A description string of the file types. i.e. ( word documents, text files)
   */
  public File getDirectory( Component parentComp )
  {
    parentComp.setEnabled( false );

    String strFileTypes = "*.";
    String strFileDesc = "(Directories)";

    VwDelimString dlmstr = new VwDelimString( ",", strFileTypes );

    String[] astrFileTypes = dlmstr.toStringArray();

    File fileChosen = null;

    JFileChooser fc = null;
    fc = new JFileChooser();
    fc.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY);
    
    VwFileChooserFilter fcf = new VwFileChooserFilter( strFileTypes, true, true );
    fcf.setDescription( strFileDesc );
    fc.setFileFilter( fcf );

    if ( m_strCurrentDir != null )
      fc.setCurrentDirectory( new File( m_strCurrentDir ) );
    else
      fc.setCurrentDirectory( new File( "." ) );


    while( true )
    {

      String strFile = null;


      int returnVal = fc.showDialog( parentComp, "Choose Directory" );

      if( returnVal == JFileChooser.APPROVE_OPTION )
      {
        fileChosen = fc.getSelectedFile();

        String strDir = fileChosen.getAbsolutePath();

        fileChosen =  new File( strDir );

        if ( !fileChosen.isDirectory() )
        {
          JOptionPane.showMessageDialog( parentComp, "Please Choose a directory only" );
          fileChosen = null;      // Invalid file
          continue;

        }

        break;


      } // end if

      break;

    } // end while()

    parentComp.setEnabled( true );

    return fileChosen;

  } // end getDirectory()


  /**
   * Show the file file save dialog using the ui JFileChooser dialog.
   *
   * @param parentComp The parent window component
   * @param strFileTypes a comma separated list of allowable file extension types.
   * <br>i.e. *.doc,*.txt ....
   * @param strFileDesc A description string of the file types. i.e. ( word documents, text files)
   */
  public File getFileSaveName( Component parentComp )
  {
    parentComp.setEnabled( false );

    JFileChooser fc = null;
    fc = new JFileChooser();

    if ( m_strCurrentDir != null )
      fc.setCurrentDirectory( new File( m_strCurrentDir ) );
    else
      fc.setCurrentDirectory( new File( "." ) );

    int returnVal = fc.showSaveDialog( parentComp );

    parentComp.setEnabled( true );

    if( returnVal == JFileChooser.CANCEL_OPTION )
      return null;

    File fileChosen =  fc.getSelectedFile();

    if ( fileChosen.exists() )
    {
      int nRet = JOptionPane.showConfirmDialog( parentComp, "File: '" + fileChosen.getAbsolutePath()
                                                + " already exists. Overwrite?" );

      if ( nRet != JOptionPane.YES_OPTION )
        return null;
    }

    return fileChosen;


  } // end getFileSaveName()

  /**
   * See if a string is contained in a string array
   *
   * @param astrItems The array of strings to search
   * @param strItem The item to search for
   *
   */
  private boolean isIn ( String[]  astrItems, String strItem )
  {
    for ( int x = 0; x < astrItems.length; x++ )
    {
      if ( astrItems[ x ].toLowerCase().indexOf( strItem ) >= 0 )
        return true;
    }

    return false;

  } // end isIn()

} // end class VwFileChooser{}


// *** End of VwFileChooser.java ***

