/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: Annotation.java

============================================================================================
*/
package javax.xml.schema;

import java.util.List;

/**
 * This interface represents the xml schema annotation component
 *
 * @author Peter VosBurgh Jr.
 */
public interface Annotation extends SchemaCommon
{


  /**
   * Adds a Documentaion content
   * @param doc The Documentaion content to add
   */
  public void addDocumentation( Documentation doc );

  /**
   * Returns a List of Documentation objects that belong to this Annotation
   *
   * @return  a List of Documentation objects that belong to this Annotation
   */
  public List getDocumentationList();

  /**
   * Removes the specified Documentation object from the content list
   * @param doc The Documentation instance to remove
   */
  public void removeDocumentation( Documentation doc );

  /**
   * Removes all Documentation objects from this Annotation content
   */
  public void removeAllDocumentation();

  /**
   * Adds an AppInfo content object to this Annotation
   * @param appInfo The AppInfo content object to add
   */
  public void addAppinfo( AppInfo appInfo );

  /**
   * Returns a List of AppInfo objects that belong to this Annotation
   *
   * @return  a List of AppInfo objects that belong to this Annotation
   */
  public List getAppInfoList();

  /**
   * Removes the specified AppInfo object from the content list
   * @param appInfo The AppInfo instance to remove
   */
  public void removeAppInfo( AppInfo appInfo );

  /**
   * Removes all AppInfo objects from this Annotation content
   */
  public void removeAllAppInfo();

  /**
   * Returns a List to the Annotation content which may contain Documentaion and AppInfo objects
   *
   * @return  a List to the Annotation content which may contain Documentaion and AppInfo objects in the order they were added
   */
  public List getContent();


  /**
   * Removes all of the content objects (Documentation and AppInfo objects) from this Annotation
   */
  public void removeAllContent();

  /**
   * Determins if there is at least one AppInfo content object in this annotation
   *
   * @return true if there is one or more AppInfo objects in the content list
   */
  public boolean hasAppInfoContent();

 /**
  * Determins if there is at least one Doumemtation content object in this annotation
  *
  * @return true if there is one or more Documemtation objects in the content list
 */
  public boolean hasDocumentationContent();

} // end interface Annotation{}

// *** End of Annotation.java ***
