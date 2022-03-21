/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwServiceFlags.java

Create Date: Apr 11, 2006
============================================================================================
*/

package com.vozzware.xml;              // The package this class belongs to

import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;


/**
 * This class manages the flags that represent the state if a service. It's methods test
 * and set the defined flag states allowable for a service.
 *
 */
public class VwServiceFlags
{
  private short               m_sFlags;             // Holds the flag combinations

  private AttributesImpl      m_listAttr = null;    // Current flag attribute object

  // Flag constants
  private static final short  EOD = 0x0001;         // End of Data flag
  private static final short  MORE_DATA = 0x0002;   // More Data flag
  private static final short  TRANSACTION = 0x0004; // Transaction flag
  private static final short  COMMIT = 0x0008;      // Commit the transaction
  private static final short  PERSIST = 0x0010;     // Persistent connection flag
  private static final short  CLOSE = 0x0020;       // Close connection flag
  private static final short  CANCEL_SERVICE = 0x0040;  // Cancel current runnning service
  private static final short  ROLLBACK_TRAN = 0x0080;   // Cancel Transaction -- do a rollback
  private static final short  ASYNC = 0x0100;       // Response is deliverd async as a callback or email
  private static final short  WAIT_FOR_CLIENT = 0x0200;  // Waiting for client to signal next data block
  private static final short  ERROR_DATA = 0x0400;  // Content in data object is error data


  /**
   * Constant for the service attribute key
   */
  public static final String  ITCSVCFLAGS = "VwFlags";


  /**
   * Constant for the service name data object key
   */
  public static final String  ITCSVCATTR = "ITCSVCATTR";


  /**
   * Constant for the row block attribute
   */
  public static final String  ITCROWBLOCK = "VwBlockCount";

  /**
   * Constant for the max rows attribute
   */
  public static final String  ITCMAXROWS= "VwMaxRows";


  /**
   * Constant for the service name data object key
   */
  public static final String  ITCSVCERROR = "ITCERR";

  
  /**
   * Constant for the service name data object key
   */
  public static final String  ITCTRANKEY = "ITCTranKey";
  
  /**
   * Default Constructor - All flag values cleared
   *
   * @param sFlags Initializer with set flag values
   */
  public VwServiceFlags()
  { m_sFlags = 0; }

  /**
   * Constructor that sets the flags from the constructor param
   *
   * @param sFlags Initializer with set flag values
   */
  public VwServiceFlags( short sFlags )
  { m_sFlags = sFlags; }


  /**
   * Constructor that sets flags from the flag attribute in the data object.
   *
   * @param sFlags Initializer with set flag values
   */
  public VwServiceFlags( VwServiceable sobj )
  { set( sobj, (short)-1 );  } // end VwServiceFlags()


  /**
   * Update flag value in the VwAttribute if attribute exists
   */
  private void update()
  {
    if ( m_listAttr != null )
    {
      int ndx = m_listAttr.getIndex( ITCSVCFLAGS );
      if ( ndx < 0 )
        m_listAttr.addAttribute( "", ITCSVCFLAGS, ITCSVCFLAGS, "CDATA", String.valueOf( m_sFlags ) );
      else
        m_listAttr.setValue( ndx, String.valueOf( m_sFlags ) );
    }

   } // end update()


  /**
   * Updates the flag values
   *
   * @param nNewFlags The new flag values
   *
   */
  public void set( short sNewFlags )
  { m_sFlags = sNewFlags; update(); }


  public void setAttributes( AttributesImpl attrs )
  { m_listAttr = attrs; }
  
  
  /**
   * Updates the flag values
   *
   * @param strNewFlags The new flag values
   *
   */
  public void set( String strNewFlags )
  {
    if ( strNewFlags == null )
      m_sFlags = 0;
    else
      m_sFlags = Short.parseShort( strNewFlags );

    update();

  } // end set()



  /**
   * Updates the flag values from flags attribute in the dataobject
   *
   * @param sobj The VwServiceable to set the flags from
   *
   */
  public void set( VwServiceable sobj )
  { set( sobj, (short)-1 ); }


  /**
   * Get the current flag value
   *
   * @return The current flag valu
   */
  public int get()
  { return m_sFlags; }



  /**
   * Gets the attribute value requested id there is a serviceable object assocaited with
   * this object through a prior set( VwServiceable ) call. This operation is ignored if no
   * current servicable was set
   *
   * @param strAttrName The name of the attribute to get
   *
   * @return a String containingthe attribute value or null if no attribute exists or
   * if no serviceable object is associated with this object
   */
  public String getAttribute( String strAttrName )
  {
    if ( m_listAttr == null )
      return null;

    return m_listAttr.getValue( strAttrName );

  } // end getAttribute()



  /**
   * Gets the attribute value requested id there is a serviceable object assocaited with
   * this object through a prior set( VwServiceable ) call. This operation is ignored if no
   * current servicable was set
   *
   * @param strAttrName The name of the attribute to get
   *
   * @return a String containingthe attribute value or null if no attribute exists or
   * if no serviceable object is associated with this object
   */
  public static String getStatic( VwServiceable sobj, String strAttrName )
  {
    Attributes listAttr = sobj.getServiceAttributes();

    if ( listAttr == null )
      return null;

    return listAttr.getValue( strAttrName );

  } // end getStatic()


  /**
   * Gets the current flag value for a specific data object instance
   */
  public static short get( VwServiceable sobj )
  {
    if ( sobj == null )
      return 0;

    Attributes listAttr = sobj.getServiceAttributes();

    if ( listAttr == null )
      return 0;

    String attrVal = listAttr.getValue( ITCSVCFLAGS );

    if ( attrVal == null )
      return 0;

    return Short.parseShort( attrVal );

  } // end get


  /**
   * Sets the current flag value for a specific data object instance
   */
  public static void setFlags( VwServiceable sobj, int nFlags )
  {
    if ( sobj == null )
      return;

    String strFlags = String.valueOf( nFlags );

    AttributesImpl listAttr = (AttributesImpl)sobj.getServiceAttributes();

    if ( listAttr == null )
    {
      listAttr = new AttributesImpl();
      sobj.setServiceAttributes( listAttr );
    }

    String  strAttrVal = listAttr.getValue( ITCSVCFLAGS );

    if ( strAttrVal  == null )
      listAttr.addAttribute( "", ITCSVCFLAGS, ITCSVCFLAGS, "CDATA", strFlags );
    else
    {
      int ndx = listAttr.getIndex( ITCSVCFLAGS );

      listAttr.setValue( ndx, strFlags );

    }

  } // end set


  /**
   * Adds the attribute to an existing attribute list for the servicable object. A new
   * attribute list will be created if this is the first attribute to be added
   *
   * @param sobj The VwServiable object that will receive the attribute
   * @param attr The attribute to add
   */
  public static void setStatic( VwServiceable sobj, String strName, String strVal )
  {
    if ( sobj == null )
      return;

    AttributesImpl listAttr = (AttributesImpl)sobj.getServiceAttributes();

    if ( listAttr == null )
    {
      listAttr = new AttributesImpl();
      sobj.setServiceAttributes( listAttr );
    }

    int ndx = listAttr.getIndex( strName );

    if ( ndx < 0 )
      listAttr.addAttribute( "", strName, strName, "CDATA", strVal );
    else
      listAttr.setValue( ndx,  strVal );

  } // end setStatic

  /**
   * Sets the current flag value for a specific data object instance
   */
  public void set( VwServiceable sobj, short sFlags )
  {
    if ( sobj == null )
      return;

    if ( sFlags != -1 )
      m_sFlags = sFlags;

    m_listAttr = (AttributesImpl)sobj.getServiceAttributes();

    if ( m_listAttr == null )
    {
      m_listAttr = new AttributesImpl();
      sobj.setServiceAttributes( m_listAttr );
    }


    String strAttrVal = m_listAttr.getValue( ITCSVCFLAGS );

    if ( strAttrVal == null )
    {
      m_sFlags = 0;
      m_listAttr.addAttribute( "", ITCSVCFLAGS, ITCSVCFLAGS, "CDATA", "0" );
    }
    else
    {
      if ( sFlags == -1 )
        m_sFlags = Short.parseShort( strAttrVal );
      else
        strAttrVal = String.valueOf( sFlags );

      int ndx = m_listAttr.getIndex( ITCSVCFLAGS );

      m_listAttr.setValue( ndx, strAttrVal );

    } // end else

  } // end set



  /**
   * Clears all flags
   */
  public void clear()
  { m_sFlags = 0; update(); }


  /**
   * Converts flags to a string
   */
  public String toString()
  { return String.valueOf( m_sFlags ) ; }


  /**
   * Sets the end of data flag to on if fSet is true else the flag is cleared
   */
  public void setEod( boolean fSet )
  {
    if ( fSet )
      m_sFlags |= EOD;
    else
      m_sFlags &= ~EOD;

    update();

  } // end setEod()

  /**
   * Tests the flags for the end of data state
   *
   * @return true if the end of data flag is set else false is returned
   */
  public boolean isEod()
  { return (m_sFlags & EOD) == EOD; }


  /**
   * Sets the more data flag (MORE_DATA) to on if fSet is true else the flag is cleared
   */
  public void setMoreData( boolean fSet )
  {
    if ( fSet )
      m_sFlags |= MORE_DATA;
    else
      m_sFlags &= ~MORE_DATA;

    update();

  } // end setMoreData()

  /**
   * Tests the flags for more data state
   *
   * @return true if the end of more data  flag is set else false is returned
   */
  public boolean hasMoreData()
  { return (m_sFlags & MORE_DATA) == MORE_DATA; }


  /**
   * Sets the transaction flag to on if fSet is true else the flag is cleared
   */
  public void setTransaction( boolean fSet )
  {
    if ( fSet )
      m_sFlags |= TRANSACTION;
    else
      m_sFlags &= ~TRANSACTION;

    update();

  } // end setTransaction()

  /**
   * Tests the flags for transaction state
   *
   * @return true if the transaction flag is set else false is returned
   */
  public boolean isTransaction()
  { return (m_sFlags & TRANSACTION) == TRANSACTION; }



  /**
   * Sets the commit transaction flag to on if fSet is true else the flag is cleared
   */
  public void setCommitTransaction( boolean fSet )
  {
    if ( fSet )
      m_sFlags |= COMMIT;
    else
      m_sFlags &= ~COMMIT;

    update();

  } // end setCommit()

  /**
   * Tests the flags for commit transaction state
   *
   * @return true if the commit transaction flag is set else false is returned
   */
  public boolean isCommitTransaction()
  { return (m_sFlags & COMMIT) == COMMIT; }


  /**
   * Sets the connection persist flag to on if fSet is true else the flag is cleared
   */
  public void setPersistentConnection( boolean fSet )
  {
    if ( fSet )
      m_sFlags |= PERSIST;
    else
      m_sFlags &= ~PERSIST;

    update();

  } // end setPersist()

  /**
   * Tests the flags for connection persist state
   *
   * @return true if the connection persist flag is set else false is returned
   */
  public boolean isPersistentConnection()
  { return (m_sFlags & PERSIST) == PERSIST; }


  /**
   * Sets the close connection flag to on if fSet is true else the flag is cleared
   */
  public void setCloseConnection( boolean fSet )
  {
    if ( fSet )
      m_sFlags |= CLOSE;
    else
      m_sFlags &= ~CLOSE;

    update();

  } // end setCloseConnection()

  /**
   * Tests the flags for connection persist state
   *
   * @return true if the connection persist flag is set else false is returned
   */
  public boolean isCloseConnection()
  { return (m_sFlags & CLOSE) == CLOSE; }


  /**
   * Sets the cancel service flag to on if fSet is true else the flag is cleared
   */
  public void setCancelService( boolean fSet )
  {
    if ( fSet )
      m_sFlags |= CANCEL_SERVICE;
    else
      m_sFlags &= ~CANCEL_SERVICE;

    update();

  } // end setCancelService()

  /**
   * Tests the flags for cancel service state
   *
   * @return true if the cancel service flag is set else false is returned
   */
  public boolean isCancelService()
  { return (m_sFlags & CANCEL_SERVICE) == CANCEL_SERVICE; }


  /**
   * Sets the rollback transaction flag to on if fSet is true else the flag is cleared
   */
  public void setRollbackTransaction( boolean fSet )
  {
    if ( fSet )
      m_sFlags |= ROLLBACK_TRAN;
    else
      m_sFlags &= ~ROLLBACK_TRAN;

    update();

  } // end setRollbackTransaction()

  /**
   * Tests the flags for rollback transaction state
   *
   * @return true if the rollback transactione flag is set else false is returned
   */
  public boolean isRollbackTransaction()
  { return (m_sFlags & ROLLBACK_TRAN) == ROLLBACK_TRAN; }


  /**
   * Sets the async flag to on if fSet is true else the flag is cleared
   */
  public void setAsync( boolean fSet )
  {
    if ( fSet )
      m_sFlags |= ASYNC;
    else
      m_sFlags &= ~ASYNC;

    update();

  } // end setAsync()

  /**
   * Tests the flags for async state
   *
   * @return true if the async flag is set else false is returned
   */
  public boolean isAsync()
  { return (m_sFlags & ASYNC) == ASYNC; }


  /**
   * Sets the waitingFornectBlockRequest flag to on if fSet is true else the flag is cleared
   */
  public void setWaitingForNextBlockRequest( boolean fSet )
  {
    if ( fSet )
      m_sFlags |= WAIT_FOR_CLIENT;
    else
      m_sFlags &= ~WAIT_FOR_CLIENT;

    update();

  } // end setAsync()

  /**
   * Tests the flags for waitingForNextBlockRequest state
   *
   * @return true if the waitingForNextBlockRequest flag is set else false is returned
   */
  public boolean waitingForNextBlockRequest()
  { return (m_sFlags & WAIT_FOR_CLIENT) == WAIT_FOR_CLIENT; }


  /**
   * Sets the error flag to indicate content is error data not response data
   */
  public void setError( boolean fSet )
  {
    if ( fSet )
      m_sFlags |= ERROR_DATA;
    else
      m_sFlags &= ~ERROR_DATA;

    update();

  } // end setError()


  /**
   * Sets the error flag to true and adds the error reasion to the current data object
   */
  public void setError( String strErrReason )
  {
    m_sFlags |= ERROR_DATA;
    update();

    if ( m_listAttr != null )
    {
      int ndx = m_listAttr.getIndex( ITCSVCERROR );

      if ( ndx < 0 )
        m_listAttr.addAttribute( "", ITCSVCERROR, ITCSVCERROR, "CDATA", strErrReason );
      else
       m_listAttr.setValue( ndx, strErrReason );

    }
  } // end setError()


  /**
   * Sets the error flag to true and adds the error reasion to the data object passed in this method
   */
  public static void setError( VwServiceable sobj, String strErrReason )
  {
    if ( sobj != null )
    {
      short sFlags = get( sobj );
      sFlags |= ERROR_DATA;

      setStatic( sobj, ITCSVCFLAGS, String.valueOf( sFlags ) );
    }

  } // end setError()


  /**
   * Sets the service name attribute for the current associated data object
   *
   * @param strName The name of the service
   */
  public void setServiceName( String strName )
  {
    if ( m_listAttr != null )
    {
      int ndx = m_listAttr.getIndex( "VwService" );

      if ( ndx < 0 )
        m_listAttr.addAttribute( "", "VwService", "VwService", "CDATA", strName );
      else
        m_listAttr.setValue( ndx, strName );

    }

  } // end setServiceName()

  
  /**
   * Updates or adds the attribute name/value pair
   * @param strAttrName
   * @param strAttrValue
   */
  public void setAttribute( String strAttrName, String strAttrValue )
  {
    if ( m_listAttr != null )
    {
      int ndx = m_listAttr.getIndex( strAttrName );

      if ( ndx < 0 )
        m_listAttr.addAttribute( "", strAttrName, strAttrName, "CDATA", strAttrValue );
      else
        m_listAttr.setValue( ndx, strAttrValue );

    }
    
  }
    /**
   * Gets the service name attribute for the current associated data object
   *
   * @param strName The name of the service
   */
  public String getServiceName()
  { return getAttribute( "VwService" ); }

  public String getTranKey()
  { return getAttribute( ITCTRANKEY ); }

  public void setTranKey( String strTranKey )
  {
    if ( m_listAttr != null )
    {
      int ndx = m_listAttr.getIndex( ITCTRANKEY );

      if ( ndx < 0 )
        m_listAttr.addAttribute( "", ITCTRANKEY, ITCTRANKEY, "CDATA", strTranKey );
      else
        m_listAttr.setValue( ndx, strTranKey );

    }
  }
  
  /**
   * Sets the service name attribute for the associated data object
   *
   * @param strName The name of the service
   */
  public static void setServiceName( VwServiceable sobj, String strName )
  { setStatic( sobj, "VwService", strName ); }



  /**
   * Gets the service name attribute for the associated data object
   *
   * @param strName The name of the service
   */
  public static String getServiceName( VwServiceable sobj )
  { return getStatic( sobj, "VwService" ); }


  /**
   * Gets the row block count attribute
   *
   * @param sobj The data object instance to get the row block count
   *
   * @return The row block count attribute
   */
  public static int getRowBlockCount( VwServiceable sobj )
  {
    String strRowBlock = getStatic( sobj, ITCROWBLOCK );
    if ( strRowBlock != null )
      return Integer.parseInt( strRowBlock );

    return 0;

  } // end getRowBlockCount()


  /**
   * Sets the row block count attribute
   *
   * @param sobj The data object instance to set the row block count
   *
   * @return The row block count attribute
   */
  public static void setRowBlockCount( VwServiceable sobj, int nBlockCount )
  { setStatic( sobj, ITCROWBLOCK, String.valueOf( nBlockCount ) ); }

  /**
   * Gets the row block count attribute
   *
   * @param sobj The data object instance to the the row block count
   *
   * @return The row block count attribute
   */
  public int getRowBlockCount()
  {
     String strRowBlock = getAttribute( ITCROWBLOCK );
      if ( strRowBlock != null )
        return Integer.parseInt( strRowBlock );

    return 0;

  } // end getRowBlockCount()


  /**
   * Sets the row block count attribute
   *
   * @param the row block count
   *
   */
  public void setRowBlockCount( int nBlockCount )
  {
    if ( m_listAttr != null )
    {
      int ndx = m_listAttr.getIndex( ITCROWBLOCK );

      if ( ndx < 0 )
        m_listAttr.addAttribute( "", ITCROWBLOCK, ITCROWBLOCK, "CDATA", String.valueOf( nBlockCount ) );
      else
        m_listAttr.setValue( ndx, String.valueOf( nBlockCount ) );
    }
  } // end setRowBlockCount()


  /**
   * Gets the max row count attribute
   * *
   * @return The row block count attribute
   */
  public int getMaxRowCount()
  {
     String strMaxRows = getAttribute( ITCMAXROWS );
      if ( strMaxRows != null )
        return Integer.parseInt( strMaxRows );

    return 0;

  } // end getMaxRowCount()


  /**
   * Sets the max nbr of rows to be returned from a sql service
   *
   * @param nMaxRowCount The max number of rows to be returned from a service
   *
   *
   */
  public void setMaxRowCount( int nMaxRowCount )
  {
    if ( m_listAttr != null )
    {
      int ndx = m_listAttr.getIndex( ITCMAXROWS );

      if ( ndx < 0 )
        m_listAttr.addAttribute( "", ITCMAXROWS, ITCMAXROWS, "CDATA", String.valueOf( nMaxRowCount ) );
      else
        m_listAttr.setValue( ndx, String.valueOf( nMaxRowCount ) );

    }
  } // end setMaxRowCount()


  /**
   * Gets the row block count attribute
   *
   * @param sobj The data object instance to get the row block count
   *
   * @return The row block count attribute
   */
  public static int getMaxRowCount( VwServiceable sobj )
  {
    String strMaxRows = getStatic( sobj, ITCMAXROWS );
    if ( strMaxRows != null )
      return Integer.parseInt( strMaxRows );

    return 0;

  } // end getMaxRowCount()


  /**
   * Sets the max row count attribute
   *
   * @param sobj The data object instance to set the row block count
   * @param nMaxRowCount The max number of rows to be returned from a service
   *
   * @return The row block count attribute
   */
  public static void setMaxRowCount( VwServiceable sobj, int nMaxRowCount )
  { setStatic( sobj, ITCMAXROWS, String.valueOf( nMaxRowCount ) ); }

  /**
   * Tests the flags for error data state
   *
   * @return true if the async flag is set else false is returned
   */
  public boolean isError()
  { return (m_sFlags & ERROR_DATA) == ERROR_DATA; }


  /**
   * For TESTING ONLY
   */

  public static void main( String[] args )
  {
    VwDataObject dobj = new VwDataObject( "TestService" );

    VwServiceFlags svcFlags = new VwServiceFlags();

    String strVal = null;

    strVal = VwServiceFlags.getServiceName( dobj );

    svcFlags.set( dobj );

    svcFlags.set( VwServiceFlags.TRANSACTION);

    Attributes listAttr = dobj.getServiceAttributes();
    return;


  }

} // end of class VwServiceFlags{}

// *** End of VwServiceFlags.java ***
