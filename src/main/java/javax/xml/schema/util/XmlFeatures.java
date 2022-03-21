/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: XmlFeatures.java

============================================================================================
*/
package javax.xml.schema.util;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface XmlFeatures
{
  /**
   * Constant for the xml parsing validation
   */
  public static final String VALIDATE = "http://jschema.org/features/validate";
  
  /**
   * Constant for the Atrribute m_btModel deserialization type
   */
  public static final String ATTRIBUTE_MODEL = "http://jschema.org/features/attribueModel";

  /**
   * Constant for the Atrribute m_btModel deserialization type
   */
  public static final String USE_NAMESPACES = "http://jschema.org/features/useNamespaces";
  
  
  public static final String EXPAND_MACROS = "http://jschema.org/features/expandMacros";
  
  /**
   * Sets a behavioural attribute for the deSerializer
   * @param strURIFeature The uri of the feature to set
   * @param fEnable if true enable the feature, else disable the feature
   * 
   * @throws Exception If the feature requested is not valid
   */
  public void setFeature( String strURIFeature, boolean fEnable ) throws Exception;
  
  
} // end interface XmlFeatures{}

// *** End of XmlFeatures.java ***

