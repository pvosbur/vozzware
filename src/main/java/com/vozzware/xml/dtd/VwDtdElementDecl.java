/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwDtdElementDecl.java

Create Date: Apr 11, 2006
============================================================================================
*/
package com.vozzware.xml.dtd;

import com.vozzware.util.VwExString;
import com.vozzware.util.VwStringCursor;
import com.vozzware.xml.schema.VwChoiceImpl;
import com.vozzware.xml.schema.VwElementImpl;
import com.vozzware.xml.schema.VwModelGroupImpl;
import com.vozzware.xml.schema.VwSequenceImpl;

import javax.xml.schema.ModelGroup;

/**
 * This utility class converts certain requested schema types into java classes that hold
 * a parsed xml document instance as well as java classes that build xml documents from
 * defined data sources.
 *
 */
public class VwDtdElementDecl
{

  /**
   * Public constant for the PCDATA element content type
   */
  public static final int PCDATA = 0;

  /**
   * Public constant for the PARENT element content type
   */
  public static final int PARENT = 1;


  /**
   * Public constant for the MIXED element content type
   */
  public static final int MIXED = 2;

  /**
   * Public constant for the EMPTY element content type
   */
  public static final int EMPTY = 3;

  /**
   * Public constant for the ANY element content type
   */
  public static final int ANY = 4;


  private int                   m_nContentType;   // DTD content type

  private String                m_strElementName; // The name of the element

  private String                m_strOrder;       // Overall order for content list, Either sequence or choice

  private ModelGroup						m_content;        // if element is a group (sequence or choice)the group object, else it's null


  /**
   * Constructor - only used by the VwDtdParser to pased the unparesed element content
   *
   * @param strElementName The name of the element
   * @param strContent The m_btModel content
   */
  VwDtdElementDecl( String strElementName, String strContent ) throws Exception
  {
    m_strElementName = strElementName;
    parse( strContent );

  } // end VwDtdElementDecl


  /**
   * Return the content type constant
   */
  public int getContentType()
  { return m_nContentType; }


  /**
   * Sets the content type constant
   */
  public void setContentType( int nContentType )
  { m_nContentType = nContentType; }

  /**
   * Returns the element group of children for this parent tag
   */
  public ModelGroup getGroup()
  { return m_content; }


  /**
   * Return the element name
   */
  public String getName()
  { return m_strElementName; }


  /**
   * Parse the content m_btModel string passed by the dtd parser engine
   *
   * @param strContent The unparsed element content m_btModel
   */
  private void parse( String strContent ) throws Exception
  {
    VwStringCursor sc = null;
    try
    {
      sc = new VwStringCursor( strContent );
    }
    catch( Exception ex )
    {
      ex.printStackTrace();
      return;
    }

    sc.setDelimiters( "(),|" );

    String strToken = sc.getWord();

    // Possible start of new group
    if ( strToken.equals( "(" ) )
    {
      m_content = doContent( sc );
      
      if ( m_content != null )
        checkConstraints( m_content, sc );
    }
    else
    if ( strToken.equalsIgnoreCase( "any" )  )
    {
      m_nContentType = ANY;
      return;
      }
    else
    if ( strToken.equalsIgnoreCase( "empty" )  )
    {
      m_nContentType = EMPTY;
      return;
    }


  }  // end parse


  /**
   * Parse out the element content into choice or sequence groups
   *
   * @param sc The String cursor parser
   */
  private ModelGroup doContent( VwStringCursor sc ) throws Exception
  {

    String strDelim = null;

    ModelGroup content = null;
    
    while( true )
    {
      String strToken = sc.getWord();
      if ( strToken == null )
        return content;

      int nPos = strToken.indexOf( ':');
      if ( nPos > 0 )
        strToken = strToken.substring( ++nPos );
      
      if ( strToken.equalsIgnoreCase( "(" )  )
      {
        if ( content == null )
          content = new VwModelGroupImpl();
        
        ModelGroup  childContent = doContent( sc );
        if ( childContent != null )
        {
          content.addModelGroup( childContent );
          checkConstraints( childContent, sc );
        }
      }
      else
      if ( strToken.equalsIgnoreCase( ")" )  )  // End of group
        return content;
      else
      if ( strToken.equalsIgnoreCase( "#pcdata" )  )
      {
        String strNext = sc.getWord();

        if ( strNext.equals( ")" ) )
        {
          m_nContentType = PCDATA;
          return null;
        }
        else
        {
          if ( content == null )
            content = new VwModelGroupImpl();
          
          doMixed( sc, content );
        }
      } // end if
      else
      if ( strToken.equals( "," )  || strToken.equals( "|" ))
        continue;
      else
      {
        if ( content == null )
        {
          nPos = sc.getCursor();
          
          String strNext = sc.getWord();
          
          if ( strNext.equals( ")" ) )
          {
            content = new VwSequenceImpl();
            sc.setCursor( nPos );  // put back tokeb
        
          }
          else
          if ( strNext.equals( "," ) )
             content = new VwSequenceImpl();
          else
          if ( strNext.equals( "|" ) )
            content = new VwChoiceImpl();
          else
            sc.setCursor( nPos );  // put back tokeb
        }
        
        m_nContentType = PARENT;
        addElement( content, strToken );
        
      } // end else

    } // end while

  } // end doContent()


  /**
   * Handle a mixed content spec
   *
   * @param sc The string cursor containing the string to parse
   */
  private void doMixed( VwStringCursor sc, ModelGroup group ) throws Exception
  {
    m_nContentType = MIXED;

    String strToken = null;

    VwChoiceImpl choice = new VwChoiceImpl();
    group.addChoice( choice );
    
    m_strOrder = "choice";

    while( true )
    {
      int nCurPos = sc.getCursor();
      strToken = sc.getWord();

      if ( strToken == null )
        return;

      if ( strToken.equals( ")" ) )
      {
        sc.setCursor( nCurPos );
        return;
      }
      else
      if ( ! strToken.equals( "|" ) )
        addElement( choice, strToken );

    } // end while()

    
  } //end doMoxed()


  /**
   * Add an element to the group and check for element constraints
   */
  public void addElement( ModelGroup group, String strToken )
  {
    char ch = strToken.charAt( strToken.length() - 1 );

    if ( VwExString.isin( ch, "*+?" ) )
      strToken = strToken.substring( 0, strToken.length() - 1 );

    VwElementImpl element = new VwElementImpl();
    element.setName( strToken );
    
    if ( ch == '*' )
    {
      element.setMinOccurs( "0" );
      element.setMaxOccurs( "unbounded" );
    }
    else
    if ( ch == '?' )
    {
      element.setMinOccurs( "0" );
      element.setMaxOccurs( "1" );
    }
    else
    if ( ch == '+' )
    {
      element.setMinOccurs( "1" );
      element.setMaxOccurs( "unbounded" );
    }

    group.addElement( element );

  } // end addElement


  /**
   * Check to see if there are any group constraints
   *
   * @param newGroup The group to apply any constraints to
   * @param sc The StringCursor
   */
  public void checkConstraints( ModelGroup newGroup, VwStringCursor sc )
  {
    int nCurPos = sc.getCursor();

    String strToken = sc.getWord();

    if ( strToken == null )
      return;

    if ( strToken.equals( "*" ) )
    {
      newGroup.setMinOccurs( "0" );
      newGroup.setMaxOccurs( "unbounded" );
    }
    else
    if ( strToken.equals( "+" ) )
    {
      newGroup.setMinOccurs( "1" );
      newGroup.setMaxOccurs( "unbounded" );
    }
    else
    if ( strToken.equals( "?" ) )
    {
      newGroup.setMinOccurs( "0" );
      newGroup.setMaxOccurs( "1" );
    }
    else
      sc.setCursor( nCurPos );    // Restore cursor


  } // end checkConstraints()



  public static void main( String[] args )
  {
    try
    {
      VwDtdElementDecl ed = new VwDtdElementDecl( "dummy",
               "(( Header, (Message | Request)) | Response)" );
      return;

    }
    catch( Exception ex )
    {
      ex.printStackTrace();
    }
  }
} // end class VwDtdElementDecl{}

// *** End of VwDtdElementDecl.java ***
