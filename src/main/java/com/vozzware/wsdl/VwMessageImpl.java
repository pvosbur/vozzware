/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwMessageImpl.java

Create Date: Apr 11, 2006
============================================================================================
*/
package com.vozzware.wsdl;

import javax.wsdl.Message;
import javax.wsdl.Part;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


/**
 * This represents the WSDL message element
 *
 * @author Peter VosBurgh
 */
public class VwMessageImpl extends VwWSDLCommonImpl implements Message
{
   private List m_listParts = new LinkedList();

  /**
   * Adds a part to this message.
   *
   * @param part the part to be added
   */
  public void addPart( Part part )
  { m_listParts.add( part ); }


  /**
   * Removes part from this message.
   *
   * @param part the part to be removed
   */
  public void removePart( Part part )
  { m_listParts.remove( part ); }


  /**
   * Removes a Psrt by its name
   *
   * @param strPartName The name of the part to remove
   */
  public void removePart( String strPartName  )
  {
    for ( Iterator iParts = m_listParts.iterator(); iParts.hasNext(); )
    {
      Part part = (Part)iParts.next();

      if ( part.getName().equalsIgnoreCase( strPartName ) )
      {
        iParts.remove();
        return;
      }
    }
  }

  /**
   * Removes all parts from this message.
   */
  public void removeAllParts()
  { m_listParts.clear(); }


  /**
   * Gets the specified part.
   *
   * @param strPartName the name of part to retrieve
   *
   * @return the corresponding part, or null if there wasn't
   * any matching part
   */
  public Part getPart( String strPartName )
  {
    for ( Iterator iParts = m_listParts.iterator(); iParts.hasNext(); )
    {
       Part part = (Part)iParts.next();

       if ( part.getName().equalsIgnoreCase( strPartName ) )
        return part;
     }

     return null;   // No match
  }

  /**
   * Gets a List the parts defined here.
   */
  public List getParts()
  { return m_listParts; }


  /**
   * Gets a List of all Types content
   *
   * @return a List of all Types content
   */
  public List getContent()
  {
    List listContent = new LinkedList();

    if ( this.getDocumentation() != null )
     listContent.add( this.getDocumentation() );

    listContent.addAll( m_listParts );

    return listContent;

  } // end getContent()


} // end class VwMessageImpl{}

/// *** End of VwMessageImpl.java ***
