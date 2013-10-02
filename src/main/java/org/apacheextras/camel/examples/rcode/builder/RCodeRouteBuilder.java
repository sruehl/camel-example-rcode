/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.apacheextras.camel.examples.rcode.builder;

import org.apacheextras.camel.examples.rcode.aggregator.ConcatenateAggregationStrategy;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dataformat.csv.CsvDataFormat;

import java.io.File;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author cemmersb, Sebastian Rühl
 */
public class RCodeRouteBuilder extends RouteBuilder {
  
  private static final Logger LOGGER = LoggerFactory.getLogger(RCodeRouteBuilder.class);
  private final static String DEVICE_COMMAND = "jpeg('${exchangeId}.jpg',quality=90);";
  private final static String PLOT_COMMAND = "plot(quantity, type=\"l\");";
  private final static String RETRIEVE_PLOT_COMMAND = "r=readBin('${exchangeId}.jpg','raw',1024*1024); unlink('${exchangeId}.jpg'); r";
  private final static String FINAL_COMMAND = DEVICE_COMMAND + PLOT_COMMAND + "dev.off();" + RETRIEVE_PLOT_COMMAND;
  private final static String HTTP4_RS_CAL_ENDPOINT = "http4://kayaposoft.com/enrico/json/v1.0/";
  private File basePath;
  private static final String DIRECT_CSV_SINK_URI = "direct://csv_sink";
  private static final String DIRECT_RCODE_SOURCE_URI = "direct://rcode_source";
  private static final String DIRECT_GRAPH_FILE_SOURCE_URI = "direct://graph_file_source";
  private static final String DIRECT_GRAPH_JSON_SOURCE_URI = "direct://graph_json_source";
  
  public RCodeRouteBuilder(File basePath) {
    this.basePath = basePath;
  }
  
  @Override
  public void configure() throws Exception {
    configureCsvRoute();
    configureRCodeRoute();
    configureGraphFileRoute();
    configureGraphJsonRoute();
    wireRoutes();
  }
  
  private void configureGraphJsonRoute() {
    // TODO: Export the binary file in a JSON rendert object and write to output folder
    from(DIRECT_GRAPH_JSON_SOURCE_URI)
        // TODO: missing JSON conversion implementation
        .to("log://graph_json?level=INFO"); // prints currently some awkward byte code
  }

  /**
   * Takes an input as bytes and writes it as an jpeg file.
   */
  private void configureGraphFileRoute() {
    from(DIRECT_GRAPH_FILE_SOURCE_URI)
        .setHeader(Exchange.FILE_NAME, simple("graph${exchangeId}.jpeg"))
        .to("file://" + basePath.getParent() + "/output")
        .log("Generated graph file: ${header.CamelFileNameProduced}");
  }

  /**
   * Takes an incoming string argument containing monthly quantities and
   * generates an output graph.
   */
  private void configureRCodeRoute() {
    from(DIRECT_RCODE_SOURCE_URI)
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
        // TODO: Do some number crunching to get monthly sales/demand figures
        .setBody(simple("${body[1]}"))
        .to("log://CSV?level=DEBUG")
        // Now we aggregate the retrived contents in a big string
        .aggregate(header("id"), new ConcatenateAggregationStrategy()).completionTimeout(3000)
        .log(LoggingLevel.INFO, "Finished the unmarshaling")
        .to(DIRECT_CSV_SINK_URI);
  }

  /**
   * Wires together the routes.
   */
  private void wireRoutes() {
    from(DIRECT_CSV_SINK_URI)
        .to(DIRECT_RCODE_SOURCE_URI)
        .multicast()
        .to(DIRECT_GRAPH_FILE_SOURCE_URI, DIRECT_GRAPH_JSON_SOURCE_URI);
    // TODO: Add a route endpoint for JSON based report file
    //.to(DIRECT_GRAPH_JSON_SOURCE_URI);
  }
}
