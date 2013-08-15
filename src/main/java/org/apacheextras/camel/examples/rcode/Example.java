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
    if (args.length > 0) {
      camelContext.addRoutes(new RCodeRouteBuilder(new File(args[0])));
    } else {
      camelContext.addRoutes(new RCodeRouteBuilder());
    }
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
