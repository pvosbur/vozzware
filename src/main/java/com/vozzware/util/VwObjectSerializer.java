package com.vozzware.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/*
============================================================================================

    Source File Name: VwObjectSerializer

    Author:           petervosburgh
    
    Date Generated:   2/12/15

    Time Generated:   9:38 AM

============================================================================================
*/

/**
 * This class handles Object serialization/de-serialization
 */
public class VwObjectSerializer
{
  /**
   * Serialize an object to a byte array
   *
   * @param objToSerialize  The object to serialize
   * @return
   * @throws Exception
   */
  public static byte[] toByteArray( Object objToSerialize )  throws Exception
  {
     ByteArrayOutputStream baos = new ByteArrayOutputStream();
     ObjectOutputStream oos = new ObjectOutputStream( baos );
     oos.writeObject( objToSerialize );
     oos.close();

     return baos.toByteArray();

   }

  /**
   * De-serialize a byte array back to an Object
   * @param abSerializedObject
   * @return
   * @throws Exception
   */
   public static Object fromByteArray( byte[] abSerializedObject )   throws Exception
   {
     return fromByteArray( abSerializedObject, 0, abSerializedObject.length );

   }


  /**
   * De-serialize a byte array back to an Object
   * @param abSerializedObject
   * @return
   * @throws Exception
   */
   public static Object fromByteArray( byte[] abSerializedObject, int nOffset, int nLength )   throws Exception
   {
     ByteArrayInputStream bais = new ByteArrayInputStream( abSerializedObject, nOffset, nLength );

     ObjectInputStream ois = new ObjectInputStream( bais );

     return ois.readObject();

   }

}
