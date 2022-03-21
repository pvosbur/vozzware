/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwTableToJava.java

============================================================================================
*/

package com.vozzware.tools;

import com.vozzware.db.VwColInfo;
import com.vozzware.db.VwDatabase;
import com.vozzware.xml.VwAttributes;
import com.vozzware.xml.VwDataObjList;
import com.vozzware.xml.VwDataObject;
import com.vozzware.xml.VwElement;
import org.xml.sax.helpers.AttributesImpl;

import java.sql.Types;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

public class VwTableToJava
{

  VwDataObject   m_dobjSchema;               // DataObject to hold parsed schema

  private static ResourceBundle  s_msgs = ResourceBundle.getBundle( "com.vozzware.xml.dtd.msgs" );

  /**
   * Make a schema complexType defibition for each table name in the table name array.
   */
  public static VwDataObjList makeSchemaType( String strSchema, String[] astrTables,
                                               VwDatabase db ) throws Exception
  {
    VwDataObjList dobjList = new VwDataObjList();



    for ( int x = 0; x < astrTables.length; x++ )
    {

      String strTable = astrTables[ x ];

      List listCols = db.getColumns( null, strSchema, strTable );

      if ( listCols.size() == 0 )
        continue;

      VwDataObject dobjComplexType = new VwDataObject(  true, true );               // DataObject to hold parsed schema
      dobjComplexType.add( new VwElement( "xsd:complexType", null, new VwAttributes( "name", strTable ) ) );
      VwDataObject dobjSequence = new VwDataObject( true, true );
      dobjComplexType.add( "xsd:sequence", dobjSequence );

      for ( Iterator iCols = listCols.iterator(); iCols.hasNext(); )
      {

        VwColInfo colInfo = (VwColInfo)iCols.next();
        AttributesImpl attrs = new AttributesImpl();

        VwElement element = new VwElement( "xsd:element", null, attrs );

        attrs.addAttribute( "", "name", "name", "CDATA", colInfo.getColumnName() );
        attrs.addAttribute( "", "type", "type", "CDATA", convertType( colInfo ) );

        dobjSequence.add( element );

      }

      dobjList.add( dobjComplexType );
    }

    return dobjList;
  }


  private static String convertType( VwColInfo colInfo )
  {
    switch( colInfo.getSQLType() )
    {
      case Types.CHAR:
      case Types.VARCHAR:
      case Types.DATE:
      case Types.TIME:
      case Types.TIMESTAMP:

           return "xsd:string";

      case Types.DECIMAL:
      case Types.DOUBLE:
      case Types.FLOAT:

           return "xsd:double";

      case Types.BIGINT:
      case Types.INTEGER:
      case Types.TINYINT:

           return "xsd:integer";

    } // end switch()

    return "xsd:string";
  }

} // end class VwTableToJava{}

// *** End VwTableToJava.java ***
