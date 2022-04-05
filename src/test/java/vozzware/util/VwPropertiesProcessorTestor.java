package vozzware.util;

import com.vozzware.util.VwPropertiesProcessor;
import org.junit.Test;
import org.springframework.util.Assert;

import java.io.FileNotFoundException;
import java.net.URL;

/*
============================================================================================

    Source File Name: Transaction.java

    Author:           petervosburgh
    
    Date Generated:   4/3/22

    Time Generated:   9:42 AM

============================================================================================
*/
public class VwPropertiesProcessorTestor
{
  @Test
  public void testPropertiesProcessor() throws Exception
  {
    URL urlProps = null;

    try
    {
      urlProps = new URL( "file:////Users/petervosburgh/dev/VozzWare/VozzWorks/src/test/resources/resources/properties/test.properties" );
      VwPropertiesProcessor pp = new VwPropertiesProcessor( urlProps );

      String strVal = pp.getString( "mytest" );

      Assert.notNull( strVal ," expeced value for property mytest but got null");

      Assert.isTrue( strVal.equals( "Test"), "expected value of 'Test' but got: " + strVal );

      // Test default value for not found key
      strVal = pp.getString( "bogus", "Test" );

      Assert.notNull( strVal ," expeced value for property mytest but got null");

      Assert.isTrue( strVal.equals( "Test"), "expected value of 'Test' but got: " + strVal );

      int nVal = pp.getInt( "two" );

      Assert.isTrue(nVal == 2, "expected numeric value of '2' but got: " + nVal );

      // Test bdefault value for not found key
      nVal = pp.getInt( "bogus", 2 );

      Assert.isTrue(nVal == 2, "expected numeric value of '2' but got: " + nVal );

      double dblVal = pp.getDouble( "mydouble" );

      Assert.isTrue(dblVal == 10.3, "expected double value of '10.3' but got: " + dblVal );

      // Test default value for not found key

      dblVal = pp.getDouble( "bogus", 10.3 );

      Assert.isTrue(dblVal == 10.3, "expected double value of '10.3' but got: " + dblVal );


    }
    catch( FileNotFoundException fne )
    {
      throw new Exception(  "Invalid URL to file: " + urlProps.getFile() );
    }
    catch( Exception ex )
    {
      throw ex;
    }
  }
}
