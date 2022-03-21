/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: ModelGroup.java

============================================================================================
*/
package javax.xml.schema;

import java.util.List;

public interface ModelGroup extends SchemaCommon
{
  /**
   * Sets the minOccurs attribute
   *
   * @param strMinOccurs The min occurs value
   */
  public void setMinOccurs( String strMinOccurs );

  /**
   * Gets the minOccurs Attribute Value
   * @return
   */
  public String getMinOccurs();


  /**
   * Sets the maxOccurs attribute
   *
   * @param strMaxOccurs The max occurs value
   */
  public void setMaxOccurs( String strMaxOccurs );

  /**
   * Gets the maxOccurs Attribute Value
   * @return
   */
  public String getMaxOccurs();

  /**
   * Adss an Element to the content list
   * @param element The element to add
   */
  public void addElement( Element element );

  /**
   * Removes an Element object from the content list
   * @param element The Element object to remove
   */
  public void removeElementl( Element element );

  
   
  /**
   * Adss an All to the content list
   * @param all The All content to add
   */
  public void addAll( All all );

  /**
   * Removes an All object from the content list
   * @param all The All object to remove
   */
  public void removeAll( All all );

  /**
   * Adss an Sequence to the content list
   * @param sequence The Sequence to add
   */
  public void addSequence( Sequence sequence );

  /**
   * Removes an Sequence object from the content list
   * @param seq The Sequence object to remove
   */
  public void removeSequence( Sequence seq );

  /**
   * Adss an Choice to the content list
   * @param choice  The Choice to add
   */
  public void addChoice( Choice choice );


  /**
   * Removes an Choice object from the content list
   * @param choice The Choice object to remove
   */
  public void removeChoice( Choice choice );

  /**
   * Adds an Group to the content list
   * @param group The Group to add
   */
  public void addGroup( Group group );

  /**
   * Removes a Group object from the content list
   * @param group The Group object to remove
   */
  public void removeGroup( Group group );

  
  /**
   * Adds an Group to the content list
   * @param group The Group to add
   */
  public void addModelGroup( ModelGroup group );

  /**
   * Removes a Group object from the content list
   * @param group The Group object to remove
   */
  public void removeModelGroup( ModelGroup group );
  
  /**
   * Adss an Any to the content list
   * @param any The Any to add
   */
  public void addAny( Any any );

  /**
   * Removes an Any object from the content list
   * @param any The Any object to remove
   */
  public void removeAny( Any any );

  /**
   * Removes all of the element,sequence,choice,group and any content
   */
  public void removeAllContent();


  /**
   * Gets a List of all the content objects defined for this m_btModel group
   *
   * @return  a List of the content objects defined for this complexType in the following order:
   * <br> Annotation (if defined),,All,Sequence,Choice,Group or Any objects),
   */
  public List getContent();

  
  /**
   * Finds the Element for the search name specified
   *
   * @param strSearchName The name of the element in the group to search for
   * @return The Element if found or null if none found
   */
  public Element findElement( String strSearchName );
  
  /**
   * Helper to locate the first element in the group or in a subgroup
   * @return
   */
  public Element findFirstElement();

} // end interface ModelGroup{}

// *** End of ModelGroup