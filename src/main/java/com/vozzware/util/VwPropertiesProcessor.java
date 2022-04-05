/*
============================================================================================

    Source File Name: VwPropertiesProcessor

    Author:           petervosburgh

    Date Generated:   4/3/22

    Time Generated:   9:19 AM

============================================================================================
*/
package com.vozzware.util;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

/**
 * This class manages property files that are not in a classpath. It requires a url to a properties file
 */
public class VwPropertiesProcessor
{

  private Properties m_props;

  /**
   *
   * @param urlProps
   * @throws Exception
   */
  public VwPropertiesProcessor( URL urlProps ) throws Exception
  {
    InputStream insProps;

    try
    {
      insProps = urlProps.openStream();
    }
    catch( FileNotFoundException fne )
    {
      throw new Exception ("Path to properties file: " + urlProps.getFile().substring(3 ) + " does not exist" );

    }
    catch( Exception ex )
    {
      throw new Exception ("Error opening url tp properties file: " + urlProps.getFile().substring(3 ) + "Reason: " + ex.getMessage() );

    }

    m_props = new Properties();
    m_props.load( insProps );

  } // end constructor()

  /**
   * Gets the property value is key exists
   *
   * @param strKey The property key to retrieve
   * @return The property value ot null if property key does not exist
   */
  public String getString( String strKey )
  {
    return m_props.getProperty( strKey  );

  } // end getString()

  /**
   * Gets property value to the specified default value if property does not exists
   *
   * @param strKey The property key of the value to retrieve
   * @param strDefault  The default value if key does not exists
   * @return
   */
  public String getString( String strKey, String strDefault )
  {
    return m_props.getProperty( strKey, strDefault  );

  } // end getString()

  public int getInt( String strKey ) throws Exception
  {
    String strVal  = getString( strKey );

    if ( strVal == null )
    {
      throw new Exception( "Property: " + strKey + " is null" );

    }

    return Integer.valueOf( strVal );

  } // end getInt()

  public int getInt( String strKey, int nDefault ) throws Exception
  {
    String strVal  = getString( strKey );

    if ( strVal == null )
    {
      return nDefault;

    }

    return Integer.valueOf( strVal );

  } // end getInt()

  /**
   * Returns property value as a double if valuew is a double
   *
   * @param strKey The property key of the value to retrieve
   * @return value as a double
   * @throws Exception If value returned is null
   */
  public double getDouble( String strKey ) throws Exception
  {
    String strVal  = getString( strKey );

    if ( strVal == null )
    {
      throw new Exception( "Property: " + strKey + " is null" );

    }

    return Double.valueOf( strVal );

  } // end getDouble()


  /**
   * 
   * @param strKey
   * @param dblDefault
   * @return
   * @throws Exception
   */
  public double getDouble( String strKey, double dblDefault ) throws Exception
  {
    String strVal  = getString( strKey );

    if ( strVal == null )
    {
      return dblDefault;

    }

    return Double.valueOf( strVal );

  } // end getDouble()


} // end VwPropertiesProcessor{}
