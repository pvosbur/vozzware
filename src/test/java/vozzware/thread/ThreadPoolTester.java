package test.vozzware.thread;

import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*
============================================================================================

    Source File Name: 

    Author:           petervosburgh
    
    Date Generated:   2/22/16

    Time Generated:   4:22 PM

============================================================================================
*/
public class ThreadPoolTester
{

  @Test
  public void testPool() throws Exception
  {

    ExecutorService executor = Executors.newFixedThreadPool( 1 );

    for ( int x = 0; x < 100; x++  )
    {
      executor.execute( new TestWorkerThread( x ) );
    }


    //executor.awaitTermination( 5000, null  );

    while( !executor.isShutdown() )
    {
      ;
    }

    System.out.println( "Done");
  }
}
