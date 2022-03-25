package vozzware.db;

import com.vozzware.db.VwDatabase;
import com.vozzware.db.VwDbMgr;
import com.vozzware.util.VwLogger;
import org.junit.Test;

import java.util.ResourceBundle;

/*
============================================================================================

    Source File Name: 

    Author:           petervosburgh
    
    Date Generated:   2/16/16

    Time Generated:   7:29 AM

============================================================================================
*/
public class TestVwDatabaseMgr
{


  @Test
  public void testDbMgrOpenClose() throws Exception
  {
    ResourceBundle rb = ResourceBundle.getBundle( "resources.properties.vwdb" );

    VwDbMgr dbMgr = new VwDbMgr( "POSTGRES", "LOCAL", VwLogger.getInstance() );

    VwDatabase db = dbMgr.login( "aiweb", "file:${user.home}/.ai/dbAccess.txt" );


    dbMgr.close();

  }
}
