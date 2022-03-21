package com.vozzware.db.util;

import com.vozzware.util.VwStack;

/*
============================================================================================

    Source File Name: Transaction.java

    Author:           petervosburgh
    
    Date Generated:   12/1/17

    Time Generated:   4:18 PM

============================================================================================
*/

/**
 * This class encapsulates The ORM VwExtendsDescriptor and adds a hierarchy stack of super classes (extended tables )
 * This is used to build sql select statemens to build table joins of the table extends hierarchy.
 */
public class VwExtendsHierarchyDescriptor
{
  private VwExtendsDescriptor m_extendsDescriptor;

  private VwStack<VwDbObjCommon> m_stackTableHierarchy = new VwStack<>(VwDbObjCommon.class);

  public VwExtendsHierarchyDescriptor( VwExtendsDescriptor extendsDescriptor )
  {
    m_extendsDescriptor = extendsDescriptor;

  }


  public VwExtendsDescriptor getExtendsDescriptor()
  {
    return m_extendsDescriptor;

  }
  public VwStack<VwDbObjCommon>getStackHierarchy()
  {
    return m_stackTableHierarchy;
  }

} // end VwExtendsHierarchyDescriptor
