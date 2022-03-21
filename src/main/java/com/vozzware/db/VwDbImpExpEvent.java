/*
 ============================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

  THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
  PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
  CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

  Source Name:   VwDbImpExpEvent.java


 ============================================================================
*/


package com.vozzware.db;                         // Our package

/**
 * This class describes the inport/export event action that is occuring.
 * This can be evry time a new schema is involved and/or when a row of data
 * is beeing inported or exported.
 */
public class VwDbImpExpEvent
{

  /**
   * The EXPORT event type constatnt
   */
  public static final int EXPORT = 1;

  /**
   * The IMPORT event type constatnt
   */
  public static final int IMPORT = 2;


  /**
   * The NEWSCHEMA action type constatnt
   */
  public static final int NEWSCHEMA = 1;


  /**
   * The NEWSCHEMA action type constatnt
   */
  public static final int NEWTABLE = 2;

  /**
   * The NEWROW action type constatnt
   */
  public static final int NEWROW = 3;


  private int         m_nEventType;   // Event type IMPORT or EXPORT
  private int         m_nActionType;  // Action type - schems change or row change

  private String      m_strDesc;      // A description of the action. This is only valid
                                      // for a NEWSCHEMA * NEWTABLE action types. Its is
                                      // the new schema or table name

  /**
   * Consructs this event
   *
   * @param nEventType The Event type IMPORT/EXPORT
   * @param nActionType The action type NEWSCHEMA,NEWTABLE or NEWROW
   * @param strDesc The name of the schema that is beeing used
   */
  VwDbImpExpEvent( int nEventType, int nActionType, String strDesc )
  {
     m_nEventType = nEventType;
     m_nActionType = nActionType;
     m_strDesc = strDesc;

  } // end Constructor  VwDbImpExpEvent()


  /**
   * Gets the event type
   *
   * @return The event type
   */
  public final int getEventType()
  { return m_nEventType; }


  /**
   * Gets the action type
   *
   * @return The action type
   */
  public final int getActionType()
  { return m_nActionType; }


  /**
   * Gets the action description for NEWSCHEMA
   *
   * @return The new schema name or null for a NEWROW action
   */
  public final String getDesc()
  { return m_strDesc; }

} // end class VwDbImpExpEvent{}

// *** End of VwDbImpExpEvent.java ***  

