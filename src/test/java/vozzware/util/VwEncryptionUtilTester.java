package test.vozzware.util;

import com.vozzware.util.VwEncryptionUtil;
import org.junit.Test;

/*
============================================================================================

    Source File Name: 

    Author:           petervosburgh
    
    Date Generated:   4/9/16

    Time Generated:   9:56 AM

============================================================================================
*/
public class VwEncryptionUtilTester
{

  @Test
  public void testStringEncrypt() throws Exception
  {
    String strEnCrypt = "DAD5CCACKFV2cmRlZmluZWQ=f602pc3RpbmdTa2e5ffZ2V0VXNlckxdad3VsP2xpZD11b4918MDMwMDAwMGIwMTAwMDAwYjAwMDAwMDBiMDIwMDAwMGI=fc04c6af9b0bf0adc56e2e5d8b8b195d";

    String strDeCrypt = VwEncryptionUtil.deCrypt( strEnCrypt );

    System.out.printf( strDeCrypt );
  }
}
