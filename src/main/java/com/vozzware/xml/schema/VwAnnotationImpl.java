/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwAnnotationImpl.java

Create Date: Apr 11, 2006
============================================================================================
*/
package com.vozzware.xml.schema;

import javax.xml.schema.Annotation;
import javax.xml.schema.AppInfo;
import javax.xml.schema.Documentation;
import java.util.LinkedList;
import java.util.List;

public class VwAnnotationImpl extends VwSchemaCommonImpl implements Annotation
{
 
  private List  m_listContent = new LinkedList();
  private List  m_listDoc = new LinkedList();
  private List  m_listAppInfo = new LinkedList();

  /**
   * Adds a Documentaion content
   *
   * @param doc The Documentaion content
   */
  public void addDocumentation( Documentation doc )
  {
    m_listContent.add( doc );
    m_listDoc.add( doc );

  } // end addDocumentation()

  /**
   * Returns a List of Documentation objects that belong to this Annotation
   *
   * @return  a List of Documentation objects that belong to this Annotation
   */
  public List getDocumentationList()
  { return m_listDoc; }

  /**
   * Removes the specified AppInfo object from the content list
   * @param doc The Documentation instance to remove
   */
  public void removeDocumentation( Documentation doc )
  {
    m_listContent.remove( doc );
    m_listDoc.remove( doc );
  }

  /**
   * Removes all AppInfo objects from this Annotation content
   */
  public void removeAllDocumentation()
  {
    m_listContent.removeAll( m_listDoc );
    m_listDoc.clear();
  }


  /**
   * Adds an AppInfo content object to this Annotation
   * @param appInfo The AppInfo content object
   */
  public void addAppinfo( AppInfo appInfo )
  {
    m_listContent.add( appInfo );
    m_listAppInfo.add( appInfo );

  }

  /**
   * Returns a List of AppInfo objects that belong to this Annotation
   *
   * @return  a List of AppInfo objects that belong to this Annotation
   */
  public List getAppInfoList()
  { return m_listAppInfo; }

  /**
   * Removes the specified AppInfo object from the content list
   * @param appInfo The AppInfo instance to remove
   */
  public void removeAppInfo( AppInfo appInfo )
  {
    m_listContent.remove( appInfo );
    m_listAppInfo.remove( appInfo );

  }

  /**
   * Removes all AppInfo objects from this Annotation content
   */
  public void removeAllAppInfo()
  {
    m_listContent.removeAll( m_listAppInfo );
    m_listAppInfo.clear();
  }



  /**
   * Returns an iterator to the Annotation content which may contain Documentaion and AppInfo objects
   *
   * @return an iterator to the Annotation content which may contain Documentaion and AppInfo objects
   */
  public List getContent()
  { return m_listContent;  }


  /**
   * Removes all of the content objects (Documentation and AppInfo objects) from this Annotation
   */
  public void removeAllContent()
  {
    m_listContent.clear();
    m_listDoc.clear();
    m_listAppInfo.clear();

  } // end removeAllContent

  /**
   * Determins if there is at least one AppInfo content object in this annotation
   *
   * @return true if there is one or more AppInfo objects in the content list
   */
  public boolean hasAppInfoContent()
  { return (m_listAppInfo.size() > 0 );  }

  /**
   * Determins if there is at least one Doumemtation content object in this annotation
   *
   * @return true if there is one or more Documemtation objects in the content list
   */
  public boolean hasDocumentationContent()
  { return (m_listDoc.size() > 0 ); }


} // end class VwAnnotationImpl{}

// *** End of VwAnnotationImpl.java ***
