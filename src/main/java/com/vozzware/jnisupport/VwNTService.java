/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwNTService.java

============================================================================================
*/

package com.vozzware.jnisupport;

/**
 *  The VwNTService class is an abstract base class for WIN32 Java applications that want to
 *  run as an NT service. This class hides the WIN32 API details for installing, registering
 *  and running Java Applications as an NT Service.
 *
 *  @author ITC
 *  @version 1.0
*/
public abstract class VwNTService extends com.vozzware.util.VwOsServiceSpec
{

  private String        m_strErrDesc;	        // Error desc. string

  private boolean       m_fIsValid;		// Object validity flag
  private boolean	m_fDebug;		// Debug Mode flag

  private String	m_strServiceName;	// Name of Serive
  private String	m_strDisplayName;	// Service display name

  private   int         m_nServiceHandle;     // Return service handle from native call

  /**
   * Native method that starts, installs or removes a service depending on the command line
   * args passed through the main function in the C/C++ program for the win32 exe
   *
   * @param astrArgsString Any command line args passed through function main. The allowable
   * arguments are:
   * -i - Installs the service
   * -r - Removes the service
   * -d - Runs the service in debug mode  (This option does not use the NT service manager)
   * or no args which is the case when the Nt service manager starts the win32 executable
   *
   * @param strServiceName - The name of the Service to be started, installed or removed depending
   * on the command line args
   *
   * @param strServiceDisplayName - The service display name ( how its viewed in the NT httpServices
   * dialog box
   * @param astrDependencies an array of any service dependencies that must be started prior to
   * this service
   *
   * @return a handle to the service which must be used for all other service functions
   */
  private native int setupService( String[] astrArgsString, String strServiceName,
                                   String strServiceDisplayName, String[] astrDependencies );

  /**
   * Starts the NT service. This will ultimatly call the onServiceStart virtual method that
   * is defined in the sub class defining the actual service
   *
   * @param nServiceHandle The service handle returned from the setupService native method
   */
  private native boolean startService( int nServiceHandle );

  /**
   * Adds a message to the NT Event log
   *
   * @param nServiceHandle The service handle returned from the setupService native method
   * @param One of the service event type code constants defined in the VwNTServiceSpec class
   * @param strMsg The Message to be put in the log
   */
  private native void addToMessageLog( int nServiceHandle, int nErrType, String strMsg );

  /**
   * Native nethod for communicating with the NT event message log
   *
   * @param nServiceHandle The service handle returned from the defineService native method
   * @param lCurrentState One of the Service state constatnr flags define in VwNTServiceSpec
   * @param lExitCode The exit code state 0 = no error
   * @param lWaitHint The time in millisecs the NT service control manager should wait for
   * a further response
   */
  private native boolean reportStatusToSCMgr( int nServiceHandle,long lCurrentState,
                                              long lExitCode, long lWaitHint );

  /**
   * Super class Constructor
   *
   * @param strServiceName - The name of the service
   * @param strServiceDisplayName - The Display Name ( can contain spaces )
   * @param astrDepen - An array of other service names on which this service is dependent on
   */
  protected void registerService( String[] astrArgs, String strServiceName, String strServiceDisplayName,
                                  String[] astrDepen ) throws Exception
  {
    m_strServiceName = strServiceName;
    m_strDisplayName = strServiceDisplayName;
    m_fIsValid = true;
    m_fDebug = false;
    m_strErrDesc = "";

    m_nServiceHandle = setupService( astrArgs, strServiceName, strServiceDisplayName, astrDepen );

    if ( m_nServiceHandle == 0 )
      throw new Exception( "Could Not Start Service " + strServiceName );

  } // end VwService()


  /**
   * Super class Constructor
   *
   * @param astrArgs Args passed from the commnad line
   * @param strServiceName - The name of the service
   * @param strServiceDisplayName - The Display Name ( can contain spaces )
   * @param astrDepen - An array of other service names on which this service is dependent on
   */
  protected VwNTService( String[] astrArgs, String strServiceName,
                          String strServiceDisplayName, String[] astrDepen ) throws Exception
  {
    registerService( astrArgs, strServiceName, strServiceDisplayName, astrDepen );

  } // end VwNTService()


  /**
   * Starts the Service
   *
   * @return true if the service started successfully
   */
  public boolean start()
  { return startService( m_nServiceHandle ); }

  /**
   * Do nothing here
   */
  public void stop()
  { ; }


  /**
   * Adds a message to the NT Event log
   *
   * @param One of the service event type code constants defined in the VwNTServiceSpec class
   * @param strMsg The Message to be put in the log
   */
  public void addToMessageLog( int nErrType, String strMsg )
  { addToMessageLog( m_nServiceHandle, nErrType, strMsg ); }

  /**
   * Native nethod for communicating with the NT event message log
   *
   * @param lCurrentState One of the Service state constatnr flags define in VwNTServiceSpec
   * @param lExitCode The exit code state 0 = no error
   * @param lWaitHint The time in millisecs the NT service control manager should wait for
   * a further response
   */
  public boolean reportStatusToSCMgr( long lCurrentState,
                                         long lExitCode, long lWaitHint )
  { return reportStatusToSCMgr( m_nServiceHandle, lCurrentState, lExitCode, lWaitHint ); }


  // *** These methods must be overridden by derived class for service functionality

  /**
   * This method is called by the service manager to indicate that the service should be
   * started. This method CANNOT block so the actual service needs to be started in another
   * thread that is launched in this method.
   */
   protected abstract boolean onServiceStart();


  /**
   * This method is called by the service manager to indicate that the service should
   * shut down. This method MUST Not return until the httpServices main thread shuts down.
   */
  protected abstract void onServiceStop();


  /**
   * Returns the object validity state
   */
  public boolean isValid()
  { return m_fIsValid; }


  /**
   * Get Last Error desc.
   *
   * @return a String containing the last error description
   */
  public String getErrDesc()
  { return m_strErrDesc; }

  /**
   * Get Name of Service
   *
   * @return a String containing the name of the service
   */
  public final String getServiceName()
  { return m_strServiceName; }


  /**
   * Get Display Name
   *
   * @return a String containingg the servoce display name
   */
  public final String getDisplayName()
  { return m_strDisplayName; }


} // end class VwNTService{}


// *** End VwNTService.java ***



