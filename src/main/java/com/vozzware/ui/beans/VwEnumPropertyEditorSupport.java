package com.vozzware.ui.beans;

import java.beans.PropertyEditorSupport;

public class VwEnumPropertyEditorSupport extends PropertyEditorSupport
{
  
  private String[]  m_astrTags = null;
  
  private Enum      m_eValue;
  private Enum[]    m_eValues;
  
  public VwEnumPropertyEditorSupport( Enum eValue ) throws Exception
  {
    
    m_eValues = eValue.getClass().getEnumConstants();
    
    m_astrTags = new String[ m_eValues.length ];
     
    for ( int x = 0; x < m_astrTags.length; x++ )
      m_astrTags[ x ] = m_eValues[ x ].toString();
  }
  
  public void setValue( Object objValue )
  { m_eValue = (Enum)objValue; }
  
  public String[] getTags()
  { return m_astrTags; }
  
  public String getAsText()
  {
    for ( int x = 0; x < m_eValues.length; x++ )
    {
      if ( m_eValues[ x ].equals( m_eValue ))
        return m_astrTags[ x ];
      
    }
    
    return "N/A";
    
  }
  public void setAsText( String strValue )
  {
    for ( int x = 0; x < m_astrTags.length; x++ )
    {
      if ( strValue.equals( m_astrTags[ x ] ))
      {
        m_eValue = Enum.valueOf( m_eValue.getClass(), m_astrTags[ x ] );
        firePropertyChange();
        return;
        
      }
      
    }
  }
  
  public Object getValue()
  { return m_eValue; }
  
}
