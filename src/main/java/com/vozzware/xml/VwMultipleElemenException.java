package com.vozzware.xml;

/**
 * Exception for MultipleElements
 */
public class VwMultipleElemenException extends Exception
{
   public VwMultipleElemenException( String strId )
   {
     super( "Multple elements exist for element name: " + strId + ", use getElement( index, name ) instead");
   }
}
