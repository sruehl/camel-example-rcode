/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.apacheextras.camel.examples.rcode;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dataformat.csv.CsvDataFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author cemmersb
 */
public class RCodeRouteBuilder extends RouteBuilder {
  
  private static final Logger LOGGER = LoggerFactory.getLogger(RCodeRouteBuilder.class);
  
  @Override
  public void configure() throws Exception {
    configureCsvRoute();
  }

  private void configureCsvRoute() {
    CsvDataFormat csv = new CsvDataFormat();
    csv.setDelimiter(";");
    csv.setSkipFirstLine(true);

    String path = System.getProperty("user.dir");
    String url = "file://" + path + "/src/main/resources/data?noop=TRUE";
    from(url)
        .process(new Processor() {
          @Override
          public void process(Exchange exchng) throws Exception {
            LOGGER.info("Exchange: {}", exchng);
          }
        })
        .log("Unmarshalling CSV file.")
        .unmarshal(csv)
        .to("log:CSV?level=INFO");
  }
}
