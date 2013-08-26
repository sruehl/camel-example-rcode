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

import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;

import java.io.Console;
import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author cemmersb
 */
public class RCodeRunner {

  public static void main(String... args) throws Exception {
    CamelContext camelContext = new DefaultCamelContext();
    File basePath = args.length > 0 ? new File(args[0]) : new File(System.getProperty("user.home") + "/data");

    camelContext.addRoutes(new RCodeRouteBuilder(basePath));

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
