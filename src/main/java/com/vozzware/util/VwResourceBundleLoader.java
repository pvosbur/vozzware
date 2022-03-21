package com.vozzware.util;


/*
============================================================================================

    Source File Name: Transaction.java

    Author:           petervosburgh
    
    Date Generated:   9/22/14

    Time Generated:   5:37 PM

============================================================================================
*/
public class VwResourceBundleLoader
{

  public void setBundleList( String strBundleList )
  {

    VwDelimString dlms = new VwDelimString( strBundleList );

    while( dlms.hasMoreElements() )
    {

      String strBundleName = dlms.getNext();
      try
      {
        VwResourceMgr.loadBundle( strBundleName, true );
      }
      catch ( Exception ex )
      {
        throw new RuntimeException( ex.toString() );
      }

    } // end while()

  }

}
