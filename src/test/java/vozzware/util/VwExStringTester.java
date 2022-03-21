/*
 *
 * ============================================================================================
 *
 *                                A r m o r e d  I n f o   W e b
 *
 *                                     Copyright(c) 2012 By
 *
 *                                       Armored Info LLC
 *
 *                             A L L   R I G H T S   R E S E R V E D
 *
 *  ============================================================================================
 * /
 */

package test.vozzware.util;

import com.vozzware.util.VwExString;
import com.vozzware.util.VwResourceMgr;
import junit.framework.Assert;
import org.junit.Test;
import test.vozzware.dvo.TestAddress;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/*
============================================================================================

    Source File Name: Transaction.java

    Author:           petervosburgh
    
    Date Generated:   8/2/13

    Time Generated:   6:41 AM

============================================================================================
*/
public class VwExStringTester
{
  @Test
  public void newTest()
  {
    File fileDir = new File( "/var/vwweb/temp/200" );


    boolean fOk = fileDir.mkdirs();

    return;
  }

  @Test
  public void testQueryStringToMap()
  {
    String strQueryString = "fname=Joe&lname=Blow&age=20";
    Map<String,String>mapParams = VwExString.queryStringToMap( strQueryString );

    Assert.assertTrue( "Expected map size to be 3 bout got: " + mapParams.size(),  mapParams.size() == 3 );

    Assert.assertTrue( "Expected fname to be Joe but got: " + mapParams.get( "fname"),  mapParams.get( "fname").equals( "Joe") );

  }
  @Test
  public void expandMacros() throws Exception
  {
    VwResourceMgr.loadBundle( "test", true );

    String strTestKey = VwResourceMgr.getString( "test.key" );


    TestAddress addr = new TestAddress(  );

    addr.setAddrLine1( "Blow Lane" );
    addr.setCity( "Famringdale" );

    addr.setState( "NY" );

    addr.setZip( "11745" );

    String strAddr = "${addrLine1} ${city} ${state}, ${zip}";

     strAddr =  VwExString.expandMacro( strAddr, addr );


    String strMacro = "${NOT_FOUND}";
    Map<String,String> mapReplace = new HashMap<>(  );
    mapReplace.put("Bogus", "bogus");

    strMacro = VwExString.expandMacro( strMacro, mapReplace );

    Assert.assertTrue( "Expected string ${NOT_FOUND} but got " + strMacro, strMacro.equals( "${NOT_FOUND}" ) );

    mapReplace.put( "NOT_FOUND", "Value not found" );

    strMacro = VwExString.expandMacro( strMacro, mapReplace );
    Assert.assertTrue( "Expected string Value not found but got " + strMacro, strMacro.equals( "Value not found" ) );

    strMacro = "${NOT_FOUND}";
    mapReplace.clear();
    mapReplace.put( "Bogus", "bogus" );

    strMacro = VwExString.replace( strMacro, mapReplace );

    return;
  }

  @Test
  public void testRemove() throws Exception
  {

    String strMultOccurs = "public static String;private class test();public void voidTest -private int m_nCount";

    String strRemoved = VwExString.remove( strMultOccurs, new String[]{"public", "private", "static", "class", "void"}, 0 );

    Assert.assertTrue( "Did not expect to find 'public'", strRemoved.indexOf( "public") < 0 );
    Assert.assertTrue( "Did not expect to find 'private'", strRemoved.indexOf( "private") < 0 );
    Assert.assertTrue( "Did not expect to find 'static'", strRemoved.indexOf( "static") < 0 );
    Assert.assertTrue( "Did not expect to find 'class'", strRemoved.indexOf( "class") < 0 );
    Assert.assertTrue( "Did not expect to find 'void'", strRemoved.indexOf( "void") < 0 );


    // Test Sing

    String strSingle = "The bad cow ate the bad bear";
    strRemoved = VwExString.remove( strSingle, "bad " );
    Assert.assertTrue( "Did not expect to find 'bad'", strRemoved.indexOf( "bad") < 0 );

    Assert.assertTrue( "Expected String The cow ate the  bear", strRemoved.equals( "The cow ate the bear")  );

  }


  @Test
  public void testToken() throws Exception
  {

    StringBuffer sbToken = new StringBuffer(  );
    String strToken = null;

    String strTest = "Hello-world,";

    int nPos  = VwExString.getToken( strTest, sbToken, 0, 1, "-" );

    Assert.assertTrue( "Did not expect nnot found", nPos >= 0  );

    strToken = sbToken.toString();

    Assert.assertTrue( "Expected token to equal Hello bot got " + strToken, strToken.equals( "Hello" ) );

    nPos = VwExString.getToken( strTest, sbToken, 5, 1, "," );

    strToken = sbToken.toString();

    Assert.assertNotNull( "Did not expect null string", strToken );

    Assert.assertTrue( "Expected token to equal -world bot got " + strToken, strToken.equals( "-world" ) );

    nPos = VwExString.getToken( strTest, sbToken, strTest.length() - 2, -1, "-" );
    strToken = sbToken.toString();

    Assert.assertNotNull( "Did not expect null string", strToken );

    Assert.assertTrue( "Expected token to equal world bot got " + strToken, strToken.equals( "world" ) );


    nPos = VwExString.getToken( strTest, sbToken, 4, -1, null );

    strToken = sbToken.toString();

    Assert.assertNotNull( "Did not expect null string", strToken );

    Assert.assertTrue( "Expected token to equal Hello bot got " + strToken, strToken.equals( "Hello" ) );

  }


  @Test
  public void testFindToken() throws Exception
  {
    String strTokens = "public static String;private class test();public void voidTest-private int m_nCount";

    // Search backwards
    int nPos = VwExString.findToken( strTokens, "static", strTokens.length() -1, -1, null  );

    Assert.assertTrue( "Expected index position to be: " + strTokens.indexOf( "static" ) + " but got " + nPos, nPos ==  strTokens.indexOf( "static" ) );

    // Search Forwards
    nPos = VwExString.findToken( strTokens, "int", 0, 1, null  );

    Assert.assertTrue( "Expected index position to be: " + strTokens.indexOf( "int" ) + " but got " + nPos, nPos ==  strTokens.indexOf( "int" ) );

    // Search forwards with no match
    nPos = VwExString.findToken( strTokens, "bogus", 0, 1, null  );

    Assert.assertTrue( "Expected index position to be: -1 but got " + nPos, nPos ==  -1 );


    // Search backwards with no match
    nPos = VwExString.findToken( strTokens, "bogus", strTokens.length() -1, -1, null  );

    Assert.assertTrue( "Expected index position to be: -1 but got " + nPos, nPos ==  -1 );

    // Search with forwards with delimiter

    nPos = VwExString.findToken( strTokens, "test()", 0, 1, ";"  );

    Assert.assertTrue( "Expected index position to be: " + strTokens.indexOf( "test()" ) + " but got " + nPos, nPos ==  strTokens.indexOf( "test()" ) );


    // Search with backwards with delimiter

    nPos = VwExString.findToken( strTokens, "private", strTokens.length() -1, -1, ";"  );

    Assert.assertTrue( "Expected index position to be: " + strTokens.indexOf( "private" ) + " but got " + nPos, nPos ==  strTokens.indexOf( "private" ) );


    // Search for next token starting at current index

    nPos = VwExString.findToken( strTokens, null, strTokens.indexOf( "String") -1, -1, null  );

    Assert.assertTrue( "Expected index position to be: " + strTokens.indexOf( "static" ) + " but got " + nPos, nPos ==  strTokens.indexOf( "static" ) );

    nPos = VwExString.findToken( strTokens, null, strTokens.indexOf( "String") -1, 1, null  );

    Assert.assertTrue( "Expected index position to be: " + strTokens.indexOf( "String" ) + " but got " + nPos, nPos ==  strTokens.indexOf( "String" ) );

  }

}
