/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwDocChangeMonitor.java

============================================================================================
*/

package com.vozzware.util;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

/**
 * This class monitors documents in a background thread and invokes the callback method specified when
 * a document change has been detected.
 * <br> This is most useful for rebuilding cached data without having to stop and start a JVM.
 * 
 * @author P. VosBurgh
 *
 */
public class VwDocChangeMonitor extends Thread
{
  private class DocEntry
  {
    File    m_fileDoc;          //File representing the doc location
    long    m_lLastChanged;     // last modify time on file
    Method  m_mthdNotify;       // Callback method
    boolean m_fTakesStringArg;
    Object  m_objNotify;
    
    DocEntry( File fileDoc, Object objNotify, Method mthdNotify, boolean fTakesStringArg )
    {
      m_fileDoc = fileDoc;
      m_lLastChanged = m_fileDoc.lastModified();
      m_mthdNotify = mthdNotify;
      m_fTakesStringArg = fTakesStringArg;
      m_objNotify = objNotify;
      
    }
  } // end class DocEntry
  
  private long m_lMonitorInterval;
  
  private boolean m_fQuit;
  
  private Map m_mapDocs = Collections.synchronizedMap( new HashMap());
  
  
  
  /**
   * Constructor
   * 
   * @param lMonitorInterval The monitor level in seconds that this object will watch the documents
   */
  public VwDocChangeMonitor( long lMonitorInterval ) throws Exception
  {
    m_lMonitorInterval = lMonitorInterval * 1000;
    VwResourceMgr.loadBundle( "com.vozzware.util.vwutil", (Locale)null, false );
    m_fQuit = false;
    
    
  }
  
  /**
   * Adds a file to be monitered
   * @param fileDoc The file tp monitor
   * @param objNotify The object instance of the class method to be notified on changes 
   * @param strNotifyMethod The public method name to get invoked when change is detected.
   * <br> NOTE! this must be a public method which takes zero or one String argument.
   * <br> The method will test for a String argument first and if not found, a zero arg method.
   * <br> If the String argument is detected, its assumed to take the name (complete absolute path) of the
   * <br> file that was modified.
   * 
   * @throws Exception if the file does not exist, or the method name passed is not valid or public
   */
  public void addDocMonitor( File fileDoc, Object objNotify, String strNotifyMethod ) throws Exception
  {
    
    Method mthdNotify = null;
    
    if ( !fileDoc.exists() )
      throw new Exception( VwResourceMgr.getString(  "com.vozzware.util.vwutil:VwUtil.docNotFound", "%1", fileDoc.getAbsolutePath() ) );
    
    boolean fTakesStringArg = false;
    
    Class clsNotify = objNotify.getClass();
    
    try
    {
      mthdNotify = clsNotify.getMethod( strNotifyMethod, new Class[]{String.class } );
      fTakesStringArg = true;
    }
    catch( Exception ex )
    {
      try
      {
        mthdNotify = clsNotify.getMethod( strNotifyMethod, null );
      }
      catch( Exception ex2 )
      {
        throw new Exception( VwResourceMgr.getString( "com.vozzware.util.vwutil:VwUtil.methodNotFound", "%",
                             new String[] {clsNotify.getName(), strNotifyMethod } ));
        
      }
    }
    
    // All good if we get here
    m_mapDocs.put( fileDoc, new DocEntry(fileDoc, objNotify, mthdNotify, fTakesStringArg ));
    
    
  }
  
  
  /**
   * Kills the monitor
   *
   */
  public void kill()
  {
    m_fQuit = true;
    this.interrupt();
    
  } // end kill
  
  public void run()
  {
    while( !m_fQuit )
    {
      checkDocsForChanges();
      
      try
      {
        Thread.sleep( m_lMonitorInterval );
      }
      catch( InterruptedException tie )
      {
        if ( m_fQuit )
          return;
        
        throw new RuntimeException( tie.toString() );
      }
      catch( Exception ex )
      {
        throw new RuntimeException( ex.toString() );
      }
      
    }
  }
  
  /**
   * Check each document for changes
   * 
   */
  private void checkDocsForChanges()
  {
    for ( Iterator iDocs = m_mapDocs.values().iterator(); iDocs.hasNext(); )
    {
      DocEntry docEntry = (DocEntry)iDocs.next();
      
      long lModTime = docEntry.m_fileDoc.lastModified();
      
      if ( lModTime != docEntry.m_lLastChanged )
      {
        docEntry.m_lLastChanged = lModTime;
        
        Object[] aParams = null;
        
        if ( docEntry.m_fTakesStringArg )
          aParams = new Object[]{ docEntry.m_fileDoc.getAbsolutePath() };
        
        try
        {
          docEntry.m_mthdNotify.invoke( docEntry.m_objNotify, aParams );
        }
        catch( Exception ex )
        {
          throw new RuntimeException( ex.toString() );
          
        }
        
      }
    }
  }
  public static void main( String[] args )
  {
    try
    {
      VwDocChangeMonitor chm = new VwDocChangeMonitor( 60 );
      chm.addDocMonitor( new File( "/mbrManageOra.sql"), VwStack.class, "peek");
      
      return;
      
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
  } // end main()
} // end class VwDocChangeMonitor{}

// *** End of VwDocChangeMonitor.java ***

