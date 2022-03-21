package test.vozzware.util;

import com.vozzware.util.VwLogger;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/*
============================================================================================

    Source File Name: 

    Author:           petervosburgh
    
    Date Generated:   1/23/16

    Time Generated:   9:35 AM

============================================================================================
*/
public class VwLoggerJMXTester
{

  private static ApplicationContext s_ctx;

  @Test
  public void loadSpring() throws Exception
  {
    long lAvailMem = getAvailableMemory();

    long lFree = Runtime.getRuntime().freeMemory();
    long lTotal = Runtime.getRuntime().totalMemory();
    long lMax = Runtime.getRuntime().maxMemory();

    s_ctx = new ClassPathXmlApplicationContext( "/main/resources/spring/test-context.xml" );


    lAvailMem = getAvailableMemory();

    lFree = Runtime.getRuntime().freeMemory();

    VwLogger logger = (VwLogger) s_ctx.getBean( "logger");

    VwLogger logger1 = VwLogger.getInstance( "junit1.properties" );
    logger1.debug( this.getClass(), "Logger one test");
    VwLogger logger2 = VwLogger.getInstance( "junit2.properties" );

    logger2.debug( this.getClass(), "Logger Two test");

    logger.info( "Test One" );
    logger.info( "Test Two" );

    /*
    logger.clearLog();
    logger.info( "Test Three" );

    logger1.clearLog();
    logger2.clearLog();

    */

    Thread.sleep( 30000 );
    for ( int x = 0; x < 15; x++ )
    {
      System.out.println( "x is " + x);
      logger.debug( "Junit Debug: " + x );
      Thread.sleep( 10000 );

    }

    return;

  }


  long getAvailableMemory()
  {
    Runtime runtime = Runtime.getRuntime();
    long totalMemory = runtime.totalMemory(); // current heap allocated to the VM process
    long freeMemory = runtime.freeMemory(); // out of the current heap, how much is free
    long maxMemory = runtime.maxMemory(); // Max heap VM can use e.g. Xmx setting
    long usedMemory = totalMemory - freeMemory; // how much of the current heap the VM is using
    long availableMemory = maxMemory - usedMemory; // available memory i.e. Maximum heap size minus the current amount used
    return availableMemory;
  }

}
