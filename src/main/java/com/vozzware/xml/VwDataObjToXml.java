/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwDataObjToXml.java

Create Date: Apr 11, 2006
============================================================================================
*/

package com.vozzware.xml;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

import java.io.File;
import java.util.Iterator;

/**
 * This class converts a VwDataObject container to an XML document.
 */
public class VwDataObjToXml
{

  private boolean   m_fIgnoreNullData = false;   // Don't generate entries

  private boolean   m_fGenNullAttrForNulls = false; // Gen xsi:null atrribute for null date

  private String    m_strDataOnlyTag = null;        // If not null, only generate the data and not the tag name
  private int       m_nNestedTagCount = 0;


  /**
   * Generate an XML document from the contents of an VwDataObject container.
   *
   * @param strParent The Doc root tag. If omitted the user is expected to add the output
   * of this method to an exisitng document.
   *
   * @param dataObj The VwDataObject to build the XML document from
   *
   * @param strXMLDecl Optional - The XML declaration string that is used to declare an XML document.
   *
   * @param fFormatted if true, format the XML document with CR/LF and indentation based on tag parentage
   *
   * @param nIndentLevel The staing level of indentation - 0 being the default
   */
  public String toXml( String strParent, VwDataObject dataObj, String strXMLDecl,
                       boolean fFormatted, int nIndentLevel )
  {
    VwXmlWriter xmlf = new VwXmlWriter( fFormatted, nIndentLevel );

    if ( strXMLDecl != null )
      xmlf.addXml( strXMLDecl );

    processDataObject( strParent, dataObj, xmlf );

    return xmlf.getXml();

  } // end toXml


  /**
   * sets the name of the element tag for which only the tag data is generated (i.e don't
   * generate the tag name.
   */
  void genTagDataOnly( String strDataOnlyTag )
  { m_strDataOnlyTag = strDataOnlyTag; }


  /**
   * Sets the the ignore nulls property.
   *
   * @param fIgnoreNullData  If true, null data is not generated in the
   * xml document.<br>If false, an empty tag is generated with an attribute of xsi:null="true"
   * if the GenNullAttrForNulls pro[perty is set
   */
  public void setIgnoreNullData( boolean fIgnoreNullData )
  { m_fIgnoreNullData = fIgnoreNullData; }


  /**
   * Sets the the generation of the xsi:null attribute for null data
   *
   * @param fGenNullAttrForNulls  If true, and the IgnoreNullData property is false, then
   * the xsi:null atrribute will be place on any data value that is null
   */
  public void setGenNullAttrForNulls( boolean fGenNullAttrForNulls )
  { m_fGenNullAttrForNulls = fGenNullAttrForNulls; }

  /**
   * Process the contents of the data object
   */
  private void processDataObject( String strDataObjName, VwDataObject dataObj, VwXmlWriter xmlf )
  {
    // Iterate the map keys to build the xml document
    VwElementList nestedEleList = null;

    if ( strDataObjName != null )
    {

      Attributes listAttr = null;

      // See if there is an attribute element for this map name

      if ( dataObj.exists( strDataObjName ) )
      {
        VwElement element = null;

        Object objContent = dataObj.getObject( strDataObjName );

        if ( objContent instanceof VwElementList )
        {

          // This is caused by nested tags of the same name. The first
          // in the list are the attributes we want here

          nestedEleList = (VwElementList)objContent;
          element = nestedEleList.getElement( 0 );
          nestedEleList.remove( 0 );

        }
        else
          element = (VwElement)objContent;

        String strData = element.getValue();
        listAttr = element.getAttributes();

        if ( strData != null )
        {

          xmlf.addParent( strDataObjName, listAttr );
          String strIndentation = xmlf.getIndentation();

          xmlf.addXml( strIndentation + strData );
        }
        else
          xmlf.addParent( strDataObjName, listAttr );

      } // end if ( dataObj.exists( strDataObjName )
      else
        xmlf.addParent( strDataObjName );


    } // end if (strDataObjName != null )


    Iterator iKeys = dataObj.keys();

    while ( iKeys.hasNext() )
    {

      String strKey = (String)iKeys.next();
      
      if ( strKey.equals( strDataObjName ) )
        continue;

      Object objData = dataObj.getObject( strKey );

      if ( objData == null )
        continue;

      if ( objData instanceof VwDataObject )
        processDataObject( strKey, (VwDataObject)objData, xmlf );
      else
      if ( objData instanceof VwDataObjList )
        processList( strKey, (VwDataObjList)objData, xmlf );
      else
      if ( objData instanceof VwElementList )
        processElementList( strDataObjName, (VwElementList)objData, xmlf );
      else
        processElement( strDataObjName, (VwElement)objData, xmlf );

    } // end while()

    if ( nestedEleList != null )
    {
      ++m_nNestedTagCount;
      processElementList( strDataObjName, nestedEleList, xmlf );
      --m_nNestedTagCount;

    }

    if ( strDataObjName != null )
      xmlf.closeParent( strDataObjName );

  } // end processMap()


  /**
   * Process a list of VwDataObjects
   *
   * @param strKey The xml tag to genereate
   * @param list The list of VwDataObjects
   * @param xmlf The xml formatter
   */
  private void processList( String strKey, VwDataObjList list, VwXmlWriter xmlf )
  {
    Iterator iObj = list.iterator();

    while( iObj.hasNext() )
    {
      Object obj = iObj.next();

      processDataObject( strKey, (VwDataObject)obj, xmlf );

    } // end while()


  } // end processList()


  /**
   * Process a list of VwDataObjects
   *
   * @param strKey The xml tag to genereate
   * @param list The list of VwDataObjects
   * @param xmlf The xml formatter
   */
  private void processElementList( String strDataObjName, VwElementList list, VwXmlWriter xmlf )
  {
    Iterator iObj = list.iterator();

    while( iObj.hasNext() )
    {
      Object obj = iObj.next();

      processElement( strDataObjName, (VwElement)obj, xmlf );

    } // end while()


  } // end processElementList()


  /**
   * Process a single VwElement
   *
   * @param strDataObjName Name of data object tag this element is contained in
   * @param element The element to process
   *
   */
  private void processElement( String strDataObjName, VwElement element, VwXmlWriter xmlf )
  {
    String strEleName = element.getName();

    if ( m_nNestedTagCount == 0 )
    {
      if ( strEleName.equalsIgnoreCase( strDataObjName ) )
        return;     // Ignore this beacuse it's the attribute element already processed
    }


    if ( m_strDataOnlyTag != null && strEleName.equalsIgnoreCase( m_strDataOnlyTag ) )
    {
      xmlf.addXml( element.getValue() );
      return;

    }
    if ( element.getObject() == null )
    {

      AttributesImpl listAttr = (AttributesImpl)element.getAttributes();

      if ( m_fIgnoreNullData && listAttr == null && element.getChildObject() == null )
        return;     // Don't generate tag if this option is set

      if ( m_fGenNullAttrForNulls )
      {
        if ( listAttr == null )
        {
          listAttr = new AttributesImpl();
          element.setAttributes( listAttr );
        }

        int ndx = listAttr.getIndex( "null" );

        if ( ndx < 0 )
          listAttr.addAttribute( "", "null", "xsi:null", "CDATA", "true" );
        else
          listAttr.setValue( ndx, "true" );
      }
    } // end if

    Object elementChild = element.getChildObject();

    if ( elementChild != null )
    {
      if ( elementChild instanceof VwDataObjList )
        processList( strEleName, (VwDataObjList)elementChild, xmlf );
      else
        processDataObject( strEleName, (VwDataObject)elementChild, xmlf );

    }
    else
    {

      if ( m_nNestedTagCount > 0 && strEleName.equals( strDataObjName ) )
        return;

     // Gen element tag
      xmlf.addChild( strEleName, element.getValue(), element.getAttributes() );

    }

  } // end processElement()

  // For testing only
  public static void main( String[] args )
  {
    try
    {

      VwXmlToDataObj xtd = new VwXmlToDataObj( true, true );
      xtd.makeDataObjectsForParentTags();

      VwDataObject dobj = xtd.parse( new File( "\\itc\\ProvisionRequest.wsdl"), false );


      String strXml = dobj.toXml( "definitions", null, true, 0 );

      System.out.println( strXml );

      return;
    }
    catch( Exception e )
    {
      System.out.println( e.toString() );
    }

    return;



  } // end main
} // end class  VwDataObjToXml{}

// *** End of  VwDataObjToXml.java ***

