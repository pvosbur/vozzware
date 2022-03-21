package test.vozzware.xml;/*
============================================================================================

                       V o z z W o r k s   C o d e   G e n e r a t o r                      

                                    Copyright(c) 2011 By                                    

                        V   o   z   z   w   a   r   e   L   L   C   .                       

                            A L L   R I G H T S   R E S E R V E D                           

    Source File Name: Transaction.java

    Author:           Vozzware LLC

    Date Generated:   04-23-2011

    Time Generated:   12:55:42

============================================================================================
*/

import com.vozzware.util.VwBeanUtils;
import com.vozzware.util.VwJsonUtils;
import com.vozzware.xml.VwBeanToJson;
import com.vozzware.xml.VwJsonToBean;
import com.vozzware.xml.VwJsonWriter;
import com.vozzware.xml.VwPrimitiveListWrapper;
import junit.framework.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class TestJson
{

  @Test
  public void testJsonWriter()
  {

    VwJsonWriter jw = new VwJsonWriter(  );

    // Simple object with an array of primitives
    jw.beginObject( null );
    jw.addProperty( "firstName", "Joe", true );
    jw.addProperty( "lastName", "blow", true );
    jw.addProperty( "isMarried", "true", false );
    jw.addArray( "favColors", new String[]{"Red", "Blue"}, true);

    jw.endObject();

    String strJson = jw.toString();

    System.out.println( strJson );


    // Array of objects

    jw.clear();

    // Simple object with an array of primitives
    jw.beginArray();

    jw.beginObject( null );
    jw.addProperty( "firstName", "Joe", true );
    jw.addProperty( "lastName", "blow", true );
    jw.addProperty( "isMarried", "true", false );
    jw.addArray( "favNumbers", new Object[]{1,2,3}, false );

    strJson = jw.toString();

    System.out.println( strJson );

    jw.endObject();

    jw.beginObject( null );

    jw.addProperty( "firstName", "Betty", true );
    jw.addProperty( "lastName", "blow", true );
    jw.addProperty( "isMarried", "false", false );
    jw.addArray( "favColors", new Object[]{"Purple", "Green"}, true );

    jw.endObject();

    jw.endArray();


    strJson = jw.toString();

    System.out.println( strJson );

    jw.clear();

    jw.beginObject( null );

    jw.addProperty( "employees", null, false );

        // Simple object with an array of primitives
    jw.beginArray();

    jw.beginObject( null );
    jw.addProperty( "firstName", "Joe", true );
    jw.addProperty( "lastName", "blow", true );
    jw.addProperty( "isMarried", "true", false );
    jw.addArray( "favColors", new Object[]{"Red", "Blue"}, true );

    jw.endObject();

    jw.beginObject( null );

    jw.addProperty( "firstName", "Betty", true );
    jw.addProperty( "lastName", "blow", true );
    jw.addProperty( "isMarried", "false", false );
    jw.addArray( "favColors", new Object[]{ "Purple", "Green" }, true );
    jw.endObject();


    jw.endArray();

    jw.beginObject( "favEmployees" );
    jw.beginObject( "address" );
    jw.addProperty( "street", "Hawthorn", true );
    jw.addProperty( "city", "Brooklyn", true );
    jw.addProperty( "state", "New York", true );
    jw.endObject();
    jw.endObject();

    jw.endObject();

    strJson = jw.toString();

    System.out.println( strJson );

  }


  @Test
  public void testJson2Bean() throws Exception
  {
    String strJson = "{\"int\":[100,200,300]}";

    VwPrimitiveListWrapper lw = (VwPrimitiveListWrapper)VwJsonUtils.fromJson( strJson, VwPrimitiveListWrapper.class );

    List<Integer> list = lw.getInt();

    Assert.assertTrue( (list.get( 0 )instanceof Integer )  );

  }


  @Test
  public void testBeanToJson() throws Exception
  {
    // Test bean with just properties

    VwTestPersonEx person = new VwTestPersonEx( "Joe", "Doe", 23 );

    VwBeanToJson btj = new VwBeanToJson(  );

    String strJson = btj.serialize( person );

    System.out.println( strJson );

    VwJsonToBean jtb = new VwJsonToBean();



    // Make sure can convert this back to a bean
    VwTestPersonEx p1 = (VwTestPersonEx)jtb.deSerialize( strJson, VwTestPersonEx.class );

    Assert.assertNotNull( p1 );

    Assert.assertTrue( p1.getFirstName().equals( "Joe" ) );
    Assert.assertTrue( p1.getLastName().equals( "Doe" ) );
    Assert.assertTrue( p1.getAge() == 23 );
    Assert.assertTrue( p1.getIsSingle() == true );


    // Test a Person List

    List<VwTestPersonEx>listPersons = new ArrayList<VwTestPersonEx>(  );
    listPersons.add( person );
    listPersons.add( new  VwTestPersonEx( "Betty", "Doe", 33 ) );


    boolean fCol = VwBeanUtils.isCollectionType( listPersons );
    btj.setForceArray( true );

    String strPersonList = btj.serialize( listPersons );
    System.out.println( strPersonList );

    Class clsPerson = listPersons.getClass();

    String strClass = clsPerson.toString();
    //List<VwTestPersonEx> listPerson1 = (List<VwTestPersonEx>)jtb.deSerialize( strPersonList, VwTestPersonEx.class );

    Object objPerson = jtb.deSerialize( strPersonList, VwTestPersonList.class );
    // Test with containing object

    VwTestPersonAddress pa = new VwTestPersonAddress( "Betty", "Doe", 31 );
    VwTestAddress ta = new VwTestAddress( "Doe Street", "Doe City", "NY", false);
    pa.setVwTestAddress( ta );

    strJson = btj.serialize( pa );

    System.out.println( strJson );

    jtb = new VwJsonToBean();

   // Make sure can convert this back to a bean
    VwTestPersonAddress pa1 = (VwTestPersonAddress)jtb.deSerialize( strJson, VwTestPersonAddress.class );
    Assert.assertNotNull( pa1 );

    VwTestAddress ta1 = pa1.getVwTestAddress();
    Assert.assertNotNull( ta1 );
    Assert.assertTrue( ta1.getStreet().equals( "Doe Street" ) );
    Assert.assertTrue( ta1.getIsPrimary() == false );

   // Test with collections

    VwTestPersonAddressCollect pac = new VwTestPersonAddressCollect( "Betty", "Doe", 31 );

    List<VwTestAddress>listAddress = new ArrayList<VwTestAddress>(  );

    listAddress.add( ta );
    ta = new VwTestAddress( "Betty Street", "East Islip", "NY", true);

    List<String>listColors = new ArrayList<String>(  );
    listColors.add( "Red" );
    listColors.add( "Green" );
    listColors.add( "Blue" );

    ta.setPrimitives( listColors );

    List<VwTestVehicles>listVehicles = new ArrayList<VwTestVehicles>(  );
    VwTestVehicles tv = new VwTestVehicles( "car", "Acura" );
    listVehicles.add( tv );
    tv = new VwTestVehicles( "car", "Honda" );
    listVehicles.add( tv );
    ta.setVwTestVehicles( listVehicles );
    listAddress.add( ta );

    pac.setVwTestAddress( listAddress );

    strJson = btj.serialize( pac );

    System.out.println( strJson );

    jtb = new VwJsonToBean();
     // Make sure can convert this back to a bean
    VwTestPersonAddressCollect ca1 = (VwTestPersonAddressCollect)jtb.deSerialize( strJson, VwTestPersonAddressCollect.class );
    Assert.assertNotNull( ca1 );
    Assert.assertTrue( ca1.getFirstName().equals( "Betty" ) );
    Assert.assertTrue( ca1.getLastName().equals( "Doe" ) );
    Assert.assertTrue( ca1.getAge() == 31 );



    listAddress =  ca1.getVwTestAddress();
    Assert.assertNotNull( listAddress );

    Assert.assertTrue( listAddress.size() == 2 );

    Assert.assertTrue( listAddress.get( 0 ).getStreet().equals( "Doe Street" ) );
    Assert.assertTrue( listAddress.get( 0 ).getIsPrimary() == false );

    Assert.assertTrue( listAddress.get( 1 ).getStreet().equals( "Betty Street" ) );
    Assert.assertTrue( listAddress.get( 1 ).getIsPrimary() == true );

    Assert.assertTrue( listAddress.get( 1 ).getPrimitives().get( 1 ).equals( "Green" ) );


  }

  @Test
  public void testPrimitiveCollections()  throws Exception

  {
    VwJsonToBean jtb = new VwJsonToBean();
    VwTestPerson person = new VwTestPerson( "Joe", "Doe", 23 );

    List<Object>listColors = new ArrayList<Object>(  );
    listColors.add( "Red" );
    listColors.add( "Green" );
    listColors.add( "Blue" );

    person.setPrimitives( listColors );
    VwBeanToJson btj = new VwBeanToJson(  );

    String strJson = btj.serialize( person );

    System.out.println( strJson );
    // Make sure can convert this back to a bean
    VwTestPerson tp = (VwTestPerson)jtb.deSerialize( strJson, VwTestPerson.class );
    Assert.assertNotNull( tp );

    List<Object>li = tp.getPrimitives();
    Assert.assertNotNull( li );

    Assert.assertTrue( li.size() == 3 );


  }


  @Test
  public void testEscapeQuotes() throws Exception
  {
    String strJson = "{\"sex\":\"M\",\"occupation\":\"Musician/Software Architect\",\"title\":\"Project Manager\",\"aboutUser\":\"\"Vozz's!\"\"}";

    System.out.println( strJson );;

    // Make sure can convert this back to a bean
    VwJsonToBean jtb = new VwJsonToBean();
    VwTestPerson tp = (VwTestPerson)jtb.deSerialize( strJson, VwTestPerson.class );
    Assert.assertNotNull( tp );

  }
}
