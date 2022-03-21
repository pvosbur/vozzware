/*
============================================================================================

                        V o z z W o r k s   C o d e   G e n e r a t o r                       

                              2009 by V o z z W a r e   L L C                              

    Source File Name: Snippet.java

    Author:           

    Date Generated:   05-28-2008

    Time Generated:   07:10:41

============================================================================================
*/

package com.vozzware.db.util;

import com.vozzware.db.VwDVOBase;
import com.vozzware.util.VwBeanUtils;

import java.io.Serializable;


public class Snippet extends VwDVOBase implements Serializable, Cloneable
{

  private String                 m_strType;                      
  private String                 m_strCode;                      

  /**
   * Renders bean instance property values to a String
   * 
   * @return  A String containing the bean property values
   */
  public String toString()
  {
    return VwBeanUtils.dumpBeanValues( this );
  } // End of toString()



  // *** The following members set or get data from the class members *** 

  /**
   * Sets the type property

   */
  public void setType( String strType )
  { 
    
    testDirty( "type", strType );
    m_strType = strType;
  }

  /**
   * Gets type property
   * 
   * @return  The type property
   */
  public String getType()
  { return m_strType; }

  /**
   * Sets the code property

   */
  public void setCode( String strCode )
  { 
    
    testDirty( "code", strCode );
    m_strCode = strCode;
  }

  /**
   * Gets code property
   * 
   * @return  The code property
   */
  public String getCode()
  { return m_strCode; }

  /**
   * Clones this object
   *
   */
  public Object clone()
  {
    Snippet classClone = new Snippet();
    
    classClone.m_strType = m_strType;
    classClone.m_strCode = m_strCode;

    return classClone;
  }



  /**
   * Performs deep equal test on this object
   *
   * @param objTest The object to compare this object to
   *
   * @return if the two objects are equal, false otherwise
   *
   */
  public boolean equals( Object objTest )
  {

    if ( objTest == null )
      return false;

    if ( this.getClass() != objTest.getClass() )
      return false;

    Snippet objToTest = (Snippet)objTest;

    if ( ! doObjectEqualsTest( m_strType, objToTest.m_strType ) )
      return false; 

    if ( ! doObjectEqualsTest( m_strCode, objToTest.m_strCode ) )
      return false; 

    return true;
  }



  /**
   * Perform an equals test on an Object
   *
   * @param obj1 first object
   * @param obj2 second object
   *
   * @return true if objects are equal, false otherwise
   *
   */
  private boolean doObjectEqualsTest( Object obj1, Object obj2 )
  {
    if ( obj1 != null )
    {
      if ( obj2 == null )
        return false;
      return obj1.equals( obj2 );
    }
    else
    if ( obj2 != null )
      return false;

    return true;

  }
} // *** End of class Snippet{}

// *** End Of Snippet.java