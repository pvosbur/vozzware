package com.vozzware.util.convert.utils;

/*
============================================================================================

    Source File Name: Transaction.java

    Author:           petervosburgh

    Date Generated:   8/2/13

    Time Generated:   6:15 AM

============================================================================================
*/

import com.vozzware.util.VwDelimString;
import com.vozzware.util.VwExString;
import com.vozzware.util.VwFileUtil;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility to convert ActionScript to JavaScript
 */
public class VwAs2Js
{
  private static final String NEW_PROP_FIXUP_MARKER = "//VWPropFixups\n";
  private static final String FUN_FIXUP = "//FUNFIXUP";

  private File m_inFile;

  private String m_strOutDir;

  private String m_strAsFile;  // Initial ActionScript file as a String

  private Map<String,Map<String,String>> m_mapProperties = new LinkedHashMap<String, Map<String,String>>();
  private Map<String, List<String>> m_mapDefparams = new HashMap<String, List<String>>(  );
  private Map<String,String> m_mapStaticFunctions = new LinkedHashMap<String, String>();

  private List<String> m_listPublicFunctions = new ArrayList<String>();

  private int m_nIndent = 4;

  private String m_strIndent = "";

  private String m_strParentObject = null;
  private String m_strClassName = null;


  private static String s_strPropGetSetDefineTemplate = "Object.defineProperty( this, \"${PROP_NAME}\", { get : function()${GET_CODE}, set : function${SET_CODE} } );";
  private static String s_strPropGetDefineTemplate = "Object.defineProperty( this, \"${PROP_NAME}\", { get : function()${GET_CODE} } );";
  private static String s_strPropSetDefineTemplate = "Object.defineProperty( this, \"${PROP_NAME}\", { set : function${SET_CODE} } );";


  /**
   * Converts the actionScript file to Javascript file replacing the extension from .as to .js
   * @param inFile The path to the file to convert
   *
   * @exception if file cannot be read
   */
  public VwAs2Js( File inFile, String strOutDir ) throws Exception
  {
    // Read file into String

    m_inFile = inFile;

    m_strAsFile = VwFileUtil.readFile( inFile );

    m_strOutDir = strOutDir;

    if ( !m_strOutDir.endsWith( "/" ) )
    {
      m_strOutDir += "/";

    }

    convert();
  }


  /**
   * Convert the ActionScript File
   */
  private void convert() throws Exception
  {

    System.out.println( "Converting File: " + m_inFile.getName() );
    for ( int x = 0; x < m_nIndent; x++ )
    {
      m_strIndent += " ";
    }

    // Remove The package declaration as well as any import statements

    int nOff = m_strAsFile.indexOf( "package " );
    int nClassPos = 0;

    String strComments = "";

    if ( nOff >= 0 )
    {

      // we will preserve any comments up to the package

      strComments = m_strAsFile.substring( 0, nOff );

      // look for the class keyword

      nClassPos = m_strAsFile.indexOf( " class " );

      while( true )
      {

        if ( isInComment( nClassPos )) // keep looking
        {
          nClassPos = m_strAsFile.indexOf( " class ", ++nClassPos );
          continue;

        }

        break;
      }

      if ( nClassPos < 0 )  // aussume this is an interface, so get out
      {
        System.out.println( "Skipping file :" + m_inFile.getName() + ", no class keyword found, assuming interface");
        return;
      }

      m_strAsFile = strComments + m_strAsFile.substring( nClassPos );

      nClassPos = m_strAsFile.indexOf( " class " );

      // Since the class constructor is not in JS we have to add parens to the class name

      // Find the first open brace '{'


      // Extract The Class name

      int nBracePos = m_strAsFile.indexOf( "{" );

      m_strClassName = VwExString.getToken( m_strAsFile, nClassPos + 6, 1, null  );

      // See if this object extends a parent object

      int nExtendsPos = m_strAsFile.indexOf( " extends " );

      if ( nExtendsPos > nClassPos && nExtendsPos < nBracePos )
      {
        // should be a legitimate extends and not in a comment

        int nLen = nExtendsPos + " extends ".length();

        m_strParentObject = VwExString.getToken( m_strAsFile, nLen, 1,null );

        // Remove extends from the string

        m_strAsFile = m_strAsFile.substring( 0, nExtendsPos )  + m_strAsFile.substring( nBracePos  );

        nBracePos = m_strAsFile.indexOf( "{" );  // update new brace position

      }
      else
      {
        int nImplementsPos = m_strAsFile.indexOf( " implements " );
        if ( nImplementsPos > nClassPos && nImplementsPos < nBracePos )
        {
          // Remove implements from the string

          m_strAsFile = m_strAsFile.substring( 0, nImplementsPos )  + m_strAsFile.substring( nBracePos  );

          nBracePos = m_strAsFile.indexOf( "{" );  // update new brace position

        }
      }


      // Next we need to see if there is a function that is the constructor and copy its arguments

      nOff = m_strAsFile.indexOf( m_strClassName, nBracePos );

      if ( nOff > 0 )
      {
        int nLParen = m_strAsFile.indexOf( "(", nOff );
        int nRParen = m_strAsFile.indexOf( ")", nOff );

        // Copy the constructor args up to the class name

        String strArgs = m_strAsFile.substring( nLParen, nRParen + 1).trim();

        m_strAsFile = m_strAsFile.substring( 0, nBracePos ).trim() +  strArgs +  "\n" + m_strAsFile.substring( nBracePos  );

        // Find the position of the first function which must be the constructor

        int nFunPos = m_strAsFile.indexOf( "function");

        // back function pointer to the start of the scope operator

        for ( int x = nFunPos - 1; x > 0; x-- )
        {
          if ( ! VwExString.isWhiteSpace(  m_strAsFile.charAt( x ) ) )
          {
            nFunPos = x;
            break;
          }
        }

        for ( int x = nFunPos; x > 0; x-- )
        {
          if ( VwExString.isWhiteSpace(  m_strAsFile.charAt( x ) ) )
          {

            nFunPos = x;
            break;
          }
        }

        // Now we have to remove the constructor function name because we just copied its params up top replacing the class name

        nBracePos = m_strAsFile.indexOf( "{" );
        nOff = m_strAsFile.indexOf( m_strClassName, nBracePos );
        nBracePos = m_strAsFile.indexOf( "{", nOff ) + 1;

        int nEndBraceOff = 0 ;

        // Find the closing brace in the constructor code

        int nOpenBraceCount = 0;

        for ( int x = nBracePos; x < m_strAsFile.length(); x++ )
        {

          char ch = m_strAsFile.charAt( x );

          if  (  ch == '}' )
          {
            if ( nOpenBraceCount > 0 )
            {
              --nOpenBraceCount;
              continue;
            }
            else
            {
              nEndBraceOff = x;
              break;
            }
          }
          else
          if ( ch == '{')
          {
            ++nOpenBraceCount;
          }

        } // end for

        String strConstructorCode =  "\n\n" + m_strIndent +  m_strAsFile.substring( nBracePos + 1, nEndBraceOff ).trim();

        m_strAsFile = m_strAsFile.substring( 0, nFunPos ).trim()  + strConstructorCode + "\n\n" + NEW_PROP_FIXUP_MARKER + m_strAsFile.substring( ++nEndBraceOff ) ;


      } // end if(
      else
      {
       // No constructor found
        m_strAsFile = m_strAsFile.substring( 0, nBracePos ).trim()  + "()\n {\n" + NEW_PROP_FIXUP_MARKER + m_strAsFile.substring( ++nBracePos ) ;
      }

    }
    // also remove the last right brace

    nOff = m_strAsFile.lastIndexOf( "}" );

    m_strAsFile = m_strAsFile.substring( 0, nOff );


    m_strAsFile = VwExString.replace( m_strAsFile, "class ", "function " );

    m_strAsFile = VwExString.replace( m_strAsFile, "const ", "var " );

    // We can Use VwExString to do most of the stripping logic


    doPropertyFixups( " get ");
    doPropertyFixups( " set " );
    doParameterFixups( "function" );
    doParameterFixups( "catch" );
    doReturnTypeFixups();
    doVariableFixups();
    addNewPropertyFixups();
    doDefParamFixups();
    doStaticFunctionFixups();

    m_strAsFile = VwExString.remove( m_strAsFile, new String[]{  "public ","private ","protected ", "static ", "class ", "void "}, 0 );

    //m_strAsFile = VwExString.replace( m_strAsFile, "public ", "" );

    if ( m_strParentObject != null )
    {

      m_strAsFile += "\n\n// Add Super class prototype\n\n" + m_strClassName + ".prototype = new " + m_strParentObject + "();\n";
    }

    cleanupWhiteSpace();

    writeConvertedFile();


    return;

  }


  private void cleanupWhiteSpace()
  {

    StringBuffer sbFile = new StringBuffer( m_strAsFile );

    int nStartPos = 0;

    int nLen = sbFile.length();

    while( nStartPos < nLen )
    {

      int nNewLinePos = sbFile.indexOf( "\n", nStartPos );

      if ( nNewLinePos < 0 )
      {
        break;

      }

      int nEndWhiteSpace = VwExString.eatWhiteSpace( sbFile.toString(), nNewLinePos, 1  );

      int nNewLineCount = 1;

      int nNewDelLineStart = -1;

      for ( int x = nNewLinePos; x < nEndWhiteSpace; x++ )
      {
        if ( sbFile.charAt( x ) == '\n' )
        {
          ++nNewLineCount;

          if ( nNewLineCount == 4 )
          {
            nNewDelLineStart = x;
          }

        }
      }


      if ( nNewDelLineStart > 0 )
      {

        int x = 0;

        for ( x = nEndWhiteSpace; x > nNewDelLineStart; x-- )
        {
          if ( sbFile.charAt( x ) == '\n' )
          {
            sbFile.delete( nNewDelLineStart, x + 1 );
            //nStartPos = nNewDelLineStart;
            break;
          }
        }
        nStartPos = x;
      }
      else
      {
        nStartPos = nEndWhiteSpace + 1;

      }
    }

    m_strAsFile = sbFile.toString();
  }


  private void doStaticFunctionFixups()
  {

    if ( m_mapStaticFunctions.size() == 0 )
    {
      return;
    }

    int nStartPos = 0;

    StringBuffer sbFile = new StringBuffer( m_strAsFile );
    StringBuffer sbStatics = new StringBuffer( "\n\n// Static Functions");

    for ( String strFunName : m_mapStaticFunctions.keySet() )
    {
      String strMarkers = m_mapStaticFunctions.get( strFunName );

      String[] astrMarkers = strMarkers.split( "," );

      int nBeginPos = sbFile.indexOf( astrMarkers[ 0 ] );

      int nEndPos = sbFile.indexOf( astrMarkers[ 1 ] );


      StringBuffer sbStaticFun = new StringBuffer(sbFile.substring( nBeginPos + astrMarkers[ 0 ].length(), nEndPos ) );


      int nFunNdx = sbStaticFun.indexOf( "function" );

      while( true )
      {
        if ( isInComment(  nFunNdx, sbStaticFun.toString() ) )
        {
          nFunNdx = sbStaticFun.indexOf( "function", ++nFunNdx );
          continue;
        }

        break;
      }

      // remove the original function decl
      sbStaticFun.delete( nFunNdx, nFunNdx + "function".length() );

      int nLParenNdx = sbStaticFun.indexOf( "(" );

       while( true )
       {
         if ( isInComment(  nLParenNdx, sbStaticFun.toString() ) )
         {
           nLParenNdx = sbStaticFun.indexOf( "(", ++nLParenNdx );
           continue;
         }

         break;
       }

      // we have to convert the the argument call to proper java script by adding the = function

      sbStaticFun.insert( nLParenNdx, " = function" );


      int nFunctionPos = sbStaticFun.indexOf( strFunName, nStartPos );

      while( true )
      {
        if ( isInComment( nFunctionPos, sbStaticFun.toString() ) )
        {
          nFunctionPos = sbStaticFun.indexOf( strFunName, ++nFunctionPos );
          continue;
        }

        break;
      }

      sbStaticFun.insert( nFunctionPos, m_strClassName + "." );


      sbStatics.append("\n\n  ").append( sbStaticFun.toString().trim() );

       // Remove the static function from the main file

      sbFile.delete( nBeginPos, nEndPos + astrMarkers[ 1 ].length() );

    }

    //tring strStatics = VwExString.remove( sbStatics.toString(), "function" );

    sbFile.append( sbStatics );


    // Second pass fixes up any other referenece to the statics

    for ( String strFunName : m_mapStaticFunctions.keySet() )
    {
      nStartPos = 0;

      int nFunctionPos = sbFile.indexOf( strFunName, nStartPos );

      // Fixup all code references to this static

      while( nFunctionPos >= 0 )
      {
        while( true )
        {
          if ( isInComment( nFunctionPos, sbFile.toString() ) )
          {
            nFunctionPos = sbFile.indexOf( strFunName, ++nFunctionPos );
            continue;
          }

          break;
        }

        // Some of these could have been done in pass one

        if( sbFile.charAt( nFunctionPos - 1 ) != '.' )
        {
          sbFile.insert( nFunctionPos, m_strClassName + "." );
          nStartPos = nFunctionPos + m_strClassName.length() + strFunName.length() + 1;
        }
        else
        {
          nStartPos = ++nFunctionPos;
        }

        nFunctionPos = sbFile.indexOf( strFunName, nStartPos );

      } // end while()

    }

    m_strAsFile = sbFile.toString();

  }

  /**
   * Add the new style getter and setter property fixups
   */
  private void addNewPropertyFixups()
  {
    int nInsertPoint = m_strAsFile.indexOf( NEW_PROP_FIXUP_MARKER  );
    int nEndMarker = nInsertPoint + NEW_PROP_FIXUP_MARKER.length();

    if ( nInsertPoint < 0 )
    {
      return;

    }

    StringBuffer sb = new StringBuffer( m_strAsFile.substring( 0, nInsertPoint ).trim() );

    if ( m_mapProperties.size() > 0 )
    {
      sb.append( "\n\n" ).append( m_strIndent ).append( "// New Style Property Accessors\n" );
    }

    for( String strPropName : m_mapProperties.keySet() )
    {
      Map<String,String>mapCode = m_mapProperties.get( strPropName );

      String strGetCode = mapCode.get( "get" );
      String strSetCode = mapCode.get( "set" );

      Map<String,String>mapReplaceValues = new LinkedHashMap<String, String>();

      String strPropDef = null;
      mapReplaceValues.put( "PROP_NAME", strPropName );

      if ( strGetCode != null && strSetCode != null )
      {
        mapReplaceValues.put( "GET_CODE", strGetCode );
        mapReplaceValues.put( "SET_CODE", strSetCode );

        strPropDef = VwExString.expandMacro( s_strPropGetSetDefineTemplate, mapReplaceValues );

      }
      else
      if ( strGetCode != null )
      {

        mapReplaceValues.put( "GET_CODE", strGetCode );

        strPropDef = VwExString.expandMacro( s_strPropGetDefineTemplate, mapReplaceValues );
      }
      else
      if ( strSetCode != null )
      {

        mapReplaceValues.put( "SET_CODE", strSetCode );

        strPropDef = VwExString.expandMacro( s_strPropSetDefineTemplate, mapReplaceValues );
      }

      sb.append("\n" ).append( m_strIndent ).append( strPropDef );

    }


    if ( m_listPublicFunctions.size() > 0 )
    {
      sb.append( "\n\n").append( m_strIndent ).append( "// Public Functions\n" );

      for ( String strDecl : m_listPublicFunctions )
      {
        sb.append( "\n" ).append( m_strIndent ).append( strDecl  );


      }
    }
    // append rest of source file

    String strRemainder = m_strAsFile.substring( ++nEndMarker );

    strRemainder = strRemainder.trim();

    sb.append("\n\n").append( m_strIndent ).append( strRemainder );

    m_strAsFile = sb.toString();


  }


  /**
   * This removes the flex datatype following the ':' character
   *
   * @param strSearchType
   */
  private void doParameterFixups( String strSearchType )
  {

    int nOffset = m_strAsFile.indexOf( strSearchType );

    int nStartPos = 0;

    StringBuffer sb = new StringBuffer(  );

    while( nOffset >= 0 )
    {
      nOffset += strSearchType.length();

      if ( isInComment( nOffset ) )
      {
        nOffset = m_strAsFile.indexOf( strSearchType, ++nOffset );
        continue;
      }


      if ( strSearchType.equals( "function" ) )
      {
        String strFunName = VwExString.getToken( m_strAsFile, nOffset, 1, "(" );

        int nOrigOffset = nOffset;

        doPublicOrStaticFunctionFixups( nOffset - "function".length() );

        nOffset = m_strAsFile.indexOf( strFunName, nOrigOffset );

      }

      // Get to o=left paren

      int nLParenPos = m_strAsFile.indexOf( '(', nOffset );

      int nRParenPos = m_strAsFile.indexOf( ')', nOffset ) + 1;


      if ( nStartPos > 0 )
      {
        ++nStartPos;

      }

      // Grab everything up to first character past left paren

      sb.append( m_strAsFile.substring( nStartPos, nLParenPos + 1 ) );

      int nParamOffset = nLParenPos + 1;

      int x = nLParenPos + 1;

      for ( ; x < nRParenPos; x++ )
      {
        char ch = m_strAsFile.charAt( x );

        if ( ch == ':' || ch == ')' || ch == '=' )
        {

          if( ch == ')' )
          {
            ++x;
          }

          if ( ch == '=') // This is a default parameter and requires a fixup
          {

            nParamOffset = x = doDefaultParameter( sb, x, nOffset, nParamOffset );

            continue;

          } // end if

          // grab parameter name up to colan
          sb.append( m_strAsFile.substring( nParamOffset, x ) );

          nParamOffset = x + 1;  // Move past colan

          nParamOffset = VwExString.eatWhiteSpace( m_strAsFile, nParamOffset, 1  );

          // Move past data type
          for ( ; nParamOffset < nRParenPos; nParamOffset++ )
          {
            ch = m_strAsFile.charAt( nParamOffset );

            if ( ch == '=' )
            {
              nParamOffset = doDefaultParameter( sb, nParamOffset, nOffset, nParamOffset );
              break;

            }

            if ( ch == ',')
            {
              break;
            }

            if ( VwExString.isWhiteSpace( ch ))
            {
              break;
            }

          } // end for()


          x = nParamOffset;

        } // end if

      } // end for()

      nStartPos = nRParenPos - 1;

      nOffset = m_strAsFile.indexOf( strSearchType, nStartPos );


    } // end while

    if ( nStartPos < m_strAsFile.length() )
    {
      if ( nStartPos == 0 ) // no matches found -- take everything
      {
       sb.append( m_strAsFile.substring( nStartPos ) );
      }
      else
      {
        sb.append( m_strAsFile.substring( ++nStartPos ) );

      }

     }

     m_strAsFile = sb.toString();


  } // end

  private int doDefaultParameter( StringBuffer sb, int nParamPos, int nOffset,  int nParamOffset )
  {
    String strParamName = null;

    strParamName = VwExString.getToken( m_strAsFile, nParamPos - 1, -1, null );

    int nColanPos = strParamName.indexOf( ':' );

    // Strip off datatype if it exists
    if ( nColanPos > 0 )
    {
      strParamName = strParamName.substring( 0, nColanPos );

    }

     // suck parameter values

    String strDefParamValue = VwExString.getToken( m_strAsFile, nParamPos + 1, 1, ",)" );


    // grab parameter name up to = sign
    sb.append( m_strAsFile.substring( nParamOffset, nParamPos ).trim() );

    nParamPos = VwExString.eatWhiteSpace( m_strAsFile, ++nParamPos, 1 );

    nParamPos += strDefParamValue.length();

    // Get function Name

    String strFunName = m_strAsFile.substring( nOffset, m_strAsFile.indexOf( '(', nOffset ) ).trim();

    int nsbFunPos = sb.indexOf( strFunName + "(");

    if ( nsbFunPos < 0 )
    {
      sb.indexOf( strFunName + " ");
    }
    // Put temp fixup marker to find this function later

    sb.insert( nsbFunPos, FUN_FIXUP + strFunName + "/" );

    List<String>listDefParams = m_mapDefparams.get( strFunName );

    if ( listDefParams == null )
    {
      listDefParams = new ArrayList<String>(  );
      m_mapDefparams.put( strFunName, listDefParams );

    }

    listDefParams.add( strParamName + "," + strDefParamValue  );


    int nCurPos = nParamPos;

    nParamPos = VwExString.eatWhiteSpace( m_strAsFile, nParamPos, 1  );

    if ( m_strAsFile.charAt( nParamPos ) == ')' )
    {

      // we are at the end of the function, get last characters after the default value up to and including the paren
      sb.append( m_strAsFile.substring( nCurPos, ++nParamPos ) );

    }

    return nParamPos;

  }



  private void doPublicOrStaticFunctionFixups( int nOffset )
  {
    int nTokenPos = -1;
    StringBuffer sbToken = new StringBuffer( );
    String strFunName = null;


    nTokenPos = VwExString.getToken( m_strAsFile, sbToken, --nOffset, -1, null );

       // Get function Name
    nOffset += "function".length() + 1;

    strFunName = m_strAsFile.substring( nOffset, m_strAsFile.indexOf( '(', nOffset ) ).trim();

    String strToken = sbToken.toString();

    if ( strToken.equals( "public" ) )
    {

      m_listPublicFunctions.add( "this." + strFunName + " = " + strFunName + ";" );
      return;

    }
    else
    if ( strToken.equals( "static" ) )
    {
      StringBuffer sb = new StringBuffer( m_strAsFile );

      nTokenPos =  VwExString.getToken( m_strAsFile, sbToken, --nTokenPos, -1, null );
      strToken = sbToken.toString();

      int nCommentTokenPos = VwExString.eatWhiteSpace( m_strAsFile, --nTokenPos, -1 );

      if ( m_strAsFile.charAt( nCommentTokenPos ) == '/' )
      {
        nCommentTokenPos = VwExString.findToken( m_strAsFile, "@@/**", nCommentTokenPos, -1, null  );

        if ( nCommentTokenPos >= 0 )
        {
          nTokenPos = nCommentTokenPos;

        }
      }

      // Find end of the function

      int nOpenBrace = m_strAsFile.indexOf( "{", nOffset );
      int nEndFunction = findEndOfFunction( nOpenBrace, m_strAsFile );


      // Grab all code starting with comment if specified

      // Insert static marker

      String strBeginMarker = " /~StaticBegin_" + strFunName;
      String strEndMarker = " /~StaticEnd_" + strFunName;

      sb.insert( nTokenPos, strBeginMarker );
      sb.insert( nEndFunction + strBeginMarker.length(), strEndMarker );

      m_mapStaticFunctions.put( strFunName, strBeginMarker + "," + strEndMarker );

      // Remove the static code

      m_strAsFile = sb.toString();

    }

    return;
  }

  private int findEndOfFunction( int nOpenBrace, String strSrc )
  {

    int nOpenBraceCount = 1;

    int nLen = strSrc.length();
    int nEndFunction = -1;

    for ( int x = ++nOpenBrace; x < nLen; x++ )
    {
      if ( m_strAsFile.charAt( x ) == '{' )
      {
        if ( isInComment( x ))
        {
          continue;
        }

        ++nOpenBraceCount;
      }

      if ( strSrc.charAt( x ) == '}' )
      {

        if ( isInComment( x ) )
        {
          continue;
        }

        --nOpenBraceCount;

        if ( nOpenBraceCount == 0 )
        {
          nEndFunction = x + 1;
          break; //we found the end the the function
        }
      }

    }

    return nEndFunction;


  }
  private void doDefParamFixups()
  {
    if ( m_mapDefparams.size() == 0 )
    {
      return;

    }

    StringBuffer sb = new StringBuffer( m_strAsFile );

    for ( String strFunName : m_mapDefparams.keySet() )
    {

      for( String strParamVal : m_mapDefparams.get( strFunName ) )
      {

        VwDelimString dlms = new VwDelimString( strParamVal );;

        String[] astrPieces = dlms.toStringArray();

        String strParamName = astrPieces[ 0 ];

        String strDefVal = astrPieces[ 1 ];

        String strFunMarker = FUN_FIXUP + strFunName + "/";

        int nFunOffset = sb.indexOf( strFunMarker  );

        String strTypeOfCode = "\n\n" + m_strIndent + "  " + strParamName +  " = (typeof " + strParamName + " == 'undefined' ? " + strDefVal + " : " + strParamName + "); \n";

        // Locate function open brace

        int nStartFun = sb.indexOf( "{", nFunOffset ) + 1;

        sb.insert( nStartFun, strTypeOfCode );

        // Remove the fixup marker
        sb.delete( nFunOffset, nFunOffset + strFunMarker.length() );

      } //end inner for()


    } // end for()

    m_strAsFile = sb.toString();

  }


  private void doReturnTypeFixups()
  {

    int nOffset = m_strAsFile.indexOf( "function" );

    int nStartPos = 0;

    StringBuffer sb = new StringBuffer(  );

    //int nParamOffset = 0;

    while( nOffset >= 0 )
    {
      nOffset += "function".length();


      if ( isInComment( nOffset ))
      {
        nOffset = m_strAsFile.indexOf( "function", ++nOffset );
        continue;

      }

      String strFunName = VwExString.getToken( m_strAsFile, nOffset, 1 , "(" );

      if ( strFunName.equals( m_strClassName ) )
      {
        nOffset = m_strAsFile.indexOf( "function", ++nOffset );
        continue;

      }

       // Get to right paren

      int nRParenPos = m_strAsFile.indexOf( ')', nOffset );

      // Grab everything up to first character past right paren
      sb.append( m_strAsFile.substring( nStartPos, nRParenPos + 1 ) );

      int nParamOffset = nRParenPos + 1;

      for ( ; nParamOffset <  m_strAsFile.length(); nParamOffset++ )
      {
        char ch = m_strAsFile.charAt( nParamOffset );

        boolean fFound = false;

        if( ch == '{' )
        {
          fFound = true;

          --nParamOffset;

          // Preserve any whitespace
          for ( ; nParamOffset <  m_strAsFile.length(); nParamOffset-- )
          {
            if ( !VwExString.isWhiteSpace( m_strAsFile.charAt( nParamOffset ) ))
            {
              break;
            }
          }

          if ( fFound )
          {
            ++nParamOffset; // bump back to first whitespace
            break;

          }
        }

      }

      nStartPos = nParamOffset;

      nOffset = m_strAsFile.indexOf( "function", nStartPos );


    } // end while

    if ( nStartPos < m_strAsFile.length() )
     {
       sb.append( m_strAsFile.substring( nStartPos ) );

     }

     m_strAsFile = sb.toString();


  } // end


  private void doVariableFixups()
  {

    int nOffset = m_strAsFile.indexOf( " var " );

    int nStartPos = 0;

    StringBuffer sb = new StringBuffer(  );

    //int nParamOffset = 0;

    while( nOffset > 0 )
    {
      nOffset += " var ".length();

      if ( isInComment( nOffset ) )
      {
        nOffset = m_strAsFile.indexOf( " var ", ++nOffset );
        continue;
      }


      for ( int x = nOffset; x < m_strAsFile.length(); x++  )
      {
        char ch = m_strAsFile.charAt( x );

        if( ch == ':' )
        {

          sb.append( m_strAsFile.substring( nStartPos, x  ) );
          ++x;

          // Look for semi-colan terminator or first whitespace

          for( ; x < m_strAsFile.length(); x++  )
          {
            ch = m_strAsFile.charAt( x );

            if ( ch == ';' || ch == '=' )
            {
              break;
            }
          }

          nStartPos = x;

          break;

        }
      }



      nOffset = m_strAsFile.indexOf( " var " , nStartPos );


    } // end while

    if ( nStartPos < m_strAsFile.length() )
     {
       sb.append( m_strAsFile.substring( nStartPos ) );

     }

     m_strAsFile = sb.toString();


  } // end

  private void doPropertyFixups( String strPropType )
  {

    int nOffset = m_strAsFile.indexOf( strPropType );

    int nStartPos = 0;

    while( nOffset > 0 )
    {

      // put our fixup marker here if not defined

       // Make sure thus is not inside a comment
      if ( isInComment( nOffset ) )
      {
        nOffset = m_strAsFile.indexOf( strPropType, nOffset + strPropType.length() );
        continue;

      }

      // get everything up to the left paren. that will be the property name
      StringBuffer sb = new StringBuffer( m_strAsFile );;

      int nFunctPos = 0;

      // Get the offset to the beginning of the function decl

      for ( nFunctPos = nOffset - 1; nFunctPos > 0; --nFunctPos )
      {
        if ( sb.charAt( nFunctPos ) == 'p' )
        {
          break;
        }

      }

      nOffset += strPropType.length();

      int nPropPos  = sb.indexOf( "(", nOffset );
      int nParamPos = nPropPos;
      int nEndPos = 0;

      // Get the property funnction name
      String strPropName = sb.substring( nOffset, nPropPos ).trim();

      // get indexes to the getter/setter code
      nPropPos  = sb.indexOf( "{", ++nPropPos );
      nEndPos = findEndOfFunction( nPropPos, sb.toString() );


      // Create map entries if needed
      Map<String,String>mapCode = m_mapProperties.get( strPropName );

      if ( mapCode == null )
      {
        mapCode = new HashMap<String, String>();
        m_mapProperties.put( strPropName, mapCode );

      }

      if ( strPropType.indexOf( "get" ) >= 0 )
      {

        String strGetterCode = sb.substring( ++nPropPos, nEndPos ).trim();

        strGetterCode = VwExString.strip( strGetterCode, "\r\n\t" ).trim();

        strGetterCode = "{ " + strGetterCode + " }";
        mapCode.put( "get", strGetterCode );


      }
      else
      {
        StringBuffer sbSetter = new StringBuffer( sb.substring( nParamPos, ++nEndPos ) );

        // This is a setter, need to do param fixup if data type was specified
        boolean fRemove = false;
        int x = 1;

        for ( ; sbSetter.charAt( x ) != ')'; ++x )
        {
          if ( sbSetter.charAt( x ) == ':' )
          {
            fRemove = true;
          }

          if ( fRemove )
          {
            sbSetter.deleteCharAt( x );
            --x;
          }
        } // end for()

        int nCodePos = sbSetter.indexOf( "{" );

        sbSetter.delete( ++x, nCodePos );

        String strSetterCode = sbSetter.toString();
        strSetterCode = VwExString.strip( strSetterCode, "\r\n\t" ).trim();

        int nLBrace = strSetterCode.indexOf( '{' );
        int nRBrace = strSetterCode.indexOf( '}' );
        strSetterCode = strSetterCode.substring( 0, ++nLBrace  ) + " " + strSetterCode.substring( nLBrace, nRBrace ).trim() + " }";

        mapCode.put( "set", strSetterCode );

      }

      // Remove the entire /get set function decl from the source, will be added later as the Object.defineProperty
      sb.delete( nFunctPos, ++nEndPos );

      m_strAsFile = sb.toString();

      nOffset =  m_strAsFile.indexOf( strPropType, nFunctPos );

    } // end while()

  } // end doPropertyFixups()

  private boolean isInComment( int nOffset )
  {
    return isInComment( nOffset, m_strAsFile );

  }


  /**
   * Checks to see if the offset of this tokenn location is within a comment or a string
   *
   * @param nOffset The offset of the token within a string
   *
   * @param strSrc  The source of the token
   * @return  true if offset is within a comment or in a quoted string
   */
  private boolean isInComment( int nOffset, String strSrc )
  {

    // back up

    // first look at all comment block s

    int nStartNdx = strSrc.indexOf( "/*" );
    int nLen = strSrc.length();

    while( nStartNdx >= 0 )
    {
      int nEndNdx = strSrc.indexOf( "*/", nStartNdx );

      if ( nOffset > nStartNdx && nOffset < nEndNdx )
      {
         return true; // This os inside a comment block;
      }

      // next comment block

      nStartNdx = strSrc.indexOf( "/*", nEndNdx );
    }

    // Not in comment block if we get here, see if this is in a single line comment
    nStartNdx = nOffset;

    for ( ; nStartNdx >= 0; --nStartNdx )
    {
      char ch = strSrc.charAt( nStartNdx );

      if ( ch == '\n' )
      {
        break;  // Hit new line so not in single line comment
      }

      if ( ch == '/' && (nStartNdx > 0 && strSrc.charAt( nStartNdx - 1  ) == '/' )  )
      {
        return true;
      }

    } // end for()


    // check for string
    nStartNdx = nOffset;

    for ( ; nStartNdx >= 0; --nStartNdx )
    {
      char ch = strSrc.charAt( nStartNdx );

      if ( ch == '\n' ) // Strings don't normally span lines
      {
        break;
      }

      if ( ch == '"' || ch == '\'' )
      {
        int nBeginString = nStartNdx;

        // ok string found starting quote to the left, now search for end quote to the right

        for ( int nEndNdx = nOffset + 1; nEndNdx < nLen; ++nEndNdx )
        {
          ch = strSrc.charAt( nEndNdx );

          if ( ch == '"' || ch == '\'' )
          {
            if ( nOffset > nBeginString && nOffset < nEndNdx )
            {
              return true;
            }
          }


          if ( ch == '\n' )
          {
            return false;
          }

        }
      }
    }

    return false;

  }


  private void writeConvertedFile() throws Exception
  {
    String strInFileName = m_inFile.getName();

    String strFilePath = m_strOutDir + strInFileName;

    int nPos = strFilePath.lastIndexOf( '.' );

    // Create new name with the .js extension
    strFilePath = strFilePath.substring( 0, ++nPos  ) + "js";

    FileWriter writer = new FileWriter( strFilePath );

    writer.write( m_strAsFile );;

    writer.close();

  }


}
