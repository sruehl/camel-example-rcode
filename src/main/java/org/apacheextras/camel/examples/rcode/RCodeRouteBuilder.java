/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.apacheextras.camel.examples.rcode;

import org.apache.camel.builder.RouteBuilder;

/**
 *
 * @author cemmersb
 */
public class RCodeRouteBuilder extends RouteBuilder {

  @Override
  public void configure() throws Exception {
    configureCsvRoute();
  }
  
  private void configureCsvRoute() {
    // TODO: Configure this route with meaningfull parameters
    from("file://")
        .marshal().csv()
        .to("mock://testEndoint");
  }
  
}
