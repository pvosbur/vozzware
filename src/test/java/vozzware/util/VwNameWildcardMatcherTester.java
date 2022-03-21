package test.vozzware.util;


import com.vozzware.util.VwJar;
import com.vozzware.util.VwNameWildcardMatcher;
import org.junit.Test;

/*
============================================================================================

    Source File Name: Transaction.java

    Author:           petervosburgh
    
    Date Generated:   12/7/21

    Time Generated:   8:28 AM

============================================================================================
*/
public class VwNameWildcardMatcherTester
{
  @Test
  public void testNameMatch()
  {


    String strName = "TestName";
    String strFilter = "TestNam";

    VwNameWildcardMatcher matcher = new VwNameWildcardMatcher( strFilter );

    boolean fMatch = matcher.hasMatch( strName );

    int kkk = 1;

  }

}
