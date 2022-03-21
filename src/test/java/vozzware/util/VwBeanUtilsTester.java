package test.vozzware.util;

import com.vozzware.util.VwBeanUtils;
import org.junit.Test;

import java.lang.reflect.Constructor;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/*
============================================================================================

    Source File Name: Transaction.java

    Author:           petervosburgh
    
    Date Generated:   3/3/22

    Time Generated:   3:47 PM

============================================================================================
*/
public class VwBeanUtilsTester
{

  @Test
  public void testCreateBeanInstance() throws Exception
  {

    TestNoArgs tna = (TestNoArgs)VwBeanUtils.createObjectInstance( "test.vozzware.util.TestNoArgs", null, null );

    assertNotNull("Expected instance of the object test.vozzware.util.TestNoArgs but got null", tna );

    String name = (String)VwBeanUtils.getValue( tna, "name" );
    int age = (int)VwBeanUtils.getValue( tna, "age" );

    assertNotNull( "Expecting a name property value but got null", name  );
    assertTrue( "Expecting name property value to be Test Name but got name", name.equals( "Test Name" ) );

    assertNotNull("Expecting an age property value but got null", age );
    assertTrue( "Expecting nameage property value to be 28 but got $age", age == 28 );

    TestArgs tArgs = (TestArgs)VwBeanUtils.createObjectInstance( "test.vozzware.util.TestArgs", new Class[]{String.class, int.class},
                                                                                                                    new Object[]{"Big Daddy", 45 } );
    assertNotNull("Expected instance of the object test.vozzware.util.TestArgs but got null", tArgs );

    name = (String)VwBeanUtils.getValue( tArgs, "name" );
    age = (int)VwBeanUtils.getValue( tArgs, "age" );

    assertNotNull( "Expecting a name property value but got null", name  );
    assertTrue( "Expecting name property value to be Test Name but got name", name.equals( "Big Daddy" ) );

    assertNotNull("Expecting an age property value but got null", age );
    assertTrue( "Expecting nameage property value to be 45 but got $age", age == 45 );


  }
}



