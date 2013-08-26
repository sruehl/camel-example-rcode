/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.apacheextras.camel.examples.rcode;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.Property;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.file.FileComponent;
import org.apache.camel.dataformat.csv.CsvDataFormat;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * @author cemmersb, Sebastian Rühl
 */
public class RCodeRouteBuilder extends RouteBuilder {

  private final static String DEVICE_COMMAND = "jpeg('${exchangeId}.jpg',quality=90);";
  private final static String PLOT_COMMAND = "plot(quantity, type=\"l\");";
  private final static String RETRIEVE_PLOT_COMMAND = "r=readBin('${exchangeId}.jpg','raw',1024*1024); unlink('${exchangeId}.jpg'); r";
  private final static String FINAL_COMMAND = DEVICE_COMMAND + PLOT_COMMAND + "dev.off();" + RETRIEVE_PLOT_COMMAND;

  private File basePath;

  public RCodeRouteBuilder(File basePath) {
    this.basePath = basePath;
  }

  @Override
  public void configure() throws Exception {
    configureCsvRoute();
    configureRCodeRoute();
    configureGraphRoute();
    wireRoutes();
  }

  /**
   * Takes an input as bytes and writes it as an jpeg file.
   */
  private void configureGraphRoute() {
    from("direct:graph")
        .setHeader(Exchange.FILE_NAME, simple("graph${exchangeId}.jpeg"))
        .to("file://" + basePath.getParent() + "/output")
        .log("Generated graph file: " + basePath.getParent() + "/graph${exchangeId}.jpeg");
  }


  /**
   * Takes an incoming string argument containing monthly quantities and
   * generates an output graph.
   */
  private void configureRCodeRoute() {
    from("direct:rcode")
        .setBody(simple("quantity <- c(${body});\n" + FINAL_COMMAND))
        .to("log://command?level=DEBUG")
        .to("rcode://localhost:6311/parse_and_eval?bufferSize=4194304")
        .to("log://r_output?level=INFO")
        .setBody(simple("${body.asBytes}"));
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
        .setHeader("id", simple("${exchangeId}"))
        .split().body()
        .to("log://CSV?level=DEBUG")
            // TODO: Create monthly based output instead of taking the yearly figures
        .setBody(simple("${body[1]}"))
        .to("log://CSV?level=DEBUG")
            // Now we aggregate the retrived contents in a big string
        .aggregate(header("id"), new ConcatenateAggregationStrategy()).completionTimeout(3000)
        .log(LoggingLevel.INFO, "Finished the unmarshaling")
        .to("direct:CSV_sink");
  }

  /**
   * Wires together the routes.
   */
  private void wireRoutes() {
    from("direct:CSV_sink").to("direct:rcode").to("direct:graph");
  }
}
