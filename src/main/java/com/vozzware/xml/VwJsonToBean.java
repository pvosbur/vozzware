package com.vozzware.xml;

import org.xml.sax.InputSource;

import java.io.StringReader;
import java.util.HashMap;

public class VwJsonToBean extends VwXmlToBean
{
  public VwJsonToBean() throws Exception
  {
    super();
    
    setDateFormat( "EEE MMM dd yyyy HH:mm:ss zzz" );
    setClearDirtyFlag( false );
  }

  public Object deSerialize( String strJsonObject, Class clsTopLevel ) throws Exception
  {
    return super.deSerialize( new InputSource( new StringReader( strJsonObject ) ), clsTopLevel  );

  }


  @Override
  public void parse( InputSource inpSrc, Object objTopLevelInstance ) throws Exception
  {
    m_objTopLevelInstance = objTopLevelInstance;
    m_strTopLevelClassName = getObjName( objTopLevelInstance );
    
    // Get the map of methods for the top level class

    //m_mapCurObjMethods = (HashMap)s_mapObjects.get( getObjName( objTopLevelInstance ).toLowerCase() );
  
    m_mapCurObjMethods = (HashMap)s_mapObjects.get( m_strTopLevelClassName.toLowerCase() );

    if ( m_strTopLevelElementName != null )
    {
      m_strTopLevelClassName = m_strTopLevelElementName;
    }

    
    if ( m_mapCurObjMethods == null || !m_mapCurObjMethods.containsKey( m_strTopLevelClassName.toLowerCase() ) )
    {
      introspect( m_clsTopLevelClass );

      m_mapCurObjMethods = (HashMap)s_mapObjects.get( m_strTopLevelClassName.toLowerCase() );
    }

    VwJsonReader rdr = new VwJsonReader( m_strTopLevelClassName );
    rdr.setContentHandler( this );
    rdr.parse( inpSrc );
  }
  

}
