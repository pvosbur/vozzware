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

public class VwTestVehicles
{
  private String m_strType;
  private String m_strMake;

  public VwTestVehicles()
  {
    ;
  }
  public VwTestVehicles( String strType, String strMake )
  {
    m_strType = strType;
    m_strMake = strMake;
  }
  public String getType()
  {
    return m_strType;
  }

  public void setType( String strType )
  {
    m_strType = strType;
  }

  public String getMake()
  {
    return m_strMake;
  }

  public void setMake( String strMake )
  {
    m_strMake = strMake;
  }
}
