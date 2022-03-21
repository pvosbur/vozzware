package com.vozzware.util;

/*PBV
import sun.org.mozilla.javascript.internal.Context;
import sun.org.mozilla.javascript.internal.NativeArray;
import sun.org.mozilla.javascript.internal.NativeObject;
*/

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Map;

/**
 * This class provides support for the JavaScript Rhino engine
 */
public class VwJavaScriptUtils
{
  private Invocable m_jsInv;
  private ScriptEngine m_jsEngine;


  /**
   * Constructor to create JavaScript engine
   */
  public VwJavaScriptUtils()
  {
    // create a script engine manager
    ScriptEngineManager factory = new ScriptEngineManager();

    // create a JavaScript engine
    m_jsEngine = factory.getEngineByName( "JavaScript" );
    m_jsInv = (Invocable)m_jsEngine;

  }


  /**
   * Process a file with JavaScript functions
   *
   * @param urlJavaScript the URL to the JavaScript file
   *
   * @throws Exception if the file cannot be processed
   */
  public void addJavaScript( URL urlJavaScript ) throws Exception
  {

    m_jsEngine.eval( new InputStreamReader( urlJavaScript.openStream() ) );

  }


  /**
   * Adds java script functions defined in a String object
   * @param strJavaScript
   * @throws Exception
   */
  public void addJavaScript( String strJavaScript ) throws Exception
  {

    m_jsEngine.eval( strJavaScript );

  }

  /**
   * Ececute the named java script function that takes no parameters
   *
   * @param strFunctionName  The name of a java script function from a previously loaded java script file
   * using the addJavaScript method
   *
   * @return An object returned by the javaScript function or null if the function has no return value
   * @throws Exception
   */
  public Object execFunction( String strFunctionName ) throws Exception
  { return execFunction( strFunctionName, (Object)null ); }

  /**
   * Ececute the named java script function
   *
   * @param strFunctionName  The name of a java script function from a previously loaded java script file
   * using the addJavaScript method
   *
   * @param objParam a single object to pass as the function parameter
   *
   * @return An object returned by the javaScript function or null if the function has no return value
   * @throws Exception
   */
  public Object execFunction( String strFunctionName, Object objParam ) throws Exception
  { return execFunction( strFunctionName, new Object[]{ objParam } ); }


  /**
   * Ececute the named java script function
   *
   * @param strFunctionName  The name of a java script function from a previously loaded java script file
   * using the addJavaScript method
   *
   * @param aobjParams Array of object paramaters the function takes or nul if function has no parameters
   *
   * @return An object returned by the javaScript function or null if the function has no return value
   * @throws Exception
   */
  public Object execFunction( String strFunctionName, Object[]aobjParams ) throws Exception
  {
    Object[] aArgs = null;

    if( aobjParams == null )
      aArgs = new Object[]{ strFunctionName, new Object[]{ null} };
    else
      aArgs = new Object[]{ strFunctionName, aobjParams };

    Object objReturn = VwBeanUtils.execMethod( m_jsInv, "invokeFunction", aArgs );

    /*PBV Have to resolve JDK 1.7 issues
    if ( objReturn instanceof NativeObject )
      return nativeObject2Map( (NativeObject)objReturn );

    if ( objReturn instanceof NativeArray )
      return nativeArray2ObjArray( (NativeArray) objReturn );

    */
    return objReturn;


  }


  /**
   * This takes a javaScript function call in the form : functionName( param1, param2... )
   * ex. getCourseId( 'ACCT.12345' ) or getCourseId( ${courseId} ) where ${courseId} is resolved in the param map
   * passed as the secon arg in this method call
   *
   * @param strFunCall The full javascript function call with the call parameters
   * @param mapParams A Map of param names if any ${} param names are used in the function call} may be null
   *
   * @return An object returned by the javaScript function or null if the function has no return value
   * @throws Exception
   */
  public Object execFunction( String strFunCall, Map<String,Object> mapParams ) throws Exception
  {

    if ( strFunCall.startsWith( "js:" ))
       strFunCall = strFunCall.substring( "js:".length() );  // Strip off js: prefix if specified


    if ( mapParams != null )
    {
      // Make sure there are no commas in the mapProperties value as it will cause problems in mapping parameter data

      for ( String strKey : mapParams.keySet() )
      {
        Object objVal= mapParams.get( strKey );
        if ( !(objVal instanceof String ) )
          continue;

         String strVal = (String)objVal;

        if ( strVal.indexOf( ',' ) >= 0 )
        {
          strVal = VwExString.replace( strVal, ",", "-" );
          mapParams.put( strKey, strVal );

        }

      }

      strFunCall = VwExString.expandMacro( strFunCall, mapParams );

    }

    int nPos = strFunCall.indexOf( '(' );

    if ( nPos < 0 )
      throw new Exception( "Expecting opening ( for function call " + strFunCall );

    int nEndPos = strFunCall.indexOf( ')' );

    if ( nEndPos < 0 )
      throw new Exception( "Expecting opening ) for function call " + strFunCall );

    String strFunName = strFunCall.substring( 0, nPos );

    String strParams = strFunCall.substring( ++nPos, nEndPos );

    // Remove any string quotes around parameters
    strParams = strParams.replace( "\"", "" );
    strParams = strParams.replace( "'", "" );
    VwDelimString dlms = new VwDelimString( strParams );
    Object[]astrParmValues = dlms.toObjectArray();

    return execFunction( strFunName, astrParmValues );

  }


  /**
   * Convert the NativeArray type returned from a javascript function call to a regular java Object array
   * @param na The NavtiveArray object returned from the Rhino scripting engine
   * @return a Java Object array
   */
  /*
  private Object[] nativeArray2ObjArray( NativeArray na )
  {

   long  lArrLen = na.getLength();

   Object[]aObj = new Object[ (int)lArrLen ];

   for ( int x = 0; x < lArrLen; x++ )
     aObj[ x ] = na.get( x , null );

   return aObj;



  }

  */

  /**
   * Convert an object created by a javascript function t a Java map
   * @param no The NavtiveObject returned from the Rhino scripting engine
   * @return
   */

  /*
  private Map<String,String> nativeObject2Map( NativeObject no )
  {
    Object[] astrKeys = no.getAllIds();

    Map<String,String> mapValues = new HashMap<String, String>();
    for ( int x = 0; x < astrKeys.length; x++ )
    {
      Object oval = no.get( astrKeys[ x ].toString(), no );
      Object javaValue = Context.jsToJava( oval, Object.class );

      mapValues.put( astrKeys[ x ].toString(), javaValue.toString() );
    }


    return mapValues;
  }

  */
}
