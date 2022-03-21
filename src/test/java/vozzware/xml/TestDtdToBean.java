package test.vozzware.xml;

import com.vozzware.tools.VwDtdToJava;
import org.junit.Test;

import java.io.File;

/*
============================================================================================

    Source File Name: Transaction.java

    Author:           petervosburgh

    Date Generated:   8/10/21

    Time Generated:   7:18 AM

============================================================================================
*/
public class TestDtdToBean
{

  @Test
  public void testDtdToBean() throws Exception
  {
    File fileDtd = new File( "/Users/petervosburgh/dev/InvoiceDetail.dtd" );
    if ( !fileDtd.exists() )
    {
      throw new Exception( "File:" + fileDtd.getAbsolutePath() + " does not exist" );
    }

    VwDtdToJava dtj = new VwDtdToJava( fileDtd, null,"/Users/petervosburgh/dev/dtdBeans", "com.cirqit.cxml.invoice", null, false );
    dtj.process();

    return;

  } //end testDtdToBean()

} // end TestDtdToBean
