package test.vozzware.xml;

import com.vozzware.util.VwResourceStoreFactory;
import com.vozzware.xml.VwXmlDataObj;
import com.vozzware.xml.VwXmlElement;
import com.vozzware.xml.VwXmlToXmlDataObj;
import junit.framework.Assert;
import org.junit.Test;

import java.net.URL;
import java.util.List;

/**
 * Tester for the VwXmlToXmlDataObj process
 */
public class TestXmlToXmlDataObj
{

  @Test
  public void testXmlToXmlDataObj() throws Exception
  {

    URL urlDoc = VwResourceStoreFactory.getInstance().getStore().getDocument( "XmlToDataObjTest.xml" );

    VwXmlToXmlDataObj xtd = new VwXmlToXmlDataObj();

    VwXmlDataObj xmlRoot = xtd.parse( urlDoc, false  );

    List<VwXmlDataObj>listChildren = xmlRoot.getChildren();

    Assert.assertNotNull( listChildren );

    List<VwXmlDataObj>listResult = xmlRoot.find( "book") ;
    Assert.assertNotNull( listResult );
    Assert.assertTrue( listResult.size() == 2 );


    listResult = xmlRoot.find( "author") ;
    Assert.assertNotNull( listResult );

    String strAuthor = listResult.get( 0 ).getString( "author");
    Assert.assertNotNull( strAuthor );
    Assert.assertTrue( strAuthor.equals( "Dr. Ruth" ) );

    String strType = listResult.get( 0 ).getElement( 0, "booktype" ).getValue();
    Assert.assertNotNull( strType );
    Assert.assertTrue( strType.equals( "HardCover" ) );


    strType = listResult.get( 0 ).getElement( 1, "booktype" ).getValue();
    Assert.assertNotNull( strType );
    Assert.assertTrue( strType.equals( "Paperback" ) );


    String  strOrderAttr = listResult.get( 0 ).getElement( 1, "booktype" ).getAttribute( "order" );
    Assert.assertNotNull( strOrderAttr );
    Assert.assertTrue( strOrderAttr.equals( "special" ) );

    listResult = xmlRoot.find( "links") ;
    Assert.assertNotNull( listResult );
    Assert.assertTrue( listResult.size() == 2 );

    List< VwXmlElement> listLinks = listResult.get( 0 ) .getElements( "link" );
    Assert.assertNotNull(listLinks );

    String strUrl = listLinks.get( 0 ).getAttribute( "url" );
    Assert.assertNotNull( strUrl );
    Assert.assertTrue( strUrl.equals( "http://book1" ) );

    listLinks = listResult.get( 1 ) .getElements( "link" );
    Assert.assertNotNull(listLinks );

    strUrl = listLinks.get( 0 ).getAttribute( "url" );
    Assert.assertNotNull( strUrl );
    Assert.assertTrue( strUrl.equals( "http://amazon.com" ) );

    strUrl = listLinks.get( 1 ).getAttribute( "url" );
    Assert.assertNotNull( strUrl );
    Assert.assertTrue( strUrl.equals( "http://borders.com" ) );


  }
}
