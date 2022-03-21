/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                               V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwElementImpl.java

Create Date: Apr 11, 2006
============================================================================================
*/
package com.vozzware.xml.schema;

import javax.xml.schema.ComplexType;
import javax.xml.schema.Element;
import javax.xml.schema.ModelGroup;
import javax.xml.schema.Schema;
import javax.xml.schema.SimpleType;
import java.util.LinkedList;
import java.util.List;

public class VwElementImpl extends VwSchemaCommonImpl implements Element
{
  private Boolean m_fAbstract;
  private Boolean m_fNillable;

  private Object  m_objContent;

  private String  m_strBlock;
  private String  m_strDefault;
  private String  m_strFinal;
  private String  m_strFixed;
  private String  m_strForm;
  private String  m_strMaxOccurs;
  private String  m_strMinOccurs;
  private String  m_strRef;
  private String  m_strSubstitutionGroup;
  private String  m_strType;

  
  /**
   * Default constructor
   *
   */
  public VwElementImpl()
  { ; }
  
  
  /**
   * Constructs with an initialized name and type
   * 
   * @param strName The element's name
   * @param strType the elements type (QName)
   */
  public VwElementImpl( String strName, String strType )
  {
    setName( strName );
    m_strType = strType;
  }

  public String toString()
  { return "name: " +getName() + ", type: " + m_strType; }
  
  /**
   * Sets the Abstract state of this element
   *
   * @param fAbstract true if this element is abstract, false otherwise
   */
  public void setAbstract( Boolean fAbstract )
  { m_fAbstract = fAbstract; }

  /**
   * Gets Abstract property
   *
   * @return true if this is an abstract element, false otherwise
   */
  public Boolean isAbstract()
  { return m_fAbstract; }

  /**
   * A space delimited string of any combination of 'extension' 'restriction' 'substitution or #all
   *
   * @param strBlock the element's block attribute value
   */
  public void setBlock( String strBlock )
  { m_strBlock = strBlock; }


  /**
   * Gets elements block attribute value
   *
   * @return elements block attribute value
   */
  public String getBlock()
  { return m_strBlock;  }

  /**
   * Sets the Default property
   *
   * @param strDefault
   */
  public void setDefault( String strDefault )
  { m_strDefault = strDefault;  }

  /**
   * Gets Default property
   *
   * @return The Default property
   */
  public String getDefault()
  { return m_strDefault; }


  /**
   * Sets the Final property
   *
   * @param strFinal A space delimited string of any combination of 'extension' or 'restriction'
   */
  public void setFinal( String strFinal )
  { m_strFinal = strFinal; }

  /**
   * Gets Final property
   *
   * @return The Final property
   */
  public String getFinal()
  { return m_strFinal;  }

  /**
   * Sets the Fixed property
   *
   * @param strFixed
   */
  public void setFixed( String strFixed )
  { m_strFixed = strFixed;  }

  /**
   * Gets Fixed property
   *
   * @return The Fixed property
   */
  public String getFixed()
  { return m_strFixed; }

  /**
   * Sets the Form property
   *
   * @param strForm
   */
  public void setForm( String strForm )
  { m_strForm = strForm;  }

  /**
   * Gets Form property
   *
   * @return The Form property
   */
  public String getForm()
  { return m_strForm; }

  /**
   * Determins if this element is a parent. This is a helper method
   */
  public boolean isParent()
  {
    return (m_objContent != null || getAnnotation() != null );
  }

  /**
   * Returns the number of direct children if this element is a parent. This is a helper method
   */
  public int getChildCount()
  {
    return 0;
  }

  /**
   * Sets the MaxOccurs property
   *
   * @param strMaxOccurs The maxOccurs value
   */
  public void setMaxOccurs( String strMaxOccurs )
  { m_strMaxOccurs = strMaxOccurs; }

  /**
   * Gets MaxOccurs property
   *
   * @return The MaxOccurs property
   */
  public String getMaxOccurs()
  { return m_strMaxOccurs;  }

  /**
   * Sets the MinOccurs property
   *
   * @param strMinOccurs
   */
  public void setMinOccurs( String strMinOccurs )
  { m_strMinOccurs = strMinOccurs; }

  /**
   * Gets MinOccurs property
   *
   * @return The MinOccurs property
   */
  public String getMinOccurs()
  { return m_strMinOccurs; }

  /**
   * Sets the Nillable property
   *
   * @param fNillable true if this element can be null, false otherwise
   */
  public void setNillable( Boolean fNillable )
  { m_fNillable = fNillable; }

  /**
   * Gets Nillable property
   *
   * @return The Nillable property
   */
  public Boolean isNillable()
  { return m_fNillable; }

  /**
   * Sets the Ref property
   *
   * @param strRef
   */
  public void setRef( String strRef )
  { m_strRef = strRef; }

  /**
   * Gets Ref property
   *
   * @return The Ref property
   */
  public String getRef()
  { return m_strRef; }

  /**
   * Sets the SubstituationGroup property
   *
   * @param strSubstitutionGroup The substitutionGroup
   */
  public void setSubstituationGroup( String strSubstitutionGroup )
  { m_strSubstitutionGroup = strSubstitutionGroup; }

  /**
   * Gets SubstituationGroup property
   *
   * @return The SubstituationGroup property
   */
  public String getSubstituationGroup()
  { return m_strSubstitutionGroup; }


  /**
   * Sets the Type property
   *
   * @param strType
   */
  public void setType( String strType )
  { m_strType = strType; }

  /**
   * Gets Type property
   *
   * @return The Type property
   */
  public String getType()
  { return m_strType; }

  /**
   * Sets the elements content to contain a complexType
   *
   * @param complexType The complexType content for this element
   */
  public void setComplexType( ComplexType complexType )
  { m_objContent = complexType;  }


  /**
   * Returns the ComplexType object if the content is a ComplexType else null is returned
   * @return
   */
  public ComplexType getComplexType()
  {
    if ( m_objContent instanceof ComplexType )
      return (ComplexType)m_objContent;

    return null;

  } // end getComplexType()

  /**
   * Returns true if the content is a ComplexType
   * @return
   */
  public boolean  isComplexType()
  { return  m_objContent instanceof ComplexType; }


  /**
   * Sets the elements content to contain a simpleType
   *
   * @param simpleType The simpleType content for this element
   */
  public void setSimpleType( SimpleType simpleType )
  { m_objContent = simpleType;  }


  /**
   * Returns the SimpleType object if the content is a SimpleType else null is returned
   * @return
   */
  public SimpleType getSimpleType()
  {
    if ( m_objContent instanceof SimpleType )
      return (SimpleType)m_objContent;

    return null;

  } // end getSimpleType()

  /**
   * Returns true if the content is a SimpleType
   * @return
   */
  public boolean  isSimpleType()
  { return  m_objContent instanceof SimpleType; }

  
  /**
   * Gets List of all content
   * @return
   */
  public List getContent()
  {
    List listContent = new LinkedList();

    if ( getAnnotation() != null )
      listContent.add( getAnnotation() );

    if ( m_objContent != null )
      listContent.add( m_objContent );

    return listContent;

  } // end getContent()


  /**
   * Helper method to determine if this element has a choice,sequence or all group child
   *
   * @return true if this element has one of the m_btModel group elements:choice,sequence or all,
   * false otherwise
   */
  public boolean hasModelGroup( Schema schema )
  {
    ComplexType type = findType( schema );

    if ( type != null )
      return type.hasModelGroup();

    return false;

  } // end hasModelGroup()

  /**
   * Helper method to return the ModelGroup if this element has one.
   * @return the ModelGroup if this element has one, null otherwise
   */
  public ModelGroup getModelGroup(  Schema schema )
  {
    ComplexType type = findType( schema );

    if ( type != null )
      return type.getModelGroup();

    return null;

  } // getsModelGroup()


  /**
   * Helper method to determine if this element has attributes defined
   * @return true if this element has attributes, false otherwise
   */
  public boolean hasAttributes(  Schema schema )
  {
    ComplexType type = findType( schema );

    if ( type != null )
      return type.hasAttributes();

    return false;

  } // end hasAttributes()


  /**
   * Helper method to return a List of Attribhute objects defined for this element
   *
   * @return a List of Attribhute objects defined for this element if they exist otherwise null is returned
   */
  public List getAttributes( Schema schema )
  {
    ComplexType type = findType( schema );

    if ( type != null  )
      return type.getAttributes();

    return null;

  } // end getAttributes

  /**
   * Return the Complex type for this elememt if this element is a complexType
   * or null if it is not
   *
   * @param schema
   * @return
   */
  private ComplexType findType( Schema schema )
  {
    if ( m_objContent instanceof ComplexType )
      return (ComplexType)m_objContent;

    if ( schema != null )
    {
      String strKey = m_strType;
      if ( strKey == null )
        strKey = m_strRef;

      if ( strKey != null )
      {
        int nPos = strKey.indexOf( ':' );

        if ( nPos > 0 )
          strKey = strKey.substring( ++nPos );

        Object objType = schema.getComponent( strKey );

        if ( objType instanceof ComplexType )
          return (ComplexType)objType;

      }
    }

    return null;

  } // end findType()

  /**
   * Helper method to determine if this element is a collection. (i.e., maxOccurs > 1
   *
   * @return true if this element represents a collection, false otherwise
   */
  public boolean isCollection()
  {
    if ( m_strMaxOccurs == null )
      return false;
    
    if ( m_strMaxOccurs.equalsIgnoreCase( "unbounded") ||
        ( !m_strMaxOccurs.equals( "0" ) && ! m_strMaxOccurs.equals( "1" ) ))
      return true;
    
    return false;

  }

} // end class VwElementImpl{}

// *** End of VwElementImpl.java ***
