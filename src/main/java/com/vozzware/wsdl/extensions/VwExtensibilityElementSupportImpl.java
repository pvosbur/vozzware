/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwExtensibilityElementSupportImpl.java

Create Date: Apr 11, 2006
============================================================================================
*/
package com.vozzware.wsdl.extensions;

import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.ExtensibilityElementSupport;
import javax.wsdl.extensions.UnknownExtensibilityElement;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Implementation class for the ExtensibilityElementSupport interface
 *
 * @author Peter VosBurgh
 */
public class VwExtensibilityElementSupportImpl implements ExtensibilityElementSupport 
{
  private List m_listExtElements = new LinkedList();
  
  
  /**
   * Gets the List of all ExtensibilityElements and UnknownExtensibilityElements
   * 
   * @return  the List of all ExtensibilityElements and UnknownExtensibilityElements
   */
  public List getAllElements()
  { return m_listExtElements; }

  
  /**
   * Gets the List of all ExtensibilityElements and UnknownExtensibilityElements
   * 
   * @return  the List of all ExtensibilityElements and UnknownExtensibilityElements
   */
  public List getAllElements( List listElements )
  { 
    List listAll = getExtensibilityElements( listElements );
    listAll.addAll( getUnknownExtensibilityElements( listElements ) );
    
    return listAll;
  }
  
  /**
   * Removes all ExtensibilityElements and UnknownExtensibilityElements
   *
   */
  public void removeAllElements()
  { m_listExtElements.clear(); }

  
  /**
   * Removes all ExtensibilityElements and UnknownExtensibilityElements
   * 
   * @param listElements The List of elements 
   *
   */
  public void removeAllElements( List listElements )
  { 
    removeAllExtensibilityElements( listElements );
    removeAllUnknownExtensibilityElements( listElements );
  
  } // end removeAllElements()
  
  /**
   * Adds an extensibility element for specific service extensions
   *
   * @param extElement extensibility element for specific service extensions
   */
  public void addExtensibilityElement( ExtensibilityElement extElement )
  {  m_listExtElements.add( extElement ); }

  /**
   * Removes the specified extensibility element
   *
   * @param extElement the extensibility element to remove
   */
  public void removeExtensibilityElement( ExtensibilityElement extElement )
  { m_listExtElements.remove( extElement ); }

  /**
   * Removes all extensibility elements
   */
  public void removeAllExtensibilityElements()
  { 
    
    for ( Iterator iExtElements = m_listExtElements.iterator(); iExtElements.hasNext(); )
    {
      Object objElement = iExtElements.next();
      
      if ( objElement instanceof ExtensibilityElement )
        iExtElements.remove();
      
    } // end for()
  
  } // end removeAllExtensibilityElements()

  
  /**
   * Removes all extensibility elements
   */
  public void removeAllExtensibilityElements( List listElements )
  { 
    
    for ( Iterator iExtElements = listElements.iterator(); iExtElements.hasNext(); )
    {
      Object objElement = iExtElements.next();
      
      if ( objElement instanceof ExtensibilityElement )
        iExtElements.remove();
      
    } // end for()
  
  } // end removeAllExtensibilityElements()
  

  /**
   * Gets all the extensibility elements defined.
   *
   * @return a List of all extensibility elements defined for this service
   */
  public List getExtensibilityElements()
  { 
    List listExtElements = new LinkedList();
    
    for ( Iterator iExtElements = m_listExtElements.iterator(); iExtElements.hasNext(); )
    {
      Object objElement = iExtElements.next();
      
      if ( objElement instanceof ExtensibilityElement )
        listExtElements.add( objElement );
      
    } // end for()
    
    return listExtElements;
    
  } // end getExtensibilityElements()

  
  /**
   * Gets all the extensibility elements defined.
   *
   * @return a List of all extensibility elements defined for this service
   */
  public List getExtensibilityElements( List listElements )
  { 
    List listExtElements = new LinkedList();
    
    for ( Iterator iExtElements = listElements.iterator(); iExtElements.hasNext(); )
    {
      Object objElement = iExtElements.next();
      
      if ( objElement instanceof ExtensibilityElement )
        listExtElements.add( objElement );
      
    } // end for()
    
    return listExtElements;
    
  } // end getExtensibilityElements()
  
  /**
   * Adds an unknown extensibility element for specific service extensions
   *
   * @param extElement the unknown extensibility element for specific service extensions
   */
  public void addUnknownExtensibilityElement( UnknownExtensibilityElement unknownExtElement )
  { m_listExtElements.add( unknownExtElement ); }

  
  /**
   * Removes the specified unknown extensibility element
   *
   * @param unknownExtElement the unknown extensibility element to remove
   */
  public void removeUnknownExtensibilityElement( UnknownExtensibilityElement unknownExtElement )
  { m_listExtElements.remove( unknownExtElement ); }


  
  /**
   * Removes all unknown extensibility elements
   */
  public void removeAllUnknownExtensibilityElements()
  {
    for ( Iterator iExtElements = m_listExtElements.iterator(); iExtElements.hasNext(); )
    {
      Object objElement = iExtElements.next();
      
      if ( objElement instanceof UnknownExtensibilityElement )
        iExtElements.remove();
      
    } // end for()
    
  } // end removeAllUnknownExtensibilityElements()

  /**
   * Removes all unknown extensibility elements
   */
  public void removeAllUnknownExtensibilityElements( List listElements )
  {
    for ( Iterator iExtElements = listElements.iterator(); iExtElements.hasNext(); )
    {
      Object objElement = iExtElements.next();
      
      if ( objElement instanceof UnknownExtensibilityElement )
        iExtElements.remove();
      
    } // end for()
    
  } // end removeAllUnknownExtensibilityElements()


  /**
   * Gets all the unknown extensibility elements defined.
   *
   * @return a List of all unknown extensibility elements defined for this service
   */
  public List getUnknownExtensibilityElements()
  {
    List listUnknownExtElements = new LinkedList();
    
    for ( Iterator iExtElements = m_listExtElements.iterator(); iExtElements.hasNext(); )
    {
      Object objElement = iExtElements.next();
      
      if ( objElement instanceof UnknownExtensibilityElement )
        listUnknownExtElements.add( objElement );
      
    } // end for()
    
    return listUnknownExtElements;
    
  } // end getUnknownExtensibilityElements()

  
  /**
   * Gets all the unknown extensibility elements defined.
   *
   * @return a List of all unknown extensibility elements defined for this service
   */
  public List getUnknownExtensibilityElements( List listElements )
  {
    List listUnknownExtElements = new LinkedList();
    
    for ( Iterator iExtElements = listElements.iterator(); iExtElements.hasNext(); )
    {
      Object objElement = iExtElements.next();
      
      if ( objElement instanceof UnknownExtensibilityElement )
        listUnknownExtElements.add( objElement );
      
    } // end for()
    
    return listUnknownExtElements;
    
  } // end getUnknownExtensibilityElements()
  
} // end class VwExtensibilityElementSupportImpl{}

// *** End of VwExtensibilityElementSupportImpl.java ***
