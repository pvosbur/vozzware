package test.vozzware.util;

import com.vozzware.util.VwFileUtil;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/*
============================================================================================

    Source File Name: Transaction.java

    Author:           petervosburgh
    
    Date Generated:   5/5/17

    Time Generated:   7:16 AM

============================================================================================
*/
public class VwZipFIleTester
{

  @Test
  public void testZipFiles() throws Exception
  {
    File fileZip = new File("/Users/petervosburgh/Documents/test.zip" );

    List<File> lisFilesToZip = new ArrayList<>( );

    File fileToZip = new File( "/Users/petervosburgh/Documents/A Matter of Trust.m4a" );

    lisFilesToZip.add( fileToZip );

    fileToZip = new File( "/Users/petervosburgh/Documents/53-FatBurning-Smoothies-NM121510BT.pdf" );

    lisFilesToZip.add( fileToZip );
    VwFileUtil.zipFileList( fileZip, lisFilesToZip );
  }
}
