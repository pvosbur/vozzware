/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwOsServiceSpec.java

============================================================================================
*/


package com.vozzware.util;

/**
 *  The VwOsServiceSpec defines the standard class interface that specific platforms httpServices
 *  will implement.
 *
 *  @author ITC
 *  @version 1.0
*/

public abstract class VwOsServiceSpec
{

  // *** Service event type codes ***

  public static final int VW_ERROR = 1;                    //	Error event
  public static final int VW_WARNING =   2;	              //  Warning event
  public static final int VW_INFO = 3;                     //	Information event
  public static final int VW_AUDIT_SUSSUSS = 4;	          //	Success Audit event
  public static final int VW_AUDIT_FAIL = 5;               //	Failure Audit event

  // *** Service state codes ***

  public static final int VW_SERVICE_STOPPED  = 1;	        // The service is not running.
  public static final int VW_SERVICE_START_PENDING = 2;	  // The service is starting.
  public static final int VW_SERVICE_STOP_PENDING = 3;	    // The service is stopping.
  public static final int VW_SERVICE_RUNNING = 4;	        //The service is running.
  public static final int VW_SERVICE_CONTINUE_PENDING = 5; //	The service continue is pending.
  public static final int VW_SERVICE_PAUSE_PENDING = 6;    //	The service pause is pending.
  public static final int VW_SERVICE_PAUSED	= 7;          // The service is paused.

  // *** Error return codes ***
  public static final int VW_NO_ERROR = 0;

  /**
   * Super class Constructor
   *
   * @param strServiceName - The name of the service
   * @param strServiceDisplayName - The Display Name ( can contain spaces )
   * @param astrDepen - An array of other service names on which this service is dependent on
   */
  protected abstract void registerService( String[] astrArgs, String strServiceName, String strServiceDisplayName,
                                           String[] astrDepen ) throws Exception;

  // *** This begins execution of your service

  protected abstract boolean onServiceStart();


  // *** This shuts down your service

  protected abstract void onServiceStop();


  // *** Put mesage in the NT message log

  public abstract void addToMessageLog( int nErrType, String strMsg );

  public abstract  boolean reportStatusToSCMgr( long lCurrentState,
                                                long lExitCode,
                                                long lWaitHint );


  public abstract boolean isValid();

  public abstract boolean start();

  public abstract void stop();

  // *** Get Last Error desc.

  public abstract String getErrDesc();

  // *** Get Name of Service

  public abstract String getServiceName();


  // *** Get Display Name
  public abstract String getDisplayName();


} // end class VwServiceSpec{}


// *** End of VwServiceSpec.java ***



