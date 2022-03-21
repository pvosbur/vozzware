/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwClassGen.java

============================================================================================
 */

package com.vozzware.codegen;

import com.vozzware.util.VwDate;
import com.vozzware.util.VwDelimString;
import com.vozzware.util.VwExString;
import com.vozzware.util.VwFormat;
import com.vozzware.util.VwLogger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * General Java class/interface code generator This is a Java metaclass that
 * generates a Java class or interface based on the attributes defined by the
 * various methods. This allows for standard automation of a class code
 * generator which strictly follows good Java codeing standards.
 * <p/>
 * This is also a base class so specific types of class code generators may be
 * derived from this class ( although not necessary )
 *
 * @version 1.0
 * @since 1.1
 */
public class VwClassGen
{
  /**
   * Defines entry in class as default scope
   */
  public static final int DEFAULT = 0;

  /**
   * Defines entry in class as public scope
   */
  public static final int PUBLIC = 1;

  /**
   * Defines entry in class as private scope
   */
  public static final int PRIVATE = 2;

  /**
   * Defines entry in class as protected scope
   */
  public static final int PROTECTED = 3;

  /**
   * Object to be generated is a class
   */
  public static final int CLASS = 1;

  /**
   * Object to be generated is an interface
   */
  public static final int INTERFACE = 2;

  /**
   * Obejct is final
   */
  public static final int FINAL = 4;

  /**
   * Object is static
   */
  public static final int STATIC = 8;

  /**
   * Method or class is abstract
   */
  public static final int ABSTRACT = 16;

  /*
   * ====================================== Data member style bit flags
   * ======================================
   */

  /**
   * Generate a Set member fun for this variable
   */
  public static final int GENSET = 1;

  /**
   * Generate a Get member fun for this variable
   */
  public static final int GENGET = 2;

  /**
   * This variable is final
   */
  public static final int ISFINAL = 4;

  /**
   * This variable is volatile
   */
  public static final int ISVOL = 8;

  /**
   * This variable is transient
   */
  public static final int ISTRAN = 16;

  /**
   * // This variable is static
   */
  public static final int ISSTATIC = 32;

  /**
   * This variable is an array
   */
  public static final int ISARRAY = 64;

  /**
   * Variable is a literal, take as is
   */
  public static final int ISLIT = 128;

  /**
   * Variable is parameter to be passed on to the super method ( contains no
   * data type )
   */
  public static final int ISSUPER_PARAM = 256;

  /**
   * generate call to super class for dirty object detection
   */
  public static final int GEN_SMARTSET = 512;

  /**
   * add synchronized
   */
  public static final int IS_SYNCHRONIZED = 1024;


  /**
   * constant string the
   *
   * @param javadoc
   * string
   */
  private static String PARAMDOC;

  /**
   * constant string the
   *
   * @return javadoc string
   */
  private static String RETURNDOC;

  /**
   * constant string the
   *
   * @exception
   *
   */
  private static String EXCEPTDOC;

  private Map m_mapImports = new HashMap();

  /*
   * ====================================== Method style bit flags
   * ======================================
   */

  class ScopeDesc
  {
    short m_nSortOrder; // Sort order of scope (1,2 or 3 )

    String m_strScope; // Scope in string format I.E "private"

    int m_nScope; // Scope an an int

    List m_listDataList; // Associated scope linked List for data items

    List m_listMethodsList; // Associated scope linked List for member function

    // *** Constructor
    ScopeDesc( short nSortOrder, String strScope, int nScope, List listDataList, List listMethodsList )
    {
      m_nSortOrder = nSortOrder;
      m_strScope = strScope;
      m_nScope = nScope;
      m_listDataList = listDataList;
      m_listMethodsList = listMethodsList;

    }
  } // end ScopeDesc{}

  ScopeDesc[] m_aScopeTbl; // 3 dim array for each possible scope

  String m_strErrDesc; // Holds Error description

  private OutputStreamWriter m_out; // Output stream for source code output

  // *** Node structures for linked Lists

  // *** Java class data members

  class DataMbr
  {
    String m_strName; // Base name of member variable

    DataType m_eDataType; // Index to primitvate type -1 if userdefined

    String m_strUserDefType; // User defined type if not an XNDATATYPES

    int[] m_anDimList; // an array of dimension sizes

    String m_strComment; // Optional comment

    String m_strInitValue; // Optional initial value set at ructor time

    int m_nDataFlags; // Flags describing data attributes

    // *** Constructor
    DataMbr( String strName, DataType eDataType, String strUserDefType, int[] anDimList, String strComment,
             String strInitValue, int nDataFlags )
    {
      m_strName = strName;
      m_eDataType = eDataType;
      m_strUserDefType = strUserDefType;
      m_anDimList = anDimList;
      m_strComment = strComment;
      m_strInitValue = strInitValue;
      m_nDataFlags = nDataFlags;
    }
  }

  // *** Method List

  class Methods
  {
    String m_strName; // Method name

    String m_strMangledName; // Base name of method in param mangled form

    DataType m_eRetDataType; // Primitave Return method datatype or -1 for
    // userdef

    String m_strMethodDoc; // Method doc desc for javadoc

    String m_strReturnDoc; // Method return javadoc desc or null if n/a

    String m_strExceptionDoc; // Method exception javadoc desc or null if n/a

    String m_strCode; // Method code

    String m_strUserDefRetType; // User defined object ret type if not primitive
    // type

    String m_strExceptions; // Any exceptions this method throws

    int m_nFlags; // Flags describing method attributes

    int m_nRetDataFlags; // Flags describing return data type

    int m_nRetNbrDim; // Nbr of dimenions if return type is array

    MethodParams[] m_aParams; // Array of method paramaters or null if no
    // parameters

    // *** Constructor

    Methods( String strName, String strMangledName, DataType eRetDataType,
             String strMethodDoc, // Method doc desc for javadoc
             String strReturnDoc, String strCode, String strUserDefRetType, String strExceptions, int nFlags,
             int nRetDataFlags, int nRetNbrDim, MethodParams[] aParams, String strExceptionDoc )
    {

      m_strName = strName;
      m_strMangledName = strMangledName;
      m_eRetDataType = eRetDataType;
      m_strMethodDoc = strMethodDoc;
      m_strReturnDoc = strReturnDoc;
      m_strExceptionDoc = strExceptionDoc;
      m_strCode = strCode;
      m_strUserDefRetType = strUserDefRetType;
      m_strExceptions = strExceptions;
      m_nFlags = nFlags;
      m_nRetDataFlags = nRetDataFlags;
      m_nRetNbrDim = nRetNbrDim;
      m_aParams = aParams;

    }

  } // end class Methods

  // *** Method parameter Descripters

  public class MethodParams
  {
    public String m_strName; // Name of parameter

    public DataType m_eDataType; // Index to primitive Parameter datatype -1 =
    // userdef

    public String m_strUserDefType; // User defined type if not primitive type

    public String m_strComment; // Comment describing parameter

    public int m_nFlags; // Flags describing parameter attributes

    public int m_nNbrDim; // Nbr of dimensions if param type is an array
  } // end class MethodParams

  class Constructor
  {
    int m_nScope; // Scope of constructor public, protected

    String m_strMangledName; // Base name of method in param mangled form

    String m_strConstructorDoc; // Constructor doc desc for javadoc

    String m_strCode; // Constructor code

    String m_strExceptions; // Any exceptions this constructor throws

    int m_nFlags; // Constructor attributes

    MethodParams[] m_aParams; // Array of constructor paramaters or null if no
    // parameters

    MethodParams[] m_aSuperParams; // Array of super paramaters or null if no
    // parameters

    // *** Constructor

    Constructor( int nScope, String strMangledName, String strConstructorDoc, String strCode, String strExceptions,
                 int nFlags, MethodParams[] aParams, MethodParams[] aSuperParams )
    {
      m_nScope = nScope;
      m_strMangledName = strMangledName;
      m_strConstructorDoc = strConstructorDoc;
      m_strCode = strCode;
      m_strExceptions = strExceptions;
      m_nFlags = nFlags;
      m_aParams = aParams;
      m_aSuperParams = aSuperParams;

    }

  } // end class Methods

  private String m_strCompanyName; // Company name generated on header comments

  private String m_strClassName; // Class/interface name

  private String m_strBaseClassName; // Base class Name

  private String m_strBaseName; // Base file name ( without ext )

  private String m_strFileName; // Name of java output file

  private String m_strPackageName; // Name of package this class belongs to

  private String m_strBasePath; // Base Path to output directory of generated
  // file

  private VwDelimString m_dlmsImportList; // Import package List

  private VwDelimString m_dlmsInterfaceList; // List of interfacees to
  // implement

  private List m_listPrivDataMbr; // Linked List of private data mbrs

  private List m_listProtDataMbr; // Linked List of protected data mbrs

  private List m_listPubDataMbr; // Linked List of public data mbrs

  private List m_listDefDataMbr; // Linked List of default data mbrs

  private List m_listPrivMethods; // Linked List of private mbr functions

  private List m_listProtMethods; // Linked List of protected mbr functions

  private List m_listPubMethods; // Linked List of public mbr functions

  private List m_listDefMethods; // Linked List of default mbr functions

  private List m_listConstructors; // Linked List of constructors

  private VwDate m_dtCurDate; // Date/time of code generated (current date )

  private static String m_strIndent; // Contains nbr of requested indentation
  // spaces

  private String m_strArrayInit = ""; // Any array initialazation code

  private String m_strTrailSpaces; // Traing spaces after data name needed

  private boolean m_fInitDefValues; // If true init data members to NULL or zero
  // values with constructor

  private boolean m_fGenCloneCode = false;

  private boolean m_fGenEqualsCode = false;

  private boolean m_fNeedListCloneCode = false;
  private boolean m_fNeedMapCloneCode = false;

  private boolean m_fSkipGenIfExists = false; // if on and file exists, then don't generate the java file

  private StringBuffer m_sbCloneable;

  private StringBuffer m_sbEquals;

  private VwCodeOptions m_codeOpt; // Code generation options struct )

  private int m_nObjectType; // Object Type to be generated

  private int m_nObjectScopeType = PUBLIC;

  private boolean m_fNeedArrayEqualsCode = false;

  private boolean m_fNeedObjectEqualsCode = false;

  private boolean m_fNeedListEqualsCode = false;

  private boolean m_fNeedMapEqualsCode = false;

  /**
   * If true generate the clone method
   *
   * @param fGenCloneCode true to generate clone method code, false to skip
   */
  public void setGenCloneCode( boolean fGenCloneCode )
  {
    m_fGenCloneCode = fGenCloneCode;
  }

  /**
   * If true, generate the equals test method
   *
   * @param fGenEqualsCode true to generate equals method code, false to skip
   */
  public void setGenEqualsCode( boolean fGenEqualsCode )
  {
    m_fGenEqualsCode = fGenEqualsCode;
  }

  // *** Extracts file name from full path
  private String getFileName( String strFilePath )
  {
    return VwExString.getFileName( strFilePath, true );
  }

  /**
   * if true and the Java file exists, then skip re-generation of the file
   *
   * @param fSkipGenIfExists
   */
  public void setSkipJavaGenIfExists( boolean fSkipGenIfExists )
  {
    m_fSkipGenIfExists = fSkipGenIfExists;
  }


  /**
   * Sets the objects scope of an interface or class to public private or protected.
   * The default setting is public. use one of the public defined constants of this lass PUBLIC, PRIVATE, or
   * <br>PROTECTED to set this value
   *
   * @param nObjectScope
   */
  public void setObjectScope( int nObjectScope )
  {
    m_nObjectScopeType = nObjectScope;
  }

  /**
   * Generates the data members for the scope in CurScopeTbl
   *
   * @param curScopeTbl // ptr to the current scope, that code is beeign
   *                     generated for
   * @return true if successfull else false is returned
   */
  private boolean genDataMembers( ScopeDesc curScopeTbl )
  {
    DataMbr dataMbr; // Holds DataMbr class returned from List

    String str = ""; // Scratch String to build the datmember entry

    String strIndent = m_strIndent; // Indent another level here

    // *** Create iterator to loop thru linked List

    Iterator itr = curScopeTbl.m_listDataList.iterator();

    while ( itr.hasNext() )
    {
      dataMbr = (DataMbr) itr.next();
      int nNbrDim = ( dataMbr.m_anDimList == null ) ? 0 : dataMbr.m_anDimList.length;
      String strType = strIndent
          + buildDataType( curScopeTbl.m_nScope, dataMbr.m_nDataFlags, dataMbr.m_eDataType, nNbrDim,
                           dataMbr.m_strUserDefType, true );
      String strLenType = "";

      String strName = "";

      String strHungPre = "";

      // *** NOTE! we don't use prefixes on public static final constants

      if ( ( curScopeTbl.m_nScope != PUBLIC ) && ( ( dataMbr.m_nDataFlags & ISSTATIC ) != ISSTATIC )
          && ( ( dataMbr.m_nDataFlags & ISFINAL ) != ISFINAL ) && m_codeOpt.m_fUseMbrPrefix )
      {
        strName = m_codeOpt.m_strMbrPre;

        if ( m_codeOpt.m_fUseHungarian ) // Add hungarian prefix if specified
        {
          strHungPre = getHungPrefix( dataMbr.m_nDataFlags, dataMbr.m_eDataType );
          strName += strHungPre;

        }

      }

      if ( m_codeOpt.m_fUseHungarian && dataMbr.m_eDataType != DataType.USERDEF ) // Add
      // hungarian
      // prefix
      // if
      // specified
      {
        if ( strHungPre.length() == 0 )
        {
          strName += Character.toLowerCase( dataMbr.m_strName.charAt( 0 ) ); // Add
          // base
          // data
          // name

          if ( dataMbr.m_strName.length() > 1 )
          {
            strName += dataMbr.m_strName.substring( 1 );
          }

        }
        else
        {
          strName += Character.toUpperCase( dataMbr.m_strName.charAt( 0 ) ); // Add
          // base
          // data
          // name

          if ( dataMbr.m_strName.length() > 1 )
          {
            strName += dataMbr.m_strName.substring( 1 );
          }
        }
      }
      else
      {
        if ( dataMbr.m_eDataType != DataType.USERDEF )
        {
          if ( !m_codeOpt.m_fUseHungarian )
          {
            strName += Character.toLowerCase( dataMbr.m_strName.charAt( 0 ) );
            if ( dataMbr.m_strName.length() > 1 )
            {
              strName += dataMbr.m_strName.substring( 1 );
            }
          }
          else
          {
            strName += dataMbr.m_strName; // Add base data name
          }

        }
        else
        {

          strName += Character.toLowerCase( dataMbr.m_strName.charAt( 0 ) );

          if ( dataMbr.m_strName.length() > 1 )
          {
            strName += dataMbr.m_strName.substring( 1 );
          }
        }
      }

      if ( dataMbr.m_strInitValue != null )
      {
        strName += " = ";

        if ( dataMbr.m_strInitValue.startsWith( "new " ) )
        {
          strName += dataMbr.m_strInitValue; // Just copy supplied value here
        }
        else
        {
          if ( dataMbr.m_eDataType == DataType.STRING && !dataMbr.m_strInitValue.equals( "null" ) )
          {
            strName += "\"";
          }

          strName += dataMbr.m_strInitValue;

          if ( dataMbr.m_eDataType == DataType.STRING && !dataMbr.m_strInitValue.equals( "null" ) )
          {
            strName += "\"";
          }

        }

      }

      strName += ";";

      // *** format string for even spacing

      int nLen = 32;
      if ( strName.length() > 32 )
      {
        nLen = strName.length() + 1;
      }

      str = "\n" + strType + " ";
      str += VwFormat.left( strName, nLen, ' ' );

      if ( dataMbr.m_strComment != null ) // Add member data comment if specified
      {
        str += m_strTrailSpaces + "  // " + dataMbr.m_strComment;
      }

      try
      {
        m_out.write( str, 0, str.length() );

      } // end try

      catch ( IOException iox )
      {
        iox.printStackTrace();
        return false;
      }

    } // end while()

    return true;

  } // end genDataMembers()

  /**
   * Sets the base path output directory. If there is a package specified then
   * the output directory will be extended to incorporate the package
   *
   * @param strOutputDir the directory path of the output directory
   */
  public void setOutputDirectory( String strOutputDir )
  {
    m_strBasePath = strOutputDir;
  }

  /**
   * Builds a hungarian prefix using the user assign values in m_codeOpt.
   *
   * @param nDataFlags -
   *                  The data attribute flags
   * @param eDataType -
   *                  The Primiteve data type
   * @return a String containg the hungarian prefix for a data type
   */
  public String getHungPrefix( int nDataFlags, DataType eDataType )
  {
    String strHung = ""; // Result string to hold hungarian prefix

    if ( ( nDataFlags & ISARRAY ) == ISARRAY )
    {
      strHung += m_codeOpt.m_strArray;
    }

    switch ( eDataType )
    {

      case BYTE:

        strHung += m_codeOpt.m_strByte;
        break;

      case CHAR:

        strHung += m_codeOpt.m_strChar;
        break;

      case BOOLEAN:

        strHung += m_codeOpt.m_strBool;
        break;

      case SHORT:

        strHung += m_codeOpt.m_strShort;
        break;

      case INT:

        strHung += m_codeOpt.m_strInt;
        break;

      case LONG:

        strHung += m_codeOpt.m_strLong;
        break;

      case FLOAT:

        strHung += m_codeOpt.m_strFloat;
        break;

      case DOUBLE:

        strHung += m_codeOpt.m_strDbl;
        break;

      case STRING:

        strHung += m_codeOpt.m_strString;
        break;

      case LIST:
      case GT_LIST:
        strHung += m_codeOpt.m_strList;
        break;

      case MAP:
      case GT_MAP:

        strHung += m_codeOpt.m_strMap;
        break;

      case VW_DATE:
      case DATE:

        strHung += m_codeOpt.m_strDate;
        break;

      case TIMESTAMP:

        strHung += m_codeOpt.m_strTimeStamp;
        break;


    } // end switch( nDataType )

    return strHung;

  } // end of getHungPrefix()

  /**
   * Constructs a data type with all modifiers in an String
   *
   * @param nScope      The scope of the data variable I.E public, private, protected,
   *                    default
   * @param nDataFlags  The data type modifier flags
   * @param eDataType   Primitive data type or -1 for userdefinined
   * @param nNbrDim     The nbr of dimensions if data is an array
   * @param strUserType User type if nDataType is -1
   * @param fFormat     if true even space the entries elase just concat as we givem
   * @returns Result in an String I.E
   */
  public String buildDataType( int nScope, int nDataFlags, DataType eDataType, int nNbrDim, String strUserType,
                               boolean fFormat )
  {

    StringBuffer sbType = new StringBuffer( "" ); // Buffer to build data type;

    // *** If data type is a lieteral just return

    if ( ( nDataFlags & ISLIT ) == ISLIT )
    {
      return sbType.toString();
    }

    if ( nScope > 0 )
    {
      switch ( nScope )
      {
        case PUBLIC:

          sbType.append( "public " );
          break;

        case PRIVATE:

          sbType.append( "private " );
          break;

        case PROTECTED:

          sbType.append( "protected " );
          break;

      } // end switch()

    } // end if

    // *** First find any type modifiers

    if ( ( nDataFlags & ISSTATIC ) == ISSTATIC )
    {
      sbType.append( "static " );
    }

    if ( ( nDataFlags & ISFINAL ) == ISFINAL )
    {
      sbType.append( "final " );
    }

    if ( ( nDataFlags & IS_SYNCHRONIZED ) == IS_SYNCHRONIZED )
    {
      sbType.append( "synchronized " );
    }

    if ( ( nDataFlags & ISVOL ) == ISVOL )
    {
      sbType.append( "volatile " );
    }

    if ( ( nDataFlags & ISTRAN ) == ISTRAN )
    {
      sbType.append( "transient " );
    }

    // *** Gets the actual data type in string format

    switch ( eDataType )
    {

      case GT_LIST:

        sbType.append( "List<" ).append( strUserType ).append( ">" );
        break;

      case GT_MAP:

        sbType.append( "Map<" );
        int nPos = strUserType.indexOf( ',' );
        if ( nPos > 0 )
        {
          sbType.append( strUserType );
        }
        else
        {
          sbType.append( "String," ).append( strUserType ).append( ">" );
        }

        break;
      default:

        if ( eDataType == DataType.USERDEF ) // If type is user, get type from
        // strUsertype parameter
        {
          sbType.append( strUserType );
        }
        else
        // else get primitive type from the type array
        {
          sbType.append( eDataType.javaType() );
        }

    }

    if ( nNbrDim > 0 )
    {
      for ( int x = 0; x < nNbrDim; x++ )
      {
        sbType.append( "[]" );
      }
    }

    m_strTrailSpaces = "";

    if ( fFormat )
    {
      int nLen = 30;
      if ( sbType.length() > nLen )
      {
        nLen = sbType.length() + 1;
      }

      for ( int x = sbType.length(); x < nLen; x++ )
      {
        sbType.append( ' ' ); // Space out for long names
      }
      m_strTrailSpaces = " "; // Need a trailing space in above routine
    }

    return sbType.toString(); // Return final result

  } // end buildDataType()

  /**
   * Generates get and set methods for any data items with those requests
   */
  private boolean genGetSetMethods()
  {
    String strIndent = m_strIndent; // level of indentation

    try
    {
      // *** if we are generating in the public member scope, we must loop thru
      // the data types for any data members
      // *** that had the GENSET & GENGET bit flags set, a genrate inlines for
      // those members.

      boolean fNeedComment = true;

      String strComment = "\n\n" + strIndent
          + "// *** The following members set or get data from the class members *** ";

      DataMbr dataMbr; // Buffer to hold one DataMbr node

      for ( int x = 0; x < 4; x++ )
      {
        Iterator i = m_aScopeTbl[ x ].m_listDataList.iterator();

        while ( i.hasNext() )
        {
          dataMbr = (DataMbr) i.next();
          if ( ( dataMbr.m_nDataFlags & GENSET ) == GENSET || ( ( dataMbr.m_nDataFlags & GEN_SMARTSET ) == GEN_SMARTSET ) )
          {
            if ( fNeedComment )
            {
              fNeedComment = false;
              m_out.write( strComment, 0, strComment.length() ); // Write out
              // the string
            }

            genSetMbr( dataMbr ); // Generate set member function

          } // end if

          if ( ( dataMbr.m_nDataFlags & GENGET ) == GENGET )
          {
            if ( fNeedComment )
            {
              fNeedComment = false;
              m_out.write( strComment, 0, strComment.length() ); // Write out
              // the string
            }

            genGetMbr( dataMbr ); // Generate get member function

          } // end if

        } // end while()

      } // end for
    } // end try

    catch ( IOException e )
    {
      m_strErrDesc = e.toString();
      return false;

    }

    return true;

  } // end genGetSetMethods()

  /**
   * Generates the Java methods not created bu get & set data items
   *
   * @param curScopeTbl CurScopeTbl // ptr to the current scope, that code is beeign
   *                     generated for
   * @return true if successfull else false is returned
   */
  private boolean genMethods( ScopeDesc curScopeTbl )
  {
    Methods method; // Buffer to hold one Methods node

    String str; // Scratch String to build the datmember entry

    String strIndent = m_strIndent; // level of indentation

    // *** Create iterator to loop thru linked List

    Iterator i = curScopeTbl.m_listMethodsList.iterator();

    try
    {
      while ( i.hasNext() )
      {
        method = (Methods) i.next();
        str = "";
        writeDoc( method );

        // *** Build return Data type

        String strRetType = "\n"
            + strIndent
            + buildDataType( curScopeTbl.m_nScope, method.m_nFlags, method.m_eRetDataType, method.m_nRetNbrDim,
                             method.m_strUserDefRetType, false );

        str += strRetType + " " + buildMethodSigniture( method.m_strName, method.m_aParams );

        if ( method.m_strExceptions != null )
        {
          str += " throws " + method.m_strExceptions;
        }

        m_out.write( str, 0, str.length() ); // Write method declaration header

        if ( ( m_nObjectType & INTERFACE ) == INTERFACE )
        {
          str = ";";  // method ends here if this is an interace
        }
        else
        {
          if ( m_codeOpt.m_nOpenBraceStyle == VwCodeOptions.ALIGN_ON )
          {
            str = "{\n";
          }
          else
          {
            str = "\n" + strIndent + "{\n";
          }

          // *** Add in method code ***

          str += method.m_strCode;

          // *** Add in closing brace

          str += "\n" + strIndent + "} // End of " + method.m_strName + "()\n\n";

        }

        m_out.write( str, 0, str.length() ); // Write method declaration header

      } // end while()

    } // end try

    catch ( IOException e )
    {
      m_strErrDesc = e.toString();
      return false;

    }

    return true;

  } // end genMethods()

  /**
   * Builds a the complete method signiture
   *
   * @param strName -
   *                The The name of the method
   * @param aParams -
   *                The array of method parameters or null if no parameters
   * @return The complete method signiture in a String
   */
  private String buildMethodSigniture( String strName, MethodParams[] aParams )
  {
    String strFun;

    // *** If the param array is null, then the function contains no parameters
    // so build name with just
    // *** left & rigth parens

    if ( aParams == null )
    {
      strFun = strName;
      strFun += "()";

      return strFun;

    } // end if

    // *** build param List with each parameter entry found

    String strParamList = "";

    int nParamCount = 0; // parameter count
    int nParamNdx = 0;

    strFun = strName; // Copy base member function name
    strFun += "( "; // Add left open paren

    for ( int ndx = 0; ndx < aParams.length; ndx++ )
    {
      MethodParams curParam = aParams[ ndx ];

      if ( ++nParamCount > 1 ) // Add comma separator if 2cd or greater parameter
      {
        strParamList += ", ";
      }

      // *** Build the data type

      strParamList += buildDataType( -1, curParam.m_nFlags, curParam.m_eDataType, curParam.m_nNbrDim,
                                     curParam.m_strUserDefType, false );

      if ( ( curParam.m_nFlags & ISLIT ) != ISLIT )
      {
        strParamList += " ";
      }

      // *** Add param variable name

      String strHung = ""; // Hungarian for the member

      if ( m_codeOpt.m_fUseHungarian && ( curParam.m_nFlags & ISLIT ) != ISLIT ) // Add
      // hungarian
      // prefix
      // if
      // specified
      // for
      // function
      // prototype
      // name
      {
        strHung += getHungPrefix( curParam.m_nFlags, curParam.m_eDataType );
      }

      strParamList += strHung + curParam.m_strName; // Add param naame

    } // end for()

    strFun += strParamList + " )";

    return strFun;

  } // end buildMethodSigniture

  /**
   * Generate a set method for a data member
   *
   * @param dataMbr the DataMbr class describing the data item
   * @return true if no disk write errors occured
   */
  private boolean genSetMbr( DataMbr dataMbr )
  {

    String strIndent = m_strIndent;               // Spaces of indentation

    int nFlags = dataMbr.m_nDataFlags;            // Copy flags for function prototype differences

    // *** See if we have an array
    boolean fIsArray = ( ( nFlags & ISARRAY ) == ISARRAY );

    int nNbrDim = ( dataMbr.m_anDimList == null ) ? 0 : dataMbr.m_anDimList.length;

    // *** Can't set data members that are declared final

    if ( ( nFlags & ISFINAL ) == ISFINAL )
    {
      m_strErrDesc = "Cannot generate set method on a final data item";
      return false;

    }

    // *** The only flag we carry over from the data attributes is the static
    // flag.
    // *** Static data will get staic methods

    if ( ( nFlags & ISSTATIC ) == ISSTATIC )
    {
      nFlags = ISSTATIC; // Preserve static attribute
    }
    else
    {
      nFlags = 0; // clear any other attributes
    }

    // *** Get return type ***

    String strSet = "\n" + strIndent + buildDataType( PUBLIC, 0, DataType.VOID, 0, null, false );

    String strPropName = null;

    strPropName = dataMbr.m_strName.substring( 0, 1 ).toUpperCase();

    if ( dataMbr.m_strName.length() > 1 )
    {
      strPropName += dataMbr.m_strName.substring( 1 );
    }

    String strSmartSetCode = null;

    if ( ( dataMbr.m_nDataFlags & GEN_SMARTSET ) == GEN_SMARTSET )
    {
      strSmartSetCode = "\n" + strIndent + "  testDirty( \"" + dataMbr.m_strName + "\"";
    }

    strSet += " set" + strPropName + "( "; // Add base data name to set

    String strHung = ""; // Hungarian for the member

    if ( m_codeOpt.m_fUseHungarian ) // Add hungarian prefix if specified for
    // function prototype name
    {
      strHung += getHungPrefix( ( dataMbr.m_nDataFlags ), dataMbr.m_eDataType );
    }

    // *** If data type is an array we have to add the index(s) to set
    // *** as the first parameter(s)

    String strIndexHung = "";
    String strIndexName = "";

    if ( m_codeOpt.m_fUseHungarian ) // Add hungarian prefix if specified for
    // function prototype name
    {
      strIndexName = "Ndx";
      strIndexHung = getHungPrefix( 0, DataType.INT );
    }
    else
    {
      strIndexName = "ndx";
    }

    // *** Build the function signiture from datatype and name

    String strType = buildDataType( -1, 0, dataMbr.m_eDataType, 0, dataMbr.m_strUserDefType, false );

    if ( fIsArray )
    {
      for ( int x = 0; x < nNbrDim; x++ )
      {
        strType += "[]";
      }

    }

    String strName = null;
    String strTypeName = null;

    if ( dataMbr.m_eDataType == DataType.USERDEF && strHung.length() == 0 )
    {
      strName = "" + Character.toLowerCase( dataMbr.m_strName.charAt( 0 ) );
      if ( dataMbr.m_strName.length() > 1 )
      {
        strName += dataMbr.m_strName.substring( 1 );
      }

    }
    else
    {
      if ( Character.isLowerCase( dataMbr.m_strName.charAt( 0 ) ) && strHung.length() > 0 )
      {
        strName = strHung + dataMbr.m_strName.substring( 0, 1 ).toUpperCase();
        if ( dataMbr.m_strName.length() > 1 )
        {
          strName += dataMbr.m_strName.substring( 1 );
        }
      }
      else
      {
        if ( !m_codeOpt.m_fUseHungarian )
        {
          strName = "" + Character.toLowerCase( dataMbr.m_strName.charAt( 0 ) );

          if ( dataMbr.m_strName.length() > 1 )
          {
            strName += dataMbr.m_strName.substring( 1 );
          }
        }
        else
        {
          if ( strHung.length() == 0 )
          {
            strName = "" + Character.toLowerCase( dataMbr.m_strName.charAt( 0 ) );
            if ( dataMbr.m_strName.length() > 1 )
            {
              strName += dataMbr.m_strName.substring( 1 );
            }
          }
          else
          {
            strName = strHung + dataMbr.m_strName;
          }

        }
      }
    }

    if ( dataMbr.m_strUserDefType != null && dataMbr.m_eDataType == DataType.USERDEF )
    {

      if ( Character.isUpperCase( strType.charAt( 0 ) ) )
      {
        strTypeName = strType.substring( 0, 1 ).toLowerCase();

        int x = 1;
        for (; x < strType.length(); x++ )
        {
          if ( Character.isLowerCase( strType.charAt( x ) ) )
          {
            break;
          }

          strTypeName += Character.toLowerCase( strType.charAt( x ) );

        }

        if ( x < strType.length() )
        {
          strTypeName += strType.substring( x );
        }

      }
      else
      {
        strTypeName = strType;
      }
    }

    strSet += strType + " ";

    if ( dataMbr.m_eDataType == DataType.USERDEF && strHung.length() == 0 )
    {
      strSet += strName;
    }
    else
    {
      if ( strTypeName != null )
      {
        strSet += strTypeName;
      }
      else
      {
        strSet += strName;
      }
    }

    // ** Add the code to set the member

    String strMbrPre = "";

    if ( m_codeOpt.m_fUseMbrPrefix ) // Add member prefix if specified
    {
      strMbrPre = m_codeOpt.m_strMbrPre;
    }
    else
    {
      strMbrPre = "this.";
    }

    strSet += " )\n" + strIndent + "{ ";

    if ( strSmartSetCode != null )
    {
      strSet += "\n" + strIndent + "  ";
    }

    MethodParams aParams[] = allocParams( nNbrDim + 1 );
    int x = 0;

    String strParamName = null;

    if ( dataMbr.m_eDataType == DataType.USERDEF && strHung.length() == 0 )
    {
      strParamName = strName;
    }
    else
    {
      if ( strTypeName != null )
      {
        strParamName = strTypeName;
      }
      else
      {
        strParamName = strName;
      }
    }

    if ( strSmartSetCode != null )
    {
      strSmartSetCode += ", " + strParamName + " );\n" + strIndent + "  ";
      strSet += strSmartSetCode;
    }

    strSet += strMbrPre + strName + " = ";

    strSet += strParamName + ";";

    if ( m_fGenCloneCode )
    {

      if ( ( dataMbr.m_nDataFlags & VwClassGen.ISARRAY ) == VwClassGen.ISARRAY )
      {
        m_sbCloneable.append( "\n\n    if ( " ).append( strMbrPre ).append( strName ).append( "  != null )" );
        m_sbCloneable.append( "\n    {" );
        m_sbCloneable.append( "\n      classClone." ).append( strMbrPre ).append( strName ).append( " = new " );
        m_sbCloneable.append( dataMbr.m_eDataType.javaType() ).append( "[ " );
        m_sbCloneable.append( strMbrPre ).append( strName ).append( ".length ];" );
        m_sbCloneable.append( "\n      System.arraycopy(" ).append( strMbrPre ).append( strName );
        m_sbCloneable.append( ", 0, " ).append( "classClone." ).append( strMbrPre ).append( strName ).append( ", 0, " );
        m_sbCloneable.append( strMbrPre ).append( strName ).append( ".length );" );
        m_sbCloneable.append( "\n    } // end if\n" );

        if ( m_fGenEqualsCode )
        {
          m_fNeedArrayEqualsCode = true;

          m_sbEquals.append( "\n\n    if ( ! Arrays.equals( " ).append( strMbrPre ).append( strName ).append( ", " );
          m_sbEquals.append( "objToTest." ).append( strMbrPre ).append( strName ).append( " ) )" );
          m_sbEquals.append( "\n      return false; " );
        }
      }
      else
      if ( isPrimitiveType( dataMbr.m_eDataType ) )
      {
        m_sbCloneable.append( "\n    classClone." ).append( strMbrPre ).append( strName ).append( " = " );
        m_sbCloneable.append( strMbrPre ).append( strName ).append( ";" );

        if ( m_fGenEqualsCode )
        {
          if ( isPrimitiveObjectType( dataMbr.m_eDataType ) )
          {
            m_fNeedObjectEqualsCode = true;
            m_sbEquals.append( "\n\n    if ( ! doObjectEqualsTest( " ).append( strMbrPre ).append( strName ).append(
                ", " );
            m_sbEquals.append( "objToTest." ).append( strMbrPre ).append( strName ).append( " ) )" );
            m_sbEquals.append( "\n      return false; " );

          }
          else
          {
            m_sbEquals.append( "\n\n    if ( " ).append( strMbrPre ).append( strName ).append( " != " );
            m_sbEquals.append( "objToTest." ).append( strMbrPre ).append( strName ).append( " )" );
            m_sbEquals.append( "\n      return false; " );

          }

        } // if

      } // end if
      else
      if ( isListType( dataMbr.m_eDataType ) )
      {
        m_fNeedListCloneCode = true;

        m_sbCloneable.append( "\n\n    if ( " ).append( strMbrPre ).append( strName ).append( "  != null )" );
        m_sbCloneable.append( "\n      classClone." ).append( strMbrPre ).append( strName ).append( " = (" );
        m_sbCloneable.append( dataMbr.m_eDataType.javaType() );

        if ( dataMbr.m_strUserDefType != null )
        {
          m_sbCloneable.append( "<" ).append( dataMbr.m_strUserDefType );
          m_sbCloneable.append( ">" );
        }

        m_sbCloneable.append( ")cloneList( " ).append( strMbrPre ).append( strName ).append( " );" );

        if ( m_fGenEqualsCode )
        {
          m_fNeedListEqualsCode = true;
          m_sbEquals.append( "\n\n    if ( ! doListElementTest( " ).append( strMbrPre ).append( strName ).append( ", " );
          m_sbEquals.append( "objToTest." ).append( strMbrPre ).append( strName ).append( " ) )" );
          m_sbEquals.append( "\n      return false;" );

        }
      }
      else
      if ( isMapType( dataMbr.m_eDataType ) )
      {
        m_fNeedMapCloneCode = true;

        m_sbCloneable.append( "\n\n    if ( " ).append( strMbrPre ).append( strName ).append( "  != null )" );
        m_sbCloneable.append( "\n      classClone." ).append( strMbrPre ).append( strName ).append( " = (" );
        m_sbCloneable.append( dataMbr.m_eDataType.javaType() );

        m_sbCloneable.append( ")cloneMap( " ).append( strMbrPre ).append( strName ).append( " );" );

        if ( m_fGenEqualsCode )
        {
          m_fNeedMapEqualsCode = true;
          m_sbEquals.append( "\n\n    if ( ! doMapElementTest( " ).append( strMbrPre ).append( strName ).append( ", " );
          m_sbEquals.append( "objToTest." ).append( strMbrPre ).append( strName ).append( " ) )" );
          m_sbEquals.append( "\n      return false;" );

        }
      }
      else
      {
        m_sbCloneable.append( "\n\n    if ( " ).append( strMbrPre ).append( strName ).append( "  != null )" );
        m_sbCloneable.append( "\n      classClone." ).append( strMbrPre ).append( strName ).append( " = (" );
        m_sbCloneable.append( dataMbr.m_strUserDefType ).append( ")" ).append( strMbrPre ).append( strName )
            .append( ".clone();" );

        if ( m_fGenEqualsCode )
        {
          m_fNeedObjectEqualsCode = true;
          m_sbEquals.append( "\n\n    if ( ! doObjectEqualsTest( " ).append( strMbrPre ).append( strName ).append(
              ", " );
          m_sbEquals.append( "objToTest." ).append( strMbrPre ).append( strName ).append( " ) )" );
          m_sbEquals.append( "\n      return false; " );

        }

      }
    }
    // *** Add in the param desc. for thw data item to be assigned

    aParams[ x ].m_strName = dataMbr.m_strName;
    aParams[ x ].m_eDataType = dataMbr.m_eDataType;
    aParams[ x ].m_strUserDefType = dataMbr.m_strUserDefType;
    aParams[ x ].m_strComment = dataMbr.m_strComment;
    aParams[ x ].m_nNbrDim = 0;
    aParams[ x ].m_nFlags = 0;

    // *** Add closing brace
    if ( ( dataMbr.m_nDataFlags & GEN_SMARTSET ) == GEN_SMARTSET )
    {
      strSet += "\n";
    }

    strSet += "  }";

    // *** Create a method instance so we can also write javadoc for our
    // generated setXXXX method

    Methods method = new Methods( "set" + dataMbr.m_strName, null, DataType.VOID, "Sets the " + dataMbr.m_strName
        + " property", null, null, null, null, 0, 0, 0, aParams, null );

    try
    {
      writeDoc( method );
      m_out.write( strSet, 0, strSet.length() );
    }
    catch ( IOException iox )
    {
      m_strErrDesc = iox.toString();
      return false;
    }

    return true;

  } // end genSetMbr()

  private boolean isListType( DataType eDataType )
  {
    switch ( eDataType )
    {
      case LIST:
      case GT_LIST:
      case ARRAY_LIST:
      case LINKED_LIST:

        return true;

    }
    return false;
  }

  private boolean isMapType( DataType eDataType )
  {
    switch ( eDataType )
    {
      case MAP:
      case GT_MAP:
      case HASH_MAP:
      case TREE_MAP:

        return true;

    }
    return false;
  }


  /**
   * Detremins if type is primitive type
   *
   * @param eDataType
   * @return
   */
  public static boolean isPrimitiveType( DataType eDataType )
  {
    switch ( eDataType )
    {

      case BYTE:
      case BYTE_OBJ:
      case BOOLEAN:
      case BOOLEAN_OBJ:
      case CHAR:
      case CHAR_OBJ:
      case SHORT:
      case SHORT_OBJ:
      case INT:
      case INT_OBJ:
      case LONG:
      case LONG_OBJ:
      case FLOAT:
      case FLOAT_OBJ:
      case DOUBLE:
      case DOUBLE_OBJ:
      case STRING:

        return true;
    } // end switch()

    return false;

  } // end isPrimitiveType

  /**
   * Detremins if type is primitive type
   *
   * @param eDataType
   * @return
   */
  public static boolean isPrimitiveObjectType( DataType eDataType )
  {
    switch ( eDataType )
    {

      case BYTE_OBJ:
      case BOOLEAN_OBJ:
      case CHAR_OBJ:
      case SHORT_OBJ:
      case INT_OBJ:
      case LONG_OBJ:
      case FLOAT_OBJ:
      case DOUBLE_OBJ:
      case STRING:

        return true;
    } // end switch()

    return false;

  } // end isPrimitiveType

  /**
   * Generate a getXXXX method for a data member
   *
   * @param dataMbr the DataMbr class describing the data item
   * @return true if no disk write errors occured
   */
  private boolean genGetMbr( DataMbr dataMbr )
  {
    String strIndent = m_strIndent; // Spaces of indentation

    int nFlags = dataMbr.m_nDataFlags; // Copy flags for function protype
    // differences
    int nNbrDim = ( dataMbr.m_anDimList == null ) ? 0 : dataMbr.m_anDimList.length;

    boolean fIsArray = ( ( nFlags & ISARRAY ) == ISARRAY );

    // the only flag we carry over from the data attributes is the static flag.
    // Static
    // data will get staic methods

    if ( ( nFlags & ISSTATIC ) == ISSTATIC )
    {
      nFlags = ISSTATIC; // Preserve static attribute
    }
    else
    {
      nFlags = 0; // clear any other attributes
    }

    // *** Get return type ***

    String strGet = "\n" + strIndent
        + buildDataType( PUBLIC, nFlags, dataMbr.m_eDataType, 0, dataMbr.m_strUserDefType, false );

    if ( fIsArray )
    {
      for ( int x = 0; x < nNbrDim; x++ )
      {
        strGet += "[]";
      }
    }

    String strPropName = null;

    strPropName = dataMbr.m_strName.substring( 0, 1 ).toUpperCase();

    if ( dataMbr.m_strName.length() > 1 )
    {
      strPropName += dataMbr.m_strName.substring( 1 );
    }

    strGet += " get" + strPropName + "("; // Add base data name to set

    // *** If data type is an array we have to add the index(s) to set
    // *** as the first parameter(s)

    String strIndexHung = "";
    String strIndexName = "";

    if ( m_codeOpt.m_fUseHungarian ) // Add hungarian prefix if specified for
    // function prototype name
    {
      strIndexName = "Ndx";
      strIndexHung = getHungPrefix( 0, DataType.INT );
    }
    else
    {
      strIndexName = "ndx";
    }

    strGet += ")";

    String strHung = ""; // Hungarian for the member

    if ( m_codeOpt.m_fUseHungarian ) // Add hungarian prefix if specified for
    // function prototype name
    {
      strHung += getHungPrefix( dataMbr.m_nDataFlags, dataMbr.m_eDataType );
    }

    // ** Add the code to set the member

    String strMbrPre = "";

    if ( m_codeOpt.m_fUseMbrPrefix ) // Add member prefix if specified
    {
      strMbrPre = m_codeOpt.m_strMbrPre;
    }

    String strName = null;

    if ( dataMbr.m_eDataType == DataType.USERDEF && strHung.length() == 0 )
    {
      strName = "" + Character.toLowerCase( dataMbr.m_strName.charAt( 0 ) );
      if ( dataMbr.m_strName.length() > 1 )
      {
        strName += dataMbr.m_strName.substring( 1 );
      }
    }
    else
    {
      if ( Character.isLowerCase( dataMbr.m_strName.charAt( 0 ) ) && strHung.length() > 0 )
      {
        strName = strHung + dataMbr.m_strName.substring( 0, 1 ).toUpperCase();
        if ( dataMbr.m_strName.length() > 1 )
        {
          strName += dataMbr.m_strName.substring( 1 );
        }
      }
      else
      {
        if ( !m_codeOpt.m_fUseHungarian )
        {
          strName = "" + Character.toLowerCase( dataMbr.m_strName.charAt( 0 ) );

          if ( dataMbr.m_strName.length() > 1 )
          {
            strName += dataMbr.m_strName.substring( 1 );
          }
        }
        else
        {
          if ( strHung.length() > 0 )
          {
            strName = strHung + dataMbr.m_strName;
          }
          else
          {
            strName = strHung + dataMbr.m_strName.substring( 0, 1 ).toLowerCase();
            if ( dataMbr.m_strName.length() > 1 )
            {
              strName += dataMbr.m_strName.substring( 1 );
            }

          }

        }
      }
    }
    strGet += "\n" + strIndent + "{ return " + strMbrPre + strName;

    strGet += "; }";

    // *** Create a method instance so we can also write javadoc for our
    // generated setXXXX method

    Methods method = new Methods( "get" + strPropName, null, dataMbr.m_eDataType, "Gets " + dataMbr.m_strName
        + " property", "The " + dataMbr.m_strName + " property", null, dataMbr.m_strUserDefType, null, 0, 0, 0, null,
                                  null );

    try
    {
      writeDoc( method );

      m_out.write( strGet, 0, strGet.length() );
    }
    catch ( IOException iox )
    {
      m_strErrDesc = iox.toString();
      return false;
    }

    return true;

  } // end genGetMbr()

  /**
   * Writes out the package import List
   */
  private void writeImports()
  {
    if ( m_dlmsImportList != null )
    {
      String strPackageName;
      try
      {
        while ( ( strPackageName = m_dlmsImportList.getNext() ) != null )
        {
          m_out.write( strPackageName, 0, strPackageName.length() );
          m_out.write( "\n", 0, 1 );
        }

        m_out.write( "\n", 0, 1 );
      }
      catch ( IOException iox )
      {
        System.out.println( iox.toString() );
        System.exit( 1 );
      }
    } // end if

  } // end writeImports

  /**
   * Makes a an import and comment string"
   *
   * @param strPackageName -
   *                       The name of the package to import
   * @param strComment     -
   *                       Any additional comment about the import
   * @return String containing the full import statement
   */
  private String makeImport( String strPackageName, String strComment )
  {
    String strImport = "import " + strPackageName + ";";
    if ( strComment != null )
    {
      strImport += "     // " + strComment;
    }

    return strImport;
  }

  /**
   * Builds javadoc documentation block for a method
   *
   * @param method -
   *               The method class describing the method and its parameters
   */
  private void writeDoc( Methods method ) throws IOException
  {
    StringBuffer sbDoc = new StringBuffer( "\n\n" );
    sbDoc.append( m_strIndent ).append( "/**\n" ).append( m_strIndent ).append( " * " );

    if ( method.m_strMethodDoc != null )
    {
      sbDoc.append( method.m_strMethodDoc ).append( "\n" );
    }

    if ( method.m_aParams != null )
    {
      MethodParams[] aParams = method.m_aParams;

      // *** Add the @param doc for each parameter in the array

      for ( int x = 0; x < aParams.length; x++ )
      {
        if ( aParams[ x ].m_strComment == null )
        {
          continue;
        }

        sbDoc.append( m_strIndent ).append( " * " ).append( PARAMDOC );
        sbDoc.append( getHungPrefix( aParams[ x ].m_nFlags, aParams[ x ].m_eDataType ) );
        sbDoc.append( aParams[ x ].m_strName );

        sbDoc.append( " - " ).append( aParams[ x ].m_strComment );
        sbDoc.append( "\n" );
      }


    } // end if

    if ( method.m_strReturnDoc != null )
    {
      sbDoc.append( m_strIndent ).append( " * " ).append( RETURNDOC ).append( " " ).append( method.m_strReturnDoc )
          .append( "\n" );
    }

    if ( method.m_strExceptionDoc != null )
    {
      VwDelimString dlms = new VwDelimString( method.m_strExceptionDoc );
      VwDelimString dlmsExcept = new VwDelimString( method.m_strExceptions );
      String[] astrExceptions = dlmsExcept.toStringArray();
      String[] astrReasons = dlms.toStringArray();

      for ( int x = 0; x < astrExceptions.length; x++ )
      {

        sbDoc.append( m_strIndent ).append( " * " ).append( "@throws " ).append( astrExceptions[ x ] ).append( " " ).append( astrReasons[ x ] )
            .append( "\n" );
      }

    }

    if ( !sbDoc.toString().endsWith( "\n" ) )
    {
      sbDoc.append( "\n" );
    }

    sbDoc.append( m_strIndent ).append( " */" );

    m_out.write( sbDoc.toString(), 0, sbDoc.length() );

    return;

  } // end writeDoc()

  /**
   * Generates constructors and member initialazation code for scope specified
   *
   * @param -- The current scope beeing generated
   */
  private void genConstructors( int nScope )
  {

    String str = ""; // Scratch String to build code
    Constructor construct; // Constructor class instance

    int nInitCnt = 0;

    // *** Create iterator to loop thru constructor List

    Iterator itr = m_listConstructors.iterator();

    try
    {
      while ( itr.hasNext() )
      {
        construct = (Constructor) itr.next();
        if ( construct.m_nScope != nScope )
        {
          continue;
        }

        str = "\n" + m_strIndent;

        switch ( nScope )
        {

          case VwClassGen.PUBLIC:

            str += "public ";
            break;

          case VwClassGen.PROTECTED:

            str += "protected ";
            break;

          case VwClassGen.PRIVATE:

            str += "private ";
            break;

        } // end switch()

        // *** Create a method instance so we can also write javadoc for our
        // generated constructor

        Methods method = new Methods( m_strClassName, null, null, "Constructs the " + m_strClassName + " instance",
                                      null, null, null, null, 0, 0, 0, construct.m_aParams, null );

        writeDoc( method );

        str += buildMethodSigniture( m_strClassName, construct.m_aParams );

        if ( construct.m_strExceptions != null )
        {
          str += " throws " + construct.m_strExceptions;
        }

        m_out.write( str, 0, str.length() ); // Write method declaration header

        if ( m_codeOpt.m_nOpenBraceStyle == VwCodeOptions.ALIGN_ON )
        {
          str = "{\n";
        }
        else
        {
          str = "\n" + m_strIndent + "{\n";
        }

        if ( m_strBaseClassName != null && construct.m_aSuperParams != null )
        {
          str += m_strIndent + m_strIndent + buildMethodSigniture( "super", construct.m_aSuperParams ) + ";\n";
        }

        // *** Add in constructor code ***

        if ( construct.m_strCode != null )
        {
          if ( construct.m_strCode.equals( "Vw:useParams" ) )
          {
            str += genConstructorInitializerCode( construct );
          }
          else
          {
            str += construct.m_strCode;
          }

        }

        // *** Add in closing brace

        str += "\n\n" + m_strIndent + "} // End of " + m_strClassName + "()\n";

        m_out.write( str, 0, str.length() ); // Write method declaration header

      } // end while()

    } // end try

    catch ( IOException iox )
    {
      System.out.println( iox.toString() );
    }

    return;

  } // end genConstructor()

  /**
   * Generate constructor initializer code from the constructor param list
   */
  private String genConstructorInitializerCode( Constructor constructor )
  {

    String strCode = "";

    for ( int x = 0; x < constructor.m_aParams.length; x++ )
    {
      strCode += "\r\n" + m_strIndent + m_strIndent;

      MethodParams param = constructor.m_aParams[ x ];

      if ( m_codeOpt.m_fUseMbrPrefix )
      {
        strCode += m_codeOpt.m_strMbrPre;
      }

      String strDataName = null;

      if ( m_codeOpt.m_fUseHungarian )
      {
        strDataName = getHungPrefix( param.m_nFlags, param.m_eDataType ) + param.m_strName;
      }
      else
      {
        strDataName = param.m_strName;
      }

      strCode += strDataName + " = " + strDataName + ";";

    } // end for()

    return strCode;

  } // end genConstructorInitializerCode()

  /**
   * Constructs the code generator instance
   *
   * @param codeOpt           The VwCodeOptions options class for determing code output
   *                          characteristics
   * @param strClassName      The Class/Interface name to be generated
   * @param strBaseClassName  The Base class/interface name or null if no base class
   * @param strPackageName    The name of the package this class or interface belongs to or null
   *                          if N/A
   * @param dlmsInterfaceList A List of iterfaces this class will implement or null if N/A
   * @param nObjectType       One of the class constants that provides scope to the class i.e
   *                          PUBLIC, ABSTRACT, PRIVATE ...
   */
  public VwClassGen( VwCodeOptions codeOpt, String strClassName, String strBaseClassName, String strPackageName,
                     VwDelimString dlmsInterfaceList, int nObjectType ) throws IOException
  {

    m_codeOpt = codeOpt;
    m_strClassName = strClassName;
    m_strFileName = m_strClassName + ".java";
    m_strBaseClassName = strBaseClassName;
    m_strBaseName = strClassName;
    m_nObjectType = nObjectType;
    m_strPackageName = strPackageName;

    m_fInitDefValues = true;

    m_listPrivDataMbr = new LinkedList();
    m_listProtDataMbr = new LinkedList();
    m_listPubDataMbr = new LinkedList();
    m_listDefDataMbr = new LinkedList();

    m_listPrivMethods = new LinkedList();
    m_listProtMethods = new LinkedList();
    m_listPubMethods = new LinkedList();
    m_listDefMethods = new LinkedList();

    m_listConstructors = new LinkedList();

    m_strIndent = VwFormat.left( " ", codeOpt.m_nIndentation, ' ' );

    PARAMDOC = "\n" + m_strIndent + " * @param ";
    RETURNDOC = "\n" + m_strIndent + " * @return ";
    EXCEPTDOC = "\n" + m_strIndent + " * @exception ";

    m_dlmsImportList = new VwDelimString();

    if ( dlmsInterfaceList != null )
    {
      m_dlmsInterfaceList = new VwDelimString( dlmsInterfaceList );
    }

    m_strCompanyName = m_codeOpt.m_strName;

    // *** Init scope table

    m_aScopeTbl = new ScopeDesc[ 4 ];

    m_aScopeTbl[ 0 ] = new ScopeDesc( m_codeOpt.m_sPrivOrder, "private", PRIVATE, m_listPrivDataMbr, m_listPrivMethods );

    m_aScopeTbl[ 1 ] = new ScopeDesc( m_codeOpt.m_sProtOrder, "protected", PROTECTED, m_listProtDataMbr,
                                      m_listProtMethods );

    m_aScopeTbl[ 2 ] = new ScopeDesc( m_codeOpt.m_sPubOrder, "public", PUBLIC, m_listPubDataMbr, m_listPubMethods );

    m_aScopeTbl[ 3 ] = new ScopeDesc( m_codeOpt.m_sDefOrder, "", DEFAULT, m_listDefDataMbr, m_listDefMethods );

  } // end VwClassGen()

  /**
   * Sets the init data members to default values flag
   *
   * @param fInit true uses defualt values false leaves data members uninitalized
   */
  public void initDefValues( boolean fInit )
  {
    m_fInitDefValues = fInit;
  }

  /**
   * Builds DataMbr class and adds it to linked List depending on the access
   * scope.
   *
   * @param strDataName    -
   *                       The Name of data member
   * @param eDataType      -
   *                       The native data type constant or -1 for class or interface
   * @param strComment     -
   *                       Optional Comment for data member or null
   * @param nScope         -
   *                       Scope constant PUBLIC, PRIVATE or PROTECTED
   * @param nDataFlags     -
   *                       Data attribute flags
   * @param strInitValue   -
   *                       Constructor initial value string or NULL for object default
   *                       If data item is an array the nbr of dimensions to be allocated
   * @param anDimList     -
   *                       An array of dimension sizes
   * @param strUserDefType -
   *                       String containing user defined data type if eDataType = USER
   * @ return true if successfully added to List else FALSE is returned.
   */
  public boolean addDataMbr( String strDataName, DataType eDataType,
                             String strComment, int nScope, int nDataFlags,
                             String strInitValue, int[] anDimList, String strUserDefType )
  {

    DataMbr mbr = new DataMbr( strDataName, eDataType, strUserDefType, anDimList,
                               strComment, strInitValue, nDataFlags );   // Allocate
    // DataMbr
    // instance


    if ( m_fGenEqualsCode && ( nDataFlags & VwClassGen.ISARRAY ) == VwClassGen.ISARRAY )
    {
      addImport( "java.util.Arrays", null );
    }

    if ( isListType( eDataType ) || isMapType( eDataType ))
    {
      if ( m_fGenCloneCode )
      {
        addImport( "java.lang.reflect.Method", null );
      }

      if ( m_fGenEqualsCode )
      {
        addImport( "java.util.Iterator", null );
      }

    }
    switch ( eDataType )
    {
      case VW_DATE:
        addImport( "com.vozzware.util.VwDate", null );
        break;

      case DATE:
        addImport( "java.util.Date", null );
        break;

      case LIST:
        addImport( "java.util.List", null );
        break;

      case LINKED_LIST:
        addImport( "java.util.LinkedList", null );
        break;

      case ARRAY_LIST:
        addImport( "java.util.ArrayList", null );
        break;

      case MAP:
        addImport( "java.util.Map", null );
        break;

      case HASH_MAP:
        addImport( "java.util.HashMap", null );
        break;

      case TREE_MAP:
        addImport( "java.util.TreeMap", null );
        break;

    }

    boolean fRet = true;

    // *** Add DataMbr class to the linked List for the access type

    switch ( nScope )
    {

      case PUBLIC:

        m_listPubDataMbr.add( mbr );  // Public data member

        break;


      case PROTECTED:

        m_listProtDataMbr.add( mbr );     // Protected data member

        break;


      case PRIVATE:

        m_listPrivDataMbr.add( mbr );     // Private data member

        break;


      case DEFAULT:

        m_listDefDataMbr.add( mbr );     // Default data member

        break;

      default:

        m_strErrDesc = "Invalid Scope Type";          // Invalid scope type

        fRet = false;

    } // end switch( eScope )

    return fRet;


  } // end addDataMbr()

  /**
   * Adds a java method to the List ( base name an return type description )
   *
   * @param strMethodName  -
   *                      The name of the method to be added
   * @param nScope        -
   *                      Class scope of method PUBLIC, PRIVATE ...
   * @param eRetDataType  -
   *                      The return data type if its is a primitive type
   * @param strCode       -
   *                      The assocaited code for this member
   * @param strMethodDoc  -
   *                      The javadoc method desciption
   * @param strReturnDoc  -
   *                      The javadoc method return type description
   * @param strExceptions -
   *                      Any exceptions this method throws       ß
   * @param nFlags        -
   *                      Method attribute flags
   * @param nRetDataFlags -
   *                      Addition attributes for return data type
   * @param nRetNbrDim    -
   *                      Nbr of dimension of the array if return data type is an array
   *                      &param strUserDefType - Object name if type is not a primitive one
   * @param aMethodParams -
   *                      Array of method parameters ( if method has params or null )
   */
  public boolean addMethod( String strMethodName, int nScope, DataType eRetDataType, String strCode,
                            String strMethodDoc, String strReturnDoc, String strExceptions, int nFlags, int nRetDataFlags, int nRetNbrDim,
                            String strUserDefRetType, MethodParams[] aMethodParams, String strExceptionDoc )
  {
    Methods method = new Methods( strMethodName, getMangledName( strMethodName, aMethodParams ), eRetDataType,
                                  strMethodDoc, strReturnDoc, strCode, strUserDefRetType, strExceptions, nFlags, nRetDataFlags, nRetNbrDim,
                                  aMethodParams, strExceptionDoc );

    boolean fRet = true;

    // *** Add DataMbr class to the linked List for the access type

    switch ( nScope )
    {

      case PUBLIC:

        m_listPubMethods.add( method ); // Public data member

        break;

      case PROTECTED:

        m_listProtMethods.add( method ); // Protected data member

        break;

      case PRIVATE:

        m_listPrivMethods.add( method ); // Private data member

        break;

      case DEFAULT:

        m_listDefMethods.add( method ); // Default data member

        break;

      default:

        m_strErrDesc = "Invalid Scope Type"; // Invalid scope type

        fRet = false;

    } // end switch( eScope )

    return fRet;

  } // end addMethod()

  /**
   * Adds a java constructor to the constructor List
   *
   * @param nScope             -
   *                           Class scope of method PUBLIC, PRIVATE ...
   * @param strCode            -
   *                           The assocaited code for this member
   * @param strConstructDoc    -
   *                           The javadoc constructor desciption
   * @param strExceptions      -
   *                           Any exceptions this method throws
   * @param nFlags             -
   *                           Constructor attribute flags
   * @param aConstructorParams -
   *                           Array of constructor parameters ( or null if no parameters )
   * @param aSuperParams       -
   *                           Array of super parameters ( or null if no parameters )
   */
  public void addConstructor( int nScope, String strCode, String strConstructDoc, String strExceptions, int nFlags,
                              MethodParams[] aConstructorParams, MethodParams[] aSuperParams )
  {
    Constructor construct = new Constructor( nScope, getMangledName( m_strClassName, aConstructorParams ),
                                             strConstructDoc, strCode, strExceptions, nFlags, aConstructorParams, aSuperParams );

    m_listConstructors.add( construct ); // Public data member

  } // end addConstructor

  /**
   * Bulds the method mangled name from base name and param types
   *
   * @param strBaseName   -
   *                      The base method name
   * @param aMethodParams -
   *                      The array of MethodParams classes
   * @return a string containing the mangled method name
   */
  public String getMangledName( String strBaseName, MethodParams[] aMethodParams )
  {
    VwDelimString dlmsList = new VwDelimString();

    if ( aMethodParams == null )
    {
      return strBaseName;
    }

    for ( int x = 0; x < aMethodParams.length; x++ )
    {
      if ( aMethodParams[ x ].m_strUserDefType != null )
      {
        dlmsList.add( aMethodParams[ x ].m_strUserDefType );
      }
      else
      {
        dlmsList.add( aMethodParams[ x ].m_eDataType.mangledName() );
      }

    }

    return strBaseName + dlmsList;

  } // end getMangledName()

  /**
   * Adds an interface name to implement
   *
   * @param strInterfaceName The name of the interface to add
   */
  public void addInterface( String strInterfaceName )
  {
    if ( m_dlmsInterfaceList == null )
    {
      m_dlmsInterfaceList = new VwDelimString();
    }

    m_dlmsInterfaceList.add( strInterfaceName );

  } // end

  /**
   * Adds a package name to the import List
   *
   * @param strPackageName -
   *                       The name of the package to be imported
   * @param strComment     -
   *                       Option comment
   */
  public void addImport( String strPackageName, String strComment )
  {
    if ( m_mapImports.containsKey( strPackageName ) )
    {
      return;
    }

    m_mapImports.put( strPackageName, null );

    m_dlmsImportList.add( makeImport( strPackageName, strComment ) );

  }

  public void setSuperClassName( String strSuperClassName )
  {
    m_strBaseClassName = strSuperClassName;
  }

  /**
   * Allocates the MethodParams array and returns a reference
   *
   * @param nNbrParams -
   *                   The Nbr of parameters in the method
   * @return - a ref to the allocated array
   */
  public final MethodParams[] allocParams( int nNbrParams )
  {
    MethodParams[] aParams = new MethodParams[ nNbrParams ];
    for ( int x = 0; x < nNbrParams; x++ )
    {
      aParams[ x ] = new MethodParams();
    }

    return aParams;

  }

  /**
   * Generates the complete Java class/interface source file
   *
   * @return true if all successfull
   */
  public boolean generate() throws Exception
  {
    return generate( null );
  }

  /**
   * Generates the complete Java class/interface source file
   *
   * @return true if all successfull
   */
  public boolean generate( VwLogger logger ) throws Exception
  {

    if ( m_fGenCloneCode )
    {
      m_sbCloneable = new StringBuffer( "\n\n  /**" );
      m_sbCloneable.append( "\n   * Clones this object" );
      m_sbCloneable.append( "\n   *" );
      m_sbCloneable.append( "\n   */" );
      m_sbCloneable.append( "\n  public Object clone()" );
      m_sbCloneable.append( "\n  {\n    " ).append( m_strClassName ).append( " classClone = new " ).append(
          m_strClassName ).append( "();\n    " );
    }

    if ( m_fGenEqualsCode )
    {

      m_sbEquals = new StringBuffer( "\n\n  /**" );
      m_sbEquals.append( "\n   * Performs deep equal test on this object" );
      m_sbEquals.append( "\n   *" );
      m_sbEquals.append( "\n   * @param objTest The object to compare this object to" );
      m_sbEquals.append( "\n   *" );
      m_sbEquals.append( "\n   * @return if the two objects are equal, false otherwise" );
      m_sbEquals.append( "\n   *" );
      m_sbEquals.append( "\n   */" );
      m_sbEquals.append( "\n  public boolean equals( Object objTest )" );
      m_sbEquals.append( "\n  {\n" );
      m_sbEquals.append( "\n    if ( objTest == null )" );
      m_sbEquals.append( "\n      return false;\n" );
      m_sbEquals.append( "\n    if ( this.getClass() != objTest.getClass() )" );
      m_sbEquals.append( "\n      return false;\n" );

      m_sbEquals.append( "\n    " ).append( m_strClassName ).append( " objToTest = (" ).append( m_strClassName )
          .append( ")objTest;" );

    }
    // *** Open output stream

    if ( m_strBasePath == null )
    {
      File dir = new File( "." );
      m_strBasePath = dir.getCanonicalPath();
    }

    String strFilePath = m_strBasePath;

    if ( m_strPackageName != null )
    {

      if ( !strFilePath.endsWith( File.separator ) )
      {
        strFilePath += File.separator;
      }

      strFilePath += m_strPackageName.replace( '.', File.separatorChar );

    }

    File file = new File( strFilePath );
    if ( !file.exists() )
    {
      file.mkdirs();
    }

    if ( strFilePath.charAt( strFilePath.length() - 1 ) != File.separatorChar )
    {
      strFilePath += File.separatorChar;
    }

    strFilePath += m_strFileName;

    if ( m_fSkipGenIfExists )
    {
      File fileJava = new File( strFilePath );
      if ( fileJava.exists() )
      {
        logger.info( "Skipping existing file '" + strFilePath + "' as requested" );
        return false;
      }
    }
    m_out = new OutputStreamWriter( new FileOutputStream( strFilePath ) );

    VwDate curDate = new VwDate();

    // *** Build standard header file comment block

    int nWidth = m_codeOpt.m_sScreenWidth - 8;

    StringBuffer str = new StringBuffer( "/*\n" );
    str.append( VwFormat.left( "=", nWidth, '=' ) ).append( "\n\n" );

    str.append( VwFormat.center( "V o z z W o r k s   C o d e   G e n e r a t o r", nWidth, ' ' ) );

    if ( m_codeOpt.m_strCopyright != null )
    {

      VwDelimString dlms = new VwDelimString( "\n", m_codeOpt.m_strCopyright );
      String strLine = null;

      while ( ( strLine = dlms.getNext() ) != null )
      {
        str.append( "\n\n" ).append( VwFormat.center( strLine, nWidth, ' ' ) );

      } // end while()

    } // end if

    str.append( "\n\n    Source File Name: " ).append( m_strFileName );

    if ( m_codeOpt.m_strAuthor != null )
    {
      str.append( "\n\n    Author:           " ).append( m_codeOpt.m_strAuthor );
    }

    str.append( "\n\n    Date Generated:   " ).append( curDate.format( VwDate.USADATE ) );
    str.append( "\n\n    Time Generated:   " ).append( curDate.format( "%H:%M:%S" ) ).append( "\n\n" );

    str.append( VwFormat.left( "=", nWidth, '=' ) ).append( "\n*/\n\n" );

    // *** If this is part of a package, write the package declaration

    if ( m_strPackageName != null )
    {
      str.append( "package " ).append( m_strPackageName ).append( ";\r\n\r\n" );
    }

    m_out.write( str.toString(), 0, str.length() );

    // *** Add any import statements

    writeImports();

    // *** Begin forming class definition code

    str.setLength( 0 );

    str.append( "\n" );

    if ( m_nObjectScopeType == PUBLIC )
    {
      str.append( "public " );
    }

    if ( m_nObjectScopeType == PROTECTED )
    {
      str.append( "protected " );
    }

    if ( m_nObjectScopeType == PRIVATE )
    {
      str.append( "private " );
    }

    if ( ( m_nObjectType & ABSTRACT ) == ABSTRACT )
    {
      str.append( "abstract " );
    }

    if ( ( m_nObjectType & CLASS ) == CLASS )
    {
      str.append( "class " );
    }
    else
    {
      str.append( "interface " );
    }

    str.append( m_strClassName );

    if ( m_strBaseClassName != null )
    {
      str.append( " extends " ).append( m_strBaseClassName );
    }

    if ( m_dlmsInterfaceList != null )
    {
      str.append( " implements " );
      String strInterface;
      int nInterfaceCount = 0;
      while ( ( strInterface = m_dlmsInterfaceList.getNext() ) != null )
      {
        if ( str.length() + strInterface.length() > nWidth )
        {
          m_out.write( str.toString(), 0, str.length() );
          str.setLength( 0 );
          str.append( m_strIndent );
        }

        if ( ++nInterfaceCount > 1 )
        {
          str.append( ", " );
        }

        str.append( strInterface );

      } // end while()

      // Add interface line

    } // end if

    str.append( "\n{\n" );

    m_out.write( str.toString(), 0, str.length() );

    str.setLength( 0 );

    ScopeDesc curScopeDesc = null;

    // *** Outer loop here processes the private, protected, and public class
    // attributes
    // *** in the order specified by user

    for ( int nScope = 1; nScope < 5; nScope++ )
    {
      for ( int ndx = 0; ndx < 4; ndx++ )
      {
        if ( m_aScopeTbl[ ndx ].m_nSortOrder == nScope )
        {
          curScopeDesc = m_aScopeTbl[ ndx ];
          if ( curScopeDesc.m_listDataList.size() > 0 )
          {
            genDataMembers( curScopeDesc );
          }
        }

      } // end inner for()

    } // end outer for

    // Gen any constructors

    for ( int nScope = 1; nScope < 5; nScope++ )
    {
      for ( int ndx = 0; ndx < 4; ndx++ )
      {
        if ( m_aScopeTbl[ ndx ].m_nSortOrder == nScope )
        {
          genConstructors( ndx );
        }
      }

    }

    // *** Gen methods

    for ( int nScope = 1; nScope < 5; nScope++ )
    {
      for ( int ndx = 0; ndx < 4; ndx++ )
      {
        if ( m_aScopeTbl[ ndx ].m_nSortOrder == nScope )
        {
          curScopeDesc = m_aScopeTbl[ ndx ];
          if ( curScopeDesc.m_listMethodsList.size() > 0 )
          {
            genMethods( curScopeDesc );
          }

        }

      } // end inner for()

    } // end outer for()

    genGetSetMethods();

    if ( m_sbCloneable != null )
    {

      m_sbCloneable.append( "\n\n    return classClone;" );
      m_sbCloneable.append( "\n  }\n\n" );
      m_out.write( m_sbCloneable.toString(), 0, m_sbCloneable.length() );

      if ( m_fNeedListCloneCode )
      {
        String strListClone = this.getCloneListCode();
        m_out.write( strListClone, 0, strListClone.length() );

      }

      if ( m_fNeedMapCloneCode )
      {
        String strMapClone = this.getCloneMapCode();
        m_out.write( strMapClone, 0, strMapClone.length() );

      }
    }

    if ( m_fGenEqualsCode )
    {
      m_sbEquals.append( "\n\n    return true;" );
      m_sbEquals.append( "\n  }\n\n" );
      m_out.write( m_sbEquals.toString(), 0, m_sbEquals.length() );

      if ( m_fNeedObjectEqualsCode )
      {
        String strObjectEquals = getObjectEqualsCode();
        m_out.write( strObjectEquals, 0, strObjectEquals.length() );
      }

      if ( m_fNeedListEqualsCode )
      {
        String strListEquals = getListEqualsCode();
        m_out.write( strListEquals, 0, strListEquals.length() );
      }

      if ( m_fNeedMapEqualsCode )
      {
        String strMapEquals = getMapEqualsCode();
        m_out.write( strMapEquals, 0, strMapEquals.length() );
      }

    }
    // *** Add closing class/interface code here

    str.append( "\n} // *** End of " );
    if ( ( m_nObjectType & CLASS ) == CLASS )
    {
      str.append( "class " );
    }
    else
    {
      str.append( "interface " );
    }

    str.append( m_strClassName ).append( "{}\n\n// *** End Of " ).append( m_strFileName );

    m_out.write( str.toString(), 0, str.length() );

    m_out.close();

    if ( File.separatorChar == '\\' )
    {
      strFilePath = strFilePath.replace( '/', '\\' );
    }

    if ( logger != null )
    {
      logger.info( this.getClass(), "Generated " + strFilePath );
    }

    return true;

  } // end genertate()


  private String getCloneListCode()
  {
    StringBuffer sbCode = new StringBuffer( "\n\n  /**" );
    sbCode.append( "\n   *Clones a list and all its elements" );
    sbCode.append( "\n   *" );
    sbCode.append( "\n   * @param list The list to clone" );
    sbCode.append( "\n   *" );
    sbCode.append( "\n   * @return The cloned List object" );
    sbCode.append( "\n   *" );
    sbCode.append( "\n   */" );
    sbCode.append( "\n  private List cloneList( List list )" );
    sbCode.append( "\n  {\n" );
    sbCode.append( "\n    try" );
    sbCode.append( "\n    {" );
    sbCode.append( "\n      List listClone = (List)list.getClass().newInstance();\n" );
    sbCode.append( "\n      for ( Object objListContent : list )" );
    sbCode.append( "\n      {" );
    sbCode.append( "\n        if ( objListContent instanceof Cloneable )" );
    sbCode.append( "\n        {" );
    sbCode.append( "\n          Method mthdClone = objListContent.getClass().getMethod( \"clone\", (Class[])null );" );
    sbCode.append( "\n          Object objClone = mthdClone.invoke( objListContent, (Object[])null );" );
    sbCode.append( "\n          listClone.add( objClone );" );
    sbCode.append( "\n        } // end if" );
    sbCode.append( "\n      } // end for()\n" );
    sbCode.append( "\n      return listClone;" );
    sbCode.append( "\n    }\n" );
    sbCode.append( "    catch( Exception ex )\n" );
    sbCode.append( "    {\n" );
    sbCode.append( "      throw new RuntimeException( ex.toString() );\n" );
    sbCode.append( "    }" );
    sbCode.append( "\n  }" );

    return sbCode.toString();

  }


  private String getCloneMapCode()
  {

    StringBuffer sbCode = new StringBuffer( "\n\n  /**" );
    sbCode.append( "\n   *Clones a Map and all its elements" );
    sbCode.append( "\n   *" );
    sbCode.append( "\n   * @param map The map to clone" );
    sbCode.append( "\n   *" );
    sbCode.append( "\n   * @return The cloned Map object" );
    sbCode.append( "\n   *" );
    sbCode.append( "\n   */" );
    sbCode.append( "\n  private Map<?,?> cloneMap( Map<?,?> map )" );
    sbCode.append( "\n  {\n" );
    sbCode.append( "\n    try" );
    sbCode.append( "\n    {" );
    sbCode.append( "\n      Map mapClone = (Map<?,?>)map.getClass().newInstance();\n" );
    sbCode.append( "\n      for ( Object objMapKey : map.keySet() )" );
    sbCode.append( "\n      {" );
    sbCode.append( "\n        Object objMapContent = map.get( objMapKey ); " );
    sbCode.append( "\n        if ( objMapContent instanceof Cloneable )" );
    sbCode.append( "\n        {" );
    sbCode.append( "\n          Method mthdClone = objMapContent.getClass().getMethod( \"clone\", (Class[])null );" );
    sbCode.append( "\n          Object objClone = mthdClone.invoke( objMapContent, (Object[])null );" );
    sbCode.append( "\n          mapClone.put( objMapKey, objClone );" );
    sbCode.append( "\n        } // end if" );
    sbCode.append( "\n      } // end for()\n" );
    sbCode.append( "\n      return mapClone;" );
    sbCode.append( "\n    }\n" );
    sbCode.append( "    catch( Exception ex )\n" );
    sbCode.append( "    {\n" );
    sbCode.append( "      throw new RuntimeException( ex.toString() );\n" );
    sbCode.append( "    }" );
    sbCode.append( "\n  }" );

    return sbCode.toString();

  }

  /**
   * Return code to do an equals test on two objects
   *
   * @return
   */
  private String getObjectEqualsCode()
  {
    StringBuffer sbEquals = new StringBuffer( "\n\n  /**" );
    sbEquals.append( "\n   * Perform an equals test on an Object" );
    sbEquals.append( "\n   *" );
    sbEquals.append( "\n   * @param obj1 first object" );
    sbEquals.append( "\n   * @param obj2 second object" );
    sbEquals.append( "\n   *" );
    sbEquals.append( "\n   * @return true if objects are equal, false otherwise" );
    sbEquals.append( "\n   *" );
    sbEquals.append( "\n   */" );
    sbEquals.append( "\n  private boolean doObjectEqualsTest( Object obj1, Object obj2 )" );
    sbEquals.append( "\n  {" );
    sbEquals.append( "\n    if ( obj1 != null )" );
    sbEquals.append( "\n    {" );
    sbEquals.append( "\n      if ( obj2 == null )" );
    sbEquals.append( "\n        return false;" );
    sbEquals.append( "\n      return obj1.equals( obj2 );" );
    sbEquals.append( "\n    }" );
    sbEquals.append( "\n    else" );
    sbEquals.append( "\n    if ( obj2 != null )" );
    sbEquals.append( "\n      return false;\n" );
    sbEquals.append( "\n    return true;\n" );
    sbEquals.append( "\n  }" );

    return sbEquals.toString();

  }

  private String getListEqualsCode()
  {
    StringBuffer sbCode = new StringBuffer( "\n\n  /**" );
    sbCode.append( "\n   * Do equals test on each object in the list" );
    sbCode.append( "\n   *" );
    sbCode.append( "\n   * @param list1 the base list" );
    sbCode.append( "\n   * @param list2 the list to compare to the base list" );
    sbCode.append( "\n   *" );
    sbCode.append( "\n   * @return true if the lists are equal, false otherwise" );
    sbCode.append( "\n   *" );
    sbCode.append( "\n   */" );
    sbCode.append( "\n  private boolean doListElementTest( List list1, List list2 )" );
    sbCode.append( "\n  {\n" );
    sbCode.append( "\n    if ( list1 != null )" );
    sbCode.append( "\n    {" );
    sbCode.append( "\n      if ( list2 == null )" );
    sbCode.append( "\n        return false;" );
    sbCode.append( "\n      else" );
    sbCode.append( "\n      {" );
    sbCode.append( "\n        if ( list1.size() != list2.size() )" );
    sbCode.append( "\n          return false;   // sizes are different, not equal\n" );
    sbCode.append( "\n        Iterator iObj2 = list2.iterator();\n" );
    sbCode.append( "\n        for ( Object obj1 : list1 )" );
    sbCode.append( "\n        {" );
    sbCode.append( "\n          Object obj2 = iObj2.next();" );
    sbCode.append( "\n          if ( !obj1.equals( obj2 ) )" );
    sbCode.append( "\n            return false;\n" );
    sbCode.append( "\n        } // end for\n" );
    sbCode.append( "\n        return true;      // all elements are equal" );
    sbCode.append( "\n      } // end else\n" );
    sbCode.append( "\n    } // end if\n" );
    sbCode.append( "\n    if ( list2 == null )" );
    sbCode.append( "\n      return true;      // both lists are null so therefore the are equal\n" );
    sbCode.append( "\n    return false;\n" );
    sbCode.append( "\n  } // end doListElementTest()\n" );

    return sbCode.toString();

  } // end getListEqualsCode()


  private String getMapEqualsCode()
  {
    StringBuffer sbCode = new StringBuffer( "\n\n  /**" );
    sbCode.append( "\n   * Do equals test on each object in the map" );
    sbCode.append( "\n   *" );
    sbCode.append( "\n   * @param map1 the base map" );
    sbCode.append( "\n   * @param map2 the map to compare to the base map" );
    sbCode.append( "\n   *" );
    sbCode.append( "\n   * @return true if the maps are equal, false otherwise" );
    sbCode.append( "\n   *" );
    sbCode.append( "\n   */" );
    sbCode.append( "\n  private boolean doMapElementTest( Map<?,?>map1, Map<?,?> map2 )" );
    sbCode.append( "\n  {\n" );
    sbCode.append( "\n    if ( map1 != null )" );
    sbCode.append( "\n    {" );
    sbCode.append( "\n      if ( map2 == null )" );
    sbCode.append( "\n        return false;" );
    sbCode.append( "\n      else" );
    sbCode.append( "\n      {" );
    sbCode.append( "\n        if ( map1.size() != map2.size() )" );
    sbCode.append( "\n          return false;   // sizes are different, not equal\n" );
    sbCode.append( "\n        for ( Object objKey1 : map1.keySet() )" );
    sbCode.append( "\n        {" );
    sbCode.append( "\n          if( !map2.containsKey( objKey1 ) )" );
    sbCode.append( "\n            return false;\n" );
    sbCode.append( "\n         Object obj1 = map1.get( objKey1 );" );
    sbCode.append( "\n         Object obj2 = map2.get( objKey1 );" );
    sbCode.append( "\n         if ( ! obj1.equals( obj2 ) )" );
    sbCode.append( "\n           return false;\n" );
    sbCode.append( "\n        } // end for\n" );
    sbCode.append( "\n        return true;      // all elements are equal" );
    sbCode.append( "\n      } // end else\n" );
    sbCode.append( "\n    } // end if\n" );
    sbCode.append( "\n    if ( map2 == null )" );
    sbCode.append( "\n      return true;      // both maps are null so therefore the are equal\n" );
    sbCode.append( "\n    return false;\n" );
    sbCode.append( "\n  } // end doMapElementTest()\n" );

    return sbCode.toString();

  } // end getListEqualsCode()

} // end class VwClassGen{}

// *** End of VwClassGen.java ***

