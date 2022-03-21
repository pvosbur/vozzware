/*
 ===========================================================================================

 
 Copyright(c) 2000 - 2006 by

 V o z z W a r e   L L C (Vw)

 All Rights Reserved

 Source Name: VwPreferencesMgr.java

 Create Date: Jun 16, 2006

 THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
 PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
 CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.


 ============================================================================================
 */
package com.vozzware.util;

import com.vozzware.xml.VwBeanToXml;
import com.vozzware.xml.VwJsonToBean;
import com.vozzware.xml.VwXmlToBean;
import org.xml.sax.InputSource;

import javax.xml.schema.util.XmlDeSerializer;
import javax.xml.schema.util.XmlFeatures;
import java.io.File;
import java.io.FileReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class VwPreferencesMgr
{
  
  private VwPreferences m_pref;

  private Map<String,VwPreferenceGroup> m_mapPrefGroups = new HashMap<String,VwPreferenceGroup>();

  private Map<VwPreferenceGroup,Map<String,VwPreference>> m_mapPrefByGroup = new HashMap<VwPreferenceGroup,Map<String,VwPreference>>();

  private Map<String,String> m_mapValuesByName = new HashMap<String,String>();

  private File m_filePref;

  private String m_strPrefPath;

  private long m_lLastModified = 0;

  private VwResourceStore    m_resStore;


  /**
   * Default constrictor that creates and empty VwPreferences object
   * @throws Exception
   */
  public VwPreferencesMgr() throws Exception
  {
    m_resStore = VwResourceStoreFactory.getInstance().getStore();
    m_pref = new VwPreferences();
  }

  /**
   * Constructor
   * 
   * @param filePreferenceFile The absolute path to the preferences file.
   * 
   * @throws Exception
   */
  public VwPreferencesMgr( File filePreferenceFile, boolean fCreate ) throws Exception
  {
    this();

    m_filePref = filePreferenceFile;

    if ( !m_filePref.exists() && !fCreate )
      throw new VwMissingResourceException( m_strPrefPath, "" );

   } // end VwPreferencesMgr()


  /**
   * Create preferences  mgr from a VwPreferenceObject
   * @param prefs
   * @throws Exception
   */
  public VwPreferencesMgr( VwPreferences prefs ) throws Exception
  {
    this();

    m_pref = prefs;

    buildMaps();


   } // end VwPreferencesMgr()

  public VwPreferencesMgr( File filePreferences ) throws Exception
  {
    this();

    loadPreferences( filePreferences );

   } // end VwPreferencesMgr()


  /**
   * Initialze with a serialized XML or JSON string
   * @param strPreferences
   * @throws Exception
   */
  public VwPreferencesMgr( String strPreferences ) throws Exception
  {
    this();

    loadPreferences( strPreferences );

   } // end VwPreferencesMgr()



  /**
   * Load/reload preferences from either an XML or JSON string
   * 
   * @throws Exception
   */
  public void loadPreferences( String strPreferences ) throws Exception
  {

    // Are we de-serializing a String
    if ( strPreferences != null )
    {


      if ( strPreferences.startsWith( "<" ))  // this is XML
      {
        VwXmlToBean xtb = new VwXmlToBean();
        xtb.setFeature( XmlDeSerializer.ATTRIBUTE_MODEL, true );

        m_pref = (VwPreferences)xtb.deSerialize( new InputSource( new StringReader( strPreferences ) ),
                 VwPreferences.class, m_resStore.getDocument( "VwPreferences.xsd" ) );
      }
      else                                    // assume This is JSON
      {

        VwJsonToBean jtb = new VwJsonToBean();
        jtb.setFeature( XmlFeatures.ATTRIBUTE_MODEL, true );

        Object obj = jtb.deSerialize( new InputSource( new StringReader( strPreferences )),VwPreferences.class, m_resStore.getDocument( "VwPreferences.xsd" )  );



        m_pref = (VwPreferences)obj;
      }

      buildMaps();
      return;

    }


  }

  public void loadPreferences( File  filePreferences ) throws Exception
  {

    VwXmlToBean xtb = new VwXmlToBean();
    xtb.setFeature( XmlDeSerializer.ATTRIBUTE_MODEL, true );

    m_filePref = filePreferences;

        // If the file doesn't yet exist, user set create flag to true, so just create empty VwPreferences.
    if ( !filePreferences.exists() )
    {
      m_pref = new VwPreferences();
      return;
    }
    // See if file was changed
    if ( m_filePref.lastModified() == m_lLastModified )
      return;

    m_lLastModified = m_filePref.lastModified();

    m_pref = (VwPreferences)xtb.deSerialize( new InputSource(  new FileReader( m_filePref  ) ),
               VwPreferences.class, m_resStore.getDocument( "VwPreferences.xsd" ) );

    buildMaps();


  }

  /**
   * Build preference group and preference maps for fast lookup
   * 
   * @throws Exception
   */
  private void buildMaps() throws Exception
  {
    if ( m_pref.getPreferenceGroup() == null )
      return;

    for ( VwPreferenceGroup prefGroup : m_pref.getPreferenceGroup() )
    {
       m_mapPrefGroups.put( prefGroup.getName().toLowerCase(), prefGroup );

      List<VwPreference> listPreferences = prefGroup.getPreference();
      if ( listPreferences == null )
        throw new Exception( "preferenceGroup '" + prefGroup.getName() + "' must have at least one preference entry" );

      Map<String,VwPreference> mapPrefs = new HashMap<String,VwPreference>();
      m_mapPrefByGroup.put( prefGroup, mapPrefs );

      for ( VwPreference pref : listPreferences )
      {
        String strPrefValue = VwExString.expandMacro( pref.getValue(), m_mapValuesByName );
        pref.setValue( strPrefValue );
        m_mapValuesByName.put( pref.getName(), strPrefValue );
        mapPrefs.put( pref.getName().toLowerCase(), pref );
      }
    }
  } // end buildMaps()


  public VwPreferences getPreferences()
  {
    return m_pref;
  }

  public void setPreferences( VwPreferences pref ) throws Exception
  {
    m_pref = pref;
    buildMaps();
  }

  /**
   * Gets an VwPreferenceGroup by group name
   * 
   * @param strName
   *          The name of the preference group to retrieve
   * 
   * @return The preference group object or null if the named group does not
   *         exist
   */
  public VwPreferenceGroup getGroup( String strName )
  {  return (VwPreferenceGroup)m_mapPrefGroups.get( strName.toLowerCase() ); }

  
  /**
   * Gets the preference value (if it exists from the preference group
   * specified)
   * 
   * @param strGroupName
   *          The name of the preference group (preference parent)
   * @param strPrefName
   *          The name of the preference value to retrieve
   * @return The preference value or null if the named preference does not exist
   */
  public String getPreference( String strGroupName, String strPrefName )
  {
    VwPreferenceGroup prefGroup = (VwPreferenceGroup)m_mapPrefGroups.get( strGroupName.toLowerCase() );
    return getPreference( prefGroup, strPrefName );
    
  } // end getPreference()
  
  /**
   * Gets the preference value (if it exists from the preference group
   * specified)
   * 
   * @param prefGroup The preference group for the preference requested
   * @param strPrefName
   *          The name of the preference value to retrieve
   * @return The preference value or null if the named preference does not exist
   */
  public String getPreference( VwPreferenceGroup prefGroup, String strPrefName )
  {

    if ( prefGroup == null )
      return null;

    Map<String,VwPreference> mapPrefs = m_mapPrefByGroup.get( prefGroup );

    VwPreference pref = (VwPreference)mapPrefs.get( strPrefName.toLowerCase() );

    if ( pref == null )
      return null;

    return pref.getValue();

  } // end getPreference()
  


  /**
   * Sets the named preference in the preference group to the value specified
   * 
   * @param strGroupName The name of the group the preference is in
   * @param strPrefName The name of the preference to set
   * @param prefValue The value of the preference
   */
  public void setPreference( String strGroupName, String strPrefName, String prefValue )
  {
    VwPreferenceGroup prefGroup = (VwPreferenceGroup)m_mapPrefGroups.get( strGroupName.toLowerCase() );

    if ( prefGroup == null )
    {
      prefGroup = new VwPreferenceGroup();
      prefGroup.setName( strGroupName );
      addPreferenceGroup( prefGroup );
    }
    
    Map<String,VwPreference> mapPrefs = m_mapPrefByGroup.get( prefGroup );

    VwPreference pref = (VwPreference)mapPrefs.get( strPrefName.toLowerCase() );
    
    if ( pref == null )
    {
      pref = new VwPreference();
      pref.setName( strPrefName );
      mapPrefs.put( strPrefName.toLowerCase(), pref );
      prefGroup.addPreference( pref );
    }
    
    pref.setValue( prefValue );

    return;

  } // end setPreference()


  /**
   * Return a preference value using a preference key - one static key strings
   * @param strPrefKey The preference key to obtaing the value for
   * @return The preference value or null if key doesent exists
   */
  public String getPreferenceByKey( String strPrefKey )
  {

    int nPos = strPrefKey.indexOf( ':' );
    if ( nPos < 0 )
      throw new RuntimeException(  "****INVALID PREFERENCE KEY MISSING the ':' SEPERATOR" );

    String strPrefGroupName = strPrefKey.substring( 0, nPos );
    String strPrefName = strPrefKey.substring( ++nPos );

    return getPreference( strPrefGroupName, strPrefName );

  }


  /**
   * Return a preference value using a preference key - one static key strings
   * @param strPrefKey The preference key to obtaing the value for
   * @return The preference value or null if key doesent exists
   */
  public void setPreferenceByKey( String strPrefKey, String strPrefVal )
  {

    int nPos = strPrefKey.indexOf( ':' );
    if ( nPos < 0 )
      throw new RuntimeException(  "****INVALID PREFERENCE KEY MISSING the ':' SEPERATOR" );

    String strPrefGroupName = strPrefKey.substring( 0, nPos );
    String strPrefName = strPrefKey.substring( ++nPos );

    setPreference( strPrefGroupName, strPrefName, strPrefVal );

  }

  /**
   * Adds an VwPreferenceGroup by group name
   * 
   * @param  newGroup The preference group to add
   * 
   */
  public void addPreferenceGroup( VwPreferenceGroup newGroup )
  {
    List<VwPreferenceGroup> listPrefGroups = m_pref.getPreferenceGroup();
    
    if ( listPrefGroups == null  )
    {
      listPrefGroups = new ArrayList<VwPreferenceGroup>();
      m_pref.setPreferenceGroup( listPrefGroups );
       
    }

    Map<String,VwPreferenceGroup> prefGroups = (Map<String, VwPreferenceGroup>) m_mapPrefGroups.get( newGroup.getName().toLowerCase());
    if (prefGroups == null)
    {
      m_mapPrefGroups.put( newGroup.getName().toLowerCase(), newGroup );
      m_mapPrefByGroup.put( newGroup, new HashMap<String,VwPreference>() );
    
      listPrefGroups.add( newGroup ); // group not there - go ahead and add
    }
    
  } // end addPreferenceGroup()
  

  /**
   * Removes an VwPreferenceGroup by group name
   * 
   * @param strName The name of the preference group to remove
   * 
   */
  public void removePreferenceGroup( String strName )
  {
    List<VwPreferenceGroup> preferences = m_pref.getPreferenceGroup();
    
    if ( preferences == null )
      return;
    Iterator<VwPreferenceGroup> iPrefGroups = preferences.iterator();

    while ( iPrefGroups.hasNext() )
    {
      VwPreferenceGroup group = iPrefGroups.next();
      String groupName = group.getName();
      if ( groupName.equalsIgnoreCase( strName ) )
      {
        preferences.remove( group );
        break;
      }
    }

  } // end removePreferenceGroup()

  
  /**
   * Returns absolute path to preference file
   * @return
   */
  public String getPrefPath()
  { return m_strPrefPath; }

  
  /**
   * Saves the VwPreferences object back to xml and saves it on disk
   * 
   * @exception Exception if any io errors occur
   */
  public void save() throws Exception
  {

    String strPrefAsXML = toXML();
    VwFileUtil.writeFile( m_filePref, strPrefAsXML );

  }

  /**
   * Seializes The VwPreferences object to s String
   * @return   An XML String which is the serialized VwPreferences object
   * @throws Exception
   */
  public String toXML() throws Exception
  {
    VwBeanToXml btx = new VwBeanToXml( null, null, true, 0 );
    btx.addSchema( m_resStore.getDocument( "VwPreferences.xsd" ), VwPreferences.class.getPackage() );
    btx.setFeature( XmlFeatures.ATTRIBUTE_MODEL, true );

    btx.setContentMethods( VwPreferences.class, "getPreferenceGroup" );
    btx.setContentMethods( VwPreferenceGroup.class, "getPreference" );
    m_lLastModified = 0;

    return btx.serialize( null, m_pref );

  }


  /**
   * Serializes VwPreferences graph as JSON string
   * @return
   * @throws Exception
   */
  public String toJSON() throws Exception
  {
    return VwJsonUtils.toJson( m_pref );

  }


  /**
   * Creates VwPreferences object from a JSON string
   * @param strJSON The JSON string to de-serialize
   *
   * @return The VwPreferences Object
   * @throws Exception
   */
  public VwPreferences fromJSON( String strJSON ) throws Exception
  {
    Map<String,Class> mapElementHandlers = new HashMap<String, Class>(  );

    mapElementHandlers.put( "preferenceGroup", VwPreferenceGroup.class );
    mapElementHandlers.put( "preference",VwPreference.class );

    m_pref = (VwPreferences)VwJsonUtils.fromJson( strJSON, VwPreferences.class, mapElementHandlers );

    return m_pref;
  }

 } // end class VwPreferencesMgr()

// *** End of VwPreferencesMgr.java ***
