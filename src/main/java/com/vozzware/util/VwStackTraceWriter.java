/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwStackTraceWriter.java

============================================================================================
*/


package com.vozzware.util;


import java.io.PrintWriter;

/**
 * This class overrides the standard methods in the PrintWriter class to capture
 * the output of the printStackTrace( PrintWriter ) method in the Throwable class.
 * Since all exceptions derive from Throwable, this allows normal output to be captured
 * and redirected in the case where the code requires knowledge of the current state of
 * the stack.  To obtain the current call stack, instantiate an VwStackTraceWriter, and
 * call the getCallStack() method.  A string representing the call tree is then returned.
 */
public class VwStackTraceWriter extends PrintWriter
{
    /**
     * The combined output of the printStackTrace() method
     */

    private StringBuffer m_sbStackTrace = new StringBuffer();

 
    /**
     * The number of calls to the captureOutput method resulting in a linefeed.
     * This number represents the number of entries we'll return when getCallStack
     * is called.
     */

    private int    m_stackLines = 1;

    /**
     * Construct a StackTraceWriter object
     */
    public VwStackTraceWriter()
    {
        // *** although this method doesn't generate output, a valid stream is established
        // *** with the super class to guard against errors

        super( System.err, true );
    }

    /**
     * Capture any output normally destined to go to a PrintWriter, concatenate the output
     * into string form, to be used for final processing in the getCallStack() method.
     *
     * @param strText - A string with the text output
     * @param fNewline - If True, the println() method was called; False if it was not called.
     */
    private void captureOutput( String strText, boolean fNewline )
    {
      m_sbStackTrace.append( strText );

      if ( fNewline )
        m_sbStackTrace.append( "\n" );
      
    } // end captureOutput()


    /**
     * Overrides the standard close() method, so that the System.err output stream passed to
     * the super class during construction is not mistakenly closed.
     */
    public void close()
    {
      // No-op: Leave the file open
    }



    public void print(   boolean b ) { captureOutput( b ? "true" : "false", false ); }
    public void println( boolean b ) { captureOutput( b ? "true" : "false", true ); }
    public void print(   char c )    { captureOutput( String.valueOf( c ), false ); }
    public void println( char c )    { captureOutput( String.valueOf( c ), true ); }
    public void print(   int i )     { captureOutput( String.valueOf( i ), false ); }
    public void println( int i )     { captureOutput( String.valueOf( i ), true ); }
    public void print(   long l )    { captureOutput( String.valueOf( l ), false ); }
    public void println( long l )    { captureOutput( String.valueOf( l ), true ); }
    public void print(   float f )   { captureOutput( String.valueOf( f ), false ); }
    public void println( float f )   { captureOutput( String.valueOf( f ), true ); }
    public void print(   double d )  { captureOutput( String.valueOf( d ), false ); }
    public void println( double d )  { captureOutput( String.valueOf( d ), true ); }
    public void print(   char[] s )  { String tString = new String( s );
                                       captureOutput( tString, false ); }
    public void println( char[] s )  { String tString = new String( s );
                                       captureOutput( tString, true ); }
    public void print(   String s )  { captureOutput( s == null ? "null" : s, false ); }
    public void println( String s )  { captureOutput( s == null ? "null" : s, true ); }
    public void print(   Object o )  { captureOutput( o.toString(), false ); }
    public void println( Object o )  { captureOutput( o.toString(), true ); }


    public String toString()
    { return m_sbStackTrace.toString(); }


 } // end class VwStackTraceWriter



// *** End if VwStackTraceWriter.java ***
