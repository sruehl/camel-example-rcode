/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.apacheextras.camel.examples.rcode;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dataformat.csv.CsvDataFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * @author cemmersb
 */
public class RCodeRouteBuilder extends RouteBuilder {

  private static final Logger LOGGER = LoggerFactory.getLogger(RCodeRouteBuilder.class);

  private File basePath;

  public RCodeRouteBuilder(File basePath) {
    this.basePath = basePath;
  }

  @Override
  public void configure() throws Exception {
    configureCsvRoute();
  }

  private void configureCsvRoute() {
    CsvDataFormat csv = new CsvDataFormat();
    csv.setDelimiter(";");
    csv.setSkipFirstLine(true);

    from(basePath.toURI() + "?noop=TRUE")
            .process(new Processor() {
              @Override
              public void process(Exchange exchange) throws Exception {
                LOGGER.info("Exchange: {}", exchange);
              }
            })
            .log("Unmarshalling CSV file.")
            .unmarshal(csv)
            .to("log:CSV?level=INFO")
            .split()
              .body()
              .to("log:CSV?level=TRACE")
              .setBody(simple("${body[1]}"))
              .to("log:CSV?level=TRACE")
            .end()
            .log(LoggingLevel.INFO, "Finished a run");
  }
}
