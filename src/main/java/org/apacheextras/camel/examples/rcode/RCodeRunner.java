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

import java.io.Console;
import java.io.File;
import java.util.concurrent.TimeUnit;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author cemmersb, Sebastian Rühl
 */
public class RCodeRunner {
  
  private static final Logger LOGGER = LoggerFactory.getLogger(RCodeRunner.class);
  
  private static File source = new File(RCodeRunner.class.getResource("data/demand.csv").toString());
  private static File target = new File(System.getProperty("user.home") + "/target");
  
  private static void parseCommandLine(String... args) throws ParseException {
    Options options = new Options();
    options.addOption("target", true, "specified the output directory where the generated graph will be stored.");
    options.addOption("source", false, "defines the source directory that contains the data directory.");
    
    CommandLineParser parser = new BasicParser();
    CommandLine commandLine = parser.parse(options, args);
    
    if(commandLine.hasOption("target")) {
      target = new File(commandLine.getOptionValue("target"));
    }
    
    if(commandLine.hasOption("source")) {
      source = new File(commandLine.getOptionValue("source"));
    }
  }
  
  public static void main(String... args) throws Exception {
    CamelContext camelContext = new DefaultCamelContext();
    camelContext.addRoutes(new RCodeRouteBuilder(source, target));

    camelContext.start();
    Console console = System.console();
    if (console != null) {
      console.printf("Please press enter to shutdown route.");
      console.readLine();
    } else {
      TimeUnit.SECONDS.sleep(30);
    }
    camelContext.stop();
  }
}
