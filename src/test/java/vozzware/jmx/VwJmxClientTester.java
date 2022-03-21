package test.vozzware.jmx;

import org.junit.Test;

import javax.management.Attribute;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import javax.naming.Context;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;


/*
============================================================================================

    Source File Name: 

    Author:           petervosburgh
    
    Date Generated:   2/3/16

    Time Generated:   5:50 AM

============================================================================================
*/
public class VwJmxClientTester
{
  @Test
  public void testJmxClient() throws Exception
  {

    Map<String,String>env = new HashMap<>(  );
    env.put( Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.rmi.registry.RegistryContextFactory");


    echo("\nCreate an RMI connector client and " +
         "connect it to the RMI connector server");
    JMXServiceURL url =
        new JMXServiceURL("service:jmx:rmi://armordevweb1/jndi/rmi://armordevweb1://localhost:1099/jmxrmi");
    JMXConnector jmxc = JMXConnectorFactory.connect(url, env );

    // Create listener
    //

    // Get an MBeanServerConnection
    //
    echo("\nGet an MBeanServerConnection");
    MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();

    // Get domains from MBeanServer
    //
    echo("\nDomains:");
    String domains[] = mbsc.getDomains();
    Arrays.sort(domains);
    for (String domain : domains) {
        echo("\tDomain = " + domain);
    }
    // Get MBeanServer's default domain
    //
    echo("\nMBeanServer default domain = " + mbsc.getDefaultDomain());


    echo("\nMBeanServer default domain = " + mbsc.getDefaultDomain());

    echo("\nMBean count = " +  mbsc.getMBeanCount());
    echo("\nQuery MBeanServer MBeans:");
    Set<ObjectName> names =
        new TreeSet<ObjectName>(mbsc.queryNames(null, null));
    for (ObjectName name : names) {
        echo("\tObjectName = " + name);
    }


    ObjectName mxbeanName = new ObjectName ("com.vozzware.util:name=vwLoggerJMX,type=VwLoggerJMX");

    MBeanInfo mbeanInfo = mbsc.getMBeanInfo(mxbeanName );
    MBeanAttributeInfo[] mbeanAttributeInfos = mbeanInfo.getAttributes();

    Attribute attr = new Attribute( "CurrentLogger", "aiLogger.properties" );

    mbsc.invoke( mxbeanName, "setCurrentLogger", new String[]{"aiLogger.properties"}, null );

    attr = new Attribute( "LevelAsString", "debug" );

    mbsc.setAttribute( mxbeanName, attr );

    String strLevel = (String)mbsc.getAttribute( mxbeanName, "LevelAsString" );


    return;

  }

  private static void echo(String msg) {
      System.out.println(msg);
  }

  private static void sleep(int millis) {
      try {
          Thread.sleep(millis);
      } catch (InterruptedException e) {
          e.printStackTrace();
      }
  }

  private static void waitForEnterPressed() {
      try {
          echo("\nPress <Enter> to continue...");
          System.in.read();
      } catch (IOException e) {
          e.printStackTrace();
      }
  }

}

