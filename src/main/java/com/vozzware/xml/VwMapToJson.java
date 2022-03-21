/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwBeanToXml.java

Create Date: Apr 11, 2006
============================================================================================
*/
package com.vozzware.xml;

import com.vozzware.util.VwBeanUtils;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;


/**
 * This class converts a bean or a list of beans (of the same class type) to an json document
 */
public class VwMapToJson
{

  private String m_strDefaultForNulls;   // Default tag data for props that return nulll

  private String m_strTopLevelClassName; // Top level bean class json is being genned from

  private String m_strCurPropName;

  private String m_strTrue = "true";
  private String m_strFalse = "false";

  private VwJsonWriter m_jsonWriter;                 // json formatter class
  private boolean m_fLowerCaseFirstCarClassNames = true;
  private boolean m_fForceArray = false;

  private String m_strDateFormat = "EEE MMM dd yyyy HH:mm:ss zzz";

  private Map<String,String> m_mapIgnoreProps = null;
  private Map<String,String> m_mapPropAliases = null;

  /**
   * This class makes an enumeration look like an iterator
   */
  class VwEnumerator implements Iterator
  {
    Enumeration m_enum;

    VwEnumerator ( Enumeration en )
    { m_enum = en; }

    public boolean hasNext()
    { return m_enum.hasMoreElements(); };

    public Object next()
    { return m_enum.nextElement(); }

    public void remove()
    {}

  } // end class Enumerator


  /**
   * This class uses the Iterator interface to access an array
   */
  class VwArrayIterator implements Iterator
  {
    Object[] m_aObj;         // Object array to iterate through
    int    m_ndx = 0;        // Array index

    VwArrayIterator( Object[] aObj )
    { m_aObj = aObj; }

    public boolean hasNext()
    { return m_ndx < m_aObj.length; };

    public Object next()
    { return m_aObj[ m_ndx++ ]; }

    public void remove()
    {}

  } // end class VwArrayIterator


  /**
   * Default Constructor
   */
  public VwMapToJson()
  { this(  null, false, 0 ); }

  /**
   * Constructor
   *
   * @param strDefaultForNulls The default placholder string when properties of a bean return
   * null. If this parameter is null, then the property will be omitted from the json document.
   *
   * @param fFormatted if true, the json document will insert CR/LF and indentation characters
   * for tag parentage.
   *
   * @param nIndentLevel The indentation level to start the formatting in. Each level nbr results
   * in an indentation of 2 spaces. This paramter only has affect if fFormatted is true. 0 should
   * be specified for the standard indentation.
   */
  public VwMapToJson( String strDefaultForNulls,
                      boolean fFormatted, int nIndentLevel )
  {

    m_strDefaultForNulls = strDefaultForNulls;

    m_jsonWriter = new VwJsonWriter( fFormatted, nIndentLevel );
 
  } // end VwBeanToXml()

  public void setDateFormat( String strDateFormat )
  {
    m_strDateFormat = strDateFormat;

  }

  public String getDateFormat()
  { return m_strDateFormat; }


  /**
   * Sets the formatted output feature which is off by default. Formatted output adds CRLF pairs as well as indentaion
   * of child elements.
   * 
   * @param fFormatOutput true to turn on formatting, false to turn off
   * 
   * @param nStartingIndentLevel The starting indentaion level. each level nbr adds two spaces of indentaion
   */
  public void setFormattedOutput( boolean fFormatOutput, int nStartingIndentLevel )
  { 
    m_jsonWriter.setFormattedOutput( fFormatOutput );
    m_jsonWriter.setLevel( nStartingIndentLevel );
    
  }


  public void setIgnoreProps( Map<String,String> mapIgnoreProps )
  {
    m_mapIgnoreProps = mapIgnoreProps;

  }

  /**
   * The default value to use for null method values. The default behaviour is to not output json for properties
   * that return null.
   * 
   * @param strDefaultForNulls The value to output for properties that return null during serialization
   */
  public void setDefaultForNulls( String strDefaultForNulls )
  { m_strDefaultForNulls = strDefaultForNulls; }
  

  /**
   * Forces the Json output string to be an array regardless of the listbeans size
   * @param fForceArray True to trun off force, false (the default) to go by the size of the bean list passed
   */
  public void setForceArray( boolean fForceArray )
  { m_fForceArray = fForceArray; }



  /**
   * Convert a list of beans to a Json string
   *
   * @param mapForJson The map to serialize to JSON
   */
  public String serialize( Map<String,Object> mapForJson ) throws Exception
  {
     mapToJson( null,mapForJson );

    return m_jsonWriter.toString();

  } // end toXml()


  /**
   * Make a Json primitive array
   * @param listValues
   * @return
   */
  private String doPrimCollection( List<?> listValues )
  {


    Object[] aValues = new Object[ listValues.size() ];

    listValues.toArray( aValues );

    boolean fNeedQuotes = false;

    Class clsEntry = listValues.get( 0 ).getClass();

    if (  clsEntry == String.class || clsEntry == Date.class )
    {
      fNeedQuotes = true;

    }

    m_jsonWriter.makePrimitiveArray(  aValues, fNeedQuotes );

    return m_jsonWriter.toString();


  }

  /**
   * Sets the json tag data string that will be generated for boolean properties that return a value of true
   *
   * @param strTrueValue The value that will be generated for true boolean properties
   */
  public void setTrueBooleanTransform( String strTrueValue )
  { m_strTrue = strTrueValue; }


  /**
   * Gets current string transform value for a true boolean state
   *
   */
  public String getTrueBooleanTransform()
  { return m_strTrue; }


  /**
   * Sets the json tag data string that will be generated for boolean properties that return a value of false
   *
   * @param strFalseValue The value that will be generated for false boolean properties
   */
  public void setFalseBooleanTransform( String strFalseValue )
  { m_strFalse = strFalseValue; }


  /**
   * Gets current string transform value for a false boolean state
   *
   */
  public String getFalseBooleanTransform()
  { return m_strFalse; }
  
  /**
   * Sets a Map of bean property aliases to use. The map key specifies the name of the bean
   * property (without the set or get ). The map value is the json tag that will be generated
   * for that property.
   *
   * @param mapPropAliases A Map of bean property aliases
   */
  public void setBeanPropertyAliases( Map mapPropAliases )
  { m_mapPropAliases = mapPropAliases; }


  /**
   * Recursive method that builds an json string Map objects
   *
   * @param strObjectName The bean object whose property values will be used to build the vector
   * @param mapToSerialize Array of property descriptors for the objBean
    *
   */
  private void mapToJson( String strObjectName, Map<String,Object> mapToSerialize )
    throws Exception
  {
    m_jsonWriter.beginObject( strObjectName );

    boolean fIgnorePropName = false;

    // Dump this object's properties
    for ( String strPropName : mapToSerialize.keySet() )
    {

      Object propVal = mapToSerialize.get( strPropName );

      if ( propVal instanceof Map )
      {
        mapToJson( strPropName, (Map<String,Object>)propVal );
        continue;
       }

      if ( m_mapIgnoreProps != null && m_mapIgnoreProps.containsKey( strPropName  ) )
      {
        continue;
      }

      // Determine object type
      if ( propVal == null )
      {
        if ( m_strDefaultForNulls != null && !fIgnorePropName )
        {
          m_jsonWriter.addProperty( strPropName, m_strDefaultForNulls, true );
        }

        //no default wase specified, then dont add property
        continue;

      } // end if ( objVal == null )
      
      if ( propVal instanceof String )
      {
        propVal =( (String)propVal).trim();

        if ( ((String)propVal).length() == 0 )
        {

          if ( m_strDefaultForNulls != null && !fIgnorePropName )
          {
            m_jsonWriter.addProperty( strPropName, m_strDefaultForNulls, true );
          }

          continue;
        }

      }


      if ( fIgnorePropName )
      {
        strPropName = null;
      }
      
      // See what kind of return object we have
      if ( propVal instanceof String )
      {
        handleSimpleType( propVal, strPropName );
      }
      else
      if ( propVal instanceof List )
      {
        handleCollectionType( strPropName, (List)propVal );

      }

    } // end for()

    m_jsonWriter.endObject();

  } // end  beanToXml()

  /**
   * Handles collection types
   *
   * @param strPropName The property name in the map
   * @param listValues The collection of value
   */
  private void handleCollectionType( String strPropName, List listValues ) throws Exception
  {
    if ( listValues.size() == 0  )
    {
      return;

    }

    Object objCollectionVal = listValues.get( 0 );

    Class clsCollection = objCollectionVal.getClass();

    if ( VwBeanUtils.isSimpleType( clsCollection ) )
    {
      handlePrimitiveCollection( strPropName, listValues );
    }
    else
    {
      if ( objCollectionVal instanceof Map )
      {
        handleMapCollection( strPropName, listValues );
      }
    }
  }

  private void handleMapCollection( String strPropName, Collection collection )  throws Exception
  {
    int nLen = collection.size();
    Object[] aValues = new Object[ nLen ];

    int x = -1;

    m_jsonWriter.beginArray( strPropName );
    for( Object objVal : collection )
    {
      mapToJson( null, (Map)objVal );

      aValues[ ++x ] = objVal;
    }

    m_jsonWriter.endArray();

  }

  /**
   *
   * @param strPropName
   * @param collection
   */
  private void handlePrimitiveCollection( String strPropName, Collection collection )
  {

    int nLen = collection.size();
    Object[] aValues = new Object[ nLen ];
    String strTag = null;

    int x = -1;

    for( Object objVal : collection )
    {
      aValues[ ++x ] = objVal;

      if ( strTag == null )
      {
        Class clsType = objVal.getClass();
        if ( clsType == String.class )
        {
          strTag = "string";
        }
        else
        if ( clsType == boolean.class || clsType == Boolean.class )
        {
          strTag = "boolean";
        }
        else
        if ( clsType == byte.class || clsType == Byte.class)
        {
          strTag = "byte";
        }
        else
        if ( clsType == Character.class || clsType == char.class)
        {
          strTag = "char";
        }
        else
        if ( clsType == short.class || clsType == Short.class)
        {
          strTag = "short";
        }
        else
        if ( clsType == int.class || clsType == Integer.class)
        {
          strTag = "int";
        }
        else
        if ( clsType == long.class || clsType == Long.class)
        {
          strTag = "long";
        }
        else
        if ( clsType == float.class || clsType == Float.class )
        {
          strTag = "float";
        }
        else
        if ( clsType == double.class || clsType == Double.class)
        {
          strTag = "double";
        }

      }

    } // end for()


    m_jsonWriter.addArray( strPropName, aValues, strTag.equals( "string" ) );

  }

  /**
   * Handle a primitive array
   * @param objVal
   * @param strPropName
    */
  private void handlePrimArray( Object objVal, String strPropName )
  {
    m_jsonWriter.addArray( strPropName, objVal,  objVal.getClass().equals( String[].class ) );

  } // end handlePrimArray()


  /**
   * Handle a primitive (simple type) property
   * @param objVal  The property value
   * @param strPropName  the property name
   */
  private void handleSimpleType( Object objVal, String strPropName )
  {
    String strText = null;

    if ( VwBeanUtils.isDateType( objVal ) )
    {
      SimpleDateFormat format =
            new SimpleDateFormat( m_strDateFormat );

      if ( m_strDateFormat.endsWith( "Z'" ))
      {
        format = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'" );
        TimeZone tz = TimeZone.getTimeZone( "UTC" );
        format.setTimeZone( tz );

      }

      strText = format.format( objVal );

    }
    else
    {
     strText = objVal.toString();      // Simple types dump property name and value
    }


    if ( objVal instanceof Boolean )
    {
      if ( strText.equals( "true" ) )
        strText = m_strTrue;
      else
        strText = m_strFalse;
    }

    m_jsonWriter.addProperty( strPropName, strText, (objVal instanceof String || VwBeanUtils.isDateType( objVal ) ) );

  } // end handleSimpleType()



  /**
   * Determins if an array object is an array of primitive types
   */
  public static boolean isPrimArray( Object objArray )
  { return isPrimArray( objArray.getClass() ); }


} // end class VwBeanToXml{}


// *** End of VwBeanToXml
