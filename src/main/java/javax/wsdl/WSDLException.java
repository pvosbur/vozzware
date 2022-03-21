/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: WSDLCommon.java

============================================================================================
*/
package javax.wsdl;

import java.io.PrintWriter;
import java.io.StringWriter;

public class WSDLException extends Exception
{
  public static final long serialVersionUID = 1;

  public static final String INVALID_WSDL = "INVALID_WSDL";
  public static final String PARSER_ERROR = "PARSER_ERROR";
  public static final String OTHER_ERROR = "OTHER_ERROR";
  public static final String CONFIGURATION_ERROR = "CONFIGURATION_ERROR";

  private String faultCode = null;
  private Throwable targetThrowable = null;
  private String location = null;

  public WSDLException(String faultCode, String msg, Throwable t)
  {
    super(msg);
    setFaultCode(faultCode);
    setTargetException(t);
  }

  public WSDLException(String faultCode, String msg)
  {
    this(faultCode, msg, null);
  }

  public void setFaultCode(String faultCode)
  {
    this.faultCode = faultCode;
  }

  public String getFaultCode()
  {
    return faultCode;
  }

  public void setTargetException(Throwable targetThrowable)
  {
    this.targetThrowable = targetThrowable;
  }

  public Throwable getTargetException()
  {
    return targetThrowable;
  }

  /**
   * Set the location using an XPath expression. Used for error messages.
   *
   * @param location an XPath expression describing the location where
   * the exception occurred.
   */
  public void setLocation(String location)
  {
    this.location = location;
  }

  /**
   * Get the location, if one was set. Should be an XPath expression which
   * is used for error messages.
   */
  public String getLocation()
  {
    return location;
  }

  public String getMessage()
  {
    StringBuffer strBuf = new StringBuffer();

    strBuf.append("WSDLException");

    if (location != null)
    {
      try
      {
        strBuf.append(" (at " + location + ")");
      }
      catch (IllegalArgumentException e)
      {
      }
    }

    if (faultCode != null)
    {
      strBuf.append(": faultCode=" + faultCode);
    }

    String thisMsg = super.getMessage();
    String targetMsg = (targetThrowable != null)
                       ? targetThrowable.getMessage()
                       : null;

    if (thisMsg != null
        && (targetMsg == null || !thisMsg.equals(targetMsg)))
    {
      strBuf.append(": " + thisMsg);
    }

    if (targetMsg != null)
    {
      strBuf.append(": " + targetMsg);
    }

    return strBuf.toString();
  }

  public String toString()
  {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);

    pw.print(getMessage() + ": ");

    if (targetThrowable != null)
    {
      targetThrowable.printStackTrace(pw);
    }

    return sw.toString();
  }
}