/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

  THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
  PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
  CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

    Source File Name: VwKeyDescriptor.java

    Author:           Vw

    Date Generated:   09-07-2005

    Time Generated:   08:11:37

============================================================================================
*/

package com.vozzware.db.util;



public class VwObjectRelation
{

  private String                 m_strBeanProperty;              
  private String                 m_strCollectionClass;          
  private String                 m_strBeanClass;        

  // *** The following members set or get data from the class members *** 

  /**
   * Sets the beanProperty property
   * 
   * @param strbeanProperty
   */
  public void setBeanProperty( String strBeanProperty )
  { m_strBeanProperty = strBeanProperty;
 }

  /**
   * Gets beanProperty property
   * 
   * @return  The beanProperty property
   */
  public String getBeanProperty()
  { return m_strBeanProperty; }


  public String getBeanClass()
  { return m_strBeanClass; }
  
  
  public void setBeanClass( String strBeanClass )
  { m_strBeanClass = strBeanClass; }
  
  
  public String getCollectionClass()
  { return m_strCollectionClass; }
  
  
  public void setCollectionClass( String strCollectionClass )
  { m_strCollectionClass = strCollectionClass; }
  
} // *** End of class VwKeyDescriptor{}

// *** End Of VwKeyDescriptor.java