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

import com.vozzware.util.VwResourceMgr;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Properties;

/*
============================================================================================

    Source File Name: Transaction.java

    Author:           petervosburgh
    
    Date Generated:   6/23/12

    Time Generated:   6:31 AM

============================================================================================
*/
public class VwResourceMgrTester
{
  @BeforeClass
  public static void loadBundles() throws Exception
  {
    VwResourceMgr.loadBundle( "junit", true );

  }
  @Test
  public void testGetMacroString() throws Exception
  {

    Properties props = VwResourceMgr.getProperties( "junit" );


    String strRes = VwResourceMgr.getMacroString( "single.object.property", 10L );

    Assert.assertTrue( "Expected to get 'you passed the number 10", strRes.equals( "you passed the number 10" ));

    strRes = VwResourceMgr.getMacroString( "array.object.values", new String[]{"boats", "cars"} );

    Assert.assertTrue( "Array index 1 value is cars and array index 0 is boats", strRes.equals( "Array index 1 value is cars and array index 0 is boats" ));

    strRes = VwResourceMgr.getMacroString( "array.object.values", new String[]{"1", "2"} );

    Assert.assertTrue( "Array index 1 value is 2 and array index 0 is 1", strRes.equals( "Array index 1 value is 2 and array index 0 is 1" ));

  }
}
