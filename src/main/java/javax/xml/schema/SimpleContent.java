/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: SimpleContent.java

============================================================================================
*/
package javax.xml.schema;

public interface SimpleContent extends SchemaCommon
{
  public Extension getExtension();

  public Restriction getRestriction();

  public void setExtesion( Extension extension );

  public void setRestriction( Restriction restriction );

  public java.util.List getContent();

  public String getType();
  
} // end inetrface SimpleContent{}

// *** end of SimpleContent.java ***
