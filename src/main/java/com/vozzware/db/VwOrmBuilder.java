/*
===========================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

  THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
  PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
  CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

  Source Name: VwOrmBuilder.java

  Create Date: Feb 12, 2005
============================================================================================
*/
package com.vozzware.db;

import com.vozzware.util.VwBeanUtils;
import com.vozzware.util.VwDelimString;
import com.vozzware.util.VwLogger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author P. VosBurgh
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class VwOrmBuilder
{
  public static List<VwSqlData> buildMappings( VwDatabase db , String strSqlStatements ) throws Exception
  { return  buildMappings( db, strSqlStatements, null ); }
  
  
  public static List<VwSqlData> buildMappings( VwDatabase db , String strSqlStatements, ClassLoader loader ) throws Exception
  {
    VwDelimString dlmsStatements = new VwDelimString( ";", strSqlStatements );
    Map<Class<?>,VwSqlData> mapSqlData = new HashMap<Class<?>,VwSqlData>();
    
    List<VwSqlData> listSqlData = new ArrayList<VwSqlData>();
    
    while ( dlmsStatements.hasMoreElements() )
    {
      String strSqlStatement = dlmsStatements.getNext();
      
      VwSqlParser sp = new VwSqlParser(db, strSqlStatement, loader );
      
      VwSqlData sdParent = sp.runParser();
      
      mapSqlData.put( sdParent.m_clsStmtClass, sdParent );
      
      if ( sdParent.m_clsTargetClass != null && mapSqlData.containsKey( sdParent.m_clsTargetClass ) )
      {
        VwSqlData sdTarget = (VwSqlData)mapSqlData.get( sdParent.m_clsTargetClass );
        if ( sdTarget.m_listSqlDataDependencies == null )
        {
          sdTarget.m_listSqlDataDependencies = new ArrayList<VwSqlData>();
        }
        
        sdTarget.m_listSqlDataDependencies.add( sdParent );
        
      }
      else
      {
        listSqlData.add( sdParent );
      }
      
      
    }
    
    return listSqlData;
    
  } // end buildMappings()
  
  
  public static Object buildObject( Object objParams , VwDatabase db, List listSqlData, VwLogger logger, Object objBean ) throws Exception
  {
    List  listTopLevel = new ArrayList();
    
    Object objStmt = null;
    
    Collection list = null;
    
    
    for ( Iterator iSqlData = listSqlData.iterator(); iSqlData.hasNext(); )
    {
      VwSqlData sqlDataParent = (VwSqlData)iSqlData.next();
      VwSqlMgr sqlMgrParent = new VwSqlMgr( db );
      sqlMgrParent.setLogger( logger );
      
      Class clsStmt = sqlDataParent.m_clsStmtClass;
      
      if ( clsStmt == null )
      {
        throw new Exception( "Class name missing for sql statement " + sqlDataParent.m_strSQL );
      }
      
      sqlMgrParent.exec( sqlDataParent, objParams );
      
      while( true )
      {
        objStmt = sqlMgrParent.getNext( clsStmt, objBean );
        
        if ( objStmt == null )
        {
          break;
        }
        
        listTopLevel.add( objStmt );
          
        if ( sqlDataParent.m_clsTargetClass != null )
        {
          if ( sqlDataParent.m_clsTargetClass == objParams.getClass() )
          {
            if ( Collection.class.isAssignableFrom( sqlDataParent.m_clsMethodParam ) )
            {
              if ( list == null )
              {
                list = new ArrayList();
              }
              
              list.add( objStmt );
              
            }
            else
            {
              sqlDataParent.m_mthdTargetProperty.invoke( objParams, new Object[]{ objStmt } );
            }
           
          } // end if
        } // end if
        
        if ( sqlDataParent.m_listSqlDataDependencies != null )
        {
          buildObject( objStmt, db, sqlDataParent.m_listSqlDataDependencies, logger, objBean );
        }
          
        if ( objStmt instanceof VwDVOBase )
        {
          ((VwDVOBase)objStmt).setDirty( false );
        }

        // If this class defined the postSuperInit() method call it now as the super class has been initiallzed
        if ( VwBeanUtils.hasMethod( objStmt, "postSuperInit", null ))
        {
          VwBeanUtils.execVoidMethod( objStmt, "postSuperInit", null  );
        }

      } // end while()
      
      sqlMgrParent.close();
            
      if ( list != null )
      {
        sqlDataParent.m_mthdTargetProperty.invoke( objParams, new Object[]{ list } );
        list = null;
        
      }
      
    } // end for()
    
   
    if ( listTopLevel.size() == 0 )
    {
      return null;
    }
    else
    if ( listTopLevel.size() == 1 )
    {
      return listTopLevel.get( 0 );
    }
    
    return listTopLevel;
    
      
  }
} // end class  VwOrmBuilder{}

// *** End of  VwOrmBuilder.java ***

