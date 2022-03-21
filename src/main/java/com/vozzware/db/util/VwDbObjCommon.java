/*
============================================================================================

                       V o z z W o r k s   C o d e   G e n e r a t o r                      

                               2009 by V o z z W a r e   L L C                              

    Source File Name: VwDbObjCommon.java

    Author:           

    Date Generated:   07-13-2019

    Time Generated:   10:51:56

============================================================================================
*/

package com.vozzware.db.util;

import com.vozzware.db.VwDVOBase;
import com.vozzware.util.VwBeanUtils;

import java.io.Serializable;


public class VwDbObjCommon extends VwDVOBase implements Serializable, Cloneable
{

  private String                 m_strKeyGenerationPolicy;       
  private String                 m_strSequenceTableName;         
  private String                 m_strSequenceColName;           
  private String                 m_strSequenceType;              
  private String                 m_strSequenceName;              
  private String                 m_strSchema;                    
  private String                 m_strOmitColumns;               
  private String                 m_strInheritClassName;          
  private String                 m_strImplementsClassName;       
  private String                 m_strClassName;                 
  private String                 m_strId;                        
  private String                 m_strGenDvoFromTable;           
  private String                 m_strSqlId;                     
  private String                 m_strPackage;                   
  private String                 m_strNoDVO;                     
  private String                 m_strTableAlias;                
  private String                 m_strExtends;                   

  /**
   * Renders bean instance property values to a String
   * 
   * @return  A String containing the bean property values
   */
  public String toString()
  {
    return VwBeanUtils.dumpBeanValues( this );
  } // End of toString()



  // *** The following members set or get data from the class members *** 

  /**
   * Sets the keyGenerationPolicy property
   */
  public void setKeyGenerationPolicy( String strKeyGenerationPolicy )
  { 
    
    testDirty( "keyGenerationPolicy", strKeyGenerationPolicy );
    m_strKeyGenerationPolicy = strKeyGenerationPolicy;
  }

  /**
   * Gets keyGenerationPolicy property
   * 
   * @return  The keyGenerationPolicy property
   */
  public String getKeyGenerationPolicy()
  { return m_strKeyGenerationPolicy; }

  /**
   * Sets the sequenceTableName property
   */
  public void setSequenceTableName( String strSequenceTableName )
  { 
    
    testDirty( "sequenceTableName", strSequenceTableName );
    m_strSequenceTableName = strSequenceTableName;
  }

  /**
   * Gets sequenceTableName property
   * 
   * @return  The sequenceTableName property
   */
  public String getSequenceTableName()
  { return m_strSequenceTableName; }

  /**
   * Sets the sequenceColName property
   */
  public void setSequenceColName( String strSequenceColName )
  { 
    
    testDirty( "sequenceColName", strSequenceColName );
    m_strSequenceColName = strSequenceColName;
  }

  /**
   * Gets sequenceColName property
   * 
   * @return  The sequenceColName property
   */
  public String getSequenceColName()
  { return m_strSequenceColName; }

  /**
   * Sets the sequenceType property
   */
  public void setSequenceType( String strSequenceType )
  { 
    
    testDirty( "sequenceType", strSequenceType );
    m_strSequenceType = strSequenceType;
  }

  /**
   * Gets sequenceType property
   * 
   * @return  The sequenceType property
   */
  public String getSequenceType()
  { return m_strSequenceType; }

  /**
   * Sets the sequenceName property
   */
  public void setSequenceName( String strSequenceName )
  { 
    
    testDirty( "sequenceName", strSequenceName );
    m_strSequenceName = strSequenceName;
  }

  /**
   * Gets sequenceName property
   * 
   * @return  The sequenceName property
   */
  public String getSequenceName()
  { return m_strSequenceName; }

  /**
   * Sets the schema property
   */
  public void setSchema( String strSchema )
  { 
    
    testDirty( "schema", strSchema );
    m_strSchema = strSchema;
  }

  /**
   * Gets schema property
   * 
   * @return  The schema property
   */
  public String getSchema()
  { return m_strSchema; }

  /**
   * Sets the omitColumns property
   */
  public void setOmitColumns( String strOmitColumns )
  { 
    
    testDirty( "omitColumns", strOmitColumns );
    m_strOmitColumns = strOmitColumns;
  }

  /**
   * Gets omitColumns property
   * 
   * @return  The omitColumns property
   */
  public String getOmitColumns()
  { return m_strOmitColumns; }

  /**
   * Sets the inheritClassName property
   */
  public void setInheritClassName( String strInheritClassName )
  { 
    
    testDirty( "inheritClassName", strInheritClassName );
    m_strInheritClassName = strInheritClassName;
  }

  /**
   * Gets inheritClassName property
   * 
   * @return  The inheritClassName property
   */
  public String getInheritClassName()
  { return m_strInheritClassName; }

  /**
   * Sets the implementsClassName property
   */
  public void setImplementsClassName( String strImplementsClassName )
  { 
    
    testDirty( "implementsClassName", strImplementsClassName );
    m_strImplementsClassName = strImplementsClassName;
  }

  /**
   * Gets implementsClassName property
   * 
   * @return  The implementsClassName property
   */
  public String getImplementsClassName()
  { return m_strImplementsClassName; }

  /**
   * Sets the className property
   */
  public void setClassName( String strClassName )
  { 
    
    testDirty( "className", strClassName );
    m_strClassName = strClassName;
  }

  /**
   * Gets className property
   * 
   * @return  The className property
   */
  public String getClassName()
  { return m_strClassName; }

  /**
   * Sets the id property
   */
  public void setId( String strId )
  { 
    
    testDirty( "id", strId );
    m_strId = strId;
  }

  /**
   * Gets id property
   * 
   * @return  The id property
   */
  public String getId()
  { return m_strId; }

  /**
   * Sets the genDvoFromTable property
   */
  public void setGenDvoFromTable( String strGenDvoFromTable )
  { 
    
    testDirty( "genDvoFromTable", strGenDvoFromTable );
    m_strGenDvoFromTable = strGenDvoFromTable;
  }

  /**
   * Gets genDvoFromTable property
   * 
   * @return  The genDvoFromTable property
   */
  public String getGenDvoFromTable()
  { return m_strGenDvoFromTable; }

  /**
   * Sets the sqlId property
   */
  public void setSqlId( String strSqlId )
  { 
    
    testDirty( "sqlId", strSqlId );
    m_strSqlId = strSqlId;
  }

  /**
   * Gets sqlId property
   * 
   * @return  The sqlId property
   */
  public String getSqlId()
  { return m_strSqlId; }

  /**
   * Sets the package property
   */
  public void setPackage( String strPackage )
  { 
    
    testDirty( "package", strPackage );
    m_strPackage = strPackage;
  }

  /**
   * Gets package property
   * 
   * @return  The package property
   */
  public String getPackage()
  { return m_strPackage; }

  /**
   * Sets the noDVO property
   */
  public void setNoDVO( String strNoDVO )
  { 
    
    testDirty( "noDVO", strNoDVO );
    m_strNoDVO = strNoDVO;
  }

  /**
   * Gets noDVO property
   * 
   * @return  The noDVO property
   */
  public String getNoDVO()
  { return m_strNoDVO; }

  /**
   * Sets the tableAlias property
   */
  public void setTableAlias( String strTableAlias )
  { 
    
    testDirty( "tableAlias", strTableAlias );
    m_strTableAlias = strTableAlias;
  }

  /**
   * Gets tableAlias property
   * 
   * @return  The tableAlias property
   */
  public String getTableAlias()
  { return m_strTableAlias; }

  /**
   * Sets the extends property
   */
  public void setExtends( String strExtends )
  { 
    
    testDirty( "extends", strExtends );
    m_strExtends = strExtends;
  }

  /**
   * Gets extends property
   * 
   * @return  The extends property
   */
  public String getExtends()
  { return m_strExtends; }

  /**
   * Clones this object
   *
   */
  public Object clone()
  {
    VwDbObjCommon classClone = new VwDbObjCommon();
    
    classClone.m_strKeyGenerationPolicy = m_strKeyGenerationPolicy;
    classClone.m_strSequenceTableName = m_strSequenceTableName;
    classClone.m_strSequenceColName = m_strSequenceColName;
    classClone.m_strSequenceType = m_strSequenceType;
    classClone.m_strSequenceName = m_strSequenceName;
    classClone.m_strSchema = m_strSchema;
    classClone.m_strOmitColumns = m_strOmitColumns;
    classClone.m_strInheritClassName = m_strInheritClassName;
    classClone.m_strImplementsClassName = m_strImplementsClassName;
    classClone.m_strClassName = m_strClassName;
    classClone.m_strId = m_strId;
    classClone.m_strGenDvoFromTable = m_strGenDvoFromTable;
    classClone.m_strSqlId = m_strSqlId;
    classClone.m_strPackage = m_strPackage;
    classClone.m_strNoDVO = m_strNoDVO;
    classClone.m_strTableAlias = m_strTableAlias;
    classClone.m_strExtends = m_strExtends;

    return classClone;
  }



  /**
   * Performs deep equal test on this object
   *
   * @param objTest The object to compare this object to
   *
   * @return if the two objects are equal, false otherwise
   *
   */
  public boolean equals( Object objTest )
  {

    if ( objTest == null )
      return false;

    if ( this.getClass() != objTest.getClass() )
      return false;

    VwDbObjCommon objToTest = (VwDbObjCommon)objTest;

    if ( ! doObjectEqualsTest( m_strKeyGenerationPolicy, objToTest.m_strKeyGenerationPolicy ) )
      return false; 

    if ( ! doObjectEqualsTest( m_strSequenceTableName, objToTest.m_strSequenceTableName ) )
      return false; 

    if ( ! doObjectEqualsTest( m_strSequenceColName, objToTest.m_strSequenceColName ) )
      return false; 

    if ( ! doObjectEqualsTest( m_strSequenceType, objToTest.m_strSequenceType ) )
      return false; 

    if ( ! doObjectEqualsTest( m_strSequenceName, objToTest.m_strSequenceName ) )
      return false; 

    if ( ! doObjectEqualsTest( m_strSchema, objToTest.m_strSchema ) )
      return false; 

    if ( ! doObjectEqualsTest( m_strOmitColumns, objToTest.m_strOmitColumns ) )
      return false; 

    if ( ! doObjectEqualsTest( m_strInheritClassName, objToTest.m_strInheritClassName ) )
      return false; 

    if ( ! doObjectEqualsTest( m_strImplementsClassName, objToTest.m_strImplementsClassName ) )
      return false; 

    if ( ! doObjectEqualsTest( m_strClassName, objToTest.m_strClassName ) )
      return false; 

    if ( ! doObjectEqualsTest( m_strId, objToTest.m_strId ) )
      return false; 

    if ( ! doObjectEqualsTest( m_strGenDvoFromTable, objToTest.m_strGenDvoFromTable ) )
      return false; 

    if ( ! doObjectEqualsTest( m_strSqlId, objToTest.m_strSqlId ) )
      return false; 

    if ( ! doObjectEqualsTest( m_strPackage, objToTest.m_strPackage ) )
      return false; 

    if ( ! doObjectEqualsTest( m_strNoDVO, objToTest.m_strNoDVO ) )
      return false; 

    if ( ! doObjectEqualsTest( m_strTableAlias, objToTest.m_strTableAlias ) )
      return false; 

    if ( ! doObjectEqualsTest( m_strExtends, objToTest.m_strExtends ) )
      return false; 

    return true;
  }



  /**
   * Perform an equals test on an Object
   *
   * @param obj1 first object
   * @param obj2 second object
   *
   * @return true if objects are equal, false otherwise
   *
   */
  private boolean doObjectEqualsTest( Object obj1, Object obj2 )
  {
    if ( obj1 != null )
    {
      if ( obj2 == null )
        return false;
      return obj1.equals( obj2 );
    }
    else
    if ( obj2 != null )
      return false;

    return true;

  }
} // *** End of class VwDbObjCommon{}

// *** End Of VwDbObjCommon.java