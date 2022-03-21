package com.vozzware.codegen;

/**
 * This class describes a DVO property
 * @author petervosburghjr
 *
 */
public class VwPropertyDefinition
{
  private String       m_strName;
  private String       m_strUserType;
  private DataType     m_eDataType;
  private int          m_nArraySize;
  private String       m_strInitialValue;
  
  public VwPropertyDefinition()
  { ; }
  
  public VwPropertyDefinition( String strName, DataType eDataType, String strUserType )
  {
    m_strName = strName;
    m_eDataType = eDataType;
    m_strUserType = strUserType;
  }
  
  public String getName()
  { return m_strName; }
  
  public void setName( String strName )
  { m_strName = strName; }
  
  
  public String getUserType()
  { return m_strUserType; }
  
  
  public void setUserType( String strUserType )
  { m_strUserType = strUserType; }
  
  
  public DataType getDataType()
  {  return m_eDataType;  }
  
  
  public void setDataType( DataType eDataType )
  {  m_eDataType = eDataType;  }

  public int getArraySize()
  { return m_nArraySize; }

  
  public void setArraySize( int nArraySize )
  { m_nArraySize = nArraySize;  }

  public String getInitialValue()
  { return m_strInitialValue; }

  public void setInitialValue( String initialValue )
  {  m_strInitialValue = initialValue;  }
  
  
  
} // end VwPropertyDefinition{}

// *** End of VwPropertyDefinition.java ***

