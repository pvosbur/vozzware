package com.vozzware.codegen;

import com.vozzware.util.VwLogger;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to generate data value objects (DVO)
 * @author petervosburghjr
 *
 */
public class VwDVOGen
{
  private   String                      m_strClassName;
  private   String                      m_strSuperClassName;
  
  private   boolean                     m_fImplementsSerializable = true;
  private   boolean                     m_fImplementsClonaeable = true;
  private   boolean                     m_fImplementsEquals = true;
  private   boolean                     m_fUseDirtyObjectDetection = true;
  private   boolean                     m_fAddToString = true;
 
  private   List<VwPropertyDefinition> m_listPropDefs;
  
  private   VwClassGen                 m_classGen;
  
  private   VwCodeOptions              m_codeOpts;
  
  private   String                      m_strPackage;
  private   String                      m_strOutputPath;
   
  private   List<String>                m_listInterfaces = new ArrayList<String>();
  private   List<String>                m_listImports = new ArrayList<String>();
  
  /**
   * Constructor
   * 
   * @param codeOpts The code options to use
   * @param strClassName The Dvo Class name
   * @param strSuperClassName The Dvo super class name (may be null)
   * @param strPackage The name of the package for the DVO
   * @param strOutputPath The start of the source directory
   */
  public VwDVOGen( VwCodeOptions codeOpts, String strClassName, String strSuperClassName, String strPackage,
                   String strOutputPath ) throws Exception
  {
    m_codeOpts = codeOpts;
    m_strClassName = strClassName;
    m_strSuperClassName = strSuperClassName;
    m_strOutputPath = strOutputPath;
    m_strPackage = strPackage;
    
    String strSuperImport = null;
    
    if ( m_strSuperClassName != null )
    {
      int nPos = m_strSuperClassName.lastIndexOf( '.' );
      if ( nPos > 0 )
      {
        strSuperImport = m_strSuperClassName;
        m_strSuperClassName = m_strSuperClassName.substring( ++nPos );
        
      }
    }
    
    m_classGen = new VwClassGen( m_codeOpts, m_strClassName, m_strSuperClassName,
                                  m_strPackage, null, VwClassGen.CLASS );
    
    if ( strSuperImport != null )
    {
      m_classGen.addImport( strSuperImport, null );
    }

  }
  
  
  public VwClassGen getClassGen()
  { return m_classGen; }
  

  /**
   * Adds additional interface
   * @param strInterFaceName The interface name to add
   */
  public void addInterface( String strInterFaceName )
  {
    if ( m_listInterfaces.indexOf( strInterFaceName ) < 0 )
    {
      m_listInterfaces.add( strInterFaceName );
    }
    
  }
  
  /**
   * Adds an import
   * @param strImport
   * @param strComment
   */
  public void addImport( String strImport, String strComment )
  {
    String strElement = strImport;
    if ( strComment != null )
    {
      strElement += ":" + strComment;
    }
    
    m_listImports.add( strElement );
    
  }


  /**
   * Generate the DVO
   * 
   * @param listPropDefs A list of property definitions

   * @throws Exception
   */
  public void genDvo( List<VwPropertyDefinition> listPropDefs, VwLogger logger ) throws Exception
  {
     m_listPropDefs = listPropDefs;
    
    if ( m_fImplementsSerializable )
    {
      m_classGen.addInterface( "Serializable" );
    }
      
    if ( m_fImplementsClonaeable)
    {
      m_classGen.addInterface( "Cloneable" );
    }

    
    for ( String strInterface : m_listInterfaces )
    {
      m_classGen.addInterface( strInterface );
    }
    
    if ( m_fUseDirtyObjectDetection && m_strSuperClassName == null )
    {
      m_classGen.setSuperClassName( "VwDVOBase" );
    }

    
    int nGenFlags = VwClassGen.GENGET;

    if ( m_fImplementsSerializable )
    {
      m_classGen.addImport( "java.io.Serializable", null );
    }
    
    if ( m_fImplementsClonaeable)
    {
      m_classGen.addImport( "java.lang.Cloneable", null );
      m_classGen.setGenCloneCode( true );
    }
    
    m_classGen.setGenEqualsCode( m_fImplementsEquals );
    
    for ( String strImport : m_listImports )
    {
      int nPos = strImport.indexOf( ':' );
      if ( nPos >= 0 )
      {
        m_classGen.addImport( strImport.substring( 0, nPos ), strImport.substring( ++nPos ) );
      }
      else
      {
        m_classGen.addImport( strImport, null );
      }
        
    }

    for ( String strInterface : m_listInterfaces )
    {
      m_classGen.addInterface( strInterface );
    }
    
    if ( m_fUseDirtyObjectDetection  )
    {
      if ( m_strSuperClassName == null )
      {
        m_classGen.addImport( "com.vozzware.db.VwDVOBase", null );
      }
      
      nGenFlags |= VwClassGen.GEN_SMARTSET;
    }
    else
    {
      nGenFlags |= VwClassGen.GENSET;
    }
    
    m_classGen.setOutputDirectory( m_strOutputPath );
    
    for ( VwPropertyDefinition propDef : m_listPropDefs )
    {
      int nDataFlags = nGenFlags;
      int[] anDim = null;

      switch( propDef.getDataType() )
      {
        case VW_DATE:
          
             m_classGen.addImport( "com.vozzware.util.VwDate", null );
             break;

        case DATE:

             m_classGen.addImport( "java.util.Date", null );
             break;

        case MAP:
        case GT_MAP:
          
             m_classGen.addImport( "java.util.Map", null );
             break;

        case HASH_MAP:
          
             m_classGen.addImport( "java.util.HashMap", null );
             break;

        case TREE_MAP:
          
             m_classGen.addImport( "java.util.TreeMap", null );
             break;

        case LIST:
        case GT_LIST:
          
             m_classGen.addImport( "java.util.List", null );
             break;

        case LINKED_LIST:
          
             m_classGen.addImport( "java.util.LinkedList", null );
             break;

        case ARRAY_LIST:
          
             m_classGen.addImport( "java.util.ArrayList", null );
             break;

      }
      
      if ( propDef.getArraySize() > 0 )
      {
        anDim = new int[ 1 ];
        anDim[0] = propDef.getArraySize();
        nDataFlags |= VwClassGen.ISARRAY;
      }
      
      m_classGen.addDataMbr( propDef.getName(), propDef.getDataType(), null,
                             VwClassGen.PRIVATE, nDataFlags,
                             propDef.getInitialValue(), anDim, propDef.getUserType() );

    }
    
    if ( m_fAddToString )
    {
      // Add the toString() method
      m_classGen.addImport( "com.vozzware.util.VwBeanUtils", null );
        
      m_classGen.addMethod(  "toString", VwClassGen.PUBLIC, DataType.STRING, getToStringCode(),
                             "Renders bean instance property values to a String",
                             "A String containing the bean property values",
                             null, 0, 0, 0, null, null, null );
      
    }
    
    m_classGen.generate( logger );
    
  }

  /**
   * 
   * @return
   */
  private String getToStringCode()
  {
    StringBuffer sb = new StringBuffer();
    
    sb.append( "    return VwBeanUtils.dumpBeanValues( this );" );
    
    return sb.toString();
  }

  
  /**
   * Sets the implements Serializeable flag, the default is true
   * @param fImplementsSerializable true to set/false to turn off
   */
  public void setImplementsSerializable( boolean fImplementsSerializable )
  { m_fImplementsSerializable = fImplementsSerializable; }
  
  /**
   * 
   * @return the ImplementsSerializable false
   */
  public boolean getImplementsSerializable()
  { return m_fImplementsSerializable; }
  
  
  /**
   * Sets the implements Cloneable flag, the default is true
   * @param fImplementsClonaeable true to set/false to turn off
   */
  public void setImplementsCloneable( boolean fImplementsClonaeable )
  { m_fImplementsClonaeable = fImplementsClonaeable; }
  
  
  /**
   * Gets the implements Cloneable flag
   * @return
   */
  public boolean getImplementsCloneable()
  { return m_fImplementsClonaeable; }
  
  
  public void setUseDirtyObjectDetection( boolean fDirtyObjectDetection )
  { m_fUseDirtyObjectDetection = fDirtyObjectDetection; }
  
  public boolean getUseDirtyObjectDetection()
  { return m_fUseDirtyObjectDetection; }
  
} // end class VwDVOGen{}

// *** End of VwDVOGen.java ***

