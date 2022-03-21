package com.vozzware.ui.beans;

import com.vozzware.util.VwExString;

import java.beans.PropertyEditorSupport;

public class VwTagsPropertyEditorSupport<T> extends PropertyEditorSupport
{
  
  private String[]  m_astrTags = null;
  private T[]       m_agtValues;
  
  private T         m_gtValue;
  
  /**
   * This class provides property editor tags that will result in a combobox dropdown of choices
   * in a swing design tools property sheet
   * @param astrTags The array of property choices that will be displayed in the combobox dropdown.
   * <br>These can also be passed as ${bundleName} entries where "bundleName" is a key in a previously loaded
   * <br>resource bundle
   * @param agtValues The corresponding array of values.
   */
  public VwTagsPropertyEditorSupport( String[] astrTags, T[] agtValues )
  {
    m_astrTags = astrTags;
    
    for ( int x = 0; x < m_astrTags.length; x++ )
      m_astrTags[ x ] = VwExString.expandMacro( m_astrTags[ x ] );
    
    m_agtValues = agtValues;
    
    if ( m_agtValues instanceof String[] )
    {
      for ( int x = 0; x < m_agtValues.length; x++ )
        m_agtValues[ x ] = (T)VwExString.expandMacro( (String)m_agtValues[ x ] );
     
    }
    
  }
  
  public void setValue( Object objValue )
  { m_gtValue = (T)objValue; }
  
  public String[] getTags()
  { return m_astrTags; }
  
  public String getAsText()
  {
    for ( int x = 0; x < m_agtValues.length; x++ )
    {
      if ( m_agtValues[ x ].equals( m_gtValue ))
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
        m_gtValue = m_agtValues[ x ];
        firePropertyChange();
        return;
        
      }
      
    }
  }
  
  public Object getValue()
  { return m_gtValue; }
  
}
