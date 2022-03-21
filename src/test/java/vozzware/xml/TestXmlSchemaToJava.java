package test.vozzware.xml;

import com.vozzware.tools.VwDtdToJava;
import com.vozzware.xml.schema.tools.VwSchemaToJava;
import org.junit.Test;

import java.io.File;

/*
============================================================================================

    Source File Name: Transaction.java

    Author:           petervosburgh

    Date Generated:   6/9/17

    Time Generated:   1:35 PM

============================================================================================
*/
public class TestXmlSchemaToJava
{


  @Test
  public void testXmlSchemaToJava() throws Exception
  {
    String strPath = "/Users/petervosburgh/dev/VozzWorks/VozzWorks_3.0.3/resources/resources/docs/VwObjectSQLMapper.xsd";
    //String strPath = "/Users/petervosburgh/dev/VozzWorks/VozzWorks_3.0.3/resources/resources/docs/VwSqlMappingDocument.xsd";
    File fileSchema = new File( strPath );

    VwSchemaToJava stj = new VwSchemaToJava( fileSchema, "/Users/petervosburgh/dev/VozzWorks/VozzWorks_3.0.3/src",
                                             "com.vozzware.db.util", true, true, true );
    stj.process();
    return;
  }

  @Test
  public void  testGenObjectSqlMapping() throws Exception
  {
    String strPath = "/Users/petervosburgh/dev/VozzWorks/VozzWorks_3.0.3/resources/resources/docs/VwSqlMappingDocument.xsd";
    File fileSchema = new File( strPath );

    VwSchemaToJava stj = new VwSchemaToJava( fileSchema, "/Users/petervosburgh/dev/VozzWorks/VozzWorks_3.0.3/src",
                                             "com.vozzware.db.util", true, true, true );
    stj.process();
    return;

  }
} // end TestXmlSchemaToJava
