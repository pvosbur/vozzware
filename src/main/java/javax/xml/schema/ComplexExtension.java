/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: ComplexExtension.java

============================================================================================
*/
package javax.xml.schema;

/**
 * This represents 
 */
public interface ComplexExtension extends Extension
{
  /**
   * Sets the  m_btModel group content type to All
   *
   * @param all The all m_btModel group content object
   *
   */
  public void setAll( All all );

  /**
   * Gets the All m_btModel group content
   * @return the All m_btModel group content if the content type is All else null is returned
   */
  public All getAll();


  /**
   * Returns true if the content type is the m_btModel group All
   * @return true if the content type is the m_btModel group All
   */
  public boolean isAllContent();

  /**
   * Sets the m_btModel group content to choice
   *
   * @param choice the choice m_btModel group content
   *
   */
  public void setChoice( Choice choice );


  /**
   * Gets the Choice m_btModel group content
   * @return the Choice m_btModel group content if the content type is Choice else null is returned
   */
  public Choice getChoice();


  /**
   * Returns true if the content type is the m_btModel group Choice
   * @return true if the content type is the m_btModel group Choice
   */
  public boolean isChoiceContent();


  /**
   * Sets the m_btModel group content to sequence
   *
   * @param sequence the choice m_btModel group content
   *
   */
  public void setSequence( Sequence sequence );


  /**
   * Gets the Sequence m_btModel group content
   * @return the Sequence m_btModel group content if the content type is Sequence else null is returned
   */
  public Sequence getSequence();


  /**
   * Returns true if the content type is the m_btModel group Sequence
   * @return true if the content type is the m_btModel group Sequence
   */
  public boolean isSequenceContent();

  /**
   * Sets a named m_btModel group
   *
   * @param group The named m_btModel group
   */
  public void setGroup( ModelGroup group );

  /**
   * Gets the m_btModel group content (if a m_btModel group content exists)
   * Returns the Group for this type if one exists or null otherwise
   */
  public ModelGroup getGroup();


  /**
   * Returns true if the content for this complexType is group, all, choice or sequence

   * @return true if the content for this complexType is group, all, choice or sequence
   */
  public boolean isModelGroup();



} // end interface ComplexExtension{}

// *** End of ComplexExtension.java ***

