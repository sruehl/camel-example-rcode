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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 *
 * @author cemmersb
 */
public class RCodeRunner {

  private final static Logger LOGGER = LoggerFactory.getLogger(RCodeRunner.class);
  private CamelContext camelContext;
  private RCodeRouteBuilder routeBuilder = null;

  public RCodeRunner() {
    try {
      initializeContext();
    } catch (Exception ex) {
      LOGGER.error("Unable to initialize context: {}", ex.getMessage());
    }
  }

  private void initializeContext() throws Exception {
    routeBuilder = new RCodeRouteBuilder(new File(System.getProperty("user.dir") + "./rcode-example/data"));
    camelContext = new DefaultCamelContext();
    camelContext.addRoutes(routeBuilder);
    camelContext.start();
  }

  @Override
  protected void finalize() throws Throwable {
    camelContext.stop();
    super.finalize();
  }

  public static void main(String... args) throws InterruptedException, Throwable {
    LOGGER.info("Starting RCodeRunner.");
    RCodeRunner rCodeRunner = new RCodeRunner();
    Thread.sleep(1000);
    LOGGER.info("Stopping RCodeRunner.");
    rCodeRunner.finalize();
  }
}
