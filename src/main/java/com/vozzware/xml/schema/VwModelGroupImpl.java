/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwModelGroupImpl.java

Create Date: Apr 11, 2006
============================================================================================
*/
package com.vozzware.xml.schema;

import javax.xml.schema.All;
import javax.xml.schema.Annotation;
import javax.xml.schema.Any;
import javax.xml.schema.Choice;
import javax.xml.schema.Element;
import javax.xml.schema.Group;
import javax.xml.schema.ModelGroup;
import javax.xml.schema.Sequence;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class VwModelGroupImpl extends VwSchemaCommonImpl implements ModelGroup
{
  private String  m_strMinOccurs;
  private String  m_strMaxOccurs;

  private List    m_listContent = new LinkedList();

  private Element m_searchElement;

  /**
   * Sets the minOccurs attribute
   *
   * @param strMinOccurs The min occurs value
   */
  public void setMinOccurs( String strMinOccurs )
  { m_strMinOccurs = strMinOccurs; }

  /**
   * Gets the minOccurs Attribute Value
   * @return
   */
  public String getMinOccurs()
  { return m_strMinOccurs; }


  /**
   * Sets the maxOccurs attribute
   *
   * @param strMaxOccurs The max occurs value
   */
  public void setMaxOccurs( String strMaxOccurs )
  { m_strMaxOccurs = strMaxOccurs; }

  /**
   * Gets the maxOccurs Attribute Value
   * @return
   */
  public String getMaxOccurs()
  { return m_strMaxOccurs; }


  /**
   * Adss an Element to the content list
   * @param element The element to add
   */
  public void addElement( Element element )
  { 
    m_listContent.add( element );
    
  }


  /**
   * Removes an Element object from the content list
   * @param element The Element object to remove
   */
  public void removeElementl( Element element )
  { m_listContent.remove( element ); }


  /**
   * Adss an All to the content list
   * @param all The All content to add
   */
  public void addAll( All all )
  { m_listContent.add( all ); }


  /**
   * Removes an All object from the content list
   * @param all The All object to remove
   */
  public void removeAll( All all )
  { m_listContent.remove( all ); }


  /**
   * Adss an Sequence to the content list
   * @param seq The Sequence to add
   */
  public void addSequence( Sequence seq )
  { m_listContent.add( seq ); }


  /**
   * Removes an Sequence object from the content list
   * @param seq The Sequence object to remove
   */
  public void removeSequence( Sequence seq )
  { m_listContent.remove( seq ); }


  /**
   * Adss an Choice to the content list
   * @param choice  The Choice to add
   */
  public void addChoice( Choice choice )
  { m_listContent.add( choice ); }


  /**
   * Removes an Choice object from the content list
   * @param choice The Choice object to remove
   */
  public void removeChoice( Choice choice )
  { m_listContent.remove( choice ); }


  /**
   * Adss an Group to the content list
   * @param group The Group to add
   */
  public void addGroup( Group group )
  { m_listContent.add( group ); }


  /**
   * Removes a Group object from the content list
   * @param group The Group object to remove
   */
  public void removeGroup( Group group )
  { m_listContent.remove( group ); }


  /**
   * Adds an Group to the content list
   * @param group The Group to add
   */
  public void addModelGroup( ModelGroup group )
  { m_listContent.add( group ); }

  /**
   * Removes a Group object from the content list
   * @param group The Group object to remove
   */
  public void removeModelGroup( ModelGroup group )
  { m_listContent.remove( group ); }
 
  /**
   * Adss an Any to the content list
   * @param any The Any to add
   */
  public void addAny( Any any )
  { m_listContent.add( any ); }


  /**
   * Removes an Any object from the content list
   * @param any The Any object to remove
   */
  public void removeAny( Any any )
  { m_listContent.remove( any ); }

  /**
   * Removes all of the element,sequence,choice,group and any content
   */
  public void removeAllContent()
  { m_listContent.clear(); }


  /**
   * Gets a List of all the content objects defined for this m_btModel group
   *
   * @return  a List of the content objects defined for this complexType in the following order:
   * <br> Annotation (if defined),,All,Sequence,Choice,Group or Any objects),
   */
  public List getContent()
  {
    Annotation anno = getAnnotation();

    List listContent = new LinkedList();

    if ( anno != null )
      listContent.add( anno );;

    listContent.addAll( m_listContent );

    return listContent;

  } // end getContent;

  
  /**
   * Finds the Element for the search name specified
   *
   * @param strSearchName The name of the element in the group to search for
   * @return The Element if found or null if none found
   */
  public synchronized Element findElement( String strSearchName )
  {
    m_searchElement = null;
    findElement( strSearchName, m_listContent );
    return m_searchElement;
  }
  

  /**
   * Finds the first VwSchemaElement either in this group or in a nested group
   *
   * @return The first occurrence of an Element found or null if none found
   */
  public synchronized Element findFirstElement()
  {
    m_searchElement = null;

    findFirstElement( m_listContent );

    return m_searchElement;

  } // end findFirstElement()


  /**
   * Recursive method to find the first occurrence of an VwSchemaElement
   */
  private void findFirstElement( List listObjects )
  {
    if ( m_searchElement != null )
      return;

    if ( listObjects.size() == 0 )
      return;
    
    Object obj = listObjects.get( 0 );

    if ( obj instanceof Element )
    {
      m_searchElement = (Element)obj;
      return;
    }

    // Search for element in nested group
    findFirstElement( ((VwModelGroupImpl)obj).m_listContent );

  } // end findElement()

  
  /**
   * Recursive method to find the first occurrence of an VwSchemaElement
   */
  private void findElement( String strSearchName, List listObjects )
  {
    if ( m_searchElement != null )
      return;

    for ( Iterator iObjects = listObjects.iterator(); iObjects.hasNext(); )
    {
      Object obj = iObjects.next();
	    if ( obj instanceof Element )
	    {
	      String strName = ((Element)obj).getName();
	      if ( strName == null )
	        strName = ((Element)obj).getRef();
	      
	      if ( strName == null )
	        continue;
	      
	      if ( strName.equalsIgnoreCase( strSearchName ))
	      {  
	        m_searchElement = (Element)obj;
	        return;
	      }
	      
	    }
	    else
	    if ( obj instanceof VwModelGroupImpl )
		    // Search for element in nested group
		    findElement( strSearchName, ((VwModelGroupImpl)obj).m_listContent );

    }
  } // end findElement()
  
} // end class VwModelGroupImpl{}

// *** End of VwModelGroupImpl.java ***