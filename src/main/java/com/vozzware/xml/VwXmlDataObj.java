package com.vozzware.xml;

import com.vozzware.util.VwBase64;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class is the new version of the VwXmlDataObj taking advantage of generics and xpath searches
 */
public class VwXmlDataObj
{
  private Map<String,Object>m_mapContent = new LinkedHashMap<String, Object>();
  private List<VwXmlDataObj>m_listChildren = new ArrayList<VwXmlDataObj>(); // Child elements that are also parents
  private Attributes        m_attrs;        // Attributes of this parent element

  private  String m_strName;
  private  String m_strQName;
  private  VwXmlDataObj m_xmlObjSearchResult;

  private  VwXmlDataObj m_xmlObjParent;


  public VwXmlDataObj( String strQName, String strName )
  {

    m_strQName = strQName;
    m_strName = strName;
  }


  /**
   * Sets the name of the xml element that corresponds to this data object
   *
   * @param strName The name of the root xml element if this data object is holding an
   * xml document
   */
  public void setName( String strName )
  { m_strName = strName; }


  /**
   * Gets the corresponding element name of this xml object
   * @return
   */
  public String getName()
  { return m_strName; }


  /**
   * Sets the qualified name of the xml element that corresponds to this data object
   *
   * @param strQName The qualified name of the root xml element if this data object is holding an
   * xml document
   */
  public void setQName( String strQName )
  { m_strQName = strQName; }


  /**
   * Gets the corresponding element name of this xml object
   * @return
   */
  public String getQName()
  { return m_strQName; }


  /**
   * Set the parent object
   * @param xmlObjPParent The parent object
   */
  public void setParent( VwXmlDataObj  xmlObjPParent )
  { m_xmlObjParent = xmlObjPParent; }


  /**
   * Get this objects parent
   * @return  Theparent object or null if this is the root object
   */
  public VwXmlDataObj getParent()
  { return m_xmlObjParent; }


  /**
   * Sets the attributes for this parent xml tag
   * @param attrs
   */
  public void setAttributes( Attributes attrs )
  { m_attrs = attrs; }


  /**
   * Gets the attributes for this parent xml tag
   * @return
   */
  public  Attributes getAttributes()
  { return m_attrs; }

  /**
   * Sets or adds an attribute to a data element. If the attribute already exists
   * <br>then the value for the attribute is updated else the attribute is added.
   *
   * @param strID The name of the element to set/add the attribute for.
   */
  public void setAttribute( String strID, String strAttrName, String strAttrVal  )
  {
    int nPos = strID.indexOf( ':' );
    
    VwXmlElement element = (VwXmlElement)m_mapContent.get( strID.toLowerCase() );
    AttributesImpl listAttr = null;

    // Add it if it doesn't exist
    if ( element == null )
    {
      listAttr = new AttributesImpl();
      listAttr.addAttribute( "", strAttrName, strAttrName, "CDATA", strAttrVal );
      m_mapContent.put( strID, new VwXmlElement( strID.toLowerCase(), null, listAttr ) );
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
   * Returns true this object has an element for the id
   * @param strId The id of the element to check
   * @return
   */
  public boolean contains( String strId )
  { return m_mapContent.containsKey( strId.toLowerCase() ); }


  /**
   * Removes all the data elements from this object
   *
   */
  public final void clear()
  {
    m_mapContent.clear();
    m_listChildren.clear();

  }



  /**
   * Gets the number of data elements in the current object
   *
   * @return The number of data elements
   */
  public final int size()
  { return m_mapContent.size(); }


  /**
   * Return the content maps' keySet
   * @return
   */
  public Set<String>keySet()
  {
    return m_mapContent.keySet();

  }


  /**
   * Gets a list of this elements children
   * @return a List of VwXnlDataObj objects
   */
  public List<VwXmlDataObj> getChildren()
  { return m_listChildren; }


  /**
   * Add a child data obj to this
   * @param xmlObjChild  The child object to add
   */
  public void addChild( VwXmlDataObj xmlObjChild )
  { m_listChildren.add( xmlObjChild ); }


  /**
   * Gets a children count for this object
   * @return
   */
  public int getChildrenCount()
  { return m_listChildren.size(); }


  /**
   * Removes all children from this object
   */
  public void removeAllChildren()
  {  m_listChildren.clear();;  }


  /**
   * Determins if the object is a child of this object
   * @param xmlDataObj  the object to test
   * @return true if the object is a chils of this object
   */
  public boolean isChild( VwXmlDataObj xmlDataObj )
  { return m_listChildren.contains( xmlDataObj ); }


  /**
   * Adds a new element to the content map. 
   * @param elementToAdd The element to Add 
   */
  public void add( VwXmlElement elementToAdd )
  {
    String strElementId = elementToAdd.getQName();
    
    Object objElementInMap  = m_mapContent.get( strElementId.toLowerCase() ); // see if we already have one with this id
    
    if( objElementInMap == null )                                  // no existing one with this id so put in the map
      m_mapContent.put( strElementId.toLowerCase(), elementToAdd );
    else
    {
      
      if ( objElementInMap instanceof List )
        ((List<VwXmlElement>)objElementInMap).add( elementToAdd );
      
      else
      {
        List<VwXmlElement>listElements = new ArrayList<VwXmlElement>();
        listElements.add( (VwXmlElement)objElementInMap);
        listElements.add( elementToAdd );
        m_mapContent.put( strElementId.toLowerCase(), listElements );
      }
    }

  }


  /**
   * Get the element wrapper by its QName, if multiple elements exist, use the getElement( int ndx, String strQName )
   * <br>method instead
   * @param strQName The QName of the elememnt tp retrieve
   * @return
   * @throws VwMultipleElemenException if more that one element exists for this name
   */
  public VwXmlElement getElement( String strQName ) throws VwMultipleElemenException
  {
    Object objElement = m_mapContent.get( strQName.toLowerCase() );
    if ( objElement instanceof List )
      throw new VwMultipleElemenException( strQName );
    
    return (VwXmlElement)objElement;
    
  }


  /**
   * Gets an element my its index in the list when multiple xml elements have the same name
   * @param ndx The element index in the list to get
   * @param strQName  The QName of the element tp retrieve
   * @return
   * @throws IndexOutOfBoundsException
   */
  public VwXmlElement getElement( int ndx,  String strQName ) throws IndexOutOfBoundsException
  {
    Object objElement = m_mapContent.get( strQName.toLowerCase() );
    VwXmlElement element = null;

    if ( objElement instanceof List )
     element = ((List<VwXmlElement>)objElement).get( ndx );
    else
    {
      element = (VwXmlElement)objElement;
      if ( ndx > 0 )
        throw new IndexOutOfBoundsException(  );
    }

    return element;
    
  }


  /**
   * Returns a list of VwXmlElemet objects for the element name reqested
   * @param strQName   The QName of the element to retrieve
   * @return
   */
  public List<VwXmlElement> getElements(  String strQName )
  {
    Object objElement = m_mapContent.get( strQName.toLowerCase() );
    if ( objElement instanceof List )
      return (List<VwXmlElement>)objElement;
    
    
    // only one so create a list of one
    
    List<VwXmlElement> listElements = new ArrayList<VwXmlElement>();
    listElements.add( (VwXmlElement)objElement );
    
    return listElements;
    
  }
  
  
  /**
   * Adds an object to this object. If one exists for this id then it becomes a list of data elements
   * The object is stored in an VwXmlElement Object.
   *
   * @param strID The id of the object used for lookup
   * @param objValue - The data object to be added
   *
   */
  public void add( String strID, Object objValue )
  {

    int nPos = strID.indexOf( ':' );

    VwXmlElement elementNew = new VwXmlElement( strID, strID.substring( ++nPos ), objValue );

    add( elementNew );

  } // end add()

  /**
   * Adds an object to this object. If one exists for this id then it becomes a list of data elements
   * The object is stored in an VwXmlElement Object.
   *
   * @param strID The id of the object used for lookup
   * @param strData - The data object to be added
   *
   */
  public void add( String strID, String strData )
  {

    int nPos = strID.indexOf( ':' );

    VwXmlElement elementNew = new VwXmlElement( strID, strID.substring( ++nPos ), strData );

    add( elementNew );    

  } // end add()




  /**
   * Adds a a byte array to the dataobject and base64 encodes the data
   *
   * @param strID The id of the object used for lookup
   * @param abData - The byte array object to be added
   *
    */
  public void add( String strID, byte[] abData )
  {
    int nPos = strID.indexOf( ':' );

    String str64 = new String( com.vozzware.util.VwBase64.encode( abData ) );
    VwXmlElement element = new VwXmlElement( strID, strID.substring( ++nPos ), abData );
    add( element );

  } // end add()
  
  

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
  public Attributes getAttributes( String strID )
  {
    if ( strID == null )
      return null;

    strID = strID.toLowerCase();

    VwXmlElement element = (VwXmlElement)m_mapContent.get( strID);
    if ( element == null )
      return null;

    return element.getAttributes();

  } // end getAttributeList()


  /**
   * Determins if the element fro the id has attrubutes
   * @param strID The id an an element in this object
   * @return true if the element exists and it has attributes
   *
   */
  public boolean hasAttributes( String strID )
  {

    strID = strID.toLowerCase();

    VwXmlElement element = (VwXmlElement)m_mapContent.get( strID);
    if ( element == null )
      return false;

    return element.hasAttributes();

  } // end getAttributeList()


  /**
   * Sets the attribute list for the specified element
   *
   * @param strID The element name to set the attribute list for
   * @param listAttr The VwAttribute list to set.<br> This method replaces an existing
   * attribute list if one already exists for the element id
   *
   */
  public void setAttributes( String strID, Attributes listAttr )
  {

    int nPos = strID.indexOf( ':' );

    VwXmlElement element = (VwXmlElement)m_mapContent.get( strID.toLowerCase() );
    if ( element == null )
    {
      element = new VwXmlElement( strID, strID.substring( ++nPos ), listAttr );
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

    strID = strID.toLowerCase();

    VwXmlElement element = (VwXmlElement)m_mapContent.get( strID );
    if ( element == null )
      return null;

    Attributes listAttr =  element.getAttributes();

    if ( listAttr == null )
      return null;            // No attributes to check for

    return listAttr.getValue( strAttrName );              // no attribute found

  } // end getAttribute()

 
  
  /**
   * Retrieves a data element with the given character ID, as a byte array
   *
   * @param strID - A string with the character ID of the data element to retrieve
   *
   * @return The data element with the given ID as a byte array
   *
   */
  public final byte[] getByteArray( String strID )  throws VwMultipleElemenException
  {
    VwXmlElement element = getElement( strID );

    if ( element == null )
      return null;

    return element.getValue().getBytes();
  }



  /**
   * Removes the element from the data object
   *
   * @param element The VwXmlElement instance to remove.
   */
  public void remove( VwXmlElement element )
  {
    String strID = element.getName();
    
    strID = strID.toLowerCase();

    Object obj = m_mapContent.remove( strID );


  } // end remove
  /**
   * Removes any object type associated with this key
   *
   * @param strID The id if the object to remove
   */
  public void remove( String strID )
  {
    strID = strID.toLowerCase();

    m_mapContent.remove( strID );

  }

  /**
   * Retrieves a data element with the given character ID, as a Boolean
   *
   * @param strID - The character ID of the data element to retrieve
   *
   * @return The data element converted to a Boolean, if possible
   *
   */
  public final Boolean getBoolean( String strID ) throws VwMultipleElemenException
  {
    VwXmlElement element = getElement( strID );

    if ( element == null )
      return null;

    return new Boolean( element.getValue() );

  }

  /**
   * Retrieves a data element with the given character ID, as a Byte
   *
   * @param strID - The character ID of the data element to retrieve
   *
   * @return The data element converted to a Byte, if possible
   *
   */
  public final Byte getByte( String strID )  throws VwMultipleElemenException
  {
    VwXmlElement element = getElement( strID );

    if ( element == null )
      return null;

    return new Byte( element.getValue() );

  }



  /**
   * Retrieves a data element with the given character ID, as a Character
   *
   * @param strID - The character ID of the data element to retrieve
   *
   * @return The data element converted to a Character, if possible
   *
   */
  public final Character getCharacter( String strID )  throws VwMultipleElemenException
  {
    VwXmlElement element = getElement( strID );

    if ( element == null )
      return null;

    return new Character( element.getValue().charAt( 0 ) );

  }



  /**
   * Retrieves a data element with the given character ID, as a String
   *
   * @param strID - The character ID of the data element to retrieve
   *
   * @return The data element converted to a String, if possible
   *
   */
  public final String getString( String strID )  throws VwMultipleElemenException
  {
    VwXmlElement element = getElement( strID );

    if ( element == null )
      return null;

    return element.getValue();

  }



  /**
   * Decodes the the data to a string
   *
   * @param strID - The character ID of the data element to retrieve
   *
   * @return The data element in string form
   *
   */
  public final String getEncoded( String strID ) throws VwMultipleElemenException
  {
    strID = strID.toLowerCase();

    Object obj = m_mapContent.get( strID );

    if ( obj == null )
      return null;

    if ( obj instanceof VwXmlElement )
    {
       obj = ((VwXmlElement)obj).getObject();

       return new String( VwBase64.decode( obj.toString().getBytes() ) );

    }

    return null;

  } // end getEncoded


  /**
   * Retrieves a data element with the given character ID, as a Short
   *
   * @param strID - The character ID of the data element to retrieve
   *
   * @return The data element converted to a Short, if possible
   *
   */
  public final Short getShort( String strID )  throws VwMultipleElemenException
  {
    VwXmlElement element = getElement( strID );

    if ( element == null )
      return null;

    return new Short( element.getValue() );

  }


  /**
   * Retrieves a data element with the given character ID, as a Integer
   *
   * @param strID - The character ID of the data element to retrieve
   *
   * @return The data element converted to a Integer, if possible
   *
   */
  public final Integer getInteger( String strID )  throws VwMultipleElemenException
  {
    VwXmlElement element = getElement( strID );

    if ( element == null )
      return null;

    return new Integer( element.getValue() );

  }


  /**
   * Retrieves a data element with the given character ID, as a Long
   *
   * @param strID - The character ID of the data element to retrieve
   *
   * @return The data element converted to a Long, if possible
   *
   */
  public final Long getLong( String strID )  throws VwMultipleElemenException
  {
    VwXmlElement element = getElement( strID );

    if ( element == null )
      return null;

    return new Long( element.getValue() );

  }


  /**
   * Retrieves a data element with the given character ID, as a Float
   *
   * @param strID - The character ID of the data element to retrieve
   *
   * @return The data element converted to a Float, if possible
   *
   */
  public final Float getFloat( String strID )  throws VwMultipleElemenException
  {
    VwXmlElement element = getElement( strID );

    if ( element == null )
      return null;

    return new Float( element.getValue() );

  }


  /**
   * Retrieves a data element with the given character ID, as a Double
   *
   * @param strID - The character ID of the data element to retrieve
   *
   * @return The data element converted to a Double, if possible
   *
   */
  public final Double getDouble( String strID )  throws VwMultipleElemenException
  {
    VwXmlElement element = getElement( strID );

    if ( element == null )
      return null;

    return new Double( element.getValue() );

  }


  /**

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
   * Finds the data object by its name or by the nameof one of its children
   * @param strName The name of the xml element or the name of one of is chile elements
   * @return
   */
  public List<VwXmlDataObj> find( String strName )
  {
    List<VwXmlDataObj>listFound = new ArrayList<VwXmlDataObj>(  );
    findObj( this, strName, listFound );
    return listFound;
  }


  /**
   * Find the data object that is or contains the element key requested
   * @param objToSearch
   * @param strQName
   * @return
   */
  private boolean findObj( VwXmlDataObj objToSearch, String strQName, List<VwXmlDataObj>listFound )
  {
    if ( objToSearch == null )
       return true;                   // All nodes searched

    if ( objToSearch.getQName().equalsIgnoreCase( strQName ))
    {
      listFound.add( objToSearch );
      return false;                   // Object found is a parent object, so return the compete child list of this parent
    }

    if ( objToSearch.contains( strQName ))
    {
      listFound.add( objToSearch );
      return false;                   // Object found is a parent object, so return the compete child list of this parent
    }

    List<VwXmlDataObj> listChildren = objToSearch.getChildren();

    if ( listChildren == null )
      return true;

    for( VwXmlDataObj childObj : listChildren )
    {
      boolean fDone = findObj( childObj, strQName, listFound  );

      if ( fDone )
        return true;
    }

    return false;    // not found
  }
}
