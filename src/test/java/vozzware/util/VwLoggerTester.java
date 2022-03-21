package test.vozzware.util;

import com.vozzware.util.VwLogger;
import org.apache.logging.log4j.Level;
import org.junit.Test;

/*
============================================================================================

    Source File Name: 

    Author:           petervosburgh
    
    Date Generated:   5/15/15

    Time Generated:   6:45 AM

============================================================================================
*/
public class VwLoggerTester
{
    @Test
    public void testRollingLogs() throws Exception
    {

      //VwLogger logger1 = VwLogger.getInstance( "junit1.properties" );
      VwLogger logger = VwLogger.getInstance( "junitDb.properties" );

      logger.clearLog();

      //logger1.info( "Info THis");
      //logger1.error( this.getClass(), "MyError");


      logger.info( this.getClass(), "My INFO Test Msg");
      Level level = logger.getLevel();
      logger.info( "First Test ");
      logger.debug( "DEBUG First Test " );

      logger.setLevel( Level.DEBUG );

      level = logger.getLevel();

      logger.clearLog();

      logger.debug( "DEBUG First Test ");

      for ( int x = 0; x < 20; x++ )
      {
          logger.debug( this.getClass(), "User Id:  100  -- This is test line: " + x );
      }

    }


  @Test
  public void testError()
  {

    VwLogger logger1 = null;

    String strTest = null;

    try
    {
      logger1 = VwLogger.getInstance( "junit.properties" );
      logger1.debug( getClass(), "MUTHA FUKKA");
      strTest.length();

    }
    catch( Exception ex )
    {
      logger1.fatal( getClass(), ex.toString(), ex );

    }
  }

}
