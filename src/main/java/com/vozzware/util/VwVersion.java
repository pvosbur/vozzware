package com.vozzware.util;

public class VwVersion
{

  /**
   * @param args
   */
  public static void main( String[] args )
  {
    try
    {
      VwResourceMgr.loadBundle( "com.vozzware.util.vwutil", true );
      System.out.println( "VwWorks Version: " + VwResourceMgr.getString( "VwWorks.Version" ));
    }
    catch( Exception ex )
    {
      ex.printStackTrace();
    }

  }

}
