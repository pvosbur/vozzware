/**
* VwCodeSnippetMgr.java
*
*/
package com.vozzware.codegen;

import com.vozzware.db.util.CodeSnippets;
import com.vozzware.db.util.CodeSnippetsReader;
import com.vozzware.db.util.Snippet;
import com.vozzware.util.VwExString;
import com.vozzware.util.VwResourceStoreFactory;

import java.net.URL;
import java.util.Map;

/**
 * This class provides a helper to loading code snippet xml files retrieving the code snippet by name
 * @author petervosburghjr
 *
 */
public class VwCodeSnippetMgr
{
  private   CodeSnippets                m_codeSnippets;

  /**
   * Load the code snamed snippets xml document
   * @throws Exception If the document could not be found
   */
  public void loadCodeSnippets( String strSnippetDocName) throws Exception
  {
    URL urlCodeSnippets = VwResourceStoreFactory.getInstance().getStore().getDocument( strSnippetDocName );
    m_codeSnippets = CodeSnippetsReader.read( urlCodeSnippets );
    
  }

  
  /**
   * Gets the code snippet for the snippet type requested
   * @param strType The code snippet type
   * @param mapMacroSubstitutions the map of macro substitutions (may be null)
   * @return
   */
  public String getSnippet(  Map<String,String>mapMacroSubstitutions, String strType )
  {
    for ( Snippet snippet : m_codeSnippets.getSnippet() )
    {
      if ( snippet.getType().equalsIgnoreCase( strType ))
      {
        String strCode =  snippet.getCode();
        if ( mapMacroSubstitutions != null )
          return "    " + VwExString.expandMacro( strCode, mapMacroSubstitutions );
        else
          return "    " + strCode;
      }
    } // end for()
    
    return null;    // not found
  }
  
} // end class
