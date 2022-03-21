/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwPickListSelectionEvent.java

============================================================================================
*/

package com.vozzware.components;

/**
 * Sent when an VwPickList item has been selected or deselected
 */
public class VwPickListSelectionEvent
{
  private VwPickList  m_plSource;    // The object invokinjg this event

  private String    m_strItem;        // The item name affected by the event
  private boolean   m_fIsSelected;    // The selection state of the item
  private int       m_nItemNbr;
  

  /**
   * Constructor
   *
   * @param plSource The VwPickList source list
   * @param strItem  The item that changed
   * @param fIsSelected  The selection state
   */
  public VwPickListSelectionEvent( VwPickList plSource, String strItem, boolean fIsSelected, int nItemNbr )
  {
    m_plSource = plSource;
    m_strItem = strItem;
    m_nItemNbr = nItemNbr;
    
    m_fIsSelected = fIsSelected;

  } // end VwPickListSelectionEvent()


  /**
   * Gets the source object invoking this event
   * @return
   */
  public VwPickList getSource()
  {
    return m_plSource;
  }

  /**
   * Sets the source object invoking this event
   * @param plSource  The VwPickList source object
   */
  public void setPlSource( VwPickList plSource )
  {
    m_plSource = plSource;
  }

  /**
   * Gets the name of the name that changed state
    * @return
   */
  public String getItem()
  {
    return m_strItem;
  }


  /**
   * Sets the name of the name that changed state
   * @param strItem  The item name that changed selection state
   */
  public void setItem( String strItem )
  {
    m_strItem = strItem;
  }
  
  public int getItemNbrSelected()
  { return  m_nItemNbr; }
  

  /**
   * Returns the selection state of the named item
   * @return
   */
  public boolean isSelected()
  {
    return m_fIsSelected;
  }


  /**
   * Sets the selection state of the named item
   * @param fIsSelected The selection state
   */
  public void setSelected( boolean fIsSelected )
  {
    m_fIsSelected = fIsSelected;
  }
} // end class VwPickListSelectionEvent{}

// ** End of VwPickListSelectionEvent.java ***

