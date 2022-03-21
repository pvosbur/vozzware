/*
============================================================================================

                       V o z z W o r k s   C o d e   G e n e r a t o r                      

                               2009 by V o z z W a r e   L L C                              

    Source File Name: VwSqlMappingSpec.java

    Author:           

    Date Generated:   07-13-2019

    Time Generated:   10:51:56

============================================================================================
*/

package com.vozzware.db.util;

import com.vozzware.db.VwDVOBase;
import com.vozzware.util.VwBeanUtils;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class VwSqlMappingSpec extends VwDVOBase implements Serializable, Cloneable
{

  private String                 m_strAuthor;                    
  private String                 m_strSqlMappingDocument;        
  private String                 m_strPrimeKeyGenTables;         
  private String                 m_strKeyGenerationPolicy;       
  private String                 m_strSequenceTableName;         
  private String                 m_strSequenceColName;           
  private String                 m_strTimestampColName;          
  private String                 m_strSequenceType;              
  private String                 m_strSequenceName;              
  private String                 m_strOmitColumns;               
  private VwConnection           m_connection;                   
  private VwObjectProperties     m_objectProperties;             
  private VwDAOProperties        m_daoProperties;                
  private List<VwDbObjCommon>    m_listSpecMappings;             

  /**
   * 
   */
  public void addOrm( VwOrm vwOrm )
  {
    addToSpecMappingsList( vwOrm  );
  } // End of addOrm()



  /**
   * 
   */
  public void addTable( VwTableSpec vwTableSpec )
  {
    addToSpecMappingsList( vwTableSpec  );
  } // End of addTable()



  /**
   * 
   */
  public void addSchema( VwDbSchema vwDbSchema )
  {
    addToSpecMappingsList( vwDbSchema  );
  } // End of addSchema()



  /**
   * 
   */
  public void addQuery( VwDbQuery vwDbQuery )
  {
    addToSpecMappingsList( vwDbQuery  );
  } // End of addQuery()



  /**
   * 
   */
  public void addProc( VwProcedure vwProcedure )
  {
    addToSpecMappingsList( vwProcedure  );
  } // End of addProc()



  /**
   * Renders bean instance property values to a String
   * 
   * @return  A String containing the bean property values
   */
  public String toString()
  {
    return VwBeanUtils.dumpBeanValues( this );
  } // End of toString()



  /**
   * 
   */
  protected void addToSpecMappingsList( VwDbObjCommon objToAdd )
  {

    if ( m_listSpecMappings == null )
      m_listSpecMappings = new ArrayList<VwDbObjCommon>();

    m_listSpecMappings.add(  objToAdd );
  } // End of addToSpecMappingsList()



  // *** The following members set or get data from the class members *** 

  /**
   * Sets the author property
   */
  public void setAuthor( String strAuthor )
  { 
    
    testDirty( "author", strAuthor );
    m_strAuthor = strAuthor;
  }

  /**
   * Gets author property
   * 
   * @return  The author property
   */
  public String getAuthor()
  { return m_strAuthor; }

  /**
   * Sets the sqlMappingDocument property
   */
  public void setSqlMappingDocument( String strSqlMappingDocument )
  { 
    
    testDirty( "sqlMappingDocument", strSqlMappingDocument );
    m_strSqlMappingDocument = strSqlMappingDocument;
  }

  /**
   * Gets sqlMappingDocument property
   * 
   * @return  The sqlMappingDocument property
   */
  public String getSqlMappingDocument()
  { return m_strSqlMappingDocument; }

  /**
   * Sets the primeKeyGenTables property
   */
  public void setPrimeKeyGenTables( String strPrimeKeyGenTables )
  { 
    
    testDirty( "primeKeyGenTables", strPrimeKeyGenTables );
    m_strPrimeKeyGenTables = strPrimeKeyGenTables;
  }

  /**
   * Gets primeKeyGenTables property
   * 
   * @return  The primeKeyGenTables property
   */
  public String getPrimeKeyGenTables()
  { return m_strPrimeKeyGenTables; }

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
   * Sets the timestampColName property
   */
  public void setTimestampColName( String strTimestampColName )
  { 
    
    testDirty( "timestampColName", strTimestampColName );
    m_strTimestampColName = strTimestampColName;
  }

  /**
   * Gets timestampColName property
   * 
   * @return  The timestampColName property
   */
  public String getTimestampColName()
  { return m_strTimestampColName; }

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
   * Sets the connection property
   */
  public void setConnection( VwConnection connection )
  { 
    
    testDirty( "connection", connection );
    m_connection = connection;
  }

  /**
   * Gets connection property
   * 
   * @return  The connection property
   */
  public VwConnection getConnection()
  { return m_connection; }

  /**
   * Sets the objectProperties property
   */
  public void setObjectProperties( VwObjectProperties objectProperties )
  { 
    
    testDirty( "objectProperties", objectProperties );
    m_objectProperties = objectProperties;
  }

  /**
   * Gets objectProperties property
   * 
   * @return  The objectProperties property
   */
  public VwObjectProperties getObjectProperties()
  { return m_objectProperties; }

  /**
   * Sets the daoProperties property
   */
  public void setDaoProperties( VwDAOProperties daoProperties )
  { 
    
    testDirty( "daoProperties", daoProperties );
    m_daoProperties = daoProperties;
  }

  /**
   * Gets daoProperties property
   * 
   * @return  The daoProperties property
   */
  public VwDAOProperties getDaoProperties()
  { return m_daoProperties; }

  /**
   * Sets the specMappings property
   */
  public void setSpecMappings( List<VwDbObjCommon> listSpecMappings )
  { 
    
    testDirty( "specMappings", listSpecMappings );
    m_listSpecMappings = listSpecMappings;
  }

  /**
   * Gets specMappings property
   * 
   * @return  The specMappings property
   */
  public List<VwDbObjCommon> getSpecMappings()
  { return m_listSpecMappings; }

  /**
   * Clones this object
   *
   */
  public Object clone()
  {
    VwSqlMappingSpec classClone = new VwSqlMappingSpec();
    
    classClone.m_strAuthor = m_strAuthor;
    classClone.m_strSqlMappingDocument = m_strSqlMappingDocument;
    classClone.m_strPrimeKeyGenTables = m_strPrimeKeyGenTables;
    classClone.m_strKeyGenerationPolicy = m_strKeyGenerationPolicy;
    classClone.m_strSequenceTableName = m_strSequenceTableName;
    classClone.m_strSequenceColName = m_strSequenceColName;
    classClone.m_strTimestampColName = m_strTimestampColName;
    classClone.m_strSequenceType = m_strSequenceType;
    classClone.m_strSequenceName = m_strSequenceName;
    classClone.m_strOmitColumns = m_strOmitColumns;

    if ( m_connection  != null )
      classClone.m_connection = (VwConnection)m_connection.clone();

    if ( m_objectProperties  != null )
      classClone.m_objectProperties = (VwObjectProperties)m_objectProperties.clone();

    if ( m_daoProperties  != null )
      classClone.m_daoProperties = (VwDAOProperties)m_daoProperties.clone();

    if ( m_listSpecMappings  != null )
      classClone.m_listSpecMappings = (List<VwDbObjCommon>)cloneList( m_listSpecMappings );

    return classClone;
  }



  /**
   *Clones a list and all its elements
   *
   * @param list The list to clone
   *
   * @return The cloned List object
   *
   */
  private List cloneList( List list )
  {

    try
    {
      List listClone = (List)list.getClass().newInstance();

      for ( Object objListContent : list )
      {
        if ( objListContent instanceof Cloneable )
        {
          Method mthdClone = objListContent.getClass().getMethod( "clone", (Class[])null );
          Object objClone = mthdClone.invoke( objListContent, (Object[])null );
          listClone.add( objClone );
        } // end if
      } // end for()

      return listClone;
    }
    catch( Exception ex )
    {
      throw new RuntimeException( ex.toString() );
    }
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

    VwSqlMappingSpec objToTest = (VwSqlMappingSpec)objTest;

    if ( ! doObjectEqualsTest( m_strAuthor, objToTest.m_strAuthor ) )
      return false; 

    if ( ! doObjectEqualsTest( m_strSqlMappingDocument, objToTest.m_strSqlMappingDocument ) )
      return false; 

    if ( ! doObjectEqualsTest( m_strPrimeKeyGenTables, objToTest.m_strPrimeKeyGenTables ) )
      return false; 

    if ( ! doObjectEqualsTest( m_strKeyGenerationPolicy, objToTest.m_strKeyGenerationPolicy ) )
      return false; 

    if ( ! doObjectEqualsTest( m_strSequenceTableName, objToTest.m_strSequenceTableName ) )
      return false; 

    if ( ! doObjectEqualsTest( m_strSequenceColName, objToTest.m_strSequenceColName ) )
      return false; 

    if ( ! doObjectEqualsTest( m_strTimestampColName, objToTest.m_strTimestampColName ) )
      return false; 

    if ( ! doObjectEqualsTest( m_strSequenceType, objToTest.m_strSequenceType ) )
      return false; 

    if ( ! doObjectEqualsTest( m_strSequenceName, objToTest.m_strSequenceName ) )
      return false; 

    if ( ! doObjectEqualsTest( m_strOmitColumns, objToTest.m_strOmitColumns ) )
      return false; 

    if ( ! doObjectEqualsTest( m_connection, objToTest.m_connection ) )
      return false; 

    if ( ! doObjectEqualsTest( m_objectProperties, objToTest.m_objectProperties ) )
      return false; 

    if ( ! doObjectEqualsTest( m_daoProperties, objToTest.m_daoProperties ) )
      return false; 

    if ( ! doListElementTest( m_listSpecMappings, objToTest.m_listSpecMappings ) )
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

  /**
   * Do equals test on each object in the list
   *
   * @param list1 the base list
   * @param list2 the list to compare to the base list
   *
   * @return true if the lists are equal, false otherwise
   *
   */
  private boolean doListElementTest( List list1, List list2 )
  {

    if ( list1 != null )
    {
      if ( list2 == null )
        return false;
      else
      {
        if ( list1.size() != list2.size() )
          return false;   // sizes are different, not equal

        Iterator iObj2 = list2.iterator();

        for ( Object obj1 : list1 )
        {
          Object obj2 = iObj2.next();
          if ( !obj1.equals( obj2 ) )
            return false;

        } // end for

        return true;      // all elements are equal
      } // end else

    } // end if

    if ( list2 == null )
      return true;      // both lists are null so therefore the are equal

    return false;

  } // end doListElementTest()

} // *** End of class VwSqlMappingSpec{}

// *** End Of VwSqlMappingSpec.java