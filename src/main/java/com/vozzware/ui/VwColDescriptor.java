/*
============================================================================================

                       V o z z W o r k s   C o d e   G e n e r a t o r

                                    Copyright(c) 2016 By

                        V   o   z   z   w   a   r   e   L   L   C   .

                            A L L   R I G H T S   R E S E R V E D

    Source File Name: VwColDescriptor.java

    Author:           Armored Info LLC

    Date Generated:   04-3-2016

============================================================================================
*/
package com.vozzware.ui;

public class VwColDescriptor
{
  public static enum ColType{ img, string };

  private String m_strTdInlineCss;
  private String m_strImgInlineCss;
  private ColType m_colType;

  public VwColDescriptor( ColType colType )
  {
    m_colType = colType;
  }

  public String getTdInlineCss()
  {
    return m_strTdInlineCss;
  }

  public void setTdInlineCss( String strTdInlineCss )
  {
    m_strTdInlineCss = strTdInlineCss;
  }

  public ColType getColType()
  {
    return m_colType;
  }

  public String getImgInlineCss()
  {
    return m_strImgInlineCss;
  }

  public void setImgInlineCss( String strImgInlineCss )
  {
    m_strImgInlineCss = strImgInlineCss;
  }
}
