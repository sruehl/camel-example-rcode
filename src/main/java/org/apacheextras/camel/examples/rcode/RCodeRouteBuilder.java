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

  /**
   * Takes an incoming string argument containing monthly quantities and
   * generates an output graph.
   */
  private void configureRCodeRoute() {
    
    final String command = "plot(quantity, type=l);";
    
    from("direct:rcode")
        .process(new Processor() {
          @Override
          public void process(Exchange exchng) throws Exception {
            // TODO: Evaluate if the parameters are actually of the format
            // 313, 175, 538, 370, 311, 61, 271, 367, 360, 286, 346, 170, 288
            final String parameters = exchng.getIn().getBody(String.class);
            final StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("quantity <- c(");
            stringBuilder.append(parameters);
            stringBuilder.append(");\n");
            stringBuilder.append(command);
            exchng.getIn().setBody(stringBuilder.toString());
          };
        })
        .to("rcode://localhost:6311/parse_and_eval?bufferSize=4194304")
        // TODO: Write the output array coming from the REXPList into a file
        .end()
        .log(LoggingLevel.INFO, "Generated ");
  }

  /**
   * Configures a CSV route that reads the quantity values from the route and
   * sends the result to the RCode route.
   */
  private void configureCsvRoute() {
    // Configure CSV data format with ';' as separator and skipping of the header
    final CsvDataFormat csv = new CsvDataFormat();
    csv.setDelimiter(";");
    csv.setSkipFirstLine(true);
    // Route takes a CSV file, splits the body and reads the actual values
    from(basePath.toURI() + "?noop=TRUE")
        .log("Unmarshalling CSV file.")
        .unmarshal(csv)
        .to("log://CSV?level=DEBUG")
        .split().body()
        .to("log://CSV?level=TRACE")
        // TODO: Create monthly based output instead of taking the yearly figures
        .setBody(simple("${body[1]}"))
        .to("log://CSV?level=TRACE")
        //.to("direct://rcode")
        .end()
        .log(LoggingLevel.INFO, "Finished the unmarshaling");
    // TODO: End the route with a meaningfull endpoint rather than logging
  }
}
