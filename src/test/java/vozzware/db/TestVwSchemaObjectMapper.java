package vozzware.db;

import com.restfb.types.Url;
import com.vozzware.db.VwDatabase;
import com.vozzware.db.VwDbMgr;
import com.vozzware.db.VwSchemaObjectMapper;
import com.vozzware.util.VwFileUtil;
import com.vozzware.util.VwLogger;
import com.vozzware.util.VwResourceMgr;
import org.junit.Test;
import org.springframework.util.Assert;

import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;

/*
============================================================================================

    Source File Name: 

    Author:           petervosburgh
    
    Date Generated:   2/16/16

    Time Generated:   7:29 AM

============================================================================================
*/
public class TestVwSchemaObjectMapper
{


  @Test
  public void testRunSchemaMapper() throws Exception
  {

    String[] astrArgs = new String[]{ "TestWebDAO.xml", "-g", "-o", "-p", "schemaMapper"};

    VwSchemaObjectMapper.main( astrArgs );

    return;



  }
}
