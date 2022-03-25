/*
===========================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

  THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
  PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
  CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwDAODocDictionary.java

Create Date: Sep 8, 2005
============================================================================================
*/
package com.vozzware.db;

import com.vozzware.db.util.VwConstraint;
import com.vozzware.db.util.VwExtendsDescriptor;
import com.vozzware.db.util.VwKeyDescriptor;
import com.vozzware.db.util.VwMappingTableConstraint;
import com.vozzware.db.util.VwPrimaryKeyGeneration;
import com.vozzware.db.util.VwSql;
import com.vozzware.db.util.VwSqlMapping;
import com.vozzware.db.util.VwSqlMappingDocument;
import com.vozzware.db.util.VwSqlMappingDocumentReader;
import com.vozzware.db.util.VwSqlStatement;
import com.vozzware.util.VwExString;
import com.vozzware.util.VwInputSource;
import com.vozzware.util.VwTextParser;

import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * @author P. VosBurgh
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class VwSqlMappingDictionary
{
  public static final String PRIMARY_KEY = "primaryKey";
  
  private Map<String,VwSqlMapping>	      m_mapSqlMappingsById = Collections.synchronizedMap( new HashMap<String,VwSqlMapping>() );
  private Map<String,Map<String,String>>	m_mapFinderById = Collections.synchronizedMap( new HashMap<String,Map<String,String>>() );
  private Map<String,Map<String,String>>  m_mapExists = Collections.synchronizedMap( new HashMap<String,Map<String,String>>() );
  private Map<String,Map<String,String>>  m_mapTsCheck = Collections.synchronizedMap( new HashMap<String,Map<String,String>>() );
  private Map<String,Map<String,String>>  m_mapUpdateById = Collections.synchronizedMap( new HashMap<String,Map<String,String>>() );
  private Map<String,Map<String,String>>  m_mapDeleteById = Collections.synchronizedMap( new HashMap<String,Map<String,String>>() );
  private Map<String,String>              m_mapProcsById = Collections.synchronizedMap( new HashMap<String,String>() );
  
  private static ResourceBundle s_rb = ResourceBundle.getBundle( "resources.properties.vwdb" );
  
  private VwSqlMappingDocument           m_sqlMappingDoc;

  /**
   * Constructor
   * 
   * @param urlDAODoc URL to the .xsm sql mapping document toload
   */
  public VwSqlMappingDictionary( URL urlDAODoc ) throws Exception
  {
      
    m_sqlMappingDoc = VwSqlMappingDocumentReader.read( urlDAODoc );
    
    if ( m_sqlMappingDoc == null )
    {
      throw new Exception ( "Error parsing DAO Docuemnt '" + urlDAODoc.toString() + "'" );
    }
    
    setup();
     
  }
  
  /**
   * Constructor
   * 
   * @param sqlMappingDoc The VwSqlMappingDocument loaded from the xml document
   */
  public VwSqlMappingDictionary( VwSqlMappingDocument sqlMappingDoc ) throws Exception
  {
    m_sqlMappingDoc = sqlMappingDoc;

    try
    {
      setup();
    }
    catch( Exception ex )
    {
      ex.printStackTrace();;
      throw ex;
      
    }
    
     
  } // end VwDAODocDictionary()
  
  
  /**
   * Get the Sql mapping document object graph
   * @return
   */
  public VwSqlMappingDocument getMappingDocument()
  { return m_sqlMappingDoc; }
  
  
  /**
   * Build the mappings in cache in Maps for fast lookup
   * @throws Exception
   */
  private void setup() throws Exception
  {
    if ( m_sqlMappingDoc.getSqlMapping() == null )
    {
      return;       // Creating an empty document 
      
    }
    // Build map of sql mappings by mapping id
    
    for ( VwSqlMapping sqlMapping : m_sqlMappingDoc.getSqlMapping() )
    {
       
      if ( sqlMapping == null )
      {
        return;
      }
      
      String strId = sqlMapping.getId();
      
      if ( strId == null )
      {
        continue;
      }

      m_mapSqlMappingsById.put( strId, sqlMapping );
      
      VwSqlStatement stmt = sqlMapping.getFindBy();
      
      Map<String,String> mapQueries = new HashMap<String,String>();
      m_mapFinderById.put( strId, mapQueries );
      
      setupWhereConstraints( sqlMapping, mapQueries, stmt  );
      
      stmt = sqlMapping.getQuery();
      
      if ( stmt != null )
      {
        mapQueries.put( ((VwSql)stmt.getSql().get( 0 )).getId(), ((VwSql)stmt.getSql().get( 0 )).getBody() );
      }
      
      stmt = sqlMapping.getExists();
      
      if ( stmt != null )
      {
        Map<String,String> mapExists = new HashMap<String,String>();
        m_mapExists.put( strId, mapExists );
        setupWhereConstraints( sqlMapping, mapExists, stmt  );
      }

      stmt = sqlMapping.getTimestampCheck();
      
      if ( stmt != null )
      {
        Map<String,String> mapTsCheck = new HashMap<String,String>();
        m_mapTsCheck.put( strId, mapTsCheck );
        setupWhereConstraints( sqlMapping, mapTsCheck, stmt  );
      }

      
      stmt = sqlMapping.getUpdateBy();
      
      if ( stmt != null )
      {
        Map<String,String> mapUpdateBy = new HashMap<String,String>();
        m_mapUpdateById.put( strId, mapUpdateBy );
        setupWhereConstraints( sqlMapping, mapUpdateBy, stmt  );
      }

      stmt = sqlMapping.getDeleteBy();
      
      if ( stmt != null )
      {
        Map<String,String> mapDeleteBy = new HashMap<String,String>();
        
        m_mapDeleteById.put( strId, mapDeleteBy );
        setupWhereConstraints( sqlMapping, mapDeleteBy, stmt  );
      
      }
      
      stmt = sqlMapping.getProc();
      
      if ( stmt != null )
      {
        m_mapProcsById.put( strId, ((VwSql)stmt.getSql().get( 0 )).getBody() );
      
      }
      
    } // end for()
    
  }

  /**
   * Build complete sql from base sql and where clause by the where clause id
   * @param mapSql The map holding complete sql by where clause id
   * @param stmt The sql statement with the list of finder contraints
   */
  private void setupWhereConstraints( VwSqlMapping sqlMapping, Map<String,String> mapSql, VwSqlStatement stmt  ) throws Exception
  {
    if ( stmt == null )
    {
      return;
    }
    
    List<VwConstraint> listConstraint = stmt.getConstraint();

    if ( listConstraint == null )
    {
      return;
    }

    List<VwSql>listSql = stmt.getSql();
    Map<String,String> mapSqlRef = new HashMap<String,String>();
    
    // Build map of sql statement(s) for ref by constraint entries
    if ( listSql != null)
    {
      for ( VwSql sql : listSql )
      {
        mapSqlRef.put( sql.getId(), sql.getBody() );
      }
    }
   
    for ( VwConstraint constraint : listConstraint)
    {

      String strSqlRefId = constraint.getSqlRef();
      
      if ( strSqlRefId == null )
      {
        strSqlRefId = "base";
      }
      
      String strBaseSql = (String)mapSqlRef.get( strSqlRefId );
      
      if ( strBaseSql == null )
      {
        throw new Exception( "No select statement or select set was defined for " + sqlMapping.getId() );
      }
      
      String[] astrQuerySet =  strBaseSql.split( ";");

      String strConstraintSql = fixupQuerySeqments( astrQuerySet, constraint );
      mapSql.put( constraint.getId(), strConstraintSql );

    } // end for()
    
    mapSql.put( "all", (String)mapSqlRef.get( "base" ) );
    
  } // end setupWhereConstraints()


  /**
   * Fix up each query segment for ech where clause and mapping table constrtaint
   * @param astrQueryies
   * @param constraint
   * @return
   */
  private String fixupQuerySeqments( String[] astrQueryies, VwConstraint constraint ) throws Exception
  {
    StringBuffer sbQuerySet = new StringBuffer();;

    sbQuerySet.append( fixupBaseSegment( astrQueryies[ 0 ], constraint ) );

    // Look through the table relation qieries for mappingTableConstraint fixups (indexes > 0
    for ( int x = 1; x < astrQueryies.length; x++ )
    {
      // get the target table name
      VwTextParser tp = new VwTextParser( new VwInputSource( astrQueryies[ x ]), 0, 1);

      int nPos = tp.findToken( "from" ) + "from ".length();

      int nEndPos = astrQueryies[ x ].indexOf( " ", nPos );

      String strTableName = astrQueryies[ x ].substring( nPos, nEndPos ).trim();

      VwMappingTableConstraint tableConstraint = getMappingTableConstraint( strTableName, constraint.getMappingConstraints() );

      String strWhereClauseFixup = null;

      if ( tableConstraint != null )
      {
        strWhereClauseFixup = tableConstraint.getWhere().trim();

        if ( strWhereClauseFixup.startsWith( "where" ))
        {
          strWhereClauseFixup = strWhereClauseFixup.substring( "where".length() );

        }
        // remove orig where clause
        tp = new VwTextParser( new VwInputSource( astrQueryies[ x ]), 0, 1);
        nPos = tp.findToken( "where" );

        tp = new VwTextParser( new VwInputSource( astrQueryies[ x ]), astrQueryies[ x ].length() - 1, -1);
        int nIntoPos = tp.findToken( "into" );

        String strSql = astrQueryies[ x ].substring( 0, nPos );
        sbQuerySet.append( "\n" ).append( strSql ).append( "\n").append( "where ").append( strWhereClauseFixup ).append( "\n").append( astrQueryies[ x ].substring( nIntoPos ) ).append( ";");

      }
      else
      {
        sbQuerySet.append( astrQueryies[ x ] ).append( ";");
      }
    }

    return sbQuerySet.toString().trim();

  }

  /**
   * Fixup where for the base sql or the top sql for a query set
   *
   * @param strTopSql
   * @param constraint
   * @return
   * @throws Exception
   */
  private StringBuffer fixupBaseSegment( String strTopSql, VwConstraint constraint ) throws Exception
  {
    StringBuffer sbSql = new StringBuffer();

    String strWhere = null;

    if ( constraint.getWhere() != null )
    {
      strWhere = constraint.getWhere().trim();
    }


    VwTextParser tp = new VwTextParser( new VwInputSource( strTopSql), strTopSql.length() - 1, -1);

    int nPos = tp.findToken( "into" );

    if ( nPos < 0 )
    {
      nPos = strTopSql.length();
    }

    sbSql.append( strTopSql.substring( 0, nPos ) );

    String strInto = strTopSql.substring( nPos );

    if ( strWhere != null )
    {
      // see if the first 5 characters equal where or order
      if ( strWhere.length() > 5 )
      {
        // leave constraint alone if keywords start with order by
        if ( !strWhere.substring( 0, 5 ).equalsIgnoreCase( "order" ) )
        {
          // if keyword does not start with 'order' or 'where' then assume 'where' and  prepend the 'where' keyword
          if ( !strWhere.substring( 0, 5 ).equalsIgnoreCase( "where" ) )
          {
            sbSql.append( " where " );
          }
        }
      }
      else
      {
        sbSql.append( " where " );      // assume where here
      }

      // *** Remove any order by clause unless this is setting up a select statment
      nPos = strWhere.toLowerCase().indexOf( " order");

      if ( nPos > 0 && !sbSql.toString().startsWith( "select" ) )
      {
        strWhere = strWhere.substring( 0, nPos );
      }

      sbSql.append( " " ).append( strWhere );

      sbSql.append( "\n" ).append( strInto ).append( ";\n" );

    }

    return sbSql;

  }


  /**
   *
   * @param strQuery
   * @param strWhereClause
   * @return
   */
  private String fixupWhereClause( String strQuery, String strWhereClause )
  {
    return null ;

  }


  /**
   * Search mapping table constarint list for a match on the table name
   * @param strTableName The table name to test for
   * @param listMappingTableConstraints The list of mapping table constaints (may be null)
   * @return
   */
  private VwMappingTableConstraint getMappingTableConstraint( String strTableName, List<VwMappingTableConstraint>listMappingTableConstraints )
  {
    if ( listMappingTableConstraints == null )
    {
      return null;
    }

    for ( VwMappingTableConstraint mapTableConstraint: listMappingTableConstraints )
    {
      if ( mapTableConstraint.getTableName().equalsIgnoreCase( strTableName  ) )
      {
        return mapTableConstraint;
      }
    }

    return null;

  }

  /**
   * Get VwSqlMapping object for the class id
   * 
   * @param clsId
   * @return The VwSqlMapping for the id or null in none exists
   */
  public VwSqlMapping getSqlMapping( Class clsId )
  { return getSqlMapping( clsId.getName() ); }

  
  /**
   * Get VwSqlMapping object for the  id
   * 
   * @param strId The id to retrieve
   * @return The VwSqlMapping for the id or null in none exists
   */
  public VwSqlMapping getSqlMapping( String strId )
  { return ( VwSqlMapping)m_mapSqlMappingsById.get( strId ); }
  
  
  /**
   * Gets the SQL insert statement for this class id (if one is defined )
   * @param cls The class (if class name is used as the mapping id) to retrieve the insert statement for
   * 
   * @return The insert SQL statement or null if no insert statement was defined
   * 
   * @exception if the id does not exist
   */
  public String  getQuery( Class cls  ) throws Exception
  { return getQuery( cls.getName(), VwSqlMgr.PRIME_KY ); }

  public String getProc( Class cls ) throws Exception
  { return getProc( cls.getName() ); }
  
  public String getProc( String strMappingId ) throws Exception
  {
    String strProc = (String)m_mapProcsById.get( strMappingId );
    if ( strProc == null )
    {
      String strErrMsg = VwExString.replace( s_rb.getString( "Vw.SqlMgr.IdNotFound"), "%1", strMappingId );
      throw new Exception( strErrMsg );
    }

    return strProc;
  }

  /**
   * Returns true if a SqlMapping already exists with this id
   * @param clsId The object cls to test for
   * @return
   */
  public boolean sqlMappingExists( Class clsId )
  { return sqlMappingExists( clsId.getName() ); } 
  
  
  /**
   * Returns true if a SqlMapping already exists with this id
   * @param strId The id to test for
   * @return
   */
  public boolean sqlMappingExists( String strId )
  { return m_mapSqlMappingsById.containsKey( strId ); }
  
  /**
   * Update a new instance of a sql mapping
   * @param strUpdateId
   * @param sqlMapping
   */
  public void updateSqlMapping( String strUpdateId, VwSqlMapping sqlMapping )
  { 
    m_mapSqlMappingsById.put( strUpdateId, sqlMapping );
    
    List<VwSqlMapping> listMappings = m_sqlMappingDoc.getSqlMapping();
    for ( int x = 0; x < listMappings.size(); x++ )
    {
      VwSqlMapping sqlOrigMapping = listMappings.get( x );
      
      if ( sqlOrigMapping.getId() == null )
      {
        continue;
      }
      
      if ( sqlOrigMapping.getId().equals( strUpdateId ) )
      {
        if ( sqlMapping == null )
        {
          listMappings.remove( x );
        }
        else
        {
          listMappings.set( x , sqlMapping );
        }
        
        return;
      }
    }
    
  } // end updateSqlMapping()
  
  
  /**
   * Gets the SQL insert statement ofr the id specified (if one is defined )
   * 
   * @param strId The id of the sql mapping to retrieve the insert statement for
   * @return The insert SQL statement or null if no insert statement was defined
   * 
   * @throws Exception if the id does not exist
   */
  public String getQuery( String strId ) throws Exception
  { return getQuery( strId, VwSqlMgr.PRIME_KY ); }
 
  
  /**
   * Gets the SQL insert statement for this class id (if one is defined )
   * @param cls The class (if class name is used as the mapping id) to retrieve the insert statement for
   * @param strFindById The id of the query to retrieve
   * 
   * @return The insert SQL statement or null if no insert statement was defined
   * 
   * @exception if the id does not exist
   */
  public String getQuery( Class cls, String strFindById  ) throws Exception
  { return getQuery( cls.getName(), strFindById ); }
  
  
  public String getExists( Class clsId ) throws Exception 
  { return getExists( clsId.getName() ); }
    
  public String getExists( Class clsId, String strExistsId ) throws Exception 
  { return getExists( clsId.getName(), strExistsId ); }
    

  public String getExists( String strId ) throws Exception 
  {  return getExists( strId, VwSqlMgr.PRIME_KY ); }

  /**
   * Gets the SQL statement that performns an exist test for a constraint
   * 
   * @param strId The id of the sql mapping to retrieve the insert statement for
   * @param strFindById The id of the query to retrieve
   * 
   * @return The insert SQL statement or null if no insert statement was defined
   * 
   * @throws Exception if the id does not exist
   */
  public String getExists( String strId, String strFindById ) throws Exception
  {
    Map mapExists  = (Map)m_mapExists.get( strId );
    
    if ( mapExists == null || mapExists.size() == 0 )
    {
      String strErrMsg = VwExString.replace( s_rb.getString( "Vw.SqlMgr.IdNotFound"), "%1", strId );
      throw new Exception( strErrMsg );
    }
    
    String strSql = (String)mapExists.get( strFindById );
    
    if ( strSql == null )
    {
      String strErrMsg = VwExString.replace( s_rb.getString( "Vw.SqlMgr.ConstraintNotFound"), "%", new String[]{"exists", strFindById, strId } );
      throw new Exception( strErrMsg );
    }
    
    return strSql;
     
  } // end getExists()
  
  /**
   * Get the timestampCheck sql using the object class as the id
   * @param clsId the mapping id 
   * @return
   * @throws Exception
   */
  public String getTsCheck( Class clsId ) throws Exception 
  { return getTsCheck( clsId.getName() ); }
    
  /**
   * Gets the SQL timestamp check statement for the id specified (if one is defined )
   * 
   * @param strId The id of the sql mapping to retrieve the timestamp check statement for
   * 
   * @return The timestamp check SQL statement or null if no  statement was defined
   * 
   * @throws Exception if the id does not exist
   */
  public String getTsCheck( String strId ) throws Exception
  {
    Map<String,String> mapTsCheck  = m_mapTsCheck.get( strId );
    
    if ( mapTsCheck == null || mapTsCheck.size() == 0 )
    {
      String strErrMsg = VwExString.replace( s_rb.getString( "Vw.SqlMgr.IdNotFound"), "%1", strId );
      throw new Exception( strErrMsg );
    }
    
    String strSql = (String)mapTsCheck.get( PRIMARY_KEY );
    
    if ( strSql == null )
    {
      String strErrMsg = VwExString.replace( s_rb.getString( "Vw.SqlMgr.ConstraintNotFound"), "%", new String[]{"timestampCheck", PRIMARY_KEY, strId } );
      throw new Exception( strErrMsg );
    }
    
    return strSql;
     
  } // end getTsCheck()
  


  /**
   * Gets the SQL select statement for the id specified (if one is defined )
   * 
   * @param strId The id of the sql mapping to retrieve the select statement for
   * @param strFindById The id of the query to retrieve
   * 
   * @return The  SQL select statement(s) or null if no select statement was defined
   * 
   * @throws Exception if the id does not exist
   */
  public String getQuery( String strId, String strFindById ) throws Exception
  {
    Map<String,String> mapQueries  = m_mapFinderById.get( strId );
    
    if ( mapQueries == null || mapQueries.size() == 0 )
    {
      String strErrMsg = VwExString.replace( s_rb.getString( "Vw.SqlMgr.IdNotFound"), "%1", strFindById );
      throw new Exception( strErrMsg );
    }
    
    String strSql = (String)mapQueries.get( strFindById );
    
    if ( strSql == null )
    {
      String strErrMsg = VwExString.replace( s_rb.getString( "Vw.SqlMgr.ConstraintNotFound"), "%", new String[]{"findBy", strFindById, strId } );
      throw new Exception( strErrMsg );
    }
    
    return strSql;
     
  } // end getQuery()
  

  
  /**
   * Gets the SQL insert statement for this class id (if one is defined )
   * @param cls The class (if class name is used as the mapping id) to retrieve the insert statement for
   * 
   * @return The insert SQL statement or null if no insert statement was defined
   * 
   * @exception if the id does not exist
   */
  public String getInsert( Class cls  ) throws Exception
  { return getInsert( cls.getName() ); }
  
  /**
   * Gets the SQL insert statement for the id specified (if one is defined )
   * 
   * @param strId The id of the sql mapping to retrieve the insert statement for
   * @return The insert SQL statement or null if no insert statement was defined
   * 
   * @throws Exception if the id does not exist
   */
  public String getInsert( String strId ) throws Exception
  {
    VwSqlMapping mapping = (VwSqlMapping)m_mapSqlMappingsById.get( strId );
    
    if ( mapping == null )
    {
      String strErrMsg = VwExString.replace( s_rb.getString( "Vw.SqlMgr.IdNotFound"), "%1", strId );
      
      throw new Exception( strErrMsg );
    }
    
    return mapping.getInsert();
    
    
  } // end getInsert()

  
  /**
   * Gets the SQL update (by primaryKey) statement for this class id (if one is defined )
   * @param cls The class (if class name is used as the mapping id) to retrieve the insert statement for
   * 
   * @return The update SQL statement (by primaryKey) or null if no updateBy primaryKey statement was defined
   * 
   * @exception if the id does not exist or no update by primary key id was defined
   */
  public String getUpdate( Class cls  ) throws Exception
  { return getUpdate( cls.getName(), PRIMARY_KEY ); }

  
  /**
   * Gets the SQL update statement for this class id (if one is defined ) and the update id
   * 
   * @param cls The class used as the id string
   * @param strUpdateById The specific update statement to retrieve
   * @return The update sql
   * @throws Exception if the cls id or updateBy id does not exist
   */
  public String getUpdate( Class cls, String strUpdateById  ) throws Exception
  { return getUpdate( cls.getName(), strUpdateById ); }
  

  /**
   * Gets the SQL update statement for the id specified (if one is defined )
   * 
   * @param strId The id of the sql mapping to retrieve the insert statement for
   *
   * @return The update SQL statement or null if no update statement was defined
   * 
   * @throws Exception if the cls id or updateBy id does not exist
   */
  public String getUpdate( String strId ) throws Exception
  { return getUpdate( strId, PRIMARY_KEY ); }
  
  /**
   * Gets the SQL update statement for the id specified (if one is defined )
   * 
   * @param strId The id of the sql mapping to retrieve the update statement for
   * @param strUpdateById The id of the update statement to retrieve
   * 
   * @return The update SQL statement or null if no update statement was defined
   * 
   * @throws Exception if the cls id or updateBy id does not exist
   */
  public String getUpdate( String strId, String strUpdateById ) throws Exception
  {
    Map mapUpdateBy = (Map)m_mapUpdateById.get( strId );
    
    if ( mapUpdateBy == null || mapUpdateBy.size() == 0 )
    {
      String strErrMsg = VwExString.replace( s_rb.getString( "Vw.SqlMgr.IdNotFound"), "%1", strId );
      throw new Exception( strErrMsg );
    }
    
    String strSql = (String)mapUpdateBy.get( strUpdateById );
    
    if ( strSql == null )
    {
      String strErrMsg = VwExString.replace( s_rb.getString( "Vw.SqlMgr.ConstraintNotFound"), "%", new String[]{"updateBy", strUpdateById, strId } );
      throw new Exception( strErrMsg );
    }
    
    return strSql;
    
  } // end getUpdate()
  
  /**
   * Gets the SQL delete (by primaryKey) statement for this class id (if one is defined )
   * @param cls The class (if class name is used as the mapping id) to retrieve the insert statement for
   * 
   * @return The delete SQL statement (by primaryKey) or null if no deleteBy primaryKey statement was defined
   * 
   * @exception if the id does not exist or no delete by primary key id was defined
   */
  public String getDelete( Class cls  ) throws Exception
  { return getDelete( cls.getName(), PRIMARY_KEY ); }

  
  /**
   * Gets the SQL delete statement for this class id (if one is defined ) and the update id
   * 
   * @param cls The class used as the id string
   * @param strDeleteById The specific delete statement to retrieve
   * @return The delete sql
   * @throws Exception if the cls id or deleteBy id does not exist
   */
  public String getDelete( Class cls, String strDeleteById  ) throws Exception
  { return getDelete( cls.getName(), strDeleteById ); }
  

  /**
   * Gets the SQL update statement for the id specified (if one is defined )
   * 
   * @param strId The id of the sql mapping to retrieve the delete statement for
   *
   * @return The delete SQL statement or null if no insert statement was defined
   * 
   * @throws Exception if the cls id or updateBy id does not exist
   */
  public String getDelete( String strId ) throws Exception
  { return getDelete( strId, PRIMARY_KEY ); }
  
  
  /**
   * Gets the SQL delete statement for the id specified (if one is defined )
   * 
   * @param strId The id of the sql mapping to retrieve the insert statement for
   * @param strDeleteById The id of the delete statement to retrieve
   * 
   * @return The delete SQL statement or null if no insert statement was defined
   * 
   * @throws Exception if the cls id or deleteBy id does not exist
   */
  public String getDelete( String strId, String strDeleteById ) throws Exception
  {
    Map<String,String> mapDeleteBy = m_mapDeleteById.get( strId );
    
    if ( mapDeleteBy == null || mapDeleteBy.size() == 0 )
    {
      String strErrMsg = VwExString.replace( s_rb.getString( "Vw.SqlMgr.IdNotFound"), "%1", strId );
      throw new Exception( strErrMsg );
    }
    
    String strSql = (String)mapDeleteBy.get( strDeleteById );
    
    if ( strSql == null )
    {
      String strErrMsg = VwExString.replace( s_rb.getString( "Vw.SqlMgr.ConstraintNotFound"), "%", new String[]{"deleteBy", strDeleteById, strId } );
      throw new Exception( strErrMsg );
    }
    
    return strSql;
    
    
  } // end getDelete()


  /**
   * gets the extens descriptor object if one exists for this mapping
   * @param cls
   * @return
   * @throws Exception
   */
  public VwExtendsDescriptor getExtendsDescriptor( Class cls )  throws Exception
  {
    return getExtendsDescriptor( cls.getName() );
  }


  /**
   * gets the extens descriptor object if one exists for this mapping
   * @param strId The mapping id
   * @return
   * @throws Exception
   */
  public VwExtendsDescriptor getExtendsDescriptor( String strId)  throws Exception
  {
    VwSqlMapping mapping = (VwSqlMapping)m_mapSqlMappingsById.get( strId );

    if ( mapping == null )
    {
      String strErrMsg = VwExString.replace( s_rb.getString( "Vw.SqlMgr.IdNotFound"), "%1", strId );

      throw new Exception( strErrMsg );
    }

    return mapping.getExtendsClass();

  }

  /**
   * Gets the List if VwPrimaryKeyGeneration classes for the mapping id specified
   * @param cls The class (if class name is used as the mapping id) 
   * 
   * @return List if VwPrimaryKeyGeneration classes
   * 
   * @exception if the id does not exist
   */
  public List<VwPrimaryKeyGeneration> getPrimaryKeyGeneration( Class cls  ) throws Exception
  { return getPrimaryKeyGeneration( cls.getName() ); }

  
  /**
   * Gets the List if VwPrimaryKeyGeneration classes for the mapping id specified
   * @param strId the mapping id
   * 
   * @return List if VwPrimaryKeyGeneration classes
   * 
   * @exception if the id does not exist
   */
  public List<VwPrimaryKeyGeneration> getPrimaryKeyGeneration( String strId ) throws Exception
  {
    VwSqlMapping mapping = (VwSqlMapping)m_mapSqlMappingsById.get( strId );
    
    if ( mapping == null )
    {
      String strErrMsg = VwExString.replace( s_rb.getString( "Vw.SqlMgr.IdNotFound"), "%1", strId );
      
      throw new Exception( strErrMsg );
    }
    
    return mapping.getPrimaryKeyGeneration();
    
    
  } // end getPrimaryKey()
  

  /**
   * Gets the List of key descriptor objects for this class id (if this class is a graph that propagates its primary key(s))
   * @param cls The class (if class name is used as the mapping id) to retrieve the insert statement for
   * 
   * @return List of key descriptor objects (may be null)
   * 
   * @exception if the id does not exist
   */
  public List<VwKeyDescriptor> getPrimaryKeySupplier( Class cls  ) throws Exception
  { return getPrimaryKeySupplier( cls.getName() ); }

  
  /**
   * Gets the List of key descriptor objects for this class id (if one is defined )
   * @param strId The mapping id
   * 
   * @return List of key descriptor objects 
   * 
   * @exception if the id does not exist
   */
  public List<VwKeyDescriptor> getPrimaryKeySupplier( String strId ) throws Exception
  {
    VwSqlMapping mapping = (VwSqlMapping)m_mapSqlMappingsById.get( strId );
    
    if ( mapping == null )
    {
      String strErrMsg = VwExString.replace( s_rb.getString( "Vw.SqlMgr.IdNotFound"), "%1", strId );
      
      throw new Exception( strErrMsg );
    }
    
    return mapping.getPrimaryKeySupplier();
    
    
  } // end getPrimaryKey()
  
  
  /**
   * Gets the List of key descriptor objects for this class id (if this class is a graph that as foreign key relationships )
   * @param cls The class (if class name is used as the mapping id) to retrieve the insert statement for
   * 
   * @return List of key descriptor objects (may be null)
   * 
   * @exception if the id does not exist
   */
  public List<VwKeyDescriptor> getForeignKeys( Class cls  ) throws Exception
  { return getForeignKeys( cls.getName() ); }

  
  /**
   * Gets the List of key descriptor objects for this class id (if this class is a graph that as foreign key relationships )
   * @param strId  mapping id) to retrieve the foreign keya
   * 
   * @return List of key descriptor objects (may be null)
   * 
   * @exception if the id does not exist
   */
  public List<VwKeyDescriptor> getForeignKeys( String strId ) throws Exception
  {
    VwSqlMapping mapping = (VwSqlMapping)m_mapSqlMappingsById.get( strId );
    
    if ( mapping == null )
    {
      String strErrMsg = VwExString.replace( s_rb.getString( "Vw.SqlMgr.IdNotFound"), "%1", strId );
      
      throw new Exception( strErrMsg );
    }
    
    return mapping.getForeignKey();
    
    
  } // end getForeignKeys()
  
  
 } // end class VwDAODocDictionary{}

//*** End of VwDAODocDictionary.java ***

