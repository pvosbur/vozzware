/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwDataObject.java

Create Date: Apr 11, 2006
============================================================================================
*/

package com.vozzware.xml;              // The package this class belongs to

import com.vozzware.util.VwBase64;
import com.vozzware.util.VwDupValueException;
import com.vozzware.util.VwExString;
import com.vozzware.util.VwInvalidFormatException;
import com.vozzware.util.VwNotFoundException;
import com.vozzware.util.VwNullValueException;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;


/**
 * This class is an intelligent Map that adds the following features:<br>
 *
 * . Can preserve the order in which data elements are placed in the container<br>
 * . Can retrieve keys is a case insensitive manner<br>
 * . Can detect duplicate key conditions dis-allowing the overwriting of data<br>
 * . Has built in support for attribute data<br>
 * . Can genereate XML documents from it's contents<br>
 * . Has overloaded getXxxx methethods to co-erce data into all the primitive types<br>
 * <br> Data elements are normally stored in the container as VwElement objects.<br> This allows
 * the preservation of case for the element name while allowing the key in the map to be
 * converted to a case insensitive manner.<br> The VwElement object also carries an optional
 * Attributes for complete XML compatability. The toXml method can generate an xml document
 * from the elements in this container.
 */
public class VwDataObject implements VwServiceable
{

  private boolean     m_fPreserveCase = false;       //  Don't preserve case as the default

  private boolean     m_fAllowDups = true;           // Allows dup keys if true

  private   int       m_nInBytes = 0;                // Nbr of bytes read from deSerialize

  private   Map       m_mapContent = new HashMap();  // data content by key

  private   ArrayList m_listDataOrder;               // Only used to preserve data order if specified

  protected ResourceBundle m_msgs = ResourceBundle.getBundle( "resources.properties.xmlmsgs" );

  private   VwDataObject m_dobjResult;
  
  private    String   m_strRootElementName;
  

  /**
   * Innser class to provide and iterator for order the content map based
   * on the m_listDataOrder Vector
   */
  class DataOrderIterator implements Iterator
  {
    int   m_ndx = 0;      // Index to the m_listDataOrder

    public boolean hasNext()
    {
      if ( m_ndx < m_listDataOrder.size() )
        return true;

      return false;

    }

    public Object next()
    { return m_mapContent.get( m_listDataOrder.get( m_ndx++ ) ); }


    public void remove()
    {}

  } // end class DataOrderIterator{}



  /**
   * Constructs an empty VwDataObject
   *
   */
  public VwDataObject()
  { ; }


  /**
   * Constructs an empty VwDataObject with an assoctaed service name
   *
   * @param strName The associated service name
   *
   */
  public VwDataObject( String strName )
  {
    VwServiceFlags.setServiceName( this, strName );
  }

  /**
   * Constructs an empty VwDataObject
   *
   * @param fPreserveCase if true preserve the case of the data keys else treat as case insensitive
   * @param fPreserveDataOrder if true maintain the order in which data elements are added
   *
   */
  public VwDataObject( boolean fPreserveCase, boolean fPreserveDataOrder )
  {
    m_fPreserveCase = fPreserveCase;

    try
    {
      if ( fPreserveDataOrder )
        setMaintainDataOrder();
    }
    catch( Exception e )
    { }              // This won't happen here
  }


  /**
   * Constructs a data object and initializes it from another data object
   *
   */
  public VwDataObject( VwDataObject copyObj )
  {
    m_fPreserveCase = copyObj.m_fPreserveCase;
    m_mapContent = new HashMap( copyObj.m_mapContent );
    if ( copyObj.m_listDataOrder != null )
    {
      m_listDataOrder = new ArrayList( copyObj.m_listDataOrder.size() );
      m_listDataOrder.addAll( copyObj.m_listDataOrder );

    }

  } // end VwDataObject()

  /**
   * Sets the service name associated with this collection. This method is used only
   * in conjunction with the Vw Opera server product.
   *
   * @param strServiceName The name of the associated service
   *
   */
  public void setServiceName( String strServiceName )
  { VwServiceFlags.setServiceName( this, strServiceName ); }

  /**
   * Returns the service name if used with the Vw Opera product
   */
  public String getServiceName()
  { return VwServiceFlags.getServiceName( this ); }


  /**
   * Returns the order in the list of the id. This request is only valid when
   * the preserve order option is used when this object is created. If there are dup
   * id keys then the the order nbr of the first one encountered is returned
   *
   * @param strID  The id to lookup
   * @return The order position in the list
   * @throws Exception if the preserve order option was not specified when object was created
   */
  public int getIdOrderNbr( String strID ) throws Exception
  {
    if ( m_listDataOrder == null )
      throw new Exception( m_msgs.getString( "VwDataObject.BadIdReq") );

    return m_listDataOrder.indexOf( strID );

  } // end getIdNbr


  /**
   * Sets the name of the root element. This is auto set by the VwXmlToDataObj process is
   * is not normally needed for standard use.
   * 
   * @param strRootElementName The name of the root xml element if this data object is holding an
   * xml document
   */
  public void setRootElementName( String strRootElementName )
  { m_strRootElementName = strRootElementName; }
  
  
  public String getRootElementName()
  { return m_strRootElementName; }
  
    
  
  /**
   * Sets or adds an attribute to a data element. If the attribute already exists
   * <br>then the value for the attribute is updated else the attribute is added.
   *
   * @param strID The name of the element to set/add the attribute for.
   */
  public void setAttribute( String strID, String strAttrName, String strAttrVal  )
  {
    String strOrigID = strID;   // Preserve original case

    if ( !m_fPreserveCase )
      strID = strID.toLowerCase();

    VwElement element = (VwElement)m_mapContent.get( strID );
    AttributesImpl listAttr = null;

    // Add it if it doesn't exist
    if ( element == null )
    {
      listAttr = new AttributesImpl();
      listAttr.addAttribute( "", strAttrName, strAttrName, "CDATA", strAttrVal );
      m_mapContent.put( strID, new VwElement( strOrigID, null, listAttr ) );
      return;
    }

    listAttr = (AttributesImpl)element.getAttributes();

    if ( listAttr == null )
    {
      listAttr = new AttributesImpl();
      element.setAttributes( listAttr );
      listAttr.addAttribute( "", strAttrName, strAttrName, "CDATA", strAttrVal );
      return;
    }
    else
    {
      for ( int x = 0; x < listAttr.getLength(); x++ )
      {
        // Update attribute value of it already exists
        if ( listAttr.getQName( x ).equalsIgnoreCase( strAttrName )  )
        {
          listAttr.setValue( x, strAttrVal );
          return;
        }

      } // end for()

      // Attribute dosen't exist in the list so add it
      listAttr.addAttribute( "", strAttrName, strAttrName, "CDATA", strAttrVal );
    } // end else

  } // end setAttribute


  /**
   * If the PreserveDataOrder option is in effect, this method allows the order
   * of the data item to be changed.
   *
   * @param strID The id of the data item
   * @param nOrderNbr The new index that the item will be retrieved in
   */
  public void changeDataOrder( String strID, int nOrderNbr )
  {

    if (  m_listDataOrder == null )
      return;

    if ( !m_fPreserveCase )
      strID = strID.toLowerCase();

    m_listDataOrder.remove( strID );
    m_listDataOrder.add( nOrderNbr, strID );

  } // end changeDataOrder()

  /**
   * Determines whether a data item with this name key exists in the dataobject
   *
   * @return True if a data item with this key exists in the dataobject;
   * False otherwise.
   */
  public final boolean exists( String strItemName )
  {
    if ( !m_fPreserveCase )
      strItemName = strItemName.toLowerCase();

    return m_mapContent.containsKey( strItemName );

  } // end exists()


  /**
   * Toggles the allows dup key flag. If this option is set to true, then duplicate
   * key values (using the add( xxx ) method will store duplicate keys as an VwElementList.
   * <br>If  this option is false, an VwDupKeyException is thrown when trying to add
   * an element whose key alreay exists.
   *
   * @pararm fAllowDups The state of this option. The default setting is false.
   */
  public void setAllowDupKeys( boolean fAllowDups )
  { m_fAllowDups = fAllowDups; }

  /**
   * Returns the state of the allow dup keys flag
   *
   */
  public boolean allowsDupKeys()
  { return m_fAllowDups; }


   /**
   * Merges the referenced dataobject (mergeObj) with this one. This will add elements from
   * the referenced dataobject not found in this one and will update any keys in this
   * object with the ones found in the referenced one if the fUpdateKeys flag is true.
   * This operation is ignored if mergerObj is null.

   * @param mergeObj The VwDataObject to be merged into this one
   *
   * @exception Exception if trying to merger objects with different key types
   */
  public void merge( VwDataObject mergeObj ) throws Exception
  {

    if ( mergeObj == null )
      return;                     // Just ignore if mergeObj is null

    Map mapMergeContent = mergeObj.m_mapContent;

    if ( m_listDataOrder != null )
    {
      if ( mergeObj.m_listDataOrder != null )
      {
        for ( int x = 0; x < mergeObj.m_listDataOrder.size(); x++ )
        {
          String strKey = (String)mergeObj.m_listDataOrder.get( x );
          if ( !m_mapContent.containsKey( strKey ) )
            m_listDataOrder.add( strKey );

        } // end for()

      } // end if

    } // end if

    // Merge in the content
    m_mapContent.putAll( mergeObj.m_mapContent );


  } // end merge()


  /**
   * Adds a data object to this object. If the key exists, an VwDupValueException is thrown.
   * The object is stored in an VwElement Object.
   *
   * @param strID The id of the object used for lookup
   * @param objData - The data object to be added
   *
   * @exception throws VwDupValueException if the key already exists in the data object
   *  and the allowDups option is not on
   */
  public void add( String strID, Object objData ) throws Exception, VwDupValueException
  {

    VwElement element = null;

    if ( objData instanceof VwElement )
      element = (VwElement)objData;
    else
      element = new VwElement( strID, objData );

    add( element );

  } // end add()




  /**
   * Adds a a byte array to the dataobject and base64 encodes the data
   *
   * @param strID The id of the object used for lookup
   * @param objData - The data object to be added
   *
   * @exception throws VwDupValueException if the key already exists in the data object
   *  and the allowDups option is not on
   */
  public void add( String strID, byte[] abData ) throws Exception, VwDupValueException
  {
    String str64 = new String( com.vozzware.util.VwBase64.encode( abData ) );
    VwElement element = new VwElement( strID, str64 );
    add( element );

  } // end add()


  /**
   * Adds a boolean in the class Boolean to this object. If the key exists, an VwDupValueException is thrown.
   * The object is stored in an VwElement Object.
   *
   * @param strID The id of the object used for lookup
   * @param fVal - The boolean value
   *
   * @exception throws VwDupValueException if the key already exists in the data object
   *  and the allowDups option is not on
   */
  public void add( String strID, boolean fVal ) throws Exception, VwDupValueException
  {
    VwElement element = new VwElement( strID, new Boolean( fVal ) );
    add( element );

  } // end add()

  /**
   * Adds a byte  in the class Byte to this object. If the key exists, an VwDupValueException is thrown.
   * The object is stored in an VwElement Object.
   *
   * @param strID The id of the object used for lookup
   * @param bVal - The byte value
   *
   * @exception throws VwDupValueException if the key already exists in the data object
   *  and the allowDups option is not on
   */
  public void add( String strID, byte bVal ) throws Exception, VwDupValueException
  {
    VwElement element = new VwElement( strID, new Byte( bVal ) );
    add( element );

  } // end add()


  /**
   * Adds a char in the class Character to this object. If the key exists, an VwDupValueException is thrown.
   * The object is stored in an VwElement Object.
   *
   * @param strID The id of the object used for lookup
   * @param cVal - The char value
   *
   * @exception throws VwDupValueException if the key already exists in the data object
   *  and the allowDups option is not on
   */
  public void add( String strID, char cVal ) throws Exception, VwDupValueException
  {
    VwElement element = new VwElement( strID, new Character( cVal ) );
    add( element );

  } // end add()


  /**
   * Adds a short in the class Short to this object. If the key exists, an VwDupValueException is thrown.
   * The object is stored in an VwElement Object.
   *
   * @param strID The id of the object used for lookup
   * @param sVal - The short value
   *
   * @exception throws VwDupValueException if the key already exists in the data object
   *  and the allowDups option is not on
   */
  public void add( String strID, short sVal ) throws Exception, VwDupValueException
  {
    VwElement element = new VwElement( strID, new Short( sVal ) );
    add( element );

  } // end add()

  /**
   * Adds an int in the class Integer to this object. If the key exists, an VwDupValueException is thrown.
   * The object is stored in an VwElement Object.
   *
   * @param strID The id of the object used for lookup
   * @param nVal - The int value
   *
   * @exception throws VwDupValueException if the key already exists in the data object
   *  and the allowDups option is not on
   */
  public void add( String strID, int nVal ) throws Exception, VwDupValueException
  {
    VwElement element = new VwElement( strID, new Integer( nVal ) );
    add( element );

  } // end add()

  /**
   * Adds a long in the class Long to this object. If the key exists, an VwDupValueException is thrown.
   * The object is stored in an VwElement Object.
   *
   * @param strID The id of the object used for lookup
   * @param lVal - The long value
   *
   * @exception throws VwDupValueException if the key already exists in the data object
   *  and the allowDups option is not on
   */
  public void add( String strID, long lVal ) throws Exception, VwDupValueException
  {
    VwElement element = new VwElement( strID, new Long( lVal ) );
    add( element );

  } // end add()

  /**
   * Adds a float in the class Float to this object. If the key exists, an VwDupValueException is thrown.
   * The object is stored in an VwElement Object.
   *
   * @param strID The id of the object used for lookup
   * @param fltVal - The float value
   *
    * @exception throws VwDupValueException if the key already exists in the data object
   *  and the allowDups option is not on
   */
  public void add( String strID, float fltVal ) throws Exception, VwDupValueException
  {
    VwElement element = new VwElement( strID, new Float( fltVal ) );
    add( element );

  } // end add()


  /**
   * Adds a double in the class Double to this object. If the key exists, an VwDupValueException is thrown.
   * The object is stored in an VwElement Object.
   *
   * @param strID The id of the object used for lookup
   * @param dblVal - The double value
   *
   * @exception throws VwDupValueException if the key already exists in the data object
   *  and the allowDups option is not on
   */
  public void add( String strID, double dblVal ) throws Exception, VwDupValueException
  {
    VwElement element = new VwElement( strID, new Double( dblVal ) );
    add( element );

  } // end add()



  /**
   * Adds an VwElemenet to the data object
   *
   *
   * @param element - VwElement object to add
   *
   * @exception throws VwDupValueException if the key already exists in the data object
   *  and the allowDups option is not on
   */
  public void add( VwElement element ) throws Exception, VwDupValueException
  {

    String strID = element.getName();

    if ( !m_fPreserveCase )
      strID = strID.toLowerCase();

    // See if this key already exists
    if ( m_mapContent.containsKey( strID )  )
    {
      if ( !m_fAllowDups )
        throw new VwDupValueException( strID );
    }

    Object objVal = m_mapContent.get( strID );
    if ( objVal != null )
    {

      // There could be a dataobject or dataobjlist already in here by the same name.
      // if this is the case the dataobject needes to be placed in an VwElement

      if ( objVal instanceof VwDataObject )
      {
        VwElementList eleList = new VwElementList();
        VwElement newelement = new VwElement( strID, null );
        element.setChildObject( (VwDataObject)objVal );
        eleList.add( newelement );
        eleList.add( element );
        m_mapContent.put( strID, eleList );
      }
      else
      if ( objVal instanceof VwDataObjList )
      {
        VwElementList eleList = new VwElementList();
        VwElement newelement = new VwElement( strID, null );
        element.setChildObject( (VwDataObjList)objVal );
        eleList.add( newelement );
        eleList.add( element );
        m_mapContent.put( strID, eleList );

      }
      else
      if ( objVal instanceof VwElement )
      {
        VwElementList list = new VwElementList();
        list.add( objVal );
        list.add( element );
        m_mapContent.put( strID, list );
      }
      else
        ((VwElementList)objVal).add( element );
    }
    else
    {    // First time add

      m_mapContent.put( strID, element );

      if ( m_listDataOrder != null )
        m_listDataOrder.add( strID );

     } // end else


    /*
      Object objStore =  m_mapContent.get( strID );

      if ( objStore instanceof VwElement )
      {
        VwElementList eleList = new VwElementList();
        eleList.add( objStore );    // Add the original element
        eleList.add( element );     // Add the new element
        m_mapContent.put( strID, eleList );
      }
      else
      if ( objStore instanceof VwElementList )
        ((VwElementList)objStore).add( element );
      else
      {
        String strMsg = m_msgs.getString( "Vw.DataObjTypeMisMatch" );

        strMsg = VwExString.replace( strMsg, "%1", "VwElement or VwElementList" );
        strMsg = VwExString.replace( strMsg, "%2", objStore.getClass().getColName() );

        throw new Exception( strMsg );

      }

    }
    else
    {    // First time add

      m_mapContent.put( strID, element );

      if ( m_listDataOrder != null )
        m_listDataOrder.add( strID );

    } // end else

    */

  } // end add()


  /**
   * Adds the contents of the specified file to the data object as a base64 encoded string.
   * The file specified in the File object is read into memory and encoded as a base64 string.
   * The string is then stored as an VwElement in the data object.
   *
   * @param strKey The associated key in the data object
   * @param file - File object to read
   *
   * @exception VwDupValueException if the key already exists in the data object
   *  and the allowDups option is not on, Exception if any io errors occur reading the file
   */
  public void add( String strID, File file ) throws Exception, VwDupValueException
  {
    // Make sure file is valid
    if ( !file.exists() )
      throw new Exception( VwExString.replace( m_msgs.getString( "Vw.DataObject.FileNotFound" ), "%1", file.getName() ) );


    FileInputStream fis = new FileInputStream( file );

    add( strID, fis );

  } // end add


  /**
   * Adds the contents from the specified input stream to the data object as a base64 encoded string.
   * The input stream specified is read into memory and encoded as a base64 string.
   * The string is then stored as an VwElement in the data object.
   *
   * @param strKey The associated key in the data object
   * @param file - File object to read
   *
   * @exception VwDupValueException if the key already exists in the data object
   *  and the allowDups option is not on, Exception if any io errors occur reading the file
   */
  public void add( String strID, InputStream ins ) throws Exception, VwDupValueException
  {

    byte[] abFileData = new byte[ (int)ins.available() ];

    ins.read( abFileData );

    byte[] ab64 = VwBase64.encode( abFileData );

    VwElement element = new VwElement( strID, new String( ab64 ) );
    add( element );

  } // end add

  /**
   * Adds an VwElementList object to this object. If the key exists, an VwDupValueException is thrown.
   *
   * @param strID The id of the object used for lookup
   * @param list The VwElementList object to be added
   *
   * @exception throws VwDupValueException if the key already exists
   */
  public void add( String strID, VwElementList list ) throws Exception, VwDupValueException
  {
    if ( !m_fPreserveCase )
      strID = strID.toLowerCase();

    if ( m_mapContent.containsKey( strID )  )
      throw new VwDupValueException( strID );

    m_mapContent.put( strID, list );

    if ( m_listDataOrder != null )
      m_listDataOrder.add( strID );

  } // end add()


  /**
   * Adds an VwDataObjList object to this object. If the key exists, an VwDupValueException is thrown.
   *
   * @param strID The id of the object used for lookup
   * @param list The VwDataObjList object to be added
   *
   * @exception throws VwDupValueException if the key already exists in the data object
   *  and the allowDups option is not on
   */
  public void add( String strID, VwDataObjList list ) throws Exception, VwDupValueException
  {
    if ( !m_fPreserveCase )
      strID = strID.toLowerCase();

    if ( m_mapContent.containsKey( strID )  )
      throw new VwDupValueException( strID );

    m_mapContent.put( strID, list );

    if ( m_listDataOrder != null )
      m_listDataOrder.add( strID );

  } // end add()



  /**
   * Adds an VwDataObject to this object. If the key exists, an VwDupValueException is thrown.
   *
   * @param strID The id of the object used for lookup
   * @param list The VwDataObjList object to be added
   *
   * @exception throws VwDupValueException if the key already exists in the data object
   *  and the allowDups option is not on
   */
  public void add( String strID, VwDataObject dataObj ) throws Exception, VwDupValueException
  {
    if ( !m_fPreserveCase )
      strID = strID.toLowerCase();

    if ( m_mapContent.containsKey( strID )  )
    {
      if ( !m_fAllowDups )
        throw new VwDupValueException( strID );

      Object objData = m_mapContent.get( strID );

      if ( objData instanceof VwElement )
      {
        VwElementList eleList = new VwElementList();
        eleList.add( objData );
        VwElement element = new VwElement( strID, null );
        element.setChildObject( dataObj );
        eleList.add( element );
        m_mapContent.put( strID, eleList );
      }
      else
      if ( objData instanceof VwElementList )
      {
        VwElement element = new VwElement( strID, null );
        element.setChildObject( dataObj );
        ((VwElementList)objData).add( element );
      }
      else
      if ( objData instanceof VwDataObject )
      {
        VwDataObjList dobjList = new VwDataObjList();
        dobjList.add( objData );
        dobjList.add( dataObj );
        m_mapContent.put( strID, dobjList );
      }
      else
      if ( objData instanceof VwDataObjList )
        ((VwDataObjList)objData).add( dataObj );
      else
      {
        String strMsg = m_msgs.getString( "Vw.DataObjTypeMisMatch" );

        strMsg = VwExString.replace( strMsg, "%1", "VwDataObject or VwDataObjList" );
        strMsg = VwExString.replace( strMsg, "%2", objData.getClass().getName() );

        throw new Exception( strMsg );

      }

    } // end if
    else
    {
      m_mapContent.put( strID, dataObj );

      if ( m_listDataOrder != null )
        m_listDataOrder.add( strID );

    }

  } // end add()


  /**
   * Removes all the data elements from this object
   *
   */
  public final void clear()
  {
    m_mapContent.clear();
    if ( m_listDataOrder != null )
      m_listDataOrder.clear();

  }



  /**
   * Gets the number of data elements in the current object
   *
   * @return The number of data elements
   */
  public final int size()
  { return m_mapContent.size(); }


  /**
   * Provides an Iterator to access the VwSmartData objects stored in this data object.
   * If the setMaintainDataOrder property was set to true then the VwSmartData objects
   * are return in the order thay were added else no order may be presumed.
   */
  public Iterator values()
  {
    if ( m_listDataOrder == null )
      return m_mapContent.values().iterator();

    return new DataOrderIterator();

  } // end values


  /**
   * Provides an Iterator to access the data keys that are stored in this data object
   * If the setMaintainDataOrder property was set to true then the VwSmartData objects
   * are return in the order thay were added else no order may be presumed.
   */
  public Iterator keys()
  {
    if ( m_listDataOrder == null )
      return m_mapContent.keySet().iterator();

    return m_listDataOrder.iterator();

  } // end values


  /**
   * Puts a data in the content map. If the key exists, it replaces the existing data reference
   * with the new one
   *
   * @param strID - The character ID of the data element to put in the content map
   * @param strData - The String data to store
   *
   */
  public final void putEncoded( String strID, String strData )
  {

    if ( strData == null )
      return;

    VwElement element = null;

    element = new VwElement( strID, new String( VwBase64.encode( strData.getBytes() ) ) );

    if ( !m_fPreserveCase )
      strID = strID.toLowerCase();

    m_mapContent.put( strID, element );

    if ( m_listDataOrder != null && (! m_listDataOrder.contains( strID ) ) )
      m_listDataOrder.add( strID );

  } // end putEncoded()

  /**
   * Puts a data in the content map. If the key exists, it replaces the existing data reference
   * with the new one
   *
   * @param strID - The character ID of the data element to put in the content map
   * @param objData - The data object to store
   *
   */
  public final void put( String strID, Object objData )
  {
    VwElement element = null;

    if ( objData instanceof VwElement )
      element = (VwElement)objData;
    else
      element = new VwElement( strID, objData );

    if ( !m_fPreserveCase )
      strID = strID.toLowerCase();

    m_mapContent.put( strID, element );

    if ( m_listDataOrder != null && (! m_listDataOrder.contains( strID ) ) )
      m_listDataOrder.add( strID );

  } // end put

  /**
   * Put the contents of the specified file to the data object as a base64 encoded string.
   * The file specified in the File object is read into memory and encoded as a base64 string.
   * The string is then stored as an VwElement in the data object.
   *
   * @param strKey The associated key in the data object
   * @param file - File object to read
   *
   * @exception Exception if any io errors occur reading the file
   */
  public void put( String strID, File file ) throws Exception
  {
    // Make sure file is valid
    if ( !file.exists() )
      throw new Exception( VwExString.replace( m_msgs.getString( "Vw.DataObject.FileNotFound" ), "%1", file.getName() ) );

    FileInputStream fis = new FileInputStream( file );

    put( strID, fis );

  } // end put


  /**
   * Put the contents from the specified input stream to the data object as a base64 encoded string.
   * The input stream specified is read into memory and encoded as a base64 string.
   * The string is then stored as an VwElement in the data object.
   *
   * @param strKey The associated key in the data object
   * @param file - File object to read
   *
   * @exception Exception if any io errors occur reading the stream
   */
  public void put( String strID, InputStream ins ) throws Exception
  {

    byte[] abFileData = new byte[ (int)ins.available() ];

    ins.read( abFileData );

    byte[] ab64 = VwBase64.encode( abFileData );

    VwElement element = new VwElement( strID, new String( ab64 ) );
    put( element );

  } // end add


  /**
   * Adds a a byte array to the dataobject and base64 encodes the data
   *
   * @param strID The id of the object used for lookup
   * @param objData - The data object to be added
   *
   */
  public void put( String strID, byte[] abData ) throws Exception, VwDupValueException
  {
    String str64 = new String( com.vozzware.util.VwBase64.encode( abData ) );
    VwElement element = new VwElement( strID, str64 );

    if ( !m_fPreserveCase )
      strID = strID.toLowerCase();

    m_mapContent.put( strID, element );

    if ( m_listDataOrder != null && (! m_listDataOrder.contains( strID ) ) )
      m_listDataOrder.add( strID );

  } // end add()

  /**
   * Stores a primitave boolean in a Boolean class
   *
   * @param fVal The boolean value to store
   */
  public void put( String strID, boolean fVal )
  {
    VwElement element = new VwElement( strID, new Boolean( fVal ) );
    put( element );

  } // end put

  /**
   * Stores a primitave byte in a Byte class
   *
   * @param bVal The byte value to store
   */
  public void put( String strID, byte bVal )
  {
    VwElement element = new VwElement( strID, new Byte( bVal ) );
    put( element );

  } // end put


  /**
   * Stores a primitave char in a Character class
   *
   * @param cVal The char value to store
   */
  public void put( String strID, char cVal )
  {
    VwElement element = new VwElement( strID, new Character( cVal ) );
    put( element );

  } // end put


  /**
   * Stores a primitave short in a Short class
   *
   * @param sVal The short value to store
   */
  public void put( String strID, short sVal )
  {
    VwElement element = new VwElement( strID, new Short( sVal ) );
    put( element );

  } // end put


  /**
   * Stores a primitave int in an Integer class
   *
   * @param nVal The int value to store
   */
  public void put( String strID, int nVal )
  {
    VwElement element = new VwElement( strID, new Integer( nVal ) );
    put( element );

  } // end put

  /**
   * Stores a primitave long in a Long class
   *
   * @param lVal The long value to store
   */
  public void put( String strID, long lVal )
  {
    VwElement element = new VwElement( strID, new Long( lVal ) );
    put( element );

  } // end put

  /**
   * Stores a primitave float in a Float class
   *
   * @param fltVal The float value to store
   */
  public void put( String strID, float fltVal )
  {
    VwElement element = new VwElement( strID, new Float( fltVal ) );
    put( element );

  } // end put


  /**
   * Stores a primitave double in a Double class
   *
   * @param dblVal The double value to store
   */
  public void put( String strID, double dblVal )
  {
    VwElement element = new VwElement( strID, new Double( dblVal ) );
    put( element );

  } // end put

  /**
   * Puts an VwElement in the data object
   *
   * @param element The VwElement to put in the data object
   */
  public void put( VwElement element )
  {
    String strID = element.getName();

    if ( !m_fPreserveCase )
      strID = strID.toLowerCase();

    m_mapContent.put( strID, element );

    if ( m_listDataOrder != null && (! m_listDataOrder.contains( strID ) ) )
      m_listDataOrder.add( strID );

  } // end put()



  /**
   * Puts an VwElementList in the content map. If the key exists, it replaces the existing data reference
   * with the new one
   *
   * @param strID - The character ID of the data element to put in the content map
   * @param objData - The data object to store
   *
   */
  public final void put( String strID, VwElementList elementList )
  {
    if ( !m_fPreserveCase )
      strID = strID.toLowerCase();

    m_mapContent.put( strID, elementList );

    if ( m_listDataOrder != null && (! m_listDataOrder.contains( strID ) ) )
      m_listDataOrder.add( strID );

  } // end put


  /**
   * Puts an VwDataObjList in the content map. If the key exists, it replaces the existing data reference
   * with the new one
   *
   * @param strID - The character ID of the data element to put in the content map
   * @param objData - The data object to store
   *
   */
  public final void put( String strID, VwDataObjList dataObjList )
  {
    if ( !m_fPreserveCase )
      strID = strID.toLowerCase();

    m_mapContent.put( strID, dataObjList );

    if ( m_listDataOrder != null && (! m_listDataOrder.contains( strID ) ) )
      m_listDataOrder.add( strID );

  } // end put


  /**
   * Puts an VwDataObject in the content map. If the key exists, it replaces the existing data reference
   * with the new one
   *
   * @param strID - The character ID of the data element to put in the content map
   * @param dataObj - The VwDataObject to store
   *
   */
  public final void put( String strID, VwDataObject dataObj )
  {
    if ( !m_fPreserveCase )
      strID = strID.toLowerCase();

    m_mapContent.put( strID, dataObj );

    if ( m_listDataOrder != null && (! m_listDataOrder.contains( strID ) ) )
      m_listDataOrder.add( strID );

  } // end put



  /**
   * Gets a data element value for the given key
   *
   * @param strID - A string with the character ID of the data element to retrieve
   *
   * @return The data element for the given key or null if no object or
   * key exists. NOTE If the object at this position is
   * an VwElement, then get returns the object contained in the VwElement not the
   * VwElement object itself. Use the getObject method to retrieve the VwElement object itself
   *
   */
  public final Object get( String strID )
  {
    if ( !m_fPreserveCase )
      strID = strID.toLowerCase();

    Object obj = m_mapContent.get( strID );

    if ( obj instanceof VwElement )
      return ((VwElement)obj).getObject();

    return obj;

  } // end get()


  /**
   * Gets a object for the given key. It will be one of three types:
   * VwElement - a single element,
   * VwElementList - a list of VwElements
   * VwDataObjList - a list of VwDataObjects
   *
   * @param strID - A string with the character ID of the data element to retrieve
   *
   * @return The dobject with the given ID or null if no object exists
   *
   */
  public final Object getObject( String strID )
  {
    if ( !m_fPreserveCase )
      strID = strID.toLowerCase();

    return m_mapContent.get( strID );

  } // end getObject()


  /**
   * Turns on the maintain data order option. This property must be set prior to any data
   * being added to the object or an Exception will be thrown.
   *
   * @exception Exception if this property is invoked after any data elements have already been
   * added.
   */
  public void setMaintainDataOrder() throws Exception
  {
    if ( m_mapContent.size() > 0 )
      throw new Exception( m_msgs.getString( "Vw.DataObj.DataOrderIllegalState" ) );

    if ( m_listDataOrder == null )
      m_listDataOrder = new ArrayList();

  } // end setMaintainDataOrder


  /**
   * Sets the preserve case flag. It true the case is preserved for the data keys else
   * keys are converted to lower case before a lookup is done (the default)
   */
  public void setPreserveCase( boolean fCase )
  { m_fPreserveCase = fCase; }

  /**
   * Retrieves a data element at a given position in the current object.<br>
   * NOTE! This method may only be called if the setMaintainDataOrder property was previously set to true.<br>
   * The default value is false.
   *
   * @param nPos - The data element number
   *
   * @return The data element at the given position. If the object at this position is
   * an VwElement the get returns the object contained in the VwElement not The
   * VwElement object itself. Use the getObject method to retrieve the VwElement object itself
   *
   * @exception throws Exception if the maintainDataOrder property not set to true
   * ArrayIndexOutOfBounds exception if the index number is out of bounds
   */
  public final Object get( int nPos ) throws Exception
  {
    if ( m_listDataOrder == null )
      throw new Exception( m_msgs.getString( "Vw.DataObj.NoDataOrder" ) );

    return get( (String)m_listDataOrder.get( nPos ) );

  } // end elementAt


  /**
   * Gets the name of the object ( key ) at the position specified
   *
   * @param nPos - The data element number
   *
   * @return The data key value at the given position
   *
   * @exception throws Exception if the maintainDataOrder property not set to true
   * ArrayIndexOutOfBounds exception if the index number is out of bounds
   */
  public String getKey( int nPos ) throws Exception
  {

    if ( m_listDataOrder == null )
      throw new Exception( m_msgs.getString( "Vw.DataObj.NoDataOrder" ) );

    return (String)m_listDataOrder.get( nPos );

  } // end ketKey

  /**
   * Determins if the data id  is multivalued
   *
   * @param strId The data id to test
   *
   * @return true if the data id is a multivalued, false otherwise.
   */
  public final boolean isMultiValued( String strID )
  {
    if ( !m_fPreserveCase )
      strID = strID.toLowerCase();

    return (m_mapContent.get( strID) instanceof List);

  }


  /**
   * Determins if the value of the parameter name is multi valued
   *
   * @param strElementName The name of the parameter to test
   *
   * @return true if the string is a multivalued string, false otherwise.
   *
   * @exception Exception if the Element name does not exist
   */
  public final boolean isMultiValuedElement( String strElementName ) throws Exception
  {
    String strValue = getString( strElementName );

    return (boolean)(strValue.indexOf( "\u0001" ) >= 0 );

  } // end isMultiValuedParam()


  /**
   * Gets the attribute list for the specified element
   *
   * @param strID The element name to get the attribute list for
   *
   * @return a List of attributes for the element specified or null if there were no
   * attributes for this element
   *
   */
  public Attributes getAttributeList( String strID )
  {
    if ( strID == null )
      return null;

    if ( !m_fPreserveCase )
      strID = strID.toLowerCase();

    VwElement element = (VwElement)m_mapContent.get( strID);
    if ( element == null )
      return null;

    return element.getAttributes();

  } // end getAttributeList()


  /**
   * Sets the attribute list for the specified element
   *
   * @param strID The element name to set the attribute list for
   * @param listAttr The VwAttribute list to set.<br> This method replaces an existing
   * attribute list if one already exists for the element id
   *
   */
  public void setAttributeList( String strID, Attributes listAttr )
  {
    String strOrigCaseID = strID;   // Preserve original case of key

    if ( !m_fPreserveCase )
      strID = strID.toLowerCase();

    VwElement element = (VwElement)m_mapContent.get( strID );
    if ( element == null )
    {
      element = new VwElement( strOrigCaseID, null );
      m_mapContent.put( strID, element );
    }

    element.setAttributes( listAttr );

  } // end setAttributeList()


  /**
   * Gets the attribute value for the specified element, and attribute name
   *
   * @param strID The element name to get the attribute list for
   * @param strAttrName The name of the attribute to retrieve
   *
   * @return a String containing the attribute value or null if the attribute name does not exist
   *
   */
  public String getAttribute( String strID, String strAttrName )
  {
    if ( strID == null )
      return null;

    if ( !m_fPreserveCase )
      strID = strID.toLowerCase();

    VwElement element = (VwElement)m_mapContent.get( strID );
    if ( element == null )
      return null;

    Attributes listAttr =  element.getAttributes();

    if ( listAttr == null )
      return null;            // No attributes to check for

    return listAttr.getValue( strAttrName );              // no attribute found

  } // end getAttribute()


  /**
   * Gets the Attributes for an Opera service
   *
   * @return The attribute list associated with the service or null if no attributes were defined
   */
  public Attributes getServiceAttributes()
  { return getAttributeList( VwServiceFlags.ITCSVCATTR ); }


  /**
   * Sets the attribute list for an Opera service
   *
   * @param listAttr The service attribute list
   */
  public void setServiceAttributes( Attributes listAttr )
  { setAttributeList( VwServiceFlags.ITCSVCATTR, listAttr ); }


  /**
   * Remove the attribute list for an Opera service
   *
   * @param listAttr The service attribute list
   */
  public void removeServiceAttributes()
  { remove( VwServiceFlags.ITCSVCATTR ); }


  /**
   * Retrieves a data element with the given character ID, as a byte array
   *
   * @param strID - A string with the character ID of the data element to retrieve
   *
   * @return The data element with the given ID as a byte array
   *
   * @exception throws VwInvalidFormatException if the data cannot be converted to a string
   * @exception throws VwNotFoundException if the ID is invalid
   */
  public final byte[] getByteArray( String strID ) throws VwInvalidFormatException,
                                                          VwNotFoundException,
                                                          VwNullValueException
  { return find( strID, false ).toString().getBytes(); }


  /**
   * Removes the element from the data object
   *
   * @param element The VwElement instance to remove.
   */
  public void remove( VwElement element )
  {
    String strID = element.getName();
    if ( !m_fPreserveCase )
      strID = strID.toLowerCase();

    Object obj = m_mapContent.get( strID );

    if ( obj instanceof VwElementList )
    {
      for ( Iterator iElements = ((VwElementList)obj).iterator(); iElements.hasNext(); )
      {
        VwElement ele = (VwElement)iElements.next();

        if ( ele == element )
        {

          iElements.remove();

          if ( ((VwElementList)obj).size() == 0 )
          {
            m_mapContent.remove( strID );
            if ( m_listDataOrder != null )
              m_listDataOrder.remove( strID );
          }

          return;
        } // end if
      } // for
    }  // end if ( obj instanceof VwElementList )
    else
    if ( obj instanceof VwElement )
    {
      m_mapContent.remove( strID );
      if ( m_listDataOrder != null )
        m_listDataOrder.remove( strID );

    }

  } // end remove
  /**
   * Removes any object type associated with this key
   *
   * @param strID The id if the object to remove
   */
  public void remove( String strID )
  {
    if ( !m_fPreserveCase )
      strID = strID.toLowerCase();

    m_mapContent.remove( strID );

    if ( m_listDataOrder != null )
      m_listDataOrder.remove( strID );

  }

  /**
   * Returns as boolean
   * @param strID The element ID to retrieve
   *
   * @return
   * @throws VwNotFoundException
   * @throws VwNullValueException
   */
  public final boolean getBoolean( String strID ) throws VwNotFoundException,
                                                         VwNullValueException
  {
    VwElement ele = find( strID, false );

    return  ((Boolean)ele.getObject()).booleanValue();

  }

  /**
   * Retrieves a data element with the given character ID, as a byte value
   *
   * @param nID - The numeric ID of the data element to retrieve
   *
   * @return The data element as a byte data type
   *
   * @exception throws VwNotFoundException if the ID is invalid
   * @exception throws VwNullValueException if value is null
   */
  public final byte getByte( String strID ) throws NumberFormatException,
                                                   VwNotFoundException,
                                                   VwNullValueException
  { return Byte.parseByte( find( strID, false ).toString() ); }



  /**
   * Retrieves a data element with the given character ID, as a char value
   *
   * @param nID - The numeric ID of the data element to retrieve
   *
   * @return The data element as a char data type
   *
   * @exception throws VwInvalidFormatException if the data cannot be converted to a string
   * @exception throws VwNotFoundException if the ID is invalid
   * @exception throws VwNullValueException if value is null
   */
  public final char getChar( String strID ) throws VwInvalidFormatException,
                                                   VwNotFoundException,
                                                   VwNullValueException
  { return find( strID, false ).toString().charAt( 0 ); }


  /**
   * Returns the Map data content for this data object
   * @return
   */
  public Map toMap()
  {
    return m_mapContent;

  }

  /**
   * Retrieves a data element with the given character ID, co-erced to  a string
   *
   * @param strID - The character ID of the data element to retrieve
   *
   * @return The data element in string form
   *
   */
  public final String getString( String strID )
  {
    if ( !m_fPreserveCase )
      strID = strID.toLowerCase();

    Object obj = m_mapContent.get( strID );

    if ( obj instanceof VwElement )
      obj = ((VwElement)obj).getObject();

    if ( obj == null )
     return null;

    return obj.toString();

  } // end getString



  /**
   * Decodes the the data to a string
   *
   * @param strID - The character ID of the data element to retrieve
   *
   * @return The data element in string form
   *
   */
  public final String getEncoded( String strID )
  {
    if ( !m_fPreserveCase )
      strID = strID.toLowerCase();

    Object obj = m_mapContent.get( strID );

    if ( obj == null )
      return null;

    if ( obj instanceof VwElement )
    {
       obj = ((VwElement)obj).getObject();

       return new String( VwBase64.decode( obj.toString().getBytes() ) );

    }

    return null;

  } // end getEncoded


  /**
   * Retrieves a data element with the given character ID, as a short
   *
   * @param strID - The character ID of the data element to retrieve
   *
   * @return The data converted to a short, if possible
   *
   * @exception throws VwInvalidFormatException if the data is not numeric or cannot be converted to a short
   * @exception throws VwNotFoundException if the ID is invalid
   */
  public final short getShort( String strID ) throws VwInvalidFormatException,
                                                     VwNotFoundException,
                                                     VwNullValueException
  { return Short.parseShort( find( strID, false ).toString() ); }



  /**
   * Retrieves a data element with the given character ID, as an integer
   *
   * @param strID - The character ID of the data element to retrieve
   *
   * @return The data element converted to an integer, if possible
   *
   * @exception throws VwInvalidFormatException if the data is not numeric or cannot be converted to an integer
   * @exception throws VwNotFoundException if the ID is invalid
   */
  public final int getInt( String strID ) throws VwInvalidFormatException,
                                                 VwNotFoundException,
                                                 VwNullValueException
  { return Integer.parseInt( find( strID, false ).toString() ); }


  /**
   * Retrieves a data element with the given character ID, as a long
   *
   * @param strID - The character ID of the data element to retrieve
   *
   * @return The data element converted to a long, if possible
   *
   * @exception throws VwInvalidFormatException if the data is not numeric or cannot be converted to a long
   * @exception throws VwNotFoundException if the ID is invalid
   */
  public final long getLong( String strID ) throws VwInvalidFormatException,
                                                   VwNotFoundException,
                                                   VwNullValueException
  { return Long.parseLong( find( strID, false ).toString() ); }


  /**
   * Retrieves a data element with the given character ID, as a float
   *
   * @param strID - The character ID of the data element to retrieve
   *
   * @return The data element converted to a float, if possible
   *
   * @exception throws VwInvalidFormatException if the data is not numeric or cannot be converted to a float
   * @exception throws VwNotFoundException if the ID is invalid
   */
  public final float getFloat( String strID ) throws VwInvalidFormatException,
                                                     VwNotFoundException,
                                                     VwNullValueException
  { return Float.parseFloat( find( strID, false ).toString() ); }


  /**
   * Retrieves a data element with the given character ID, as a double
   *
   * @param strID - The character ID of the data element to retrieve
   *
   * @return The data element converted to a double, if possible
   *
   * @exception throws VwInvalidFormatException if the data is not numeric or cannot be converted to a double
   * @exception throws VwNotFoundException if the ID is invalid
   */
  public final double getDouble( String strID ) throws VwInvalidFormatException,
                                                       VwNotFoundException,
                                                       VwNullValueException
  { return Double.parseDouble( find( strID, false ).toString() ); }


  /**
   * Find the data object or child data object that matches the key and optionally an attribute/value
   *
   * @param strKey The key within a speciific data object instance
   * @param strAttrName The name of the attribute associated with the key to match on (May be null)
   * @param strValue The value of the attribute to match on (May be null )
   *
   * @return The VwDataObject instance that contains the key and if specified a match on the attribute
   * and value
   */
  public VwDataObject find( String strKey, String strAttrName, String strValue )
  {
    m_dobjResult = null;

    find( this, strKey, strAttrName, strValue );

    return m_dobjResult;

  } // end find

  /**
   * Locates the dataobject with the key, and attribute valie (if specified)
   *
   * @param strKey The key within a speciific data object instance
   * @param strAttrName The name of the attribute associated with the key
   * @param strValue The value of the attribute to match on
   */
  private void find( VwDataObject dobjParent, String strKey, String strAttrName, String strValue )
  {

    if ( m_dobjResult != null )
      return;

    Object obj = dobjParent.getObject( strKey );

    if ( obj == null )
    {
      // Iterate through this object's keys to see if what we're looking for is in a child dataobject

      for ( Iterator iKeys = dobjParent.keys(); iKeys.hasNext(); )
      {
        Object objChild = dobjParent.getObject( (String)iKeys.next() );

        if ( objChild instanceof VwDataObject )
        {
          find( (VwDataObject)objChild, strKey, strAttrName, strValue );
          if ( m_dobjResult != null )
            return;
        }
        else
        if ( objChild instanceof VwDataObjList )
        {
          for ( Iterator iDataObjChild = ((VwDataObjList)objChild).iterator(); iDataObjChild.hasNext(); )
          {
            find( (VwDataObject)iDataObjChild.next(), strKey, strAttrName, strValue );

            if ( m_dobjResult != null )
             return;

          } // end for()

        } // end if
        else
        if ( objChild instanceof VwElementList )
        {
          for ( Iterator iElement = ((VwElementList)objChild).iterator(); iElement.hasNext(); )
          {
            VwElement element = (VwElement)iElement.next();

             if ( hasAttribute( element.getAttributes(), strAttrName, strValue ) )
             {
               m_dobjResult = dobjParent;
               return;
             }

          } // end for()

        } // end if


      } // end for()

      return;

    } // end if  if ( obj == null )
    else
    if ( obj instanceof VwDataObjList )
    {
      for ( Iterator iDataObjects = ((VwDataObjList)obj).iterator(); iDataObjects.hasNext(); )
      {
        find( (VwDataObject)iDataObjects.next(), strKey, strAttrName, strValue );

        if ( m_dobjResult != null )
          return;

      }

    }
    else
    if ( obj instanceof VwDataObject )
    {
      find( (VwDataObject)obj, strKey, strAttrName, strValue );
      if ( m_dobjResult != null )
        return;
    }
    else
    if ( obj instanceof VwElementList )
    {
      for ( Iterator iElement = ((VwElementList)obj).iterator(); iElement.hasNext(); )
      {
        VwElement element = (VwElement)iElement.next();

         if ( hasAttribute( element.getAttributes(), strAttrName, strValue ) )
         {
           m_dobjResult = dobjParent;
           return;
         }

      } // end for()

    } // end if
    else
    {
       if ( strAttrName != null )
       {
         if ( obj instanceof VwElement )
         {
           Attributes listAttr = ((VwElement)obj).getAttributes();

           if ( hasAttribute( listAttr, strAttrName, strValue ) )
           {
             m_dobjResult = dobjParent;
             return;
           }

         } // end if
         else
         {
           for ( Iterator iElements = ((VwElementList)obj).iterator(); iElements.hasNext(); )
           {
             VwElement ele = (VwElement)iElements.next();
             Attributes listAttr = ((VwElement)obj).getAttributes();

             if ( hasAttribute( listAttr, strAttrName, strValue ) )
             {
               m_dobjResult = dobjParent;
               return;
             }

           } // end for

        } // end else

      } // end if ( strAttrName != null )
      else
        m_dobjResult = dobjParent;

    } // end if

    return;

  } // end find


  /**
   * Searches attribute list for a match on attribute name a value
   *
   * @param listAttr The attribute list to search
   * @param strAttrName The attribute name to match in the attribute list
   * @param strValue The attribute value to match in the attribute list
   *
   */
  private boolean hasAttribute( Attributes listAttr, String strAttrName, String strValue )
  {

    if ( listAttr == null )
      return false;

    String strVal = listAttr.getValue( strAttrName );

    if ( strVal != null && strVal.equalsIgnoreCase( strValue )  )
      return true;

    return false;

  } // end hasAttribute()


  /**
   * Retrieves a data element with the given character ID, as an integer
   *
   * @param strKey - The character key  of the data element to retrieve
   * @param fReturnNUllOBject if true, return null objects else throw VwNullValueException
   *
   * @return The data object with the given key
   *
   * @exception throws VwNotFoundException if the name does not exists
   */
  private VwElement find( String strKey, boolean fReturnNUllOBject ) throws VwNotFoundException,
                                                                             VwNullValueException
  {
    if ( !m_fPreserveCase )
      strKey = strKey.toLowerCase();

    VwElement element = (VwElement)m_mapContent.get( strKey );
    if ( element == null )
        throw new VwNotFoundException( "Data ID " + strKey + " does not exist" );

    if ( element.getObject() == null )
    {
      if ( !fReturnNUllOBject )
        throw new VwNullValueException( strKey );

    }

    return element;         // Return the object

  } // end find()


  /**
   * Serializes the current object into a byte array for the Vw protocol.
   *
   * @return A String containing the serialized object in ITCX protocol format
   *
   * @exception throws Exception if there are any protocol or internal errors
   */
  public byte[] serialize() throws Exception
  {
    return toXml( getString( VwServiceFlags.getServiceName( this ) ), null, true, 0 ).getBytes();

  } // end serialize()


  /**
   * Serializes the current object into a byte array for the Vw protocol for the Error return
   *
   * @return A byte array containing the serialized object in ITCX protocol format
   *
   * @exception throws Exception if there are any protocol or internal errors
   */
  public byte[] serializeError() throws Exception
  {
    return null;

  } // end serializeError()


  /**
   * De-Serializes the object from an InputStream
   *
   * @param inStream The input stream that the bytes will be read from
   *
   * @exception throws java IOException if an I/O error occurs
   * @exception throws java Exception if a protocol error is found
   *
   */
  public void deSerialize( InputStream inStream ) throws IOException, Exception
  {
    deSerialize( buildArrayFromStream( inStream ) );
  }


  /**
   * De-Serializes the object from a byte array
   *
   * @param abMsg The byte array the data object wil be initialized from
   *
   * @exception throws java IOException if an I/O error occurs
   * @exception throws java Exception if a protocol error is found
   *
   */
  public void deSerialize( byte[] abMsg ) throws IOException, Exception
  {
  } // end deSerialize()


  /**
   * Gets the number of bytes actually processed (read) from the deSerialize() method
   *
   * @return and int containing the number of bytes actually processed (read) from the deSerialize() method
   */
  public int getDeSerialzeBytesRead()
  { return m_nInBytes; }


  /**
   * Builds a byte array from the input stream for de-serialization
   *
   * @param inStream - The DataInputStream containing the ITCX protocol data
   *
   * @return A byte array containing the data read from stream
   *
   * @exception throws java IOException if an I/O error occurs
   * @exception throws java Exception if a protocol error is found
   */
  private byte[] buildArrayFromStream( InputStream inStream ) throws Exception,
                                                                     IOException
  {
    return null;          // Return array of data

  } // end buildArrayFromStream()


  /**
   * Put the current thread to sleep
   *
   * @param lMilliSecs The nbr of milliseconds to sleep
   */
  private void sleep( long lMilliSecs )
  {
    try
    {
      Thread.sleep( lMilliSecs );  // Let things breathe

    }
    catch( Exception e )
    {}

  } // end sleep()



  /**
   * Creates an XML representation of the data object. This method omitts xml formatting.
   * The Xml string is a contigous stream of xml tags and data only.
   *
   * @param strParentTag Optional - the parent tag of the data elements
   * @param strDefaultForNull A defualt placeholder for null data elements. If omitted
   * the data element will not appear in the xml output.
   */
  public String toXml( String strParentTag, String strDefaultForNull )
  { return toXml( strParentTag, strDefaultForNull, false, -1, null ); }


  /**
   * Creates an XML representation of the data object
   *
   * @param strParentTag Optional - the parent tag of the data elements
   * @param strDefaultForNull A defualt placeholder for null data elements. If omitted
   * the data element will not appear in the xml output.
   * @param fFormatted if the, the XML will be formatted with CR/LF and indentation according
   * to the parentage.
   * @param nLevel The indentation parentage level to start the formatting with. Each level
   * adds two spaces of indentation.
   */
  public String toXml( String strParentTag, String strDefaultForNull,
                       boolean fFormatted, int nLevel )
  { return toXml( strParentTag, strDefaultForNull, fFormatted, nLevel, null ); }


  /**
   * Creates an XML representation of the data object
   *
   * @param strParentTag Optional - the parent tag of the data elements
   * @param strDefaultForNull A defualt placeholder for null data elements. If omitted
   * the data element will not appear in the xml output.
   * @param fFormatted if the, the XML will be formatted with CR/LF and indentation according
   * to the parentage.
   * @param nLevel The indentation parentage level to start the formatting with. Each level
   * adds two spaces of indentation.
   */
  public String toXml( String strParentTag, String strDefaultForNull,
                       boolean fFormatted, int nLevel, String strElementDataOnlyTag )
  {
    String strRootTag = strParentTag;
    
    if ( strRootTag == null )
      strRootTag = m_strRootElementName;
    
      
    VwDataObjToXml xmlWriter = new VwDataObjToXml();

    if ( strElementDataOnlyTag != null )
      xmlWriter.genTagDataOnly( strElementDataOnlyTag );

    if ( strDefaultForNull  == null )
      xmlWriter.setIgnoreNullData( true );

    return xmlWriter.toXml( strRootTag, this, null, fFormatted, nLevel );

  }


  // **** FOR TESTING ONLY
  public static void main( String[] args )
  {
    String str = null;

    try
    {
      VwDataObject d1 = new VwDataObject( true, true );

      d1.putEncoded( "test", "<test>ok</test>" );


      String strXml = d1.getString( "test" );

      strXml = d1.getEncoded( "test" );

      strXml = d1.toXml( null, null, true, 0 );

      VwXmlToDataObj xtd1 = new VwXmlToDataObj( true, true );
      VwDataObject dx = xtd1.parse( strXml, false );

      String sss = dx.getEncoded( "test" );

      VwElement e1 = new VwElement( "line", "line 1" );
      d1.add( e1 );

      VwElement e2 = new VwElement( "line", "line 2" );
      d1.add( e2 );

      VwDataObject d2 = new VwDataObject( true, true );
      d2.add( new VwElement( "subLine", "sub line data" ) );

      VwElement e3 = new VwElement( "line", "line 4" );

      d2.add( e3 );
      d1.add( "line", d2 );

      Object objVal = d1.getObject( "line" );

      strXml = d1.toXml( "doc", null, true, 0 );
      System.out.println( strXml );

      d1.remove( e3 );
      str = d1.toXml( "Doc", null, true, 0 );
      System.out.println( str );

      d1.remove( e1 );
      str = d1.toXml( "Doc", null, true, 0 );
      System.out.println( str );


      VwXmlToDataObj xtd = new VwXmlToDataObj();

      str = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"
          + "<!DOCTYPE greeting ["
          + "<!ENTITY sp \"                \">]>"
          + "<greeting><![CDATA[Anderson & Sons]]></greeting>";

      VwDataObject dobj = xtd.parse( str, false );

      String s1 = dobj.getString( "greeting" );

       str = "<DriverList>"
               + "  <Driver name=\"OPERA\" >"
               + "   <class>class1</class>"
               + "    <Cat name=\"local\" >"
               + "      <test>1</test>"
               + "    </Cat>"
               + "    <Cat name=\"rem\" >"
               + "      <test>2</test>"
               + "    </Cat>"
               + "  </Driver>"
               + "  <Driver name=\"CIRQ\" >"
               + "   <class>class2</class>"
               + "    <Cat name=\"local1\" >"
               + "      <test>1</test>"
               + "    </Cat>"
               + "    <Cat name=\"rem1\" >"
               + "      <test>2</test>"
               + "    </Cat>"
               + "  </Driver>"
               + "</DriverList>";



      xtd = new VwXmlToDataObj();
      xtd.makeDataObjectsForParentTags();
      VwDataObject dobjDrivers = xtd.parse( str, false );

      VwDataObject dobjRes = dobjDrivers.find( "Driver", null, null );
      dobjRes = dobjDrivers.find( "notfound", null, null );
      dobjRes = dobjDrivers.find( "Cat", null, null );
      dobjRes = dobjDrivers.find( "Driver", "name", "CIRQ" );
      dobjRes = dobjDrivers.find( "Cat", "name", "rem1" );

      int i = 1;

    }
    catch( Exception e )
    {
      e.printStackTrace();

    }
  }

} // end class VwDataObject


// *** End of VwDataObject.java ***

