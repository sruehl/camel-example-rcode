/*
 * Copyright 2013 Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apacheextras.camel.examples.rcode;

import org.apacheextras.camel.examples.rcode.builder.RCodeRouteBuilder;
import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;

import java.io.File;
import java.util.concurrent.TimeUnit;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Commandline tool for starting up the {@link RCodeRouteBuilder}.
 *
 * @author cemmersb, Sebastian Rühl
 */
public class RCodeRunner {

  /** Provides some basic output. */
  private static final Logger LOGGER = LoggerFactory.getLogger(RCodeRunner.class);
  /** Default file that points to the source directory . */
  private static File source = new File(RCodeRunner.class.getResource("data/").toString());
  /** Default file that points the user home target directory. */
  private static File target = new File(System.getProperty("user.home") + "/target");

  /**
   * Create a set of options that configures the Camel Routes
   */
  private static Options createOptions() {
    final Options options = new Options();
    options.addOption("h","help", false, "provides a list of availble command options.");
    options.addOption("t","target", true, "specified the output directory where the generated graph will be stored.");
    options.addOption("s","source", true, "defines the source directory that contains the data directory.");
    return options;
  }
  
  /**
   * Shows all commands in a formatted way to provide some guidance for specifying
   * the correct options.
   */
  private static void showHelp(Options options) {
    final HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp("help", options);
  }
  
  /**
   * Takes the argument String from the command line and parses according to the
   * specified parameter.
   * If any parameter is not specified correctly, the parser defaults to show the
   * help as output.
   */
  private static boolean parseCommandLine(String... args) {
    // Create the options
    final Options options = createOptions();
    // Initialize a basic parser
    final CommandLineParser parser = new BasicParser();
    // Parse options from command line

    CommandLine commandLine;
    try {
      commandLine = parser.parse(options, args);
      // Catch any parse exception and show the help
    } catch (ParseException ex) {
      LOGGER.error("Could not parse the specified options!");
      showHelp(options);
      return false;
    }

    if (commandLine.hasOption("help")) {
      showHelp(options);
      return false;
    }
    // If source has not been specified or is null show options, otherwise process the option
    if(!commandLine.hasOption("source") || null == commandLine.getOptionValue("source")) {
      showHelp(options);
      return false;
    }
    if (commandLine.hasOption("source")) {
      LOGGER.debug("Command line option is: {}", commandLine.getOptionValue("source"));
      source = new File(commandLine.getOptionValue("source"));
    }
    // If target has not been specified or is null show options, otherwise process the option
    if(!commandLine.hasOption("target") || null == commandLine.getOptionValue("target")) {
      showHelp(options);
      return false;
    }
    if (commandLine.hasOption("target")) {
      LOGGER.debug("Command line option is: {}", commandLine.getOptionValue("target"));
      target = new File(commandLine.getOptionValue("target"));
    }
    return true;
  }
  
  
  /**
   * Kicks of the main project to run an integration of Apache Camel and RCode.
   * Accepted parameters are:
   * <ul>
   * <li><code>-help</code>: provides a list of available command options.</li>
   * <li><code>-source &lt;arg&gt;</code>: defines the source directory that contains the data directory.</li>
   * <li><code>-target &lt;arg&gt;</code>: specified the output directory where the generated graph will be stored.</li>
   * </ul>
   * An example for an acceptable command is:<br>
   * <code>java -jar camel-example-rcode-${VERSION}.jar -source /${PATH}/${TO}/${SOURCE} -target /${PATH}/${TO}/${TARGET}</code>
   * 
   */
  public static void main(String... args) throws Exception {
    // Parse the command line arguments
    if (!parseCommandLine(args)) {
      System.exit(1);
    }
    // Start the camel context
    CamelContext camelContext = new DefaultCamelContext();
    camelContext.addRoutes(new RCodeRouteBuilder(source, target));
    camelContext.start();
    // Give Camel some time to process the data
    LOGGER.info("Waiting to finish the route calculation.");
    TimeUnit.SECONDS.sleep(10);
    // Shutdown the camel context
    camelContext.stop();
  }
}
