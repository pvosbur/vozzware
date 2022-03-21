package test.vozzware.thread;

/*
============================================================================================

    Source File Name: 

    Author:           petervosburgh
    
    Date Generated:   2/22/16

    Time Generated:   4:18 PM

============================================================================================
*/
public class TestWorkerThread implements Runnable
{

  private int m_nSeqNbr;
  public TestWorkerThread( int nSeqNbr )
  {
    m_nSeqNbr = nSeqNbr;

  }
  public void run()
  {
    System.out.println( Thread.currentThread().getName() + " " + m_nSeqNbr );
  }
}
