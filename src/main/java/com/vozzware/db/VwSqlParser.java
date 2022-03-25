/*
  ===========================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

  THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
  PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
  CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

  Source Name:  VwSqlParser.java


  ============================================================================================
*/

package com.vozzware.db;

import com.vozzware.util.VwBeanUtils;
import com.vozzware.util.VwDelimString;
import com.vozzware.util.VwExString;
import com.vozzware.util.VwInputSource;
import com.vozzware.util.VwTextParser;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

/**
 * This class parses SQL statements in order to obtain the columns in the table(s) that
 * represent parameter or result columns.
 */

public class VwSqlParser
{
  private ArrayList             m_listResults;          // Columns that are in a result list
  private ArrayList             m_listParams;
  private ArrayList             m_listParamTypes;       // Corresponding data type list to params
  private ArrayList             m_listResultTypes;

  private short[]               m_asResultTypes;        // Column data types
  
  private String                m_strSQL;               // The xlated sql statement that is JDBC
  private String                m_strVanillaSQL;        // The original SQL statement with ITC's
  private String                m_strClassTarget;       // The java class that is the data holder fro this statement
  private String				        m_strPropertyTarget;    // The name of the setter property from the class target that
  private String                m_strUserID;            // Used for schema ownership
                                                        // the object will be put
  private String                m_strPackageName;           // pack name to resove stmtClass references
  
  private VwDatabase           m_db;                  // Connected database


  private int                   m_nStmtType;           // SQL statement type


  ResourceBundle m_msgs = ResourceBundle.getBundle( "resources.properties.vwdb" );

  private boolean               m_fSqlModified = false; // Set to true if a select * was expanded
  private boolean               m_fExpandSelects = true;  // Expand select * to full column list
  private boolean               m_fIsTargetPropList;    // Will be true if target property is a List

  private Class 				        m_clsStmtClass;					// Class used to put select statement result in
  private Class 	    		      m_clsTargetClass;				// Target class where stmtClass object will be placed
  private Class                 m_clsMethodParam;   // The paramater class type of the targeted class property
  
  private Method  				      m_mthdTargetProperty;   // The target method of the target class
  
  private ClassLoader           m_loader;
  
  private static final String[]  s_astrOperators = { "=","eq","equal","<","<=",">",">=","<>",
                                                     "and","not","or","like","between","than",
                                                     "in","less","greater",":","(",")","*","/","+",
                                                     "-",","
                                                    };

  private static final String[] s_astrJoinKeywords = {"inner", "outer", "left", "right", "full", "natural" };
  
  private static Object s_semi = new Object();
  
  
  /**
   * constant for a EXECUTABLE Statement
   */
  public static final int EXECUTABLE = 0;


  /**
   * constant for a INSERT SQL Statement
   */
  public static final int INSERT = 1;


  /**
   * constant for an UPDATE SQL Statement
   */
  public static final int UPDATE = 2;


  /**
   * constant for a DELETE SQL Statement
   */
  public static final int DELETE = 3;


  /**
   * constant for a SELECT SQL Statement
   */
  public static final int SELECT = 4;


  /**
   * constant for a a stored procedure
   */
  public static final int PROC = 5;

  /**
   * Constructs an VwSqlParser object
   *
   * @param db - Connected database instance
   * @param strSQL - The SQL statement to parse
   *
   * @exception - throws SQLException if any errors occur
   */
  public VwSqlParser( VwDatabase db, String strSQL, ClassLoader loader ) throws SQLException, Exception
  {
    m_db = db;
    m_loader = loader;
    m_strVanillaSQL = strSQL.trim();          // This SQL we never alter
    
  } // End VwSqlParser(


  
  /**
   * Constructs an VwSqlParser object
   *
   * @param db - Connected database instance
   * @param strSQL - The SQL statement to parse
   *
   * @exception - throws SQLException if any errors occur
   */
  public VwSqlParser( VwDatabase db, String strSQL ) throws SQLException, Exception
  {
    this( db, strSQL, true, null );

  } // End VwSqlParser(

  /**
   * Constructs an VwSqlParser object
   *
   * @param db - Connected database instance
   * @param strSQL - The SQL statement to parse
   *
   * @exception - throws SQLException if any errors occur
   */
  public VwSqlParser( VwDatabase db, String strSQL, String strPackageName ) throws SQLException, Exception
  {
    this( db, strSQL, true, strPackageName );

  } // End VwSqlParser(

  /**
   * Constructs an VwSqlParser object
   *
   * @param db - Connected database instance
   * @param strSQL - The SQL statement to parse
   * @param fExpandSelect - if true expand any select * statements to the full column list of the table
   *
   * @exception - throws SQLException if any errors occur
   */
  public VwSqlParser( VwDatabase db, String strSQL, boolean fExpandSelects  ) throws SQLException, Exception
  {
    this( db, strSQL, fExpandSelects, null );
  }
  
  /**
   * Constructs an VwSqlParser object
   *
   * @param db - Connected database instance
   * @param strSQL - The SQL statement to parse
   * @param fExpandSelects - if true expand any select * statements to the full column list of the table
   * @param strPackageName
   *
   * @exception - throws SQLException if any errors occur
   */
  public VwSqlParser( VwDatabase db, String strSQL, boolean fExpandSelects, String strPackageName ) throws SQLException, Exception
  {
    m_strPackageName = strPackageName;
    expandSelects( fExpandSelects );

    m_db = db;

    if ( db != null )
      m_strUserID = m_db.getDbMgr().getUserID();
    
    m_strVanillaSQL = strSQL.trim();          // This SQL we never alter

    xlateVanillaSQL();

    parse();

  } // end VwSQLParser()

  public VwSqlData runParser() throws Exception
  {
    m_strUserID = m_db.getDbMgr().getUserID();
    
    xlateVanillaSQL();
    parse();
    
    return getParsedSqlData();
    
  } // end run Parser
  
  public void setPackage( String strPackageName )
  { m_strPackageName = strPackageName; }
  
  
  /**
   * Returns true if a select * statement was expanded in to the full column list
   */
  public boolean sqlWasModified()
  { return m_fSqlModified; }


  /**
   * Set the expand select statement flag. If true select * statements are expanded to
   * the full column list and the sql statement staring is modified.<br> This is the default
   * setting. A value of false preserves the sql statement
   */
  public void expandSelects( boolean fExpandSelects )
  { m_fExpandSelects = fExpandSelects; }


  /**
   * Returns an VwSqlData object with its members initialized from the parsed SQL statement
   *
   * @return VwSqlData object with its members initialized from the parsed SQL statement
   */
  public final VwSqlData getParsedSqlData()
  {
    VwSqlData sqlData = new VwSqlData();

    sqlData.m_strSQL = m_strSQL;
    sqlData.m_nStmtType = m_nStmtType;

    sqlData.m_listParams = m_listParams;
    if ( sqlData.m_strSQL.toLowerCase().indexOf( "itccallback") > 0 )
      sqlData.m_fIsDynamicWhere = true;
    

    if ( m_nStmtType == EXECUTABLE )
      return sqlData;

    if ( m_listParamTypes != null )
    {
      sqlData.m_asParamTypes = new short[ m_listParamTypes.size() ];
      int x = -1;
      for ( Iterator iTypes = m_listParamTypes.iterator(); iTypes.hasNext(); )
        sqlData.m_asParamTypes[ ++x ] = Short.parseShort( (String)iTypes.next() );
 
    }
    
 
    sqlData.m_asParamNbr = getProcParamNbrs();

    sqlData.m_asResultParamNbr = getProcResultParamNbrs();

    if ( m_listResultTypes != null )
    {
      sqlData.m_asResultTypes = new short[ m_listResultTypes.size() ];
      int x = -1;
      for ( Iterator iTypes = m_listResultTypes.iterator(); iTypes.hasNext(); )
        sqlData.m_asResultTypes[ ++x ] = Short.parseShort( (String)iTypes.next() );
 
    }
    
    sqlData.m_listResults = m_listResults;

    sqlData.m_fReuseDataObj = false;


    // For stored procedures we have to strip off the param nbr prexfix

    if ( m_nStmtType == PROC )
    {
      if ( sqlData.m_listParams != null )
      {
        for ( int x = 0; x < sqlData.m_listParams.size(); x++ )
        {
          String strVal = (String)sqlData.m_listParams.get(  x  );
          sqlData.m_listParams.set( x, strVal.substring( strVal.indexOf( ':' ) + 1 ) );
        }
      }

      if ( sqlData.m_listResults != null )
      {
        for ( int x = 0; x < sqlData.m_listResults.size(); x++ )
        {
          String strVal = (String)sqlData.m_listResults.get(  x  );
          sqlData.m_listResults.set( x, strVal.substring( strVal.indexOf( ':' ) + 1 ) );
          
        }
      }

    } // end if

    sqlData.m_clsStmtClass = m_clsStmtClass;
    sqlData.m_clsTargetClass = m_clsTargetClass;
    sqlData.m_clsMethodParam = m_clsMethodParam;
    sqlData.m_mthdTargetProperty = m_mthdTargetProperty;
    sqlData.m_fIsTargetPropList = m_fIsTargetPropList;
    
    return sqlData;

  } // end getParsedSqlData()


  /**
   * Returns the execution SQL statement which can be different from the "plain vanilla" SQL if
   * any host variable names are used in the query like "select * from table where bal > :hiBal".
   * ":hiBal" is a host variable that translates to the '?' param place holder for JDBC compliance.
   * See the xlateVanillaSQL() method of this class.
   *
   * @return The execution SQL string
   */
  public final String getExecSql()
  { return m_strSQL; }


  /**
   * Returns the vanilla sql wich may have been modified if a select * statemenet was expanded
   * into the full column list (which is the default)
   *
   * @return The execution SQL string
   */
  public final String getVanillaSql()
  { return m_strVanillaSQL; }

  /**
   * Gets the SQL statement type (INSERT, UPDATE, etc.)
   *
   * @return The constant for the statement type
   */
  public final int getStatementType()
  { return m_nStmtType; }

  
  /**
   * Gets the list of parameter bind names
   * @return
   */
  public List getParamsList()
  { return m_listParams; }
 
  /**
   * Gets the list of output paramater names for a store procedure name
   * @return
   */
  public List getProcOutputParams()
  { return m_listResults; }
  
  public List getProcOutputParamTypes()
  { return m_listResults; }
  
  /**
   * Gets the data type list corresponding to the paramter column list, or null
   * if there are no parameter columns
   *
   * @return The parameter column dataq types list or null there are no paramter columns
   */
  public final ArrayList getParamTypeList()
  { return m_listParamTypes; }


  /**
   * Gets the parameter numbers of the input parameter list of a stored prodedure
   *
   * @return A short array of input parameter numbers, or null if not a stored procedure or
   * if the stored procedure does not have any IN parameters.
   */
  public final short[] getProcParamNbrs()
  {
    if ( m_nStmtType != PROC )
      return null;

    if ( m_listParams == null )
      return null;


    short[] asParamNbrs = new short[ m_listParams.size() ];

    for ( int x = 0; x < m_listParams.size(); x++ )
    {
      String strVal = (String)m_listParams.get( x );
      int nPos = strVal.indexOf( ':' );
      if ( nPos < 0 )
        return null;
      
      asParamNbrs[ x ] = Short.parseShort( strVal.substring( 0, nPos ) );
    } // end for()

    return asParamNbrs;

  } // end getProcParamNbrs()


  /**
   * Gets the result parameter numbers of the result parameter list of a stored prodedure
   *
   * @return A short array of result parameter numbers; null if not a stored procedure or
   * if the stored procedure does not have any OUT or INOUT parameters.
   */
  public final short[] getProcResultParamNbrs()
  {
    if ( m_nStmtType != PROC )
      return null;

    if ( m_listResults == null )
      return null;


    short[] asParamNbrs = new short[ m_listResults.size() ];

    for ( int x = 0; x < m_listResults.size(); x++ )
    {
      String strVal = (String)m_listResults.get(  x  );
      
      int nPos = strVal.indexOf( ':' );
      if ( nPos < 0 )
        return null;
      
      asParamNbrs[ x ] = Short.parseShort( strVal.substring( 0, nPos ) );
    } // end for()

    return asParamNbrs;

  } // end getProcParamNbrs()



  /**
   * Parses the SQL statement into result and parameter column lists
   *
   * @exception throws SQLException if any SQL errors occur
   */
  private void parse() throws SQLException, Exception
  {
    m_strSQL = m_strSQL.trim();          // trim leading and trailing white space

    if ( m_strSQL.length() == 0 )
      return;

    int nPos = VwExString.findWhiteSpace( m_strSQL, 0, 1 );

    String strVerb = m_strSQL.substring( 0, nPos );

    strVerb = strVerb.toLowerCase();

    if ( strVerb.equals( "select" ) )
      m_nStmtType = SELECT;
    else
    if (  strVerb.equals( "insert" ) )
      m_nStmtType = INSERT;
    else
    if (  strVerb.equals( "update" ) )
      m_nStmtType = UPDATE;
    else
    if (  strVerb.equals( "delete" ) )
      m_nStmtType = DELETE;
    else
    if ( strVerb.startsWith( "{" ) )
      m_nStmtType = PROC;
    else
      m_nStmtType = EXECUTABLE;

    
    if ( m_nStmtType == EXECUTABLE )
      return;
    
    // *** Any other verb found will be assumed to be a valid executable statememnt
    // *** allowed by the database vendor

    // *** The the JDBC Connection instance so we can create Statements to execute

    if ( m_nStmtType == PROC )
    {
      doProc();
      return;
    }
    else
    if ( m_nStmtType == INSERT )
      doInsert();
    else
      m_listParams = getBindParamNames();

  } // end parse()



  /**
   * Search SQL string and get a list of binding parameter names
   * @return
   */
  private ArrayList getBindParamNames() throws Exception
  {
    ArrayList listParams = new ArrayList();
    StringBuffer sbParam = new StringBuffer();
    boolean fColanIsTypeCast = false;

    int nPos = m_strVanillaSQL.indexOf( '?' );
    
    if ( nPos >= 0 )
      return getBindNamesFromJDCBPlaceholders();
    
    nPos = m_strVanillaSQL.indexOf( ':' );
    if ( nPos < 0 )
      return null;

    if ( m_strVanillaSQL.charAt( nPos + 1 )== ':' )
    {
      nPos += 2;
      fColanIsTypeCast = true;
    }

    int nSqlLen = m_strVanillaSQL.length();
    while( nPos >= 0 )
    {
      if ( !fColanIsTypeCast )
      {
        sbParam.setLength( 0 );

        for ( ++nPos; nPos < nSqlLen; ++nPos )
        {
          char ch = m_strVanillaSQL.charAt( nPos );

          if ( Character.isWhitespace( ch ) || VwExString.isin( ch, ",()" ) )
            break;

          sbParam.append( ch );

        } // end for()

        listParams.add( sbParam.toString() );

      }

      nPos = m_strVanillaSQL.indexOf( ':', nPos );

      if ( nPos < 0 )
      {
        break;

      }
      if ( m_strVanillaSQL.charAt( nPos + 1 )== ':' )
      {
        nPos += 2;
        fColanIsTypeCast = true;
      }
      else
      {
        fColanIsTypeCast = false;

      }

    } // end while

    if ( listParams.size() == 0 )
    {
      return null;
    }

    return listParams;
    
  } // end getBindParamNames()
  
  /**
   * Finds all the table names referenced in a SQL statement
   *
   * @return A List of VwTableName objects
   */
  public ArrayList<VwTableName> getTableList() throws Exception
  {

    String strSQL = m_strSQL;   // Lower case for easy serach

    VwTextParser tp = new VwTextParser( new VwInputSource( strSQL ) );
    tp.setDelimiters( ",(" );
    
    StringBuffer sb = new StringBuffer();        // Useed for getWord results

    int nPos = -1;
    
    ArrayList<VwTableName> listTable = new ArrayList<VwTableName>();
    
    switch( m_nStmtType  )
    {
      case INSERT:

           nPos =  tp.findToken( "into" );
           
           if ( nPos < 0 )
             throw new Exception( "Invalid insert statement, did not find expected into clause");
           
           tp.getToken( sb );
           
           listTable.add( new VwTableName( sb.toString() ) );
           break;

      case DELETE:

           
           nPos = tp.findToken( "from" );

           if ( nPos < 0 )
             throw new Exception( "Invalid delete statement, did not find expected from clause");

      
           if ( ! (tp.getToken( sb ) == VwTextParser.WORD ) )
             throw new Exception( "Invalid delete statement, unexpected end of stement found following the from clause");
           
           // *** Next word will be the name of the table we're deleting form

           listTable.add( new VwTableName( sb.toString() ) );
           
           break;

      case SELECT:

           nPos = 0;

           nPos = tp.findToken( "from" );

           if ( nPos < 0 )
             throw new Exception( "Invalid select statement, did not find expected from clause");

           StringBuffer sbTable = new StringBuffer();

           boolean fGotInner = false;
           
           while( nPos >= 0 )
           {
             // Stay in word loop until we hit a where, order or group or left paren token

             int nPieceCount = 0; // Nbr of elements in table ( cound be 2 table name + alias )
             
             while( true )
             {

               String strWord;

               if ( tp.getToken( sb ) == VwTextParser.EOF )
                 break;

               strWord = sb.toString();

               if ( strWord.charAt( 0 ) == ',' )
               {
                 nPieceCount = 0;
                 listTable.add( new VwTableName( sbTable.toString() ) );
                 sbTable.setLength( 0 );
                 
                 continue;          // We have another table name in the list,
                                    // add the one we've just built
               }

               if ( strWord.charAt( 0 ) == '(' )
                 continue;             // probably an inner select look for next from keywird

               if ( strWord.equalsIgnoreCase( "where" ) || strWord.equalsIgnoreCase( "order" )
                    || strWord.equalsIgnoreCase( "group" ) )
                  break;

               if ( isJoinSyntax( strWord ) ) 
               {
                 fGotInner = true;
                 listTable.add( new VwTableName( sbTable.toString() ));

                 while( true )
                 {
                   sbTable.setLength( 0 );
                   int nTokPos = tp.findToken( "join" );
                   
                   if ( nTokPos < 0 )
                     break;
                   
                   if ( tp.getToken( sb ) == VwTextParser.EOF )
                     break;

                   // Table name
                   strWord = sb.toString();
                   
                   sbTable.append( strWord );

                   if ( tp.getToken( sb ) == VwTextParser.EOF )
                     break;

                   // possible alia name could be the keyword 'as'
                   strWord = sb.toString();

                   if ( strWord.equalsIgnoreCase( "as" ))
                   {
                     if ( tp.getToken( sb ) == VwTextParser.EOF )
                       break;
                     strWord = sb.toString(); // this is alias name
                   }
                   
                   sbTable.append( " " ).append( strWord );
                   
                   listTable.add( new VwTableName( sbTable.toString() ));
                   
                   // do we have any more joins
                   
                   for ( int x = 0; x < s_astrJoinKeywords.length; x++ )
                   {
                     
                     nTokPos = tp.findToken( s_astrJoinKeywords[ x ] );
                     if ( nTokPos >= 0 )
                       break;
                   }
                   
                   if ( nTokPos < 0 )
                     break;
                   
                 }
                 // 
               }
               // *** If we get here we have a table or an alias we keep the two together

               if ( fGotInner )
                 break;
               
               if ( nPieceCount == 1 ) // we have the table name this must be an alias
                 sbTable.append( " " ).append( strWord );
               else
                 sbTable.append( strWord );
               
               ++nPieceCount;

               if ( nPos < 0 )
                 break;

             } // end while()

             // Add table if there is one

             if ( fGotInner )
               break;
             
             if ( sbTable.length() > 0 )
               listTable.add( new VwTableName( sbTable.toString() ));

              break;
              
 
           } // end while()

           break;

      case UPDATE:

           nPos = tp.findToken( "update" );

           
           if ( ! (tp.getToken( sb ) == VwTextParser.WORD ) )
             return null;
           
           // *** Next word will be the name of the table we're deleting form

           listTable.add( new VwTableName( sb.toString() ) );
           
           break;

    } // end switch()

    return listTable;

  }  // end getTableList()

  
  private boolean isJoinSyntax( String strWord )
  {
    for ( int x = 0; x < s_astrJoinKeywords.length; x++ )
    {
      if ( strWord.equalsIgnoreCase( s_astrJoinKeywords[ x ] ))
        return true;
    }
    
    return false;
    
  }



  public String getProcName() throws Exception
  {
    String strSQL = m_strSQL;   // Lower case for easy serach

    VwTextParser tp = new VwTextParser( new VwInputSource( strSQL ) );
    tp.setDelimiters( "(" );
    
    StringBuffer sbToken = new StringBuffer();
    
    int nPos = tp.findToken( "call" );
    
    if ( nPos < 0 )
      throw new Exception( "invalid stored procedure syntax, expecting the 'call' keyword");
    
    if ( tp.getToken( sbToken ) == VwTextParser.EOF )
      throw new Exception( "invalid stored procedure syntax, expecting the stored procedure name following call but got end of statement" );

    return sbToken.toString();
    
  }

  /**
   * Builds a delimited string of SQL data types that correspond to the parameter types
   *
   * @param dlmsColList - The column list to get the data types for
   * @param dlmsTables - A delimited string of table names
   *
   * @return A delimited string of SQL types converted to string form
   */
  private VwDelimString buildTypesList( VwDelimString dlmsColList, VwDelimString dlmsTables )
    throws Exception
  {
    if ( dlmsColList == null )                  // No variable place holders exist in query
      return null;

    VwDelimString dlms = new VwDelimString();

    int nCount = dlmsTables.count();

    String[] astrTables = new String[ nCount ];
    String[] astrQualifiers = new String[ nCount ];

    String strTable = null;                     // Table name

    int ndx = -1;

    DatabaseMetaData md = m_db.getConnection().getMetaData();

    Hashtable htTableMetaData = new Hashtable();

    while( (strTable = dlmsTables.getNext() ) != null )
    {
      ++ndx;

      strTable = strTable.trim().toUpperCase();

      // *** this test is for oracle database link names which must be stripped off
      // *** for metadata lookup purposes

      int nPos = strTable.indexOf( '@' );   //Oracle database link test

      if ( nPos > 0 )
        strTable = strTable.substring( 0, nPos );

      String strSchema = null;

      nPos = strTable.indexOf( '.' );

      if ( nPos > 0 )
      {
        strSchema = strTable.substring( 0, nPos );
        strTable = strTable.substring( nPos + 1 );
      }

      // Test to see if we have a correlation name
      nPos = strTable.indexOf( ' ' );

      if ( nPos > 0 )
      {
        astrTables[ ndx ] = strTable.substring( 0, nPos );

        nPos = VwExString.eatWhiteSpace( strTable, nPos, 1 );
        astrQualifiers[ ndx ] = strTable.substring( nPos );

        strTable = astrTables[ ndx ];  // Reassign table with out the correlation name

      }
      else
      {
        astrTables[ ndx ] = VwExString.strip( strTable, ", \r\n" );
        astrQualifiers[ ndx ] = null;
      }

      Hashtable htColMetaData = new Hashtable();
      ResultSet rs = null;
      
      synchronized (s_semi )
      {
        rs = md.getColumns( null, strSchema,
                                    strTable, null );

      }
      // Build a hashtable of column names and their SQL data types

     nCount = 0;

      while( rs.next() )
      {
        String strColName = rs.getString( 4 ).toUpperCase();
        htColMetaData.put( strColName, rs.getString( 5 ) );
        ++nCount;
      }

      rs.close();
      rs = null;

      if ( nCount == 0 )
      {
        String strError = m_msgs.getString( "Vw.Db.NoMetaData" );
        strError = VwExString.replace( strError, "<TABLE>", "<" + strTable + ">" );
        throw new Exception( strError );
      }

      // *** add column hashtabel to the hashtable of table names

      htTableMetaData.put( strTable, htColMetaData );

    } // end while( (strTable = dlmsTables.getNext() ) != null )

    String strParamOrCol;     // Parameter or column name

    while( (strParamOrCol = dlmsColList.getNext() ) != null )
    {
      String strQual = null;
      String strSchema = null;

      strParamOrCol = strParamOrCol.toUpperCase();

      int nPos = strParamOrCol.indexOf( '.' );
      if ( nPos > 0 )
      {
        strQual = strParamOrCol.substring( 0, nPos );

        strParamOrCol = strParamOrCol.substring( nPos + 1 );
      }

      if ( strQual != null )
      {
        strTable = null;

        for ( int x = 0; x < astrQualifiers.length; x++ )
        {
          if ( astrQualifiers[ x ] != null && astrQualifiers[ x ].equalsIgnoreCase( strQual ) )
          {
            strTable = astrTables[ x ];
            break;
          } // end if()

        } // end for()

      }
      else
        strTable = null;

      if ( strTable != null )
      {

        Hashtable htColDataTypes = (Hashtable)htTableMetaData.get( strTable );

        String strColDataType = (String)htColDataTypes.get( strParamOrCol );
        if ( strColDataType != null )
          dlms.add( strColDataType );
        else
          throw new Exception( "Could not find table with a column named " + strParamOrCol );

      } // end if

      else
      {
        boolean fFound = false;

        for ( int x = 0; x < astrTables.length; x++ )
        {
          strTable = astrTables[ x ];

          Hashtable htColDataTypes = (Hashtable)htTableMetaData.get( strTable );

          String strColDataType = (String)htColDataTypes.get( strParamOrCol );
          if ( strColDataType != null )
          {
            dlms.add( strColDataType );
            fFound = true;
            break;
          }

        } //end for()

        if ( !fFound )
        {
          String strMsg = m_msgs.getString( "Vw.Db.InvalidColName" );

          strMsg = VwExString.replace( strMsg, "COLNAME", strParamOrCol );
          strMsg = VwExString.replace( strMsg, "TABLE", strTable );

          throw new Exception( strMsg );
        }

      } // end else


    } // end while()

    return dlms;

  } // end buildTypesList()


  /**
   * Parses an INSERT statement to find the insert column names
   * @param m_listParams 
   *
   * @exception Throws Exception if any parsing or possible syntax errors occur
   */
  private void doInsert() throws Exception
  {
     
    int nPos = m_strVanillaSQL.indexOf( ':' );
    
    if ( nPos > 0 )
    {
      m_listParams = getBindParamNames();
      return;
    }
    else
    {
      nPos = m_strVanillaSQL.indexOf( '?' );
      if ( nPos < 0 )
        return;
      
    }
    
    VwTextParser tp = new VwTextParser( new VwInputSource( m_strVanillaSQL ));
    tp.setDelimiters( "()," );
    StringBuffer sbToken = new StringBuffer();
    
    nPos = tp.findToken( "into" );
    
    if ( nPos < 0 )
      throw new Exception( "Invalid insert statement, did not find the expected sql keyword 'into'");
    
    // Next token should be the table name
    if ( tp.getToken( sbToken ) == VwTextParser.EOF )
      throw new Exception( "Invalid insert statement, did not find table name following the into clause" );
      
    String strTableName = sbToken.toString();
    
    if ( tp.getToken( sbToken ) == VwTextParser.EOF )
      throw new Exception( "Invalid insert statement, premature end of sql string following the table name '" + strTableName + "'" );
    
    String strWord = sbToken.toString();
    
    if ( strWord.toLowerCase().equals( "select" ))
    {
      m_listParams = getBindNamesFromJDCBPlaceholders();
      return;
    }
    else
    if ( strWord.toLowerCase().equals( "values" ))
      throw new Exception( "Invalid insert statement\nWhen using the bind parameter placeholder '?', the insert statement requires the column list follwoing the table name " );
    
    if ( ! strWord.equals( "(" ))      
      throw new Exception( "Invalid insert statement,Expected the '(' token to start the column list " );
      
    int nCurPos = tp.getCursor();
    
    int nTemp = tp.findToken( ")" );
    
    if ( nTemp < 0 )
      throw new Exception( "Invalid insert statement,Expected the ')' token following the column list " );
    
    tp.setCursor( ++nTemp );
    
    tp.getToken( sbToken );
    
    strWord = sbToken.toString();
    
    if ( strWord.equalsIgnoreCase( "select" ) )
    {
      m_listParams = getBindNamesFromJDCBPlaceholders();
      return;
  
    }
    if ( !strWord.toLowerCase().equals( "values" ))
      throw new Exception( "Invalid insert statement\nWhen using the bind parameter placeholder '?', the insert statement requires the column list follwoing the table name " );
    
    // reset cursir back to start of the insert column list
    tp.setCursor( nCurPos );
    
    ArrayList listParams = new ArrayList();
    
    while ( true )
    {
      if ( tp.getToken( sbToken ) == VwTextParser.EOF )
        throw new Exception( "Invalid insert statement, premature end of sql string following the table name '" + strTableName + "'" );
     
      strWord = sbToken.toString();
      
      if ( strWord.equals( ")" ) )
        break;   // All done
      else
      if ( strWord.equals( "," ) )
        continue;
      
      listParams.add( strWord );
        
    } // end while()
    
    m_listParams = listParams;
    
  } // end doInsert()


  private ArrayList getBindNamesFromJDCBPlaceholders() throws Exception 
  {

    int nPos = m_strVanillaSQL.indexOf( '?' );
    
    if ( nPos < 0 )
      return null;
    
    ArrayList listParams = new ArrayList();
    StringBuffer sbToken = new StringBuffer();
    
    VwTextParser tp = new VwTextParser( new VwInputSource( m_strVanillaSQL), m_strVanillaSQL.length() -1,  -1 );
     
    while( nPos >= 0 )
    {
      tp.setCursor( nPos - 1 );
      if ( tp.getToken( sbToken ) == VwTextParser.EOF  )
        break;
      
      String strWord = sbToken.toString();
      if ( strWord.equals( "=" ) || strWord.equals( ">" ) || strWord.equals( "<" ) || strWord.equals( "<>" ) ||
           strWord.equals( "!=" ) ||  strWord.equals( ">=" ) || strWord.equals( "<=" ) || strWord.equalsIgnoreCase( "like" ) )
      {
        int nCursorPos = tp.getCursor();
        
        if ( tp.getToken( sbToken ) == VwTextParser.EOF )
          throw new Exception( "Unexpected end of sql string found at position: " + nCursorPos + "expecting column name to precede operator");
        
        strWord = sbToken.toString();

        if ( strWord.toLowerCase().equals( "and" ) )
          throw new Exception( "The ? bind operator cannot be used when using the between operator. You must use the :<your bind named> style of place holder");;
          
        
        if ( strWord.toLowerCase().equals( "not" ) )
        {
          
          if ( tp.getToken( sbToken ) == VwTextParser.EOF )
            throw new Exception( "Unexpected end of sql string found at position: " + nCursorPos + "expecting column name to precede operator");
          
          strWord = sbToken.toString();
        }
        
        int nPosDot = strWord.lastIndexOf( '.' );
         
        listParams.add( strWord.substring( ++nPosDot ) );
        
      } // end if
      
      nPos = m_strVanillaSQL.indexOf( '?', ++nPos );
      
    }
    
    return listParams;
  }



  /**
   * Get the param list and param type list for a stored procedure
   *
   * @exception Throws SQLException if any errors occur
   */
  private void doProc() throws Exception
  {
    // *** Extract the procedure name so we can get the param col info

    StringBuffer sbToken = new StringBuffer();

    VwTextParser tp = new VwTextParser( new VwInputSource( m_strVanillaSQL ));
    tp.setDelimiters( "(=" );
    
    if ( tp.findToken( "call" ) < 0 )
      throw new Exception( "missing the 'call' keyworkd in stored procedure call statement");
    
    tp.getToken( sbToken );
    
    // Next token is the stored proc name
    String strProcName = sbToken.toString();
    int nPos = strProcName.indexOf( '.' );

    m_strUserID = null;

    String strPackName = null;        // For oracle 888
    if ( nPos > 0 )
    {
      m_strUserID =  strProcName.substring( 0, nPos );
      strProcName = strProcName.substring( nPos + 1 );

      nPos = strProcName.indexOf( '.' );


      if ( nPos > 0 )
      {
        strPackName = strProcName.substring( 0, nPos );
        strProcName = strProcName.substring( nPos + 1 );
      }

    }

    if ( m_strUserID == null )
      m_strUserID = m_db.getDbMgr().getUserID();

    nPos = m_strSQL.indexOf( '(' );

    if ( nPos < 0 )
      throw new SQLException( m_msgs.getString( "Vw.Db.InvalidProc" ) );



    // *** Enumerate the procedure columns

   Iterator iProcCols = m_db.getProcedureColumns( null, m_strUserID, strProcName, null ).iterator();

    if ( !iProcCols.hasNext() )
    {
      if ( m_strUserID != null )
        iProcCols = m_db.getProcedureColumns( null, null, strProcName, null ).iterator();
    }

    int nParamNbr = 0;

    nPos = m_strVanillaSQL.indexOf( '?' );
    
    if ( nPos >= 0 )
    {
      while( iProcCols.hasNext() )
      {
  
        VwColInfo ci = (VwColInfo)iProcCols.next();
        String strColName = ci.getColumnName();
  
        String strParamNbr = null;
  
        if ( ci.getProcParamType() == VwColInfo.IN || ci.getProcParamType() == VwColInfo.INOUT ||
             ci.getProcParamType() == VwColInfo.OUT || ci.getProcParamType() == VwColInfo.RETURN )
          strParamNbr = String.valueOf( ++nParamNbr );
  
        // *** Some databases like SQL server and Sybase want proc name to start with
        // *** a special symbol. we will strip that off
  
        if ( strColName.charAt( 0 ) == '@' )
          strColName =  strColName.substring( 1 );
  
        if ( ci.getProcParamType() == VwColInfo.IN || ci.getProcParamType() == VwColInfo.INOUT )
        {
          if ( m_listParams == null )
          {
            // *** Create the paran name and types lists
            m_listParams = new ArrayList();
            m_listParamTypes = new ArrayList();
          }
  
          // *** Stored procedures have the format <param nbr>:<paramName>
  
          m_listParams.add( strParamNbr + ":" + strColName );
          m_listParamTypes.add( String.valueOf( ci.getSQLType() ) );
  
        } // end if
  
        if ( ci.getProcParamType() == VwColInfo.OUT || ci.getProcParamType() == VwColInfo.INOUT  ||
             ci.getProcParamType() == VwColInfo.RETURN )
        {
          if ( m_listResults == null )
          {
            m_listResults = new ArrayList();
            m_listResultTypes =  new ArrayList();
          }
  
          // *** Stored procedures have the format <param nbr>:<paramName>
  
          m_listResults.add( strParamNbr + ":" + strColName );
          m_listResultTypes.add( String.valueOf( ci.getSQLType() ) );
        }
  
      } // end while()

    } // end if
  } // end doProc()


  /**
   * Alters any param placeholder names that start with the colon ':' character to the
   * JDBC compliant '?' symbol so the the string ":hiValParam" would be replaced by a single
   * '?'.  This is necessary to prepare the SQL statement with the proper JDBC param syntax.
   * Also remove any comment lines or groups from the statement
   *
   */
  private void xlateVanillaSQL() throws Exception
  {
    
    int nStartPos = 0;
    
    String strExtensionSQL = null;
    
    String strTempSQL = m_strVanillaSQL.trim();
    VwTextParser tp = new VwTextParser( new VwInputSource( strTempSQL ), strTempSQL.length() -1, -1 );
    
    nStartPos = tp.findToken( "into" );
    
    if ( nStartPos > 0 && strTempSQL.length() >= 6 && strTempSQL.substring( 0, 6).equalsIgnoreCase( "select"))
    {
      strExtensionSQL = strTempSQL.substring( nStartPos );
      strTempSQL = strTempSQL.substring( 0 , nStartPos );
      
    }
    
    if ( strExtensionSQL != null )
      doExtensionSQL( strExtensionSQL );
    
    nStartPos = 0;
    
    int nCurPos = VwExString.findAny( strTempSQL, "/-:", 0 );

    // Statement is fine as is, except the vanilla sql
    if ( nCurPos < 0 )
    {
      m_strSQL = strTempSQL;
      return;
    }

    //  StringBuffer will hold are extracted or translated statement
    int nLen = strTempSQL.length();

    StringBuffer sb = new StringBuffer( strTempSQL.length() );

    while( nCurPos >= 0 )
    {
      // Test first to see if we have the beginning of a comment symbol either // or --
      if ( strTempSQL.charAt( nCurPos ) == '/'
           || strTempSQL.charAt( nCurPos ) == '-')
      {
        if ( (nCurPos + 1) < nLen )     // Make sure we're not at the last character
        {
          String strEnd = null;         // look at next character to see if we're a comment

          if ( strTempSQL.charAt( nCurPos + 1 ) == '/' ||
               strTempSQL.charAt( nCurPos + 1 ) == '-' )
            // this is a single line comment, advance cursor to next \n
            strEnd = "\n";
          else
          if ( strTempSQL.charAt( nCurPos ) == '/' &&
               strTempSQL.charAt( nCurPos + 1 ) == '*' )
            strEnd = "*/";    // This is a multi=line comment

          if ( strEnd != null )
          {
            // First grab everything up to this point.
            sb.append( strTempSQL.substring( nStartPos, nCurPos ) );

            nCurPos = strTempSQL.indexOf( strEnd, nCurPos );

            if ( nCurPos < 0 )
            {
              nStartPos = -1;
              continue;
            }

            // Look for next special character
            nStartPos = nCurPos + strEnd.length();  // Back to top of loop to look at next sequence


            nCurPos = VwExString.findAny(strTempSQL, "/-:", nStartPos );
            continue;

          } // end if

        } // end if (nCurPos + 1)

      } // end if  if ( strTempSQL ...

      // We found a colon character if we get here
      // Copy original SQL up to the colon character

      if ( strTempSQL.charAt( nCurPos ) != ':' )
      {
        sb.append( strTempSQL.substring( nStartPos, ++nCurPos ) );
        nStartPos = nCurPos;
      }
      else
      {
        sb.append( strTempSQL.substring( nStartPos, nCurPos ) );

        // A double colan is a postgres typecast operator its not a variable to be resolved
        if ( strTempSQL.charAt( nCurPos + 1 ) == ':')
        {
          sb.append( "::" ); // add the ? replcement for the colon character
          nCurPos += 2;
          nStartPos = nCurPos;
          continue;

        }

        sb.append( "?" ); // add the ? replcement for the colon character

        // *** Bypass the paran name to first white space
        nStartPos = ++nCurPos;

        // Advance past the userdefined name

        while( true )
        {
          if ( nStartPos == nLen )
          break;

          char ch = strTempSQL.charAt( nStartPos );
          if ( VwExString.isWhiteSpace( ch )  || VwExString.isin( ch, "-;/,()" ) )
            break;

          ++nStartPos;
        }

      } // end else

      // Find next occurrence

      nCurPos = VwExString.findAny(strTempSQL, "/-:", nStartPos );

    } // end while()

    // *** Add in final original segment

    if ( nStartPos >= 0 && nStartPos < nLen )
    {
      sb.append( strTempSQL.substring( nStartPos ) );
    }


    m_strSQL = sb.toString();  // get the sql string we will execute

    
  } // end xlateVanillaSQL()


  private void doExtensionSQL( String strExtensionSQL ) throws Exception
  {  
    StringBuffer sb = new StringBuffer();
    
    VwTextParser tp = new VwTextParser( new VwInputSource(strExtensionSQL) );
    tp.getToken( sb );
    
    if ( !sb.toString().toLowerCase().equals( "into") )
      throw new Exception( "Expecting key word 'into' following the select clause but got "  + sb.toString());
    
    // Next token should be class name
    
    if ( tp.getToken( sb ) == VwTextParser.EOF )
      throw new Exception( "Unexpected EOF encountered. Expecting name of java class to hold query" );
    
    String strClassName = sb.toString();
    
    m_clsStmtClass = convertToClass( strClassName );
    
    if ( tp.getToken( sb) == VwTextParser.EOF )
      return;
        
    if ( !sb.toString().toLowerCase().equals( "for") ) 
      throw new Exception( "Expecting key word 'for' following the into caluse but got " + sb.toString());

    if ( tp.getToken( sb ) == VwTextParser.EOF )
      throw new Exception( "Unexpected EOF encountered follwoing te 'for' keyword. Expecting the setter property name in the form (ClassName.propName)" );
    
    String strTargetName = sb.toString();
    
    int nPos = strTargetName.lastIndexOf( '.' );
    
    if ( nPos < 0 )
      throw new Exception( "Expecting the setter target to be in the form ClassName.propertyName but go " + strTargetName );
    
    strClassName = strTargetName.substring( 0, nPos );
    
    m_clsTargetClass = convertToClass( strClassName );
    
    String strPropName = strTargetName.substring( ++nPos );
    
    PropertyDescriptor prop = VwBeanUtils.getPropDescriptor( m_clsTargetClass, strPropName );
    
    if ( prop == null )
      throw new Exception( "Could not find property name '" + strPropName + "' for bean " + m_clsTargetClass.getName() );
    
    Method mthdSetter = prop.getWriteMethod();
    
    if ( mthdSetter == null )
      throw new Exception( "Could not find setter property name '" + strPropName + "' for bean " + m_clsTargetClass.getName() );
      
    m_mthdTargetProperty = mthdSetter;
    
    Class[] aclsParamTypes = m_mthdTargetProperty.getParameterTypes();
    
    
    if ( aclsParamTypes.length > 1 )
      throw new Exception( "The property '" +  strPropName + "' must be a standard bean setter method that takes only one parameter");

    m_clsMethodParam = aclsParamTypes[ 0 ];
    
    m_fIsTargetPropList = false;
    
    if ( java.util.Collection.class.isAssignableFrom( m_clsMethodParam ) )
      m_fIsTargetPropList = true;
    else
    {
      if ( ! (m_clsMethodParam == m_clsStmtClass) )
        throw new Exception( "The property '" +  strPropName + "' is defined to take a class type of '" + m_clsMethodParam +
                             "' but the select into clause specified a class type of '" + m_clsStmtClass + "'" );
       
    }
    
  } // end doExtensionSQL()
  
  
  /**
   * Create the Class object for the class name specified
   * @param strClassName The String name of the class
   * 
   * @return The Class object for the name specified
   */
  private Class convertToClass( String strClassName ) throws Exception
  {
    int nPos = strClassName.indexOf( '.');
    
    if ( nPos < 0 )
    {
      if ( m_strPackageName == null )
        throw new Exception( "A fully qualified class name must be useed (package.ClassName) when the use package statement is not present ");
      
      strClassName = m_strPackageName + "." + strClassName;
    }
    
    // Try to access the class 
    
    try
    {
      Class clsTarget = null;
      
      if ( m_loader != null )
        clsTarget = Class.forName( strClassName, true, m_loader );
      else
        clsTarget = Class.forName( strClassName );
      
      return clsTarget;
    }
    catch( Exception ex )
    {
      throw new Exception( "Could not load the class " + strClassName + ". Check to make sure the class name is spelled right and its package is in the classpath");
      
    }
    
  } // end 

} // end class VwSqlParser{}


// *** End VwSqlParser.java

