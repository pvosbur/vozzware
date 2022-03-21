package test.vozzware.serialize;

import junit.framework.Assert;
import org.junit.Test;
import test.vozzware.dvo.TestAddress;
import test.vozzware.dvo.TestComplexDvo;
import test.vozzware.dvo.TestSimpleDvo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

/*
============================================================================================

    Source File Name: Transaction.java

    Author:           petervosburgh
    
    Date Generated:   2/8/15

    Time Generated:   7:36 AM

============================================================================================
*/
public class TestObjectSerialize
{

  @Test
  public void testSimpleSerialize() throws Exception
  {
    TestSimpleDvo simpleDvo = new TestSimpleDvo();
    simpleDvo.setFirstName( "Jon" );
    simpleDvo.setLastName( "Doe" );
    simpleDvo.setAge( 40 );


    byte[] abObj = toByteArray( simpleDvo );

    TestSimpleDvo simpleDvo2 = (TestSimpleDvo)fromByteArray( abObj );

    Assert.assertNotNull( simpleDvo2 );
    Assert.assertTrue( simpleDvo2.getFirstName().equals( "Jon" ) );

    return;

  }

  @Test
  public void testComplexSerialize() throws Exception
  {
    TestComplexDvo complexDvo = new TestComplexDvo();
    complexDvo.setFirstName( "Jon" );
    complexDvo.setLastName( "Doe" );
    complexDvo.setAge( 40 );

    List<TestAddress>listAddresses = new ArrayList<TestAddress>(  );

    listAddresses.add( new TestAddress( "One Doe Lane", "Queens", "NY", "11111" ));
    listAddresses.add( new TestAddress( "40 Valley Circle", "Fairfield", "CT", "06845" ));

    complexDvo.setAddresses( listAddresses );

    byte[] abObj = toByteArray( complexDvo );

    Assert.assertNotNull( abObj );

    TestComplexDvo complexDvo2 = (TestComplexDvo)fromByteArray( abObj );
    Assert.assertNotNull( complexDvo2 );


    abObj = toByteArray( "This is just a String" );
    Assert.assertNotNull( abObj );

    String objTest = (String)fromByteArray( abObj );
    Assert.assertNotNull( abObj );

    Assert.assertTrue( "Expected String to be 'This is just a String' but got " + objTest, objTest.equals( "This is just a String") );

    abObj = toByteArray( 222L );
    Assert.assertNotNull( abObj );

    long lVal = (long)fromByteArray( abObj );
    Assert.assertNotNull( lVal );

    Assert.assertTrue( "Expected lVal tp be 222 but got " + lVal, lVal == 222 );

  }


  private byte[] toByteArray( Object objToSerialize )  throws Exception
  {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ObjectOutputStream oos = new ObjectOutputStream( baos );
    oos.writeObject( objToSerialize );
    oos.close();

    return baos.toByteArray();

  }


  private Object fromByteArray( byte[] abSerializedObject )   throws Exception
  {
    ByteArrayInputStream bais = new ByteArrayInputStream( abSerializedObject );

    ObjectInputStream ois = new ObjectInputStream( bais );

    return ois.readObject();

  }

}
