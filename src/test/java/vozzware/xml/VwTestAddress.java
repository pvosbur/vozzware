package test.vozzware.xml;/*
============================================================================================

                       V o z z W o r k s   C o d e   G e n e r a t o r                      

                                    Copyright(c) 2011 By                                    

                        V   o   z   z   w   a   r   e   L   L   C   .                       

                            A L L   R I G H T S   R E S E R V E D                           

    Source File Name: Transaction.java

    Author:           Vozzware LLC

    Date Generated:   04-23-2011

    Time Generated:   12:55:42

============================================================================================
*/

import java.util.List;

public class VwTestAddress
{
  private String m_strStreet;
  private String m_strCity;
  private String m_strState;
  
  private boolean m_fIsPrimary;

  private List<String>m_listPrimitives;

  private List<VwTestVehicles>m_listVehicles;

  public VwTestAddress()
  {
    ;

  }

  public VwTestAddress( String strStreet, String strCity, String strState, boolean fIsPrimary )
  {
    m_strStreet = strStreet;
    m_strCity = strCity;
    m_strState = strState;
    m_fIsPrimary = fIsPrimary;

  }

  public void setPrimitives( List<String> listPrimitives )
  {
    m_listPrimitives = listPrimitives;

  }

  public List<VwTestVehicles> getVwTestVehicles()
  {
    return m_listVehicles;
  }

  public void setVwTestVehicles( List<VwTestVehicles> listVehicles )
  {
    m_listVehicles = listVehicles;
  }

  public List<String>getPrimitives()
  {
    return m_listPrimitives;
  }

  public String getStreet()
  {
    return m_strStreet;
  }

  public void setStreet( String strStreet )
  {
    m_strStreet = strStreet;
  }

  public String getCity()
  {
    return m_strCity;
  }

  public void setCity( String strCity )
  {
    m_strCity = strCity;
  }

  public String getState()
  {
    return m_strState;
  }

  public void setState( String strState )
  {
    m_strState = strState;
  }

  public boolean getIsPrimary()
  {
    return m_fIsPrimary;
  }

  public void setIsPrimary( boolean fIsPrimary )
  {
    m_fIsPrimary = fIsPrimary;
  }
}
