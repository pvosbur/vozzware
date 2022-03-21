/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwSimpleContentImpl.java

Create Date: Apr 11, 2006
============================================================================================
*/
package com.vozzware.xml.schema;

import javax.xml.schema.Extension;
import javax.xml.schema.Restriction;
import javax.xml.schema.SimpleContent;
import java.util.LinkedList;

public class VwSimpleContentImpl extends VwSchemaCommonImpl implements SimpleContent
{
  private Object m_content;


  public String getType()
  {
    if ( m_content instanceof Extension )
      ((Extension)m_content).getBase();
    else
    if ( m_content instanceof Restriction )
      ((Restriction)m_content).getBase();

    return null;
    
  }
  public Extension getExtension()
  {
    if ( m_content instanceof Extension )
      return (Extension)m_content;

    return null;
  }

  public Restriction getRestriction()
  {
    if ( m_content instanceof Restriction )
      return (Restriction)m_content;

    return null;

  }

  public void setExtesion( Extension extension )
  { m_content = extension;}

  public void setRestriction( Restriction restriction )
  { m_content = restriction; }

  public java.util.List getContent()
  {
    java.util.List listContent = new LinkedList();

    if ( getAnnotation() != null )
      listContent.add( getAnnotation() );

    if ( m_content != null )
      listContent.add( m_content );

    return listContent;
  }



} // end class VwSimpleContentImpl{}

// *** end of VwSimpleContentImpl.java ***
