package org.apacheextras.camel.examples.rcode;

import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;

import java.util.concurrent.TimeUnit;

/**
 * @author Sebastian Rühl
 */
public class Example {

  public static void main(String... args) throws Exception {
    CamelContext camelContext = new DefaultCamelContext();
    camelContext.addRoutes(new RCodeRouteBuilder());
    camelContext.start();
    //TODO: change to a better method
    TimeUnit.SECONDS.sleep(5);
    camelContext.stop();
  }
}
