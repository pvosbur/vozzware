package com.vozzware.xml;

import java.util.Date;
import java.util.List;

/*
============================================================================================

    Source File Name: 

    Author:           petervosburgh
    
    Date Generated:   3/15/16

    Time Generated:   5:28 AM

============================================================================================
*/
public class VwPrimitiveListWrapper
{

   private List<String> m_listString;
   private List<Long>  m_listLong;
   private List<Integer>  m_listInt;
   private List<Double>  m_listDouble;
   private List<Date>  m_listDate;
   private List<Boolean>  m_listBoolean;

   public void setString( List<String>listString)
   { m_listString = listString; }

   public List<String> getString()
   { return m_listString; }

   public List<Long> getLong()
   {
     return m_listLong;
   }

   public void setLong( List<Long> listLong )
   {
     m_listLong = listLong;
   }

   public List<Integer> getInt()
   {
     return m_listInt;
   }

   public void setInt( List<Integer> listInt )
   {
     m_listInt = listInt;
   }

   public List<Double> getDouble()
   {
     return m_listDouble;
   }

   public void setDouble( List<Double> listDouble )
   {
     m_listDouble = listDouble;
   }

   public List<Date> getDate()
   {
     return m_listDate;
   }

   public void setDate( List<Date> listDate )
   {
     m_listDate = listDate;
   }

   public List<Boolean> getBoolean()
   {
     return m_listBoolean;
   }

   public void setBoolean( List<Boolean> listBoolean )
   {
     m_listBoolean = listBoolean;
   }

} // end VwPrimitiveListWrapper{}
