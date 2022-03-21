/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwOsService.java

============================================================================================
*/

package com.vozzware.util;

/**
 *  The VwOsService class defines generic a
 *  will implement.
 *
 *  @author ITC
 *  @version 1.0
*/

public abstract class VwOsService extends VwOsServiceSpec
{

  private String      m_strServiceName;           // Name of the Service
  private String      m_strServiceDisplayName;    // Serice display name may contain spaces

  /**
   * Super class Constructor
   *
   * @param strServiceName - The name of the service
   * @param strServiceDisplayName - The Display Name ( can contain spaces )
   * @param astrDepen - An array of other service names on which this service is dependent on
   */
  protected  void registerService( String[] astrArgs, String strServiceName,
                                   String strServiceDisplayName,
                                   String[] astrDepen ) throws Exception
  {

    m_strServiceName = strServiceName;
    m_strServiceDisplayName = strServiceDisplayName;
  }

  // *** This begins execution of your service

  protected abstract boolean onServiceStart();


  // *** This shuts down your service

  protected abstract void onServiceStop();


  // *** Put mesage in the NT message log

  public void addToMessageLog( int nErrType, String strMsg )
  {
    System.out.println( strMsg );

  }

  public boolean reportStatusToSCMgr( long lCurrentState,
                                      long lExitCode,
                                      long lWaitHint )
  {
    return true; // No op

  }

  public boolean isValid()
  { return true; }

  public void  stop()
  { onServiceStop(); }

  public boolean start()
  { return onServiceStart(); }

  // *** Get Last Error desc.

  public String getErrDesc()
  { return ""; }

  // *** Get Name of Service

  public String getServiceName()
  { return m_strServiceName; }


  // *** Get Display Name
  public String getDisplayName()
  { return m_strServiceDisplayName; }


} // end class VwOsService{}{}


// *** End of VwOsService.java ***



