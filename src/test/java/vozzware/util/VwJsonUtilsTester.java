package test.vozzware.util;

import com.vozzware.util.VwJsonUtils;
import org.junit.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/*
============================================================================================

    Source File Name: Transaction.java

    Author:           petervosburgh
    
    Date Generated:   12/21/14

    Time Generated:   7:12 AM

============================================================================================
*/
public class VwJsonUtilsTester
{
  @Test
  public void testMapToJson() throws Exception
  {
    Date toDay = new Date();


    Map<String,Object> mapValues = new HashMap( );
    mapValues.put( "name", "Jon Doe");
    mapValues.put( "age", "30");

    String strJson = VwJsonUtils.toJson( mapValues );

    System.out.println( strJson );

    strJson = VwJsonUtils.toJson( mapValues, true );

    System.out.println( strJson );

    Map<String,String> mapIgnoreKeys = new HashMap<String, String>(  );

    mapIgnoreKeys.put( "name", null );

    //todo strJson = VwJsonUtils.toJson( mapValues, true, null, null, mapIgnoreKeys );

    System.out.println( strJson );


    mapValues.put( "date", toDay );

    //todo strJson = VwJsonUtils.toJson( mapValues, true, new String[]{"date"}, "EEE MMM dd yyyy HH:mm:ss zzz", mapIgnoreKeys );

    System.out.println( strJson );

  }

}
