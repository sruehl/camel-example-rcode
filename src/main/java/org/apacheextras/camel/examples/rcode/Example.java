package org.apacheextras.camel.examples.rcode;

import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;

import java.io.Console;
import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * @author Sebastian Rühl
 */
public class Example {

  public static void main(String... args) throws Exception {
    CamelContext camelContext = new DefaultCamelContext();
    File basePath = args.length > 0 ? new File(args[0]) : new File(System.getProperty("user.dir") + "./rcode-example/data");

    camelContext.addRoutes(new RCodeRouteBuilder(basePath));

    camelContext.start();
    Console console = System.console();
    if (console != null) {
      console.printf("Please press enter to shutdown route.");
      console.readLine();
    } else {
      TimeUnit.SECONDS.sleep(5);
    }
    camelContext.stop();
  }
}
