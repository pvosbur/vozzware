package test.vozzware.util;

import com.vozzware.util.VwFileFilterHandler;
import com.vozzware.util.VwJar;
import com.vozzware.util.VwNameWildcardMatcher;
import org.junit.Test;

import java.io.File;

//import static com.amazonaws.services.sagemaker.model.TrainingInputMode.File;

/*
============================================================================================

    Source File Name: Transaction.java

    Author:           petervosburgh
    
    Date Generated:   12/6/21

    Time Generated:   12:04 PM

============================================================================================
*/
public class VwJarTester
{
  @Test
  public void testFindClasses()
  {
    try
    {
      /*
      java.io.File dir = new File( "/Users/petervosburgh/dev/tmp");

      VwFileFilterHandler fh = new VwFileFilterHandler( "*.class"  );
      boolean fAccept = fh.accept( dir, "App.java");
      fAccept = fh.accept( dir, "CustomerModel.class");
      fAccept = fh.accept( dir, "CustomerModelKt.class");
      fAccept = fh.accept( dir, "TestJS_WKWebView-master.zip");

      int jjj = 1;
      */


      //VwJar jar = new VwJar();
      // /Users/petervosburgh/dev/tmp
      String[] astrArgs = new String[]{"-cv", "-R", "-f", "pbv99.jar", "-C", ".", "-mf", "*.jar"};
      //String[] astrArgs = new String[]{"-t", "-f", "/Users/petervosburgh/dev/KotlinJarTest/build/classes/java/main/pbv.jar"};

      VwJar.main( astrArgs );

      //jar.exec( astrArgs );

    }
    catch( Exception ex )
    {
      ex.printStackTrace();
    }
  }
}
